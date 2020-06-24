
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'customerMaster.label', default: 'CustomerMaster')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#create-customerMaster" class="skip" tabindex="-1">
            <g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div id="create-customerMaster" class="content scaffold-create" role="main">
			<h1>
                <g:message code="default.create.label" args="[entityName]" />
            </h1>
			<g:if test="${flash.message}">
			    <div class="update_message" role="status">${flash.message}</div>
			</g:if>
			<g:hasErrors bean="${customerMasterInstance}">
			<ul class="errors" role="alert">
				<g:eachError bean="${customerMasterInstance}" var="error">
				<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
			</g:hasErrors>
			<g:form action="save" >
				<fieldset class="form">
					<g:render template="form"/>
                    <div class="buttonClass">
                    <g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" />
                    </div>
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
