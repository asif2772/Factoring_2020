package factoring

import grails.plugin.springsecurity.SpringSecurityService
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import io.micronaut.spring.tx.annotation.Transactional
import org.springframework.beans.factory.annotation.Autowired

import java.text.DateFormat
import java.text.SimpleDateFormat


@Transactional
class DebtorCustomerService {

    SpringSecurityService springSecurityService
    @Autowired
    BudgetViewDatabaseService budgetViewDatabaseService

     Map getDebtorCustomerDataAsMap(def dbcusId){

        def debtorCustomerInstanceTemp

        Map debtorCustomerInstance = ["id"                : 0,   "VERSION"      : 0,        "bookingPeriod"  : '',
                                     "debCusNumber"       : '',  "customerId"  : 0,         "debtorId"       : 0,
                                     "defaultFee"         : 0.0, "insuaranceLimit" : 0.0,
                                     "outpayment"         : 100, "acceptanceFee"    : 20,   "adminCost"       : 10,
                                     "debtorTermsId"      : 1,   "acceptenceDate"    : "",
                                     "firstReminder"      : 30,  "secondReminder"    : 30,
                                     "thirdReminder"      : 30,  "finalReminder"    : 30]

        def editId = 0
        if(dbcusId instanceof String){
            editId = Integer.parseInt(dbcusId)
        }
        else{
            editId = dbcusId
        }

        if(editId > 0){
            String selectQuery = """SELECT a.id AS id,a.version As version,a.debtor_customer_number AS debCusNumber,
                                a.customer_id AS customerId,a.debtor_id AS debtorId,a.default_fee AS defaultFee,
                                a.insuarance_limit AS insuaranceLimit,a.outpayment AS outpayment,a.admin_cost AS adminCost,
                                a.acceptence_fee AS acceptanceFee,a.payment_term_id AS debtorTermsId,
                                a.acceptence_date AS acceptenceDate,a.first_reminder as firstReminder,
                                a.second_reminder as secondReminder,a.third_reminder as thirdReminder,a.final_reminder as finalReminder
                                FROM debtor_customer AS a WHERE a.id = ${editId}"""

            debtorCustomerInstanceTemp = budgetViewDatabaseService.executeQueryAtSingle(selectQuery)

            debtorCustomerInstance.id = debtorCustomerInstanceTemp[0]
            debtorCustomerInstance.VERSION = debtorCustomerInstanceTemp[1]
            debtorCustomerInstance.debCusNumber = debtorCustomerInstanceTemp[2]
            debtorCustomerInstance.customerId = debtorCustomerInstanceTemp[3]
            debtorCustomerInstance.debtorId = debtorCustomerInstanceTemp[4]
            debtorCustomerInstance.defaultFee = debtorCustomerInstanceTemp[5]
            debtorCustomerInstance.insuaranceLimit = debtorCustomerInstanceTemp[6]
            debtorCustomerInstance.outpayment = debtorCustomerInstanceTemp[7]
            debtorCustomerInstance.adminCost = debtorCustomerInstanceTemp[8]
            debtorCustomerInstance.acceptanceFee = debtorCustomerInstanceTemp[9]
            debtorCustomerInstance.debtorTermsId = debtorCustomerInstanceTemp[10]
            debtorCustomerInstance.acceptenceDate = debtorCustomerInstanceTemp[11]
            debtorCustomerInstance.firstReminder = debtorCustomerInstanceTemp[12]
            debtorCustomerInstance.secondReminder = debtorCustomerInstanceTemp[13]
            debtorCustomerInstance.thirdReminder = debtorCustomerInstanceTemp[14]
            debtorCustomerInstance.finalReminder = debtorCustomerInstanceTemp[15]
        }

        return debtorCustomerInstance
    }

