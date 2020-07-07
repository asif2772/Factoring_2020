
<%@ page import="factoring.InvoiceIncome;" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'invoiceIncome.label', default: 'InvoiceIncome')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-invoiceIncome" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-invoiceIncome" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list invoiceIncome">
			
				<g:if test="${invoiceIncomeInstance?.invoiceNo}">
				<li class="fieldcontain">
					<span id="invoiceNo-label" class="property-label"><g:message code="invoiceIncome.invoiceNo.label" default="Invoice No" /></span>
					
						<span class="property-value" aria-labelledby="invoiceNo-label"><g:fieldValue bean="${invoiceIncomeInstance}" field="invoiceNo"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${invoiceIncomeInstance?.salesOrderNo}">
				<li class="fieldcontain">
					<span id="salesOrderNo-label" class="property-label"><g:message code="invoiceIncome.salesOrderNo.label" default="Sales Order No" /></span>
					
						<span class="property-value" aria-labelledby="salesOrderNo-label"><g:fieldValue bean="${invoiceIncomeInstance}" field="salesOrderNo"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${invoiceIncomeInstance?.comments}">
				<li class="fieldcontain">
					<span id="comments-label" class="property-label"><g:message code="invoiceIncome.comments.label" default="Comments" /></span>
					
						<span class="property-value" aria-labelledby="comments-label"><g:fieldValue bean="${invoiceIncomeInstance}" field="comments"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${invoiceIncomeInstance?.currencyCode}">
				<li class="fieldcontain">
					<span id="currencyCode-label" class="property-label"><g:message code="invoiceIncome.currencyCode.label" default="Currency Code" /></span>
					
						<span class="property-value" aria-labelledby="currencyCode-label"><g:link controller="currencies" action="show" id="${invoiceIncomeInstance?.currencyCode?.id}">${invoiceIncomeInstance?.currencyCode?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${invoiceIncomeInstance?.customerAccountNo}">
				<li class="fieldcontain">
					<span id="customerAccountNo-label" class="property-label"><g:message code="invoiceIncome.customerAccountNo.label" default="Customer Account No" /></span>
					
						<span class="property-value" aria-labelledby="customerAccountNo-label"><g:fieldValue bean="${invoiceIncomeInstance}" field="customerAccountNo"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${invoiceIncomeInstance?.customerId}">
				<li class="fieldcontain">
					<span id="customerId-label" class="property-label"><g:message code="invoiceIncome.customerId.label" default="Customer Id" /></span>
					
						<span class="property-value" aria-labelledby="customerId-label"><g:fieldValue bean="${invoiceIncomeInstance}" field="customerId"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${invoiceIncomeInstance?.dueDate}">
				<li class="fieldcontain">
					<span id="dueDate-label" class="property-label"><g:message code="invoiceIncome.dueDate.label" default="Due Date" /></span>
					
						<span class="property-value" aria-labelledby="dueDate-label"><g:formatDate date="${invoiceIncomeInstance?.dueDate}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${invoiceIncomeInstance?.inventoryLocationId}">
				<li class="fieldcontain">
					<span id="inventoryLocationId-label" class="property-label"><g:message code="invoiceIncome.inventoryLocationId.label" default="Inventory Location Id" /></span>
					
						<span class="property-value" aria-labelledby="inventoryLocationId-label"><g:link controller="inventoryLocations" action="show" id="${invoiceIncomeInstance?.inventoryLocationId?.id}">${invoiceIncomeInstance?.inventoryLocationId?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${invoiceIncomeInstance?.status}">
				<li class="fieldcontain">
					<span id="status-label" class="property-label"><g:message code="invoiceIncome.status.label" default="Status" /></span>
					
						<span class="property-value" aria-labelledby="status-label"><g:fieldValue bean="${invoiceIncomeInstance}" field="status"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${invoiceIncomeInstance?.terms}">
				<li class="fieldcontain">
					<span id="terms-label" class="property-label"><g:message code="invoiceIncome.terms.label" default="Terms" /></span>
					
						<span class="property-value" aria-labelledby="terms-label"><g:link controller="paymentTerms" action="show" id="${invoiceIncomeInstance?.terms?.id}">${invoiceIncomeInstance?.terms?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${invoiceIncomeInstance?.transDate}">
				<li class="fieldcontain">
					<span id="transDate-label" class="property-label"><g:message code="invoiceIncome.transDate.label" default="Trans Date" /></span>
					
						<span class="property-value" aria-labelledby="transDate-label"><g:formatDate date="${invoiceIncomeInstance?.transDate}" /></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${invoiceIncomeInstance?.id}" />
					<g:link class="edit" action="edit" id="${invoiceIncomeInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
