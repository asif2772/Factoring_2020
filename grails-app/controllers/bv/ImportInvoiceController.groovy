package bv

import factoring.BudgetViewDatabaseService
import factoring.CoreParamsHelperTagLib
import factoring.ImportInvoiceService
import factoring.IncomeInvoiceService
import factoring.QuickEntryService
import factoring.QuickEntryUtil
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import org.springframework.beans.factory.annotation.Autowired

import java.text.SimpleDateFormat

@Secured(["hasAnyRole('ROLE_ADMIN', 'ROLE_ACCOUNTANT', 'ROLE_USER')"])
class ImportInvoiceController {

    SpringSecurityService springSecurityService
    ImportInvoiceService importInvoiceService
    QuickEntryService quickEntryService
    QuickEntryUtil quickEntryUtil
    IncomeInvoiceService incomeInvoiceService
    @Autowired
    BudgetViewDatabaseService budgetViewDatabaseService

    def index() {}

    def importInvoice(){
        quickEntryUtil = new QuickEntryUtil()
        if(params.editId){
            def invoiceInfo = importInvoiceService.getInvoiceInfo(params.editId)
            [invoiceInfo:invoiceInfo]
        }
        else{
            //def invoiceList = ImportInvoice.executeQuery("from ImportInvoice as ii where ii.isValidDebtor=:isValidDebtor",[isValidDebtor:0])
            def invoiceList = budgetViewDatabaseService.executeQuery("SELECT * FROM import_invoice AS ii WHERE ii.is_valid_debtor=0")
            def isDataAvailable = false
            def debtorSuggestionList = []
            def debtorInactiveList = []
            def debtorDifferentAddressList = []
            if(invoiceList){
                isDataAvailable = true
                def debtorList = invoiceList.groupBy {it -> it[9]}
                for(debtor in debtorList){
                    def debtorSuggestionMap = [
                            debtorName: "",
                            isNewDebtor:false,
                            debtorSuggestion:[],
                            id:''
                    ]
                    def foundDebtor = budgetViewDatabaseService.executeQuery("SELECT * FROM customer_master AS cm WHERE cm.customer_name='"+debtor.key+"'")
                    if(!foundDebtor){
                        debtorSuggestionMap.debtorName = debtor.key
                        debtorSuggestionMap.isNewDebtor = true
                        debtorSuggestionMap.debtorSuggestion = budgetViewDatabaseService.executeQuery("SELECT * FROM customer_master AS cm WHERE cm.customer_name LIKE '%"+debtor.key+"%'")
                        def forId = debtor.getValue()
                        debtorSuggestionMap.id = forId.get(0)[0]
                        debtorSuggestionList.add(debtorSuggestionMap)
                    }
                    if(foundDebtor && foundDebtor[0][18]==2){
                        debtorInactiveList.add(foundDebtor)
                    }
                }
                for(debtorData in invoiceList){
                    def isDebtorAddressDifferent = quickEntryUtil.checkDebtorAddressDifferentByDebtorName(debtorData[9],debtorData[2])
                    if(isDebtorAddressDifferent){
                        debtorDifferentAddressList.add(debtorData)
                    }
                }
            }
            [debtorInactiveList:debtorInactiveList,debtorSuggestionList:debtorSuggestionList,isDataAvailable:isDataAvailable,debtorDifferentAddressList:debtorDifferentAddressList]
        }
    }

    def searchDebtor(){
        def strSearchDebtor = params.strDebtor
        def debtorName = params.debtor
        def debtorList = budgetViewDatabaseService.executeQuery("SELECT * FROM customer_master AS cm WHERE cm.customer_name LIKE '%"+strSearchDebtor+"%' order by cm.customer_name")
        [debtorList:debtorList,debtorName:debtorName]
    }

