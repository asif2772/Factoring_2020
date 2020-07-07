<%@ page import="factoring.CoreParamsHelperTagLib; factoring.InvoiceIncome;" %>
<!doctype html>
<html>

<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'invoiceIncome.label', default: 'InvoiceIncome')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
    %{--<title><g:message code="default.name.label" args="[entityName]"/></title>--}%
</head>

<body>
<%  def contextPath = request.getServletContext().getContextPath()  %>
<g:javascript>
    var isClicked = false;
    var invoiceSavedCustomerId = ${invoiceSavedCustomerId};
    var datePickerTransDate = "${datePickerTransDate}";
    function showSaveMessage(){
        setTimeout(function(){
            $("#msgCusDiv").hide('blind', {}, 1000);
        }, 9000);
    }
    function showfiscalYearMessage(){
        $("#dateFiscalYear").show(500);
        setTimeout(function(){
            $("#dateFiscalYear").hide('blind', {}, 1000);
        }, 9000);
    }
    function isAlreadySubmitted(btnFrom){
        if(btnFrom==2){
            $("#hiddenData").html($("#resultDetailsOfIncomeInvoice").html());
            $("#hiddenData #customerId").val($("#resultDetailsOfIncomeInvoice #customerId").val());
            $("#hiddenData #debtorId").val($("#resultDetailsOfIncomeInvoice #debtorId").val());
            $("#hiddenData #documentChecked").val($("#resultDetailsOfIncomeInvoice #documentChecked").val());
        }
        if(isClicked){
            return false;
        }
        else{
            isClicked = true;
            if(btnFrom == 1){
                $("#addInvSpin").show();
            }
            else{
                $("#saveInvSpin").show();
            }
            return true
        }
    }

    function updateDataAndMessage(data) {
        isClicked = false;
        if(data.length>0){
            $("#resultDetailsOfIncomeInvoice").html(data);
        }else {
            showfiscalYearMessage();
        }
        showSaveMessage();
        $("#saveInvSpin").hide();
    }
    function enableSubmit(btnTo){
        isClicked = false;
        if(btnTo == 1){
            $("#addInvSpin").hide();
        }
        else{
            if(data.length>0){
                $("#resultDetailsOfIncomeInvoice").html(data);
            }
            else{

            }
            showSaveMessage();
            $("#saveInvSpin").hide();
        }
    }
    function startTimer(){
        setTimeout(function () {$("#msgdivAnother").hide('blind', {}, 1000);
            //alert("setTimeout");
        }, 5000);
    }

    function saveDataToSession(){
        var customerId = $('#customerId').val();
        var paymentRef = $('#paymentRef').val();
        var editId = '${params.editId}';
        //alert("customerId " + customerId +" PaymentRef " + paymentRef);

        $.ajax(
           {type:'POST',
            data:'customerId=' + customerId +'&paymentRef=' + paymentRef +'&editId=' + editId,
            url:'${contextPath}/invoiceIncome/saveDataToSession',
            success:function(data,textStatus){
                if(data != ""){
                    $(msgdivAnother).show();
                    $(msgdivAnother).html(data);
                    startTimer();
                    document.getElementById("paymentRef").value = "";
                }
                //alert("data " + data + "status "+ textStatus);
            },
            error: function(XMLHttpRequest,textStatus,errorThrown) {
                //alert("textStatus "+textStatus);
            }
        });
    }

     $(document).ready(function() {
        if(invoiceSavedCustomerId!='0'){
            $("#customerId").val(invoiceSavedCustomerId).change();
            $("#documentChecked").val('Yes');
        }
        if(datePickerTransDate!='0')
            $("#date_picker_transDate").val(datePickerTransDate);
        var editId = "${params.editId}";
//        alert(editId);
        if(editId > 0){
            generateDebtorDropList();
            selectVATDropDown();
        }

         $(msgdivAnother).hide();
         $.fn.dataTable.moment('DD-MM-YYYY');
         $.fn.dataTable.moment('MMM-YYYY');
         $('#invoiceIncomeListView').dataTable( {

             "ajax":"${contextPath}/invoiceIncome/showInvoiceIncList?customerId=${params.customerId}",
             "bProcessing": true,
             "bAutoWidth":false,
             "aLengthMenu": [[10,50, 75,100,150,200,300], [10,50, 75,100,150,200,300]],
             "iDisplayLength": 50,
             "aaSorting": [[0,'desc']],
             "columns": [
                    { "data": "invoiceNumber" },
                    { "data": "bookingPeriod" },
                    { "data": "debtorName" },
                    { "data": "invoiceDate" },
                    { "data": "paymentReference" },
                    { "data": "totalGlAmount" },
                    { "data": "totalVat" },
                    { "data": "action" }
                ]

    } );
    } );

    function changeBooking(incomeInvoiceId,customerId,debtorId){
        var redirectUrl="list?editId="+incomeInvoiceId+"&customerId="+customerId+"&debtorId="+debtorId;
        window.location.replace(redirectUrl);
    }

    $("#customerId").change(function(){
        generateDebtorDropList();
        showCustomerRemainingInsuredAmount();
    });

    function generateDebtorDropList(){

      $("#debtorActiveInactive").hide();
      $('#bookInvoiceDetailsAddButton').show();

        $.ajax({
            type:'POST',
            dataType: 'json',
            url:'${contextPath}/invoiceIncome/generateDebtorNameList',
            data:{
                customerId: $('#customerId').val(),
                debtorId:'${params.debtorId}'
            },
            success: function(ajax)
            {
                $('#debtorList').html(ajax.result);
            }
        });
    }

    function selectVATDropDown(){
        $.ajax({
                type:'POST',
                dataType: 'json',
                url:'${contextPath}/invoiceIncome/selectVATFromCustomerId',
                //url:'${contextPath}/invoiceExpense/selectVATFromVendorId',
                data:{
                    customerId: $('#customerId').val()
                    //debtorId: $('#debtorId').val()
                },
                success: function(data)
                {
                    var vatRate = parseFloat(data);
                    var varRateFormat = number_format(vatRate, 1, '.', '${session.companyInfo?.thousandSeperator[0]}');
                    //alert(varRateFormat);
                    $("#vatRate option[value='" + varRateFormat + "']").attr('selected', 'selected');

                }
            });
    }

    function selectDebtorTerms(){

        var debtStatus = $('#debtorId option:selected').attr('debtStatus');

        if(debtStatus === '2'){
            $("#debtorActiveInactive").show();
            $('#bookInvoiceDetailsAddButton').hide();
            $('#saveIncomeInvoiceButton').hide();
        }
        else {
            $("#debtorActiveInactive").hide();
            $('#bookInvoiceDetailsAddButton').show();
            $('#saveIncomeInvoiceButton').show();
        }

        $.ajax({
            type:'POST',
            dataType: 'json',
            url:'${contextPath}/invoiceIncome/getDebtorStatus',
            data:{
                debtorId: $('#debtorId').val()
            },
            success: function(data){
                if(data==2){
                    $("#cusDebInactiveMsg").show();
                    $("#create").attr('disabled', true);
                }
                else{
                    $("#cusDebInactiveMsg").hide();
                    $("#create").attr('disabled', false);
                }
            }
        });
        $.ajax({
                type:'GET',
                url:'${contextPath}/invoiceIncome/selectDebtorTermsDropDown',
                data:{
                    customerId: $('#customerId').val(),
                    debtorId: $('#debtorId').val()
                },
                success: function(data)
                {
                    //alert(data)
                    var dataArr = data.split("::");
                    var termsId = parseInt(dataArr[0]);
                    //alert(termsId);

                    $("#termsId option[value='" + termsId + "']").attr('selected', 'selected');

                    var noOfDays = parseInt(dataArr[1]);
                    //alert(noOfDays);
                    adjustDueDate(noOfDays);
                }
            });

            showDebtorRemainingInsuredAmount();
            //selectVATDropDown();
    }

    function showDebtorRemainingInsuredAmount(){
        $.ajax({
                type:'GET',
                url:'${contextPath}/invoiceIncome/showDebtorRemainingInsuredAmount',
                data:{
                    debtorId: $('#debtorId').val()
                },
                success: function(data)
                {
                    //alert(data)
                    $('#insuadAmountDebtor').html(data);
                }
            });
    }

    function showCustomerRemainingInsuredAmount(){
        $.ajax({
                type:'GET',
                url:'${contextPath}/invoiceIncome/showCustomerRemainingInsuredAmount',
                data:{
                    customerId: $('#customerId').val()
                },
                success: function(data)
                {
                    //alert(data)
                    $('#insuadAmountCustomer').html(data);
                }
            });
    }

    function changeDueDateFromTerms(){
        $.ajax({
                type:'POST',
                url:'${contextPath}/invoiceIncome/changeDueDate',
                data:{
                    termsId: $('#termsId').val()
                },
                success: function(data)
                {
                    var noOfDays = parseInt(data);
                    adjustDueDate(noOfDays);
                }
            });
    }

    $("#date_picker_transDate").change(function(){
        changeDueDateFromTerms();
    });

    $("#termsId").change(function(){
        changeDueDateFromTerms();
    });

