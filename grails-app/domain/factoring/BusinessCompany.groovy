package factoring

class BusinessCompany {

    String name
    String driverName
    String serverUrl
    String dbName
    String dbUser
    String dbPassword
    Integer createdBy
    Date dateCreated
    Integer updatedBy
    Date lastUpdated
    Integer status

    static constraints = {
    }

    def beforeInsert = {
        dateCreated = new Date()
    }
    def beforeUpdate ={
        lastUpdated = new Date()
    }


}
