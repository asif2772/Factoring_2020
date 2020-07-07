<%@ page import="factoring.CoreParamsHelperTagLib;" %>

<script>
    $(document).ready(function() {

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

    } );
</script>

<%
    float totalAmount = 0.00
    float totalVat = 0.00
%>
<% def contextPath = request.getServletContext().getContextPath() %>
<%
    if (params.editId) {
        if (params.editId == null) {
            params.editId = 0
        } else if (params.InvoiceIncomeEditId == null) {
            params.InvoiceIncomeEditId = 0
        } else if (params.editId == '') {
            params.editId = 0
        }
    } else {
        params.editId = 0
    }

    //println(" params.editId "+params.editId + " params.bookInvoiceId "+params.bookInvoiceId + " params.budgetCustomerId "+params.budgetCustomerId);
%>
<g:if test="${flash?.message}">
    <div style="color: #a51f1c; font-size: 14px; text-align: center;width:935px" class="message">${flash.message}</div>
</g:if>
<div id="dateFiscalYear" class="alertMsg">
    Attention! Invoice date is in closed fiscal year.
</div>
<table class="createReceipt tableLeftSpace" style="float: left; border-collapse: separate;border-spacing: 0 1em; border: 1px solid #f7f5f5; margin-bottom: -10px;">
    <tbody>

    <%
        def tempEditId = 0
        if (params.editId) {
            tempEditId = params.editId
        }
        def tempId = ""

        if (session.invoiceIncomeArr) {

            for (i = 0; i < session.invoiceIncomeArr.size(); i++) {
//                println(session.invoiceIncomeArr[i]['ii_id'])
                if (Double.parseDouble(session.invoiceIncomeArr[i]['unitPrice'])) {
                    totalAmount = totalAmount + Double.parseDouble(session.invoiceIncomeArr[i]['unitPrice'])
                } else {
                    totalAmount = totalAmount + Double.parseDouble(session.invoiceIncomeArr[i]['unitPrice'])
                }

                totalVat = totalVat + session.invoiceIncomeArr[i]['vatAmount']
                //totalDiscount = totalDiscount + session.invoiceIncomeArr[i]['discountAmount']
                def vatDetails = new CoreParamsHelperTagLib().getVatCategoryDetails(session.invoiceIncomeArr[i]['vatCategoryId'])
                def JournalChartIdDetails = new CoreParamsHelperTagLib().getChartMasterInformationByCode(session.invoiceIncomeArr[i]['JournalChartId'])

                tempId = i + "::" + tempEditId

                BigDecimal showUnitPrice = new BigDecimal(session.invoiceIncomeArr[i]['unitPrice'])
                BigDecimal showVAT = new BigDecimal(session.invoiceIncomeArr[i]['vatAmount'])

    %>
    <tr class="" style="background: none repeat scroll 0 0 #ffffff;">
        <td width="272">${JournalChartIdDetails[0] + " " + JournalChartIdDetails[1]}</td>
        <td width="213" style=" padding-left: 20px;">${session.invoiceIncomeArr[i]['note']}</td>
        <td width="132" style="padding-left: 21px;">${vatDetails[2] + "&nbsp(" + vatDetails[1] + "%)"}</td>
        <td width="105" style="text-align: right">${showUnitPrice.setScale(2, BigDecimal.ROUND_HALF_UP)}</td>
        <td width="81" style="text-align: right">${showVAT.setScale(2, BigDecimal.ROUND_HALF_UP)}</td>
        <td width="50" style="text-align: center;padding-left: 8px;">

            <%= remoteLink(controller: 'invoiceIncome', action: 'edit', id: i, params: [vatCategoryId: params.vatCategoryId, editId: params.editId, budgetCustomerId : params.budgetCustomerId],
                    update: 'itemList') { '<img style="margin-right: 16px;" src="../images/edit.png" alt="delete" height="15" width="16">' }%>
            &nbsp;
            <%  def bookInvoiceId = params.bookInvoiceId ? params.bookInvoiceId : params.budgetItemIncomeId  %>
            <%= remoteLink(controller: 'invoiceIncome', action: 'deleteItem', id: tempId, params: [vatCategoryId: params.vatCategoryId, editId: params.editId, budgetCustomerId : params.budgetCustomerId], offset: params.offset, max: params.max, order: params.order,
                          update: 'updateItemList') {'<img src="../images/delete.png" alt="delete" height="15" width="16">'}%>
        </td>
    </tr>
    <%
            }
    %>
    <% } %>
  </tbody>
