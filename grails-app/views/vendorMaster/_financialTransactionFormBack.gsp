<%@ page import="factoring.CoreParamsHelperTagLib; java.text.SimpleDateFormat;" %>

<script type="text/javascript">
    $(function () {
        setTimeout(function () {
            $("#msgdiv").hide('blind', {}, 1000);
        }, 3000)
    })

    $(document).ready(function () {
        // restrict the navigation by setting min and max properties.
        $("#transDate").datepicker({
            dateFormat: 'dd-mm-yy',
            showOn: "button"}).datepicker("setDate", new Date());
    });
</script>
<% if (executeMessage) { %>
<div id="msgdiv" style="text-align: center;height:40px;margin-top:15px">
    <b style="font-size:17px;color:${executeMessageColor};"><g:message code="${executeMessageCode}" default="${executeMessage}"/></b>
</div>
<% } %>
<div class="main-container" style="margin-bottom: 24px;">
    <div class="codeContainerSections">
        %{--<div class="fcCodeNumber">--}%
        <div class="codeFieldLeft">
            <div class="codeContainer">
                <div class="fieldContainer required">
                    <label for="vendorCode">
                        <g:message code="vendorMaster.vendorCode.label" default="Vendor Code"/>
                        <span class="required-indicator">*</span>
                    </label>
                    <p class="labelCode">${vendorPrefix ? vendorPrefix : venPrefix}${"-"}${vendorMasterInstance?.vendor_code ? vendorMasterInstance?.vendor_code : venCode}</p>

                </div>
            </div>  <!--codeContainer-->
        </div>  <!--fieldContainerLeft-->

        <div class="codeFieldMiddle">
            <div class="codeContainer">
                <div class="fieldContainer required">
                    <label for="vendorName"">
                <g:message code="bv.reconcilationUnknownBankNewVendorVendorName" default="Vendor Name"/>
                    <span class="required-indicator">*</span>
                </label>
                    <p class="labelCode">${vendorMasterInstance?.vendor_name ? vendorMasterInstance?.vendor_name : venName}</p>
                </div>
            </div>  <!--codeContainer-->
        </div>  <!--codeFieldMiddle-->

        <div class="codeFieldRight">
            <div class="codeContainer">
                <div class="fieldContainer required">
                </div>
            </div>  <!--codeContainer-->
        </div>  <!--codeFieldRight-->
    </div>  <!--codeContainerSections-->


    <div class="singleContainer" style="">

    %{--1st table--}%
        <div class="fieldContainer" style="display: block; height: 36px;">
            <p style="font-weight: bold;font-size: 14px;margin-bottom: 7px;">
                <g:message code="customerMaster.financeTranTableHeader1.label" default="Invoices Including Paid Amount"/>
            </p>
            %{--<div class="navigationbtn" style="float: right;21px; margin-top:8px; ">--}%
            <p class="ashBtnDiv" style="float: right; margin-top: -23px; margin-right: 22px;">
                <g:link class="ashBtn" style="line-height: 22px;" action="exportExcelofBankTransaction"  id="${vendorMasterInstance?.id?vendorMasterInstance?.id:venId}" ><g:message code="bv.vendorMaster.excelReportButton.lable" default="Excel Report" /></g:link>

            </p>
            %{--</div>--}%
        </div>
        <div id="tabPageLoadData6" class="fieldContainer">
            <div class="spinLoader fieldContainer">
                <img src="${resource(dir:'images',file:'spinner.gif')}" alt="${message(code:'spinner.alt.Loading',default:'Loading...')}" />
            </div>
        </div>

     <g:formRemote name="writeOffForm" url="[controller:'vendorMaster',action:'writeOffInvoicePaymentVendor' ]">
     <%

