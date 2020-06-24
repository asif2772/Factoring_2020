<!doctype html>
<html lang="en" class="no-js">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title><g:layoutTitle default="Budget View"/></title>

    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <asset:link rel="icon" href="favicon.ico" type="image/x-ico"/>
    <asset:link rel="apple-touch-icon" href="apple-touch-icon.png"/>
    <asset:link rel="apple-touch-icon" sizes="114x114"  href="apple-touch-icon-retina.png"/>

    <asset:stylesheet src="factoringApplicationMnf.css"/>
    <asset:stylesheet src="factoringThemeMnf.css"/>
    <asset:stylesheet src="bootstrap-modals.css"/>
    <asset:javascript src="jquery.min.js"/>
    <link rel="stylesheet" href="//cdn.datatables.net/1.10.4/css/jquery.dataTables.min.css">
    <asset:javascript src="jquery.min.js"/>
    <asset:javascript src="jquery-ui.min.js"/>
    <asset:javascript src="dataTables.min.js"/>
    <asset:javascript src="jqueryPluginsMnf.js"/>
    <asset:javascript src="jqxWidgetsCoreCalenderMnf.js"/>
    <asset:javascript src="jqxWidgetsComboMnf.js"/>
    <asset:javascript src="bootstrap-modals.js"/>

    <g:layoutHead/>

    <script>
        var appContext = '${request.contextPath}';
        var sessionExpMsg = '<g:message code="session.expiry.msg" default="Your session is about to finish, do you want to keep current session?"/>';
    </script>
</head>

<body>

<g:render template="/common/header" />
<div id="content-whole">
    <div id="content-whole-inner">
        <div class="logoDiv">
            <div class="logoDivLeft">
                <a href="#"><asset:image src="logo.png" alt="Budget View"/></a>
            </div>
            <div class="logoDivRight">
                <div id="spinner" class="spinner" style="display:none;">
                    <asset:image src="spinner.gif" alt="${message(code:'spinner.alt.Loading',default:'Loading...')}" />
                </div>
            </div>
        </div>

<g:layoutBody/>

        <div id="spinner2" class="spinner" style="display:none;"><g:message code="spinner.alt.Loading&hellip" default="Loading&hellip;"/></div>
        <g:javascript library="application"/>
    </div>

    <g:render template="/common/footer" />
</div>
</body>
</html>
