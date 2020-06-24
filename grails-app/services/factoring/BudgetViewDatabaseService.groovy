package factoring

import bv.auth.User
import grails.gorm.transactions.Transactional
import grails.web.servlet.mvc.GrailsHttpSession
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.context.request.RequestContextHolder

import java.sql.SQLException

@Transactional
class BudgetViewDatabaseService {


    static transactional = false

    public LinkedHashMap getConnectionInformation() {

        GrailsWebRequest request = RequestContextHolder.currentRequestAttributes()
        GrailsHttpSession session = request.session
        SecurityContext ctx = SecurityContextHolder.getContext()
        Authentication auth = ctx.getAuthentication()
        String username = auth.getName()

        Map map = ["id": 0, "companyName": '', "createdBy": 0, "createdDate": '', "dbName": '', "dbPassword": '', "dbUser": '', "driverName": '', "serverUrl": '', "status": 0, "updatedBy": 0, "updatedDate": '']

        if(session.dbConnection == null){

            def permittedUserId
            def permittedBusinessCompanyId

            if(session.permittedBusinessCompanyId != null){
                permittedBusinessCompanyId = session.permittedBusinessCompanyId
                permittedUserId = session.permittedUserId
            }

            User user = User.findByUsername(username)
            def authUserId = user.getAt('id')
            Integer authUsercompanyId
            if(permittedUserId == null || permittedUserId == authUserId){
                authUsercompanyId = user.getAt('businessCompanyId')
            }else{
                authUsercompanyId = permittedBusinessCompanyId
            }

            ArrayList companyConfig
            companyConfig = BusinessCompany.executeQuery("SELECT id,name,createdBy,dateCreated,dbName,dbPassword,dbUser,driverName,serverUrl,status,updatedBy,lastUpdated FROM BusinessCompany WHERE id =" + authUsercompanyId)
            map.id = companyConfig[0][0]
            map.companyName = companyConfig[0][1]
            map.createdBy = companyConfig[0][2]
            map.createdDate = companyConfig[0][3]
            map.dbName = companyConfig[0][4]
            map.dbPassword = companyConfig[0][5]
            map.dbUser = companyConfig[0][6]
            map.driverName = companyConfig[0][7]
            map.serverUrl = companyConfig[0][8]
            map.status = companyConfig[0][9]
            map.updatedBy = companyConfig[0][10]
            map.updatedDate = companyConfig[0][11]
            session.dbConnection = map
        }
        else{
            map = session.dbConnection
        }

        return map
    }

    public camelcaseToUnderscore(String camelCase = '') {
        String sk = camelCase.replaceAll("(?<=\\p{Ll})(?=\\p{Lu})|(?<=\\p{L})(?=\\p{Lu}\\p{Ll})", "::")
        def firstArr = sk.split("::")
        Integer index = 0;
        String underScoreString = ""
        firstArr.each { phn ->
            if (index != 0) {
                String name = phn
                name = name.replaceFirst(name[0], name[0].toLowerCase());
                underScoreString = underScoreString + "_" + name
            } else {
                underScoreString = phn
            }
            index++
        }
        return underScoreString
    }


    public LinkedHashMap select(String select = '*', String from = '', String where = '', String orderBy = '', String groupBy = '', String integerIndex = 'false', String selectIndex = '', Integer limit = 0, Integer offset = 0) {

        ///Get Configuration information//////
        def companyConfig = getConnectionInformation()
        ///Get Configuration object//////
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String selectIndexString = camelcaseToUnderscore(select.trim())
        select = " SELECT " + camelcaseToUnderscore(select.trim())
        from = " FROM " + camelcaseToUnderscore(from.trim())
        from = from.toLowerCase()

        if (where.trim()) {
            where = " WHERE " + camelcaseToUnderscore(where.trim())
        }
        if (orderBy.trim()) {
            orderBy = " ORDER BY " + camelcaseToUnderscore(orderBy.trim())
        }
        if (groupBy.trim()) {
            groupBy = "GROUP BY " + camelcaseToUnderscore(groupBy.trim())
        }

        String queryLimit = ""
        String queryOffset = ""

        if (limit > 0) {
            queryLimit = "LIMIT ${limit}"
        }
        if (offset > 0) {
            queryOffset = "OFFSET ${offset}"
        }

        String selectString = """$select $from $where $groupBy $orderBy $queryLimit $queryOffset"""

        List<GroovyRowResult> dataGridList = db.rows(selectString)
        db.close()

        def trueDataGridList = []
        if (integerIndex == "false") {
            return [dataGridList: dataGridList]
        } else {
            def selectIndexArray = ""
            if (selectIndex) {
                selectIndex = camelcaseToUnderscore(selectIndex.trim())
                selectIndexArray = selectIndex.split(",")
            } else {
                selectIndexArray = selectIndexString.split(",")
            }

            dataGridList.each { phn ->
                def tempTrueDataGridList = []
                selectIndexArray.each { phnEach ->
                    tempTrueDataGridList.add(phn[phnEach])
                }
                trueDataGridList.add(tempTrueDataGridList)
            }


            return [dataGridList: trueDataGridList]
        }

    }