</table>

<div id="lineform" style="float: left;">
    <g:render template="lineform" />
</div>


<table class="createReceipt" style="float: left">
    <tbody>

    <% if (session.invoiceIncomeArr) {
        float grandTotal = 0.00
        grandTotal = (totalAmount + totalVat)
        BigDecimal showgrandTotal = new BigDecimal(grandTotal)

        BigDecimal showtotalTax = new BigDecimal(totalVat)
        BigDecimal showtotalAmount = new BigDecimal(totalAmount)
    %>

    <tr style="background-color: #F2F2F2;">
        %{--<td>&nbsp;</td>--}%
        <td width="91%" style="text-align: right"><g:message code="budgetExpenseItemDetails.totalAmount.label" default="Total Amount"/>:&nbsp;</td>
        <td>&nbsp;</td>
        <td width="13%" style="text-align: right">${showtotalAmount.setScale(2, BigDecimal.ROUND_HALF_UP)}</td>
        <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
    </tr>

    <tr style="background-color: #EAEAEA;">
        %{--<td>&nbsp;</td>--}%
        <td  width="91%" style="text-align: right"><g:message code="budgetExpenseItemDetails.totalVAT.label" default="Total VAT"/>:&nbsp;</td>
        <td>&nbsp;</td>
        %{--<td></td>--}%
        <td width="13%" style="text-align: right">${showtotalTax.setScale(2, BigDecimal.ROUND_HALF_UP)}</td>
        <td>&nbsp;</td>
    </tr>
    <tr style="background-color: #DFDFDF;">
        %{--<td>&nbsp;</td>--}%
        <td width="91%" style="text-align: right;"><b><g:message code="budgetExpenseItemDetails.grandTotal.label" default="Grand Total"/>:</b>&nbsp;</td>
        <td>&nbsp;</td>
        <td width="13%" style="text-align: right;"><span id="GrandTotal"><b>${showgrandTotal.setScale(2, BigDecimal.ROUND_HALF_UP)}</b></span></td>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td colspan="7">
                <div id="saveIncomeInvoiceButton" class="btnClassDiv updateLinkBtn" style="float: right; margin-bottom: 0;">

                    <img id="saveInvSpin" style="float: left;margin: 5px 0 0 -30px;display: none;" src="${resource(dir:'images',file:'spinner.gif')}" alt="${message(code:'spinner.alt.Loading',default:'Loading...')}" />
                    <% if (Integer.parseInt(params.editId.toString()) != 0) { %>
                        <g:submitToRemote before="if(isAlreadySubmitted(2)){" after="}" onerror="enableSubmit(2)" onSuccess="updateDataAndMessage(data)" class="updateBtn addBtn" name="updateInvoiceIncome"
                                          action="updateInvoice" controller="invoiceIncome"
                                          value="${message(code: 'invoiceIncome.updateIncomeInvoice.button', default: 'Update Income Invoice')}"/>
                    <% }else { %>
                        <g:submitToRemote before="if(isAlreadySubmitted(2)){" after="}" onerror="enableSubmit(2)" onSuccess="updateDataAndMessage(data)" class="updateBtn addBtn" name="saveInvoiceIncome"
                                          action="saveInvoice" controller="invoiceIncome" value="${message(code: 'invoiceIncome.saveIncomeInvoice.button', default: 'Save Income Invoice')}"/>
                    <% } %>

                </div>
        </td>
    </tr>
    <% } %>
    </tbody>

</table>

