package factoring

import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.*
import org.springframework.beans.factory.annotation.Autowired

import java.text.DecimalFormat
import java.text.SimpleDateFormat

@Transactional
class ImportInvoiceService {

    SpringSecurityService springSecurityService
    DebtorCustomerService debtorCustomerService
    @Autowired
    BudgetViewDatabaseService budgetViewDatabaseService

    QuickEntryUtil quickEntryUtil = new QuickEntryUtil()

    def readXlsx(def filePath, def user,def vendorMaster, def bookingPeriod) {
        try {
            saveImportInvoice(filePath, user,vendorMaster, bookingPeriod)
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    def createImportCustomerDebtor(def invoiceEntry, def reportData=null) {
        def customerDebtorId
        def debtorId
        def customer
        def debtor
        try{
            customer = budgetViewDatabaseService.executeQuery("SELECT * FROM vendor_master AS vm WHERE vm.id="+invoiceEntry.vendorId)
            debtor = budgetViewDatabaseService.executeQuery("SELECT * FROM customer_master AS cm WHERE cm.customer_name='"+invoiceEntry.debtorName+"'")
        }
        catch(Exception e){

        }
        if(customer && debtor){
            debtorId = debtor[0][0]
            customerDebtorId = debtorCustomerService.getDebtorCustomerId(debtor[0][0],customer[0][0])
            if(customerDebtorId == 0)
                customerDebtorId = debtorCustomerService.createDefaultCustomerDebtor(debtor[0][0],customer[0][0],invoiceEntry.paymentTermsDays)
        }
        else if(customer && !debtor){
            debtorId = createNewDebtor(invoiceEntry,reportData)
            if(debtorId)
                reportData.newDebtorCreated += 1
            customerDebtorId = debtorCustomerService.createDefaultCustomerDebtor(debtorId,customer[0][0],invoiceEntry.paymentTermsDays)
        }
        return [debtorId:debtorId,customerId:customer[0][0],customerDebtorId:customerDebtorId]
    }

    def createNewDebtor(def invoiceEntry,def reportData=null){
        def paymentTermsDays = invoiceEntry.paymentTermsDays
        def paymentData = budgetViewDatabaseService.executeCustomQuery("SELECT id from payment_terms WHERE days_before_due BETWEEN ${paymentTermsDays} AND ${paymentTermsDays+4}")
        def termsId = 1
        if(paymentData)
            termsId = paymentData[0][0]
        def customerCode = new CoreParamsHelperTagLib().getNextGeneratedNumber('customer')
        def status = 1
        def customerType = "cn"
        def defaultGlAccount = "8000"
        def debtorMail = invoiceEntry.debtorMail?invoiceEntry.debtorMail:""
        Map insertedValue = [
                cham_of_commerce  : "",
                comments          : "",
                customer_name     : "${invoiceEntry.debtorName}",
                default_gl_account: "${defaultGlAccount}",
                email             : "${debtorMail}",
                first_name        : "",
                last_name         : "",
                middle_name       : "",
                payment_term_id   : termsId,
                vat               : "1",
                customer_type     : "${customerType}",
                status            : "${status}",
                credit_status     : "Good History",
                credit_limit      : "1000.0",
                customer_code     : "${customerCode}"
        ]

        def tableName = "customer_master"
        Integer customerMasterInstanceId = budgetViewDatabaseService.insert(insertedValue,tableName)
        def phoneNo = invoiceEntry.debtorPhone?invoiceEntry.debtorPhone:""
        def contactPersonName = invoiceEntry.debtorContactPerson?invoiceEntry.debtorContactPerson:""
        def debtorCity = invoiceEntry.debtorCity?invoiceEntry.debtorCity:""
        def country = budgetViewDatabaseService.executeQuery("SELECT * FROM countries AS c WHERE c.name='"+invoiceEntry.debtorCountry+"'")

        def countryId = 2
        if(country)
            countryId = country[0][0]
        def debtorAreaCode = invoiceEntry.debtorAreaCode?invoiceEntry.debtorAreaCode:""
        def addressLine1 = invoiceEntry.addressLine1?invoiceEntry.addressLine1:""
        def addressLine2 = invoiceEntry.addressLine2?invoiceEntry.addressLine2:""
        Map insertCustomerGeneralAddress=[
                phone_no            : "${phoneNo}",
                website_address     : "",
                customer_id         : "${customerMasterInstanceId}",
                city                : "${debtorCity}",
                contact_person_name : "${contactPersonName}",
                country_id          : countryId,
                postal_code         : "${debtorAreaCode}",
                address_line1       : "${addressLine1}",
                address_line2       : "${addressLine2}",
                status              : 1
        ]

        def table = "customer_general_address"
        Integer customerGeneralAddressInstanceId = budgetViewDatabaseService.insert(insertCustomerGeneralAddress, table)

        return customerMasterInstanceId;

    }


    def saveImportInvoice(def filePath, def user,def vendorMaster, def bookingPeriod) {

        DecimalFormat df = new DecimalFormat("#.##")
        FileInputStream fis = null
        try {
            fis = new FileInputStream(filePath)
//            Workbook workbook = new XSSFWorkbook(fis)
            Workbook workbook = new HSSFWorkbook(fis)  // this is for new version
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator()
            int numberOfSheets = workbook.getNumberOfSheets()

            for (int i = 0; i < numberOfSheets; i++) {

                Sheet sheet = workbook.getSheetAt(i)
                Iterator rowIterator = sheet.iterator()
                boolean fileOk = true

                def flag = 0
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next()
                    if (flag != 0) {

                        Map invoiceMap = [
                                "vendorId":vendorMaster[0][0],
                                "vendorName":vendorMaster[0][20],
                                "debtorName":'',
                                "invoiceDate":'',
                                "total":0,
                                "subtotal":0,
                                "vat":0,
                                "paymentTermsDays":'',
                                "paymentRef":'',
                                "debtorContactPerson":'',
                                "addressLine1":'',
                                "addressLine2":'',
                                "debtorAreaCode":'',
                                "debtorCity":'',
                                "debtorCountry":'',
                                "iban":'',
                                "debtorMail":'',
                                "debtorPhone":'',
                                "isValidDebtor":0,
                                "userId":user.id,
                                "bookingPeriod":bookingPeriod]

                        Iterator cellIterator = row.cellIterator()
                        while (cellIterator.hasNext()) {
                            Cell cell = cellIterator.next()
                            if (cell.getColumnIndex() == 0) {
                                invoiceMap.debtorName = quickEntryUtil.getCellValue(cell,XlsxCellType.STRING)
                            } else if (cell.getColumnIndex() == 1) {
                                Date tmpDate = quickEntryUtil.getCellValue(cell,XlsxCellType.DATE)
                                if(tmpDate == null ){
                                    fileOk = false;
                                    break;
                                }
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                invoiceMap.invoiceDate = sdf.format(tmpDate)
                            } else if (cell.getColumnIndex() == 2) {
                                def total = df.format(quickEntryUtil.getCellValue(cell,XlsxCellType.DECIMAL,evaluator))
                                invoiceMap.total = total
                            } else if (cell.getColumnIndex() == 3) {
                                def subtotal = df.format(quickEntryUtil.getCellValue(cell,XlsxCellType.DECIMAL,evaluator))
                                invoiceMap.subtotal = subtotal
                            } else if (cell.getColumnIndex() == 4) {
                                def vat = df.format(quickEntryUtil.getCellValue(cell,XlsxCellType.DECIMAL,evaluator))
                                invoiceMap.vat = vat
                            } else if (cell.getColumnIndex() == 5) {
                                invoiceMap.paymentTermsDays = quickEntryUtil.getCellValue(cell,XlsxCellType.DECIMAL,evaluator)
                            } else if (cell.getColumnIndex() == 6) {
                                invoiceMap.paymentRef = quickEntryUtil.getCellValue(cell,XlsxCellType.STRING)
                            } else if (cell.getColumnIndex() == 7) {
                                invoiceMap.debtorContactPerson = quickEntryUtil.getCellValue(cell,XlsxCellType.STRING)
                            } else if (cell.getColumnIndex() == 8) {
                                invoiceMap.addressLine1 = quickEntryUtil.getCellValue(cell,XlsxCellType.STRING)
                            } else if (cell.getColumnIndex() == 9) {
                                invoiceMap.addressLine2 = quickEntryUtil.getCellValue(cell,XlsxCellType.STRING)
                            } else if (cell.getColumnIndex() == 10) {
                                invoiceMap.debtorAreaCode = quickEntryUtil.getCellValue(cell,XlsxCellType.STRING)
                            } else if (cell.getColumnIndex() == 11) {
                                invoiceMap.debtorCity = quickEntryUtil.getCellValue(cell,XlsxCellType.STRING)
                            }else if (cell.getColumnIndex() == 12) {
                                invoiceMap.debtorCountry = quickEntryUtil.getCellValue(cell,XlsxCellType.STRING)
                            } else if (cell.getColumnIndex() == 13) {
                                invoiceMap.iban = quickEntryUtil.getCellValue(cell,XlsxCellType.STRING)
                            } else if (cell.getColumnIndex() == 14) {
                                invoiceMap.debtorMail = quickEntryUtil.getCellValue(cell,XlsxCellType.STRING)
                            } else if (cell.getColumnIndex() == 15) {
                                invoiceMap.debtorPhone = quickEntryUtil.getCellValue(cell,XlsxCellType.STRING)
                            }
                        }
                        if(!fileOk)
                            break;
                        def tableName = "import_invoice"
                        def insertId = budgetViewDatabaseService.insert(invoiceMap, tableName)

                    }
                    flag++
                }

            }
            fis.close()
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    def getInvoiceInfo(def ImportInvoiceId) {
        def sql = """SELECT debtor_name,invoice_date,total,subtotal,vat,debtor_contact_person,payment_ref,iban,vendor_id FROM import_invoice WHERE id= '${ImportInvoiceId}'"""
        def infoArr = budgetViewDatabaseService.executeQuery(sql)
        String date_s = infoArr[0][1].toString()
        SimpleDateFormat dt = new SimpleDateFormat("yyyyy-MM-dd hh:mm:ss")
        Date date = dt.parse(date_s);
        SimpleDateFormat dt1 = new SimpleDateFormat("dd-MM-yyyy")
        Map invoiceInfo = [
                debtorName          : infoArr[0][0],
                invoiceDate         : dt1.format(date),
                total               : infoArr[0][2],
                subtotal            : infoArr[0][3],
                vat                 : infoArr[0][4],
                debtorContactPerson : infoArr[0][5],
                paymentRef          : infoArr[0][6],
                iban                : infoArr[0][7],
                vendorId            : infoArr[0][8]
        ]
        return invoiceInfo
    }
}
