package factoring

import bv.auth.User
import grails.plugin.springsecurity.SpringSecurityService
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import io.micronaut.spring.tx.annotation.Transactional

import java.text.DateFormat
import java.text.SimpleDateFormat

@Transactional
class IncomeInvoiceService {

    SpringSecurityService springSecurityService

    LinkedHashMap listOfIncomeInvoice(String fiscalYearId) {

        User user = springSecurityService.currentUser
        def userCustomerId = user.customerId

        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String incomeInvoice = """SELECT a.id AS id,
                                           CONCAT(sp2.prefix, '-', a.invoice_no) AS invoiceNumber,
                                           a.booking_period AS bookingPeriod,
                                           CONCAT(v.customer_name, ' [', sp.prefix, '-', v.customer_code, ']') AS debtorName,
                                           a.customer_id AS customerId,
                                           DATE_FORMAT(a.trans_date, '%d-%m-%Y') AS invoiceDate,
                                           a.payment_ref AS paymentRef,
                                           a.status AS invStatus,
                                           a.total_gl_amount AS totalAmountIncVat,
                                           a.total_vat AS totalVat,
                                           a.debtor_id AS debtorId
                                    FROM invoice_income AS a,
                                         customer_master AS v,
                                         system_prefix AS sp,
                                         system_prefix AS sp1,
                                         system_prefix AS sp2
                                    WHERE a.debtor_id= v.id
                                      AND a.booking_year='${fiscalYearId}'
                                      AND sp.id=1
                                      AND sp1.id=11
                                      AND sp2.id=8"""

        if(userCustomerId)
            incomeInvoice += " AND a.customer_id='${userCustomerId}'"

        List<GroovyRowResult> incomeInvoiceList = db.rows(incomeInvoice)

        int total = incomeInvoiceList.size()
        db.close()

        return [incomeInvoiceList: incomeInvoiceList, count: total]
    }

    public Map getInvoiceIncomeDataAsMap(def invIncId){

        def invoiceIncomeInstanceTemp
        Map invoiceIncomeInstance = ["id"                : 0,"VERSION"            : 0,"bookingPeriod"      : '',
                                     "bookingYear"       : '',"budgetCustomerId"  : 0,"budgetItemIncomeId" : 0,
                                     "comments"          : '',"currencyCode"      : 'EUR',"customerAccountNo" : '',
                                     "customerId"        : 0,"dueDate"            : '',"invoiceNo"         : '',
                                     "isReverse"         : 0,"paidAmount"         : 0,"paidStatus"         : 0,
                                     "paymentRef"        : '',"reverseInvoiceId"  : '',"STATUS"            : "NEW",
                                     "termsId"           : 1,"totalGlAmount"      : 0,"totalVat"           : 0,
                                     "transDate"         : '',"showInvoiceNumber" : '',"debtorId"           : 0,
                                     "reminderDate1"     : '',"reminderDate2"     : '',"reminderDate3"      : '',
                                     "reminderDate4"     : '',"reminderType"      : 0,"reminderOnHold"      : 0,
                                     "allDocsOk"        : 'No']


        def editId = 0;
        if(invIncId instanceof String){
            editId = Integer.parseInt(invIncId)
        }
        else{
            editId = invIncId
        }

        if(editId > 0){

            String strQuery = """SELECT a.id,a.version,a.booking_period,a.booking_year,a.budget_customer_id,a.budget_item_income_id,a.comments,
                                a.currency_code,a.customer_id,a.invoice_no,a.is_reverse,a.paid_amount,a.paid_status,a.payment_ref,a.reverse_invoice_id,
                                a.status,a.terms_id,a.total_gl_amount,a.total_vat,a.trans_date,a.due_date,a.all_docs_ok,a.debtor_id,
                                CONCAT(sp.prefix,'-',a.invoice_no) AS showInvoiceNumber,a.reminder_date1,a.reminder_date2,a.reminder_date3,
                                a.reminder_date4,a.reminder_type,a.reminder_on_hold
                                FROM invoice_income AS a,system_prefix AS sp WHERE sp.id=8 AND a.id=${editId}"""

            invoiceIncomeInstanceTemp = new BudgetViewDatabaseService().executeQueryAtSingle(strQuery)

            strQuery = """SELECT sum(tm.amount*-1) AS paidAmount FROM invoice_income AS ic INNER JOIN trans_master tm ON tm.recenciliation_code = CONCAT(ic.id,'#1') AND
            tm.account_code=(Select debitor_gl_code from debit_credit_gl_setup) AND tm.trans_type <> 1 WHERE ic.id = ${editId} GROUP BY ic.id"""

            def paidAmountArr = new BudgetViewDatabaseService().executeQueryAtSingle(strQuery)

            def paidAmount

            if(paidAmountArr){
                paidAmount = paidAmountArr[0]
            }else{
                paidAmount = 0
            }

            invoiceIncomeInstance.id = invoiceIncomeInstanceTemp[0]
            invoiceIncomeInstance.VERSION = invoiceIncomeInstanceTemp[1]
            invoiceIncomeInstance.bookingPeriod = invoiceIncomeInstanceTemp[2]
            invoiceIncomeInstance.bookingYear = invoiceIncomeInstanceTemp[3]
            invoiceIncomeInstance.budgetCustomerId = invoiceIncomeInstanceTemp[4]
            invoiceIncomeInstance.budgetItemIncomeId = invoiceIncomeInstanceTemp[5]
            invoiceIncomeInstance.comments = invoiceIncomeInstanceTemp[6]
            invoiceIncomeInstance.currencyCode = invoiceIncomeInstanceTemp[7]

            invoiceIncomeInstance.customerId = invoiceIncomeInstanceTemp[8]
            invoiceIncomeInstance.invoiceNo = invoiceIncomeInstanceTemp[9]
            invoiceIncomeInstance.isReverse = invoiceIncomeInstanceTemp[10]

            invoiceIncomeInstance.paidAmount = paidAmount
//            invoiceIncomeInstance.paidAmount = invoiceIncomeInstanceTemp[11]
            invoiceIncomeInstance.paidStatus = invoiceIncomeInstanceTemp[12]
            invoiceIncomeInstance.paymentRef = invoiceIncomeInstanceTemp[13]
            invoiceIncomeInstance.reverseInvoiceId = invoiceIncomeInstanceTemp[14]
            invoiceIncomeInstance.STATUS = invoiceIncomeInstanceTemp[15]
            invoiceIncomeInstance.termsId = invoiceIncomeInstanceTemp[16]
            invoiceIncomeInstance.totalGlAmount = invoiceIncomeInstanceTemp[17]
            invoiceIncomeInstance.totalVat = invoiceIncomeInstanceTemp[18]
            invoiceIncomeInstance.transDate = invoiceIncomeInstanceTemp[19]
            invoiceIncomeInstance.dueDate = invoiceIncomeInstanceTemp[20]
            invoiceIncomeInstance.allDocsOk = invoiceIncomeInstanceTemp[21]
            invoiceIncomeInstance.debtorId = invoiceIncomeInstanceTemp[22]
            invoiceIncomeInstance.showInvoiceNumber = invoiceIncomeInstanceTemp[23]

            invoiceIncomeInstance.reminderDate1 = invoiceIncomeInstanceTemp[24]
            invoiceIncomeInstance.reminderDate2 = invoiceIncomeInstanceTemp[25]
            invoiceIncomeInstance.reminderDate3 = invoiceIncomeInstanceTemp[26]
            invoiceIncomeInstance.reminderDate4 = invoiceIncomeInstanceTemp[27]
            invoiceIncomeInstance.reminderType = invoiceIncomeInstanceTemp[28]
            invoiceIncomeInstance.reminderOnHold = invoiceIncomeInstanceTemp[29]
        }

        return invoiceIncomeInstance
    }

