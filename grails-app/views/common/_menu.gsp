<%@ page import="bv.auth.User; user.UserController; factoring.CustomerMaster; factoring.UserTagLib; factoring.VendorMaster; factoring.CompanySetup;" %>
<%@ page import="factoring.BudgetViewDatabaseService; factoring.FiscalYear; factoring.UserLog; org.springframework.security.core.context.SecurityContextHolder; org.springframework.security.core.Authentication; org.springframework.security.core.context.SecurityContext" %>

<%
    def CompanySetup = 1
    SecurityContext ctx = SecurityContextHolder.getContext()
    Authentication auth = ctx.getAuthentication()
    String username = auth.getName()

    User user = User.findByUsername(username)
    def businessCompanyId = user.getAt('businessCompanyId')

    def permittedBusinessCompanyId = session.permittedBusinessCompanyId
    def contextPath = request.getServletContext().getContextPath()
%>

<div>
<div class="blue">
<ul id="mega-menu-1" class="mega-menu">
<% if (CompanySetup) { %>
    <li><g:link url="[action: 'index', controller: 'dashboard']"><g:message code="bv.menu.Home" default="Home"/></g:link></li>
    <sec:ifAnyGranted roles="ROLE_ACCOUNTANT,ROLE_ADMIN,ROLE_CUSTOMER">
        <sec:ifNotGranted roles="ROLE_CUSTOMER">
        <li>
            <g:link url="#"><g:message code="bv.menu.bankJournal" default="Bank & Journal"/></g:link>
            <ul>
                <li>
                    <g:link url="[controller: 'BankReconciliation', action: 'reconciliation']"><g:message
                            code="bv.menu.Reconciliation" default="Reconciliation"/></g:link>
                </li>
                <li>
                    <g:link url="[controller: 'BankReconciliation', action: 'manualReconciliation']">
                        <g:message code="bv.menu.ManualReconciliation" default="Manual Reconciliation"/></g:link>
                </li>
                <li>
                    <g:link url="[controller: 'UndoReconciliation', action: 'index']"><g:message code="bv.menu.UndoReconciliation"
                                                                                           default="Undo Reconciliation"/></g:link>
                </li>
                <li>
                    <g:link url="[controller: 'JournalEntry', action: 'index']"><g:message code="bv.menu.JournalEntry"
                                                                                           default="Journal Entry"/></g:link>
                </li>

                <li>
                    <g:link url="[controller: 'ManualEntryOfBankStatement', action: 'list']"><g:message
                            code="bv.menu.ManualEntryBanStatement" default="Manual Entry of Bank Statement"/></g:link>
                </li>
            </ul>
        </li>
        </sec:ifNotGranted>
    <li>
        <g:link url="#"><g:message code="bv.menu.bookings" default="Bookings"/></g:link>
        <ul>
            <li><g:link url="[controller: 'invoiceIncomeReport', action: 'listofincomebook']"><g:message
                    code="bv.menu.ListIncomeBooked" default="List of Income Booked"/></g:link></li>
            <li><g:link url="[controller: 'reportCustomerSettlement', action: 'list']"><g:message
                code="bv.menu.ListExpenseBooked" default="List of Expense Booked"/></g:link></li>
        <sec:ifNotGranted roles="ROLE_CUSTOMER">
            <li><g:link url="[controller: 'reportJournalEntry', action: 'list']"><g:message
                    code="bv.menu.ListJournalEntry" default="List of Journal Entry"/></g:link></li>
            <li><g:link url="[controller: 'reportReconciliation', action: 'listOfManualReconciliationEntry']"><g:message
                    code="bv.menu.ListManualBankStatement" default="List of All Bank Statement"/></g:link></li></sec:ifNotGranted>
        </ul>
    </li>
        <sec:ifNotGranted roles="ROLE_CUSTOMER">
        <li>
            <g:link url="#"><g:message code="bv.menu.reports"
                                       default="Reports"/></g:link>
            <ul>
                <li><g:link url="[controller: 'reports', action: 'glReport']"><g:message code="bv.menu.GLReport"
                                                                                 default="GL Report"/></g:link></li>
                <li><g:link url="[controller: 'reports', action: 'incomeStatement']"><g:message code="bv.menu.IncomeStatement"
                                                                                        default="Income Statement"/></g:link></li>

                <li><g:link url="[controller: 'reports', action: 'balanceSheetNew']"><g:message code="bv.menu.BalanceSheet"
                                                                                        default="Balance Sheet"/></g:link></li>

                <li><g:link url="[controller: 'reports', action: 'vatReport']"><g:message code="bv.menu.VatReport"
                                                                                          default="Vat Report"/></g:link></li>
                <li><g:link url="[controller: 'reports', action: 'agingReport']"><g:message code="bv.menu.AgingReport"
                                                                                            default="Aging Report"/></g:link></li>
                <li><g:link url="[controller: 'reports', action: 'trialBalanceReport']"><g:message code="bv.menu.TrialBalanceReport"
                                                                                        default="Trial Balance Report"/></g:link></li>
                <li><g:link url="[controller: 'reports', action: 'creditRiskReport']"><g:message code="bv.menu.creditRiskReport"
                                                                                             default="Credit Risk Report"/></g:link></li>
                <li><g:link url="[controller: 'reports', action: 'brokerReport']"><g:message code="bv.menu.brokerReport"
                                                                                             default="Broker Report"/></g:link></li>

            </ul>
        </li></sec:ifNotGranted>
    </sec:ifAnyGranted>
<% } %>
<sec:ifAnyGranted roles="ROLE_ACCOUNTANT,ROLE_ADMIN">
<li>
    <g:link url="#"><g:message code="bv.menu.settings"
                               default="Settings"/></g:link>
    <ul>
        <li>
            <g:link url="#"><g:message code="bv.menu.Company"
                                       default="COMPANY"/></g:link>
            <ul>

                <li>
                    <g:link url="[controller: 'companySetup', action: 'index']"><g:message code="bv.menu.CompanySetup"
                                                                                           default="Company Setup"/></g:link>
                </li>

                <% if (CompanySetup) { %>
                <li>
                    <g:link url="[controller: 'companyBankAccounts', action: 'index']"><g:message
                            code="bv.menu.CompanyBankAccount" default="Company Bank Account"/></g:link>
                </li>

                <li>
                    <g:link url="[controller: 'bankAccountType', action: 'index']"><g:message
                            code="bv.menu.BankAccountType" default="Bank Account Type"/></g:link>
                </li>

                <% } %>
            <li>
                <g:link url="[controller: 'uploadPdf', action: 'uploadPdf']"><g:message code="bv.uploadPDF.label"
                                                                                       default="Upload PDF"/></g:link>
            </li>

            </ul>
        </li>

        <% if (CompanySetup) { %>
        <li>
            <g:link url="#"><g:message code="bv.menu.SetUp"
                                       default="SETUP"/></g:link>
            <ul>
                <li>
                    <g:link url="[controller: 'customerMaster', action: 'index']"><g:message code="bv.menu.Debtors"
                                                                                             default="Debtors"/></g:link>
                </li>
                <li>
                    <g:link url="[controller: 'vendorMaster', action: 'index']"><g:message code="bv.menu.Customers"
                                                                                           default="Customers"/></g:link>
                </li>
                <li>
                    <g:link url="[controller: 'debtorCustomer', action: 'index']"><g:message code="bv.menu.DebtorCustomer"
                                                                                           default="DebtorCustomer"/></g:link>
                </li>

                <li>
                    <g:link url="[controller: 'fiscalYear', action: 'index']"><g:message code="bv.menu.FiscalYear"
                                                                                         default="Fiscal Year"/></g:link>
                </li>
                <li>
                    <g:link url="[controller: 'paymentTerms', action: 'index']"><g:message code="bv.menu.PaymentTerms"
                                                                                           default="Payment Terms"/></g:link>
                </li>
            </ul>
        </li>
        <sec:ifAnyGranted roles="ROLE_ADMIN">
        <li>
            <g:link url="#"><g:message code="bv.menu.Security" default="SECURITY"/></g:link>
            <ul>
                <li>
                    <g:link url="[controller: 'user', action: 'index']"><g:message code="bv.menu.Users" default="Users"/></g:link>
                </li>
                <li>
                    <g:link url="[controller: 'businessCompany', action: 'list']"><g:message code="businessCompany.label" default="Business Company"/></g:link>
                </li>
                <li>
                    <g:link controller="user" action="managePermission" params="[id: sec.loggedInUserInfo(field: 'id'), st: 1]"><g:message code="bv.menu.ManagePermission" default="Manage Permission"/></g:link>
                </li>
            </ul>
        </li>
        </sec:ifAnyGranted>
        <% } %>
    </ul>
