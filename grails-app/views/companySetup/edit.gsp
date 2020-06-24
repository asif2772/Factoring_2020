<%@ page import="org.springframework.validation.FieldError; factoring.CompanySetup" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'companySetup.label', default: 'CompanySetup')}"/>
    %{--<title><g:message code="company.setup.edit.label" args="[entityName]"/></title>--}%
</head>

<body>

<a href="#edit-companySetup" class="skip" tabindex="-1">
    <g:message code="default.link.skip.label" default="Skip to content&hellip;"/>
</a>

%{--<div id="edit-companySetup" class="content scaffold-edit" role="main">--}%
%{--############### start my MC  ###############--}%

<div id="wrapper">
  <div id="tabContainer">

      <div id="edit-companySetup" class="content scaffold-edit" role="main">

          <div class="list-boxtitle boxTitle">
              <div class="list-boxtitle-left"><g:message code="company.setup.edit.label" args="[entityName]"/>
              </div>
              <div class="slideContent2" style="display: none; font-weight: lighter; color: #0066FF; font-size: 12px;line-height:220%  ">
                  <g:message code="companySetup.helpInfo"/>
              </div>
          </div>

      </div>

    <g:if test="${flash.message}">
        <div class="update_message" role="status">${flash.message}</div>
    </g:if>

      <g:hasErrors bean="${companySetupInstance}">
          <ul class="errors" role="alert">
              <g:eachError bean="${companySetupInstance}" var="error">
                  <li> <g:if test="${error in FieldError}">data-field-id="${error.field}"</g:if>>
                      <g:message error="${error}"/>
                  </li>
              </g:eachError>
          </ul>
      </g:hasErrors>
    <g:hasErrors bean="${companySetupInstance}">
        <ul class="errors" role="alert">
            <g:eachError bean="${companySetupInstance}" var="error">
                <li <g:if test="${error in FieldError}">data-field-id="${error.field}"</g:if> > <g:message
                        error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
    <g:uploadForm method="post" style="margin: 0">
        <g:hiddenField name="id" value="1"/>
        <g:hiddenField name="version" value="${companySetupInstance?.version}"/>

        <div id="tabs" class="tabHolder">
            <g:javascript src="tabs.js"/>
            <ul>
                <li id="tabHeader_1" class="tabBtn t_1 redCommon"><g:message code="companySetup.generalinformation.label" default="General Information"/></li>
                <li id="tabHeader_2" class="tabBtn t_2 redCommon"><g:message code="companySetup.newAddress.label" default="Company Address"/></li>
                <li id="tabHeader_3" class="tabBtn t_3 redCommon"><g:message code="companySetup.postaladdress.label" default="Postal Address"/></li>
                <li id="tabHeader_4" class="tabBtn t_4 redCommon"><g:message code="companySetup.defaultsettings.label" default="Default Settings"/></li>
                %{--<li id="tabHeader_5"><g:message code="bv.customerMaster.bankAccountInfo.label" default="Bank 5555"/></li>--}%
            </ul>
        </div>

     <div id="tabscontent">

         <fieldset>
             <div class="tabpage" id="tabpage_1" style="">
                 <fieldset class="form" id="test90">
                     <g:render template="form"/>
                 </fieldset>
             </div>

             <div class="tabpage" id="tabpage_2">
                 <fieldset class="form">
                     <g:render template="newAddressForm"/>
                 </fieldset>
             </div>  <!--tabpage-->

             <div class="tabpage" id="tabpage_3">
                 <fieldset class="form">
                     <g:render template="postalAddress"/>
                 </fieldset>
             </div>

             <div class="tabpage" id="tabpage_4">
                 <fieldset class="form">
                     <g:render template="defaultSettingsForm"/>
                 </fieldset>
             </div>
        </fieldset>

        <fieldset class="buttons_new updateLinkBtn" style="margin-bottom: 7px;">
            <g:actionSubmit name="create" class="save updateBtn" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}"/>
            <g:link id="btnNextId" action="list" class="close linkBtn" params="[incBItem: params.incBItem, fInv: params.fInv]">${message(code: 'default.button.next.label', default: 'Next')}</g:link>

            <div class="fieldContainer logoImage" style="padding-top: 14px;">
                <% if (!(companySetupInstance?.logo_file).empty) {%>
                <asset:image tabindex="6" src="${resource(dir: 'images/companylogo', file: companySetupInstance?.logo_file)}" width="220" height="60"
                     alt="${message(code: 'companySetup.thereIsNoLogo.label', default: 'There is no logo')}"/>
                <% } else {%>
                <asset:image tabindex="6" src="${resource(dir: 'images/companylogo', file: 'logo-default.png')}" width="220" height="60"
                     alt="${message(code: 'companySetup.thereIsNoLogo.label', default: 'There is no logo')}"/>
                <% } %>
            </div>
        </fieldset>
        <% /** End of the code**/ %>

     </div>
    </g:uploadForm>
   </div> <!--tabContainer-->

</div>  <!--wrapper-->

</body>
</html>
