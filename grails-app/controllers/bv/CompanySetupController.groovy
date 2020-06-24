package bv

import factoring.BudgetViewDatabaseService
import factoring.CompanySetup
import grails.plugin.springsecurity.annotation.Secured
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.web.multipart.commons.CommonsMultipartFile
import org.springframework.web.servlet.support.RequestContextUtils

@Secured(["hasAnyRole('ROLE_ADMIN', 'ROLE_ACCOUNTANT', 'ROLE_USER')"])
class CompanySetupController {
    @Autowired
    BudgetViewDatabaseService budgetViewDatabaseService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {

        def compSetup = new BudgetViewDatabaseService().executeQueryAtSingle("SELECT id FROM CompanySetup")
//        println('compSetupNew=='+compSetupNew)
//        println('compSetup==' + compSetup)
        params.max = Math.min(max ?: 10, 100)
        params.sort = "id"
        params.order = "desc"

        String select = "a.id AS id,a.version AS version,a.address_line1 AS addressLine1,a.address_line2 AS addressLine2," +
                "a.city As city,a.company_full_name AS companyFullName,a.company_short_name As companyShortName, " +
                "a.chamber_commerce_no AS chamberCommerceNo,a.country AS country, " +
                "a.income_tax_reservation AS incomeTaxReservation,a.language AS LANGUAGE,a.logo as logoFile, " +
                "a.payment_term_id AS paymentTermId,a.postal_city AS postalCity," +
                "a.postal_state AS postalState,a.postal_country AS postalCountry,a.postal_zip_code AS postalZipCode,a.general_postal_code as generalPostalCode," +
                "a.report_page_size AS reportPageSize,a.show_itemcode_in_report AS itemNumberMention,a.show_glcode_in_report AS showGlcodeInReport,"+
                "a.vat_no AS vatNo,a.tax_no AS taxNo,a.vat_category_id AS vatCategoryId,website_address AS websiteAddress,phone_no AS phoneNo," +
                "email_address as emailAddress,postal_address_line1 as postalAddressLine1,iban as iban,a.bic,a.payment_iban AS paymentIban"

        String selectIndex = "id,version,addressLine1,addressLine2, " +
                "city,companyFullName,companyShortName, "+
                "chamberCommerceNo,country, "+
                "incomeTaxReservation,LANGUAGE,logoFile, "+
                "paymentTermId,postalCity, "+
                "postalState,postalCountry,postalZipCode,generalPostalCode,"+
                "reportPageSize,itemNumberMention,showGlcodeInReport,"+
                "vatNo,taxNo,vatCategoryId,websiteAddress,phoneNo,emailAddress,postalAddressLine1,iban,a.bic, paymentIban"

        String where = ""
        String orderBy = ""
        String from = "company_setup AS a"

        if (params.sort && params.order) {
            orderBy = "a.${params.sort} ${params.order}"
        } else {
            orderBy = "a.id ASC"
        }

        LinkedHashMap gridResult = budgetViewDatabaseService.select(select, from, where, orderBy, '', 'false', selectIndex, params.limit, params.offset)

        String whereInstance = ""
        whereInstance = "a.id=1"

        LinkedHashMap gridResultInstance = budgetViewDatabaseService.select(select, from, whereInstance, orderBy, '', 'false', selectIndex)
        def totalCount = new BudgetViewDatabaseService().executeQuery("SELECT count(a.id)  FROM company_setup AS a")

        if (compSetup[0]) {
            params.id = 1
            //[companySetupInstance: CompanySetup.get(params.id), companySetupInstanceList: CompanySetup.list(params), companySetupInstanceTotal: CompanySetup.count()]
            [companySetupInstance: gridResultInstance['dataGridList'][0], companySetupInstanceList: gridResult['dataGridList'], companySetupInstanceTotal: totalCount[0][0]]
        } else {
            //[companySetupInstance: new CompanySetup(params), companySetupInstanceList: CompanySetup.list(params), companySetupInstanceTotal: CompanySetup.count()]
            [companySetupInstanceList: gridResult['dataGridList'], companySetupInstanceTotal: totalCount[0][0], companySetupInstance: gridResultInstance['dataGridList'][0]]
        }
    }

    def create() {
        [companySetupInstance: new CompanySetup(params)]
    }

