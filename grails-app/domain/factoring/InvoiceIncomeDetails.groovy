package factoring

class InvoiceIncomeDetails {

    Integer invoiceId
    String  productCode
    String  accountCode
    Double  quantity
    Double  unitPrice
    Integer vatCategoryId
    Double  vatRate
    Double  discountAmount
    Double  totalAmountWithoutVat
    Double  totalAmountWithVat
    String  note

    static constraints = {
        invoiceId (nullable: false, blank: false, size: 1..11)
        accountCode (nullable: false, blank:false, size:1..15)
        productCode (nullable: false, blank: false,size: 1..15)
        quantity (nullable: false, blank: false,size: 1..19)
        unitPrice (nullable: false, blank: false,size: 1..19)
        note(nullable: true)
    }
}
