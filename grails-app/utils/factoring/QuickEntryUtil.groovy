package factoring

import grails.util.Holders
import groovy.sql.GroovyRowResult
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellValue
import org.grails.plugins.web.taglib.ValidationTagLib

import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat


import org.apache.commons.lang.time.DateUtils

class QuickEntryUtil {

    public DateFormat formatter
    public Date date
    public static final String STR_DATE_FORMAT = "yyyy-MM-dd"
    public static final String GRID_DATE_FORMAT = "dd-MM-yyyy"
    public String STR_DATE_RETURN = ""
    private String contextPath = Holders.applicationContext.servletContext.contextPath
    public String[] allDateFormats = [
            "dd-MM-yy","dd-MM-yyyy","MM-dd-yy","MM-dd-yyyy","yyyy-MM-dd",
            "dd/MM/yy","dd/MM/yyyy","MM/dd/yy","MM/dd/yyyy","yyyy/MM/dd",
            "ddMMyy","ddMMyyyy","MMddyy","MMddyyyy","yyyyMMdd",
            "dd:MM:yy","dd:MM:yyyy","MM:dd:yy","MM:dd:yyyy","yyyy:MM:dd",
            "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm:ssZ",
            "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd HH:mm:ss",
            "MM/dd/yyyy HH:mm:ss", "MM/dd/yyyy'T'HH:mm:ss.SSS'Z'",
            "MM/dd/yyyy'T'HH:mm:ss.SSSZ", "MM/dd/yyyy'T'HH:mm:ss.SSS",
            "MM/dd/yyyy'T'HH:mm:ssZ", "MM/dd/yyyy'T'HH:mm:ss",
            "yyyy:MM:dd HH:mm:ss"
    ]

    def getCellValue(Cell cell, def inputValueType, def formulaEvaluator = null) {
        if (cell.cellType == Cell.CELL_TYPE_NUMERIC) {
            if (inputValueType == XlsxCellType.DATE) {
                if(DateUtil.isCellDateFormatted(cell)){
                    return cell.getDateCellValue()
                }
                else{
                    Double dblValue = cell.getNumericCellValue()
                    BigDecimal bd = new BigDecimal(dblValue.toString())
                    Long lngDate = bd.longValue()
                    return DateUtils.parseDateStrictly(lngDate.toString(),allDateFormats)
                }
            }
            else if (inputValueType == XlsxCellType.STRING){
                def cellData = cell.getNumericCellValue()
                BigDecimal bdData = new BigDecimal(cellData)
                DecimalFormat twoDForm = new DecimalFormat("#")
                def strData = twoDForm.format(bdData)
                return strData
            }
            else{
                return cell.getNumericCellValue()
            }
        } else if (cell.cellType == Cell.CELL_TYPE_STRING) {
            if (inputValueType == XlsxCellType.DATE) {
                return DateUtils.parseDateStrictly(cell.getStringCellValue(),allDateFormats)
            }
            if (inputValueType == XlsxCellType.DECIMAL) {
                def strData = cell.getStringCellValue()
                if(strData.lastIndexOf(".") + 1 == strData.length() || strData.lastIndexOf(".") + 2 == strData.length()){
                    strData = strData.replaceAll(",","")
                }
                else{
                    strData = strData.replaceAll("\\.","")
                    strData = strData.replaceAll(",",".")
                }
                BigDecimal bdData = new BigDecimal(strData)
                DecimalFormat twoDForm = new DecimalFormat("#.00")
                def numCellValue = twoDForm.format(bdData)
                return numCellValue
            }else {
                return cell.getStringCellValue()
            }
        } else if (cell.cellType == Cell.CELL_TYPE_BOOLEAN) {
            return cell.getBooleanCellValue()
        } else if (cell.cellType == Cell.CELL_TYPE_BLANK) {
            if (inputValueType == XlsxCellType.DECIMAL) {
                return 0
            }else if (inputValueType == XlsxCellType.STRING) {
                return ""
            }
            else{
                return null
            }
        }
        else if (cell.cellType == Cell.CELL_TYPE_FORMULA) {
            CellValue cellValue = formulaEvaluator.evaluate(cell)
            return cellValue.getNumberValue()
        }
    }

    public Date getDateFormInput(String str) {
        try {
            formatter = new SimpleDateFormat(GRID_DATE_FORMAT);
            date = (Date) formatter.parse(str);
        } catch (Exception e) {
            STR_DATE_RETURN = "";
        }
        return date;
    }

    def checkDuplicateRowImportFile(def debtorName, def amount, def date,paymentRef) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy")
        Date parsed = format.parse(date.toString())
        date = new java.sql.Date(parsed.getTime())
        String strSelect = """SELECT * from import_invoice WHERE debtor_name = '${debtorName}' AND total = ${amount} AND invoice_date = '${date}' AND payment_ref = '${paymentRef}'"""
        def importArr = new BudgetViewDatabaseService().executeQuery(strSelect)

