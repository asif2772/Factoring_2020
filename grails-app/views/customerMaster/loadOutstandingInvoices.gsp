<%@ page import="bv.CoreParamsHelperTagLib;java.text.SimpleDateFormat;" %>

<% def contextPath = request.getServletContext().getContextPath()%>
<script type="text/javascript">

    $( document ).ready(function() {
        $('#debtorOutstandingTable').dataTable({
            "paging":   false
        });
        $('#addExtraDaysModal').hide();
    });

    function openAddExtraDaysModal(extraDays, invoiceId) {
        $('#invoiceIncomeId').val(invoiceId);
        $('#numberOfDays').val(extraDays);
        $('#addExtraDaysModal').modal('show');
    }

    function saveExtraDays() {
        $.ajax({
            type:'POST',
            url:'${contextPath}/invoiceIncome/saveExtraDays',
            data:{
                invoiceIncomeId: $('#invoiceIncomeId').val(),
                numberOfDays: $('#numberOfDays').val()
            },
            success: function(data)
            {
                location.href='${contextPath}/customerMaster/list/'+data[0][0]+'?edit='+data[0][0];
            },
            error: function(XMLHttpRequest,textStatus,errorThrown) {

            }
        });
    }

</script>

<div id="tableHeadOutstanding" class="financeTranTablHead">
    <g:if test="${debtorOutstandingInvoices}">
        <g:set var="creditLimit" value="${creditLimit}"></g:set>
        <g:set var="totalAmount" value="${0}"></g:set>
        <g:set var="totalPaidAmount" value="${0}"></g:set>

        <g:set var="lastSixMonthsRecs" value="${0}"></g:set>
        <g:set var="lastSixMonthsDays" value="${0}"></g:set>
        <g:set var="totalRecs" value="${0}"></g:set>
        <g:set var="totalDays" value="${0}"></g:set>
        <table style="border-bottom: 1px solid #a5a5a5;">
            <tbody>
            <g:each in="${debtorOutstandingInvoices}" status="j" var="DSInvoice">
                <g:set var="totalAmount" value="${totalAmount+DSInvoice[7]}"></g:set>
                <g:set var="totalPaidAmount" value="${totalPaidAmount+DSInvoice[9]}"></g:set>

                <g:set var="totalRecs" value="${totalRecs+1}"></g:set>
                <g:set var="totalDays" value="${totalDays+DSInvoice[4]}"></g:set>
                <g:if test="${DSInvoice[5]<=0}">
                    <g:set var="lastSixMonthsRecs" value="${lastSixMonthsRecs+1}"></g:set>
                    <g:set var="lastSixMonthsDays" value="${lastSixMonthsDays+DSInvoice[4]}"></g:set>
                </g:if>
            </g:each>
            <%
                BigDecimal bdTotalAmount = new BigDecimal(totalAmount)
                BigDecimal bdOutstanding = new BigDecimal(totalAmount-totalPaidAmount)
                BigDecimal bdBalanceCreditLimit = new BigDecimal(creditLimit-totalAmount+totalPaidAmount)

            %>
            <tr style="background-color: #fcfcfc;">
                <td colspan="7" width="84%"><span style="display: block;text-align: right;padding-right: 5px;font-weight:bold">Total invoice: </span></td>
                <td width=""><span style="display: block;font-weight:bold"><bv:euroDotCommaDecimalFormat decimalData="${bdTotalAmount}" /></span></td>
            </tr>
            <tr style="background-color: #fcfcfc;">
                <td colspan="7" width="84%"><span style="display: block;text-align: right;padding-right: 5px;font-weight:bold">Total amount outstanding: </span></td>
                <td width=""><span style="display: block;font-weight:bold"><bv:euroDotCommaDecimalFormat decimalData="${bdOutstanding}" /></span></td>
            </tr>
            <tr style="background-color: #fcfcfc;">
                <td colspan="7" width="84%"><span style="display: block;text-align: right;padding-right: 5px;font-weight:bold">Balance credit limit: </span></td>
                <td width=""><span style="display: block;text-align: left" class="<g:if test="${(creditLimit-totalAmount+totalPaidAmount)>0}">nrmAlrt</g:if><g:else>rdAlrt</g:else>"><bv:euroDotCommaDecimalFormat decimalData="${bdBalanceCreditLimit}" /> </span></td>
            </tr>
            </tbody>
        </table>
    </g:if>
    <table id="debtorOutstandingTable" cellspacing="0" class="createReceipt createReceiptFinanceHead" width="100%" style="border: 1px solid #fdfcfc;">
        <thead>
        <tr>
            <th width="14%">${message(code: 'bv.invoiceExpense.paymentRef.label', default: 'Payment Reference')}</th>
            <th width="26%">${message(code: 'customerBankAccount.customer.label', default: 'Customer')}</th>
            <th width="12%">${message(code: 'reportIncomeBudget.InvoiceDate.label', default: 'Invoice Date')}</th>
            <th width="12%">${message(code: 'paymentTerms.label', default: 'Payment Terms')}</th>
            <th width="12%">${message(code: 'report.daysToPayList.label', default: 'Days to Pay')}</th>
            <th width="12%">${message(code: 'report.aging.dueAmount.label', default: 'Due Amount')}</th>
            <th width="">${message(code: 'report.aging.invoiceAmount.label', default: 'Invoice Amount')}</th>
            <th></th>
        </tr>
        </thead>
        <g:if test="${debtorOutstandingInvoices}">

            <tbody>
            <g:each in="${debtorOutstandingInvoices}" status="j" var="DSInvoice">
                <%
                    BigDecimal bdDueAmount = new BigDecimal(DSInvoice[8])
                    BigDecimal bdInvoiceAmount = new BigDecimal(DSInvoice[7])
                %>
                <tr style="background-color: #f9f9f9;">
                    <td width="14%">
                        <g:link style="color: #1870b3;text-align: left;" action="outstandingInvoicesDetails"  controller="reportCustomerSettlement" id="${DSInvoice[13]}" >${DSInvoice[0]}</g:link>
                    </td>
                    <td width="26%">${DSInvoice[1]}</td>
                    <td width="12%">${DSInvoice[2]}</td>
                    <td width="12%">${DSInvoice[6]}</td>
                    <td width="12%">${DSInvoice[3]}</td>
                    <td width="12%"><bv:euroDotCommaDecimalFormat decimalData="${bdDueAmount}" /> </td>
                    <td width=""><bv:euroDotCommaDecimalFormat decimalData="${bdInvoiceAmount}" /></td>
                    <td><img onclick="openAddExtraDaysModal(${DSInvoice[14]}, ${DSInvoice[13]})" width="16" height="15" title="${DSInvoice[14]}" alt="Edit" src="${assetPath(src:'agenda-icon.png')}"></td>
                </tr>
            </g:each>
            </tbody>

        </g:if>
        <g:else>
            <div class="fieldContainer" style="margin-top: 11px;margin-left:364px;font-weight:bold;font-size: 14px; ">${message(code: 'customerMaster.outstandingInvoices.nodata.label', default: 'There is no outstanding invoices to show')}</div>
        </g:else>
    </table>
</div>

<!-- Modal -->
<div id="addExtraDaysModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="addExtraDaysModalLabel" aria-hidden="true">
    <g:form method="post">
        <input type="hidden" name="invoiceIncomeId" id="invoiceIncomeId"/>
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
            <h3 id="addExtraDaysModalLabel">Extra payment days</h3>
        </div>
        <div class="modal-body">
            <input type="number" pattern="[0-9]" onkeypress="return !(event.charCode == 46)" min=0 oninput="validity.valid||(value='')" name="numberOfDays" id="numberOfDays" required="" placeholder="" style="border-radius: 3px;">
        </div>
        <div class="modal-footer">
            <div class="btnClassDiv updateLinkBtn">
                <button onclick="saveExtraDays()" class="greenBtn"><span><strong>Add extra days</strong></span></button>
            </div>

        </div>
    </g:form>
</div>

