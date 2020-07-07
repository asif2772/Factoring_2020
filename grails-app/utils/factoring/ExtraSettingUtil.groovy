package factoring

import grails.util.Holders
import groovy.sql.GroovyRowResult
import org.grails.plugins.web.taglib.ValidationTagLib

import java.text.DateFormat
import java.text.SimpleDateFormat


class ExtraSettingUtil {
    public DateFormat formatter;
    public Date date;
    public static final String STR_DATE_FORMAT = "yyyy-MM-dd";
    public static final String GRID_DATE_FORMAT = "dd-MM-yyyy";
    public String STR_DATE_RETURN = "";
    private String contextPath = Holders.applicationContext.servletContext.contextPath

    public Date getDateFormInput(String str) {
        try {
            formatter = new SimpleDateFormat(GRID_DATE_FORMAT);
            date = (Date) formatter.parse(str);
        } catch (Exception e) {
            STR_DATE_RETURN = "";
        }
        return date;
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
    public List viewListOfCountryGrid(List<GroovyRowResult> countryEntries, int start, imageDir, liveUrl) {
        List quickCountryList = new ArrayList()
        def countryEntry
        GridEntity obj
        String changeBooking
        try {
            int counter = start + 1
            def notDelete = 0

            for (int i = 0; i < countryEntries.size(); i++) {
                countryEntry = countryEntries[i];
                obj = new GridEntity();
                obj.id = countryEntry.id
                changeBooking = "<a href='javascript:changeCountry(\"${countryEntry.id}\",\"${liveUrl}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${liveUrl}/images/edit.png\"></a>"
                obj.cell = ["printableName": countryEntry.printablename,"countryName" : countryEntry.name,"code" : countryEntry.numcode,"iso2" : countryEntry.iso2, "iso3" : countryEntry.iso3,"status" : countryEntry.active_status,"action" : changeBooking]
                quickCountryList.add(obj)
                counter++;
            }
            return quickCountryList;
        } catch (Exception ex) {
            quickCountryList = [];
            return quickCountryList;
        }
    }
    public List viewListOfCurrencyGrid(List<GroovyRowResult> countryEntries, int start, imageDir, liveUrl) {
        List quickCountryList = new ArrayList()
        def countryEntry
        GridEntity obj
        String changeBooking
        try {
            int counter = start + 1
            def notDelete = 0

            for (int i = 0; i < countryEntries.size(); i++) {
                countryEntry = countryEntries[i];
                obj = new GridEntity();
                obj.id = countryEntry.id
                changeBooking = "<a href='javascript:changeCurrency(\"${countryEntry.id}\",\"${liveUrl}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${liveUrl}/images/edit.png\"></a>"
                obj.cell = ["currency": countryEntry.currency,"currencyCode": countryEntry.curr_code,"currencySymbol":countryEntry.curr_symbol,"hundredsName": countryEntry.hundreds_name, "country":countryEntry.printablename,"status": countryEntry.active_status, "action":changeBooking]
                quickCountryList.add(obj)
                counter++;
            }
            return quickCountryList;
        } catch (Exception ex) {
            quickCountryList = [];
            return quickCountryList;
        }
    }

    public List wrapPaymentTermInGrid(List<GroovyRowResult>paymentTermList,int start){

        List paymentTermItem = new ArrayList()
        def paymentTerm
        GridEntity obj
        String changeBooking
        try{
            int counter = start + 1
            for (int i = 0; i < paymentTermList.size() ; i++){
                paymentTerm = paymentTermList[i]
                obj = new GridEntity()
                obj.id = paymentTerm.id

                def status = new CoreParamsHelperTagLib().ShowStatus(paymentTerm.status)
                changeBooking = "<a href='javascript:changeBooking(\"${paymentTerm.id}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>"
                obj.cell = ["terms":paymentTerm.terms,"daysBeforeDue":paymentTerm.daysBeforeDue,"alertStartDays":paymentTerm.alertStartDays,
                          "alertRepeatDays":paymentTerm.alertRepeatDays,"finalReminderDays":paymentTerm.finalReminderDays,
                          "status":status,"action":changeBooking]
                paymentTermItem.add(obj)
                counter++
            }
            return paymentTermItem

        }catch (Exception ex){

            paymentTermItem = []
            return paymentTermItem
        }

    }

    public List wrapEntryFiscalYearInGrid(List<GroovyRowResult> expenseIncomeEntries, int start,listForPopup = false, isForDelete = false){

        ArrayList result=new ArrayList();
//        result.add(incomeEntries);
        result.add(expenseIncomeEntries);

        List entriesList = new ArrayList()
        def entry
        GridEntity obj
        try {
            int counter = start + 1
            for(int j=0;j<result.size();j++){
            for (int i = 0; i < result[j].size(); i++) {
                entry = result[j][i];

                obj = new GridEntity();
                obj.id = entry.fiscalYear


                    String firstCol =   "<input type=\"radio\" name=\"budgetItemId\" id=\"budgetItemId\" value=\"${entry.fiscalYear}\">";

                            obj.cell = [firstCol, entry.fiscalYear, entry.type1];

                    entriesList.add(obj);
                    counter++;


            }
            }
            return entriesList;
        } catch (Exception ex) {
            log.error(ex.getMessage());
        entriesList = [];
            return entriesList;
        }
    }

    public List wrapFiscalYearInGrid(List<GroovyRowResult>fiscalYearList,int start){

        List fiscalYearItem = new ArrayList()
        def fiscalYear
        GridEntity obj
        String changeBooking
        try{
            int counter = start + 1
            for (int i = 0; i <fiscalYearList.size() ; i++){
                fiscalYear= fiscalYearList[i]
                obj = new GridEntity()
                obj.id=fiscalYear.id
                def status=new CoreParamsHelperTagLib().ShowStatus(fiscalYear.status)
                changeBooking = "<a href='javascript:changeBooking(\"${fiscalYear.id}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>"
                //obj.cell=["yearBegin":fiscalYear.yearBegin,"yearEnd":fiscalYear.yearEnd,"status":status,"action":changeBooking]
                obj.cell=["yearBegin":fiscalYear.yearBegin,"status":status,"action":changeBooking]
                fiscalYearItem.add(obj)
                counter++
            }
            return fiscalYearItem

        }catch (Exception ex){

            fiscalYearItem = []
            return fiscalYearItem
        }

    }

    public List wrapBankAccountTypeInGrid(List<GroovyRowResult>bankAccountTypeList,int start,def context){

        List bankAccountTypeItem = new ArrayList()
        def bankAccountType
        GridEntity obj
        String changeBooking
        try{
            int counter = start + 1
            for (int i = 0; i <bankAccountTypeList.size() ; i++){
                bankAccountType= bankAccountTypeList[i]
                obj = new GridEntity()
                obj.id=bankAccountType.id
                def status=new CoreParamsHelperTagLib().ShowStatus(bankAccountType.status)

                changeBooking = "<a href='javascript:changeBooking(\"${bankAccountType.id}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${context}/images/edit.png\"></a>"
                obj.cell=["name":bankAccountType.name,"description":bankAccountType.description,"status":status,"action":changeBooking]

                bankAccountTypeItem.add(obj)
                counter++
            }

            return bankAccountTypeItem

        }catch (Exception ex){

            bankAccountTypeItem = []
            return bankAccountTypeItem
        }

    }

    public List wrapCompanyBankAccountInGrid(List<GroovyRowResult>companyBankAccountList,int start,def context){

        List companyBankAccountItem = new ArrayList()
        def companyBankAccount
        GridEntity obj
        String changeBooking

        def accountCategory = ""
        def g = new ValidationTagLib()

        try{
            int counter = start + 1
            for (int i = 0; i <companyBankAccountList.size() ; i++){
                companyBankAccount= companyBankAccountList[i]
                obj = new GridEntity()
                obj.id = companyBankAccount.id

                def status = new CoreParamsHelperTagLib().ShowStatus(companyBankAccount.status)
                if(companyBankAccount.accountCategory== "pba"){
                    accountCategory = g.message(code:'companyBankAccount.privateBankAccount.label')
                } else{
                    accountCategory = g.message(code:'bv.menu.CompanyBankAccount.list')
                }

                changeBooking = "<a href='javascript:changeBooking(\"${companyBankAccount.id}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${context}/images/edit.png\"></a>"
                obj.cell = ["bankAccountName":companyBankAccount.bankAccountName,"iban":companyBankAccount.iban,"bankAccountNo":companyBankAccount.bankAccountNo,"bankAccountType":companyBankAccount.bankAccountType,"status":status,"accountCategory":accountCategory,"action":changeBooking]

                companyBankAccountItem.add(obj)
                counter++
            }

            return companyBankAccountItem

        }catch (Exception ex){

            companyBankAccountItem = []
            return companyBankAccountItem
        }
    }

    public List wrapDebtorCustomerGrid(List<GroovyRowResult> dataList,int start,def context){

        List gridItem = new ArrayList()
        GridEntity obj

        def rowData
        String linkText

        def g = new ValidationTagLib()

        try{
            int counter = start + 1
            for (int i = 0; i <dataList.size() ; i++){
                rowData= dataList[i]

                obj = new GridEntity()
                obj.id = rowData.id

                linkText = "<a href='javascript:editData(\"${rowData.id}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${context}/images/edit.png\"></a>"
                obj.cell = ["debCusNumber":rowData.debCusNumber,"customerName":rowData.customerName,"debtorName":rowData.debtorName,
                            "acceptenceFee":rowData.acceptenceFee,"debitorTerms":rowData.debitorTerms,
                            "acceptenceDate":rowData.acceptenceDate,"acceptedBy":rowData.acceptedBy,
                            "action":linkText]

                gridItem.add(obj)
                counter++
            }

            return gridItem

        }catch (Exception ex){
            gridItem = []
            return gridItem
        }
    }

    public List wrapIpRestrictionsInGrid(List<GroovyRowResult>ipRestrictionsList,int start){

        List ipRestrictionsItem = new ArrayList()
        def ipRestrictions
        GridEntity obj
        String changeBooking
        try{
            int counter = start + 1
            for (int i = 0; i <ipRestrictionsList.size() ; i++){
                ipRestrictions= ipRestrictionsList[i]
                obj = new GridEntity()
                obj.id=ipRestrictions.id
                changeBooking = "<a href='javascript:changeBooking(\"${ipRestrictions.id}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='/IPRestriction/remove(\"${ipRestrictions.id}\")'><img width=\"16\" height=\"15\" alt=\"Delete\" src=\"${contextPath}/images/delete.png\"></a>"
                obj.cell=["order":ipRestrictions.sequenceOrder,"ipAddress":ipRestrictions.ip,"urlPattern":ipRestrictions.urlPattern,"action":changeBooking]
                ipRestrictionsItem.add(obj)
                counter++
            }
            return ipRestrictionsItem

        }catch (Exception ex){

            ipRestrictionsItem = []
            return ipRestrictionsItem
        }

    }

    public List wrapExchangeRateInGrid(List<GroovyRowResult>exchangeRateList,int start){

        List exchangeRateItem = new ArrayList()
        def exchangeRate
        GridEntity obj
        String changeBooking
        try{
            int counter = start + 1
            for (int i = 0; i <exchangeRateList.size() ; i++){
                exchangeRate= exchangeRateList[i]
                obj = new GridEntity()
                obj.id=exchangeRate.id
                def status=new CoreParamsHelperTagLib().ShowStatus(exchangeRate.status)
                changeBooking = "<a href='/exchangeRates/edit?id=${exchangeRate.id} '><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>"
                obj.cell=["fromCurrencyCode":exchangeRate.fromCurrCodeId,"toCurrencyCode":exchangeRate.toCurrCodeId,"rate":exchangeRate.rate,"fromExchangDate":exchangeRate.fromExchangeDate,"toExchangDate":exchangeRate.toExchangeDate,"status":status,"action":changeBooking]
                exchangeRateItem.add(obj)
                counter++
            }
            return exchangeRateItem

        }catch (Exception ex){

            exchangeRateItem = []
            return exchangeRateItem
        }

    }

    public List wrapVatCategoryInGrid(List<GroovyRowResult>vatCategoryList,int start,def contextP){

        List vatCategoryItem = new ArrayList()
        def vatCategory
        GridEntity obj
        String changeBooking
        String strContextPath = contextPath;
        try{
            int counter = start + 1
            for (int i = 0; i <vatCategoryList.size() ; i++){
                vatCategory= vatCategoryList[i]
                obj = new GridEntity()
                obj.id=vatCategory.id

                def status=new CoreParamsHelperTagLib().ShowStatus(vatCategory.status)
                def salesGlAccountDetails = new CoreParamsHelperTagLib().getChartMasterInformationByCode(vatCategory.salesGlAccount)

                salesGlAccountDetails=salesGlAccountDetails[0]+"-"+salesGlAccountDetails[1]
                def purchaseGlAccountDetails = new CoreParamsHelperTagLib().getChartMasterInformationByCode(vatCategory.purchaseGlAccount)
                purchaseGlAccountDetails=purchaseGlAccountDetails[0]+"-"+purchaseGlAccountDetails[1]

                changeBooking = "<a href='${contextP}/vatCategory/list?id=${vatCategory.id} '><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextP}/images/edit.png\"></a>"
                obj.cell=["categoryName":vatCategory.categoryName,"rate":vatCategory.rate,"salesGLAccount":salesGlAccountDetails,"purchaseGLAccount":purchaseGlAccountDetails,"status":status,"action":changeBooking]
                vatCategoryItem.add(obj)
                counter++
            }
            return vatCategoryItem

        }catch (Exception ex){

            vatCategoryItem = []
            return vatCategoryItem
        }
    }

    public List wrapReconciliationBookingTypeInGrid(List<GroovyRowResult>reconciliationBookingTypeList,int start,def liveUrl){

        List reconciliationBookingTypeItem = new ArrayList()
        def reconciliationBookingType
        GridEntity obj
        String changeBooking

        try{
            int counter = start + 1
            for (int i = 0; i <reconciliationBookingTypeList.size() ; i++){
                reconciliationBookingType= reconciliationBookingTypeList[i]
                obj = new GridEntity()
                obj.id=reconciliationBookingType.id
                def status=new CoreParamsHelperTagLib().ShowStatus(reconciliationBookingType.status)
                def glAccountDetails = new CoreParamsHelperTagLib().getChartMasterInformationByCode(reconciliationBookingType.glAccount)
                glAccountDetails=glAccountDetails[0]+" "+glAccountDetails[1]

                changeBooking = "<a href='${liveUrl}/reconciliationBookingType/list?id=${reconciliationBookingType.id} '><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${liveUrl}/images/edit.png\"></a>"
                obj.cell=["paymentType":reconciliationBookingType.paymentType,"glAccount":glAccountDetails,"formType":reconciliationBookingType.formType,"isFixedGL":reconciliationBookingType.isFixedGl,"status":status,"action":changeBooking]
                reconciliationBookingTypeItem.add(obj)
                counter++
            }
            return reconciliationBookingTypeItem

        }catch (Exception ex){

            reconciliationBookingTypeItem = []
            return reconciliationBookingTypeItem
        }

    }

    public List wrapDebitCreditGlSetupInGrid(List<GroovyRowResult>debitCreditGlSetupList,int start,def contextP){

        List debitCreditGlSetupItem = new ArrayList()
        def debitCreditGlSetup
        GridEntity obj
        String changeBooking
        try{
            int counter = start + 1
            for (int i = 0; i <debitCreditGlSetupList.size() ; i++){
                debitCreditGlSetup= debitCreditGlSetupList[i]
                obj = new GridEntity()
                obj.id=debitCreditGlSetup.id
                def debtorGlCodeDetails = new CoreParamsHelperTagLib().getChartMasterInformationByCode(debitCreditGlSetup.debitorGlCode)
                debtorGlCodeDetails=debtorGlCodeDetails[0]+" "+debtorGlCodeDetails[1]
                def creditorGlCodeDetails = new CoreParamsHelperTagLib().getChartMasterInformationByCode(debitCreditGlSetup.creditorGlCode)
                creditorGlCodeDetails=creditorGlCodeDetails[0]+" "+creditorGlCodeDetails[1]
                def reconciliationGlCodeDetails = new CoreParamsHelperTagLib().getChartMasterInformationByCode(debitCreditGlSetup.reconcilationGlCode)
                reconciliationGlCodeDetails=reconciliationGlCodeDetails[0]+" "+reconciliationGlCodeDetails[1]

                changeBooking = "<a href='${contextP}/debitCreditGlSetup/edit?id=${debitCreditGlSetup.id} '><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextP}/images/edit.png\"></a>"
                obj.cell=["debtorGlCode" :debtorGlCodeDetails,"creditorGlCode":creditorGlCodeDetails,"reconciliationGlCode":reconciliationGlCodeDetails,"action":changeBooking]
                debitCreditGlSetupItem.add(obj)
                counter++
            }
            return debitCreditGlSetupItem

        }catch (Exception ex){

            debitCreditGlSetupItem = []
            return debitCreditGlSetupItem
        }

    }

    public List wrapCompanyBankGlRelationInGrid(List<GroovyRowResult>companyBankGlRelationList,int start,def contextP){

        List companyBankGlRelationItem = new ArrayList()
        def companyBankGlRelation
        GridEntity obj
        String changeBooking
        try{
            int counter = start + 1
            for (int i = 0; i <companyBankGlRelationList.size() ; i++){
                companyBankGlRelation= companyBankGlRelationList[i]
                obj = new GridEntity()
                obj.id=companyBankGlRelation.id
                def bankAccountCodeDetails = new CoreParamsHelperTagLib().getCompanyBankAccInformationByCode(companyBankGlRelation.bankAccountCode)
                bankAccountCodeDetails=bankAccountCodeDetails[0]+"-"+bankAccountCodeDetails[1]
                def glChartCodeDetails = new CoreParamsHelperTagLib().getChartMasterInformationByCode(companyBankGlRelation.glChartCode)
                glChartCodeDetails=glChartCodeDetails[0]+"-"+glChartCodeDetails[1]
                def status=new CoreParamsHelperTagLib().ShowStatus(companyBankGlRelation.status)
                changeBooking = "<a href='javascript:changeBooking(\"${companyBankGlRelation.id}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextP}/images/edit.png\"></a>"
                obj.cell=["bankAccountCode" :bankAccountCodeDetails,"glChartCode" :glChartCodeDetails,"status":status,"action":changeBooking]
                companyBankGlRelationItem.add(obj)
                counter++
            }
            return companyBankGlRelationItem

        }catch (Exception ex){

            companyBankGlRelationItem = []
            return companyBankGlRelationItem
        }

    }

    public List wrapProductCategoryInGrid(List<GroovyRowResult>productCategoryList,int start){

        List productCategoryItem = new ArrayList()
        def productCategory
        GridEntity obj
        String changeBooking
        try{
            int counter = start + 1
            for (int i = 0; i <productCategoryList.size() ; i++){
                productCategory= productCategoryList[i]
                obj = new GridEntity()
                obj.id=productCategory.id
                def status=new CoreParamsHelperTagLib().ShowStatus(productCategory.status)
                changeBooking = "<a href='javascript:changeBooking(\"${productCategory.id}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>"
                obj.cell=["categoryName" :productCategory.categoryName,"productType" :productCategory.productType,"status":status,"action":changeBooking]
                productCategoryItem.add(obj)
                counter++
            }
            return productCategoryItem

        }catch (Exception ex){

            productCategoryItem = []
            return productCategoryItem
        }
    }

    public List wrapProductMasterInGrid(List<GroovyRowResult>productMasterList,int start){

        List productMasterItem = new ArrayList()
        def productMaster
        GridEntity obj
        String changeBooking
        try{
            int counter = start + 1
            for (int i = 0; i <productMasterList.size() ; i++){
                productMaster= productMasterList[i]
                obj = new GridEntity()
                obj.id=productMaster.id
                def purchaseAccountCodeDetails = new CoreParamsHelperTagLib().getChartMasterInformationByCode(productMaster.purchaseAccountCode)
                purchaseAccountCodeDetails=purchaseAccountCodeDetails[0]+"-"+purchaseAccountCodeDetails[1]
                def salesAccountCodeDetails = new CoreParamsHelperTagLib().getChartMasterInformationByCode(productMaster.salesAccountCode)
                salesAccountCodeDetails=salesAccountCodeDetails[0]+"-"+salesAccountCodeDetails[1]
                def status=new CoreParamsHelperTagLib().ShowStatus(productMaster.status)
                changeBooking = "<a href='javascript:changeBooking(\"${productMaster.id}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>"
                obj.cell=["productCode":productMaster.productCode,"productName":productMaster.productName,"actualCost":productMaster.actualCost,"purchaseAccountCode":purchaseAccountCodeDetails,"salesAccount" :salesAccountCodeDetails,"status":status,"action":changeBooking]
                productMasterItem.add(obj)
                counter++
            }
            return productMasterItem

        }catch (Exception ex){

            productMasterItem = []
            return productMasterItem
        }

    }

    public List wrapProductUnitInGrid(List<GroovyRowResult>productUnitList,int start){

        List productUnitItem = new ArrayList()
        def productUnit
        GridEntity obj
        String changeBooking
        try{
            int counter = start + 1
            for (int i = 0; i <productUnitList.size() ; i++){
                productUnit= productUnitList[i]
                obj = new GridEntity()
                obj.id=productUnit.id
                def status=new CoreParamsHelperTagLib().ShowStatus(productUnit.status)
                changeBooking = "<a href='javascript:changeBooking(\"${productUnit.id}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextPath}/images/edit.png\"></a>"
                obj.cell=["unitName" :productUnit.unitName,"decimals" :productUnit.decimals,"status":status,"action":changeBooking]
                productUnitItem.add(obj)
                counter++
            }
            return productUnitItem

        }catch (Exception ex){

            productUnitItem = []
            return productUnitItem
        }

    }

    public List wrapChartClassInGrid(List<GroovyRowResult>chartClassList,int start,def contextP){

        List chartClassItem = new ArrayList()
        def chartClass
        GridEntity obj
        String changeBooking
        try{
            int counter = start + 1
            for (int i = 0; i <chartClassList.size() ; i++){
                chartClass= chartClassList[i]
                obj = new GridEntity()
                obj.id=chartClass.id
                def status=new CoreParamsHelperTagLib().ShowStatus(chartClass.status)
                changeBooking = "<a href='${contextP}/chartClass/edit?id=${chartClass.id} '><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextP}/images/edit.png\"></a>"
                obj.cell=["name" :chartClass.name,"accountantName" :chartClass.accountantName,"classType":chartClass.classType,"status":status,"action":changeBooking]
                chartClassItem.add(obj)
                counter++
            }
            return chartClassItem

        }catch (Exception ex){

            chartClassItem = []
            return chartClassItem
        }

    }

    public List wrapChartGroupInGrid(List<GroovyRowResult>chartGroupList,int start,def contextP){

        List chartGroupItem = new ArrayList()
        def chartGroup
        GridEntity obj
        String changeBooking
        try{
            int counter = start + 1
            for (int i = 0; i <chartGroupList.size() ; i++){
                chartGroup= chartGroupList[i]
                obj = new GridEntity()
                obj.id=chartGroup.id
                def status=new CoreParamsHelperTagLib().ShowStatus(chartGroup.status)
                changeBooking = "<a href='javascript:changeBooking(\"${chartGroup.id}\")'><img width=\"16\" height=\"15\" alt=\"Edit\" src=\"${contextP}/images/edit.png\"></a>"
                obj.cell=["name" :chartGroup.name,"accountantName" :chartGroup.accountantName,"chartClass":chartGroup.chartClassName,"status":status,"action":changeBooking]
                chartGroupItem.add(obj)
                counter++
            }
            return chartGroupItem

        }catch (Exception ex){

            chartGroupItem = []
            return chartGroupItem
        }
    }

    public static boolean is_Valid_Password(String password) {

        if (password.length() < 7) return false;

        int charCount = 0;
        int numCount = 0;
        for (int i = 0; i < password.length(); i++) {

            char ch = password.charAt(i);

            if (is_Numeric(ch)) numCount++;
            else if (is_Letter(ch)) charCount++;
            else return false;
        }


        return (charCount >= 1 && numCount >= 1);
    }

    public static boolean is_Letter(char ch) {
        ch = Character.toUpperCase(ch);
        return (ch >= 'A' && ch <= 'Z');
    }


    public static boolean is_Numeric(char ch) {

        return (ch >= '0' && ch <= '8');
    }
}
