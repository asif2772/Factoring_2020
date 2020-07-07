package bv

import bv.auth.User
import factoring.ApplicationUtil
import factoring.BudgetViewDatabaseService
import factoring.CoreParamsHelperTagLib
import factoring.DebtorCustomerService
import factoring.ExtraSettingService
import factoring.ExtraSettingUtil
import factoring.GridEntity

import factoring.InvoiceUtil
import factoring.VendorBankAccount
import factoring.VendorGeneralAddress
import factoring.VendorMaster
import factoring.VendorMasterService
import factoring.VendorPostalAddress
import grails.converters.JSON
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import user.UserController

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class VendorMasterController {

    @Autowired
    BudgetViewDatabaseService budgetViewDatabaseService
    DebtorCustomerService debtorCustomerService
    ExtraSettingService extraSettingService
    SpringSecurityService springSecurityService
    def mailService
    VendorMasterService vendorMasterService
    InvoiceUtil invoiceUtil


    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
    static transactional = false

//    private Logger log = Logger.getLogger(getClass());


    def index() {
        params.max = params.max ?: 10
        params.offset = params.offset ?: 0
        params.sort = "id"
        params.order = "desc"
        session.resultVendorInvInfo = []

        User user = springSecurityService.currentUser
        def userCustomerId = user.customerId
        if (user && userCustomerId) {
            redirect(controller: 'vendorMaster', action: "list", params:[id:user.getCustomerId().toString()])
            return false
        }
        else
            redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = params.max ?: 10
        params.offset = (params.offset) ? Integer.parseInt(params.offset) : 0
        params.limit = (params.limit) ? Integer.parseInt(params.limit) : 10
        params.sort = "id"
        params.order = "desc"

        def vendorPrefix = new CoreParamsHelperTagLib().showGeneratedVendorCode()

        String select = """a.id AS id,a.version AS version, a.cham_of_commerce AS chamOfCommerce, a.comments AS comments,a.default_gl_account AS defaultGlAccount,
                            a.email As email,a.gender As gender,a.first_name as firstName, a.last_name As lastName,a.middle_name As middleName,
                            a.payment_term_id As paymentTermId, a.vat As vat,a.vendor_code AS vendorCode,a.vendor_name As vendorName,
                            a.vendor_type As vendorType,a.by_shop As byShop,a.credit_status AS creditStatus,a.credit_limit AS creditLimit,
                            a.vendor_code AS vendorCode,a.vat_number As vatNumber,a.status As status, a.optional_email AS optionalEmail"""
        String selectIndex = """id,version,chamOfCommerce,comments,defaultGlAccount,email,gender,firstName,lastName,middleName,paymentTermId,
                                vat,vendorCode,vendorName,vendorType,byShop,creditStatus,creditLimit,vendorCode,vatNumber,status,optionalEmail"""
        String from = "vendor_master AS a "

        String orderBy = ""
        if (params.sort && params.order) {
            orderBy = "a.${params.sort} ${params.order}"
        } else {
            orderBy = "a.id ASC"
        }


        String whereInstance = ""
        if (params.id) {
            whereInstance = "a.id=${params.id}"
        } else {
            whereInstance = "a.id=0"
        }
        LinkedHashMap gridResultInstance = budgetViewDatabaseService.select(select, from, whereInstance, orderBy, '', 'false', selectIndex)

        def customerId = params.id

        User user = springSecurityService.currentUser
        def userCustomerId = user.customerId

        if(!userCustomerId)
            userCustomerId = -1

        if (customerId) {

            if(userCustomerId > 0){
                if(!customerId.equals(userCustomerId.toString())){
                    print('access denied...')
                    render(view: "/errors/error403")
                    return false
                }
            }

            String selectga = """a.id AS id,a.version AS version, a.address_line1 AS addressLine1,a.address_line2 AS addressLine2,a.city AS city,a.contact_person_name AS contactPersonName,a.country_id AS countryId, a.postal_code As postalCode, a.state As state,a.status As status,a.vendor_id As vendorrId,a.phone_no,a.website_address"""
            String selectIndexga = """id,version,addressLine1,addressLine2,city,contactPersonName,countryId,postalCode,state,status,vendorrId"""
            String fromga = "vendor_general_address AS a "
            String wherega = "vendor_id='${customerId}'"
            String orderByga = "a.id ASC"
            LinkedHashMap gridResultga = budgetViewDatabaseService.select(selectga, fromga, wherega, orderByga, '', 'false', selectIndexga)

            /* Selection Bank Account List*/
            String selectsa = """a.id AS id,a.version AS version,a.bank_account_name AS bankAccountName,a.bank_account_no AS bankAccountNo, a.iban_prefix AS ibanPrefix,a.status As status,a.vendor_id AS vendorId"""
            String selectIndexsa = """id,version,bankAccountName,bankAccountNo,ibanPrefix,status,vendorId"""
            String fromsa = "vendor_bank_account AS a "
            String wheresa = "vendor_id='${customerId}'"
            String orderBysa = "a.id ASC"
            LinkedHashMap gridResultsa = budgetViewDatabaseService.select(selectsa, fromsa, wheresa, orderBysa, '', 'false', selectIndexsa)

            if (params.bankid) {
                if (params.delbank) {
                    VendorBankAccount.executeUpdate("Delete FROM VendorBankAccount WHERE id='" + params.bankid + "'")
                }
            }
            String selectbel = """a.id AS id,a.version AS version, a.bank_account_name AS bankAccountName,a.bank_account_no AS bankAccountNo, a.iban_prefix AS ibanPrefix"""
            String selectIndexbel = """id,version,bankAccountName,bankAccountNo,ibanPrefix"""
            String frombel = "vendor_bank_account AS a "
            String wherebel = "id='" + params.bankid + "'"
            String orderBybel = "a.id ASC"
            LinkedHashMap gridResultbel = budgetViewDatabaseService.select(selectbel, frombel, wherebel, orderBybel, '', 'false', selectIndexbel)

            //Vendor Factoring
            def vendorFactoringInstance = debtorCustomerService.getCustomerFactoringDataAsMap(customerId)
            def revenuePerCustomer = debtorCustomerService.getCustomerRevenueDataAsMap(customerId)

            //statistics
            def outstandingDetails = debtorCustomerService.getOutstandingInvoiceDetails(params.id)
            def paidDetails = debtorCustomerService.getPaidInvoiceDetails(params.id)
            def (int totalOutstandingInv, int totalPaidInv, int totalInvoices, BigDecimal totalInvAmount, TreeMap invAmountPerYear, Object[] invAmountValArr, Object[] revenuePerCustomerKeyArr, Object[] revenuePerCustomerValArr, Serializable avgOutstandingDays, Serializable avgDaysLastSixMonths, BigDecimal totalOutstandingInvAmount, BigDecimal totalPaidInvAmount) = getCustomerStatistics(outstandingDetails, paidDetails, revenuePerCustomer)


            [vendorMasterInstance: gridResultInstance['dataGridList'][0], vendorBankAccountInstance: gridResultsa['dataGridList'],
             vendorEditBankAccountInstance: gridResultbel['dataGridList'][0], vendorPrefix: vendorPrefix,
             vendorFactoringInstance: vendorFactoringInstance,vendorGeneralAddressInstance: gridResultga['dataGridList'][0],revenuePerCustomer:revenuePerCustomer,
             totalOutstandingInv: totalOutstandingInv, totalPaidInv: totalPaidInv , totalInvoices : totalInvoices, totalInvAmount: totalInvAmount, invAmountPerYear:invAmountPerYear,
             invAmountValArr : invAmountValArr, revenuePerCustomerKeyArr: revenuePerCustomerKeyArr, revenuePerCustomerValArr: revenuePerCustomerValArr,
             avgOutstandingDays : avgOutstandingDays, avgDaysLastSixMonths:avgDaysLastSixMonths, totalOutstandingInvAmount : totalOutstandingInvAmount,  totalPaidInvAmount : totalPaidInvAmount,
             userCustomerId: userCustomerId]

        } else {
            [vendorMasterInstance: gridResultInstance['dataGridList'][0], vendorPrefix: vendorPrefix, userCustomerId: userCustomerId]
        }
    }

    private List getCustomerStatistics(List outstandingDetails, List paidDetails, TreeMap revenuePerCustomer) {

        def totalOutstandingInv = outstandingDetails.size()
        def totalPaidInv = paidDetails.size()

        def totalInvoices = totalOutstandingInv + totalPaidInv

        def totalInvAmount = 0.0
        def totalOutstandingInvAmount = 0.0
        def totalPaidInvAmount = 0.0

        def outstandingDays = 0
        def daysLastSixMonths = 0
        def countDaysLastSixMonths = 0
        def avgDaysLastSixMonths = 0
        def avgOutstandingDays = 0

        TreeMap invAmountPerYear = [:]

        for (outstandingByYearData in outstandingDetails) {

            totalOutstandingInvAmount += outstandingByYearData[4]

            if(revenuePerCustomer.containsKey(outstandingByYearData[0])){
                if (invAmountPerYear.containsKey(outstandingByYearData[0])) {
                    invAmountPerYear.put(outstandingByYearData[0], invAmountPerYear.get(outstandingByYearData[0]) + outstandingByYearData[4])
                } else
                    invAmountPerYear.put(outstandingByYearData[0], outstandingByYearData[4])
            }
        }

        for (paidByYearData in paidDetails) {

            totalPaidInvAmount += paidByYearData[4]
            outstandingDays += paidByYearData[2]

            if (paidByYearData[3] <= 0) {
                daysLastSixMonths += paidByYearData[2]
                countDaysLastSixMonths++
            }

            if (invAmountPerYear.containsKey(paidByYearData[0]))
                invAmountPerYear.put(paidByYearData[0], invAmountPerYear.get(paidByYearData[0]) + paidByYearData[4])
            else
                invAmountPerYear.put(paidByYearData[0], paidByYearData[4])
        }

        totalInvAmount = totalOutstandingInvAmount + totalPaidInvAmount

        if (totalPaidInv > 0)
            avgOutstandingDays = new DecimalFormat("#.##").format(outstandingDays / totalPaidInv)

        if (countDaysLastSixMonths > 0)
            avgDaysLastSixMonths = new DecimalFormat("#.##").format(daysLastSixMonths / countDaysLastSixMonths)

        //print("total invAmount: "+ invAmountPerYear)

        def invAmountValArr
        def revenuePerCustomerKeyArr
        def revenuePerCustomerValArr

        if (invAmountPerYear.size() == revenuePerCustomer.size()) {
            //print("array Size same")
            invAmountValArr = invAmountPerYear.values().toArray()
            revenuePerCustomerKeyArr = revenuePerCustomer.keySet().toArray()
            revenuePerCustomerValArr = revenuePerCustomer.values().toArray()
        } else
            print("revenue and invoiceAmount arrays sizes are not same")

        [totalOutstandingInv, totalPaidInv, totalInvoices, totalInvAmount, invAmountPerYear, invAmountValArr, revenuePerCustomerKeyArr, revenuePerCustomerValArr, avgOutstandingDays, avgDaysLastSixMonths, totalOutstandingInvAmount, totalPaidInvAmount]
    }

    def loadCustomerGeneralAddress(){
        def customerId = request.getParameter("customerId")
        def venId = request.getParameter("venId")
        def venPrefix = request.getParameter("venPrefix")
        def venCode = request.getParameter("venCode")
        def venName = request.getParameter("venName")

        String selectga = """a.id AS id,a.version AS version, a.address_line1 AS addressLine1,a.address_line2 AS addressLine2,a.city AS city,a.contact_person_name AS contactPersonName,a.country_id AS countryId, a.postal_code As postalCode, a.state As state,a.status As status,a.vendor_id As vendorrId,a.phone_no,a.website_address,a.second_email"""
        String selectIndexga = """id,version,addressLine1,addressLine2,city,contactPersonName,countryId,postalCode,state,status,vendorrId"""
        String fromga = "vendor_general_address AS a "
        String wherega = "vendor_id='" + customerId + "'"
        String orderByga = "a.id ASC"
        LinkedHashMap gridResultga = budgetViewDatabaseService.select(selectga, fromga, wherega, orderByga, '', 'false', selectIndexga)
        render(template: "generalAddressForm", model: [vendorGeneralAddressInstance: gridResultga['dataGridList'][0],venPrefix:venPrefix,venCode:venCode,venName:venName,venId:venId])
    }

    def loadCustomerPostalAddress(){
        def customerId = request.getParameter("customerId")
        def venId = request.getParameter("venId")
        def venPrefix = request.getParameter("venPrefix")
        def venCode = request.getParameter("venCode")
        def venName = request.getParameter("venName")

        String selectpa = """a.id AS id,a.version AS version,a.postal_address_line1 AS postalAddressLine1,a.postal_address_line2 AS postalAddressLine2, a.postal_city AS postalCity,a.postal_contact_person_name AS postalContactPersonName,a.postal_postcode AS postalPostcode,a.postal_state As postalState,a.postal_country_id AS postalCountryId,a.status As status, a.vendor_id AS vendorId, a.postal_email"""
        String selectIndexpa = """id,version,postalAddressLine1,postalAddressLine2,postalCity,postalContactPersonName,postalPostcode,postalState,postalCountryId,status,vendorId"""
        String frompa = "vendor_postal_address AS a "
        String wherepa = "vendor_id='" + customerId + "'"
        String orderBypa = "a.id ASC"
        LinkedHashMap gridResultpa = budgetViewDatabaseService.select(selectpa, frompa, wherepa, orderBypa, '', 'false', selectIndexpa)
        render(template: "postalAddressForm", model: [vendorPostalAddressInstance: gridResultpa['dataGridList'][0],venPrefix:venPrefix,venCode:venCode,venName:venName,venId:venId])
    }

    def loadCustomerFactoring(){
        def customerId = request.getParameter("customerId")
        def venId = request.getParameter("venId")
        def venPrefix = request.getParameter("venPrefix")
        def venCode = request.getParameter("venCode")
        def venName = request.getParameter("venName")

        String select = """a.id AS id,a.version AS version, a.cham_of_commerce AS chamOfCommerce, a.comments AS comments,a.default_gl_account AS defaultGlAccount,
                            a.email As email,a.gender As gender,a.first_name as firstName, a.last_name As lastName,a.middle_name As middleName,
                            a.payment_term_id As paymentTermId, a.vat As vat,a.vendor_code AS vendorCode,a.vendor_name As vendorName,
                            a.vendor_type As vendorType,a.by_shop As byShop,a.credit_status AS creditStatus,a.credit_limit AS creditLimit,
                            a.vendor_code AS vendorCode,a.vat_number As vatNumber"""
        String selectIndex = """id,version,chamOfCommerce,comments,defaultGlAccount,email,gender,firstName,lastName,middleName,paymentTermId,
                                vat,vendorCode,vendorName,vendorType,byShop,creditStatus,creditLimit,vendorCode,vatNumber"""
        String from = "vendor_master AS a "
        String orderBy = "a.id ASC"
        String whereInstance = "a.id=${customerId}"
        LinkedHashMap gridResultInstance = budgetViewDatabaseService.select(select, from, whereInstance, orderBy, '', 'false', selectIndex)

        def vendorFactoringInstance = debtorCustomerService.getCustomerFactoringDataAsMap(customerId)

        render(template: "factoringForm", model: [vendorMasterInstance: gridResultInstance['dataGridList'][0],vendorFactoringInstance:vendorFactoringInstance,venPrefix:venPrefix,venCode:venCode,venName:venName,venId:venId])
    }

    def loadCustomerFinancialTransaction(){
        def customerId = request.getParameter("customerId")
        def venId = request.getParameter("venId")
        def venPrefix = request.getParameter("venPrefix")
        def venCode = request.getParameter("venCode")
        def venName = request.getParameter("venName")
        String strQuery =   """SELECT CONCAT(IF(ie.is_book_receive=1, spr.prefix, spe.prefix), '-', ie.invoice_no) AS invoiceNumber,
                                       ie.payment_ref,
                                       ie.trans_date,
                                       ie.booking_period,
                                       ROUND(ie.fact_inv_amount-(ie.total_gl_amount + ie.total_vat), 2) AS invAmount,
                                       ROUND(ROUND(ie.fact_inv_amount-(ie.total_gl_amount + ie.total_vat), 2) - (SUM(tm.amount)*(-1)), 2) AS paidAmount,
                                       ROUND(SUM(tm.amount)*(-1), 2) AS remainAmount,
                                       ie.id,
                                       ie.booking_year
                                FROM system_prefix AS spe,
                                     system_prefix AS spr,
                                     invoice_expense AS ie
                                INNER JOIN trans_master AS tm ON (tm.recenciliation_code = CONCAT(ie.id, '#2')
                                                                  OR tm.recenciliation_code = CONCAT(ie.id, '#4')) AND tm.vendor_id ='${customerId}'
                                AND tm.account_code=
                                  (SELECT creditor_gl_code
                                   FROM debit_credit_gl_setup)
                                WHERE spe.id=7
                                  AND spr.id=12
                                GROUP BY tm.recenciliation_code
                                ORDER BY ie.trans_date"""

        def resultVendorInvInfo = budgetViewDatabaseService.executeQuery(strQuery)

        if (session.ongoingID != customerId) {
            session.ongoingID = customerId
            session.dummyData = 0
            session.resultVendorInvInfo = []
        }

        resultVendorInvInfo.eachWithIndex { Phn, key ->
            Map map = ["invoiceId": 0, "paymentReference": '', "transactionDate": 0, "bookingPeriod": 0, "paidAmount": 0, "dueAmount": 0,"bookingYear" : 0]
            map.invoiceId = Phn[7]
            map.paymentReference = Phn[1].toString()
            map.transactionDate = Phn[2].toString()
            map.bookingPeriod = Phn[3]
            map.paidAmount = Phn[5].toString()
            map.dueAmount = Phn[6]
            map.bookingYear = Phn[8]
            session.resultVendorInvInfo << map
        }
        //For bank transaction details

        render(template: "financialTransactionForm", model: [invoiceVendorDetailsInstance: resultVendorInvInfo,venPrefix:venPrefix,venCode:venCode,venName:venName,venId:venId])
    }

    def loadBankDataInfo(){
        def customerId = params.customerId
        def strQuery = """SELECT bsidf.bank_payment_id,
                                   bsidf.trans_bank_account_no,
                                   CONCAT(IF(ie.is_book_receive=1, spr.prefix, spe.prefix), '-', ie.invoice_no) AS invoiceNumber,
                                   IF(tm.trans_type=8, IF(ISNULL(tmd.write_off_description), 'Write off by user', tmd.write_off_description), bsidf.description),
                                   bsidf.trans_date_time,
                                   IF(tm.trans_type=7, bsidf.reconciliated_amount, ROUND(tm.amount, 2)) AS reconAmount
                            FROM system_prefix AS spe,
                                 system_prefix AS spr,
                                 invoice_expense AS ie
                            INNER JOIN trans_master AS tm ON (tm.recenciliation_code = CONCAT(ie.id, '#', 2)
                                                              OR tm.recenciliation_code = CONCAT(ie.id, '#', 4)) AND tm.vendor_id ='${customerId}'
                            INNER JOIN bank_statement_import_details_final AS bsidf ON bsidf.bank_payment_id = tm.invoice_no
                            LEFT JOIN trans_master_write_off_details AS tmd ON tm.invoice_no=tmd.invoice_no
                            WHERE (tm.trans_type = 7 || tm.trans_type = 8)
                              AND spe.id=7
                              AND spr.id=12
                            GROUP BY bsidf.bank_payment_id
                            ORDER BY ie.trans_date"""
        def bankPaymentInfoDetails = budgetViewDatabaseService.executeQuery(strQuery)
        [bankPaymentInfoDetailsInstance: bankPaymentInfoDetails,customerId:customerId]
    }

    def loadCustomerOutstandingInvoices(){
        def customerId = request.getParameter("customerId")
        def venId = request.getParameter("venId")
        def venPrefix = request.getParameter("venPrefix")
        def venCode = request.getParameter("venCode")
        def venName = request.getParameter("venName")
        def creditLimit = budgetViewDatabaseService.executeCustomQuery("SELECT vm.credit_limit AS creditLimit FROM vendor_master AS vm WHERE vm.id='${customerId}'")
        if(creditLimit)
            creditLimit = creditLimit[0][0]

        def customerOutstandingInvoices = budgetViewDatabaseService.executeCustomQuery("""SELECT *
                                                                                                    FROM
                                                                                                      (SELECT ic.payment_ref AS paymentRefference,
                                                                                                              cm.customer_name AS debtorName,
                                                                                                              DATE_FORMAT(ic.trans_date, '%d-%m-%Y') AS invoiceDate,
                                                                                                              TO_DAYS(ic.due_date)-TO_DAYS(NOW()) AS daysToPay,
                                                                                                              IF(ISNULL(tblPaidInvoices.lastPaymentDate), TO_DAYS(NOW())-TO_DAYS(ic.trans_date), TO_DAYS(tblPaidInvoices.lastPaymentDate)-TO_DAYS(ic.trans_date)) AS paymentRealized,
                                                                                                              (TO_DAYS(NOW())-TO_DAYS(ic.trans_date))-180 AS isSixMonths,
                                                                                                              pt.terms AS paymentTerms,
                                                                                                              ROUND(ic.total_gl_amount+ic.total_vat, 2) AS invoiceAmount,
                                                                                                              IF(ISNULL(tblPaidInvoices.invoicePaidAmount), ROUND(ic.total_gl_amount+ic.total_vat, 2), ROUND((ic.total_gl_amount+ic.total_vat)-tblPaidInvoices.invoicePaidAmount, 2)) AS dueAmount,
                                                                                                              IF(ISNULL(tblPaidInvoices.invoicePaidAmount), 0, ROUND(tblPaidInvoices.invoicePaidAmount, 2)) AS paidAmount,
                                                                                                              cm.credit_limit AS creditLimit,
                                                                                                              ic.customer_id AS customerId,
                                                                                                              ic.debtor_id AS debtorId
                                                                                                       FROM invoice_income AS ic
                                                                                                       INNER JOIN customer_master AS cm ON ic.debtor_id=cm.id
                                                                                                       INNER JOIN payment_terms AS pt ON pt.id=ic.terms_id
                                                                                                       LEFT JOIN
                                                                                                         (SELECT ic.id AS invoiceId,
                                                                                                                 SUM(tm.amount*-1) AS invoicePaidAmount,
                                                                                                                 MAX(tm.trans_date) AS lastPaymentDate
                                                                                                          FROM invoice_income AS ic
                                                                                                          INNER JOIN trans_master AS tm ON tm.recenciliation_code = CONCAT(ic.id, '#1')
                                                                                                          AND ic.customer_id='${customerId}'
                                                                                                          AND tm.account_code='1300'
                                                                                                          AND tm.trans_type <> 1
                                                                                                          GROUP BY ic.id) AS tblPaidInvoices ON tblPaidInvoices.invoiceId=ic.id) AS tblAllInfo
                                                                                                    WHERE tblAllInfo.customerId='${customerId}' 
                                                                                                      AND tblAllInfo.invoiceamount <> tblAllInfo.paidamount
                                                                                                    ORDER BY STR_TO_DATE(invoiceDate, '%d-%m-%Y') ASC""")

        render(template: "outstandingInvoices", model: [customerOutstandingInvoices:customerOutstandingInvoices,venPrefix:venPrefix,venCode:venCode,venName:venName,venId:venId,creditLimit:creditLimit])

    }

    def showUndoWriteOff(){
        def customerId = params.id
        def customerWriteOffs = new BudgetViewDatabaseService().executeCustomQuery("SELECT tm.invoice_no AS invoiceNo,tm.recenciliation_code AS reconciliation,DATE_FORMAT(tm.trans_date,'%d-%m-%Y') as transactionDate,tm.amount AS amount FROM trans_master AS tm WHERE tm.account_code='1600' AND tm.trans_type=8 AND tm.vendor_id="+customerId)
        [customerWriteOffs:customerWriteOffs,customerId:customerId]
    }

    def undoWriteOff(){

        def invoiceExpense = new BudgetViewDatabaseService().executeCustomQuery("SELECT trans_date AS transDate FROM trans_master WHERE invoice_no= ${params.invoiceNo} AND trans_type=8 AND account_code = 1600")

        if(invoiceExpense){
            def transDate = invoiceExpense[0][0]
            def isValidDate  = new CoreParamsHelperTagLib().isDateClosedFiscalYearUndoWriteOff(transDate)
            if(isValidDate){
                def tableName = "trans_master"
                def deletedWhereSrting = "invoice_no="+"'${params.invoiceNo}' and trans_type = 8"
                new BudgetViewDatabaseService().delete(tableName, deletedWhereSrting)
            }
            else {
                session.isFiscalYearClosed = true
            }
        }
        redirect(action: "showUndoWriteOff", id: params.customerId)
    }

    /*def exportExcelofOutstandingInvoice() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm")
        Date date = new Date()
        String strDateCreated = dateFormat.format(date)
        String fileName = "Customer_Outstanding_" + strDateCreated
        int nRowNo = 5
        def ActiveFiscalYear = new CoreParamsHelperTagLib().getActiveFiscalYear()
        def fiscalYearInfo = new CoreParamsHelperTagLib().getActiveFiscalYearInformation(ActiveFiscalYear)

        //Getting budget customer data.

        response.setContentType('application/vnd.ms-excel')
        response.setHeader('Content-Disposition', 'Attachment;Filename=' + fileName + '.xls')
        WritableWorkbook workbook = Workbook.createWorkbook(response.outputStream)

        WritableSheet sheet1 = workbook.createSheet("Customer Outstanding Invoices", 0)
//      set column width
        sheet1.setColumnView(0, 20)
        sheet1.setColumnView(1, 30)
        sheet1.setColumnView(2, 20)
        sheet1.setColumnView(3, 20)
        sheet1.setColumnView(4, 15)
        sheet1.setColumnView(5, 15)
        sheet1.setColumnView(6, 15)

//      set column name
        sheet1.addCell(new Label(0, 4, "Payment Reference"))
        sheet1.addCell(new Label(1, 4, "Debtor Name"))
        sheet1.addCell(new Label(2, 4, "Invoice Date"))
        sheet1.addCell(new Label(3, 4, "Payment Terms"))
        sheet1.addCell(new Label(4, 4, "Days to Pay"))
        sheet1.addCell(new Label(5, 4, "Due Amount"))
        sheet1.addCell(new Label(6, 4, "Invoice Amount"))

        def totalAmount = 0.0
        def totalPaidAmount = 0.0
        def creditLimit = budgetViewDatabaseService.executeCustomQuery("SELECT vm.credit_limit AS creditLimit FROM vendor_master AS vm WHERE vm.id="+params.id)
        if(creditLimit)
            creditLimit = creditLimit[0][0]

        def invoicePaidAmountArr = budgetViewDatabaseService.executeCustomQuery("SELECT * FROM (SELECT ic.payment_ref AS paymentRefference,cm.customer_name AS debtorName,DATE_FORMAT(ic.trans_date,'%d-%m-%Y') AS invoiceDate,TO_DAYS(ic.trans_date)-TO_DAYS(NOW()) AS daysToPay,if(ISNULL(tblPaidInvoices.lastPaymentDate),TO_DAYS(NOW())-TO_DAYS(ic.trans_date),TO_DAYS(tblPaidInvoices.lastPaymentDate)-TO_DAYS(ic.trans_date)) AS paymentRealized, (TO_DAYS(NOW())-TO_DAYS(ic.trans_date))-180 AS isSixMonths,pt.terms AS paymentTerms,ROUND(ic.total_gl_amount+ic.total_vat,2) AS invoiceAmount,if(ISNULL(tblPaidInvoices.invoicePaidAmount),ROUND(ic.total_gl_amount+ic.total_vat,2),ROUND((ic.total_gl_amount+ic.total_vat)-tblPaidInvoices.invoicePaidAmount,2)) AS dueAmount,if(ISNULL(tblPaidInvoices.invoicePaidAmount),0,ROUND(tblPaidInvoices.invoicePaidAmount,2)) AS paidAmount,cm.credit_limit AS creditLimit,ic.customer_id AS customerId,ic.debtor_id AS debtorId FROM invoice_income AS ic INNER JOIN customer_master AS cm ON ic.debtor_id=cm.id INNER JOIN payment_terms AS pt ON pt.id=ic.terms_id LEFT JOIN (SELECT ic.id AS invoiceId, sum(tm.amount*-1) AS invoicePaidAmount,MAX(tm.trans_date) AS lastPaymentDate FROM invoice_income AS ic INNER JOIN trans_master AS tm ON tm.recenciliation_code = CONCAT(ic.id,'#1') AND tm.account_code='1300' AND tm.trans_type <> 1 GROUP BY ic.id) AS tblPaidInvoices ON tblPaidInvoices.invoiceId=ic.id) AS tblAllInfo WHERE tblAllInfo.invoiceAmount>tblAllInfo.paidAmount+0.05 AND tblAllInfo.customerId="+params.id)
        for (int i = 0; i < invoicePaidAmountArr.size(); i++) {

            BigDecimal showInvoiceAmount = new BigDecimal(invoicePaidAmountArr[i][7])
            BigDecimal showPaidAmount = new BigDecimal(invoicePaidAmountArr[i][9])
            BigDecimal showDueAmount = new BigDecimal(invoicePaidAmountArr[i][8])

            Double dInvAmount = showInvoiceAmount.toDouble()
            Double dPaidAmount = showPaidAmount.toDouble()
            Double dDueAmount = showDueAmount.toDouble()

            totalAmount = totalAmount + dInvAmount
            totalPaidAmount = totalPaidAmount + dPaidAmount

            sheet1.addCell(new Label(0, nRowNo, invoicePaidAmountArr[i][0]))
            sheet1.addCell(new Label(1, nRowNo, invoicePaidAmountArr[i][1]))
            sheet1.addCell(new Label(2, nRowNo, invoicePaidAmountArr[i][2]))
            sheet1.addCell(new Label(3, nRowNo, invoicePaidAmountArr[i][6]))
            sheet1.addCell(new Number(4, nRowNo, invoicePaidAmountArr[i][3]))
            sheet1.addCell(new Number(5, nRowNo, dDueAmount))
            sheet1.addCell(new Number(6, nRowNo, dInvAmount))
            nRowNo++
        }

        sheet1.addCell(new Label(0, 0, "Total invoice"))
        sheet1.addCell(new Number(1, 0, totalAmount))
        sheet1.addCell(new Label(0, 1, "Total amount outstanding"))
        sheet1.addCell(new Number(1, 1, Math.round((totalAmount-totalPaidAmount)*100)/100))
        sheet1.addCell(new Label(0, 2, "Balance credit limit"))
        sheet1.addCell(new Number(1, 2, Math.round((creditLimit - totalAmount + totalPaidAmount)*100)/100))

        workbook.write()
        workbook.close()
    }
*/
    def loadCustomerPaidInvoices(){
        def customerId = request.getParameter("customerId")
        def venId = request.getParameter("venId")
        def venPrefix = request.getParameter("venPrefix")
        def venCode = request.getParameter("venCode")
        def venName = request.getParameter("venName")
        def creditLimit = budgetViewDatabaseService.executeCustomQuery("SELECT vm.credit_limit AS creditLimit FROM vendor_master AS vm WHERE vm.id='${customerId}'")
        if(creditLimit)
            creditLimit = creditLimit[0][0]

        def customerPaidInvoices = budgetViewDatabaseService.executeCustomQuery("""SELECT *
                                                                                            FROM
                                                                                              (SELECT ic.payment_ref AS paymentRefference,
                                                                                                      cm.customer_name AS debtorName,
                                                                                                      DATE_FORMAT(ic.trans_date, '%d-%m-%Y') AS invoiceDate,
                                                                                                      TO_DAYS(ic.trans_date)-TO_DAYS(NOW()) AS daysToPay,
                                                                                                      IF(ISNULL(tblPaidInvoices.lastPaymentDate), TO_DAYS(NOW())-TO_DAYS(ic.trans_date), TO_DAYS(tblPaidInvoices.lastPaymentDate)-TO_DAYS(ic.trans_date)) AS paymentRealized,
                                                                                                      (TO_DAYS(NOW())-TO_DAYS(ic.trans_date))-180 AS isSixMonths,
                                                                                                      pt.terms AS paymentTerms,
                                                                                                      ROUND(ic.total_gl_amount+ic.total_vat, 2) AS invoiceAmount,
                                                                                                      IF(ISNULL(tblPaidInvoices.invoicePaidAmount), ROUND(ic.total_gl_amount+ic.total_vat, 2), ROUND((ic.total_gl_amount+ic.total_vat)-tblPaidInvoices.invoicePaidAmount, 2)) AS dueAmount,
                                                                                                      IF(ISNULL(tblPaidInvoices.invoicePaidAmount), 0, ROUND(tblPaidInvoices.invoicePaidAmount, 2)) AS paidAmount,
                                                                                                      cm.credit_limit AS creditLimit,
                                                                                                      ic.customer_id AS customerId,
                                                                                                      ic.debtor_id AS debtorId
                                                                                               FROM invoice_income AS ic
                                                                                               INNER JOIN customer_master AS cm ON ic.debtor_id=cm.id
                                                                                               INNER JOIN payment_terms AS pt ON pt.id=ic.terms_id
                                                                                               LEFT JOIN
                                                                                                 (SELECT ic.id AS invoiceId,
                                                                                                         SUM(tm.amount*-1) AS invoicePaidAmount,
                                                                                                         MAX(tm.trans_date) AS lastPaymentDate
                                                                                                  FROM invoice_income AS ic
                                                                                                  INNER JOIN trans_master AS tm ON tm.recenciliation_code = CONCAT(ic.id, '#1')
                                                                                                  AND ic.customer_id='${customerId}'
                                                                                                  AND tm.account_code='1300'
                                                                                                  AND tm.trans_type <> 1
                                                                                                  GROUP BY ic.id) AS tblPaidInvoices ON tblPaidInvoices.invoiceId=ic.id) AS tblAllInfo
                                                                                            WHERE tblAllInfo.customerId='${customerId}' 
                                                                                              AND tblAllInfo.invoiceAmount=tblAllInfo.paidAmount
                                                                                            ORDER BY STR_TO_DATE(invoiceDate, '%d-%m-%Y') ASC""")

        render(template: "paidInvoices", model: [customerPaidInvoices:customerPaidInvoices,venPrefix:venPrefix,venCode:venCode,venName:venName,venId:venId,creditLimit:creditLimit])
    }

    def checkCustomer(){
        def vendorName = request.getParameter("vendorName")
        def customerId = request.getParameter("customerId")
        vendorName = vendorName.trim()
        def checkQuery = "SELECT * FROM vendor_master AS vm WHERE LOWER(vm.vendor_name)='${vendorName.toLowerCase()}'"
        def alreadyVendorExist = budgetViewDatabaseService.executeQuery(checkQuery)
        if(customerId){
            def oldVendor = budgetViewDatabaseService.executeQuery("SELECT * FROM vendor_master AS vm WHERE vm.id='${customerId}'")
            if(alreadyVendorExist && oldVendor && (oldVendor[0][0] == alreadyVendorExist[0][0])){
                render "Ok"
            }
            else{
                if(alreadyVendorExist)
                    render "Bad"
                else
                    render "Ok"
            }
        }
        else{
            if(alreadyVendorExist)
                render "Bad"
            else
                render "Ok"
        }

    }

    def writeOffInvoicePaymentVendor(){
        def isFiscalYearClosed = false
        def vendorId = params.id
        def venPrefix = params.vendorPrefix
        def venCode = params.vendorCode
        def venName = params.vendorName

        def selectedRowArr = []
        def rowId = 0;
        int nSelectedCount = 0;
        boolean bIsArray = false;

        String writeOffTransDate = params.transDate
        def writeOffDateArray = writeOffTransDate.split("-")
        def writeOffDay = writeOffDateArray[0]
        def writeOffMonth = writeOffDateArray[1]
        def writeOffYear = writeOffDateArray[2]

        def writeOffAmount
        def writeOffDescription
        if(params.writeOffDescription != ""){
            writeOffDescription = params.writeOffDescription
        } else {
            writeOffDescription = "write off by user"
        }
        try {
            writeOffAmount = Double.parseDouble(params.writeOffAmount)
        }
        catch(Exception e){

        }

        if(!writeOffAmount)
            writeOffAmount = 0.0

        if(params.invBankStatement instanceof Object[]){
            bIsArray = true;
            selectedRowArr = params.invBankStatement
            nSelectedCount = selectedRowArr.size();
        }
        else{

            nSelectedCount = 1;
            rowId = Integer.parseInt(params.invBankStatement);
        }

        for (int i = 0; i < nSelectedCount; i++) {
            if(bIsArray){
                rowId = Integer.parseInt(selectedRowArr[i])
            }

            def invoiceId = session.resultVendorInvInfo[rowId]['invoiceId']
            def paytmentReference = session.resultVendorInvInfo[rowId]['paymentReference']
            def transactionDate = session.resultVendorInvInfo[rowId]['transactionDate']
            def invTransDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(transactionDate)

            def bookingPeriod = writeOffMonth
            def paidAmount = session.resultVendorInvInfo[rowId]['paidAmount']
            def dueAmount = session.resultVendorInvInfo[rowId]['dueAmount']

            def bookingYear = writeOffYear
            def comapnyBankCode = session.resultVendorInvInfo[rowId]['companyBankCode']

            def isValidDate  = new CoreParamsHelperTagLib().isDateClosedFiscalYearForWriteOff(writeOffTransDate)
            if(isValidDate){
                extraSettingService.writeOffInvoicePayment(invoiceId, paytmentReference, transactionDate, bookingPeriod, paidAmount, dueAmount, bookingYear ,writeOffDay,vendorId,writeOffAmount,writeOffDescription)
            }else{
                isFiscalYearClosed = true
            }

        }
        session.resultVendorInvInfo = []
        //Query for invoice and vendor info.
        String strQuery =   "SELECT CONCAT(IF(ie.is_book_receive=1,spr.prefix,spe.prefix),'-',ie.invoice_no) AS invoiceNumber,ie.payment_ref,ie.trans_date,ie.booking_period," +
                "ROUND(ie.fact_inv_amount-(ie.total_gl_amount + ie.total_vat),2) AS invAmount," +
                "ROUND(ROUND(ie.fact_inv_amount-(ie.total_gl_amount + ie.total_vat),2) - (SUM(tm.amount)*(-1)),2) AS paidAmount," +
                "ROUND(SUM(tm.amount)*(-1),2) as remainAmount,ie.id,ie.booking_year  " +
                "FROM system_prefix AS spe,system_prefix AS spr,invoice_expense AS ie " +
                "INNER JOIN trans_master AS tm ON (tm.recenciliation_code = CONCAT(ie.id,'#2') OR tm.recenciliation_code = CONCAT(ie.id,'#4'))  AND " +
                "tm.account_code=(Select creditor_gl_code from debit_credit_gl_setup) "+
                "WHERE ie.vendor_id = " + vendorId +
                " AND spe.id=7 AND  spr.id=12 GROUP BY tm.recenciliation_code ORDER BY ie.trans_date";
        def resultVendorInvInfo = new BudgetViewDatabaseService().executeQuery(strQuery)

        resultVendorInvInfo.eachWithIndex { Phn, key ->
            Map map = ["invoiceId": 0, "paymentReference": '', "transactionDate": 0, "bookingPeriod": 0, "paidAmount": 0, "dueAmount": 0,"bookingYear" : 0]
            map.invoiceId = Phn[7]
            map.paymentReference = Phn[1].toString()
            map.transactionDate = Phn[2].toString()
            map.bookingPeriod = Phn[3]
            map.paidAmount = Phn[5].toString()
            map.dueAmount = Phn[6]
            map.bookingYear = Phn[8]
            session.resultVendorInvInfo << map
        }

        def message="Succesfully Write Off"
        def messageCode="bv.customerMaster.writeOff.message"
        def messageColor = "#057B21"
        if(isFiscalYearClosed){
            message = "One or more invoices transaction date is in closed fiscal year"
            messageCode = "bv.manual.reconciliation.fiscalyearclosed.label"
            messageColor = "#d69302"
        }
        render(template: "financialTransactionForm", model: [invoiceVendorDetailsInstance: resultVendorInvInfo,
                                                             venPrefix:venPrefix,venCode:venCode,venName:venName,venId:vendorId,
                                                             executeMessage : message, executeMessageCode : messageCode,
                                                             executeMessageColor : messageColor])
    }

    def privatePaid(){

        def vendorId = params.id
        def venPrefix = params.vendorPrefix
        def venCode = params.vendorCode
        def venName = params.vendorName

        def selectedRowArr = []
        def rowId = 0;
        int nSelectedCount = 0
        boolean bIsArray = false

        String writeOffTransDate = params.transDate
        def writeOffDateArray = writeOffTransDate.split("-")
        def writeOffDay = writeOffDateArray[0]
        def writeOffMonth = writeOffDateArray[1]
        def writeOffYear = writeOffDateArray[2]

        if(params.invBankStatement instanceof Object[]){
            bIsArray = true;
            selectedRowArr = params.invBankStatement
            nSelectedCount = selectedRowArr.size()
        }
        else{

            nSelectedCount = 1
            rowId = Integer.parseInt(params.invBankStatement);
        }

        for (int i = 0; i < nSelectedCount; i++) {
            if(bIsArray){
                rowId = Integer.parseInt(selectedRowArr[i])
            }

            def invoiceId = session.resultVendorInvInfo[rowId]['invoiceId']
            def paytmentReference = session.resultVendorInvInfo[rowId]['paymentReference']
            def transactionDate = session.resultVendorInvInfo[rowId]['transactionDate']

            def bookingPeriod = writeOffMonth
            def paidAmount = session.resultVendorInvInfo[rowId]['paidAmount']
            def dueAmount = session.resultVendorInvInfo[rowId]['dueAmount']

            def bookingYear = writeOffYear
            def comapnyBankCode = session.resultVendorInvInfo[rowId]['companyBankCode']
            extraSettingService.privatePaidVendor(invoiceId, paytmentReference, transactionDate, bookingPeriod, paidAmount, dueAmount, bookingYear,writeOffDay )
        }

        session.resultVendorInvInfo = []
        //Query for invoice and vendor info.
        String strQuery =   "SELECT CONCAT(IF(ie.is_book_receive=1,spr.prefix,spe.prefix),'-',ie.invoice_no) AS invoiceNumber,ie.payment_ref,ie.trans_date,ie.booking_period," +
                "ROUND((ie.total_gl_amount + ie.total_vat),2) as invAmt," +
                "ROUND((ie.total_gl_amount + ie.total_vat) - (SUM(tm.amount)*(-1)),2) AS paidAmount," +
                "ROUND(SUM(tm.amount)*(-1),2) as remainAmount,ie.id,ie.booking_year  " +
                "FROM system_prefix AS spe,system_prefix AS spr,invoice_expense AS ie " +
                "INNER JOIN trans_master AS tm ON (tm.recenciliation_code = CONCAT(ie.id,'#2') OR tm.recenciliation_code = CONCAT(ie.id,'#4'))  AND " +
                "tm.account_code=(Select creditor_gl_code from debit_credit_gl_setup) "+
                "WHERE ie.vendor_id = " + vendorId +
                " AND spe.id=7 AND  spr.id=12 GROUP BY tm.recenciliation_code ORDER BY ie.trans_date";

        def resultVendorInvInfo = new BudgetViewDatabaseService().executeQuery(strQuery)

        resultVendorInvInfo.eachWithIndex { Phn, key ->
            Map map = ["invoiceId": 0, "paymentReference": '', "transactionDate": 0, "bookingPeriod": 0, "paidAmount": 0, "dueAmount": 0,"bookingYear" : 0]
            map.invoiceId = Phn[7]
            map.paymentReference = Phn[1].toString()
            map.transactionDate = Phn[2].toString()
            map.bookingPeriod = Phn[3]
            map.paidAmount = Phn[5].toString()
            map.dueAmount = Phn[6]
            map.bookingYear = Phn[8]
            session.resultVendorInvInfo << map
        }
        //For bank transaction details
        strQuery =   "SELECT bsidf.bank_payment_id,bsidf.trans_bank_account_no,CONCAT(IF(ie.is_book_receive=1,spr.prefix,spe.prefix),'-',ie.invoice_no) AS invoiceNumber,bsidf.description,bsidf.trans_date_time, " +
                "bsidf.reconciliated_amount AS reconAmount " +
                "FROM system_prefix AS spe,system_prefix AS spr,invoice_expense AS ie " +
                "INNER JOIN trans_master AS tm ON (tm.recenciliation_code = CONCAT(ie.id,'#',2) OR tm.recenciliation_code = CONCAT(ie.id,'#',4)) " +
                "INNER JOIN bank_statement_import_details_final AS bsidf ON bsidf.bank_payment_id = tm.invoice_no " +
                "WHERE ie.vendor_id = " + vendorId + " AND tm.trans_type = 7 AND spe.id=7 AND  spr.id=12 " +
                " GROUP BY bsidf.bank_payment_id ORDER BY ie.trans_date";

        def bankPaymentInfoDetails = new BudgetViewDatabaseService().executeQuery(strQuery)
        def message="Succesfully Write Off"
        def messageCode="bv.customerMaster.privatePaid.message"
        def messageColor = "#057B21"

        render(template: "financialTransactionForm", model: [invoiceVendorDetailsInstance: resultVendorInvInfo,bankPaymentInfoDetailsInstance: bankPaymentInfoDetails,
                                                             venPrefix:venPrefix,venCode:venCode,venName:venName,venId:vendorId,
                                                             executeMessage : message, executeMessageCode : messageCode,
                                                             executeMessageColor : messageColor])
    }

    def userGrid(Integer max){

        String pageNumber = '0'
        if (params.page) {
            pageNumber = params.page
        }
        String rp = "15"
        if (params.rp) {
            rp = params.rp
        }
        int intOffset = 0
        if (params.page && params.rp) {
            intOffset = ((Integer.parseInt(pageNumber) - 1) * Integer.parseInt(rp))
        }

        String gridOutput
        int aInt = 0

        User user = springSecurityService.currentUser
        def userCustomerId = user.customerId

        def vendorPrefix = new CoreParamsHelperTagLib().showGeneratedVendorCode()

        params.max = params.max ?: 10
        params.offset = (params.offset) ? Integer.parseInt(params.offset) : 0
        params.limit = (params.limit) ? Integer.parseInt(params.limit) : 10
        params.sort = "id"
        params.order = "desc"

        String select = """a.id AS id,a.version AS version, a.cham_of_commerce AS chamOfCommerce, a.comments AS comments,
                         a.default_gl_account AS defaultGlAccount,a.email As email,a.gender As gender,a.first_name as firstName,
                         a.last_name As lastName,a.middle_name As middleName,a.payment_term_id As paymentTermId, a.vat As vat,
                         a.vendor_code AS vendorCode,a.vendor_name As vendorName, a.vendor_type As vendorType,a.by_shop As byShop,
                         a.credit_status AS creditStatus,a.credit_limit AS creditLimit,a.vendor_code AS vendorCode"""
        String selectIndex = """id,version,chamOfCommerce,comments,defaultGlAccount,email,gender,
                    firstName,lastName,middleName,paymentTermId,vat,vendorCode,vendorName,vendorType,byShop,creditStatus,creditLimit,vendorCode"""
        String from = "vendor_master AS a "
        String where = ""
        if(userCustomerId)
            where = "a.id='${userCustomerId}'"
        String orderBy = ""
        if (params.sortname && params.order) {
            orderBy = "a.${params.sortname} ${params.order}"
        } else {
            orderBy = "a.id ASC"
        }
        LinkedHashMap gridResult = budgetViewDatabaseService.select(select, from, where, orderBy, '', 'false', selectIndex,0,intOffset)

        String whereInstance = ""
        if (params.id) {
            whereInstance = "a.id=${params.id}"
        } else {
            whereInstance = "a.id=0"
        }

        List quickExpenseList = new ArrayList()
        GridEntity obj
        String userEdit = ""

        def protocol = request.isSecure() ? "https://" : "http://"
        def host = request.getServerName()
        def port = request.getServerPort()
        def context = request.getServletContext().getContextPath()
        def liveUrl = ""
        liveUrl = protocol + host + ":" + port + context
        gridResult['dataGridList'].each { phn ->

            obj = new GridEntity()
            aInt = aInt + 1
            obj.id = aInt
            userEdit = "<a href='javascript:editUrl(\"${phn.id}\",\"${liveUrl}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${liveUrl}/images/edit.png\"></a>"
            obj.cell = ["vendorCode":vendorPrefix + "-" + phn.vendor_code,"vendorName": phn.vendor_name,
                        "email":phn.email, "creditLimit":  phn.credit_limit,
                        "creditStatus": phn.credit_status,"action": userEdit]
            quickExpenseList.add(obj)

        }

        LinkedHashMap result = [draw: 1, recordsTotal: gridResult.size(), recordsFiltered:  gridResult.size(),data:quickExpenseList.cell, userCustomerId : userCustomerId]
        gridOutput = result as JSON
        render gridOutput
    }

    /*def create() {
        [vendorMasterInstance: new VendorMaster(params)]
    }*/

    /*def save() {
        def vendorMasterInstance = new VendorMaster(params)
        if (!vendorMasterInstance.save(flush: true)) {
            render(view: "create", model: [vendorMasterInstance: vendorMasterInstance])
            return
        }

        flash.message = message(code: 'com.created.message', args: [message(code: 'vendorMaster.label', default: 'VendorMaster'), vendorMasterInstance.id])
        redirect(action: "show", id: vendorMasterInstance.id)
    }*/

    def show(Long id) {
        def vendorMasterInstance = VendorMaster.get(id)
        if (!vendorMasterInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'vendorMaster.label', default: 'VendorMaster'), id])
            redirect(action: "list")
            return
        }
        [vendorMasterInstance: vendorMasterInstance]
    }

    def edit(Long id) {
        def vendorMasterInstance = VendorMaster.get(id)
        if (!vendorMasterInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'vendorMaster.label', default: 'VendorMaster'), id])
            redirect(action: "list")
            return
        }

        [vendorMasterInstance: vendorMasterInstance]
    }

    def update(Long id, Long version) {
        def vendorId = saveAndUpdate(id, version, params)
        if (id) {
            flash.message = message(code: 'com.updated.message', args: [message(code: 'vendorMaster.label', default: 'VendorMaster'), vendorId])
            redirect(action: "list", id: vendorId, params: [bookInvoiceId: params.bookInvoiceId, bookingPeriod: params.bookingPeriod, vendorId: params.vendorId, budgetItemDetailsId: params.budgetItemDetailsId, journalId: params.journalId, expBItem: params.expBItem, sid: params.sid, fInv: params.fInv,invEditId:params.editId])
        } else {
            flash.message = message(code: 'com.created.message', args: [message(code: 'vendorMaster.label', default: 'vendorMaster'), vendorId])
            redirect(action: "list", id: vendorId, params: [bookInvoiceId: params.bookInvoiceId, bookingPeriod: params.bookingPeriod, vendorId: params.vendorId, budgetItemDetailsId: params.budgetItemDetailsId, journalId: params.journalId, expBItem: params.expBItem, sid: params.sid, fInv: params.fInv,invEditId:params.editId])

        }
    }

    def updateAndCreateDebtor(Long id, Long version){
        saveAndUpdate(id, version,params)
        if(id){
            redirect(controller: "debtorCustomer", action: "index")
        }else {
            redirect(controller: "debtorCustomer", action: "index")
        }
    }

    @Transactional
    def saveAndUpdate(Long id, Long version, def params){
        if (id) {

            String select = "id,version"
            String selectIndex = "id,version"
            String from = "vendor_master"
            String where = "id='${id}'"
            String orderBy = "id ASC"
            LinkedHashMap gridResult = budgetViewDatabaseService.select(select, from, where, orderBy, '', 'false', selectIndex)

            def vendorMasterInstance = ""
            if (gridResult['dataGridList'].size()) {
                vendorMasterInstance = gridResult['dataGridList'][0]
            }

            if (!vendorMasterInstance) {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'vendorMaster.label', default: 'VendorMaster'), id])
                redirect(action: "list")
                return
            }

            if (version != null) {
                if (vendorMasterInstance.version > version) {
                    vendorMasterInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                            [message(code: 'vendorMaster.label', default: 'VendorMaster')] as Object[],
                            "Another user has updated this VendorMaster while you were editing")
                    render(view: "list", model: [vendorMasterInstance: vendorMasterInstance])
                    return
                }
            }

            def byShop = 0
            if (params.vendorType == "sn") {
                byShop = 1
            }

            params.creditLimit = debtorCustomerService.replaceComaToDot(params.creditLimit)

            updateBasicCustomerData(params, byShop, id)

            boolean createCusToggle = params.isCustomerPortalUser instanceof String ? params.isCustomerPortalUser : ""

            //save customer as user with random password
            if (createCusToggle.equals(false)) {
                saveOrUpdateCustomerAsUser(id, params)
            }
            return id

        } else { //new Customer Creation

            def vendorMasterInstanceId =  newCustomerCreation(params)
            return vendorMasterInstanceId

        }
    }

    def sendEmailToCustomerAsUser(long id, params, String password) {

        String email = params.email
        String pass = password
        String firstName = params.firstName
        String lastName = params.lastName

        try{
            //dummy body, has to send proper message
            mailService.sendMail {
                async true
                to email
                subject message(code: "default.customer.create.welcome.label")
                body(view: "customerWelcomeMailBody", model: [email: email, password: pass, firstName: firstName , lastName: lastName ])
            }

        }catch(Exception e){
            e.printStackTrace()
            //flash.message = "Mail send failed. Please try again."
        }
    }

    private void updateBasicCustomerData(params, int byShop, long id) {
        Map updatededValue = [
                cham_of_commerce  : "${params.chamOfCommerce}",
                comments          : "${params.comments}",
                default_gl_account: "7000",
                email             : "${params.email}",
                first_name        : "${params.firstName}",
                last_name         : "${params.lastName}",
                middle_name       : "${params.middleName}",
                payment_term_id   : "${params.paymentTerm}",
                status            : "${params.status}",
                gender            : "${params.gender}",
                vat               : "${params.vat}",
                vendor_type       : "${params.vendorType}",
                vendor_name       : "${params.vendorName}",
                by_shop           : byShop,
                credit_status     : "${params.creditStatus}",
                credit_limit      : "${params.creditLimit}",
                vat_number        : "${params.vatNumber}",
                optional_email    : "${params.optionalEmail}"
        ]

        def updatedTableName = "vendor_master"
        def updatedWhereString = "id='${id}'"
        budgetViewDatabaseService.update(updatededValue, updatedTableName, updatedWhereString)

        updateCustomerGeneralAddress((params.id).toLong(), params)
    }

    private void updateCustomerGeneralAddress(long id, params) {
        def generalAddressId = budgetViewDatabaseService.executeQueryAtSingle("SELECT id FROM vendor_general_address WHERE vendor_id='${id}'")

        if (generalAddressId != "") {
            Map updatededGeneralAddressValue = [
                    phone_no       : "${params.phoneNo}",
                    website_address: "${params.websiteAddress}",
            ]

            def updatedTable = "vendor_general_address"
            def updatedWhere = "id='${generalAddressId[0]}'"
            budgetViewDatabaseService.update(updatededGeneralAddressValue, updatedTable, updatedWhere)

        } else {

            Map insertVendorGeneralAddress = [

                    phone_no           : "${params.phoneNo}",
                    website_address    : "${params.websiteAddress}",
                    vendor_id          : "${params.id}",
                    country_id         : 1,
                    city               : "${params.city}",
                    contact_person_name: "${params.contactPersonName}",
                    postal_code        : "${params.postalCode}",
                    status             : 1

            ]

            def table = "vendor_general_address"
            Integer vendorrGeneralAddressInstanceId = budgetViewDatabaseService.insert(insertVendorGeneralAddress, table)
        }
    }

    @Secured(['ROLE_ADMIN'])
    private void saveOrUpdateCustomerAsUser(long id, params){

        User customer = User.findByCustomerId(id)

        if (customer) {
            User userName = User.findByUsername(params.email)
            if(!userName) {
                customer.username = params.email
                customer.save(flush: true)
            }

        } else {

            User userInDb = User.findByCustomerIdAndEmail(id, params.email)

            if(!userInDb){

                String password = ApplicationUtil.generateRandomString()

                User userInstance =  new User()
                userInstance.enabled = 'on'
                userInstance.lastName = params.lastName
                userInstance.username = params.email
                userInstance.email = params.email
                userInstance.firstName = params.firstName
                userInstance.password = password
                userInstance.customerId = id

                if(session.permittedBusinessCompanyId != null){
                    userInstance.businessCompanyId = session.permittedBusinessCompanyId
                } else {
                    def user = springSecurityService.currentUser
                    userInstance.businessCompanyId = user.businessCompanyId
                }

                new UserController().saveCustomerAsUser(userInstance)
                sendEmailToCustomerAsUser(id, params , password)
            }
        }



    }

    private int newCustomerCreation(params) {
        params.vendorCode = new CoreParamsHelperTagLib().getNextGeneratedNumber('vendor')

        params.status = 1
        params.vendorType = "cn"

        params.byShop = 0
        if (params.vendorType == "sn") {
            params.byShop = 1
        }

        params.creditLimit = debtorCustomerService.replaceComaToDot(params.creditLimit)

        Map insertStr = [
                cham_of_commerce  : "",
                comments          : "",
                default_gl_account: "7000",
                email             : "",
                first_name        : "",
                last_name         : "",
                middle_name       : "",
                payment_term_id   : params.paymentTerm,
                status            : "1",
                gender            : "${params.gender}",
                vat               : "${params.vat}",
                vendor_type       : params.vendorType,
                vendor_name       : "${params.vendorName}",
                by_shop           : params.byShop,

                credit_status     : params.creditStatus,
                credit_limit      : params.creditLimit,
                vendor_code       : params.vendorCode

        ]

        def tableName = "vendor_master"
        Integer vendorMasterInstanceId = budgetViewDatabaseService.insert(insertStr, tableName)

        //Add default data into Vendor_Factoring
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String text = date.format(formatter);
        LocalDate currentDate = LocalDate.parse(text, formatter);

        Map factorMapData = [
                acceptence_fee    : "20.0",
                admin_cost        : "10.0",
                default_fee       : "0.0",
                outpayment        : "100.0",
                subcription_amount: "199.0",
                subcription_date  : "${currentDate}",
                customer_id       : "${vendorMasterInstanceId}",
                broker_id         : "0",
                broker_fee        : "10.0",
                broker_date       : "${currentDate}"
        ]

        tableName = "vendor_factoring"
        new BudgetViewDatabaseService().insert(factorMapData, tableName)

        return vendorMasterInstanceId
    }

    def delete(Long id) {

        def vendorMasterInstance = VendorMaster.get(id)

        if (!vendorMasterInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'vendorMaster.label', default: 'VendorMaster'), id])
            redirect(action: "list")
            return
        }
        /* Check the vendor has any transaction in Income Expense table.
        If yes then throw a message in view page
        else it will permanently delete all information from related tables*/

        def vendorMasterarr = InvoiceExpense.executeQuery("SELECT vendorAccountNo FROM bv.InvoiceExpense where vendorAccountNo='" + vendorMasterInstance.vendorCode + "'")

        if (vendorMasterarr.size() > 0) {
            flash.message = message(code: 'com.not.deleted.message', args: [message(code: 'vendorMaster.label', default: 'VendorMaster'), vendorMasterInstance.vendorName])
            redirect(action: "list", id: id)
        } else {
            try {
                VendorGeneralAddress.executeUpdate("Delete FROM VendorGeneralAddress WHERE vendor_id='" + id + "'")
                VendorPostalAddress.executeUpdate("Delete FROM VendorPostalAddress WHERE vendor_id='" + id + "'")
                VendorBankAccount.executeUpdate("Delete FROM VendorBankAccount WHERE vendor_id='" + id + "'")
                vendorMasterInstance.delete(flush: true)
                flash.message = message(code: 'com.deleted.message', args: [message(code: 'vendorMaster.label', default: 'VendorMaster'), id])
                redirect(action: "list")
            }
            catch (DataIntegrityViolationException e) {
                flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'vendorMaster.label', default: 'VendorMaster'), id])
                redirect(action: "list", id: id)
            }
        }
    }

    def updategeneral(Long id, Long version) {

        if (id) {
            String select = "id,version"
            String selectIndex = "id,version"
            String from = "vendor_general_address"
            String where = "id='" + id + "'"
            String orderBy = "id ASC"
            LinkedHashMap gridResult = budgetViewDatabaseService.select(select, from, where, orderBy, '', 'false', selectIndex)

            def vendorGeneralAddressInstance = ""

            if (gridResult['dataGridList'].size()) {
                vendorGeneralAddressInstance = gridResult['dataGridList'][0]
            }

            if (!vendorGeneralAddressInstance) {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'vendorGeneralAddress.label', default: 'VendorGeneralAddress'), id])
                redirect(action: "list")
                return
            }

            if (version != null) {
                if (vendorGeneralAddressInstance.version > version) {
                    vendorGeneralAddressInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                            [message(code: 'vendorGeneralAddress.label', default: 'VendorGeneralAddress')] as Object[],
                            "Another user has updated this VendorGeneralAddress while you were editing")
                    render(view: "list", model: [vendorGeneralAddressInstance: vendorGeneralAddressInstance])
                    return
                }
            }

            Map updatededValue = [

                    address_line1               :       "${params.addressLine1}",
                    address_line2               :       "${params.addressLine2}",
                    city                        :       "${params.city}",
                    contact_person_name         :       "${params.contactPersonName}",
                    country_id                  :       "${params.countryId}",
                    postal_code                 :       "${params.postalCode}",
                    status                      :       "1",
                    vendor_id                   :       "${params.vendor_id}",
                    second_email                :       "${params.officeEmail}"
            ]

            def updatedTableName = "vendor_general_address"
            def updatedWhereSrting = "id='${id}'"
            budgetViewDatabaseService.update(updatededValue, updatedTableName, updatedWhereSrting)

            flash.message = message(code: 'com.updated.message', args: [message(code: 'vendorGeneralAddress.label', default: 'VendorGeneralAddress'), id])
            redirect(action: "list", id: params.vendor_id, params: [bookInvoiceId: params.bookInvoiceId, bookingPeriod: params.bookingPeriod, vendorId: params.vendorId, budgetItemDetailsId: params.budgetItemDetailsId, journalId: params.journalId, expBItem: params.expBItem, sid: params.sid, fInv: params.fInv])

        } else {
            Map insertedValue = [

                    address_line1               :       "${params.addressLine1}",
                    address_line2               :       "${params.addressLine2}",
                    city                        :       "${params.city}",
                    contact_person_name         :       "${params.contactPersonName}",
                    country_id                  :       "${params.countryId}",
                    postal_code                 :       "${params.postalCode}",
                    status                      :       "1",
                    vendor_id                   :       "${params.vendor_id}",
                    second_email                :       "${params.officeEmail}"
            ]

            def tableName = "vendor_general_address"

            Integer vendorrGeneralAddressInstanceId = budgetViewDatabaseService.insert(insertedValue, tableName)
            flash.message = message(code: 'com.created.message', args: [message(code: 'vendorGeneralAddress.label', default: 'VendorGeneralAddress'), vendorrGeneralAddressInstanceId])
            redirect(action: "list", id: params.vendor_id)

        }
    }

    def updatepostal(Long id, Long version) {

        if (id) {

            //vendor_postal_address
            String select = "id,version"
            String selectIndex = "id,version"
            String from = "vendor_postal_address"
            String where = "id='${id}'"
            String orderBy = "id ASC"
            LinkedHashMap gridResult = budgetViewDatabaseService.select(select, from, where, orderBy, '', 'false', selectIndex)

            def vendorPostalAddressInstance = ""

            if (gridResult['dataGridList'].size()) {
                vendorPostalAddressInstance = gridResult['dataGridList'][0]
            }

            if (!vendorPostalAddressInstance) {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'vendorPostalAddress.label', default: 'VendorPostalAddress'), id])
                redirect(action: "list")
                return
            }

            if (version != null) {
                if (vendorPostalAddressInstance.version > version) {
                    vendorPostalAddressInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                            [message(code: 'vendorPostalAddress.label', default: 'VendorPostalAddress')] as Object[],
                            "Another user has updated this VendorPostalAddress while you were editing")
                    render(view: "list", model: [vendorPostalAddressInstance: vendorPostalAddressInstance])
                    return
                }
            }

            Map updatededValue = [

                    postal_address_line1        :   "${params.postalAddressLine1}",
                    postal_address_line2        :   "${params.postalAddressLine2}",
                    postal_city                 :   "${params.postalCity}",
                    postal_contact_person_name  :   "${params.postalContactPersonName}",
                    postal_postcode             :   "${params.postalPostcode}",
                    postal_country_id           :   "${params.postalCountry.id}",
                    vendor_id                   :   "${params.vendor_id}",
                    status                      :   "1",
                    postal_email                :  "${params.postalEmail}"

            ]

            def updatedTableName = "vendor_postal_address"
            def updatedWhereSrting = "id='${id}'"
            budgetViewDatabaseService.update(updatededValue, updatedTableName, updatedWhereSrting)
            flash.message = message(code: 'com.updated.message', args: [message(code: 'vendorPostalAddress.label', default: 'VendorPostalAddress'), vendorPostalAddressInstance.id])
            redirect(action: "list", id: params.vendor_id, params: [selecedTab: params.selecedTab, bookInvoiceId: params.bookInvoiceId, bookingPeriod: params.bookingPeriod, vendorId: params.vendorId, budgetItemDetailsId: params.budgetItemDetailsId, journalId: params.journalId, expBItem: params.expBItem, sid: params.sid, fInv: params.fInv])


        } else {

            Map insertedValue = [

                    postal_address_line1        :   "${params.postalAddressLine1}",
                    postal_address_line2        :   "${params.postalAddressLine2}",
                    postal_city                 :   "${params.postalCity}",
                    postal_contact_person_name  :   "${params.postalContactPersonName}",
                    postal_postcode             :   "${params.postalPostcode}",
                    postal_country_id           :   "${params.postalCountry.id}",
                    vendor_id                   :   "${params.vendor_id}",
                    status                      :   "1",
                    postal_email                :  "${params.postalEmail}"

            ]

            def tableName = "vendor_postal_address"
            //selecedTab
            Integer vendorPostalAddressInstanceId = budgetViewDatabaseService.insert(insertedValue, tableName)
            flash.message = message(code: 'com.created.message', args: [message(code: 'vendorPostalAddress.label', default: 'VendorPostalAddress'), vendorPostalAddressInstanceId])
            redirect(action: "list", id: params.vendor_id, params: [selecedTab: params.selecedTab, bookInvoiceId: params.bookInvoiceId, bookingPeriod: params.bookingPeriod, vendorId: params.vendorId, budgetItemDetailsId: params.budgetItemDetailsId, journalId: params.journalId, expBItem: params.expBItem, sid: params.sid, fInv: params.fInv])
        }
    }

    def updateFactoring(Long id, Long version){

        debtorCustomerService.updateCustomerFactoring(id,params)

        flash.message = message(code: 'com.updated.message', args: [message(code: 'bv.customerMaster.factoring.label', default: 'Customer Factoring'), 0])
        redirect(action: "list", id: params.customerId, params: [selecedTab: params.selecedTab,vendorId: params.customerId])
    }

    def updatebanking(Long id, Long version) {

        if (id) {

            String select = "id,version"
            String selectIndex = "id,version"
            String from = "vendor_bank_account"
            String where = "id='${id}'"
            String orderBy = "id ASC"
            LinkedHashMap gridResult = budgetViewDatabaseService.select(select, from, where, orderBy, '', 'false', selectIndex)

            def vendorBankAccountInstance = ""

            if (gridResult['dataGridList'].size()) {
                vendorBankAccountInstance = gridResult['dataGridList'][0]
            }

            if (!vendorBankAccountInstance) {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'vendorBankAccount.label', default: 'VendorBankAccount'), id])
                redirect(action: "list")
                return
            }

            if (version != null) {
                if (vendorBankAccountInstance.version > version) {
                    vendorBankAccountInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                            [message(code: 'vendorBankAccount.label', default: 'VendorBankAccount')] as Object[],
                            "Another user has updated this VendorBankAccount while you were editing")
                    render(view: "list", model: [vendorBankAccountInstance: vendorBankAccountInstance])
                    return
                }
            }

            Map updatededValue = [
                    bank_account_name   :   "${params.bankAccountName}",
                    bank_account_no     :   "${params.bankAccountNo}",
                    iban_prefix         :   "${params.ibanPrefix}",
                    status           :      "${params.status}",
                    vendor_id           :   "${params.vendor_id}"

            ]


            def updatedTableName = "vendor_bank_account"
            def updatedWhereSrting = "id='${id}'"
            budgetViewDatabaseService.update(updatededValue, updatedTableName, updatedWhereSrting)

            flash.message = message(code: 'com.updated.message', args: [message(code: 'vendorBankAccount.label', default: 'VendorBankAccount'), vendorBankAccountInstance.id])
            redirect(action: "list", id: params.vendor_id, params: [banktab: params.vendor_id, bookInvoiceId: params.bookInvoiceId, bookingPeriod: params.bookingPeriod, vendorId: params.vendorId, budgetItemDetailsId: params.budgetItemDetailsId, journalId: params.journalId, expBItem: params.expBItem, sid: params.sid, fInv: params.fInv])

        } else {

            Map insertedValue = [

                    bank_account_name   :   "${params.bankAccountName}",
                    bank_account_no     :   "${params.bankAccountNo}",
                    iban_prefix         :   "${params.ibanPrefix}",
                    status              :    "1",
                    vendor_id           :   "${params.vendor_id}"

            ]
            def tableName = "vendor_bank_account"

            Integer customerEditBankAccountInstanceId = budgetViewDatabaseService.insert(insertedValue, tableName)
            flash.message = message(code: 'com.created.message', args: [message(code: 'customerBankAccount.label', default: 'CustomerBankAccount'), customerEditBankAccountInstanceId])
            redirect(action: "list", id: params.vendor_id, params: [banktab: params.vendor_id, bookInvoiceId: params.bookInvoiceId, bookingPeriod: params.bookingPeriod, vendorId: params.vendorId, budgetItemDetailsId: params.budgetItemDetailsId, journalId: params.journalId, expBItem: params.expBItem, sid: params.sid, fInv: params.fInv])

        }
    }

    def selectSearchType(){
        render(template: "ajaxSelectSearchType", model: [params:params])
    }

    def updateVendorListView(){
        render(template: "updateVendorListView", model: [params:params])
    }

    def searchVendor(Integer max){

        String pageNumber = '0'
        if (params.page) {
            pageNumber = params.page
        }
        String rp = "15"
        if (params.rp) {
            rp = params.rp
        }
        int intOffset = 0
        if (params.page && params.rp) {
            intOffset = ((Integer.parseInt(pageNumber) - 1) * Integer.parseInt(rp))
        }
        String strSortedBy = 'id'
        strSortedBy = params.sortname
        String sortOrder = 'asc'
        sortOrder = params.sortorder

        String gridOutput
        int aInt = 0
        String searchItem
        String searchString
        searchItem = params.qtype;
        searchString = params.query;
        def userList


        def vendorPrefix = new CoreParamsHelperTagLib().showGeneratedVendorCode()

        params.max = params.max ?: 10
        params.offset = (params.offset) ? Integer.parseInt(params.offset) : 0
        params.limit = (params.limit) ? Integer.parseInt(params.limit) : 10
        params.sort = "id"
        params.order = "desc"

        String select
        String selectIndex
        String from
        String where
        String orderBy
        LinkedHashMap gridResult
        String whereInstance
        LinkedHashMap gridResultInstance
        def totalCount
        String searchInput = params.searchInput
        String glAccount=params.glAccountSearch

        String searchByVendorName=searchInput.trim()

        if(searchByVendorName){
            select = """a.id AS id,a.version AS version, a.cham_of_commerce AS chamOfCommerce, a.comments AS comments,a.default_gl_account AS defaultGlAccount,a.email As email,a.first_name as firstName, a.last_name As lastName,a.middle_name As middleName,a.payment_term_id As paymentTermId, a.vat As vat,a.vendor_code AS vendorCode,a.vendor_name As vendorName, a.vendor_type As vendorType,a.by_shop As byShop,a.credit_status AS creditStatus,a.vendor_code AS vendorCode"""
            selectIndex = """id,version,chamOfCommerce,comments,defaultGlAccount,email,firstName,lastName,middleName,paymentTermId,vat,vendorCode,vendorName,vendorType,byShop,creditStatus,vendorCode"""
            from = "vendor_master AS a "
            where = " a.vendor_name like'%" +searchByVendorName+ "%' "
            orderBy = ""
            if (params.sortname && params.order) {
                orderBy = "a.${params.sortname} ${params.order}"
            } else {
                orderBy = "a.id ASC"
            }
            gridResult = budgetViewDatabaseService.select(select, from, where, orderBy, '', 'false', selectIndex, Integer.parseInt(params.rp),intOffset)

            whereInstance = ""
            if (params.id) {
                whereInstance = "a.id=${params.id}"
            } else {
                whereInstance = "a.id=0"
            }
            gridResultInstance = budgetViewDatabaseService.select(select, from, whereInstance, orderBy, '', 'false', selectIndex)
            totalCount = new BudgetViewDatabaseService().executeQuery("SELECT count(a.id)  FROM vendor_master AS a WHERE a.vendor_name like'%" +searchByVendorName+ "%' AND status =1")
        }

        else if(glAccount){

            select = """a.id AS id,a.version AS version, a.cham_of_commerce AS chamOfCommerce, a.comments AS comments,a.default_gl_account AS defaultGlAccount,a.email As email,a.first_name as firstName, a.last_name As lastName,a.middle_name As middleName,a.payment_term_id As paymentTermId, a.vat As vat,a.vendor_code AS vendorCode,a.vendor_name As vendorName, a.vendor_type As vendorType,a.by_shop As byShop,a.credit_status AS creditStatus,a.vendor_code AS vendorCode"""
            selectIndex = """id,version,chamOfCommerce,comments,defaultGlAccount,email,firstName,lastName,middleName,paymentTermId,vat,vendorCode,vendorName,vendorType,byShop,creditStatus,vendorCode"""
            from = "vendor_master AS a "
            where = " a.default_gl_account='" +glAccount+ "' "
            orderBy = ""
            if (params.sortname && params.order) {
                orderBy = "a.${params.sortname} ${params.order}"
            } else {
                orderBy = "a.id ASC"
            }
            gridResult = budgetViewDatabaseService.select(select, from, where, orderBy, '', 'false', selectIndex, Integer.parseInt(params.rp),intOffset)

            whereInstance = ""
            if (params.id) {
                whereInstance = "a.id=${params.id}"
            } else {
                whereInstance = "a.id=0"
            }
            gridResultInstance = budgetViewDatabaseService.select(select, from, whereInstance, orderBy, '', 'false', selectIndex)
            totalCount = new BudgetViewDatabaseService().executeQuery("SELECT count(a.id)  FROM vendor_master AS a WHERE a.default_gl_account='" +glAccount+ "' AND status =1")


        }
        List quickExpenseList = new ArrayList()
        GridEntity obj
        String userEdit = ""

        def protocol = request.isSecure() ? "https://" : "http://"
        def host = request.getServerName()
        def port = request.getServerPort()
        def context = request.getServletContext().getContextPath()
        def liveUrl = ""
        liveUrl = protocol + host + ":" + port + context
        println("liveUrl------" + liveUrl)

        println("gridResult['dataGridList'][0]--------" + gridResult['dataGridList'][0])
        println("gridResult[0]--------" + gridResult[0])
        gridResult['dataGridList'].each { phn ->

            def showVendorType = new CoreParamsHelperTagLib().getBookingPeriodDetails(phn.vendor_type, 'VENDOR_TYPE')

            obj = new GridEntity();
            aInt = aInt + 1
            obj.id = aInt
            userEdit = "<a href='javascript:editUrl(\"${phn.id}\",\"${liveUrl}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${liveUrl}/images/edit.png\"></a>"
            obj.cell = [aInt,vendorPrefix + "-" + phn.vendor_code, phn.vendor_name,phn.email,  showVendorType, phn.credit_status, userEdit]
            quickExpenseList.add(obj)

        }
        LinkedHashMap result = [page: pageNumber, total: totalCount, rows: quickExpenseList]
        gridOutput = result as JSON
        render gridOutput;
    }

    /*def exportExcelofBankTransaction(){

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm");
        Date date = new Date();
        String strDateCreated = dateFormat.format(date);
        String fileName="Financial_Transaction_Customer"+strDateCreated
        int nRowNo = 1;
        def ActiveFiscalYear = new CoreParamsHelperTagLib().getActiveFiscalYear()
        def fiscalYearInfo = new CoreParamsHelperTagLib().getActiveFiscalYearInformation(ActiveFiscalYear)

        //Getting budget customer data.

        response.setContentType('application/vnd.ms-excel')
        response.setHeader('Content-Disposition', 'Attachment;Filename='+fileName+'.xls')
        WritableWorkbook workbook = Workbook.createWorkbook(response.outputStream)


        WritableSheet sheet1 = workbook.createSheet("Invoices Including Paid Amount", 0)
//       set column width
        sheet1.setColumnView(0,20)
        sheet1.setColumnView(1,30)
        sheet1.setColumnView(2,30)
        sheet1.setColumnView(3,15)
        sheet1.setColumnView(4,15)
        sheet1.setColumnView(5,15)
        sheet1.setColumnView(6,15)

//      set column name
        sheet1.addCell(new Label(0,0, "Invoice Number"))
        sheet1.addCell(new Label(1,0, "Payment Reference"))
        sheet1.addCell(new Label(2,0, "Transaction Date"))
        sheet1.addCell(new Label(3,0, "Booking Period"))
        sheet1.addCell(new Label(4,0, "Paid Amount"))
        sheet1.addCell(new Label(5,0, "Due Amount"))
        sheet1.addCell(new Label(6,0, "Paid Status"))


        String strQuery =   "SELECT CONCAT(IF(ie.is_book_receive=1,spr.prefix,spe.prefix),'-',ie.invoice_no) AS invoiceNumber,ie.payment_ref,ie.trans_date,ie.booking_period," +
                "ROUND((ie.total_gl_amount + ie.total_vat),2) as invAmt," +
                "ROUND((ie.total_gl_amount + ie.total_vat) - (SUM(tm.amount)*(-1)),2) AS paidAmount," +
                "ROUND(SUM(tm.amount)*(-1),2) as remainAmount " +
                "FROM system_prefix AS spe,system_prefix AS spr,invoice_expense AS ie " +
                "INNER JOIN trans_master AS tm ON (tm.recenciliation_code = CONCAT(ie.id,'#2') OR tm.recenciliation_code = CONCAT(ie.id,'#4'))  AND " +
                "tm.account_code=(Select creditor_gl_code from debit_credit_gl_setup) "+
                "WHERE ie.vendor_id = " +params.id +
                " AND spe.id=7 AND  spr.id=12 GROUP BY tm.recenciliation_code ORDER BY ie.trans_date";
        def invoicePaidAmountArr = new BudgetViewDatabaseService().executeQuery(strQuery)


        for(int i=0;i<invoicePaidAmountArr.size();i++){

            BigDecimal showInvoiceAmount = new BigDecimal(invoicePaidAmountArr[i][4])
            BigDecimal showPaidAmount = new BigDecimal(invoicePaidAmountArr[i][5])
            BigDecimal showDueAmount = new BigDecimal(invoicePaidAmountArr[i][6])

            Double dInvAmount = showInvoiceAmount.toDouble()
            Double dPaidAmount = showPaidAmount.toDouble()
            Double dDueAmount = showDueAmount.toDouble()
            String strPaidStatus = "Not Paid"
            if(dDueAmount == 0.00)
            {
                strPaidStatus = "Paid"
            }
            else if(dDueAmount < 0.00 && dInvAmount < dPaidAmount)
            {
                strPaidStatus = "Over Paid"
            }

            sheet1.addCell(new Label(0,nRowNo, invoicePaidAmountArr[i][0]))
            sheet1.addCell(new Label(1,nRowNo, invoicePaidAmountArr[i][1]))
            sheet1.addCell(new Label(2,nRowNo, new SimpleDateFormat("dd MMM yyyy").format(invoicePaidAmountArr[i][2])))
            def bookingMonth = new CoreParamsHelperTagLib().monthNameShow(Integer.parseInt(invoicePaidAmountArr[i][3]))
            sheet1.addCell(new Label(3,nRowNo,bookingMonth ))
            sheet1.addCell(new Label(4,nRowNo,dPaidAmount.toString()))
            sheet1.addCell(new Label(5,nRowNo, dDueAmount.toString()))
            sheet1.addCell(new Label(6,nRowNo, strPaidStatus))
            nRowNo++
        }

        //Getting income invoice data.

        int incresedRowNumber=nRowNo+2
        sheet1.addCell(new Label(0,incresedRowNumber, "Bank Payment Id"))
        sheet1.addCell(new Label(1,incresedRowNumber, "Company Bank Account"))
        sheet1.addCell(new Label(2,incresedRowNumber, "Invoice Number"))
        sheet1.addCell(new Label(3,incresedRowNumber, "Payment Description"))
        sheet1.addCell(new Label(4,incresedRowNumber, "Date"))
        sheet1.addCell(new Label(5,incresedRowNumber, "Amount"))

        sheet1.setColumnView(0,20)
        sheet1.setColumnView(1,30)
        sheet1.setColumnView(2,30)
        sheet1.setColumnView(3,100)
        sheet1.setColumnView(4,15)
        sheet1.setColumnView(5,15)

        //For bank transaction details
        strQuery =   "SELECT bsidf.bank_payment_id,bsidf.trans_bank_account_no,CONCAT(IF(ie.is_book_receive=1,spr.prefix,spe.prefix),'-',ie.invoice_no) AS invoiceNumber,bsidf.description,bsidf.trans_date_time, " +
                "bsidf.reconciliated_amount AS reconAmount " +
                "FROM system_prefix AS spe,system_prefix AS spr, invoice_expense AS ie " +
                "INNER JOIN trans_master AS tm ON (tm.recenciliation_code = CONCAT(ie.id,'#',2) OR tm.recenciliation_code = CONCAT(ie.id,'#',4)) " +
                "INNER JOIN bank_statement_import_details_final AS bsidf ON bsidf.bank_payment_id = tm.invoice_no " +
                "WHERE ie.vendor_id = " + params.id + " AND tm.trans_type = 7  AND spe.id=7 AND  spr.id=12 " +
                "GROUP BY bsidf.bank_payment_id ORDER BY ie.trans_date"
        def bankPaymentInfoDetails = new BudgetViewDatabaseService().executeQuery(strQuery)


        for(int i=0;i<bankPaymentInfoDetails.size();i++){
            sheet1.addCell(new Label(0,incresedRowNumber+1, bankPaymentInfoDetails[i][0].toString()))
            sheet1.addCell(new Label(1,incresedRowNumber+1, bankPaymentInfoDetails[i][1].toString()))
            sheet1.addCell(new Label(2,incresedRowNumber+1, bankPaymentInfoDetails[i][2].toString()))
            sheet1.addCell(new Label(3,incresedRowNumber+1, bankPaymentInfoDetails[i][3]))
            sheet1.addCell(new Label(4,incresedRowNumber+1, bankPaymentInfoDetails[i][4]))
            sheet1.addCell(new Label(5,incresedRowNumber+1, bankPaymentInfoDetails[i][5].toString()))
            incresedRowNumber++
        }

        workbook.write()
        workbook.close()
    }*/

    def changeAddress () {
        changePostalAddress()
    }

    def changePostalAddress(Long id, Long version) {

        withForm {

            User user = springSecurityService.currentUser
            def userCustomerId = user.customerId.toString()

            def customerPostalId
            boolean isInsertPostalVal = false

            if (!id) {
                customerPostalId = params.postalAddId

            } else {
                customerPostalId = id
            }


            String requestedTable = "vendor_postal_address"
            String tableName = "vendor_change_request"

            //requested Changes
            Map submittedPostalMap = [
                    postal_address_line1        :   "${params.postalAddLine1.trim()}",
                    postal_address_line2        :   "${params.postalAddLine2.trim()}",
                    postal_city                 :   "${params.postalCity.trim()}",
                    //                postal_contact_person_name  :   "${params.postalContactPersonName.trim()}",
                    postal_postcode             :   "${params.postalPostCode.trim()}",
                    postal_country_id           :   "${params.postalCountryId}",
            ]

            if(!customerPostalId){ //new request while no entry in db

                String newRequestMap = ApplicationUtil.mapToJsonConvert(submittedPostalMap)

                String sameCreateRequest = """SELECT *
                                        FROM `vendor_change_request`
                                        WHERE vendor_id ='${userCustomerId}' 
                                          AND status!='approved'
                                          AND table_name='${requestedTable}'
                                          AND request_type='create'
                                          AND fields_values = '${newRequestMap}'
                                          AND row_id IS NULL"""

                //check same existing change request
                def sameRequestAvailable = budgetViewDatabaseService.executeQuery(sameCreateRequest)

                if(!sameRequestAvailable){ //insert
                    Map insertedValue = [
                            vendor_id                   :   "${userCustomerId}",
                            table_name                  :   "${requestedTable}",
                            fields_values               :   "${newRequestMap}",
                            status                      :   "pending",
                            request_type                :   "create"
                    ]

                    //selectedTab
                    Integer requestId = budgetViewDatabaseService.insert(insertedValue, tableName)
                    isInsertPostalVal = true

                    changeGeneralAddress(id, version, isInsertPostalVal)
                }
                else{

                    changeGeneralAddress(id, version, isInsertPostalVal)

                }

            }
            else {
                //get existing database records and putting them in map
                String queryPostalAddress = """SELECT postal_address_line1 AS postalAddressLine1,
                                               postal_address_line2 AS postalAddressLine2,
                                               postal_city AS postalCity,
                                               postal_contact_person_name AS postalContactPersonName,
                                               postal_postcode AS postalPostcode,
                                               postal_state AS postalState,
                                               postal_country_id AS postalCountryId,
                                               id
                                        FROM vendor_postal_address
                                        WHERE vendor_id = '${userCustomerId}'"""

                def dbPostalResult = budgetViewDatabaseService.executeQuery(queryPostalAddress)

                Map dbPostalMap = [

                        postal_address_line1        :   "${dbPostalResult[0][0].trim()}",
                        postal_address_line2        :   "${dbPostalResult[0][1].trim()}",
                        postal_city                 :   "${dbPostalResult[0][2].trim()}",
                        //                    postal_contact_person_name  :   "${dbPostalResult[0][3].trim()}",
                        postal_postcode             :   "${dbPostalResult[0][4].trim()}",
                        postal_country_id           :   "${dbPostalResult[0][6]}",

                ] //got values and putted them in  map

                //find differences between requested and existing values
                String changedPostalMap = ApplicationUtil.findDifferenceKeyValue(submittedPostalMap, dbPostalMap)

                if(changedPostalMap) {//insert or reject

                    String sameUpdateRequest = """SELECT *
                                        FROM `vendor_change_request`
                                        WHERE vendor_id ='${userCustomerId}' 
                                          AND status!='approved'
                                          AND table_name='${requestedTable}'
                                          AND request_type='update'
                                          AND fields_values = '${changedPostalMap}'
                                          AND row_id ='${customerPostalId}'"""

                    //check same existing change request
                    def sameRequestAvailable = budgetViewDatabaseService.executeQuery(sameUpdateRequest)

                    if(!sameRequestAvailable){ //insert

                        Map insertedValue = [
                                vendor_id                   :   "${userCustomerId}",
                                table_name                  :   "${requestedTable}",
                                fields_values               :   "${changedPostalMap}",
                                status                      :   "pending",
                                request_type                :   "update",
                                row_id                      :   "${customerPostalId}"
                        ]

                        //selectedTab
                        Integer requestId = budgetViewDatabaseService.insert(insertedValue, tableName)
                        isInsertPostalVal = true

                        changeGeneralAddress(id, version, isInsertPostalVal)

                    } //insert
                    else{ //same request found

                        changeGeneralAddress(id, version, isInsertPostalVal)
                    }//same request found

                }//insert or reject
                else{ //no data changed during request

                    changeGeneralAddress(id, version, isInsertPostalVal)
                }//no data changed during request
            }
        }.invalidToken {
            response.status = 500
            log.warn("User: ${springSecurityService.currentUser.id} possible CSRF or double submit: $params")
            flash.message = "${message(code: 'spring.security.ui.invalid.save.form', args: [params.className])}"
            redirect(action: "userInfoForm")
        }

    }

    def changeGeneralAddress(Long id, Long version, boolean isInsertPostalVal) {

        User user = springSecurityService.currentUser
        def userCustomerId = user.customerId.toString()

        def customerAddressId

        if (!id) {
            customerAddressId = params.officeAddId

        } else {
            customerAddressId = id
        }


        String requestedTable = "vendor_general_address"
        String tableName = "vendor_change_request"

        //requested Changes
        Map submittedAddressMap = [

                address_line1               :       "${params.officeAddLine1.trim()}",
                address_line2               :       "${params.officeAddLine2.trim()}",
                city                        :       "${params.officePostalCity.trim()}",
//                contact_person_name         :       "${params.contactPersonName.trim()}",
                country_id                  :       "${params.officeCountryId}",
                postal_code                 :       "${params.officePostalCode.trim()}"
        ]

        if(!customerAddressId){ //new request while no entry in db

            String newRequestMap = ApplicationUtil.mapToJsonConvert(submittedAddressMap)
            String sameCreateRequest = """SELECT *
                                    FROM `vendor_change_request`
                                    WHERE vendor_id ='${userCustomerId}' 
                                      AND status!='approved'
                                      AND table_name='${requestedTable}'
                                      AND request_type='create'
                                      AND fields_values = '${newRequestMap}'
                                      AND row_id IS NULL"""

            //check same existing change request
            def sameRequestAvailable = budgetViewDatabaseService.executeQuery(sameCreateRequest)

            if(!sameRequestAvailable){ //insert

                Map insertedValue = [
                        vendor_id                   :   "${userCustomerId}",
                        table_name                  :   "${requestedTable}",
                        fields_values               :   "${newRequestMap}",
                        status                      :   "pending",
                        request_type                :   "create"
                ]

                //selectedTab
                Integer requestId = budgetViewDatabaseService.insert(insertedValue, tableName)

                def flashSuccess1 = flash.message = "Uw wijziging is aangevraagd"
                def flashSuccess2 = flash.message = "na controle en goedkeuring door NL Credit Service ontvangt u een e-mail ter bevestiging"

                redirect(action: "userInfoForm", params: [form: 'companyForm', flashSuccess1: flashSuccess1, flashSuccess2: flashSuccess2])
            }
            else{

                def flashSuccess1 = flash.message = "Uw wijziging is aangevraagd"
                def flashSuccess2 = flash.message = "na controle en goedkeuring door NL Credit Service ontvangt u een e-mail ter bevestiging"

                redirect(action: "userInfoForm", params: [form: 'companyForm', flashSuccess1: flashSuccess1, flashSuccess2: flashSuccess2])

            }

        }
        else {
            //get existing database records and putting them in map
            String queryOfficeAddress = """SELECT address_line1,
                                               address_line2,
                                               city,
                                               contact_person_name,
                                               country_id,
                                               postal_code,
                                               state
                                    FROM vendor_general_address
                                    WHERE vendor_id = '${userCustomerId}'"""

            def dbAddressResult = budgetViewDatabaseService.executeQuery(queryOfficeAddress)

            Map dbAddressMap = [
                    address_line1               :       "${dbAddressResult[0][0].trim()}",
                    address_line2               :       "${dbAddressResult[0][1].trim()}",
                    city                        :       "${dbAddressResult[0][2].trim()}",
//                    contact_person_name         :       "${dbAddressResult[0][3].trim()}",
                    country_id                  :       "${dbAddressResult[0][4]}",
                    postal_code                 :       "${dbAddressResult[0][5].trim()}"
            ] //got values and putted them in  map

            //find differences between requested and existing values
            String changedAddressMap = ApplicationUtil.findDifferenceKeyValue(submittedAddressMap, dbAddressMap)

            if(changedAddressMap) {//insert or reject

                String sameUpdateRequest = """SELECT *
                                    FROM `vendor_change_request`
                                    WHERE vendor_id ='${userCustomerId}' 
                                      AND status!='approved'
                                      AND table_name='${requestedTable}'
                                      AND request_type='update'
                                      AND fields_values = '${changedAddressMap}'
                                      AND row_id ='${customerAddressId}'"""

                //check same existing change request
                def sameRequestAvailable = budgetViewDatabaseService.executeQuery(sameUpdateRequest)

                if(!sameRequestAvailable){ //insert

                    Map insertedValue = [
                            vendor_id                   :   "${userCustomerId}",
                            table_name                  :   "${requestedTable}",
                            fields_values               :   "${changedAddressMap}",
                            status                      :   "pending",
                            request_type                :   "update",
                            row_id                      :   "${customerAddressId}"
                    ]

                    //selectedTab
                    Integer requestId = budgetViewDatabaseService.insert(insertedValue, tableName)

                    def flashSuccess1 = flash.message = "Uw wijziging is aangevraagd"
                    def flashSuccess2 = flash.message = "na controle en goedkeuring door NL Credit Service ontvangt u een e-mail ter bevestiging"

                    redirect(action: "userInfoForm", params: [form: 'companyForm', flashSuccess1: flashSuccess1, flashSuccess2: flashSuccess2])
                } //insert
                else{ //same request found

                    def flashSuccess1 = flash.message = "Uw wijziging is aangevraagd"
                    def flashSuccess2 = flash.message = "na controle en goedkeuring door NL Credit Service ontvangt u een e-mail ter bevestiging"

                    redirect(action: "userInfoForm", params: [form: 'companyForm', flashSuccess1: flashSuccess1, flashSuccess2: flashSuccess2])

                }//same request found

            } else if (isInsertPostalVal.equals(true)) {

                def flashSuccess1 = flash.message = "Uw wijziging is aangevraagd"
                def flashSuccess2 = flash.message = "na controle en goedkeuring door NL Credit Service ontvangt u een e-mail ter bevestiging"

                redirect(action: "userInfoForm", params: [form: 'companyForm', flashSuccess1: flashSuccess1, flashSuccess2: flashSuccess2])
            }
            //insert or reject
            else{ //no data changed during request
                def flashError1 = flash.message = "De oranje velden zijn niet correct/incompleet ingevuld"
                def flashError2 = flash.message = "Vul de juiste gegevens in."

                redirect(action: "userInfoForm", params: [form: 'companyForm', flashError1: flashError1, flashError2: flashError2])

            }//no data changed during request
        }

    }

    def changeBankAccountInformation(Long id, Long version){

        User user = springSecurityService.currentUser
        def userCustomerId = user.customerId.toString()

        withForm {

            def customerBankAccId

            if (!id) {
                customerBankAccId = Integer.parseInt(params.bankNo)
            } else {
                customerBankAccId = id
            }

            String requestedTable = "vendor_bank_account"
            String tableName = "vendor_change_request"

            Map submittedBAccMap = [
                    bank_account_name   :   "${params.bankAccName.trim()}",
                    bank_account_no     :   "${params.bankAccNo.trim()}",
                    iban_prefix         :   "${params.iban.trim()}",
                    status              :   "${params.status}" ? "${params.status}" : "1"
            ]

            if(!customerBankAccId){ //new request while no entry in db

                String newRequestMap = ApplicationUtil.mapToJsonConvert(submittedBAccMap)
                String sameCreateRequest = """SELECT *
                                        FROM `vendor_change_request`
                                        WHERE vendor_id ='${userCustomerId}' 
                                          AND status!='approved'
                                          AND table_name='${requestedTable}'
                                          AND request_type='create'
                                          AND fields_values = '${newRequestMap}'
                                          AND row_id IS NULL"""

                //check same existing change request
                def sameRequestAvailable = budgetViewDatabaseService.executeQuery(sameCreateRequest)

                if(!sameRequestAvailable){ //insert
                    Map insertedValue = [
                            vendor_id                   :   "${userCustomerId}",
                            table_name                  :   "${requestedTable}",
                            fields_values               :   "${newRequestMap}",
                            status                      :   "pending",
                            request_type                :   "create"
                    ]
                    //selectedTab
                    Integer requestId = budgetViewDatabaseService.insert(insertedValue, tableName)

                    //                def flash = flash.message = message(code: 'bv.change.request.message', args: [message(code: 'vendorBankAccount.label', default: 'Bank Account')])
                    def flashSuccess1 = flash.message = "Uw wijziging is aangevraagd"
                    def flashSuccess2 = flash.message = "na controle en goedkeuring door NL Credit Service ontvangt u een e-mail ter bevestiging"

                    redirect(action: "userInfoForm", params: [form: 'bankForm', flashSuccess1: flashSuccess1, flashSuccess2: flashSuccess2])
                }
                else{

                    def flashSuccess1 = flash.message = "Uw wijziging is aangevraagd"
                    def flashSuccess2 = flash.message = "na controle en goedkeuring door NL Credit Service ontvangt u een e-mail ter bevestiging"
                    redirect(action: "userInfoForm", params: [form: 'bankForm', flashSuccess1: flashSuccess1, flashSuccess2: flashSuccess2])

                }

            }
            else {
                //get existing database records and putting them in map
                String queryBAccAddress = """SELECT bank_account_name,
                                                       bank_account_no,
                                                       iban_prefix,
                                                       STATUS,
                                                       id
                                                FROM vendor_bank_account
                                                WHERE id = '${customerBankAccId}'
                                                  AND vendor_id = '${userCustomerId}'"""

                def dbBAccResult = budgetViewDatabaseService.executeQuery(queryBAccAddress)

                Map dbBAccMap = [

                        bank_account_name           :   "${dbBAccResult[0][0].trim()}",
                        bank_account_no             :   "${dbBAccResult[0][1].trim()}",
                        iban_prefix                 :   "${dbBAccResult[0][2].trim()}",
                        status                      :   "${dbBAccResult[0][3]}"

                ] //got values and putted them in  map

                //find differences between requested and existing values
                String changedBAccMap = ApplicationUtil.findDifferenceKeyValue(submittedBAccMap, dbBAccMap)

                if(changedBAccMap) {//insert or reject

                    String sameUpdateRequest = """SELECT *
                                        FROM `vendor_change_request`
                                        WHERE vendor_id ='${userCustomerId}' 
                                          AND status!='approved'
                                          AND table_name='${requestedTable}'
                                          AND request_type='update'
                                          AND fields_values = '${changedBAccMap}'
                                          AND row_id ='${customerBankAccId}'"""

                    //check same existing change request
                    def sameRequestAvailable = budgetViewDatabaseService.executeQuery(sameUpdateRequest)

                    if(!sameRequestAvailable){ //insert
                        Map insertedValue = [
                                vendor_id                   :   "${userCustomerId}",
                                table_name                  :   "${requestedTable}",
                                fields_values               :   "${changedBAccMap}",
                                status                      :   "pending",
                                request_type                :   "update",
                                row_id                      :   "${customerBankAccId}",
                        ]

                        //selectedTab
                        Integer requestId = budgetViewDatabaseService.insert(insertedValue, tableName)

                        def flashSuccess1 = flash.message = "Uw wijziging is aangevraagd"
                        def flashSuccess2 = flash.message = "na controle en goedkeuring door NL Credit Service ontvangt u een e-mail ter bevestiging"

                        redirect(action: "userInfoForm", params: [form: 'bankForm', flashSuccess1: flashSuccess1, flashSuccess2: flashSuccess2])

                    } //insert
                    else{ //same request found
                        def flashSuccess1 = flash.message = "Uw wijziging is aangevraagd"
                        def flashSuccess2 = flash.message = "na controle en goedkeuring door NL Credit Service ontvangt u een e-mail ter bevestiging"

                        redirect(action: "userInfoForm", params: [form: 'bankForm', flashSuccess1: flashSuccess1, flashSuccess2: flashSuccess2])
                    }//same request found

                }//insert or reject
                else{ //no data changed during request
                    def flashError1 = flash.message = "De oranje velden zijn niet correct/incompleet ingevuld"
                    def flashError2 = flash.message = "Vul de juiste gegevens in."

                    redirect(action: "userInfoForm", params: [form: 'bankForm', flashError1: flashError1, flashError2: flashError2])
                }//no data changed during request
            }
        }.invalidToken {
            response.status = 500
            log.warn("User: ${springSecurityService.currentUser.id} possible CSRF or double submit: $params")
            flash.message = "${message(code: 'spring.security.ui.invalid.save.form', args: [params.className])}"
            redirect(action: "userInfoForm")
        }
    }

    def changeCustomerInfo () {

        User user = springSecurityService.currentUser
        def userCustomerId = user.customerId.toString()

        def customerId = params.customerId

        withForm {

            String requestedTable = "vendor_master"
            String tableName = "vendor_change_request"

            def lastNameParams = params.lastName.trim()
            def lastNameList = lastNameParams.split(" ")
            def middleName
            def lastName

            if (lastNameList.size() > 2) {
                middleName = lastNameList[0]
                lastName = lastNameList[2]

            } else {

                middleName = ""
                lastName = lastNameParams
            }

            if (params.phoneNumber) {

                changePhoneNo()
            }

            //requested Changes
            Map submittedAddressMap = [

                    first_name    : "${params.firstName.trim()}",
                    middle_name   : middleName,
                    last_name     : lastName,
                    email         : "${params.businessMail}",
                    optional_email: "${params.privateMail.trim()}"
            ]

            if (!customerId) { //new request while no entry in db

                String newRequestMap = ApplicationUtil.mapToJsonConvert(submittedAddressMap)
                String sameCreateRequest = """SELECT *
                                       FROM `vendor_change_request`
                                       WHERE vendor_id ='${userCustomerId}' 
                                       AND status!='approved'
                                       AND table_name='${requestedTable}'
                                       AND request_type='create'
                                       AND fields_values = '${newRequestMap}'
                                       AND row_id IS NULL"""

                //check same existing change request
                def sameRequestAvailable = budgetViewDatabaseService.executeQuery(sameCreateRequest)

                if (!sameRequestAvailable) { //insert

                    Map insertedValue = [
                            vendor_id    : "${userCustomerId}",
                            table_name   : "${requestedTable}",
                            fields_values: "${newRequestMap}",
                            status       : "pending",
                            request_type : "create"
                    ]

                    //selectedTab
                    Integer requestId = budgetViewDatabaseService.insert(insertedValue, tableName)

                    def flashSuccess1 = flash.message = "Uw wijziging is aangevraagd"
                    def flashSuccess2 = flash.message = "na controle en goedkeuring door NL Credit Service ontvangt u een e-mail ter bevestiging"

                    redirect(action: "userInfoForm", params: [form: 'personalForm', flashSuccess1: flashSuccess1, flashSuccess2: flashSuccess2])
                } else {

                    def flashSuccess1 = flash.message = "Uw wijziging is aangevraagd"
                    def flashSuccess2 = flash.message = "na controle en goedkeuring door NL Credit Service ontvangt u een e-mail ter bevestiging"

                    redirect(action: "userInfoForm", params: [form: 'personalForm', flashSuccess1: flashSuccess1, flashSuccess2: flashSuccess2])

                }

            } else {
                //get existing database records and putting them in map
                String queryOfficeAddress = """SELECT first_name,
                                               middle_name,
                                               last_name,
                                               email,
                                               optional_email
                                         FROM vendor_master
                                         WHERE id = ${customerId}"""

                def dbAddressResult = budgetViewDatabaseService.executeQuery(queryOfficeAddress)

                Map dbAddressMap = [
                        first_name    : "${dbAddressResult[0][0]}",
                        middle_name   : "${dbAddressResult[0][1]}",
                        last_name     : "${dbAddressResult[0][2]}",
                        email         : "${dbAddressResult[0][3]}",
                        optional_email: "${dbAddressResult[0][4]}"
                ] //got values and putted them in  map

                //find differences between requested and existing values
                String changedAddressMap = ApplicationUtil.findDifferenceKeyValue(submittedAddressMap, dbAddressMap)

                if (changedAddressMap) {//insert or reject

                    String sameUpdateRequest = """SELECT *
                                    FROM `vendor_change_request`
                                    WHERE vendor_id ='${userCustomerId}' 
                                      AND status!='approved'
                                      AND table_name='${requestedTable}'
                                      AND request_type='update'
                                      AND fields_values = '${changedAddressMap}'
                                      AND row_id ='${customerId}'"""

                    //check same existing change request
                    def sameRequestAvailable = budgetViewDatabaseService.executeQuery(sameUpdateRequest)

                    if (!sameRequestAvailable) { //insert

                        Map insertedValue = [
                                vendor_id    : "${userCustomerId}",
                                table_name   : "${requestedTable}",
                                fields_values: "${changedAddressMap}",
                                status       : "pending",
                                request_type : "update",
                                row_id       : "${customerId}"
                        ]

                        //selectedTab
                        Integer requestId = budgetViewDatabaseService.insert(insertedValue, tableName)

                        def flashSuccess1 = flash.message = "Uw wijziging is aangevraagd"
                        def flashSuccess2 = flash.message = "na controle en goedkeuring door NL Credit Service ontvangt u een e-mail ter bevestiging"

                        redirect(action: "userInfoForm", params: [form: 'personalForm', flashSuccess1: flashSuccess1, flashSuccess2: flashSuccess2])
                    } //insert
                    else { //same request found

                        def flashSuccess1 = flash.message = "Uw wijziging is aangevraagd"
                        def flashSuccess2 = flash.message = "na controle en goedkeuring door NL Credit Service ontvangt u een e-mail ter bevestiging"

                        redirect(action: "userInfoForm", params: [form: 'personalForm', flashSuccess1: flashSuccess1, flashSuccess2: flashSuccess2])

                    }//same request found

                }//insert or reject
                else { //no data changed during request
                    def flashError1 = flash.message = "De oranje velden zijn niet correct/incompleet ingevuld"
                    def flashError2 = flash.message = "Vul de juiste gegevens in."

                    redirect(action: "userInfoForm", params: [form: 'personalForm', flashError1: flashError1, flashError2: flashError2])

                }//no data changed during request
            }
        }.invalidToken {
            response.status = 500
            log.warn("User: ${springSecurityService.currentUser.id} possible CSRF or double submit: $params")
            flash.message = "${message(code: 'spring.security.ui.invalid.save.form', args: [params.className])}"
            redirect(action: "userInfoForm")
        }

    }

    def changePhoneNo () {

        User user = springSecurityService.currentUser
        def userCustomerId = user.customerId.toString()

        def officeAddressId = Integer.parseInt(params.officeAddId)

        String requestedTable = "vendor_general_address"
        String tableName = "vendor_change_request"

        //requested Changes
        Map submittedAddressMap = [

                phone_no             :       "${params.phoneNumber.trim()}",
        ]

        if(!officeAddressId){ //new request while no entry in db

            String newRequestMap = ApplicationUtil.mapToJsonConvert(submittedAddressMap)
            String sameCreateRequest = """SELECT *
                                    FROM `vendor_change_request`
                                    WHERE vendor_id ='${userCustomerId}' 
                                      AND status!='approved'
                                      AND table_name='${requestedTable}'
                                      AND request_type='create'
                                      AND fields_values = '${newRequestMap}'
                                      AND row_id IS NULL"""

            //check same existing change request
            def sameRequestAvailable = budgetViewDatabaseService.executeQuery(sameCreateRequest)

            if(!sameRequestAvailable){ //insert

                Map insertedValue = [
                        vendor_id                   :   "${userCustomerId}",
                        table_name                  :   "${requestedTable}",
                        fields_values               :   "${newRequestMap}",
                        status                      :   "pending",
                        request_type                :   "create"
                ]

                //selectedTab
                Integer requestId = budgetViewDatabaseService.insert(insertedValue, tableName)

            }

        }

        else {
            //get existing database records and putting them in map
            String queryOfficeAddress = """SELECT phone_no                                                                                     
                                    FROM vendor_general_address
                                    WHERE vendor_id = '${userCustomerId}'"""

            def dbAddressResult = budgetViewDatabaseService.executeQuery(queryOfficeAddress)

            Map dbAddressMap = [
                    phone_no               :       "${dbAddressResult[0][0]}"

            ] //got values and putted them in  map

            //find differences between requested and existing values
            String changedAddressMap = ApplicationUtil.findDifferenceKeyValue(submittedAddressMap, dbAddressMap)

            if(changedAddressMap) {//insert or reject

                String sameUpdateRequest = """SELECT *
                                    FROM `vendor_change_request`
                                    WHERE vendor_id ='${userCustomerId}' 
                                      AND status!='approved'
                                      AND table_name='${requestedTable}'
                                      AND request_type='update'
                                      AND fields_values = '${changedAddressMap}'
                                      AND row_id ='${officeAddressId}'"""

                //check same existing change request
                def sameRequestAvailable = budgetViewDatabaseService.executeQuery(sameUpdateRequest)

                if(!sameRequestAvailable){ //insert

                    Map insertedValue = [
                            vendor_id                   :   "${userCustomerId}",
                            table_name                  :   "${requestedTable}",
                            fields_values               :   "${changedAddressMap}",
                            status                      :   "pending",
                            request_type                :   "update",
                            row_id                      :   "${officeAddressId}"
                    ]
                    //selectedTab
                    Integer requestId = budgetViewDatabaseService.insert(insertedValue, tableName)

                } //insert

            }//insert or reject

        }

    }

    def changeLoginInfo () {

        User user = springSecurityService.currentUser
        def userCustomerId = user.customerId.toString()

//        def officeAddressId = Integer.parseInt(params.officeAddId)

        withForm {

            String requestedTable = "user"
            String tableName = "vendor_change_request"

            def currentPass
            def currentPassHas
            if (params.currentPassword){
                currentPass = params.currentPassword
                currentPassHas = springSecurityService.encodePassword(params.currentPassword)
            }

            def userPass = user.password
            if (userPass == currentPassHas) {

                def checkPassValid = new ExtraSettingUtil().is_Valid_Password(params.password)

                if (checkPassValid) {

                    if (params.password == params.rePassword) {

                        Map submittedAddressMap = [

                                email             :       "${params.email.trim()}",
                                password          :       "${params.password.trim()}"
                        ]

                        //new request while no entry in db

                        String newRequestMap = ApplicationUtil.mapToJsonConvert(submittedAddressMap)

                        String sameCreateRequest = """SELECT *
                                        FROM `vendor_change_request`
                                        WHERE vendor_id ='${userCustomerId}' 
                                          AND status!='approved'
                                          AND table_name='${requestedTable}'
                                          AND request_type='create'
                                          AND fields_values = '${newRequestMap}'
                                          AND row_id IS NULL"""

                        //check same existing change request
                        def sameRequestAvailable = budgetViewDatabaseService.executeQuery(sameCreateRequest)

                        if(!sameRequestAvailable){ //insert

                            Map insertedValue = [
                                    vendor_id                   :   "${userCustomerId}",
                                    table_name                  :   "${requestedTable}",
                                    fields_values               :   "${newRequestMap}",
                                    status                      :   "pending",
                                    request_type                :   "update",
                                    user_id                     :   user.id
                            ]

                            //selectedTab
                            Integer requestId = budgetViewDatabaseService.insert(insertedValue, tableName)

                            def flashSuccess1 = flash.message = "Uw wijziging is aangevraagd"
                            def flashSuccess2 = flash.message = "na controle en goedkeuring door NL Credit Service ontvangt u een e-mail ter bevestiging"

                            redirect(action: "userInfoForm", params: [form: 'loginForm', flashSuccess1: flashSuccess1, flashSuccess2: flashSuccess2])

                        } else {

                            def flashError1 = flash.message = "De oranje velden zijn niet correct/incompleet ingevuld"
                            def flashError2 = flash.message = "Vul de juiste gegevens in."

                            redirect(action: "userInfoForm", params: [form: 'loginForm', flashError1: flashError1, flashError2: flashError2])
                        }

                    } else {
                        def flashError1 = flash.message = "New passwords are match"
                        def flashError2 = flash.message = "please provide correct current password."

                        redirect(action: "userInfoForm", params: [form: 'loginForm', flashError1: flashError1, flashError2: flashError2])
                    }

                } else {
                    def flashError1 = flash.message = "Invalid password"
                    def flashError2 = flash.message = "password must minimum 8 characters and minimum one number "

                    redirect(action: "userInfoForm", params: [form: 'loginForm', flashError1: flashError1, flashError2: flashError2])
                }

            } else {

                def flashError1 = flash.message = "Current password is incorrect"
                def flashError2 = flash.message = "please provide correct current password."

                redirect(action: "userInfoForm", params: [form: 'loginForm', flashError1: flashError1, flashError2: flashError2])
            }

            //requested Changes

        }.invalidToken {
            response.status = 500
            log.warn("User: ${springSecurityService.currentUser.id} possible CSRF or double submit: $params")
            flash.message = "${message(code: 'spring.security.ui.invalid.save.form', args: [params.className])}"
            redirect(action: "userInfoForm")
        }

    }

    def customerCreditLimitInfo () {

    }

    def getCustomerCreditLimitsInfo () {

        String gridOutput

        invoiceUtil = new InvoiceUtil()
        int start = 0
        LinkedHashMap gridResult
        gridResult = vendorMasterService.customerCreditLimitsInfo()

        List debtorCreditLimitList = invoiceUtil.customerCreditLimitWrapEntry(gridResult.customerCreditLimitInfo)

        LinkedHashMap result = [draw: 1, data : debtorCreditLimitList.cell]
        gridOutput = result as JSON

        render gridOutput
    }

    def portalList () {

        def user
        def currentUser = springSecurityService.currentUser.id
        Long userId
        if (params.userId) {
            userId = Long.parseLong(params.userId)
        }
        if (currentUser && userId) {
            if (userId.equals(currentUser)) {
                user = User.findById(userId)
                session.customerPortalMail = user.email
            }
        }
    }

    def userInfoForm () {

        User user = springSecurityService.currentUser
        def userCustomerId = user.customerId

        if (userCustomerId) {
            def customerMasterArr = budgetViewDatabaseService.executeQuery("""select * from vendor_master vm 
                                WHERE vm.id = ${userCustomerId}""")
            def customerGeneralAddressArr = budgetViewDatabaseService.executeQuery("""select * from 
                                vendor_general_address vga WHERE vga.vendor_id = ${userCustomerId}""")
            def customerPostalAddressArr = budgetViewDatabaseService.executeQuery("""select * from
                                vendor_postal_address vpa WHERE vpa.vendor_id = ${userCustomerId}""")
            def customerBankAccArr = budgetViewDatabaseService.executeQuery("""select * from vendor_bank_account vba WHERE 
                                vba.vendor_id = ${userCustomerId} AND vba.status = 1 ORDER BY id DESC LIMIT 1""")


            [customerMasterArr: customerMasterArr[0],
             customerGeneralAddressArr: customerGeneralAddressArr[0],
             customerPostalAddressArr: customerPostalAddressArr[0],
             customerBankAccArr: customerBankAccArr[0],
             form: params.form,
             flashSuccess1: params.flashSuccess1,
             flashSuccess2: params.flashSuccess2,
             flashError1: params.flashError1,
             flashError2: params.flashError2]
        }
    }

    def nlServiceNews () {

    }

    def factorInfo () {
        User user = springSecurityService.currentUser
        def userCustomerId = user.customerId

        def vendorFactoringInstance
        def customerMasterArr

        if (userCustomerId) {

            customerMasterArr = budgetViewDatabaseService.executeQuery("""select vm.vendor_name as vendorName,
                                vm.vendor_code as vendorCode from vendor_master vm WHERE vm.id = ${userCustomerId}""")

            vendorFactoringInstance = debtorCustomerService.getCustomerFactoringDataAsMap(userCustomerId)
        }

        [vendorFactoringInstance: vendorFactoringInstance, customerMasterArr: customerMasterArr[0]]

    }

}
