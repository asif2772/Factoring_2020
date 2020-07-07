<%@ page import="factoring.CoreParamsHelperTagLib; java.text.SimpleDateFormat;" %>
<% def contextPath = request.getServletContext().getContextPath()%>
<script type="text/javascript">
    function isWriteOffClicked(){
        var isValid = false;
        $('input:checked').each(function() {
            $("#writeOffLoader").show();
            isValid = true;
        });
        return isValid;
    }
    $(document).ready(function () {
        setTimeout(function () {
            $("#msgdiv").hide('blind', {}, 1000);
        }, 3000);
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

<div class="main-container">
   <div class="codeContainerSections">

        <div class="codeFieldLeft">
            <div class="codeContainer">
                <div class="fieldContainer required">
                    <label for="customerCode">
                        <g:message code="customerMaster.customerCode.label" default="Customer Code"/><b>:</b>
                        <span class="required-indicator">*</span>
                    </label>
                    <p class="labelCode">DEB${"-"}${customerMasterInstance?.customer_code?customerMasterInstance?.customer_code:cusCode}</p>
                </div>
            </div>  <!--codeContainer-->
        </div>  <!--fieldContainerLeft-->

        <div class="codeFieldMiddle">
            <div class="codeContainer">
                <div class="fieldContainer required">
                    <label for="customerName">
                        <g:message code="customerMaster.budgetName.label" default="Budget Name"/><b>:</b>
                        <span class="required-indicator">*</span>
                    </label>
                    %{--<g:message code="customerMaster.lastName.label" default="Last Name"/>--}%
                    <p class="labelCode">${customerMasterInstance?.customer_name?customerMasterInstance?.customer_name:cusName}</p>

                    %{--<p class="labelCode">${customerPrefix + customerMasterInstance?.customer_code}</p>--}%
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
    <div class="fieldContainer" style="display: block; height: 36px;margin-left: 11px;">
        <p style="font-weight: bold;font-size: 14px;margin-bottom: 7px;">
            <g:message code="customerMaster.financeTranTableHeader1.label" default="Invoices Including Paid Amount"/>
        </p>
        <p class="ashBtnDiv" style="float: right; margin-top: -23px; margin-right: 22px;">
            <g:link class="ashBtn" style="line-height: 22px;" action="exportExcelofBankTransaction"  controller="customerMaster" id="${customerMasterInstance?.id?customerMasterInstance?.id:cusId}" ><g:message code="bv.vendorMaster.excelReportButton.lable" default="Excel Report" /></g:link>
        </p>
    </div>
    <div class="singleContainer" style="margin-top: 5px;">

        %{--1st table--}%
        <g:form name="writeOffForm" url="[controller:'customerMaster',action:'writeOffInvoicePaymentCustomer']" >
        <%
            def rowNo = 0
        %>
        <div class="InvoiceAmountTabl">
            <div id="tableHeadForQuickEntry" class="financeTranTablHead">
                %{--<hr style="color: #fdfcfc;  margin-bottom: -1px;">--}%
                <table class="createReceipt createReceiptFinanceHead" width="100%" style="border: 1px solid #fdfcfc;">
                    <thead>
                    <tr>
                        <th width="190" style="padding-left: 20px;"><g:message code="invoiceExpense.gridList.invoiceNumber.label"  default="Invoice Number"/></th>
                        <th width="155"><g:message code="customerMaster.paymentRefference.label" default="Payment Refference"/></th>
                        <th width="120"><g:message code="bv.autoReconciliationOpenInvoicesTransactionDate.label" default="Transaction Date"/></th>
                        <th width="120" style="text-align: center"><g:message code="report.aging.dueDate.label" default="Due Date"/></th>
                        <th width="120" style="text-align: center"><g:message code="report.aging.lastPaymentDate.label" default="Last Payment Date"/></th>
                        <th width="130" style="text-align: right;"><g:message code="report.aging.invoiceAmount.label" default="Invoice Amount"/></th>
                        <th width="130" style="text-align: right"><g:message code="report.aging.dueAmount.label" default="Due Amount"/></th>
                        <th width="" style="margin-right: 50px;text-align: center;width: 96px;"><g:message code="report.aging.paidStatus.label" default="Paid Status"/></th>
                    </tr>
                    </thead>
                </table>
            </div>
            %{--<hr style="border-top: 1px solid #8e8e8e;">--}%

            %{--1st--}%
            <% if(invoiceCustomerDetailsInstance !=[] ){ %>

            <table style="border-bottom: 1px solid #a5a5a5;">
                <tbody>
                <g:each in="${invoiceCustomerDetailsInstance}" status="i" var="invoiceCustomerDetails">
                <%
                    BigDecimal showInvoiceAmount = new BigDecimal(invoiceCustomerDetails[4])
                    BigDecimal showPaidAmount = new BigDecimal(invoiceCustomerDetails[5])
                    BigDecimal showDueAmount = new BigDecimal(invoiceCustomerDetails[6])

                    def notPaidFlag = false;
                    Double dInvAmount = showInvoiceAmount.toDouble();
                    Double dPaidAmount = showPaidAmount.toDouble();
                    Double dDueAmount = showDueAmount.toDouble();
                    String strPaidStatus = "Not Paid";
                    if(dDueAmount == 0.00){
                        strPaidStatus = "Paid";
                    }
                    else if(dDueAmount < 0.00 && dInvAmount < dPaidAmount){
                        strPaidStatus = "Over Paid";
                        notPaidFlag = true
                    }else{
                        notPaidFlag = true;
                    }

                        def bookingMonth = new CoreParamsHelperTagLib().monthNameShow(Integer.parseInt(invoiceCustomerDetails[3]))
                %>
                    <tr style="background-color: #f9f9f9;">
                        <td width="190" style="padding-left: 20px;">${invoiceCustomerDetails[0]}</td>
                        <td width="155">${invoiceCustomerDetails[1]}</td>
                        <td width="120">${new SimpleDateFormat("dd MMM yyyy").format(invoiceCustomerDetails[2])}</td>
                        <td width="120" style="text-align: center">${new SimpleDateFormat("dd MMM yyyy").format(invoiceCustomerDetails[9])}</td>
                        <td width="120" style="text-align: center"><g:if test="${!strPaidStatus.equals('Not Paid')}">${new SimpleDateFormat("dd MMM yyyy").format(invoiceCustomerDetails[10])}</g:if></td>
                        <td width="130" style="text-align: right;padding-right: 4px;"><bv:euroDotCommaDecimalFormat decimalData="${showInvoiceAmount.setScale(2, BigDecimal.ROUND_HALF_UP)}" /></td>
                        <td width="130" style="text-align: right;padding-right: 2px;"><bv:euroDotCommaDecimalFormat decimalData="${showDueAmount.setScale(2, BigDecimal.ROUND_HALF_UP)}" /></td>
                        <% if(notPaidFlag){ %>
                        <td width="20" style="text-align: right"><input type="checkbox" name = "invBankStatement" value="${rowNo}"/></td>
                        <% }else{ %>
                        <td width="20" style="text-align: right"></td>
                        <%}%>
                        <td width="" style="text-align: center;width: 96px;">${strPaidStatus}</td>
                    </tr>
                    <% rowNo++; %>
                </g:each>

                </tbody>
            </table>


            <div class="fieldContainer fieldContainerRightOffDiv" style="display: block !important;height: 70px;">
                <div class="fieldContainerRightOffDiv wrightOffDateDiv" style="">
                    <div class="fieldContainer onStyle">
                        <g:message code="vendorCustomerMasterOn.label" default="Booking Date On"/>
                    </div>

                    <div  class="required fieldContainer manualEntryDatePick datePicVenCus">
                        <input type="text" id="transDate" name="transDate">
                    </div>

                    <div class="fieldContainerRightOffDiv writeOff2Button" style="padding-right: 0px;">
                        <div class="fieldContainer updateLinkBtn ashBtnDiv writeOffBtn" style="margin: 25px 10px 0px;">
                            <img id="writeOffLoader" style="display: none;margin-left: -22px;" src="${assetPath(src:'spinner.gif')}" alt="${message(code:'spinner.alt.Loading',default:'Loading...')}" />
                            <g:actionSubmit before="if(isWriteOffClicked()){" after="}" style="width:100px;" class="ashBtn" action="writeOffInvoicePaymentCustomer" update="tabpage_6" value="${message(code: 'customerMaster.financeTranTab.writeOff.label', default: 'Write Off')}"/>
                            <g:link class="ashBtn" style="line-height: 22px;display:inline-block;position: relative;top: 1px;" action="showUndoWriteOff"  id="${customerMasterInstance?.id?customerMasterInstance?.id:cusId}" ><g:message code="customerMaster.undo.writeoff.label" default="Undo Write Off" /></g:link>
                            <div class="" style="padding-top: 10px; padding-bottom: 10px;width: 262px;">
                                <input type="text" id="writeOffAmount" name="writeOffAmount" placeholder="0.00">
                            </div>
                            <div class="" style="padding-bottom: 10px;width: 262px;">
                                <input type="text" class="" style="width:220px" name="writeOffDescription" id="writeOffDescription" placeholder="Description">
                            </div>
                        </div>
                    </div>

                    <%}else{%>
                    <div class="fieldContainer" style="text-align:center;margin-top: 11px;font-weight:bold;font-size: 14px;">
                        <g:message code="vendorMaster.financialTab.paidInvoice.label"/>
                    </div>
                    <%}%>

                </div>


            </div>  <!--fieldContainer-->

        </div>

            <g:hiddenField name="id" value="${customerMasterInstance?.id?customerMasterInstance?.id:cusId}" />
            <g:hiddenField name="customerPrefix" value="${customerPrefix?customerPrefix:cusPrefix}" />
            <g:hiddenField name="customerCode" value="${customerMasterInstance?.customer_code?customerMasterInstance?.customer_code:cusCode}" />
            <g:hiddenField name="customerName" value="${customerMasterInstance?.customer_name?customerMasterInstance?.customer_name:cusName}" />
        </g:form>


    %{--2nd Table--}%

        <div class="InvoiceAmountTabl" style="margin-top: 40px;">
            <div style="clear:both"></div>
            <p style="font-weight: bold;font-size: 14px;margin-bottom: 7px; margin-top: 17px;">
                <g:message code="customerMaster.financeTranTableHeader2.label" default="Bank Transaction That were Reconciliated for These Invoice"/>
            </p>
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
            <% if(bankPaymentInfoDetailsInstance !=[]){ %>
            <div id="tableHeadForQuickEntry" class="financeTranTablHead">
                <table class="createReceipt createReceiptFinanceHead" width="100%" style="border: 1px solid #fdfcfc;">
                    <thead>
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
                            <td width="" style="padding-right: 20px;text-align: right"><bv:euroDotCommaDecimalFormat decimalData="${showAmount.setScale(2, BigDecimal.ROUND_HALF_UP)}" /></td>
                        </tr>
                    </g:each>
                    </thead>
                </table>
            </div>
            <% } else{%>
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
                        %{--<th width="285" style="text-align: center;"><g:message code="invoiceIncome.amount.label" default="Amount"/></th>--}%
                        %{--<th width="" style="text-align: center;"><g:message code="customerMaster.contraBankAccount.label" default="Contra Bank Account"/></th>--}%
                    %{--</tr>--}%
                    %{--</thead>--}%
                %{--</table>--}%
            %{--</div>--}%
            %{--3rd--}%
            %{--<table>--}%
                %{--<tbody>--}%
                %{--<tr style="background-color: #f9f9f9;border-top: 1px solid #f2f2f2;">--}%
                    %{--<td width="305" style="padding-left: 20px;">8000-Verkoop geleverde diensten (BTW hoog)</td>--}%
                    %{--<td width="287" style="text-align: center">9252.08</td>--}%
                    %{--<td width="" style="text-align: center">NL29RABO0335984738</td>--}%
                %{--</tr>--}%
                %{--<tr style="background-color: #f2f2f2;border-top: 1px solid #f2f2f2;">--}%
                    %{--<td width="305" style="padding-left: 20px;">8000-Verkoop geleverde diensten (BTW hoog)</td>--}%
                    %{--<td width="287" style="text-align: center">9252.08</td>--}%
                    %{--<td width="" style="text-align: center">NL29RABO0335984738</td>--}%
                %{--</tr>--}%
                %{--</tbody>--}%
            %{--</table>--}%
        %{--</div>  <!--InvoiceAmountTabl-->--}%

    </div>  <!--singleContainer-->
</div> <!---main-container--->