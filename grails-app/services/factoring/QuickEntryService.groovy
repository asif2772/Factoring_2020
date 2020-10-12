package factoring

import grails.gorm.transactions.Transactional
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import org.apache.logging.log4j.Logger

import java.text.SimpleDateFormat

@Transactional
class QuickEntryService {

    static transactional = false
//    private Logger log = Logger.getLogger(getClass())
//    DataSource dataSource

    public LinkedHashMap getReceiptEntries(int offset, String limit, String sortItem, String sortOrder) {
        //def db = new Sql(dataSource)
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance( companyConfig.serverUrl+companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)


        String expenseEntry = """SELECT id, invoice_number, vendor_id, trans_date, created_date, total_gl_amount, total_vat FROM receipt_entry ORDER BY ${sortItem} ${sortOrder} LIMIT ${limit} OFFSET ${offset}  """;
        List<GroovyRowResult> expenseEntryList = db.rows(expenseEntry);

        String countQuery = """SELECT count(id) as count FROM receipt_entry""";
        List<GroovyRowResult> count_result = db.rows(countQuery)
        int total = count_result[0].count
        db.close();
        return [expenseEntryList: expenseEntryList, count: total]
    }

    public LinkedHashMap receiptEntryList(int offset, String limit, String sortItem, String sortOrder) {
        //def db = new Sql(dataSource)
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance( companyConfig.serverUrl+companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)


        String expenseEntry = """SELECT id, invoice_number, vendor_id, trans_date, created_date, total_gl_amount, total_vat FROM receipt_entry ORDER BY ${sortItem} ${sortOrder} LIMIT ${limit} OFFSET ${offset}  """;
        List<GroovyRowResult> expenseEntryList = db.rows(expenseEntry);

        String countQuery = """SELECT count(id) as count FROM receipt_entry""";
        List<GroovyRowResult> count_result = db.rows(countQuery)
        int total = count_result[0].count
        db.close();
        return [expenseEntryList: expenseEntryList, count: total]
    }

    /*
    * Dashboar Expense list -> from all budget item list show
    *
    * */

