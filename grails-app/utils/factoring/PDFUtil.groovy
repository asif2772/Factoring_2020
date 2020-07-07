package factoring

import grails.util.Pair
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDFont
import org.grails.plugins.web.taglib.ValidationTagLib

import java.awt.Font
import java.awt.FontMetrics
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.text.DecimalFormat

class PDFUtil {
    def g=new ValidationTagLib()
    PDPage page = null;
    PDPageContentStream contentStream = null;
    PDDocument pd = null

    float lbX = 0.0
    float lbY = 0.0
    float rtX = 0.0
    float rtY = 0.0

    float headerHeight = 350.0
    float footerHeight = 50.0

    float pageWidth = 603.0
    float pageHeight = 837.0

    float leftMargin = 100.0;
    float rightMargin = 100.0


    int globalLineCount = 0
    int gap = 50
    int currentPageIndex = 0

    int mailAddressStartY = 0;
    int mailSubjectStartY = 0;
    int mailBodyStartY = 0;
    int mailConclusionStartY = 0;

    def getCurrentPageIndex(){
        return currentPageIndex
    }
    def incrementPageIndex(){
        currentPageIndex++
    }

    def writeMailContentToPDF(PDDocument pd,def userInfo,def invSummary,def customerInfoMap,def reminderNumber,dueAmount, def companyInfo, def updatedDate) {
        this.pd = pd
        List allPages = pd.getDocumentCatalog().getAllPages()
        PDPage page = (PDPage) allPages.get(0);
        setupPage(page, pd)
        //headerHeight,footerHeight,leftMargin,rightMargin
        adjustBoundBox(120.0, 70.0, 55.0, 100.0)

        //get mail texts from table
        def reminderMailTextArr = new BudgetViewDatabaseService().executeQuery('select * from send_remainder_mail_text')

        //Mail Address
        writeUserInfo(userInfo, invSummary)

        //Mail Subject
        writeMailSubject(invSummary,reminderNumber,reminderMailTextArr, companyInfo)

        //Mail Body
        writeMailBodyForReminder(invSummary,customerInfoMap,updatedDate,"bankinfo",dueAmount, companyInfo, reminderMailTextArr,reminderNumber)

        //Mail Conclusion
        writeMailConclusion(reminderNumber, companyInfo, reminderMailTextArr)

        contentStream.close()
    }

