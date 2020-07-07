<%@ page import="factoring.CoreParamsHelperTagLib; factoring.VendorPostalAddress;" %>

<div class="main-container">
    <div class="codeContainerSections">
        <div class="codeFieldLeft">
            <div class="codeContainer">
                <div class="fieldContainer required">
                    <label for="vendorCode">
                        <g:message code="vendorMaster.vendorCode.label" default="Vendor Code"/>
                        <span class="required-indicator">*</span>
                    </label>
                    <p class="labelCode">${vendorPrefix + "-" + vendorMasterInstance?.vendor_code}</p>
                </div>
            </div>  <!--codeContainer-->
        </div>  <!--fieldContainerLeft-->

        <div class="codeFieldMiddle">
            <div class="codeContainer">
                <div class="fieldContainer required">
                    <label for="vendorName"">
                <g:message code="vendorMaster.vendorName.label" default="Vendor Name"/>
                    <span class="required-indicator">*</span>
                </label>
                    <p class="labelCode">${vendorMasterInstance?.vendor_name}</p>
                </div>
            </div>  <!--codeContainer-->
        </div>  <!--codeFieldMiddle-->

        <div class="codeFieldRight">
            <div class="codeContainer">
                <div class="emptyDiv"></div>
            </div>  <!--codeContainer-->
        </div>  <!--codeFieldRight-->

    </div>  <!--codeContainerSections-->

    <div id="tabPageLoadData3" class="upperContainer">
        <div class="spinLoader fieldContainer">
            <img src="${resource(dir:'images',file:'spinner.gif')}" alt="${message(code:'spinner.alt.Loading',default:'Loading...')}" />
        </div>
    </div>

    <div class="upperContainer">
        <div class="fieldContainerLeft">

            <div class="rowContainer">
                <div class="fieldContainer fcInputText">
                    <label for="postalContactPersonName">
                        <g:message code="vendorPostalAddress.postalContactPersonName.label" default="Contact Person Name" />
                    </label>
                    <g:textField tabindex="1" placeholder="${g.message(code:'vendorPostalAddress.postalContactPersonName.label')}" name="postalContactPersonName" value="${vendorPostalAddressInstance?.postal_contact_person_name}"/>
                </div>
            </div>

            <div class="rowContainer">
                <div class="fieldContainer fcInputText">
                    <label for="postalAddressLine1">
                        <g:message code="vendorPostalAddress.postalAddressLine1.label" default="Postal Address Line1" />
                    </label>
                    <g:textField tabindex="2" placeholder="${g.message(code:'vendorPostalAddress.postalAddressLine1.label')}" name="postalAddressLine1" style="overflow: hidden;" value="${vendorPostalAddressInstance?.postal_address_line1}"/>
                </div>
            </div>

            <div class="rowContainer">
                <div class="fieldContainer fcInputText">
                    <label for="postalPostcode">
                        <g:message code="vendorPostalAddress.postalPostcode.label" default="Postal Postcode" />
                    </label>
                    <g:textField tabindex="4" placeholder="${g.message(code:'vendorPostalAddress.postalPostcode.label')}" name="postalPostcode" value="${vendorPostalAddressInstance?.postal_postcode}"/>
                </div>
            </div>

            <div class="rowContainer">
                <div class="fieldContainer fcInputText">
                    <label for="postalState">
                        <g:message code="vendorPostalAddress.postalState.label" default="Postal State" />
                    </label>
                    <g:textField tabindex="6" placeholder="${g.message(code:'vendorPostalAddress.postalState.label')}" name="postalState" value="${vendorPostalAddressInstance?.postal_state}"/>
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
                        <g:message code="vendorPostalAddress.postalAddressLine2.label" default="Postal Address Line2" />
                    </label>
                    <g:textField tabindex="3" placeholder="${g.message(code:'vendorPostalAddress.postalAddressLine2.label')}" name="postalAddressLine2" style="overflow: hidden;" value="${vendorPostalAddressInstance?.postal_address_line2}"/>
                 </div>
            </div>

            <div class="rowContainer">
                <div class="fieldContainer fcInputText">
                    <label for="postalCity">
                        <g:message code="vendorPostalAddress.postalCity.label" default="Postal City" />
                    </label>
                    <g:textField tabindex="5" placeholder="${g.message(code:'vendorPostalAddress.postalCity.label')}" name="postalCity" value="${vendorPostalAddressInstance?.postal_city}"/>
                </div>
            </div>

            <div class="rowContainer">
                <div class="fieldContainer required fcCombo">
                    <label for="postalCountry">
                        <g:message code="vendorPostalAddress.postalCountry.label" default="Postal Country" />
                        <span class="required-indicator">*</span>
                    </label>
                    <% def contryId = '2';
                    if(vendorPostalAddressInstance?.postal_country_id){
                        contryId = vendorPostalAddressInstance?.postal_country_id
                    }
                    %>
                    <%= "${new CoreParamsHelperTagLib().showCountryList("postalCountry.id","${contryId}")}" %>
                </div>
            </div>
        </div> <!--fieldContainerRight-->
    </div>  <!---upperContainer--->
</div>   <!---main-container--->


<g:hiddenField name="vendor_id" value="${params.id}" />
<g:hiddenField name="id" value="${vendorPostalAddressInstance?.id}" />
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
    <g:hiddenField name="sid" value="${params.sid}" />
<% }%>

<% if(params.expBItem){ %>
    <g:hiddenField name="bookingPeriod" value="${params.bookingPeriod}" />
    <g:hiddenField name="vendorId" value="${params.vendorId}" />
    <g:hiddenField name="journalId" value="${params.journalId}" />
    <g:hiddenField name="expBItem" value="${params.expBItem}" />
<% }%>