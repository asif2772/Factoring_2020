<%@ page import="factoring.CompanySetup" %>

<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'companySetup.label', default: 'CompanySetup')}" />
		<title><g:message code="default.name.label" args="[entityName]" /></title>
	</head>
	<body>

    <g:javascript>
        $(function() {
            $( "#accordion" ).accordion({
                autoHeight: false,
                navigation: true
            });
        });
    </g:javascript>
    <%
        def compSetup = CompanySetup.executeQuery("SELECT id FROM CompanySetup")
        if (compSetup[0]){ %>
            <g:include view="companySetup/edit.gsp" />
        <% }else{ %>
            <g:include view="companySetup/create.gsp"/>
    <% } %>

	</body>
</html>
