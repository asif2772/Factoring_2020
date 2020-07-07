<%@ page import="factoring.BudgetViewDatabaseService; factoring.CoreParamsHelperTagLib; factoring.InvoiceIncomeDetails;" %>
<%
    ArrayList budItemDArr = new ArrayList()
    if (params.customerId) {
        if (params.budgetItemDetailsId) {
            def BudgetItemExpenseDetailsArr = new BudgetViewDatabaseService().executeQuery("Select glAccount,vatCategoryId FROM BudgetItemIncomeDetails where id=" + params.budgetItemDetailsId)
            if (BudgetItemExpenseDetailsArr.size()) {
                budItemDArr = BudgetItemExpenseDetailsArr[0]
            }
        }
    }
%>

<% def contextcustomer = request.getServletContext().getContextPath() %>

<div id="itemList" class="itemListField" style=" margin-top: 10px;margin-bottom: 0; background: none repeat scroll 0 0 white;height: 52px;">
    <g:hiddenField name="productId" id="productId" value="1"/>

    <div id="searchresults" class="searchresultsDivStyle">
        <div class="fieldcontain ${hasErrors(bean: invoiceIncomeInstance, field: 'accountCode', 'error')} required invoiceSearchresultsCombo"
             style="float: left; width: 206px;margin-left: 16px;">
            %{--<%--}%
                %{--if (budItemDArr) {--}%
            %{--%>--}%
            %{--<%="${new CoreParamsHelperTagLib().getDebtorCustomerGLDropDownForIncomeIvoice("JournalChartId", request.getParameter('JournalChartId') ? budItemDArr[0] : budItemDArr[0], params.bookInvoiceId,0,0)}"%>--}%
            %{--<% } else { %>--}%
            %{--<%= "${new CoreParamsHelperTagLib().getDebtorCustomerGLDropDownForIncomeIvoice("JournalChartId", request.getParameter('JournalChartId') ? request.getParameter('JournalChartId') : '0', params.bookInvoiceId,0,0)}"%>--}%
            %{--<% } %>--}%
        </div>

        <div class="fieldcontain" style="float: left;width:230px; margin-right: 19px;margin-left: 84px;">
            <g:textField name="note" value="" style="margin-bottom: 20px; width: 212px;"/>
            %{--<g:textArea name="note" value="" style="margin-bottom: 20px; width: 212px;height: 23px;"/>--}%
        </div>

        <div class="fieldcontain vatCategoryCombo">
            <%
                if (budItemDArr) {
            %>
            <%
                    def vats = new CoreParamsHelperTagLib().getVatCategoryWithAjax('vatRate', budItemDArr[1])
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
            <% } else {
                if(params.editId){%>
            <%
                    def vats = new CoreParamsHelperTagLib().getVatCategoryWithAjax('vatRate', invoiceIncomeDetailsInstance?.vatCategoryId)
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
            <%}else{%>
            <%
                    def vats = new CoreParamsHelperTagLib().getVatCategoryWithAjax('vatRate', '1')
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
            <%}%>
            <% } %>
        </div>

        <div class="fieldcontain" style="width: 90px; margin-left:11px; text-align:right;">
            <g:textField name="unitPrice" id="unitPrice111" required="" placeholder="0.00"
                         style="width:80px;text-align:right"
                         oninvalid="this.setCustomValidity('${message(code: 'bv.common.unitPrice.blank', default: 'Please give Unit Price')}')"
                         oninput="setCustomValidity('')"
                         title="${message(code: 'bv.common.unitPrice.blank', default: 'Please give Unit Price')}"
                         onkeyup="calPrice(document.getElementById('vatCategoryId').value, this.value)"/>
        </div>

        <g:hiddenField name="discount" value="0" onkeyup="allnumeric(this.value)" required="" style="width:40px;"/>
        <g:hiddenField name="quantity" value="1"/>

        <div id="searchresultsVAT" class="fieldcontain" style="width: 65px;margin-left: 11px;">
            <g:field name="vatAmount" id="vatAmount" value="0.00" style="text-align:right;width: 60px;"/>
        </div>

    </div>

    %{--<div class="buttonClass" style="margin-right: 12px;">--}%
        <div class="btnClassDiv updateLinkBtn" style="float: right; margin-right: 25px;">
            <g:submitButton name="create" class="save updateBtn addBtn" style="padding: 0 2px;" value="${message(code: 'default.button.add.label', default: 'Add')}"/>
        </div>
    %{--</div>--}%
</div> <!--itemListField-->