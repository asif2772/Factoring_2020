<%@ page import="factoring.CoreParamsHelperTagLib;" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="mainCustomerPortal">
    <g:set var="entityName" value="${message(code: 'bv.menu.Customers', default: 'CustomerMaster')}"/>
    <title><g:message code="bv.menu.Customers" args="[entityName]"/></title>
</head>
<% def contextPath = request.getServletContext().getContextPath()%>

<body>

<div>
  <div class="wrapper">
      <div class="bodyHead">
          <h2 class="h2 head2Cls">Mijn overzichten</h2>
          <p class="headPcls">Al uw gegevens in één oogopslag.</p>
      </div>
      <div class="btn-group">
          <g:link url="[controller: 'vendorMaster', action: 'portalList']">
              <button id="customerStlmtBtn" style="border: none;margin-top: 1px; outline: none">
                    <span class="menuItem" id="" style="color: #0c0c0c">Overzicht koopafrekeningen</span>
              </button></g:link>
              <button id="invoListBtn" onclick="showInvoiceBookEntry()" style="border: none; margin-left: 24px;margin-top: 1px;outline: none">
                  <span class="menuItem" style="color: #0c0c0c;">Uw facturen</span>
              </button>
          %{--</g:link>--}%
      </div>
  </div>

    <div id="filterDiv" style="background: #ebebe0; width: 1501px;margin: 0 auto;">
        <div id="create-reportExpenseBookedAllList" class="content scaffold-create" role="main" style="margin: 0 auto;
        width: 1500px; margin-top: 0px;margin-bottom: 0px; background-color: #FFFFFF">
            <table id="expenseBookedListView" class="display" cellspacing="0" width="100%" style="width: 1500px;
            margin: 0 auto;">

                <thead style="background-color: #E2E2E2;">
                <tr>
                    <th style="text-align: left; color: #4F4F4F; padding-left: 159px;">${message(code: 'reportIncomeBudget.expenseInvoiceNumber.label', default: 'Expense Invoice Number')}</th>
                    <th style="text-align: left; color: #4F4F4F">${message(code: 'reportIncomeBudget.customerName.label', default: 'Customer Name')}</th>
                    <th style="text-align: left; color: #4F4F4F">${message(code: 'reportIncomeBudget.transactionDate.label', default: 'Transaction Date')}</th>
                    <th style="text-align: left; color: #4F4F4F">${message(code: 'customerSettlement.factoredInvoiceTotal.label', default: 'Fact.Inv.Amount')}</th>
                    <th style="text-align: left; color: #4F4F4F">${message(code: 'reportIncomeBudget.totalExVAT.label', default: 'Total Amount ex VAT')}</th>
                    <th style="text-align: left; color: #4F4F4F">${message(code: 'reportIncomeBudget.totalVAT.label', default: 'Total VAT')}</th>
                    <th style="text-align: left; color: #4F4F4F;padding-right: 82px;">${message(code: 'reportIncomeBudget.action.label', default: 'Action')}</th>
                </tr>
                </thead>

            </table>

            <table id="invoiceEntryList" class="display" cellspacing="0" width="100%">

                <thead style="background-color: #E2E2E2;">
                <tr>
                    <th style="text-align: left; color: #4F4F4F;padding-left: 159px;">${message(code: 'reportIncomeBudget.incomeInvoiceNumber.label', default: 'Invoice Number')}</th>
                    <th style="text-align: left; color: #4F4F4F">${message(code: 'customerMaster.customerBudgetName.label', default: 'Debtor Name')}</th>
                    <th style="text-align: left; color: #4F4F4F">${message(code: 'reportIncomeBudget.totalExVAT.label', default: 'Total Amount ex VAT')}</th>
                    <th style="text-align: left; color: #4F4F4F">${message(code: 'reportIncomeBudget.totalVAT.label', default: 'Total VAT')}</th>
                    <th style="text-align: left; color: #4F4F4F">${message(code: 'reportIncomeBudget.bookingPeriod.label', default: 'Booking Period')}</th>
                    <th style="text-align: left; color: #4F4F4F">${message(code: 'reportIncomeBudget.transactionDate.label', default: 'Transaction Date')}</th>
                    <th style="text-align: left; color: #4F4F4F;padding-right: 96px">${message(code: 'reportIncomeBudget.paymentRef.label', default: 'Payment Reference')}</th>
                    <th style="text-align: left; color: #4F4F4F;"></th>
                </tr>
                </thead>

            </table>
        </div>
    </div>