    public LinkedHashMap listOfDebtorCustomer(){

        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String selectQuery = """SELECT a.id AS id,a.debtor_customer_number AS debCusNumber,
                                customer.vendor_name AS customerName,debtor.customer_name AS debtorName,
                                a.acceptence_fee AS acceptenceFee,pt.terms AS debitorTerms,
                                DATE_FORMAT(a.acceptence_date,'%Y-%m-%d') AS acceptenceDate,
                                u.username AS acceptedBy FROM debtor_customer AS a
                                INNER JOIN customer_master as debtor ON a.debtor_id = debtor.id
                                INNER JOIN vendor_master as customer ON a.customer_id = customer.id
                                INNER JOIN payment_terms as pt ON a.payment_term_id = pt.id
                                INNER JOIN user as u ON a.create_user_id = u.id"""

        List<GroovyRowResult> itemList = db.rows(selectQuery)
        int total = itemList.size()

        return[debtorCustomerItemList:itemList,count:total]
    }

    public LinkedHashMap listOfDebtorCustomerGrid(){

        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String selectQuery = """SELECT a.id AS id,a.debtor_customer_number AS debCusNumber,
                                customer.vendor_name AS customerName,debtor.customer_name AS debtorName,
                                a.default_fee AS acceptenceFee,pt.terms AS debitorTerms,
                                DATE_FORMAT(a.acceptence_date,'%Y-%m-%d') AS acceptenceDate,
                                u.username AS acceptedBy FROM debtor_customer AS a
                                INNER JOIN customer_master as debtor ON a.debtor_id = debtor.id
                                INNER JOIN vendor_master as customer ON a.customer_id = customer.id
                                INNER JOIN payment_terms as pt ON a.payment_term_id = pt.id
                                INNER JOIN tomcatbu_factoring_auth.user as u ON a.create_user_id = u.id"""

        List<GroovyRowResult> itemList = db.rows(selectQuery)
        int total = itemList.size()

        return[debtorCustomerItemList:itemList,count:total]
    }

