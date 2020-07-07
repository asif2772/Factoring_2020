package factoring

class CompanyBankAccounts {
    String bankAccountCode
    String bankAccountType
    String iban
    String bankAccountName
    String bankAccountNo
    String bankName
    String bankAddress
    String bankAccountCategory
    //static belongsTo = [bankCurrCode:Currencies]
    Integer status

    static constraints = {
        bankAccountCode (nullable: false, blank: false, size: 1..20,unique: true)
        bankAccountType (nullable: false, blank: false,size: 1..20)
        iban (nullable: false, blank: false,size: 1..20)
        bankAccountName (nullable: false, blank: false,size: 1..60)
        bankAccountNo (nullable: false, blank: false,size: 1..50)
        bankName (nullable: false, blank: false,size: 1..60)
        bankAddress (nullable: false, blank: false)
        bankAccountCategory(default:'cba')
    }

    static mapping = {
        bankAccountCode column: "bank_account_code",type: 'string', length:20
        bankAccountType column: "bank_account_type",type:'string', length: 50
        iban column: "iban",type:'string', length: 20
        bankAccountName column: "bank_account_name",type:'string', length: 60
        bankAccountNo column: "bank_account_no",type:'string', length: 50
        bankName column: "bank_name",type:'string',length: 60
        bankAddress column: "bank_address",type:'text'
        status column: "status",type:'integer', length: 1
    }
}
