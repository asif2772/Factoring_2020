%{--<%@ page import="javax.sql.DataSource" %>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <meta name='layout' content='main'/>
    <title><g:message code="springSecurity.login.title"/></title>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'auth_login.css')}" type="text/css" media='screen'>

    <script>
        var sesStorage = window.sessionStorage;
        sesStorage.setItem("budgetType","incNexp");
        sesStorage.setItem("budgetSortType","name_wise");

        sesStorage.setItem("dashboardURL","");
        sesStorage.setItem('selectedValue','-1')

        var browserName = getBrowserName();

        function getContextPath() {
            return "<%=request.getContextPath()%>";
        }

        if (browserName == 'IE11' || browserName == 'MSIE') {
            var conPath = getContextPath();
            window.location = conPath + "/login/showWarning";
        }
     </script>

</head>

<body>

<g:if test='${params.session_expired_ajax}'>
    <g:javascript>
        window.location.href = '/login/auth?session_expired=true';
    </g:javascript>
</g:if>

<div class="messageLogin" style="display: none;">
    <span><g:message code="browser.warning.message.label"/> <g:link
            url="http://www.mozilla.org/en-US/firefox/new/">Download</g:link></span><a href="#" class="close-notify">X</a>
</div>

<div class="topcorner">
    <span class="logoDivRightFirst">
        <g:link controller="${params.controller}" action="${params.action}" params="[lang:'en']" class="flagEnglish"></g:link>
    </span>&nbsp;
    <span>
        <g:link controller="${params.controller}" action="${params.action}" params="[lang:'nl']" class="flagNetherlands"></g:link>
    </span>
</div>

<div class='login'>
    <div class='inner'>
        <div class="factorIt"><g:message code="bv.login.welcome.factorIt"/></div>
<div class="fheader"><g:message code="springSecurity.login.header"/></div>


        <div style="border-top: 1px solid #ffffff; padding-top: 14px;">
            <g:if test='${flash.message}'>
                <div class='login_message'><p>${flash.message}</p></div>
            </g:if>
            <g:if test='${params.session_expired}'>
                <div class='login_message'><p>Session expeired, please login again</p></div>
            </g:if>

            <form action='${postUrl}' method='POST' id='loginForm' class='cssform' autocomplete='off'>
                <p>
                    <label for='username'><g:message code="bv.login.username.factorIt"/>:</label>
                    <input type='text' class='text_' name='j_username' id='username' required='required'/>
                </p>

                <p>
                    <label for='password'><g:message code="bv.login.password.factorIt"/>:</label>
                    <input type='password' class='text_' name='j_password' id='password' required='required'/>
                </p>

                <p style="margin-left:-65px">
                    <input type='submit' id="submit" class="loginCustomButton" value='${message(code: "bv.login.button.factorIt")}'/>
                </p>

                <p class="forgotPassword">
                    <g:link controller="login" class="forgotPass" action="forgotPassword"><g:message code="bv.login.forgot.password.factorIt"/>?</g:link>
                </p>

                <p class=""> <g:message code="bv.login.not.user.factorIt"/>!
                    <g:link controller="login" class="" action="signUp"><g:message code="bv.login.sign.up.factorIt"/></g:link>
                </p>

            </form>
        </div>
    </div>
</div>
<script type='text/javascript'>

    (function () {
        document.forms['loginForm'].elements['j_username'].focus();
        //$('#outstandingInvoiceListView').DataTable().state.clear();
    })();

</script>
</body>
</html>--}%









<!--
/*
 *	This content is generated from the PSD File Info.
 *	(Alt+Shift+Ctrl+I).
 *
 *	@desc
 *	@file 		web_1920___1
 *	@date 		0
 *	@title 		Web 1920  1
 *	@author
 *	@keywords
 *	@generator 	Export Kit v1.2.8.xd
 *
*/
 -->

