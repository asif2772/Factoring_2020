<%@ page import="factoring.CoreParamsHelperTagLib; java.text.SimpleDateFormat;" %>

<script type="text/javascript">

</script>

<div class="main-container">
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



    <div class="singleContainer">
        <div class="fieldContainer" style="display: block; height: 36px;margin-left: 11px;">
            <p style="font-weight: bold;font-size: 14px;margin-bottom: 7px;">
                <g:message code="customerMaster.outstandingInvoices.label" default="Outstanding invoices"/>
            </p>
        </div>
        <div id="tabPageLoadData7" class="fieldContainer">
            <div class="spinLoader fieldContainer">
                <img src="${resource(dir:'images',file:'spinner.gif')}" alt="${message(code:'spinner.alt.Loading',default:'Loading...')}" />
            </div>
        </div>
        <div class="InvoiceAmountTabl">
            <div id="tableHeadOutstanding" class="financeTranTablHead">
                <g:if test="${customerOutstandingInvoices}">
                    <g:set var="creditLimit" value="${0}"></g:set>
                    <g:set var="totalAmount" value="${0}"></g:set>
                    <g:set var="totalPaidAmount" value="${0}"></g:set>

                    <g:set var="lastSixMonthsRecs" value="${0}"></g:set>
                    <g:set var="lastSixMonthsDays" value="${0}"></g:set>
                    <g:set var="totalRecs" value="${0}"></g:set>
                    <g:set var="totalDays" value="${0}"></g:set>
                    <table style="border-bottom: 1px solid #a5a5a5;">
                        <tbody>
                        <g:each in="${customerOutstandingInvoices}" status="j" var="DSInvoice">
                            <g:set var="creditLimit" value="${DSInvoice[11]}"></g:set>
                            <g:set var="totalAmount" value="${totalAmount+DSInvoice[7]}"></g:set>
                            <g:set var="totalPaidAmount" value="${totalPaidAmount+DSInvoice[8]}"></g:set>

                            <g:set var="totalRecs" value="${totalRecs+1}"></g:set>
                            <g:set var="totalDays" value="${totalDays+DSInvoice[9]}"></g:set>
                            <g:if test="${DSInvoice[10]<=0}">
                                <g:set var="lastSixMonthsRecs" value="${lastSixMonthsRecs+1}"></g:set>
                                <g:set var="lastSixMonthsDays" value="${lastSixMonthsDays+DSInvoice[9]}"></g:set>
                            </g:if>
                        </g:each>
                        <tr style="background-color: #fcfcfc;">
                            <td colspan="7" width="84%"><span style="display: block;text-align: right;padding-right: 5px;font-weight:bold">Total invoice: </span></td>
                            <td width=""><span style="display: block;font-weight:bold">${Math.round(totalAmount*100)/100}</span></td>
                        </tr>
                        <tr style="background-color: #fcfcfc;">
                            <td colspan="7" width="84%"><span style="display: block;text-align: right;padding-right: 5px;font-weight:bold">Total amount outstanding: </span></td>
                            <td width=""><span style="display: block;font-weight:bold">${Math.round((totalAmount-totalPaidAmount)*100)/100}</span></td>
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

            <g:if test="${customerOutstandingInvoices}">
                <table style="border-bottom: 1px solid #a5a5a5;">
                    <tbody>
                    <g:each in="${customerOutstandingInvoices}" status="j" var="DSInvoice">
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
                <div class="fieldContainer" style="margin-top: 11px;margin-left:364px;font-weight:bold;font-size: 14px; "><g:message code="customerMaster.outstandingInvoices.nodata.label"/></div>
            </g:else>

        </div>

    </div>  <!--singleContainer-->
</div> <!---main-container--->