    def List<Map> getInvoiceIncomeDetailsDataAsMapList(def invIncId){

        List invoiceDetailsArr = new ArrayList();

        String strQuery = "SELECT accountCode,vatCategoryId,unitPrice,quantity,totalAmountWithVat,"+
                "totalAmountWithoutVat,vatRate,productCode,discountAmount,note,id "+
                " FROM bv.InvoiceIncomeDetails WHERE invoiceId = "+invIncId

        ArrayList queryResults = new BudgetViewDatabaseService().executeQuery(strQuery)

        queryResults.eachWithIndex { Phn, key ->
            Map map = ["JournalChartId"       : 0, "vatCategoryId": '1', "unitPrice": 0,
                       "quantity"             : 0, "totalAmountWithVat": 0, "totalPriceWithoutTax": 0,
                       "totalAmountWithoutVat": 0, "vatAmount": 0, "productId": 0,
                       "discount"             : 0, "note": '', "vatRate": 21.0]

            map.JournalChartId = Phn[0]
            map.vatCategoryId = Phn[1].toString()
            map.unitPrice = Phn[2].toString()
            map.quantity = Phn[3]
            map.totalAmountWithVat = Phn[4].toString()
            map.totalPriceWithoutTax = Phn[5]
            map.totalAmountWithoutVat = Phn[5].toString()
            map.vatAmount = Phn[4] - Phn[5]
            map.productId = Phn[7]
            map.discount = Phn[8]
            map.vatRate = Phn[6]
            map.note = Phn[9]
            map.ii_id = Phn[10]

            invoiceDetailsArr.add(map);
        }

        return invoiceDetailsArr;
    }

    def incomeInvoiceDataArray(def invIncId){
        def incomeHeadData
        def editId = 0;
        if(invIncId instanceof String){
            editId = Integer.parseInt(invIncId)
        }else{
            editId = invIncId
        }

        if(editId > 0){
            String strQuery = "Select CONCAT(sp.prefix,'-',ie.invoiceNo), ie.paidAmount,vm.vendorName," +
                    "vm.email,CONCAT(sp.prefix,'-',vm.vendorCode),ie.totalVat,ie.comments,ie.dueDate,ie.paymentRef, ie.transDate, ie.bookingPeriod, " +
                    "ie.bookingYear,ie.total_gl_amount FROM bv.InvoiceIncome AS ie ,bv.SystemPrefix As sp," +
                    "bv.VendorMaster as vm WHERE ie.id=" + editId + " AND sp.id=8 AND vm.id = ie.customerId "

            incomeHeadData = new BudgetViewDatabaseService().executeQuery(strQuery)
        }

        return incomeHeadData
    }

    def getLastCusotmerInvoiceDate(def invoiceSavedCustomerId){
        String strQuery = "SELECT DATE_FORMAT(ii.trans_date,'%d-%m-%Y') FROM invoice_income as ii where ii.customer_id = " + invoiceSavedCustomerId + " order by ii.id desc limit 1"
        def customerLastInvoiceDate = new BudgetViewDatabaseService().executeCustomQuery(strQuery)
        return customerLastInvoiceDate
    }

