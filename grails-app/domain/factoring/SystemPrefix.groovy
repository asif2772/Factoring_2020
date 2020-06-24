package factoring

class SystemPrefix {
    String title
    String prefix
    Integer prefixLen
    static constraints = {
        title (nullable: false, blank: false,unique: true,size: 1..60)
        prefix (nullable: true, blank: true,unique: true,size: 1..10)
        prefixLen (nullable: true, blank: true,unique: false,size: 1..3)
    }
    String toString(){
        return prefix
    }
}
