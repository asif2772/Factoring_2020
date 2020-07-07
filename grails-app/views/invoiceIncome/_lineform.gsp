<%@ page import="factoring.BudgetViewDatabaseService; factoring.CoreParamsHelperTagLib; factoring.InvoiceIncomeDetails;" %>
<%
    def vatCategoryId = ""+params.vatCategoryId
%>

<% def contextPath = request.getServletContext().getContextPath() %>

<div id="itemList" class="itemListField" style=" margin-top: 10px;margin-bottom: 0; background: none repeat scroll 0 0 white;height: 52px;">
        <g:hiddenField name="productId" id="productId" value="1"/>

    <div id="searchresults" class="searchresultsDivStyle">
        <div class="fieldcontain ${hasErrors(bean: invoiceIncomeInstance, field: 'accountCode', 'error')} required invoiceSearchresultsCombo">
            %{--<% if (budItemDArr) {%>--}%
            <%
                def vats = new CoreParamsHelperTagLib().getDebtorCustomerGLDropDownForIncomeIvoice("JournalChartId", 1)
            %>
            <select class="styled sidebr01" name="JournalChartId" id="JournalChartId">
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
            %{--<% } %>--}%



        </div>

        <div class="fieldcontain descriptionDiv">
            <g:textField name="note" value=""/>
        </div>

        <div class="fieldcontain vatCategoryCombo">
             <%
                 def vatCom = new CoreParamsHelperTagLib().getVatCategoryWithAjax('vatRate', vatCategoryId)
             %>
            <select onchange="calPriceByVAT(this.value);" class="styled sidebr01" name="vatRate" id="vatRate">
            %{-- <option value="">- no select -</option>--}%
                <g:each in="${vatCom}" var="opt">
                    <g:if test="${vatCom.size() > 0}">
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
            <g:textField name="unitPrice" id="unitPrice" required="" placeholder=""
                         value="0.00" onfocus="ClearPlaceHolder (this)" onblur="SetPlaceHolder (this)"
                         oninvalid="this.setCustomValidity('${message(code: 'bv.common.unitPrice.blank', default: 'Please give Unit Price')}')"
                         title="${message(code: 'bv.common.unitPrice.blank', default: 'Please give Unit Price')}"
                         onkeyup="calPrice(document.getElementById('vatRate').value, this.value)"/>
        </div>

            <g:hiddenField name="discount" value="0" onkeyup="allnumeric(this.value)" required="" style="width:40px;"/>
            <g:hiddenField name="quantity" value="1"/>

        <div id="searchresultsVAT" class="fieldcontain searchresultsVatDiv">
            <g:field name="vatAmount" id="vatAmount" value="0.00" />
        </div>
    </div>

    <div id="bookInvoiceDetailsAddButton" class="btnClassDiv updateLinkBtn btnAddPositionDiv">
        <img id="addInvSpin" style="float: left;margin: 5px 0 0 -30px;display: none;" src="${resource(dir:'images',file:'spinner.gif')}" alt="${message(code:'spinner.alt.Loading',default:'Loading...')}" />
        <g:submitButton name="create" class="updateBtn addBtn addBtnPosition" value="${message(code: 'default.button.add.label', default: 'Add')}"/>
    </div>
</div> <!--itemListField-->


