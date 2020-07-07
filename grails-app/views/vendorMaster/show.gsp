<%@ page import="factoring.VendorMaster" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'vendorMaster.label', default: 'VendorMaster')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-vendorMaster" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-vendorMaster" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list vendorMaster">
			
				<g:if test="${vendorMasterInstance?.vendorCode}">
				<li class="fieldcontain">
					<span id="vendorCode-label" class="property-label"><g:message code="vendorMaster.vendorCode.label" default="Vendor Code" /></span>
					
						<span class="property-value" aria-labelledby="vendorCode-label"><g:fieldValue bean="${vendorMasterInstance}" field="vendorCode"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${vendorMasterInstance?.vendorName}">
				<li class="fieldcontain">
					<span id="vendorName-label" class="property-label"><g:message code="vendorMaster.vendorName.label" default="Vendor Name" /></span>
					
						<span class="property-value" aria-labelledby="vendorName-label"><g:fieldValue bean="${vendorMasterInstance}" field="vendorName"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${vendorMasterInstance?.gender}">
				<li class="fieldcontain">
					<span id="gender-label" class="property-label"><g:message code="vendorMaster.gender.label" default="Gender" /></span>
					
						<span class="property-value" aria-labelledby="gender-label"><g:fieldValue bean="${vendorMasterInstance}" field="gender"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${vendorMasterInstance?.firstName}">
				<li class="fieldcontain">
					<span id="firstName-label" class="property-label"><g:message code="vendorMaster.firstName.label" default="First Name" /></span>
					
						<span class="property-value" aria-labelledby="firstName-label"><g:fieldValue bean="${vendorMasterInstance}" field="firstName"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${vendorMasterInstance?.middleName}">
				<li class="fieldcontain">
					<span id="middleName-label" class="property-label"><g:message code="vendorMaster.middleName.label" default="Middle Name" /></span>
					
						<span class="property-value" aria-labelledby="middleName-label"><g:fieldValue bean="${vendorMasterInstance}" field="middleName"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${vendorMasterInstance?.lastName}">
				<li class="fieldcontain">
					<span id="lastName-label" class="property-label"><g:message code="vendorMaster.lastName.label" default="Last Name" /></span>
					
						<span class="property-value" aria-labelledby="lastName-label"><g:fieldValue bean="${vendorMasterInstance}" field="lastName"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${vendorMasterInstance?.email}">
				<li class="fieldcontain">
					<span id="email-label" class="property-label"><g:message code="vendorMaster.email.label" default="Email" /></span>
					
						<span class="property-value" aria-labelledby="email-label"><g:fieldValue bean="${vendorMasterInstance}" field="email"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${vendorMasterInstance?.momentOfSending}">
				<li class="fieldcontain">
					<span id="momentOfSending-label" class="property-label"><g:message code="vendorMaster.momentOfSending.label" default="Moment Of Sending" /></span>
					
						<span class="property-value" aria-labelledby="momentOfSending-label"><g:fieldValue bean="${vendorMasterInstance}" field="momentOfSending"/></span>
					
				</li>
				</g:if>
			
				%{--<g:if test="${vendorMasterInstance?.frequencyOfInvoice}">
				<li class="fieldcontain">
					<span id="frequencyOfInvoice-label" class="property-label"><g:message code="vendorMaster.frequencyOfInvoice.label" default="Frequency Of Invoice" /></span>
					
						<span class="property-value" aria-labelledby="frequencyOfInvoice-label"><g:fieldValue bean="${vendorMasterInstance}" field="frequencyOfInvoice"/></span>
					
				</li>
				</g:if>--}%
			
				<g:if test="${vendorMasterInstance?.defaultGlAccount}">
				<li class="fieldcontain">
					<span id="defaultGlAccount-label" class="property-label"><g:message code="vendorMaster.defaultGlAccount.label" default="Default Gl Account" /></span>

						<span class="property-value" aria-labelledby="defaultGlAccount-label"><g:fieldValue bean="${vendorMasterInstance}" field="defaultGlAccount"/></span>

				</li>
				</g:if>
			
				<g:if test="${vendorMasterInstance?.currCode}">
				<li class="fieldcontain">
					<span id="currCode-label" class="property-label"><g:message code="vendorMaster.currCode.label" default="Curr Code" /></span>
					
						<span class="property-value" aria-labelledby="currCode-label"><g:fieldValue bean="${vendorMasterInstance}" field="currCode"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${vendorMasterInstance?.purchaseType}">
				<li class="fieldcontain">
					<span id="purchaseType-label" class="property-label"><g:message code="vendorMaster.purchaseType.label" default="Purchase Type" /></span>
					
						<span class="property-value" aria-labelledby="purchaseType-label"><g:fieldValue bean="${vendorMasterInstance}" field="purchaseType"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${vendorMasterInstance?.creditStatus}">
				<li class="fieldcontain">
					<span id="creditStatus-label" class="property-label"><g:message code="vendorMaster.creditStatus.label" default="Credit Status" /></span>
					
						<span class="property-value" aria-labelledby="creditStatus-label"><g:fieldValue bean="${vendorMasterInstance}" field="creditStatus"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${vendorMasterInstance?.creditLimit}">
				<li class="fieldcontain">
					<span id="creditLimit-label" class="property-label"><g:message code="vendorMaster.creditLimit.label" default="Credit Limit" /></span>
					
						<span class="property-value" aria-labelledby="creditLimit-label"><g:fieldValue bean="${vendorMasterInstance}" field="creditLimit"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${vendorMasterInstance?.chamOfCommerce}">
				<li class="fieldcontain">
					<span id="chamOfCommerce-label" class="property-label"><g:message code="vendorMaster.chamOfCommerce.label" default="Cham Of Commerce" /></span>
					
						<span class="property-value" aria-labelledby="chamOfCommerce-label"><g:fieldValue bean="${vendorMasterInstance}" field="chamOfCommerce"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${vendorMasterInstance?.tax}">
				<li class="fieldcontain">
					<span id="tax-label" class="property-label"><g:message code="vendorMaster.tax.label" default="Tax" /></span>
					
						<span class="property-value" aria-labelledby="tax-label"><g:fieldValue bean="${vendorMasterInstance}" field="tax"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${vendorMasterInstance?.vat}">
				<li class="fieldcontain">
					<span id="vat-label" class="property-label"><g:message code="vendorMaster.vat.label" default="Vat" /></span>
					
						<span class="property-value" aria-labelledby="vat-label"><g:fieldValue bean="${vendorMasterInstance}" field="vat"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${vendorMasterInstance?.comments}">
				<li class="fieldcontain">
					<span id="comments-label" class="property-label"><g:message code="vendorMaster.comments.label" default="Comments" /></span>
					
						<span class="property-value" aria-labelledby="comments-label"><g:fieldValue bean="${vendorMasterInstance}" field="comments"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${vendorMasterInstance?.payment_term}">
				<li class="fieldcontain">
					<span id="payment_term-label" class="property-label"><g:message code="vendorMaster.payment_term.label" default="Paymentterm" /></span>
					
						<span class="property-value" aria-labelledby="payment_term-label"><g:link controller="paymentTerms" action="show" id="${vendorMasterInstance?.payment_term?.id}">${vendorMasterInstance?.payment_term?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${vendorMasterInstance?.vendor}">
				<li class="fieldcontain">
					<span id="vendor-label" class="property-label"><g:message code="vendorMaster.vendor.label" default="Vendor" /></span>
					
						<g:each in="${vendorMasterInstance.vendor}" var="v">
						<span class="property-value" aria-labelledby="vendor-label"><g:link controller="vendorBankAccount" action="show" id="${v.id}">${v?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${vendorMasterInstance?.id}" />
					<g:link class="edit" action="edit" id="${vendorMasterInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
