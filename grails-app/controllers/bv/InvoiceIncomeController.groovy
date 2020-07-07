package bv

import factoring.BudgetViewDatabaseService
import factoring.CoreParamsHelperTagLib
import factoring.CustomerMaster
import factoring.DashboardDetailsTagLib
import factoring.DebtorCustomerService
import factoring.IncomeInvoiceService

import factoring.InvoiceUtil
import factoring.VendorMaster
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService
import org.springframework.beans.factory.annotation.Autowired

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class InvoiceIncomeController {
    @Autowired
    BudgetViewDatabaseService budgetViewDatabaseService
    IncomeInvoiceService incomeInvoiceService
    DebtorCustomerService debtorCustomerService
    SpringSecurityService springSecurityService

    InvoiceUtil incomeInvoiceUtil

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        session.invoiceIncomeArr = []
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        def invoiceSavedCustomerId = "0"
        if(params.customerIdInv)
            invoiceSavedCustomerId = params.customerIdInv
        if(session.lastSelectedCustomerId)
            invoiceSavedCustomerId = session.lastSelectedCustomerId[0]
        if(session.invoiceIncomeArr?.customerId)
            invoiceSavedCustomerId = session.invoiceIncomeArr?.customerId[0]

        def datePickerTransDate
        if(!invoiceSavedCustomerId.equals("0"))
            datePickerTransDate = incomeInvoiceService.getLastCusotmerInvoiceDate(invoiceSavedCustomerId)
        if(datePickerTransDate)
            datePickerTransDate = datePickerTransDate[0][0]
        else
            datePickerTransDate = '0'
        session.editData = 0
        Map invoiceIncomeInstance

        if (params.editId) {

            invoiceIncomeInstance = incomeInvoiceService.getInvoiceIncomeDataAsMap(params.editId)
            List<Map> invoiceDetailsArr = incomeInvoiceService.getInvoiceIncomeDetailsDataAsMapList(params.editId)

            if(!params.backFrmMaster){
                session.invoiceIncomeArr = []
                for(int i=0;i<invoiceDetailsArr.size();i++){
                    session.invoiceIncomeArr << invoiceDetailsArr[i]
                }
            }

            session.dummyData = 1
            flash.message = ""

        } else {
            invoiceIncomeInstance = incomeInvoiceService.getInvoiceIncomeDataAsMap(0)
            flash.message = ""
            if(params.keepSession == "1"){
                invoiceIncomeInstance.paymentRef = session.paymentRef
            }else{
                session.paymentRef = ""
                session.selCustomerId = 0
                session.invoiceIncomeArr = []
            }
        }

        [invoiceIncomeInstance: invoiceIncomeInstance,invoiceSavedCustomerId:invoiceSavedCustomerId,datePickerTransDate:datePickerTransDate]
    }

    def saveDataToSession(){

        String strMsg = ""
        ArrayList invoiceIncomeData

        if (params.editId == "") {
            invoiceIncomeData = new BudgetViewDatabaseService().executeQuery("""Select id from invoice_income where
                                      invoice_income.customer_id = '$params.customerId' AND invoice_income.payment_ref = '$params.paymentRef' """);
        }else{
            invoiceIncomeData = new BudgetViewDatabaseService().executeQuery("""Select id from invoice_income where
                                      invoice_income.customer_id = '$params.customerId' AND invoice_income.payment_ref = '$params.paymentRef' AND id != '$params.editId' """);
        }

        if(invoiceIncomeData.size() > 0){
            //Payment ref exist
            strMsg = message(code: 'bv.incInvSelectAnotherPaymentReference.label', default: 'This Payment Reference Already Exist for This Customer.Please Select Another Payment Reference')
//            strMsg = flash.message
//            strMsg = "<b style=\"font-size:15px;color:#ff0000;\">" + "This payment reference already exist for this customer.Please select another payment reference." + "</b>";
        }else{
            session.paymentRef = params.paymentRef;
            session.selCustomerId = params.customerId;
        }

        render strMsg
    }

    /*def create() {
        [invoiceIncomeInstance: new InvoiceIncome(params)]
    }
*/
    def showInvoiceIncList() {
        def ActiveFiscalYear = new CoreParamsHelperTagLib().getActiveFiscalYear()
        def FiscalYearInfo = new CoreParamsHelperTagLib().getActiveFiscalYearInformation(ActiveFiscalYear)

        String gridOutput
        String fiscalYearId = FiscalYearInfo[0][4]

        incomeInvoiceUtil = new InvoiceUtil()
        int start = 0

        LinkedHashMap gridResult
        gridResult = incomeInvoiceService.listOfIncomeInvoice(fiscalYearId)

        List incInvList = incomeInvoiceUtil.wrapInvoiceIncomeInGrid(gridResult.incomeInvoiceList, start,fiscalYearId,getContextPath())

        LinkedHashMap result = [draw: 1, recordsTotal: gridResult.count, recordsFiltered:  gridResult.count,data : incInvList.cell]
        gridOutput = result as JSON

        render gridOutput
    }

    def save() {
        params.unitPrice = new InvoiceUtil().replaceComaToDot(params.unitPrice)
        params.price = params.unitPrice

        //Input Vat
        params.vatAmount = new InvoiceUtil().replaceComaToDot(params.vatAmount)
        params.vatAmount = Double.parseDouble(params.vatAmount)
        params.vatCategoryId = new CoreParamsHelperTagLib().getVatCategoryIdFromRate(Double.parseDouble(params.vatRate))
        params.totalPriceWithoutTax = Double.parseDouble(params.price)

        def df = new SimpleDateFormat("dd-MM-yyyy")
        def invoiceDate = df.parse(params.transDate)


        if(new CoreParamsHelperTagLib().isDateClosedFiscalYear(invoiceDate)){
            def param = params
            if (session.invoiceIncomeArr) {
                session.invoiceIncomeArr << params
            } else {
                session.invoiceIncomeArr = []
                session.invoiceIncomeArr << params
            }
        }
        else{
            flash.message = "Invoice date is in closed Fiscal Year"
        }
        render(layout: 'ajax', template: 'linelist', model: [editId: params.editId])
    }

    def update(Long id, Long version) {

        def editId = Integer.parseInt(params.sessionPId)
        def ii_iD = session.invoiceIncomeArr[editId]['ii_id']

        if (session.invoiceIncomeEditArr) {
            session.invoiceIncomeEditArr << ii_iD
        } else {
            session.invoiceIncomeEditArr = []
            session.invoiceIncomeEditArr << ii_iD
        }

        params.unitPrice = new InvoiceUtil().replaceComaToDot(params.unitPrice)
        params.vatAmount = new InvoiceUtil().replaceComaToDot(params.vatAmount)
        params.vatAmount = Double.parseDouble(params.vatAmount)

        params.editId = params.InvoiceIncomeEditId
        params.vatCategoryId = new CoreParamsHelperTagLib().getVatCategoryIdFromRate(Double.parseDouble(params.vatRate))

        def temp = []
        temp = session.invoiceIncomeArr
        temp.remove(Integer.parseInt(params.sessionPId))
        session.invoiceIncomeArr = temp

        params.price = Double.parseDouble(params.unitPrice) + params.vatAmount
        BigDecimal b = new BigDecimal(params.price)
        params.price = b.setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
        params.totalPriceWithoutTax = Double.parseDouble(params.unitPrice)

        def df = new SimpleDateFormat("dd-MM-yyyy")
        def invoiceDate = df.parse(params.transDate)
        if(new CoreParamsHelperTagLib().isDateClosedFiscalYear(invoiceDate)){
            if (session.invoiceIncomeArr) {
                session.invoiceIncomeArr << params
            } else {
                session.invoiceIncomeArr = []
                session.invoiceIncomeArr << params
            }
        }else{
            flash.message = "Invoice date is in closed Fiscal Year"
        }

        render(layout: 'ajax',template: 'linelist', model: [editIdSel:editId])
    }

    def show(Long id) {
        def invoiceIncomeInstance = InvoiceIncome.get(id)
        if (!invoiceIncomeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'invoiceIncome.label', default: 'InvoiceIncome'), id])
            redirect(action: "list")
            return
        }

        [invoiceIncomeInstance: invoiceIncomeInstance]
    }

    def edit(int id) {

        def editId=Integer.parseInt(params.id)
        def editData = session.invoiceIncomeArr.get(Integer.parseInt(params.id))
        def vatCatID = session.invoiceIncomeArr[editId]['vatCategoryId']
        def defaultGlAccount = session.invoiceIncomeArr[editId]['JournalChartId']
        String vatCatId = vatCatID.toString()

        render(template: "ajaxEditForm", model: [invoiceIncomeInstance: editData, sessionPId: params.id,
                                                 budgetCustomerId : params.budgetCustomerId,vatCategoryId:vatCatId,
                                                 glAccount:defaultGlAccount])
    }

    def deleteItem() {
        def temp = []
        temp = session.invoiceIncomeArr
        def tempId = params.id
        def firstArr = tempId.split("::")
        def deleteId = firstArr[0]

        params.editId = firstArr[1]
        //println(""+params.budgetCustomerId)
        temp.remove(Integer.parseInt(deleteId))
        session.invoiceIncomeArr = temp

        render(layout: 'ajax',template: "linelist")
    }

    def close() {
        String vatCategoryId = ""+params.vatCategoryId

        render(layout: 'ajax',template: "lineInnerForm",model:[vatCatId : vatCategoryId] )
    }

    def saveInvoice() {

        session.dummyData = 1

        def ActiveFiscalYear = new CoreParamsHelperTagLib().getActiveFiscalYear()
        def FiscalYearInfo = new CoreParamsHelperTagLib().getActiveFiscalYearInformation(ActiveFiscalYear)
        String fiscalYear = FiscalYearInfo[0][4]
        def df = new SimpleDateFormat("dd-MM-yyyy")
        def invoiceDate = df.parse(params.transDate)
        def isValidDate  = new CoreParamsHelperTagLib().isDateClosedFiscalYear(invoiceDate)
        if (session.invoiceIncomeArr && isValidDate) {
            session.lastSelectedCustomerId = session.invoiceIncomeArr.customerId
            def insertedId =  incomeInvoiceService.saveIncomeInvoice(session, params,fiscalYear)

            def incomeHeadData = incomeInvoiceService.incomeInvoiceDataArray(insertedId)
            def incomeDetailsData = incomeInvoiceService.incomeInvoiceDetailsDataArray(insertedId)
            def totalVatAmountByCategory = incomeInvoiceService.getVatAmountByCategory(insertedId)

            flash.message = message(code: 'bv.saveInvoiceIncomeDetails.label', args: [message(code: 'bv.saveInvoiceIncomeDetails.label', default: 'Income invoice saved successfully')])
            render(template: "afterSavingPrint", model: [incomeHeadData: incomeHeadData, invoiceIncomeDetailsInstance: incomeDetailsData, insertedId: insertedId,
                                                         customerID:params.customerId,totalVatAmountByCategory:totalVatAmountByCategory])

        } else {
            render ""
        }

    }

    def saveInvoiceAndCreateSettlement() {

        session.dummyData = 1

        def ActiveFiscalYear = new CoreParamsHelperTagLib().getActiveFiscalYear()
        def FiscalYearInfo = new CoreParamsHelperTagLib().getActiveFiscalYearInformation(ActiveFiscalYear)
        String fiscalYear = FiscalYearInfo[0][4]

        if (session.invoiceIncomeArr) {

            def insertedId =  incomeInvoiceService.saveIncomeInvoice(session, params,fiscalYear)

            def incomeHeadData = incomeInvoiceService.incomeInvoiceDataArray(insertedId)
            def incomeDetailsData = incomeInvoiceService.incomeInvoiceDetailsDataArray(insertedId)
            def totalVatAmountByCategory = incomeInvoiceService.getVatAmountByCategory(insertedId);
            Map<String, Object> result = [result: true];
            render result as JSON;
        } else {
            Map<String, Object> result = [result: true];
            render result as JSON;

        }

    }

    /**
     * Update for change the invoice of income
     * **/
    def updateInvoice () {
        def df = new SimpleDateFormat("dd-MM-yyyy")
        def invoiceDate = df.parse(params.transDate)
        def isValidDate  = new CoreParamsHelperTagLib().isDateClosedFiscalYear(invoiceDate)
        if(isValidDate){
            incomeInvoiceService.updateIncomeInvoice(session.invoiceIncomeArr,params)

            session.editData = 0
            session.dummyData = 0
            session.invoiceIncomeArr = []

            def incomeHeadData = incomeInvoiceService.incomeInvoiceDataArray(params.editId)
            def incomeDetailsData = incomeInvoiceService.incomeInvoiceDetailsDataArray(params.editId)
            def totalVatAmountByCategory = incomeInvoiceService.getVatAmountByCategory(params.editId);


            flash.message = message(code: 'bv.updateInvoiceIncomeDetails.label', args: [message(code: 'bv.updateInvoiceIncomeDetails.label', default: 'Income invoice upadated successfully')])


            render(template: "afterSavingPrint", model: [incomeHeadData: incomeHeadData, invoiceIncomeDetailsInstance: incomeDetailsData,
                                                         insertedId: params.editId,
                                                         customerID:params.customerId,totalVatAmountByCategory:totalVatAmountByCategory])
        }else{
            render ""
        }

    }

    def getContextPath(){
        def protocol = request.isSecure() ? "https://" : "http://"
        def host = request.getServerName()
        def port = request.getServerPort()
        def context = request.getServletContext().getContextPath()
        def fullContextPath = ""

        fullContextPath = protocol + host + ":" + port + context

        return fullContextPath;
    }

    def getPaymentTermsDays(Integer id) {
        def paymentTermsDays = new BudgetViewDatabaseService().executeQuery("SELECT daysBeforeDue FROM PaymentTerms  where id=" + id)
        render(template: "dateChange", model: [invoiceIncomeInstance: paymentTermsDays[0]])
    }

    def changeDueDate() {
        def paymentTermsDays = new BudgetViewDatabaseService().executeQuery("SELECT daysBeforeDue FROM PaymentTerms  where id=" + params.termsId)

        render paymentTermsDays[0][0];
    }

    def selectDebtorTermsDropDown() {
        String strQuery = "SELECT dc.payment_term_id,pt.daysBeforeDue FROM debtor_customer as dc INNER JOIN payment_terms as pt ON pt.id=dc.payment_term_id where dc.customer_id=" + params.customerId + " AND dc.debtor_id ="+params.debtorId
        def paymentTermsId = new BudgetViewDatabaseService().executeQuery(strQuery)

//        Map<String, Object> result = ['termsId': paymentTermsId[0][0],'noOfDays':paymentTermsId[0][1]];
        def dataResult = paymentTermsId[0][0] + "::" + paymentTermsId[0][1];
        render dataResult;
    }

    def selectVATRelatedInformation() {
        def VatCategoryArr = new BudgetViewDatabaseService().executeQuery("Select rate FROM VatCategory where id=" + params.id)
        render(template: "lineformVat", model: [InvoiceIncomeInstanceVatRate: VatCategoryArr[0][0]])
    }

    def selectVATFromCustomerId() {
        String strQuery = "SELECT vc.rate from vendor_master as vm INNER Join vat_category as vc ON vc.id = vm.vat WHERE vm.id = "+params.customerId
        def vatRateArr = new BudgetViewDatabaseService().executeQuery(strQuery)

        render vatRateArr[0][0]
    }

    def upateInvoiceListView(){
        render(template:"upateInvoiceListView", model:[params:params])
    }

    def generateDebtorNameList(){
        def dropdwon = new CoreParamsHelperTagLib().getDebtorNameListForIncInv(params.customerId,params.debtorId)

        Map<String, Object> result = [result: dropdwon]
        render result as JSON
    }

    def showDebtorRemainingInsuredAmount(){

        String strQuery = """SELECT SUM(dueAmount) as dueAmount,creditLimit as creditLimit FROM(
                                SELECT SUM(tm.amount) as dueAmount,cm.credit_limit as creditLimit
                                FROM invoice_income AS a
                                INNER JOIN trans_master tm ON tm.recenciliation_code = CONCAT(a.id,'#1') AND
                                tm.account_code=(Select debitor_gl_code from debit_credit_gl_setup)
                                LEFT JOIN customer_master AS cm ON a.debtor_id=cm.id
                                WHERE a.debtor_id = ${params.debtorId} GROUP BY tm.recenciliation_code) as innerTable"""
        def insuadAmountArr = new BudgetViewDatabaseService().executeQuery(strQuery)

        int dueAmount = new DashboardDetailsTagLib().getRoundedValue(insuadAmountArr[0][0])
        int creditLimit = new DashboardDetailsTagLib().getRoundedValue(insuadAmountArr[0][1])

        int remainInsuadAmount = creditLimit - dueAmount;

        String htmlText = "<p class=\"labelCode\" style=\"margin-top:18px;font-size:16px;\">€ ${creditLimit} - € ${dueAmount} = € "+ remainInsuadAmount + "</p>"
        if(remainInsuadAmount < 0){
            htmlText = "<p class=\"labelCode\" style=\"margin-top:18px;font-size:16px;color:#FF0000\">€ ${creditLimit} - € ${dueAmount} = € "+ remainInsuadAmount + "</p>"
        }

        render htmlText;
    }

    def getCustomerStatus(){
        def customerId = params.long('customerId')
        def customer = VendorMaster.executeQuery("from VendorMaster as VM where VM.id=:customerId",[customerId:customerId])
        render customer.status
    }

    def getDebtorStatus(){
        def debtorId = params.int('debtorId')
        def debtor = CustomerMaster.executeQuery("from CustomerMaster as CM where CM.id=:debtorId",[debtorId:debtorId])
        render debtor.status
    }

    def showCustomerRemainingInsuredAmount(){

        String strQuery = """SELECT SUM(dueAmount) as dueAmount,creditLimit as creditLimit FROM(
                                SELECT SUM(tm.amount) as dueAmount,vm.credit_limit as creditLimit
                                FROM invoice_income AS a
                                INNER JOIN trans_master tm ON tm.recenciliation_code = CONCAT(a.id,'#1') AND
                                tm.account_code=(Select debitor_gl_code from debit_credit_gl_setup)
                                LEFT JOIN vendor_master AS vm ON a.customer_id=vm.id
                                WHERE a.customer_id = ${params.customerId} GROUP BY tm.recenciliation_code) as innerTable"""
        def insuadAmountArr = new BudgetViewDatabaseService().executeQuery(strQuery)

        int dueAmount = new DashboardDetailsTagLib().getRoundedValue(insuadAmountArr[0][0])
        int creditLimit = new DashboardDetailsTagLib().getRoundedValue(insuadAmountArr[0][1])

        int remainInsuadAmount = creditLimit - dueAmount;

        String htmlText = "<p class=\"labelCode\" style=\"margin-top:18px;font-size:16px;\">€ ${creditLimit} - € ${dueAmount} = € "+ remainInsuadAmount + "</p>"
        if(remainInsuadAmount < 0){
            htmlText = "<p class=\"labelCode\" style=\"margin-top:18px;font-size:16px;color:#FF0000\">€ ${creditLimit} - € ${dueAmount} = € "+ remainInsuadAmount + "</p>"
        }

        render htmlText
    }

    def saveExtraDays () {
        Integer numberOfDays = Integer.parseInt(params.numberOfDays)
        Integer invoiceIncomeId = Integer.parseInt(params.invoiceIncomeId)
        def updateExtraDays = budgetViewDatabaseService.executeUpdate("UPDATE invoice_income SET extra_days = ${numberOfDays} WHERE id = ${invoiceIncomeId}")
        def getDebtorID = budgetViewDatabaseService.executeQuery("SELECT ii.debtor_id from invoice_income AS ii WHERE ii.id = ${invoiceIncomeId}")
        def result = getDebtorID as JSON
        render result

    }
}
