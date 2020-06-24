package bv

import factoring.BudgetViewDatabaseService
import factoring.CoreParamsHelperTagLib
import factoring.CustomerBankAccount
import factoring.CustomerMaster
import factoring.CustomerMasterService
import factoring.DebtorCustomerService
import factoring.ExtraSettingService
import factoring.GridEntity
import factoring.InvoiceUtil
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import org.grails.plugins.web.taglib.ValidationTagLib
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException

import java.text.SimpleDateFormat


@Secured(["hasAnyRole('ROLE_ADMIN', 'ROLE_ACCOUNTANT', 'ROLE_USER')"])
class CustomerMasterController {
    @Autowired
    BudgetViewDatabaseService budgetViewDatabaseService
    DebtorCustomerService debtorCustomerService
    ExtraSettingService extraSettingService
    CustomerMasterService customerMasterService
    InvoiceUtil invoiceUtil
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        params.max = params.max ?: 10
        params.offset = params.offset ?: 0
        params.sort = "id"
        params.order = "desc"
        session.resultCustomerInvInfo = []
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = params.max ?: 10
        params.offset = (params.offset) ? Integer.parseInt(params.offset) : 0
        params.limit = (params.limit) ? Integer.parseInt(params.limit) : 10
        params.sort = "id"
        params.order = "desc"

        def customerPrefix = new CoreParamsHelperTagLib().showGeneratedCustomerCode()

        String select = """a.id AS id,a.version AS version, a.cham_of_commerce AS chamOfCommerce,a.comments AS comments,
                             a.customer_name AS customerName,a.customer_type As customerType,a.customer_code AS customerCode,
                             a.default_gl_account AS defaultGlAccount,a.email As email,a.gender As gender,a.first_name as firstName,
                             a.last_name As lastName, a.middle_name As middleName, a.payment_term_id As paymentTermId,
                             a.company_name As companyName, a.vat As vat,a.status As status,a.credit_limit As creditLimit,
                             a.insuad_amout As insuadAmout,a.credit_status AS creditStatus,a.vat_number As vatNumber"""
        String selectIndex = """id,version,chamOfCommerce,comments,customerName,customerType,customerCode,
                                defaultGlAccount,email,gender,firstName,lastName,middleName,paymentTermId,
                                companyName, vat,status,creditLimit,insuadAmout,creditStatus,vatNumber"""
        String from = "customer_master AS a "
        String where = ""
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