//            if(session.resultVendorInvInfo ){
            def rowNo = 0;
           def multipleElements=1;
           def contextPath = request.getServletContext().getContextPath()
    %>


        <div class="InvoiceAmountTabl">
            <div id="tableHeadForQuickEntry" class="financeTranTablHead">
                %{--<hr style="color: #fdfcfc;  margin-bottom: -1px;">--}%
                <table class="createReceipt createReceiptFinanceHead" width="100%" style="border: 1px solid #fdfcfc;">
                    <thead>
                    %{--1st table TABLE--}%
                    <tr>
                        <th width="197" style="padding-left: 20px;"><g:message code="invoiceExpense.gridList.invoiceNumber.label"  default="Invoice Number"/></th>
                        <th width="163" style="padding-left: 0px;"><g:message code="customerMaster.paymentRefference.label" default="Payment Refference"/></th>
                        <th width="120" style="padding-left: 0px;"><g:message code="bv.autoReconciliationOpenInvoicesTransactionDate.label" default="Transaction Date"/></th>
                        <th width="129" style="text-align: center"><g:message code="DashboardListIncome.BookingPeriod.list.label" default="Booking Period"/></th>
                        <th width="130" style="text-align: right"><g:message code="report.aging.paidAmount.label" default="Paid Amount"/></th>
                        <th width="130" style="text-align: right"><g:message code="report.aging.dueAmount.label" default="Due Amount"/></th>
                        <th width="" style="margin-right: 50px;text-align: center;width: 96px;"><g:message code="report.aging.paidStatus.label" default="Paid Status"/></th>
                    </tr>
                    </thead>
                </table>
            </div>
                %{--<hr style="border-top: 1px solid #8e8e8e;">--}%

            %{--1st--}%
          <%if(invoiceVendorDetailsInstance !=[] ){%>
            <table style="border-bottom: 1px solid #a5a5a5;">
                <tbody>
                    <g:each in="${invoiceVendorDetailsInstance}" status="i" var="invoiceVendorDetails">
                        <%
                            BigDecimal showInvoiceAmount = new BigDecimal(invoiceVendorDetails[4])
                            BigDecimal showPaidAmount = new BigDecimal(invoiceVendorDetails[5])
                            BigDecimal showDueAmount = new BigDecimal(invoiceVendorDetails[6])

                            def notPaidFlag = false;
                            Double dInvAmount = showInvoiceAmount.toDouble();
                            Double dPaidAmount = showPaidAmount.toDouble();
                            Double dDueAmount = showDueAmount.toDouble();
                            String strPaidStatus = "Not Paid";
                            if(dDueAmount == 0.00)
                            {
                                strPaidStatus = "Paid";
                            }
                            else if(dDueAmount < 0.00 && dInvAmount < dPaidAmount)
                            {
                                strPaidStatus = "Over Paid";
                                notPaidFlag = true
                            }
                            else{
                                notPaidFlag = true;
                            }

                            def bookingMonth = new CoreParamsHelperTagLib().monthNameShow(Integer.parseInt(invoiceVendorDetails[3]))
                        %>
                    %{--1st--}%
                        <tr style="background-color: #f9f9f9;">
                            <td width="185" style="padding-left: 20px;">${invoiceVendorDetails[0]}</td>
                            <td width="155" style="padding-left: 1px;">${invoiceVendorDetails[1]}</td>
                            <td width="120" style="padding-left: 7px;"><g:if test="${!strPaidStatus.equals('Not Paid')}">${new SimpleDateFormat("dd MMM yyyy").format(invoiceVendorDetails[2])}</g:if></td>
                            <td width="120" style="text-align: center">${bookingMonth}</td>
                            <td width="130" style="text-align: right">${showPaidAmount.setScale(2, BigDecimal.ROUND_HALF_UP)}</td>
                            <td width="130" style="text-align: right">${showDueAmount.setScale(2, BigDecimal.ROUND_HALF_UP)}</td>
                            <% if(notPaidFlag){ %>
                                <td width="20" style="text-align: right"><input type="checkbox" name = "invBankStatement" value="${rowNo}"/></td>
                            <% }else{ %>
                            <td width="20" style="text-align: right"></td>
                            <%}%>
                            <td width="" style="text-align: center;width: 96px;">${strPaidStatus}</td>
                        </tr>
                        <%rowNo++;%>
                    </g:each>
               </tbody>
            </table>

         <div class="fieldContainer fieldContainerRightOffDiv" style="display: block !important;height: 70px;">
             <div class="fieldContainerRightOffDiv wrightOffDateDiv">

                 <div class="fieldContainer onStyle">
                     <g:message code="vendorCustomerMasterOn.label" default="Booking Date On"/>
                 </div>

                 <div  class="required fieldContainer manualEntryDatePick datePicVenCus">
                     <input type="text" id="transDate" name="transDate">
                 </div>

                 <div class="fieldContainerRightOffDiv writeOff2Button">
                     <div class="fieldContainer updateLinkBtn ashBtnDiv writeOffBtn">
                         <g:submitToRemote class="ashBtn" action="writeOffInvoicePaymentVendor" update="tabpage_6"  value="${message(code: 'customerMaster.financeTranTab.writeOff.label', default: 'Write Off')}"/>
                     </div>

                    <div class="fieldContainer updateLinkBtn ashBtnDiv privatePaidBtn">
                        <g:submitToRemote class="ashBtn" action="privatePaid" update="tabpage_6" value="${message(code: 'customerMaster.financeTranTab.privatePaid.label', default: 'Private Paid')}"/>
                    </div>
                 </div>

               <%}else{%>
               <div class="fieldContainer" style="text-align:center;margin-top: 11px;font-weight:bold;font-size: 14px; ">
                    <g:message code="vendorMaster.financialTab.paidInvoice.label"/>
                </div>
               <%}%>

             </div>  <!--fieldContainerRightOffDiv-->
         </div>  <!--fieldContainer-->

     </div>  <!--InvoiceAmountTabl-->
    <%
