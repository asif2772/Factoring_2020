<%@ page import="factoring.CompanyBankAccounts" %>

%{--<a href="#edit-companyBankAccounts" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>--}%
<div id="edit-companyBankAccounts" class="content scaffold-edit" role="main">
    <h1><g:message code="debtorCustomer.edit.label" args="[entityName]" /></h1>
    <div id="msgdivAnother" class="paymntRefMessage"></div>
    <g:if test="${flash.message}">
        <div class="update_message" role="status">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${debtorCustomerInstance}">
        <ul class="errors" role="alert">
            <g:eachError bean="${debtorCustomerInstance}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
    <g:form action="saveDebtorCustomer" method="post" >
        <g:hiddenField name="id" value="${debtorCustomerInstance?.id}" />
        <g:hiddenField name="version" value="${debtorCustomerInstance?.version}" />

        <fieldset class="form">
            <g:render template="form"/>
        </fieldset>

        <fieldset class="form">
            <div class="buttonClass">
                <div class="btnClassDiv updateLinkBtn" style="margin-right: 0;">
                    <fieldset class="btnClassDiv updateLinkBtn">
                        <g:submitButton name="update" class="save updateBtn" value="${message(code: 'default.button.update.label', default: 'Update')}" />
                        <g:actionSubmit class="save closeBtn" action="closeAndAddNew" value="${message(code: 'default.button.close.label', default: 'Close')}" />
                    </fieldset>
                </div>
            </div>
        </fieldset>

    </g:form>
</div>