    def changeImportInvoice(){
        String originalDebtorNameWithCap = params.originalDebtor
        String firstLetterOfDebtor = originalDebtorNameWithCap.substring(0,1).toUpperCase()
        String restLettersOfDebtor = originalDebtorNameWithCap.substring(1).toLowerCase()
        def originalDebtorName = firstLetterOfDebtor + restLettersOfDebtor
        def toChangeDebtorName = params.toChangeDebtor
        def accept = params.accept
        def reject = params.reject
        def acceptAddress = params.acceptAddress
        def rejectAddress = params.rejectAddress
        if(accept && accept.equals("yes")){
            def changeInvoiceList = budgetViewDatabaseService.executeQuery("SELECT * FROM import_invoice AS ii WHERE ii.id='${originalDebtorNameWithCap}'")
            for(changeInvoice in changeInvoiceList){
                Map updatedValue = [
                        is_valid_debtor : 1
                ]
                String updatedTableName = "import_invoice"
                String updatedWhereString = "id='${changeInvoice[0]}'"

                budgetViewDatabaseService.update(updatedValue,updatedTableName,updatedWhereString)
            }
        }
        else if(reject && reject.equals("yes")){
            def changeInvoiceList = budgetViewDatabaseService.executeQuery("SELECT * FROM import_invoice AS ii WHERE ii.id='${originalDebtorNameWithCap}'")
            for(changeInvoice in changeInvoiceList){
                String deleteTableName = "import_invoice"
                String deleteWhereString = "id='${changeInvoice[0]}'"
                budgetViewDatabaseService.delete(deleteTableName,deleteWhereString)
            }
        }
        else if(toChangeDebtorName){
            def originalDebtorList = budgetViewDatabaseService.executeQuery("SELECT * FROM import_invoice AS ii WHERE ii.debtor_name='"+originalDebtorName+"'")
            def toChangeDebtor = budgetViewDatabaseService.executeQuery("SELECT * FROM customer_master AS cm WHERE cm.customer_name='"+toChangeDebtorName+"'")
            for(originalDebtor in originalDebtorList){
                Map updatedValue = [
                        debtor_name : "${toChangeDebtor[0][8]}"
                ]
                String updatedTableName = "import_invoice"
                String updatedWhereString = "id='"+originalDebtor[0]+"'"
                budgetViewDatabaseService.update(updatedValue,updatedTableName,updatedWhereString)
            }
        }
        else if(acceptAddress && acceptAddress.equals("yes")){
            def changeInvoiceList = budgetViewDatabaseService.executeQuery("SELECT * FROM import_invoice AS ii WHERE ii.debtor_name='"+originalDebtorName+"'")
            for(changeInvoice in changeInvoiceList){
                Map updatedValue = [
                        is_valid_debtor : 2
                ]
                String updatedTableName = "import_invoice"
                String updatedWhereString = "debtor_name='"+changeInvoice[9]+"'"
                budgetViewDatabaseService.update(updatedValue,updatedTableName,updatedWhereString)
            }
        }
        else if(rejectAddress && rejectAddress.equals("yes")){
            def originalDebtorList = budgetViewDatabaseService.executeQuery("SELECT * FROM import_invoice AS ii WHERE ii.debtor_name='"+originalDebtorName+"'")
            def toChangeDebtor = budgetViewDatabaseService.executeQuery("SELECT * FROM customer_master AS cm WHERE cm.customer_name='"+originalDebtorName+"'")
            def debtorCustomerGeneralAddress = budgetViewDatabaseService.executeQuery("SELECT * FROM customer_general_address AS cga WHERE cga.customer_id="+toChangeDebtor[0][0])
            for(originalDebtor in originalDebtorList){
                Map updatedValue = [
                        addressLine1        : debtorCustomerGeneralAddress[0][2]
                ]
                String updatedTableName = "import_invoice"
                String updatedWhereString = "id='"+originalDebtor[0]+"'"
                budgetViewDatabaseService.update(updatedValue,updatedTableName,updatedWhereString)
            }
        }
        redirect(action: "importInvoice")
    }

    def importInvoiceFile(){
        def user = springSecurityService.currentUser
        def vendorMaster
        try{
            vendorMaster = budgetViewDatabaseService.executeQuery("select * from vendor_master AS vm WHERE vm.id="+params.customerId)
        }
        catch (Exception e){
        }
        def uploadedFile = request.getFile('xlsxFile')
        if(vendorMaster && uploadedFile){
            def fileName = uploadedFile.originalFilename
            def webRootDir = servletContext.getRealPath("/")+ "importInvoices"
            File userDir = new File(webRootDir)
            if(!userDir.exists()){
                userDir.mkdir()
            }
            def path = userDir.toString()+ File.separatorChar+fileName
            uploadedFile.transferTo(new File(path))
            importInvoiceService.readXlsx(path,user,vendorMaster,params.bookingPeriod)
            flash.message = message(code: 'bv.uploadPDF.confirmationMessage')
            redirect(action: "importInvoice")
        }
        else{
            redirect(action: "importInvoice")
        }
    }

