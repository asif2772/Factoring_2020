package factoring

import grails.converters.JSON
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import groovy.sql.GroovyRowResult

import javax.sql.DataSource
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.logging.Logger
import groovy.util.logging.Slf4j

@Slf4j
@Transactional
class ReconciliationService {
    SpringSecurityService springSecurityService

    static transactional = false
//    private Logger log = Logger.getLogger(getClass());
    DataSource dataSource
    BudgetViewDatabaseService budgetViewDatabaseService

    public LinkedHashMap reportListOfManualReconciliationEntry() {
        //def db = new Sql(dataSource)
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String incomeEntry = """SELECT b.id AS id,b.trans_bank_account_no AS bankAccountNo,b.bank_name AS companyBankName,
                                b.total_debit AS totalDebit ,b.total_credit AS totalCredit,
                                DATE_FORMAT(b.start_trans_date,"%d-%m-%Y") AS startTransDate,DATE_FORMAT(b.end_trans_date,"%d-%m-%Y") AS endTransDate,
                                DATE_FORMAT(b.upload_date,"%d-%m-%Y") AS transactionDate,
                                b.starting_balance AS openingBalance,
                                b.ending_balance AS closingBalance,b.number_of_transaction AS noOfTrans,a.reconcilated
                                FROM bank_statement_import_final AS b,bank_statement_import_details_final AS a
                                WHERE b.mt_file_name = 'Manual' AND b.id=a.bank_import_id GROUP BY b.id"""


        List<GroovyRowResult> dashboardExpenseBudgetItemList = db.rows(incomeEntry);

        int total = dashboardExpenseBudgetItemList.size()
        db.close();

        return [dashboardExpenseBudgetItemList: dashboardExpenseBudgetItemList, count: total]
    }

    public LinkedHashMap allBankStatementEntry(def sortingType) {
        //def db = new Sql(dataSource)
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)
        String bankStatementEntry=""
        if(sortingType=="bba"){
            bankStatementEntry = """SELECT b.id AS id,b.trans_bank_account_no AS bankAccountNo,b.mt_file_name AS companyBankName,
                                    b.total_debit AS totalDebit ,b.total_credit AS totalCredit,
                                    DATE_FORMAT(b.upload_date,"%d-%m-%Y") AS transactionDate,
                                    b.starting_balance AS openingBalance,
                                    b.ending_balance AS closingBalance,b.number_of_transaction AS noOfTrans,
                                    c.bank_account_category AS bankAccountCategory,min(b.start_trans_date) as OpeningDate,
                                    max(b.end_trans_date) as ClosingDate
                                    FROM bank_statement_import_final AS b, company_bank_accounts AS c
                                    WHERE b.trans_bank_account_no = c.bank_account_no
                                    Group by b.trans_bank_account_no
                                    ORDER BY b.trans_bank_account_no """

            /*
            bankStatementEntry = """SELECT b.id AS id,b.trans_bank_account_no AS bankAccountNo,b.mt_file_name AS companyBankName,
                                                b.total_debit AS totalDebit ,b.total_credit AS totalCredit,
                                                DATE_FORMAT(b.start_trans_date,"%d-%m-%Y") AS startTransDate,DATE_FORMAT(b.end_trans_date,"%d-%m-%Y") AS endTransDate,
                                                DATE_FORMAT(b.upload_date,"%d-%m-%Y") AS transactionDate,
                                                b.starting_balance AS openingBalance,
                                                b.ending_balance AS closingBalance,b.number_of_transaction AS noOfTrans,c.bank_account_category AS bankAccountCategory
                                                FROM bank_statement_import_final AS b, company_bank_accounts AS c WHERE b.trans_bank_account_no = c.bank_account_no ORDER BY b.trans_bank_account_no """

            */

        }else{
            bankStatementEntry = """SELECT b.id AS id,b.trans_bank_account_no AS bankAccountNo,b.mt_file_name AS companyBankName,
                                        b.total_debit AS totalDebit ,b.total_credit AS totalCredit,
                                        DATE_FORMAT(b.start_trans_date,"%d-%m-%Y") AS startTransDate,DATE_FORMAT(b.end_trans_date,"%d-%m-%Y") AS endTransDate,
                                        DATE_FORMAT(b.upload_date,"%d-%m-%Y") AS transactionDate,
                                        b.starting_balance AS openingBalance,
                                        b.ending_balance AS closingBalance,b.number_of_transaction AS noOfTrans,c.bank_account_category AS bankAccountCategory
                                        FROM bank_statement_import_final AS b, company_bank_accounts AS c  WHERE b.trans_bank_account_no = c.bank_account_no  ORDER BY b.mt_file_name"""

        }

        List<GroovyRowResult> dashboardExpenseBudgetItemList = db.rows(bankStatementEntry);

        int total = dashboardExpenseBudgetItemList.size()
        db.close();

