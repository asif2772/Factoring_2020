<%@ page import="factoring.CoreParamsHelperTagLib; factoring.CustomerPostalAddress;" %>


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

                    <p class="labelCode">${customerPrefix +"-"+ customerMasterInstance?.customer_code}</p>
                </div>
            </div>  <!--codeContainer-->
        </div>  <!--fieldContainerLeft-->

        <div class="codeFieldMiddle">
            <div class="codeContainer">
                <div class="fieldContainer required">
                    <label for="customerName">
                        <g:message code="debtorMaster.debtorName.label" default="Debtor"/><b>:</b>
                        <span class="required-indicator">*</span>
                    </label>
                    %{--<g:message code="customerMaster.lastName.label" default="Last Name"/>--}%
                    <p class="labelCode">${customerMasterInstance?.customer_name}</p>

                    %{--<p class="labelCode">${customerPrefix + customerMasterInstance?.customer_code}</p>--}%
                </div>
            </div>  <!--codeContainer-->
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
            </div>  <!--codeContainer-->
        </div>  <!--codeFieldRight-->

    </div>  <!--codeContainerSections-->

<div class="upperContainer">
    <div class="fieldContainerLeft">

        <div class="rowContainer">
            <div class="fieldContainer fcInputText">
                <label for="postalContactPersonName">
                    <g:message code="customerPostalAddress.postalContactPersonName.label" default="Postal Contact Name" /><b>:</b>
                </label>
                <g:textField tabindex="1" placeholder="${g.message(code:'customerPostalAddress.postalContactPersonName.label')}" name="postalContactPersonName" value="${customerPostalAddressInstance?.postal_contact_person_name}"/>
            </div>
        </div>

        <div class="rowContainer">
            <div class="fieldContainer fcInputText">
                <label for="postalAddressLine1">
                    <g:message code="customerPostalAddress.postalAddressLine1.label" default="First Postal Address" /><b>:</b>
                </label>
                <g:textField tabindex="2" placeholder="${g.message(code:'customerPostalAddress.postalAddressLine1.label')}" name="postalAddressLine1" style="overflow: hidden;" value="${customerPostalAddressInstance?.postal_address_line1}"/>
            </div>
        </div>

        <div class="rowContainer">
            <div class="fieldContainer fcInputText">
                <label for="postalPostcode">
                    <g:message code="customerPostalAddress.postalPostcode.label" default="Postal Postcode" /><b>:</b>
                </label>
                <g:textField tabindex="4" placeholder="${g.message(code:'customerPostalAddress.postalPostcode.label')}" name="postalPostcode" value="${customerPostalAddressInstance?.postal_postcode}"/>
            </div>
        </div>

        <div class="rowContainer">
            <div class="fieldContainer fcInputText">
                <label for="postalState">
                    <g:message code="customerPostalAddress.postalState.label" default="Postal State" /><b>:</b>
                </label>
                <g:textField tabindex="6" placeholder="${g.message(code:'customerPostalAddress.postalState.label')}" name="postalState" value="${customerPostalAddressInstance?.postal_state}"/>
            </div>
        </div>

    </div>  <!--fieldContainerLeft-->


    <div class="fieldContainerRight">

        <div class="rowContainer">
            <div class="emptyDiv"></div>
        </div>

        <div class="rowContainer">
            <div class="fieldContainer fcInputText">
                <label for="postalAddressLine2">
                    <g:message code="customerPostalAddress.postalAddressLine2.label" default="Postal Address Line2" /><b>:</b>
                </label>
                <g:textField tabindex="3" placeholder="${g.message(code:'customerPostalAddress.postalAddressLine2.label')}" name="postalAddressLine2" style="overflow: hidden;"  value="${customerPostalAddressInstance?.postal_address_line2}"/>
            </div>
        </div>

            <div class="rowContainer">
                <div class="fieldContainer fcInputText">
                    <label for="postalCity">
                        <g:message code="customerPostalAddress.postalCity.label" default="Postal City" /><b>:</b>
                    </label>
                    <g:textField tabindex="5" placeholder="${g.message(code:'customerPostalAddress.postalCity.label')}" name="postalCity" value="${customerPostalAddressInstance?.postal_city}"/>
                </div>
            </div>

            <div class="rowContainer">
                <div class="fieldContainer required fcCombo">
                    <label for="postalCountry">
                        <g:message code="customerPostalAddress.postalCountry.label" default="Postal Country" /><b>:</b>
                        <span class="required-indicator">*</span>
                    </label>
                    <% def contryId = '2';
                    if(customerPostalAddressInstance?.postal_country_id){
                        contryId = customerPostalAddressInstance?.postal_country_id
                    }
                    %>
                    <%="${new CoreParamsHelperTagLib().showCountryList("postalCountryId","${contryId}")}"%>
                </div>
            </div>

    </div> <!--fieldContainerRight-->

    </div>  <!---upperContainer--->

</div>   <!---main-container--->


<g:hiddenField name="customer_id" value="${params.id}" />
<g:hiddenField name="id" value="${customerPostalAddressInstance?.id}" />

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
