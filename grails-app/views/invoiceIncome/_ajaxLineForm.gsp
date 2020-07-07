<%@ page import="factoring.CoreParamsHelperTagLib;" %>

<div class="fieldcontain ${hasErrors(bean: invoiceIncomeInstance, field: 'accountCode', 'error')} required" style="width: 180px;" >
    <%
        def journalChart = new CoreParamsHelperTagLib().getJournalChartGroupDropDown("JournalChartId",invoiceIncomeInstance?.salesAccountCode)
    %>

    <select class="styled sidebr01" name="JournalChartId" id="JournalChartId">
    %{-- <option value="">- no select -</option>--}%
        <g:each in="${journalChart}" var="opt">
            <g:if test="${journalChart.size() > 0}">
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

<div class="fieldcontain"  style="float: left;width:180px;">
    <%
        def vats = new CoreParamsHelperTagLib().getVatCategoryWithAjax('vatRate',invoiceIncomeInstance?.vatCategoryId)
    %>
    <select onchange="calPriceByVAT(this.value);" class="styled sidebr01" name="vatRate" id="vatRate">
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


<div class="fieldcontain" style="width: 105px;margin-left:10px;text-align:right">
    <g:field name="unitPrice" id="unitPrice" value="${invoiceIncomeInstance.actualCost}"
             required=""
             oninvalid="this.setCustomValidity('${message(code: 'bv.common.unitPrice.blank',default:'Please give Unit Price' )}')"
             oninput="setCustomValidity('')" title="${message(code: 'bv.common.unitPrice.blank',default:'Please give Unit Price' )}"
             style="width:85px;" onkeyup="calPrice(document.getElementById('quantity').value, this.value)"/>
</div>

<g:hiddenField name="discount" value="0" onkeyup="allnumeric(this.value)" required="" style="width:40px;" />

<div class="fieldcontain" style="width: 64px;text-align:right">
    <g:field name="quantity" value="1" required="" style="width:40px;"
             onkeyup="calPrice(this.value, document.getElementById('unitPrice').value)"  />
</div>

<div class="fieldcontain" style="width: 90px;text-align:right">
    <span id="price" >${(invoiceIncomeInstance?.actualCost)?((invoiceIncomeInstance?.actualCost).round(2)):'0.00'}</span>
</div>