</g:javascript>

<div id="list-page-body-inner" class="content white-background">
<div id="list-page">
<div class="budgetHeader">
    <div class="headerMainLeft">
        <label class="">
            <% if (params.editId) { %>
                <g:message code="bv.updateIncomeInvoice.label"/>
            <%  } else { %>
                <g:message code="default.create.label" args="[entityName]"/>
            <% } %>
        </label>
    </div>

     <div class="headerMainMiddleTwo">
        ${params.budgetCustomerId ? new CoreParamsHelperTagLib().getCustomerName(Integer.parseInt(params.budgetCustomerId)) : ""}
    </div>
    <div class="headerMainRightFiscalYr">
        <a href=" ${contextPath}/fiscalYear/list">${bookingPeriodFormat}</a>
    </div>
</div>

<div id="" class="explanationDiv">
    <h4 class="slideToggler2 slideSign2">
        <label class="explanationLabel">
            <span class="explanationArrow"></span>
            <p><g:message code="bv.dashboard.explanation.label" default="Explanation" /></p>
        </label>
    </h4>

<div class="slideContent2 explanationSlideContainer">
    <ul class="explanationList">
        <li><a href=" ${contextPath}/explanation/invoicesAndReceipt"><g:message code="explanation.invoicesAndReceipt.label" /></a></li>
        <li><a href=" ${contextPath}/explanation/changeOrDelete"><g:message code="explanation.changeOrDelete.label" /></a></li>
    </ul>