    def writeMailBodyForReminder(def invSummary,def customerInfoMap,def updatedDate,def cusBankInfoMap,def dueAmount, def companyInfo, def reminderMailTextArr, def reminderNumber){
        float fontSize = 10.50f
        PDFont font = PDType1Font.HELVETICA
        contentStream.setFont(font, fontSize)

        int nNextColX = lbX;
        int nDescStartY = mailBodyStartY;

        DecimalFormat df = new DecimalFormat("####0.00")

        //1st Line
        String bodyLine1 = reminderMailTextArr[0][8] + ","
        drawAnyString(bodyLine1,nNextColX,nDescStartY);

        //2nd Line
        nDescStartY = nDescStartY - 25;
        Date invoiceDate
        invoiceDate = invSummary[0][9]
        String invoiceDateString = invoiceDate.format("d-MM-yyyy")
        Date dueDate = invSummary[0][7]
        String dueDateString = dueDate.format("d-MM-yyyy")
        Double dueAmountArr = dueAmount[0][0]
        dueAmountArr.toString()

        def invoicedateArr = invoiceDateString.split('-')
        def tempInvoiceDate = invoicedateArr[0]
        def invoiceMonth = Integer.parseInt(invoicedateArr[1])
        def invoiceYear = invoicedateArr[2]
        def invoiceMonthInDutch = getFullMonthInDutch(invoiceMonth)
        invoiceDateString =tempInvoiceDate+" "+invoiceMonthInDutch+" "+invoiceYear
        def totalInvoiceAmount = invSummary[0][5] + invSummary[0][16]
            totalInvoiceAmount = df.format(totalInvoiceAmount)
            totalInvoiceAmount = totalInvoiceAmount.toString().replace(".",",")
        def totalInvoiceAmountStr = "EUR " + totalInvoiceAmount

        String bodyLine2 = ""
        bodyLine2 = reminderMailTextArr[0][9]+ " " + invoiceDateString + " " +reminderMailTextArr[0][10] + " "
        String bodyLine2_a = totalInvoiceAmountStr + " "
        String bodyLine2_b = reminderMailTextArr[0][11]

        List<Pair<String, Boolean>> wordList2 = new ArrayList<>()
        wordList2.add( new Pair<String, Boolean>(bodyLine2, false) )
        wordList2.add( new Pair<String, Boolean>(bodyLine2_a, true) )
        wordList2.add( new Pair<String, Boolean>(bodyLine2_b, false) )

        drawAnyStringDynamic(wordList2,nNextColX,nDescStartY)

        nDescStartY = nDescStartY - 15;
        String bodyLine2a = ""
        bodyLine2a = invSummary[0][8]

        if (reminderNumber.reminderType == "1") {

            drawAnyString(bodyLine2a,nNextColX,nDescStartY)

        } else if (reminderNumber.reminderType == "2") {

            def getFirstReminder = new BudgetViewDatabaseService().executeQuery("""select ii.reminder_date1
                                        from invoice_income ii where ii.id = ${invSummary[0][17]}""")


            Date firstDate = getFirstReminder[0][0]
            String firstDateString = firstDate.format("d-M-yyyy")

            def firstDateArr = firstDateString.split('-')
            def tempUpdatedDate = firstDateArr[0]
            def updatedMonth = Integer.parseInt(firstDateArr[1])
            def updatedYear = firstDateArr[2]
            def updatedMonthInDutch = getFullMonthInDutch(updatedMonth)
            firstDateString =tempUpdatedDate+" "+updatedMonthInDutch+" "+updatedYear
            def bodyLine2SecReminder = invSummary[0][8] + ". " + reminderMailTextArr[0][12] + " " + firstDateString + "."
            drawAnyString(bodyLine2SecReminder,nNextColX,nDescStartY)

        } else if (reminderNumber.reminderType == "3") {


            def getFirstAndSecReminder = new BudgetViewDatabaseService().executeQuery("""select ii.reminder_date1, 
                                        ii.reminder_date2 from invoice_income ii where ii.id = ${invSummary[0][17]}""")

            Date firstDate = getFirstAndSecReminder[0][0]
            String firstDateString = firstDate.format("d-M-yyyy")

            def firstDateArr = firstDateString.split('-')
            def tempUpdatedDate = firstDateArr[0]
            def updatedMonth = Integer.parseInt(firstDateArr[1])
            def updatedYear = firstDateArr[2]
            def updatedMonthInDutch = getFullMonthInDutch(updatedMonth)
                firstDateString =tempUpdatedDate+" "+updatedMonthInDutch+" "+updatedYear

            Date secDate = getFirstAndSecReminder[0][1]
            String secDateString = secDate.format("d-M-yyyy")

            def secDateArr = secDateString.split('-')
            def secUpdatedDate = secDateArr[0]
            def secupdatedMonth = Integer.parseInt(secDateArr[1])
            def secupdatedYear = secDateArr[2]
            def secupdatedMonthInDutch = getFullMonthInDutch(secupdatedMonth)
                secDateString =secUpdatedDate+" "+secupdatedMonthInDutch+" "+secupdatedYear

            def bodyLine2SecReminder = invSummary[0][8] + ". " + reminderMailTextArr[0][12] + " " + firstDateString

                drawAnyString(bodyLine2SecReminder,nNextColX,nDescStartY)

            def sesLn = reminderMailTextArr[0][13] + " " + secDateString
            def replaceCma = sesLn.toString().replaceAll(",","")
                sesLn = replaceCma
                nDescStartY = nDescStartY - 15
                drawAnyString(sesLn,nNextColX,nDescStartY)

        } else {

            def getFirstAndSecAndLastReminder = new BudgetViewDatabaseService().executeQuery("""select ii.reminder_date1, 
                                        ii.reminder_date2, ii.reminder_date3 from invoice_income ii where ii.id = ${invSummary[0][17]}""")

            Date firstDate = getFirstAndSecAndLastReminder[0][0]
            String firstDateString = firstDate.format("d-M-yyyy")

            def firstDateArr = firstDateString.split('-')
            def tempUpdatedDate = firstDateArr[0]
            def updatedMonth = Integer.parseInt(firstDateArr[1])
            def updatedYear = firstDateArr[2]
            def updatedMonthInDutch = getFullMonthInDutch(updatedMonth)
            firstDateString =tempUpdatedDate+" "+updatedMonthInDutch+" "+updatedYear

            Date secDate = getFirstAndSecAndLastReminder[0][1]
            String secDateString = secDate.format("d-M-yyyy")

            def secDateArr = secDateString.split('-')
            def secUpdatedDate = secDateArr[0]
            def secupdatedMonth = Integer.parseInt(secDateArr[1])
            def secupdatedYear = secDateArr[2]
            def secupdatedMonthInDutch = getFullMonthInDutch(secupdatedMonth)
            secDateString =secUpdatedDate+" "+secupdatedMonthInDutch+" "+secupdatedYear

            Date thDate = getFirstAndSecAndLastReminder[0][1]
            String thDateString = thDate.format("d-M-yyyy")

            def thDateArr = thDateString.split('-')
            def thUpdatedDate = thDateArr[0]
            def thupdatedMonth = Integer.parseInt(thDateArr[1])
            def thupdatedYear = thDateArr[2]
            def thupdatedMonthInDutch = getFullMonthInDutch(thupdatedMonth)
                thDateString = thUpdatedDate+" "+thupdatedMonthInDutch+" "+thupdatedYear
            def bodyLine2SecReminder = invSummary[0][8] + ". " + reminderMailTextArr[0][12] + " " + firstDateString


            drawAnyString(bodyLine2SecReminder,nNextColX,nDescStartY)

            def sesLn = reminderMailTextArr[0][13] + " " +secDateString + " " + reminderMailTextArr[0][14] + " " + thDateString + "."
            def replaceCma = sesLn.toString().replaceAll(",","")
            sesLn = replaceCma
            nDescStartY = nDescStartY - 15
            drawAnyString(sesLn,nNextColX,nDescStartY)
        }


        //2_1 Line
        nDescStartY = nDescStartY - 25;
        String bodyLine2_1 =  reminderMailTextArr[0][15]
        drawAnyString(bodyLine2_1,nNextColX,nDescStartY);

        //3rd Line
        nDescStartY = nDescStartY - 25
        String line3 = reminderMailTextArr[0][17]
        def line3b = line3.split('<company setup / IBAN>')

        String bodyLine3 = line3b[0]
        String bodyLine3First = bodyLine3.substring(0, bodyLine3.indexOf("<unpaid amount>"));
        def dueAmountDcml = df.format(dueAmountArr)
        String bodyLine3Second =  "EUR " + dueAmountDcml.toString().replace(".", ",");
        String bodyLine3Third = bodyLine3.substring(bodyLine3.indexOf("<unpaid amount>") + "<unpaid amount>".size());

        List<Pair<String, Boolean>> wordListLine3 = new ArrayList<>();
        wordListLine3.add(new Pair<String, Boolean>(bodyLine3First, false) )
        wordListLine3.add(new Pair<String, Boolean>(bodyLine3Second, true) )
        wordListLine3.add(new Pair<String, Boolean>(bodyLine3Third, false) )

        drawAnyStringDynamic(wordListLine3,nNextColX,nDescStartY)

        //3rd_a Line
        nDescStartY = nDescStartY - 15
        String bodyLine3c =  companyInfo[0][9]+":" + " "+ companyInfo[0][50] + " "
        List<Pair<String, Boolean>> wordList = new ArrayList<>();
        wordList.add(new Pair<String, Boolean>(bodyLine3c, true) )
        wordList.add(new Pair<String, Boolean>(reminderMailTextArr[0][18] + ".", false) )
        drawAnyStringDynamic(wordList,nNextColX,nDescStartY)

        contentStream.setFont(font, fontSize)

        //4th Line
        nDescStartY = nDescStartY - 25
//        String updatedDateString = updatedDate.format("d-M-yyyy")
        String updatedDateString = updatedDate
        def updatedDateArr = updatedDateString.split('-')
        def tempUpdatedDate = updatedDateArr[0]
        def updatedMonth = Integer.parseInt(updatedDateArr[1])
        def updatedYear = updatedDateArr[2]
        def updatedMonthInDutch = getFullMonthInDutch(updatedMonth)
        updatedDateString =tempUpdatedDate+" "+updatedMonthInDutch+" "+updatedYear
        String bodyLine4 = reminderMailTextArr[0][19] + " " + updatedDateString + "."
        drawAnyString(bodyLine4,nNextColX,nDescStartY)

        //5th Line
        nDescStartY = nDescStartY - 25
        String bodyLine5 = reminderMailTextArr[0][20]
        drawAnyString(bodyLine5,nNextColX,nDescStartY)

        //6th Line
        nDescStartY = nDescStartY - 25
        String bodyLine6 = reminderMailTextArr[0][21]
        def strReplaceEmail = bodyLine6.replaceAll("<Company address /email>", companyInfo[0][16])
        def splitPhnStr = strReplaceEmail.split("<company address/phone number.")
        drawAnyString(splitPhnStr,nNextColX,nDescStartY)

        //6_ath Line
        nDescStartY = nDescStartY - 15
        String bodyLine6a = companyInfo[0][27]
        drawAnyString(bodyLine6a,nNextColX,nDescStartY)

        //7 th Line
        if (reminderNumber.reminderType == "4") {
            nDescStartY = nDescStartY - 25
            String bodyLine7 = reminderMailTextArr[0][22]
            drawAnyString(bodyLine7,nNextColX,nDescStartY)
        }

        mailConclusionStartY = nDescStartY - 15
    }