        return [dashboardExpenseBudgetItemList: dashboardExpenseBudgetItemList, count: total]
    }


    public LinkedHashMap reportListOfManualReconciliationEntrySearch(int offset, String limit, String sortItem, String sortOrder, String searchItem, String searchString) {
        //def db = new Sql(dataSource)
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String queryString = "LOWER('%" + searchString + "%')"

        String searchBy = ""

        if (searchItem == "bankAccountNo") {
            searchBy = "AND c.bank_account_no "
        } else if (searchItem == "companyBankAccountName") {
            searchBy = "AND c.bank_account_name "
        }

        String expenseEntry = """SELECT b.id AS id,c.bank_account_no AS bankAccountNo,c.bank_account_name AS companyBankAccountName,SUM(IF(a.debit_credit='D', a.amount, 0)) AS totalDebit ,SUM(IF(a.debit_credit='C', a.amount, 0)) AS totalCredit,DATE_FORMAT(b.upload_date,"%d-%m-%Y") AS transactionDate FROM   bank_statement_import_details_final AS a LEFT JOIN bank_statement_import_final AS b ON a.bank_import_id=b.id LEFT JOIN company_bank_accounts AS c ON b.trans_bank_account_no=c.bank_account_no WHERE  b.mt_file_name= 'manual'  ${
            searchBy
        } LIKE ${queryString} GROUP BY a.bank_import_id
        ORDER BY ${sortItem} ${sortOrder}  LIMIT ${limit} OFFSET ${offset} """;

        List<GroovyRowResult> dashboardExpenseBudgetItemList = db.rows(expenseEntry);

        String countQuery = """SELECT COUNT(b.id) As totalBudgetItem FROM   bank_statement_import_details_final AS a LEFT JOIN bank_statement_import_final AS b ON a.bank_import_id=b.id LEFT JOIN company_bank_accounts AS c ON b.trans_bank_account_no=c.bank_account_no WHERE  b.mt_file_name= 'manual'  ${
            searchBy
        } LIKE ${queryString} GROUP BY a.bank_import_id """;
        List<GroovyRowResult> count_result = db.rows(countQuery)
        db.close();

        int total = 0
        if (count_result) {
            total = count_result[0].totalBudgetItem
        }

        return [dashboardExpenseBudgetItemList: dashboardExpenseBudgetItemList, count: total]
    }


    public LinkedHashMap reportListOfReconciliationEntry(int offset, String limit, String sortItem, String sortOrder) {
        //def db = new Sql(dataSource)
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        ///All Invoice List
        String reconcilEntry = """SELECT recenciliation_code FROM trans_master WHERE recenciliation_code<>"" GROUP BY recenciliation_code ORDER BY ${
            sortItem
        } ${sortOrder}  LIMIT ${limit} OFFSET ${offset} """
        List<GroovyRowResult> reconcilEntryItemList = db.rows(reconcilEntry);

        ArrayList allIncomeInvoiceArr = new ArrayList()
        ArrayList allExpanseInvoiceArr = new ArrayList()

        if (reconcilEntryItemList.size()) {
            reconcilEntryItemList.each { phn ->

                def firstArr = phn.recenciliation_code.split("#")
                def invoiceNo = firstArr[0]
                def invoiceCategory = firstArr[1]
                if (invoiceCategory == '1') {
                    allIncomeInvoiceArr << invoiceNo
                } else if (invoiceCategory == '2') {
                    allExpanseInvoiceArr << invoiceNo
                }
            }
        }

        //println(allIncomeInvoiceArr)
        //println(allExpanseInvoiceArr)

//        String incomeEntry = """SELECT b.id AS id,c.bank_account_no AS bankAccountNo,c.bank_account_name AS companyBankAccountName,SUM(IF(a.debit_credit='D', a.amount, 0)) AS totalDebit ,SUM(IF(a.debit_credit='C', a.amount, 0)) AS totalCredit,DATE_FORMAT(b.upload_date,"%d-%m-%Y") AS transactionDate FROM   bank_statement_import_details_final AS a LEFT JOIN bank_statement_import_final AS b ON a.bank_import_id=b.id LEFT JOIN company_bank_accounts AS c ON b.trans_bank_account_no=c.bank_account_no WHERE  b.mt_file_name= 'manual' GROUP BY a.bank_import_id  ORDER BY ${sortItem} ${sortOrder}  LIMIT ${limit} OFFSET ${offset} """;
        String incomeEntry = """SELECT b.id AS id,c.bank_account_no AS bankAccountNo,c.bank_account_name AS companyBankAccountName,SUM(IF(a.debit_credit='D', a.amount, 0)) AS totalDebit ,SUM(IF(a.debit_credit='C', a.amount, 0)) AS totalCredit,DATE_FORMAT(b.upload_date,"%d-%m-%Y") AS transactionDate FROM   bank_statement_import_details_final AS a LEFT JOIN bank_statement_import_final AS b ON a.bank_import_id=b.id LEFT JOIN company_bank_accounts AS c ON b.trans_bank_account_no=c.bank_account_no WHERE  b.mt_file_name= 'manual' GROUP BY a.bank_import_id  ORDER BY ${
            sortItem
        } ${sortOrder}  LIMIT ${limit} OFFSET ${offset} """
        List<GroovyRowResult> dashboardExpenseBudgetItemList = db.rows(incomeEntry);


        String countQuery = """SELECT COUNT(b.id) As totalBudgetItem FROM bank_statement_import_final AS b WHERE  b.mt_file_name= 'manual' GROUP BY b.id  """;
        List<GroovyRowResult> count_result = db.rows(countQuery)
        db.close();

        println("incomeEntry" + countQuery);
        int total = 0
        if (count_result) {
            total = count_result[0].totalBudgetItem
        }

        return [dashboardExpenseBudgetItemList: dashboardExpenseBudgetItemList, count: total]
    }

    public LinkedHashMap reportListOfReconciliationEntrySearch(int offset, String limit, String sortItem, String sortOrder, String searchItem, String searchString) {
        //def db = new Sql(dataSource)
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String queryString = "LOWER('%" + searchString + "%')"

        String searchBy = ""

        if (searchItem == "bankAccountNo") {
            searchBy = "AND c.bank_account_no "
        } else if (searchItem == "companyBankAccountName") {
            searchBy = "AND c.bank_account_name "
        }

        String expenseEntry = """SELECT b.id AS id,c.bank_account_no AS bankAccountNo,c.bank_account_name AS companyBankAccountName,SUM(IF(a.debit_credit='D', a.amount, 0)) AS totalDebit ,SUM(IF(a.debit_credit='C', a.amount, 0)) AS totalCredit,DATE_FORMAT(b.upload_date,"%d-%m-%Y") AS transactionDate FROM   bank_statement_import_details_final AS a LEFT JOIN bank_statement_import_final AS b ON a.bank_import_id=b.id LEFT JOIN company_bank_accounts AS c ON b.trans_bank_account_no=c.bank_account_no WHERE  b.mt_file_name= 'manual'  ${
            searchBy
        } LIKE ${queryString} GROUP BY a.bank_import_id
        ORDER BY ${sortItem} ${sortOrder}  LIMIT ${limit} OFFSET ${offset} """;

        List<GroovyRowResult> dashboardExpenseBudgetItemList = db.rows(expenseEntry);

        String countQuery = """SELECT COUNT(b.id) As totalBudgetItem FROM   bank_statement_import_details_final AS a LEFT JOIN bank_statement_import_final AS b ON a.bank_import_id=b.id LEFT JOIN company_bank_accounts AS c ON b.trans_bank_account_no=c.bank_account_no WHERE  b.mt_file_name= 'manual'  ${
            searchBy
        } LIKE ${queryString} GROUP BY a.bank_import_id """;
        List<GroovyRowResult> count_result = db.rows(countQuery)
        db.close();

        int total = 0
        if (count_result) {
            total = count_result[0].totalBudgetItem
        }

        return [dashboardExpenseBudgetItemList: dashboardExpenseBudgetItemList, count: total]
    }

    def insertCSVDataToBankStatementTables(listStatement, traceCode, originalFilename) {

        def bankImportId = 0;
        def randomSequence = 0;
        Integer fiscalYearTransFlag = 0

        def ActiveFiscalYear = new CoreParamsHelperTagLib().getActiveFiscalYear()
        def FiscalYearInfo = new CoreParamsHelperTagLib().getActiveFiscalYearInformation(ActiveFiscalYear)

        //def fiscalStart = FiscalYearInfo[0][7]
        //def fiscalEnd = FiscalYearInfo[0][8]

        int  nFiscalYear = Integer.parseInt(FiscalYearInfo[0][4])

        Date now = new Date()
        List<Map> finalListStatement = listStatement

        String nowDate = now.format("yyyy-MM-dd hh:mm:ss")

//        Map mapStatementHead = ["companyBankName": '',"companyBankNo":'', "startDate": '00', "startingBalance": 0.00,"startingBalanceType": 'C',
//                                "endDate": '00',"endingBalance": 0.00,"endingBalanceType": 'D',  "currType": 'EUR',"tracCode":'',"transFileName":'']

        //Import bank statement table.
        int nSize = finalListStatement.size() - 1;
        Map mapStmtRoot = finalListStatement.getAt(nSize);

        if (mapStmtRoot == null) return;

        Map bankStatementImport = [
                bankName           : mapStmtRoot.companyBankName,
                endingBalance      : mapStmtRoot.endingBalance,
                startingBalance    : mapStmtRoot.startingBalance,
                transBankAccountNo : mapStmtRoot.companyBankNo,
                transCurrency      : mapStmtRoot.currType,
                startTransDate     : mapStmtRoot.startDate,
                endTransDate       : mapStmtRoot.endDate,
                startingBalanceType: mapStmtRoot.startingBalanceType,
                endingBalanceType  : mapStmtRoot.endingBalanceType,
                uploadDate         : nowDate,
                transNo            : '',
                trackCode          : traceCode,
                mtFileName         : originalFilename]

        def tableBankStatementImport = "BankStatementImport"
        bankImportId = new BudgetViewDatabaseService().insert(bankStatementImport, tableBankStatementImport)

//        Map mapStatementDetails = ["companyBankNo": '', "tranactionDate": '', "amount": 0.00, "debitCredit": '',"relationBankNo": '',
//                                   "byAccountName": '', "description": '', "ibaNumber": '',"tracCode":'',"currType": 'EUR']

        //Bank statement details
        finalListStatement.remove(nSize);
        finalListStatement.each { detailsStmt ->

//            if (detailsStmt.tranactionDate >= fiscalStart && detailsStmt.tranactionDate <= fiscalEnd) {
//                fiscalYearTransFlag = 1
//            } else {
//                fiscalYearTransFlag = 0
//            }
            fiscalYearTransFlag = 0
            int nYear = getYearFromDateString(detailsStmt.tranactionDate);
            if(nFiscalYear == nYear){
                fiscalYearTransFlag = 1
            }

            def tempDescription = detailsStmt.description;
            if (detailsStmt.description == null) {
                tempDescription = ""
            } else {
                tempDescription = tempDescription.replace("'", "")
            }

            def tempAccountName = detailsStmt.byAccountName
            if (detailsStmt.byAccountName == null) {
                tempAccountName = ""
            } else {
                tempAccountName = tempAccountName.replace("'", "")
            }

            Map bankStatementImportDetails = [
                    amount             : detailsStmt.amount,
                    remainAmount       : detailsStmt.amount,
                    reconciliatedAmount: 0.00,
                    bankImportId       : bankImportId,
                    byAccountName      : tempAccountName,
                    byBankAccountNo    : detailsStmt.relationBankNo,
                    debitCredit        : detailsStmt.debitCredit,
                    description        : tempDescription,
                    entryTimestamp     : detailsStmt.tranactionDate,
                    transBankAccountNo : mapStmtRoot.companyBankNo,
                    transDateTime      : detailsStmt.tranactionDate,
                    transactionCode    : detailsStmt.tracCode,
                    fiscalYearTrans    : fiscalYearTransFlag,
                    reconcilated       : 0,
                    skipAccount        : 0,
                    ibaNumber          : detailsStmt.ibaNumber
            ]

            def tableBankStatementImportDetails = "BankStatementImportDetails"
            def insertedId1 = new BudgetViewDatabaseService().insert(bankStatementImportDetails, tableBankStatementImportDetails)
        }

    }


    def insertTXTDataToBankStatementTables(listStatement, traceCode, originalFilename) {

        def bankImportId = 0;
        def randomSequence = 0;
        Integer fiscalYearTransFlag = 0

        def ActiveFiscalYear = new CoreParamsHelperTagLib().getActiveFiscalYear()
        def FiscalYearInfo = new CoreParamsHelperTagLib().getActiveFiscalYearInformation(ActiveFiscalYear)

//        def fiscalStart = FiscalYearInfo[0][7]
//        def fiscalEnd = FiscalYearInfo[0][8]
        int  nFiscalYear = Integer.parseInt(FiscalYearInfo[0][4])

        Date now = new Date()
        List<Map> finalListStatement = listStatement.details
        List<Map> finalHeaderListStatement = listStatement.header

        String nowDate = now.format("yyyy-MM-dd hh:mm:ss")

//        Map mapStatementHead = ["companyBankName": '',"companyBankNo":'', "startDate": '00', "startingBalance": 0.00,"startingBalanceType": 'C',
//                                "endDate": '00',"endingBalance": 0.00,"endingBalanceType": 'D',  "currType": 'EUR',"tracCode":'',"transFileName":'']

        //Import bank statement table.
        /*int nSize = finalListStatement.size() - 1;
        Map mapStmtRoot = finalListStatement.getAt(nSize);

        if (mapStmtRoot == null) return;*/
        finalHeaderListStatement.each { mapStmtRoot ->
            Map bankStatementImport = [
                    bankName           : mapStmtRoot.companyBankName,
                    endingBalance      : mapStmtRoot.endingBalance,
                    startingBalance    : mapStmtRoot.startingBalance,
                    transBankAccountNo : mapStmtRoot.companyBankNo,
                    transCurrency      : mapStmtRoot.currType,
                    startTransDate     : mapStmtRoot.startDate,
                    endTransDate       : mapStmtRoot.endDate,
                    startingBalanceType: mapStmtRoot.startingBalanceType,
                    endingBalanceType  : mapStmtRoot.endingBalanceType,
                    uploadDate         : nowDate,
                    transNo            : '',
                    trackCode          : traceCode,
                    mtFileName         : originalFilename]

            def tableBankStatementImport = "BankStatementImport"
            bankImportId = new BudgetViewDatabaseService().insert(bankStatementImport, tableBankStatementImport)


            finalListStatement.each { detailsStmt ->
                if (detailsStmt.companyBankNo == mapStmtRoot.companyBankNo) {

                    fiscalYearTransFlag = 0
                    int nYear = getYearFromDateString(detailsStmt.tranactionDate);
                    if(nFiscalYear == nYear){
                        fiscalYearTransFlag = 1
                    }
//                if (detailsStmt.tranactionDate >= fiscalStart && detailsStmt.tranactionDate <= fiscalEnd) {
//                    fiscalYearTransFlag = 1
//                } else {
//                    fiscalYearTransFlag = 0
//                }

                    def tempDescription = detailsStmt.description;
                    if (detailsStmt.description == null) {
                        tempDescription = ""
                    } else {
                        tempDescription = tempDescription.replace("'", "")
                    }

                    def tempAccountName = detailsStmt.byAccountName
                    if (detailsStmt.byAccountName == null) {
                        tempAccountName = ""
                    } else {
                        tempAccountName = tempAccountName.replace("'", "")
                    }

                    Map bankStatementImportDetails = [
                            amount             : detailsStmt.amount,
                            remainAmount       : detailsStmt.amount,
                            reconciliatedAmount: 0.00,
                            bankImportId       : bankImportId,
                            byAccountName      : tempAccountName,
                            byBankAccountNo    : detailsStmt.relationBankNo,
                            debitCredit        : detailsStmt.debitCredit,
                            description        : tempDescription,
                            entryTimestamp     : detailsStmt.tranactionDate,
                            transBankAccountNo : detailsStmt.companyBankNo,
                            transDateTime      : detailsStmt.tranactionDate,
                            transactionCode    : detailsStmt.tracCode,
                            fiscalYearTrans    : fiscalYearTransFlag,
                            reconcilated       : 0,
                            skipAccount        : 0,
                            ibaNumber          : detailsStmt.ibaNumber
                    ]

                    def tableBankStatementImportDetails = "BankStatementImportDetails"
                    def insertedId1 = new BudgetViewDatabaseService().insert(bankStatementImportDetails, tableBankStatementImportDetails)
                }
            }
        }
    }

    def insertXMLDataToBankStatementTables(listStatement, traceCode, originalFilename) {

//        Map bankStatement = ["bankName":'',"id": '', "creationDt": '',"companyAccount":null,
//                             "balance":null,"summary":null,"entry":null];
//        Map balanceAmount = ["openBal": '',"cdtDbtIndOpn":'',"openBalDt":'',
//                             "closeBal": '',"cdtDbtIndCls":'',"closeBalDt":'',];
//        Map companyAccount = ["acctName": '',"acctNumber": '', "iBAN": '',"currency":''];


        def bankImportId = 0;
        def randomSequence = 0;
        Integer fiscalYearTransFlag = 0

        def ActiveFiscalYear = new CoreParamsHelperTagLib().getActiveFiscalYear()
        def FiscalYearInfo = new CoreParamsHelperTagLib().getActiveFiscalYearInformation(ActiveFiscalYear)

        //def fiscalStart = FiscalYearInfo[0][7]
        //def fiscalEnd = FiscalYearInfo[0][8]
        int  nFiscalYear = Integer.parseInt(FiscalYearInfo[0][4])

        //Random random = new Random()
        List insertedAccntList = new ArrayList();
        def accountNoById = [:]

        Date now = new Date()

        listStatement.each { mapStmt ->

            String nowDate = now.format("yyyy-MM-dd hh:mm:ss")

            Map balance = mapStmt.balance;
            Map companyAccount = mapStmt.companyAccount;

            Map bankStatementImport = [
                    bankName           : mapStmt.bankName,
                    endingBalance      : Double.parseDouble(balance.closeBal).round(2),
                    startingBalance    : Double.parseDouble(balance.openBal).round(2),
                    transBankAccountNo : companyAccount.acctNumber,
                    transCurrency      : companyAccount.currency,
                    startTransDate     : balance.openBalDt,
                    endTransDate       : balance.closeBalDt,
                    startingBalanceType: balance.cdtDbtIndOpn,
                    endingBalanceType  : balance.cdtDbtIndCls,
                    uploadDate         : nowDate,
                    transNo            : mapStmt.id,
                    trackCode          : traceCode,
                    mtFileName         : originalFilename]

            def tableBankStatementImport = "BankStatementImport"
            if (!insertedAccntList.contains(companyAccount.acctNumber)) {
                insertedAccntList.add(companyAccount.acctNumber);
                bankImportId = new BudgetViewDatabaseService().insert(bankStatementImport, tableBankStatementImport)

                accountNoById[companyAccount.acctNumber] = bankImportId
            }

            def bankImportIdByAccNo = accountNoById[companyAccount.acctNumber];
            def bankImportDetailsIns = new BudgetViewDatabaseService().executeUpdate("UPDATE bv.BankStatementImport SET ending_balance=" + Double.parseDouble(balance.closeBal).round(2)
                    + ",end_trans_date='" + balance.closeBalDt + "',ending_balance_type='" + balance.cdtDbtIndCls + "'"
                    + " WHERE id=" + bankImportIdByAccNo);
//        Map entryStmt = ["amount": '',"currency": '', "cdtDbtInd": '',"rvslInd":'',
//                         "entryDt":'',"transCode":'',"entryDetails":null];
//        Map entryDetails = ["name": '', "address": '',"country":'',"description":'',
//                            "transIBAN":'',"transAccNo":'',"transCode":''];
            List<Map> listEntry = mapStmt.entry;

            listEntry.each { entryStmt ->

                Map entryDetails = entryStmt.entryDetails;

                fiscalYearTransFlag = 0
                int nYear = getYearFromDateString(entryStmt.entryDt);
                if(nFiscalYear == nYear){
                    fiscalYearTransFlag = 1
                }
//                if (entryStmt.entryDt >= fiscalStart && entryStmt.entryDt <= fiscalEnd) {
//                    fiscalYearTransFlag = 1
//                }

                def tempDescription = (entryDetails == null) ? "" : entryDetails.description.trim()
                if (tempDescription == "") {
                    tempDescription = entryStmt.description.trim();
                }
                tempDescription = tempDescription.replace("'", "").trim()

                def tempAccountName = (entryDetails == null) ? "" : entryDetails.name.trim()
                tempAccountName = tempAccountName.replace("'", "").trim()

                Map bankStatementImportDetails = [
                        amount             : (entryStmt.amount == "") ? 0.0 : Double.parseDouble(entryStmt.amount).round(2),
                        remainAmount       : (entryStmt.amount == "") ? 0.0 : Double.parseDouble(entryStmt.amount).round(2),
                        reconciliatedAmount: 0.00,
                        bankImportId       : bankImportIdByAccNo,
                        byAccountName      : tempAccountName,
                        byBankAccountNo    : (entryDetails == null) ? "" : entryDetails.transAccNo,
                        debitCredit        : entryStmt.cdtDbtInd,
                        description        : tempDescription,
                        entryTimestamp     : entryStmt.entryDt,
                        transBankAccountNo : companyAccount.acctNumber,
                        transDateTime      : entryStmt.entryDt,
                        transactionCode    : (entryDetails == null) ? "" : entryDetails.transCode,
                        fiscalYearTrans    : fiscalYearTransFlag,
                        reconcilated       : 0,
                        skipAccount        : 0,
                        ibaNumber          : (entryDetails == null) ? "" : entryDetails.transIBAN
                ]

                def tableBankStatementImportDetails = "BankStatementImportDetails"
                def insertedId1 = new BudgetViewDatabaseService().insert(bankStatementImportDetails, tableBankStatementImportDetails)
            }
        }
    }

    def insertABNAParsedDataToBankStmtRootTable(listStatement, traceCode, originalFilename) {

        def bankImportId = 0;
        def randomSequence = 0;
        Integer fiscalYearTransFlag = 0

        def ActiveFiscalYear = new CoreParamsHelperTagLib().getActiveFiscalYear()
        def FiscalYearInfo = new CoreParamsHelperTagLib().getActiveFiscalYearInformation(ActiveFiscalYear)

//        def fiscalStart = FiscalYearInfo[0][7]
//        def fiscalEnd = FiscalYearInfo[0][8]
        int  nFiscalYear = Integer.parseInt(FiscalYearInfo[0][4])

        List insertedAccntList = new ArrayList();
        def accountNoById = [:]

        Date now = new Date()

        List<Map> finalListStatement = listStatement
        int nSize = finalListStatement.size() - 1;
        finalListStatement.remove(nSize)

        finalListStatement.each { mapStmt ->

            String nowDate = now.format("yyyy-MM-dd hh:mm:ss")
            def rootTransCurrency = "EUR"

            Map key_20 = mapStmt.key_20
            Map key_25 = mapStmt.key_25
            Map key_28 = mapStmt.key_28
            Map key_60F = mapStmt.key_60F
            Map key_62F = mapStmt.key_62F

            Map invoiceHead = [
                    bankName           : key_20.bankName,
                    endingBalance      : key_62F.endBalance,
                    startingBalance    : key_60F.startBalance,
                    transBankAccountNo : key_25.bankAccountNo,
                    transCurrency      : rootTransCurrency,
                    startTransDate     : key_60F.startTransDate,
                    endTransDate       : key_62F.endTransDate,
                    startingBalanceType: key_60F.startBalanceType,
                    endingBalanceType  : key_62F.endBalanceType,
                    uploadDate         : nowDate,
                    transNo            : key_28.rootTransNo,
                    trackCode          : traceCode,
                    mtFileName         : originalFilename
            ]

            def tableNameinvoiceHead = "BankStatementImport"
            if (!insertedAccntList.contains(key_25.bankAccountNo)) {
                insertedAccntList.add(key_25.bankAccountNo)
                bankImportId = new BudgetViewDatabaseService().insert(invoiceHead, tableNameinvoiceHead)
                accountNoById[key_25.bankAccountNo] = bankImportId
            }

            def bankImportIdByAccNo = accountNoById[key_25.bankAccountNo];
            def bankImportDetailsIns = new BudgetViewDatabaseService().executeUpdate("UPDATE bv.BankStatementImport SET ending_balance=" + Double.parseDouble(key_62F.endBalance).round(2)
                    + ",end_trans_date='" + key_62F.endTransDate + "',ending_balance_type='" + key_62F.endBalanceType + "'"
                    + " WHERE id=" + bankImportIdByAccNo);


            List<Map> list_key_61 = mapStmt.key_61
            List<Map> list_key_86 = mapStmt.key_86

            int i = 0;
            list_key_61.each { key_61 ->

                Map key_86 = list_key_86[i]
                i++;

                fiscalYearTransFlag = 0
                int nYear = getYearFromDateString(key_61.transDateTime);
                if(nFiscalYear == nYear){
                    fiscalYearTransFlag = 1
                }
//                if (key_61.transDateTime >= fiscalStart && key_61.transDateTime <= fiscalEnd) {
//                    fiscalYearTransFlag = 1
//                }

                def tempDescription = (key_86 == null) ? "" : key_86.description.trim()
                tempDescription = tempDescription.replace("'", "").trim()
                String description = new String(tempDescription.getBytes("latin1"));



                def tempAccountName = (key_86.byAccountName == null) ? "" : key_86.byAccountName.trim()
                tempAccountName = tempAccountName.replace("'", "").trim()
                String accountName = new String(tempAccountName.getBytes("latin1"));

                Map bankStatementImportDetails = [
                        amount             : (key_61.amount == "") ? 0.0 : key_61.amount,
                        remainAmount       : (key_61.amount == "") ? 0.0 : key_61.amount,
                        reconciliatedAmount: 0.00,
                        bankImportId       : bankImportIdByAccNo,
                        byAccountName      : accountName,
                        byBankAccountNo    : (key_86 == null) ? "" : key_86.byBankAccountNo,
                        debitCredit        : key_61.debitCredit,
                        description        : description,
                        entryTimestamp     : key_61.entryTimeStamp,
                        transBankAccountNo : key_25.bankAccountNo,
                        transDateTime      : key_61.transDateTime,
                        transactionCode    : (key_61 == null) ? "" : key_61.transactionCode,
                        fiscalYearTrans    : fiscalYearTransFlag,
                        reconcilated       : 0,
                        skipAccount        : 0,
                        ibaNumber          : (key_86 == null) ? "" : key_86.ibaNumber
                ]

                def tableBankStatementImportDetails = "BankStatementImportDetails"
                def insertedId1 = new BudgetViewDatabaseService().insert(bankStatementImportDetails, tableBankStatementImportDetails)
            }

        }
    }

    def insertKNABParsedDataToBankStmtRootTable(listStatement, traceCode, originalFilename) {

        def bankImportId = 0;
        def randomSequence = 0;
        Integer fiscalYearTransFlag = 0

        def ActiveFiscalYear = new CoreParamsHelperTagLib().getActiveFiscalYear()
        def FiscalYearInfo = new CoreParamsHelperTagLib().getActiveFiscalYearInformation(ActiveFiscalYear)

        //def fiscalStart = FiscalYearInfo[0][7]
        //def fiscalEnd = FiscalYearInfo[0][8]

        int  nFiscalYear = Integer.parseInt(FiscalYearInfo[0][4])

        List insertedAccntList = new ArrayList();
        def accountNoById = [:]

        Date now = new Date()

        List<Map> finalListStatement = listStatement
        int nSize = finalListStatement.size() - 1;
        finalListStatement.remove(nSize)

        finalListStatement.each { mapStmt ->

            String nowDate = now.format("yyyy-MM-dd hh:mm:ss")
            def rootTransCurrency = "EUR"

            Map key_25 = mapStmt.key_25
            Map key_28 = mapStmt.key_28
            Map key_60F = mapStmt.key_60F
            Map key_62F = mapStmt.key_62F

            Map invoiceHead = [
                    bankName           : "KNAB",
                    endingBalance      : key_62F.endBalance,
                    startingBalance    : key_60F.startBalance,
                    transBankAccountNo : key_25.bankAccountNo,
                    transCurrency      : rootTransCurrency,
                    startTransDate     : key_60F.startTransDate,
                    endTransDate       : key_62F.endTransDate,
                    startingBalanceType: key_60F.startBalanceType,
                    endingBalanceType  : key_62F.endBalanceType,
                    uploadDate         : nowDate,
                    transNo            : key_28.rootTransNo,
                    trackCode          : traceCode,
                    mtFileName         : originalFilename
            ]

            def tableNameinvoiceHead = "BankStatementImport"
            if (!insertedAccntList.contains(key_25.bankAccountNo)) {
                insertedAccntList.add(key_25.bankAccountNo)
                bankImportId = new BudgetViewDatabaseService().insert(invoiceHead, tableNameinvoiceHead)
                accountNoById[key_25.bankAccountNo] = bankImportId
            }

            def bankImportIdByAccNo = accountNoById[key_25.bankAccountNo];
            def bankImportDetailsIns = new BudgetViewDatabaseService().executeUpdate("UPDATE bv.BankStatementImport SET ending_balance=" + Double.parseDouble(key_62F.endBalance.toString()).round(2)
                    + ",end_trans_date='" + key_62F.endTransDate + "',ending_balance_type='" + key_62F.endBalanceType + "'"
                    + " WHERE id=" + bankImportIdByAccNo);


            List<Map> list_key_61 = mapStmt.key_61
            List<Map> list_key_86 = mapStmt.key_86

            int i = 0;
            list_key_61.each { key_61 ->

                Map key_86 = list_key_86[i]
                i++;

                //if (key_61.transDateTime >= fiscalStart && key_61.transDateTime <= fiscalEnd)
                fiscalYearTransFlag = 0
                int nYear = getYearFromDateString(key_61.transDateTime);
                if(nFiscalYear == nYear){
                    fiscalYearTransFlag = 1
                }

                def tempDescription = (key_86 == null) ? "" : key_86.description.trim()
                tempDescription = tempDescription.replace("'", "").trim()
                String description = new String(tempDescription.getBytes("latin1"));

                def tempAccountName = (key_86.byAccountName == null) ? "" : key_86.byAccountName.trim()
                tempAccountName = tempAccountName.replace("'", "").trim()
                String accountName = new String(tempAccountName.getBytes("latin1"));

                Map bankStatementImportDetails = [
                        amount             : (key_61.amount == "") ? 0.0 : key_61.amount,
                        remainAmount       : (key_61.amount == "") ? 0.0 : key_61.amount,
                        reconciliatedAmount: 0.00,
                        bankImportId       : bankImportIdByAccNo,
                        byAccountName      : accountName,
                        byBankAccountNo    : (key_86 == null) ? "" : key_86.byBankAccountNo,
                        debitCredit        : key_61.debitCredit,
                        description        : description,
                        entryTimestamp     : key_61.entryTimeStamp,
                        transBankAccountNo : key_25.bankAccountNo,
                        transDateTime      : key_61.transDateTime,
                        transactionCode    : (key_61 == null) ? "" : key_61.transactionCode,
                        fiscalYearTrans    : fiscalYearTransFlag,
                        reconcilated       : 0,
                        skipAccount        : 0,
                        ibaNumber          : (key_86 == null) ? "" : key_86.ibaNumber
                ]

                def tableBankStatementImportDetails = "BankStatementImportDetails"
                def insertedId1 = new BudgetViewDatabaseService().insert(bankStatementImportDetails, tableBankStatementImportDetails)
            }

        }
    }

    //Input date as 151231 (yymmdd) string format then return only year 2015
    def getYearFromDateString(def dateString){

        def yearPart = ""
        if(dateString.length() > 2){
            yearPart = dateString.substring(0,2)
        }

        def formatYear = "20"+yearPart;

        int year = Integer.parseInt(formatYear);

        return year;
    }

    def insertINGBParsedDataToBankStmtRootTable(listStatement, traceCode, originalFilename) {

        def bankImportId = 0;
        def randomSequence = 0;
        Integer fiscalYearTransFlag = 0

        def ActiveFiscalYear = new CoreParamsHelperTagLib().getActiveFiscalYear()
        def FiscalYearInfo = new CoreParamsHelperTagLib().getActiveFiscalYearInformation(ActiveFiscalYear)

//        def fiscalStart = FiscalYearInfo[0][7]
//        def fiscalEnd = FiscalYearInfo[0][8]
        int  nFiscalYear = Integer.parseInt(FiscalYearInfo[0][4])

        List insertedAccntList = new ArrayList();
        def accountNoById = [:]

        Date now = new Date()

        List<Map> finalListStatement = listStatement
        int nSize = finalListStatement.size() - 1;
        finalListStatement.remove(nSize)

        finalListStatement.each { mapStmt ->

            String nowDate = now.format("yyyy-MM-dd hh:mm:ss")
            def rootTransCurrency = "EUR"

            Map key_25 = mapStmt.key_25
            Map key_28C = mapStmt.key_28C
            Map key_60F = mapStmt.key_60F
            Map key_62F = mapStmt.key_62F

            Map invoiceHead = [
                    bankName           : 'ING',
                    endingBalance      : key_62F.endBalance,
                    startingBalance    : key_60F.startBalance,
                    transBankAccountNo : key_25.bankAccountNo,
                    transCurrency      : rootTransCurrency,
                    startTransDate     : key_60F.startTransDate,
                    endTransDate       : key_62F.endTransDate,
                    startingBalanceType: key_60F.startBalanceType,
                    endingBalanceType  : key_62F.endBalanceType,
                    uploadDate         : nowDate,
                    transNo            : key_28C.rootTransNo,
                    trackCode          : traceCode,
                    mtFileName         : originalFilename
            ]

            def tableNameinvoiceHead = "BankStatementImport"
            if (!insertedAccntList.contains(key_25.bankAccountNo)) {
                insertedAccntList.add(key_25.bankAccountNo)
                bankImportId = new BudgetViewDatabaseService().insert(invoiceHead, tableNameinvoiceHead)
                accountNoById[key_25.bankAccountNo] = bankImportId
            }

            def bankImportIdByAccNo = accountNoById[key_25.bankAccountNo];
            def bankImportDetailsIns = new BudgetViewDatabaseService().executeUpdate("UPDATE bv.BankStatementImport SET ending_balance=" + Double.parseDouble(key_62F.endBalance).round(2)
                    + ",end_trans_date='" + key_62F.endTransDate + "',ending_balance_type='" + key_62F.endBalanceType + "'"
                    + " WHERE id=" + bankImportIdByAccNo);


            List<Map> list_key_61 = mapStmt.key_61
            List<Map> list_key_86 = mapStmt.key_86

            int i = 0;
            list_key_61.each { key_61 ->

                Map key_86 = list_key_86[i]
                i++;

//                if (key_61.transDateTime >= fiscalStart && key_61.transDateTime <= fiscalEnd) {
//                    fiscalYearTransFlag = 1
//                }
                fiscalYearTransFlag = 0
                int nYear = getYearFromDateString(key_61.transDateTime);
                if(nFiscalYear == nYear){
                    fiscalYearTransFlag = 1
                }

                def tempDescription = (key_86 == null) ? "" : key_86.description.trim()
                tempDescription = tempDescription.replace("'", "").trim()

                Map bankStatementImportDetails = [
                        amount             : (key_61.amount == "") ? 0.0 : key_61.amount,
                        remainAmount       : (key_61.amount == "") ? 0.0 : key_61.amount,
                        reconciliatedAmount: 0.00,
                        bankImportId       : bankImportIdByAccNo,
                        byAccountName      : (key_86 == null) ? "" : key_86.byAccountName,
                        byBankAccountNo    : (key_86 == null) ? "" : key_86.byBankAccountNo,
                        debitCredit        : key_61.debitCredit,
                        description        : tempDescription,
                        entryTimestamp     : key_61.entryTimeStamp,
                        transBankAccountNo : key_25.bankAccountNo,
                        transDateTime      : (key_61.transDateTime == "") ? 0 : key_61.transDateTime,
                        transactionCode    : (key_61 == null) ? "" : key_61.transactionCode,
                        fiscalYearTrans    : fiscalYearTransFlag,
                        reconcilated       : 0,
                        skipAccount        : 0,
                        ibaNumber          : (key_86 == null) ? "" : key_86.ibaNumber
                ]

                def tableBankStatementImportDetails = "BankStatementImportDetails"
                def insertedId1 = new BudgetViewDatabaseService().insert(bankStatementImportDetails, tableBankStatementImportDetails)
            }

        }
    }


    def insert940ParsedDataToBankStmtRootTable(listStatement, traceCode, originalFilename) {

        def bankImportId = 0;
        def randomSequence = 0;
        Integer fiscalYearTransFlag = 0

        def ActiveFiscalYear = new CoreParamsHelperTagLib().getActiveFiscalYear()
        def FiscalYearInfo = new CoreParamsHelperTagLib().getActiveFiscalYearInformation(ActiveFiscalYear)

//        def fiscalStart = FiscalYearInfo[0][7]
//        def fiscalEnd = FiscalYearInfo[0][8]
        int  nFiscalYear = Integer.parseInt(FiscalYearInfo[0][4])

        List insertedAccntList = new ArrayList();
        def accountNoById = [:]

        Date now = new Date()

        List<Map> finalListStatement = listStatement
        int nSize = finalListStatement.size() - 1;
        finalListStatement.remove(nSize)

        finalListStatement.each { mapStmt ->

            String nowDate = now.format("yyyy-MM-dd hh:mm:ss")
            def rootTransCurrency = "EUR"

            Map key_25 = mapStmt.key_25
            Map key_28C = mapStmt.key_28C
            Map key_60F = mapStmt.key_60F
            Map key_62F = mapStmt.key_62F

            Map invoiceHead = [
                    bankName           : 'Rabo',
                    endingBalance      : key_62F.endBalance,
                    startingBalance    : key_60F.startBalance,
                    transBankAccountNo : key_25.bankAccountNo,
                    transCurrency      : rootTransCurrency,
                    startTransDate     : key_60F.startTransDate,
                    endTransDate       : key_62F.endTransDate,
                    startingBalanceType: key_60F.startBalanceType,
                    endingBalanceType  : key_62F.endBalanceType,
                    uploadDate         : nowDate,
                    transNo            : key_28C.rootTransNo,
                    trackCode          : traceCode,
                    mtFileName         : originalFilename
            ]

            def tableNameinvoiceHead = "BankStatementImport"
            if (!insertedAccntList.contains(key_25.bankAccountNo)) {
                insertedAccntList.add(key_25.bankAccountNo)
                bankImportId = new BudgetViewDatabaseService().insert(invoiceHead, tableNameinvoiceHead)
                accountNoById[key_25.bankAccountNo] = bankImportId
            }

            def bankImportIdByAccNo = accountNoById[key_25.bankAccountNo];
            def bankImportDetailsIns = new BudgetViewDatabaseService().executeUpdate("UPDATE bv.BankStatementImport SET ending_balance=" + Double.parseDouble(key_62F.endBalance).round(2)
                    + ",end_trans_date='" + key_62F.endTransDate + "',ending_balance_type='" + key_62F.endBalanceType + "'"
                    + " WHERE id=" + bankImportIdByAccNo);


            List<Map> list_key_61 = mapStmt.key_61
            List<Map> list_key_86 = mapStmt.key_86

            int i = 0;
            list_key_61.each { key_61 ->

                Map key_86 = list_key_86[i]
                i++;

                fiscalYearTransFlag = 0
                int nYear = getYearFromDateString(key_61.transDateTime);
                if(nFiscalYear == nYear){
                    fiscalYearTransFlag = 1
                }
//                if (key_61.transDateTime >= fiscalStart && key_61.transDateTime <= fiscalEnd) {
//                    fiscalYearTransFlag = 1
//                }

                def tempDescription = (key_86 == null) ? "" : key_86.description.trim()
                tempDescription = tempDescription.replace("'", "").trim()

                Map bankStatementImportDetails = [
                        amount             : (key_61.amount == "") ? 0.0 : key_61.amount,
                        remainAmount       : (key_61.amount == "") ? 0.0 : key_61.amount,
                        reconciliatedAmount: 0.00,
                        bankImportId       : bankImportIdByAccNo,
                        byAccountName      : (key_86 == null) ? "" : key_86.byAccountName,
                        byBankAccountNo    : (key_61.byBankAccountNo == "") ? "" : key_61.byBankAccountNo,
                        debitCredit        : (key_61.debitCredit == "") ? "N/A" : key_61.debitCredit,
                        description        : (tempDescription == "") ? "" : tempDescription,
                        entryTimestamp     : (key_61.entryTimeStamp == "") ? 0 : key_61.entryTimeStamp,
                        transBankAccountNo : key_25.bankAccountNo,
                        transDateTime      : (key_61.transDateTime == "") ? 0 : key_61.transDateTime,
                        transactionCode    : (key_61 == null) ? "" : key_61.transactionCode,
                        fiscalYearTrans    : fiscalYearTransFlag,
                        reconcilated       : 0,
                        skipAccount        : 0,
                        ibaNumber          : (key_61.ibaNumber == "") ? "" : key_61.ibaNumber
                ]

                def tableBankStatementImportDetails = "BankStatementImportDetails"
                def insertedId1 = new BudgetViewDatabaseService().insert(bankStatementImportDetails, tableBankStatementImportDetails)
            }

        }
    }


    def insertSNSBNLParsedDataToBankStmtRootTable(listStatement, traceCode, originalFilename) {

        def bankImportId = 0;
        def randomSequence = 0;
        Integer fiscalYearTransFlag = 0

        def ActiveFiscalYear = new CoreParamsHelperTagLib().getActiveFiscalYear()
        def FiscalYearInfo = new CoreParamsHelperTagLib().getActiveFiscalYearInformation(ActiveFiscalYear)

        //def fiscalStart = FiscalYearInfo[0][7]
//        def fiscalEnd = FiscalYearInfo[0][8]
        int  nFiscalYear = Integer.parseInt(FiscalYearInfo[0][4])

        List insertedAccntList = new ArrayList();
        def accountNoById = [:]

        Date now = new Date()

        List<Map> finalListStatement = listStatement
        int nSize = finalListStatement.size() - 1;
        finalListStatement.remove(nSize)

        finalListStatement.each { mapStmt ->

            String nowDate = now.format("yyyy-MM-dd hh:mm:ss")
            def rootTransCurrency = "EUR"

            Map key_25 = mapStmt.key_25
            Map key_28C = mapStmt.key_28C
            Map key_60F = mapStmt.key_60F
            Map key_62F = mapStmt.key_62F

            Map invoiceHead = [
                    bankName           : "ASN",
                    endingBalance      : key_62F.endBalance,
                    startingBalance    : key_60F.startBalance,
                    transBankAccountNo : key_25.bankAccountNo,
                    transCurrency      : rootTransCurrency,
                    startTransDate     : key_60F.startTransDate,
                    endTransDate       : key_62F.endTransDate,
                    startingBalanceType: key_60F.startBalanceType,
                    endingBalanceType  : key_62F.endBalanceType,
                    uploadDate         : nowDate,
                    transNo            : key_28C.rootTransNo,
                    trackCode          : traceCode,
                    mtFileName         : originalFilename
            ]

            def tableNameinvoiceHead = "BankStatementImport"
            if (!insertedAccntList.contains(key_25.bankAccountNo)) {
                insertedAccntList.add(key_25.bankAccountNo)
                bankImportId = new BudgetViewDatabaseService().insert(invoiceHead, tableNameinvoiceHead)
                accountNoById[key_25.bankAccountNo] = bankImportId
            }

            def bankImportIdByAccNo = accountNoById[key_25.bankAccountNo];
            def bankImportDetailsIns = new BudgetViewDatabaseService().executeUpdate("UPDATE bv.BankStatementImport SET ending_balance=" + Double.parseDouble(key_62F.endBalance).round(2)
                    + ",end_trans_date='" + key_62F.endTransDate + "',ending_balance_type='" + key_62F.endBalanceType + "'"
                    + " WHERE id=" + bankImportIdByAccNo);


            List<Map> list_key_61 = mapStmt.key_61
            List<Map> list_key_86 = mapStmt.key_86

            int i = 0;
            list_key_61.each { key_61 ->

                Map key_86 = list_key_86[i]
                i++;

                fiscalYearTransFlag = 0
                int nYear = getYearFromDateString(key_61.transDateTime);
                if(nFiscalYear == nYear){
                    fiscalYearTransFlag = 1
                }
//                if (key_61.transDateTime >= fiscalStart && key_61.transDateTime <= fiscalEnd) {
//                    fiscalYearTransFlag = 1
//                }

                def tempDescription = (key_86 == null) ? "" : key_86.description.trim()
                tempDescription = tempDescription.replace("'", "").trim()

                def tempAccountName = (key_86.byAccountName == null) ? "" : key_86.byAccountName.trim()
                tempAccountName = tempAccountName.replace("'", "").trim()

                Map bankStatementImportDetails = [
                        amount             : (key_61.amount == "") ? 0.0 : key_61.amount,
                        remainAmount       : (key_61.amount == "") ? 0.0 : key_61.amount,
                        reconciliatedAmount: 0.00,
                        bankImportId       : bankImportIdByAccNo,
                        byAccountName      : tempAccountName,
                        byBankAccountNo    : (key_86 == null) ? "" : key_86.byBankAccountNo,
                        debitCredit        : key_61.debitCredit,
                        description        : tempDescription,
                        entryTimestamp     : key_61.entryTimeStamp,
                        transBankAccountNo : key_25.bankAccountNo,
                        transDateTime      : key_61.transDateTime,
                        transactionCode    : (key_61 == null) ? "" : key_61.transactionCode,
                        fiscalYearTrans    : fiscalYearTransFlag,
                        reconcilated       : 0,
                        skipAccount        : 0,
                        ibaNumber          : (key_86 == null) ? "" : key_86.ibaNumber
                ]

                def tableBankStatementImportDetails = "BankStatementImportDetails"
                def insertedId1 = new BudgetViewDatabaseService().insert(bankStatementImportDetails, tableBankStatementImportDetails)
            }

        }
    }

    def insertTRIODOSParsedDataToBankStmtRootTable(listStatement, traceCode, originalFilename) {

        def bankImportId = 0;
        def randomSequence = 0;
        Integer fiscalYearTransFlag = 0

        def ActiveFiscalYear = new CoreParamsHelperTagLib().getActiveFiscalYear()
        def FiscalYearInfo = new CoreParamsHelperTagLib().getActiveFiscalYearInformation(ActiveFiscalYear)

//        def fiscalStart = FiscalYearInfo[0][7]
//        def fiscalEnd = FiscalYearInfo[0][8]
        int  nFiscalYear = Integer.parseInt(FiscalYearInfo[0][4])

        List insertedAccntList = new ArrayList();
        def accountNoById = [:]

        Date now = new Date()

        List<Map> finalListStatement = listStatement
        int nSize = finalListStatement.size() - 1;
        finalListStatement.remove(nSize)

        finalListStatement.each { mapStmt ->

            String nowDate = now.format("yyyy-MM-dd hh:mm:ss")
            def rootTransCurrency = "EUR"


            Map key_25 = mapStmt.key_25
            Map key_28 = mapStmt.key_28
            Map key_60F = mapStmt.key_60F
            Map key_62F = mapStmt.key_62F

            Map invoiceHead = [
                    bankName           : "TRIODOS",
                    endingBalance      : key_62F.endBalance,
                    startingBalance    : key_60F.startBalance,
                    transBankAccountNo : key_25.bankAccountNo,
                    transCurrency      : rootTransCurrency,
                    startTransDate     : key_60F.startTransDate,
                    endTransDate       : key_62F.endTransDate,
                    startingBalanceType: key_60F.startBalanceType,
                    endingBalanceType  : key_62F.endBalanceType,
                    uploadDate         : nowDate,
                    transNo            : key_28.rootTransNo,
                    trackCode          : traceCode,
                    mtFileName         : originalFilename
            ]

            def tableNameinvoiceHead = "BankStatementImport"
            if (!insertedAccntList.contains(key_25.bankAccountNo)) {
                insertedAccntList.add(key_25.bankAccountNo)
                bankImportId = new BudgetViewDatabaseService().insert(invoiceHead, tableNameinvoiceHead)
                accountNoById[key_25.bankAccountNo] = bankImportId
            }

            def bankImportIdByAccNo = accountNoById[key_25.bankAccountNo];
            def bankImportDetailsIns = new BudgetViewDatabaseService().executeUpdate("UPDATE bv.BankStatementImport SET ending_balance=" + Double.parseDouble(key_62F.endBalance).round(2)
                    + ",end_trans_date='" + key_62F.endTransDate + "',ending_balance_type='" + key_62F.endBalanceType + "'"
                    + " WHERE id=" + bankImportIdByAccNo);


            List<Map> list_key_61 = mapStmt.key_61
            List<Map> list_key_86 = mapStmt.key_86

            int i = 0;
            list_key_61.each { key_61 ->

                Map key_86 = list_key_86[i]
                i++;

                fiscalYearTransFlag = 0
                int nYear = getYearFromDateString(key_61.transDateTime);
                if(nFiscalYear == nYear){
                    fiscalYearTransFlag = 1
                }
//                if (key_61.transDateTime >= fiscalStart && key_61.transDateTime <= fiscalEnd) {
//                    fiscalYearTransFlag = 1
//                }

                def tempDescription = (key_86 == null) ? "" : key_86.description.trim()
                tempDescription = tempDescription.replace("'", "").trim()

                def tempAccountName = (key_86.byAccountName == null) ? "" : key_86.byAccountName.trim()
                tempAccountName = tempAccountName.replace("'", "").trim()

                Map bankStatementImportDetails = [
                        amount             : (key_61.amount == "") ? 0.0 : key_61.amount,
                        remainAmount       : (key_61.amount == "") ? 0.0 : key_61.amount,
                        reconciliatedAmount: 0.00,
                        bankImportId       : bankImportIdByAccNo,
                        byAccountName      : tempAccountName,
                        byBankAccountNo    : (key_86 == null) ? "" : key_86.byBankAccountNo,
                        debitCredit        : key_61.debitCredit,
                        description        : tempDescription,
                        entryTimestamp     : key_61.entryTimeStamp,
                        transBankAccountNo : key_25.bankAccountNo,
                        transDateTime      : key_61.transDateTime,
                        transactionCode    : (key_61 == null) ? "" : key_61.transactionCode,
                        fiscalYearTrans    : fiscalYearTransFlag,
                        reconcilated       : 0,
                        skipAccount        : 0,
                        ibaNumber          : (key_86 == null) ? "" : key_86.ibaNumber
                ]

                def tableBankStatementImportDetails = "BankStatementImportDetails"
                def insertedId1 = new BudgetViewDatabaseService().insert(bankStatementImportDetails, tableBankStatementImportDetails)
            }

        }
    }

    def ArrayList getSelectedStatementListForBookPeriod(def allBankStatementArrData, def params) {
        ArrayList detailsIdArr = new ArrayList()

        def isDebit = true;
        double selectedAmount = 0.00
        String debitCredit = "C"
        String tempGetCheck = ""

        if (params.check instanceof Object[]) {
            debitCredit = params.check[0]
        }else {
            debitCredit = params.check
        }

        if (allBankStatementArrData.size()) {
            allBankStatementArrData.each { phnStatement ->
                def firstArr = phnStatement.split("::")
                def detailsId = firstArr[0]
                isDebit = "D".equals(firstArr[2]);
                def tempindexBank = "checkBankStatement_" + detailsId

                params.each { phnshowParams ->
                    def tempShowParamsStr = phnshowParams.toString();
                    ArrayList tempShowParamsArr = new ArrayList()
                    tempShowParamsArr = tempShowParamsStr.split("=")

                    if (tempShowParamsArr[0] == tempindexBank) {
//                        if (tempShowParamsArr[1] == 'on') {
                        if (debitCredit == "D" || debitCredit == "C") {

                            Map map = ["id": 0, "amount": 0.00, "bookingPeriod": 0, "bookingYear": 0, "isDebit": true, "debitCredit": 'C']
                            map.id = firstArr[0]
                            map.amount = firstArr[1]

                            def bokYear = "bookingPeriodStartYear_" + firstArr[0]
                            def bokMon = "bookingPeriodStartMonth_" + firstArr[0]
                            def tempBokPerMon = "bookingPeriodStartMonthYear_" + firstArr[0]
                            map.isDebit = "D".equals(debitCredit);
                            map.debitCredit = debitCredit

                            params.each { phnshowParamsPeriod ->

                                def tempShowParamsStrPeriod = phnshowParamsPeriod.toString();
                                ArrayList tempShowParamsArrPeriod = new ArrayList()
                                tempShowParamsArrPeriod = tempShowParamsStrPeriod.split("=")

                                if (tempShowParamsArrPeriod[0] == tempBokPerMon) {
                                    def monthYearArr = tempShowParamsArrPeriod[1].split("-")
                                    map.bookingPeriod = monthYearArr[0]
                                    map.bookingYear = monthYearArr[1]
                                } else if (tempShowParamsArrPeriod[0] == bokMon) {
                                    def monthArr = tempShowParamsArrPeriod[1]
                                    map.bookingPeriod = monthArr
                                } else if (tempShowParamsArrPeriod[0] == bokYear) {
                                    def yearArr = tempShowParamsArrPeriod[1]
                                    map.bookingYear = yearArr
                                }
                            }

                            detailsIdArr << map
                            BigDecimal selectedAmountDouble = new BigDecimal(firstArr[1])
                            selectedAmount = selectedAmount + selectedAmountDouble
                        }
                    }
                }
            }
        }

        return detailsIdArr;
    }

    def getBankAccountType(def relationalBankAccountNo) {

        def sql = """SELECT DISTINCT(cba.bank_account_category)
                    FROM bank_statement_import_details_final as bsi
                    INNER JOIN company_bank_accounts as cba
                    ON cba.bank_account_no = bsi.trans_bank_account_no
                    where bsi.by_bank_account_no ='${relationalBankAccountNo}' """

        def typeArr = new BudgetViewDatabaseService().executeQuery(sql)
        def type = ''
        if (typeArr.size() > 0) {
            type = typeArr[0][0]
        }
        return type;
    }

    def getSelectedAmountTotal(def detailsIdArr) {

        double selectedTotalAmount = 0.00
        detailsIdArr.each { detailsPhn ->
            selectedTotalAmount = selectedTotalAmount + detailsPhn.amount
        }

        return selectedTotalAmount;
    }

    def ArrayList getSelectedStatementList(def allBankStatementArrData, def params) {
        ArrayList detailsIdArr = new ArrayList()
        def isDebit = true;

        if (allBankStatementArrData.size()) {
            allBankStatementArrData.each { phnStatement ->
                def firstArr = phnStatement.split("::")
                def detailsId = firstArr[0]
                if (firstArr.size() > 2) {
                    isDebit = "D".equals(firstArr[2]);
                }

                def tempindexBank = "checkBankStatement_" + detailsId

                params.each { phnshowParams ->
                    def tempShowParamsStr = phnshowParams.toString();
                    ArrayList tempShowParamsArr = new ArrayList()
                    tempShowParamsArr = tempShowParamsStr.split("=")
                    if (tempShowParamsArr[0] == tempindexBank) {
                        BigDecimal selectedAmountDouble = new BigDecimal(firstArr[1])
                        selectedAmountDouble = selectedAmountDouble.setScale(2, BigDecimal.ROUND_HALF_UP)
                        Map map = ["id": 0, "amount": 0.00, "isDebit": true]
                        map.id = firstArr[0]
                        map.amount = selectedAmountDouble
                        map.isDebit = isDebit;
                        detailsIdArr << map
                    }
                }
            }
        }

        return detailsIdArr;
    }

    def getReconciliatedRemainAmountData(def detailsIdArr,def invoiceSelectArr,def invoiceFinalArr,def selectedAmountIdsArr,def selInvoiceIdsArr){

        //ArrayList invoiceFinalArr = new ArrayList()

        detailsIdArr.each { detailsPhn ->
            Integer BankTrnsid = Integer.parseInt(detailsPhn.id.toString())
            if(selectedAmountIdsArr.contains(BankTrnsid)){return;}
            BigDecimal remainBankTrnsAmount = new BigDecimal(detailsPhn.amount)
            remainBankTrnsAmount = remainBankTrnsAmount.setScale(2, BigDecimal.ROUND_HALF_UP)

            invoiceSelectArr.each { invoicePhn ->

                Integer invoiceId = Integer.parseInt(invoicePhn.id.toString())
                if(selInvoiceIdsArr.contains(invoiceId)){return;}
                BigDecimal reconAmountRemainTrn = new BigDecimal(invoicePhn.reconAmountRemain)
                reconAmountRemainTrn = reconAmountRemainTrn.setScale(2, BigDecimal.ROUND_HALF_UP)
                Integer invoiceTrnId = Integer.parseInt(invoicePhn.id.toString())



                if (reconAmountRemainTrn > 0.00 || reconAmountRemainTrn < 0.00) {

                    Map mapFinal = ["trnasId": 0, "invoiceId": 0, "reconcilationAmount": 0.00]

                    if (remainBankTrnsAmount >= reconAmountRemainTrn) {
                        if (remainBankTrnsAmount > 0.00) {
                            mapFinal.trnasId = BankTrnsid
                            mapFinal.invoiceId = invoiceTrnId
                            mapFinal.reconcilationAmount = reconAmountRemainTrn

                            invoiceFinalArr << mapFinal

                            def temp = []
                            temp = invoiceSelectArr
                            invoiceSelectArr = []

                            temp.each { tempPhn ->
                                Map mapSelectTemp = ["id": 0, "reconcilationAmount": 0.00, "reconAmountRemain": 0.00]
                                mapSelectTemp.id = tempPhn.id
                                mapSelectTemp.reconcilationAmount = tempPhn.reconcilationAmount
                                mapSelectTemp.reconAmountRemain = tempPhn.reconAmountRemain

                                if (tempPhn.reconAmountRemain > 0) {
                                    if (Integer.parseInt(tempPhn.id.toString()) == Integer.parseInt(invoiceTrnId.toString())) {
                                        mapSelectTemp.reconAmountRemain = 0
                                    }
                                }

                                invoiceSelectArr << mapSelectTemp
                                remainBankTrnsAmount = remainBankTrnsAmount - reconAmountRemainTrn
                                reconAmountRemainTrn = 0
                            }
                        }
                    } else {

                        if (remainBankTrnsAmount > 0.00) {
                            mapFinal.trnasId = BankTrnsid
                            mapFinal.invoiceId = invoiceTrnId
                            mapFinal.reconcilationAmount = remainBankTrnsAmount
                            invoiceFinalArr << mapFinal

                            def tempElse = []
                            tempElse = invoiceSelectArr
                            invoiceSelectArr = []

                            tempElse.each { tempElsePhn ->
                                Map mapSelectTempElse = ["id": 0, "reconcilationAmount": 0.00, "reconAmountRemain": 0.00]
                                mapSelectTempElse.id = tempElsePhn.id
                                mapSelectTempElse.reconcilationAmount = tempElsePhn.reconcilationAmount
                                mapSelectTempElse.reconAmountRemain = tempElsePhn.reconAmountRemain

                                if (tempElsePhn.reconAmountRemain > 0) {
                                    if (Integer.parseInt(tempElsePhn.id.toString()) == Integer.parseInt(invoiceTrnId.toString())) {
                                        mapSelectTempElse.reconAmountRemain = reconAmountRemainTrn - remainBankTrnsAmount
                                    }
                                }

                                invoiceSelectArr << mapSelectTempElse
                                reconAmountRemainTrn = reconAmountRemainTrn - remainBankTrnsAmount
                                remainBankTrnsAmount = 0
                            }
                        }
                    }
                }

            }



        }

        //return invoiceFinalArr
    }


    def ArrayList getReconciliatedFinalAmountDataArr(def selectedAmountArr, def reconcilAmountArr) {

        ArrayList reconciliatedFinalAmountArr = new ArrayList()

        ArrayList selectedAmountIdsArr = new ArrayList()
        ArrayList selInvoiceIdsArr = new ArrayList()

        Double selectedRemainAmount = 0.0

        //Get the matching amount compare with reconciliated amount.
        selectedAmountArr.each { selAmtPhn ->
            Integer bankTrnsId = Integer.parseInt(selAmtPhn.id.toString())
            Double selectedAmount = selAmtPhn.amount
            selectedRemainAmount = selectedAmount.round(2);

            //Fisrt try to get the matching amount.
            if (!selectedAmountIdsArr.contains(bankTrnsId)) {
                prepareMatchedReconcilatedAmount(selectedRemainAmount, reconcilAmountArr, bankTrnsId,
                        reconciliatedFinalAmountArr, selInvoiceIdsArr, selectedAmountIdsArr);
            }

        }

        if(selectedAmountArr.size() != selectedAmountIdsArr.size()){
            getReconciliatedRemainAmountData(selectedAmountArr,reconcilAmountArr,reconciliatedFinalAmountArr,selectedAmountIdsArr,selInvoiceIdsArr);
        }

        return reconciliatedFinalAmountArr
    }

    def prepareMatchedReconcilatedAmount(def selectedAmount, def reconcilAmountArr, def bankTrnsId,def reconciliatedFinalAmountArr,
                                         def selInvoiceIdsArr, def selectedAmountIdsArr) {

        reconcilAmountArr.each { invoicePhn ->

            Double reconcilAmount = invoicePhn.reconAmountRemain
            reconcilAmount = reconcilAmount.round(2);
            Integer invoiceTrnId = Integer.parseInt(invoicePhn.id.toString())

            if(reconcilAmount == 0.0) return;

            Map mapFinal = ["trnasId": 0, "invoiceId": 0, "reconcilationAmount": 0.00]
            //Fisrt try to get the matching amount.
            if (selectedAmount == reconcilAmount
                    && !selInvoiceIdsArr.contains(invoiceTrnId)
                    && !selectedAmountIdsArr.contains(bankTrnsId))
            {
                mapFinal.trnasId = bankTrnsId
                mapFinal.invoiceId = invoiceTrnId
                mapFinal.reconcilationAmount = reconcilAmount

                selInvoiceIdsArr.add(invoiceTrnId);
                selectedAmountIdsArr.add(bankTrnsId);
                reconciliatedFinalAmountArr << mapFinal
                return true;
            }
        }
    }

    def prepareIfSelAmountGreaterFrmReconcilatedAmount(def selectedAmount, def reconcilAmountArr, def bankTrnsId,def reconciliatedFinalAmountArr,
                                                       def selInvoiceIdsArr, def selectedAmountIdsArr) {
/*
    def prepareIfSelAmountGreaterFrmReconcilatedAmount(
            def selectedAmount, def reconcilAmountArr, def reconciliatedFinalAmountArr, def selInvoiceIdsArr) {
*/

        reconcilAmountArr.each { invoicePhn ->

            Double reconcilAmount = invoicePhn.reconAmountRemain
            reconcilAmount = reconcilAmount.round(2);
            Integer invoiceTrnId = Integer.parseInt(invoicePhn.id.toString())

            if(reconcilAmount == 0.0) return;

            Map mapFinal = ["trnasId": 0, "invoiceId": 0, "reconcilationAmount": 0.00]
            //Fisrt try to get the matching amount.
            if (selectedAmount > reconcilAmount && !selInvoiceIdsArr.contains(invoiceTrnId)) {
                mapFinal.trnasId = bankTrnsId
                mapFinal.invoiceId = invoiceTrnId
                mapFinal.reconcilationAmount = reconcilAmount
                selectedAmount = selectedAmount - reconcilAmount

                selInvoiceIdsArr.add(invoiceTrnId);
                reconciliatedFinalAmountArr << mapFinal

            }else if(selectedAmount <= reconcilAmount && !selInvoiceIdsArr.contains(invoiceTrnId)){
                mapFinal.trnasId = bankTrnsId
                mapFinal.invoiceId = invoiceTrnId
                mapFinal.reconcilationAmount = selectedAmount
//                selectedAmount = reconcilAmount - selectedAmount

                selInvoiceIdsArr.add(invoiceTrnId);
                reconciliatedFinalAmountArr << mapFinal
                return true
            }

        }
    }

    def prepareIfSelAmountLessFrmReconcilatedAmount(def selectedAmount, def reconcilAmountArr, def bankTrnsId,def reconciliatedFinalAmountArr,
                                                    def selInvoiceIdsArr, def selectedAmountIdsArr) {

        reconcilAmountArr.each { invoicePhn ->

            Double reconcilAmount = invoicePhn.reconAmountRemain
            reconcilAmount = reconcilAmount.round(2);
            Integer invoiceTrnId = Integer.parseInt(invoicePhn.id.toString())

            if(reconcilAmount == 0.0) return;
            Map mapFinal = ["trnasId": 0, "invoiceId": 0, "reconcilationAmount": 0.00]
            //Fisrt try to get the matching amount.
            if (selectedAmount == reconcilAmount && !selInvoiceIdsArr.contains(invoiceTrnId)) {
                mapFinal.trnasId = BankTrnsid
                mapFinal.invoiceId = invoiceTrnId
                mapFinal.reconcilationAmount = reconcilAmount

                selInvoiceIdsArr.add(invoiceTrnId);
                reconciliatedFinalAmountArr << mapFinal
                //break;
            }
        }
    }

    def Map getSelectedInvoiceAmountData(params) {

        Map invoiceAmountData = ["reconcilAmountArr": null, "invoiceNoAmountSelected": 0.0]
        ArrayList invoiceSelectArr = new ArrayList()

        def allInvoiceNoStr = params.allInvoiceNoArr
        ArrayList allInvoiceNoArrData = new ArrayList()

        if (allInvoiceNoStr) {
            allInvoiceNoStr = allInvoiceNoStr.trim().replace("[", "");
            allInvoiceNoStr = allInvoiceNoStr.trim().replace("]", "");
            allInvoiceNoStr = allInvoiceNoStr.trim().replace(" ", "");

            allInvoiceNoArrData = allInvoiceNoStr.split(",")
        }

        Double allInvoiceNoAmountSelected = 0.00

        /*allInvoiceNoArrData.each { phnddd ->
            def tempindexddd = "reconciliateAmount_" + phnddd
            params.each { phnParamsddd ->
                def tempParamsStrddd = phnParamsddd.toString();
                ArrayList tempParamsArrddd = new ArrayList()
                tempParamsArrddd = tempParamsStrddd.split("=")

                if (tempParamsArrddd[0] == tempindexddd) {
                    Integer paidStatusdd = 1
                    def tempValue = tempParamsArrddd[1]
                    if(tempValue == null || tempValue == ""){
                        tempValue = "0.00"
                        tempValue = tempValue.replace(",", ".")
                    }

                    tempParamsArrddd[1] = tempValue
                    if (tempValue == null) {
                        paidStatusdd = 0
                    } else {
                        if (tempValue == '0.0') {
                            paidStatusdd = 0
                        } else if (tempValue == '.0') {
                            paidStatusdd = 0
                        } else if (tempParamsArrddd[1] == '0.00') {
                            paidStatusdd = 0
                        } else if (tempParamsArrddd[1] == '0') {
                            paidStatusdd = 0
                        }
                    }

                    if (paidStatusdd == 1) {
                        BigDecimal selectedInvoiceDouble = new BigDecimal(tempParamsArrddd[1])
                        selectedInvoiceDouble = selectedInvoiceDouble.setScale(2, BigDecimal.ROUND_HALF_UP)
                        *//*if(selectedInvoiceDouble<0.0){
                            selectedInvoiceDouble = selectedInvoiceDouble *(-1)
                        }*//*
                        allInvoiceNoAmountSelected = allInvoiceNoAmountSelected + selectedInvoiceDouble

                        Map mapSelect = ["id": 0, "reconcilationAmount": 0.00, "reconAmountRemain": 0.00]
                        mapSelect.id = phnddd
                        mapSelect.reconcilationAmount = selectedInvoiceDouble
                        mapSelect.reconAmountRemain = selectedInvoiceDouble
                        invoiceSelectArr << mapSelect
                    }
                }
            }
        }*/

        params.each { phnParamsddd ->
            def tempParamsStrddd = phnParamsddd.toString()
            ArrayList tempParamsArrddd = new ArrayList()
            tempParamsArrddd = tempParamsStrddd.split("=")
            ArrayList tmpInvoiceIdArr = new ArrayList()
            tmpInvoiceIdArr = tempParamsArrddd[0].split("_")
            if (tmpInvoiceIdArr[0] == "reconciliateAmount") {
                def phnddd = Integer.parseInt(tmpInvoiceIdArr[1])
                Integer paidStatusdd = 1
                def tempValue = tempParamsArrddd[1]
                if(tempValue == null || tempValue == ""){
                    tempValue = "0.00"
                    tempValue = tempValue.replace(",", ".")
                }

                tempParamsArrddd[1] = tempValue
                if (tempValue == null) {
                    paidStatusdd = 0
                } else {
                    if (tempValue == '0.0') {
                        paidStatusdd = 0
                    } else if (tempValue == '.0') {
                        paidStatusdd = 0
                    } else if (tempParamsArrddd[1] == '0.00') {
                        paidStatusdd = 0
                    } else if (tempParamsArrddd[1] == '0') {
                        paidStatusdd = 0
                    }
                }

                if (paidStatusdd == 1) {
                    BigDecimal selectedInvoiceDouble = new BigDecimal(tempParamsArrddd[1])
                    selectedInvoiceDouble = selectedInvoiceDouble.setScale(2, BigDecimal.ROUND_HALF_UP)
                    allInvoiceNoAmountSelected = allInvoiceNoAmountSelected + selectedInvoiceDouble
                    Map mapSelect = ["id": 0, "reconcilationAmount": 0.00, "reconAmountRemain": 0.00]
                    mapSelect.id = phnddd
                    mapSelect.reconcilationAmount = selectedInvoiceDouble
                    mapSelect.reconAmountRemain = selectedInvoiceDouble
                    invoiceSelectArr << mapSelect
                }
            }
        }

        invoiceAmountData.reconcilAmountArr = invoiceSelectArr;
        invoiceAmountData.invoiceNoAmountSelected = allInvoiceNoAmountSelected;

        return invoiceAmountData;
    }

    def Map getBankTransactionData(def selectedByBankAccountNo) {

        Map bankTransData = ["totalDebit": 0.00, "totalCredit": 0.00, "grandTotal": 0.00, "noOfTransaction": 0, "companyBankAcc": '', "allTransDataArr": null]

        ArrayList allBankAccountArr = new ArrayList()
        allBankAccountArr = new CoreParamsHelperTagLib().getAccountNoForAllDebitCreditTrans(selectedByBankAccountNo)

        /* allBankAccountArr = new CoreParamsHelperTagLib().getAllBankAccountForManualReconciliationBySelectedBankAccount(selectedByBankAccountNo, "D")

         if (allBankAccountArr.size() == 0) {
             allBankAccountArr = new CoreParamsHelperTagLib().getAllBankAccountForManualReconciliationBySelectedBankAccount(selectedByBankAccountNo, "C")
         }*/

        if (allBankAccountArr.size() > 0) {

            allBankAccountArr.eachWithIndex { bankStatementImportDetailsIndex, bankStatementImportDetailsKey ->

                if (bankStatementImportDetailsIndex[6] == "D") {
                    bankTransData.totalDebit = bankTransData.totalDebit + bankStatementImportDetailsIndex[2]
                    bankTransData.grandTotal = bankTransData.grandTotal + bankStatementImportDetailsIndex[10]
                } else {
                    bankTransData.totalCredit = bankTransData.totalCredit + bankStatementImportDetailsIndex[2]
                    bankTransData.grandTotal = bankTransData.grandTotal - bankStatementImportDetailsIndex[10]
                }
                bankTransData.noOfTransaction = bankTransData.noOfTransaction + 1
                bankTransData.companyBankAcc = bankStatementImportDetailsIndex[11]
            }

        }

        bankTransData.allTransDataArr = allBankAccountArr

        return bankTransData;
    }

    def Map getBankTransactionDataForBookedInvoiecReceipt(def selectedByBankAccountNo) {

        Map bankTransData = ["totalDebit": 0.00, "totalCredit": 0.00, "grandTotal": 0.00, "noOfTransaction": 0, "companyBankAcc": '', "allTransDataArr": null]

        ArrayList allBankAccountArr = new ArrayList()
        allBankAccountArr = new CoreParamsHelperTagLib().getAccountNoForAllDebitCreditTransForBookedInvoiecReceipt(selectedByBankAccountNo)

        /* allBankAccountArr = new CoreParamsHelperTagLib().getAllBankAccountForManualReconciliationBySelectedBankAccount(selectedByBankAccountNo, "D")

         if (allBankAccountArr.size() == 0) {
             allBankAccountArr = new CoreParamsHelperTagLib().getAllBankAccountForManualReconciliationBySelectedBankAccount(selectedByBankAccountNo, "C")
         }*/

        if (allBankAccountArr.size() > 0) {

            allBankAccountArr.eachWithIndex { bankStatementImportDetailsIndex, bankStatementImportDetailsKey ->

                if (bankStatementImportDetailsIndex[6] == "D") {
                    bankTransData.totalDebit = bankTransData.totalDebit + bankStatementImportDetailsIndex[2]
                    bankTransData.grandTotal = bankTransData.grandTotal + bankStatementImportDetailsIndex[10]
                } else {
                    bankTransData.totalCredit = bankTransData.totalCredit + bankStatementImportDetailsIndex[2]
                    bankTransData.grandTotal = bankTransData.grandTotal - bankStatementImportDetailsIndex[10]
                }
                bankTransData.noOfTransaction = bankTransData.noOfTransaction + 1
                bankTransData.companyBankAcc = bankStatementImportDetailsIndex[11]
            }

        }

        bankTransData.allTransDataArr = allBankAccountArr

        return bankTransData;
    }

    def getBankTransactionDataForBookedInvoiecReceiptDesc(def selectedByBankAccountNo) {

        ArrayList allBankAccountArrDesc = new ArrayList()
        allBankAccountArrDesc = new CoreParamsHelperTagLib().getAccountNoForAllDebitCreditTransForBookedInvoiecReceipt(selectedByBankAccountNo)
        StringBuffer description =  new StringBuffer()
        String descriptionNumbers = null

        String typeCreditOrDebit = allBankAccountArrDesc[0][6].toString()
        ArrayList<String> paymentReferencesAll = new ArrayList<String>()
        String paymentReferencesStr
        String paymentReferenceQuery
        ArrayList<String> matchedPmntRfrncs = new ArrayList<>()
        ArrayList<String> invoicePmntRfrncs = new ArrayList<>()
        List matchedPmntRfrncsIndexes
        int paymentReferenceLength = 0
        boolean sameDebtorOrCustomer = false
        Double bankAmount = allBankAccountArrDesc[0][10]
        def resultSets = [:]
        int foundPmntRfrncMatch = -100
        def name = ''

        if(typeCreditOrDebit.equalsIgnoreCase("C"))
            paymentReferenceQuery = """SELECT ii.invoice_no, ii.payment_ref
                                        FROM invoice_income AS ii
                                        LEFT JOIN trans_master tm ON tm.recenciliation_code = CONCAT(ii.id,'#1')
                                        AND tm.account_code=
                                          (SELECT debitor_gl_code
                                           FROM debit_credit_gl_setup)
                                        LEFT JOIN system_prefix AS sp ON sp.id=8
                                        GROUP BY tm.recenciliation_code,
                                                 ii.id
                                        HAVING ROUND(SUM(tm.amount),2) != 0"""
        else if (typeCreditOrDebit.equalsIgnoreCase("D"))
            paymentReferenceQuery = """SELECT ie.invoice_no, ie.payment_ref
                                        FROM invoice_expense AS ie
                                        INNER JOIN trans_master tm ON (tm.recenciliation_code = CONCAT(ie.id,'#2')
                                                                       OR tm.recenciliation_code = CONCAT(ie.id,'#4'))
                                        AND tm.account_code =
                                          (SELECT creditor_gl_code
                                           FROM debit_credit_gl_setup)
                                        LEFT JOIN system_prefix AS sp ON sp.id=7
                                        LEFT JOIN system_prefix AS sp2 ON sp2.id=12
                                        GROUP BY tm.recenciliation_code,
                                                 ie.id
                                        HAVING ROUND(SUM(tm.amount*(-1)),2)!=0"""


        paymentReferencesAll = budgetViewDatabaseService.executeQuery(paymentReferenceQuery)

        for (int counter = 0; counter < allBankAccountArrDesc.size(); counter++) {

            matchedPmntRfrncsIndexes = findMatchingPaymentReferences(paymentReferencesAll, allBankAccountArrDesc[counter][7]);

            if (matchedPmntRfrncsIndexes.size() > 0) {

                for (int i = 0; i < matchedPmntRfrncsIndexes.size(); i++) {
                    matchedPmntRfrncs.add(paymentReferencesAll.get(matchedPmntRfrncsIndexes.get(0))[1])
                    invoicePmntRfrncs.add(paymentReferencesAll.get(matchedPmntRfrncsIndexes.get(0))[0])
                    bankAmount = allBankAccountArrDesc[counter][10]
                    paymentReferenceLength++
                    foundPmntRfrncMatch = allBankAccountArrDesc[counter][0]
                }

            }
            if (paymentReferenceLength > 0)
                break
        }

        if(paymentReferenceLength > 0)
            sameDebtorOrCustomer = checkSameDebtorOrVendor(typeCreditOrDebit,invoicePmntRfrncs)


        paymentReferencesStr = "'"+ matchedPmntRfrncs.toString().replaceAll("[\\[.\\].\\s+]", "").replaceAll(",", "','") +"'"



        resultSets.put('typeCreditOrDebit',typeCreditOrDebit)
        resultSets.put('paymentReferencesStr',paymentReferencesStr)
        resultSets.put('paymentReferenceLength',paymentReferenceLength)
        resultSets.put('sameDebtorOrCustomer',sameDebtorOrCustomer)
        resultSets.put('bankAmount',bankAmount)
        resultSets.put('foundPmntRfrncMatch',foundPmntRfrncMatch)
        resultSets.put('debtorOrVendorName',name)

        return resultSets
    }

    String getCustomerOrVendorname(String type, ArrayList<String> invoiceList) {

        String name
        print("invoice: "+invoiceList[0])
        if(type.equalsIgnoreCase("C")){
            name = """SELECT customer_name FROM customer_master cm
                      INNER JOIN invoice_income ii
                      ON ii.debtor_id = cm.id WHERE ii.invoice_no ='${invoiceList[0]}'"""
        }
        else{
            name = """SELECT vendor_name FROM vendor_master vm
                      INNER JOIN invoice_expense ie
                      ON ie.vendor_id = vm.id WHERE ie.invoice_no = '${invoiceList[0]}'"""
        }

        def nameFound = budgetViewDatabaseService.executeQuery(name)

        return nameFound[0][0]
    }

    List findMatchingPaymentReferences(ArrayList<String> pmntRefs, String description) {

        AhoCorasick ahoCorasick = new AhoCorasick(2000000)

        for (int i = 0; i < pmntRefs.size(); i++) {
            try{
                if(!pmntRefs.get(i)[1].equals(""))
                    ahoCorasick.addString(pmntRefs.get(i)[1].toString(),i)
            }catch (Exception e){
                print("ahocorasic exception: "+e.toString())
            }
        }

        String s = description
        print(description: description)
        int node = 0
        List<Integer> positions = new ArrayList<>()

        for (int i = 0; i < s.length(); i++) {
            try {
                node = ahoCorasick.transition(node, s.charAt(i))
                if (ahoCorasick.nodes[node].leaf) {
                    int id = ahoCorasick.nodes[node].dictId
                    positions.add(id)
                }
            }catch (Exception e){
                print("ahocorasic exception: "+e.toString())
            }
        }

        return positions
    }

    boolean checkSameDebtorOrVendor(String typeCreditOrDebit, ArrayList<String> invoiceList) {

        String vendorOrDebtorQuery
        String invoices = "'"+ invoiceList.toString().replaceAll("[\\[.\\].\\s+]", "").replaceAll(",", "','") +"'"
        //String invoices = "'000005','000004'"

        if(typeCreditOrDebit.equalsIgnoreCase("C")){
            vendorOrDebtorQuery = "SELECT debtor_id FROM invoice_income WHERE invoice_no IN (${invoices})"
        }
        else{
            vendorOrDebtorQuery = "SELECT vendor_id FROM invoice_expense WHERE invoice_no IN (${invoices})"
        }

        Set debotorOrVendoirId = budgetViewDatabaseService.executeQuery(vendorOrDebtorQuery)

        if(debotorOrVendoirId.size()>1)
            return false

        return true

    }

    def splitTransactionData(def id, def splitAmount) {
        def flag = "failed"
        def sql = """SELECT * FROM bank_statement_import_details_final WHERE id= ${id}"""
        def bankStatementData = new BudgetViewDatabaseService().executeQuery(sql)

        if (bankStatementData.size() > 0 &&  bankStatementData[0][17] == 0 && bankStatementData[0][2] != 0) {
            def oldAmount = bankStatementData[0][2]
            def newAmount = oldAmount - Math.abs(Double.parseDouble(splitAmount.toString()))
            def tableName = "BankStatementImportDetailsFinal"
            def wherecondition = "id = " + id
            new BudgetViewDatabaseService().delete(tableName, wherecondition)

            for (def i = 0; i < 2; i++) {
                def bankPaymentId = bankStatementData[0][16]
                if (bankPaymentId) {
                    bankPaymentId = bankPaymentId
                } else {
                    bankPaymentId = 0
                }

                def tempAmount
                if (i == 0) {
                    tempAmount = newAmount
                } else {
                    tempAmount = Math.abs(Double.parseDouble(splitAmount.toString()))
                }

                Map dataArr = [
                        amount             : tempAmount,
                        bankImportId       : bankStatementData[0][3],
                        byAccountName      : bankStatementData[0][4],
                        byBankAccountNo    : bankStatementData[0][5],
                        debitCredit        : bankStatementData[0][6],
                        description        : bankStatementData[0][7],
                        entryTimestamp     : bankStatementData[0][8],
                        fiscalYearTrans    : bankStatementData[0][9],
                        remainAmount       : tempAmount,
                        transBankAccountNo : bankStatementData[0][11],
                        transDateTime      : bankStatementData[0][12],
                        transactionCode    : bankStatementData[0][13],
                        skipAccount        : bankStatementData[0][14],
                        reconcilated       : bankStatementData[0][15],
                        bankPaymentId      : bankPaymentId,
                        reconciliatedAmount: bankStatementData[0][17],
                        ibaNumber          : bankStatementData[0][18]
                ]
                new BudgetViewDatabaseService().insert(dataArr, tableName)
            }
            flag = "success"

        } else {
            flag = "failed"
        }

        return flag
    }

    def Map matchingInfoOfVendorOrCustomer(def bankAccountNo) {
        def ActiveFiscalYear = new CoreParamsHelperTagLib().getActiveFiscalYear()
        def FiscalYearInfo = new CoreParamsHelperTagLib().getActiveFiscalYearInformation(ActiveFiscalYear)
        def idArr = bankAccountNo.split('::')
        bankAccountNo = idArr[0]
        Map result = [flag      : 'failed',
                      cusOrVen  : "CUS",
                      cusOrVenId: 0,
                      formType  : 0,

        ]
        def bankAccountType = getBankAccountType(bankAccountNo)
        if (bankAccountType == 'cba') {
            result.cusOrVen = "CUS"
            result.flag = "success"
            def customerInfoArr = budgetViewDatabaseService.executeQuery("""SELECT CBA.customer_id as customerId FROM customer_bank_account as CBA INNER JOIN customer_master as CM ON CBA.customer_id = CM.id WHERE CBA.bank_account_no= '${bankAccountNo}' """)

            if (customerInfoArr.size() > 0) {
                result.cusOrVenId = customerInfoArr[0][0]
                result.cusOrVen = "CUS"
            } else {
                def vendorInfoArr = budgetViewDatabaseService.executeQuery("""SELECT VBA.vendor_id as vendorId FROM vendor_bank_account as VBA INNER JOIN vendor_master as VM ON VBA.vendor_id=VM.id WHERE VBA.bank_account_no='${bankAccountNo}' """)
                if (vendorInfoArr.size() > 0) {
                    result.cusOrVenId = vendorInfoArr[0][0]
                    result.cusOrVen = "VEN"
                }
            }
        }
        def formTypeArr
        if( result.cusOrVen == "VEN" || result.cusOrVen == "CUS"){
            formTypeArr = new BudgetViewDatabaseService().executeQuery("""SELECT  id FROM reconciliation_booking_type where form_type = 1""")
        }else if(result.cusOrVen == "RES"){
            formTypeArr = new BudgetViewDatabaseService().executeQuery("""SELECT  id FROM reconciliation_booking_type where form_type = 6""")
        }else if(result.cusOrVen == "PVT"){
            formTypeArr = new BudgetViewDatabaseService().executeQuery("""SELECT  id FROM reconciliation_booking_type where form_type = 5 """)
        }

        if(formTypeArr.size()){
            result.formType = formTypeArr[0][0]
        }

        return result
    }

    def Map matchingInfoOfVendorOrCustomerBackup(def bankAccountNo) {

        def ActiveFiscalYear = new CoreParamsHelperTagLib().getActiveFiscalYear()
        def FiscalYearInfo = new CoreParamsHelperTagLib().getActiveFiscalYearInformation(ActiveFiscalYear)
        def idArr = bankAccountNo.split('::')
        bankAccountNo = idArr[0]

        Map result = [flag      : 'failed',
                      cusOrVen  : "CUS",
                      cusOrVenId: 0,
                      formType  : 0,

        ]

        def bankAccountType = getBankAccountType(bankAccountNo);

        if (bankAccountType == 'cba') {

            result.cusOrVen = "CUS"
            result.flag = "success"

            def customerInfoArr = budgetViewDatabaseService.executeQuery("""select bc.customer_id  from (SELECT ii.debtor_id as customer_id,cm.customer_name,cm.customer_code,
                                                                        ii.id,ROUND(abs(SUM(tm.amount))) as remainAmount FROM invoice_income AS ii
                                                                        INNER JOIN customer_master AS cm ON (ii.debtor_id=cm.id)
                                                                        INNER JOIN trans_master tm ON tm.recenciliation_code = CONCAT(ii.id,'#1') AND
                                                                        tm.account_code=(Select debitor_gl_code from debit_credit_gl_setup)
                                                                        GROUP BY ii.customer_id ORDER BY cm.customer_name ASC ) as  xx
                                                                        INNER JOIN customer_bank_account as bc on xx.customer_id = bc.customer_id
                                                                        where bc.bank_account_no = '${bankAccountNo}' and xx.remainAmount !=0 """)

            if (customerInfoArr.size() > 0) {
                result.cusOrVenId = customerInfoArr[0][0]
                result.cusOrVen = "CUS"
            } else {

                def vendorInfoArr = budgetViewDatabaseService.executeQuery("""select bc.vendor_id  from ( SELECT ie.vendor_id,vm.vendor_name,vm.vendor_code,
                                                                        ie.id,ROUND(abs(SUM(tm.amount))) as remainAmount FROM invoice_expense AS ie
                                                                        INNER JOIN vendor_master AS vm ON (ie.vendor_id=vm.id)
                                                                        INNER JOIN trans_master tm ON (tm.recenciliation_code = CONCAT(ie.id,'#2') OR tm.recenciliation_code = CONCAT(ie.id,'#4'))
                                                                        AND tm.account_code=(Select creditor_gl_code from debit_credit_gl_setup)
                                                                        GROUP BY ie.vendor_id ORDER BY vm.vendor_name ASC) as  xx
                                                                        INNER JOIN vendor_bank_account as bc on xx.vendor_id = bc.vendor_id
                                                                        where bc.bank_account_no = '${bankAccountNo}'  and xx.remainAmount !=0 """)
                if (vendorInfoArr.size() > 0) {
                    result.cusOrVenId = vendorInfoArr[0][0]
                    result.cusOrVen = "VEN"
                }

            }
        }
//        else if (bankAccountType == 'pba') {
//
//            result.cusOrVen = "PVT"
//            result.flag = "success"
//            def privateInfoArr = budgetViewDatabaseService.executeQuery("""SELECT xx.budget_id FROM (SELECT DISTINCT(a.id) AS budget_id,a.budget_name As budgetName FROM private_budget_master AS a,private_budget_item AS b
//                                                                            WHERE b.booking_period >= ${FiscalYearInfo[0][5]}
//                                                                            AND b.booking_year >=${FiscalYearInfo[0][6]}
//                                                                            AND b.budget_name_id=a.id  AND a.status=1
//                                                                            ORDER BY a.budget_name ASC) xx INNER JOIN private_budget_bank_account  pbc on xx.budget_id = pbc.budget_master_id
//                                                                            WHERE pbc.bank_account_no ='${bankAccountNo}' """)
//            if (privateInfoArr.size() > 0) {
//                result.cusOrVenId = privateInfoArr[0][0]
//            }
//        }

        def formTypeArr
        if( result.cusOrVen == "VEN" || result.cusOrVen == "CUS"){
            formTypeArr = new BudgetViewDatabaseService().executeQuery("""
                                    SELECT  id FROM reconciliation_booking_type where form_type = 1""")
        }else if(result.cusOrVen == "RES"){
            formTypeArr = new BudgetViewDatabaseService().executeQuery("""
                                    SELECT  id FROM reconciliation_booking_type where form_type = 6""")
        }else if(result.cusOrVen == "PVT"){
            formTypeArr = new BudgetViewDatabaseService().executeQuery("""
                                    SELECT  id FROM reconciliation_booking_type where form_type = 5 """)
        }

        if(formTypeArr.size()){
            result.formType = formTypeArr[0][0]
        }

        return result;
    }

    def Map getBankTransactionDataTypePayment(def selectedByBankAccountNo) {

        Map bankTransData = ["totalDebit": 0.00, "totalCredit": 0.00, "grandTotal": 0.00, "noOfTransaction": 0, "companyBankAcc": '', "allTransDataArr": null]

        ArrayList allBankAccountArr = new ArrayList()
        allBankAccountArr = new CoreParamsHelperTagLib().getAccountNoForAllDebitCreditTrans(selectedByBankAccountNo)

//        if (allBankAccountArr.size() == 0) {
//            allBankAccountArr = new CoreParamsHelperTagLib().getAllBankAccountForManualReconciliationBySelectedBankAccount(selectedByBankAccountNo, "C")
//        }


        if (allBankAccountArr.size() > 0) {

            allBankAccountArr.eachWithIndex { bankStatementImportDetailsIndex, bankStatementImportDetailsKey ->

                if (bankStatementImportDetailsIndex[6] == "D") {
                    bankTransData.totalDebit = bankTransData.totalDebit + bankStatementImportDetailsIndex[2]
                    bankTransData.grandTotal = bankTransData.grandTotal + bankStatementImportDetailsIndex[10]
                } else {
                    bankTransData.totalCredit = bankTransData.totalCredit + bankStatementImportDetailsIndex[2]
                    bankTransData.grandTotal = bankTransData.grandTotal - bankStatementImportDetailsIndex[10]
                }
                bankTransData.noOfTransaction = bankTransData.noOfTransaction + 1
                bankTransData.companyBankAcc = bankStatementImportDetailsIndex[11]
            }
        }
        bankTransData.allTransDataArr = allBankAccountArr

        return bankTransData;
    }

    def getSelectedRelationBankAccNo() {

        ArrayList allBankAccountIDArr = new ArrayList()
        allBankAccountIDArr = new CoreParamsHelperTagLib().getAllBankAccountForManualReconciliation()

        def actionTypeId = '1'
        def selectedRelationBankAccNo = '0'
        if (allBankAccountIDArr.size()) {
            selectedRelationBankAccNo = allBankAccountIDArr[0][0]
        }

        return selectedRelationBankAccNo;
    }