    def showInvoiceList(){
        quickEntryUtil = new QuickEntryUtil()
        String gridOutput
        LinkedHashMap gridResult
        gridResult = quickEntryService.getImportInvoices()
        List expInvList = quickEntryUtil.importInvoiceInGrid(gridResult.importInvoiceList)
        LinkedHashMap result = [draw: 1, recordsTotal: gridResult.count, recordsFiltered: gridResult.count, data: expInvList.cell]
        gridOutput = result as JSON
        render gridOutput
    }

    def deleteAllImportInvoice(){
        budgetViewDatabaseService.updateByString("DELETE FROM  import_invoice")
        Map<String, Object> result = [result: 'success']
        render result as JSON
    }

    def deleteImportInvoice(){
        def invoiceId = params.invoiceId
        budgetViewDatabaseService.updateByString("DELETE FROM  import_invoice WHERE id= " + invoiceId)
        Map<String, Object> result = [result: 'success']
        render result as JSON
    }

    def deleteImportInvoiceById(def invoiceId){
        budgetViewDatabaseService.updateByString("DELETE FROM  import_invoice WHERE id= " + invoiceId)
    }

    def updateInvoice(){
        def id = params.id
        def tempDate = params.invoiceDate
        def originalFormat = new SimpleDateFormat("dd-MM-yyyy")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        Date date = originalFormat.parse(tempDate)
        def invoiceDate = sdf.format(date)

        Map updatedValue=[
                debtorName:params.debtorName,
                invoiceDate:invoiceDate,
                vendorId:params.vendorId,
                total:params.totalAmntWithVat,
                subtotal:params.totalAmntWithOutVat,
                vat:params.vat,
                debtorContactPerson:params.debtorContactPerson,
                paymentRef:params.paymentRef,
                iban:params.iban
        ]
        def updatedTableName="importInvoice"
        def updatedWhereString="id="+"'"+id+"'"
        budgetViewDatabaseService.update(updatedValue,updatedTableName,updatedWhereString)
        Map<String, Object> result = [result: 'success']
        render result as JSON
    }

