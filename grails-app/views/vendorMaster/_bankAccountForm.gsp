<%@ page import="factoring.CoreParamsHelperTagLib; factoring.VendorBankAccount;" %>

<fieldset class="form">
    <div class="main-container">

        <div class="codeContainerSections">
            <div class="codeFieldLeft">
                <div class="codeContainer">
                    <div class="fieldContainer required">
                        <label for="vendorCode">
                            <g:message code="vendorMaster.vendorCode.label" default="Vendor Code"/><b>:</b>
                            <span class="required-indicator">*</span>
                        </label>
                        <p class="labelCode">${vendorPrefix + "-" + vendorMasterInstance?.vendor_code}</p>
                    </div>
                </div>  <!--codeContainer-->
            </div>  <!--codeFieldLeft-->

            <div class="codeFieldMiddle">
                <div class="codeContainer">
                    <div class="fieldContainer required">
                        <label for="vendorName">
                    <g:message code="vendorMaster.vendorName.label" default="Vendor Name"/><b>:</b>
                        <span class="required-indicator">*</span>
                    </label>
                        <p class="labelCode">${vendorMasterInstance?.vendor_name}</p>
                    </div>
                </div>  <!--rowContainer-->
            </div>  <!--codeFieldMiddle-->

            <div class="codeFieldRight">
                <div class="codeContainer">
                    <div class="emptyDiv"></div>
                </div>  <!--rowContainer-->
            </div>  <!--codeFieldRight-->

        </div>  <!--codeContainerSections-->


        <div class="singleContainer">
            <div class="fieldContainerLeft">

                <div class="rowContainer">
                    <div class="fieldContainer fcInputText">
                        <label for="bankAccountName">
                            <g:message code="vendorBankAccount.bankAccountName.label" default="Bank Account Name" /><b>:</b>
                            %{--<span class="required-indicator">*</span>--}%
                        </label>
                        <g:textField tabindex="1" placeholder="${g.message(code:'vendorBankAccount.bankAccountName.label')}" name="bankAccountName" required="" oninvalid="this.setCustomValidity('${message(code: 'vendorBankAccount.bankAccountName.blank',default:'Please give Bank Account Name' )}')" oninput="setCustomValidity('')" title="${message(code: 'vendorBankAccount.bankAccountName.blank',default:'Please give Bank Account Name' )}" value="${vendorEditBankAccountInstance?.bank_account_name}"/>
                    </div>
                </div>

                <% if(params.bankid) { %>
                <div class="rowContainer">
                    <div class="fieldContainer required fcCombo">
                        <label for="">
                            <g:message code="companyBankAccounts.status.label" default="Bank Account Status"/><b>:</b>
                        </label>
                        <bv:statusDropDown name="status" selectionVal="${vendorBankAccountInstance?.status}"  allowEmpty="false"/>
                    </div>
                </div>
                <% } else { %>
                <% } %>

            </div>  <!--fieldContainerLeft-->


            <div class="fieldContainerRight">

                <div class="rowContainer">
                    <div class="fieldContainer fcInputText borderRed">
                        <label for="ibanPrefix">
                            <g:message code="vendorBankAccount.ibanPrefix.label" default="Iban Prefix" /><b>:</b>
                        </label>
                        <g:textField tabindex="2" placeholder="${g.message(code:'customerBankAccount.ibanPrefix.NL27ABNA.label')}"
                                     name="ibanPrefix" oninvalid="this.setCustomValidity('${message(code: 'vendorBankAccount.ibanPrefix.blank',default:'Please give Iban Prefix' )}')"
                                     oninput="setCustomValidity('')" title="${message(code: 'vendorBankAccount.ibanPrefix.blank',default:'Please give Iban Prefix' )}"
                                     value="${vendorEditBankAccountInstance?.iban_prefix}"/>
                     </div>
                </div>

            </div> <!--fieldContainerRight-->

            <div class="fieldContainerRight">
                <div class="rowContainer">
                    <div class="fieldContainer fcInputText">
                        <label for="bankAccountNo">
                            <g:message code="vendorBankAccount.bankAccountNo.label" default="Bank Account No" /><b>:</b>
                            %{--<span class="required-indicator">*</span>--}%
                        </label>
                        <g:textField tabindex="3" placeholder="${g.message(code:'vendorBankAccount.bankAccountNo.label')}"
                                     name="bankAccountNo" required="" oninvalid="this.setCustomValidity('${message(code: 'vendorBankAccount.bankAccountNo.blank',default:'Please give Bank Account No' )}')"
                                     oninput="setCustomValidity('')" title="${message(code: 'vendorBankAccount.bankAccountNo.blank',default:'Please give Bank Account No' )}"
                                     value="${vendorEditBankAccountInstance?.bank_account_no}"/>
                    </div>
                </div>
            </div>  <!--fieldContainerRight-->

        </div>  <!---singleContainer-->
    </div>   <!---main-container--->
</fieldset>
<sec:ifNotGranted roles="ROLE_CUSTOMER">
    <fieldset class="buttons_new updateLinkBtn">
        <% if (!params?.bankid) { %>
        <g:actionSubmit name="create" class="save updateBtn 2" controller ="vendorMaster" action="updatebanking" value="${message(code: 'default.button.create.label', default: 'Create')}"/>
        <% } else { %>
        <g:actionSubmit name="create" class="update updateBtn 1" controller ="vendorMaster" action="updatebanking" value="${message(code: 'default.button.update.label', default: 'Update')}"/>

        <% } %>
    </fieldset>
</sec:ifNotGranted>
<sec:ifNotGranted roles="ROLE_ADMIN,ROLE_ACCOUNTANT,ROLE_USER">
    <fieldset class="buttons_new updateLinkBtn">
        <g:actionSubmit class="orangeBtn" style="height: 30px; margin-right: 12px" controller ="vendorMaster" action="changeBankAccountInformation" value="${message(code: 'bv.change.request.customer', default: 'Change')}"/>
    </fieldset>
</sec:ifNotGranted>

<g:hiddenField name="vendor_id" value="${params.id}" />
<g:hiddenField name="id" value="${vendorEditBankAccountInstance?.id}" />
<% if(params.fInv){ %>
<g:hiddenField name="bookInvoiceId" value="${params.bookInvoiceId}" />
<g:hiddenField name="bookingPeriod" value="${params.bookingPeriod}" />
<g:hiddenField name="vendorId" value="${params.vendorId}" />
<g:hiddenField name="budgetItemDetailsId" value="${params.budgetItemDetailsId}" />
<g:hiddenField name="fInv" value="${params.fInv}" />
<% }%>

<% if(params.sid){ %>
<g:hiddenField name="bookInvoiceId" value="${params.bookInvoiceId}" />
<g:hiddenField name="bookingPeriod" value="${params.bookingPeriod}" />
<g:hiddenField name="vendorId" value="${params.vendorId}" />
<g:hiddenField name="budgetItemDetailsId" value="${params.budgetItemDetailsId}" />
<g:hiddenField name="fInv" value="${params.sid}" />
<% }%>

<% if(params.expBItem){ %>
<g:hiddenField name="bookingPeriod" value="${params.bookingPeriod}" />
<g:hiddenField name="vendorId" value="${params.vendorId}" />
<g:hiddenField name="journalId" value="${params.journalId}" />
<g:hiddenField name="expBItem" value="${params.expBItem}" />
<% }%>