        if (params.id) {

            if (params.bankid) {
                if (params.delbank) {
                    CustomerBankAccount.executeUpdate("Delete FROM CustomerBankAccount WHERE id='" + params.bankid + "'")
                }
            }

            String selectga = """a.id AS id,a.version AS version,a.address_line1 AS addressLine1,a.address_line2 AS addressLine2,a.city AS city, a.contact_person_name AS contactPersonName, a.postal_code As postalCode, a.state As state,a.country_id AS countryId,a.status As status,a.customer_id As customerId,a.phone_no,a.website_address"""
            String selectIndexga = """id,version,addressLine1,addressLine2,city,contactPersonName,postalCode,state,countryId,status,customerId"""
            String fromga = "customer_general_address AS a "
            String wherega = "customer_id='" + params.id + "'"
            String orderByga = "a.id ASC"
            LinkedHashMap gridResultga = budgetViewDatabaseService.select(selectga, fromga, wherega, orderByga, '', 'false', selectIndexga)

            String selectba = """a.id AS id,a.version AS version, a.bank_account_name AS bankAccountName,a.bank_account_no AS bankAccountNo,a.iban_prefix AS ibanPrefix,a.status As status,a.customer_id As customerId"""
            String selectIndexba = """id,version,bankAccountName,bankAccountNo,ibanPrefix,status, customerId"""
            String fromba = "customer_bank_account AS a "
            String whereba = "customer_id='" + params.id + "'"
            String orderByba = "a.id ASC"
            LinkedHashMap gridResultba = budgetViewDatabaseService.select(selectba, fromba, whereba, orderByba, '', 'false', selectIndexba)

            String selectbel = """a.id AS id,a.version AS version,a.bank_account_name AS bankAccountName,a.bank_account_no AS bankAccountNo,a.iban_prefix AS ibanPrefix,a.status As status,a.customer_id As customerId"""
            String selectIndexbel = """id,version,bankAccountName,bankAccountNo,ibanPrefix,status,customerId"""
            String frombel = "customer_bank_account AS a "
            String wherebel = "id='" + params.bankid + "'"
            String orderBybel = "a.id ASC"
            LinkedHashMap gridResultbel = budgetViewDatabaseService.select(selectbel, frombel, wherebel, orderBybel, '', 'false', selectIndexbel)

            [customerMasterInstance : gridResultInstance['dataGridList'][0],customerBankAccountInstance  : gridResultba['dataGridList'],customerGeneralAddressInstance: gridResultga['dataGridList'][0],
             customerEditBankAccountInstance: gridResultbel['dataGridList'][0],customerPrefix : customerPrefix]
        } else {
            select = """a.id AS id,a.version AS version, a.cham_of_commerce AS chamOfCommerce,a.comments AS comments,
                             a.customer_name AS customerName,a.customer_type As customerType,a.customer_code AS customerCode,
                             a.default_gl_account AS defaultGlAccount,a.email As email,a.gender As gender,a.first_name as firstName,
                             a.last_name As lastName, a.middle_name As middleName, a.payment_term_id As paymentTermId,
                             a.company_name As companyName, a.vat As vat,a.status As status,a.credit_limit As creditLimit,
                             a.insuad_amout As insuadAmout,a.credit_status AS creditStatus,a.vat_number As vatNumber"""
            selectIndex = """id,version,chamOfCommerce,comments,customerName,customerType,customerCode,
                                defaultGlAccount,email,gender,firstName,lastName,middleName,paymentTermId,
                                companyName, vat,status,creditLimit,insuadAmout,creditStatus,vatNumber"""
            from = "customer_master AS a "
            where = ""
            orderBy = ""

            if (params.sort && params.order) {
                orderBy = "a.${params.sort} ${params.order}"
            } else {
                orderBy = "a.id ASC"
            }
            LinkedHashMap gridResult = budgetViewDatabaseService.select(select, from, where, orderBy, '', 'false', selectIndex, params.limit, params.offset)
            def totalCount = new BudgetViewDatabaseService().executeQuery("SELECT count(a.id)  FROM customer_master AS a WHERE status =1")
            [customerMasterInstanceList: gridResult['dataGridList'], customerMasterInstanceTotal: totalCount[0][0], customerMasterInstance: gridResultInstance['dataGridList'][0], customerPrefix: customerPrefix]
        }

    }


    def listBackupCode(Integer max) {
        params.max = params.max ?: 10
        params.offset = (params.offset) ? Integer.parseInt(params.offset) : 0
        params.limit = (params.limit) ? Integer.parseInt(params.limit) : 10
        params.sort = "id"
        params.order = "desc"

        def customerPrefix = new CoreParamsHelperTagLib().showGeneratedCustomerCode()

        String select = """a.id AS id,a.version AS version, a.cham_of_commerce AS chamOfCommerce,a.comments AS comments,
                             a.customer_name AS customerName,a.customer_type As customerType,a.customer_code AS customerCode,
                             a.default_gl_account AS defaultGlAccount,a.email As email,a.gender As gender,a.first_name as firstName,
                             a.last_name As lastName, a.middle_name As middleName, a.payment_term_id As paymentTermId,
                             a.company_name As companyName, a.vat As vat,a.status As status,a.credit_limit As creditLimit,
                             a.insuad_amout As insuadAmout,a.credit_status AS creditStatus,a.vat_number As vatNumber"""
        String selectIndex = """id,version,chamOfCommerce,comments,customerName,customerType,customerCode,
                                defaultGlAccount,email,gender,firstName,lastName,middleName,paymentTermId,
                                companyName, vat,status,creditLimit,insuadAmout,creditStatus,vatNumber"""
        String from = "customer_master AS a "
        String where = ""
        String orderBy = ""

        if (params.sort && params.order) {
            orderBy = "a.${params.sort} ${params.order}"
        } else {
            orderBy = "a.id ASC"
        }
        LinkedHashMap gridResult = budgetViewDatabaseService.select(select, from, where, orderBy, '', 'false', selectIndex, params.limit, params.offset)

        //print("gridResult--------" + gridResult)
        String whereInstance = ""
        if (params.id) {
            whereInstance = "a.id=${params.id}"
        } else {
            whereInstance = "a.id=0"
        }

        LinkedHashMap gridResultInstance = budgetViewDatabaseService.select(select, from, whereInstance, orderBy, '', 'false', selectIndex)
        def totalCount = new BudgetViewDatabaseService().executeQuery("SELECT count(a.id)  FROM customer_master AS a WHERE status =1")

        //print("totalCount--------" + totalCount)
        if (params.id) {
            String selectga = """a.id AS id,a.version AS version,a.address_line1 AS addressLine1,a.address_line2 AS addressLine2,a.city AS city, a.contact_person_name AS contactPersonName, a.postal_code As postalCode, a.state As state,a.country_id AS countryId,a.status As status,a.customer_id As customerId,a.phone_no,a.website_address"""
            String selectIndexga = """id,version,addressLine1,addressLine2,city,contactPersonName,postalCode,state,countryId,status,customerId"""
            String fromga = "customer_general_address AS a "
            String wherega = "customer_id='" + params.id + "'"
            String orderByga = "a.id ASC"
            LinkedHashMap gridResultga = budgetViewDatabaseService.select(selectga, fromga, wherega, orderByga, '', 'false', selectIndexga)

            String selectpa = """a.id AS id,a.version AS version,a.postal_address_line1 AS postalAddressLine1,a.postal_address_line2 AS postalAddressLine2, a.postal_city AS postalCity,a.postal_contact_person_name AS postalContactPersonName,a.postal_postcode AS postalPostcode,a.postal_state As postalState,a.postal_country_id AS postalCountryId,a.status As status,a.customer_id As customerId"""
            String selectIndexpa = """id,version,postalAddressLine1,postalAddressLine2,postalCity,postalContactPersonName,postalPostcode,postalState,postalCountryId,status,customerId"""
            String frompa = "customer_postal_address AS a "
            String wherepa = "customer_id='" + params.id + "'"
            String orderBypa = "a.id ASC"
            LinkedHashMap gridResultpa = budgetViewDatabaseService.select(selectpa, frompa, wherepa, orderBypa, '', 'false', selectIndexpa)

            String selectsa = """a.id AS id,a.version AS version, a.customer_id AS customerId,a.note AS note,a.ship_add_line1 AS shipAddLine1, a.ship_add_line2 AS shipAddLine2,a.ship_city AS shipCity,a.ship_contact_name AS shipContactName,a.ship_country_id AS shipCountryId,a.ship_email As shipEmail,a.ship_fax As shipFax,a.ship_phone_no1 As shipPhoneNo1,a.ship_phone_no2 As shipPhoneNo2, a.ship_post_code As shipPostCode, a.ship_state AS shipState, a.ship_website AS shipWebsite, a.status As status"""
            String selectIndexsa = """id,version,customerId,note,shipAddLine1,shipAddLine2,shipCity,shipContactName,shipCountryId,shipEmail,shipFax,shipPhoneNo1,shipPhoneNo2,shipPostCode,shipState,shipWebsite,status"""
            String fromsa = "customer_shipment_address AS a "
            String wheresa = "customer_id='" + params.id + "'"
            String orderBysa = "a.id ASC"
            LinkedHashMap gridResultsa = budgetViewDatabaseService.select(selectsa, fromsa, wheresa, orderBysa, '', 'false', selectIndexsa)

            if (params.bankid) {
                if (params.delbank) {
                    CustomerBankAccount.executeUpdate("Delete FROM CustomerBankAccount WHERE id='" + params.bankid + "'")
                }
            }

            String selectba = """a.id AS id,a.version AS version, a.bank_account_name AS bankAccountName,a.bank_account_no AS bankAccountNo,a.iban_prefix AS ibanPrefix,a.status As status,a.customer_id As customerId"""
            String selectIndexba = """id,version,bankAccountName,bankAccountNo,ibanPrefix,status, customerId"""
            String fromba = "customer_bank_account AS a "
            String whereba = "customer_id='" + params.id + "'"
            String orderByba = "a.id ASC"
            LinkedHashMap gridResultba = budgetViewDatabaseService.select(selectba, fromba, whereba, orderByba, '', 'false', selectIndexba)

            String selectbel = """a.id AS id,a.version AS version,a.bank_account_name AS bankAccountName,a.bank_account_no AS bankAccountNo,a.iban_prefix AS ibanPrefix,a.status As status,a.customer_id As customerId"""
            String selectIndexbel = """id,version,bankAccountName,bankAccountNo,ibanPrefix,status,customerId"""
            String frombel = "customer_bank_account AS a "
            String wherebel = "id='" + params.bankid + "'"
            String orderBybel = "a.id ASC"
            LinkedHashMap gridResultbel = budgetViewDatabaseService.select(selectbel, frombel, wherebel, orderBybel, '', 'false', selectIndexbel)

            //Query for invoice and customer info.
            String strQuery = "SELECT CONCAT(sp.prefix,'-',ii.invoice_no) AS invoiceNumber,ii.payment_ref,ii.trans_date,ii.booking_period," +
                    "ROUND((ii.total_gl_amount + ii.total_vat),2) as invAmt," +
                    "ROUND((ii.total_gl_amount + ii.total_vat) - SUM(tm.amount),2) AS paidAmount," +
                    "ROUND(SUM(tm.amount),2) as remainAmount,ii.id,ii.booking_year, ii.due_date as dueDate , MAX(tm.trans_date) as lastPaymentDate " +
                    "FROM system_prefix AS sp,invoice_income AS ii " +
                    "INNER JOIN trans_master AS tm ON tm.recenciliation_code = CONCAT(ii.id,'#1')  AND " +
                    "tm.account_code=(Select debitor_gl_code from debit_credit_gl_setup) " +
                    "WHERE ii.debtor_id = " + params.id +
                    "  AND sp.id=8 GROUP BY tm.recenciliation_code ORDER BY ii.trans_date";

            def resultCustomerInvInfo = new BudgetViewDatabaseService().executeQuery(strQuery)

            if (session.ongoingID != params.id) {
                session.ongoingID = params.id
                session.dummyData = 0
                session.resultCustomerInvInfo = []
//                println("Session:"+session.resultCustomerInvInfo)
            }

            resultCustomerInvInfo.eachWithIndex { Phn, key ->
                Map map = ["invoiceId": 0, "paymentReference": '', "transactionDate": 0, "bookingPeriod": 0, "paidAmount": 0, "dueAmount": 0, "bookingYear": 0]
                map.invoiceId = Phn[7]
                map.paymentReference = Phn[1].toString()
                map.transactionDate = Phn[2].toString()
                map.bookingPeriod = Phn[3]
                map.paidAmount = Phn[5].toString()
                map.dueAmount = Phn[6]
                map.bookingYear = Phn[8]

                session.resultCustomerInvInfo << map
            }

            //For bank transaction details

            strQuery = """SELECT bsidf.bank_payment_id,
                    IF(tm.trans_type=7,  bsidf.trans_bank_account_no, ' ') AS trans_bank_account_no,
                    CONCAT(sp.prefix,'-',ii.invoice_no) AS invoiceNumber,
                    IF(tm.trans_type=7,  bsidf.description, 'Write off by user.') AS description,
                    IF(tm.trans_type=7,  bsidf.trans_date_time, DATE_FORMAT(tm.trans_date,'%y%m%d')) AS trans_date_time,
                    IF(tm.trans_type=7,  bsidf.reconciliated_amount, ROUND((ii.total_gl_amount + ii.total_vat),2)) AS reconAmount
                    FROM system_prefix AS sp,invoice_income AS ii
                    INNER JOIN trans_master AS tm ON tm.recenciliation_code = CONCAT(ii.id,'#1')
                    INNER JOIN bank_statement_import_details_final AS bsidf ON bsidf.bank_payment_id = tm.invoice_no
                    WHERE ii.debtor_id = '${params.id}' AND tm.trans_type IN (7,8) AND sp.id=8
                    GROUP BY bsidf.bank_payment_id ORDER BY ii.trans_date"""

            def bankPaymentInfoDetails = new BudgetViewDatabaseService().executeQuery(strQuery)

            def debtorOutstandingInvoices = budgetViewDatabaseService.executeCustomQuery("SELECT * FROM(SELECT tblCusDebInfo.payment_ref as paymentRefference,tblCusDebInfo.customer_name as debtorName, tblCusDebInfo.vendor_name as customerName,DATE_FORMAT(tblCusDebInfo.trans_date,'%Y-%m-%d') as invoiceDate, tblCusDebInfo.terms as paymentTerms, DATE_FORMAT(tblCusDebInfo.due_date,'%Y-%m-%d') as dueDate,TO_DAYS(tblCusDebInfo.due_date)-TO_DAYS(NOW()) AS numOfDays,ROUND(tblCusDebInfo.invoicAmount,2) as invoiceAmount,if(ISNULL(tblPaidInvoice.invoicePaidAmount),0,ROUND(tblPaidInvoice.invoicePaidAmount,2)) as paidAmount,if(ISNULL(tblPaidInvoice.lastPaymentDate),TO_DAYS(NOW())-TO_DAYS(tblCusDebInfo.trans_date),TO_DAYS(tblPaidInvoice.lastPaymentDate)-TO_DAYS(tblCusDebInfo.trans_date)) AS paidDays, (TO_DAYS(NOW())-TO_DAYS(tblCusDebInfo.trans_date))-180 AS isSixMonths,tblCusDebInfo.creditLimit AS creditLimit, tblCusDebInfo.bebtorId as debtorId, tblCusDebInfo.customerId AS customerId, tblCusDebInfo.invoiceId AS invoiceId,tblPaidInvoice.paymentDescription as paymentDescription FROM(SELECT ii.payment_ref,debInfo.customer_name,cusInfo.vendor_name,ii.trans_date,pmtTerm.terms,ii.due_date,ii.total_gl_amount+ii.total_vat as invoicAmount, ii.id as invoiceId,debInfo.id as bebtorId,cusInfo.id as customerId,debInfo.credit_limit as creditLimit from invoice_income as ii INNER JOIN vendor_master as cusInfo on ii.customer_id = cusInfo.id INNER JOIN customer_master as debInfo on debInfo.id = ii.debtor_id INNER JOIN payment_terms as pmtTerm on ii.terms_id=pmtTerm.id) AS tblCusDebInfo LEFT JOIN (SELECT tblPaidAmount.invoiceId as invoiceId, tblPaidAmount.invoicePaidAmount as invoicePaidAmount, tblPaidAmount.lastPaymentDate as lastPaymentDate, bsf.description as paymentDescription from (SELECT ic.id AS invoiceId, sum(tm.amount*-1) AS invoicePaidAmount,MAX(tm.trans_date ) as lastPaymentDate,tm.invoice_no as invoiceNo FROM invoice_income AS ic INNER JOIN trans_master tm ON tm.recenciliation_code = CONCAT(ic.id,'#1') AND tm.account_code='1300' AND tm.trans_type <> 1 GROUP BY ic.id ORDER BY tm.trans_date) as tblPaidAmount INNER JOIN bank_statement_import_details_final as bsf ON bsf.bank_payment_id=tblPaidAmount.invoiceNo) AS tblPaidInvoice on tblCusDebInfo.invoiceId=tblPaidInvoice.invoiceId) AS tblFinalInfo WHERE tblFinalInfo.invoiceAmount<>tblFinalInfo.paidAmount and tblFinalInfo.debtorId="+params.id)
            def debtorPaidInvoices = budgetViewDatabaseService.executeCustomQuery("SELECT * FROM(SELECT tblCusDebInfo.payment_ref as paymentRefference,tblCusDebInfo.customer_name as debtorName, tblCusDebInfo.vendor_name as customerName,DATE_FORMAT(tblCusDebInfo.trans_date,'%Y-%m-%d') as invoiceDate, tblCusDebInfo.terms as paymentTerms, DATE_FORMAT(tblCusDebInfo.due_date,'%Y-%m-%d') as dueDate,TO_DAYS(tblCusDebInfo.due_date)-TO_DAYS(NOW()) AS numOfDays,ROUND(tblCusDebInfo.invoicAmount,2) as invoiceAmount,if(ISNULL(tblPaidInvoice.invoicePaidAmount),0,ROUND(tblPaidInvoice.invoicePaidAmount,2)) as paidAmount,if(ISNULL(tblPaidInvoice.lastPaymentDate),TO_DAYS(NOW())-TO_DAYS(tblCusDebInfo.trans_date),TO_DAYS(tblPaidInvoice.lastPaymentDate)-TO_DAYS(tblCusDebInfo.trans_date)) AS paidDays, (TO_DAYS(NOW())-TO_DAYS(tblCusDebInfo.trans_date))-180 AS isSixMonths,tblCusDebInfo.creditLimit AS creditLimit, tblCusDebInfo.bebtorId as debtorId, tblCusDebInfo.customerId AS customerId, tblCusDebInfo.invoiceId AS invoiceId,tblPaidInvoice.paymentDescription as paymentDescription FROM(SELECT ii.payment_ref,debInfo.customer_name,cusInfo.vendor_name,ii.trans_date,pmtTerm.terms,ii.due_date,ii.total_gl_amount+ii.total_vat as invoicAmount, ii.id as invoiceId,debInfo.id as bebtorId,cusInfo.id as customerId,debInfo.credit_limit as creditLimit from invoice_income as ii INNER JOIN vendor_master as cusInfo on ii.customer_id = cusInfo.id INNER JOIN customer_master as debInfo on debInfo.id = ii.debtor_id INNER JOIN payment_terms as pmtTerm on ii.terms_id=pmtTerm.id) AS tblCusDebInfo LEFT JOIN (SELECT tblPaidAmount.invoiceId as invoiceId, tblPaidAmount.invoicePaidAmount as invoicePaidAmount, tblPaidAmount.lastPaymentDate as lastPaymentDate, bsf.description as paymentDescription from (SELECT ic.id AS invoiceId, sum(tm.amount*-1) AS invoicePaidAmount,MAX(tm.trans_date ) as lastPaymentDate,tm.invoice_no as invoiceNo FROM invoice_income AS ic INNER JOIN trans_master tm ON tm.recenciliation_code = CONCAT(ic.id,'#1') AND tm.account_code='1300' AND tm.trans_type <> 1 GROUP BY ic.id ORDER BY tm.trans_date) as tblPaidAmount INNER JOIN bank_statement_import_details_final as bsf ON bsf.bank_payment_id=tblPaidAmount.invoiceNo) AS tblPaidInvoice on tblCusDebInfo.invoiceId=tblPaidInvoice.invoiceId) AS tblFinalInfo WHERE tblFinalInfo.invoiceAmount=tblFinalInfo.paidAmount and tblFinalInfo.debtorId="+params.id)

            [debtorOutstandingInvoices:debtorOutstandingInvoices,debtorPaidInvoices:debtorPaidInvoices,customerMasterInstance       : gridResultInstance['dataGridList'][0], customerMasterInstanceList: gridResult['dataGridList'],
             customerMasterInstanceTotal  : totalCount[0][0], customerGeneralAddressInstance: gridResultga['dataGridList'][0],
             customerPostalAddressInstance: gridResultpa['dataGridList'][0], customerShipmentAddressInstance: gridResultsa['dataGridList'][0],
             customerBankAccountInstance  : gridResultba['dataGridList'], customerEditBankAccountInstance: gridResultbel['dataGridList'][0],
             customerPrefix               : customerPrefix, invoiceCustomerDetailsInstance: resultCustomerInvInfo, bankPaymentInfoDetailsInstance: bankPaymentInfoDetails]
        } else {
            [customerMasterInstanceList: gridResult['dataGridList'], customerMasterInstanceTotal: totalCount[0][0], customerMasterInstance: gridResultInstance['dataGridList'][0], customerPrefix: customerPrefix]
        }

    }

    def loadGeneralAddress(){
        def debtorId = request.getParameter("debtorId")
        String selectga = """a.id AS id,a.version AS version,a.address_line1 AS addressLine1,a.address_line2 AS addressLine2,a.city AS city, a.contact_person_name AS contactPersonName, a.postal_code As postalCode, a.state As state,a.country_id AS countryId,a.status As status,a.customer_id As customerId,a.phone_no,a.website_address"""
        String selectIndexga = """id,version,addressLine1,addressLine2,city,contactPersonName,postalCode,state,countryId,status,customerId"""
        String fromga = "customer_general_address AS a "
        String wherega = "customer_id='" + debtorId + "'"
        String orderByga = "a.id ASC"
        LinkedHashMap gridResultga = budgetViewDatabaseService.select(selectga, fromga, wherega, orderByga, '', 'false', selectIndexga)
        [customerGeneralAddressInstance: gridResultga['dataGridList'][0],customerId:debtorId]
    }

    def loadPostalAddress(){
        def debtorId = request.getParameter("debtorId")

        String selectpa = """a.id AS id,a.version AS version,a.postal_address_line1 AS postalAddressLine1,a.postal_address_line2 AS postalAddressLine2, a.postal_city AS postalCity,a.postal_contact_person_name AS postalContactPersonName,a.postal_postcode AS postalPostcode,a.postal_state As postalState,a.postal_country_id AS postalCountryId,a.status As status,a.customer_id As customerId"""
        String selectIndexpa = """id,version,postalAddressLine1,postalAddressLine2,postalCity,postalContactPersonName,postalPostcode,postalState,postalCountryId,status,customerId"""
        String frompa = "customer_postal_address AS a "
        String wherepa = "customer_id='" + debtorId + "'"
        String orderBypa = "a.id ASC"
        LinkedHashMap gridResultpa = budgetViewDatabaseService.select(selectpa, frompa, wherepa, orderBypa, '', 'false', selectIndexpa)

        [customerPostalAddressInstance: gridResultpa['dataGridList'][0],customerId:debtorId]
    }

    def loadPaidInvoices(){
        def debtorId = request.getParameter("debtorId")
        def creditLimit = budgetViewDatabaseService.executeCustomQuery("SELECT cm.credit_limit AS creditLimit FROM customer_master AS cm WHERE cm.id="+debtorId)
        if(creditLimit)
            creditLimit = creditLimit[0][0]
        def debtorPaidInvoices = budgetViewDatabaseService.executeCustomQuery("""SELECT *
                                                                                        FROM   (SELECT ic.payment_ref
                                                                                                              AS paymentRefference,
                                                                                                       vm.vendor_name
                                                                                                              AS customerName,
                                                                                                       DATE_FORMAT(ic.trans_date, '%d-%m-%Y')
                                                                                                              AS invoiceDate,
                                                                                                       TO_DAYS(ic.trans_date) - TO_DAYS(NOW())
                                                                                                              AS daysToPay,
                                                                                                       IF(ISNULL(tblPaidInvoices.lastpaymentdate), TO_DAYS(NOW()) -
                                                                                                       TO_DAYS(ic.trans_date),
                                                                                                       TO_DAYS(
                                                                                                       tblPaidInvoices.lastpaymentdate)
                                                                                                       - TO_DAYS(ic.trans_date))
                                                                                                              AS paymentRealized,
                                                                                                       ( TO_DAYS(NOW()) - TO_DAYS(ic.trans_date) ) - 180
                                                                                                              AS isSixMonths,
                                                                                                       pt.terms
                                                                                                              AS paymentTerms,
                                                                                                       ROUND(ic.total_gl_amount + ic.total_vat, 2)
                                                                                                              AS invoiceAmount,
                                                                                                       IF(ISNULL(tblPaidInvoices.invoicepaidamount), ROUND(
                                                                                                       ic.total_gl_amount + ic.total_vat, 2),
                                                                                                       ROUND(( ic.total_gl_amount + ic.total_vat ) -
                                                                                                             tblPaidInvoices.invoicepaidamount, 2)) AS dueAmount,
                                                                                                       IF(ISNULL(tblPaidInvoices.invoicepaidamount), 0, ROUND(
                                                                                                       tblPaidInvoices.invoicepaidamount, 2))
                                                                                                              AS paidAmount,
                                                                                                       vm.credit_limit
                                                                                                              AS creditLimit,
                                                                                                       ic.customer_id
                                                                                                              AS customerId,
                                                                                                       ic.debtor_id
                                                                                                              AS debtorId
                                                                                                FROM   invoice_income AS ic
                                                                                                       INNER JOIN vendor_master AS vm
                                                                                                               ON ic.customer_id = vm.id
                                                                                                       INNER JOIN payment_terms AS pt
                                                                                                               ON pt.id = ic.terms_id
                                                                                                       LEFT JOIN (SELECT DISTINCT ic.id               AS invoiceId,
                                                                                                                         SUM(tm.amount *- 1) AS invoicePaidAmount,
                                                                                                                         MAX(tm.trans_date)  AS lastPaymentDate
                                                                                                                  FROM   invoice_income AS ic
                                                                                                                         INNER JOIN trans_master AS tm
                                                                                                                                 ON tm.recenciliation_code =
                                                                                                                                    CONCAT(ic.id, '#1')
                                                                                                                                    AND ic.debtor_id = '${debtorId}'
                                                                                                                                    AND tm.account_code = '1300'
                                                                                                                                    AND tm.trans_type <> 1
                                                                                                                  GROUP  BY ic.id) AS tblPaidInvoices
                                                                                                              ON tblPaidInvoices.invoiceid = ic.id) AS tblAllInfo
                                                                                        WHERE tblAllInfo.debtorid = '${debtorId}' AND tblAllInfo.invoiceamount = tblAllInfo.paidamount 
                                                                                        ORDER  BY STR_TO_DATE(invoicedate, '%d-%m-%Y') DESC""")
        [debtorPaidInvoices:debtorPaidInvoices,creditLimit:creditLimit]
    }

    def loadOutstandingInvoices(){
        def debtorId = request.getParameter("debtorId")
        def creditLimit = budgetViewDatabaseService.executeCustomQuery("SELECT cm.credit_limit AS creditLimit FROM customer_master AS cm WHERE cm.id="+debtorId)
        if(creditLimit)
            creditLimit = creditLimit[0][0]
        def debtorOutstandingInvoices = budgetViewDatabaseService.executeCustomQuery("""SELECT *
                                                                                                FROM   (SELECT ic.payment_ref
                                                                                                                      AS paymentRefference,
                                                                                                               vm.vendor_name
                                                                                                                      AS customerName,
                                                                                                               DATE_FORMAT(ic.trans_date, '%d-%m-%Y')
                                                                                                                      AS invoiceDate,
                                                                                                               TO_DAYS(ic.due_date) - TO_DAYS(NOW())
                                                                                                                      AS daysToPay,
                                                                                                               IF(ISNULL(tblPaidInvoices.lastpaymentdate), TO_DAYS(NOW()) -
                                                                                                               TO_DAYS(ic.trans_date),
                                                                                                               TO_DAYS(
                                                                                                               tblPaidInvoices.lastpaymentdate)
                                                                                                               - TO_DAYS(ic.trans_date))
                                                                                                                      AS paymentRealized,
                                                                                                               ( TO_DAYS(NOW()) - TO_DAYS(ic.trans_date) ) - 180
                                                                                                                      AS isSixMonths,
                                                                                                               pt.terms
                                                                                                                      AS paymentTerms,
                                                                                                               ROUND(ic.total_gl_amount + ic.total_vat, 2)
                                                                                                                      AS invoiceAmount,
                                                                                                               IF(ISNULL(tblPaidInvoices.invoicepaidamount),
                                                                                                               ROUND(ic.total_gl_amount + ic.total_vat, 2),
                                                                                                               ROUND(( ic.total_gl_amount + ic.total_vat ) -
                                                                                                                     tblPaidInvoices.invoicepaidamount, 2)) AS dueAmount,
                                                                                                               IF(ISNULL(tblPaidInvoices.invoicepaidamount), 0, ROUND(
                                                                                                               tblPaidInvoices.invoicepaidamount, 2))
                                                                                                                      AS paidAmount,
                                                                                                               vm.credit_limit
                                                                                                                      AS creditLimit,
                                                                                                               ic.customer_id
                                                                                                                      AS customerId,
                                                                                                               ic.debtor_id
                                                                                                                      AS debtorId,
                                                                                                               ic.id
                                                                                                                      AS invoiceId,
                                                                                                               ic.extra_days
                                                                                                                        AS extraDays      
                                                                                                        FROM   invoice_income AS ic
                                                                                                               INNER JOIN vendor_master AS vm
                                                                                                                       ON ic.customer_id = vm.id
                                                                                                               INNER JOIN payment_terms AS pt
                                                                                                                       ON pt.id = ic.terms_id
                                                                                                               LEFT JOIN (SELECT DISTINCT ic.id               AS invoiceId,
                                                                                                                                 SUM(tm.amount *- 1) AS invoicePaidAmount,
                                                                                                                                 MAX(tm.trans_date)  AS lastPaymentDate
                                                                                                                          FROM   invoice_income AS ic
                                                                                                                                 INNER JOIN trans_master AS tm
                                                                                                                                         ON tm.recenciliation_code =
                                                                                                                                            CONCAT(ic.id, '#1')
                                                                                                                                            AND ic.debtor_id = '${debtorId}'
                                                                                                                                            AND tm.account_code = '1300'
                                                                                                                                            AND tm.trans_type <> 1                                        
                                                                                                                          GROUP  BY ic.id) AS tblPaidInvoices
                                                                                                                      ON tblPaidInvoices.invoiceid = ic.id) AS tblAllInfo
                                                                                                WHERE  tblAllInfo.debtorid = '${debtorId}' AND tblAllInfo.invoiceamount <> tblAllInfo.paidamount 
                                                                                                ORDER  BY STR_TO_DATE(invoicedate, '%d-%m-%Y') DESC""")
        [debtorOutstandingInvoices:debtorOutstandingInvoices,creditLimit:creditLimit]
    }

    def moveHistory(){
        def paramsId = params.id
        List idList = new ArrayList()
        idList.add(params.checkMoveHistory)
        String eachIdList = idList.toString()
        def moveId = eachIdList.substring(1, eachIdList.length()-1)

       String firstLetter = String.valueOf(moveId.charAt(0));
        if (firstLetter == "["){
            moveId = moveId.substring(1, moveId.length()-1)
        }
        def updatedMoveHistory = budgetViewDatabaseService.executeUpdate("UPDATE invoice_income SET history_status = 1 WHERE id IN (${moveId})")
//        render updatedMoveHistory as JSON
        LinkedHashMap result = [paramsId: paramsId]
        def gridOutput = result as JSON
        render gridOutput
    }

    def loadMoveHistoryTransaction(){
        def debtorId = request.getParameter("debtorId")
        def customerMasterResult = budgetViewDatabaseService.executeCustomQuery("SELECT a.id AS id,a.version AS version, a.cham_of_commerce AS cham_of_commerce,a.comments AS comments, a.customer_name AS customer_name,a.customer_type As customer_type,a.customer_code AS customer_code, a.default_gl_account AS default_gl_account,a.email As email,a.gender As gender,a.first_name as first_name, a.last_name As last_name, a.middle_name As middle_name, a.payment_term_id As payment_term_id, a.company_name As company_name, a.vat As vat,a.status As status,a.credit_limit As credit_limit, a.insuad_amout As insuad_amout,a.credit_status AS credit_status,a.vat_number As vat_number from customer_master as a WHERE a.id=${debtorId}")
        String strQuery = """SELECT CONCAT('INVI-', ii.invoice_no) AS invoiceNumber,
                                   ii.payment_ref,
                                   ii.trans_date,
                                   ii.booking_period,
                                   ROUND((ii.total_gl_amount + ii.total_vat),2) AS invAmt,
                                   IF(ISNULL(tblPaidInfo.invoicePaidAmount), 0, ROUND(tblPaidInfo.invoicePaidAmount, 2)) AS paidAmount,
                                   IF(ISNULL(tblPaidInfo.invoicePaidAmount), ROUND(((ii.total_gl_amount + ii.total_vat) - 0),2), ROUND(((ii.total_gl_amount + ii.total_vat) - tblPaidInfo.invoicePaidAmount),2)) AS remainAmount,
                                   ii.id,
                                   ii.booking_year,
                                   ii.due_date AS dueDate,
                                   tblPaidInfo.lastPaymentDate AS lastPaymentDate
                            FROM invoice_income AS ii
                            LEFT JOIN
                              (SELECT ic.id AS invoiceId,
                                      SUM(tm.amount*-1) AS invoicePaidAmount,
                                      MAX(tm.trans_date) AS lastPaymentDate,
                                      tm.invoice_no AS invoiceNo
                               FROM invoice_income AS ic
                               INNER JOIN trans_master tm ON tm.recenciliation_code = CONCAT(ic.id, '#1')
                               AND ic.debtor_id='${debtorId}'
                               AND tm.account_code='1300'
                               AND tm.trans_type <> 1
                               GROUP BY ic.id
                               ORDER BY tm.trans_date) AS tblPaidInfo ON ii.id=tblPaidInfo.invoiceId
                            WHERE ii.debtor_id='${debtorId}'
                              AND ii.history_status=1"""

        def resultCustomerInvInfo = new BudgetViewDatabaseService().executeQuery(strQuery)

        if (session.ongoingID != debtorId) {
            session.ongoingID = debtorId
            session.dummyData = 0
            session.resultCustomerInvInfo = []
        }

        resultCustomerInvInfo.eachWithIndex { Phn, key ->
            Map map = ["invoiceId": 0, "paymentReference": '', "transactionDate": 0, "bookingPeriod": 0, "paidAmount": 0, "dueAmount": 0, "bookingYear": 0]
            map.invoiceId = Phn[7]
            map.paymentReference = Phn[1].toString()
            map.transactionDate = Phn[2].toString()
            map.bookingPeriod = Phn[3]
            map.paidAmount = Phn[5].toString()
            map.dueAmount = Phn[6]
            map.bookingYear = Phn[8]

            session.resultCustomerInvInfo << map
        }

        [customerMasterInstance:customerMasterResult[0], invoiceCustomerDetailsInstance: resultCustomerInvInfo]
    }

    def moveHistoryToFinancialTrans (){
        def paramsId = params.id
        List idList = new ArrayList()
        idList.add(params.checkMoveHistory)
        String eachIdList = idList.toString()
        def moveId = eachIdList.substring(1, eachIdList.length()-1)

        String firstLetter = String.valueOf(moveId.charAt(0));
        if (firstLetter == "["){
            moveId = moveId.substring(1, moveId.length()-1)
        }
        def updatedMoveHistory = budgetViewDatabaseService.executeUpdate("UPDATE invoice_income SET history_status = 0 WHERE id IN (${moveId})")
//        render updatedMoveHistory as JSON
        LinkedHashMap result = [paramsId: paramsId]
        def gridOutput = result as JSON
        render gridOutput
    }

    def loadFinancialTransaction(){
        def debtorId = request.getParameter("debtorId")
        def customerMasterResult = budgetViewDatabaseService.executeCustomQuery("SELECT a.id AS id,a.version AS version, a.cham_of_commerce AS cham_of_commerce,a.comments AS comments, a.customer_name AS customer_name,a.customer_type As customer_type,a.customer_code AS customer_code, a.default_gl_account AS default_gl_account,a.email As email,a.gender As gender,a.first_name as first_name, a.last_name As last_name, a.middle_name As middle_name, a.payment_term_id As payment_term_id, a.company_name As company_name, a.vat As vat,a.status As status,a.credit_limit As credit_limit, a.insuad_amout As insuad_amout,a.credit_status AS credit_status,a.vat_number As vat_number from customer_master as a WHERE a.id=${debtorId}")
        String strQuery = """SELECT CONCAT('INVI-', ii.invoice_no) AS invoiceNumber,
                                       ii.payment_ref,
                                       ii.trans_date,
                                       ii.booking_period,
                                       ROUND((ii.total_gl_amount + ii.total_vat),2) AS invAmt,
                                       IF(ISNULL(tblPaidInfo.invoicePaidAmount), 0, ROUND(tblPaidInfo.invoicePaidAmount, 2)) AS paidAmount,
                                       IF(ISNULL(tblPaidInfo.invoicePaidAmount), ROUND(((ii.total_gl_amount + ii.total_vat) - 0),2), ROUND(((ii.total_gl_amount + ii.total_vat) - tblPaidInfo.invoicePaidAmount),2)) AS remainAmount,
                                       ii.id,
                                       ii.booking_year,
                                       ii.due_date AS dueDate,
                                       tblPaidInfo.lastPaymentDate AS lastPaymentDate
                                FROM invoice_income AS ii
                                LEFT JOIN
                                  (SELECT ic.id AS invoiceId,
                                          SUM(tm.amount*-1) AS invoicePaidAmount,
                                          MAX(tm.trans_date) AS lastPaymentDate,
                                          tm.invoice_no AS invoiceNo
                                   FROM invoice_income AS ic
                                   INNER JOIN trans_master tm ON tm.recenciliation_code = CONCAT(ic.id, '#1')
                                   AND ic.debtor_id='${debtorId}'
                                   AND tm.account_code='1300'
                                   AND tm.trans_type <> 1
                                   GROUP BY ic.id
                                   ORDER BY tm.trans_date) AS tblPaidInfo ON ii.id=tblPaidInfo.invoiceId
                                WHERE ii.debtor_id='${debtorId}'
                                  AND ii.history_status=0"""
        def resultCustomerInvInfo = budgetViewDatabaseService.executeQuery(strQuery)

        if (session.ongoingID != debtorId) {
            session.ongoingID = debtorId
            session.dummyData = 0
            session.resultCustomerInvInfo = []
        }

        resultCustomerInvInfo.eachWithIndex { Phn, key ->
            Map map = ["invoiceId": 0, "paymentReference": '', "transactionDate": 0, "bookingPeriod": 0, "paidAmount": 0, "dueAmount": 0, "bookingYear": 0]
            map.invoiceId = Phn[7]
            map.paymentReference = Phn[1].toString()
            map.transactionDate = Phn[2].toString()
            map.bookingPeriod = Phn[3]
            map.paidAmount = Phn[5].toString()
            map.dueAmount = Phn[6]
            map.bookingYear = Phn[8]

            session.resultCustomerInvInfo << map
        }

        [customerMasterInstance:customerMasterResult[0], invoiceCustomerDetailsInstance: resultCustomerInvInfo]
    }

    def undoWriteOff(){

        def invoiceIncome = new BudgetViewDatabaseService().executeCustomQuery("SELECT trans_date AS transDate FROM trans_master WHERE invoice_no = ${params.invoiceNo} and customer_id = ${params.customerId} and trans_type = 8 AND account_code = 1300")
        if(invoiceIncome){
            def transDate = invoiceIncome[0][0]

            def isValidDate  = new CoreParamsHelperTagLib().isDateClosedFiscalYearUndoWriteOff(transDate)
            if(isValidDate) {
                def tableName = "trans_master"
                def deletedWhereSrting = "invoice_no="+"'${params.invoiceNo}' and trans_type = 8"
                new BudgetViewDatabaseService().delete(tableName, deletedWhereSrting)
            } else {
                session.isFiscalYearClosed = true
            }
        }
        redirect(action: "showUndoWriteOff", id: params.customerId)
    }

    def showUndoWriteOff(){
        def debtorId = params.id
        def debtorWriteOffs = new BudgetViewDatabaseService().executeCustomQuery("SELECT tm.invoice_no AS invoiceNo,tm.recenciliation_code AS reconciliation,DATE_FORMAT(tm.trans_date,'%d-%m-%Y') as transactionDate,tm.amount*(-1) AS amount FROM trans_master AS tm WHERE tm.account_code='1300' AND trans_type=8 AND tm.customer_id="+debtorId)
        [debtorWriteOffs:debtorWriteOffs,debtorId:debtorId]
    }

    def loadBankDataInfo(){
        def debtorId = request.getParameter("debtorId")
        String strQuery = """SELECT bsidf.bank_payment_id,
                                   IF(tm.trans_type=7, bsidf.trans_bank_account_no, ' ') AS trans_bank_account_no,
                                   CONCAT('INVI-', ii.invoice_no) AS invoiceNumber,
                                   IF(tm.trans_type=7, bsidf.description, IF(ISNULL(tmd.write_off_description), 'Write off by user', tmd.write_off_description)) AS description,
                                   IF(tm.trans_type=7, bsidf.trans_date_time, DATE_FORMAT(tm.trans_date, '%y%m%d')) AS trans_date_time,
                                   IF(tm.trans_type=7, bsidf.reconciliated_amount, ROUND(tm.amount*(-1), 2)) AS reconAmount,
                                   tm.amount*(-1) AS trans_total_amount
                            FROM invoice_income AS ii
                            INNER JOIN trans_master AS tm ON tm.recenciliation_code = CONCAT(ii.id, '#1')
                            INNER JOIN bank_statement_import_details_final AS bsidf ON bsidf.bank_payment_id = tm.invoice_no
                            LEFT JOIN trans_master_write_off_details AS tmd ON tm.invoice_no=tmd.invoice_no
                            WHERE ii.debtor_id = ${debtorId}
                              AND tm.trans_type IN (7,
                                                    8)
                              AND ii.history_status = 0
                            GROUP BY bsidf.bank_payment_id
                            ORDER BY ii.trans_date"""

        def bankPaymentInfoDetails = budgetViewDatabaseService.executeCustomQuery(strQuery)
        [bankPaymentInfoDetailsInstance: bankPaymentInfoDetails]
    }

    def loadMoveHistoryBankDataInfo (){
        def debtorId = request.getParameter("debtorId")
        String strQuery = "SELECT bsidf.bank_payment_id,IF(tm.trans_type=7,  bsidf.trans_bank_account_no, ' ') AS trans_bank_account_no,CONCAT('INVI-',ii.invoice_no) AS invoiceNumber, IF(tm.trans_type=7,  bsidf.description,IF(ISNULL(tmd.write_off_description),'Write off by user',tmd.write_off_description)) AS description, IF(tm.trans_type=7,  bsidf.trans_date_time, DATE_FORMAT(tm.trans_date,'%y%m%d')) AS trans_date_time, IF(tm.trans_type=7,  bsidf.reconciliated_amount, ROUND(tm.amount*(-1),2)) AS reconAmount,tm.amount*(-1) as trans_total_amount FROM invoice_income AS ii INNER JOIN trans_master AS tm ON tm.recenciliation_code = CONCAT(ii.id,'#1') INNER JOIN bank_statement_import_details_final AS bsidf ON bsidf.bank_payment_id = tm.invoice_no LEFT JOIN trans_master_write_off_details AS tmd ON tm.invoice_no=tmd.invoice_no WHERE ii.debtor_id = ${debtorId} AND tm.trans_type IN (7,8) AND ii.history_status = 1 GROUP BY bsidf.bank_payment_id ORDER BY ii.trans_date"
        def bankPaymentInfoDetails = new BudgetViewDatabaseService().executeCustomQuery(strQuery)
        [bankPaymentInfoDetailsInstance: bankPaymentInfoDetails]
    }

    def writeOffInvoicePaymentCustomer() {
        def isFiscalYearClosed = false
        def customerId = params.id
        def cusPrefix = params.customerPrefix
        def cusCode = params.customerCode
        def cusName = params.customerName

        def selectedRowArr = []
        def rowId = -1
        int nSelectedCount = 0
        boolean bIsArray = false
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

        if (params.invBankStatement instanceof Object[]) {
            bIsArray = true;
            selectedRowArr = params.invBankStatement
            nSelectedCount = selectedRowArr.size()
        } else {
            try {
                rowId = Integer.parseInt(params.invBankStatement)
            }
            catch(Exception e){

            }
            if(rowId>=0)
                nSelectedCount = 1

        }

        for (int i = 0; i < nSelectedCount; i++) {

            if (bIsArray) {
                rowId = Integer.parseInt(selectedRowArr[i])
            }

            def invoiceId = session.resultCustomerInvInfo[rowId]['invoiceId']
            def paytmentReference = session.resultCustomerInvInfo[rowId]['paymentReference']
            def transactionDate = session.resultCustomerInvInfo[rowId]['transactionDate']
            def invTransDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(transactionDate)
            def bookingPeriod = writeOffMonth
            def paidAmount = session.resultCustomerInvInfo[rowId]['paidAmount']
            def dueAmount = session.resultCustomerInvInfo[rowId]['dueAmount']
            def bookingYear = writeOffYear
            def comapnyBankCode = session.resultCustomerInvInfo[rowId]['companyBankCode']

            def isValidDate  = new CoreParamsHelperTagLib().isDateClosedFiscalYearForWriteOff(writeOffTransDate)
            if(isValidDate){
                extraSettingService.writeOffInvoicePaymentCustomer(invoiceId, paytmentReference, transactionDate, bookingPeriod, paidAmount,dueAmount, bookingYear, writeOffDay,params.id,writeOffAmount,writeOffDescription)
            }
            else{
                isFiscalYearClosed = true
            }
        }

        session.resultCustomerInvInfo = []
        String strQuery = """SELECT CONCAT(sp.prefix, '-', ii.invoice_no) AS invoiceNumber,
                                   ii.payment_ref,
                                   ii.trans_date,
                                   ii.booking_period,
                                   ROUND((ii.total_gl_amount + ii.total_vat),2) AS invAmt,
                                   ROUND((ii.total_gl_amount + ii.total_vat) - SUM(tm.amount), 2) AS paidAmount,
                                   ROUND(SUM(tm.amount), 2) AS remainAmount,
                                   ii.id,
                                   ii.booking_year,
                                   ii.due_date AS dueDate,
                                   MAX(tm.trans_date) AS lastPaymentDate
                             FROM system_prefix AS sp,
                                 invoice_income AS ii
                             INNER JOIN trans_master AS tm ON tm.recenciliation_code = CONCAT(ii.id, '#1')
                             AND tm.account_code=
                              (SELECT debitor_gl_code
                               FROM debit_credit_gl_setup)
                             WHERE ii.debtor_id ='${params.id}'
                             AND sp.id=8
                             GROUP BY tm.recenciliation_code
                             ORDER BY ii.trans_date"""

        def resultCustomerInvInfo = new BudgetViewDatabaseService().executeQuery(strQuery)

        resultCustomerInvInfo.eachWithIndex { Phn, key ->
            Map map = ["invoiceId": 0, "paymentReference": '', "transactionDate": 0, "bookingPeriod": 0, "paidAmount": 0, "dueAmount": 0, "bookingYear": 0]
            map.invoiceId = Phn[7]
            map.paymentReference = Phn[1].toString()
            map.transactionDate = Phn[2].toString()
            map.bookingPeriod = Phn[3]
            map.paidAmount = Phn[5].toString()
            map.dueAmount = Phn[6]
            map.bookingYear = Phn[8]
            session.resultCustomerInvInfo << map
        }

        //For bank transaction details

        strQuery = """SELECT bsidf.bank_payment_id,
                           IF(tm.trans_type=7, bsidf.trans_bank_account_no, ' ') AS trans_bank_account_no,
                           CONCAT('INVI-', ii.invoice_no) AS invoiceNumber,
                           IF(tm.trans_type=7, bsidf.description, IF(ISNULL(tmd.write_off_description), 'Write off by user', tmd.write_off_description)) AS description,
                           IF(tm.trans_type=7, bsidf.trans_date_time, DATE_FORMAT(tm.trans_date, '%y%m%d')) AS trans_date_time,
                           IF(tm.trans_type=7, bsidf.reconciliated_amount, ROUND(tm.amount*(-1), 2)) AS reconAmount,
                           tm.amount*(-1) AS trans_total_amount
                      FROM invoice_income AS ii
                      INNER JOIN trans_master AS tm ON tm.recenciliation_code = CONCAT(ii.id, '#1')
                      INNER JOIN bank_statement_import_details_final AS bsidf ON bsidf.bank_payment_id = tm.invoice_no
                      LEFT JOIN trans_master_write_off_details AS tmd ON tm.invoice_no=tmd.invoice_no
                      WHERE ii.debtor_id = '${params.id}'
                        AND tm.trans_type IN (7,8)              
                      GROUP BY bsidf.bank_payment_id
                      ORDER BY ii.trans_date"""

        def bankPaymentInfoDetails = new BudgetViewDatabaseService().executeQuery(strQuery)

        def message = "Succesfully Write Off"
        def messageCode = "bv.customerMaster.writeOff.message"
        def messageColor = "#057B21"
        if(isFiscalYearClosed){
            message = "One or more invoices transaction date is in closed fiscal year"
            messageCode = "bv.manual.reconciliation.fiscalyearclosed.label"
            messageColor = "#d69302"
        }
        render(template: "financialTransactionFormWriteOff", model: [invoiceCustomerDetailsInstance: resultCustomerInvInfo,
                                                             bankPaymentInfoDetailsInstance: bankPaymentInfoDetails,
                                                             cusPrefix: cusPrefix, cusCode: cusCode, cusName: cusName,
                                                             cusId: customerId, executeMessage: message, executeMessageCode: messageCode,
                                                             executeMessageColor: messageColor])

    }

    def gridList(Integer max) {

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
        searchItem = params.qtype
        searchString = params.query
        def userList

        def customerPrefix = new CoreParamsHelperTagLib().showGeneratedCustomerCode()

        params.max = params.max ?: 10
        params.offset = (params.offset) ? Integer.parseInt(params.offset) : 0
        params.limit = (params.limit) ? Integer.parseInt(params.limit) : 10
        params.sort = "id"
        params.order = "desc"

        String select = """a.id AS id,a.version AS version, a.cham_of_commerce AS chamOfCommerce,a.comments AS comments,
                        a.customer_name AS customerName,a.customer_type As customerType,a.customer_code AS customerCode,a.default_gl_account AS defaultGlAccount,
                        a.email As email,a.gender As gender,a.first_name as firstName, a.last_name As lastName, a.middle_name As middleName,
                        a.payment_term_id As paymentTermId, a.company_name As companyName, a.vat As vat,
                        a.status As status,a.credit_status AS creditStatus,a.credit_limit AS creditLimit"""
        String selectIndex = """id,version,chamOfCommerce,comments,customerName,customerType,customerCode,defaultGlAccount,email,gender,firstName,lastName,middleName,paymentTermId,companyName, vat,status,creditStatus,creditLimit"""
        String from = "customer_master AS a "
        String where = ""
        String orderBy = ""

        if (params.sortname && params.sortorder) {
            orderBy = "a.${params.sortname} ${params.sortorder}"
        } else {
            orderBy = "a.id ASC"
        }

        LinkedHashMap gridResult = budgetViewDatabaseService.select(select, from, where, orderBy, '', 'false', selectIndex, 0, intOffset)
        String whereInstance = ""
        if (params.id) {
            whereInstance = "a.id=${params.id}"
        } else {
            whereInstance = "a.id=0"
        }

        LinkedHashMap gridResultInstance = budgetViewDatabaseService.select(select, from, whereInstance, orderBy, '', 'false', selectIndex)
        def totalCount = new BudgetViewDatabaseService().executeQuery("SELECT count(a.id)  FROM customer_master AS a WHERE status =1")

        List quickExpenseList = new ArrayList()
        GridEntity obj
        String userEdit = ""

        def protocol = request.isSecure() ? "https://" : "http://"
        def host = request.getServerName()
        def port = request.getServerPort()
        def context = request.getServletContext().getContextPath()
        def g = new ValidationTagLib()

        def liveUrl = ""
        liveUrl = protocol + host + ":" + port + context

        gridResult['dataGridList'].each { phn ->

            def showCustomerType = (phn.customer_type == 'bn') ? g.message(code: 'customerMaster.changingCustomer.dropdown') : g.message(code: 'customerMaster.fixedCustomer.dropdown') //new CoreParamsHelperTagLib().getBookingPeriodDetails(phn.customer_type, 'CUSTOMER_TYPE')

            obj = new GridEntity();
            aInt = aInt + 1
            obj.id = aInt
            userEdit = "<a href='javascript:editUrl(\"${phn.id}\",\"${liveUrl}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${liveUrl}/images/edit.png\"></a>"
            obj.cell = ["customerCode": customerPrefix + "-" + phn.customer_code, "customerName": phn.customer_name,
                        "email": phn.email, "creditLimit": phn.credit_limit, "action": userEdit]
            quickExpenseList.add(obj)
        }

        LinkedHashMap result = [draw: 1, recordsTotal: gridResult.size(), recordsFiltered: gridResult.size(), data: quickExpenseList.cell]
        gridOutput = result as JSON
        render gridOutput;
    }

    def save() {
        flash.message = message(code: 'com.created.message', args: [message(code: 'customerMaster.label', default: 'CustomerMaster'), customerMasterInstance.id])
        redirect(action: "list", id: customerMasterInstance.id)
    }

    def show(Long id) {
        def customerMasterInstance = CustomerMaster.get(id)
        if (!customerMasterInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'customerMaster.label', default: 'CustomerMaster'), id])
            redirect(action: "list")
            return
        }
        [customerMasterInstance: customerMasterInstance]
    }

    def edit(Long id) {

        def customerMasterInstance = CustomerMaster.get(id)
        if (!customerMasterInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'customerMaster.label', default: 'CustomerMaster'), id])
            redirect(action: "list")
            return
        }
        [customerMasterInstance: customerMasterInstance]
    }

    def update(Long id, Long version) {

        def customerId = saveAndUpdate(id, version,params)
        if(id){

            flash.message = message(code: 'com.updated.message', args: [message(code: 'customerMaster.label', default: 'CustomerMaster'), customerId])
            redirect(action: "list", id: customerId, params: [bookInvoiceId      : params.bookInvoiceId, bookingPeriod: params.bookingPeriod,
                                                      customerId         : params.customerId, budgetCustomerId: params.budgetCustomerId,
                                                      budgetItemDetailsId: params.budgetItemDetailsId, journalId: params.journalId,
                                                      incBItem           : params.incBItem, fInv: params.fInv, invEditId: params.invEditId])

        }else {
            flash.message = message(code: 'com.created.message', args: [message(code: 'customerMaster.label', default: 'CustomerMaster'), customerId])
            redirect(action: "list", id: customerId, params: [bookInvoiceId      : params.bookInvoiceId, bookingPeriod: params.bookingPeriod,
                                                                            customerId         : params.customerId, budgetCustomerId: params.budgetCustomerId,
                                                                            budgetItemDetailsId: params.budgetItemDetailsId, journalId: params.journalId,
                                                                            incBItem           : params.incBItem, fInv: params.fInv, invEditId: params.invEditId])
        }
    }

    def updateAndCreateDebtor(Long id, Long version){
        def customerId = saveAndUpdate(id, version,params)
        if(id){
           redirect(controller: "debtorCustomer", action: "index")
        }else {
            redirect(controller: "debtorCustomer", action: "index")
        }
    }

    def saveAndUpdate(Long id, Long version,def params){


        params.creditLimit = debtorCustomerService.replaceComaToDot(params.creditLimit);
        params.insuadAmout = debtorCustomerService.replaceComaToDot(params.insuadAmout);

        if (id) {
            String select = "id,version"
            String selectIndex = "id,version"
            String from = "customer_master"
            String where = "id='" + id + "'"
            String orderBy = "id ASC"
            LinkedHashMap gridResult = budgetViewDatabaseService.select(select, from, where, orderBy, '', 'false', selectIndex)

            def customerMasterInstance = ""

            if (gridResult['dataGridList'].size()) {
                customerMasterInstance = gridResult['dataGridList'][0]
            }

            if (!customerMasterInstance) {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'customerMaster.label', default: 'CustomerMaster'), id])
                redirect(action: "list")
                return
            }

            if (version != null) {
                if (customerMasterInstance.version > version) {
                    customerMasterInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                            [message(code: 'customerMaster.label', default: 'CustomerMaster')] as Object[],
                            "Another user has updated this CustomerMaster while you were editing")
                    render(view: "list", model: [customerMasterInstance: customerMasterInstance])
                    return
                }
            }

            params.customerType = "cn"
            params.defaultGlAccount = "8000"
