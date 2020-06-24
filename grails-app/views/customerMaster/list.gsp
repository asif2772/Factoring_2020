<%@ page import="factoring.CoreParamsHelperTagLib" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'bv.menu.Debtors', default: 'DebtorMaster')}"/>
    <title><g:message code="bv.menu.Debtors" args="[entityName]"/></title>
</head>

<body>
    <a href="#list-customerMaster" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>

    <%
        def contextPath = request.getServletContext().getContextPath()
    %>
<g:javascript>
  var isAlreadyLoaded = [true, true, false, false, true, true, false, false,false,false];
  var debId = "${customerMasterInstance?.id}"
  var cusId = 0;
  var factorPage = "debtor";
  var isValidated = false;
  var isAlreaadyClicked = false;
  function isValidCustomer(){
    if(isValidated){
        return true;
    }
    else{
        if(!isAlreaadyClicked){
            isAlreaadyClicked = true;
            $(':input[type="submit"]').prop('disabled', true);
            $("#newCusLoader").show();
            $.ajax({
                url: '${request.contextPath}'+"/customerMaster/checkDebtor",
                data: {customerName:$("#customerName").val().trim(),customerId:debId},
                success: function(data) {
                    $("#newCusLoader").hide();
                    isAlreaadyClicked = false;
                    if(data=="Ok"){
                        isValidated = true;
                        $("#newFormCustomerMaster").submit();
                    }
                    else{
                        $("#customerName").jqxTooltip("open");
                        $(':input[type="submit"]').prop('disabled', false);
                    }
                },
                error: function(){
                    isAlreaadyClicked = false;
                    $(':input[type="submit"]').prop('disabled', false);
                }
            });
        }
        return false;
    }
  }

  $(document).ready(function() {
    $("#customerName").jqxTooltip({ content: 'Debtor with this name is already exist', position: 'bottom', autoHide: true, trigger: "none", closeOnClick: false });
    $('#customerListView').dataTable( {

   "ajax":"${contextPath}/customerMaster/gridList",
     "bProcessing": true,
     "bAutoWidth":false,

     "columns": [
            { "data": "customerCode" },
            { "data": "customerName" },
            { "data": "email" },
            { "data": "creditLimit" },
            { "data": "action" }
        ]

    } );
    } );

    function clearPlaceHolder (input) {
        if (input.value == 0.0) {
            input.value = "";
        }
    }
    //ajax load data in financial transaction tab


    function setPlaceHolder (input) {
        if (input.value == "") {
            input.value = input.defaultValue;
        }
    }

     function editUrl(userId,liveUrl){
                var redirectUrl=liveUrl+"/customerMaster/list/"+userId+"?eid="+userId;
                window.location.replace(redirectUrl);
            }

</g:javascript>
<%
    def newCustomerId=''

    if (customerMasterInstance?.id) {
        newCustomerId= customerMasterInstance?.id
    }


