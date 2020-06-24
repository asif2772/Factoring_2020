package factoring

class PaymentTerms {
    /*static      hasMany = [paymentTerm:VendorMaster]*/
    String      terms
    Integer     daysBeforeDue
    Integer     alertStartDays
    Integer     alertRepeatDays
    Integer     finalReminderDays
    Integer     status

    static constraints = {
        terms           (nullable: false, blank: false, size: 1..80)
        daysBeforeDue   (nullable: false, blank: false,size: 1..3)
        alertStartDays  (nullable: false, blank: false,size: 1..3)
        alertRepeatDays (nullable: true, blank: true,size: 1..3)
        finalReminderDays (nullable: true, blank: true,size: 1..3)
    }

    String toString(){
        return terms
    }
}
