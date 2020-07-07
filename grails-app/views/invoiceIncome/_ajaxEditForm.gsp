<%@ page import="factoring.CoreParamsHelperTagLib;" %>

<div id="searchresults" class="searchresultsDivStyle">
    <div class="fieldcontain ${hasErrors(bean: invoiceIncomeInstance, field: 'JournalChartId', 'error')} required invoiceSearchresultsCombo">
       %{--<%="${new CoreParamsHelperTagLib().getDebtorCustomerGLDropDownForIncomeIvoice("JournalChartId", request.getParameter('JournalChartId') ? request.getParameter('JournalChartId') : '0', params.bookInvoiceId?params.bookInvoiceId:0,0,glAccount)}"%>--}%
       <%
           def journalCharts = new CoreParamsHelperTagLib().getDebtorCustomerGLDropDownForIncomeIvoice("JournalChartId", request.getParameter('JournalChartId') ? request.getParameter('JournalChartId') :glAccount)
       %>
        <select class="styled sidebr01" name="JournalChartId" id="JournalChartId">
        %{-- <option value="">- no select -</option>--}%
            <g:each in="${journalCharts}" var="opt">
                <g:if test="${journalCharts.size() > 0}">
                    <g:if test="${opt.inSelected}">
                        <option selected value="${opt.value}">${opt.index}</option>
                    </g:if>
                    <g:else>
                        <option value="${opt.value}">${opt.index}</option>
                    </g:else>
                </g:if>
                <g:else>
                    <option value="0">"No items.."</option>
                </g:else>
            </g:each>
        </select>

    </div>

    <div class="fieldcontain descriptionDiv">
        <g:textField name="note" value="${invoiceIncomeInstance?.note}"/>
    </div>

    <div class="fieldcontain vatCategoryCombo">
        <%
            def vats = new CoreParamsHelperTagLib().getDebtorCustomerGLDropDownForIncomeIvoice('vatRate', vatCategoryId)
        %>
        <select class="styled sidebr01" name="vatRate" id="vatRate">
        %{-- <option value="">- no select -</option>--}%
            <g:each in="${vats}" var="opt">
                <g:if test="${vats.size() > 0}">
                    <g:if test="${opt.inSelected}">
                        <option selected value="${opt.value}">${opt.index}</option>
                    </g:if>
                    <g:else>
                        <option value="${opt.value}">${opt.index}</option>
                    </g:else>
                </g:if>
                <g:else>
                    <option value="0">"No items.."</option>
                </g:else>
            </g:each>
        </select>
    </div>

    <div class="fieldcontain unitPriceDiv">
        <g:field name="unitPrice" id="unitPrice" value="${(invoiceIncomeInstance?.unitPrice)}" required="" placeholder="0.00"
                 oninvalid="this.setCustomValidity('${message(code: 'bv.common.unitPrice.blank', default: 'Please give Unit Price')}')"
                 oninput="setCustomValidity('')"
                 title="${message(code: 'bv.common.unitPrice.blank', default: 'Please give Unit Price')}"
                 onkeyup="calPrice(document.getElementById('vatRate').value, this.value)"/>
    </div>

    <div id="searchresultsVAT" class="fieldcontain searchresultsVatDiv">
        <g:field name="vatAmount" id="vatAmount" placeholder="0.00" value="${(invoiceIncomeInstance?.vatAmount)?((invoiceIncomeInstance?.vatAmount).round(2)):'0.00'}"/>
    </div>

</div>
<g:hiddenField name="sessionPId" value="${sessionPId}"/>
%{--<g:hiddenField name="discount" value="0" onkeyup="allnumeric(this.value)" required="" style="width:40px;"/>--}%
<g:hiddenField name="vatCategoryId" value="${vatCategoryId}" />

%{--<div class="btnClassDiv updateLinkBtn formEditBtn" style="margin: 12px -19px;border: 1px solid red;">--}%
<div class="btnDivStyle btnDivStyleSmallUpdate">
    <g:submitToRemote update="updateItemList" class="greenBtn updateSmallBtn" style="margin-left: 10px;" name="updateInvoiceIncomeItem" action="update" controller="invoiceIncome" value="${message(code: 'default.button.update.label', default: 'Update')}"/>
    <g:submitToRemote class="closeBtn updateCloseBtn" update="itemList" controller="invoiceIncome" action="close" value="${message(code: 'default.button.x.label', default: 'X')}"/>
</div>