    def writeMailConclusion(def reminderNumber, def companyInfo, def reminderMailTextArr){

        float fontSize = 10.50f
        PDFont font = PDType1Font.HELVETICA
        contentStream.setFont(font, fontSize)

        int nNextColX = lbX;
        int nDescStartY = mailConclusionStartY;

        //7th Line
        nDescStartY = nDescStartY - 30
        String bodyLine7 = reminderMailTextArr[0][23]
        def bodyLine7a = bodyLine7.split(",")
        def bodyLine7b = bodyLine7a[0] + ","
        drawAnyString(bodyLine7b,nNextColX,nDescStartY)

        //8th Line
        nDescStartY = nDescStartY - 30
        String bodyLine8 = companyInfo[0][9]
        drawAnyString(bodyLine8,nNextColX,nDescStartY)

        //3rd Line
         nDescStartY = nDescStartY - 30;
        String concluLine3 = ""
        concluLine3 = ""

        drawAnyString(concluLine3,nNextColX,nDescStartY);
    }

    def writeMailAddress(def userInfo,def customerName) {

        contentStream.beginText();

        PDFont font = PDType1Font.HELVETICA
        page.getResources().getFonts()
        float fontSize = 11.0f
        contentStream.setFont(font, fontSize)
        float textHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;

        float lineNoIndex = 0.0;
        float linegap

        def addressLine1
        def addressLine2
        def city
        def postalCode
        def contactPersonName

        if (userInfo.size() > 0) {
            addressLine1 = userInfo[0][0] ? userInfo[0][0] : ""
            addressLine2 = userInfo[0][1] ? userInfo[0][1] : ""
            city = userInfo[0][2] ? userInfo[0][2] : ""
            postalCode = userInfo[0][3] ? userInfo[0][3] : ""
            contactPersonName = userInfo[0][4] ? userInfo[0][4] : ""
        } else {
            addressLine1 = ""
            addressLine2 = ""
            city = ""
            postalCode = ""
            contactPersonName = ""
        }



        int addressEndY = 0
        linegap = textHeight + 2.00
        //User Name
        contentStream.moveTextPositionByAmount(lbX, rtY)
        contentStream.drawString("${customerName}")

        addressEndY += linegap
        contentStream.moveTextPositionByAmount(0, -linegap)
        contentStream.drawString("${contactPersonName}")

        //Address
        addressEndY += linegap
        contentStream.moveTextPositionByAmount(0, -linegap)
        contentStream.drawString("${addressLine1}")
        contentStream.drawString("  ")
        contentStream.drawString("${addressLine2}")

        //Country
        addressEndY += linegap
        contentStream.moveTextPositionByAmount(0, -linegap)
        contentStream.drawString("${postalCode}")
        contentStream.drawString("  ")
        contentStream.drawString("${city}")

        contentStream.endText();

        mailSubjectStartY =  addressEndY + 50;
    }

