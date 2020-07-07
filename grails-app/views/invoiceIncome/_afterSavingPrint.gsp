<%@ page import="factoring.CustomerPostalAddress;factoring.CustomerGeneralAddress;factoring.BudgetViewDatabaseService; factoring.CoreParamsHelperTagLib;factoring.InvoiceIncome" %>

<div class="navigation">
    <div class="navigationbtn" style="float: right;margin-right: 35px;">
        <g:link controller="dashboard" action="index" class="printpagebtn">
            <g:message code="bv.autoReconciliationConfirmationMessageNo.label" default="Return to Dashboard"/>
        </g:link>
    </div>

    <div class="navigationbtn" style="float: right;margin-right: 0px;">
        <g:link controller="invoiceExpense" action="list">
            <g:message code="invoiceIncome.createCustomerSettlement.button" default="Create Customer Settlement "/>
        </g:link>
    </div>

    <div class="navigationbtn" style="float: right;margin-right: 0px;">
        <g:link controller="invoiceIncome" action="list" params="${[customerIdInv:customerID]}" class="printpagebtn"><g:message code="bv.InvoiceExpenseDetails.savingsPrint.addmore.label" default="Add More"/></g:link>
    </div>

    <div class="navigationbtn">
    </div>
</div>  <!--navigation-->


<div style='display: block;' class="printcontentarea">
<div id="list-page-body-inner" class="content" style="margin-top:0px;">
<g:if test="${flash?.message}">
    <div id="msgCusDiv" style="text-align: center; line-height: 40px;font-size: 15px;font-weight: bold;background:#dff0d8;color:#3c763d;vertical-align: middle;">
        <img style="position: relative;top: 3px;right: 5px;" src="${resource(dir:'images/skin',file:'information.png')}"  />
        ${flash.message}
    </div>
    <%
        flash.message = ""
    %>
