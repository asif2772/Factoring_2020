<%@ page import="factoring.CompanyBankAccounts" %>

<div id="create-companyBankAccounts" class="content scaffold-create" role="main">
    <div class="list-boxtitle">
        <div class="list-boxtitle-left"><g:message code="debtorCustomer.create.label" args="[entityName]" /></div>

        <div class="slideContent2" style="display: none; font-weight: lighter; color: #0066FF; font-size: 12px; ">
            <g:message code="companyBankAccount.helpInfo"  />
        </div>
    </div>
    <div id="msgdivAnother" class="paymntRefMessage"></div>

    <%if(insertFailed){ println("insertFailed : "+insertFailed);%>
        <g:if test="${flash.message}">
            <div class="errorMessage" role="status">${flash.message}</div>
        </g:if>
    <%}else{%>
        <g:if test="${flash.message}">
            <div class="update_message" role="status">${flash.message}</div>
        </g:if>
    <%}%>

    <g:hasErrors bean="${debtorCustomerInstance}">
        <ul class="errors" role="alert">
            <g:eachError bean="${debtorCustomerInstance}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>

    <g:form action="saveDebtorCustomer" >
        <fieldset class="form">
            <g:render template="form" />
        </fieldset>

        <fieldset class="btnClassDiv updateLinkBtn">
            <g:submitButton name="create" class="save updateBtn" style="width:auto"  value="${message(code: 'default.button.createAddMore.label', default: 'Create Another')}" />
            <g:actionSubmit class="save updateBtn" style="width:auto" action="saveAndAddInvoice" value="${message(code: 'default.button.createAddInvoice.label', default: 'Close')}" />
        </fieldset>

    </g:form>

</div>