    def incomeInvoiceDetailsDataArray(def invIncId){
        def incomeDetailsData
        def editId = 0;
        if(invIncId instanceof String){
            editId = Integer.parseInt(invIncId)
        }else{
            editId = invIncId
        }

        if(editId > 0){
            String strQuery = "Select ied.accountCode,ied.discountAmount,ied.note,ied.productCode," +
                    "ied.quantity,ied.totalAmountWithoutVat,ied.totalAmountWithVat,ied.unitPrice,ied.vatCategoryId,ied.vatRate,CONCAT(sp.prefix,'-',iin.invoiceNo) " +
                    "AS invoiceId from bv.InvoiceIncome as iin, bv.InvoiceIncomeDetails  as ied, bv.SystemPrefix AS sp WHERE sp.id = 8 AND iin.id = ied.invoiceId AND" +
                    " ied.invoiceId='" + editId + "'"

            incomeDetailsData = new BudgetViewDatabaseService().executeQuery(strQuery)
        }


        return incomeDetailsData
    }

    def getVatAmountByCategory(def invIncId){
        def totalVatAmountByCategory
        def editId = 0;
        if(invIncId instanceof String){
            editId = Integer.parseInt(invIncId)
        }else{
            editId = invIncId
        }

        if(editId > 0){
            String strQuery = "Select iid.vat_rate, SUM(iid.total_amount_with_vat-iid.total_amount_without_vat) as toatalVatAmount" +
                    " from invoice_income_details as iid where iid.invoice_id = "+editId+" group by iid.vat_category_id"

            totalVatAmountByCategory = new BudgetViewDatabaseService().executeQuery(strQuery)

        }

        return totalVatAmountByCategory
    }

    def Map getCustomerGeneralAddress(def customerId){
        String strQuery = "SELECT address_line1,address_line2,city,postal_code,contact_person_name FROM " +
                "vendor_general_address WHERE vendor_id=" +customerId
        def customerGeneralAddress = new BudgetViewDatabaseService().executeQuery(strQuery)
        Map generalAddressMap = ["addressLine1":'',"addressLine2":'',"city":'',"postalCode":'',"contactPersonName":'']

        if(customerGeneralAddress.size() > 0){
            generalAddressMap.addressLine1 = customerGeneralAddress[0][0]
            generalAddressMap.addressLine2 = customerGeneralAddress[0][1]
            generalAddressMap.city = customerGeneralAddress[0][2]
            generalAddressMap.postalCode = customerGeneralAddress[0][3]
            generalAddressMap.contactPersonName = customerGeneralAddress[0][4]
        }

        return generalAddressMap
    }

    def calculateAcceptanceCost(def customerId,def debtorId){
        def acceptanceCost = 0 ;
        String strQuery = "Select * from invoice_income WHERE customer_id=${customerId} AND debtor_id =${debtorId}";

        ArrayList queryResults = new BudgetViewDatabaseService().executeQuery(strQuery)
        if(queryResults.size() > 0){
            return acceptanceCost;
        }

        strQuery = "Select acceptence_fee from debtor_customer WHERE customer_id=${customerId} AND debtor_id =${debtorId}";
        queryResults = new BudgetViewDatabaseService().executeQuery(strQuery)

        queryResults.eachWithIndex { Phn, key ->
            acceptanceCost = Phn[0]
        }

        return acceptanceCost
    }

    def saveImportedIncomeInvoice(def invoiceEntry,def userId,def customerDebtorInfo,def reportData=null, def bookingPeriod=null){
        ///////////INVOICE NO/////////////////
        def customerDebtorInfoDetails = new BudgetViewDatabaseService().executeCustomQuery("SELECT dc.id AS debCusId,pt.id AS payTermId,pt.days_before_due AS dueDays FROM debtor_customer AS dc INNER JOIN payment_terms AS pt ON dc.payment_term_id=pt.id WHERE dc.id="+customerDebtorInfo.customerDebtorId)
        def payment_term_id = 1
        if(customerDebtorInfoDetails)
            payment_term_id = customerDebtorInfoDetails[0][1]
        def InvoiceNo = new CoreParamsHelperTagLib().getNextGeneratedNumber('invoiceIncome')
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy")
        Date tempTransDate = df.parse(invoiceEntry.invoiceDate)
        Date tempDueDate
        if(customerDebtorInfoDetails)
            tempDueDate = tempTransDate + customerDebtorInfoDetails[0][2]
        else
            tempDueDate = tempTransDate + 32
        def activeFiscalYear = new CoreParamsHelperTagLib().getActiveFiscalYearString()
        String transDate = tempTransDate.format("yyyy-MM-dd hh:mm:ss")
        String dueDate = tempDueDate.format("yyyy-MM-dd hh:mm:ss")
        def acceptanceCost = calculateAcceptanceCost(customerDebtorInfo.customerId,customerDebtorInfo.debtorId)
        def debtorCustomerNo = customerDebtorInfo.debtorId + "#" +customerDebtorInfo.customerId
        def totalVat = invoiceEntry.vat

        Calendar cal = Calendar.getInstance()
        cal.setTime(tempTransDate)
        def bookingMonth
        if(bookingPeriod != null)
            bookingMonth = bookingPeriod
        else
            bookingMonth = cal.get(Calendar.MONTH) + 1
        def bookingYear = cal.get(Calendar.YEAR)

        if(invoiceEntry.isValidDebtor == '2'){
            Map updatededValue=[
                    address_line1: "invoiceEntry.add ressLine1",
                    address_line2:"invoiceEntry.addressLine2",
                    city: "invoiceEntry.debtorCity",
                    postal_code: "invoiceEntry.debtorAreaCode}",
            ]
            def updatedTableName="customer_general_address"
            def updatedWhereSrting="id="+"'"+id+"'"
            new BudgetViewDatabaseService().update(updatededValue,updatedTableName,updatedWhereSrting)

        }


        Map InvoiceIncome = [
                bookingPeriod     : bookingMonth,
                bookingYear       : bookingYear,
                budgetItemIncomeId: 0,
                comments          : "",
                currencyCode      : "EURO",
                customerId        : customerDebtorInfo.customerId,
                debtorId          : customerDebtorInfo.debtorId,
                dueDate           : dueDate,
                invoiceNo         : InvoiceNo,
                isReverse         : 0,
                paidStatus        : 0,
                paidAmount        : 0.00,
                reverseInvoiceId  : 0,
                status            : "new",
                termsId           : payment_term_id,
                totalGlAmount     : Double.parseDouble(invoiceEntry.subtotal.toString()).round(2),
                totalVat          : Double.parseDouble(totalVat.toString()).round(2),
                transDate         : transDate,
                paymentRef        : invoiceEntry.paymentRef,
                budgetCustomerId    : 0,
                debitor_customer_no : debtorCustomerNo,
                allDocsOk           : "Yes",
                userIdCreate        : userId,
                userIdUpdate        : userId,
                acceptence_fee      : acceptanceCost,
                historyStatus       : 0
        ]

        def tableNameInvoiceIncome = "InvoiceIncome"
        def insertedInvoiceId = new BudgetViewDatabaseService().insert(InvoiceIncome, tableNameInvoiceIncome)
        if(insertedInvoiceId)
            reportData.processedRecords += 1
        insertImportDetailsAndTransDataToTable(invoiceEntry,userId,customerDebtorInfo,insertedInvoiceId,bookingMonth)
    }