</div>
</div>

<div id="resultDetailsOfIncomeInvoice" class="sh">
    <div class="boxouter boxouter-modified">
        <g:formRemote before="if(isAlreadySubmitted(1)){" after="}" onerror="enableSubmit(1)" onSuccess="enableSubmit(1)" name="invoiceIncomeDetailsForm" url="[controller: 'InvoiceIncome', action: 'save']" update="updateItemList" style="margin-top: 0">
            <fieldset class="form" style="margin-bottom: 10px;">
                <g:render template="form"/>
            </fieldset>
        </g:formRemote>

    </div>

    %{--//////// Search Code ///////////--}%
    <div class="scaffold-create searchField" role="main">
        <label class="searchHeadLineLabel">
            <g:message code="budgetItemIncomeDetails.bookedInvoiceList.label" default="Booked Invoice List" />
        </label>

    </div>

    %{--Flexi Grid--}%
    <div class="boxouter" id="incomeInvoiceDiv">
        <div class="content" style=" margin-top: 0;">
            %{--<table id="flex1" style="display: none"></table>--}%
            <table id="invoiceIncomeListView" class="display" cellspacing="0" width="100%">
                <thead>
                <tr>
                    <th>${message(code: 'invoiceExpense.gridList.invoiceNumber.label', default: 'Invoice Number')}</th>
                    <th>${message(code: 'invoiceIncomeInstance.bookingPeriod.label', default: 'Booking Period')}</th>
                    <th>${message(code: 'debtorMaster.debtorName.label', default: 'Debtor Name')}</th>
                    <th>${message(code: 'bv.invoiceIncome.transDate.label', default: 'Invoice Date')}</th>
                    <th>${message(code: 'reportIncomeBudget.paymentRef.label', default: 'Payment Ref')}</th>
                    <th style="width:20px;">${message(code: 'invoiceExpense.gridList.totalVAT.label', default: 'Total Amount Inc VAT')}</th>
                    <th>${message(code: 'invoiceExpense.gridList.vat.label', default: 'VAT')}</th>
                    <th style="width:40px;">${message(code: 'invoiceIncome.action.label', default: 'Action')}</th>

                </tr>
                </thead>


            </table>
        </div>
    </div>
</div>  <!--resultDetailsOfIncomeInvoice-->

</div>
</div>
%{--########################### END ##################--}%
<g:javascript>

        function calPrice(vatRate, newval){
            newval = newval.replace(",", ".")
            if(allnumeric(newval))
            {   totalPrice = (vatRate * newval)/100;
                document.getElementById("vatAmount").value = number_format(totalPrice, 2, '.', '${session.companyInfo?.thousandSeperator[0]}');
            }
        }

        function getCalcVATFromAmount()
        {
           var vatRate = 21.0;
           var amount = document.getElementById("unitPrice").value;
           var totalVat = (vatRate * amount)/100;

           return totalVat;
        }

        function calPriceByVAT(vatRate) {
           //alert("test"+editMode);
           var amount = document.getElementById("unitPrice").value;
           amount = amount.replace(",",".")
           allnumeric(amount);
           var totalVat = (vatRate * amount)/100;

           document.getElementById("vatAmount").value = number_format(totalVat, 2, '.', '${session.companyInfo?.thousandSeperator[0]}');
        }

        function setEmptyField() {
           var priceVal = document.getElementById("unitPrice").value
           if(priceVal == 0.00)
           {
               document.getElementById("unitPrice").value = "";
               //alert(val);
               document.getElementById("vatAmount").innerHTML = "0.00";
           }
        }

        function calGrandTotal(oldval, newval,totalAmount) {
            var difference=Math.abs((oldval-newval));
            var grandAmount=parseFloat(totalAmount)+parseFloat(newval)

            if(difference>0.5){
                //alert("${message(code: 'bv.invoiceVatDifference.label', default: 'Difference more than +/- 0.5 not acceptable')}");
                document.getElementById("tempVatAmount").value = number_format(oldval, 2, '.', '${session.companyInfo?.thousandSeperator[0]}');
            }else{
                document.getElementById("tempVatAmount").value = number_format(newval, 2, '.', '${session.companyInfo?.thousandSeperator[0]}');
                document.getElementById("GrandTotal").innerHTML = number_format(grandAmount, 2, '.', '${session.companyInfo?.thousandSeperator[0]}');
            }
        }

       function number_format(number, decimals, dec_point, thousands_sep) {
            // * example 1: number_format(1234.5678, 2, '.', '');
            // * returns 1: 1234.57
            var n = number, c = isNaN(decimals = Math.abs(decimals)) ? 2 : decimals;
            var d = dec_point == undefined ? "," : dec_point;
            var t = thousands_sep == undefined ? "." : thousands_sep, s = n < 0 ? "-" : "";
            var i = parseInt(n = Math.abs(+n || 0).toFixed(c)) + "", j = (j = i.length) > 3 ? j % 3 : 0;
            return s + (j ? i.substr(0, j) + t : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + t) + (c ? d + Math.abs(n - i).toFixed(c).slice(2) : "");
        }

       function allnumeric(value) {
            var repval=value.replace(/,/g,'');

              if(!isNaN(repval)){
                return true;
              } else if(repval =="-"){

              } else {
                document.getElementById("unitPrice").value = "";
                document.getElementById("unitPrice").placeholder = "";
                document.getElementById("vatAmount").value = "0.00";
            }
            return false;
       }

        /////////Place holder Income Invoice 'unit price' field ///////////
        function ClearPlaceHolder (input) {
            if (input.value == input.defaultValue) {
                input.value = "";
            }
        }

        function SetPlaceHolder (input) {
            if (input.value == "") {
                input.value = input.defaultValue;
              }
         }
        /////////// end ............
        function adjustDueDate(numberOfDaysToAdd) {
            var datevalue = $("#date_picker_transDate").val();
            var spldate = datevalue.split('-');
            var joindate = new Date(
                parseInt(spldate[2], 10),
                parseInt(spldate[1], 10) - 1,
                parseInt(spldate[0], 10)
            );

            joindate.setDate(joindate.getDate() + numberOfDaysToAdd);

            var newday = joindate.getDate()>9?joindate.getDate():("0"+joindate.getDate());
            var newMonth = joindate.getMonth()+ 1;
            newMonth = newMonth>9?newMonth:("0"+newMonth);
            var newYear = joindate.getFullYear();

            var formateddate = newday+"-"+newMonth+"-"+newYear;
            if(formateddate=="NaN-NaN-NaN"){
                $("#date_picker_dueDate").val('');
            }  else{
                $("#date_picker_dueDate").val(formateddate);
            }
         }

</g:javascript>
</body>
</html>

