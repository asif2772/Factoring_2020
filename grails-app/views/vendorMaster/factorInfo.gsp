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
          <h2 class="h2 head2Cls">Mijn factoring</h2>
          <p class="headPcls">Al uw gegevens in één oogopslag.</p>
      </div>
  </div>

    <div style="width: 1502px; margin: 0 auto">
        <div id="companyInfoForm" class="companyForm" style="width: 1124px; margin-left: 152px">
            <div class="codeContainerSections" style="margin-top: 20px;margin-left: 0;">
                <div class="codeFieldLeft">
                    <div class="codeContainer">
                        <div class="fieldContainer required">
                            <label for="customerCode">
                                <span class="fieldsName">Klantnummer:</span>
                            </label>
                            <p class="labelCode">CUS-${customerMasterArr[1]}</p>
                        </div>
                    </div>  <!--codeContainer-->
                </div>  <!--codeFieldLeft-->

                <div class="codeFieldMiddle">
                    <div class="codeContainer">
                        <div class="fieldContainer required">
                            <label for="customerName">
                                <span class="fieldsName">Bedrijfsnaam:</span>
                            </label>
                            <p class="labelCode">${customerMasterArr[0]}</p>
                        </div>
                    </div>  <!--codeContainer-->
                </div>  <!--codeFieldMiddle-->

                <div class="codeFieldRight">
                    <div class="codeContainer">
                        <div class="emptyDiv"></div>
                    </div>  <!--rowContainer-->
                </div>  <!--codeFieldRight-->

            </div>

            <br>
            <hr>

            <div class="upperContainer" style="margin-top: 25px;">

                <div class="fieldContainerLeft">
                    <div class="rowContainer">
                        <div class="fieldContainer required fcInputText">
                            <label for="defaultFee">
                                <span class="fieldsName">Standaard factorloon:(%)<b>:</b></span>
                            </label>
                            <input class="fieldDsn" style="border-radius: 0 !important;" type="text" tabindex=""
                                   placeholder="" name="defaultFee"
                                   value="${vendorFactoringInstance?.defaultFee}" id="defaultFee" readonly/>
                        </div>
                    </div>
                    <div class="rowContainer">
                        <div class="fieldContainer required fcInputText">
                            <label for="outPayment">
                                <span class="fieldsName">Uitbetaling:(%)<b>:</b></span>
                            </label>
                            <input class="fieldDsn" style="border-radius: 0 !important;" type="text" tabindex=""
                                   placeholder="" name="outPayment"
                                   value="${vendorFactoringInstance?.outpayment}" id="outPayment" readonly/>
                        </div>
                    </div>
                </div>

                <!--fieldContainerLeft-->

                <div class="fieldContainerRight">
                    <div class="rowContainer">
                        <div class="fieldContainer required fcInputText">
                            <label for="acceptanceFee">
                                <span class="fieldsName">Acceptatiekosten per debiteur<b>:</b></span>
                            </label>
                            <input class="fieldDsn" style="border-radius: 0 !important;" type="text" tabindex="6"
                                   placeholder="" name="acceptanceFee"
                                   value="${vendorFactoringInstance?.acceptenceFee}" id="acceptanceFee" readonly/>
                        </div>
                    </div>
                    <div class="rowContainer">
                        <div class="fieldContainer required fcInputText">
                            <label for="subscriptionAmount">
                                <span class="fieldsName">Abonnementskosten<b>:</b></span>
                            </label>
                            <input class="fieldDsn" style="border-radius: 0 !important;" type="text" tabindex="6"
                                   placeholder="" name="subscriptionAmount"
                                   value="${vendorFactoringInstance?.subcriptionAmount}" id="subscriptionAmount" readonly/>
                        </div>
                    </div>
                </div>

                <!--fieldContainerRight-->

                <div class="fieldContainerRight">
                    <div class="rowContainer">
                        <div class="fieldContainer required fcInputText">
                            <label for="acceptanceFee">
                                <span class="fieldsName">Administratiekosten per factuur<b>:</b></span>
                            </label>
                            <input class="fieldDsn" style="border-radius: 0 !important;" type="text" tabindex="6"
                                   placeholder="" name="adminCost"
                                   value="${vendorFactoringInstance?.adminCost}" id="adminCost" readonly/>
                        </div>

                    </div>
                    <div class="rowContainer">
                        <div class="fieldContainer required fcInputText">
                            <label for="subcriptionDate">
                                <span class="fieldsName">Abonnementsdatum<b>:</b></span>
                            </label>
                            <% if(vendorFactoringInstance?.subcriptionDate == "") { %>
                            <bv:datePicker name="subcriptionDate" value="${new Date()}"/>
                            <% }else { %>
                            <bv:datePicker name="subcriptionDate" value="${vendorFactoringInstance?.subcriptionDate}"/>
                            <% } %>
                        </div>
                    </div>
                </div>

                <!--fieldContainerRight-->

            </div>
        </div>
    </div>

    <div class="wrapper" id="">

    </div>
</div>
<g:javascript>

    $(document).ready(function() {

         $('#invoiceEntryList').hide();
         getCustomerSettlementList();
         $("input[name='subcriptionDate']").addClass("fieldDsn");
         $("input[name='subcriptionDate']").css({"border-radius": "0"});
         $("input[name='subcriptionDate']").attr("disabled", true);
         $('#menuFactor').addClass('menuOrangeBeam');
    });

    function showInvoiceBookEntry() {

         $('#expenseBookedListView_wrapper').hide();
         $('#invoiceEntryList').show();
         getInvoiceEntryList();

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
                }
             },
            "bInfo" : false,

            "columns": [
                { "data": "invoiceNumber" },
                { "data": "vendorName" },
                { "data": "transactionDate" },
                { "data": "factoredInvAmount" },
                { "data": "totalGlAmount" },
                { "data": "totalVat" },
                { "data": "action" }
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
                }
             },
            "bDestroy": true,
            "bInfo" : false,

           "columns": [
                    { "data": "invoiceNumber" },
                    { "data": "debtorName" },
                    { "data": "totalGlAmount" },
                    { "data": "totalVat" },
                    { "data": "bookingPeriod" },
                    { "data": "transactionDate" },
                    { "data": "paymentReference" },
                    { "data": "action" }
                ]
        } );
    }
</g:javascript>





</body>
</html>