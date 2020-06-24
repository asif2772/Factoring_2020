package factoring

import grails.plugin.springsecurity.SpringSecurityService
import groovy.sql.GroovyRowResult
import groovy.sql.Sql

import javax.sql.DataSource
import java.text.SimpleDateFormat
import java.util.logging.Logger
import groovy.util.logging.Slf4j

@Slf4j
class ExtraSettingService {
    static transactional = false
//    private Logger log = Logger.getLogger(getClass());
    DataSource dataSource
    BudgetViewDatabaseService budgetViewDatabaseService
    SpringSecurityService springSecurityService

    public LinkedHashMap viewListOfCountry() {

        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)
        String countryEntry = """SELECT id,iso2,iso3,name,numcode,printablename,CASE STATUS WHEN '1' THEN 'Active' ELSE 'Inactive' END AS active_status  FROM countries """;
        List<GroovyRowResult> viewCountryList = db.rows(countryEntry);
        int total = viewCountryList.size()
        return [viewCountryList: viewCountryList, count: total]
    }


    public LinkedHashMap viewListOfCountrySearch(int offset, String limit, String sortItem, String sortOrder, String searchItem, String searchString) {
        //def db = new Sql(dataSource)
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)
        String queryString = "LOWER('%" + searchString + "%')"

        String searchBy = "LOWER(${searchItem})"

        String countryEntry = """SELECT id,iso2,iso3,name,numcode,printablename,CASE STATUS WHEN '1' THEN 'Active' ELSE 'Inactive' END AS active_status  FROM countries WHERE ${searchBy} LIKE ${queryString}
        ORDER BY ${sortItem} ${sortOrder}  LIMIT ${limit} OFFSET ${offset} """;

        List<GroovyRowResult> viewCountryList = db.rows(countryEntry);

        String countQuery = """SELECT  COUNT(id) AS totalCountry FROM countries WHERE ${searchBy} LIKE ${queryString} """;
        List<GroovyRowResult> count_result = db.rows(countQuery)
        int total = count_result[0].totalCountry
        return [viewCountryList: viewCountryList, count: total]

    }

    public LinkedHashMap viewListOfCurrency() {

        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)
        String countryEntry = """SELECT a.id AS id,currency,curr_code,curr_symbol,hundreds_name,b.printablename AS printablename,CASE a.status WHEN '1' THEN 'Active' WHEN '2' THEN 'Inactive' END AS active_status FROM currencies AS a LEFT JOIN countries AS b ON a.country_id=b.id """;
        List<GroovyRowResult> viewCurrencyList = db.rows(countryEntry);
        int total = viewCurrencyList.size()
        return [viewCurrencyList: viewCurrencyList, count: total]
    }



    public LinkedHashMap viewListOfCurrencySearch(int offset, String limit, String sortItem, String sortOrder, String searchItem, String searchString) {
        //def db = new Sql(dataSource)
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)
        String queryString = "LOWER('%" + searchString + "%')"

        String searchBy = "LOWER(${searchItem})"

        String countryEntry = """SELECT a.id AS id,currency,curr_code,curr_symbol,hundreds_name,b.printablename AS printablename,CASE a.status WHEN '1' THEN 'Active' WHEN '2' THEN 'Inactive' END AS active_status FROM currencies AS a LEFT JOIN countries AS b ON a.country_id=b.id WHERE ${searchBy} LIKE ${queryString}
        ORDER BY ${sortItem} ${sortOrder}  LIMIT ${limit} OFFSET ${offset} """;

        List<GroovyRowResult> viewCountryList = db.rows(countryEntry);

        String countQuery = """SELECT  COUNT(a.id) AS totalCountry FROM currencies AS a LEFT JOIN countries AS b ON a.country_id=b.id WHERE ${searchBy} LIKE ${queryString} """;
        List<GroovyRowResult> count_result = db.rows(countQuery)
        int total = count_result[0].totalCountry
        return [viewCountryList: viewCountryList, count: total]

    }

    public LinkedHashMap listOfPaymentTerms(){

        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String paymentTermQuery="""select a.id AS id,a.alert_repeat_days AS alertRepeatDays,a.final_reminder_days AS finalReminderDays,
                                   a.alert_start_days AS alertStartDays,a.days_before_due AS daysBeforeDue,
                                   a.terms AS terms, a.status AS status, a.version As version from payment_terms AS a """

        List<GroovyRowResult> paymentTermItemList = db.rows(paymentTermQuery)
        int total = paymentTermItemList.size()

        return[paymentTermItemList:paymentTermItemList,count:total]
    }

    public LinkedHashMap mainListOfFiscalYear(){

        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String fiscalYearQuery="""select a.id AS id,date_format(a.year_begin,'%d-%m-%Y') AS yearBegin,date_format(a.year_end,'%d-%m-%Y') AS yearEnd,a.status AS status,a.version As version from fiscal_year AS a WHERE a.status IN (1,2)"""
        List<GroovyRowResult>fiscalYearItemList=db.rows(fiscalYearQuery)
        int total=fiscalYearItemList.size()
        return[fiscalYearItemList:fiscalYearItemList,count:total]
    }

    public LinkedHashMap listOfChartGroup(){

        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String chartGroupQuery="""select a.id AS id,a.name AS name,a.status AS status,b.name as chartClassName, a.accountant_name As accountantName from chart_group AS a LEFT JOIN chart_class AS b ON a.chart_class_id=b.id"""
        List<GroovyRowResult>chartGroupItemList=db.rows(chartGroupQuery)
        int total=chartGroupItemList.size()
        return[chartGroupItemList:chartGroupItemList,count:total]

    }

    public LinkedHashMap listOfChartClass(){

        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String chartClassQuery="""select a.id AS id,a.name AS name,a.status AS status,b.class_type As classType,a.accountant_name As accountantName from chart_class AS a LEFT JOIN chart_class_type AS b ON a.chart_class_type_id=b.id"""
        List<GroovyRowResult>chartClassItemList=db.rows(chartClassQuery)
        int total=chartClassItemList.size()
        return[chartClassItemList:chartClassItemList,count:total]
    }

    public LinkedHashMap listOfProductUnit(){

        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String productUnitQuery="""select a.id AS id, a.decimals AS decimals, a.status AS status, a.unit_name AS unitName from product_unit AS a  """
        List<GroovyRowResult>productUnitItemList=db.rows(productUnitQuery)
        int total=productUnitItemList.size()
        return[productUnitItemList:productUnitItemList,count:total]
    }

    public LinkedHashMap listOfProductMaster(){

        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String productMasterQuery="""select a.id AS id, a.actual_cost AS actualCost,a.product_code AS productCode,a.product_name AS productName,a.purchase_account_code AS purchaseAccountCode,a.sales_account_code AS salesAccountCode,a.status AS status from product_master AS a"""
        List<GroovyRowResult>productMasterItemList=db.rows(productMasterQuery)
        int total=productMasterItemList.size()
        return[productMasterItemList:productMasterItemList,count:total]

    }

    public LinkedHashMap listOfProductCategory(){

        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String productCategoryQuery="""select a.id AS id, a.category_name AS categoryName, a.status AS status, a.product_type AS productType from product_category AS a """
        List<GroovyRowResult>productCategoryItemList=db.rows(productCategoryQuery)
        int total=productCategoryItemList.size()
        return[productCategoryItemList:productCategoryItemList,count:total]


    }

    public LinkedHashMap listOfcompanyBankGlRelation(){

        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String companyBankGlRelationQuery="""select a.id AS id,a.bank_account_code AS bankAccountCode,a.gl_chart_code As glChartCode,a.status AS status from company_bank_gl_relation AS a"""
        List<GroovyRowResult>companyBankGlRelationItemList=db.rows(companyBankGlRelationQuery)
        int total=companyBankGlRelationItemList.size()
        return[companyBankGlRelationItemList:companyBankGlRelationItemList,count:total]
    }

    public LinkedHashMap listOfDebitCreditGlSetup(){

        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String debitCreditGlSetupQuery="""select a.id AS id,a.creditor_gl_code AS creditorGlCode,a.debitor_gl_code AS debitorGlCode,a.reconcilation_gl_code As reconcilationGlCode from debit_credit_gl_setup AS a """
        List<GroovyRowResult>debitCreditGlSetupItemList=db.rows(debitCreditGlSetupQuery)
        int total=debitCreditGlSetupItemList.size()
        return[debitCreditGlSetupItemList:debitCreditGlSetupItemList,count:total]

    }

    public LinkedHashMap listOfReconciliationBookingType(){

        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String reconciliationBookingTypeQuery="""select a.id AS id,a.form_type AS formType,a.gl_account AS glAccount,a.is_fixed_gl As isFixedGl,a.payment_type as paymentType,a.status AS status from reconciliation_booking_type AS a """
        List<GroovyRowResult>reconciliationBookingTypeItemList=db.rows(reconciliationBookingTypeQuery)
        int total=reconciliationBookingTypeItemList.size()
        return[reconciliationBookingTypeItemList:reconciliationBookingTypeItemList,count:total]
    }

    public LinkedHashMap listOfVatCategory() {

        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String vatCategoryQuery="""select a.id AS id, a.category_name AS categoryName, CONCAT(a.rate,'%') AS rate, a.purchase_gl_account AS purchaseGlAccount, a.sales_gl_account AS salesGlAccount,a.status AS status from vat_category AS a"""
        List<GroovyRowResult>vatCategoryItemList=db.rows(vatCategoryQuery)
        int total=vatCategoryItemList.size()
        return[vatCategoryItemList:vatCategoryItemList,count:total]
    }

    public LinkedHashMap listOfExchangeRate(){

        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String exchangeRateQuery="""select a.id AS id, a.from_curr_code_id AS fromCurrCodeId,date_format(a.from_exchange_date,'%d-%m-%Y')  AS fromExchangeDate, a.rate AS rate, a.to_curr_code_id as toCurrCodeId,date_format(a.to_exchange_date,'%d-%m-%Y')  as toExchangeDate,a.status as status from exchange_rates AS a"""
        List<GroovyRowResult>exchangeRateItemList=db.rows(exchangeRateQuery)
        int total=exchangeRateItemList.size()
        return[exchangeRateItemList:exchangeRateItemList,count:total]
    }

    public LinkedHashMap listOfIpRestrictions(){

        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String ipRestrictionsQuery="""select a.id AS id,a.ip AS ip,a.sequence_order AS sequenceOrder,a.url_pattern AS urlPattern from ipaddress AS a"""
        List<GroovyRowResult>ipRestrictionsItemList=db.rows(ipRestrictionsQuery)
        int total=ipRestrictionsItemList.size()
        return[ipRestrictionsItemList:ipRestrictionsItemList,count:total]
    }

    public LinkedHashMap listOfBankAccounType(){
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String bankAccountTypeQuery="""select a.id AS id,a.name AS name,a.status AS status,a.description As description from bank_account_type AS a"""
        List<GroovyRowResult>bankAccountTypeItemList=db.rows(bankAccountTypeQuery)
        int total=bankAccountTypeItemList.size()
        return[bankAccountTypeItemList:bankAccountTypeItemList,count:total]
    }

    public LinkedHashMap listOfCompanyBankAccount(){

        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String companyBankAccountQuery="""select a.id AS id,a.version As version,a.bank_account_code AS bankAccountCode,
                                        a.bank_account_name AS bankAccountName,a.bank_account_no AS bankAccountNo,a.bank_account_type AS bankAccountType,
                                        a.bank_address AS bankAddress,cb_gl.gl_chart_code AS companyBankGLCode,a.bank_name AS bankName,a.iban AS iban,
                                        a.status AS status,a.bank_account_category AS accountCategory from company_bank_accounts AS a,
                                        company_bank_gl_relation AS cb_gl where a.bank_account_code = cb_gl.bank_account_code """

        List<GroovyRowResult>companyBankAccountItemList=db.rows(companyBankAccountQuery)
        int total=companyBankAccountItemList.size()

        return[companyBankAccountItemList:companyBankAccountItemList,count:total]
    }

    public LinkedHashMap listOfFiscalYear(int offset, String limit, String sortItem, String sortOrder, def selectedYear){

        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance( companyConfig.serverUrl+companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)
        List<GroovyRowResult> expenseIncomeYearList
        List<GroovyRowResult> incomeYearList
        List<GroovyRowResult> expenseYearcount
        List<GroovyRowResult> incomeYearcount
//        String sqlExpense="SELECT YEAR(year_begin) as year,'Expense' as type FROM fiscal_year WHERE YEAR(year_begin) not in (SELECT ie.booking_year FROM invoice_expense as ie )";
//        expenseYearList = db.rows(sqlExpense);
//        String sqlIncome="SELECT YEAR(year_begin) as year,'Income' as type FROM fiscal_year WHERE YEAR(year_begin) not in (SELECT ii.booking_year FROM invoice_income as ii )"
//        incomeYearList = db.rows(sqlIncome);

        println(selectedYear);
//        def splitted = selectedYear.split("-")
//        println(splitted[2]);
//        String sqlExpenseIncome="select fiscalYear,'Income,Expense' as type1 from (\n" +
//                "SELECT YEAR(fye.year_begin) as fiscalYear FROM fiscal_year as fye WHERE YEAR(fye.year_begin) not in \n" +
//                "(SELECT ie.booking_year FROM invoice_expense as ie )\n" +
//                "union\n" +
//                "SELECT YEAR(fyi.year_begin) as fiscalYear FROM fiscal_year as fyi WHERE YEAR(fyi.year_begin) \n" +
//                "not in (SELECT ii.booking_year FROM invoice_income as ii )\n" +
//                ") as t group by fiscalYear;";

//        String sqlExpenseIncome="SELECT YEAR(year_begin) as fiscalYear,'Income,Expense' as type1 FROM fiscal_year where YEAR(year_begin) <> '"+selectedYear+"'  and YEAR(year_begin) not in ( select booking_year from invoice_income group by booking_year union select booking_year from invoice_expense group by booking_year)";
        String sqlExpenseIncome = "SELECT YEAR(year_begin) as fiscalYear,'Income,Expense' as type1 FROM fiscal_year where YEAR(year_begin) <> '"+selectedYear+"' order by year_begin"
        expenseIncomeYearList = db.rows(sqlExpenseIncome);

        String sqlCountIncome="SELECT COUNT(*) FROM fiscal_year WHERE YEAR(year_begin) not in (SELECT ii.booking_year FROM invoice_income as ii )"
        incomeYearcount = db.rows(sqlCountIncome)
        String sqlCountExpense="SELECT COUNT(*) FROM fiscal_year WHERE YEAR(year_begin) not in (SELECT ie.booking_year FROM invoice_expense as ie )"
        expenseYearcount = db.rows(sqlCountExpense)


        return [expenseIncomeYearList: expenseIncomeYearList, expenseYearcount: expenseYearcount, incomeYearcount: incomeYearcount]
    }

    String copyInvoiceDateForFiscalYear(String[] ids, def fiscalYear){
        String copyForFiscalYear='';

        String flag='0';

        for(int i=0;i<ids.size();i++){
            copyForFiscalYear=ids[i]

                String tableDeleteExpenseBudgetDetails="budget_item_expense_details"
                String whereStringDeleteExpenseBudgetDetails="budget_item_expense_id = ANY (SELECT  id FROM budget_item_expense where booking_period_end_year ='"+fiscalYear+"')"
                budgetViewDatabaseService.delete(tableDeleteExpenseBudgetDetails,whereStringDeleteExpenseBudgetDetails)

                String tableDeleteExpenseBudget="budget_item_expense"
                String whereStringDeleteExpenseBudget="booking_period_end_year = '"+fiscalYear+"'"
                budgetViewDatabaseService.delete(tableDeleteExpenseBudget,whereStringDeleteExpenseBudget)

                String sqlExpenseData="SELECT " +
                        "booking_period_end_month, " +
                        "booking_period_end_year, " +
                        "booking_period_start_month, " +
                        "booking_period_start_year, " +
                        "booking_period_type, " +
                        "budget_id, " +
                        "created_date, " +
                        "fiscal_id, " +
                        "invoice_frequency, " +
                        "moment_of_sending_invoice, " +
                        "payment_terms_id, " +
                        "`status`, " +
                        "total_gl_amount, " +
                        "total_vat, " +
                        "updated_date, " +
                        "vendor_id, " +
                        "id " +
                        "FROM " +
                        "budget_item_expense where booking_period_end_year = '"+copyForFiscalYear+"'"


               def resultForExpenseData = budgetViewDatabaseService.executeQuery(sqlExpenseData);

                def db='';
                db = budgetViewDatabaseService.startTransaction();

                resultForExpenseData.each { resultLine ->
                    def bookingPeriodEndMonthTemp = resultLine[0]
                    def bookingPeriodEndYearTemp = fiscalYear
                    def bookingPeriodStartMonthTemp = resultLine[2]
                    def bookingPeriodStartYearTemp = fiscalYear
                    def bookingPeriodTypeTemp = resultLine[4]
                    def budgetIdTemp = resultLine[5]
                    def createdDateTemp = resultLine[6]
                    def fiscalIdTemp = resultLine[7]
                    def invoiceFrequencyTemp = resultLine[8]
                    def momentOfSendingInvoiceTemp = resultLine[9]
                    def paymentTermsIdTemp = resultLine[10]
                    def statusTemp = resultLine[11]
                    def totalGlAmountTemp = resultLine[12]
                    def totalVatTemp = resultLine[13]
                    def updatedDateTemp = resultLine[14]
                    def vendorIdTemp = resultLine[15]
                    def idTemp = resultLine[16]


                    Map mapForExpenseData = [
                            bookingPeriodEndMonth : bookingPeriodEndMonthTemp ,
                            bookingPeriodEndYear :bookingPeriodEndYearTemp,
                            bookingPeriodStartMonth :bookingPeriodStartMonthTemp,
                            bookingPeriodStartYear :bookingPeriodStartYearTemp ,
                            bookingPeriodType :bookingPeriodTypeTemp,
                            budgetId :budgetIdTemp,
                            createdDate :createdDateTemp,
                            fiscalId :fiscalIdTemp,
                            invoiceFrequency :invoiceFrequencyTemp,
                            momentOfSendingInvoice :momentOfSendingInvoiceTemp,
                            paymentTermsId :paymentTermsIdTemp,
                            status :statusTemp,
                            totalGlAmount :totalGlAmountTemp,
                            totalVat :totalVatTemp,
                            updatedDate :updatedDateTemp,
                            vendorId:vendorIdTemp
                    ]

                    int currentBudgetItemExpenseId;
                    currentBudgetItemExpenseId = budgetViewDatabaseService.insertAfterTransaction(mapForExpenseData, "BudgetItemExpense",db)

                    String sqlExpenseBudgetDetails = "SELECT " +
                            "budget_item_expense_id, " +
                            "gl_account, " +
                            "note, " +
                            "price, " +
                            "product_id, " +
                            "quantity, " +
                            "total_price_with_vat, " +
                            "total_price_without_vat, " +
                            "unit_price, " +
                            "vat_amount, " +
                            "vat_category_id, " +
                            "vat_rate " +
                            "FROM " +
                            "budget_item_expense_details WHERE budget_item_expense_id ='"+idTemp+"'"


                    def resultForExpenseDetails = budgetViewDatabaseService.executeQuery(sqlExpenseBudgetDetails);
                    resultForExpenseDetails.each{ resultLineDetails ->

                        def glAccountTemp = resultLineDetails[1]
                        def noteTemp = resultLineDetails[2]
                        def priceTemp = resultLineDetails[3]
                        def productIdTemp = resultLineDetails[4]
                        def quantityTemp = resultLineDetails[5]
                        def totalPriceWithVatTemp = resultLineDetails[6]
                        def totalPriceWithoutVatTemp = resultLineDetails[7]
                        def unitPriceTemp = resultLineDetails[8]
                        def vatAmountTemp = resultLineDetails[9]
                        def vatCategoryIdTemp = resultLineDetails[10]
                        def vatRateTemp = resultLineDetails[11]

                        Map mapForExpenseDetails = [
                                budgetItemExpenseId :currentBudgetItemExpenseId,
                                glAccount :glAccountTemp,
                                note :noteTemp,
                                price :priceTemp,
                                productId :productIdTemp,
                                quantity :quantityTemp,
                                totalPriceWithVat :totalPriceWithVatTemp,
                                totalPriceWithoutVat :totalPriceWithoutVatTemp,
                                unitPrice : unitPriceTemp,
                                vatAmount :vatAmountTemp,
                                vatCategoryId :vatCategoryIdTemp,
                                vatRate :vatRateTemp,
                        ]

                        budgetViewDatabaseService.insertAfterTransaction(mapForExpenseDetails, "BudgetItemExpenseDetails",db)
                    }
                 }
                budgetViewDatabaseService.commitData(db);


                String tableDeleteIncomeBudgetDetails="budget_item_income_details"
                String whereStringDeleteIncomeBudgetDetails="budget_item_income_id = ANY (SELECT  id FROM budget_item_income where booking_period_end_year ='"+fiscalYear+"')"
                budgetViewDatabaseService.delete(tableDeleteIncomeBudgetDetails,whereStringDeleteIncomeBudgetDetails)


                String tableDeleteIncomeBudget="budget_item_income"
                String whereStringDeleteIncomeBudget="booking_period_end_year="+"'${fiscalYear}'"
                budgetViewDatabaseService.delete(tableDeleteIncomeBudget,whereStringDeleteIncomeBudget)


                String sqlIncomeData="SELECT " +
                        "booking_period_end_month, " +
                        "booking_period_end_year, " +
                        "booking_period_start_month, " +
                        "booking_period_start_year, " +
                        "booking_period_type, " +
                        "budget_id, " +
                        "created_date, " +
                        "customer_id, " +
                        "fiscal_id, " +
                        "invoice_frequency, " +
                        "moment_of_sending_invoice, " +
                        "payment_terms_id, " +
                        "`status`, " +
                        "total_gl_amount, " +
                        "total_vat, " +
                        "updated_date, " +
                        "id " +
                        "FROM " +
                        "budget_item_income WHERE booking_period_end_year = '"+copyForFiscalYear+"'"

                def resultForIncomeData =budgetViewDatabaseService.executeQuery(sqlIncomeData);

                def db1='';
                db1 = budgetViewDatabaseService.startTransaction();
                resultForIncomeData.each { resultLine ->

                    def bookingPeriodEndMonthTemp =  resultLine[0]
                    def bookingPeriodEndYearTemp = fiscalYear
                    def bookingPeriodStartMonthTemp = resultLine[2]
                    def bookingPeriodStartYearTemp = fiscalYear
                    def bookingPeriodTypeTemp = resultLine[4]
                    def budgetIdTemp = resultLine[5]
                    def createdDateTemp = resultLine[6]
                    def customerIdTemp = resultLine[7]
                    def fiscalIdTemp = resultLine[8]
                    def invoiceFrequencyTemp = resultLine[9]
                    def momentOfSendingInvoiceTemp = resultLine[10]
                    def paymentTermsIdTemp = resultLine[11]
                    def statusTemp = resultLine[12]
                    def totalGlAmountTemp = resultLine[13]
                    def totalVatTemp = resultLine[14]
                    def updatedDateTemp = resultLine[15]
                    def idIncomeTemp = resultLine[16]


                    Map mapForIncomeData = [
                            bookingPeriodEndMonth : bookingPeriodEndMonthTemp ,
                            bookingPeriodEndYear : bookingPeriodEndYearTemp ,
                            bookingPeriodStartMonth : bookingPeriodStartMonthTemp,
                            bookingPeriodStartYear : bookingPeriodStartYearTemp,
                            bookingPeriodType : bookingPeriodTypeTemp,
                            budgetId : budgetIdTemp,
                            createdDate : createdDateTemp,
                            customerId :customerIdTemp,
                            fiscalId : fiscalIdTemp,
                            invoiceFrequency : invoiceFrequencyTemp,
                            momentOfSendingInvoice : momentOfSendingInvoiceTemp,
                            paymentTermsId : paymentTermsIdTemp,
                            status : statusTemp,
                            totalGlAmount : totalGlAmountTemp,
                            totalVat : totalVatTemp,
                            updatedDate :  updatedDateTemp
                    ]
                    def currentBudgetIncomeId;
                    currentBudgetIncomeId=budgetViewDatabaseService.insertAfterTransaction(mapForIncomeData, "BudgetItemIncome",db1)

                    String sqlIncomeBudgetDetails ="SELECT " +
                            "budget_item_income_id, " +
                            "gl_account, " +
                            "note, " +
                            "price, " +
                            "product_id, " +
                            "quantity, " +
                            "total_price_with_vat, " +
                            "total_price_without_vat, " +
                            "unit_price, " +
                            "vat_amount, " +
                            "vat_category_id, " +
                            "vat_rate " +
                            "FROM " +
                            "budget_item_income_details WHERE budget_item_income_id ='"+idIncomeTemp+"'"

                    def resultForIncomeDetails =budgetViewDatabaseService.executeQuery(sqlIncomeBudgetDetails);
                    resultForIncomeDetails.each{ resultLineIncomeDetails ->

                        def budgetItemIncomeIdTemp = currentBudgetIncomeId
                        def glAccountTemp = resultLineIncomeDetails[1]
                        def noteTemp = resultLineIncomeDetails[2]
                        def priceTemp = resultLineIncomeDetails[3]
                        def productIdTemp = resultLineIncomeDetails[4]
                        def quantityTemp = resultLineIncomeDetails[5]
                        def totalPriceWithVatTemp = resultLineIncomeDetails[6]
                        def totalPriceWithoutVatTemp = resultLineIncomeDetails[7]
                        def unitPriceTemp = resultLineIncomeDetails[8]
                        def vatAmountTemp = resultLineIncomeDetails[9]
                        def vatCategoryIdTemp = resultLineIncomeDetails[10]
                        def vatRateTemp = resultLineIncomeDetails[11]


                        Map mapForIncomeDetails = [
                                budgetItemIncomeId : budgetItemIncomeIdTemp ,
                                glAccount : glAccountTemp,
                                note : noteTemp,
                                price : priceTemp,
                                productId : productIdTemp,
                                quantity : quantityTemp,
                                totalPriceWithVat : totalPriceWithVatTemp,
                                totalPriceWithoutVat : totalPriceWithoutVatTemp,
                                unitPrice : unitPriceTemp,
                                vatAmount : vatAmountTemp,
                                vatCategoryId : vatCategoryIdTemp,
                                vatRate : vatRateTemp
                        ]
                        budgetViewDatabaseService.insertAfterTransaction(mapForIncomeDetails, "budgetItemIncomeDetails",db1)
                    }

                }
                budgetViewDatabaseService.commitData(db1);
                flag='1';


        }
                return flag;

    }

    def writeOffInvoicePayment(def invoiceId,def paytmentReference,def transactionDate, def bookingPeriod, def paidAmount,def dueAmount, def bookingYear, def day, def customerId,def writeOffAmount = 0,def writeOffDescription="Write off by user"){
        def totalWriteOff = 0
        if(writeOffAmount > 0)
            totalWriteOff = writeOffAmount
        else
            totalWriteOff = dueAmount
        def transDate = bookingYear+"-"+bookingPeriod+"-"+day
        def maxDBNum=0
        //Generate invoice number for  write off
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)
        maxDBNum = new BudgetViewDatabaseService().executeQueryAtSingle("SELECT write_off FROM next_generated_number")
        def writeOffNextGenNum = maxDBNum[0] + 1

        String updateNextGenerateNumber = """UPDATE next_generated_number SET write_off='$writeOffNextGenNum' """
        db.execute(updateNextGenerateNumber)

        String isReceiptQuery =  """SELECT is_book_receive from invoice_expense where id='$invoiceId' """
        def isReceipt = new BudgetViewDatabaseService().executeQuery(isReceiptQuery)

        def recenciliationCode
        if(isReceipt[0][0]== 1){
            recenciliationCode = invoiceId + "#4"
        }else{
            recenciliationCode = invoiceId + "#2"
        }

        def creditorCreditGlSetupInfo = new CoreParamsHelperTagLib().getDebitCreditGlSetupInfo()
        def recenciliationGL = creditorCreditGlSetupInfo[1]
        def recenciliationBankGL = "1091"

        def bookingDate = new ApplicationUtil().convertDateFromMonthAndYear(bookingPeriod, bookingYear)

        Map trnMasBooked = [
                accountCode       : recenciliationGL,
                amount            : totalWriteOff,
                transDate         : transDate,
                transType         : 8,
                invoiceNo         : writeOffNextGenNum,
                bookingPeriod     : bookingPeriod,
                bookingYear       : bookingYear,
                recenciliationCode: recenciliationCode,
                userId            : springSecurityService.principal.id,
                createDate        : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()),
                process           : com.bv.constants.Process.MANUAL_RECONCILIATION,
                bookingDate       : bookingDate,
                customerId        : 0,
                vendorId          : customerId
        ]

        def tableNametrnMasBooked = "TransMaster"
        new BudgetViewDatabaseService().insert(trnMasBooked, tableNametrnMasBooked)

        Map trnMasBank = [
                accountCode       : recenciliationBankGL,
                amount            : -totalWriteOff,
                transDate         : transDate,
                transType         : 8,
                invoiceNo         : writeOffNextGenNum,
                bookingPeriod     : bookingPeriod,
                bookingYear       : bookingYear,
                recenciliationCode: recenciliationCode,
                userId            : springSecurityService.principal.id,
                createDate        : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()),
                process           : com.bv.constants.Process.MANUAL_RECONCILIATION,
                bookingDate       : bookingDate,
                customerId        : 0,
                vendorId          : customerId
        ]

        def tableNametrnMasBank = "TransMaster"
        new BudgetViewDatabaseService().insert(trnMasBank, tableNametrnMasBank)

        Map writeOffDetails = [
                writeOffDescription: writeOffDescription,
                invoiceNo: writeOffNextGenNum
        ]

        def tableTransMasterWriteOffDetails = "TransMasterWriteOffDetails"
        new BudgetViewDatabaseService().insert(writeOffDetails, tableTransMasterWriteOffDetails)

    }

    def writeOffInvoicePaymentCustomer(def invoiceId,def paytmentReference,def transactionDate, def bookingPeriod,def paidAmount,def dueAmount, def bookingYear,def day, def debtorId,def writeOffAmount = 0,def writeOffDescription){
        def totalWriteOff = 0
        if(writeOffAmount > 0)
            totalWriteOff = writeOffAmount
        else
            totalWriteOff = dueAmount

        def transDate = bookingYear+"-"+bookingPeriod+"-"+day

        def maxDBNum=0
        //Generate invoice number for  write off
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)
        maxDBNum = new BudgetViewDatabaseService().executeQueryAtSingle("SELECT write_off FROM next_generated_number")
        def writeOffNextGenNum = maxDBNum[0] + 1

        String updateNextGenerateNumber = """UPDATE next_generated_number SET write_off='$writeOffNextGenNum' """
        db.execute(updateNextGenerateNumber)
        def recenciliationCode = invoiceId + "#1"
        def creditorCreditGlSetupInfo = new CoreParamsHelperTagLib().getDebitCreditGlSetupInfo()
        def recenciliationGL = creditorCreditGlSetupInfo[2]
        def recenciliationBankGL = "1091"

        def bookingDate = new ApplicationUtil().convertDateFromMonthAndYear(bookingPeriod, bookingYear)

        Map trnMasBooked = [
                accountCode       : recenciliationGL,
                amount            : totalWriteOff * (-1),
                transDate         : transDate,
                transType         : 8,
                invoiceNo         : writeOffNextGenNum,
                bookingPeriod     : bookingPeriod,
                bookingYear       : bookingYear,
                recenciliationCode: recenciliationCode,
                userId            : springSecurityService.principal.id,
                createDate        : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()),
                process           : com.bv.constants.Process.MANUAL_RECONCILIATION,
                bookingDate       : bookingDate,
                customerId        : debtorId,
                vendorId          : 0
        ]

        def tableNametrnMasBooked = "TransMaster"
        new BudgetViewDatabaseService().insert(trnMasBooked, tableNametrnMasBooked)

        Map trnMasBank = [
                accountCode       : recenciliationBankGL,
                amount            : totalWriteOff,
                transDate         : transDate,
                transType         : 8,
                invoiceNo         : writeOffNextGenNum,
                bookingPeriod     : bookingPeriod,
                bookingYear       : bookingYear,
                recenciliationCode: recenciliationCode,
                userId            : springSecurityService.principal.id,
                createDate        : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()),
                process           : com.bv.constants.Process.MANUAL_RECONCILIATION,
                bookingDate       : bookingDate,
                customerId        : debtorId,
                vendorId          : 0
        ]

        def tableNametrnMasBank = "TransMaster"
        new BudgetViewDatabaseService().insert(trnMasBank, tableNametrnMasBank)

        Map writeOffDetails = [
                writeOffDescription: writeOffDescription,
                invoiceNo: writeOffNextGenNum
        ]

        def tableTransMasterWriteOffDetails = "TransMasterWriteOffDetails"
        new BudgetViewDatabaseService().insert(writeOffDetails, tableTransMasterWriteOffDetails)

    }

    def privatePaidCustomer(def invoiceId,def paytmentReference,def transactionDate, def bookingPeriod, def paidAmount,def dueAmount, def bookingYear, def day){

        def transDate = bookingYear+"-"+bookingPeriod+"-"+day
        println(""+invoiceId)
        def maxDBNum=0
        //Generate invoice number for  write off
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)
        maxDBNum = new BudgetViewDatabaseService().executeQueryAtSingle("SELECT private_paid FROM next_generated_number");
        def privatePaidNextGenNum = maxDBNum[0] + 1;
        println(""+privatePaidNextGenNum)
        String updateNextGenerateNumber = """UPDATE next_generated_number SET private_paid ='$privatePaidNextGenNum' """;
        db.execute(updateNextGenerateNumber);
        def recenciliationCode = invoiceId + "#1"
        def creditorCreditGlSetupInfo = new CoreParamsHelperTagLib().getDebitCreditGlSetupInfo()
        def recenciliationGL = creditorCreditGlSetupInfo[2]
        def recenciliationBankGL = creditorCreditGlSetupInfo[4]

        def bookingDate = new ApplicationUtil().convertDateFromMonthAndYear(bookingPeriod, bookingYear)

        Map trnMasBooked = [
                accountCode       : recenciliationGL,
                amount            : -dueAmount,
                transDate         : transDate,
                transType         : 9,
                invoiceNo         : privatePaidNextGenNum,
                bookingPeriod     : bookingPeriod,
                bookingYear       : bookingYear,
                recenciliationCode: recenciliationCode,
                userId            : springSecurityService.principal.id,
                createDate        : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()),
                process           : com.bv.constants.Process.MANUAL_RECONCILIATION,
                bookingDate       : bookingDate,
                customerId        : 0,
                vendorId          : 0
        ]

        def tableNametrnMasBooked = "TransMaster"
        new BudgetViewDatabaseService().insert(trnMasBooked, tableNametrnMasBooked)

        Map trnMasBank = [
                accountCode       : recenciliationBankGL ,
                amount            : dueAmount,
                transDate         : transDate,
                transType         : 9,
                invoiceNo         : privatePaidNextGenNum,
                bookingPeriod     : bookingPeriod,
                bookingYear       : bookingYear,
                recenciliationCode: recenciliationCode,
                userId            : springSecurityService.principal.id,
                createDate        : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()),
                process           : com.bv.constants.Process.MANUAL_RECONCILIATION,
                bookingDate       : bookingDate,
                customerId        : 0,
                vendorId          : 0
        ]

        def tableNametrnMasBank = "TransMaster"
        new BudgetViewDatabaseService().insert(trnMasBank, tableNametrnMasBank)


    }

    def privatePaidVendor(def invoiceId,def paytmentReference,def transactionDate, def bookingPeriod, def paidAmount,def dueAmount, def bookingYear,def day){

        def transDate = bookingYear+"-"+bookingPeriod+"-"+day
        println(""+invoiceId)
        def maxDBNum=0
        //Generate invoice number for  write off
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)
        maxDBNum = new BudgetViewDatabaseService().executeQueryAtSingle("SELECT private_paid FROM next_generated_number");
        def privatePaidNextGenNum = maxDBNum[0] + 1;
        println(""+privatePaidNextGenNum)
        String updateNextGenerateNumber = """UPDATE next_generated_number SET private_paid ='$privatePaidNextGenNum' """;
        db.execute(updateNextGenerateNumber);

        String isReceiptQuery =  """SELECT is_book_receive from invoice_expense where id='$invoiceId' """
        def isReceipt = new BudgetViewDatabaseService().executeQuery(isReceiptQuery)
        def recenciliationCode
        if(isReceipt[0][0]== 1){
            recenciliationCode = invoiceId + "#4"
        }else{
            recenciliationCode = invoiceId + "#2"
        }

        def bookingDate = new ApplicationUtil().convertDateFromMonthAndYear(bookingPeriod, bookingYear)

        def creditorCreditGlSetupInfo = new CoreParamsHelperTagLib().getDebitCreditGlSetupInfo()
        def recenciliationGL = creditorCreditGlSetupInfo[1]
        def recenciliationBankGL = creditorCreditGlSetupInfo[4]

        Map trnMasBooked = [
                accountCode       : recenciliationGL,
                amount            : dueAmount,
                transDate         : transDate,
                transType         : 9,
                invoiceNo         : privatePaidNextGenNum,
                bookingPeriod     : bookingPeriod,
                bookingYear       : bookingYear,
                recenciliationCode: recenciliationCode,
                userId            : springSecurityService.principal.id,
                createDate        : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()),
                process           : com.bv.constants.Process.MANUAL_RECONCILIATION,
                bookingDate       : bookingDate,
                customerId        : 0,
                vendorId          : 0
        ]

        def tableNametrnMasBooked = "TransMaster"
        new BudgetViewDatabaseService().insert(trnMasBooked, tableNametrnMasBooked)

        Map trnMasBank = [
                accountCode       : recenciliationBankGL ,
                amount            : -dueAmount,
                transDate         : transDate,
                transType         : 9,
                invoiceNo         : privatePaidNextGenNum,
                bookingPeriod     : bookingPeriod,
                bookingYear       : bookingYear,
                recenciliationCode: recenciliationCode,
                userId            : springSecurityService.principal.id,
                createDate        : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()),
                process           : com.bv.constants.Process.MANUAL_RECONCILIATION,
                bookingDate       : bookingDate,
                customerId        : 0,
                vendorId          : 0
        ]

        def tableNametrnMasBank = "TransMaster"
        new BudgetViewDatabaseService().insert(trnMasBank, tableNametrnMasBank)
    }

     Map getCompanyInfoMap(){
        Map map = ["Id"                   : 0,
                   "version"              : 0,
                   "language"             : 'nl',
                   "address_line1"        : '',
                   "address_line2"        : '',
                   "amountDecimalPoint"   : 0,
                   "chamber_commerce_no"  : '',
                   "city"                 : '',
                   "company_date_format"  : '',
                   "company_full_name"    : '',
                   "company_short_name"   : '',
                   "country"              : '',
                   "currency_id"          : 0,
                   "date_seperator"       : '',
                   "decimal_rounding_type": '',
                   "decimal_seprator"     : '',
                   "email_address"        : '',
                   "iban"                 : '',
                   "bic"                  : ''
        ]

        String queryLanguage = """SELECT id,version,language,address_line1,address_line2,amount_decimal_point,chamber_commerce_no,city,company_date_format,
                                company_full_name,company_short_name,country,email_address,date_seperator,
                                decimal_rounding_type,decimal_seprator,iban,bic
                                FROM company_setup WHERE id= 1"""

        ArrayList companyInfo = new BudgetViewDatabaseService().executeQuery(queryLanguage)

        companyInfo.eachWithIndex { item, key ->

            map.Id = item[0]
            map.version = item[1]
            if(item[2] == 'du'){
                map.language = 'nl'
            }else{
                map.language = item[2]
            }

            map.address_line1 = item[3]
            map.address_line2 = item[4]
            map.amountDecimalPoint = item[5]
            map.chamber_commerce_no = item[6]
            map.city = item[7]
            map.company_date_format = item[8]
            map.company_full_name = item[9]
            map.company_short_name = item[10]
            map.country = item[11]
            map.email_address = item[12]
            map.date_seperator = item[13]
            map.decimal_rounding_type = item[14]
            map.decimal_seprator = item[15]
            map.iban = item[16]
            map.bic = item[17]
        }

        return map;
    }

}
