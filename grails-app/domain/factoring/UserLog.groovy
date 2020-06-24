package factoring

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
class UserLog {

    String logInfo
    Integer userId
    String ipAddress
    Date logTime

    static constraints = {
        logInfo (column: "logInfo",type:'text',size: 1..765)
        userId (size: 1..19,type:'integer')
        ipAddress(size: 1..20)
    }
}