    def writeMailSubject(def invSummary,def reminderNumber, def reminderMailTextArr, def companyInfo){

        println "sdf"

        float fontSize = 10.50f
        PDFont font = PDType1Font.HELVETICA
        contentStream.setFont(font, fontSize)

        int nNextColX = lbX;
        int nDescStartY = rtY - 100;

        Date todayDate = new Date()
        String todayDateString = todayDate.format("yyyy-MM-d")
        def todayArr = todayDateString.split('-')
        def currentDate= todayArr[2]
        def currentMonth = Calendar.getInstance().get(Calendar.MONTH)+1
        def currentMonthInDutch= getFullMonthInDutch(currentMonth)
        def currentYear= todayArr[0]
        todayDateString = currentDate + " "+ currentMonthInDutch +" "+ currentYear


        //Line 1
        String subjectLine1 = companyInfo[0][7] + ", " + todayDateString
        drawAnyString(subjectLine1,nNextColX,nDescStartY);

        //Line 2
        nDescStartY = nDescStartY - 30;
        String subjectLine2Part1 = reminderMailTextArr[0][2] + ": "

        drawAnyString(subjectLine2Part1,nNextColX,nDescStartY);
        int text_width = (font.getStringWidth(subjectLine2Part1) / 1000.0f) * fontSize
        //
        String subjectLine2Part2 = ""

        def reminderNumberInc = ++reminderNumber.reminderType

        if(reminderNumberInc){
            if(reminderNumberInc == "1"){
                subjectLine2Part2 = reminderMailTextArr[0][4]
            } else if (reminderNumberInc == "2"){
                subjectLine2Part2 = reminderMailTextArr[0][5]
            } else if (reminderNumberInc == "3"){
                subjectLine2Part2 = reminderMailTextArr[0][6]
            } else{
                subjectLine2Part2 = reminderMailTextArr[0][7]
            }
        }

        font = PDType1Font.HELVETICA_BOLD
        contentStream.setFont(font, fontSize)

        nNextColX = nNextColX + text_width
        drawAnyString(subjectLine2Part2,nNextColX,nDescStartY)
        text_width = (font.getStringWidth(subjectLine2Part2) / 1000.0f) * fontSize

        //begin to draw our line
        int nX2 = nNextColX + text_width
        contentStream.drawLine(nNextColX, (nDescStartY - 0.99), nX2, (nDescStartY - 0.99));

        mailBodyStartY = nDescStartY - 50
    }

    def drawAnyString(def dataString,def cordX,def cordY){
        String colValue = String.format("%s",dataString);

        contentStream.beginText();
        contentStream.moveTextPositionByAmount(cordX, cordY)
        contentStream.drawString("${colValue}")
        contentStream.endText();
    }

    def drawAnyStringDynamic(List<Pair<String, Boolean >>wordList, def cordX, def cordY){

        float fontSize = 10.50f
        contentStream.beginText()
        contentStream.moveTextPositionByAmount(cordX, cordY)
        for(Pair<String, Boolean> word : wordList) {
            if(word.bValue) {

                PDFont font = PDType1Font.HELVETICA_BOLD
                contentStream.setFont(font, fontSize)
                String text = String.format("%s",word.aValue);
                contentStream.drawString("${text}")
            } else {
                String nonBoldVal = String.format("%s",word.aValue);
                PDFont font = PDType1Font.HELVETICA
                contentStream.setFont(font, fontSize)
                contentStream.drawString("${nonBoldVal}");
            }
        }
        contentStream.endText()
    }

    def getFullMonthInDutch(def currentMonth){

        def monthName = ""
        if(currentMonth==1){
            monthName = 'januari'
        }else if(currentMonth==2){
            monthName = 'februari'
        }else if(currentMonth==3){
            monthName = 'maart'
        }else if(currentMonth==4){
            monthName = 'april'
        }else if(currentMonth==5){
            monthName = 'mei'
        }else if(currentMonth==6){
            monthName = 'juni'
        }else if(currentMonth==7){
            monthName = 'juli'
        }else if(currentMonth==8){
            monthName = 'augustus'
        }else if(currentMonth==9){
            monthName = 'september'
        }else if(currentMonth==10){
            monthName = 'oktober'
        }else if(currentMonth==11){
            monthName = 'november'
        }else if(currentMonth==12){
            monthName = 'december'
        }
        return monthName
    }

