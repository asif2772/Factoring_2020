<%@ page import="factoring.CoreParamsHelperTagLib; factoring.VendorMaster" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'bv.menu.Customers', default: 'CustomerMaster')}"/>
    <title><g:message code="bv.menu.Customers" args="[entityName]"/></title>
</head>

<body>

<a href="#list-vendorMaster" class="skip" tabindex="-1">
    <g:message code="default.link.skip.label" default="Skip to content&hellip;"/>
</a>

<% def contextPath = request.getServletContext().getContextPath()
   def userCustomerId = userCustomerId
%>
<g:javascript>
      var isAlreadyLoaded = [true, true, false, false, true, true, false, false,false];
      var cusId = "${vendorMasterInstance?.id}";
      var debId = 0;
      var factorPage = "customer";
      var isValidated = false;
      var isAlreaadyClicked = false;
      var dvenId = "${vendorMasterInstance?.id}";
      var dvenPrefix = "${vendorPrefix}";
      var dvenCode = "${vendorMasterInstance?.vendor_code}";
      var dvenName = "${vendorMasterInstance?.vendor_name}";

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
                    url: '${request.contextPath}'+"/vendorMaster/checkCustomer",
                    data: {vendorName:$("#vendorName").val().trim(),customerId:cusId},
                    success: function(data) {
                        $("#newCusLoader").hide();
                        isAlreaadyClicked = false;
                        if(data=="Ok"){
                            isValidated = true;
                            $("#newFormVendorMaster").submit();
                        }
                        else{
                            $("#vendorName").jqxTooltip({ content: 'Vendor with this name is already exist',
                            position: 'bottom', autoHide: true, trigger: "none", closeOnClick: false });
                            $("#vendorName").jqxTooltip("open");
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
      $( window ).load(function() {
         if(${userCustomerId} > 0){
           $( "#tabHeader_2" ).trigger( "click" );
           console.info("userCustomerId: "+${userCustomerId})
        }
        else{
           getCustomerGrid();
           console.warn("userCustomerId: "+${userCustomerId})
        }
      });

     function getCustomerGrid(){

        $('#vendorListView').dataTable( {

                 "ajax":"${contextPath}/vendorMaster/userGrid",
                 "bProcessing": true,
                 "bAutoWidth":false,

                  "columns": [
                              { "data": "vendorCode" },
                              { "data": "vendorName" },
                              { "data": "email" },
                              { "data": "creditLimit" },
                              { "data": "action" }
                          ]
         } );
    }

    function clearPlaceHolder (input) {
        if (input.value == 0.0) {
            input.value = "";
        }
    }

    function setPlaceHolder (input) {
        if (input.value == "") {
            input.value = input.defaultValue;
        }
    }

     function editUrl(userId,liveUrl){
            var redirectUrl=liveUrl+"/vendorMaster/list/"+userId+"?/eid="+userId;
            window.location.replace(redirectUrl);
        }

</g:javascript>

<%
    def newVendorId = ''
    if (vendorMasterInstance?.id) {
        newVendorId = vendorMasterInstance?.id
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
            <asset:javascript src="vendorbanktabs.js"/>
        <% } else { %>
            <asset:javascript src="tabs.js"/>
        <% } %>

        <ul>
            <li id="tabHeader_1"><g:message code="bv.vendorMaster.basic.data.label" default="New Customer"/></li>
            <% if (params.id) { %>
            <li id="tabHeader_2"><g:message code="bv.vendorMaster.officeAddress.label" default="Office Address"/></li>
            <li id="tabHeader_3"><g:message code="bv.vendorMaster.postalAddress.label" default="Postal Address"/></li>
            <li id="tabHeader_4"><g:message code="bv.vendorMaster.bankAccInfo.label" default="Bank Account"/></li>
            <li id="tabHeader_5"><g:message code="bv.customerMaster.factoring.label" default="Factoring"/></li>
        %{--<sec:ifNotGranted roles="ROLE_CUSTOMER">--}%
            <li id="tabHeader_6"><g:message code="bv.customerMaster.financialTrans.label" default="Financial Trans."/></li>
            <li id="tabHeader_7"><g:message code="customerMaster.outstandingInvoices.label" default="Outstanding"/></li>
            <li id="tabHeader_8"><g:message code="bv.customerMaster.paidInvo.label" default="Paid Invo."/></li>
        %{--</sec:ifNotGranted>--}%
            <% } %>
        </ul>


    </div>  <!--id= tabs -->

    <div id="tabscontent" class="tabsContentBtn">
        <div class="tabpage" id="tabpage_1">

            <g:form name="newFormVendorMaster" onsubmit="return isValidCustomer()" action="update">
                <fieldset class="form">
                    <g:render template="form"/>
                </fieldset>
                <fieldset class="buttons_new updateLinkBtn">
                    <% if (vendorMasterInstance?.id) { %>
                    <% if (params.del) { %>
                    <g:link action="list" class="close 1">
                        ${message(code: 'default.button.next.label', default: 'Next')}</g:link>
                    <g:actionSubmit class="delete" action="delete"
                                    value="${message(code: 'default.button.delete.label', default: 'Delete')}"
                                    onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
                    <% } else {  %>
                    %{--<sec:ifNotGranted roles="ROLE_CUSTOMER">--}%
                    <g:link controller="vendorMaster" action="list" class="orangeBtn" style="height: 27px; margin-right: 12px;">
                        ${message(code: 'bv.InvoiceExpenseDetails.savingsPrint.addmore.label', default: 'Add More')}</g:link>


                    <g:submitButton name="create" class="greenBtn tabFieldsetPosition" style="margin-right: 11px;"
                                    value="${message(code: 'default.button.update.label', default: 'Update')}"/>
                    <g:link controller="reportCustomerSettlement" action="listOutstandingInvoices" params="[strSearch:vendorMasterInstance?.vendor_name]" class="orangeBtn" style="height: 27px;width: 150px;">${message(code: 'bv.dashboard.outstandingInvoices.label', default: 'Outstanding Invoices')}</g:link>
                    %{--</sec:ifNotGranted>--}%
                    <% if (params.fInv == '1') { %>
                    <g:hiddenField name="fInv" value="${params.fInv}"/>
                    <g:hiddenField name="bookInvoiceId" value="${params.bookInvoiceId}"/>
                    <g:hiddenField name="bookingPeriod" value="${params.bookingPeriod}"/>
                    <g:hiddenField name="vendorId" value="${params.vendorId}"/>
                    <g:hiddenField name="editId" value="${params.invEditId}"/>
                    <g:hiddenField name="budgetItemDetailsId" value="${params.budgetItemDetailsId}"/>
                      <%if(params.invEditId){%>

                                <g:link controller="invoiceExpense" action="list" class="close backBudgetBtn"
                                params="[editId:params.invEditId,bookInvoiceId: params.bookInvoiceId, bookingPeriod: params.bookingPeriod, budgetVendorId: params.vendorId, vendorId: params.vendorId, budgetItemDetailsId: params.budgetItemDetailsId, newVenId: newVendorId,backFrmMaster:true]">
                                ${message(code: 'default.button.BackToInvoice.backInvoice.label', default: 'Back to Invoice')}</g:link>
                       <%}else{ %>
                                <g:link controller="invoiceExpense" action="list" class="close backBudgetBtn"
                                        params="[bookInvoiceId: params.bookInvoiceId, bookingPeriod: params.bookingPeriod, budgetVendorId: params.vendorId, vendorId: params.vendorId, budgetItemDetailsId: params.budgetItemDetailsId, newVenId: newVendorId,keepSession:1]">
                                    ${message(code: 'default.button.BackToInvoice.backInvoice.label', default: 'Back to Invoice')}</g:link>
                    <% } %>
                    <%}%>
                    <% if (params.expBItem == '1') { %>
                    <g:hiddenField name="expBItem" value="${params.expBItem}"/>
                    <g:hiddenField name="bookingPeriod" value="${params.bookingPeriod}"/>
                    <g:hiddenField name="journalId" value="${params.journalId}"/>

                    <g:link controller="budgetItemExpenseDetails" action="list" class="close backBudgetBtn"
                            params="[bookingPeriod: params.bookingPeriod, journalId: params.journalId, vendorId: newVendorId]">${
                            message(code: 'default.button.BackToInvoice.backBudget.label', default: 'Back to Budget')}</g:link>
                    <% } %>
                    <% if (params.sid == '1') {
                    %>
                    <g:hiddenField name="sid" value="${params.sid}"/>
                    <g:hiddenField name="bookInvoiceId" value="${params.bookInvoiceId}"/>
                    <g:hiddenField name="bookingPeriod" value="${params.bookingPeriod}"/>
                    <g:hiddenField name="vendorId" value="${params.vendorId}"/>
                    <g:hiddenField name="editId" value="${params.invEditId}"/>
                    <g:hiddenField name="budgetItemDetailsId" value="${params.budgetItemDetailsId}"/>
                    <% } %>
                    <% } %>
                    <% } else {  %>
                    <g:hiddenField name="editId" value="${params.invEditId}"/>
                    %{--<sec:ifNotGranted roles="ROLE_CUSTOMER">--}%
                    <g:actionSubmit name="create" class="save updateBtn btnWidth" onclick="this.form.action='${createLink(action:'update')}';"
                                    value="${message(code: 'default.button.create.label', default: 'Create & Add Another')}"/>
                    %{--</sec:ifNotGranted>--}%
                    <img id="newCusLoader" style="float: right;display: none;margin-top: 7px;margin-right: 10px;" src="${resource(dir:'images',file:'spinner.gif')}" alt="${message(code:'spinner.alt.Loading',default:'Loading...')}" />
                    <% if (params.bcBtn) { %>
                    <input class="close closeBtn" action="action"
                           value="${message(code: 'default.button.back.level', default: 'Back')}" type="button"
                           onclick="history.go(-1);"/>
                    <% } %>
                    <% if (params.fInv) { %>
                      <%if (params.invEditId){%>
                            <g:link controller="invoiceExpense" action="list" class="close closeBtn"
                                    style="height: 27px; margin-right: 12px;">${
                                    message(code: 'default.button.BackToInvoiceCancel.backInvoice.label', default: 'Cancel')}</g:link>
                        <%}else{%>
                           <%if(newVendorId){%>
                                <g:link controller="invoiceExpense" action="list" class="close closeBtn"
                                    style="height: 27px; margin-right: 12px;"
                                    params="[bookInvoiceId: params.bookInvoiceId, bookingPeriod: params.bookingPeriod,budgetVendorId: params.vendorId, vendorId: params.vendorId, budgetItemDetailsId: params.budgetItemDetailsId,newVenId: newVendorId,keepSession:1]">${
                                    message(code: 'default.button.BackToInvoiceCancel.backInvoice.label', default: 'Cancel')}</g:link>
                            <%}else{%>
                                 <g:link controller="invoiceExpense" action="list" class="close closeBtn"
                                    style="height: 27px; margin-right: 12px;">${
                                    message(code: 'default.button.BackToInvoiceCancel.backInvoice.label', default: 'Cancel')}</g:link>
                            <%}%>
                        <% } %>
                    <%}%>
                    <% if (params.expBItem) { %>
                    <g:link controller="budgetItemExpenseDetails" action="list" class="close closeBtn" style="height: 27px;"
                            params="[bookingPeriod: params.bookingPeriod, journalId: params.journalId, vendorId: newVendorId]">${
                            message(code: 'default.button.BackToInvoice.backBudget.Cancel.label', default: 'Cancel')}</g:link>
                    <% } %>
                    <% if (params.sid) { %>
                    <%if( params.invEditId!='0'&& params.invEditId!='null' ){  %>

                        <g:link controller="invoiceExpense" action="receiptList" class="close closeBtn 8" style="height: 27px;"
                                params="[editId:params.invEditId,bookInvoiceId: params.bookInvoiceId, bookingPeriod: params.bookingPeriod,budgetVendorId: params.vendorId, vendorId: params.vendorId, budgetItemDetailsId: params.budgetItemDetailsId,backFrmMaster:true,vendorShopId:params.shopId]">${
                                message(code: 'default.button.BackToInvoiceCancel.backInvoice.label', default: 'Cancel')}</g:link>
                    <%}else{ %>
                        <g:link controller="invoiceExpense" action="receiptList" class="close closeBtn 8" style="height: 27px;"
                                params="[bookInvoiceId: params.bookInvoiceId, bookingPeriod: params.bookingPeriod,budgetVendorId: params.vendorId, vendorId: params.vendorId, budgetItemDetailsId: params.budgetItemDetailsId,vendorShopId:params.shopId,keepSession:1]">${
                                message(code: 'default.button.BackToInvoiceCancel.backInvoice.label', default: 'Cancel')}</g:link>
                    <%}%>
                    <% } %>
                    <% } %>
                </fieldset>
            </g:form>
        </div>
    <% if (params.id) { %>
        <div class="tabpage" id="tabpage_7">
            <fieldset class="form" id="tabPageLoadData7">
                <div class="spinLoader fieldContainer">
                    <img src="${resource(dir:'images',file:'spinner.gif')}" alt="${message(code:'spinner.alt.Loading',default:'Loading...')}" />
                </div>
            </fieldset>
        </div>
        <div class="tabpage" id="tabpage_8">
            <fieldset class="form" id="tabPageLoadData8">
                <div class="spinLoader fieldContainer">
                    <img src="${resource(dir:'images',file:'spinner.gif')}" alt="${message(code:'spinner.alt.Loading',default:'Loading...')}" />
                </div>
            </fieldset>
        </div>
        <div class="tabpage" id="tabpage_2">
            <g:form >
                <div id="tabPageLoadData2" class="fieldContainer">
                    <div class="spinLoader fieldContainer">
                        <img src="${resource(dir:'images',file:'spinner.gif')}" alt="${message(code:'spinner.alt.Loading',default:'Loading...')}" />
                    </div>
                </div>
                <% if(params.fInv){ %>
                <g:hiddenField name="bookInvoiceId" value="${params.bookInvoiceId}" />
                <g:hiddenField name="bookingPeriod" value="${params.bookingPeriod}" />
                <g:hiddenField name="vendorId" value="${params.vendorId}" />
                <g:hiddenField name="budgetItemDetailsId" value="${params.budgetItemDetailsId}" />
                <g:hiddenField name="fInv" value="${params.fInv}" />
                <% }%>

                <% if(params.sid){ %>
                <g:hiddenField name="bookInvoiceId" value="${params.bookInvoiceId}" />
                <g:hiddenField name="bookingPeriod" value="${params.bookingPeriod}" />
                <g:hiddenField name="vendorId" value="${params.vendorId}" />
                <g:hiddenField name="budgetItemDetailsId" value="${params.budgetItemDetailsId}" />
                <g:hiddenField name="sid" value="${params.sid}" />
                <% }%>

                <% if(params.expBItem){ %>
                <g:hiddenField name="bookingPeriod" value="${params.bookingPeriod}" />
                <g:hiddenField name="vendorId" value="${params.vendorId}" />
                <g:hiddenField name="journalId" value="${params.journalId}" />
                <g:hiddenField name="expBItem" value="${params.expBItem}" />
                <% }%>
            </g:form>
        </div>

        <div class="tabpage" id="tabpage_3">

            <g:form >
                <div id="tabPageLoadData3" class="fieldContainer">
                    <div class="spinLoader fieldContainer">
                        <img src="${resource(dir:'images',file:'spinner.gif')}" alt="${message(code:'spinner.alt.Loading',default:'Loading...')}" />
                    </div>
                </div>
                <% if(params.fInv){ %>
                <g:hiddenField name="bookInvoiceId" value="${params.bookInvoiceId}" />
                <g:hiddenField name="bookingPeriod" value="${params.bookingPeriod}" />
                <g:hiddenField name="vendorId" value="${params.vendorId}" />
                <g:hiddenField name="budgetItemDetailsId" value="${params.budgetItemDetailsId}" />
                <g:hiddenField name="fInv" value="${params.fInv}" />
                <% }%>

                <% if(params.sid){ %>
                <g:hiddenField name="bookInvoiceId" value="${params.bookInvoiceId}" />
                <g:hiddenField name="bookingPeriod" value="${params.bookingPeriod}" />
                <g:hiddenField name="vendorId" value="${params.vendorId}" />
                <g:hiddenField name="budgetItemDetailsId" value="${params.budgetItemDetailsId}" />
                <g:hiddenField name="sid" value="${params.sid}" />
                <% }%>

                <% if(params.expBItem){ %>
                <g:hiddenField name="bookingPeriod" value="${params.bookingPeriod}" />
                <g:hiddenField name="vendorId" value="${params.vendorId}" />
                <g:hiddenField name="journalId" value="${params.journalId}" />
                <g:hiddenField name="expBItem" value="${params.expBItem}" />
                <% }%>
            </g:form>
        </div>

        <div class="tabpage" id="tabpage_4">
            <g:form>
                    <g:render template="bankAccountForm"/>
                %{--<sec:ifNotGranted roles="ROLE_CUSTOMER">--}%
            %{--Bank Account List--}%
                <%
                        if (vendorBankAccountInstance.size() > 0) { %>
                <table style="margin-top: 55px">
                    <thead>
                    <tr style="background: #dddddd">
                        <th width="300px"><g:message code="vendorBankAccount.bankAccountName.label" default="Bank Account Name"/></th>
                        <th width="200px"><g:message code="vendorBankAccount.ibanPrefix.label" default="Iban Prefix"/></th>
                        <th width="200px"><g:message code="vendorBankAccount.bankAccountNo.label" default="Bank Account No"/></th>
                        <th width="150px"><g:message code="vendorBankAccount.status.label" default="Status"/></th>
                        <th> <g:message code="invoiceExpense.gridList.action.label" default="Action"/> </th>
                    </tr>
                    </thead>

                    <tbody>
                    <g:each in="${vendorBankAccountInstance}" status="i" var="vendorBankAccountInstanceList">
                        <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                            <td>${vendorBankAccountInstanceList?.bank_account_name}</td>
                            <td>${vendorBankAccountInstanceList?.iban_prefix}</td>
                            <td>${vendorBankAccountInstanceList?.bank_account_no}</td>
                            <td>${new CoreParamsHelperTagLib().ShowStatus(vendorBankAccountInstanceList?.status)}</td>
                            <td>
                                <% if (params.sort) { %>
                                <g:link style="width: 30px; float: left;" action="list"
                                        params="[bankid: vendorBankAccountInstanceList.id, banktab: 1, id: vendorMasterInstance?.id, offset: params.offset, max: params.max, sort: params.sort, order: params.order, bookInvoiceId: params.bookInvoiceId, bookingPeriod: params.bookingPeriod, vendorId: params.vendorId, budgetItemDetailsId: params.budgetItemDetailsId, fInv: params.fInv]"><g:img
                                        dir="images" file="edit.png" width="15" height="16" alt="edit"/></g:link>&nbsp;

                                <% } else { %>
                                <g:link style="width: 30px; float: left;" action="list"
                                        params="[bankid: vendorBankAccountInstanceList.id, banktab: 1, id: vendorMasterInstance?.id, offset: params.offset, max: params.max, order: params.order, bookInvoiceId: params.bookInvoiceId, bookingPeriod: params.bookingPeriod, vendorId: params.vendorId, budgetItemDetailsId: params.budgetItemDetailsId, fInv: params.fInv]"><g:img
                                        dir="images" file="edit.png" width="15" height="16" alt="edit"/></g:link>&nbsp;

                                <% } %>
                            </td>
                        </tr>
                    </g:each>
                    </tbody>
                </table>
                <% } %>
                %{--</sec:ifNotGranted>--}%
            </g:form>
        </div>

        <div class="tabpage" id="tabpage_5">
            <g:form action="updateFactoring">
                <fieldset class="form">
                    <g:render template="factoringForm" />
                </fieldset>
                %{--<sec:ifNotGranted roles="ROLE_CUSTOMER">--}%
                    <fieldset class="buttons_new updateLinkBtn">
                        <% if (!vendorFactoringInstance?.id) { %>
                        <g:submitButton name="create" class="save updateBtn 2" value="${message(code: 'default.button.create.label', default: 'Create')}"/>
                        <% } else { %>
                        <g:submitButton name="create" class="update updateBtn 1" value="${message(code: 'default.button.update.label', default: 'Update')}"/>
                        <% } %>
                    </fieldset>
                %{--</sec:ifNotGranted>--}%
            </g:form>
        </div>

        <div class="tabpage" id="tabpage_6">
            <g:form action="financialTransaction">
                <fieldset class="form" id="financialTabInfoData">
                    <div class="spinLoader fieldContainer">
                        <img src="${resource(dir:'images',file:'spinner.gif')}" alt="${message(code:'spinner.alt.Loading',default:'Loading...')}" />
                    </div>
                </fieldset>
                <fieldset class="buttons_new updateLinkBtn">
                </fieldset>
            </g:form>
        </div>


    %{--Here will be code apply--}%

    <% } %>
    </div>
    </div>  <!--tabContainer-->
</div>  <!--wrapper-->

<div>&nbsp;</div>
%{--<sec:ifNotGranted roles="ROLE_CUSTOMER">--}%
<div class="scaffold-create searchField" role="main" style=" margin: 8px auto 0;">
    <label class="searchHeadLineLabel">
        <g:message code="customer.master.list.label" args="[entityName]"/>
    </label>
</div>

<div id="list-page-body-inner" class="content">
    <div id="list-page">
        <div id="vendorList" class="content scaffold-create" role="main" style="margin-top: -28px;margin-bottom: 0px;">
          <table id="vendorListView" class="display" cellspacing="0" width="100%">
            <thead>
                <tr>
                    <th >${message(code: 'vendorMaster.vendorCode.label', default: 'Customer Code')}</th>
                        <th>${message(code: 'vendorMaster.vendorName.label', default: 'Customer Name')}</th>
                        <th>${message(code: 'vendorMaster.email.label', default: 'Email Address')}</th>
                        <th>${message(code: 'vendorMaster.creditLimit.label', default: 'Credit Limit')}</th>
                        <th>${message(code: 'invoiceExpense.gridList.action.label', default: 'Action')}</th>
                    </tr>
                    </thead>
                </table>
        </div>
     </div>
</div>
%{--</sec:ifNotGranted>--}%

<div>&nbsp;</div>

</body>
</html>
