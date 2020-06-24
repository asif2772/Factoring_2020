package factoring

import grails.util.Holders
import groovy.sql.GroovyRowResult

import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat


/**
 * Created with IntelliJ IDEA.
 * User: Ashish saha
 * Date: 6/10/13
 * Time: 4:26 PM
 * To change this template use File | Settings | File Templates.
 */

class InvoiceUtil {
    public DateFormat formatter;
    public Date date;
    public static final String STR_DATE_FORMAT = "yyyy-MM-dd";
    public static final String GRID_DATE_FORMAT = "dd-MM-yyyy";
    public String STR_DATE_RETURN = "";
    private String contextPath = Holders.applicationContext.servletContext.contextPath

    public Date getDateFormInput(String str) {
        try {
            formatter = new SimpleDateFormat(GRID_DATE_FORMAT)
            date = (Date) formatter.parse(str)
        } catch (Exception e) {
            STR_DATE_RETURN = ""
        }
        return date
    }

    def prepareInvoicesAmount(def customerInvoicesData,def settlementIds){
        def customerInvoices = [:]
        def totalInvoices = 0
        def totalAmount = 0.0
        customerInvoicesData.each{ invoice ->
            if(settlementIds.contains(""+invoice.settlementNo)){
                def dueAmount = invoice.tobePaidAmount
                BigDecimal amount = new BigDecimal(dueAmount)
                def payableAmount = amount.setScale(2, BigDecimal.ROUND_HALF_EVEN)
                totalInvoices++
                if(invoice.manualDiscount){
                    BigDecimal manualDiscountAmount = new BigDecimal(invoice.manualDiscount)
                    def manualDiscount = manualDiscountAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN)
                    totalAmount += payableAmount - manualDiscount
                }
                else{
                    totalAmount += payableAmount
                }
            }
        }
        customerInvoices.put("totalInvoices",totalInvoices)
        customerInvoices.put("totalAmount",totalAmount)
        return customerInvoices
    }

    def prepareInvoicesAndAmount(def customerInvoicesMap,def settlementIds){
        def customerInvoices = [:]
        def totalInvoices = 0
        def totalAmount = 0.0
        customerInvoicesMap.each{ K,V ->
            def noInvoicesAndAmount = []
            def totalInvoiceAmount = 0.0
            def noInvoices = 0
            V.each{invoice ->
                if(settlementIds.contains(""+invoice.settlementNo)){
                    def dueAmount = invoice.tobePaidAmount
                    BigDecimal amount = new BigDecimal(dueAmount)
                    def payableAmount = amount.setScale(2, BigDecimal.ROUND_HALF_EVEN)
                    noInvoices++
                    totalInvoices++
                    if(invoice.manualDiscount){
                        BigDecimal manualDiscountAmount = new BigDecimal(invoice.manualDiscount)
                        def manualDiscount = manualDiscountAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN)
                        totalAmount += payableAmount - manualDiscount
                        totalInvoiceAmount += payableAmount - manualDiscount
                    }
                    else{
                        totalAmount += payableAmount
                        totalInvoiceAmount += payableAmount
                    }
                }
            }
            noInvoicesAndAmount.add(noInvoices)
            noInvoicesAndAmount.add(totalInvoiceAmount)
            customerInvoices.put(K,noInvoicesAndAmount)
        }
        customerInvoices.put("totalInvoices",totalInvoices)
        customerInvoices.put("totalAmount",totalAmount)
        return customerInvoices
    }

    public String getStrTransDate(Date transDate) {
        try {
            formatter = new SimpleDateFormat(GRID_DATE_FORMAT);
            STR_DATE_RETURN = formatter.format(transDate);
        } catch (Exception e) {
            STR_DATE_RETURN = "";
        }
        return STR_DATE_RETURN;
    }
    //method to get UI grid str date from rest api date str

    public String getStrDateForGrid(String str) {
        try {
            formatter = new SimpleDateFormat(STR_DATE_FORMAT);
            date = (Date) formatter.parse(str);
            formatter = new SimpleDateFormat(GRID_DATE_FORMAT);
            STR_DATE_RETURN = formatter.format(date);
        } catch (Exception e) {
            STR_DATE_RETURN = "";
        }
        return STR_DATE_RETURN;
    }

    public String getStrDateForGrid(Date transDate) {
        try {
            formatter = new SimpleDateFormat(GRID_DATE_FORMAT);
            STR_DATE_RETURN = formatter.format(transDate);
        } catch (Exception e) {
            STR_DATE_RETURN = "";
        }
        return STR_DATE_RETURN;
    }

    public String replaceComaToDot(String strInput){
        String strOutput = ""

        String strTemp = strInput.replaceAll(",", ".")
        strOutput = strTemp

        return strOutput;
    }

    public boolean isValidVATInput(double dInputVat,double dActualVat){
        double vatDifference = (dInputVat - dActualVat);
        vatDifference = vatDifference.round(2);

        if(vatDifference > 0.05 || vatDifference < -0.05){
            return false;
        }

        return true;
    }

    /**
    * Grid for income invoice list
    */
    List wrapInvoiceIncomeInGrid(List<GroovyRowResult> quickEntries, int start, fiscalYearId, def context) {
        List quickExpenseList = new ArrayList()
        def expenseEntry
        GridEntity obj
        String changeBooking
        DecimalFormat twoDForm = new DecimalFormat("#0.00")

        try {

            for (int i = 0; i < quickEntries.size(); i++) {
                expenseEntry = quickEntries[i]
                obj = new GridEntity()
                obj.id = expenseEntry.id

                def bookingPeriodFormat = (new CoreParamsHelperTagLib().monthNameShow(Integer.parseInt(expenseEntry.bookingPeriod ))) + '-' + fiscalYearId
                def customerId = expenseEntry.customerId
                def debtorId = expenseEntry.debtorId

                BigDecimal totalAmountIncVat = new BigDecimal(expenseEntry.totalAmountIncVat)
                def tAmountIncVat = twoDForm.format(totalAmountIncVat)

                if(expenseEntry.invStatus == "settled"){
                    changeBooking = ""
                }else{
                    changeBooking = "<a href='javascript:changeBooking(\"${expenseEntry.id}\",\"${customerId}\",\"${debtorId}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${context}/images/edit.png\"></a>"
                }

                obj.cell = ["invoiceNumber": expenseEntry.invoiceNumber, "bookingPeriod": bookingPeriodFormat,
                            "debtorName": expenseEntry.debtorName, "invoiceDate": expenseEntry.invoiceDate,
                            "paymentReference": expenseEntry.paymentRef, "totalGlAmount": tAmountIncVat,
                            "totalVat": expenseEntry.totalVat, "action": changeBooking]

                quickExpenseList.add(obj)
            }

            return quickExpenseList

        } catch (Exception ex) {
            quickExpenseList = []
            return quickExpenseList
        }
    }

    public List reportWrapEntryIncomeBookedInGrid(List<GroovyRowResult> quickEntries, int start,context) {
        List quickExpenseList = new ArrayList()
        def expenseEntry
        GridEntity obj
        String changeBooking
        try {
            int counter = start + 1
            for (int i = 0; i < quickEntries.size(); i++) {
                expenseEntry = quickEntries[i];

                def showtotalGlAmounta = expenseEntry.totalGlAmount
                def showtotalGlAmount=String.format("%.2f", showtotalGlAmounta)
                def showtotalVata = expenseEntry.totalVat
                def showtotalVat=String.format("%.2f", showtotalVata)

                obj = new GridEntity();
                obj.id = expenseEntry.id

                if(expenseEntry.invStatus == "settled"){

                }else{
                    changeBooking = "<a href='javascript:changeBooking(\"${expenseEntry.id}\",\"${expenseEntry.customerId}\",\"${expenseEntry.debtorId}\",\"${context}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${context}/images/edit.png\"></a>"
                }

                obj.cell = ["invoiceNumber": expenseEntry.invoiceNumber, "debtorName":expenseEntry.debtorName,
                            "totalGlAmount":showtotalGlAmount, "totalVat":showtotalVat,"bookingPeriod": expenseEntry.bookinhPeriod,
                            "transactionDate":expenseEntry.transactionDate,"paymentReference": expenseEntry.paymentRef,"action":changeBooking ]

                quickExpenseList.add(obj)
                counter++;
            }
            return quickExpenseList;
        } catch (Exception ex) {
//            log.error(ex.getMessage());
            quickExpenseList = [];
            return quickExpenseList;
        }
    }

    public List wrapDebtorsFromCustomerInGrid(List<GroovyRowResult> quickEntries,fiscalYearId,context) {
        List quickExpenseList = new ArrayList()
        def debtorsEntry
        GridEntity obj
        String changeBooking
        try {
            int counter = 1
            for (int i = 0; i < quickEntries.size(); i++) {

                debtorsEntry = quickEntries[i];
                obj = new GridEntity();
                obj.id = debtorsEntry.id

                DecimalFormat twoDForm = new DecimalFormat("#.00");
                BigDecimal showtotalGlAmounta = new BigDecimal(debtorsEntry.totalAmountIncVat)
                def showtotalGlAmount = twoDForm.format(showtotalGlAmounta)
                BigDecimal showtotalVata = new BigDecimal(debtorsEntry.totalVat)
                def showtotalVat = twoDForm.format(showtotalVata)
                def debtorId = debtorsEntry.debtorId
                def customerId = debtorsEntry.customerId

                if(debtorsEntry.status == "settled"){
                    changeBooking = "";
                }else{
                    changeBooking = "<a href='javascript:changeBooking(\"${debtorsEntry.id}\",\"${customerId}\",\"${debtorId}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${context}/images/edit.png\"></a>"
                }

                obj.cell = ["invoiceNumber":debtorsEntry.payRef,"debtorName":debtorsEntry.debtorName,
                            "invoiceDate":debtorsEntry.invoiceDate,
                            "totalGlAmount": showtotalGlAmount,"totalVat":showtotalVat,
                            "factorFee": debtorsEntry.factorFee,"status":debtorsEntry.status,"docChecked":debtorsEntry.docChecked,
                            "action": changeBooking]

                quickExpenseList.add(obj)
                counter++;
            }
            return quickExpenseList;
        } catch (Exception ex) {

            quickExpenseList = [];
            return quickExpenseList;
        }
    }

    public List wrapDebtorsFromCustomerInGridInvoices(List<GroovyRowResult> quickEntries,fiscalYearId,context) {
        List quickExpenseList = new ArrayList()
        def debtorsEntry
        GridEntity obj
        String changeBooking
        DecimalFormat twoDForm = new DecimalFormat("#.00")

        try {
            for (int i = 0; i < quickEntries.size(); i++) {

                debtorsEntry = quickEntries[i]
                obj = new GridEntity()
                obj.id = debtorsEntry.id

                BigDecimal showtotalGlAmountVat = new BigDecimal(debtorsEntry.totalAmountIncVat+debtorsEntry.totalVat)
                def showTotalVat = twoDForm.format(showtotalGlAmountVat)

                def debtorId = debtorsEntry.debtorId
                def customerId = debtorsEntry.customerId

                if(debtorsEntry.status == "settled"){
                    changeBooking = ""
                }else{
                    changeBooking = "<a href='javascript:changeBooking(\"${debtorsEntry.id}\",\"${customerId}\",\"${debtorId}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${context}/images/edit.png\"></a>"
                }

                obj.cell = ["invoiceNumber":debtorsEntry.payRef,"debtorName":debtorsEntry.debtorName,
                            "invoiceDate":debtorsEntry.invoiceDate,
                            "totalGlAmountVat": showTotalVat,
                            "factorFee": debtorsEntry.factorFee,"status":debtorsEntry.status,"docChecked":debtorsEntry.docChecked,
                            "action": changeBooking]

                quickExpenseList.add(obj)
            }

            return quickExpenseList

        } catch (Exception ex) {
            quickExpenseList = []
            return quickExpenseList
        }
    }

    List reportWrapEntryCustomerSettlement(List<GroovyRowResult> quickEntries,def context, def userCustomerId) {
        List quickExpenseList = new ArrayList()
        def expenseEntry
        GridEntity obj

        String editSettlementLink
        String actionLink
        String expordPdf
        String expordPdfActLink

        try {
            int counter = 1
            for (int i = 0; i < quickEntries.size(); i++) {
                expenseEntry = quickEntries[i];

                def showtotalGlAmounta = expenseEntry.totalGlAmount
                def showtotalGlAmount = String.format("%.2f", showtotalGlAmounta)
                def showtotalVata = expenseEntry.totalVat
                def showtotalVat = String.format("%.2f", showtotalVata)

                def showInvoiceAmta = expenseEntry.factoredInvAmount
                def showInvoiceAmt = String.format("%.2f", showInvoiceAmta)

                def vendorId = expenseEntry.vendorId
                def shopId = expenseEntry.shopId
                def sendFlag = expenseEntry.sendFlag

                obj = new GridEntity();
                obj.id = expenseEntry.id

                if(userCustomerId){
                    expordPdf = "<a style='margin-left: 20px;' href='javascript:directSettlementDownload(\"${expenseEntry.id}\",\"${vendorId}\")'><img width=\"18\" height=\"18\" alt=\"SEND\" src=\"${context}/images/download-pdf.png\"></a>"
                    actionLink =  expordPdf

                }else {
                    editSettlementLink = "<a class=\"edit-link-customer-settlement\" style=\"margin-right: 5px\" href='javascript:editSettlement(\"${expenseEntry.id}\",\"${vendorId}\",\"${sendFlag}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${context}/images/edit.png\"></a>"
                    expordPdf = "<a href='javascript:previewPDF(\"${expenseEntry.id}\",\"${vendorId}\")'><img width=\"18\" height=\"18\" alt=\"SEND\" src=\"${context}/images/download-pdf.png\"></a>"
                    actionLink = editSettlementLink + "  " + expordPdf
                }

                obj.cell = ["invoiceNumber":expenseEntry.invoiceNumber,"vendorName": expenseEntry.vendorName,
                            "factoredInvAmount": expenseEntry.factoredInvAmount,
                            "totalGlAmount": showtotalGlAmount,"totalVat": showtotalVat,
                            "transactionDate": expenseEntry.transactionDate,"action":actionLink]

                quickExpenseList.add(obj)
                counter++
            }
            return quickExpenseList
        } catch (Exception ex) {
            print(ex.getMessage())
            quickExpenseList = []
            return quickExpenseList
        }
    }

    public List reportWrapEntryCustomerSettlementSend(List<GroovyRowResult> quickEntries,def context) {
        List quickExpenseList = new ArrayList()
        def expenseEntry
        GridEntity obj

        String sendSettlementLink
        String actionLink
        String checkBoxCtrl

        try {
            int counter = 1
            for (int i = 0; i < quickEntries.size(); i++) {
                expenseEntry = quickEntries[i];

                def showtotalGlAmounta = expenseEntry.totalGlAmount
                def showtotalGlAmount = String.format("%.2f", showtotalGlAmounta)
//                def showtotalVata = expenseEntry.totalVat
//                def showtotalVat = String.format("%.2f", showtotalVata)
                def showTobePaidAmounta = expenseEntry.tobePaidAmount
                def showTobePaidAmount = String.format("%.2f", showTobePaidAmounta)

                def showInvoiceAmta = expenseEntry.factoredInvAmount
                def showInvoiceAmt = String.format("%.2f", showInvoiceAmta)

                def vendorId = expenseEntry.vendorId

                obj = new GridEntity();
                obj.id = expenseEntry.id

                sendSettlementLink = "<a href='javascript:previewPDF(\"${expenseEntry.id}\",\"${vendorId}\")'><img width=\"32\" height=\"32\" alt=\"SEND\" src=\"${context}/images/document-preview.png\"></a>"
                actionLink = sendSettlementLink

                checkBoxCtrl = "<input name=\"checkSettlemnt_${expenseEntry.id}\" value=\"${expenseEntry.id}_${vendorId}\" id=\"checkSettlemnt_${expenseEntry.id}\" type=\"checkbox\"></input>"

                obj.cell = ["checkCtrl":checkBoxCtrl,"invoiceNumber":expenseEntry.invoiceNumber,"vendorName": expenseEntry.vendorName,
                            "vendorEmail": expenseEntry.vendorEmail,
                            "factoredInvAmount": expenseEntry.factoredInvAmount,
                            "totalGlAmount": showtotalGlAmount,"tobePaidAmount": showTobePaidAmount,
                            "action":actionLink]

                quickExpenseList.add(obj)
                counter++;
            }
            return quickExpenseList;
        } catch (Exception ex) {
//            log.error(ex.getMessage());
            quickExpenseList = [];
            return quickExpenseList;
        }
    }

    def getInvoicesGridResult(def quickEntries,def context) {
        def custInvoiceList = new ArrayList()
        def invoiceEntry
        GridEntity obj
        String checkBoxId
        String buttonOnBlocked
        int counter = 0
        try {
            for (int i = 0; i < quickEntries.size(); i++) {
                invoiceEntry = quickEntries[i]
                String blockedOnHoldText = "Delete"
                String btnColor = "#ffffff"
                if(invoiceEntry.isBlocked){
                    blockedOnHoldText = "Deleted"
                    btnColor = "#f73d3d"
                }
                def flag = "new"
                if(invoiceEntry.paidStatus == 2){
                    flag = "exported"
                }
                else if(invoiceEntry.isBlocked){
                    flag = "blocked"
                }
                checkBoxId = "<input class=\"${flag}\" name=\"checkBlocked_${invoiceEntry.settlementNo}\" value=\"${invoiceEntry.settlementNo}\" id=\"checkBlocked_${invoiceEntry.settlementNo}\" type=\"checkbox\"></input>"
                buttonOnBlocked = "<input name=\"buttonOnBlocked_${invoiceEntry.settlementNo}\" value=\"${blockedOnHoldText}\" id=\"buttonOnBlocked_${invoiceEntry.settlementNo}\" onclick=\"updatePaymentOnBlocked(${invoiceEntry.settlementNo},this)\" style=\"width:100px;background-color:${btnColor};\" type=\"button\"></input>"
                obj = new GridEntity()
                obj.id = invoiceEntry.settlementNo
                def tobePaidAmount
                if(invoiceEntry.manualDiscount)
                    tobePaidAmount = invoiceEntry.tobePaidAmount - invoiceEntry.manualDiscount
                else
                    tobePaidAmount = invoiceEntry.tobePaidAmount
                obj.cell = ["checkId":checkBoxId,
                            "settlementNo":invoiceEntry.settlementNo,
                            "settlementDate":invoiceEntry.settlementDate,
                            "customerName": invoiceEntry.customerName,
                            "bankAccNo": invoiceEntry.bankAccNo,
                            "factInvAmount": dotCommaDecimalFormat(invoiceEntry.factInvAmount),
                            "tobePaidAmount": dotCommaDecimalFormat(tobePaidAmount),
                            "checkOnBlocked":buttonOnBlocked,
                            "paidAmount":dotCommaDecimalFormat(invoiceEntry.paidAmount),
                            "rowFlag":flag]
                custInvoiceList.add(obj)
                counter++
            }
        }
        catch(Exception e){

        }

        return [custInvoiceList:custInvoiceList,total:counter]
    }

     List reportWrapEntryDebtorInvoiceReminder(List<GroovyRowResult> quickEntries, def context, def customerSettlementService) {

        List quickExpenseList = new ArrayList()
        def invoiceEntry
        GridEntity obj

        String checkBoxId
        String buttonOnHold
        String sendRemind
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd")

        try {

            for (int i = 0; i < quickEntries.size(); i++) {
                invoiceEntry = quickEntries[i]
                def reminderNumber = customerSettlementService.getReminderTypeUsingDateLogic(invoiceEntry)
                //below code has N + 1 problem
                //def reminderDayArr = customerSettlementService.getReminderDaysFromId(invoiceEntry.id)
                if(reminderNumber == 0) continue

                def showTobePaidAmounta = invoiceEntry.tobePaidAmount
                def showTobePaidAmount = dotCommaDecimalFormat(showTobePaidAmounta)

                def showInvoiceAmta = invoiceEntry.invoiceAmount
                def showInvoiceAmt = dotCommaDecimalFormat(showInvoiceAmta)

                def debtorId = invoiceEntry.debtorId
                def reminderType = invoiceEntry.reminderType
                def invoiceDate=invoiceEntry.transactionDate

                //Reminder on hold status
                String reminderOnHoldText = "NO"
                String btnColor = "#ffffff"

                if(invoiceEntry.onHoldFlag == 1){
                    reminderOnHoldText = "YES"
                    btnColor = "#f73d3d"
                }

                def tempReminderDate
                //Reminder Status
                String reminderTypeString = "First"
                if(reminderType == 1){
                    reminderTypeString = "Second"
                    tempReminderDate = df.parse(invoiceEntry.reminderDate1.toString())
                    tempReminderDate.setDate(tempReminderDate.getDate() + invoiceEntry.secondReminderDays)
                }else if(reminderType == 2){
                    reminderTypeString = "Third"
                    tempReminderDate = df.parse(invoiceEntry.reminderDate2.toString())
                    tempReminderDate.setDate(tempReminderDate.getDate() + invoiceEntry.thirdReminderDays)
                }else if(reminderType == 3){
                    reminderTypeString = "Final"
                    tempReminderDate = df.parse(invoiceEntry.reminderDate3.toString())
                    tempReminderDate.setDate(tempReminderDate.getDate() + invoiceEntry.finalReminderDays)
                }
                Date today = new Date()

                def isMail = quickEntries[i][3]

                checkBoxId = "<input name=\"checkReminder_${invoiceEntry.id}\" value=\"${invoiceEntry.id}_${debtorId}_${reminderType}_${invoiceEntry.onHoldFlag}\" id=\"checkSettlemnt_${invoiceEntry.id}\" type=\"checkbox\"></input>"
                buttonOnHold = "<input name=\"buttonOnHold_${invoiceEntry.id}\" value=\"${reminderOnHoldText}\" id=\"buttonOnHold_${invoiceEntry.id}\" onclick=\"updateReminderOnHold(${invoiceEntry.id},this)\" style=\"width:100px;background-color:${btnColor};\" type=\"button\"></input>"
                sendRemind = "<input name=\"sendRemind_${invoiceEntry.id}\" value=\"Send\" id=\"sendRemind_${invoiceEntry.id}\" onclick=\"sendReminderNow(${invoiceEntry.id},this)\" style=\"width:80px;\" type=\"button\"></input>"
                if((showTobePaidAmounta > 0.00 ||  showTobePaidAmounta < -0.05) && today > tempReminderDate){
                    obj = new GridEntity();
                    obj.id = invoiceEntry.id
                    obj.cell = ["checkId":checkBoxId,
                                "paymentRef":invoiceEntry.paymentRef,
                                "invoiceDate":invoiceDate,
                                "debtorName": invoiceEntry.debtorName,
                                "invoiceAmount": showInvoiceAmt,
                                "tobePaidAmount": showTobePaidAmount,
                                "reminderType": reminderTypeString,
                                "checkOnHold":buttonOnHold,
                                "sendReminder":sendRemind,
                                "isMail": isMail]

                    quickExpenseList.add(obj)
                }
            }
            return quickExpenseList
        } catch (Exception ex) {
            quickExpenseList = []
            return quickExpenseList
        }
    }

    def dotCommaDecimalFormat(def decimalData = 0){
        DecimalFormat formatter = new DecimalFormat("€ #,##0.00;€ -#,##0.00")
        def formattedData = formatter.format(decimalData)
        formattedData = formattedData.replace(".","-")
        formattedData = formattedData.replaceAll(",",".")
        formattedData = formattedData.replace("-",",")
        return formattedData
    }

    def amountConvertToCDotDCommaEuro(def decimalData = 0){
        DecimalFormat formatter = new DecimalFormat("€ #,##0.00;€ -#,##0.00")
        def formattedData = formatter.format(decimalData)
        formattedData = formattedData.replace(".",":")
        formattedData = formattedData.replaceAll(",",".")
        formattedData = formattedData.replace(":",",")
        return formattedData
    }

    def parseCheckIdList(def params){

        ArrayList checkedIdArr = new ArrayList()

        params.each { phnsParams ->
            def tempParamsStr = phnsParams.toString();

            ArrayList tempShowParamsArr = new ArrayList()
            tempShowParamsArr = tempParamsStr.split("=")

            if (tempParamsStr.contains("checkSettlemnt_")) {

                def value = tempShowParamsArr[1]
                def valueArr = value.split("_");

                Map map = ["id": 0, "customerId": 0]
                map.id = valueArr[0]
                map.customerId = valueArr[1]

                checkedIdArr << map
            }
        }

        return checkedIdArr;
    }

    def parseReminderCheckIdList(def params){

        ArrayList checkedIdArr = new ArrayList()

        params.each { phnsParams ->
            def tempParamsStr = phnsParams.toString();

            ArrayList tempShowParamsArr = new ArrayList()
            tempShowParamsArr = tempParamsStr.split("=")

            if(tempParamsStr.contains("checkReminder_")){
                def value = tempShowParamsArr[1]
                def valueArr = value.split("_");

                Map map = ["id": 0, "debtorId": 0,"reminderType": 0,"onHoldFlag":0]
                map.id = valueArr[0]
                map.debtorId = valueArr[1]
                map.reminderType = valueArr[2]
                map.onHoldFlag = valueArr[3]

                checkedIdArr << map
            }
        }

        return checkedIdArr
    }

    List creditLimitWrapEntry(List<GroovyRowResult> creditLimitInfo) {

        List creditLimitInfoList = new ArrayList()
        def debtorCreditLimit
        GridEntity obj
        DecimalFormat df = new DecimalFormat("#.##")
        long size = creditLimitInfo.size()

        try {

            for (int i = 0; i < size; i++) {
                debtorCreditLimit = creditLimitInfo[i]

                def insuadAmount = QuickEntryUtil.dottedDecimal(debtorCreditLimit.insuadAmount)
                def outStandingAmount =  QuickEntryUtil.dottedDecimal(Double.valueOf(df.format(debtorCreditLimit.dueAmountSum)))
                def overDue = Math.abs(debtorCreditLimit.overDue)
                    overDue =  QuickEntryUtil.dottedDecimal(Double.valueOf(df.format(overDue)))

                    obj = new GridEntity()
                    obj.cell = ["debtorName":debtorCreditLimit.customerName,
                                "creditLimit":insuadAmount,
                                "outStandingAmount":outStandingAmount,
                                "overDue": overDue]


                    creditLimitInfoList.add(obj)
                }

            return creditLimitInfoList
        } catch (Exception ex) {
            creditLimitInfoList = []
            return creditLimitInfoList
        }
    }

    List customerCreditLimitWrapEntry(List<GroovyRowResult> customerCreditLimitInfo) {

        List creditLimitInfoList = new ArrayList()
        def customerCreditLimit
        GridEntity obj
        DecimalFormat df = new DecimalFormat("#.##")
        long size = customerCreditLimitInfo.size()

        try {

            for (int i = 0; i < size; i++) {
                customerCreditLimit = customerCreditLimitInfo[i]

                def creditLimit =  QuickEntryUtil.dottedDecimal(Double.valueOf(df.format(customerCreditLimit.creditLimit)))
                def outStandingAmount =  QuickEntryUtil.dottedDecimal(Double.valueOf(df.format(customerCreditLimit.dueAmountSum)))
                def overDue = Math.abs(customerCreditLimit.overDue)
                    overDue =  QuickEntryUtil.dottedDecimal(Double.valueOf(df.format(overDue)))

                obj = new GridEntity()
                obj.cell = ["debtorName":customerCreditLimit.customerName,
                            "creditLimit": creditLimit,
                            "outStandingAmount":outStandingAmount,
                            "overDue": overDue]

                    creditLimitInfoList.add(obj)
            }

            return creditLimitInfoList
        } catch (Exception ex) {
            creditLimitInfoList = []
            return creditLimitInfoList
        }
    }

}
