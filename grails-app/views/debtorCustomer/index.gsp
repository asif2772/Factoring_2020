<%@ page import="factoring.CoreParamsHelperTagLib;" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'bv.menu.DebtorCustomer', default: 'Accept Debtors')}"/>
    <title><g:message code="bv.menu.DebtorCustomer" args="[entityName]"/></title>
</head>

<body>

<a href="#list-customerMaster" class="skip" tabindex="-1">
    <g:message code="default.link.skip.label" default="Skip to content&hellip;"/>
</a>

<%
    def contextPath = request.getServletContext().getContextPath()
%>
<g:javascript>
    function startTimer(){
        setTimeout(function () {$("#msgdivAnother").hide('blind', {}, 1000);
            //alert("setTimeout");
        }, 5000);
    }

  $(document).ready(function() {
    var editId = "${params.id}";

    $(msgdivAnother).hide();
    $('#customerListView').dataTable( {

   "ajax":"${contextPath}/debtorCustomer/showDataList",
     "bProcessing": true,
     "bAutoWidth":false,

     "columns": [
            { "data": "debCusNumber" },
            { "data": "customerName" },
            { "data": "debtorName" },
            { "data": "acceptenceFee" },
            { "data": "debitorTerms" },
            { "data": "acceptenceDate" },
            { "data": "acceptedBy" },
            { "data": "action" }
        ]

    } );

    if(editId == 0){
        var termsID = $('#debtorTermsId').val();
        changeReminderData(termsID);
    }

    } );

    function editData(editId){
        var redirectUrl="${contextPath}/debtorCustomer/index?id="+editId;
        window.location.replace(redirectUrl);
    }

    $("#debtorTermsId").change(function(){
        var termsID = $(this).val();
        changeReminderData(termsID);
    });

    function changeReminderData(termsID){

        $.ajax({
        url:"${g.createLink(controller: 'PaymentTerms', action: 'changeReminderData')}",
        type:'POST',
        data:'termsId=' + termsID,
            success:function (data) {
               var dataArr = data.split("::");

               $("#firstReminder").val(dataArr[0]);
               $("#secondReminder").val(dataArr[1]);
               $("#thirdReminder").val(dataArr[2]);
               $("#finalReminder").val(dataArr[3]);
            }
        });
    }

    function copyDefaultValueForCustomer(customerId){
        $.ajax({
        url:"${g.createLink(controller: 'DebtorCustomer', action: 'copyDefaultValueForCustomer')}",
        type:'POST',
        data:'customerId=' + customerId,
            success:function (data) {
               var dataArr = data.split("::");

               $("#defaultFee").val(dataArr[0]);
               $("#outpayment").val(dataArr[1]);
               $("#adminCost").val(dataArr[2]);
               $("#acceptanceFee").val(dataArr[3]);
            }
        });
    }

    function checkDebtorCustomerCombination(){
        $.ajax({
                type:'POST',
                url:'${contextPath}/debtorCustomer/checkDebtorCustomerCombination',
                data:{
                    customerId: $('#customerId').val(),
                    debtorId: $('#debtorId').val()
                },
                success: function(data,textStatus)
                {
                    if(data != ""){
                        //alert(data)
                        $(msgdivAnother).show();
                        $(msgdivAnother).html(data);
                        startTimer();
                    }
                },
                error: function(XMLHttpRequest,textStatus,errorThrown) {
                    alert("textStatus "+textStatus);
                }
            });
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

    function setZero(fieldValue){
        if(!fieldValue){
            $('#acceptanceFee').val(0);
        }
    }

</g:javascript>

<% if (params.id) { %>
    <g:include view="debtorCustomer/edit.gsp"/>
<% } else { %>
    <g:include view="debtorCustomer/create.gsp"/>
<% } %>

<div id="list-page-body-inner" class="content">
    <div id="list-page">
        <div class="list-boxtitle">
            <div class="list-boxtitle-left">
                <g:message code="debtorCustomer.list.label" args="[entityName]"/>
            </div>
        </div>

        <table id="customerListView" class="display" cellspacing="0" width="100%">
            <thead>
            <tr>
                <th>${message(code: 'debtorCustomer.bankAccountName.label', default: 'Debtor#Customer')}</th>
                <th>${message(code: 'debtorCustomer.customerName.label', default: 'Customer Name')}</th>
                <th>${message(code: 'debtorCustomer.debtorName.label', default: 'Debtor Name')}</th>
                <th>${message(code: 'debtorCustomer.factorFee.label', default: 'Factor Fee')}</th>
                <th>${message(code: 'debtorCustomer.debitorTerms.label', default: 'Debitor Terms')}</th>
                <th>${message(code: 'debtorCustomer.acceptenceDate.label', default: 'Acceptence Date')}</th>
                <th>${message(code: 'debtorCustomer.acceptedBy.label', default: 'Accepted By')}</th>
                <th>${message(code: 'customerBankAccount.action.label', default: 'Action')}</th>
            </tr>
            </thead>
        </table>

    </div>
</div>
</body>
</html>