</li>
%{--<% if (CompanySetup) { %>--}%


<li>
    <g:link url="#"><g:message code="bv.menu.extraSetting" default="Extra Setting"/></g:link>
    <ul>
        <sec:ifAnyGranted roles="ROLE_ADMIN">
        <li>
            <g:link url="#">GL Account</g:link>
            <ul>
                <li>
                    <g:link url="[controller: 'chartClass', action: 'index']"><g:message code="bv.menu.ChartClass"
                                                                                         default="Chart Class"/></g:link>
                </li>

                <li>
                    <g:link url="[controller: 'chartGroup', action: 'index']"><g:message code="bv.menu.ChartGroup"
                                                                                         default="Chart Group"/></g:link>
                </li>
                <li>
                    <g:link url="[controller: 'chartMaster', action: 'index']"><g:message code="bv.menu.ChartMaster"
                                                                                          default="Chart Master"/></g:link>
                </li>
            </ul>
        </li>
        </sec:ifAnyGranted>
        <li style="margin-right: 0"><g:link url="#">Default Setting</g:link>
            <ul>
                <li>
                    <g:link url="[controller: 'countries', action: 'index']"><g:message code="bv.menu.Countries"
                                                                                        default="Countries"/></g:link>
                </li>
                <li>
                    <g:link url="[controller: 'vatCategory', action: 'index']"><g:message code="bv.menu.VATCategory"
                                                                                          default="VAT Category"/></g:link>
                </li>
                <li>
                    <g:link url="[controller: 'reconciliationBookingType', action: 'index']"><g:message
                            code="bv.menu.ReconciliationBookingType" default="Reconciliation Booking Type"/></g:link>
                </li>
                <li>
                    <g:link url="[controller: 'debitCreditGlSetup', action: 'index']"><g:message
                            code="bv.menu.DebtorCreditorSetup" default="Default GL Setup"/></g:link>
                </li>
                <li>
                    <g:link url="[controller: 'companyBankGlRelation', action: 'index']"><g:message
                            code="bv.menu.CompanyGLRelation" default="Company GL Relation"/></g:link>
                </li>
            </ul>
        </li>

    </ul>
</li>

</sec:ifAnyGranted>
%{--<% } %>--}%

