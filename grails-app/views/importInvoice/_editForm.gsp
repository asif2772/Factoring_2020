<%@ page import="java.text.SimpleDateFormat; factoring.CoreParamsHelperTagLib;" %>

<div class="" style="float: left;">
    <div class="fieldContainerLeft">
        <div class="rowContainer">
            <div class="fieldContainer required fcCombo fcInputText">
                <label for="customerId">
                    <g:message code="debtorMaster.debtorName.label" default="Debtor Name"/><b>:</b>
                    <span class="required-indicator">*</span>
                </label><br>
                %{--<label> ${invoiceInfo.budgetName}</label>--}%
                <g:textField name="debtorName" required="" value="${invoiceInfo.debtorName}"></g:textField>
                %{--<%= "${new CoreParamsHelperTagLib().getIncomeNExpenseBudgetDropdown('budgetName',invoiceInfo.budgetName)}"%>--}%
            </div>  <!--fieldContainer-->
        </div>  <!--rowContainer-->

        <div class="rowContainer">
            <div class="required DatePickNew">
                <label>
                    <g:message code="bv.invoiceExpense.transDate.label" default="Invoice Date"/>
                    <span class="required-indicator">*</span>
                </label>
                <%if(params.editId){%>
                <input type="text" id="invoiceDate" name="invoiceDate" value="${invoiceInfo.invoiceDate}">
                <%}else{%>.
                <input type="text" id="invoiceDate" name="invoiceDate" value="${new SimpleDateFormat("dd-MM-yyyy").parse(new Date())}">
                <%}%>


            </div>
        </div>  <!--rowContainer-->

        <div class="rowContainer">
            <div class="fieldContainer required fcInputText">
                <label for="paymentRef">
                    <g:message code="bv.importInvoice.totalAmountWithVat.label" default="Total Amount With Vat"/>
                    <span class="required-indicator">*</span>
                </label>
                <g:textField name="totalAmntWithVat" required=""  value="${invoiceInfo.total}" placeholder="${g.message(code: 'bv.importInvoice.totalAmountWithVat.label')}"></g:textField>
            </div>
        </div>


    </div>  <!--fieldContainerLeft_1-->

    <div class="fieldContainerRight">
        <div class="rowContainer">
            <div class="fieldContainer required fcCombo fcInputText">
                <label for="customerId">
                    <g:message code="bv.invoiceExpense.vendorId.label" default="Customer Name"/><b>:</b>
                    <span class="required-indicator">*</span>
                </label><br>
                %{--<label> ${invoiceInfo.budgetName}</label>--}%
                <%
                    def cusList = new CoreParamsHelperTagLib().getCustomerListDropDownBox('customerId')
                %>
                <select class="styled sidebr01" name="customerId" id="customerId">
                %{-- <option value="">- no select -</option>--}%
                    <g:each in="${cusList}" var="opt">
                        <g:if test="${cusList.size() > 0}">
                            <option  value="${opt.value}">${opt.index}</option>
                        </g:if>
                        <g:else>
                            <option value="0">"No items.."</option>
                        </g:else>
                    </g:each>
                </select>
                %{--<%= "${new CoreParamsHelperTagLib().getIncomeNExpenseBudgetDropdown('budgetName',invoiceInfo.budgetName)}"%>--}%
            </div>  <!--fieldContainer-->
        </div>

        <div class="rowContainer">
            <div class="fieldContainer required fcInputText borderRed">
                <label for="paymentRef">
                    <g:message code="customerMaster.paymentRefference.label" default="Payment Refference"/>
                    <span class="required-indicator">*</span>
                </label>
                <g:textField name="paymentRef" required="" placeholder="${g.message(code: 'bv.undoReconciliation.InvoiceNo.label')}" value="${invoiceInfo.paymentRef}"></g:textField>
            </div>
        </div>
        <div class="rowContainer">
            <div class="fieldContainer required fcInputText">
                <label for="paymentRef">
                    <g:message code="bv.importInvoice.totalAmountWithOutVat.label" default="Total Amount Without Vat"/>
                    <span class="required-indicator">*</span>
                </label>
                <g:textField name="totalAmntWithOutVat" required="" value="${invoiceInfo.subtotal}" placeholder="${g.message(code: 'bv.importInvoice.totalAmountWithOutVat.label')}"></g:textField>
            </div>
        </div>

    </div>  <!--fieldContainerRight_2-->

    <div class="fieldContainerRight">
        <div class="rowContainer">
            <div class="fieldContainer required fcTextArea fcTextAreaSmall">
                <label>
                    <g:message code="customerGeneralAddress.contactPersonName.label" default="Contact Person Name"/>
                    <span class="required-indicator">*</span>
                </label>
                <g:textArea  name="debtorContactPerson" value="" placeholder="Contact Person Name"
                             title="${message(code: 'customerGeneralAddress.contactPersonName.label', default: 'Please Give Description')} " >${invoiceInfo.debtorContactPerson}</g:textArea>

            </div>
        </div>

        <div class="rowContainer">
            <div class="fieldContainer required fcInputText">
                <label for="iban">
                    <g:message code="companyBankAccounts.iban.label" default="Iban"/><b>:</b>
                    <span class="required-indicator">*</span>
                </label>
                <g:textField name="iban" placeholder="${g.message(code: 'customerBankAccount.ibanPrefix.NL27ABNA.label')}" value="${invoiceInfo.iban}"/>
            </div>
        </div>


        <div class="rowContainer">
            <div class="fieldContainer required fcInputText">
                <label for="paymentRef">
                    <g:message code="bv.pdf.totalVATLabel" default="Total Vat"/>
                    <span class="required-indicator">*</span>
                </label>
                <g:textField name="vat" required="" value="${invoiceInfo.vat}"  placeholder="${g.message(code: 'bv.pdf.totalVATLabel')}"></g:textField>
            </div>
        </div>


    </div>  <!--fieldContainerRight_3-->
</div>  <!--formInvoiceContainer-->


<div class="btnDivStyle btnDivPosition">
    <input type="button" class="greenBtn" name="update" value="Update" style="margin-right: 22px;" onclick="updateInvoice();"/>
</div>
%{--</g:form>--}%