        def res = false
        if (importArr.size() > 1) {
            res = true
        }
        return res
    }

    def checkBookingYearValid(def date) {
        def dateArr = date.toString().split("-")
        def bookingYear = dateArr[2]
        def activeFiscalYear = new CoreParamsHelperTagLib().getActiveFiscalYear()
        def fiscalYearInfo = new CoreParamsHelperTagLib().getActiveFiscalYearInformation(activeFiscalYear)
        def activeBookingYear = fiscalYearInfo[0][4]
        return bookingYear.equals(activeBookingYear)
    }

    def checkInvoiceAlreadyProcess(def vendorId, def debtorName, def amount, def date, def paymentRef) {
        def customer
        try{
            customer = new BudgetViewDatabaseService().executeQuery("SELECT * FROM customer_master as cm WHERE cm.customer_name='"+debtorName+"'")
        }
        catch(Exception e){

        }
        if(customer){
            def debtorId = customer[0][0]
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy")
            Date parsed = format.parse(date.toString())
            def parsedDate = new java.sql.Date(parsed.getTime())
            def processedInvoices = new BudgetViewDatabaseService().executeQuery("""SELECT ic.id from invoice_income as ic where ic.customer_id = '${vendorId}' and ic.debtor_id = '${debtorId}' and ic.total_gl_amount='${amount}' and date(ic.trans_date)='${parsedDate}' and ic.payment_ref='${paymentRef}'""")
            if(processedInvoices)
                return true
            else
                return false
        }
        else{
            return false
        }
    }


    def checkVatMatched(def totalAmountWithOutVat, def totalAmountWithVat, def vat) {
        def flag = false
        if(vat > 0){
            def vatFor21 = ((totalAmountWithOutVat * 21) / 100).round(2)
            if(Math.abs(vat - vatFor21) <= 0.05){
                flag = true
            }
            def vatFor06 = ((totalAmountWithOutVat * 6) / 100).round(2)
            if(Math.abs(vat - vatFor06) <= 0.05){
                flag = true
            }
        }
        else if(vat == 0 && totalAmountWithVat==totalAmountWithOutVat){
            flag = true
        }
        return flag
    }

    public List importInvoiceInGrid(List<GroovyRowResult> quickEntries) {
        List quickInvoiceList = new ArrayList()
        def invoiceEntry
        GridEntity obj
        String changeBooking
        def size = quickEntries.size()

        try {
            int counter = 1
            for (int i = 0; i < size ; i++) {
                invoiceEntry = quickEntries[i]
                obj = new GridEntity()
                obj.id = invoiceEntry.id

                DecimalFormat twoDForm = new DecimalFormat("#.00")
                BigDecimal dblTotalAmount = new BigDecimal(invoiceEntry.total)
                BigDecimal dblVat = new BigDecimal(invoiceEntry.vat)

                def flag = false
                def errorMessage = ""
                def vatFlag
                def duplicateFlag
                def addressFlag
                def paymentRef = invoiceEntry.paymentRef
                def vendorName = invoiceEntry.vendorName
                def debtorName = invoiceEntry.debtorName
                def invoiceDate = invoiceEntry.invoiceDate

                def totalAmount = twoDForm.format(dblTotalAmount)
                def totalVat = twoDForm.format(dblVat)

                def isInvoiceProcessed = checkInvoiceAlreadyProcess(invoiceEntry.vendorId,debtorName,invoiceEntry.subtotal,invoiceDate,invoiceEntry.paymentRef)
                def isDuplicateRow = checkDuplicateRowImportFile(debtorName, invoiceEntry.total, invoiceDate,invoiceEntry.paymentRef)
                def isBookingYearValid = checkBookingYearValid(invoiceDate)
                def isVatMatch = checkVatMatched(invoiceEntry.subtotal, invoiceEntry.total,invoiceEntry.vat)
                def isDebtorAddressDifferent = checkDebtorAddressDifferent(invoiceEntry)
                def isDebtorNew = checkDebtorExist(invoiceEntry)
                def isDebtorInActive = checkDebtorStatus(invoiceEntry)

                if(isInvoiceProcessed){
                    flag = true
                }else if(!isBookingYearValid){
                    flag = true
                }
                else if(isDuplicateRow) {
                    duplicateFlag = 3
                }else if(!isVatMatch){
                    vatFlag = 2
                }
                else if(isDebtorAddressDifferent || isDebtorNew){
                    addressFlag = 4
                }

                if(isInvoiceProcessed){
                    flag = true
                    errorMessage = errorMessage + "Invoice already Processed.\n"
                }
                if(isDuplicateRow) {
                    flag = true
                    errorMessage = errorMessage + "Duplicate row in Import file\n"
                }
                if(!isBookingYearValid) {
                    flag = true
                    errorMessage = errorMessage + "Date of invoice not in active bookyear\n"
                }
                if(!isVatMatch){
                    flag = true
                    errorMessage = errorMessage + "Vat not matching (not 21% or 6%)\n"
                }
                if(isDebtorAddressDifferent){
                    flag = true
                    errorMessage = errorMessage + "Debtor address is different\n"
                }
                if(isDebtorNew){
                    flag = true
                    errorMessage = errorMessage + "Debtor is new\n"
                }
                if(isDebtorInActive){
                    flag = true
                    errorMessage = errorMessage + "Debtor is Inactive\n"
                }


                if (flag) {
                    changeBooking = "<a href='${contextPath}/importInvoice/importInvoice?editId=${invoiceEntry.id}'><img width='16' height='15' alt='Edit' src='${contextPath}/images/edit.png'></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='javascript:deleteImportInvoice(${invoiceEntry.id})'><img width='16' height='15' alt='Delete' src='${contextPath}/images/delete.png'></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<abbr title='${errorMessage}'><img width='16' height='17' alt='Delete' src='${contextPath}/images/skin/information.png'></abbr>"
                } else {
                    changeBooking = "<a href='${contextPath}/importInvoice/importInvoice?editId=${invoiceEntry.id}'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='javascript:deleteImportInvoice(${invoiceEntry.id})'><img width=\"16\" height=\"15\" alt=\"Delete\" src=\"${contextPath}/images/delete.png\"></a>"
                }
                if(vatFlag == 2 ){
                    obj.cell = ["paymentRef": paymentRef, "debtorName": debtorName, "customerName": vendorName, "invoiceDate": invoiceDate, "totalAmount": totalAmount, "vat": totalVat, "action": changeBooking, "flag": vatFlag]
                }else if(duplicateFlag == 3){
                    obj.cell = ["paymentRef": paymentRef, "debtorName": debtorName, "customerName": vendorName, "invoiceDate": invoiceDate, "totalAmount": totalAmount, "vat": totalVat, "action": changeBooking, "flag": duplicateFlag]
                }else if(addressFlag == 4){
                    obj.cell = ["paymentRef": paymentRef, "debtorName": debtorName, "customerName": vendorName, "invoiceDate": invoiceDate, "totalAmount": totalAmount, "vat": totalVat, "action": changeBooking, "flag": addressFlag]
                }
                else{
                    obj.cell = ["paymentRef": paymentRef, "debtorName": debtorName, "customerName": vendorName, "invoiceDate": invoiceDate, "totalAmount": totalAmount, "vat": totalVat, "action": changeBooking, "flag": flag]
                }

                quickInvoiceList.add(obj)
                counter++
            }
            return quickInvoiceList
        } catch (Exception ex) {
            quickInvoiceList = []
            return quickInvoiceList
        }
    }

    public checkDebtorAddressDifferent(def invoiceEntry){
        def debtor = new BudgetViewDatabaseService().executeQuery("SELECT * FROM customer_master as cm WHERE cm.customer_name='"+invoiceEntry.debtorName+"'")
        if(debtor){
            def debtorCustomerGeneralAddress = new BudgetViewDatabaseService().executeQuery("SELECT * FROM customer_general_address AS cga WHERE cga.customer_id="+debtor[0][0])
            //if general address different then error and return false
            def strAddressLine
            if(debtorCustomerGeneralAddress)
                strAddressLine = debtorCustomerGeneralAddress[0][2]
            def strCurrentAddress = invoiceEntry.addressLine1
            if(strAddressLine)
                strAddressLine = strAddressLine.trim()
            if(strCurrentAddress)
                strCurrentAddress = strCurrentAddress.toString().trim()
            if(strAddressLine.equalsIgnoreCase(strCurrentAddress)){
                return false
            }
            else{
                return true
            }
        }
        else{
            return false
        }
    }

    public checkDebtorAddressDifferentByDebtorName(def debtorName,def addressLine1){
        def debtor = new BudgetViewDatabaseService().executeQuery("SELECT * FROM customer_master as cm WHERE cm.customer_name='"+debtorName+"'")
        if(debtor){
            def debtorCustomerGeneralAddress = new BudgetViewDatabaseService().executeQuery("SELECT * FROM customer_general_address AS cga WHERE cga.customer_id="+debtor[0][0])
            //if general address different then error and return false
            def strAddressLine
            if(debtorCustomerGeneralAddress)
                strAddressLine = debtorCustomerGeneralAddress[0][2]
            def strCurrentAddress = addressLine1
            if(strAddressLine)
                strAddressLine = strAddressLine.trim()
            if(strCurrentAddress)
                strCurrentAddress = strCurrentAddress.toString().trim()
            if(strAddressLine) {
                if(strAddressLine.equalsIgnoreCase(strCurrentAddress))
                    return false
                else
                    return true
            }
            else return true
        }
        else{
            return false
        }
    }

    public checkDebtorExist(def invoiceEntry){
        def debtor = new BudgetViewDatabaseService().executeQuery("SELECT * FROM customer_master as cm WHERE cm.customer_name='"+invoiceEntry.debtorName+"'")
        if(debtor){
            return false
        }
        else{
            return true
        }
    }

    private checkDebtorStatus(def invoiceEntry){

        boolean status = false

        def debtorStatus = new BudgetViewDatabaseService().executeQuery("SELECT status FROM customer_master as cm WHERE cm.customer_name='${invoiceEntry.debtorName}'")
        if(debtorStatus){
            if(debtorStatus[0][0] == 2){
                status = true
            }
        }
        return status

    }

    public String getStrTransDate(Date transDate) {
        try {
            formatter = new SimpleDateFormat(GRID_DATE_FORMAT)
            STR_DATE_RETURN = formatter.format(transDate)
        } catch (Exception e) {
            STR_DATE_RETURN = ""
        }
        return STR_DATE_RETURN
    }
    //method to get UI grid str date from rest api date str

    public String getStrDateForGrid(String str) {
        try {
            formatter = new SimpleDateFormat(STR_DATE_FORMAT)
            date = (Date) formatter.parse(str)
            formatter = new SimpleDateFormat(GRID_DATE_FORMAT)
            STR_DATE_RETURN = formatter.format(date)
        } catch (Exception e) {
            STR_DATE_RETURN = ""
        }
        return STR_DATE_RETURN
    }

    public String getStrDateForGrid(Date transDate) {
        try {
            formatter = new SimpleDateFormat(GRID_DATE_FORMAT)
            STR_DATE_RETURN = formatter.format(transDate)
        } catch (Exception e) {
            STR_DATE_RETURN = ""
        }
        return STR_DATE_RETURN
    }

    public List wrapListInGrid(List<GroovyRowResult> quickEntries, int start) {
        List quickExpenseList = new ArrayList()
        def expenseEntry
        GridEntity obj
        String changeBooking
        try {
            int counter = start + 1
            for (int i = 0; i < quickEntries.size(); i++) {
                expenseEntry = quickEntries[i]
                obj = new GridEntity()
                obj.id = expenseEntry.id
                changeBooking = "<a href='javascript:changeBooking(\"${expenseEntry.id}\")'>Edit</a>"
                obj.cell = [counter, expenseEntry.invoice_number, expenseEntry.vendor_id, "", getStrDateForGrid(expenseEntry.created_date), getStrDateForGrid(expenseEntry.trans_date), expenseEntry.total_gl_amount, expenseEntry.total_vat, changeBooking]
                quickExpenseList.add(obj)
                counter++
            }
            return quickExpenseList
        } catch (Exception ex) {
            quickExpenseList = []
            return quickExpenseList
        }
    }

    /*
    * Grid for List budget Item page which comes from dashboard
    *
    * */

    public List wrapBudgetItemInGrid(List<GroovyRowResult> quickEntries, int start, journalId, vendorId, bookingPeriod) {
        //public List wrapBudgetItemInGrid(List<GroovyRowResult> quickEntries, int start) {
        List quickExpenseList = new ArrayList()
        def expenseEntry
        GridEntity obj
        String changeBooking
        try {
            int counter = start + 1
            for (int i = 0; i < quickEntries.size(); i++) {
                expenseEntry = quickEntries[i];

                DecimalFormat twoDForm = new DecimalFormat("#.00");

                def aa = expenseEntry.totalPriceWithoutVat
                Double bb = Double.parseDouble(aa)
                def totalPriceWithoutVatMd = twoDForm.format(bb)

                def cc = expenseEntry.totalPriceWithVat
                Double dd = Double.parseDouble(cc)
                def totalPriceWithVatMd = twoDForm.format(dd)

                obj = new GridEntity();
                obj.id = expenseEntry.invoiceExpenseId + "::" + expenseEntry.detailsID
                //changeBooking = "<a href='javascript:changeBooking(\"${expenseEntry.invoiceExpenseId}\",\"${journalId}\",\"${vendorId}\",\"${bookingPeriod}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"../../images/edit.png\"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='javascript:deleteBooking(\"${journalId}\",\"${journalId}\",\"${journalId}\",\"${journalId}\")'><img width=\"16\" height=\"15\" alt=\"Delete\" src=\"../../images/delete.png\"></a>"
                changeBooking = "<a href='javascript:changeBooking(\"${expenseEntry.invoiceExpenseId}\",\"${journalId}\",\"${vendorId}\",\"${bookingPeriod}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>"
                obj.cell = [counter, expenseEntry.budgetItemID, expenseEntry.glAccountName, expenseEntry.createdDate, totalPriceWithoutVatMd, totalPriceWithVatMd, expenseEntry.total, changeBooking]
                quickExpenseList.add(obj)
                counter++;
            }
            return quickExpenseList
        } catch (Exception ex) {
            quickExpenseList = []
            return quickExpenseList
        }
    }

    public List wrapEntryBudgetItemInGrid(List<GroovyRowResult> quickEntries, int start, vendorId, journalId, bookingPeriod, invoiceBudgetExpenseData) {
        List quickExpenseList = new ArrayList()
        def expenseEntry
        GridEntity obj
        String changeBooking

        try {
            def notDelete = 0
            int counter = start + 1
            for (int i = 0; i < quickEntries.size(); i++) {
                expenseEntry = quickEntries[i];
                notDelete = 0
                invoiceBudgetExpenseData.each { phn ->
                    if (expenseEntry.id == phn) {
                        notDelete = 1
                    }
                }

                def bookStartPeriod = ""
                if (expenseEntry.booking_period_start_month == 12) {
                    bookStartPeriod = "Dec" + "-" + expenseEntry.booking_period_start_year
                } else {
                    bookStartPeriod = new CoreParamsHelperTagLib().monthNameShow(expenseEntry.booking_period_start_month) + "-" + expenseEntry.booking_period_start_year
                }
                def bookEndPeriod = ""
                if (expenseEntry.booking_period_end_month == 12) {
                    bookEndPeriod = "Dec" + "-" + expenseEntry.booking_period_end_year
                } else {
                    bookEndPeriod = new CoreParamsHelperTagLib().monthNameShow(expenseEntry.booking_period_end_month) + "-" + expenseEntry.booking_period_end_year
                }

                def showtotalGlAmount = expenseEntry.totalGlAmount
                def showtotalVat = expenseEntry.totalVat

                Double showtotalGlAmountc = Double.parseDouble(showtotalGlAmount)
                Double showtotalVatc = Double.parseDouble(showtotalVat)

                def showtotalGlAmounta = String.format("%.2f", showtotalGlAmountc)
                def showtotalVata = String.format("%.2f", showtotalVatc)

                obj = new GridEntity()
                obj.id = expenseEntry.id

                if(bookingPeriod){

                }


                if (notDelete == 0) {
                    changeBooking = "<a href='javascript:changeBooking(\"${expenseEntry.id}\",\"${expenseEntry.vendor_id}\",\"${journalId}\",\"${bookingPeriod}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='javascript:deleteExpenseBudgetItem(\"${expenseEntry.id}\")'><img width=\"16\" height=\"15\" alt=\"Delete\" src=\"${contextPath}/images/delete.png\"></a>"
                } else {
                    changeBooking = "<a href='javascript:changeBooking(\"${expenseEntry.id}\",\"${expenseEntry.vendor_id}\",\"${journalId}\",\"${bookingPeriod}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>"
                }

                obj.cell = ["budgetItemName": expenseEntry.budgetItemName,"budgetCode": expenseEntry.budgetCode,"totalGlAmount": showtotalGlAmounta,"totalVat": showtotalVata,"bookStartPeriod":  bookStartPeriod, "action" :changeBooking]
                quickExpenseList.add(obj)
                counter++;
            }
            return quickExpenseList
        } catch (Exception ex) {
            quickExpenseList = []
            return quickExpenseList
        }
    }

    public List wrapEntryBudgetItemSearchInGrid(List<GroovyRowResult> quickEntries, int start, vendorId, journalId, bookingPeriod, invoiceBudgetExpenseData) {
        List quickExpenseList = new ArrayList()
        def expenseEntry
        GridEntity obj
        String changeBooking

        try {
            def notDelete = 0
            int counter = start + 1
            for (int i = 0; i < quickEntries.size(); i++) {
                expenseEntry = quickEntries[i];
                notDelete = 0
                invoiceBudgetExpenseData.each { phn ->
                    if (expenseEntry.id == phn) {
                        notDelete = 1
                    }
                }

                def bookStartPeriod = ""
                if (expenseEntry.booking_period_start_month == 12) {
                    bookStartPeriod = "Dec " + " - " + expenseEntry.booking_period_start_year
                } else {
                    bookStartPeriod = new CoreParamsHelperTagLib().monthNameShow(expenseEntry.booking_period_start_month) + " - " + expenseEntry.booking_period_start_year
                }
                def bookEndPeriod = ""
                if (expenseEntry.booking_period_end_month == 12) {
                    bookEndPeriod = "Dec " + " - " + expenseEntry.booking_period_end_year
                } else {
                    bookEndPeriod = new CoreParamsHelperTagLib().monthNameShow(expenseEntry.booking_period_end_month) + " - " + expenseEntry.booking_period_end_year
                }

                def showtotalGlAmount = expenseEntry.totalGlAmount
                def showtotalVat = expenseEntry.totalVat

                def showtotalGlAmounta = String.format("%.2f", showtotalGlAmount)
                def showtotalVata = String.format("%.2f", showtotalVat)

                obj = new GridEntity()
                obj.id = expenseEntry.id

                if(bookingPeriod){

                }


                if (notDelete == 0) {
                    changeBooking = "<a href='javascript:changeBooking(\"${expenseEntry.id}\",\"${expenseEntry.vendor_id}\",\"${journalId}\",\"${bookingPeriod}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='javascript:deleteExpenseBudgetItem(\"${expenseEntry.id}\")'><img width=\"16\" height=\"15\" alt=\"Delete\" src=\"${contextPath}/images/delete.png\"></a>"
                } else {
                    changeBooking = "<a href='javascript:changeBooking(\"${expenseEntry.id}\",\"${expenseEntry.vendor_id}\",\"${journalId}\",\"${bookingPeriod}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>"
                }


                //obj.cell = [counter, expenseEntry.budgetItemName, expenseEntry.budgetCode, showtotalGlAmounta, showtotalVata, bookStartPeriod, bookEndPeriod, changeBooking]
                obj.cell = [ expenseEntry.budgetItemName, expenseEntry.budgetCode, showtotalGlAmounta, showtotalVata, bookStartPeriod, changeBooking]
                quickExpenseList.add(obj)
                counter++;
            }
            return quickExpenseList
        } catch (Exception ex) {
            quickExpenseList = []
            return quickExpenseList
        }
    }