    def saveIncomeInvoice(def sessionDataArr, def paramsDataArr,def fiscalYear){

        def budgetItemId = 0
        def budgetCustomerId = 0

        Double headTotalGlAmount = 0.00
        Double lineTotalVATAmount = 0.00

        for (int k = 0; k < sessionDataArr.invoiceIncomeArr.size(); k++) {
            headTotalGlAmount = headTotalGlAmount + sessionDataArr.invoiceIncomeArr[k].totalPriceWithoutTax
            lineTotalVATAmount = lineTotalVATAmount + sessionDataArr.invoiceIncomeArr[k].vatAmount
        }

        ///////////INVOICE NO/////////////////
        def InvoiceNo = new CoreParamsHelperTagLib().getNextGeneratedNumber('invoiceIncome')
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        Date tempDueDate = df.parse(paramsDataArr.dueDate);
        Date tempTransDate = df.parse(paramsDataArr.transDate);
        String TransDate = tempTransDate.format("yyyy-MM-dd hh:mm:ss")
        String dueDate = tempDueDate.format("yyyy-MM-dd hh:mm:ss")

        def bookingDate = new ApplicationUtil().convertDateFromMonthAndYear(paramsDataArr.bookingPeriod,paramsDataArr.bookingYear)

        def acceptanceCost = calculateAcceptanceCost(paramsDataArr.customerId,paramsDataArr.debtorId)

        def debitorCustomerNo = paramsDataArr.debtorId + "#" +paramsDataArr.customerId
        Map InvoiceIncome = [
                bookingPeriod     : paramsDataArr.bookingPeriod,
                bookingYear       : paramsDataArr.bookingYear,
                budgetItemIncomeId: budgetItemId,
                comments          : paramsDataArr.comments,
                currencyCode      : "EURO",
                customerId        : paramsDataArr.customerId,
                debtorId          : paramsDataArr.debtorId,
                dueDate           : dueDate,
                invoiceNo         : InvoiceNo,
                isReverse         : 0,
                paidStatus        : 0,
                paidAmount        : 0.00,
                reverseInvoiceId  : 0,
                status            : "new",
                termsId           : paramsDataArr.termsId,
                totalGlAmount     : Double.parseDouble(headTotalGlAmount.toString()).round(2),
                totalVat          : Double.parseDouble(lineTotalVATAmount.toString()).round(2),
                transDate         : TransDate,
                paymentRef        : paramsDataArr.paymentRef,
                //budgetCustomerId  : budgetcustomerIdArr[0]
                budgetCustomerId    : budgetCustomerId,
                debitor_customer_no : debitorCustomerNo,
                allDocsOk           : paramsDataArr.documentChecked,
                userIdCreate        : springSecurityService.principal.id,
                userIdUpdate        : springSecurityService.principal.id,
                acceptence_fee      : acceptanceCost,
                historyStatus       : 0
        ]

        def tableNameInvoiceIncome = "InvoiceIncome"
        def insertedInvoiceId = new BudgetViewDatabaseService().insert(InvoiceIncome, tableNameInvoiceIncome)

        //Insert data to rest of the tables.
        insertDetailsAndTransDataToTable(sessionDataArr.invoiceIncomeArr, paramsDataArr,headTotalGlAmount,
                lineTotalVATAmount,bookingDate,insertedInvoiceId,false)

        return insertedInvoiceId
    }

