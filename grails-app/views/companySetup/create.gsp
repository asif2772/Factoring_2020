<%@ page import="org.springframework.validation.FieldError; factoring.CompanySetup" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'companySetup.label', default: 'CompanySetup')}" />
		%{--<title><g:message code="company.setup.create.label" args="[entityName]" /></title>--}%
	</head>
	<body>
		<a href="#create-companySetup" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>

		<div id="create-companySetup" class="content scaffold-create" role="main">
			<h1><g:message code="company.setup.create.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:hasErrors bean="${companySetupInstance}">
			<ul class="errors" role="alert">
				<g:eachError bean="${companySetupInstance}" var="error">
				<li <g:if test="${error in FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
			</g:hasErrors>
			<g:uploadForm  action="save" onsubmit="return sfuSubmitForm(this);" style="margin: 0">
				<fieldset class="form">
					<g:render template="form"/>
                        <div class="buttonClass" style="margin-right: 10px">
                            <div class="buttonCLassInner">
                                <g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" style="font-size: 16px" />
                            </div>
                        </div>
				</fieldset>
			</g:uploadForm>
		</div>
	</body>
</html>