<li>
    <g:link url="#"><sec:loggedInUserInfo field="username"/></g:link>
    <ul>
        <%
            def permittedDbArr = new UserTagLib().gettingPermittedDBInfo(sec.loggedInUserInfo(field: 'id'))
            if(permittedDbArr.size()){
        %>

        %{---------   USER PERMISSION MENU BLOCK  ----------}%

        <li>
            <g:link url="#">Permitted Database</g:link>
            <ul>
                <%
                        if( permittedBusinessCompanyId == businessCompanyId || permittedBusinessCompanyId == null){%>
                <li>
                    <g:link controller="user" action="selectedDataBase" style="color:#000000;font-weight: bold;"
                            params="[businessCompanyId: businessCompanyId, userId: sec.loggedInUserInfo(field: 'id')]">
                        <g:message code="bv.menu.ChangePassword1" default="${sec.loggedInUserInfo(field: 'username')}"/></g:link>
                </li>
                <%}else{%>
                <li>
                    <g:link controller="user" action="selectedDataBase" params="[businessCompanyId: businessCompanyId, userId: sec.loggedInUserInfo(field: 'id')]">
                        <g:message code="bv.menu.ChangePassword1" default="${sec.loggedInUserInfo(field: 'username')}"/></g:link>
                </li>
                <%}%>

                <%

                        if(permittedDbArr.size()>0){
                            permittedDbArr.each { phn ->
                                if(permittedBusinessCompanyId == phn[2] ){
                %>

                <li>
                    <g:link controller="user" action="selectedDataBase" style="color:#000000;font-weight: bold;"
                            params="[businessCompanyId: phn[2], userId: phn[3]]"><g:message code="bv.menu.ChangePassword1" default="${phn[7]}"/></g:link>
                </li>
                <%}else{%>
                <li>
                    <g:link controller="user" action="selectedDataBase" params="[businessCompanyId: phn[2], userId: phn[3]]">
                        <g:message code="bv.menu.ChangePassword1" default="${phn[7]}"/></g:link>
                </li>
                <%}%>
                <% } } %>
            </ul>
        </li>
        <%}%>

    <sec:ifNotGranted roles="ROLE_CUSTOMER, ROLE_USER">
        <li>
            <g:link url="#"><g:message code="bv.changeRequests" default="Change Requests"/></g:link>
            <ul>
                <li><g:link controller="vendorChangeRequest" action="index"><g:message code="bv.changeRequests" default="Change Requests"/></g:link></li>
            </ul>
        </li>
    </sec:ifNotGranted>

        <li>
            <g:link url="#"><g:message code="bv.manageUser" default="MANAGE USER"/></g:link>
            <ul>
                <li><g:link controller="logout" action="index"><g:message code="bv.menu.Logout" default="Logout"/></g:link></li>
                <sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_CUSTOMER">
                <li>
                    <g:link controller="user" action="updatePassword" params="[id: sec.loggedInUserInfo(field: 'id'), st: 1]"><g:message code="bv.menu.ChangePassword" default="Change Password"/></g:link>
                </li>
                </sec:ifAnyGranted>
            </ul>
        </li>


    </ul>
</li>
</ul>

</div>
</div>