// ******************************************************
    /*
   * Grid for List Expense Invoice List
   *
   * */

    public List wrapInvoiceExpenseInGrid(List<GroovyRowResult> quickEntries, int start, budgetVendorId, bookingPeriod, bookInvoiceId,fiscalYearId) {
        List quickExpenseList = new ArrayList()
        def expenseEntry
        GridEntity obj
        String changeBooking
        try {
            int counter = start + 1
            for (int i = 0; i < quickEntries.size(); i++) {
                expenseEntry = quickEntries[i];
                obj = new GridEntity();
                obj.id = expenseEntry.id

                DecimalFormat twoDForm = new DecimalFormat("#.00");

                BigDecimal showtotalGlAmounta = new BigDecimal(expenseEntry.totalAmountIncVat)
                def showtotalGlAmount = twoDForm.format(showtotalGlAmounta)
                BigDecimal showtotalVata = new BigDecimal(expenseEntry.totalVat)
                def showtotalVat = twoDForm.format(showtotalVata)
                def vendorId = expenseEntry.vendorId

                def bookingPeriodFormat = (new CoreParamsHelperTagLib().monthNameShow(Integer.parseInt(expenseEntry.bookingPeriod))) + '-' + fiscalYearId;
                def budgetItemDetailsId = new CoreParamsHelperTagLib().getBudgetItemExpenseDetailsIdFromInvoiceExpense(expenseEntry.id)

                //changeBooking = "<a href='javascript:changeBooking(\"${expenseEntry.id}\",\"${vendorId}\",\"${bookingPeriod}\",\"${bookInvoiceId}\",\"${budgetItemDetailsId}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='javascript:deleteBooking(\"${expenseEntry.id}\",\"${vendorId}\",\"${bookingPeriod}\",\"${bookInvoiceId}\")'><img width=\"16\" height=\"15\" alt=\"Delete\" src=\"${contextPath}/images/delete.png\"></a>"
                changeBooking = "<a href='javascript:changeBooking(\"${expenseEntry.id}\",\"${vendorId}\",\"${bookingPeriod}\",\"${bookInvoiceId}\",\"${budgetItemDetailsId}\",\"${budgetVendorId}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>"
                //obj.cell = [counter, expenseEntry.invoiceNumber, expenseEntry.budgetItemID, expenseEntry.budgetItemName, expenseEntry.InvoiceVendorName, expenseEntry.invoiceDate, expenseEntry.dueDate, showtotalGlAmount, showtotalVat, changeBooking]
                obj.cell = ["invoiceNumber":expenseEntry.invoiceNumber,"bookingPeriod": bookingPeriodFormat,"vendorName": expenseEntry.vendorName,"invoiceDate": expenseEntry.invoiceDate,"paymentReference": expenseEntry.paymentRef,"totalGlAmount": showtotalGlAmount, "totalVat":showtotalVat,"action": changeBooking]

                quickExpenseList.add(obj)
                counter++;
            }
            return quickExpenseList
        } catch (Exception ex) {

            quickExpenseList = []
            return quickExpenseList
        }
    }


    public List wrapPrivateProcessInGrid(List<GroovyRowResult> quickEntries, int start) {
        List quickPrivateProcessList = new ArrayList()
        def processEntry
        GridEntity obj
        String changeBooking
        try {
            int counter = start + 1
            for (int i = 0; i < quickEntries.size(); i++) {
                processEntry = quickEntries[i]
                obj = new GridEntity()
                obj.id = processEntry.id

                def amount = processEntry.amount.round(2)
                def description =processEntry.description
                def budgetName = "<div>"+ new CoreParamsHelperTagLib().getBudgetNameDropDownForPrivateProcess(processEntry.budgetMasterId,processEntry.id)+"</div>"
                def bookingPeriod ="<div id ='pvtBookingPeriod_${processEntry.id}' >"+ new CoreParamsHelperTagLib().getMonthDropDownForPrivateProcess(processEntry.budgetMasterId,processEntry.bookingPeriod,processEntry.id)+"</div>"
                def date=processEntry.transDate
                def bankId=processEntry.bankId
                def action= "<input class='greenBtnDataGrid' type='button' value='Update' onclick='updatePrivateProcessAmount(${processEntry.id})'>"
                obj.cell = ["bankId":bankId,"date": date,"bookingPeriod": bookingPeriod,"budgetName": budgetName,"description": description,"amount": amount,"action": action]

                quickPrivateProcessList.add(obj)
                counter++
            }
            return quickPrivateProcessList
        }catch (Exception ex) {
            quickPrivateProcessList = []
            return quickPrivateProcessList
        }
    }


    public List wrapReservationInvoiceInGrid(List<GroovyRowResult> quickEntries, int start) {
        List quickReservationInvoiceList = new ArrayList()
        def processEntry
        GridEntity obj
        String changeBooking
        try {
            int counter = start + 1
            for (int i = 0; i < quickEntries.size(); i++) {
                processEntry = quickEntries[i]
                obj = new GridEntity()
                obj.id = processEntry.id

                def amount = processEntry.amount.round(2)
                def description =processEntry.description
                def budgetName = "<div>"+ new CoreParamsHelperTagLib().getBudgetNameDropDownForReservationInvoice(processEntry.budgetMasterId,processEntry.id)+"</div>"
                def bookingPeriod ="<div id ='pvtBookingPeriod_${processEntry.id}' >"+ new CoreParamsHelperTagLib().getMonthDropDownForReservationInvoice(processEntry.budgetMasterId,processEntry.bookingPeriod,processEntry.id)+"</div>"
                def date=processEntry.transDate
                def bankId=processEntry.bankId
                def action= "<input class='greenBtnDataGrid' type='button' value='Update' onclick='updateReservationInvoiceAmount(${processEntry.id})'>"
                obj.cell = ["bankId":bankId,"date": date,"bookingPeriod": bookingPeriod,"budgetName": budgetName,"description": description,"amount": amount,"action": action]

                quickReservationInvoiceList.add(obj)
                counter++
            }
            return quickReservationInvoiceList
        }catch (Exception ex) {
            quickReservationInvoiceList = []
            return quickReservationInvoiceList
        }
    }


    public List wrapSearchInvoiceExpenseInGrid(List<GroovyRowResult> quickEntries, int start, fiscalYearId) {
        List quickExpenseList = new ArrayList()
        def expenseEntry
        GridEntity obj
        String changeBooking
        try {
            int counter = start + 1
            for (int i = 0; i < quickEntries.size(); i++) {
                expenseEntry = quickEntries[i]
                obj = new GridEntity()
                obj.id = expenseEntry.id

                DecimalFormat twoDForm = new DecimalFormat("#.00");

                BigDecimal showtotalGlAmounta = new BigDecimal(expenseEntry.totalAmountIncVat)
                def showtotalGlAmount = twoDForm.format(showtotalGlAmounta)
                BigDecimal showtotalVata = new BigDecimal(expenseEntry.totalVat)
                def showtotalVat = twoDForm.format(showtotalVata)
                def vendorId = expenseEntry.vendorId
                //println(expenseEntry.bookingPeriod)

                def bookingPeriodFormat = (new CoreParamsHelperTagLib().monthNameShow(Integer.parseInt(expenseEntry.bookingPeriod))) + '-' + fiscalYearId;
                def budgetItemDetailsId = new CoreParamsHelperTagLib().getBudgetItemExpenseDetailsIdFromInvoiceExpense(expenseEntry.id)

                //changeBooking = "<a href='javascript:changeBooking(\"${expenseEntry.id}\",\"${vendorId}\",\"${bookingPeriod}\",\"${bookInvoiceId}\",\"${budgetItemDetailsId}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='javascript:deleteBooking(\"${expenseEntry.id}\",\"${vendorId}\",\"${bookingPeriod}\",\"${bookInvoiceId}\")'><img width=\"16\" height=\"15\" alt=\"Delete\" src=\"${contextPath}/images/delete.png\"></a>"
                changeBooking = "<a href='javascript:changeBooking(\"${expenseEntry.id}\",\"${vendorId}\",\"${expenseEntry.bookingPeriod}\",\"${expenseEntry.budget_item_expense_id}\",\"${budgetItemDetailsId}\",\"${expenseEntry.budget_vendor_id}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>"
                //obj.cell = [counter, expenseEntry.invoiceNumber, expenseEntry.budgetItemID, expenseEntry.budgetItemName, expenseEntry.InvoiceVendorName, expenseEntry.invoiceDate, expenseEntry.dueDate, showtotalGlAmount, showtotalVat, changeBooking]
                obj.cell = [expenseEntry.invoiceNumber, bookingPeriodFormat, expenseEntry.vendorName, expenseEntry.invoiceDate, expenseEntry.paymentRef, showtotalGlAmount, showtotalVat, changeBooking]

                quickExpenseList.add(obj)
                counter++
            }
            return quickExpenseList
        } catch (Exception ex) {
            quickExpenseList = []
            return quickExpenseList
        }
    }


    public List wrapReceiptInGrid(List<GroovyRowResult> quickEntries, int start, budgetVendorId, bookingPeriod, bookInvoiceId,fiscalYearId) {
        List quickExpenseList = new ArrayList()
        def expenseEntry
        GridEntity obj
        String changeBooking
        try {
            int counter = start + 1
            for (int i = 0; i < quickEntries.size(); i++) {
                expenseEntry = quickEntries[i];
                obj = new GridEntity();
                obj.id = expenseEntry.id

                DecimalFormat twoDForm = new DecimalFormat("#0.00");

                BigDecimal showtotalGlAmounta = new BigDecimal(expenseEntry.totalAmountIncVat)
                def showtotalGlAmount = twoDForm.format(showtotalGlAmounta)
                BigDecimal showtotalVata = new BigDecimal(expenseEntry.totalVat)
                def showtotalVat = twoDForm.format(showtotalVata)

                def bookingPeriodFormat = (new CoreParamsHelperTagLib().monthNameShow(Integer.parseInt(expenseEntry.bookingPeriod))) + '-' + fiscalYearId;
                def budgetItemDetailsId = new CoreParamsHelperTagLib().getBudgetItemExpenseDetailsIdFromInvoiceExpense(expenseEntry.id)
                def vendorId = expenseEntry.vendorId
                def vendorShopId = expenseEntry.shopId

                changeBooking = "<a href='javascript:changeBooking(\"${expenseEntry.id}\",\"${vendorId}\",\"${bookingPeriod}\",\"${bookInvoiceId}\",\"${budgetItemDetailsId}\",\"${budgetVendorId}\",\"${vendorShopId}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>"
                obj.cell = ["invoiceNumber":expenseEntry.invoiceNumber,"bookingPeriod": bookingPeriodFormat,"vendorName": expenseEntry.vendorName,"invoiceDate": expenseEntry.invoiceDate,"paymentReference":expenseEntry.paymentRef,"totalGlAmount": showtotalGlAmount,"totalVat": showtotalVat,"action": changeBooking]
                quickExpenseList.add(obj)
                counter++
            }

            return quickExpenseList
        } catch (Exception ex) {
            quickExpenseList = []
            return quickExpenseList
        }
    }



    public List wrapSearchReceiptInGrid(List<GroovyRowResult> quickEntries, int start,fiscalYearId) {
        List quickExpenseList = new ArrayList()
        def expenseEntry
        GridEntity obj
        String changeBooking
        try {
            int counter = start + 1
            for (int i = 0; i < quickEntries.size(); i++) {
                expenseEntry = quickEntries[i];
                obj = new GridEntity();
                obj.id = expenseEntry.id

                DecimalFormat twoDForm = new DecimalFormat("#0.00");

                BigDecimal showtotalGlAmounta = new BigDecimal(expenseEntry.totalAmountIncVat)
                def showtotalGlAmount = twoDForm.format(showtotalGlAmounta)
                BigDecimal showtotalVata = new BigDecimal(expenseEntry.totalVat)
                def showtotalVat = twoDForm.format(showtotalVata)

                def bookingPeriodFormat = (new CoreParamsHelperTagLib().monthNameShow(Integer.parseInt(expenseEntry.bookingPeriod))) + '-' + fiscalYearId;
                def budgetItemDetailsId = new CoreParamsHelperTagLib().getBudgetItemExpenseDetailsIdFromInvoiceExpense(expenseEntry.id)
                def vendorId = expenseEntry.vendorId
                def vendorShopId = expenseEntry.shopId


                changeBooking = "<a href='javascript:changeBooking(\"${expenseEntry.id}\",\"${vendorId}\",\"${expenseEntry.bookingPeriod}\",\"${expenseEntry.budget_item_expense_id}\",\"${budgetItemDetailsId}\",\"${expenseEntry.budget_vendor_id}\",\"${vendorShopId}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>"
                obj.cell = [expenseEntry.invoiceNumber, bookingPeriodFormat, expenseEntry.vendorName, expenseEntry.invoiceDate,expenseEntry.paymentRef, showtotalGlAmount, showtotalVat, changeBooking]
                quickExpenseList.add(obj)
                counter++
            }
            return quickExpenseList
        } catch (Exception ex) {
            quickExpenseList = []
            return quickExpenseList
        }
    }



    /*
    * function list for Journal Entry Util
    * */

    public List wrapJournalEntryInGrid(List<GroovyRowResult> journalEntries, int start,liveUrl) {
        List journalEntriesList = new ArrayList()
        def journalEntry
        GridEntity obj
        String changeBooking
        try {
            int counter = start + 1
            for (int i = 0; i < journalEntries.size(); i++) {
                journalEntry = journalEntries[i];
                obj = new GridEntity();
                obj.id = journalEntry.id

                DecimalFormat twoDForm = new DecimalFormat("#.00");
                BigDecimal showtotalGlAmounta = new BigDecimal(journalEntry.totlaAmount)
                def showtotalGlAmount = twoDForm.format(showtotalGlAmounta)
                changeBooking = "<a href='javascript:changeJournalEntry(\"${journalEntry.id}\",\"${journalEntry.invoiceNumber}\",\"${liveUrl}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>"
                obj.cell = ["journalNumber":journalEntry.invoiceNumber,"transactionDate": journalEntry.transactionDate,"description": journalEntry.description,"totalAmount": showtotalGlAmount,"action": changeBooking]
                journalEntriesList.add(obj)
                counter++;
            }
            return journalEntriesList
        } catch (Exception ex) {
            journalEntriesList = []
            return journalEntriesList
        }
    }

    public List wrapChartMasterEntryInGrid(List<GroovyRowResult> chartMasterEntries, int start, context, fromAction) {
        List chartMasterEntriesList = new ArrayList()
        def chartMasterEntry
        GridEntity obj
        String changeBooking
        try {
            int counter = start + 1
            for (int i = 0; i < chartMasterEntries.size(); i++) {
                chartMasterEntry = chartMasterEntries[i];
                obj = new GridEntity();
                obj.id = chartMasterEntry.id

                if (fromAction == "list") {
                    changeBooking = "<a href='javascript:changeBooking(\"${chartMasterEntry.id}\",\"${context}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${context}/images/edit.png\"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                } else {
                    changeBooking = "<a href='javascript:changeBooking(\"${chartMasterEntry.id}\",\"${context}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${context}/images/edit.png\"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                }

                obj.cell = ["accountCode": chartMasterEntry.accountCode,"accountName": chartMasterEntry.accountName,"accountantName": chartMasterEntry.accountantName, "chartGroup":chartMasterEntry.groupName, "status":chartMasterEntry.showStatus,"action": changeBooking]
                chartMasterEntriesList.add(obj)

                counter++;
            }
            return chartMasterEntriesList
        } catch (Exception ex) {
            chartMasterEntriesList = []
            return chartMasterEntriesList
        }
    }

    /*
   * function list for Manual Reconciliation Entry Util
   * */

    public List wrapManualReconcileEntryInGrid(List<GroovyRowResult> manualEntries, int start) {
        List manuallEntriesList = new ArrayList()
        def manualEntry
        GridEntity obj
        String changeBooking
        try {
            int counter = start + 1
            for (int i = 0; i < manualEntries.size(); i++) {
                manualEntry = manualEntries[i]
                obj = new GridEntity()
                obj.id = manualEntry.id
                DecimalFormat twoDForm = new DecimalFormat("#.00");

                BigDecimal showTotalGlAmounta = new BigDecimal(manualEntry.totalAmnt)
                def showTotalGlAmount = twoDForm.format(showTotalGlAmounta)
                BigDecimal showTotalPaidAmounta = new BigDecimal(manualEntry.paid_amount)
                def showTotalPaidAmount = twoDForm.format(showTotalPaidAmounta)

                changeBooking = "<a href='javascript:changeBooking(\"${manualEntry.id}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='javascript:deleteBooking(\"${manualEntry.id}\")'><img width=\"16\" height=\"15\" alt=\"Delete\" src=\"${contextPath}/images/delete.png\"></a>"
                obj.cell = [counter, manualEntry.invoiceNumber, showTotalGlAmount, showTotalPaidAmount, manualEntry.transactionDate, changeBooking]
                manuallEntriesList.add(obj)
                counter++;
            }
            return manuallEntriesList
        } catch (Exception ex) {

            manuallEntriesList = []
            return manuallEntriesList
        }
    }

    public List wrapUndoReconciliateListInGrid(List<GroovyRowResult>undoReconciliateList,int start,def liveUrl){

        List undoReconciliateItem = new ArrayList()
        GridEntity obj
        def undoReconciliate
        def g = new ValidationTagLib()
        try{
            int counter = start + 1
            for (int i = 0; i <undoReconciliateList.size() ; i++){
                undoReconciliate= undoReconciliateList[i]
                obj = new GridEntity()
                obj.id=undoReconciliate.id

                DecimalFormat twoDForm = new DecimalFormat("#.00");
                BigDecimal showAmounta = new BigDecimal(undoReconciliate.amount)
                def amount = twoDForm.format(showAmounta)

                String firstCol = "<input type=\"checkbox\" value=\"${undoReconciliate.bank_payment_id}\"  onchange=\"jQuery.ajax({type:'POST',data:'id=' + this.value, url:'${liveUrl}/undoReconciliation/showBankStmtDetails',success:function(data,textStatus){jQuery('#updateStatementDetails').html(data);},error:function(XMLHttpRequest,textStatus,errorThrown){}});\"  id=\"UndoRecon\" name=\"UndoRecon\">"

                obj.cell=["firstCol":firstCol,"date":undoReconciliate.date,"relationalBankAccount":undoReconciliate.by_bank_account_no,"description":undoReconciliate.description,"bankPaymentId":undoReconciliate.bank_payment_id,"amount":amount]
                undoReconciliateItem.add(obj)
                counter++
            }

            return undoReconciliateItem

        } catch (Exception ex){

            undoReconciliateItem = []
            return undoReconciliateItem
        }

    }

    public List wrapEntryReservationBudgetItemInGrid(List<GroovyRowResult> quickEntries, int start,journalId,
                                                     bookingPeriod,isForDelete=false) {
        List quickRerservationList = new ArrayList()
        def reservationEntry
        GridEntity obj
        String changeBooking
        String tempBookingPeriod = ""
        try {
            int counter = start + 1
            for (int i = 0; i < quickEntries.size(); i++) {
                reservationEntry = quickEntries[i];

                tempBookingPeriod = ""
                def bookStartPeriod = ""
                if (reservationEntry.booking_period_month == 12) {
                    bookStartPeriod = "Dec" + "-" + reservationEntry.booking_period_year
                } else {
                    bookStartPeriod = new CoreParamsHelperTagLib().monthNameShow(reservationEntry.booking_period_month) + "-" + reservationEntry.booking_period_year
                }
                def bookEndPeriod = ""
                if (reservationEntry.booking_period_month == 12) {
                    bookEndPeriod = "Dec" + "-" + reservationEntry.booking_period_year
                } else {
                    bookEndPeriod = new CoreParamsHelperTagLib().monthNameShow(reservationEntry.booking_period_month) + "-" + reservationEntry.booking_period_year
                }

                def showtotalGlAmount = reservationEntry.totalGlAmount
                def showtotalVat = reservationEntry.totalVat

                Double showtotalGlAmountc = Double.parseDouble(showtotalGlAmount)
                Double showtotalVatc = Double.parseDouble(showtotalVat)

                def showtotalGlAmounta = String.format("%.2f", showtotalGlAmountc)
                def showtotalVata = String.format("%.2f", showtotalVatc)

                obj = new GridEntity()
                obj.id = reservationEntry.id

                if(bookingPeriod == ""){
                    tempBookingPeriod = reservationEntry.booking_period_month
                }else{
                    tempBookingPeriod = bookingPeriod;
                }

                def canDelete
                def isReconcilateArr = budgetViewDatabaseService.executeQuery("""SELECT COUNT(prst.id)
                                                                                       FROM reservation_budget_item as rbi
                                                                                       INNER JOIN private_reservation_spending_trans  as prst
                                                                                       ON rbi.id=prst.budget_item_id where trans_type= '5' and rbi.id = '${reservationEntry.id}';""")

                if(isReconcilateArr[0][0]>0){
                    canDelete = false
                }
                else{
                    canDelete = true
                }


                if(isForDelete){
                    String firstCol =   "<input type=\"checkbox\" name=\"budgetItemId\" value=\"${reservationEntry.id}\">";
                    obj.cell = ["firstCol":firstCol,"budgetItemName": reservationEntry.budgetItemName,"budgetCode": reservationEntry.budgetCode,"totalGlAmount": showtotalGlAmounta,"totalVat": showtotalVata,"bookStartPeriod":  bookStartPeriod]
                }
                else{

                    if(canDelete){
                        changeBooking = "<a href='javascript:changeReservationBudget(\"${reservationEntry.id}\",\"${reservationEntry.budget_name_id}\",\"${journalId}\",\"${tempBookingPeriod}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='javascript:deleteReservationBudgetItem(\"${reservationEntry.id}\")'><img width=\"16\" height=\"15\" alt=\"Delete\" src=\"${contextPath}/images/delete.png\"></a>"
                    }else{
                        changeBooking = "<a href='javascript:changeReservationBudget(\"${reservationEntry.id}\",\"${reservationEntry.budget_name_id}\",\"${journalId}\",\"${tempBookingPeriod}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>"
                    }

                    obj.cell = ["budgetItemName": reservationEntry.budgetItemName,"budgetCode": reservationEntry.budgetCode,"totalGlAmount": showtotalGlAmounta,"totalVat": showtotalVata,"bookStartPeriod":  bookStartPeriod, "action" :changeBooking]
                }



                quickRerservationList.add(obj)
                counter++
            }

            return quickRerservationList
        } catch (Exception ex) {
            quickRerservationList = []
            return quickRerservationList
        }
    }

    def static dottedDecimal(amount){
        def decimalData = amount
        DecimalFormat formatter = new DecimalFormat("€ #,##0.00;€ -#,##0.00")
        def formattedData = formatter.format(decimalData)
        formattedData = formattedData.replace(".",":")
        formattedData = formattedData.replaceAll(",",".")
        formattedData = formattedData.replace(":",",")
        return formattedData
    }

}
