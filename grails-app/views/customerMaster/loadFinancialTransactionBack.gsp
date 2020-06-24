<%@ page import="bv.CoreParamsHelperTagLib;java.text.SimpleDateFormat;" %>
<script type="text/javascript">

    (function(){
        setTimeout(function () {
           $("#msgdiv").hide('blind', {}, 1000);
        }, 3000);

        $("#transDate").datepicker({
            dateFormat: 'dd-mm-yy',
            showOn: "button"}).datepicker("setDate", new Date());
    })();

</script>

    <g:formRemote name="writeOffForm" url="[controller:'customerMaster',action:'writeOffInvoicePaymentCustomer']" >
        <%

            def rowNo = 0;

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
                        <td width="120" style="text-align: center"><g:if test="${invoiceCustomerDetails[10]}">${new SimpleDateFormat("dd MMM yyyy").format(invoiceCustomerDetails[10])}</g:if> </td>
                        <td width="130" style="text-align: right;padding-right: 4px;">${showInvoiceAmount.setScale(2, BigDecimal.ROUND_HALF_UP)}</td>
                        <td width="130" style="text-align: right;padding-right: 2px;">${showDueAmount.setScale(2, BigDecimal.ROUND_HALF_UP)}</td>
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

                    <div class="fieldContainerRightOffDiv writeOff2Button">
                        <div class="fieldContainer updateLinkBtn ashBtnDiv writeOffBtn">
                            <g:submitToRemote class="ashBtn" action="writeOffInvoicePaymentCustomer" update="tabpage_6" value="${message(code: 'customerMaster.financeTranTab.writeOff.label', default: 'Write Off')}"/>
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
    </g:formRemote>


%{--2nd Table--}%

    <div class="InvoiceAmountTabl" style="margin-top: 40px;">

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
                        <td width="" style="padding-right: 20px;text-align: right">${showAmount.setScale(2, BigDecimal.ROUND_HALF_UP)}</td>
                    </tr>
                </g:each>
                </thead>
            </table>
        </div>
        <% } else{%>
        <div class="fieldContainer" style="margin-top: 11px;margin-left:364px;font-weight:bold;font-size: 14px; "><g:message code="vendorMaster.financialTab.bankTransaction.label"/></div>
        <%}%>
    </div>  <!--InvoiceAmountTabl-->
