<%@ page import="bv.CoreParamsHelperTagLib;java.text.SimpleDateFormat;" %>
<script type="text/javascript">

    $( document ).ready(function() {
        $('#debtorPaidInvoicesTable').dataTable({
            "paging":   false
        });
    });

</script>
<div id="tableHeadOutstanding" class="financeTranTablHead">
    <g:if test="${debtorPaidInvoices}">
        <g:set var="creditLimit" value="${0}"></g:set>
        <g:set var="totalAmount" value="${0}"></g:set>

        <g:set var="lastSixMonthsRecs" value="${0}"></g:set>
        <g:set var="lastSixMonthsDays" value="${0}"></g:set>
        <g:set var="totalRecs" value="${0}"></g:set>
        <g:set var="totalDays" value="${0}"></g:set>
        <table style="border-bottom: 1px solid #a5a5a5;">
            <tbody>
            <g:each in="${debtorPaidInvoices}" status="j" var="DSInvoice">
                <g:set var="creditLimit" value="${creditLimit}"></g:set>
                <g:set var="totalAmount" value="${totalAmount+DSInvoice[7]}"></g:set>

                <g:set var="totalRecs" value="${totalRecs+1}"></g:set>
                <g:set var="totalDays" value="${totalDays+DSInvoice[4]}"></g:set>
                <g:if test="${DSInvoice[5]<=0}">
                    <g:set var="lastSixMonthsRecs" value="${lastSixMonthsRecs+1}"></g:set>
                    <g:set var="lastSixMonthsDays" value="${lastSixMonthsDays+DSInvoice[4]}"></g:set>
                </g:if>
            </g:each>
            <tr style="background-color: #fcfcfc;">
                <td colspan="3" width="36%"><span style="display: block;text-align: left;padding-right: 5px;font-weight:bold">Average days outstanding: <g:if test="${totalRecs>0}">${Math.round((totalDays/totalRecs)*100)/100}</g:if></span></td>
                <td colspan="4" width="48%"><span style="display: block;text-align: right;padding-right: 5px;font-weight:bold">Total: </span></td>
                <td width=""><span style="display: block;font-weight:bold"><bv:euroDotCommaDecimalFormat decimalData="${totalAmount}" /> </span></td>
            </tr>
            <tr style="background-color: #fcfcfc;">
                <td colspan="3" width="36%"><span style="display: block;text-align: left;padding-right: 5px;font-weight:bold">Average days last 6 months: <g:if test="${lastSixMonthsRecs>0}">${Math.round((lastSixMonthsDays/lastSixMonthsRecs)*100)/100}</g:if></span></td>
                <td colspan="5"></td>
            </tr>
            </tbody>
        </table>
    </g:if>
    <table id="debtorPaidInvoicesTable" cellspacing="0" class="createReceipt createReceiptFinanceHead" width="100%" style="border: 1px solid #fdfcfc;">
        <thead>
        <tr>
            <th width="14%">${message(code: 'bv.invoiceExpense.paymentRef.label', default: 'Payment Reference')}</th>
            <th width="26%">${message(code: 'customerBankAccount.customer.label', default: 'Customer')}</th>
            <th width="12%">${message(code: 'reportIncomeBudget.InvoiceDate.label', default: 'Invoice Date')}</th>
            <th width="12%">${message(code: 'paymentTerms.label', default: 'Payment Terms')}</th>
            <th width="12%">${message(code: 'report.paymentRealized.label', default: 'Payment Realised')}</th>
            <th width="12%">${message(code: 'report.aging.dueAmount.label', default: 'Due Amount')}</th>
            <th width="">${message(code: 'report.aging.invoiceAmount.label', default: 'Invoice Amount')}</th>
        </tr>
        </thead>
        <g:if test="${debtorPaidInvoices}">

            <tbody>
            <g:each in="${debtorPaidInvoices}" status="j" var="DSInvoice">
                <tr style="background-color: #f9f9f9;">
                    <td width="14%">${DSInvoice[0]}</td>
                    <td width="26%">${DSInvoice[1]}</td>
                    <td width="12%">${DSInvoice[2]}</td>
                    <td width="12%">${DSInvoice[6]}</td>
                    <td width="12%">${DSInvoice[4]}</td>
                    <td width="12%"><bv:euroDotCommaDecimalFormat decimalData="${DSInvoice[8]}" /> </td>
                    <td width=""><span title="${DSInvoice[15]}"><bv:euroDotCommaDecimalFormat decimalData="${DSInvoice[7]}" /></span></td>
                </tr>
            </g:each>
            </tbody>

        </g:if>
        <g:else>
            <div class="fieldContainer" style="margin-top: 11px;margin-left:364px;font-weight:bold;font-size: 14px; ">${message(code: 'customerMaster.paidInvoices.nodata.label', default: 'There is no paid invoices to show')}</div>
        </g:else>
    </table>
</div>