    public insert(Map insertArray, String tableName = '') {

        ///Get Configuration information//////
        def companyConfig = getConnectionInformation()
        ///Get Configuration object//////
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)
        tableName = camelcaseToUnderscore(tableName.trim())
        tableName = tableName.toLowerCase()

        def insertStr = "INSERT INTO " + tableName + " ("
        def insertIndexArray = []
        def insertValueArray = []

        insertIndexArray.add("version")
        insertValueArray.add("'0'")

        insertArray.each() { key, value ->
            insertIndexArray.add(camelcaseToUnderscore(key.trim()))
            String strTempValue = value.toString();
            strTempValue  = strTempValue.replaceAll("'","")
            insertValueArray.add("'" + strTempValue + "'")
        }

        insertStr = insertStr + insertIndexArray.join(",") + ") VALUES(" + insertValueArray.join(",") + ")"

        def id = db.executeInsert(insertStr)
        db.close()

        def returnId = id[0][0]
        return returnId
    }

    public update(Map updateArray, String tableName = '', String whereString = '') {

        ///Get Configuration information//////
        def companyConfig = getConnectionInformation()
        ///Get Configuration object//////
        tableName = camelcaseToUnderscore(tableName.trim())
        tableName = tableName.toLowerCase();
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        def updateStr = "UPDATE " + tableName + " SET "

        def updateIndexArray=[]
        def updateTempArray = []

        updateArray.each() { key, value ->

            String strTempValue = value.toString()
            strTempValue  = strTempValue.replaceAll("'","")

            def tempString = camelcaseToUnderscore(key.trim()) + "=" + "'" + strTempValue + "'"
            updateTempArray.add(tempString)
        }

        updateStr = updateStr + updateTempArray.join(",")

        if (whereString) {
            updateStr = updateStr + " WHERE " + camelcaseToUnderscore(whereString.trim())
        }

        db.execute(updateStr)
        db.close()

    }

    public delete(String tableName = '', String whereString = '') {

        ///Get Configuration information//////
        def companyConfig = getConnectionInformation()
        ///Get Configuration object//////
        tableName = camelcaseToUnderscore(tableName.trim())
        tableName = tableName.toLowerCase()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        def deleteStr = "DELETE FROM " + tableName

        if (whereString) {
            deleteStr = deleteStr + " WHERE " + camelcaseToUnderscore(whereString.trim())
        }

        db.execute(deleteStr)
        db.close()
    }

    public updateByString(String updateStr = '') {

        ///Get Configuration information//////
        def companyConfig = getConnectionInformation()
        ///Get Configuration object//////
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        updateStr = updateStr.trim().replace("bv.", "")
        updateStr = camelcaseToUnderscore(updateStr)

        db.execute(updateStr.toLowerCase())
        db.close()
    }

    public List<GroovyRowResult> runQuery(String selectString) {

        if (selectString.isEmpty()) {
            println("Querysting is empty")
            return;
        }

        ///Get Configuration information//////
        def companyConfig = getConnectionInformation()
        ///Get Configuration object//////
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        selectString = selectString.trim().replace("bv.", "")
        selectString = camelcaseToUnderscore(selectString.trim())
        List<GroovyRowResult> dataGridList = db.rows(selectString.toLowerCase())

        db.close()

        return dataGridList
    }

    public executeQueryWithoutCamelCase(String selectString) {

        def valueArrayFinal = []
        if (selectString.isEmpty()) {
            //println("Querysting is empty");
            return valueArrayFinal;
        }

        ///Get Configuration information//////
        def companyConfig = getConnectionInformation()
        ///Get Configuration object//////
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        selectString = selectString.trim().replace("bv.", "")
        List<GroovyRowResult> dataGridList = db.rows(selectString.toLowerCase())

        db.close();
        dataGridList.each { phn ->
            def ValueArray = []
            phn.each() { phnOne, value ->
                ValueArray.add(value)
            }
            valueArrayFinal.add(ValueArray)
        }
        //println('RETURN=<><>='+valueArrayFinal)
        return valueArrayFinal
    }

    public executeCustomQuery(String strQuery) {
        def companyConfig = getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)
        List<GroovyRowResult> dataList = db.rows(strQuery)
        db.close()
        return dataList
    }

    public executeQuery(String selectString) {

        def valueArrayFinal = []
        if (selectString.isEmpty()) {
            return valueArrayFinal
        }

        ///Get Configuration information//////
        def companyConfig = getConnectionInformation()
        ///Get Configuration object//////
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        selectString = selectString.trim().replace("bv.", "")
        selectString = camelcaseToUnderscore(selectString.trim())
        List<GroovyRowResult> dataGridList = db.rows(selectString.toLowerCase())

        db.close()
        dataGridList.each { phn ->
            def ValueArray = []
            phn.each() { phnOne, value ->
                ValueArray.add(value)
            }
            valueArrayFinal.add(ValueArray)
        }

        return valueArrayFinal
    }

    public executeQueryAtSingle(String selectString) {

        ///Get Configuration information//////
        def companyConfig = getConnectionInformation()
        ///Get Configuration object//////
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        selectString = selectString.trim().replace("bv.", "")
        selectString = camelcaseToUnderscore(selectString.trim())

        List<GroovyRowResult> dataGridList = db.rows(selectString.toLowerCase())
        db.close()

        def ValueArrayFinal = []
        if (dataGridList.size()) {
            dataGridList.each { phn ->
                phn.each() { phnOne, value ->
                    ValueArrayFinal.add(value)
                }
            }
            return ValueArrayFinal
        } else {
            return ""
        }

    }

    public executeUpdate(String updateStr = '') {

        ///Get Configuration information//////
        def companyConfig = getConnectionInformation()
        ///Get Configuration object//////
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        updateStr = updateStr.trim().replace("bv.", "")
        updateStr = camelcaseToUnderscore(updateStr)
        updateStr = updateStr.toLowerCase()

        db.execute(updateStr)
        db.close()
    }

    public startTransaction() {

        ///Get Configuration information//////
        def companyConfig = getConnectionInformation()
        ///Get Configuration object//////
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)
        String sql = "START TRANSACTION;"
        db.execute(sql)

        return db
    }

    public commitData(def db) {

        String sql = "COMMIT;"
        db.execute(sql)
        db.close()
    }

    public insertAfterTransaction(Map insertArray, String tableName = '', def db) {

        ///Get Configuration information//////
        tableName = camelcaseToUnderscore(tableName.trim())
        tableName = tableName.toLowerCase()

        def insertStr = "INSERT INTO " + tableName + " ("
        def insertIndexArray = []
        def insertValueArray = []

        insertIndexArray.add("version")
        insertValueArray.add("'0'")

        insertArray.each() { key, value ->
            insertIndexArray.add(camelcaseToUnderscore(key.trim()))
            String strTempValue = value.toString()
            strTempValue  = strTempValue.replaceAll("'","")
            insertValueArray.add("'" + strTempValue + "'")
        }

        insertStr = insertStr + insertIndexArray.join(",") + ") VALUES(" + insertValueArray.join(",") + ")"

        def id = db.executeInsert(insertStr)

        def returnId = id[0][0]
        return returnId
    }

    User getCustomerDetails(String strQuery, String password) {

        def businessCompanyList = BusinessCompany.findAll()
        int size = businessCompanyList.size()
        User userInstance = new User()

        //will store the result set
        List<GroovyRowResult> dataList

        for (int i = 0 ; i < size ; i++ ){

            BusinessCompany connectionDetails = businessCompanyList[i]

            String serverUrl = connectionDetails.getServerUrl()
            String dbName = connectionDetails.getDbName()
            String dbUser = connectionDetails.getDbUser()
            String dbPassword = connectionDetails.getDbPassword()
            String driverName = connectionDetails.getDriverName()

            try{
                def db = Sql.newInstance(serverUrl + dbName, dbUser, dbPassword, driverName)
                dataList = db.rows(strQuery)
                db.close()
            }catch(SQLException sqlEx){
                println(sqlEx)
            }

            if(dataList.size() > 0){

                dataList.each { key ->

                    userInstance.enabled = 'on'
                    userInstance.lastName = key.last_name
                    userInstance.username = key.email
                    userInstance.email = key.email
                    userInstance.firstName = key.first_name
                    userInstance.password = password
                    userInstance.customerId = key.id
                    userInstance.businessCompanyId = connectionDetails.getId()
                }

                return userInstance
            }
        }

        return null
    }
}