//      }
    %>
      <g:hiddenField name="id" value="${vendorMasterInstance?.id?vendorMasterInstance?.id:venId}" />
      <g:hiddenField name="vendorPrefix" value="${vendorPrefix?vendorPrefix:venPrefix}" />
      <g:hiddenField name="vendorCode" value="${vendorMasterInstance?.vendor_code?vendorMasterInstance?.vendor_code:venCode}" />
      <g:hiddenField name="vendorName" value="${vendorMasterInstance?.vendor_name?vendorMasterInstance?.vendor_name:venName}" />
    </g:formRemote>
%{--2nd Table--}%
    <p style="font-weight: bold;font-size: 14px;margin-bottom: 7px; margin-top: 30px;">
        <g:message code="customerMaster.financeTranTableHeader2.label" default="Bank Transaction That were Reconciliated for These Invoice"/>
    </p>
    <div class="InvoiceAmountTabl">
        <div id="tableHeadForQuickEntry" class="financeTranTablHead">
            <table class="createReceipt createReceiptFinanceHead" width="100%" style="border: 1px solid #fdfcfc;">
                <thead>
                <tr>
                    <th width="132" style="padding-left: 20px;"><g:message code="bv.undoReconciliation.BankPaymentId.label" default="Bank payment ID "/></th>
                    <th width="150"><g:message code="bv.menu.CompanyBankAccount" default="Company bank account "/></th>
                    <th width="144"><g:message code="invoiceExpense.gridList.invoiceNumber.label"  default="Invoice Number"/></th>
                    <th width="354"><g:message code="customerMaster.paymentDescription.label" default="Payment Description "/></th>
                    <th width="55" style="text-align: center;"><g:message code="bv.undoReconciliation.Date.label" default="Date "/></th>
                    <th width="" style="padding-right: 20px;text-align: right"><g:message code="bv.undoReconciliation.Amount.label" default="Amount"/></th>
                </tr>
                </thead>
            </table>
        </div>

%{--2nd--}%
        <%if(bankPaymentInfoDetailsInstance !=[] ){%>

         <table style="border-bottom: 1px solid #a5a5a5;">
            <tbody>
                <g:each in="${bankPaymentInfoDetailsInstance}" status="j" var="bankPaymentInfoDetails">
                    <%
                        BigDecimal showAmount = new BigDecimal(bankPaymentInfoDetails[5])
                    %>
                    <tr style="background-color: #f9f9f9;">
                        <td width="126" style="padding-left: 25px;">${bankPaymentInfoDetails[0]}</td>
                        <td width="150" style="padding-left: 5px;">${bankPaymentInfoDetails[1]}</td>
                        <td width="144" style="padding-left: 5px;">${bankPaymentInfoDetails[2]}</td>
                        <td width="356" style="padding-left: 3px;">${bankPaymentInfoDetails[3]}</td>
                        <td width="60" style="text-align: center;">${bankPaymentInfoDetails[4]}</td>
                        <td width="" style="padding-right: 20px;text-align: right">${showAmount.setScale(2, BigDecimal.ROUND_HALF_UP)}</td>
                    </tr>
                </g:each>
            </tbody>
        </table>
        <%}else{ %>
           <div class="fieldContainer" style="margin-top: 11px;margin-left:364px;font-weight:bold;font-size: 14px; "><g:message code="vendorMaster.financialTab.bankTransaction.label"/></div>
        <%}%>
    </div>  <!--InvoiceAmountTabl-->