    def save(Integer max) {

        def requestFileName = request.getFile('logoFile')
        def pdfFile = request.getFile('pdfFile')

        if (!requestFileName) {
            flash.message = 'Please provide company Logo, it can not be empty!'
            params.max = Math.min(max ?: 10, 100)
            def companySetupInstance =  new CompanySetup(params)
            render(view: "list", model: [companySetupInstance: companySetupInstance, companySetupInstanceList: CompanySetup.list(params), companySetupInstanceTotal: CompanySetup.count()])
            //render(view: 'list', model: [companySetupInstance: companySetupInstance])
            return
        } else {
            def CommonsMultipartFile uploadedFile = params.logoFile
            def fileName = uploadedFile.originalFilename

            String imagePath = grailsAttributes.getApplicationContext().getResource("images/").getFile().toString() + File.separatorChar + fileName
            requestFileName.transferTo(new File(imagePath))
            //println(imagePath)
            params.logoFile = imagePath
        }

        if(pdfFile.empty){
            flash.message = 'File can not be empty!'
            render(view: 'list', model: [companySetupInstance: new CompanySetup(params), companySetupInstanceList: CompanySetup.list(params), companySetupInstanceTotal: CompanySetup.count()])
        }else{

            def fileName = pdfFile.originalFilename
            String pdfPath = grailsAttributes.getApplicationContext().getResource("PDF/").getFile().toString() + File.separatorChar + fileName
            file.transferTo(new File(pdfPath))
        }

        def companySetupInstance = new CompanySetup(params)
        if (!companySetupInstance.save(flush: true)) {
            // render(view: "list", model: [companySetupInstance: companySetupInstance])
            params.max = Math.min(max ?: 10, 100)
            render(view: 'list', model: [companySetupInstance: new CompanySetup(params), companySetupInstanceList: CompanySetup.list(params), companySetupInstanceTotal: CompanySetup.count()])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'companySetup.label', default: 'CompanySetup'), companySetupInstance.id])
        //redirect(action: "list")
        params.max = Math.min(max ?: 10, 100)
        render(view: 'list', model: [companySetupInstance: new CompanySetup(params), companySetupInstanceList: CompanySetup.list(params), companySetupInstanceTotal: CompanySetup.count()])

    }


    def close() {
        redirect(action: "list", params: [offset: params.offset, max: params.max, order: params.order])
    }

    def show(Long id) {
        def companySetupInstance = CompanySetup.get(id)
        if (!companySetupInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'companySetup.label', default: 'CompanySetup'), id])
            redirect(action: "list")
            return
        }

        [companySetupInstance: companySetupInstance]
    }

    def edit(Long id) {
        def companySetupInstance = CompanySetup.get(id)
        if (!companySetupInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'companySetup.label', default: 'CompanySetup'), id])
            redirect(action: "list")
            return
        }

        [companySetupInstance: companySetupInstance]
    }

    def update(Long id, Long version) {

        String select = "id,version"
        String selectIndex = "id,version"
        String from = "company_setup"
        String where = "id='" + id + "'"
        String orderBy = "id ASC"

        LinkedHashMap gridResult = budgetViewDatabaseService.select(select, from, where, orderBy, '', 'false', selectIndex)

        def companySetupInstance = ""

        if (gridResult['dataGridList'].size()) {
            companySetupInstance = gridResult['dataGridList'][0]
        }

        if (!companySetupInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'companySetup.label', default: 'CompanySetup'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (companySetupInstance.version > version) {
                companySetupInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'companySetup.label', default: 'CompanySetup')] as Object[],
                        "Another user has updated this CompanySetup while you were editing")
                render(view: "edit", model: [companySetupInstance: companySetupInstance])
                return
            }
        }

        //println("Logo File : "+params.logoFile)

        def requestFileName = request.getFile('logoFileSelector')

        if (requestFileName) {
            def CommonsMultipartFile uploadedFile = params.logoFileSelector
            def fileName = uploadedFile.originalFilename
            def imagePath = grailsApplication.mainContext.servletContext.getRealPath("/images/companylogo/" + "${fileName}")
            requestFileName.transferTo(new File(imagePath))
            //println(imagePath)
            params.logoFile = fileName
        }

        Map updatedValue = [
                address_line1                 : "${params.addressLine1}",
                address_line2                 : "${params.addressLine2}",
                city                          : "${params.city}",
                company_full_name             : "${params.companyFullName}",
                company_short_name            : "${params.companyShortName}",
                chamber_commerce_no           : "${params.chamberCommerceNo}",
//                currency_id                   : "${params.currency.id}",
                country                       : "${params.country}",
                income_tax_reservation        : "${params.incomeTaxReservation}",
                language                      : "${params.LANGUAGE}",
                logo                          : "${params.logoFile}",
//                moment_of_sending_invoice_id  : "${params.momentOfSendingInvoice.id}",
                payment_term_id               : "${params.paymentTermId}",
                postal_address_line1          : "${params.postalAddressLine1}",
                postal_city                   : "${params.postalCity}",
                postal_state                  : "${params.postalState}",
                postal_country                : "${params.postalCountry}",
                postal_zip_code               : "${params.postalZipCode}",
                general_postal_code           : "${params.postCode}",
                report_page_size              : "${params.reportPageSize}",
                show_itemcode_in_report       : "${params.itemNumberMention}",
                show_glcode_in_report         : "${params.showGlcodeInReport}",
                tax_no                        :  "${params.taxNo}",
                vat_no                        : "${params.vatNo}",
                vat_category_id               : "${params.defaultVATId}",
                iban                          : "${params.iban}",
                phone_no                      : "${params.phoneNo}",
                website_address               : "${params.websiteAddress}",
                email_address                 : "${params.email}",
                bic                           : "${params.bic}",
                payment_iban                  : "${params.paymentIban}"
        ]

        def updatedTableName = "company_setup"
        def updatedWhereString = "id=" + "'" + id + "'"
        try {
            budgetViewDatabaseService.update(updatedValue, updatedTableName, updatedWhereString)
        } catch (e) {
        }

        //Update session data.
        //println(session.companyInfo);
//        session.companyInfo[0].currency_id = params.currency.id

        //Change the UI as per changed language
        String resultSetLn = params.LANGUAGE

        def locale = new Locale(resultSetLn, resultSetLn.toUpperCase(), "")
        RequestContextUtils.getLocaleResolver(request).setLocale(request, response, locale)
        //End language update

        //println(params.LANGUAGE + " "+ id)
        //flash.message = message(code: 'default.updated.message', args: [message(code: 'companySetup.label', default: 'CompanySetup'), companySetupInstance.id])
        flash.message = message(code: 'com.updated.message', args: [message(code: 'companySetup.label', default: 'Company Setup')])
        redirect(action: "list")
    }

    def delete(Long id) {
        def companySetupInstance = CompanySetup.get(id)
        if (!companySetupInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'companySetup.label', default: 'CompanySetup'), id])
            redirect(action: "list")
            return
        }

        try {
            companySetupInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'companySetup.label', default: 'CompanySetup'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'companySetup.label', default: 'CompanySetup'), id])
            redirect(action: "show", id: id)
        }
    }
}