<%@ page import="javax.sql.DataSource" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en" >
<head>
    <meta name='layout' content='mainCustomerPortal'/>
    <meta http-equiv="content-type" content="text/html" charset="utf-8" />
    <title>web_1920___1</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="" >
    <asset:stylesheet src="web_1920___1.css"/>
    <asset:javascript src="exportkitGoogleFont.js"/>
    %{--<script src="https://secure.exportkit.com/cdn/js/ek_googlefonts.js"></script>--}%
    <!-- Add your custom HEAD content here -->
    <script>
        var sesStorage = window.sessionStorage;
        sesStorage.setItem("budgetType","incNexp");
        sesStorage.setItem("budgetSortType","name_wise");
        sesStorage.setItem("dashboardURL","");
        sesStorage.setItem('selectedValue','-1')

        var browserName = getBrowserName();

        function getContextPath() {
            return "<%=request.getContextPath()%>";
        }

        if (browserName == 'IE11' || browserName == 'MSIE') {
            var conPath = getContextPath();
            window.location = conPath + "/login/showWarning";
        }
    </script>
</head>
<body>
<div id="content-container" >
    <asset:image src="home/logo.png" alt="Budget View"/>
    <div id="_bg__web_1920___1"  ></div>
%{--    <asset:image src="advancedgrails.svg" alt="Grails Guides" class="float-left"/>--}%
%{--    <asset:image src="home/bg.png" alt="Grails Guides" class="float-left"/>--}%
    <asset:image src="home/bg.png" id="bg" />
    <asset:image src="home/screen_shot_2019_06_21_at_10_08_26.png" id="screen_shot_2019_06_21_at_10_08_26" />

    <div id="footer"  >
        <div id="rectangle_1"  ></div>
        <div id="group_3"  >
            <div id="group_1"  >
                <asset:image src="home/path_1.png" id="path_1" />
                <asset:image src="home/path_2.png" id="path_2" />
                <asset:image src="home/path_3.png" id="path_3" />
                <asset:image src="home/path_4.png" id="path_4" />
                <asset:image src="home/path_5.png" id="path_5" />
                <asset:image src="home/path_6.png" id="path_6" />
                <asset:image src="home/path_7.png" id="path_7" />
                <asset:image src="home/path_8.png" id="path_8" />
                <asset:image src="home/path_9.png" id="path_9" />
            </div>
            <div id="group_2"  >
                <asset:image src="home/path_10.png" id="path_10" />
                <asset:image src="home/path_11.png" id="path_11" />
                <asset:image src="home/path_12.png" id="path_12" />
                <asset:image src="home/path_13.png" id="path_13" />
                <asset:image src="home/path_14.png" id="path_14" />
                <asset:image src="home/path_15.png" id="path_15" />
                <asset:image src="home/path_16.png" id="path_16" />
                <asset:image src="home/path_17.png" id="path_17" />
                <asset:image src="home/path_18.png" id="path_18" />
            </div>
        </div>
    </div>

    <div id="__p__welkom___bij_nl_credit_services_" >
        <span style="color:#ffffff; font-style:normal; font-weight:bold; ">Welkom! <br/></span>
        <span style="color:#ffffff; font-size:18px; font-style:normal; font-weight:normal; ">bij NL Credit Services.</span>
    </div>

%{--<g:if test='${flash.message}'>
    <div class='login_message'><p>${flash.message}</p></div>
</g:if>
<g:if test='${params.session_expired}'>
    <div class='login_message'><p>Session expeired, please login again</p></div>
</g:if>--}%

    <g:if test='${flash.message}'>
        <%if (!changedPass) { %>
        <div id="errorMsg" style="top: 140px; left: 563px; width: 398px;">
            <div style="background: #f5e1e1;">
                <span><asset:image style="padding: 25px 0 0 14px;" src="home/stop.png"  /></span>
                <p style="padding: 4px 58px 0; font-family: Open Sans; color: red; font-size: 14px">
                    Inloggegevens onjuist. U kunt een nieuw wachtwoord aanvragen door ik ben mijn wachtwoord vergeten te kiezen.
                </p>
            </div>
        </div>
        <% } else { %>
        <div id="successMsg" style="top: 163px; left: 563px;">
            <div style="background: #eef7ee; width: 398px">
                <span><asset:image  style="padding-top: 4px;"  src="home/lock.png"/></span>
                <p style="padding: 5px 58px 0; font-weight: bold; font-family: Open Sans; color: green">Wachtwoord succesvol bijgewerkt!</p>
            </div>
        </div>
        <% } %>
    </g:if>


    <form action='${postUrl}' method='POST' id='loginForm'>