    public LinkedHashMap getImportInvoices() {
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance( companyConfig.serverUrl+companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String importInvoice = """  SELECT
                                    id,
                                    vendor_id as vendorId,
                                    vendor_name AS vendorName,
                                    debtor_name AS debtorName,                                    
                                    DATE_FORMAT(invoice_date,'%d-%m-%Y') AS invoiceDate,
                                    total as total,
                                    subtotal as subtotal,
                                    vat as vat,
                                    payment_terms_days as paymentTermsDays,
                                    payment_ref as paymentRef,
                                    debtor_contact_person as debtorContactPerson,
                                    address_line1 as addressLine1,
                                    address_line2 as addressLine2,
                                    debtor_area_code as debtorAreaCode,
                                    debtor_city as debtorCity,
                                    debtor_country as debtorCountry,
                                    iban as iban,
                                    debtor_mail as debtorMail,
                                    debtor_phone as debtorPhone,
                                    user_id as userId,
                                    is_valid_debtor as isValidDebtor
                                    FROM
                                    import_invoice""";

        List<GroovyRowResult> importInvoiceList = db.rows(importInvoice)
        int total = importInvoiceList.size()

        db.close()

        return [importInvoiceList: importInvoiceList, count: total]
    }

    public LinkedHashMap getDuplicateImportInvoices(def debtorName, def amount, def date) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy")
        Date parsed = format.parse(date.toString())
        date = new java.sql.Date(parsed.getTime())
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance( companyConfig.serverUrl+companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String importInvoice = """  SELECT
                                    id,
                                    vendor_id as vendorId,
                                    vendor_name AS vendorName,
                                    debtor_name AS debtorName,                                    
                                    DATE_FORMAT(invoice_date,'%d-%m-%Y') AS invoiceDate,
                                    total as total,
                                    subtotal as subtotal,
                                    vat as vat,
                                    payment_terms_days as paymentTermsDays,
                                    payment_ref as paymentRef,
                                    debtor_contact_person as debtorContactPerson,
                                    address_line1 as addressLine1,
                                    address_line2 as addressLine2,
                                    debtor_area_code as debtorAreaCode,
                                    debtor_city as debtorCity,
                                    debtor_country as debtorCountry,
                                    iban as iban,
                                    debtor_mail as debtorMail,
                                    debtor_phone as debtorPhone,
                                    user_id as userId
                                    FROM
                                    import_invoice WHERE debtor_name = '${debtorName}' AND total = ${amount} AND invoice_date = '${date}'""";

        List<GroovyRowResult> importInvoiceList = db.rows(importInvoice)

        db.close()

        return [importInvoiceList: importInvoiceList, count: importInvoiceList.size()]
    }

    public LinkedHashMap listOfBudgetItem(int offset, String limit, String sortItem, String sortOrder, String bookingPeriod, String fiscalYearId, String vendorId) {
        //def db = new Sql(dataSource)
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance( companyConfig.serverUrl+companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)


        String expenseEntry = """SELECT a.id as invoiceExpenseId, CONCAT(b.gl_account,'-',c.account_name) as glAccountName, CONCAT(sp.prefix,'-',a.budget_id) as budgetItemID,DATE_FORMAT(a.created_date,'%d-%m-%Y') as createdDate, b.total_price_without_vat as totalPriceWithoutVat, b.total_price_with_vat as totalPriceWithVat, a.STATUS AS total,b.gl_account AS tempGLAccount, b.id AS detailsID FROM budget_item_expense AS a,budget_item_expense_details AS b,chart_master AS c, system_prefix AS sp WHERE b.budget_item_expense_id=a.id AND b.gl_account=c.account_code AND a.vendor_id= ${vendorId} AND a.booking_period_start_month <= ${bookingPeriod} AND a.booking_period_start_year<=${fiscalYearId} AND a.booking_period_end_month>=${bookingPeriod} AND a.booking_period_end_year>=${fiscalYearId} AND sp.id=6
        ORDER BY ${sortItem} ${sortOrder} LIMIT ${limit} OFFSET ${offset} """;
        List<GroovyRowResult> dashboardExpenseBudgetItemList = db.rows(expenseEntry);

        ArrayList dashboardExpenseBudgetItemListFinal = new ArrayList()
        dashboardExpenseBudgetItemList.eachWithIndex {indexValue, keyValue ->
            String expenseTotalEntry = """SELECT COUNT(DISTINCT(a.id)) AS count_total FROM invoice_expense AS a LEFT JOIN invoice_expense_details AS b ON a.id=b.invoice_id WHERE a.budget_item_expense_id=${indexValue.invoiceExpenseId} AND b.account_code='${indexValue.tempGLAccount}'""";
            List<GroovyRowResult> dashboardExpenseBudgetTotalItemList = db.rows(expenseTotalEntry);

            indexValue.total = dashboardExpenseBudgetTotalItemList[0].count_total

            BigDecimal showtotalPriceWithoutVat = new BigDecimal(indexValue.totalPriceWithoutVat)
            indexValue.totalPriceWithoutVat = String.format("%.2f", showtotalPriceWithoutVat)

            BigDecimal showtotalPriceWithVat = new BigDecimal(indexValue.totalPriceWithVat)
            indexValue.totalPriceWithVat = String.format("%.2f", showtotalPriceWithVat)

            dashboardExpenseBudgetItemListFinal << indexValue
        }

        String countQuery = """SELECT  COUNT(a.id) AS totalBudgetItem FROM budget_item_expense AS a,budget_item_expense_details AS b,chart_master AS c, system_prefix AS sp WHERE b.budget_item_expense_id=a.id AND b.gl_account=c.account_code AND a.vendor_id= ${vendorId} AND a.booking_period_start_month <= ${bookingPeriod} AND a.booking_period_start_year<=${fiscalYearId} AND a.booking_period_end_month>=${bookingPeriod} AND a.booking_period_end_year>=${fiscalYearId} AND sp.id=6""";
        List<GroovyRowResult> count_result = db.rows(countQuery)
        int total = count_result[0].totalBudgetItem
        db.close()
        return [dashboardExpenseBudgetItemList: dashboardExpenseBudgetItemListFinal, count: total]
    }

    public LinkedHashMap listOfBudgetItemSearch(int offset, String limit, String sortItem, String sortOrder, String bookingPeriod, String fiscalYearId, String vendorId, String searchItem, String searchString) {
        //def db = new Sql(dataSource)
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance( companyConfig.serverUrl+companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)


        String queryString = "LOWER('%" + searchString + "%')"

        String searchBy = ""

        if (searchItem == "GLAccount") {
            searchBy = "AND LOWER(CONCAT(b.gl_account,'-',c.account_name))"
        }
        else if (searchItem == "BudgetItemCode") {
            searchBy = "AND LOWER(CONCAT(sp.prefix,'-',a.budget_id))"
        }

        String expenseEntry = """SELECT a.id as invoiceExpenseId, CONCAT(b.gl_account,'-',c.account_name) as glAccountName, CONCAT(sp.prefix,'-',a.budget_id) as budgetItemID,DATE_FORMAT(a.created_date,'%d-%m-%Y') as createdDate, b.total_price_without_vat as totalPriceWithoutVat, b.total_price_with_vat as totalPriceWithVat, a.STATUS AS total,b.gl_account AS tempGLAccount, b.id AS detailsID FROM budget_item_expense AS a,budget_item_expense_details AS b,chart_master AS c, system_prefix AS sp WHERE b.budget_item_expense_id=a.id AND b.gl_account=c.account_code AND a.vendor_id= ${vendorId} AND a.booking_period_start_month <= ${bookingPeriod} AND a.booking_period_start_year<=${fiscalYearId} AND a.booking_period_end_month>=${bookingPeriod} AND a.booking_period_end_year>=${fiscalYearId} ${searchBy} LIKE ${queryString} AND sp.id=6
        ORDER BY a.id LIMIT ${limit} OFFSET ${offset} """;
        List<GroovyRowResult> dashboardExpenseBudgetItemList = db.rows(expenseEntry);

        ArrayList dashboardExpenseBudgetItemListFinal = new ArrayList()
        dashboardExpenseBudgetItemList.eachWithIndex {indexValue, keyValue ->
            String expenseTotalEntry = """SELECT COUNT(DISTINCT(a.id)) AS count_total FROM invoice_expense AS a LEFT JOIN invoice_expense_details AS b ON a.id=b.invoice_id WHERE a.budget_item_expense_id=${indexValue.invoiceExpenseId} AND b.account_code='${indexValue.tempGLAccount}'""";
            List<GroovyRowResult> dashboardExpenseBudgetTotalItemList = db.rows(expenseTotalEntry);

            indexValue.total = dashboardExpenseBudgetTotalItemList[0].count_total

            BigDecimal showtotalPriceWithoutVat = new BigDecimal(indexValue.totalPriceWithoutVat)
            indexValue.totalPriceWithoutVat = "${showtotalPriceWithoutVat.setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()}"

            BigDecimal showtotalPriceWithVat = new BigDecimal(indexValue.totalPriceWithVat)
            indexValue.totalPriceWithVat = "${showtotalPriceWithVat.setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()}"

            dashboardExpenseBudgetItemListFinal << indexValue
        }

        String countQuery = """SELECT  COUNT(a.id) AS totalBudgetItem FROM budget_item_expense AS a,budget_item_expense_details AS b,chart_master AS c, system_prefix AS sp WHERE b.budget_item_expense_id=a.id AND b.gl_account=c.account_code AND a.vendor_id= ${vendorId} AND a.booking_period_start_month <= ${bookingPeriod} AND a.booking_period_start_year<=${fiscalYearId} AND a.booking_period_end_month>=${bookingPeriod} AND a.booking_period_end_year>=${fiscalYearId} ${searchBy} LIKE ${queryString} AND sp.id=6""";
        List<GroovyRowResult> count_result = db.rows(countQuery)
        int total = count_result[0].totalBudgetItem
        db.close();
        return [dashboardExpenseBudgetItemList: dashboardExpenseBudgetItemListFinal, count: total]
    }



    public LinkedHashMap listOfEntryBudgetItem(String bookingPeriod, String fiscalYearId, String vendorId) {
        //def db = new Sql(dataSource)
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance( companyConfig.serverUrl+companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)


        String wherePostCondition = ""

//        if (bookingPeriod) { wherePostCondition = wherePostCondition + " AND a.booking_period_start_month=" + bookingPeriod }
        if (fiscalYearId) { wherePostCondition = wherePostCondition + " AND a.booking_period_start_year=" + fiscalYearId }
        if (vendorId) { wherePostCondition = wherePostCondition + " AND a.vendor_id=" + vendorId }


        String expenseEntry = """SELECT a.id,CONCAT(sp.prefix,'-',a.budget_id) AS budgetCode,a.vendor_id,CONCAT(vm.vendor_name,' [',spvm.prefix,'-',vm.vendor_code,']') AS budgetItemName,a.booking_period_start_month,a.booking_period_start_year,CONCAT((CASE a.booking_period_start_month WHEN '1' THEN 'Jan' WHEN '2' THEN 'Feb' WHEN '3' THEN 'Mar' WHEN '4' THEN 'Apr' WHEN '5' THEN 'May' WHEN '6' THEN 'Jun' WHEN '7' THEN 'Jul' WHEN '8' THEN 'Aug' WHEN '9' THEN 'Sep' WHEN '10' THEN 'Oct' WHEN '11' THEN 'Nov' WHEN '12' THEN 'Dec' END),'-',a.booking_period_start_year) AS bookStartPeriod,a.booking_period_end_month,a.booking_period_end_year,CONCAT((CASE a.booking_period_end_month WHEN '1' THEN 'Jan' WHEN '2' THEN 'Feb' WHEN '3' THEN 'Mar' WHEN '4' THEN 'Apr' WHEN '5' THEN 'May' WHEN '6' THEN 'Jun' WHEN '7' THEN 'Jul' WHEN '8' THEN 'Aug' WHEN '9' THEN 'Sep' WHEN '10' THEN 'Oct' WHEN '11' THEN 'Nov' WHEN '12' THEN 'Dec' END),'-',a.booking_period_end_year) AS bookEndPeriod,CAST(a.total_gl_amount AS CHAR) AS totalGlAmount ,CAST(a.total_vat AS CHAR) AS totalVat FROM budget_item_expense AS a,system_prefix AS sp,vendor_master AS vm,system_prefix AS spvm WHERE sp.id=6 AND a.vendor_id=vm.id AND spvm.id=2 ${wherePostCondition}""";
        List<GroovyRowResult> dashboardExpenseBudgetItemList = db.rows(expenseEntry);

        String countQuery = """SELECT  COUNT(a.id) AS totalBudgetItem FROM budget_item_expense AS a,system_prefix AS sp,vendor_master AS vm,system_prefix AS spvm WHERE sp.id=6 AND a.vendor_id=vm.id AND spvm.id=2 ${wherePostCondition} """;
        List<GroovyRowResult> count_result = db.rows(countQuery)
        int total = count_result[0].totalBudgetItem
        db.close()
        return [dashboardExpenseBudgetItemList: dashboardExpenseBudgetItemList, count: total]
    }

    public LinkedHashMap listOfEntryBudgetItemSearch(int offset, String limit, String sortItem, String sortOrder, String bookingPeriod, String fiscalYearId, String vendorId, String searchType, String searchByVendorName,String glAccountSearch,searchMonth,searchYear) {
        //def db = new Sql(dataSource)
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance( companyConfig.serverUrl+companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String queryString = "LOWER('%" + searchByVendorName + "%')"

        println searchMonth
        println searchYear

        String searchBy = ""

        String wherePostCondition = ""

        if (bookingPeriod) { wherePostCondition = wherePostCondition + " AND a.booking_period_start_month=" + bookingPeriod }
        if (fiscalYearId) { wherePostCondition = wherePostCondition + " AND a.booking_period_start_year=" + fiscalYearId }
        if (vendorId) { wherePostCondition = wherePostCondition + " AND a.vendor_id=" + vendorId }

        String expenseEntry
        List<GroovyRowResult> dashboardExpenseBudgetItemList
        String countQuery

        if(searchByVendorName){
            expenseEntry = "SELECT a.id,CONCAT(sp.prefix,'-',a.budget_id) AS budgetCode,a.vendor_id,CONCAT(vm.vendor_name,' [',spvm.prefix,'-',vm.vendor_code,']') AS budgetItemName," +
                    "a.booking_period_start_month,a.booking_period_start_year,CONCAT((CASE a.booking_period_start_month WHEN '1' THEN 'Jan' WHEN '2' THEN 'Feb' WHEN '3' THEN 'Mar' " +
                    "WHEN '4' THEN 'Apr' WHEN '5' THEN 'May' WHEN '6' THEN 'Jun' WHEN '7' THEN 'Jul' WHEN '8' THEN 'Aug' WHEN '9' THEN 'Sep' WHEN '10' THEN 'Oct' WHEN '11' THEN 'Nov' " +
                    "WHEN '12' THEN 'Dec' END),'-',a.booking_period_start_year) AS bookStartPeriod,a.booking_period_end_month,a.booking_period_end_year,CONCAT((CASE a.booking_period_end_month " +
                    "WHEN '1' THEN 'Jan' WHEN '2' THEN 'Feb' WHEN '3' THEN 'Mar' WHEN '4' THEN 'Apr' WHEN '5' THEN 'May' WHEN '6' THEN 'Jun' WHEN '7' THEN 'Jul' WHEN '8' THEN 'Aug' WHEN '9' THEN 'Sep'" +
                    " WHEN '10' THEN 'Oct' WHEN '11' THEN 'Nov' WHEN '12' THEN 'Dec' END),'-',a.booking_period_end_year) AS bookEndPeriod,a.total_gl_amount AS totalGlAmount ,a.total_vat AS totalVat " +
                    "FROM budget_item_expense AS a,system_prefix AS sp,vendor_master AS vm,system_prefix AS spvm WHERE sp.id=6 AND a.vendor_id=vm.id AND spvm.id=2 AND vm.vendor_name like '%"+searchByVendorName+"%' ${wherePostCondition} ORDER BY ${sortItem} ${sortOrder}  LIMIT ${limit} OFFSET ${offset}";

            dashboardExpenseBudgetItemList = db.rows(expenseEntry);

            countQuery = "SELECT  COUNT(a.id) AS totalBudgetItem FROM budget_item_expense AS a,system_prefix AS sp,vendor_master AS vm," +
                    "system_prefix AS spvm WHERE sp.id=6 AND a.vendor_id=vm.id AND spvm.id=2 AND vm.vendor_name like '%" + searchByVendorName + "%' ${wherePostCondition}  ";
        }

        else if(glAccountSearch){

            expenseEntry = "SELECT a.id,CONCAT(sp.prefix,'-',a.budget_id) AS budgetCode,a.vendor_id,CONCAT(vm.vendor_name,' [',spvm.prefix,'-',vm.vendor_code,']') AS budgetItemName," +
                    "a.booking_period_start_month,a.booking_period_start_year,CONCAT((CASE a.booking_period_start_month WHEN '1' THEN 'Jan' WHEN '2' THEN 'Feb' WHEN '3' THEN 'Mar' " +
                    "WHEN '4' THEN 'Apr' WHEN '5' THEN 'May' WHEN '6' THEN 'Jun' WHEN '7' THEN 'Jul' WHEN '8' THEN 'Aug' WHEN '9' THEN 'Sep' WHEN '10' THEN 'Oct' WHEN '11' THEN 'Nov' " +
                    "WHEN '12' THEN 'Dec' END),'-',a.booking_period_start_year) AS bookStartPeriod,a.booking_period_end_month,a.booking_period_end_year,CONCAT((CASE a.booking_period_end_month " +
                    "WHEN '1' THEN 'Jan' WHEN '2' THEN 'Feb' WHEN '3' THEN 'Mar' WHEN '4' THEN 'Apr' WHEN '5' THEN 'May' WHEN '6' THEN 'Jun' WHEN '7' THEN 'Jul' WHEN '8' THEN 'Aug' WHEN '9' THEN 'Sep'" +
                    " WHEN '10' THEN 'Oct' WHEN '11' THEN 'Nov' WHEN '12' THEN 'Dec' END),'-',a.booking_period_end_year) AS bookEndPeriod,a.total_gl_amount AS totalGlAmount ,a.total_vat AS totalVat " +
                    "FROM budget_item_expense AS a,budget_item_expense_details AS d,system_prefix AS sp,vendor_master AS vm,system_prefix AS spvm WHERE sp.id=6 AND a.vendor_id=vm.id AND a.id=d.budget_item_expense_id AND spvm.id=2 AND d.gl_account like '%"+glAccountSearch+"%' ${wherePostCondition} ORDER BY ${sortItem} ${sortOrder}  LIMIT ${limit} OFFSET ${offset}";

            dashboardExpenseBudgetItemList = db.rows(expenseEntry);

            countQuery = "SELECT  COUNT(a.id) AS totalBudgetItem FROM budget_item_expense AS a,budget_item_expense_details AS d,system_prefix AS sp,vendor_master AS vm," +
                    "system_prefix AS spvm WHERE sp.id=6 AND a.vendor_id=vm.id AND a.id=d.budget_item_expense_id AND spvm.id=2 AND d.gl_account like '%" + glAccountSearch + "%' ${wherePostCondition}  ";

        }

        else if(searchMonth && searchYear){

            expenseEntry = "SELECT a.id,CONCAT(sp.prefix,'-',a.budget_id) AS budgetCode,a.vendor_id,CONCAT(vm.vendor_name,' [',spvm.prefix,'-',vm.vendor_code,']') AS budgetItemName," +
                    "a.booking_period_start_month,a.booking_period_start_year,CONCAT((CASE a.booking_period_start_month WHEN '1' THEN 'Jan' WHEN '2' THEN 'Feb' WHEN '3' THEN 'Mar' " +
                    "WHEN '4' THEN 'Apr' WHEN '5' THEN 'May' WHEN '6' THEN 'Jun' WHEN '7' THEN 'Jul' WHEN '8' THEN 'Aug' WHEN '9' THEN 'Sep' WHEN '10' THEN 'Oct' WHEN '11' THEN 'Nov' " +
                    "WHEN '12' THEN 'Dec' END),'-',a.booking_period_start_year) AS bookStartPeriod,a.booking_period_end_month,a.booking_period_end_year,CONCAT((CASE a.booking_period_end_month " +
                    "WHEN '1' THEN 'Jan' WHEN '2' THEN 'Feb' WHEN '3' THEN 'Mar' WHEN '4' THEN 'Apr' WHEN '5' THEN 'May' WHEN '6' THEN 'Jun' WHEN '7' THEN 'Jul' WHEN '8' THEN 'Aug' WHEN '9' THEN 'Sep'" +
                    " WHEN '10' THEN 'Oct' WHEN '11' THEN 'Nov' WHEN '12' THEN 'Dec' END),'-',a.booking_period_end_year) AS bookEndPeriod,a.total_gl_amount AS totalGlAmount ,a.total_vat AS totalVat " +
                    "FROM budget_item_expense AS a,budget_item_expense_details AS d,system_prefix AS sp,vendor_master AS vm,system_prefix AS spvm WHERE sp.id=6 AND a.vendor_id=vm.id AND a.id=d.budget_item_expense_id AND spvm.id=2 AND a.booking_period_start_month ='"+searchMonth+"' AND a.booking_period_start_year ='"+searchYear+"'  ${wherePostCondition} ORDER BY ${sortItem} ${sortOrder}  LIMIT ${limit} OFFSET ${offset}";

            dashboardExpenseBudgetItemList = db.rows(expenseEntry);

            countQuery = "SELECT  COUNT(a.id) AS totalBudgetItem FROM budget_item_expense AS a,budget_item_expense_details AS d,system_prefix AS sp,vendor_master AS vm," +
                    "system_prefix AS spvm WHERE sp.id=6 AND a.vendor_id=vm.id AND a.id=d.budget_item_expense_id AND spvm.id=2 AND a.booking_period_start_month ='"+searchMonth+"' AND a.booking_period_start_year ='"+searchYear+"'  ${wherePostCondition}  ";


        }
        List<GroovyRowResult> count_result = db.rows(countQuery)
        int total = count_result[0].totalBudgetItem
        return [dashboardExpenseBudgetItemList: dashboardExpenseBudgetItemList, count: total]
    }
    /*
   * Expense Invoice list -> from all budget item list show
   *
   * */

    public LinkedHashMap listOfExpenseInvoice(String bookingPeriod, String fiscalYearId, String vendorId, String bookInvoiceId) {
        //def db = new Sql(dataSource)
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance( companyConfig.serverUrl+companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String expenseInvoice = """SELECT a.id as id, CONCAT(sp2.prefix,'-',a.invoice_no) AS invoiceNumber,
                                a.booking_period AS bookingPeriod,a.vendor_id AS vendorId, DATE_FORMAT(a.trans_date,'%d-%m-%Y') AS invoiceDate,
                                a.payment_ref AS paymentRef, a.total_gl_amount AS totalAmountIncVat, a.total_vat AS totalVat,
                                CONCAT(iv.vendor_name,' [',sp.prefix,'-',iv.vendor_code,']') AS vendorName FROM invoice_expense AS a,
                                vendor_master AS v,vendor_master AS iv, system_prefix AS sp, system_prefix AS sp1,
                                system_prefix AS sp2  WHERE a.budget_vendor_id= v.id  AND a.vendor_id= iv.id
                                AND a.booking_year=${fiscalYearId} AND sp.id=2 AND sp1.id=6 AND sp2.id=7 AND a.is_book_receive=0  """;

        List<GroovyRowResult> expenseInvoiceList = db.rows(expenseInvoice);
        int total = expenseInvoiceList.size();

        db.close();

        return [expenseInvoiceList: expenseInvoiceList, count: total]
    }

    /**
     * For Private process amount list
     * */
    public LinkedHashMap listOfPrivateProcess(def bookingYear, def bookingPeriod, def budgetMasterId) {
        //def db = new Sql(dataSource)
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance( companyConfig.serverUrl+companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String processAmountSql = """SELECT prst.id as id, prst.amount as amount,prst.booking_period as bookingPeriod,
                                    prst.bank_payment_id as bankId, pbm.budget_name as budgetName,pbm.id as budgetMasterId,
                                    bsidf.description as description,DATE_FORMAT(bsidf.trans_date_time,'%d-%m-%Y') as transDate
                                    from private_reservation_spending_trans AS prst
                                    INNER JOIN bank_statement_import_details_final AS bsidf ON prst.bank_payment_id = bsidf.bank_payment_id
                                    INNER JOIN private_budget_master AS pbm ON prst.budget_master_id = pbm.id
                                    WHERE prst.booking_period = '${bookingPeriod}' AND
                                    prst.booking_year = '${bookingYear}' and prst.budget_master_id = '${budgetMasterId}'""";

        List<GroovyRowResult> privateProcessList = db.rows(processAmountSql);
        int total = privateProcessList.size();

        db.close();

        return [privateProcessList: privateProcessList, count: total]
    }

    public LinkedHashMap listOfReservationInvoice(def bookingYear, def bookingPeriod, def budgetMasterId) {
        //def db = new Sql(dataSource)
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance( companyConfig.serverUrl+companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String processAmountSql = """SELECT prst.id as id, prst.amount as amount,prst.booking_period as bookingPeriod,
                                    prst.bank_payment_id as bankId, rbm.reservation_name as budgetName,rbm.id as budgetMasterId,
                                    bsidf.description as description,DATE_FORMAT(bsidf.trans_date_time,'%d-%m-%Y') as transDate
                                    from private_reservation_spending_trans AS prst
                                    INNER JOIN bank_statement_import_details_final AS bsidf ON prst.bank_payment_id = bsidf.id
                                    INNER JOIN reservation_budget_master AS rbm ON prst.budget_master_id = rbm.id
                                    WHERE prst.booking_period = '${bookingPeriod}' AND
                                    prst.booking_year = '${bookingYear}' and prst.budget_master_id = '${budgetMasterId}'""";

        List<GroovyRowResult> reservationInvoiceList = db.rows(processAmountSql);
        int total = reservationInvoiceList.size();

        db.close()

        return [reservationInvoiceList: reservationInvoiceList, count: total]
    }

    public LinkedHashMap listOfReceipt( String bookingPeriod, String fiscalYearId, String vendorId, String bookInvoiceId) {

        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance( companyConfig.serverUrl+companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)


        String expenseInvoice = """SELECT a.id as id, CONCAT(sp2.prefix,'-',a.invoice_no) AS invoiceNumber,a.vendor_id AS vendorId, a.booking_period AS bookingPeriod, DATE_FORMAT(a.trans_date,'%d-%m-%Y') AS invoiceDate, a.payment_ref AS paymentRef, a.total_gl_amount AS totalAmountIncVat, a.total_vat AS totalVat,CONCAT(iv.vendor_name,' [',sp.prefix,'-',iv.vendor_code,']') AS vendorName,ied.shop_info as shopId FROM invoice_expense AS a,invoice_expense_details AS ied,budget_item_expense AS b, vendor_master AS v,vendor_master AS iv, system_prefix AS sp, system_prefix AS sp1, system_prefix AS sp2  WHERE a.budget_item_expense_id=b.id AND ied.invoice_id = a.id AND a.budget_vendor_id= v.id  AND a.vendor_id= iv.id AND a.is_book_receive=1 AND a.booking_year=${fiscalYearId}  AND a.budget_vendor_id= ${vendorId}  AND sp.id=2 AND sp1.id=6 AND sp2.id=12 group by a.id """;
        List<GroovyRowResult> expenseInvoiceList = db.rows(expenseInvoice);

        int total = expenseInvoiceList.size()
        db.close();
        return [expenseInvoiceList: expenseInvoiceList, count: total]
    }

    public LinkedHashMap listOfExpenseInvoiceSearch(int offset, String limit, String sortItem, String sortOrder, String bookingPeriod, String fiscalYearId, String vendorId, String bookInvoiceId,String searchByVendorName,String searchByPaymentReference,String searchMonth,String searchYear) {

        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance( companyConfig.serverUrl+companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String queryString = "LOWER('%" + searchByVendorName + "%')"
        String wherePostCondition = ""

        if (fiscalYearId) {
            wherePostCondition = wherePostCondition + " AND a.booking_year=" + fiscalYearId
        }

        String expenseInvoice
        List<GroovyRowResult> expenseInvoiceList
        String countQuery

        if(searchByVendorName){
            expenseInvoice ="SELECT a.id as id, CONCAT(sp2.prefix,'-',a.invoice_no) AS invoiceNumber, a.booking_period AS bookingPeriod,a.vendor_id AS vendorId,v.vendor_name AS vendorName,  " +
                    " CONCAT(v.vendor_name,' [',sp.prefix,'-',v.vendor_code,']') AS budgetItemName, DATE_FORMAT(a.trans_date,'%d-%m-%Y') AS invoiceDate, a.payment_ref AS paymentRef, " +
                    "a.total_gl_amount AS totalAmountIncVat, a.total_vat AS totalVat,CONCAT(v.vendor_name,' [',sp.prefix,'-',v.vendor_code,']') AS InvoiceVendorName, " +
                    "a.budget_item_expense_id,a.budget_vendor_id FROM invoice_expense AS a,budget_item_expense AS b, vendor_master AS v,system_prefix AS sp, system_prefix AS sp1, system_prefix AS sp2  " +
                    "WHERE a.budget_item_expense_id=b.id AND a.vendor_id=v.id  AND a.booking_year=${fiscalYearId}  " +
                    " AND sp.id=2 AND sp1.id=6 AND sp2.id=7 AND a.is_book_receive=0 AND v.vendor_name like '%" +searchByVendorName+ "%' ORDER BY a.id DESC LIMIT ${limit} OFFSET ${offset} " ;

            expenseInvoiceList = db.rows(expenseInvoice);

            countQuery = "SELECT  COUNT(a.id) AS totalBudgetItem FROM invoice_expense AS a,budget_item_expense AS b, vendor_master AS v, system_prefix AS sp, " +
                    "system_prefix AS sp1, system_prefix AS sp2  WHERE a.budget_item_expense_id=b.id AND a.vendor_id=v.id  " +
                    "AND a.booking_year=${fiscalYearId}  AND sp.id=2 AND sp1.id=6 AND sp2.id=7 AND a.is_book_receive=0 AND v.vendor_name like '%" +searchByVendorName+ "%' " ;
        }

        else if(searchByPaymentReference){

            expenseInvoice ="SELECT a.id as id, CONCAT(sp2.prefix,'-',a.invoice_no) AS invoiceNumber, a.booking_period AS bookingPeriod,a.vendor_id AS vendorId,v.vendor_name AS vendorName,  " +
                    " CONCAT(v.vendor_name,' [',sp.prefix,'-',v.vendor_code,']') AS budgetItemName, DATE_FORMAT(a.trans_date,'%d-%m-%Y') AS invoiceDate, a.payment_ref AS paymentRef, " +
                    "a.total_gl_amount AS totalAmountIncVat, a.total_vat AS totalVat,CONCAT(v.vendor_name,' [',sp.prefix,'-',v.vendor_code,']') AS InvoiceVendorName, " +
                    " a.budget_item_expense_id,a.budget_vendor_id FROM invoice_expense AS a,budget_item_expense AS b, vendor_master AS v,system_prefix AS sp, system_prefix AS sp1, system_prefix AS sp2  " +
                    "WHERE a.budget_item_expense_id=b.id AND a.vendor_id=v.id  AND a.booking_year=${fiscalYearId}  " +
                    " AND sp.id=2 AND sp1.id=6 AND sp2.id=7 AND a.is_book_receive=0 AND a.payment_ref= '" +searchByPaymentReference+ "' ORDER BY a.id DESC LIMIT ${limit} OFFSET ${offset} " ;

            expenseInvoiceList = db.rows(expenseInvoice);

            countQuery = "SELECT  COUNT(a.id) AS totalBudgetItem FROM invoice_expense AS a,budget_item_expense AS b, vendor_master AS v, system_prefix AS sp, " +
                    "system_prefix AS sp1, system_prefix AS sp2  WHERE a.budget_item_expense_id=b.id AND a.vendor_id=v.id  " +
                    "AND a.booking_year=${fiscalYearId}  AND sp.id=2 AND sp1.id=6 AND sp2.id=7 AND a.is_book_receive=0 AND a.payment_ref= '" +searchByPaymentReference+ "' " ;

        }

        else if(searchMonth && searchYear){

            expenseInvoice ="SELECT a.id as id, CONCAT(sp2.prefix,'-',a.invoice_no) AS invoiceNumber, a.booking_period AS bookingPeriod,a.vendor_id AS vendorId,v.vendor_name AS vendorName,  " +
                    " CONCAT(v.vendor_name,' [',sp.prefix,'-',v.vendor_code,']') AS budgetItemName, DATE_FORMAT(a.trans_date,'%d-%m-%Y') AS invoiceDate, a.payment_ref AS paymentRef, " +
                    "a.total_gl_amount AS totalAmountIncVat, a.total_vat AS totalVat,CONCAT(v.vendor_name,' [',sp.prefix,'-',v.vendor_code,']') AS InvoiceVendorName, " +
                    " a.budget_item_expense_id,a.budget_vendor_id FROM invoice_expense AS a,budget_item_expense AS b, vendor_master AS v,system_prefix AS sp, system_prefix AS sp1, system_prefix AS sp2  " +
                    "WHERE a.budget_item_expense_id=b.id AND a.vendor_id=v.id  AND a.booking_year=${fiscalYearId}  " +
                    " AND sp.id=2 AND sp1.id=6 AND sp2.id=7 AND a.is_book_receive=0 AND a.booking_period= '" +searchMonth+ "' AND a.booking_year= '" +searchYear+ "' ORDER BY a.id DESC LIMIT ${limit} OFFSET ${offset} " ;

            expenseInvoiceList = db.rows(expenseInvoice);

            countQuery = "SELECT  COUNT(a.id) AS totalBudgetItem FROM invoice_expense AS a,budget_item_expense AS b, vendor_master AS v, system_prefix AS sp, " +
                    "system_prefix AS sp1, system_prefix AS sp2  WHERE a.budget_item_expense_id=b.id AND a.vendor_id=v.id  " +
                    "AND a.booking_year=${fiscalYearId}  AND sp.id=2 AND sp1.id=6 AND sp2.id=7 AND a.is_book_receive=0 AND a.booking_period= '" +searchMonth+ "' AND a.booking_year= '" +searchYear+ "' " ;

        }

        List<GroovyRowResult> count_result = db.rows(countQuery)
        int total = count_result[0].totalBudgetItem

        return [expenseInvoiceList: expenseInvoiceList, count: total]
    }

    public LinkedHashMap listOfReceiptSearch(int offset, String limit, String sortItem, String sortOrder, String bookingPeriod, String fiscalYearId, String vendorId, String bookInvoiceId,String searchByVendorName,String searchByPaymentReference,String searchMonth,String searchYear) {
        //def db = new Sql(dataSource)
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance( companyConfig.serverUrl+companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)
        String expenseInvoice
        List<GroovyRowResult> expenseInvoiceList
        String countQuery

        if(searchByVendorName){
            expenseInvoice ="SELECT a.id as id, CONCAT(sp2.prefix,'-',a.invoice_no) AS invoiceNumber, a.booking_period AS bookingPeriod,v.vendor_name AS vendorName,a.vendor_id AS vendorId, " +
                    "CONCAT(v.vendor_name,' [',sp.prefix,'-',v.vendor_code,']') AS budgetItemName, DATE_FORMAT(a.trans_date,'%d-%m-%Y') AS invoiceDate, a.payment_ref AS paymentRef," +
                    " a.total_gl_amount AS totalAmountIncVat, a.total_vat AS totalVat,CONCAT(v.vendor_name,' [',sp.prefix,'-',v.vendor_code,']') AS InvoiceVendorName, " +
                    " a.budget_item_expense_id,a.budget_vendor_id,ied.shop_info as shopId   FROM invoice_expense AS a,invoice_expense_details as ied,budget_item_expense AS b, vendor_master AS v, system_prefix AS sp, system_prefix AS sp1, system_prefix AS sp2  " +
                    " WHERE a.budget_item_expense_id=b.id AND ied.invoice_id = a.id AND a.vendor_id=v.id AND a.booking_year=${fiscalYearId}   AND sp.id=2 AND sp1.id=6 AND sp2.id=12  " +
                    " AND a.is_book_receive=1 AND v.vendor_name like '%" +searchByVendorName+ "%' ORDER BY a.id DESC LIMIT ${limit} OFFSET ${offset}";

            expenseInvoiceList = db.rows(expenseInvoice);

            countQuery ="SELECT  COUNT(a.id) AS totalBudgetItem FROM invoice_expense AS a,invoice_expense_details as ied,budget_item_expense AS b, vendor_master AS v, " +
                    "system_prefix AS sp, system_prefix AS sp1, system_prefix AS sp2  WHERE a.budget_item_expense_id=b.id AND ied.invoice_id = a.id AND a.vendor_id=v.id  " +
                    "  AND a.booking_year=${fiscalYearId} AND sp.id=2 AND sp1.id=6 AND sp2.id=12  AND a.is_book_receive=1 AND v.vendor_name like '%" +searchByVendorName+ "%' ";
        }
        else if(searchByPaymentReference){

            expenseInvoice ="SELECT a.id as id, CONCAT(sp2.prefix,'-',a.invoice_no) AS invoiceNumber, a.booking_period AS bookingPeriod,v.vendor_name AS vendorName,a.vendor_id AS vendorId, " +
                    "CONCAT(v.vendor_name,' [',sp.prefix,'-',v.vendor_code,']') AS budgetItemName, DATE_FORMAT(a.trans_date,'%d-%m-%Y') AS invoiceDate, a.payment_ref AS paymentRef," +
                    " a.total_gl_amount AS totalAmountIncVat, a.total_vat AS totalVat,CONCAT(v.vendor_name,' [',sp.prefix,'-',v.vendor_code,']') AS InvoiceVendorName, " +
                    " a.budget_item_expense_id,a.budget_vendor_id,ied.shop_info as shopId  FROM invoice_expense AS a,invoice_expense_details as ied,budget_item_expense AS b, vendor_master AS v, system_prefix AS sp, system_prefix AS sp1, system_prefix AS sp2  " +
                    " WHERE a.budget_item_expense_id=b.id AND ied.invoice_id = a.id  AND a.vendor_id=v.id AND a.booking_year=${fiscalYearId}   AND sp.id=2 AND sp1.id=6 AND sp2.id=12  " +
                    " AND a.is_book_receive=1 AND a.payment_ref= '" +searchByPaymentReference+ "' ORDER BY a.id DESC LIMIT ${limit} OFFSET ${offset}";

            expenseInvoiceList = db.rows(expenseInvoice);

            countQuery ="SELECT  COUNT(a.id) AS totalBudgetItem FROM invoice_expense AS a,invoice_expense_details as ied,budget_item_expense AS b, vendor_master AS v, " +
                    "system_prefix AS sp, system_prefix AS sp1, system_prefix AS sp2  WHERE a.budget_item_expense_id=b.id AND ied.invoice_id = a.id AND a.vendor_id=v.id  " +
                    "  AND a.booking_year=${fiscalYearId} AND sp.id=2 AND sp1.id=6 AND sp2.id=12  AND a.is_book_receive=1 AND a.payment_ref= '" +searchByPaymentReference+ "' ";
        }
        else if(searchMonth && searchYear){

            expenseInvoice ="SELECT a.id as id, CONCAT(sp2.prefix,'-',a.invoice_no) AS invoiceNumber, a.booking_period AS bookingPeriod,v.vendor_name AS vendorName,a.vendor_id AS vendorId, " +
                    "CONCAT(v.vendor_name,' [',sp.prefix,'-',v.vendor_code,']') AS budgetItemName, DATE_FORMAT(a.trans_date,'%d-%m-%Y') AS invoiceDate, a.payment_ref AS paymentRef," +
                    " a.total_gl_amount AS totalAmountIncVat, a.total_vat AS totalVat,CONCAT(v.vendor_name,' [',sp.prefix,'-',v.vendor_code,']') AS InvoiceVendorName, " +
                    " a.budget_item_expense_id, a.budget_vendor_id,ied.shop_info as shopId FROM invoice_expense AS a,invoice_expense_details as ied,budget_item_expense AS b, vendor_master AS v, system_prefix AS sp, system_prefix AS sp1, system_prefix AS sp2  " +
                    " WHERE a.budget_item_expense_id=b.id AND ied.invoice_id = a.id  AND a.vendor_id=v.id AND a.booking_year=${fiscalYearId}   AND sp.id=2 AND sp1.id=6 AND sp2.id=12  " +
                    " AND a.is_book_receive=1 AND a.booking_period= '" +searchMonth+ "' AND a.booking_year= '" +searchYear+ "' ORDER BY a.id DESC LIMIT ${limit} OFFSET ${offset}";

            expenseInvoiceList = db.rows(expenseInvoice);

            countQuery ="SELECT  COUNT(a.id) AS totalBudgetItem FROM invoice_expense AS a,invoice_expense_details as ied,budget_item_expense AS b, vendor_master AS v, " +
                    "system_prefix AS sp, system_prefix AS sp1, system_prefix AS sp2  WHERE a.budget_item_expense_id=b.id  AND ied.invoice_id = a.id AND  a.vendor_id=v.id  " +
                    "  AND a.booking_year=${fiscalYearId} AND sp.id=2 AND sp1.id=6 AND sp2.id=12  AND a.is_book_receive=1 AND a.booking_period= '" +searchMonth+ "' AND a.booking_year= '" +searchYear+ "' ";

        }
        List<GroovyRowResult> count_result = db.rows(countQuery)
        int total = count_result[0].totalBudgetItem
        db.close();
        return [expenseInvoiceList: expenseInvoiceList, count: total]
    }


    public LinkedHashMap receiptEntrySearch(int offset, String limit, String sortItem, String sortOrder, String searchItem, String searchString) {
        //def db = new Sql(dataSource)
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance( companyConfig.serverUrl+companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String queryString = "%" + searchString + "%"
        String expenseEntry = """SELECT id, invoice_number, vendor_id, trans_date, created_date, total_gl_amount, total_vat FROM receipt_entry WHERE ${searchItem} LIKE '${queryString}'  ORDER BY ${sortItem} ${sortOrder} LIMIT ${limit} OFFSET ${offset}  """;
        List<GroovyRowResult> expenseEntryList = db.rows(expenseEntry);

        String countQuery = """SELECT count(id) as count FROM receipt_entry""";
        List<GroovyRowResult> count_result = db.rows(countQuery)
        int total = count_result[0].count
        db.close();
        return [expenseEntryList: expenseEntryList, count: total]
    }

    public def getReceiptSequenceNumber() {

        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance( companyConfig.serverUrl+companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String sequenceQuery = """SELECT max(id) as count FROM receipt_entry""";
        List<GroovyRowResult> count_result = db.rows(sequenceQuery)
        def currentSequence = count_result[0].count
        if (!currentSequence) {
            currentSequence = 1
        } else {
            currentSequence += 1
        }
        db.close()
        return currentSequence
    }

    public List<GroovyRowResult> entryDetailListOfReceipt(Long invoiceId) {
        //def db = new Sql(dataSource)
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance( companyConfig.serverUrl+companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String receiptDetailQuery = """SELECT account_code, amount, shop_id, vat_amount, vat_category_id, vat_rate FROM receipt_entry_details WHERE receipt_entry_id=${invoiceId}  """;
        //newly create by ashish //String receiptDetailQuery = """SELECT red.account_code, red.amount, red.shop_id, red.vat_amount, red.vat_category_id, red.vat_rate, CONCAT(red.account_code,'-',cm.account_name) AS glAccountName, CONCAT(vm.first_name,'[',(SELECT prefix FROM system_prefix WHERE id=2),'-',vm.vendor_code,']') AS vendorName FROM receipt_entry_details AS red LEFT JOIN vendor_master AS vm ON vm.id= red.shop_id LEFT JOIN chart_master AS cm ON cm.account_code = red.account_code WHERE red.receipt_entry_id=${invoiceId}  """;
        List<GroovyRowResult> receiptDetailList = db.rows(receiptDetailQuery);
        db.close();
        return receiptDetailList
    }

    /*
   * Invoice Expense edit information
   * */

    public List<GroovyRowResult> detailListOfExpenseInvoice(Long invoiceId) {
        //def db = new Sql(dataSource)
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance( companyConfig.serverUrl+companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String expenseInvoiceDetailQuery = """SELECT account_code, note, vat_category_id, total_amount_without_vat, total_amount_with_vat, vat_rate FROM invoice_expense_details WHERE invoice_id=${invoiceId}  """;
        List<GroovyRowResult> invoiceExpenseDetailList = db.rows(expenseInvoiceDetailQuery);
        db.close();
        return invoiceExpenseDetailList
    }

    /*
    * Function of Journal entry list
    * */
    def listOfJournalEntry(String fiscalYearId){

        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance( companyConfig.serverUrl+companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String wherePostCondition = ""
        //if (fiscalYearId) { wherePostCondition = wherePostCondition + "  AND EXTRACT(YEAR FROM a.trans_date) = " + fiscalYearId  }

        String journalEntry = """SELECT a.id, CONCAT(sp.prefix,'-',a.invoice_id) AS invoiceNumber, DATE_FORMAT(a.trans_date, '%Y-%m-%d')
                                AS transactionDate,SUM(b.amount) AS totlaAmount,a.comments as description FROM journal_entry AS a, journal_entry_details AS b,
                                system_prefix AS sp WHERE  sp.id=4  AND a.id = b.journal_entry_id AND b.amount>0 AND
                                STATUS = 1 ${wherePostCondition} GROUP BY b.journal_entry_id  """;

        List<GroovyRowResult> journalEntryList = db.rows(journalEntry);
        int total = journalEntryList.size()
        db.close();
        return [journalEntryList: journalEntryList, count: total]
    }

    /**
     * Function of Journal Entry Search
     * */

    public LinkedHashMap listOfJournalEntrySearch(int offset, String limit, String sortItem, String sortOrder, String fiscalYearId, String searchItem, String searchString) {
        //def db = new Sql(dataSource)
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance( companyConfig.serverUrl+companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String wherePostCondition = ""
        if (fiscalYearId) { wherePostCondition = wherePostCondition + "  AND EXTRACT(YEAR FROM a.trans_date) = " + fiscalYearId  }

        String queryString = "LOWER('%" + searchString + "%')"

        String searchBy = ""

        if (searchItem == "invoiceNumber") {
            searchBy = "AND LOWER(CONCAT(sp.prefix,'-',a.invoice_id))"
        }

        String journalEntry = """SELECT a.id, CONCAT(sp.prefix,'-',a.invoice_id) AS invoiceNumber, DATE_FORMAT(a.trans_date, '%Y-%m-%d') AS transactionDate, SUM(b.amount) AS totlaAmount FROM journal_entry AS a, journal_entry_details AS b, system_prefix AS sp WHERE  sp.id=4  AND a.id = b.journal_entry_id AND b.amount>0 AND STATUS = 1 ${wherePostCondition} ${searchBy} LIKE ${queryString}  GROUP BY b.journal_entry_id
        ORDER BY a.id DESC LIMIT ${limit} OFFSET ${offset} """;
        List<GroovyRowResult> journalEntryList = db.rows(journalEntry);

        String countQuery = """SELECT  COUNT(a.id) AS totalJournalEntry FROM journal_entry AS a, journal_entry_details AS b, system_prefix AS sp WHERE  sp.id=4  AND a.id = b.journal_entry_id AND b.amount>0 AND STATUS = 1 ${wherePostCondition} ${searchBy} LIKE ${queryString}""";
        List<GroovyRowResult> count_result = db.rows(countQuery)
        int total = count_result[0].totalJournalEntry
        db.close();
        return [journalEntryList: journalEntryList, count: total]
    }


    def listOfChartMasterEntry(){
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance( companyConfig.serverUrl+companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)
        String chartEntry = """SELECT a.id,a.account_code As accountCode,a.account_name AS accountName,b.name AS groupName,(CASE a.status WHEN 2 THEN 'Inactive' ELSE 'Active' END) AS showStatus,a.accountant_name As accountantName FROM chart_master AS a LEFT JOIN chart_group AS b ON a.chart_group_id=b.id  """;
        List<GroovyRowResult> chartMasterList = db.rows(chartEntry);
        int total = chartMasterList.size()
        db.close();
        return [chartMasterList: chartMasterList, count: total]
    }

    def listOfChartMasterEntrySearch(int offset, String limit, String sortItem, String sortOrder,String searchItem, String searchString) {
        //def db = new Sql(dataSource)
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance( companyConfig.serverUrl+companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String queryString = "LOWER('%" + searchString + "%')"
        String searchBy = ""
        if (searchItem == "accountCode") {
            searchBy = "WHERE LOWER(a.account_code)"
        }else if (searchItem == "accountName") {
            searchBy = "WHERE LOWER(a.account_name)"
        }else if (searchItem == "groupName") {
            searchBy = "WHERE LOWER(b.name)"
        }


        String chartEntry = """SELECT a.id,a.account_code As accountCode,a.account_name AS accountName,b.name AS groupName,(CASE a.status WHEN 2 THEN 'Inactive' ELSE 'Active' END) AS showStatus,a.accountant_name As accountantName FROM chart_master AS a LEFT JOIN chart_group AS b ON a.chart_group_id=b.id ${searchBy} LIKE ${queryString}
        ORDER BY ${sortItem} ${sortOrder} LIMIT ${limit} OFFSET ${offset} """;

        List<GroovyRowResult> chartMasterList = db.rows(chartEntry);

        String countQuery = """SELECT  COUNT(a.id) AS totalChartEntry FROM chart_master AS a LEFT JOIN chart_group AS b ON a.chart_group_id=b.id ${searchBy} LIKE ${queryString}""";
        List<GroovyRowResult> count_result = db.rows(countQuery)
        int total = count_result[0].totalChartEntry
        db.close();
        return [chartMasterList: chartMasterList, count: total]
    }

    /*
   * Function of Manual Reconciliation list
   * */
    def listOfManualReconcileEntry(int offset, String limit, String sortItem, String sortOrder, String fiscalYearId){
        //def db = new Sql(dataSource)
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance( companyConfig.serverUrl+companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String tempsortItem = "a.id"
        String tempsortOrder = "DESC"

        String wherePostCondition = ""
        if (fiscalYearId) { wherePostCondition = wherePostCondition + "  AND EXTRACT(YEAR FROM a.trans_date) = " + fiscalYearId  }

        String manualEntry = """SELECT a.id, CONCAT(sp.prefix,'-',a.invoice_no) AS invoiceNumber, DATE_FORMAT(a.trans_date, '%Y-%m-%d') AS transactionDate, a.paid_amount, ROUND((a.total_gl_amount + a.total_vat), 2) AS totalAmnt  FROM Invoice_expense AS a, system_prefix AS sp WHERE  sp.id=7  AND a.paid_amount < (ROUND((a.total_gl_amount + a.total_vat),2)) AND STATUS = 1 ${wherePostCondition}
        ORDER BY ${tempsortItem} ${tempsortOrder} LIMIT ${limit} OFFSET ${offset} """;

        List<GroovyRowResult> manualEntryList = db.rows(manualEntry);

        String countQuery = """SELECT  COUNT(a.id) AS totalManualRecList FROM Invoice_expense AS a, system_prefix AS sp WHERE  sp.id=7  AND STATUS = 1 AND a.paid_amount < (ROUND((a.total_gl_amount + a.total_vat),2)) ${wherePostCondition}""";
        List<GroovyRowResult> count_result = db.rows(countQuery)
        int total = count_result[0].totalManualRecList
        db.close();
        return [manualEntryList: manualEntryList, count: total]
    }


    public LinkedHashMap listOfManualReconcileEntrySearch(int offset, String limit, String sortItem, String sortOrder, String fiscalYearId, String searchItem, String searchString) {
        //def db = new Sql(dataSource)
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance( companyConfig.serverUrl+companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String wherePostCondition = ""
        if (fiscalYearId) { wherePostCondition = wherePostCondition + "  AND EXTRACT(YEAR FROM a.trans_date) = " + fiscalYearId  }

        String queryString = "LOWER('%" + searchString + "%')"

        String searchBy = ""

        if (searchItem == "invoiceNumber") {
            searchBy = "AND LOWER(CONCAT(sp.prefix,'-',a.invoice_no))"
        }

        String manualEntry = """SELECT a.id, CONCAT(sp.prefix,'-',a.invoice_no) AS invoiceNumber, DATE_FORMAT(a.trans_date, '%Y-%m-%d') AS transactionDate, a.paid_amount, ROUND((a.total_gl_amount + a.total_vat), 2) AS totalAmnt  FROM Invoice_expense AS a, system_prefix AS sp WHERE  sp.id=7  AND a.paid_amount < (ROUND((a.total_gl_amount + a.total_vat),2)) AND STATUS = 1 ${wherePostCondition} ${searchBy} LIKE ${queryString}
                                ORDER BY a.id DESC LIMIT ${limit} OFFSET ${offset} """;
        List<GroovyRowResult> manualEntryList = db.rows(manualEntry);

        String countQuery = """SELECT  COUNT(a.id) AS totalManualRecList FROM Invoice_expense AS a, system_prefix AS sp
                                WHERE  sp.id=7  AND STATUS = 1 AND a.paid_amount <
                                (ROUND((a.total_gl_amount + a.total_vat),2)) ${wherePostCondition} ${searchBy} LIKE ${queryString}""";
        List<GroovyRowResult> count_result = db.rows(countQuery)
        int total = count_result[0].totalManualRecList
        db.close();

        return [manualEntryList: manualEntryList, count: total]
    }

    public LinkedHashMap listOfUndoReconciliateItem(String selectedBankAccountNo ) {

        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance( companyConfig.serverUrl+companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        def accountType = new ReconciliationService().getBankAccountTypeFromNumber(selectedBankAccountNo)
        String transactionQuery = ""

        List<GroovyRowResult> undoReconciliateItemList
        if(accountType == '(Private)'){
            transactionQuery = """SELECT b.id, date_format(b.trans_date_time,'%d %b %Y') AS date , b.by_bank_account_no, b.description,b.bank_payment_id,
                                    IF(b.debit_credit='D',CONCAT('-',b.amount) ,CONCAT('+',b.amount) )amount,b.debit_credit
                                    FROM bank_statement_import_details_final AS b
                                    INNER JOIN private_reservation_spending_trans AS prst ON b.bank_payment_id = prst.bank_payment_id
                                    WHERE b.trans_bank_account_no = '${selectedBankAccountNo}' AND b.reconcilated <> 0
                                    GROUP BY b.id ORDER BY b.id """
            undoReconciliateItemList = db.rows(transactionQuery);
            if(undoReconciliateItemList.size() == 0){
                transactionQuery = """SELECT b.id, date_format(b.trans_date_time,'%d %b %Y') AS date , b.by_bank_account_no, b.description,b.bank_payment_id,
                                    IF(b.debit_credit='D',CONCAT('-',b.amount) ,CONCAT('+',b.amount) )amount,b.debit_credit
                                    FROM bank_statement_import_details_final AS b
                                    INNER JOIN trans_master AS t ON b.bank_payment_id = t.invoice_no
                                    WHERE b.trans_bank_account_no = '${selectedBankAccountNo}' AND b.reconcilated <> 0
                                    AND t.trans_type = 10 AND t.account_code = '1410'  AND recenciliation_code IS NOT NULL
                                    GROUP BY b.id ORDER BY b.id """

                undoReconciliateItemList = db.rows(transactionQuery);
            }

        }else if(accountType == '(Company)'){
            transactionQuery = """SELECT b.id, date_format(b.trans_date_time,'%d %b %Y') AS date , b.by_bank_account_no, b.description,b.bank_payment_id,
                                    IF(b.debit_credit='D',CONCAT('-',b.amount) ,CONCAT('+',b.amount) )amount,b.debit_credit
                                    FROM bank_statement_import_details_final AS b
                                    INNER JOIN trans_master AS t ON b.bank_payment_id = t.invoice_no
                                    WHERE b.trans_bank_account_no = '${selectedBankAccountNo}' AND b.reconcilated <> 0
                                    AND (t.trans_type = 7 OR t.trans_type = 6) AND recenciliation_code IS NOT NULL
                                    GROUP BY b.id ORDER BY b.id """

            undoReconciliateItemList = db.rows(transactionQuery);
        }

        int total = undoReconciliateItemList.size()
        db.close()

        return [undoReconciliateItemList: undoReconciliateItemList, count: total]
    }

}
