<%@ page import="bv.CoreParamsHelperTagLib;java.text.SimpleDateFormat;" %>

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