//              println ""+params.companyName
            Map updatededValue = [
                    cham_of_commerce  : "${params.chamOfCommerce}",
                    comments          : "${params.comments}",
                    default_gl_account: "${params.defaultGlAccount}",
                    email             : "${params.email}",
                    first_name        : "${params.firstName}",
                    last_name         : "${params.lastName}",
                    middle_name       : "${params.middleName}",
                    payment_term_id   : "${params.paymentTerm.id}",
                    customer_name     : "${params.customerName}",
                    vat               : "${params.vat}",
                    customer_type     : "${params.customerType}",
                    status            : "${params.status}",
                    gender            : "${params.gender}",
                    credit_status     : "${params.creditStatus}",
                    credit_limit     : "${params.creditLimit}",
                    insuad_amout     : "${params.insuadAmout}",
                    vat_number        : "${params.vatNumber}"
            ]

            def updatedTableName = "customer_master"
            def updatedWhereSrting = "id=" + "'" + id + "'"
            budgetViewDatabaseService.update(updatededValue, updatedTableName, updatedWhereSrting)

            def generalAddressId=new BudgetViewDatabaseService().executeQueryAtSingle("""SELECT id,contact_person_name,city,postal_code
                                FROM customer_general_address WHERE customer_id= '${id}' """)

            if(generalAddressId != ""){

                Map updatededGeneralAddressValue = [
                        phone_no                    : "${params.phoneNo}",
                        website_address             : "${params.websiteAddress}",
                        customer_id                 : "${params.id}",
                        city                        : generalAddressId[2],
                        contact_person_name         : generalAddressId[1],
//                        country_id                  : 2,
                        postal_code                 : generalAddressId[3],
                        status                      : 1
                ]

                def updatedTable = "customer_general_address"
                def whereSrting = "id=" + "'" + generalAddressId[0] + "'"
                budgetViewDatabaseService.update(updatededGeneralAddressValue, updatedTable, whereSrting)
            }else{

                Map insertCustomerGeneralAddress=[
                        phone_no        : "${params.phoneNo}",
                        website_address : "${params.websiteAddress}",
                        customer_id     : "${params.id}",
                        city            : "",
                        contact_person_name:"",
                        country_id      :       2,
                        postal_code     : "",
                        status          :       1
                ]

                def table = "customer_general_address"
                Integer customerGeneralAddressInstanceId = budgetViewDatabaseService.insert(insertCustomerGeneralAddress, table)
            }

            return id
        } else{
            params.customerCode = new CoreParamsHelperTagLib().getNextGeneratedNumber('customer')
            params.status = 1
            params.customerType = "cn"
            params.defaultGlAccount = "8000"

            def mapRe = ["cham_of_commerce"  : "",
                         "comments" : "",
                         "customer_name"     : "${params.customerName}",
                         "default_gl_account": "${params.defaultGlAccount}",
                         "email"             : "",
                         "first_name"        : "",
                         "last_name"         : "",
                         "middle_name"       : "",
                         "vat"               : "${params.vat}",
                      "customer_type"     : "${params.customerType}",
                      "status"            : "${params.status}",
                      "gender"            : "${params.gender}",
                      "credit_status"     : "${params.creditStatus}",
                      "credit_limit"      : "${params.creditLimit}",
                      "customer_code"     : "${params.customerCode}",
                         "payment_term_id"   : "${params.paymentTerm.id}"]

            def insertedValue = [
                    "cham_of_commerce"  : "",
                    "comments"          : "",
                    "customer_name"     : "${params.customerName}",
                    "default_gl_account": "${params.defaultGlAccount}",
                    "email"             : "",
                    "first_name"        : "",
                    "last_name"         : "",
                    "middle_name"       : "",
                    "payment_term_id"   : "${params.paymentTerm.id}",
                    "vat"               : "${params.vat}",
                    "customer_type"     : "${params.customerType}",
                    "status"            : "${params.status}",
                    "gender"            : "${params.gender}",
                    "credit_status"     : "${params.creditStatus}",
                    "credit_limit"      : "${params.creditLimit}",
                    "customer_code"     : "${params.customerCode}"
            ]

            def tableName = "customer_master"
            Integer customerMasterInstanceId = budgetViewDatabaseService.insert(insertedValue, tableName)

            return customerMasterInstanceId

        }
    }

    def delete(Long id) {

        def customerMasterInstance = CustomerMaster.get(id)
        //println('customerMasterInstance=<ASHISH>==' + customerMasterInstance?.customerCode)

        if (!customerMasterInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'customerMaster.label', default: 'CustomerMaster'), id])
            redirect(action: "list")
            return
        }

        def selectedcustomerMasterId = new BudgetViewDatabaseService().executeQuery("SELECT customerId FROM bv.InvoiceIncome where customerId='" + customerMasterInstance.customerCode + "'")
        //println('selectedcustomerMasterId=<ASHISH>==' + selectedcustomerMasterId)
        if (selectedcustomerMasterId.size() > 0) {
            flash.message = message(code: 'com.not.deleted.message', args: [message(code: 'customerMaster.label', default: 'CustomerMaster'), customerMasterInstance.customerName])
            //redirect(action: "list", id: id)
        } else {
            try {
                new BudgetViewDatabaseService().executeUpdate("Delete FROM CustomerGeneralAddress WHERE customer_id='" + id + "'")
                new BudgetViewDatabaseService().executeUpdate("Delete FROM CustomerPostalAddress WHERE customer_id='" + id + "'")
                new BudgetViewDatabaseService().executeUpdate("Delete FROM CustomerShipmentAddress WHERE customer_id='" + id + "'")
                new BudgetViewDatabaseService().executeUpdate("Delete FROM CustomerBankAccount WHERE customer_id='" + id + "'")
                new BudgetViewDatabaseService().delete(flush: true)
                flash.message = message(code: 'com.deleted.message', args: [message(code: 'customerMaster.label', default: 'CustomerMaster'), id])
                redirect(action: "list")
            }
            catch (DataIntegrityViolationException e) {
                flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'customerMaster.label', default: 'CustomerMaster'), id])
                redirect(action: "list", id: id)
            }
        }
    }

    def updategeneral(Long id, Long version) {

        if (id) {
            String select = "id,version"
            String selectIndex = "id,version"
            String from = "customer_general_address"
            String where = "id='" + id + "'"
            String orderBy = "id ASC"
            LinkedHashMap gridResult = budgetViewDatabaseService.select(select, from, where, orderBy, '', 'false', selectIndex)

            def customerGeneralAddressInstance = ""
            if (gridResult['dataGridList'].size()) {
                customerGeneralAddressInstance = gridResult['dataGridList'][0]
            }

            if (!customerGeneralAddressInstance) {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'customerGeneralAddress.label', default: 'CustomerGeneralAddress'), id])
                redirect(action: "list")
                return
            }

            if (version != null) {
                if (customerGeneralAddressInstance.version > version) {
                    customerGeneralAddressInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                            [message(code: 'customerGeneralAddress.label', default: 'CustomerGeneralAddress')] as Object[],
                            "Another user has updated this CustomerGeneralAddress while you were editing")
                    render(view: "edit", model: [customerGeneralAddressInstance: customerGeneralAddressInstance])
                    return
                }
            }

            Map updatededValue = [
                    address_line1      : "${params.addressLine1}",
                    address_line2      : "${params.addressLine2}",
                    city               : "${params.city}",
                    contact_person_name: "${params.contactPersonName}",
                    country_id         : "${params.countryId}",
                    postal_code        : "${params.postalCode}",
                    state              : "${params.state}",
                    status             : "1",
                    customer_id        : "${params.customer_id}"
            ]

            def updatedTableName = "customer_general_address"
            def updatedWhereSrting = "id=" + "'" + id + "'"
            budgetViewDatabaseService.update(updatededValue, updatedTableName, updatedWhereSrting)

            flash.message = message(code: 'default.updated.message', args: [message(code: 'customerGeneralAddress.label', default: 'CustomerGeneralAddress'), " "])
            redirect(action: "list", id: params.customer_id, params: [bookInvoiceId: params.bookInvoiceId, bookingPeriod: params.bookingPeriod, customerId: params.customerId, budgetItemDetailsId: params.budgetItemDetailsId, journalId: params.journalId, incBItem: params.incBItem, fInv: params.fInv, budgetCustomerId: params.budgetCustomerId])
        } else {
            Map insertedValue = [
                    address_line1      : "${params.addressLine1}",
                    address_line2      : "${params.addressLine2}",
                    city               : "${params.city}",
                    contact_person_name: "${params.contactPersonName}",
                    country_id         : "${params.countryId}",
                    postal_code        : "${params.postalCode}",
                    state              : "${params.state}",
                    status             : "1",
                    customer_id        : "${params.customer_id}"
            ]

            def tableName = "customer_general_address"

            Integer customerGeneralAddressInstanceId = budgetViewDatabaseService.insert(insertedValue, tableName)
            flash.message = message(code: 'com.created.message', args: [message(code: 'customerGeneralAddress.label', default: 'customerGeneralAddress'), customerGeneralAddressInstanceId])

            redirect(action: "list", id: params.customer_id, params: [bookInvoiceId: params.bookInvoiceId,
                    bookingPeriod: params.bookingPeriod, customerId: params.customerId, budgetItemDetailsId: params.budgetItemDetailsId,
                    journalId: params.journalId, incBItem: params.incBItem, fInv: params.fInv, budgetCustomerId: params.budgetCustomerId])
        }
    }

    def updatepostal(Long id, Long version) {

        if (id) {
            String select = "id,version"
            String selectIndex = "id,version"
            String from = "customer_postal_address"
            String where = "id='" + id + "'"
            String orderBy = "id ASC"
            LinkedHashMap gridResult = budgetViewDatabaseService.select(select, from, where, orderBy, '', 'false', selectIndex)

            def customerPostalAddressInstance = ""

            if (gridResult['dataGridList'].size()) {
                customerPostalAddressInstance = gridResult['dataGridList'][0]
            }

            if (!customerPostalAddressInstance) {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'customerPostalAddress.label', default: 'Customer Postal Address'), id])
                redirect(action: "list")
                return
            }

            if (version != null) {
                if (customerPostalAddressInstance.version > version) {
                    customerPostalAddressInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                            [message(code: 'customerPostalAddress.label', default: 'Customer Postal Address')] as Object[],
                            "Another user has updated this CustomerPostalAddress while you were editing")
                    render(view: "edit", model: [customerPostalAddressInstance: customerPostalAddressInstance])
                    return
                }
            }

            Map updatededValue = [
                    postal_address_line1      : "${params.postalAddressLine1}",
                    postal_address_line2      : "${params.postalAddressLine2}",
                    postal_city               : "${params.postalCity}",
                    postal_contact_person_name: "${params.postalContactPersonName}",
                    postal_postcode           : "${params.postalPostcode}",
                    postal_state              : "${params.postalState}",
                    postal_country_id         : "${params.postalCountryId}",
                    status                    : "1",
                    customer_id               : "${params.customer_id}"
            ]

            def updatedTableName = "customer_postal_address"
            def updatedWhereSrting = "id=" + "'" + id + "'"
            budgetViewDatabaseService.update(updatededValue, updatedTableName, updatedWhereSrting)

            flash.message = message(code: 'com.updated.message', args: [message(code: 'customerPostalAddress.label', default: 'Customer Postal Address'), id])
            redirect(action: "list", id: params.customer_id, params: [bookInvoiceId: params.bookInvoiceId, bookingPeriod: params.bookingPeriod, customerId: params.customerId, budgetItemDetailsId: params.budgetItemDetailsId, journalId: params.journalId, incBItem: params.incBItem, fInv: params.fInv])
        } else {
            Map insertedValue = [
                    customer_id               : "${params.customer_id}",
                    postal_address_line1      : "${params.postalAddressLine1}",
                    postal_address_line2      : "${params.postalAddressLine2}",
                    postal_city               : "${params.postalCity}",
                    postal_contact_person_name: "${params.postalContactPersonName}",
                    postal_country_id         : "${params.postalCountryId}",
                    postal_postcode           : "${params.postalPostcode}",
                    postal_state              : "${params.postalState}",
                    status                    : "1"
            ]

            def tableName = "customer_postal_address"

            Integer customerPostalAddressInstanceId = budgetViewDatabaseService.insert(insertedValue, tableName)
            flash.message = message(code: 'com.created.message', args: [message(code: 'customerPostalAddress.label', default: 'Customer Postal Address'), customerPostalAddressInstanceId])
            redirect(action: "list", id: params.customer_id, params: [bookInvoiceId: params.bookInvoiceId, bookingPeriod: params.bookingPeriod, customerId: params.customerId, budgetItemDetailsId: params.budgetItemDetailsId, journalId: params.journalId, incBItem: params.incBItem, fInv: params.fInv])
        }
    }

    def updateshipping(Long id, Long version) {

        if (id) {
            String select = "id,version"
            String selectIndex = "id,version"
            String from = "customer_shipment_address"
            String where = "id='" + id + "'"
            String orderBy = "id ASC"
            LinkedHashMap gridResult = budgetViewDatabaseService.select(select, from, where, orderBy, '', 'false', selectIndex)

            def customerShipmentAddressInstance = ""

            if (gridResult['dataGridList'].size()) {
                customerShipmentAddressInstance = gridResult['dataGridList'][0]
            }

            if (!customerShipmentAddressInstance) {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'customerBankAccount.label', default: 'customerBankAccount'), id])
                redirect(action: "list")
                return
            }

            if (version != null) {
                if (customerShipmentAddressInstance.version > version) {
                    customerShipmentAddressInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                            [message(code: 'customerShipmentAddress.label', default: 'CustomerShipmentAddress')] as Object[],
                            "Another user has updated this CustomerShipmentAddress while you were editing")
                    render(view: "list", model: [customerShipmentAddressInstance: customerShipmentAddressInstance])
                    return
                }
            }


            Map updatededValue = [
                    customer_id      : "${params.customer_id}",
                    note             : "${params.note}",
                    ship_add_line1   : "${params.shipAddLine1}",
                    ship_add_line2   : "${params.shipAddLine2}",
                    ship_city        : "${params.shipCity}",
                    ship_contact_name: "${params.shipContactName}",
                    ship_country_id  : "${params.shipCountry.id}",
                    ship_email       : "${params.shipEmail}",
                    ship_fax         : "${params.shipFax}",
                    ship_phone_no1   : "${params.shipPhoneNo1}",
                    ship_phone_no2   : "${params.shipPhoneNo2}",
                    ship_post_code   : "${params.shipPostCode}",
                    ship_state       : "${params.shipState}",
                    ship_website     : "${params.shipWebsite}",
                    status           : "${params.status}"
            ]

            def updatedTableName = "customer_shipment_address"
            def updatedWhereSrting = "id=" + "'" + id + "'"
            budgetViewDatabaseService.update(updatededValue, updatedTableName, updatedWhereSrting)

            flash.message = message(code: 'com.updated.message', args: [message(code: 'customerShipmentAddress.label', default: 'CustomerShipmentAddress'), id])
            redirect(action: "list", id: params.customer_id)

        } else {
            Map insertedValue = [
                    customer_id      : "${params.customer_id}",
                    note             : "${params.note}",
                    ship_add_line1   : "${params.shipAddLine1}",
                    ship_add_line2   : "${params.shipAddLine2}",
                    ship_city        : "${params.shipCity}",
                    ship_contact_name: "${params.shipContactName}",
                    ship_country_id  : "${params.shipCountry.id}",
                    ship_email       : "${params.shipEmail}",
                    ship_fax         : "${params.shipFax}",
                    ship_phone_no1   : "${params.shipPhoneNo1}",
                    ship_phone_no2   : "${params.shipPhoneNo2}",
                    ship_post_code   : "${params.shipPostCode}",
                    ship_state       : "${params.shipState}",
                    ship_website     : "${params.shipWebsite}",
                    status           : "${params.status}"
            ]
            def tableName = "customer_shipment_address"

            Integer customerShipmentAddressInstanceId = budgetViewDatabaseService.insert(insertedValue, tableName)
            flash.message = message(code: 'com.created.message', args: [message(code: 'customerShipmentAddress.label', default: 'CustomerShipmentAddress'), customerShipmentAddressInstanceId])
            redirect(action: "list", id: params.customer_id)
        }

    }

    def updatebanking(Long id, Long version) {

        if (id) {
            String select = "id,version"
            String selectIndex = "id,version"
            String from = "customer_bank_account"
            String where = "id='" + id + "'"
            String orderBy = "id ASC"
            LinkedHashMap gridResult = budgetViewDatabaseService.select(select, from, where, orderBy, '', 'false', selectIndex)

            def customerEditBankAccountInstance = ""

            if (gridResult['dataGridList'].size()) {
                customerEditBankAccountInstance = gridResult['dataGridList'][0]
            }

            if (!customerEditBankAccountInstance) {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'customerBankAccount.label', default: 'customerBankAccount'), id])
                redirect(action: "list")
                return
            }

            if (version != null) {
                if (customerEditBankAccountInstance.version > version) {
                    customerEditBankAccountInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                            [message(code: 'customerBankAccount.label', default: 'CustomerBankAccount')] as Object[],
                            "Another user has updated this CustomerBankAccount while you were editing")
                    render(view: "list", model: [customerEditBankAccountInstance: customerEditBankAccountInstance, banktab: customerEditBankAccountInstance.customerId])
                    return
                }
            }

            Map updatededValue = [
                    bank_account_name: "${params.bankAccountName}",
                    bank_account_no  : "${params.bankAccountNo}",
                    iban_prefix      : "${params.ibanPrefix}",
                    status           : "${params.status}",
                    customer_id      : "${params.customer_id}"

            ]

            def updatedTableName = "customer_bank_account"
            def updatedWhereSrting = "id=" + "'" + id + "'"
            budgetViewDatabaseService.update(updatededValue, updatedTableName, updatedWhereSrting)

            flash.message = message(code: 'com.updated.message', args: [message(code: 'customerBankAccount.label', default: 'CustomerBankAccount'), id])
            redirect(action: "list", id: params.customer_id, params: [banktab: params.customer_id, bookInvoiceId: params.bookInvoiceId, bookingPeriod: params.bookingPeriod, customerId: params.customerId, budgetItemDetailsId: params.budgetItemDetailsId, journalId: params.journalId, incBItem: params.incBItem, fInv: params.fInv])

        } else {
            //params.customerId =params.customer_id
            println("params=<>=" + params)
            Map insertedValue = [
                    bank_account_name: "${params.bankAccountName}",
                    bank_account_no  : "${params.bankAccountNo}",
                    iban_prefix      : "${params.ibanPrefix}",
                    status           : "1",
                    customer_id      : "${params.customer_id}",
            ]
            def tableName = "customer_bank_account"

            Integer customerEditBankAccountInstanceId = budgetViewDatabaseService.insert(insertedValue, tableName)
            flash.message = message(code: 'com.created.message', args: [message(code: 'customerBankAccount.label', default: 'CustomerBankAccount'), customerEditBankAccountInstanceId])
            redirect(action: "list", id: params.customer_id, params: [banktab: params.customer_id, bookInvoiceId: params.bookInvoiceId, bookingPeriod: params.bookingPeriod, customerId: params.customerId, budgetItemDetailsId: params.budgetItemDetailsId, journalId: params.journalId, incBItem: params.incBItem, fInv: params.fInv])

        }
    }

    def updateCustomerListView() {
        render(template: "updateCustomerListView", model: [params: params])
    }

   /* def exportExcelofBankTransaction() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm")
        Date date = new Date()
        String strDateCreated = dateFormat.format(date)
        String fileName = "Financial_Transaction_Customer" + strDateCreated
        int nRowNo = 1
        def ActiveFiscalYear = new CoreParamsHelperTagLib().getActiveFiscalYear()
        def fiscalYearInfo = new CoreParamsHelperTagLib().getActiveFiscalYearInformation(ActiveFiscalYear)

        //Getting budget customer data.

        response.setContentType('application/vnd.ms-excel')
        response.setHeader('Content-Disposition', 'Attachment;Filename=' + fileName + '.xls')

        WritableWorkbook workbook = Workbook.createWorkbook(response.outputStream)
        WritableSheet sheet1 = workbook.createSheet("Invoices Including Paid Amount", 0)
//       set column width
        sheet1.setColumnView(0, 20)
        sheet1.setColumnView(1, 30)
        sheet1.setColumnView(2, 30)
        sheet1.setColumnView(3, 15)
        sheet1.setColumnView(4, 20)
        sheet1.setColumnView(5, 15)
        sheet1.setColumnView(6, 15)
        sheet1.setColumnView(7, 15)
        sheet1.setColumnView(8, 15)

//      set column name
        sheet1.addCell(new Label(0, 0, "Invoice Number"))
        sheet1.addCell(new Label(1, 0, "Payment Reference"))
        sheet1.addCell(new Label(2, 0, "Transaction Date"))
        sheet1.addCell(new Label(3, 0, "Due Date"))
        sheet1.addCell(new Label(4, 0, "Last Payment Date"))
        sheet1.addCell(new Label(5, 0, "Invoice Amount"))
        sheet1.addCell(new Label(6, 0, "Paid Amount"))
        sheet1.addCell(new Label(7, 0, "Due Amount"))
        sheet1.addCell(new Label(8, 0, "Paid Status"))

        String strQuery = "SELECT CONCAT(sp.prefix,'-',ii.invoice_no) AS invoiceNumber,ii.payment_ref,ii.trans_date as transDate,ii.booking_period,ROUND((ii.total_gl_amount + ii.total_vat),2) as invAmt,ROUND((ii.total_gl_amount + ii.total_vat) - SUM(tm.amount),2) AS paidAmount,ROUND(SUM(tm.amount),2) as remainAmount,ii.id,ii.booking_year, ii.due_date, MAX(tm.trans_date) as lastPaymentDate FROM system_prefix AS sp,invoice_income AS ii INNER JOIN trans_master AS tm ON tm.recenciliation_code = CONCAT(ii.id,'#1')  AND tm.account_code=(Select debitor_gl_code from debit_credit_gl_setup) WHERE ii.debtor_id = "+params.id+" AND sp.id=8 GROUP BY tm.recenciliation_code ORDER BY ii.trans_date"

        def invoicePaidAmountArr = new BudgetViewDatabaseService().executeCustomQuery(strQuery)

        for (int i = 0; i < invoicePaidAmountArr.size(); i++) {

            BigDecimal showInvoiceAmount = new BigDecimal(invoicePaidAmountArr[i][4])
            BigDecimal showPaidAmount = new BigDecimal(invoicePaidAmountArr[i][5])
            BigDecimal showDueAmount = new BigDecimal(invoicePaidAmountArr[i][6])

            Double dInvAmount = showInvoiceAmount.toDouble()
            Double dPaidAmount = showPaidAmount.toDouble()
            Double dDueAmount = showDueAmount.toDouble()
            String strPaidStatus = "Not Paid"
            if (dDueAmount == 0.00) {
                strPaidStatus = "Paid"
            } else if (dDueAmount < 0.00 && dInvAmount < dPaidAmount) {
                strPaidStatus = "Over Paid"
            }

            sheet1.addCell(new Label(0, nRowNo, invoicePaidAmountArr[i][0]))
            sheet1.addCell(new Label(1, nRowNo, invoicePaidAmountArr[i][1]))
            sheet1.addCell(new Label(2, nRowNo, new SimpleDateFormat("dd MMM yyyy").format(invoicePaidAmountArr[i][2])))
            sheet1.addCell(new Label(3, nRowNo, new SimpleDateFormat("dd MMM yyyy").format(invoicePaidAmountArr[i][9])))
            sheet1.addCell(new Label(4, nRowNo, new SimpleDateFormat("dd MMM yyyy").format(invoicePaidAmountArr[i][10])))
            sheet1.addCell(new Number(5, nRowNo, dInvAmount))
            sheet1.addCell(new Number(6, nRowNo, dPaidAmount))
            sheet1.addCell(new Number(7, nRowNo, dDueAmount))
            sheet1.addCell(new Label(8, nRowNo, strPaidStatus))
            nRowNo++;
        }

        //Getting income invoice data.

        int incresedRowNumber = nRowNo + 2
        sheet1.addCell(new Label(0, incresedRowNumber, "Bank Payment Id"))
        sheet1.addCell(new Label(1, incresedRowNumber, "Company Bank Account"))
        sheet1.addCell(new Label(2, incresedRowNumber, "Invoice Number"))
        sheet1.addCell(new Label(3, incresedRowNumber, "Payment Description"))
        sheet1.addCell(new Label(4, incresedRowNumber, "Date"))
        sheet1.addCell(new Label(5, incresedRowNumber, "Amount"))

        sheet1.setColumnView(0, 20)
        sheet1.setColumnView(1, 30)
        sheet1.setColumnView(2, 30)
        sheet1.setColumnView(3, 50)
        sheet1.setColumnView(4, 20)
        sheet1.setColumnView(5, 15)

        //For bank transaction details

        strQuery= "SELECT bsidf.bank_payment_id,IF(tm.trans_type=7,  bsidf.trans_bank_account_no, ' ') AS trans_bank_account_no,CONCAT(sp.prefix,'-',ii.invoice_no) AS invoiceNumber,IF(tm.trans_type=7,  bsidf.description, 'Write off by user.') AS description,IF(tm.trans_type=7,  bsidf.trans_date_time, DATE_FORMAT(tm.trans_date,'%y%m%d')) AS trans_date_time, IF(tm.trans_type=7,  bsidf.reconciliated_amount, ROUND((ii.total_gl_amount + ii.total_vat),2)) AS reconAmount FROM system_prefix AS sp,invoice_income AS ii INNER JOIN trans_master AS tm ON tm.recenciliation_code = CONCAT(ii.id,'#1') INNER JOIN bank_statement_import_details_final AS bsidf ON bsidf.bank_payment_id = tm.invoice_no WHERE ii.debtor_id = "+params.id+" AND tm.trans_type IN (7,8) AND sp.id=8 GROUP BY bsidf.bank_payment_id ORDER BY ii.trans_date"

        def bankPaymentInfoDetails = new BudgetViewDatabaseService().executeQuery(strQuery)


        for (int i = 0; i < bankPaymentInfoDetails.size(); i++) {
            sheet1.addCell(new Label(0, incresedRowNumber + 1, bankPaymentInfoDetails[i][0].toString()))
            sheet1.addCell(new Label(1, incresedRowNumber + 1, bankPaymentInfoDetails[i][1].toString()))
            sheet1.addCell(new Label(2, incresedRowNumber + 1, bankPaymentInfoDetails[i][2].toString()))
            sheet1.addCell(new Label(3, incresedRowNumber + 1, bankPaymentInfoDetails[i][3]))
            sheet1.addCell(new Label(4, incresedRowNumber + 1, bankPaymentInfoDetails[i][4]))
            sheet1.addCell(new Number(5, incresedRowNumber + 1, bankPaymentInfoDetails[i][5]))
            incresedRowNumber++
        }

        workbook.write()
        workbook.close()
    }*/

    def checkDebtor(){
        def customerName = request.getParameter("customerName")
        def customerId = request.getParameter("customerId")
        customerName = customerName.trim()
        String strQuery= "SELECT cm.id FROM customer_master AS cm WHERE cm.customer_name='"+customerName+"'"
        def alreadyCustomerExist = new BudgetViewDatabaseService().executeQuery(strQuery)
        if(customerId){
            strQuery = "SELECT cm.id FROM customer_master AS cm WHERE cm.id="+customerId
            def oldCustomer = new BudgetViewDatabaseService().executeQuery(strQuery)
            if(alreadyCustomerExist && oldCustomer && (oldCustomer[0][0] == alreadyCustomerExist[0][0])){
                render "Ok"
            }
            else{
                if(alreadyCustomerExist)
                    render "Bad"
                else
                    render "Ok"
            }
        }
        else{
            if(alreadyCustomerExist)
                render "Bad"
            else
                render "Ok"
        }

    }

   /* def exportExcelofOutstandingInvoices() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm")
        Date date = new Date()
        String strDateCreated = dateFormat.format(date)
        String fileName = "Outstanding_Invoices_Customer" + strDateCreated
        int nRowNo = 5
        def ActiveFiscalYear = new CoreParamsHelperTagLib().getActiveFiscalYear()
        def fiscalYearInfo = new CoreParamsHelperTagLib().getActiveFiscalYearInformation(ActiveFiscalYear)

        //Getting budget customer data.

        response.setContentType('application/vnd.ms-excel')
        response.setHeader('Content-Disposition', 'Attachment;Filename=' + fileName + '.xls')
        WritableWorkbook workbook = Workbook.createWorkbook(response.outputStream)

        WritableSheet sheet1 = workbook.createSheet("Outstanding Invoices", 0)
//       set column width
        sheet1.setColumnView(0, 20)
        sheet1.setColumnView(1, 30)
        sheet1.setColumnView(2, 30)
        sheet1.setColumnView(3, 15)
        sheet1.setColumnView(4, 20)
        sheet1.setColumnView(5, 15)
        sheet1.setColumnView(6, 15)
        sheet1.setColumnView(7, 15)
        sheet1.setColumnView(8, 15)

//      set column name
        sheet1.addCell(new Label(0, 4, "Payment Reference"))
        sheet1.addCell(new Label(1, 4, "Debtor Name"))
        sheet1.addCell(new Label(2, 4, "Customer"))
        sheet1.addCell(new Label(3, 4, "Invoice Date"))
        sheet1.addCell(new Label(4, 4, "Payment Terms"))
        sheet1.addCell(new Label(5, 4, "Days to Pay"))
        sheet1.addCell(new Label(6, 4, "Invoice Amount"))
        sheet1.addCell(new Label(7, 4, "Due Amount"))
        sheet1.addCell(new Label(8, 4, "Paid Amount"))

        def invoicePaidAmountArr = new BudgetViewDatabaseService().executeCustomQuery("SELECT * FROM(SELECT tblCusDebInfo.payment_ref as paymentRefference,tblCusDebInfo.customer_name as debtorName, tblCusDebInfo.vendor_name as customerName,DATE_FORMAT(tblCusDebInfo.trans_date,'%Y-%m-%d') as invoiceDate, tblCusDebInfo.terms as paymentTerms, DATE_FORMAT(tblCusDebInfo.due_date,'%Y-%m-%d') as dueDate,TO_DAYS(tblCusDebInfo.due_date)-TO_DAYS(NOW()) AS numOfDays,ROUND(tblCusDebInfo.invoicAmount,2) as invoiceAmount,if(ISNULL(tblPaidInvoice.invoicePaidAmount),0,ROUND(tblPaidInvoice.invoicePaidAmount,2)) as paidAmount,if(ISNULL(tblPaidInvoice.lastPaymentDate),TO_DAYS(NOW())-TO_DAYS(tblCusDebInfo.trans_date),TO_DAYS(tblPaidInvoice.lastPaymentDate)-TO_DAYS(tblCusDebInfo.trans_date)) AS paidDays, (TO_DAYS(NOW())-TO_DAYS(tblCusDebInfo.trans_date))-180 AS isSixMonths,tblCusDebInfo.creditLimit AS creditLimit, tblCusDebInfo.bebtorId as debtorId, tblCusDebInfo.customerId AS customerId, tblCusDebInfo.invoiceId AS invoiceId,tblPaidInvoice.paymentDescription as paymentDescription FROM(SELECT ii.payment_ref,debInfo.customer_name,cusInfo.vendor_name,ii.trans_date,pmtTerm.terms,ii.due_date,ii.total_gl_amount+ii.total_vat as invoicAmount, ii.id as invoiceId,debInfo.id as bebtorId,cusInfo.id as customerId,debInfo.credit_limit as creditLimit from invoice_income as ii INNER JOIN vendor_master as cusInfo on ii.customer_id = cusInfo.id INNER JOIN customer_master as debInfo on debInfo.id = ii.debtor_id INNER JOIN payment_terms as pmtTerm on ii.terms_id=pmtTerm.id) AS tblCusDebInfo LEFT JOIN (SELECT tblPaidAmount.invoiceId as invoiceId, tblPaidAmount.invoicePaidAmount as invoicePaidAmount, tblPaidAmount.lastPaymentDate as lastPaymentDate, bsf.description as paymentDescription from (SELECT ic.id AS invoiceId, sum(tm.amount*-1) AS invoicePaidAmount,MAX(tm.trans_date ) as lastPaymentDate,tm.invoice_no as invoiceNo FROM invoice_income AS ic INNER JOIN trans_master tm ON tm.recenciliation_code = CONCAT(ic.id,'#1') AND tm.account_code='1300' AND tm.trans_type <> 1 GROUP BY ic.id ORDER BY tm.trans_date) as tblPaidAmount INNER JOIN bank_statement_import_details_final as bsf ON bsf.bank_payment_id=tblPaidAmount.invoiceNo) AS tblPaidInvoice on tblCusDebInfo.invoiceId=tblPaidInvoice.invoiceId) AS tblFinalInfo WHERE tblFinalInfo.invoiceAmount<>tblFinalInfo.paidAmount and tblFinalInfo.debtorId="+params.id)

        def creditLimit = 0.0
        def totalAmount = 0.0
        def totalPaidAmount = 0.0
        for (int i = 0; i < invoicePaidAmountArr.size(); i++) {

            BigDecimal showInvoiceAmount = new BigDecimal(invoicePaidAmountArr[i][7])
            BigDecimal showPaidAmount = new BigDecimal(invoicePaidAmountArr[i][8])
            Long daysToPay = new Long(invoicePaidAmountArr[i][6])
            Double dInvAmount = showInvoiceAmount.toDouble()
            Double dPaidAmount = showPaidAmount.toDouble()
            Double dDueAmount = Math.round((dInvAmount-dPaidAmount)*100)/100
            creditLimit = invoicePaidAmountArr[i][11]
            totalAmount = totalAmount + dInvAmount
            totalPaidAmount = totalPaidAmount + dPaidAmount

            sheet1.addCell(new Label(0, nRowNo, invoicePaidAmountArr[i][0]))
            sheet1.addCell(new Label(1, nRowNo, invoicePaidAmountArr[i][1]))
            sheet1.addCell(new Label(2, nRowNo, invoicePaidAmountArr[i][2]))
            sheet1.addCell(new Label(3, nRowNo, invoicePaidAmountArr[i][3]))
            sheet1.addCell(new Label(4, nRowNo, invoicePaidAmountArr[i][4]))
            sheet1.addCell(new Number(5, nRowNo, daysToPay))
            sheet1.addCell(new Number(6, nRowNo, dInvAmount))
            sheet1.addCell(new Number(7, nRowNo, dDueAmount))
            sheet1.addCell(new Number(8, nRowNo, dPaidAmount))
            nRowNo++;
        }

        //Getting income invoice data.
        sheet1.addCell(new Label(0, 0, "Total invoice"))
        sheet1.addCell(new Number(1, 0, totalAmount))
        sheet1.addCell(new Label(0, 1, "Total amount outstanding"))
        sheet1.addCell(new Number(1, 1, Math.round((totalAmount-totalPaidAmount)*100)/100))
        sheet1.addCell(new Label(0, 2, "Balance credit limit"))
        sheet1.addCell(new Number(1, 2, Math.round((creditLimit - totalAmount + totalPaidAmount)*100)/100))

        workbook.write()
        workbook.close()
    }*/

    def bookingPeriodForChange() {
        def dropdwon = new CoreParamsHelperTagLib().getMonthDropDownForIncInv(params.customerIdForChange, params.bookingPeriod)

        Map<String, Object> result = [result: dropdwon]
        render result as JSON
    }

    def creditLimitInfo () {

    }
    def getCreditLimitsInfo () {

        String gridOutput
        invoiceUtil = new InvoiceUtil()

        int start = 0
        LinkedHashMap gridResult
        gridResult = customerMasterService.creditLimitsInfo();

        List debtorCreditLimitList = invoiceUtil.creditLimitWrapEntry(gridResult.creditLimitInfo)

        LinkedHashMap result = [data : debtorCreditLimitList.cell]
        gridOutput = result as JSON

        render gridOutput
    }
}