</div>



<g:javascript>

    $(document).ready(function() {

         $('#invoiceEntryList').hide();

         getCustomerSettlementList();

          $('#customerStlmtBtn').addClass('activeBtn');
          $('#menuList').addClass('menuOrangeBeam');

    });

    function showInvoiceBookEntry() {

         $('#expenseBookedListView_wrapper').hide();
         $('#invoiceEntryList').show();
         getInvoiceEntryList();
         $('#customerStlmtBtn').removeClass('activeBtn');
         $('#invoListBtn').addClass('activeBtn');

    };

    function editSettlement(editId,customerId,sendFlag){
        var redirectUrl="${contextPath}/invoiceExpense/list?editId=" + editId + "&customerId=" + customerId+ "&sendMailFlag=" + sendFlag;
        window.location.replace(redirectUrl);
    }

    function previewPDF(editId,vendorId){
        var redirectUrl="${contextPath}/invoiceExpense/preview?editId=" + editId + "&customerId=" + vendorId;
        window.location.replace(redirectUrl);
    }

    function directSettlementDownload(editId,vendorId){
        var redirectUrl="${contextPath}/invoiceExpense/exportInvoicePdf?invoiceId=" + editId + "&customerId=" + vendorId;
        window.location.replace(redirectUrl);
    }

    function getCustomerSettlementList() {

        $.fn.dataTable.moment('DD-MM-YYYY');
        $.fn.dataTable.moment('MMM-YYYY');
        $('#expenseBookedListView').dataTable( {

            "ajax":"${contextPath}/reportCustomerSettlement/customerSettlementListForEdit",
            "bProcessing": true,
            "bAutoWidth":false,
            "aLengthMenu": [[10,50,75,100,150,200,300], [10,50,75,100,150,200,300]],
            "iDisplayLength": 10,
             'columnDefs': [{
                  "targets": [0,1,2,3,4,5,6], // your case first column
                  "className": "text-center"
             }],
             language: {
                search: "",
                searchPlaceholder: "Zoek...",
                "paginate": {
                    "previous": "Getoond 1 tot 10",
                    "next": ""
                },
                        "sLengthMenu": "Toon:  _MENU_ "

             },
            "bInfo" : false,

            "fnRowCallback": function( nRow, aData) {

                        $('td', nRow).css('background-color', '#F8F8F8');

                    },

            "columns": [
                { "data": "invoiceNumber" },
                { "data": "vendorName" },
                { "data": "transactionDate" },
                { "data": "factoredInvAmount" },
                { "data": "totalGlAmount" },
                { "data": "totalVat" },
                { "data": "action" }
            ],

            "columnDefs": [
                {className: "classBold", "targets": [ 0 ]}
                ]
        } );
    }

    function getInvoiceEntryList() {

        $.fn.dataTable.moment('DD-MM-YYYY');
        $.fn.dataTable.moment('MMM-YYYY');
        $('#invoiceEntryList').dataTable( {

             "ajax":"${contextPath}/invoiceIncomeReport/showIncomeBooked",
            "bProcessing": true,
            "bAutoWidth":false,
            "aLengthMenu": [[10,50,75,100,150,200,300], [10,50,75,100,150,200,300]],
            "iDisplayLength": 10,
             'columnDefs': [{
                  "targets": [0,1,2,3,4,5,6], // your case first column
                  "className": "text-center"
             }],
             language: {
                search: "",
                searchPlaceholder: "Zoek...",
                "paginate": {
                    "previous": "Getoond 1 tot 10",
                    "next": ""
                },
                 "sLengthMenu": "Toon:  _MENU_ "
             },
            "bDestroy": true,
            "bInfo" : false,

                "fnRowCallback": function( nRow, aData) {

                        $('td', nRow).css('background-color', '#F8F8F8');

                    },

           "columns": [
                    { "data": "invoiceNumber" },
                    { "data": "debtorName" },
                    { "data": "totalGlAmount" },
                    { "data": "totalVat" },
                    { "data": "bookingPeriod" },
                    { "data": "transactionDate" },
                    { "data": "paymentReference" },
                    { "data": "action" }
                ],
                 "columnDefs": [
                    {className: "classBold", "targets": [ 0 ]}
                ]
        } );
    }
</g:javascript>


</body>
</html>