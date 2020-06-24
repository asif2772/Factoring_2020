<%@ page import="factoring.CoreParamsHelperTagLib; factoring.CustomerGeneralAddress" %>

<div class="main-container">

    <div class="codeContainerSections">
       <div class="codeFieldLeft">
            <div class="codeContainer">
                <div class="fieldContainer required">
                    <label for="customerCode">
                        <g:message code="customerMaster.customerCode.label" default="Customer Code"/><b>:</b>
                        <span class="required-indicator">*</span>
                    </label>

                    <p class="labelCode">${customerPrefix + "-" + customerMasterInstance?.customer_code}</p>
                </div>
            </div>  <!--codeContainer-->
        </div>  <!--codeFieldLeft-->

        <div class="codeFieldMiddle">
            <div class="codeContainer">
                <div class="fieldContainer required">

                    <label for="customerName">
                        <g:message code="debtorMaster.debtorName.label" default="Debtor Name"/><b>:</b>
                        <span class="required-indicator">*</span>
                    </label>
                    <p class="labelCode">${customerMasterInstance?.customer_name}</p>
                </div>
            </div>  <!--codeContainer-->
        </div>  <!--codeFieldMiddle-->

        <div class="codeFieldRight">
            <div class="codeContainer">
                <div class="emptyDiv"></div>
            </div>
        </div>
    </div>  <!--codeContainerSections-->

   <div class="upperContainer">
      <div class="fieldContainerLeft">

        <div class="rowContainer">
            <div class="fieldContainer required fcInputText">
                <label for="contactPersonName">
                    <g:message code="customerGeneralAddress.contactPersonName.label" default="Contact Person Name" /><b>:</b>
                    <span class="required-indicator">*</span>
                </label>
                <g:textField tabindex="1" placeholder="${g.message(code:'customerGeneralAddress.contactPersonName.label')}" name="contactPersonName" value="${customerGeneralAddressInstance?.contact_person_name}"/>
            </div>
        </div>

        <div class="rowContainer">
            <div class="fieldContainer fcInputText">
                <label for="addressLine1">
                    <g:message code="customerGeneralAddress.addressLine1.label" default="Address Line1" /><b>:</b>
                </label>
                <g:textField tabindex="2" placeholder="${g.message(code: 'customerGeneralAddress.addressLine1.label')}" name="addressLine1" style="overflow: hidden;" value="${customerGeneralAddressInstance?.address_line1}"/>
            </div>
        </div>

       <div class="rowContainer">
            <div class="fieldContainer fcInputText">
                <label>
                    <g:message code="customerGeneralAddress.postalCode.label" default="Postal Code" /><b>:</b>
                </label>
                <g:textField tabindex="4" placeholder="${g.message(code: 'customerGeneralAddress.postalCode.label')}" name="postalCode" value="${customerGeneralAddressInstance?.postal_code}"/>
            </div>
        </div>

        <div class="rowContainer">
            <div class="fieldContainer fcInputText">
                <label for="state">
                    <g:message code="customerGeneralAddress.state.label" default="State" /><b>:</b>
                </label>
                <g:textField tabindex="6" placeholder="${g.message(code: 'customerGeneralAddress.state.label')}" name="state" value="${customerGeneralAddressInstance?.state}"/>
            </div>
        </div>

    </div>  <!--fieldContainerLeft-->

    <div class="fieldContainerRight">

     <div class="rowContainer">
        <div class="emptyDiv"></div>
     </div>

        <div class="rowContainer">
            <div class="fieldContainer fcInputText">
                <label for="addressLine2">
                    <g:message code="customerGeneralAddress.addressLine2.label" default="Address Line2" /><b>:</b>
                </label>
                <g:textField tabindex="3" placeholder="${g.message(code: 'customerGeneralAddress.addressLine2.label')}" name="addressLine2" style="overflow: hidden;" value="${customerGeneralAddressInstance?.address_line2}"/>
            </div>
        </div>

        <div class="rowContainer">
            <div class="fieldContainer fcInputText">
                <label for="city">
                    <g:message code="customerGeneralAddress.city.label" default="City" /><b>:</b>
                    <span class="required-indicator">*</span>
                </label>
                <g:textField tabindex="5" placeholder="${g.message(code: 'customerGeneralAddress.city.label')}" name="city" value="${customerGeneralAddressInstance?.city}"/>
            </div>
        </div>

        <div class="rowContainer">
            <div class="fieldContainer required fcCombo">
                <label for="country">
                    <g:message code="customerGeneralAddress.country.label" default="Country" /><b>:</b>
                    <span class="required-indicator">*</span>
                </label>
                <% def contryId = '2';
                if(customerGeneralAddressInstance?.country_id){
                    contryId = customerGeneralAddressInstance?.country_id
                }
                %>
                <%= "${new CoreParamsHelperTagLib().showCountryList("countryId","${contryId.toString()}")}" %>
            </div>
        </div>

        </div><!--fieldContainerRight-->
    </div> <!--upperContainer-->
</div>   <!---main-container--->

<g:hiddenField name="customer_id" value="${params.id}" />
<g:hiddenField name="id" value="${customerGeneralAddressInstance?.id}" />

<% if(params.fInv){ %>
<g:hiddenField name="bookInvoiceId" value="${params.bookInvoiceId}" />
<g:hiddenField name="bookingPeriod" value="${params.bookingPeriod}" />
<g:hiddenField name="customerId" value="${params.customerId}" />
<g:hiddenField name="budgetItemDetailsId" value="${params.budgetItemDetailsId}" />
<g:hiddenField name="fInv" value="${params.fInv}" />
<% }%>

<% if(params.incBItem){ %>
<g:hiddenField name="bookingPeriod" value="${params.bookingPeriod}" />
<g:hiddenField name="customerId" value="${params.customerId}" />
<g:hiddenField name="journalId" value="${params.journalId}" />
<g:hiddenField name="incBItem" value="${params.incBItem}" />
<% }%>




