package bv

import bv.auth.User
import factoring.CompanySetup
import factoring.ExtraSettingService
import grails.plugin.springsecurity.SpringSecurityService
import org.springframework.web.servlet.support.RequestContextUtils

class DashboardController {

    ExtraSettingService extraSettingService
    SpringSecurityService springSecurityService

    def index() {
        //Company setup info.
        setCompanyInfo()
        Integer max
        params.max = Math.min(max ?: 2, 100)
        def CompanySetup = CompanySetup.executeQuery("select id from CompanySetup")

        User user = springSecurityService.currentUser
        def userCustomerId = user.customerId
        def resetPass = user.resetPass
        if(resetPass == true) {

            redirect(action: 'setPass', params: [id: user.id])
            return

        } else {

            if (CompanySetup) {
                if(userCustomerId){
                    redirect(controller: 'vendorMaster', action: "portalList", params:[id:userCustomerId.toString(), userId: user.id])
                    return false
                }
            } else {
                redirect(controller: 'companySetup', action: "index")
            }
        }
    }

    def setCompanyInfo() {

        if (!session["isLangSet"]) {

            Map map = extraSettingService.getCompanyInfoMap()

            session.companyInfo = []
            session.companyInfo << map

            String resultSetLn = 'nl'
            resultSetLn = session.companyInfo[0].language

            //println(session.companyInfo.language);
            def locale = new Locale(resultSetLn, resultSetLn.toUpperCase(), "") //
            //def lastLocale = RequestContextUtils.getLocale(request)
            RequestContextUtils.getLocaleResolver(request).setLocale(request, response, locale)
            session["isLangSet"] = true
        }
    }
}