%{--3rd table--}%
    %{--<p style="font-weight: bold;font-size: 14px;margin-bottom: 7px; margin-top: 30px;">--}%
        %{--<g:message code="customerMaster.financeTranTableHeader3.label" default="Book of Or Write of Amount"/>--}%
    %{--</p>--}%
    %{--<div class="InvoiceAmountTabl">--}%
        %{--<div id="tableHeadForQuickEntry" class="financeTranTablHead">--}%
            %{--<hr style="color: #fdfcfc;  margin-bottom: -1px;">--}%
            %{--<table class="createReceipt createReceiptFinanceHead" width="100%" style="border: 1px solid #fdfcfc;">--}%
                %{--<thead>--}%
                %{--<tr>--}%
                    %{--<th width="305" style="padding-left: 20px;"><g:message code="bv.budgetItemExpenseDetails.glAccount.label" default="GL Account"/></th>--}%
                    %{--<th width="242" style="text-align: right;"><g:message code="invoiceIncome.amount.label" default="Amount"/></th>--}%
                    %{--<th width="" style="text-align: center;"><g:message code="customerMaster.contraBankAccount.label" default="Contra Bank Account"/></th>--}%
                %{--</tr>--}%
                %{--</thead>--}%
            %{--</table>--}%
        %{--</div>--}%
%{--3rd--}%
        %{--<table>--}%
            %{--<tbody>--}%
            %{--<tr style="background-color: #f9f9f9;border-top: 1px solid #f2f2f2;">--}%
                %{--<td width="305" style="padding-left: 20px;">8800  Verkoop (BTW laag)</td>--}%
                %{--<td width="247" style="text-align: right;">4567890342</td>--}%
                %{--<td width="" style="text-align: center">Contra-67890</td>--}%
            %{--</tr>--}%
            %{--<tr style="background-color: #f2f2f2;border-top: 1px solid #f2f2f2;">--}%
                %{--<td width="305" style="padding-left: 20px;">8950  Verkoop buitenland binnen EU</td>--}%
                %{--<td width="247" style="text-align: right;">8907654321</td>--}%
                %{--<td width="" style="text-align: center">Contra-12345</td>--}%
            %{--</tr>--}%
            %{--</tbody>--}%
        %{--</table>--}%
    %{--</div>  <!--InvoiceAmountTabl-->--}%

    </div>  <!--singleContainer-->
</div> <!---main-container--->

%{--</div>--}%

%{--------------- /////////////// --------------------}%
%{--<g:hiddenField name="customer_id" value="${params.id}" />--}%
%{--<g:hiddenField name="id" value="${customerPostalAddressInstance?.id}" />--}%

%{--<% if(params.fInv){ %>--}%
%{--<g:hiddenField name="bookInvoiceId" value="${params.bookInvoiceId}" />--}%
%{--<g:hiddenField name="bookingPeriod" value="${params.bookingPeriod}" />--}%
%{--<g:hiddenField name="customerId" value="${params.customerId}" />--}%
%{--<g:hiddenField name="budgetItemDetailsId" value="${params.budgetItemDetailsId}" />--}%
%{--<g:hiddenField name="fInv" value="${params.fInv}" />--}%
%{--<% }%>--}%

%{--<% if(params.incBItem){ %>--}%
%{--<g:hiddenField name="bookingPeriod" value="${params.bookingPeriod}" />--}%
%{--<g:hiddenField name="customerId" value="${params.customerId}" />--}%
%{--<g:hiddenField name="journalId" value="${params.journalId}" />--}%
%{--<g:hiddenField name="incBItem" value="${params.incBItem}" />--}%
%{--<% }%>--}%