    def setupPage(PDPage page, PDDocument pd) {
        this.page = page;
        this.pd = pd
        page.setMediaBox(PDPage.PAGE_SIZE_A4);

        //Init page widht and height
        PDRectangle pagesize = page.findMediaBox()
        this.pageWidth = pagesize.getWidth()
        this.pageHeight = pagesize.getHeight();

        //Init content stream.
        this.contentStream = new PDPageContentStream(pd, page, true, true, true)
        contentStream.concatenate2CTM(new AffineTransform(1, 0, 0, 1, 0, 0))

    }

    def adjustBoundBox(headerHeight, footerHeight, leftMargin, rightMargin) {
        this.headerHeight = headerHeight;
        this.footerHeight = footerHeight;

        this.leftMargin = leftMargin;
        this.rightMargin = rightMargin;
        //Left
        lbX = 0.0 + leftMargin;
        lbY = footerHeight;
        //Right
        rtX = pageWidth - rightMargin;
        rtY = pageHeight - headerHeight;
    }

    def writeInvoicePdfData(def welcome) {
        contentStream.beginText()
        PDFont font = PDType1Font.HELVETICA
        page.getResources().getFonts()
        float fontSize = 10.0f
        contentStream.setFont(font, fontSize)
        float textHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;

        float lineNoIndex = 0.0;
        float linegap

        linegap = textHeight+4.00
        float onelineGap = 4.0f
        rtY =rtY-onelineGap - 15.00
        //User Name
        contentStream.moveTextPositionByAmount(lbX, rtY)
        contentStream.drawString("${welcome}")
        contentStream.endText()
        contentStream.close()
    }

    def writeUserData(def userInfo, def invSummary, def invDescription, def invoiceType,def totalVatByCategory, def budgetCustomerInfo) {

//        contentStream.drawLine(lbX,rtY,rtX,rtY);

        writeUserInfo(userInfo, invSummary)

        writeInvoiceSummary(invSummary, invDescription, invoiceType)

        writeInvoiceDescription(invDescription)
//
        writeInvoiceAmountSummary(invSummary, invDescription, invoiceType,totalVatByCategory)
        writeVatAndCommentsInfo(budgetCustomerInfo,totalVatByCategory,invSummary)
//        contentStream.drawLine(lbX,lbY,rtX,lbY);

        contentStream.close()
    }

    def writeUserInfo(def userInfo, def invSummary) {

        contentStream.beginText();

        PDFont font = PDType1Font.HELVETICA
        page.getResources().getFonts()
        float fontSize = 9.50f
        contentStream.setFont(font, fontSize)
        float textHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;

        float lineNoIndex = 0.0;
        float linegap
        def addressLine1
        def addressLine2
        def city
        def postalCode
        def contactPersonName

        def customerName = invSummary[0][2]
        if (userInfo.size() > 0) {
            addressLine1 = userInfo[0][0] ? userInfo[0][0] : ""
            addressLine2 = userInfo[0][1] ? userInfo[0][1] : ""
            city = userInfo[0][2] ? userInfo[0][2] : ""
            postalCode = userInfo[0][3] ? userInfo[0][3] : ""
            contactPersonName = userInfo[0][4] ? userInfo[0][4] : ""
        } else {
            addressLine1 = ""
            addressLine2 = ""
            city = ""
            postalCode = ""
            contactPersonName = ""
        }

        linegap = textHeight+4.00
        float onelineGap = 4.0f
        rtY =rtY-onelineGap - 15.00
        //User Name
        contentStream.moveTextPositionByAmount(lbX, rtY)
        contentStream.drawString("${customerName}")

        contentStream.moveTextPositionByAmount(0, -linegap)
        contentStream.drawString("${contactPersonName}")
        //Address
        contentStream.moveTextPositionByAmount(0, -linegap)
        contentStream.drawString("${addressLine1}")
        contentStream.drawString("  ")
        contentStream.drawString("${addressLine2}")
        //Country
        contentStream.moveTextPositionByAmount(0, -linegap)
        contentStream.drawString("${postalCode}")
        contentStream.drawString("  ")
        contentStream.drawString("${city}")

        contentStream.endText();
    }