    def updateIncomeInvoice(def sessionDataArr, def paramsDataArr){

        Double dAmountWithoutVAT = 0.00
        Double dTotalVATAmount = 0.00

        if (sessionDataArr) {
            for (int k = 0; k < sessionDataArr.size(); k++) {
                double tempAmountWithoutVat = sessionDataArr[k].totalPriceWithoutTax
                dAmountWithoutVAT = dAmountWithoutVAT + tempAmountWithoutVat
                dTotalVATAmount = dTotalVATAmount + sessionDataArr[k].vatAmount
            }
        }

        def dateArr = paramsDataArr.transDate.split("-")
        if (dateArr[0].length() < 4) {
            paramsDataArr.transDate = dateArr[2] + "-" + dateArr[1] + "-" + dateArr[0]
        }
        def dateDueArr = paramsDataArr.dueDate.split("-")
        if (dateDueArr[0].length() < 4) {
            paramsDataArr.dueDate = dateDueArr[2] + "-" + dateDueArr[1] + "-" + dateDueArr[0]
        }

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date tempTransDate = df.parse(paramsDataArr.transDate);
        String TransDate = tempTransDate.format("yyyy-MM-dd hh:mm:ss")

        def debitorCustomerNo = paramsDataArr.debtorId + "#" + paramsDataArr.customerId
        ///////////////Update the Invoice Income Head Information//////
        Map updatededValue = [
                bookingPeriod      : paramsDataArr.bookingPeriod,
                bookingYear        : paramsDataArr.bookingYear,
                debitor_customer_no: debitorCustomerNo,
                comments           : paramsDataArr.comments,
                currencyCode       : "EURO",
                customerId         : paramsDataArr.customerId,
                debtorId           : paramsDataArr.debtorId,
                dueDate            : paramsDataArr.dueDate,
                status             : "updated",
                termsId            : paramsDataArr.termsId,
                totalGlAmount      : Double.parseDouble(dAmountWithoutVAT.toString()).round(2),
                totalVat           : Double.parseDouble(dTotalVATAmount.toString()).round(2),
                transDate          : paramsDataArr.transDate,
                paymentRef         : paramsDataArr.paymentRef,
                allDocsOk          : paramsDataArr.documentChecked,
                userIdCreate       : springSecurityService.principal.id,
                userIdUpdate       : springSecurityService.principal.id
        ]

        def updatedTableName = "invoice_income"
        def updatedWhereSrting = "id="+ paramsDataArr.editId
        new BudgetViewDatabaseService().update(updatededValue, updatedTableName, updatedWhereSrting)

        ////////Delete InvoiceDetails///////////////
        def InvoiceIncomeDetailsDelete = new BudgetViewDatabaseService().executeUpdate("DELETE FROM  bv.InvoiceIncomeDetails WHERE invoiceId=" + paramsDataArr.editId)
        //////////Delete TransMaster///////////////
        def TransMasterDelete = new BudgetViewDatabaseService().executeUpdate("DELETE FROM  bv.TransMaster WHERE transType=1 AND invoiceNo=" + paramsDataArr.editId)
        //////////Delete CompanyBankTrans///////////////
        def CompanyBankTransDelete = new BudgetViewDatabaseService().executeUpdate("DELETE FROM  bv.CompanyBankTrans WHERE transType=1 AND invoiceNo=" + paramsDataArr.editId)


        def bookingDate = new ApplicationUtil().convertDateFromMonthAndYear(paramsDataArr.bookingPeriod,paramsDataArr.bookingYear)

        //Insert data to rest of the tables.
        insertDetailsAndTransDataToTable(sessionDataArr, paramsDataArr,dAmountWithoutVAT,dTotalVATAmount,
                bookingDate,paramsDataArr.editId,true);
    }

    def insertImportDetailsAndTransDataToTable(def invoiceEntry,def userId,def customerDebtorInfo,def insertedInvoiceId, def bookingPeriod=null){
        /*def sessionDataArr, def paramsDataArr, def dAmountWithoutVAT,def dTotalVATAmount, def bookingDate,def invoiceId,def isUpdate,def insertedInvoiceId*/
        String strRecenciliationCode = ""
        def activeFiscalYear = new CoreParamsHelperTagLib().getActiveFiscalYearString()
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy")
        Date tempTransDate = df.parse(invoiceEntry.invoiceDate)

        Calendar cal = Calendar.getInstance()
        cal.setTime(tempTransDate)
        def bookingMonth
        if(bookingPeriod != null)
            bookingMonth = bookingPeriod
        else
            bookingMonth = cal.get(Calendar.MONTH) + 1

        def bookingYear = cal.get(Calendar.YEAR)

        String transDate = tempTransDate.format("yyyy-MM-dd hh:mm:ss")
        def vatCategory = 1
        def vatRate = 21
        def totalVat = invoiceEntry.vat
        def totalAmountWithOutVat = invoiceEntry.subtotal
        if(totalVat != 0){
            def vatFor21 = ((totalAmountWithOutVat * 21) / 100).round(2)
            if(Math.abs(totalVat - vatFor21) <= 0.05){
                vatCategory = 1
                vatRate = 21
            }
            def vatFor06 = ((totalAmountWithOutVat * 6) / 100).round(2)
            if(Math.abs(totalVat - vatFor06) <= 0.05){
                vatCategory = 3
                vatRate = 6
            }
        }
        else if(totalVat == 0 && invoiceEntry.total==invoiceEntry.subtotal){
            vatCategory = 4
            vatRate = 0
        }

        def glAccountCodeTotal = "8100"
        def glAccountCodeVat = "8101"
        def glAccountCodeTotalVat = "1300"
        def invoiceId = insertedInvoiceId

        def bookingDate = new ApplicationUtil().convertDateFromMonthAndYear(bookingMonth,activeFiscalYear)

        Map invoiceLineDetails = [
                accountCode          : glAccountCodeTotal,
                discountAmount       : 0.0,
                invoiceId            : invoiceId,
                note                 : "",
                productCode          : 1,
                quantity             : 1,
                totalAmountWithVat   : Double.parseDouble((invoiceEntry.subtotal + totalVat).toString()).round(2),
                totalAmountWithoutVat: Double.parseDouble(invoiceEntry.subtotal.toString()).round(2),
                unitPrice            : Double.parseDouble(invoiceEntry.subtotal.toString()).round(2),
                vatCategoryId        : vatCategory,
                vatRate              : vatRate
        ]

        def tableNameInvoiceIncomeDetails = "InvoiceIncomeDetails"
        new BudgetViewDatabaseService().insert(invoiceLineDetails, tableNameInvoiceIncomeDetails)

        //Trans Master
        strRecenciliationCode = ""+invoiceId+"#1"//BDR-77
        Double tempAmount = -invoiceEntry.subtotal
        Map trnMas = [
                accountCode  : glAccountCodeTotal,
                amount       : Double.parseDouble(tempAmount.toString()).round(2),
                transDate    : transDate,
                transType    : 1,
                invoiceNo    : invoiceId,
                bookingPeriod: bookingMonth,
                bookingYear  : bookingYear,
                userId       : userId,
                createDate   : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()),
                process      : com.bv.constants.Process.INVOICE_INCOME,
                recenciliationCode : strRecenciliationCode,
                customerId   : customerDebtorInfo.debtorId,
                vendorId     : customerDebtorInfo.customerId,
                bookingDate  : bookingDate
        ]