    def createDefaultCustomerDebtor(def debtorId,def customerId,def paymentTermsDays){
        def paymentData = budgetViewDatabaseService.executeCustomQuery("SELECT id,days_before_due,alert_start_days,alert_repeat_days,final_reminder_days from payment_terms WHERE days_before_due BETWEEN ${paymentTermsDays} AND ${paymentTermsDays+4}")
        def defaultFee = budgetViewDatabaseService.executeCustomQuery("SELECT VF.default_fee AS defaultFee,VF.admin_cost AS adminCost,VF.acceptence_fee AS acceptenceFee FROM vendor_factoring AS VF WHERE VF.customer_id=${customerId}")
        def factorFee = 3.0
        def adminCost = 10.0
        def acceptenceFee = 20.0
        if(defaultFee){
            if(defaultFee[0][0]>=0)
                factorFee = defaultFee[0][0]
            if(defaultFee[0][1]>=0)
                adminCost = defaultFee[0][1]
            if(defaultFee[0][2]>=0)
                acceptenceFee = defaultFee[0][2]
        }
        def payment_term_id = 1
        def first_reminder = 32
        def second_reminder = 10
        def third_reminder = 10
        def final_reminder = 15
        if(paymentData){
            payment_term_id = paymentData[0][0]
            first_reminder = paymentData[0][1]
            second_reminder = paymentData[0][2]
            third_reminder = paymentData[0][3]
            final_reminder = paymentData[0][4]
        }
        else{
            paymentData = budgetViewDatabaseService.executeCustomQuery("SELECT id,days_before_due,alert_start_days,alert_repeat_days,final_reminder_days from payment_terms WHERE days_before_due BETWEEN 28 AND 32")
            if(paymentData){
                payment_term_id = paymentData[0][0]
                first_reminder = paymentData[0][1]
                second_reminder = paymentData[0][2]
                third_reminder = paymentData[0][3]
                final_reminder = paymentData[0][4]
            }
        }

        def user = springSecurityService.principal
        String userId = ""+user.id

        def debtorCustomerNo = debtorId+"#"+customerId
        Map mappedValue = [
                debtor_id              :   "${debtorId}",
                customer_id            :   "${customerId}",
                debtor_customer_number :   "${debtorCustomerNo}",
                default_fee            :   factorFee,
                insuarance_limit       :   "0.0",
                outpayment             :   "100.0",
                admin_cost             :   adminCost,
                acceptence_fee         :   acceptenceFee,
                acceptence_date        :   new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()),
                payment_term_id        :   payment_term_id,
                first_reminder         :   first_reminder,
                second_reminder        :   second_reminder,
                third_reminder         :   third_reminder,
                final_reminder         :   final_reminder,
                create_user_id         :   "${userId}",
                update_user_id         :   "${userId}",
                create_date            :   new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()),
                update_date            :   new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()),
                acceptence_fee_inv_number : 0
        ]

        def tableName = "DebtorCustomer"
        def insertedId = budgetViewDatabaseService.insert(mappedValue, tableName)

        return insertedId
    }

    def insertAcceptDebtors(def params){
        def user = springSecurityService.principal
        String userId = ""+user.id

        DateFormat df = new SimpleDateFormat("dd-MM-yyyy")
        Date tempTransDate = df.parse(params.acceptanceDate)
        String acceptenceDate = tempTransDate.format("yyyy-MM-dd hh:mm:ss")

        def debtorCustomerNo = params.debtorId+"#"+params.customerId
        Map mappedValue = [
                debtor_id              :   "${params.debtorId}",
                customer_id            :   "${params.customerId}",
                debtor_customer_number :   "${debtorCustomerNo}",
                default_fee            :   "${params.defaultFee}",
                insuarance_limit       :   "${params.insuaranceLimit}",
                outpayment             :   "${params.outpayment}",
                admin_cost             :   "${params.adminCost}",
                acceptence_fee         :   "${params.acceptanceFee}",
                acceptence_date        :   "${acceptenceDate}",
                payment_term_id        :   "${params.debtorTermsId}",
                first_reminder         :   "${params.firstReminder}",
                second_reminder        :   "${params.secondReminder}",
                third_reminder         :   "${params.thirdReminder}",
                final_reminder         :   "${params.finalReminder}",
                create_user_id         :   "${userId}",
                update_user_id         :   "${userId}",
                create_date            :   new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()),
                update_date            :   new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()),
                acceptence_fee_inv_number : 0
        ]

        def tableName = "DebtorCustomer"
        def insertedId = budgetViewDatabaseService.insert(mappedValue, tableName)

        return insertedId
    }

    def updateAcceptDebtors(def params){

        def user = springSecurityService.principal
        String userId = "" + user.id

        DateFormat df = new SimpleDateFormat("dd-MM-yyyy")
        Date tempTransDate = df.parse(params.acceptanceDate)
        String acceptenceDate = tempTransDate.format("yyyy-MM-dd hh:mm:ss")

        def debtorCustomerNo = params.debtorId + "#" + params.customerId
        Map mappedValue = [
                debtor_id             : "${params.debtorId}",
                customer_id           : "${params.customerId}",
                debtor_customer_number: "${debtorCustomerNo}",
                default_fee           : "${params.defaultFee}",
                insuarance_limit      : "${params.insuaranceLimit}",
                outpayment            : "${params.outpayment}",
                admin_cost            : "${params.adminCost}",
                acceptence_fee        : "${params.acceptanceFee}",
                acceptence_date       : "${acceptenceDate}",
                payment_term_id       : "${params.debtorTermsId}",
                first_reminder        : "${params.firstReminder}",
                second_reminder       : "${params.secondReminder}",
                third_reminder        : "${params.thirdReminder}",
                final_reminder        : "${params.finalReminder}",
                create_user_id        : "${userId}",
                update_user_id        : "${userId}",
                create_date           : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()),
                update_date           : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date())
        ]

        def tableName = "DebtorCustomer"
        def updatedWhereSrting = "id=" + "'" + params.id + "'"
        budgetViewDatabaseService.update(mappedValue, tableName, updatedWhereSrting)
    }

    def checkDebtorCustomerCombination(def debtorId,def customerId){
        boolean bComExist = false
        String selectQuery = """SELECT * FROM debtor_customer WHERE debtor_id = ${debtorId} AND customer_id = ${customerId}"""

        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        List<GroovyRowResult> itemList = db.rows(selectQuery)
        int total = itemList.size()
        if(total > 0){
            bComExist = true
        }

        return bComExist
    }

    def getDebtorCustomerInvoiceId(def debtorId,def customerId){
        boolean bComExist = false
        String selectQuery = """SELECT id FROM invoice_income WHERE debtor_id = ${debtorId} AND customer_id = ${customerId}"""

        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        List<GroovyRowResult> itemList = db.rows(selectQuery)
        if(itemList)
            return itemList[0][0]
        else
            return 0
    }

    def getDebtorCustomerId(def debtorId,def customerId){
        String selectQuery = """SELECT id FROM debtor_customer WHERE debtor_id = ${debtorId} AND customer_id = ${customerId}"""

        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        List<GroovyRowResult> itemList = db.rows(selectQuery)
        if(itemList)
            return itemList[0][0]
        else
            return 0
    }

    def getDebtorListForCustomer(def fiscalYearId,def customerId){
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        if(customerId != ""){
            String strQuery = """ SELECT a.id as id, CONCAT(sp2.prefix,'-',a.invoice_no) AS invoiceNumber,a.payment_ref AS payRef,
                                CONCAT(cm.customer_name,' [',sp.prefix,'-',cm.customer_code,']') AS debtorName,
                                DATE_FORMAT(a.trans_date,'%d-%m-%Y') AS invoiceDate,a.status AS status,a.all_docs_ok as docChecked,
                                a.total_gl_amount AS totalAmountIncVat, a.total_vat AS totalVat,
                                a.debtor_id AS debtorId,dc.default_fee as factorFee,a.customer_id AS customerId FROM invoice_income AS a
                                INNER JOIN debtor_customer as dc ON a.debtor_id = dc.debtor_id AND a.customer_id = dc.customer_id
                                INNER JOIN customer_master as cm ON a.debtor_id = cm.id
                                INNER JOIN system_prefix AS sp ON sp.id=1
                                INNER JOIN system_prefix AS sp1 ON sp1.id=6
                                INNER JOIN system_prefix AS sp2 ON sp2.id=8
                                WHERE a.customer_id = ${customerId} AND a.booking_year = '${fiscalYearId}' AND a.status IN('updated','new')"""

            List<GroovyRowResult> debtorList = db.rows(strQuery)

            int total = debtorList.size()
            db.close()
            return [debtorList: debtorList, count: total]
        }else{
            return [debtorList: [], count: 0]
        }
    }

    def getDebtorListFromSettledId(def settlementId){
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        def settlId = Integer.parseInt(settlementId)
        if(settlId > 0){
            String strQuery = """SELECT a.id as id, CONCAT(sp2.prefix,'-',a.invoice_no) AS invoiceNumber,a.payment_ref as payRef,
                                CONCAT(cm.customer_name,' [',sp.prefix,'-',cm.customer_code,']') AS debtorName,
                                DATE_FORMAT(a.trans_date,'%d-%m-%Y') AS invoiceDate,a.status AS status,a.all_docs_ok as docChecked,
                                a.total_gl_amount AS totalAmountIncVat, a.total_vat AS totalVat,
                                a.debtor_id AS debtorId,dc.default_fee as factorFee,a.customer_id AS customerId,
                                a.acceptence_fee as acceptenceFee,dc.admin_cost as adminCost,dc.outpayment as discount,
                                cm.customer_name AS debtorNameWithoutCode
                                FROM invoice_income AS a
                                INNER JOIN debtor_customer as dc ON a.debtor_id = dc.debtor_id AND a.customer_id = dc.customer_id
                                INNER JOIN customer_master as cm ON a.debtor_id = cm.id
                                INNER JOIN system_prefix AS sp ON sp.id=1
                                INNER JOIN system_prefix AS sp1 ON sp1.id=6
                                INNER JOIN system_prefix AS sp2 ON sp2.id=8
                                WHERE a.customer_settled_id = ${settlId}
                                ORDER BY a.payment_ref ASC"""

            List<GroovyRowResult> debtorList = db.rows(strQuery)

            int total = debtorList.size()
            db.close()
            return [debtorList: debtorList, count: total]
        }else{
            return [debtorList: [], count: 0]
        }
    }
    ///End DebtorCustomer Service function.

    ///Only Customer Service function.
    def getCustomerFactoringDataAsMap(def customerId){
        def customerFactoringInstanceTemp

        Map customerFactoringrInstance = ["id"               : 0,"VERSION"          : 0,
                                      "defaultFee"       : 0.0,"outpayment"       : 100.0,
                                      "acceptenceFee"    : 20.0,"adminCost"       : 10,
                                      "subcriptionDate"  : "","subcriptionAmount" : 199.0,
                                      "brokerId"         :  0,"brokerFee"         : 10.0, "brokerDate"  : ""]

        def editId = 0
        if(customerId instanceof String){
            editId = Integer.parseInt(customerId)
        }
        else{
            editId = customerId
        }

        if(editId > 0){
            String selectQuery = """SELECT a.id AS id,a.version As version,
                                a.default_fee AS defaultFee,
                                a.outpayment AS outpayment,a.acceptence_fee AS acceptanceFee,
                                a.admin_cost AS adminCost,a.broker_fee AS brokerFee,a.subcription_amount,
                                a.subcription_date AS subcriptionDate,a.broker_id,a.broker_date
                                FROM vendor_factoring AS a WHERE a.customer_id = ${editId}"""

            customerFactoringInstanceTemp = budgetViewDatabaseService.executeQueryAtSingle(selectQuery)

            if(customerFactoringInstanceTemp.size()> 0){
                customerFactoringrInstance.id = customerFactoringInstanceTemp[0]
                customerFactoringrInstance.VERSION = customerFactoringInstanceTemp[1]
                customerFactoringrInstance.defaultFee = customerFactoringInstanceTemp[2]
                customerFactoringrInstance.outpayment = customerFactoringInstanceTemp[3]
                customerFactoringrInstance.acceptenceFee = customerFactoringInstanceTemp[4]
                customerFactoringrInstance.adminCost = customerFactoringInstanceTemp[5]
                customerFactoringrInstance.brokerFee = customerFactoringInstanceTemp[6]

                customerFactoringrInstance.subcriptionAmount = customerFactoringInstanceTemp[7]
                customerFactoringrInstance.subcriptionDate = customerFactoringInstanceTemp[8]

                customerFactoringrInstance.brokerId = customerFactoringInstanceTemp[9]
                customerFactoringrInstance.brokerDate = customerFactoringInstanceTemp[10]
            }

        }

        return customerFactoringrInstance
    }

    def getCustomerRevenueDataAsMap(def customerId){

        def editId = 0
        def customerRevenueTemp
        TreeMap customerRevenue = [:]

        if(customerId instanceof String){
            editId = Integer.parseInt(customerId)
        }
        else{
            editId = customerId
        }

        if(editId > 0){
            String selectQuery = """SELECT tm.booking_year, ROUND(SUM(tm.amount * -1),2) AS amount
                                    FROM trans_master AS tm
                                    WHERE tm.account_code IN ('8010',
                                                              '8020',
                                                              '8030',
                                                              '8040')
                                    AND tm.trans_type=2 AND vendor_id = ${editId} GROUP BY tm.booking_year ORDER BY tm.booking_year"""

            customerRevenueTemp = budgetViewDatabaseService.executeQuery(selectQuery)
            int size = customerRevenueTemp.size()

            for(int i =0; i < size; i++){
                customerRevenue.put(customerRevenueTemp[i][0].toString(), customerRevenueTemp[i][1])
            }
        }

        return customerRevenue
    }

     List getOutstandingInvoiceDetails(def customerId){

        def editId = 0
        def customerOutstandingInvoices

        if(customerId instanceof String){
            editId = Integer.parseInt(customerId)
        }
        else{
            editId = customerId
        }

        if(editId > 0){
            String selectQuery = """SELECT *
                                     FROM
                                       (SELECT
                                           ic.booking_year AS invoiceDate,
                                           TO_DAYS(ic.due_date)-TO_DAYS(NOW()) AS daysToPay,
                                           IF(ISNULL(tblPaidInvoices.lastPaymentDate), TO_DAYS(NOW())-TO_DAYS(ic.trans_date), TO_DAYS(tblPaidInvoices.lastPaymentDate)-TO_DAYS(ic.trans_date)) AS paymentRealized,
                                           (TO_DAYS(NOW())-TO_DAYS(ic.trans_date))-180 AS isSixMonths,
                                           ROUND(ic.total_gl_amount+ic.total_vat, 2) AS invoiceAmount,
                                           IF(ISNULL(tblPaidInvoices.invoicePaidAmount), ROUND(ic.total_gl_amount+ic.total_vat, 2), ROUND((ic.total_gl_amount+ic.total_vat)-tblPaidInvoices.invoicePaidAmount, 2)) AS dueAmount,
                                           IF(ISNULL(tblPaidInvoices.invoicePaidAmount), 0, ROUND(tblPaidInvoices.invoicePaidAmount, 2)) AS paidAmount,
                                           ic.debtor_id AS debtorId,
                                           ic.customer_id AS customerId
                                    FROM invoice_income AS ic
                                    INNER JOIN customer_master AS cm ON ic.debtor_id=cm.id
                                    LEFT JOIN
                                      (SELECT ic.id AS invoiceId,
                                          SUM(tm.amount*-1) AS invoicePaidAmount,
                                          MAX(tm.trans_date) AS lastPaymentDate
                                       FROM invoice_income AS ic
                                       INNER JOIN trans_master AS tm ON tm.recenciliation_code = CONCAT(ic.id, '#1')
                                       AND ic.customer_id='${editId}'
                                       AND tm.account_code='1300'
                                       AND tm.trans_type <> 1
                                       GROUP BY ic.id) AS tblPaidInvoices ON tblPaidInvoices.invoiceId=ic.id) AS tblAllInfo
                                     WHERE tblAllInfo.customerId='${editId}'
                                       AND tblAllInfo.invoiceAmount<>tblAllInfo.paidAmount
                                     ORDER BY STR_TO_DATE(invoiceDate, '%Y') ASC"""

            customerOutstandingInvoices = budgetViewDatabaseService.executeQuery(selectQuery)

            return customerOutstandingInvoices

        }

    }

    List getPaidInvoiceDetails(def customerId){

        def editId = 0
        def customerPaidInvoices

        if(customerId instanceof String){
            editId = Integer.parseInt(customerId)
        }
        else{
            editId = customerId
        }

        if(editId > 0){
            String selectQuery = """SELECT *
                                     FROM
                                       (SELECT 
                                               ic.booking_year AS invoiceDate,
                                               TO_DAYS(ic.trans_date)-TO_DAYS(NOW()) AS daysToPay,
                                               IF(ISNULL(tblPaidInvoices.lastPaymentDate), TO_DAYS(NOW())-TO_DAYS(ic.trans_date), TO_DAYS(tblPaidInvoices.lastPaymentDate)-TO_DAYS(ic.trans_date)) AS paymentRealized,
                                               (TO_DAYS(NOW())-TO_DAYS(ic.trans_date))-180 AS isSixMonths,
                                               ROUND(ic.total_gl_amount+ic.total_vat, 2) AS invoiceAmount,
                                               IF(ISNULL(tblPaidInvoices.invoicePaidAmount), ROUND(ic.total_gl_amount+ic.total_vat, 2), ROUND((ic.total_gl_amount+ic.total_vat)-tblPaidInvoices.invoicePaidAmount, 2)) AS dueAmount,
                                               IF(ISNULL(tblPaidInvoices.invoicePaidAmount), 0, ROUND(tblPaidInvoices.invoicePaidAmount, 2)) AS paidAmount,
                                               cm.credit_limit AS creditLimit,
                                               ic.customer_id AS customerId,
                                               ic.debtor_id AS debtorId
                                        FROM invoice_income AS ic
                                        INNER JOIN customer_master AS cm ON ic.debtor_id=cm.id
                                        LEFT JOIN
                                          (SELECT ic.id AS invoiceId,
                                                  SUM(tm.amount*-1) AS invoicePaidAmount,
                                                  MAX(tm.trans_date) AS lastPaymentDate
                                           FROM invoice_income AS ic
                                           INNER JOIN trans_master AS tm ON tm.recenciliation_code = CONCAT(ic.id, '#1')
                                           AND ic.customer_id='${editId}'
                                           AND tm.account_code='1300'
                                           AND tm.trans_type <> 1
                                           GROUP BY ic.id) AS tblPaidInvoices ON tblPaidInvoices.invoiceId=ic.id) AS tblAllInfo
                                     WHERE tblAllInfo.customerId='${editId}'
                                       AND tblAllInfo.invoiceAmount=tblAllInfo.paidAmount
                                     ORDER BY STR_TO_DATE(invoiceDate, '%Y') ASC"""

            customerPaidInvoices = budgetViewDatabaseService.executeQuery(selectQuery)

            return customerPaidInvoices
        }

    }

    def String replaceComaToDot(String strInput){
        String strOutput = ""

        if(strInput){
            String strTemp = strInput.replaceAll(",", ".")
            strOutput = strTemp
        }


        return strOutput
    }

    def updateCustomerFactoring(Long id,def params){

        params.acceptenceFee = replaceComaToDot(params.acceptenceFee)
        params.adminCost = replaceComaToDot(params.adminCost)
        params.defaultFee = replaceComaToDot(params.defaultFee)
        params.outpayment = replaceComaToDot(params.outpayment)
        params.subcriptionAmount = replaceComaToDot(params.subcriptionAmount)
        params.brokerFee = replaceComaToDot(params.brokerFee)


        DateFormat df = new SimpleDateFormat("dd-MM-yyyy")
        Date tempSubcriptionDate = df.parse(params.subcriptionDate)
        String subcriptionDate = tempSubcriptionDate.format("yyyy-MM-dd hh:mm:ss")

        Date tempBrokerDate
        String brokerDate
        if (params.brokerDate){
            tempBrokerDate = df.parse(params.brokerDate)
            brokerDate = tempBrokerDate.format("yyyy-MM-dd hh:mm:ss")
        } else {
            tempBrokerDate = new Date()
            brokerDate = tempBrokerDate.format("yyyy-MM-dd hh:mm:ss")
        }


        def brokerFee = 0
        if(params.brokerFee){
            brokerFee = params.brokerFee
        }

        def cusBrokerId
        if(params.brokerId){
            cusBrokerId = params.brokerId
        } else {
            cusBrokerId = 0
        }

        Map updatededValue = [
                acceptence_fee          :   "${params.acceptenceFee}",
                admin_cost              :   "${params.adminCost}",
                default_fee             :   "${params.defaultFee}",
                outpayment              :   "${params.outpayment}",
                subcription_amount      :   "${params.subcriptionAmount}",
                subcription_date        :   "${subcriptionDate}",
                customer_id             :   "${params.customerId}",
                broker_id               :   "${cusBrokerId}",
                broker_fee              :   "${brokerFee}",
                broker_date             :   "${brokerDate}"
        ]

        Integer newInsertedId = 0
        def tableName = "vendor_factoring"
        if (id) {
            def updatedWhereSrting = "id='${id}'"
            budgetViewDatabaseService.update(updatededValue, tableName, updatedWhereSrting)
        }else{
            newInsertedId = budgetViewDatabaseService.insert(updatededValue, tableName)
        }


    }

}