    def writeInvoiceSummary(def invSummary, def invDescription, def invoiceType) {


        float fontSize
        PDFont font
        def transDate = ""
        def showingDate = ""
        Date budgetItemDate
        Date budgetItemTransDate
        if (invSummary.size() > 0) {
            if (invoiceType == "invoiceExpense" || invoiceType == "receipt") {
                budgetItemDate = invSummary[0][6]
                budgetItemTransDate = invSummary[0][8]
            } else {
                budgetItemDate = invSummary[0][7]
                budgetItemTransDate = invSummary[0][9]
            }

            showingDate = budgetItemDate.format("dd-MM-yyyy")
            transDate = budgetItemTransDate.format("dd-MM-yyyy")
        }

        contentStream.beginText()
        font = PDType1Font.HELVETICA
        fontSize = 18.0f
        contentStream.setFont(font, fontSize)
        int nInvoiceLabelY = rtY - (gap + 33)

        contentStream.moveTextPositionByAmount(lbX, nInvoiceLabelY)
        contentStream.drawString(g.message(code: 'bv.pdf.invoiceLabel'))
        contentStream.endText()
        int nSumStartY = rtY - 130.0;

//        contentStream.drawLine(lbX,nSumStartY,rtX,nSumStartY);

        contentStream.beginText();

        font = PDType1Font.HELVETICA
        fontSize = 11.0f
        contentStream.setFont(font, fontSize)
        float textHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;

        nSumStartY = rtY - (gap + 77)
        contentStream.moveTextPositionByAmount(lbX, nSumStartY)
        contentStream.drawString(g.message(code: 'bv.pdf.invoiceDateLabel'))
        int nMoveColumnX = 133
        contentStream.moveTextPositionByAmount(nMoveColumnX, 0)
        contentStream.drawString(g.message(code: 'bv.pdf.dueDateLabel'))

//        nMoveColumnX  = nMoveColumnX
        contentStream.moveTextPositionByAmount(nMoveColumnX, 0)
        contentStream.drawString(g.message(code: 'bv.pdf.invoiceNumberLabel'))

        nMoveColumnX = nMoveColumnX + 20
        contentStream.moveTextPositionByAmount(nMoveColumnX, 0)
        contentStream.drawString(g.message(code: 'bv.pdf.referenceLabel'))
        contentStream.endText();

        nSumStartY = nSumStartY - 10
//        contentStream.drawLine(lbX,nSumStartY,rtX,nSumStartY);

        contentStream.beginText();
        font = PDType1Font.HELVETICA
        fontSize = 10.0f
        contentStream.setFont(font, fontSize)
        textHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize
        nSumStartY = nSumStartY - textHeight
        contentStream.moveTextPositionByAmount(lbX, nSumStartY)
        contentStream.drawString("${transDate}")
        int vMoveColumnX = 133
        contentStream.moveTextPositionByAmount(vMoveColumnX, 0)
        contentStream.drawString("${showingDate}")
        contentStream.moveTextPositionByAmount(vMoveColumnX, 0)
        contentStream.drawString("${invDescription[0][10]}")
        vMoveColumnX = vMoveColumnX + 20
        if (invoiceType == "invoiceExpense" || invoiceType == "receipt") {
            contentStream.moveTextPositionByAmount(vMoveColumnX, 0)
            contentStream.drawString("${invSummary[0][7]}")
        } else {
            contentStream.moveTextPositionByAmount(vMoveColumnX, 0)
            contentStream.drawString("${invSummary[0][8]}")
        }

        contentStream.endText();

    }



    def writeInvoiceDescription(def invDescription) {

        PDFont font
        float fontSize

        int nDescStartY = rtY - (gap + 143.0);

        //Header Text
        contentStream.beginText();
        font = PDType1Font.HELVETICA
        fontSize = 11.0f
        contentStream.setFont(font, fontSize)
        float textHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;

        contentStream.moveTextPositionByAmount(lbX, nDescStartY)
        contentStream.drawString(g.message(code: 'bv.pdf.descriptionLabel'))

        int nSecondColumnX = 435
        contentStream.moveTextPositionByAmount(nSecondColumnX, 0)
        contentStream.drawString(g.message(code: 'bv.pdf.amountLabel'))
        contentStream.endText();

        //Line under descriptoin header
        rtX = rtX + 38.00
        contentStream.drawLine(lbX, nDescStartY - 4, rtX, nDescStartY - 4);

        font = PDType1Font.HELVETICA
        fontSize = 10.0f
        textHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
        nDescStartY = nDescStartY - (textHeight + 10)
        int vMoveColumnX = 350
        def description

        //First Column
        List<Integer> linesCount = new ArrayList<Integer>()
        contentStream.beginText();
        contentStream.setFont(font, fontSize)
        for (int y = 0; y < invDescription.size(); y++) {

//            description = invDescription[y][2]

            def itemQuinty = new BigDecimal(invDescription[y][4])
            itemQuinty = itemQuinty.setScale(2,BigDecimal.ROUND_HALF_UP)

            def unitPrice = new BigDecimal(invDescription[y][7])
            unitPrice = unitPrice.setScale(2,BigDecimal.ROUND_HALF_UP)

            if(itemQuinty > 1){
                description = invDescription[y][2] +" ( " + itemQuinty +" stuks ad EUR " + unitPrice+" ) "
            }else{
                description = invDescription[y][2]
            }

            int text_width = (font.getStringWidth(description) / 1000.0f) * fontSize

            //
            List<String> lines;
            lines = getWrapTextList(description,nSecondColumnX-5,font);

            //For the first line.
            if (y == 0) {
                contentStream.moveTextPositionByAmount(lbX, nDescStartY);
            }
            //Draw wrapped description text.
            for (String line: lines){
                contentStream.drawString(line)
                contentStream.moveTextPositionByAmount(0, -15)
            }

            if(lines.size()== 0){
                linesCount.add(1)
                contentStream.moveTextPositionByAmount(0, -15)
            }else{
                linesCount.add(lines.size())
            }

//

        }
        contentStream.endText();

        //Second Column
        contentStream.beginText();
        contentStream.setFont(font, fontSize)

        int nSecondColValueX = lbX + 474
        int descLineCount = linesCount.getAt(0);


        int lastRowVal = 0;
        for (int y = 0; y < invDescription.size(); y++) {
//            BigDecimal showPrice = new BigDecimal(invDescription[y][7])
            BigDecimal showPrice = new BigDecimal(invDescription[y][5])
            showPrice = showPrice.setScale(2, BigDecimal.ROUND_HALF_UP)
            String price = showPrice.toString()

            int priceWidth = (font.getStringWidth(price) / 1000.0f) * fontSize
            descLineCount = linesCount.getAt(y)

            //For the first line.
            if (y == 0) {
                contentStream.moveTextPositionByAmount(nSecondColValueX, nDescStartY)
                contentStream.moveTextPositionByAmount(-priceWidth,0)
                contentStream.drawString("${showPrice}")
                contentStream.moveTextPositionByAmount(priceWidth,0)
                contentStream.moveTextPositionByAmount(0, -(15 * descLineCount))
            } else {
                contentStream.moveTextPositionByAmount(-priceWidth,0)
                contentStream.drawString("${showPrice.setScale(2, BigDecimal.ROUND_HALF_UP)}")
                contentStream.moveTextPositionByAmount(priceWidth,0)
                contentStream.moveTextPositionByAmount(0, -(15 * descLineCount))
            }
        }

        contentStream.endText();
        for (int y = 0; y < invDescription.size(); y++) {
            descLineCount = linesCount.getAt(y)
            globalLineCount = linesCount.getAt(y)+globalLineCount
            nDescStartY = nDescStartY - (15 * descLineCount)
        }
         contentStream.drawLine(lbX, nDescStartY, rtX, nDescStartY);
    }

