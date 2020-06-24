<%@ page import="factoring.CoreParamsHelperTagLib; java.text.SimpleDateFormat;" %>

<script type="text/javascript">

</script>

<div class="main-container">
    <div class="codeContainerSections">

        <div class="codeFieldLeft">
            <div class="codeContainer">
                <div class="fieldContainer required">
                    <label for="customerCode">
                        <g:message code="customerMaster.customerCode.label" default="Customer Code"/><b>:</b>
                        <span class="required-indicator">*</span>
                    </label>

                    <p class="labelCode">${customerPrefix?customerPrefix:cusPrefix}${"-"}${customerMasterInstance?.customer_code?customerMasterInstance?.customer_code:cusCode}</p>
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


    <div class="singleContainer">

        <div class="fieldContainer" style="display: block; height: 36px;margin-left: 11px;">
            <p style="font-weight: bold;font-size: 14px;margin-bottom: 7px;">
                <g:message code="bv.customerMaster.paidInvoices.label" default="Paid invoices"/>
            </p>
        </div>

        <div class="InvoiceAmountTabl">
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
                            <g:set var="creditLimit" value="${DSInvoice[11]}"></g:set>
                            <g:set var="totalAmount" value="${totalAmount+DSInvoice[7]}"></g:set>

                            <g:set var="totalRecs" value="${totalRecs+1}"></g:set>
                            <g:set var="totalDays" value="${totalDays+DSInvoice[9]}"></g:set>
                            <g:if test="${DSInvoice[10]<=0}">
                                <g:set var="lastSixMonthsRecs" value="${lastSixMonthsRecs+1}"></g:set>
                                <g:set var="lastSixMonthsDays" value="${lastSixMonthsDays+DSInvoice[9]}"></g:set>
                            </g:if>
                        </g:each>
                        <tr style="background-color: #fcfcfc;">
                            <td colspan="3" width="36%"><span style="display: block;text-align: left;padding-right: 5px;font-weight:bold">Average days outstanding: <g:if test="${totalRecs>0}">${Math.round((totalDays/totalRecs)*100)/100}</g:if></span></td>
                            <td colspan="4" width="48%"><span style="display: block;text-align: right;padding-right: 5px;font-weight:bold">Total: </span></td>
                            <td width=""><span style="display: block;font-weight:bold">${Math.round(totalAmount*100)/100}</span></td>
                        </tr>
                        <tr style="background-color: #fcfcfc;">
                            <td colspan="3" width="36%"><span style="display: block;text-align: left;padding-right: 5px;font-weight:bold">Average days last 6 months: <g:if test="${lastSixMonthsRecs>0}">${Math.round((lastSixMonthsDays/lastSixMonthsRecs)*100)/100}</g:if></span></td>
                            <td colspan="5"></td>
                        </tr>
                        </tbody>
                    </table>
                </g:if>
                <table cellspacing="0" class="createReceipt createReceiptFinanceHead" width="100%" style="border: 1px solid #fdfcfc;">
                    <thead>
                    <tr>
                        <th width="12%">${message(code: 'bv.invoiceExpense.paymentRef.label', default: 'Payment Reference')}</th>
                        <th width="12%">${message(code: 'debtorMaster.debtorName.label', default: 'Debtor Name')}</th>
                        <th width="12%">${message(code: 'customerBankAccount.customer.label', default: 'Customer')}</th>
                        <th width="12%">${message(code: 'reportIncomeBudget.InvoiceDate.label', default: 'Invoice Date')}</th>
                        <th width="12%">${message(code: 'paymentTerms.label', default: 'Payment Terms')}</th>
                        <th width="12%">${message(code: 'report.daysToPayList.label', default: 'Days to Pay')}</th>
                        <th width="12%">${message(code: 'report.aging.dueAmount.label', default: 'Due Amount')}</th>
                        <th width="">${message(code: 'report.aging.invoiceAmount.label', default: 'Invoice Amount')}</th>
                    </tr>
                    </thead>
                </table>
            </div>

            <g:if test="${debtorPaidInvoices}">
                <table style="border-bottom: 1px solid #a5a5a5;">
                    <tbody>
                    <g:each in="${debtorPaidInvoices}" status="j" var="DSInvoice">
                        <tr style="background-color: #f9f9f9;">
                            <td width="12%">${DSInvoice[0]}</td>
                            <td width="12%">${DSInvoice[1]}</td>
                            <td width="12%">${DSInvoice[2]}</td>
                            <td width="12%">${DSInvoice[3]}</td>
                            <td width="12%">${DSInvoice[4]}</td>
                            <td width="12%">${DSInvoice[6]}</td>
                            <td width="12%">${Math.round((DSInvoice[7]-DSInvoice[8])*100)/100}</td>
                            <td width=""><span title="${DSInvoice[15]}">${DSInvoice[7]}</span></td>
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </g:if>
            <g:else>
                <div class="fieldContainer" style="margin-top: 11px;margin-left:364px;font-weight:bold;font-size: 14px; ">${message(code: 'customerMaster.paidInvoices.nodata.label', default: 'There is no paid invoices to show')}</div>
            </g:else>

        </div>

    </div>  <!--singleContainer-->
</div> <!---main-container--->

