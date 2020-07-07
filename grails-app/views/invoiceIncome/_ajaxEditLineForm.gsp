<%@ page import="factoring.CoreParamsHelperTagLib;" %>

<div style="float: left;width:56px;margin-left: 3px">
    <g:field name="unitPrice" id="unitPrice" value="${invoiceIncomeInstance.unitPrice}" required=""
             oninvalid="this.setCustomValidity('${message(code: 'bv.common.unitPrice.blank',default:'Please give Unit Price' )}')"
             oninput="setCustomValidity('')" title="${message(code: 'bv.common.unitPrice.blank',default:'Please give Unit Price' )}"
             style="width:45px;" onkeyup="calPrice(document.getElementById('quantity').value, this.value)"/>
</div>

<div style="float: left;width:50px;margin-left: 6px">
    <g:field name="discount" value="${invoiceIncomeInstance.discount}" style="width:40px;" onkeyup="allnumeric(this.value)" required="" />
</div>

<div style="float: left;width:205px;margin-left: 21px">
%{-- <g:field name="budgetItemIncomeId" type="number" value="" required=""/>--}%
    <%
        def expanseBookPeriod = new CoreParamsHelperTagLib().getExpanseBookPeriod("budgetItemIncomeId","${invoiceIncomeInstance.productId}","${invoiceIncomeInstance.budgetItemIncomeId}")
    %>
    <select class="styled sidebr01" name="budgetItemIncomeId" id="budgetItemIncomeId">
    %{-- <option value="">- no select -</option>--}%
        <g:each in="${expanseBookPeriod}" var="opt">
            <g:if test="${expanseBookPeriod.size() > 0}">
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

<div style="float: left;width:50px;margin-left: 3px">
    <g:field name="quantity" style="width:40px;" value="${invoiceIncomeInstance.quantity}" required=""
             onkeyup="calPrice(this.value, document.getElementById('unitPrice').value)"  />
    %{--<g:field name="quantity" value="${fieldValue(bean: invoiceIncomeInstance, field: 'quantity')}" required=""/>--}%
</div>

<div style="float: left;width:60px;margin-left: 20px">
    <span id="price" >${(invoiceIncomeInstance?.actualCost)?(invoiceIncomeInstance?.actualCost):'0.0'}</span>
    %{--<g:field name="price" value="${fieldValue(bean: budgetItemIncomeDetailsInstance, field: 'price')}"/>--}%
</div>


<div style="float: left;width:175px;margin-left: 15px">
    <g:select name="VatCategoryId" from="${bv.VatCategory.list()}" optionValue="categoryName" optionKey="id" style="width: 100px;" />
    <g:field name="taxRate" value="${invoiceIncomeInstance?.taxCategory?.rate}" required="" style="width: 40px;"/>
    <g:hiddenField name="taxCategoryId.id" value="${invoiceIncomeInstance.taxCategoryId}" />
    <g:hiddenField name="sessionPId" value="${sessionPId}" />
</div>
%{--<g:select name="VatCategoryId" from="${bv.VatCategory.list()}" optionValue="categoryName" optionKey="id" /> --}%