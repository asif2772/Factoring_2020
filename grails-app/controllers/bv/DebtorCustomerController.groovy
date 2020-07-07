package bv

import factoring.BudgetViewDatabaseService
import factoring.DebtorCustomerService
import factoring.ExtraSettingUtil
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import org.springframework.beans.factory.annotation.Autowired

@Secured(["hasAnyRole('ROLE_ADMIN', 'ROLE_ACCOUNTANT', 'ROLE_USER')"])
class DebtorCustomerController {

    @Autowired
    DebtorCustomerService debtorCustomerService;

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        if (params.strSearch){
            def strSearch =  params.strSearch
            params.max = Math.min(max ?: 10, 100)

            def debtorCustomerInstanceTemp = debtorCustomerService.getDebtorCustomerDataAsMap(params.id)

            [debtorCustomerInstance: debtorCustomerInstanceTemp, strSearch: strSearch]
        }
        else{

            params.max = Math.min(max ?: 10, 100)

            def debtorCustomerInstanceTemp = debtorCustomerService.getDebtorCustomerDataAsMap(params.id)

            [debtorCustomerInstance: debtorCustomerInstanceTemp]
        }

    }

    def saveDebtorCustomer() {
        def parameters = params
        def insertedId = 0;

        if(params.id){
            debtorCustomerService.updateAcceptDebtors(params)
            insertedId = params.id;
            flash.message = "Data updated successfully!!!"
        }else{
            if(debtorCustomerService.checkDebtorCustomerCombination(params.debtorId,params.customerId)){
                flash.message = "Selected Debtor and Customer combination exist!!!Please select another combination."
            }else {
                insertedId = debtorCustomerService.insertAcceptDebtors(params)
                flash.message = "Data inserted successfully!!!"
            }
        }

        redirect(action: "index")
    }

    def saveAndAddInvoice() {
        def insertedId = 0;
        if(debtorCustomerService.checkDebtorCustomerCombination(params.debtorId,params.customerId)){
            flash.message = "Failed!!! Selected Debtor and Customer combination exist.Please select another combination."

            redirect(action: "index",model:[insertFailed:true])
        }else {
            insertedId = debtorCustomerService.insertAcceptDebtors(params)
            flash.message = "Data inserted successfully!!!"

            redirect(controller: "invoiceIncome", action: "list")
        }
    }

    def closeAndAddNew() {
        redirect(action: "index")
    }

    def showDataList() {
        String gridOutput
        int start = 0

        LinkedHashMap gridResult
        gridResult = debtorCustomerService.listOfDebtorCustomerGrid()

        List dataGridList = new ExtraSettingUtil().wrapDebtorCustomerGrid(gridResult.debtorCustomerItemList,start,getContextPath())
        LinkedHashMap result = [draw: 1, recordsTotal: gridResult.count, recordsFiltered:  gridResult.count,data:dataGridList.cell]

        gridOutput = result as JSON
        render gridOutput
    }

    def checkDebtorCustomerCombination() {

        boolean bComExist = false
        if(params.debtorId != "" && params.customerId != ""){
            bComExist = debtorCustomerService.checkDebtorCustomerCombination(params.debtorId,params.customerId)
        }

        def strMsg = ""
        if(bComExist)
        {
            strMsg = message(code: 'bv.incInvSelectAnotherCombination.label', default: 'This Combination Already Exist for This Customer.Please Select Another Combination.')
        }

        render strMsg
    }

    def copyDefaultValueForCustomer(){

        String strQuery = "SELECT default_fee,outpayment,admin_cost,acceptence_fee FROM vendor_factoring  WHERE customer_id="+params.customerId
        def customerDataArr = new BudgetViewDatabaseService().executeQuery(strQuery)
        def customerData = customerDataArr[0][0] + "::" + customerDataArr[0][1] +"::"+customerDataArr[0][2]+"::"+customerDataArr[0][3]

        render customerData
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
}