    def processInvoice(){
        quickEntryUtil = new QuickEntryUtil()
        def isProcessable = true
        def invoiceListData = budgetViewDatabaseService.executeQuery("SELECT * FROM import_invoice AS ii WHERE ii.is_valid_debtor=0 OR ii.is_valid_debtor=2")
        def debtorList = invoiceListData.groupBy {it -> it[9]}
        for(debtor in debtorList){
            def foundDebtor = budgetViewDatabaseService.executeQuery("SELECT * FROM customer_master AS cm WHERE cm.customer_name='"+debtor.key+"'")
            if(!foundDebtor){
                isProcessable = false
                break
            }
            if(foundDebtor && foundDebtor[0][18]==2){
                isProcessable = false
                break
            }
        }

        for(debtorData in invoiceListData){
            if(debtorData[21]==2)
                continue
            def isDebtorAddressDifferent = quickEntryUtil.checkDebtorAddressDifferentByDebtorName(debtorData[9],debtorData[2])
            if(isDebtorAddressDifferent){
                isProcessable = false
                break
            }
        }

        if(!isProcessable){
            Map<String, Object> result = [result: 'success']
            render result as JSON
        }
        else{
            Map reportData = [
                    totalRecords        : 0,
                    processedRecords    : 0,
                    duplicateRecords    : 0,
                    vatNotMatched       : 0,
                    newDebtorCreated    : 0,
                    debtorAddressChanged: 0,
                    fiscalYearClosed    : 0

            ]
            def gridResult = quickEntryService.getImportInvoices()
            def invoiceList = gridResult.importInvoiceList
            def userId = springSecurityService.principal.id
            for (int i = 0; i < invoiceList.size(); i++) {
                reportData.totalRecords += 1
                def invoiceEntry = invoiceList[i]
                def debtorName = invoiceEntry.debtorName
                def invoiceDate = invoiceEntry.invoiceDate
                def bookingPeriod = budgetViewDatabaseService.executeQuery("SELECT booking_period from import_invoice limit 1")
                def isInvoiceProcessed = quickEntryUtil.checkInvoiceAlreadyProcess(invoiceEntry.vendorId,debtorName,invoiceEntry.subtotal,invoiceDate,invoiceEntry.paymentRef)
                def isDuplicateRow = quickEntryUtil.checkDuplicateRowImportFile(debtorName, invoiceEntry.total, invoiceDate,invoiceEntry.paymentRef)
                def isBookingYearValid = quickEntryUtil.checkBookingYearValid(invoiceDate)
                def isVatMatch = quickEntryUtil.checkVatMatched(invoiceEntry.subtotal, invoiceEntry.total,invoiceEntry.vat)
                def isDebtorAddressDifferent = quickEntryUtil.checkDebtorAddressDifferent(invoiceEntry)
                if(isDebtorAddressDifferent)
                    reportData.debtorAddressChanged += 1
                if(!isVatMatch)
                    reportData.vatNotMatched += 1
                if(isDuplicateRow)
                    reportData.duplicateRecords += 1
                if(!isBookingYearValid)
                    reportData.fiscalYearClosed += 1
                if(!isInvoiceProcessed && !isDuplicateRow && isBookingYearValid && isVatMatch){
                    def isValidDate  = new CoreParamsHelperTagLib().isDateClosedFiscalYear(invoiceDate)
                    if(isValidDate){
                        def customerDebtorMap = importInvoiceService.createImportCustomerDebtor(invoiceEntry,reportData)
                        incomeInvoiceService.saveImportedIncomeInvoice(invoiceEntry,userId,customerDebtorMap,reportData,bookingPeriod[0][0])
                        deleteImportInvoiceById(invoiceEntry.id)
                    }
                }
            }
            flash.message = "Total Records: " + reportData.totalRecords + "<br>"
            flash.message = flash.message + "Processed Records: " + reportData.processedRecords + "<br>"
            flash.message = flash.message + "Duplicate Records: " + reportData.duplicateRecords + "<br>"
            flash.message = flash.message + "Vat not Matched Records: " + reportData.vatNotMatched + "<br>"
            flash.message = flash.message + "New debtor Created: " + reportData.newDebtorCreated + "<br>"
            flash.message = flash.message + "Debtor Address Changed: " + reportData.debtorAddressChanged + "<br>"
            flash.message = flash.message + "Fiscal Year Closed or Inactive: " + reportData.fiscalYearClosed + "<br>"

            Map<String, Object> result = [result: 'success']
            render result as JSON
        }
    }