%>
<div id="wrapper">
    <div id="tabContainer">
        <g:if test="${flash.message}">
            <div class="update_message" role="status">${flash.message}</div>
        </g:if>
        <div id="tabs" class="tabHolder">
            <%
                if (params.banktab) {
            %>
                <asset:javascript src="banktabs.js"/>
            <% } else { %>
                <asset:javascript src="tabs.js"/>
            <% } %>
            <ul>
                %{--<li id="tabHeader_1"><g:message code="bv.debtorMaster.basic.label" default="New Customer"/></li>--}%
                <% if (params.id) { %>
                <li id="tabHeader_1"><g:message code="bv.customerMaster.debtorInfo.label" default="Debtor Info"/></li>
                <li id="tabHeader_2"><g:message code="bv.customerMaster.generalAddress.label" default="General Address"/></li>
                <li id="tabHeader_3"><g:message code="bv.customerMaster.postalAddress.label" default="Postal Address"/></li>
                %{--<li id="tabHeader_4"><g:message code="bv.customerMaster.shippingAddress.label" default="Shipping Address"/></li>--}%
                <li id="tabHeader_5"><g:message code="bv.customerMaster.bankAccountInfo.label" default="Bank Account Info"/></li>
                    %{--<% if(invoiceCustomerDetailsInstance && bankPaymentInfoDetailsInstance){ %>--}%
                <li id="tabHeader_6"><g:message code="bv.customerMaster.financialTransaction.label" default="Financial Transaction"/></li>
                <li id="tabHeader_7"><g:message code="customerMaster.outstandingInvoices.label" default="Outstanding Invoices"/></li>
                <li id="tabHeader_8"><g:message code="bv.customerMaster.paidInvoices.label" default="Paid Invoices"/></li>
                <li id="tabHeader_9"><g:message code="bv.customerMaster.history.label" default="History"/></li>
                     %{--<% } %>--}%
                <% } else { %>
                    <li id="tabHeader_1"><g:message code="bv.debtorMaster.basic.label" default="New Customer"/></li>
                <% } %>
            </ul>
        </div>

        <div id="tabscontent">

        <div class="tabpage" id="tabpage_1">

            <% if (params.del) { %>
            <g:form action="delete">
                <fieldset class="form">
                    <g:render template="form"/>
                </fieldset>

                <fieldset class="buttons_new">
                    <% if (customerMasterInstance?.id) {
                    %>
                    <% if (params.del) { %>
                    <button href="${createLink(action: 'list')}"/>
                    %{-- <g:link action="list" class="close" >${message(code: 'default.button.close.label', default: 'Close')}</g:link>--}%
                    %{-- <g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />--}%
                    <g:submitButton name="delete" class="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}"/>
                    <% } else { %>
                        <g:link action="list" class="close" params="[incBItem: params.incBItem, fInv: params.fInv]"
                                style="border-radius: 6px">${message(code: 'default.button.next.label', default: 'Next')}</g:link>
                        <g:submitButton name="create" class="update" style="border-radius: 0 6px 6px 0"
                                        value="${message(code: 'default.button.update.label', default: 'Update')}"/>
                    <% } %>
                    <% } else { %>
                        <g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}"/>

                    <% } %>
                </fieldset>

            </g:form>
            <% } else { %>

            <g:form action="update" onsubmit="return isValidCustomer()" name="newFormCustomerMaster">
                <fieldset class="form">
                    <g:render template="form"/>
                </fieldset>

                <fieldset class="tabFieldset buttons_new updateLinkBtn">  <!--buttons_new updateLinkBtn-->
                <div class="btnDivStyle tabFieldsetDiv">
                    %{--<div class="btnDivStyle btnDivPosition tabFieldsetDiv" style="display: block!important;">--}%

                    <% if (customerMasterInstance?.id) { %>
                    
                    <% if (params.del) { %>
                        <button href="${createLink(action: 'list')}"/>
                        <g:submitButton name="delete" class="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}"/>
                    <% } else { %>

                        <% if (params.fInv=='1') { %>
                            <g:hiddenField name="fInv" value="${params.fInv}"/>
                            <g:hiddenField name="bookInvoiceId" value="${params.bookInvoiceId}"/>
                            <g:hiddenField name="bookingPeriod" value="${params.bookingPeriod}"/>
                            <g:hiddenField name="customerId" value="${params.customerId}"/>
                            <g:hiddenField name="budgetCustomerId" value="${params.budgetCustomerId}"/>
                            <g:hiddenField name="budgetItemDetailsId" value="${params.budgetItemDetailsId}"/>
                        <% } %>

                        <% if (params.incBItem=='1') { %>
                            <g:hiddenField name="incBItem" value="${params.incBItem}"/>
                            <g:hiddenField name="fInv" value="${params.fInv}"/>
                            <g:hiddenField name="bookingPeriod" value="${params.bookingPeriod}"/>
                            <g:hiddenField name="journalId" value="${params.journalId}"/>

                            %{--<g:link controller="budgetItemIncomeDetails" action="list" class="close"--}%
                                        %{--params="[bookingPeriod: params.bookingPeriod, journalId: params.journalId, customerId: newCustomerId]">${message(code: 'default.button.BackToInvoice.cancel.label', default: 'Cancel')}</g:link>--}%
                        <% } %>
                        <g:link controller="customerMaster" action="list" class="orangeBtn" style="height: 27px; margin-right: 12px;">${message(code: 'bv.InvoiceExpenseDetails.savingsPrint.addmore.label', default: 'Add More')}</g:link>
                        <g:submitButton name="create" class="greenBtn tabFieldsetPosition" style="border-radius: 0 6px 6px 0" value="${message(code: 'default.button.update.label', default: 'Update')}"/>
                        %{--<g:link controller="reportCustomerSettlement" action="listOutstandingInvoices" params="[strSearch:customerMasterInstance?.customer_name]" class="orangeBtn" style="height: 27px;width: 150px;">${message(code: 'bv.dashboard.outstandingInvoices.label', default: 'Outstanding Invoices')}</g:link>--}%
                        <g:link controller="debtorCustomer" action="index" params="[strSearch:customerMasterInstance?.customer_name]" class="orangeBtn" style="height: 27px;width: 150px;">${message(code: 'bv.dashboard.debtorCustomer.label', default: 'Debtor Customer')}</g:link>
                        <img id="newCusLoader" style="float: right;display: none;margin-top: 7px;margin-right: 10px;" src="${resource(dir:'images',file:'spinner.gif')}" alt="${message(code:'spinner.alt.Loading',default:'Loading...')}" />
                        <% if (params.fInv) { %>
                            %{-- After customer created --}%
                            <% if (params.invEditId) {
                                if(newCustomerId == ""){
                                    newCustomerId = id;
                                }
                            %>
                                %{-- If come in edit mode --}%
                                <g:link controller="invoiceIncome" action="list" class="close backBudgetBtn" style="height: 27px; margin-right: 12px;" params="[editId:params.invEditId,bookInvoiceId: params.bookInvoiceId, bookingPeriod: params.bookingPeriod,budgetCustomerId: params.budgetCustomerId,customerId: newCustomerId, budgetItemDetailsId: params.budgetItemDetailsId,backFrmMaster:true]">${message(code: 'default.button.BackToInvoice.backInvoice.label', default: 'Back to Invoice')}</g:link>
                           <% }else{ %>
                                <g:link controller="invoiceIncome" action="list" class="close backBudgetBtn" style="height: 27px; margin-right: 12px;" params="[bookInvoiceId: params.bookInvoiceId, bookingPeriod: params.bookingPeriod,budgetCustomerId: params.budgetCustomerId,customerId: newCustomerId, budgetItemDetailsId: params.budgetItemDetailsId,keepSession:1]">${message(code: 'default.button.BackToInvoice.backInvoice.label', default: 'Back to Invoice')}</g:link>
                            <% } %>

                        <% } %>

                        <% if (params.incBItem) { %>
                            <g:link controller="budgetItemIncomeDetails" action="list" class="close backBudgetBtn" style="height: 27px; margin-right: 12px;" params="[bookingPeriod: params.bookingPeriod, journalId: params.journalId, customerId: newCustomerId]">${message(code: 'default.button.BackToInvoice.backBudget.label', default: 'Back to Budget')}</g:link>
                        <% } %>

                    <% } %>
                    <% } else { %>

                        <% if (params.bcBtn) { %>
                            <input action="action" value="Back" type="button" onclick="history.go(-1);"/>
                        <% } %>

                        <g:actionSubmit name="create" class="save updateBtn" style="width:auto" onclick="this.form.action='${createLink(action:'update')}';" value="${message(code: 'default.button.create.label', default: 'Create &')}"/>
                        %{--<g:actionSubmit class="save updateBtn" style="width:auto" onclick="this.form.action='${createLink(action:'updateAndCreateDebtor')}';" value="${message(code: 'default.button.updateAndCreateDebtor.label', default: 'Close')}" />--}%
                        <asset:image id="newCusLoader" style="float: right;display: none;margin-top: 7px;margin-right: 10px;" src="${resource(dir:'images',file:'spinner.gif')}" alt="${message(code:'spinner.alt.Loading',default:'Loading...')}" />
                        <% if (params.fInv) { %>
                            %{-- Direct came from invoice UI --}%
                            <% if (params.invEditId) { %>
                                %{-- If come in edit mode --}%
                                <g:link controller="invoiceIncome" action="list" class="close closeBtn" style="height: 27px;"
                                        params="[editId:params.invEditId,bookInvoiceId: params.bookInvoiceId, bookingPeriod: params.bookingPeriod,budgetCustomerId: params.budgetCustomerId, customerId: params.customerId,budgetItemDetailsId: params.budgetItemDetailsId,backFrmMaster:true]">${message(code: 'default.button.BackToInvoiceCancel.backInvoice.label', default: 'Cancel')}</g:link>
                            <% }else{ %>
                                %{-- If come in without edit mode --}%
                                <g:link controller="invoiceIncome" action="list" class="close closeBtn" style="height: 27px;"
                                        params="[bookInvoiceId: params.bookInvoiceId, bookingPeriod: params.bookingPeriod,budgetCustomerId: params.budgetCustomerId, customerId: params.customerId,budgetItemDetailsId: params.budgetItemDetailsId,keepSession:1]">${message(code: 'default.button.BackToInvoiceCancel.backInvoice.label', default: 'Cancel')}</g:link>

                            <% } %>
                        <% } %>

                        <% if (params.incBItem) { %>
                                %{-- Direct came from budget UI --}%
                                <g:link controller="budgetItemIncomeDetails" action="index" class="close closeBtn" style="height: 27px;"
                                        params="[bookingPeriod: params.bookingPeriod, journalId: params.journalId, customerId: newCustomerId]">${message(code: 'default.button.BackToInvoice.backBudget.Cancel.label', default: 'Cancel')}</g:link>
                        <% } %>
                    <% } %>

                    </div>
                </fieldset>
            </g:form>
            <% } %>
        </div>

        <% if (params.id) { %>
        <div class="tabpage" id="tabpage_7">
            <fieldset class="form">
                <g:render template="outstandingInvoices"  />
            </fieldset>
        </div>
        <div class="tabpage" id="tabpage_8">
            <fieldset class="form">
                <g:render template="paidInvoices"  />
            </fieldset>
        </div>
        <div class="tabpage" id="tabpage_2">

            <g:form action="updategeneral">
                <fieldset class="form">
                    <g:render template="generalAddressForm"/>
                </fieldset>
            </g:form>
        </div>

        <div class="tabpage" id="tabpage_3">
            <g:form action="updatepostal">
                <fieldset class="form">
                    <g:render template="postalAddressForm"/>
                </fieldset>
            </g:form>
        </div>

        %{--<div class="tabpage" id="tabpage_4">

            <g:form action="updateshipping">
                <fieldset class="form">
                    <g:render template="shoppingAddressForm"/>
                </fieldset>
                <fieldset class="buttons_new">
                    <% if (customerShipmentAddressInstance?.id) { %>
                    <g:submitButton name="create" class="update"
                                    value="${message(code: 'default.button.update.label', default: 'Update')}"/>
                    <% } else { %>
                        <g:submitButton name="create" class="save"
                                    value="${message(code: 'default.button.create.label', default: 'Create')}"/>
                    <% } %>
                </fieldset>
            </g:form>
        </div>--}%

        <% /* here code will be*/ %>
        <div class="tabpage" id="tabpage_5">

            <g:form action="updatebanking">
                <fieldset class="form">
                    <g:render template="bankAccountForm"/>
                </fieldset>
                <fieldset class="tabFieldset buttons_new updateLinkBtn">
                    <div class="btnDivStyle tabFieldsetDiv">
                    <% if (!params?.bankid) { %>
                        <g:submitButton name="create" class="greenBtn tabFieldsetPosition"
                                    value="${message(code: 'default.button.create.label', default: 'Create')}"/>
                    <% } else { %>
                        <g:submitButton name="create" class="greenBtn tabFieldsetPosition"
                                        value="${message(code: 'default.button.update.label', default: 'Update')}"/>
                    <% } %>
                        </div>
                </fieldset>

                <% if (customerBankAccountInstance.size() > 0) { %>
                <table style="margin-top: 38px;margin-bottom: 10px;">
                    <thead>
                    <tr style="background: #dddddd">
                        <th width="300px"><g:message code="customerBankAccount.bankAccountName.label" default="Bank Account Name"/></th>
                        <th width="200px"><g:message code="customerBankAccount.ibanPrefix.label" default="Iban Prefix"/></th>
                        <th width="200px"><g:message code="customerBankAccount.bankAccountNo.label" default="Bank Account No"/></th>
                        <th width="150px"><g:message code="customerBankAccount.status.label" default="Status"/></th>
                        <th><g:message code="customerBankAccount.action.label" default="Action"/></th>
                    </tr>
                    </thead>
                    <tbody>
                    <g:each in="${customerBankAccountInstance}" status="i" var="customerBankAccountInstanceList">
                        <% //out << customerBankAccountInstanceList  %>
                        <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                            <td>${customerBankAccountInstanceList?.bank_account_name /*fieldValue(bean: customerBankAccountInstanceList, field: "bankAccountName")*/}</td>
                            <td>${customerBankAccountInstanceList?.iban_prefix /*fieldValue(bean: customerBankAccountInstanceList, field: "ibanPrefix")*/}</td>
                            <td>${customerBankAccountInstanceList?.bank_account_no /*fieldValue(bean: customerBankAccountInstanceList, field: "bankAccountNo")*/}</td>
                            <td>${new CoreParamsHelperTagLib().ShowStatus(customerBankAccountInstanceList?.status)}</td>
                            %{--<td>${customerBankAccountInstanceList?.status}</td>--}%
                            <td>
                                <% if (params.sort) { %>
                                <g:link style="width: 30px; float: left;" action="list"
                                        params="[bankid: customerBankAccountInstanceList?.id, banktab: 1, id: customerMasterInstance?.id, offset: params.offset, max: params.max, sort: params.sort, order: params.order]"><g:img
                                        dir="images" file="edit.png" width="15" height="16" alt="edit"/></g:link>&nbsp;

                                <% } else { %>
                                <g:link style="width: 30px; float: left;" action="list"
                                        params="[bankid: customerBankAccountInstanceList?.id, banktab: 1, id: customerMasterInstance?.id, offset: params.offset, max: params.max, order: params.order]"><g:img
                                        dir="images" file="edit.png" width="15" height="16" alt="edit"/></g:link>&nbsp;

                                <% } %>
                            </td>
                        </tr>
                    </g:each>
                    </tbody>
                </table>
                <% } %>

            </g:form>
        </div>

        <div class="tabpage" id="tabpage_6">
            <g:form action="financialTransaction">
                <fieldset class="form">
                    <g:render template="financialTransactionForm"/>
                </fieldset>
                <fieldset class="buttons_new updateLinkBtn">

                </fieldset>
            </g:form>
        </div>
            <div class="tabpage" id="tabpage_9">
                <fieldset class="form">
                    <g:render template="historyTransaction"/>
                </fieldset>
            </div>


        <% /** End of the code**/ %>

        <% } %>
        </div>  <!---tabscontent-->
    </div>  <!---tabContainer--->