//  ********

    def saveNewVendorForUnknownBankStatement(params) {
        def vendorCode = new CoreParamsHelperTagLib().getNextGeneratedNumber('vendor')

        def creditStatus = params.creditStatus
        def currCode = params.currCode
        def vendorName = params.vendorName
        def vendorType = params.vendorType
        def defaultGlAccount = params.defaultGlAccount
        def paymentTermId = params.paymentTerm.id

        //def db = new Sql(dataSource)
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String vendorSaveQuery = """
                INSERT INTO vendor_master(version,by_shop,cham_of_commerce,comments,company_name,credit_status,curr_code,default_gl_account,email,first_name,frequency_of_invoice,
                gender,last_name,middle_name,moment_of_sending,payment_term_id,status,vat,vendor_code,vendor_name,vendor_type) VALUES( '0', '0','','','','$creditStatus','$currCode','$defaultGlAccount',
                '','', 'monthly','Male','', '', '','$paymentTermId', '1', '1', '$vendorCode', '$vendorName', '$vendorType' ) """;
        db.execute(vendorSaveQuery);

        //////////GET LAST INSERTED ID///////////
        String customerSaveLastIdQuery = """SELECT id FROM vendor_master WHERE vendor_code=$vendorCode ORDER BY id ASC""";
        def lastInseredIdArr = db.rows(customerSaveLastIdQuery);
        def lastInseredId = lastInseredIdArr[0]['id'];

        ///GET BANK ACCOUNT NO////
        def noTransBankAccountNo = params.noTransBankAccountNo

        String customerBankAccountQuery = """SELECT by_bank_account_no FROM bank_statement_import_details_final WHERE id=$noTransBankAccountNo ORDER BY id ASC""";
        def customerBankAccountArr = db.rows(customerBankAccountQuery);
        def customerBankAccount = customerBankAccountArr[0]['by_bank_account_no']
        def bankAccountName = params.bankAccountName
        def ibanPrefix = params.ibanPrefix

        String vendorBankSaveQuery = """ INSERT INTO vendor_bank_account( version, bank_account_name, bank_account_no, vendor_id, iban_prefix, STATUS) VALUES( '0', '$bankAccountName', '$customerBankAccount', '$lastInseredId',
                            '$ibanPrefix','1') """;
        db.execute(vendorBankSaveQuery);
    }

    def saveNewCustomerForUnknownBankStatement(params) {

        def customerCode = new CoreParamsHelperTagLib().getNextGeneratedNumber('customer')

        def creditStatus = params.creditStatus
        def currCode = params.currCode
        def customerName = params.customerName
        def customerType = params.customerType
        def defaultGlAccount = params.defaultGlAccount
        def paymentTermId = params.paymentTerm.id

        //def db = new Sql(dataSource)
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String customerSaveQuery = """
                INSERT INTO customer_master( version, cham_of_commerce, comments, company_name, credit_status, curr_code, customer_code, customer_name, customer_type,
                    default_gl_account, email, first_name, frequency_of_invoice, gender, last_name, middle_name, moment_of_sending, payment_term_id, status, vat) VALUES(
                    '0', '', '', '', '$creditStatus', '$currCode', '$customerCode', '$customerName', '$customerType', '$defaultGlAccount', '', '', 'Once', 'Male', '', '', '', '$paymentTermId',
                     1, '1' ) """;
        db.execute(customerSaveQuery);

        /*********GET LAST INSERTED ID********/
        String customerSaveLastIdQuery = """SELECT id FROM customer_master WHERE customer_code=$customerCode ORDER BY id ASC""";
        def lastInseredIdArr = db.rows(customerSaveLastIdQuery);
        def lastInseredId = lastInseredIdArr[0]['id'];

        ///GET BANK ACCOUNT NO////
        def noTransBankAccountNo = params.noTransBankAccountNo

        String customerBankAccountQuery = """SELECT by_bank_account_no FROM bank_statement_import_details_final WHERE id=$noTransBankAccountNo ORDER BY id ASC""";
        def customerBankAccountArr = db.rows(customerBankAccountQuery);
        def customerBankAccount = customerBankAccountArr[0]['by_bank_account_no']
        def bankAccountName = params.bankAccountName
        def ibanPrefix = params.ibanPrefix

        String customerBankSaveQuery = """
                INSERT INTO customer_bank_account( version, bank_account_name, bank_account_no, customer_id, iban_prefix,
                    STATUS) VALUES( '0', '$bankAccountName', '$customerBankAccount', '$lastInseredId',
                        '$ibanPrefix', 1) """;
        db.execute(customerBankSaveQuery);
    }

    def saveExistingVendorForUnknownBankStatement(def bankAccountNo,def vendorId) {

        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String vendorBankAccountQuery = """SELECT vendor_id FROM vendor_bank_account WHERE bank_account_no='${bankAccountNo}' ORDER BY id ASC""";
        def vendorBankAccountArr = db.rows(vendorBankAccountQuery);

        if(vendorBankAccountArr.size() == 0){

            def bankAccountName = new CoreParamsHelperTagLib().getVendorName(vendorId);
            def ibanPrefix = new CoreParamsHelperTagLib().getIbanPrefixFromAccountNo(bankAccountNo);

            String vendorBankSaveQuery = """
                INSERT INTO vendor_bank_account( version, bank_account_name, bank_account_no, vendor_id, iban_prefix,
                    STATUS) VALUES( '0', '$bankAccountName', '$bankAccountNo', '$vendorId', '$ibanPrefix', '1') """;
            db.execute(vendorBankSaveQuery);
        }

    }


    def saveExistingVendorForUnknownBankStatement(params) {
        //def db = new Sql(dataSource)
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        def lastInseredId = params.vendorId

        ///GET BANK ACCOUNT NO////
        def noTransBankAccountNo = params.noTransBankAccountNo

        String customerBankAccountQuery = """SELECT by_bank_account_no FROM bank_statement_import_details_final WHERE id=$noTransBankAccountNo ORDER BY id ASC""";
        def customerBankAccountArr = db.rows(customerBankAccountQuery);
        def customerBankAccount = customerBankAccountArr[0]['by_bank_account_no']
        def bankAccountName = params.bankAccountName
        def ibanPrefix = params.ibanPrefix

        String vendorBankSaveQuery = """
                INSERT INTO vendor_bank_account( version, bank_account_name, bank_account_no, vendor_id, iban_prefix,
                    STATUS) VALUES( '0', '$bankAccountName', '$customerBankAccount', '$lastInseredId', '$ibanPrefix', '1') """;
        db.execute(vendorBankSaveQuery);
    }

    def saveExistingCustomerForUnknownBankStatement(def bankAccountNo,def customerId) {
        //def db = new Sql(dataSource)
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String customerBankAccountQuery = """SELECT customer_id FROM customer_bank_account WHERE bank_account_no='${bankAccountNo}' ORDER BY id ASC""";
        def customerBankAccountArr = db.rows(customerBankAccountQuery);

        if(customerBankAccountArr.size() == 0){
            def bankAccountName = new CoreParamsHelperTagLib().getCustomerName(customerId);
            def ibanPrefix = new CoreParamsHelperTagLib().getIbanPrefixFromAccountNo(bankAccountNo);

            String customerBankSaveQuery = """INSERT INTO customer_bank_account( version, bank_account_name, bank_account_no, customer_id, iban_prefix,
                    STATUS) VALUES( '0', '$bankAccountName', '$bankAccountNo', '$customerId', '$ibanPrefix', '1') """;
            db.execute(customerBankSaveQuery);
        }

    }

    def saveExistingCustomerForUnknownBankStatement(params) {
        //def db = new Sql(dataSource)
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)
        def lastInseredId = params.customerId

        ///GET BANK ACCOUNT NO////
        def noTransBankAccountNo = params.noTransBankAccountNo

        String customerBankAccountQuery = """SELECT by_bank_account_no FROM bank_statement_import_details_final WHERE id=$noTransBankAccountNo ORDER BY id ASC""";
        def customerBankAccountArr = db.rows(customerBankAccountQuery);
        def customerBankAccount = customerBankAccountArr[0]['by_bank_account_no']
        def bankAccountName = params.bankAccountName
        def ibanPrefix = params.ibanPrefix

        String customerBankSaveQuery = """INSERT INTO customer_bank_account( version, bank_account_name, bank_account_no, customer_id, iban_prefix,
                    STATUS) VALUES( '0', '$bankAccountName', '$customerBankAccount', '$lastInseredId', '$ibanPrefix', '1') """;
        db.execute(customerBankSaveQuery);
    }