    def writeInvoiceAmountSummary(def invSummary, def invDescription, def invoiceType,def totalVatByCategory) {

        PDFont font
        float fontSize
        float vat
        def vatDetails
        float totalAmount = 0.00
        float textHeight
        int nInvAmountSumY
        int nMoveInvAmntColumnX

        font = PDType1Font.HELVETICA
        fontSize = 10.0f
        textHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
//        nInvAmountSumY = rtY - (gap + 240.0 + (15 * invDescription.size()))
        println(""+globalLineCount)
        nInvAmountSumY = rtY - (gap + 183.0 + (15 * globalLineCount))

        //Column 1 (Label and Vat)
        contentStream.beginText();

        contentStream.setFont(font, fontSize)
        nMoveInvAmntColumnX = lbX + 220

        contentStream.moveTextPositionByAmount(nMoveInvAmntColumnX, nInvAmountSumY)
        contentStream.drawString(g.message(code: 'bv.pdf.totalAmountExVATLabel'))
        for (int y = 0; y < totalVatByCategory.size(); y++) {

            contentStream.moveTextPositionByAmount(0, -15)
            contentStream.drawString(g.message(code: 'bv.pdf.totalVATLabel') + "(" + "${totalVatByCategory[y][0]}" + "%)")

        }

        contentStream.endText();

        //Column 2(Total amount and Vat Amount)
        contentStream.beginText();

        contentStream.setFont(font, fontSize)

        Font fnt = new Font("Helvetica", Font.PLAIN, 11)
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
        FontMetrics fm = img.getGraphics().getFontMetrics(fnt)

        nMoveInvAmntColumnX = lbX + 474
        for (int i = 0; i < invDescription.size(); i++) {
            totalAmount = totalAmount + invDescription[i][5]
        }

        BigDecimal showTotalAmount = new BigDecimal(totalAmount)
        showTotalAmount = showTotalAmount.setScale(2, BigDecimal.ROUND_HALF_UP)
        String totalAmountExVat = showTotalAmount.toString()
        int totalAmountWidth = fm.stringWidth(totalAmountExVat)
        int text_width = (font.getStringWidth(totalAmountExVat) / 1000.0f) * fontSize

        contentStream.moveTextPositionByAmount(nMoveInvAmntColumnX, nInvAmountSumY)
        contentStream.moveTextPositionByAmount(-text_width, 0)
        contentStream.drawString("${showTotalAmount}")
        contentStream.moveTextPositionByAmount(text_width, 0)

//       show toatl vat amount by vat category
        for (int y = 0; y < totalVatByCategory.size(); y++) {
            vat = totalVatByCategory[y][1]
            BigDecimal showVat = new BigDecimal(Float.toString(vat))
            showVat = showVat.setScale(2, BigDecimal.ROUND_HALF_UP)
            String vatAmount = showVat.toString()
            text_width = (font.getStringWidth(vatAmount) / 1000.0f) * fontSize
            contentStream.moveTextPositionByAmount(0, -15)
            contentStream.moveTextPositionByAmount(-text_width, 0)
            contentStream.drawString("${showVat}")
            contentStream.moveTextPositionByAmount(text_width, 0)
        }
        contentStream.endText();

        //Total Amoun with vat
        nMoveInvAmntColumnX = lbX + 220
        nInvAmountSumY = nInvAmountSumY - (10 + (15 * totalVatByCategory.size()))
        contentStream.drawLine(nMoveInvAmntColumnX, nInvAmountSumY, rtX, nInvAmountSumY);
        double totalAmountWithVax
        if (invoiceType == "invoiceExpense" || invoiceType == "receipt") {
            totalAmountWithVax = showTotalAmount + invSummary[0][4]
        } else {
            totalAmountWithVax = showTotalAmount + invSummary[0][5]
        }
        BigDecimal showTotalAmtWithTax = new BigDecimal(totalAmountWithVax)
        showTotalAmtWithTax = showTotalAmtWithTax.setScale(2, BigDecimal.ROUND_HALF_UP)
        String totalAmtWithTax = showTotalAmtWithTax.toString()
        text_width = (font.getStringWidth(totalAmtWithTax) / 1000.0f) * fontSize

        contentStream.beginText();
        nInvAmountSumY = nInvAmountSumY - (textHeight + 5)
        contentStream.moveTextPositionByAmount(nMoveInvAmntColumnX, nInvAmountSumY)
        contentStream.drawString(g.message(code: 'bv.pdf.totalAmountIncVATLabel'))
        contentStream.endText();

        contentStream.beginText();
        nMoveInvAmntColumnX = lbX + 474
        contentStream.moveTextPositionByAmount(nMoveInvAmntColumnX, nInvAmountSumY)
        contentStream.moveTextPositionByAmount(-text_width,0)
        contentStream.drawString("${showTotalAmtWithTax}")
        contentStream.moveTextPositionByAmount(text_width,0)
        contentStream.endText();
    }

