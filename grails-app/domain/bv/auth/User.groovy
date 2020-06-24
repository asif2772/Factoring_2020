package bv.auth

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
@EqualsAndHashCode(includes='username')
@ToString(includes='username', includeNames=true, includePackage=false)

class User implements Serializable {

    private static final long serialVersionUID = 1

    transient role
    transient passwordPlain
    String username
    String password
    Integer businessCompanyId
    String email
    String firstName
    String lastName
    boolean enabled
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired
    Long customerId
    boolean resetPass

    Set<Role> getAuthorities() {
        (UserRole.findAllByUser(this) as List<UserRole>)*.role as Set<Role>
    }
    static constraints = {
        username blank: false, unique: true
        password blank: false
        email email: true, unique: true
        businessCompanyId (nullable: true)
        customerId (nullable: true)
    }

    static mapping = {
        password column: '`password`'
    }
}