/**
 * For exiting private Budget
 * */

    def saveExistingPrivateForUnknownBankStatement(params) {
        //def db = new Sql(dataSource)
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)
        def lastInseredId = params.budgetIds

        ///GET BANK ACCOUNT NO////
        def noTransBankAccountNo = params.noTransBankAccountNo

        String BankAccountQuery = """SELECT by_bank_account_no FROM bank_statement_import_details_final WHERE id=$noTransBankAccountNo ORDER BY id ASC""";
        def BankAccountArr = db.rows(BankAccountQuery);
        def BankAccount = BankAccountArr[0]['by_bank_account_no']
        def bankAccountName = ""
        def ibanPrefix = ""
        def type
        if (BankAccount.isNumber()) {
            type = 1
            ibanPrefix = params.ibanPrefix
            bankAccountName = params.bankAccountName
        } else {
            def bankAccountNameArr = db.rows("""SELECT budget_name FROM private_budget_master where id= ${
                lastInseredId
            } """)
            bankAccountName = bankAccountNameArr[0][0]
            type = 2
        }

        Map details = [
                bankAccountName: bankAccountName,
                bankAccountNo  : BankAccount,
                ibanPrefix     : ibanPrefix,
                budgetMasterId : lastInseredId,
                status         : 1,
                type           : type
        ]
        def tableName = "privateBudgetBankAccount"
        new BudgetViewDatabaseService().insert(details, tableName)

    }


    def saveNewCompanyForUnknownBankStatement(params) {
        //def db = new Sql(dataSource)
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        def bankAccountCode = params.bankAccountCode
        def bankAccountName = params.bankAccountName

        ///GET BANK ACCOUNT NO////
        def noTransBankAccountNo = params.noTransBankAccountNo

        String customerBankAccountQuery = """SELECT by_bank_account_no FROM bank_statement_import_details_final WHERE id=$noTransBankAccountNo ORDER BY id ASC""";
        def customerBankAccountArr = db.rows(customerBankAccountQuery);
        def bankAccountNo = customerBankAccountArr[0]['by_bank_account_no']
        def bankAccountType = params.bankAccountType
        def bankAddress = params.bankAddress
        def bankCurrCodeId = params.bankCurrCode.id
        def bankName = params.bankName
        def iban = params.iban

        String companyBankSaveQuery = """
                INSERT INTO company_bank_accounts( version, bank_account_code, bank_account_name, bank_account_no,
                    bank_account_type, bank_address, bank_curr_code_id, bank_name, iban, STATUS) VALUES( '0',
                       '$bankAccountCode', '$bankAccountName', '$bankAccountNo', '$bankAccountType', '$bankAddress',
                        '$bankCurrCodeId', '$bankName', '$iban', '1') """;
        db.execute(companyBankSaveQuery);

        def glChartCode = params.glChartCode

        String companyBankGLQuery = """
                INSERT INTO company_bank_gl_relation( version, bank_account_code, gl_chart_code, STATUS) VALUES( '0',
                       '$bankAccountCode', '$glChartCode', '1') """;
        db.execute(companyBankGLQuery);
    }

    def List getOpenInvoices(def customerOrVendorId, def customerOrVendorType, def bankId=null, def filterOnOff="true") {

        ArrayList invoices = new ArrayList()
        boolean isSameDebtorOrCustomer = false
        Double bankAmount = 0.0
        int foundPmntRfrncMatch = -100
        String debtorOrVendorName =''
        String type = null

        if (customerOrVendorId == ''){
            if (filterOnOff.equals("false")){

                def bankDescriptionNumbersWithType
                print("----targetedBlock----")

                bankDescriptionNumbersWithType = getBankTransactionDataForBookedInvoiecReceiptDesc(bankId);

                type = bankDescriptionNumbersWithType.typeCreditOrDebit
                String bankDescriptionNumbers = bankDescriptionNumbersWithType.paymentReferencesStr;
                int length = bankDescriptionNumbersWithType.paymentReferenceLength
                isSameDebtorOrCustomer = bankDescriptionNumbersWithType.sameDebtorOrCustomer
                bankAmount = bankDescriptionNumbersWithType.bankAmount
                foundPmntRfrncMatch = bankDescriptionNumbersWithType.foundPmntRfrncMatch
                debtorOrVendorName = bankDescriptionNumbersWithType.debtorOrVendorName
                print(type)
                if(type.equalsIgnoreCase("C")){
                    if(length>0){
                        print("found paymentRef: "+bankDescriptionNumbers)
                        invoices = new BudgetViewDatabaseService().executeQuery("""SELECT ii.id,
                                                                                               ii.bookingPeriod,
                                                                                               ii.bookingYear,
                                                                                               ii.dueDate,
                                                                                               ii.transDate,
                                                                                               ii.totalGlAmount,
                                                                                               ii.totalVat,
                                                                                               ROUND((ii.totalGlAmount+ii.totalVat),2) AS totalAmount,
                                                                                               CONCAT(sp.prefix,'-',ii.invoiceNo) AS invoiceNumber,
                                                                                               ROUND(SUM(tm.amount),2) AS remainAmount,
                                                                                               ii.budgetCustomerId AS budgetId,
                                                                                               ii.comments,
                                                                                               ii.paymentRef,
                                                                                               cm.customerName
                                                                                        FROM InvoiceIncome AS ii
                                                                                        LEFT JOIN TransMaster tm ON tm.recenciliationCode = CONCAT(ii.id,'#1')
                                                                                        AND tm.account_code=
                                                                                          (SELECT debitor_gl_code
                                                                                           FROM debit_credit_gl_setup)
                                                                                        LEFT JOIN SystemPrefix AS sp ON sp.id=8
                                                                                        LEFT JOIN customerMaster cm ON cm.id = ii.debtorId
                                                                                        WHERE ii.paymentRef IN (${bankDescriptionNumbers})
                                                                                        GROUP BY tm.recenciliation_code,
                                                                                                 ii.id
                                                                                        HAVING remainAmount!= 0
                                                                                        ORDER BY ii.id ASC""")

                    }
                    else {
                        print("no result found:type: "+type+" bankId: "+bankId + " bankAmount: "+bankAmount)
                        invoices = new BudgetViewDatabaseService().executeQuery("""SELECT ii.id,
                                                                                                       ii.bookingPeriod,
                                                                                                       ii.bookingYear,
                                                                                                       ii.dueDate,
                                                                                                       ii.transDate,
                                                                                                       ii.totalGlAmount,
                                                                                                       ii.totalVat,
                                                                                                       ROUND((ii.totalGlAmount+ii.totalVat),2) AS totalAmount,
                                                                                                       CONCAT(sp.prefix,'-',ii.invoiceNo) AS invoiceNumber,
                                                                                                       ROUND(SUM(tm.amount),2) AS remainAmount,
                                                                                                       ii.budgetCustomerId AS budgetId,
                                                                                                       ii.comments,
                                                                                                       ii.paymentRef,
                                                                                                       cm.customerName
                                                                                                FROM InvoiceIncome AS ii
                                                                                                LEFT JOIN TransMaster tm ON tm.recenciliationCode = CONCAT(ii.id,'#1')
                                                                                                AND tm.account_code=
                                                                                                  (SELECT debitor_gl_code
                                                                                                   FROM debit_credit_gl_setup)
                                                                                                LEFT JOIN SystemPrefix AS sp ON sp.id=8
                                                                                                LEFT JOIN customerMaster cm ON cm.id = ii.debtorId
                                                                                                WHERE ROUND((ii.total_gl_amount+ii.total_vat),2) = "${bankAmount}"
                                                                                                GROUP BY tm.recenciliation_code,
                                                                                                         ii.id
                                                                                                HAVING remainAmount!= 0
                                                                                                ORDER BY ii.id ASC""")

                        if(invoices.size()<=0){
                            print("nothing found on amount")
                            invoices = new BudgetViewDatabaseService().executeQuery("""SELECT ii.id,
                                                                                                       ii.bookingPeriod,
                                                                                                       ii.bookingYear,
                                                                                                       ii.dueDate,
                                                                                                       ii.transDate,
                                                                                                       ii.totalGlAmount,
                                                                                                       ii.totalVat,
                                                                                                       ROUND((ii.totalGlAmount+ii.totalVat),2) AS totalAmount,
                                                                                                       CONCAT(sp.prefix,'-',ii.invoiceNo) AS invoiceNumber,
                                                                                                       ROUND(SUM(tm.amount),2) AS remainAmount,
                                                                                                       ii.budgetCustomerId AS budgetId,
                                                                                                       ii.comments,
                                                                                                       ii.paymentRef,
                                                                                                       cm.customerName
                                                                                                FROM InvoiceIncome AS ii
                                                                                                LEFT JOIN TransMaster tm ON tm.recenciliationCode = CONCAT(ii.id,'#1')
                                                                                                AND tm.account_code=
                                                                                                  (SELECT debitor_gl_code
                                                                                                   FROM debit_credit_gl_setup)
                                                                                                LEFT JOIN SystemPrefix AS sp ON sp.id=8
                                                                                                LEFT JOIN customerMaster cm ON cm.id = ii.debtorId
                                                                                                GROUP BY tm.recenciliation_code,
                                                                                                         ii.id
                                                                                                HAVING remainAmount!= 0
                                                                                                ORDER BY ii.id ASC""")
                            print("fetched all data")
                        }
                        else {
                            print('found on amount')
                            ArrayList<String> invoiceList = new ArrayList<String>()

                            for(int i=0; i<invoices.size();i++){
                                def values = invoices[i][8].split('-')
                                String invNo = values[1]
                                invoiceList.add(invNo)
                            }

                            isSameDebtorOrCustomer = checkSameDebtorOrVendor(type,invoiceList)
                        }
                    }

                }
                else if (type.equalsIgnoreCase("D")){
                    if(length>0){
                        print("found paymentRef: "+bankDescriptionNumbers)
                        invoices = new BudgetViewDatabaseService().executeQuery("""SELECT ie.id,
                                                                                                       ie.booking_period,
                                                                                                       ie.booking_year,
                                                                                                       ie.due_date,
                                                                                                       ie.trans_date,
                                                                                                       ie.total_gl_amount,
                                                                                                       ie.total_vat,
                                                                                                       ROUND(ie.fact_inv_amount-(ie.total_gl_amount+ie.total_vat),2) AS totalAmount,
                                                                                                       (CASE ie.is_book_receive
                                                                                                            WHEN '0' THEN CONCAT(sp.prefix,'-',ie.invoice_no)
                                                                                                            WHEN '1' THEN CONCAT(sp2.prefix,'-',ie.invoice_no)
                                                                                                        END) AS invoiceNumber,
                                                                                                       ROUND(SUM(tm.amount*(-1)),2) AS remainAmount,
                                                                                                       ie.budget_vendor_id AS budgetId,
                                                                                                       ie.comments,
                                                                                                       ie.paymentRef,
                                                                                                       vm.vendor_name 
                                                                                                FROM invoice_expense AS ie
                                                                                                INNER JOIN trans_master tm ON (tm.recenciliation_code = CONCAT(ie.id,'#2')
                                                                                                                               OR tm.recenciliation_code = CONCAT(ie.id,'#4'))
                                                                                                AND tm.account_code =
                                                                                                  (SELECT creditor_gl_code
                                                                                                   FROM debit_credit_gl_setup)
                                                                                                LEFT JOIN system_prefix AS sp ON sp.id=7
                                                                                                LEFT JOIN system_prefix AS sp2 ON sp2.id=12
                                                                                                LEFT JOIN vendor_master vm ON vm.id = ie.vendor_id
                                                                                                WHERE ie.paymentRef IN (${bankDescriptionNumbers})
                                                                                                GROUP BY tm.recenciliation_code,
                                                                                                         ie.id
                                                                                                HAVING remainAmount!= 0
                                                                                                ORDER BY ie.id ASC""")

                    }
                    else {
                        print("no result found:type: "+type+" bankId: "+bankId + " bankAmount: "+bankAmount )
                        invoices = new BudgetViewDatabaseService().executeQuery("""SELECT ie.id,
                                                                                                       ie.booking_period,
                                                                                                       ie.booking_year,
                                                                                                       ie.due_date,
                                                                                                       ie.trans_date,
                                                                                                       ie.total_gl_amount,
                                                                                                       ie.total_vat,
                                                                                                       ROUND(ie.fact_inv_amount-(ie.total_gl_amount+ie.total_vat),2) AS totalAmount,
                                                                                                       (CASE ie.is_book_receive
                                                                                                            WHEN '0' THEN CONCAT(sp.prefix,'-',ie.invoice_no)
                                                                                                            WHEN '1' THEN CONCAT(sp2.prefix,'-',ie.invoice_no)
                                                                                                        END) AS invoiceNumber,
                                                                                                       ROUND(SUM(tm.amount*(-1)),2) AS remainAmount,
                                                                                                       ie.budget_vendor_id AS budgetId,
                                                                                                       ie.comments,
                                                                                                       ie.paymentRef,
                                                                                                       vm.vendor_name
                                                                                                FROM invoice_expense AS ie
                                                                                                INNER JOIN trans_master tm ON (tm.recenciliation_code = CONCAT(ie.id,'#2')
                                                                                                                               OR tm.recenciliation_code = CONCAT(ie.id,'#4'))
                                                                                                AND tm.account_code =
                                                                                                  (SELECT creditor_gl_code
                                                                                                   FROM debit_credit_gl_setup)
                                                                                                LEFT JOIN system_prefix AS sp ON sp.id=7
                                                                                                LEFT JOIN system_prefix AS sp2 ON sp2.id=12
                                                                                                LEFT JOIN vendor_master vm ON vm.id = ie.vendor_id
                                                                                                WHERE ROUND(ie.fact_inv_amount-(ie.total_gl_amount+ie.total_vat),2) = "${bankAmount}"
                                                                                                GROUP BY tm.recenciliation_code,
                                                                                                         ie.id
                                                                                                HAVING remainAmount!= 0
                                                                                                ORDER BY ie.id ASC""")
                        if(invoices.size()<=0){
                            print("nothing found on amount")
                            invoices = new BudgetViewDatabaseService().executeQuery("""SELECT ie.id,
                                                                                                       ie.booking_period,
                                                                                                       ie.booking_year,
                                                                                                       ie.due_date,
                                                                                                       ie.trans_date,
                                                                                                       ie.total_gl_amount,
                                                                                                       ie.total_vat,
                                                                                                       ROUND(ie.fact_inv_amount-(ie.total_gl_amount+ie.total_vat),2) AS totalAmount,
                                                                                                       (CASE ie.is_book_receive
                                                                                                            WHEN '0' THEN CONCAT(sp.prefix,'-',ie.invoice_no)
                                                                                                            WHEN '1' THEN CONCAT(sp2.prefix,'-',ie.invoice_no)
                                                                                                        END) AS invoiceNumber,
                                                                                                       ROUND(SUM(tm.amount*(-1)),2) AS remainAmount,
                                                                                                       ie.budget_vendor_id AS budgetId,
                                                                                                       ie.comments,
                                                                                                       ie.paymentRef,
                                                                                                       vm.vendor_name
                                                                                                FROM invoice_expense AS ie
                                                                                                INNER JOIN trans_master tm ON (tm.recenciliation_code = CONCAT(ie.id,'#2')
                                                                                                                               OR tm.recenciliation_code = CONCAT(ie.id,'#4'))
                                                                                                AND tm.account_code =
                                                                                                  (SELECT creditor_gl_code
                                                                                                   FROM debit_credit_gl_setup)
                                                                                                LEFT JOIN system_prefix AS sp ON sp.id=7
                                                                                                LEFT JOIN system_prefix AS sp2 ON sp2.id=12
                                                                                                LEFT JOIN vendor_master vm ON vm.id = ie.vendor_id
                                                                                                GROUP BY tm.recenciliation_code,
                                                                                                         ie.id
                                                                                                HAVING remainAmount!= 0
                                                                                                ORDER BY ie.id ASC""")
                            print("fetched all data")
                        }
                        else {
                            print('found on amount')
                            ArrayList<String> invoiceList = new ArrayList<String>()

                            for(int i=0; i<invoices.size();i++){
                                def values = invoices[i][8].split('-')
                                String invNo = values[1]
                                invoiceList.add(invNo)
                            }

                            isSameDebtorOrCustomer = checkSameDebtorOrVendor(type,invoiceList)
                        }

                    }
                }

            } else { // when true
                if(customerOrVendorType == "CUS"){

                    invoices = new BudgetViewDatabaseService().executeQuery("""SELECT ii.id,
                                                                                                       ii.bookingPeriod,
                                                                                                       ii.bookingYear,
                                                                                                       ii.dueDate,
                                                                                                       ii.transDate,
                                                                                                       ii.totalGlAmount,
                                                                                                       ii.totalVat,
                                                                                                       ROUND((ii.totalGlAmount+ii.totalVat),2) AS totalAmount,
                                                                                                       CONCAT(sp.prefix,'-',ii.invoiceNo) AS invoiceNumber,
                                                                                                       ROUND(SUM(tm.amount),2) AS remainAmount,
                                                                                                       ii.budgetCustomerId AS budgetId,
                                                                                                       ii.comments,
                                                                                                       ii.paymentRef,
                                                                                                       cm.customerName
                                                                                                FROM InvoiceIncome AS ii
                                                                                                LEFT JOIN TransMaster tm ON tm.recenciliationCode = CONCAT(ii.id,'#1')
                                                                                                AND tm.account_code=
                                                                                                  (SELECT debitor_gl_code
                                                                                                   FROM debit_credit_gl_setup)
                                                                                                LEFT JOIN SystemPrefix AS sp ON sp.id=8
                                                                                                LEFT JOIN customerMaster cm ON cm.id = ii.debtorId
                                                                                                GROUP BY tm.recenciliation_code,
                                                                                                         ii.id
                                                                                                HAVING remainAmount!= 0
                                                                                                ORDER BY ii.id ASC""")


                }
                else{
                    invoices = new BudgetViewDatabaseService().executeQuery("""SELECT ie.id,
                                                                                                       ie.booking_period,
                                                                                                       ie.booking_year,
                                                                                                       ie.due_date,
                                                                                                       ie.trans_date,
                                                                                                       ie.total_gl_amount,
                                                                                                       ie.total_vat,
                                                                                                       ROUND(ie.fact_inv_amount-(ie.total_gl_amount+ie.total_vat),2) AS totalAmount,
                                                                                                       (CASE ie.is_book_receive
                                                                                                            WHEN '0' THEN CONCAT(sp.prefix,'-',ie.invoice_no)
                                                                                                            WHEN '1' THEN CONCAT(sp2.prefix,'-',ie.invoice_no)
                                                                                                        END) AS invoiceNumber,
                                                                                                       ROUND(SUM(tm.amount*(-1)),2) AS remainAmount,
                                                                                                       ie.budget_vendor_id AS budgetId,
                                                                                                       ie.comments,
                                                                                                       ie.paymentRef,
                                                                                                       vm.vendor_name
                                                                                                FROM invoice_expense AS ie
                                                                                                INNER JOIN trans_master tm ON (tm.recenciliation_code = CONCAT(ie.id,'#2')
                                                                                                                               OR tm.recenciliation_code = CONCAT(ie.id,'#4'))
                                                                                                AND tm.account_code =
                                                                                                  (SELECT creditor_gl_code
                                                                                                   FROM debit_credit_gl_setup)
                                                                                                LEFT JOIN system_prefix AS sp ON sp.id=7
                                                                                                LEFT JOIN system_prefix AS sp2 ON sp2.id=12
                                                                                                LEFT JOIN vendor_master vm ON vm.id = ie.vendor_id
                                                                                                GROUP BY tm.recenciliation_code,
                                                                                                         ie.id
                                                                                                HAVING remainAmount!= 0
                                                                                                ORDER BY ie.id ASC""")

                }
            }
        } else {
            if (customerOrVendorType == "CUS"){
                invoices = new BudgetViewDatabaseService().executeQuery("SELECT ii.id,ii.bookingPeriod,ii.bookingYear,ii.dueDate,ii.transDate,ii.totalGlAmount," +
                        "ii.totalVat,ROUND((ii.totalGlAmount+ii.totalVat),2) AS totalAmount," +
                        "CONCAT(sp.prefix,'-',ii.invoiceNo) AS invoiceNumber," +
                        "ROUND(SUM(tm.amount),2) AS remainAmount, " +
                        "ii.budgetCustomerId As budgetId,ii.comments,ii.paymentRef FROM InvoiceIncome AS ii " +
                        //"LEFT JOIN TransMaster tm ON tm.recenciliationCode  = CONCAT(ii.id,'#1') and tm.accountCode='"+recenciliationBankGL+"' "+
                        "LEFT JOIN TransMaster tm ON tm.recenciliationCode  = CONCAT(ii.id,'#1') AND tm.account_code=(Select debitor_gl_code from debit_credit_gl_setup) " +
                        "LEFT JOIN SystemPrefix AS sp ON sp.id=8  " +
                        "WHERE ii.debtorId=" + customerOrVendorId +  "  GROUP BY tm.recenciliation_code,ii.id HAVING remainAmount!= 0 ORDER BY ii.id ASC")

                def getTotalAmount = budgetViewDatabaseService.executeQuery("""SELECT SUM(b.remain_amount) FROM bank_statement_import_final AS a
                                                                                        LEFT JOIN bank_statement_import_details_final AS b ON a.id = b.bank_import_id
                                                                                        WHERE  ROUND((b.amount - b.reconciliated_amount),2) != 0  AND b.skip_account != 2
                                                                                        AND b.by_bank_account_no='${bankId}'""")
                bankAmount = getTotalAmount[0][0]

            } else {
                invoices = new BudgetViewDatabaseService().executeQuery("SELECT ie.id,ie.booking_period,ie.booking_year,ie.due_date,ie.trans_date,ie.total_gl_amount," +
                        "ie.total_vat," +
                        "ROUND(ie.fact_inv_amount-(ie.total_gl_amount+ie.total_vat),2) AS totalAmount," +
                        "(CASE ie.is_book_receive WHEN '0' THEN CONCAT(sp.prefix,'-',ie.invoice_no) WHEN '1' THEN CONCAT(sp2.prefix,'-',ie.invoice_no) END) AS invoiceNumber," +
                        "ROUND(SUM(tm.amount*(-1)),2) AS remainAmount, " +
                        "ie.budget_vendor_id As budgetId,ie.comments,ie.paymentRef FROM invoice_expense AS ie " +
                        //"LEFT JOIN trans_master tm ON tm.recenciliation_code  = CONCAT(ie.id,'#2') and tm.account_code='"+recenciliationBankGL+"' "+
                        "INNER JOIN trans_master tm ON (tm.recenciliation_code = CONCAT(ie.id,'#2') OR tm.recenciliation_code = CONCAT(ie.id,'#4')) " +
                        "AND tm.account_code =(Select creditor_gl_code from debit_credit_gl_setup) " +
                        "LEFT JOIN system_prefix AS sp ON sp.id=7  " +
                        "LEFT JOIN system_prefix AS sp2 ON sp2.id=12  " +
                        "WHERE ie.vendor_id=" + customerOrVendorId + " GROUP BY tm.recenciliation_code,ie.id HAVING remainAmount!= 0 ORDER BY ie.id ASC")

                def getTotalAmount = budgetViewDatabaseService.executeQuery("""SELECT SUM(b.remain_amount) FROM bank_statement_import_final AS a
                                                                                        LEFT JOIN bank_statement_import_details_final AS b ON a.id = b.bank_import_id
                                                                                        WHERE  ROUND((b.amount - b.reconciliated_amount),2) != 0  AND b.skip_account != 2
                                                                                        AND b.by_bank_account_no='${bankId}'""")
                bankAmount = getTotalAmount[0][0]
            }

        }

        List<Map> list = new ArrayList<Map>();
        for (def invoice in invoices) {
            if (invoice[0] == null) {
                continue;
            }

            Map map = new HashMap();

            map.put("id", invoice[0]);
            map.put("bookingPeriod", invoice[1]);
            map.put("bookingYear", invoice[2]);
            map.put("dueDate", invoice[3]);
            map.put("transDate", invoice[4]);
            map.put("totalGlAmount", invoice[5]);
            map.put("totalVat", invoice[6]);
            map.put("totalAmount", invoice[7]);
            map.put("invoiceNumber", invoice[8]);
            if (invoice[9] != null) {
                map.put("remainAmount", invoice[9]);
            } else {
                map.put("remainAmount", new Double(0.00));
            }

            //map.put("paidAmount",invoice[9]?((masterType==1?1:-1)*invoice[9]):new Double(0.00));
            map.put("paidAmount", (map.totalAmount - map.remainAmount));
            map.put("masterId", invoice[10]);
            map.put("comments", invoice[11]);
            map.put("paymentRef", invoice[12]);
            map.put("reconciliatedAmount", map.paidAmount);
            //map.put("remainAmount",(map.totalAmount - map.reconciliatedAmount));
            map.put("tempRemainAmount", map.remainAmount);
            map.put("reconciliation", new Double(0.00));
            map.put("reconAmounts", new ArrayList<Double>());
            map.put("bankStatementDetailsFinalId", "");
            map.put("isSameDebtorOrCustomer", isSameDebtorOrCustomer);
            map.put("bankAmount", bankAmount);
            map.put("foundPmntRfrncMatch", foundPmntRfrncMatch);
            map.put("debtorOrVendorName", invoice[13]);
            map.put("type",type)
            if (map.remainAmount != 0.00) {
                list.add(map);
            }

        }

        return list;
    }

    def List<Map> getMatchedInvoices(List bankStatements, List openInvoices) {

        List<Map> matchedInvoices = new ArrayList<Map>();
        List<Long> processedInvoices = new ArrayList<Long>();

        boolean matched = false;
        for (def statement in bankStatements) {
            for (def invoice in openInvoices) {

                double bankStatementRemainAmount = statement.tempRemainAmount
                def debitCredit = statement.debitCredit
                double invoiceRemainAmount = invoice.remainAmount;
                matched = false;

                def tempfirstTransDate = statement.transDateTime
                Integer tempfirstYear = Integer.parseInt(tempfirstTransDate.substring(0, 2)) + 100
                Integer tempfirstMonth = Integer.parseInt(tempfirstTransDate.substring(2, 4))
                Integer tempfirstDay = Integer.parseInt(tempfirstTransDate.substring(4, 6))

                def transDateStr = new Date(tempfirstYear, tempfirstMonth - 1, tempfirstDay)
                transDateStr = new SimpleDateFormat("yyyy-MM-dd").format(transDateStr)

                def invoiceDateStr = invoice.transDate
                invoiceDateStr = new SimpleDateFormat("yyyy-MM-dd").format(invoiceDateStr)


                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date invoiceDate = sdf.parse(invoiceDateStr);
                Date transDate = sdf.parse(transDateStr);

                if (debitCredit.equals("C") || debitCredit.equals("D")) {
                    //if((masterType==1 && debitCredit.equals("C")) || (masterType==2 && debitCredit.equals("D"))) {
                    if (!processedInvoices.contains(invoice.id) && (invoiceRemainAmount > 0.0 || invoiceRemainAmount < 0.0)) {
                        if (invoiceRemainAmount == bankStatementRemainAmount && (invoiceDate.before(transDate) || invoiceDate.equals(transDate))) {
                            statement.invoices.add(invoice.invoiceNumber);
                            statement.tempRemainAmount = new Double(0.0);
                            invoice.tempRemainAmount = new Double(0.0);
                            invoice.reconciliation = invoice.remainAmount;
                            invoice.bankStatementDetailsFinalId = statement.id;
                            invoice.reconAmounts.add(invoiceRemainAmount);
                            matched = true;
                            matchedInvoices.add(invoice);
                            processedInvoices.add(invoice.id);
                            break;
                        }
                    }
                }

            }
        }

        if (matched == false) {
            matchedInvoices = new ArrayList<Map>();
        } else {

            for (def invoice in openInvoices) {
                if (!processedInvoices.contains(invoice.id)) {
                    invoice.tempRemainAmount = invoice.remainAmount;
                    invoice.reconciliation = new Double(0.0);
                    matchedInvoices.add(invoice);
                }
            }

        }

        return matchedInvoices;
    }

    def getMatchedTransactionIdList(def bankStatements, def openInvoices) {

        def matchIds =""
        List<Long> processedInvoices = new ArrayList<Long>();

        for (def statement in bankStatements) {
            for (def invoice in openInvoices) {

                double bankStatementRemainAmount = statement.tempRemainAmount
                def debitCredit = statement.debitCredit
                double invoiceRemainAmount = invoice.remainAmount;

                def tempfirstTransDate = statement.transDateTime
                Integer tempfirstYear = Integer.parseInt(tempfirstTransDate.substring(0, 2)) + 100
                Integer tempfirstMonth = Integer.parseInt(tempfirstTransDate.substring(2, 4))
                Integer tempfirstDay = Integer.parseInt(tempfirstTransDate.substring(4, 6))

                def transDateStr = new Date(tempfirstYear, tempfirstMonth - 1, tempfirstDay)
                transDateStr = new SimpleDateFormat("yyyy-MM-dd").format(transDateStr)

                def invoiceDateStr = invoice.transDate
                invoiceDateStr = new SimpleDateFormat("yyyy-MM-dd").format(invoiceDateStr)


                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date invoiceDate = sdf.parse(invoiceDateStr);
                Date transDate = sdf.parse(transDateStr);

                //matched = false;
                if (debitCredit.equals("C") || debitCredit.equals("D")) {
                    //if((masterType==1 && debitCredit.equals("C")) || (masterType==2 && debitCredit.equals("D"))) {
                    if (!processedInvoices.contains(invoice.id) && (invoiceRemainAmount > 0.0 || invoiceRemainAmount < 0.0)) {
                        if (invoiceRemainAmount == bankStatementRemainAmount && (invoiceDate.before(transDate) || invoiceDate.equals(transDate))) {
                            processedInvoices.add(invoice.id);

                            if(matchIds == ""){
                                matchIds = statement.id
                            } else{
                                matchIds = matchIds + "::"+statement.id
                            }
                            break;
                        }
                    }
                }

            }
        }

        return matchIds;
    }



    def List<Map> getBankStatementsByAccountNo(String companyBankAccountNo, String byAccountNo, String statementIds) {

        def comBankInstance = new CoreParamsHelperTagLib().getBankGlFromAccountNo(companyBankAccountNo)
        def recenciliationBankGL = comBankInstance[0]

        List bankStatementImportDetailsData = budgetViewDatabaseService.executeQuery("Select bs.id,bs.bank_import_id,sum(tm.amount) as paidAmount," +
                "bs.debit_credit,bs.fiscal_year_trans,bs.by_account_name,bs.by_bank_account_no,bs.description,bs.trans_date_time,bs.entry_timestamp," +
                "bs.amount as totalAmount FROM bank_statement_import_details_final bs " +
                "LEFT JOIN trans_master tm ON tm.invoice_no  = bs.bank_payment_id and tm.account_code='" + recenciliationBankGL + "' " +
                "Where bs.bank_import_id IN(" + statementIds + ") AND bs.by_bank_account_no='" + byAccountNo + "' AND bs.skip_account!=2 " +
                "AND bs.debit_credit='C' AND bs.reconcilated=0 GROUP BY bs.id ORDER BY id ASC")

        List<Map> list = new ArrayList<Map>();
        for (def bankStatement in bankStatementImportDetailsData) {
            if (!bankStatement[0]) {
                continue;
            }
            Map map = new HashMap();
            map.put("id", bankStatement[0]);
            map.put("bankImportId", bankStatement[1]);
            map.put("reconciliatedAmount", bankStatement[2] ? bankStatement[2] : new Double(0.00));
            map.put("debitCredit", bankStatement[3]);
            map.put("fiscalYearTrans", bankStatement[4]);
            map.put("byAccountName", bankStatement[5]);
            map.put("byBankAccountNo", bankStatement[6]);
            map.put("description", bankStatement[7]);
            map.put("transDateTime", bankStatement[8]);
            map.put("entryTimestamp", bankStatement[9]);
            map.put("amount", bankStatement[10]);
            map.put("remainAmount", map.amount - map.reconciliatedAmount);
            map.put("tempRemainAmount", map.remainAmount);
            map.put("invoices", new ArrayList<String>());
            if (map.remainAmount <= 0) {
                continue;
            }
            list.add(map);
        }

        if (list.size() == 0) {
            bankStatementImportDetailsData = budgetViewDatabaseService.executeQuery("Select bs.id,bs.bankImportId,sum(tm.amount) as paidAmount ,bs.debitCredit," +
                    "bs.fiscalYearTrans,bs.byAccountName,bs.byBankAccountNo,bs.description,bs.transDateTime,entryTimestamp," +
                    "bs.amount as totalAmount FROM BankStatementImportDetailsFinal bs " +
                    "LEFT JOIN TransMaster tm ON tm.invoiceNo  = bs.bankPaymentId and tm.accountCode='" + recenciliationBankGL + "' " +
                    "Where bs.bankImportId IN(" + statementIds + ") AND bs.byBankAccountNo='" + byAccountNo + "' AND bs.skip_account!=2  " +
                    "AND bs.debit_credit='D' AND bs.reconcilated=0 GROUP BY bs.id ORDER BY id ASC")
        } else {
            return list;
        }

        for (def bankStatement in bankStatementImportDetailsData) {
            if (!bankStatement[0]) {
                continue;
            }
            Map map = new HashMap();
            map.put("id", bankStatement[0]);
            map.put("bankImportId", bankStatement[1]);
            map.put("reconciliatedAmount", bankStatement[2] ? -1 * bankStatement[2] : new Double(0.00));
            map.put("debitCredit", bankStatement[3]);
            map.put("fiscalYearTrans", bankStatement[4]);
            map.put("byAccountName", bankStatement[5]);
            map.put("byBankAccountNo", bankStatement[6]);
            map.put("description", bankStatement[7]);
            map.put("transDateTime", bankStatement[8]);
            map.put("entryTimestamp", bankStatement[9]);
            map.put("amount", bankStatement[10]);
            map.put("remainAmount", map.amount - map.reconciliatedAmount);
            map.put("tempRemainAmount", map.remainAmount);
            map.put("invoices", new ArrayList<String>());
            if (map.remainAmount <= 0) {
                continue;
            }
            list.add(map);
        }
        return list;
    }

    def saveBookInvoiceReceiptPayment(def showMasterType, def reconNewInvoiceId, def reconNewBankTransId,
                                      def transType, def recenciliationCode,
                                      def recenciliationGL, def recenciliationBankGL,
                                      def invoiceInfo, def reconNewBankTransAmount,
                                      def db, def isDebit, def springSecurityService, def companyBankCode,
                                      def customerOrVendorId,def bankAccountCategory) {


        ///PAID STATUS UPDATE AT TRANS MASTER/////////////////////
        String updateTransMaster = "";
        if (showMasterType == 1) {
            updateTransMaster = """UPDATE trans_master SET recenciliation_code='$recenciliationCode' WHERE invoice_no='$reconNewInvoiceId' AND trans_type=1 AND account_code='$recenciliationGL'""";
        } else if (showMasterType == 2) {
            updateTransMaster = """UPDATE trans_master SET recenciliation_code='$recenciliationCode' WHERE invoice_no='$reconNewInvoiceId' AND trans_type=2 AND account_code='$recenciliationGL'""";
        }

        //BDR-4
        db.execute(updateTransMaster);

        Date tempTransDate = new Date()
        String TransDate = tempTransDate.format("yyyy-MM-dd hh:mm:ss")

        def transBookPeriod
        String strTransBookQuery = ""
        if (showMasterType == 1) {
            strTransBookQuery = """SELECT booking_period,booking_year FROM trans_master WHERE invoice_no='$invoiceInfo.invoiceId' AND trans_type=1 AND account_code='$recenciliationGL'"""
            transBookPeriod = new BudgetViewDatabaseService().executeQuery(strTransBookQuery);
        } else if (showMasterType == 2) {
            strTransBookQuery = """SELECT booking_period,booking_year FROM trans_master WHERE invoice_no='$invoiceInfo.invoiceId' AND trans_type=2 AND account_code='$recenciliationGL'"""
            transBookPeriod = new BudgetViewDatabaseService().executeQuery(strTransBookQuery);
        }

        def bookingPeriodTrans = 0
        def bookingYearTrans = 0
        if (transBookPeriod.size()) {
            bookingPeriodTrans = transBookPeriod[0][0]
            bookingYearTrans = transBookPeriod[0][1]
        }

        Integer bankPaymentId = new CoreParamsHelperTagLib().hasBankPaymentIdImportDetailsTable(reconNewBankTransId)

        if (bankPaymentId == 0) {
            def recenciliationEntryCode = ""
            recenciliationEntryCode = new CoreParamsHelperTagLib().getNextGeneratedNumberWithoutPrefix('recenciliationEntry')

            bankPaymentId = Integer.parseInt(recenciliationEntryCode.toString())
            new CoreParamsHelperTagLib().updateBankPaymentIdAtImportDetailsFinal(reconNewBankTransId, recenciliationEntryCode)
        }

        String updatebankStatementImportDetailsFinal = """UPDATE bank_statement_import_details_final SET remain_amount=ROUND(remain_amount-'$reconNewBankTransAmount',2),reconciliated_amount=ROUND(reconciliated_amount+'$reconNewBankTransAmount',2) WHERE id='$reconNewBankTransId' """;
        db.execute(updatebankStatementImportDetailsFinal);

        def remainAmountBankArr = new BudgetViewDatabaseService().executeQuery("SELECT remainAmount,transDateTime,entryTimestamp FROM bankStatementImportDetailsFinal WHERE id='${reconNewBankTransId}'")
        def remainAmountBankString = remainAmountBankArr[0][0]

        double remainAmountBank = 0
        if (remainAmountBankString) {
            remainAmountBank = Double.parseDouble(remainAmountBankString.toString())
        }

        if (remainAmountBank <= 0.00) {
            String updateIncomeInvoicePaidBankStatus = """UPDATE bank_statement_import_details_final SET reconcilated='1' WHERE id='$reconNewBankTransId' """;
            db.execute(updateIncomeInvoicePaidBankStatus);
        } else {
            String updateIncomeInvoicePaidBankStatus = """UPDATE bank_statement_import_details_final SET reconcilated='2' WHERE id='$reconNewBankTransId' """;
            db.execute(updateIncomeInvoicePaidBankStatus);
        }

        def TransDatex = new ReconciliationService().getReconTransactionDate(reconNewBankTransId)

        def reconRemainAmount = remainAmountBankArr
        def bookingPeriodTransx = 0
        def bookingYearTransx = 0
        String seedupriyaBookingDateTemp = reconRemainAmount[0][1]
        bookingPeriodTransx = seedupriyaBookingDateTemp.substring(2, 4)
        bookingYearTransx = "20" + seedupriyaBookingDateTemp.substring(0, 2)

        BigDecimal bdReconNewBankTransAmount = new BigDecimal(invoiceInfo.reconcilationAmount)
        reconNewBankTransAmount = bdReconNewBankTransAmount.setScale(2, BigDecimal.ROUND_HALF_UP)

        def bookingDate = new ApplicationUtil().convertDateFromMonthAndYear(bookingPeriodTransx,bookingYearTransx)
        def vendorId = 0;
        def customerId = 0;

        if (showMasterType == 1) {
            vendorId = 0;
            customerId = customerOrVendorId;
        } else if (showMasterType == 2) {
            vendorId = customerOrVendorId;
            customerId = 0;
        }

        def transTypeCode = 7;
        if(bankAccountCategory == 'pba'){
            transTypeCode = 10;
        }

        //Trans Master
        Map trnMasBooked = [
                accountCode       : recenciliationGL,
                amount            : isDebit ? reconNewBankTransAmount : -reconNewBankTransAmount,
                transDate         : TransDatex,
                transType         : transTypeCode,
                invoiceNo         : bankPaymentId,
                bookingPeriod     : bookingPeriodTransx,
                bookingYear       : bookingYearTransx,
                recenciliationCode: recenciliationCode,
                userId            : springSecurityService.principal.id,
                createDate        : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()),
                process           : com.bv.constants.Process.MANUAL_RECONCILIATION,
                customerId        : customerId,
                vendorId          : vendorId,
                bookingDate       : bookingDate
        ]

        def tableNametrnMasBooked = "TransMaster"  //BDR-4
        new BudgetViewDatabaseService().insert(trnMasBooked, tableNametrnMasBooked)

        Map trnMasBank = [
                accountCode       : recenciliationBankGL,
                amount            : isDebit ? -reconNewBankTransAmount : reconNewBankTransAmount,
                transDate         : TransDatex,
                transType         : transTypeCode,
                invoiceNo         : bankPaymentId,
                bookingPeriod     : bookingPeriodTransx,
                bookingYear       : bookingYearTransx,
                recenciliationCode: recenciliationCode,
                userId            : springSecurityService.principal.id,
                createDate        : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()),
                process           : com.bv.constants.Process.MANUAL_RECONCILIATION,
                customerId        : customerId,
                vendorId          : vendorId,
                bookingDate       : bookingDate
        ]

        def tableNametrnMasBank = "TransMaster"  //BDR-4
        new BudgetViewDatabaseService().insert(trnMasBank, tableNametrnMasBank)

        /////////////////////Seedupriya////////////////////////////////////////
        Map comBankTrans = [
                amount         : reconNewBankTransAmount,
                companyBankCode: companyBankCode,
                invoiceNo      : bankPaymentId,
                personCode     : 0,
                transDate      : TransDatex,
                transType      : transTypeCode,
                bookingPeriod  : bookingPeriodTransx,
                bookingYear    : bookingYearTransx
        ]
        def tableNamecomBankTrans = "CompanyBankTrans"
        new BudgetViewDatabaseService().insert(comBankTrans, tableNamecomBankTrans)

    }


    def getBankPaymentId(def reconNewBankTransId) {
        Integer bankPaymentId = new CoreParamsHelperTagLib().hasBankPaymentIdImportDetailsTable(reconNewBankTransId)

        if (bankPaymentId == 0) {
            def recenciliationEntryCode = ""
            recenciliationEntryCode = new CoreParamsHelperTagLib().getNextGeneratedNumberWithoutPrefix('recenciliationEntry')

            bankPaymentId = Integer.parseInt(recenciliationEntryCode.toString())
            new CoreParamsHelperTagLib().updateBankPaymentIdAtImportDetailsFinal(reconNewBankTransId, recenciliationEntryCode)
        }
        return bankPaymentId;
    }


    def saveReconcilationReservation(def detailsIdArr, def budgetIdDashboad, def budgetGLDashboad, def isDebit, def db) {

        def date = new Date()
        def formatter = new SimpleDateFormat("yyyy-MM-dd H:m:s")
        def currDate = formatter.format(date)

        if (detailsIdArr.size()) {
            detailsIdArr.each { phnDetails ->
//                def budgetItemIdArr = new BudgetViewDatabaseService().executeQueryAtSingle("""SELECT id FROM reservation_budget_item WHERE booking_period_month ='${phnDetails.bookingPeriod}' AND booking_period_year = '${phnDetails.bookingYear}' AND budget_name_id ='${budgetIdDashboad}'""")
                def budgetItemIdArr = new BudgetViewDatabaseService().executeQueryAtSingle("""SELECT id FROM reservation_budget_item
                                                                                            WHERE booking_period_month ='${phnDetails.bookingPeriod}'
                                                                                            AND booking_period_year = '${phnDetails.bookingYear}'
                                                                                            AND budget_name_id ='${budgetIdDashboad}' ORDER BY created_date LIMIT 1""")

                def budgetItemId = budgetItemIdArr[0]
                def trackingNumArr = new BudgetViewDatabaseService().executeQueryAtSingle("""SELECT reservation_code
                                                                    FROM reservation_budget_master WHERE id= '${budgetIdDashboad}'""")
                def trackingNum = trackingNumArr[0]

                def recenciliationEntryCode = ""

                ///Details ID have bank_payment_id/////////
                def previousBankPaymentId = new CoreParamsHelperTagLib().getPreviousBankPaymentID(phnDetails.id)
                if (previousBankPaymentId == '0') {
                    recenciliationEntryCode = new CoreParamsHelperTagLib().getNextGeneratedNumberWithoutPrefix('recenciliationEntry')
                } else {
                    recenciliationEntryCode = previousBankPaymentId
                }

                Map spendingArr = [
                        bookingPeriod : phnDetails.bookingPeriod,
                        bookingYear   : phnDetails.bookingYear,
                        budgetItemId  : budgetItemId,
                        budgetMasterId: budgetIdDashboad,
                        trackingNumber: trackingNum,
                        amount        : phnDetails.debitCredit == "C" ? phnDetails.amount : -1 * new BigDecimal(phnDetails.amount),
                        date          : currDate,
                        currency      : '',
                        userId        : springSecurityService.principal.id,
                        status        : 1,
                        bankPaymentId : recenciliationEntryCode,
//                        bankPaymentId : phnDetails.id,
                        transType     : 5
                ]

                def tableSpending = "privateReservationSpendingTrans"    //BDR-4
                def insertedId = new BudgetViewDatabaseService().insert(spendingArr, tableSpending)

                def bookedId = phnDetails.id
                def comBankInstance = new CoreParamsHelperTagLib().getReconciliationCompanyBankAccountByReconciliationId(bookedId)

                def personCOdeQuery = """ SELECT a.id,a.budget_name_id FROM reservation_budget_item AS a
                LEFT JOIN reservation_budget_item_details AS b ON a.id = b.reserv_budget_item_id
                LEFT JOIN reservation_budget_master AS v ON a.budget_name_id = v.id
                WHERE a.booking_period_month='${phnDetails.bookingPeriod}'
                AND a.booking_period_year='${phnDetails.bookingYear}'
                AND b.gl_account=${budgetGLDashboad} AND a.budget_name_id=${budgetIdDashboad} ORDER BY a.id ASC"""
                def personCodeArr = new BudgetViewDatabaseService().executeQueryAtSingle(personCOdeQuery)
                def personCode = personCodeArr[1]
                def newInvoiceNo = new ReconciliationService().getBankPaymentId(phnDetails.id)

                def bookingDate = new ApplicationUtil().convertDateFromMonthAndYear(phnDetails.bookingPeriod,phnDetails.bookingYear)

                if (comBankInstance.size()) {
                    //Trans Master
                    Map trnMas = [
                            accountCode       : budgetGLDashboad,
                            amount            : phnDetails.isDebit ? phnDetails.amount : -1 * new BigDecimal(phnDetails.amount),
                            transDate         : currDate,
                            transType         : 5,
                            invoiceNo         : newInvoiceNo,
                            recenciliationCode: newInvoiceNo + "#5",
                            bookingPeriod     : phnDetails.bookingPeriod,
                            bookingYear       : phnDetails.bookingYear,
                            userId            : springSecurityService.principal.id,
                            createDate        : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()),
                            process           : com.bv.constants.Process.MANUAL_RECONCILIATION,
                            customerId        : 0,
                            vendorId          : 0,
                            bookingDate       : bookingDate
                    ]

                    def tableNametrnMas = "TransMaster"    //BDR-4
                    new BudgetViewDatabaseService().insert(trnMas, tableNametrnMas)
                }

                //////////////////////////Creditor Entry IN Master Table/////////////////////
//                def creditorCreditGlSetupInfo = new CoreParamsHelperTagLib().getDebitCreditGlSetupInfo()

                //Trans Master
                BigDecimal tempAmount = new BigDecimal(phnDetails.amount);
                Map trnMasDr = [
                        accountCode       : comBankInstance[0],
//                        accountCode       : creditorCreditGlSetupInfo[1],
                        amount            : phnDetails.isDebit ? -1 * tempAmount : new BigDecimal(phnDetails.amount),
                        transDate         : currDate,
                        transType         : 5,
                        invoiceNo         : newInvoiceNo,
                        recenciliationCode: newInvoiceNo + "#5",
                        bookingPeriod     : phnDetails.bookingPeriod,
                        bookingYear       : phnDetails.bookingYear,
                        userId            : springSecurityService.principal.id,
                        createDate        : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()),
                        process           : com.bv.constants.Process.MANUAL_RECONCILIATION,
                        customerId        : 0,
                        vendorId          : 0,
                        bookingDate       : bookingDate
                ]

                def tableNametrnMasDr = "TransMaster"      //BDR-4
                new BudgetViewDatabaseService().insert(trnMasDr, tableNametrnMasDr)

                def comBankInstanceInvoice = new CoreParamsHelperTagLib().getCompanyBankAccountByGlAccount(budgetGLDashboad)
                if (comBankInstanceInvoice.size()) {

                    Map comBankTrans = [
                            amount         : phnDetails.isDebit ? phnDetails.amount : -1 * new BigDecimal(phnDetails.amount),
                            companyBankCode: comBankInstanceInvoice[0],//need to fixed
                            invoiceNo      : insertedId,
                            personCode     : personCode.toString(),
                            transDate      : currDate,
                            transType      : 5,
                            bookingPeriod  : phnDetails.bookingPeriod,
                            bookingYear    : phnDetails.bookingYear
                    ]
                    def tableNamecomBankTrans = "CompanyBankTrans"
                    new BudgetViewDatabaseService().insert(comBankTrans, tableNamecomBankTrans)

                }

//                }

                /* def recenciliationEntryCode = ""

                 ///Details ID have bank_payment_id/////////
                 def previousBankPaymentId = new CoreParamsHelperTagLib().getPreviousBankPaymentID(phnDetails.id)
                 if (previousBankPaymentId == '0') {
                     recenciliationEntryCode = new CoreParamsHelperTagLib().getNextGeneratedNumberWithoutPrefix('recenciliationEntry')
                 } else {
                     recenciliationEntryCode = previousBankPaymentId
                 }*/

                //  ********** Important Note *************

                def detailsIdBankInfo = phnDetails.id
                String updatebankStatementImportDetailsFinal = """UPDATE bank_statement_import_details_final SET
                                                                reconcilated='1',remain_amount=0,reconciliated_amount=amount,
                                                                bank_payment_id = ${newInvoiceNo}  WHERE id = '$detailsIdBankInfo' """;

                db.execute(updatebankStatementImportDetailsFinal);

                def tempId = phnDetails.id

                ///Update Reconciliation Info at details table
                new CoreParamsHelperTagLib().updateBankPaymentIdAtImportDetailsFinal(tempId, recenciliationEntryCode)


            }
        }

    }


    def Map searchBankTrans(def bankId, def str) {

        def bankStatementImportDetailsData = new BudgetViewDatabaseService().executeQuery("Select id,bankImportId,amount,debitCredit,fiscalYearTrans," +
                "byAccountName,byBankAccountNo,description,transDateTime,entryTimestamp,iba_number FROM BankStatementImportDetails" +
                " Where bankImportId='" + bankId + "' ORDER BY id ASC")

        def dataArr = bankStatementImportDetailsData

        def res = false
        def searchResult = "";

        for (int i = 0; i < dataArr.size(); i++) {
            def temp = false
            if (dataArr[i][6] == "P" && str != "") {
                def tempData = dataArr[i][7].toString().toLowerCase().replace(" ", "")
                str = str.toString().trim().toLowerCase().replace(" ", "");
                temp = tempData.contains(str);
                tempData = ''
                if (temp == true) {
                    res = temp
                    searchResult = searchResult + dataArr[i][0].toString() + "::"
                }
            }
        }

        Map result = [res         : res,
                      searchResult: searchResult

        ]

        return result
    }

    def getImportBankTransaction(def params, def liveUrl) {

        int aInt = 0;
        String gridOutput = ''
        def importBankTransinfo
        if (params.id) {
            importBankTransinfo = getImportBankStatementInfoForSelectedBankAcc(params)
        } else {
            importBankTransinfo = getImportBankStatementInfo(params)
        }

        def controllerName = "privateBudgetMaster"

        List quickExpenseList = new ArrayList()
        GridEntity gridEntity
        String budgetEdit = ""

        importBankTransinfo.gridResult.each { phn ->

            gridEntity = new GridEntity();
            aInt = aInt + 1
            gridEntity.id = aInt

            def bankAccNo = phn[6]
            if (bankAccNo == "P") {
//                bankAccNo = message(code: 'bv.bankImportTransactionEmptyBankAccNo.label', default: 'No contra acc')
                bankAccNo = 'No contra acc'
            }

            def tempfirstTransDate = phn[8]

            Integer tempfirstYear = Integer.parseInt(tempfirstTransDate.substring(0, 2)) + 100
            Integer tempfirstMonth = Integer.parseInt(tempfirstTransDate.substring(2, 4))
            Integer tempfirstDay = Integer.parseInt(tempfirstTransDate.substring(4, 6))

            def startDate = new Date(tempfirstYear, tempfirstMonth - 1, tempfirstDay)
            startDate = new SimpleDateFormat("dd MMM yyyy").format(startDate)

            def tempDateTime = phn[9]
            def showTempDateTime
            if (tempDateTime == "0") {
                showTempDateTime = "0:0"
            } else {
                showTempDateTime = tempDateTime.substring(0, 2) + ":" + tempDateTime.substring(2, 4)
            }

            BigDecimal showAmount = new BigDecimal(phn[2])
            def df = new DecimalFormat("0.00########")
            def formattedAmount = df.format(showAmount.stripTrailingZeros())

            def iban = "<input type='text' name='tempByIBANo::${phn[0]}' id= 'tempByIBANo::${phn[0]}' value='${phn[10]}' >"
            def bankAccountNo = "<input type='text' name='tempByBankAccountNo::${phn[0]}' id= 'tempByBankAccountNo::${phn[0]}' value='${bankAccNo}'  >"
            def transactionDateTime = "<div style='margin-left: 6px; width: 85px;'>${startDate}</div>"
            def amount

            if (phn[3] == "D") {
                amount = "<div style='float:Right;margin-right: 9px;'>-${formattedAmount}</div>"
            } else {
                amount = "<div style='float:Right;margin-right: 9px;'>${formattedAmount}</div>"

            }

//            def description = "<input type='text' name='tempByDescription::${phn[0]}' id= 'tempByDescription::${phn[0]}' value='${phn[7]}' style='margin-top: 5px;width:490px' readonly=\"true\">"
            def description = "<div  style='font-size: 11px;line-height: 20px;' id= 'tempByDescription::${phn[0]}'>${phn[7]}</div>"
//            def description = "<div class='bankImportDes' id= 'tempByDescription::${phn[0]}'>${phn[7]}</div>"


            gridEntity.cell = ["iban": iban, "bankAccountNo": bankAccountNo, "transDataTime": transactionDateTime, "amount": amount, "description": description]
            quickExpenseList.add(gridEntity)
        }

        LinkedHashMap result = [draw: 1, recordsTotal: importBankTransinfo.gridResult.size(), recordsFiltered: importBankTransinfo.gridResult.size(), data: quickExpenseList.cell]
        gridOutput = result as JSON

        return gridOutput;

    }

    def getImportBankStatementInfo(def infoArr) {

        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)
        def firstListId = 0

        ////SHAWPON 13-03-2014 Delete all skiped transaction////
        String bankSkipStatementDelete = """DELETE FROM  bank_statement_import_details WHERE fiscal_year_trans=0""";
        db.execute(bankSkipStatementDelete)
        String bankSkipStatementDeleteDuplicate = """DELETE FROM  bank_statement_import_details WHERE duplicate_entry=1""";
        db.execute(bankSkipStatementDeleteDuplicate)

        String bankStatementImportDetailsForDeleteQuery = """SELECT a.id As id,COUNT(b.id) AS countDetails
                                                          FROM bank_statement_import AS a
                                                          LEFT JOIN bank_statement_import_details AS b ON a.id=b.bank_import_id
                                                          GROUP BY a.id""";
        def bankStatementImportDetailsForDeleteArr = db.rows(bankStatementImportDetailsForDeleteQuery);

        Integer hasData = 0
        bankStatementImportDetailsForDeleteArr.each { phnDetateArr ->
            def phnDetateId = phnDetateArr['id']
            def phnCountDetails = phnDetateArr['countDetails']
            if (phnCountDetails == 0) {
                String bankStatementMainDelete = """DELETE FROM bank_statement_import WHERE id='$phnDetateId'""";
                db.execute(bankStatementMainDelete)
            } else {
                hasData = hasData + 1
            }
        }

        Map result = [
                gridResult: ''

        ]
        if (hasData > 0) {

            if (infoArr.bid) {
                firstListId = infoArr.bid
            } else {
                def BankStatementImportFinalArr = new BudgetViewDatabaseService().executeQuery("SELECT id,startTransDate,endTransDate,transBankAccountNo FROM BankStatementImport WHERE  trackCode='" + infoArr.tc + "' ORDER BY id ASC")
                firstListId = BankStatementImportFinalArr[0][0]
            }

            def bankStatementImportDataFinal = new BudgetViewDatabaseService().executeQuery("Select id,transBankAccountNo,endTransDate,startTransDate,startingBalance,endingBalance,trackCode,mtFileName FROM BankStatementImport Where id='" + firstListId + "'")

            def bankStatementImportDetailsData = setSearchStringAsBankAccount(bankStatementImportDataFinal[0][0]);
            result.gridResult = bankStatementImportDetailsData
        }

        return result
    }


    def getImportBankStatementInfoForSelectedBankAcc(def params) {
        def bankStatementImportData = new BudgetViewDatabaseService().executeQuery("Select id,transBankAccountNo,endTransDate,startTransDate,startingBalance,endingBalance,trackCode,mtFileName FROM BankStatementImport Where id='" + params.id + "'")
        def bankStatementImportDetailsData = setSearchStringAsBankAccount(bankStatementImportData[0][0]);

        Map result = [
                gridResult: bankStatementImportDetailsData
        ]
        return result
    }


    def setSearchStringAsBankAccount(def bankImportId) {

        def bankStatementImportDetailsData = new BudgetViewDatabaseService().executeQuery("Select id,bankImportId,amount,debitCredit,fiscalYearTrans," +
                " byAccountName,byBankAccountNo,description,transDateTime,entryTimestamp,iba_number,transBankAccountNo " +
                "FROM BankStatementImportDetails Where bankImportId='" + bankImportId + "' ORDER BY by_bank_account_no DESC")
        //Get account category. Company or Private.
        def transBankAccountNo = bankStatementImportDetailsData[0][11];

        def bankAccountCategory = new BudgetViewDatabaseService().executeQuery("""SELECT
                                                                             bank_account_category
                                                                             FROM
                                                                             company_bank_accounts
                                                                             WHERE bank_account_no = ${transBankAccountNo}""")

        def bankCategory = bankAccountCategory[0][0]
        //Execute query and get category from company bank account.
        def searchStrArr
        if ("cba" == bankCategory) {
            //Vendor,Customer and reservation.
            searchStrArr  =new BudgetViewDatabaseService().executeQuery("""SELECT bank_account_no from vendor_bank_account WHERE `status` ='1'""")
        } else {
            searchStrArr = new BudgetViewDatabaseService().executeQuery("""SELECT  bank_account_no
                                                                                FROM private_budget_bank_account
                                                                                WHERE type ='2'
                                                                                """)
        }
        //Private bank account
        //Select all search string list from private bank account table.
        def stringToSearch = ""
        for (def i = 0; i < bankStatementImportDetailsData.size(); i++) {
            for (def j = 0; j < searchStrArr.size(); j++) {
                stringToSearch = searchStrArr[j][0]
                def searchStrLenght = stringToSearch.toString().length();

                if (bankStatementImportDetailsData[i][6] == "P" && stringToSearch != "" && searchStrLenght > 2) {
                    def flag = false;
                    def tempDescription = bankStatementImportDetailsData[i][7].toString().toLowerCase().replace(" ", "")
                    def tempStringToSearch = stringToSearch.toString().toLowerCase().replace(" ", "");
                    flag = tempDescription.contains(tempStringToSearch)
                    if (flag == true) {
                        bankStatementImportDetailsData[i][6] = stringToSearch
                        break;
                    }
                }
            }
        }

        return bankStatementImportDetailsData
    }

    def getReconTransactionDate(def id) {

        Date tempTransDate = new Date()
        String transDate = tempTransDate.format("yyyy-MM-dd hh:mm:ss")
        def reconTransDateArr = new BudgetViewDatabaseService().executeQuery("""SELECT remain_amount,DATE_FORMAT(trans_date_time,'%Y-%m-%d') as transTime,TIME_FORMAT(entry_timestamp,'%h:%m:%s') as entryTime FROM bank_statement_import_details_final WHERE id='$id'""");
        String finalTransDate = transDate
        if (reconTransDateArr.size()) {
            String transDateTemp = reconTransDateArr[0][1]
            String transTimeTemp = reconTransDateArr[0][2]
            finalTransDate = transDateTemp + " " + transTimeTemp
        }
        return finalTransDate
    }

    def saveAcceptPaymentWithoutReceipt(def params, def detailsIdArr, def noVATCategoryId,def dataMap,def bankAccountCategory) {

        def errorProcesss = 0
        def budgetGLDashboad = ""
        def budgetIdDashboad = ""
        def isDebit = true;
        def budgetGLDashboadArr = params.budgetGLDashboad.split("::")
        budgetGLDashboad = budgetGLDashboadArr[0]

        def budgetIdDashboadArr = params.budgetItemIdDashboad.split("::")
        budgetIdDashboad = budgetIdDashboadArr[0]

        detailsIdArr.each { phnDetails ->
            def allBudgetItemExpanse = "SELECT a.id,a.vendor_id,v.vendor_type,v.payment_term_id  FROM budget_item_expense AS a " +
                    "LEFT JOIN budget_item_expense_details AS b ON a.id=b.budget_item_expense_id " +
                    "LEFT JOIN vendor_master AS v ON a.vendor_id=v.id WHERE a.booking_period_start_month='" + phnDetails.bookingPeriod +
                    "' AND a.booking_period_start_year='" + phnDetails.bookingYear + "' AND b.gl_account='" + budgetGLDashboad +
                    "' AND a.vendor_id = '" + budgetIdDashboad + "' ORDER BY a.id ASC";

            def allBudgetItemExpanseArr = new BudgetViewDatabaseService().executeQuery(allBudgetItemExpanse)
            def budgetItemExpenseId = '0'
            def budgetItemExpenseVendorId = '0'
            def budgetItemExpenseVendorType = ''
            def vendorTermID = '0'

            isDebit = phnDetails.isDebit;

            if (allBudgetItemExpanseArr.size()) {
                budgetItemExpenseId = allBudgetItemExpanseArr[0][0]
                budgetItemExpenseVendorId = allBudgetItemExpanseArr[0][1]
                budgetItemExpenseVendorType = allBudgetItemExpanseArr[0][2]
                vendorTermID = allBudgetItemExpanseArr[0][3]
            } else {
                errorProcesss = 1
            }

            if (errorProcesss == 0) {
                def vendorIdfinal = params.vendorId
                if (budgetItemExpenseVendorType == 'bn') {
                    if (vendorIdfinal == '0') {
                        vendorIdfinal = budgetItemExpenseVendorId
                    }
                } else {
                    vendorIdfinal = budgetItemExpenseVendorId
                }

                //def InvoiceNo = new CoreParamsHelperTagLib().getNextGeneratedNumber('invoiceExpense')
                //def TransDate = getReconTransactionDate(phnDetails.id)
                String strCustomizedPayRef = "Bank Transaction Booking";

                Map dataArr = [
                        bookingPeriod   : phnDetails.bookingPeriod,
                        bookingYear     : phnDetails.bookingYear,
                        vendorIdfinal   : vendorIdfinal,
                        id              : phnDetails.id,
                        amount          : phnDetails.amount,
                        isDebit         : isDebit,
                        noVATCategoryId : noVATCategoryId,
                        budgetGLDashboad: budgetGLDashboad
                ]

                def insertedId = saveInvoiceForPaymentWithoutReceipt(dataArr, allBudgetItemExpanseArr,dataMap,bankAccountCategory)
                saveReconciliationForPaymentWithoutReceipt(dataArr, insertedId,bankAccountCategory)
            }

        }

        return errorProcesss
    }

    def saveInvoiceForPaymentWithoutReceipt(def dataArr, def allBudgetItemExpanseArr,def dataMap,def bankAccountCategory) {

        def budgetItemExpenseId = '0'
        def budgetItemExpenseVendorId = '0'
        def vendorTermID = '0'

        if (allBudgetItemExpanseArr.size()) {
            budgetItemExpenseId = allBudgetItemExpanseArr[0][0]
            budgetItemExpenseVendorId = allBudgetItemExpanseArr[0][1]
            vendorTermID = allBudgetItemExpanseArr[0][3]
        }

        def invoiceNo = new CoreParamsHelperTagLib().getNextGeneratedNumber('invoiceExpense')
        def transDate = getReconTransactionDate(dataArr.id)
        String strCustomizedPayRef = "Bank Transaction Booking";

        dataMap.invoiceNumber = "INVE-"+invoiceNo;

        def bookingDate = new ApplicationUtil().convertDateFromMonthAndYear(dataArr.bookingPeriod,dataArr.bookingYear)

        Double dataArrAmount = Double.parseDouble(dataArr.amount);
        dataArrAmount = dataArrAmount.round(2);

        Map invoiceHead = [
                bookingPeriod      : dataArr.bookingPeriod,
                bookingYear        : dataArr.bookingYear,
                budgetItemExpenseId: budgetItemExpenseId,
                comments           : '',
                currencyCode       : "EURO",
                vendorAccountNo    : '',
                vendorId           : dataArr.vendorIdfinal,
                dueDate            : transDate,
                invoiceNo          : invoiceNo,
                isReverse          : 0,
                paidStatus         : 1,
                paidAmount         : dataArr.isDebit ? dataArrAmount : -1 * dataArrAmount,
                reverseInvoiceId   : 0,
                status             : 1,
                termsId            : vendorTermID,
                totalGlAmount      : dataArr.isDebit ? dataArrAmount : -1 * dataArrAmount,
                totalVat           : 0.00,
                transDate          : transDate,
                paymentRef         : strCustomizedPayRef,
                budgetVendorId     : budgetItemExpenseVendorId,
                isBookReceive      : 0
        ]

        def tableNameinvoiceHead = "InvoiceExpense"
        def insertedId = new BudgetViewDatabaseService().insert(invoiceHead, tableNameinvoiceHead)

        Map invoiceLineDetails = [
                accountCode          : dataArr.budgetGLDashboad,
                discountAmount       : 0,
                invoiceId            : insertedId,
                note                 : '',
                productCode          : 1,
                quantity             : 1,
                totalAmountWithVat   : dataArr.isDebit ? dataArrAmount : -1 * dataArrAmount,
                totalAmountWithoutVat: dataArr.isDebit ? dataArrAmount : -1 * dataArrAmount,
                unitPrice            : dataArr.isDebit ? dataArrAmount : -1 * dataArrAmount,
                vatCategoryId        : dataArr.noVATCategoryId,
                vatRate              : 0,
                shopInfo             : 0
        ]//dataArr.isDebit ? dataArr.amount : -1 * new BigDecimal(dataArr.amount),

        def tableNameinvoiceLineDetails = "InvoiceExpenseDetails"
        def detailsInsertedId = new BudgetViewDatabaseService().insert(invoiceLineDetails, tableNameinvoiceLineDetails)
        def bookedId = dataArr.id

        def comBankInstance = new CoreParamsHelperTagLib().getReconciliationCompanyBankAccountByReconciliationId(bookedId)
        if (comBankInstance.size()) {
            //Trans Master
            //Without VAT amount
            Map trnMas = [
                    accountCode       : dataArr.budgetGLDashboad,
                    amount            : dataArr.isDebit ? dataArr.amount : -1 * new BigDecimal(dataArr.amount),
                    transDate         : transDate,
                    transType         : 2,
                    invoiceNo         : insertedId,
                    recenciliationCode: insertedId + "#2",
                    bookingPeriod     : dataArr.bookingPeriod,
                    bookingYear       : dataArr.bookingYear,
                    userId            : springSecurityService.principal.id,
                    createDate        : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()),
                    process           : com.bv.constants.Process.MANUAL_RECONCILIATION,
                    customerId        : 0,
                    vendorId          : dataArr.vendorIdfinal,
                    bookingDate       : bookingDate
            ]

            def tableNametrnMas = "TransMaster"    //BDR-4
            new BudgetViewDatabaseService().insert(trnMas, tableNametrnMas)
            //Insert only VAT amount.(No need now) as we assume no VAT for this invoice.

            def comBankInstanceInvoice = new CoreParamsHelperTagLib().getCompanyBankAccountByGlAccount(dataArr.budgetGLDashboad)
            if (comBankInstanceInvoice.size()) {
                Map comBankTrans = [
                        amount         : dataArr.isDebit ? dataArr.amount : -1 * new BigDecimal(dataArr.amount),
                        companyBankCode: comBankInstanceInvoice[0],
                        invoiceNo      : insertedId,
                        personCode     : dataArr.vendorIdfinal,
                        transDate      : transDate,
                        transType      : 2,
                        bookingPeriod  : dataArr.bookingPeriod,
                        bookingYear    : dataArr.bookingYear
                ]
                def tableNamecomBankTrans = "CompanyBankTrans"
                new BudgetViewDatabaseService().insert(comBankTrans, tableNamecomBankTrans)
            }
            //////////////////////////Creditor Entry IN Master Table/////////////////////
            def creditorCreditGlSetupInfo = new CoreParamsHelperTagLib().getDebitCreditGlSetupInfo()

            //Trans Master
            //Total amount without VAT.
            BigDecimal tempAmount = new BigDecimal(dataArr.amount);
            Map trnMasDr = [
                    accountCode       : creditorCreditGlSetupInfo[1],
                    amount            : dataArr.isDebit ? -1 * tempAmount : new BigDecimal(dataArr.amount),
                    transDate         : transDate,
                    transType         : 2,
                    invoiceNo         : insertedId,
                    recenciliationCode: insertedId + "#2",
                    bookingPeriod     : dataArr.bookingPeriod,
                    bookingYear       : dataArr.bookingYear,
                    userId            : springSecurityService.principal.id,
                    createDate        : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()),
                    process           : com.bv.constants.Process.MANUAL_RECONCILIATION,
                    customerId        : 0,
                    vendorId          : dataArr.vendorIdfinal,
                    bookingDate       : bookingDate
            ]

            def tableNametrnMasDr = "TransMaster"      //BDR-4
            new BudgetViewDatabaseService().insert(trnMasDr, tableNametrnMasDr)


        }

        return insertedId
    }

    def saveReconciliationForPaymentWithoutReceipt(def dataArr, def insertedId,def bankAccountCategory) {

        //BigDecimal tempAmount = new BigDecimal(dataArr.amount);
        double tempAmount = Double.parseDouble(dataArr.amount).round(2)
        def creditorCreditGlSetupInfo = new CoreParamsHelperTagLib().getDebitCreditGlSetupInfo()
        def comBankInstance = new CoreParamsHelperTagLib().getReconciliationCompanyBankAccountByReconciliationId(dataArr.id)
        def transDate = getReconTransactionDate(dataArr.id)
        def bookingDate = new ApplicationUtil().convertDateFromMonthAndYear(dataArr.bookingPeriod,dataArr.bookingYear)


        if (comBankInstance.size()) {
            ////////////////Reconcilate/////////////////////////////////////////
            def recenciliationEntryCode = ""

            ///Details ID have bank_payment_id/////////
            def previousBankPaymentId = new CoreParamsHelperTagLib().getPreviousBankPaymentID(dataArr.id)
            if (previousBankPaymentId == '0') {
                recenciliationEntryCode = new CoreParamsHelperTagLib().getNextGeneratedNumberWithoutPrefix('recenciliationEntry')
            } else {
                recenciliationEntryCode = previousBankPaymentId
            }

            def detailsIdBankInfo = dataArr.id
            String updatebankStatementImportDetailsFinal = """UPDATE bank_statement_import_details_final SET reconcilated='1',remain_amount=0,reconciliated_amount=amount WHERE id='$detailsIdBankInfo' """;
            budgetViewDatabaseService.executeUpdate(updatebankStatementImportDetailsFinal)
            ///Update Reconciliation Info at details table
            new CoreParamsHelperTagLib().updateBankPaymentIdAtImportDetailsFinal(dataArr.id, recenciliationEntryCode)

            ////////////////////Trans Master Effect
            //I think no need this code. Need to check.
            def recenciliationCode = ""
            def recenciliationGL = ""
            def recenciliationBankGL = ""

            recenciliationCode = insertedId + "#2"
            recenciliationGL = creditorCreditGlSetupInfo[1]
            recenciliationBankGL = comBankInstance[0]
            //BDR-4
            String updateTransMaster = """UPDATE trans_master SET recenciliation_code='$recenciliationCode' WHERE invoice_no='$insertedId' AND trans_type=2 AND account_code='$recenciliationGL'""";
            budgetViewDatabaseService.executeUpdate(updateTransMaster)
            ///End

            def transTypeCode = 7
            if(bankAccountCategory == 'pba'){
                transTypeCode = 10
            }
            //Trans Master
            Map trnMasBooked = [
                    accountCode       : recenciliationGL,
                    amount            : dataArr.isDebit ? tempAmount : -1 * tempAmount,
                    transDate         : transDate,
                    transType         : transTypeCode,
                    invoiceNo         : recenciliationEntryCode,
                    bookingPeriod     : dataArr.bookingPeriod,
                    bookingYear       : dataArr.bookingYear,
                    recenciliationCode: recenciliationCode,
                    userId            : springSecurityService.principal.id,
                    createDate        : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()),
                    process           : com.bv.constants.Process.MANUAL_RECONCILIATION,
                    customerId        : 0,
                    vendorId          : dataArr.vendorIdfinal,
                    bookingDate       : bookingDate
            ]

            def tableNametrnMasBooked = "TransMaster"  //BDR-4
            new BudgetViewDatabaseService().insert(trnMasBooked, tableNametrnMasBooked)

            Map trnMasBank = [
                    accountCode       : recenciliationBankGL,
                    amount            : dataArr.isDebit ? -1 * tempAmount : tempAmount,
                    transDate         : transDate,
                    transType         : transTypeCode,
                    invoiceNo         : recenciliationEntryCode,
                    bookingPeriod     : dataArr.bookingPeriod,
                    bookingYear       : dataArr.bookingYear,
                    recenciliationCode: recenciliationCode,
                    userId            : springSecurityService.principal.id,
                    createDate        : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()),
                    process           : com.bv.constants.Process.MANUAL_RECONCILIATION,
                    customerId        : 0,
                    vendorId          : dataArr.vendorIdfinal,
                    bookingDate       : bookingDate
            ]

            def tableNametrnMasBank = "TransMaster" //BDR-4
            new BudgetViewDatabaseService().insert(trnMasBank, tableNametrnMasBank)

            Map comBankTransRecon = [
                    amount         : tempAmount,
                    companyBankCode: comBankInstance[1],
                    invoiceNo      : recenciliationEntryCode,
                    personCode     : 0,
                    transDate      : transDate,
                    transType      : transTypeCode,
                    bookingPeriod  : dataArr.bookingPeriod,
                    bookingYear    : dataArr.bookingYear
            ]

            def tableNamecomBankTransRecon = "CompanyBankTrans"
            new BudgetViewDatabaseService().insert(comBankTransRecon, tableNamecomBankTransRecon)

        }
    }

    def ArrayList getAllCustomerBankAccountNo() {

        ArrayList existingAccountArr = new ArrayList()
        def CustomerBankAccountArr = new BudgetViewDatabaseService().executeQuery("SELECT id,bankAccountNo FROM CustomerBankAccount")
        if (CustomerBankAccountArr.size()) {
            for (int k = 0; k < CustomerBankAccountArr.size(); k++) {
                existingAccountArr << CustomerBankAccountArr[k][1];//.replace(".", "")
            }
        }

        def VendorBankAccountArr = new BudgetViewDatabaseService().executeQuery("SELECT id,bankAccountNo FROM VendorBankAccount")

        if (VendorBankAccountArr.size()) {
            for (int k = 0; k < VendorBankAccountArr.size(); k++) {
                existingAccountArr << VendorBankAccountArr[k][1];//.replace(".", "")
            }
        }

        def CompanyBankAccountArr = new BudgetViewDatabaseService().executeQuery("SELECT id,bankAccountNo FROM CompanyBankAccounts")

        if (CompanyBankAccountArr.size()) {
            for (int m = 0; m < CompanyBankAccountArr.size(); m++) {
                existingAccountArr << CompanyBankAccountArr[m][1];//.replace(".", "")
            }
        }

        def PrivateBankAccountArr = new BudgetViewDatabaseService().executeQuery("""SELECT id, bank_account_no,type FROM
                                                                                private_budget_bank_account where status = 1""")
        if (PrivateBankAccountArr.size()) {
            for (int k = 0; k < PrivateBankAccountArr.size(); k++) {
                existingAccountArr << PrivateBankAccountArr[k][1];//.replace(".", "")
            }
        }


        def ReservationBankAccountArr = new BudgetViewDatabaseService().executeQuery("""SELECT id,bank_account_no,type
                                                                                    FROM reservation_budget_bank_account where status = 1""")
        if (ReservationBankAccountArr.size()) {
            for (int k = 0; k < ReservationBankAccountArr.size(); k++) {
                existingAccountArr << ReservationBankAccountArr[k][1];//.replace(".", "")
            }
        }

        return existingAccountArr;
    }

    def getBankAccountTypeFromNumber(def id) {
        def type = '';
        def bankType = new BudgetViewDatabaseService().executeQuery("""SELECT bank_account_category
            FROM company_bank_accounts AS cba where bank_account_no = '${id}'""");

        if (bankType.size() > 0) {
            if (bankType[0][0] == 'pba') {
                type = '(Private)'
            } else if (bankType[0][0] == 'cba') {
                type = '(Company)'
            }
        }


        return type
    }

}
