<%@ page import="factoring.CoreParamsHelperTagLib;factoring.CustomerBankAccount;" %>

<div class="main-container">

    <div class="codeContainerSections">
        %{--<div class="fcCodeNumber">--}%
        <div class="codeFieldLeft">
            <div class="codeContainer">
                <div class="fieldContainer required">
                    <label for="customerCode">
                        <g:message code="customerMaster.customerCode.label" default="Customer Code"/><b>:</b>
                        <span class="required-indicator">*</span>
                    </label>

                    <p class="labelCode">${customerPrefix +"-"+  customerMasterInstance?.customer_code}</p>
                </div>
            </div>  <!--rowContainer-->
        </div>  <!--codeFieldLeft-->

        <div class="codeFieldMiddle">
            <div class="codeContainer">
                <div class="fieldContainer required">
                    <label for="customerName">
                        <g:message code="debtorMaster.debtorName.label" default="Debtor Name"/><b>:</b>
                        <span class="required-indicator">*</span>
                    </label>
                    %{--<g:message code="customerMaster.lastName.label" default="Last Name"/>--}%
                    <p class="labelCode">${customerMasterInstance?.customer_name}</p>

                    %{--<p class="labelCode">${customerPrefix + customerMasterInstance?.customer_code}</p>--}%
                </div>
            </div>  <!--rowContainer-->
        </div>  <!--codeFieldMiddle-->

        <div class="codeFieldRight">
            <div class="codeContainer">
                <div class="fieldContainer required">
                    %{--<label for="companyName">--}%
                        %{--<g:message code="customerMaster.companyName.label" default="Company Name"/><b>:</b>--}%
                        %{--<span class="required-indicator">*</span>--}%
                    %{--</label>--}%
                    %{--<p class="labelCode">${customerMasterInstance?.company_name}</p>--}%
                </div>
            </div>  <!--rowContainer-->
        </div>  <!--codeFieldRight-->

    </div>  <!--codeContainerSections-->


    <div class="singleContainer" style="margin-top: 5px;">
        <div class="fieldContainerLeft">

            <div class="rowContainer">
                <div class="fieldContainer required fcInputText">
                    <label for="bankAccountName">
                        <g:message code="customerBankAccount.bankAccountName.label" default="Bank Account Name"/><b>:</b>
                    </label>
                    <g:textField tabindex="1" placeholder="${g.message(code:'customerBankAccount.bankAccountName.label')}" name="bankAccountName" required="" value="${customerEditBankAccountInstance?.bank_account_name}"/>
                </div>
            </div>

            <% if(params.bankid) { %>
            <div class="rowContainer">
                <div class="fieldContainer required fcCombo">
                    <label for="">
                        <g:message code="companyBankAccounts.status.label" default="Bank Account Status"/><b>:</b>
                    </label>
                    <bv:statusDropDown name="status" selectionVal="${customerEditBankAccountInstance?.status}"  allowEmpty="false"/>
                </div>
            </div>
            <% } else { %>
            <% } %>


        </div>  <!--fieldContainerLeft-->


        <div class="fieldContainerRight">

            <div class="rowContainer">
                <div class="fieldContainer fcInputText borderRed">
                    <label for="ibanPrefix">
                        <g:message code="customerBankAccount.ibanPrefix.label" default="Iban Prefix"/><b>:</b>
                    </label>
                    <g:textField tabindex="2" placeholder="${g.message(code:'customerBankAccount.ibanPrefix.NL27ABNA.label')}" name="ibanPrefix" value="${customerEditBankAccountInstance?.iban_prefix}"/>
                </div>
            </div>
        </div> <!--fieldContainerRight-->

        <div class="fieldContainerRight">
            <div class="rowContainer">
                <div class="fieldContainer fcInputText">
                    <label for="bankAccountNo" style="width:150px">
                        <g:message code="customerBankAccount.bankAccountNo.label" default="Bank Account No"/><b>:</b>
                    </label>
                    <g:textField tabindex="3" placeholder="${g.message(code:'customerBankAccount.bankAccountNo.label')}" name="bankAccountNo" required="" value="${customerEditBankAccountInstance?.bank_account_no}"/>
                </div>
            </div>
        </div>

    </div>
</div>   <!---main-container--->



%{--START ---15/09/14--}%
%{--<div class="fieldcontain ${hasErrors(bean: customerEditBankAccountInstance, field: 'bankAccountName', 'error')} " style="width:50%">--}%
    %{--<label for="bankAccountName" style="width:150px">--}%
        %{--<g:message code="customerBankAccount.bankAccountName.label" default="Bank Account Name" />--}%
    %{--</label>--}%
    %{--<g:textField name="bankAccountName" maxlength="100"  value="${customerEditBankAccountInstance?.bank_account_name}"/>--}%
%{--</div>--}%

%{--<div class="fieldcontain ${hasErrors(bean: customerEditBankAccountInstance, field: 'ibanPrefix', 'error')} " style="width:50%">--}%
    %{--<label for="ibanPrefix">--}%
        %{--<g:message code="customerBankAccount.ibanPrefix.label" default="Iban Prefix" />--}%
    %{--</label>--}%
    %{--<g:textField name="ibanPrefix" maxlength="50"  value="${customerEditBankAccountInstance?.iban_prefix}"/>--}%
%{--</div>--}%

%{--<div class="fieldcontain ${hasErrors(bean: customerEditBankAccountInstance, field: 'bankAccountNo', 'error')} " style="width:50%">--}%
    %{--<label for="bankAccountNo" style="width:150px">--}%
        %{--<g:message code="customerBankAccount.bankAccountNo.label" default="Bank Account No" />--}%
    %{--</label>--}%
    %{--<g:textField name="bankAccountNo" maxlength="50" value="${customerEditBankAccountInstance?.bank_account_no}"/>--}%
%{--</div>--}%

%{--<div class="fieldcontain ${hasErrors(bean: customerEditBankAccountInstance, field: 'status', 'error')} " style="width:50%">--}%
    %{--<label for="status">--}%
        %{--<g:message code="customerBankAccount.status.label" default="Status" />--}%
    %{--</label>--}%
    %{--<%= "${new CoreParamsHelperTagLib().StatusDropDown("status","${customerEditBankAccountInstance?.status}",false)}" %>--}%
%{--</div>--}%
%{--end 15/09/14--}%
%{--#################################################--}%

<g:hiddenField name="customer_id" value="${params.id}" />
<g:hiddenField name="id" value="${customerEditBankAccountInstance?.id}" />

<% if(params.fInv){ %>
<g:hiddenField name="bookInvoiceId" value="${params.bookInvoiceId}" />
<g:hiddenField name="bookingPeriod" value="${params.bookingPeriod}" />
<g:hiddenField name="customerId" value="${params.customerId}" />
<g:hiddenField name="budgetItemDetailsId" value="${params.budgetItemDetailsId}" />
<g:hiddenField name="fInv" value="${params.fInv}" />
<% }%>

<% if(params.incBItem){ %>
<g:hiddenField name="bookingPeriod" value="${params.bookingPeriod}" />
<g:hiddenField name="customerId"    value="${params.customerId}" />
<g:hiddenField name="journalId"     value="${params.journalId}" />
<g:hiddenField name="incBItem"      value="${params.incBItem}" />
<% }%>