        def tableNameTransMaster = "TransMaster" //BDR-4
        new BudgetViewDatabaseService().insert(trnMas, tableNameTransMaster)


        if (vatRate != 0) {
            strRecenciliationCode = "" + invoiceId+"#1"//BDR-77
            Double tempVatAmount = -totalVat
            Map trnVatMas = [
                    accountCode  : glAccountCodeVat,
                    amount       : Double.parseDouble(tempVatAmount.toString()).round(2),
                    transDate    : transDate,
                    transType    : 1,
                    invoiceNo    : invoiceId,
                    bookingPeriod: bookingMonth,
                    bookingYear  : bookingYear,
                    userId       : userId,
                    createDate   : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()),
                    process      : com.bv.constants.Process.INVOICE_INCOME,
                    recenciliationCode : strRecenciliationCode,
                    customerId   : customerDebtorInfo.debtorId,
                    vendorId     : customerDebtorInfo.customerId,
                    bookingDate  : bookingDate
            ]

            def tableNametrnVatMas = "TransMaster" //BDR-4
            new BudgetViewDatabaseService().insert(trnVatMas, tableNametrnVatMas)
        }

        //////////////////////////Creditor Entry IN Master Table/////////////////////

        strRecenciliationCode = ""+invoiceId+"#1"//BDR-77
        //Trans Master
        Map transMasTotal = [
                accountCode  : glAccountCodeTotalVat,
                amount       : Double.parseDouble((invoiceEntry.subtotal + totalVat).toString()).round(2),
                transDate    : transDate,
                transType    : 1,
                invoiceNo    : invoiceId,
                bookingPeriod: bookingMonth,
                bookingYear  : bookingYear,
                userId       : userId,
                createDate   : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()),
                process      : com.bv.constants.Process.INVOICE_INCOME,
                recenciliationCode : strRecenciliationCode,
                customerId   : customerDebtorInfo.debtorId,
                vendorId     : customerDebtorInfo.customerId,
                bookingDate  : bookingDate
        ]

        def tableNametrnMas = "TransMaster" //BDR-4
        new BudgetViewDatabaseService().insert(transMasTotal, tableNametrnMas)
    }

    def insertDetailsAndTransDataToTable(def sessionDataArr, def paramsDataArr,
                                         def dAmountWithoutVAT,def dTotalVATAmount,
                                         def bookingDate,def invoiceId,def isUpdate){

        String strRecenciliationCode = ""

        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        Date tempDueDate = df.parse(paramsDataArr.dueDate);
        Date tempTransDate = df.parse(paramsDataArr.transDate);
        String dueDate = tempDueDate.format("yyyy-MM-dd hh:mm:ss")
        String transDate = tempTransDate.format("yyyy-MM-dd hh:mm:ss")

        def bookingPeriod = paramsDataArr.bookingPeriod
        if(isUpdate){
            transDate = paramsDataArr.transDate
            //bookingPeriod = paramsDataArr.bookingPeriodStartMonthForChange
        }

        for (int i = 0; i < sessionDataArr.size(); i++) {

            Map invoiceLineDetails = [
                    accountCode          : sessionDataArr[i].JournalChartId,
                    discountAmount       : 0.0,
                    invoiceId            : invoiceId,
                    note                 : sessionDataArr[i].note,
                    productCode          : 1,
                    quantity             : 1,
                    totalAmountWithVat   : Double.parseDouble((sessionDataArr[i].vatAmount + sessionDataArr[i].totalPriceWithoutTax).toString()).round(2),
                    totalAmountWithoutVat: Double.parseDouble(sessionDataArr[i].unitPrice.toString()).round(2),
                    unitPrice            : Double.parseDouble(sessionDataArr[i].unitPrice.toString()).round(2),
                    vatCategoryId        : sessionDataArr[i].vatCategoryId,
                    vatRate              : sessionDataArr[i].vatRate
            ]

            def tableNameInvoiceIncomeDetails = "InvoiceIncomeDetails"
            def detailsInsertedId = new BudgetViewDatabaseService().insert(invoiceLineDetails, tableNameInvoiceIncomeDetails)

            //Trans Master
            strRecenciliationCode = ""+invoiceId+"#1"//BDR-77
            Double tempAmount = -sessionDataArr[i].totalPriceWithoutTax
            Map trnMas = [
                    accountCode  : sessionDataArr[i].JournalChartId,
                    amount       : Double.parseDouble(tempAmount.toString()).round(2),
                    transDate    : transDate,
                    transType    : 1,
                    invoiceNo    : invoiceId,
                    bookingPeriod: bookingPeriod,
                    bookingYear  : paramsDataArr.bookingYear,
                    userId       : springSecurityService.principal.id,
                    createDate   : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()),
                    process      : com.bv.constants.Process.INVOICE_INCOME,
                    recenciliationCode : strRecenciliationCode,
                    customerId   : paramsDataArr.debtorId,
                    vendorId     : paramsDataArr.customerId,
                    bookingDate  : bookingDate
            ]

            def tableNameTransMaster = "TransMaster" //BDR-4
            new BudgetViewDatabaseService().insert(trnMas, tableNameTransMaster)

            def comBankInstance = new CoreParamsHelperTagLib().getCompanyBankAccountByGlAccount(sessionDataArr[i].JournalChartId)

            if (comBankInstance.size()) {
                Map comBankTrans = [
                        amount         : Double.parseDouble(sessionDataArr[i].totalPriceWithoutTax.toString()).round(2),
                        companyBankCode: comBankInstance,
                        invoiceNo      : invoiceId,
                        personCode     : paramsDataArr.customerId,
                        transDate      : transDate,
                        transType      : 1,
                        bookingPeriod  : bookingPeriod,
                        bookingYear    : paramsDataArr.bookingYear
                ]

                def tableNameCompanyBankTrans = "CompanyBankTrans"
                new BudgetViewDatabaseService().insert(comBankTrans, tableNameCompanyBankTrans)
            }
            //////////////////////////////
            //            def vatRate=sessionDataArr[i].vatRate
            String vat = sessionDataArr[i].vatRate
            def vatRate = Double.parseDouble(vat)
            def vatId = new CoreParamsHelperTagLib().getVatCategoryIdFromRate(vatRate)

            if ( vatRate > 0) {

                def vatGLAccountInfo = new CoreParamsHelperTagLib().getSpacificVatGLAccount(vatId)
                def vatGLAcc = ""
                if (vatGLAccountInfo.size()) {
                    vatGLAcc = vatGLAccountInfo[2];
                }

                strRecenciliationCode = "" + invoiceId+"#1"//BDR-77
                if (vatGLAcc) {
                    Double tempVatAmount = -sessionDataArr[i].vatAmount
                    Map trnVatMas = [
                            accountCode  : vatGLAcc,
                            amount       : Double.parseDouble(tempVatAmount.toString()).round(2),
                            transDate    : transDate,
                            transType    : 1,
                            invoiceNo    : invoiceId,
                            bookingPeriod: bookingPeriod,
                            bookingYear  : paramsDataArr.bookingYear,
                            userId       : springSecurityService.principal.id,
                            createDate   : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()),
                            process      : com.bv.constants.Process.INVOICE_INCOME,
                            recenciliationCode : strRecenciliationCode,
                            customerId   : paramsDataArr.debtorId,
                            vendorId     : paramsDataArr.customerId,
                            bookingDate  : bookingDate
                    ]

                    def tableNametrnVatMas = "TransMaster" //BDR-4
                    new BudgetViewDatabaseService().insert(trnVatMas, tableNametrnVatMas)
                }

                def comVatBankInstance = new CoreParamsHelperTagLib().getCompanyBankAccountByGlAccount(vatGLAcc)

                if (comVatBankInstance.size()) {

                    Map comVatBankTrans = [
                            amount         : Double.parseDouble(sessionDataArr[i].vatAmount.toString()).round(2),
                            companyBankCode: comVatBankInstance,
                            invoiceNo      : invoiceId,
                            personCode     : paramsDataArr.customerId,
                            transDate      : transDate,
                            transType      : 1,
                            bookingPeriod  : bookingPeriod,
                            bookingYear    : paramsDataArr.bookingYear
                    ]

                    def tableNamecomVatBankTrans = "CompanyBankTrans"
                    new BudgetViewDatabaseService().insert(comVatBankTrans, tableNamecomVatBankTrans)

                }
            }
            //dTotalVATAmount = dTotalVATAmount + sessionDataArr[i].vatAmount
        }

        //////////////////////////Creditor Entry IN Master Table/////////////////////
        def creditorCreditGlSetupInfo = new CoreParamsHelperTagLib().getDebitCreditGlSetupInfo()
        strRecenciliationCode = ""+invoiceId+"#1"//BDR-77
        //Trans Master
        Map trnMas = [
                accountCode  : creditorCreditGlSetupInfo[2],
                amount       : Double.parseDouble((dAmountWithoutVAT + dTotalVATAmount).toString()).round(2),
                transDate    : transDate,
                transType    : 1,
                invoiceNo    : invoiceId,
                bookingPeriod: bookingPeriod,
                bookingYear  : paramsDataArr.bookingYear,
                userId       : springSecurityService.principal.id,
                createDate   : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()),
                process      : com.bv.constants.Process.INVOICE_INCOME,
                recenciliationCode : strRecenciliationCode,
                customerId   : paramsDataArr.debtorId,
                vendorId     : paramsDataArr.customerId,
                bookingDate  : bookingDate
        ]

        def tableNametrnMas = "TransMaster" //BDR-4
        new BudgetViewDatabaseService().insert(trnMas, tableNametrnMas)

        sessionDataArr = []

    }

    LinkedHashMap reportListOfIncomeBookedHas(int offset, String limit, String sortItem, String sortOrder, String fiscalYearId) {

        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance( companyConfig.serverUrl+companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)
        String wherePostCondition = ""

        User user = springSecurityService.currentUser
        def userCustomerId = user.customerId

        if (fiscalYearId) { wherePostCondition = wherePostCondition + " AND a.booking_year=" + fiscalYearId }
        if (userCustomerId) { wherePostCondition += " AND a.customer_id = '${userCustomerId}'" }

        String incomeEntry = """SELECT a.id,CONCAT(sp.prefix,'-',a.invoice_no) AS invoiceNumber,a.customer_id AS customerId,a.debtor_id AS debtorId,
                                a.budget_customer_id AS budgetCustomerId,
                                CONCAT(v.vendor_name,' [',spv.prefix,'-',v.vendor_code,']') AS customerName,
                                CONCAT(cm.customer_name,' [',spcm.prefix,'-',cm.customer_code,']') AS debtorName,
                                a.booking_period,a.booking_year,CONCAT((CASE a.booking_period WHEN '1' THEN 'Jan' WHEN '2' THEN 'Feb' WHEN '3' THEN 'Mar'
                                WHEN '4' THEN 'Apr' WHEN '5' THEN 'May' WHEN '6' THEN 'Jun' WHEN '7' THEN 'Jul' WHEN '8' THEN 'Aug' WHEN '9' THEN 'Sep'
                                WHEN '10' THEN 'Oct' WHEN '11' THEN 'Nov' WHEN '12' THEN 'Dec' END),'-',a.booking_year) AS bookinhPeriod,
                                a.total_gl_amount AS totalGlAmount ,a.total_vat AS totalVat,DATE_FORMAT(a.trans_date, '%d-%m-%Y') AS transactionDate,
                                budget_item_income_id, a.payment_ref as paymentRef, a.status as invStatus FROM invoice_income AS a,system_prefix AS sp,
                                customer_master AS cm,vendor_master AS v,system_prefix AS spcm,system_prefix AS spv WHERE sp.id=8
                                AND a.debtor_id=cm.id AND a.customer_id=v.id AND spcm.id=1 AND spv.id=2 ${wherePostCondition}"""

        List<GroovyRowResult> incomeBookedList = db.rows(incomeEntry)

        String countQuery = """SELECT COUNT(a.id) AS totalInvoiceNo FROM invoice_income AS a,system_prefix AS sp,customer_master AS cm,system_prefix AS spcm WHERE sp.id=8 AND a.STATUS=1 AND a.is_reverse=0 AND a.reverse_invoice_id=0 AND a.total_gl_amount!=0 AND a.budget_customer_id=cm.id AND spcm.id=1 ${wherePostCondition} """;
        List<GroovyRowResult> count_result = db.rows(countQuery)
        int total = count_result[0].totalInvoiceNo
        return [incomeBookedList: incomeBookedList, count: total]
    }

    LinkedHashMap reportListOfIncomeBooked(String fiscalYearId) {

        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance( companyConfig.serverUrl+companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)
        String wherePostCondition = ""

        User user = springSecurityService.currentUser
        def userCustomerId = user.customerId

        if (fiscalYearId) { wherePostCondition = wherePostCondition + " AND a.booking_year=" + fiscalYearId }
        if (userCustomerId) { wherePostCondition += " AND a.customer_id = '${userCustomerId}'" }

        String incomeEntry = """SELECT a.id,CONCAT(sp.prefix,'-',a.invoice_no) AS invoiceNumber,a.customer_id AS customerId,a.debtor_id AS debtorId,
                                a.budget_customer_id AS budgetCustomerId,CONCAT(cm.customer_name,' [',spcm.prefix,'-',cm.customer_code,']') AS debtorName,
                                a.booking_period,a.booking_year,CONCAT((CASE a.booking_period WHEN '1' THEN 'Jan' WHEN '2' THEN 'Feb' WHEN '3' THEN 'Mar'
                                WHEN '4' THEN 'Apr' WHEN '5' THEN 'May' WHEN '6' THEN 'Jun' WHEN '7' THEN 'Jul' WHEN '8' THEN 'Aug' WHEN '9' THEN 'Sep'
                                WHEN '10' THEN 'Oct' WHEN '11' THEN 'Nov' WHEN '12' THEN 'Dec' END),'-',a.booking_year) AS bookinhPeriod,
                                a.total_gl_amount AS totalGlAmount ,a.total_vat AS totalVat,DATE_FORMAT(a.trans_date, '%d-%m-%Y') AS transactionDate,
                                budget_item_income_id, a.payment_ref as paymentRef, a.status as invStatus FROM invoice_income AS a,system_prefix AS sp,
                                customer_master AS cm,system_prefix AS spcm WHERE sp.id=8
                                AND a.debtor_id=cm.id AND spcm.id=1 ${wherePostCondition} ORDER BY cm.customer_name"""

        List<GroovyRowResult> incomeBookedList = db.rows(incomeEntry)

        int total = incomeBookedList.size()
        return [incomeBookedList: incomeBookedList, count: total]
    }
}
