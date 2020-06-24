package factoring

import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import io.micronaut.spring.tx.annotation.Transactional

@Transactional
class CustomerMasterService {

    def creditLimitsInfo() {
        def companyConfig = new BudgetViewDatabaseService().getConnectionInformation()
        def db = Sql.newInstance(companyConfig.serverUrl + companyConfig.dbName, companyConfig.dbUser, companyConfig.dbPassword, companyConfig.driverName)

        String creditLimitInfoStr = """SELECT *
                                                FROM
                                                  (SELECT customerName,
                                                          insuadAmount,
                                                          ROUND(SUM(dueAmount), 2) AS dueAmountSum,
                                                          ROUND(insuadAmount - SUM(dueAmount), 2) AS overDue
                                                   FROM
                                                     (SELECT cm.customer_name AS customerName,
                                                             cm.insuad_amout AS insuadAmount,
                                                             cm.credit_limit AS creditLimit,
                                                             IF(ISNULL(tblPaidInvoices.invoicepaidamount), ROUND(ic.total_gl_amount + ic.total_vat, 2), ROUND((ic.total_gl_amount + ic.total_vat) - tblPaidInvoices.invoicepaidamount, 2)) AS dueAmount,
                                                             ic.debtor_id AS debtorId
                                                      FROM invoice_income AS ic
                                                      LEFT JOIN customer_master AS cm ON cm.id = ic.debtor_id
                                                      LEFT JOIN
                                                        (SELECT DISTINCT ic.id AS invoiceId,
                                                                         SUM(tm.amount *- 1) AS invoicePaidAmount,
                                                                         MAX(tm.trans_date) AS lastPaymentDate
                                                         FROM invoice_income AS ic
                                                         INNER JOIN trans_master AS tm ON tm.recenciliation_code = CONCAT(ic.id, '#1')
                                                         AND tm.account_code = '1300'
                                                         AND tm.trans_type <> 1 GROUP  BY ic.id) AS tblPaidInvoices ON tblPaidInvoices.invoiceid = ic.id) AS tblAllInfo
                                                   WHERE tblAllInfo.dueAmount > 0
                                                   GROUP BY debtorId) AS tml
                                                WHERE tml.overDue < 0
                                                ORDER BY tml.overDue ASC"""
        
        List<GroovyRowResult> creditLimitInfo = db.rows(creditLimitInfoStr)

        [creditLimitInfo: creditLimitInfo]
    }
}
