<%@ page import="factoring.CoreParamsHelperTagLib;factoring.DashboardDetailsTagLib;org.springframework.web.servlet.support.RequestContextUtils" %>
<!doctype html>
<html>
<%
    def protocol = request.isSecure() ? "https://" : "http://"
    def host = request.getServerName()
    def port = request.getServerPort()
    def context = request.getServletContext().getContextPath()

    def dashboardURL = ""
    dashboardURL = protocol + host + ":" + port + context + "/dashboardDetails/incomeAndExpenseNameWise"

    def activeFiscalYear = new CoreParamsHelperTagLib().getActiveFiscalYear()
    def fiscalYearInfo = new CoreParamsHelperTagLib().getActiveFiscalYearInformation(activeFiscalYear)
    def companyName
    if(session.companyInfo)
        companyName = session.companyInfo[0].company_full_name
%>


<head>
    <meta name="layout" content="main" >
    <g:set var="entityName" value="${message(code: 'dashboard.label', default: 'Dashboard')}"/>
    <title><g:message code="default.name.label" args="[entityName]"/></title>
</head>

<body style="zoom:100%;-moz-transform: scale(2);">


<div id="list-page-body-inner" class="content" style="background-color: #fff; border: none;">

    <div class="budgetHeader">
        <div class="headerMainLeft cus-lft">

        </div>

        <div class="headerMainMiddle" style="width: 25%;font-size: 20px;padding-top: 12px;">
            <label class="">${companyName}</label>
        </div>
        <div id="profitSummary" class="headerMainRightFiscalYr cus-fis">
            <% if(fiscalYearInfo){%>
                <a href="${context}/fiscalYear/list">${fiscalYearInfo[0][4]}</a>
            <%}%>
        </div>
    </div>  <!--budgetHeader-->

    <div class="explanationDiv">

    </div>  <!--explanationDiv-->

    <div class="navigation">
        <div class="navigationWizard">


            <div class="navigationbtn" style="float: right; margin-right: 43px;">

            </div>
        </div>
    </div>  <!--navigation-->

    <hr style=" margin-top: 23px;">
    <div class="dash-sec">
        <div class="part-sec first">
            <div class="part-sec-th"><g:message code="bv.dashboard.master.data.label" default="Master Data" /></div>
            <div class="navigationbtn prt-btn">
                <a href="${context}/customerMaster/list"><g:message code="bv.dashboard.newDebtor.label" default="Debtor" /></a>
            </div>
            <div class="navigationbtn prt-btn">
                <a href="${context}/vendorMaster/list"><g:message code="bv.dashboard.newCustomer.label" default="Customer" /></a>
            </div>
            <div class="navigationbtn prt-btn">
                <a href="${context}/debtorCustomer/index"><g:message code="bv.dashboard.debtorCustomer.label" default="Debtor Customer" /></a>
            </div>
        </div>
        <div class="part-sec second">
            <div class="part-sec-th"><g:message code="bv.dashboard.invoice.processing.label" default="Invoice Processing" /></div>
            <div class="navigationbtn prt-btn">
                <a href="${context}/invoiceIncome/list"><g:message code="bv.dashboard.single.entry.label" default="Single Entry" /></a>
            </div>
            <div class="navigationbtn prt-btn">
                <a href="${context}/importInvoice/importInvoice"><g:message code="bv.dashboard.bulk.entry.label" default="Bulk Entry" /></a>
            </div>
            <div class="navigationbtn prt-btn">
                <a href="${context}/invoiceExpense/list"><g:message code="bv.dashboard.generate.settlment.label" default="Generate Settlement" /></a>
            </div>
            <div class="navigationbtn prt-btn">
                <a href="${context}/reportCustomerSettlement/listSettlementSend"><g:message code="bv.dashboard.send.settlement.label" default="Send Settlement" /></a>
            </div>
            <div class="navigationbtn prt-btn">
                <a href="${context}/reportCustomerSettlement/listBatchPayment?showDeleted=No"><g:message code="bv.dashboard.batch.payment.label" default="Batch Payment" /></a>
            </div>
        </div>
        <div class="part-sec third">
          <sec:ifNotGranted roles="ROLE_CUSTOMER">
            <div class="part-sec-th"><g:message code="bv.dashboard.credit.management.label" default="Credit Management" /></div>
            <div class="navigationbtn prt-btn">
                <a href="${context}/reportCustomerSettlement/listReminderSend"><g:message code="bv.dashboard.send.reminder.label" default="Send Reminder" /></a>
            </div>
            <div class="navigationbtn prt-btn">
                <a href="${context}/reportCustomerSettlement/listOutstandingInvoices"><g:message code="bv.dashboard.all.outstanding.label" default="Outstanding Invoices" /></a>
            </div>
            <div class="navigationbtn prt-btn">
                <a href="${context}/reportCustomerSettlement/allListDebtors"><g:message code="bv.dashboard.actionList.label" default="Action List" /></a>
            </div>

          </sec:ifNotGranted>

            <div class="navigationbtn prt-btn">
                <a href="${context}/customerMaster/creditLimitInfo"><g:message code="bv.dashboard.debtorCreditLimit.label" default="Debtor Credit Limit" /></a>
            </div>
            <div class="navigationbtn prt-btn">
                <a href="${context}/vendorMaster/customerCreditLimitInfo"><g:message code="bv.dashboard.customerCreditLimit.label" default="Customer Credit Limit" /></a>
            </div>

        </div>
        <div class="part-sec fourth">
            <sec:ifAnyGranted roles="ROLE_ACCOUNTANT,ROLE_ADMIN">
                <div class="part-sec-th"><g:message code="bv.dashboard.management.information.label" default="Management Information" /></div>
                <div class="navigationbtn prt-btn">
                    <a href="${context}/management/report"><g:message code="bv.dashboard.management.report.label" default="Management Information" /></a>
                </div>
            </sec:ifAnyGranted>
        </div>
        <div style="clear: both;"></div>
    </div>

    <hr>

    <div id="detalis_view" class="graph" style="border-radius: 3px;height:auto;">
    </div>

</div>  <!--content-->


</body>
</html>