%{--        <g:form action="forgotPassword" >--}%
            <div style="top: 222px; left: 563px;">
                <input type="text" class="loginInputPlace" name='username' id='username' required='required' placeholder="E-mailadres"/>
            </div>
            <div style="top: 271px; left: 563px;">
                <input type="password" class="loginInputPlace" name='password' id='password' required='required' placeholder="Watchwoord"/>
            </div>
            <div id="_001_lock"  >
                <div id="group_4"  >
                    <asset:image src="home/path_38.png" id="path_38" />
                </div>
            </div>
            <div id="_002_envelope"  >
                <div id="group_21"  >
                    <asset:image src="home/path_52.png" id="path_52" />
                    <asset:image src="home/path_53.png" id="path_53" />
                </div>
            </div>
            <div style="top: 350px;left: 793px;">
                <input type='submit' id="submit" class="rectangle_4" value='Aanmelden'/>
            </div>
%{--        </g:form>--}%
    </form>

    <div id="logout"  >
        <div id="group_20"  >
            <div id="group_19"  >
                <asset:image src="home/path_51.png" id="path_51" />
            </div>
        </div>
    </div>

    <div id="__p__ik_ben_mijn_wachtwoord_vergeten" >
        <g:link controller="login" action="forgotPassword" style="color: white">Ik ben mijn wachtwoord vergeten</g:link>
    </div>

    <div id="group_7"  >
        <div id="group_4_ek1"  >
            <asset:image src="home/path_19.png" id="path_19" />
            <asset:image src="home/path_20.png" id="path_20" />
            <asset:image src="home/path_21.png" id="path_21" />
            <asset:image src="home/path_22.png" id="path_22" />
            <asset:image src="home/path_23.png" id="path_23" />
            <asset:image src="home/path_24.png" id="path_24" />
            <asset:image src="home/path_25.png" id="path_25" />
            <asset:image src="home/path_26.png" id="path_26" />
            <asset:image src="home/path_27.png" id="path_27" />
            <asset:image src="home/path_28.png" id="path_28" />
            <asset:image src="home/path_29.png" id="path_29" />
            <asset:image src="home/path_30.png" id="path_30" />
            <asset:image src="home/path_31.png" id="path_31" />
            <asset:image src="home/path_32.png" id="path_32" />
            <asset:image src="home/path_33.png" id="path_33" />
            <asset:image src="home/path_34.png" id="path_34" />
        </div>

        <div id="group_5"  >
            <asset:image src="home/path_35.png" id="path_35" />
            <asset:image src="home/path_36.png" id="path_36" />
            <asset:image src="home/path_37.png" id="path_37" />
        </div>

        <div id="group_6"  >
            <asset:image src="home/path_38_ek1.png" id="path_38_ek1" />
            <asset:image src="home/path_39.png" id="path_39" />
            <asset:image src="home/path_40.png" id="path_40" />
            <asset:image src="home/path_41.png" id="path_41" />
            <asset:image src="home/path_42.png" id="path_42" />
            <asset:image src="home/path_43.png" id="path_43" />
            <asset:image src="home/path_44.png" id="path_44" />
            <asset:image src="home/path_45.png" id="path_45" />
            <asset:image src="home/path_46.png" id="path_46" />
            <asset:image src="home/path_47.png" id="path_47" />
            <asset:image src="home/path_48.png" id="path_48" />
            <asset:image src="home/path_49.png" id="path_49" />
            <asset:image src="home/path_50.png" id="path_50" />
            <asset:image src="home/path_51_ek1.png" id="path_51_ek1" />
            <asset:image src="home/path_52_ek1.png" id="path_52_ek1" />
            <asset:image src="home/path_53_ek1.png" id="path_53_ek1" />
            <asset:image src="home/path_54.png" id="path_54" />
            <asset:image src="home/path_55.png" id="path_55" />
        </div>
    </div>

    %{--<div class="selectParent">
        <select class="" style="background:none; border: none; color: white;">
            <option style="color: #0c0c0c" value="NL">NL</option>
            <option style="color: #0c0c0c" value="EN">EN</option>
        </select>
    </div>--}%
    <div class="selectParent">
        <select style="outline: none; cursor: pointer">
            <option style="color: #0c0c0c" value="NL">NL</option>
            <option style="color: #0c0c0c" value="EN"value="2">EN</option>
        </select>
    </div>
</div>

<script type='text/javascript'>

    (function () {
        document.forms['loginForm'].elements['username'].focus();
    })();

</script>
</body>
</html>
