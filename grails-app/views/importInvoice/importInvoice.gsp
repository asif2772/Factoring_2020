<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.text.SimpleDateFormat; factoring.CoreParamsHelperTagLib;" %>
<%  def contextPath = request.getServletContext().getContextPath() %>

<html>
    <head>
        <meta name="layout" content="main">
        <g:set var="entityName" value="${message(code: 'bv.ImportInvoice.Header.label', default: 'Import Invoices')}"/>
        <title><g:message code="default.name.label" args="[entityName]"/></title>
    </head>
    <g:javascript>
        var searchTimeout;
        var searchHideTimeout;
        $(document).ready(function() {
            var selCustomer = "${invoiceInfo?.vendorId}";
            $.fn.dataTable.moment('DD-MM-YYYY');
            $.fn.dataTable.moment('MMM-YYYY');
            $("#invoiceDate").datepicker({dateFormat: 'dd-mm-yy',showOn: "button"});
            $("#processDiv").hide();
            if(selCustomer.length > 0)
                $("#customerId").val(selCustomer);
            $('#importInvoiceListView').dataTable( {
             "ajax":"${contextPath}/ImportInvoice/showInvoiceList",
             "bProcessing": true,
             "bAutoWidth":false,
             "aLengthMenu": [10,50, 75,100,150,200,300],
             "iDisplayLength": 50,
             "fnRowCallback": function( nRow, aData, iDisplayIndex, iDisplayIndexFull ) {
                if ( aData.flag == '1'){
                    $('td', nRow).css('background-color', 'Red');
                }else if(aData.flag == '2'){
                    $('td', nRow).css('background-color', 'Yellow');
                }else if(aData.flag == '3'){
                    $('td', nRow).css('background-color', 'Orange ');
                }else if(aData.flag == '4'){
                    $('td', nRow).css('background-color', 'Yellow ');
                }
             },
             "columns": [
                    { "data": "paymentRef" },
                    { "data": "debtorName" },
                    { "data": "customerName" },
                    { "data": "invoiceDate" },
                    { "data": "totalAmount" },
                    { "data": "vat" },
                    { "data": "action" }
                ]
            });

        });

        function closeSearchBox() {
            $(".nSearchBoxResult").hide();
        }

        $(".nSearchBoxText").focus(function() {
            $(".nSearchBoxResult").hide();
        });

        $(".nSearchBoxText").blur(function() {
            clearTimeout(searchHideTimeout);
            searchHideTimeout = setTimeout(function() {
                 $(".nSearchBoxResult").hide();
            },500)
        });

        $(".nSearchBoxText").keyup(function() {
            var debtor = $(this).data('debtor');
            var idSearch = "#"+$(this).attr('id');
            var strSearch = $(this).val();
            clearTimeout(searchTimeout);
            if(strSearch.length>1){
                searchTimeout = setTimeout(function() {
                     debtorSearch(strSearch,idSearch,debtor);
                },100)
            }
            else{
                $(idSearch).next(".nSearchBoxResult").children(".nSearchBoxResultSet").hide();
            }

        });

        //nSearchBoxResult,nSpinLoader,nSearchBoxResultSet
        function debtorSearch(strSearch,idSearch,debtor){
            $(idSearch).next(".nSearchBoxResult").show();
            $.ajax({
                url         : "${contextPath}/ImportInvoice/searchDebtor",
                type        : "POST",
                data        : {strDebtor : strSearch,debtor:debtor},
                success     : function(data, status) {
                    if(data.length > 10){
                        $(idSearch).next(".nSearchBoxResult").children(".nSearchBoxResultSet").html(data);
                        $(idSearch).next(".nSearchBoxResult").children(".nSearchBoxResultSet").show();
                    }
                    else{
                        $(idSearch).next(".nSearchBoxResult").children(".nSearchBoxResultSet").html('<div class="nSearchItem">No result found</div>');
                        $(idSearch).next(".nSearchBoxResult").children(".nSearchBoxResultSet").show();
                    }
                },
                error       : function(request,error) {
                }
            })
        }

        function processInvoice(){
             hideProcessButton();
             $.ajax({
                 url: '${contextPath}/ImportInvoice/processInvoice',
                 type: 'POST',
                 data: {},
                 success: function (resp) {
                     if (resp.result == 'success') {
                        location.href="${contextPath}/importInvoice/importInvoice";
                     }
                     showProcessButton();
                 },
                 error: function (resp) {
                    alert(JSON.stringify(resp));
                    showProcessButton();
                 }
             });
        }

        function isCustomerSelected(){
            if($("#customerId").val().length > 0 && $("#xlsxFile").val().length > 0)
                return true;
            else
                return false;
        }

        function updateInvoice(){
              $.ajax({
                    url: '${contextPath}/ImportInvoice/updateInvoice',
                    type: 'POST',
                    data: {
                         id:'${params.editId}',
                         debtorName: document.getElementById("debtorName").value,
                         vendorId: document.getElementById("customerId").value,
                         paymentRef: document.getElementById("paymentRef").value,
                         invoiceDate: document.getElementById("invoiceDate").value,
                         iban: document.getElementById("iban").value,
                         totalAmntWithVat: document.getElementById("totalAmntWithVat").value,
                         totalAmntWithOutVat: document.getElementById("totalAmntWithOutVat").value,
                         vat: document.getElementById("vat").value,
                         debtorContactPerson: document.getElementById("debtorContactPerson").value
                     },
                     success: function (resp) {
                         if (resp.result == 'success' ) {
                            location.href="${contextPath}/importInvoice/importInvoice";
                         }
                     },
                     error: function (resp) {
                        alert(JSON.stringify(resp));
                     }
              });
        }

        function deleteImportInvoice(invoiceId){
             $.ajax({
                 url: '${contextPath}/ImportInvoice/deleteImportInvoice',
                 type: 'POST',
                 data: {
                     invoiceId: invoiceId
                 },
                 success: function (resp) {
                     if (resp.result == 'success') {
                        location.href="${contextPath}/importInvoice/importInvoice";
                     }
                 },
                 error: function (resp) {
                    alert(JSON.stringify(resp));
                 }
             });
        }

        function processDuplicateInvoice(){
             hideProcessButton();
             $.ajax({
                 url: '${contextPath}/ImportInvoice/processDuplicateInvoice',
                 type: 'POST',
                 data: {

                 },
                 success: function (resp) {
                     if (resp.result == 'success') {
                        location.href="${contextPath}/importInvoice/importInvoice";
                     }
                     showProcessButton();
                 },
                 error: function (resp) {
                    alert(JSON.stringify(resp));
                    showProcessButton();
                 }
             });
        }

        function deleteAllInvoice(){
             hideProcessButton();
             $.ajax({
                 url: '${contextPath}/ImportInvoice/deleteAllImportInvoice',
                 type: 'POST',
                 data: {
                 },
                 success: function (resp) {
                     if (resp.result == 'success') {
                        location.href="${contextPath}/importInvoice/importInvoice";
                     }
                     showProcessButton();
                 },
                 error: function (resp) {
                    alert(JSON.stringify(resp));
                    showProcessButton();
                 }
             });
        }

        function hideProcessButton(){
            $("#processDiv").show();
            $("#Process").hide();
            $("#ProcessDuplicatedata").hide();
            $("#deleteAll").hide();
        }

        function showProcessButton(){
            $("#processDiv").hide();
            $("#Process").show();
            $("#ProcessDuplicatedata").show();
            $("#deleteAll").show();
        }

    </g:javascript>