</g:if>
<table style="border:none;">
    <tr>
        <td class="afterReceipt" colspan="2" style="padding-right: 20px;">
            <table class="vedordetails" style="border: 1px solid #999;">
                <tr class="headerrow">
                    <td style="width: 150px">&nbsp;&nbsp;&nbsp;&nbsp;
                    <g:message code="bv.InvoiceExpenseDetails.savingsPrint.bookingNumber.label" default="Booking Number"/>
                    </td>
                    <td style="width: 150px"><g:message code="reportIncomeBudget.customerName.label" default="Customer Name"/></td>
                    <td><g:message code="reportIncomeBudget.paymentRef.label" default="Payment Reference"/></td>
                    <td><g:message code="bv.InvoiceExpenseDetails.bookingPeriod.label" default="Booking Period"/></td>
                    <td><g:message code="bv.InvoiceExpenseDetails.invoiceDate.label" default="Invoice Date"/></td>
                    <td><g:message code="bv.InvoiceExpenseDetails.dueDate.label" default="Due Date"/></td>
                </tr>
                <tr>
                    <td><b style="color: #ff0000;">${incomeHeadData[0][0]}</b></td>
                    <td>${incomeHeadData[0][2]}</td>
                    <td>${incomeHeadData[0][8]}</td>
                    <td>
                        <%
                                def monthShow
                                if (Integer.parseInt(incomeHeadData[0][10]) == 12) {
                                    monthShow = "Dec - " + incomeHeadData[0][11]
                                } else {
                                    monthShow = new CoreParamsHelperTagLib().monthNameShow(Integer.parseInt(incomeHeadData[0][10])) + '-' + incomeHeadData[0][11]
                                }
                        %>
                        ${monthShow}
                    </td>
                    <td>
                        <%
                                def tempDateBudgetItemDateTemp = incomeHeadData[0][7]
                                tempDateBudgetItemDateTemp = tempDateBudgetItemDateTemp.toString()
                                def showingDate = tempDateBudgetItemDateTemp.substring(0, 10)

                                def tempDateBudgetItemTransDate = incomeHeadData[0][9]
                                tempDateBudgetItemTransDate = tempDateBudgetItemTransDate.toString()
                                def transDate = tempDateBudgetItemTransDate.substring(0, 10)

                        %>
                        ${transDate}
                    </td>
                    <td>${showingDate}</td>
                </tr>
            </table>
            <br/>


            <table class="vedordetails" style="border: 1px solid #999;">
                <tr class="headerrow">

                    %{--<td style="width: 100px"><g:message code="bv.invoiceExpense.vendorId.label" default="Vendor Name"/></td>--}%
                    <td style="width: 400px">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<g:message code="bv.InvoiceExpenseDetails.savingsPrint.gLAccount.label" default="GL Account"/></td>
                    <td style="width: 250px"><g:message code="bv.InvoiceExpenseDetails.savingsPrint.vatCategory.label" default="Vat Category"/></td>
                    <td style="width: 120px;text-align: right"><g:message code="bv.InvoiceExpenseDetails.vat.label" default="VAT"/></td>
                    <td style="width: 150px;text-align:right;padding-right:30px;"><g:message code="bv.InvoiceExpenseDetails.savingsPrint.price.label" default="Price"/></td>

                </tr>
                <%
                        float totalAmount = 0.00
                        for ($i = 0; $i < invoiceIncomeDetailsInstance.size(); $i++) {
                            def PrefixDataArr = new CoreParamsHelperTagLib().showSystemPrefix(3)
                            def glAccountDetails = new CoreParamsHelperTagLib().getChartMasterInformationByCode(invoiceIncomeDetailsInstance[$i][0])
                            def vatDetails = new CoreParamsHelperTagLib().getVatCategoryDetails(invoiceIncomeDetailsInstance[$i][8])
                            vat=invoiceIncomeDetailsInstance[$i][6]-invoiceIncomeDetailsInstance[$i][5]
                            totalAmount = totalAmount + invoiceIncomeDetailsInstance[$i][5]
                            BigDecimal showVat=new BigDecimal(vat)

                            BigDecimal showPrice = new BigDecimal(invoiceIncomeDetailsInstance[$i][5])
                %>
                <tr>
                    %{--<td>${invoiceIncomeDetailsInstance[$i][11]}</td>--}%
                    <td>${glAccountDetails[0] + "-" + glAccountDetails[1]}</td>
                    <td>${vatDetails[2] + "(" + vatDetails[1] + "%)"}</td>
                    <td style="text-align: right;">${showVat.setScale(2, BigDecimal.ROUND_HALF_UP)}</td>
                    <td style="text-align: right; padding-right: 30px;">${showPrice.setScale(2, BigDecimal.ROUND_HALF_UP)}</td>
                </tr>
                <%
                        }
                        BigDecimal showTotalAmount = new BigDecimal(totalAmount)
                        BigDecimal showTotalVAT = new BigDecimal(incomeHeadData[0][5])

                        double totalAmountWithVax = showTotalAmount + incomeHeadData[0][5]
                        BigDecimal showInvoiceTotal = new BigDecimal(totalAmountWithVax)
                %>
                <tr>
                    <td></td>
                    <td></td>
                    <td  style="text-align: right; padding-right: 20px;"><b>${message(code: 'budgetItemExpenseDetails.subTotal.label', default: 'Sub Total')}</b></td>
                    <td style="text-align: right; padding-right: 20px;">${showTotalAmount.setScale(2, BigDecimal.ROUND_HALF_UP)}&nbsp;&nbsp;&nbsp;&nbsp;</td>
                </tr>
                <tr>
                    <td></td>
                    <td></td>
                    <td style="text-align: right; padding-right: 20px;"><b>${message(code: 'bv.budgetItemExpenseDetails.totalVAT.label', default: 'Total VAT')}</b></td>
                    <td style="text-align: right; padding-right: 20px;">${showTotalVAT.setScale(2, BigDecimal.ROUND_HALF_UP)}&nbsp;&nbsp;&nbsp;&nbsp;</td>
                </tr>
                <tr>
                    <td></td>
                    <td></td>
                    <td  style="text-align: right; padding-right: 20px;"><b>${message(code: 'receipt.grandTotal.label', default: 'Grand Total')}</b></td>
                    <td style="text-align: right; padding-right: 20px;">${showInvoiceTotal.setScale(2, BigDecimal.ROUND_HALF_UP)}&nbsp;&nbsp;&nbsp;&nbsp;</td>
                </tr>
            </table>

        </td>
    </tr>

</table>

</div>  <!--content-->
</div>
