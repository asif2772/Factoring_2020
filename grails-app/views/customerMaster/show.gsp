
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'customerMaster.label', default: 'CustomerMaster')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-customerMaster" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-customerMaster" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list customerMaster">

				<g:if test="${customerMasterInstance?.customerCode}">
				<li class="fieldcontain">
					<span id="customerCode-label" class="property-label"><g:message code="customerMaster.customerCode.label" default="Customer Code" /></span>
					
						<span class="property-value" aria-labelledby="customerCode-label"><g:fieldValue bean="${customerMasterInstance}" field="customerCode"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${customerMasterInstance?.customerName}">
				<li class="fieldcontain">
					<span id="customerName-label" class="property-label"><g:message code="debtorMaster.debtorName.label" default="Customer Name" /></span>

						<span class="property-value" aria-labelledby="customerName-label"><g:fieldValue bean="${customerMasterInstance}" field="customerName"/></span>

				</li>
				</g:if>
			
				<g:if test="${customerMasterInstance?.gender}">
				<li class="fieldcontain">
					<span id="gender-label" class="property-label"><g:message code="customerMaster.gender.label" default="Gender" /></span>
					
						<span class="property-value" aria-labelledby="gender-label"><g:fieldValue bean="${customerMasterInstance}" field="gender"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${customerMasterInstance?.firstName}">
				<li class="fieldcontain">
					<span id="firstName-label" class="property-label"><g:message code="customerMaster.firstName.label" default="First Name" /></span>
					
						<span class="property-value" aria-labelledby="firstName-label"><g:fieldValue bean="${customerMasterInstance}" field="firstName"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${customerMasterInstance?.middleName}">
				<li class="fieldcontain">
					<span id="middleName-label" class="property-label"><g:message code="customerMaster.middleName.label" default="Middle Name" /></span>
					
						<span class="property-value" aria-labelledby="middleName-label"><g:fieldValue bean="${customerMasterInstance}" field="middleName"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${customerMasterInstance?.lastName}">
				<li class="fieldcontain">
					<span id="lastName-label" class="property-label"><g:message code="customerMaster.lastName.label" default="Last Name" /></span>
					
						<span class="property-value" aria-labelledby="lastName-label"><g:fieldValue bean="${customerMasterInstance}" field="lastName"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${customerMasterInstance?.email}">
				<li class="fieldcontain">
					<span id="email-label" class="property-label"><g:message code="customerMaster.email.label" default="Email" /></span>
					
						<span class="property-value" aria-labelledby="email-label"><g:fieldValue bean="${customerMasterInstance}" field="email"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${customerMasterInstance?.momentOfSending}">
				<li class="fieldcontain">
					<span id="momentOfSending-label" class="property-label"><g:message code="customerMaster.momentOfSending.label" default="Moment Of Sending" /></span>
					
						<span class="property-value" aria-labelledby="momentOfSending-label"><g:fieldValue bean="${customerMasterInstance}" field="momentOfSending"/></span>
					
				</li>
				</g:if>
			
				%{--<g:if test="${customerMasterInstance?.frequencyOfInvoice}">
				<li class="fieldcontain">
					<span id="frequencyOfInvoice-label" class="property-label"><g:message code="customerMaster.frequencyOfInvoice.label" default="Frequency Of Invoice" /></span>
					
						<span class="property-value" aria-labelledby="frequencyOfInvoice-label"><g:fieldValue bean="${customerMasterInstance}" field="frequencyOfInvoice"/></span>
					
				</li>
				</g:if>--}%
			
				<g:if test="${customerMasterInstance?.defaultGlAccount}">
				<li class="fieldcontain">
					<span id="defaultGlAccount-label" class="property-label"><g:message code="customerMaster.defaultGlAccount.label" default="Default Gl Account" /></span>
					
						<span class="property-value" aria-labelledby="defaultGlAccount-label"><g:fieldValue bean="${customerMasterInstance}" field="defaultGlAccount"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${customerMasterInstance?.currCode}">
				<li class="fieldcontain">
					<span id="currCode-label" class="property-label"><g:message code="customerMaster.currCode.label" default="Curr Code" /></span>
					
						<span class="property-value" aria-labelledby="currCode-label"><g:fieldValue bean="${customerMasterInstance}" field="currCode"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${customerMasterInstance?.salesType}">
				<li class="fieldcontain">
					<span id="salesType-label" class="property-label"><g:message code="customerMaster.salesType.label" default="Sales Type" /></span>
					
						<span class="property-value" aria-labelledby="salesType-label"><g:fieldValue bean="${customerMasterInstance}" field="salesType"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${customerMasterInstance?.creditStatus}">
				<li class="fieldcontain">
					<span id="creditStatus-label" class="property-label"><g:message code="customerMaster.creditStatus.label" default="Credit Status" /></span>
					
						<span class="property-value" aria-labelledby="creditStatus-label"><g:fieldValue bean="${customerMasterInstance}" field="creditStatus"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${customerMasterInstance?.creditLimit}">
				<li class="fieldcontain">
					<span id="creditLimit-label" class="property-label"><g:message code="customerMaster.creditLimit.label" default="Credit Limit" /></span>
					
						<span class="property-value" aria-labelledby="creditLimit-label"><g:fieldValue bean="${customerMasterInstance}" field="creditLimit"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${customerMasterInstance?.chamOfCommerce}">
				<li class="fieldcontain">
					<span id="chamOfCommerce-label" class="property-label"><g:message code="customerMaster.chamOfCommerce.label" default="Cham Of Commerce" /></span>
					
						<span class="property-value" aria-labelledby="chamOfCommerce-label"><g:fieldValue bean="${customerMasterInstance}" field="chamOfCommerce"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${customerMasterInstance?.tax}">
				<li class="fieldcontain">
					<span id="tax-label" class="property-label"><g:message code="customerMaster.tax.label" default="Tax" /></span>
					
						<span class="property-value" aria-labelledby="tax-label"><g:fieldValue bean="${customerMasterInstance}" field="tax"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${customerMasterInstance?.vat}">
				<li class="fieldcontain">
					<span id="vat-label" class="property-label"><g:message code="customerMaster.vat.label" default="Vat" /></span>
					
						<span class="property-value" aria-labelledby="vat-label"><g:fieldValue bean="${customerMasterInstance}" field="vat"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${customerMasterInstance?.comments}">
				<li class="fieldcontain">
					<span id="comments-label" class="property-label"><g:message code="customerMaster.comments.label" default="Comments" /></span>
					
						<span class="property-value" aria-labelledby="comments-label"><g:fieldValue bean="${customerMasterInstance}" field="comments"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${customerMasterInstance?.customerBankAccount}">
				<li class="fieldcontain">
					<span id="customerBankAccount-label" class="property-label"><g:message code="customerMaster.customerBankAccount.label" default="Customer Bank Account" /></span>
					
						<g:each in="${customerMasterInstance.customerBankAccount}" var="c">
						<span class="property-value" aria-labelledby="customerBankAccount-label"><g:link controller="customerBankAccount" action="show" id="${c.id}">${c?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${customerMasterInstance?.customerGeneralAddress}">
				<li class="fieldcontain">
					<span id="customerGeneralAddress-label" class="property-label"><g:message code="customerMaster.customerGeneralAddress.label" default="Customer General Address" /></span>
					
						<g:each in="${customerMasterInstance.customerGeneralAddress}" var="c">
						<span class="property-value" aria-labelledby="customerGeneralAddress-label"><g:link controller="customerGeneralAddress" action="show" id="${c.id}">${c?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${customerMasterInstance?.customerPostalAddress}">
				<li class="fieldcontain">
					<span id="customerPostalAddress-label" class="property-label"><g:message code="customerMaster.customerPostalAddress.label" default="Customer Postal Address" /></span>
					
						<g:each in="${customerMasterInstance.customerPostalAddress}" var="c">
						<span class="property-value" aria-labelledby="customerPostalAddress-label"><g:link controller="customerPostalAddress" action="show" id="${c.id}">${c?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${customerMasterInstance?.customerShipmentAddress}">
				<li class="fieldcontain">
					<span id="customerShipmentAddress-label" class="property-label"><g:message code="customerMaster.customerShipmentAddress.label" default="Customer Shipment Address" /></span>
					
						<g:each in="${customerMasterInstance.customerShipmentAddress}" var="c">
						<span class="property-value" aria-labelledby="customerShipmentAddress-label"><g:link controller="customerShipmentAddress" action="show" id="${c.id}">${c?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${customerMasterInstance?.paymentTerm}">
				<li class="fieldcontain">
					<span id="paymentTerm-label" class="property-label"><g:message code="customerMaster.paymentTerm.label" default="Payment Term" /></span>
					
						<span class="property-value" aria-labelledby="paymentTerm-label"><g:link controller="paymentTerms" action="show" id="${customerMasterInstance?.paymentTerm?.id}">${customerMasterInstance?.paymentTerm?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${customerMasterInstance?.status}">
				<li class="fieldcontain">
					<span id="status-label" class="property-label"><g:message code="customerMaster.status.label" default="Status" /></span>
					
						<span class="property-value" aria-labelledby="status-label"><g:formatBoolean boolean="${customerMasterInstance?.status}" /></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${customerMasterInstance?.id}" />
					<g:link class="edit" action="edit" id="${customerMasterInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