</head>
<body>
    <div id="list-page-body-inner" class="content white-background">
        <div id="list-page">
            <div class="budgetHeader">
                <div class="headerMainLeft">
                    <label class="">
                        <g:message code="bv.ImportInvoice.Header.label" />
                    </label>
                </div>
            </div>
            <div class="boxouter boxouter-modified">
                <div class="navigation"></div>  <!--navigation-->
                <div class="formInvoiceField" style="">
                    <%if(params.editId){%>
                        <fieldset class="form" style="margin-bottom: 10px;">
                            <g:render template="editForm"/>
                        </fieldset>
                    <%}else{%>
                        <fieldset class="" style="margin-bottom: 25px;">
                            <g:if test="${flash.message}">
                                <div class="update_message" role="status">${flash.message}</div>
                            </g:if>
                            <g:form method="post" enctype="multipart/form-data" action="ImportInvoiceFile" onsubmit="return isCustomerSelected()">
                                <div class="formInvoiceContainer">
                                    <div class="fieldContainerLeft" style="margin-right: -165px;">
                                        <div class="rowContainer">
                                            <div class="fieldContainer required fcSpecialCombo">
                                                <label for="customerId">
                                                    <g:message code="reportIncomeBudget.customerName.label" default="Customer Name"/><b>:</b>
                                                    <span class="required-indicator">*</span>
                                                </label>
                                                <%
                                                        def cusList = new CoreParamsHelperTagLib().getCustomerListDropDownBox('customerId')
                                                %>
                                                <select class="styled sidebr01" name="customerId" id="customerId">
                                                %{-- <option value="">- no select -</option>--}%
                                                    <g:each in="${cusList}" var="opt">
                                                        <g:if test="${cusList.size() > 0}">
                                                            <option  value="${opt.value}">${opt.index}</option>
                                                        </g:if>
                                                        <g:else>
                                                            <option value="0">"No items.."</option>
                                                        </g:else>
                                                    </g:each>
                                                </select>
                                            </div>
                                        </div>
                                        <input id="xlsxFile" style="float: left;" type="file" name="xlsxFile" value=""/>
                                        <div class="btnDivStyle uploadPdfBtn">
                                            <g:actionSubmit class="greenBtn" style="float: left;" action="ImportInvoiceFile" value="${g.message(code: 'bv.uploadExcelFile.label')}">
                                            </g:actionSubmit>
                                        </div>
                                    </div>
                                    <div class="fieldContainerRight">
                                        <div class="rowContainer">
                                            <div class="fieldContainer required fcSpecialCombo">
                                                <label>
                                                    <g:message code="budgetItemIncome.bookingPeriodForChange.label" default="Booking Period"/>
                                                    <span class="required-indicator">*</span>
                                                </label>
                                                <%
                                                        def bookingPeriod = new CoreParamsHelperTagLib().showBookingPeriod('bookingPeriod',invoiceIncomeInstance?.bookingPeriod)
                                                %>
                                                <select class="styled sidebr01" name="bookingPeriod" id="bookingPeriod">
                                                %{-- <option value="">- no select -</option>--}%
                                                    <g:each in="${bookingPeriod}" var="opt">
                                                        <g:if test="${bookingPeriod.size() > 0}">
                                                            <g:if test="${opt.inSelected}">
                                                                <option selected value="${opt.value}">${opt.index}</option>
                                                            </g:if>
                                                            <g:else>
                                                                <option value="${opt.value}">${opt.index}</option>
                                                            </g:else>
                                                        </g:if>
                                                        <g:else>
                                                            <option value="0">"No items.."</option>
                                                        </g:else>
                                                    </g:each>
                                                </select>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </g:form>
                        </fieldset>
                    <%}%>
                </div>  <!--formInvoiceField-->
            </div>
            <div class="scaffold-create searchField" role="main">
                <label class="searchHeadLineLabel">
                    <g:message code="bv.ImportInvoice.Header.label" default="Import Invoices" />
                </label>
            </div>
            <div class="boxouter" id="expenseInvoiceList">
                <%if(!params.editId){%>
                    <g:if test="${debtorDifferentAddressList}">
                        <div class="content" style="margin-top: 0px;">
                            <div class="debAlert">Warning! Debtor address is different, Please change debtor status before process.</div>
                            <table class="display dataTable no-footer" cellspacing="0" width="99%">
                                <thead>
                                <tr>
                                    <th width="33%">${message(code: 'debtorMaster.debtorName.label', default: 'Debtor Name')}</th>
                                    <th width="33%">${message(code: 'debtorMaster.change.address.status.label', default: 'Change Address')}</th>
                                    <th width="33%">${message(code: 'debtorMaster.RejectAddress.label', default: 'Reject Address')}</th>
                                </tr>
                                </thead>
                                <g:each var="debtorAddressData" in="${debtorDifferentAddressList}">
                                    <tr>
                                        <td>
                                            ${debtorAddressData[9]}
                                        </td>
                                        <td>
                                            <g:link class="dbtBtn debOvr debacc" controller="importInvoice" action="changeImportInvoice" params="[originalDebtor: debtorAddressData[9],acceptAddress:'yes']">Accept Address</g:link>
                                        </td>
                                        <td>
                                            <g:link class="dbtBtn debOvr debsug" controller="importInvoice" action="changeImportInvoice" params="[originalDebtor: debtorAddressData[9],rejectAddress:'yes']">Reject Address</g:link>
                                        </td>
                                    </tr>
                                </g:each>
                            </table>
                        </div>
                    </g:if>
                    <g:if test="${debtorInactiveList}">
                        <div class="content" style="margin-top: 0px;">
                            <div class="debAlert">Warning! Debtor is inactive, Please change debtor status before process.</div>
                            <table class="display dataTable no-footer" cellspacing="0" width="99%">
                                <thead>
                                    <tr>
                                        <th width="33%">${message(code: 'debtorMaster.debtorName.label', default: 'Debtor Name')}</th>
                                        <th width="33%">${message(code: 'debtorMaster.RejectDebtor.label', default: 'Reject Debtor')}</th>
                                        <th width="33%">${message(code: 'debtorMaster.change.debtor.status.label', default: 'Change Debtor Status')}</th>
                                    </tr>
                                </thead>
                                <g:each var="debtorData" in="${debtorInactiveList}">
                                    <tr>
                                        <td>
                                            ${debtorData[0][8]}
                                        </td>
                                        <td>
                                            <a class="dbtBtn debOvr debrej" href="${contextPath}/ImportInvoice/changeImportInvoice?originalDebtor=${debtorData[0][8]}&reject=yes">Reject All</a>
                                        </td>
                                        <td>
                                            <a class="dbtBtn debOvr debsug" href="${contextPath}/CustomerMaster/list/${debtorData[0][0]}">Change Debtor Status</a>
                                        </td>
                                    </tr>
                                </g:each>
                            </table>
                        </div>
                    </g:if>
                    <g:if test="${debtorSuggestionList}">
                        <div class="content" style="margin-top: 0px;">
                            <div class="debAlert">Warning! Please change new debtor status in import invoices before process.</div>
                            <table class="display dataTable no-footer" cellspacing="0" width="99%">
                                <thead>
                                    <tr>
                                        <th style="padding: 10px" width="180px">${message(code: 'debtorMaster.debtorName.label', default: 'Debtor Name')}</th>
                                        <th style="padding: 10px" width="80px">${message(code: 'debtorMaster.AcceptDebtor.label', default: 'Accept Debtor')}</th>
                                        <th style="padding: 10px" width="80px">${message(code: 'debtorMaster.RejectDebtor.label', default: 'Reject Debtor')}</th>
                                        <th style="padding: 10px" width="300px">${message(code: 'debtorMaster.DebtorSearch.label', default: 'Debtor Search')}</th>
                                        <th style="padding: 10px">${message(code: 'debtorMaster.DebtorSuggestion.label', default: 'Debtor Suggestion')}</th>
                                    </tr>
                                </thead>
                                <g:each status="k" var="debtorMap" in="${debtorSuggestionList}">
                                    <g:set var="debtorName" value="${debtorMap.debtorName}"></g:set>
                                    <g:set var="isNewDebtor" value="${debtorMap.isNewDebtor}"></g:set>
                                    <g:set var="debtorSuggestionList" value="${debtorMap.debtorSuggestion}"></g:set>
                                    <g:set var="debtorId" value="${debtorMap.id}"></g:set>
                                    <tr>
                                        <td>
                                            ${debtorName}
                                        </td>
                                        <td>
                                            <g:link class="dbtBtn debOvr debacc" controller="importInvoice" action="changeImportInvoice" params="[originalDebtor: debtorId,accept:'yes']">Accept All</g:link>
                                        </td>
                                        <td>
                                            <g:link class="dbtBtn debOvr debrej" controller="importInvoice" action="changeImportInvoice" params="[originalDebtor: debtorId,reject:'yes']">Reject All</g:link>
                                        </td>
                                        <td>
                                            <div class="nSearchBox">
                                                <div class="nSearchIcon"></div>
                                                <input class="nSearchBoxText" name="nDebtorSearch${k}" id="nDebtorSearch${k}" data-debtor="${debtorName}" value="" autocomplete="off" />
                                                <div class="nSearchBoxResult">
                                                    <div class="nSearchBoxResultSet">

                                                    </div>
                                                </div>
                                            </div>
                                        </td>
                                        <td>
                                            <g:each var="debtorSuggestion" in="${debtorSuggestionList}">
                                                <g:link class="dbtBtn debOvr debsug" controller="importInvoice" action="changeImportInvoice" params="[originalDebtor: debtorName,toChangeDebtor: debtorSuggestion[8]]">${debtorSuggestion[8]}</g:link>
                                            </g:each>
                                        </td>
                                    </tr>
                                </g:each>
                            </table>
                        </div>
                    </g:if>
                <%}%>
                <div class="content" style="margin-top: 0px;">

                    <table id="importInvoiceListView" class="display" cellspacing="0" width="100%">
                        <thead>
                            <tr>
                                <th style="width: 120px;">${message(code: 'invoiceExpense.gridList.invoiceNumber.label', default: 'Invoice Number')}</th>
                                <th>${message(code: 'debtorMaster.debtorName.label', default: 'Debtor Name')}</th>
                                <th>${message(code: 'customerBankAccount.customer.label', default: 'Customer')}</th>
                                <th style="width: 120px;">${message(code: 'reportIncomeBudget.InvoiceDate.label', default: 'Invoice Date')}</th>
                                <th style="width: 100px;">${message(code: 'report.aging.invoiceAmount.label', default: 'Invoice Amount')}</th>
                                <th>${message(code: 'invoiceExpense.gridList.vat.label', default: 'VAT')}</th>
                                <th style="width: 90px;">${message(code: 'invoiceIncome.action.label', default: 'Action')}</th>
                            </tr>
                        </thead>
                    </table>
                    <table class="itemtableStyle">
                        <tr>
                            <td>
                                <%if(!params.editId){%>
                                    <div class="btnDivStyle btnDivPosition">
                                        <input type="button" class="greenBtn leftBtnPosition" id="Process" name="Process"
                                               value="${message(code: 'bv.ImportExpense.processBtn.label', default: 'Process')}"
                                               style="margin-right: 22px;" onclick="processInvoice();"/>
                                        <input class="orangeBtn" style="height: 24px;" name="ProcessDuplicatedata" id="ProcessDuplicatedata"
                                               value='${message(code: 'bv.importInvoice.duplicateProcess.label', default: 'Duplicates & VAT')}' onclick="processDuplicateInvoice();"/>

                                        <input type="button" class="close closeBtn" name="deleteAll" id="deleteAll"
                                               value="${message(code: 'bv.ImportExpense.deleteAllBtn.label', default: 'Delete All')}"
                                               style="margin-right: 22px;" onclick="deleteAllInvoice();"/>
                                    </div>
                                <%}%>
                            </td>
                        </tr>
                    </table>
                    <div id="processDiv" class="processDivStyle" style="display: none">
                        <label class="processFontStyle">Processing...</label>
                    </div>
                </div>
            </div>

            %{--</div>  <!--boxouter-->--}%
        </div>  <!--id="list-page"-->

    </div>  <!--content-->

</body>


</html>