    def processDuplicateInvoice(){
        quickEntryUtil = new QuickEntryUtil()
        def isProcessable = true
        def invoiceListData = budgetViewDatabaseService.executeQuery("SELECT * FROM import_invoice AS ii WHERE ii.is_valid_debtor=0 OR ii.is_valid_debtor=2")
        def debtorList = invoiceListData.groupBy {it -> it[9]}
        for(debtor in debtorList){
            def foundDebtor = budgetViewDatabaseService.executeQuery("SELECT * FROM customer_master AS cm WHERE cm.customer_name='"+debtor.key+"'")
            if(!foundDebtor){
                isProcessable = false
                break
            }
            if(foundDebtor && foundDebtor[0][18]==2){
                isProcessable = false
                break
            }
        }

        for(debtorData in invoiceListData){
            if(debtorData[21]==2)
                continue
            def isDebtorAddressDifferent = quickEntryUtil.checkDebtorAddressDifferentByDebtorName(debtorData[9],debtorData[2])
            if(isDebtorAddressDifferent){
                isProcessable = false
                break
            }
        }

        if(!isProcessable){
            Map<String, Object> result = [result: 'success']
            render result as JSON
        }
        else{
            Map reportData = [
                    totalRecords        : 0,
                    processedRecords    : 0,
                    duplicateRecords    : 0,
                    vatNotMatched       : 0,
                    newDebtorCreated    : 0,
                    debtorAddressChanged: 0,
                    fiscalYearClosed    : 0

            ]

            def gridResult = quickEntryService.getImportInvoices()
            def invoiceList = gridResult.importInvoiceList
            def userId = springSecurityService.principal.id
            for (int i = 0; i < invoiceList.size(); i++) {
                reportData.totalRecords += 1
                def invoiceEntry = invoiceList[i]
                def debtorName = invoiceEntry.debtorName
                def invoiceDate = invoiceEntry.invoiceDate
                def bookingPeriod = budgetViewDatabaseService.executeQuery("SELECT booking_period from import_invoice limit 1")
                //print("dup:" +bookingPeriod)
                def isInvoiceProcessed = quickEntryUtil.checkInvoiceAlreadyProcess(invoiceEntry.vendorId,debtorName,invoiceEntry.subtotal,invoiceDate,invoiceEntry.paymentRef)
                def isDuplicateRow = quickEntryUtil.checkDuplicateRowImportFile(debtorName, invoiceEntry.total, invoiceDate,invoiceEntry.paymentRef)
                def isVatMatch = quickEntryUtil.checkVatMatched(invoiceEntry.subtotal, invoiceEntry.total,invoiceEntry.vat)
                def isDebtorAddressDifferent = quickEntryUtil.checkDebtorAddressDifferent(invoiceEntry)
                if(isDebtorAddressDifferent)
                    reportData.debtorAddressChanged += 1
                if(!isVatMatch)
                    reportData.vatNotMatched += 1
                if(isDuplicateRow)
                    reportData.duplicateRecords += 1
                if(!isInvoiceProcessed && isDuplicateRow){
                    def duplicateGridResult = quickEntryService.getDuplicateImportInvoices(debtorName, invoiceEntry.total, invoiceDate)
                    def allDuplicateList = duplicateGridResult.importInvoiceList
                    def isFiscalYearDuplicateProcessed = false
                    for(int j=0; j<allDuplicateList.size();j++){
                        def duplicateRowEntry = allDuplicateList[j]
                        def isBookingYearValid = quickEntryUtil.checkBookingYearValid(duplicateRowEntry.invoiceDate)
                        if(!isBookingYearValid && !isFiscalYearDuplicateProcessed){
                            isFiscalYearDuplicateProcessed = true
                            reportData.fiscalYearClosed += 1
                        }
                        if(isBookingYearValid){
                            def isValidDate  = new CoreParamsHelperTagLib().isDateClosedFiscalYear(invoiceDate)
                            if(isValidDate){
                                def customerDebtorMap = importInvoiceService.createImportCustomerDebtor(duplicateRowEntry,reportData)
                                incomeInvoiceService.saveImportedIncomeInvoice(duplicateRowEntry,userId,customerDebtorMap,reportData,bookingPeriod[0][0])
                                deleteImportInvoiceById(duplicateRowEntry.id)
                            }
                        }
                    }
                }
                else{
                    def isBookingYearValid = quickEntryUtil.checkBookingYearValid(invoiceDate)
                    if(!isBookingYearValid)
                        reportData.fiscalYearClosed += 1
                    if(!isInvoiceProcessed && isBookingYearValid){
                        def isValidDate  = new CoreParamsHelperTagLib().isDateClosedFiscalYear(invoiceDate)
                        if(isValidDate){
                            def customerDebtorMap = importInvoiceService.createImportCustomerDebtor(invoiceEntry,reportData)
                            incomeInvoiceService.saveImportedIncomeInvoice(invoiceEntry,userId,customerDebtorMap,reportData,bookingPeriod[0][0])
                            deleteImportInvoiceById(invoiceEntry.id)
                        }
                    }
                }

            }

            flash.message = "Total Records: " + reportData.totalRecords + "<br>"
            flash.message = flash.message + "Processed Records: " + reportData.processedRecords + "<br>"
            flash.message = flash.message + "Duplicate Records: " + reportData.duplicateRecords + "<br>"
            flash.message = flash.message + "Vat not Matched Records: " + reportData.vatNotMatched + "<br>"
            flash.message = flash.message + "New debtor Created: " + reportData.newDebtorCreated + "<br>"
            flash.message = flash.message + "Debtor Address Changed: " + reportData.debtorAddressChanged + "<br>"
            flash.message = flash.message + "Fiscal Year Closed or Inactive: " + reportData.fiscalYearClosed + "<br>"

            Map<String, Object> result = [result: 'success']
            render result as JSON
        }
    }
}
