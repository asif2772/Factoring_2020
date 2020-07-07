package factoring

class InvoiceIncome {

    Integer customerId
    Integer debtorId
    String  invoiceNo
    Integer budgetItemIncomeId
    Date transDate
    //static belongsTo = [terms:PaymentTerms,currencyCode:Currencies]
    Integer termsId
//  String currencyCode
    Date    dueDate
    String  debitorCustomerNo
    String  bookingPeriod
    String  bookingYear
    String  comments
    Double  totalVat
    Double  totalGlAmount
    Double  paidAmount
    Integer paidStatus
    String  reverseInvoiceId
    String  paymentRef
    Integer isReverse
    String  status
    String  allDocsOk
    Integer budgetCustomerId
    Integer userIdCreate;
    Integer userIdUpdate;
    Integer historyStatus = 0
    Integer extraDays = 0

    static constraints = {
        invoiceNo (nullable: false, blank: false, size: 1..100)
        budgetItemIncomeId (nullable: false, blank: false,size: 1..11)
        customerId (min:0)
        reverseInvoiceId (nullable: true)
        comments (nullable: true)
    }

    static mapping = {
        invoiceNo column: "invoice_no",type: 'string', length:100
        customerAccountNo column: "customer_account_no",type:'string', length: 255
        comments column: "comments",type:'text'
        status column: "status",type:'integer', length: 1
    }
}
