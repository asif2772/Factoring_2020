package factoring

import grails.gorm.transactions.Transactional
import groovy.sql.GroovyRowResult

@Transactional
class VendorMasterService {

    def customerCreditLimitsInfo() {
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String customerCreditLimitInfoStr = """SELECT *
                                                    FROM
                                                      (SELECT customerName,
                                                              creditLimit,
                                                              ROUND(SUM(dueAmount), 2) AS dueAmountSum,
                                                              ROUND(creditLimit - SUM(dueAmount), 2) AS overDue
                                                       FROM
                                                         (SELECT vm.vendor_name AS customerName,
                                                                 IF(ISNULL(tblPaidInvoices.invoicepaidamount), ROUND(ic.total_gl_amount + ic.total_vat, 2), ROUND((ic.total_gl_amount + ic.total_vat) - tblPaidInvoices.invoicepaidamount, 2)) AS dueAmount,
                                                                 vm.credit_limit AS creditLimit,
                                                                 ic.customer_id AS customerId,
                                                                 ic.debtor_id AS debtorId,
                                                                 ic.id AS invoiceId
                                                          FROM invoice_income AS ic
                                                          INNER JOIN vendor_master AS vm ON ic.customer_id = vm.id
                                                          LEFT JOIN
                                                            (SELECT DISTINCT ic.id AS invoiceId,
                                                                             SUM(tm.amount *- 1) AS invoicePaidAmount,
                                                                             MAX(tm.trans_date) AS lastPaymentDate
                                                             FROM invoice_income AS ic
                                                             INNER JOIN trans_master AS tm ON tm.recenciliation_code = CONCAT(ic.id, '#1')
                                                             AND tm.account_code = '1300'
                                                             AND tm.trans_type <> 1 GROUP  BY ic.id) AS tblPaidInvoices ON tblPaidInvoices.invoiceid = ic.id) AS tblAllInfo
                                                       WHERE tblAllInfo.dueAmount > 0
                                                       GROUP BY customerId) AS tml
                                                    WHERE tml.overDue < 0
                                                    ORDER BY tml.overDue ASC"""
        List<GroovyRowResult> customerCreditLimitInfo = db.rows(customerCreditLimitInfoStr)

        [customerCreditLimitInfo: customerCreditLimitInfo]
    }
}