</div>  <!---wrapper--->

<div>&nbsp;</div>

%{--//////// Search Code ///////////--}%
<div class="scaffold-create searchField" role="main" style="margin: 8px auto 0;">
    %{--<% def contextPath = request.getServletContext().getContextPath()%>--}%
    <label class="searchHeadLineLabel">
        <g:message code="debtor.master.list.label" args="[entityName]"/>
    </label>
</div>


<div id="list-page-body-inner" class="content">
    <div id="list-page">
        <div id="customerList" class="content scaffold-create" role="main" style="margin-top: -28px;margin-bottom: 0px;">
            <table id="customerListView" class="display" cellspacing="0" width="100%">
                <thead>
                <tr>
                    <th>${message(code: 'customerMaster.customerCode.label', default: 'Code')}</th>
                    <th>${message(code: 'debtorMaster.debtorName.label', default: 'Debtor Name')}</th>
                    <th>${message(code: 'customerMaster.email.label', default: 'Email Address')}</th>
                    <th>${message(code: 'customerMaster.creditLimit.label', default: 'Credit Limit')}</th>
                    <th>${message(code: 'common.gridAction.label', default: 'Action')}</th>
                </tr>
                </thead>
            </table>
        </div>  <!--content-->
    </div>  <!--id="list-page"-->
</div>  <!--content-->

<div id="cusLoadSpinner" style="display: none;">
    <div class="spinLoader fieldContainer">
        <asset:image src="spinner.gif" alt="${message(code:'spinner.alt.Loading',default:'Loading...')}" />
    </div>
</div>
</body>
</html>