    def writeVatAndCommentsInfo(def budgetCustomerInfo,def totalVatByCategory,def invSummary) {

        contentStream.beginText();

        PDFont font = PDType1Font.HELVETICA
        page.getResources().getFonts()
        float fontSize = 10.0f
        contentStream.setFont(font, fontSize)
        float textHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;

        float lineNoIndex = 0.0;
        float linegap
        int nInvAmountSumY
        linegap = textHeight+4.00
        nInvAmountSumY = rtY - (gap + 183.0 + (15 * globalLineCount))
        nInvAmountSumY = nInvAmountSumY - (10 + (15 * totalVatByCategory.size()))
        nInvAmountSumY = nInvAmountSumY - (textHeight + 60)
        def vatNumber

        if (budgetCustomerInfo.size() > 0) {
            vatNumber = budgetCustomerInfo[0][0]
            vatNumber = invSummary[0][15]
        }
        def vatIdForComment = 0
        if(totalVatByCategory.size()){
            for(int i = 0;i<totalVatByCategory.size(); i++){
                def vatId = totalVatByCategory[i][2]
                def vatInfo = new BudgetViewDatabaseService().executeQuery("""select category_name from vat_category where id ='${vatId}'""")
                def vatCategoryName = vatInfo[0][0]
                if(vatCategoryName == 'BTW verlegd'){
                    vatIdForComment = 1
                }
            }
        }

        def companyArr =new BudgetViewDatabaseService().executeQuery("""SELECT zorgSector FROM company_setup ORDER BY id DESC LIMIT 1""")
        def zorgSector = 0
        if(companyArr.size()){
            zorgSector = companyArr[0][0]
        }

        if(vatIdForComment == 1  && zorgSector ==0){
            contentStream.moveTextPositionByAmount(lbX, nInvAmountSumY)
            contentStream.drawString(g.message(code: 'bv.incomeInvoice.afterSave.BTWverlegd-BTWnummerAfnemer.label')+ " ${vatNumber}")

            contentStream.moveTextPositionByAmount(0, -linegap)
            if(invSummary[0][14]){
                contentStream.drawString("${invSummary[0][14]}")
            }
        }else if( zorgSector ==1){
            contentStream.moveTextPositionByAmount(lbX, nInvAmountSumY)
            contentStream.drawString(g.message(code: 'bv.incomeInvoice.afterSave.Burger.Service.Nummer.label')+ " ${vatNumber}")

            contentStream.moveTextPositionByAmount(0, -linegap)
            if(invSummary[0][14]){
                contentStream.drawString("${invSummary[0][14]}")
            }
        }else{
            contentStream.moveTextPositionByAmount(lbX, nInvAmountSumY)
            if(invSummary[0][14]){
                contentStream.drawString("${invSummary[0][14]}")
            }

        }


        contentStream.endText();
    }

    def List<String> getWrapTextList(String description,float lineSize, PDFont font){
        String text = description
        float fontSize = 12.0
        List<String> lines = new ArrayList<String>()
        int lastSpace = -1
        while (text.length() > 0){
            int spaceIndex = text.indexOf(' ', lastSpace + 1);
            if (spaceIndex < 0)
                spaceIndex = text.length()
            String subString = text.substring(0, spaceIndex)
            float size = fontSize * font.getStringWidth(subString) / 1000;
            if (size > lineSize){
                if (lastSpace < 0)
                    lastSpace = spaceIndex
                subString = text.substring(0, lastSpace)
                lines.add(subString)
                text = text.substring(lastSpace).trim()
                System.out.printf("'%s' is line\n", subString)
                lastSpace = -1
            }else if (spaceIndex == text.length()){
                lines.add(text)
                System.out.printf("'%s' is line\n", text)
                text = ""
            }else {
                lastSpace = spaceIndex
            }
        }

        return lines;
    }


}
