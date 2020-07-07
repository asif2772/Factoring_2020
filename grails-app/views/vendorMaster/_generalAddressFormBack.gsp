<%@ page import="factoring.CoreParamsHelperTagLib; factoring.VendorGeneralAddress;" %>

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
        </div>  <!--codeFieldLeft-->

        <div class="codeFieldMiddle">
            <div class="codeContainer">
                <div class="fieldContainer required">
                    <label for="vendorName">
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
            </div>  <!--rowContainer-->
        </div>  <!--codeFieldRight-->

    </div>  <!--codeContainerSections-->
    <div id="tabPageLoadData2" class="upperContainer">
        <div class="spinLoader fieldContainer">
            <img src="${resource(dir:'images',file:'spinner.gif')}" alt="${message(code:'spinner.alt.Loading',default:'Loading...')}" />
        </div>
    </div>
    <div class="upperContainer">
        <div class="fieldContainerLeft">

            <div class="rowContainer">
                <div class="fieldContainer required fcInputText">
                    <label for="contactPersonName">
                        <g:message code="vendorGeneralAddress.contactPersonName.label" default="Contact Person Name" />
                        <span class="required-indicator">*</span>
                    </label>
                    <g:textField tabindex="1" placeholder="${g.message(code:'vendorGeneralAddress.contactPersonName.label')}" name="contactPersonName" value="${vendorGeneralAddressInstance?.contact_person_name}"/>
                </div>
            </div>

            <div class="rowContainer">
                <div class="fieldContainer fcInputText">
                    <label for="addressLine1">
                        <g:message code="vendorGeneralAddress.addressLine1.label" default="Address Line1" />
                    </label>
                    <g:textField tabindex="2" placeholder="${g.message(code:'vendorGeneralAddress.addressLine1.label')}" name="addressLine1" style="overflow: hidden;" value="${vendorGeneralAddressInstance?.address_line1}"/>
                </div>
            </div>

            <div class="rowContainer">
                <div class="fieldContainer required fcInputText">
                    <label for="postalCode">
                        <g:message code="vendorGeneralAddress.postalCode.label" default="Postal Code" />
                        <span class="required-indicator">*</span>
                    </label>
                    <g:textField tabindex="4" placeholder="${g.message(code:'vendorGeneralAddress.postalCode.label')}" name="postalCode" oninvalid="this.setCustomValidity('${message(code: 'vendorGeneralAddress.postalCode.blank',default:'Please give Postal Code' )}')" oninput="setCustomValidity('')" title="${message(code: 'vendorGeneralAddress.postalCode.blank',default:'Please give Postal Code' )}" value="${vendorGeneralAddressInstance?.postal_code}"/>
                </div>
            </div>

            <div class="rowContainer">
                <div class="fieldContainer fcInputText">
                    <label for="state">
                        <g:message code="vendorGeneralAddress.state.label" default="State" />
                    </label>
                    <g:textField tabindex="6" placeholder="${g.message(code:'vendorGeneralAddress.state.label')}" name="state" value="${vendorGeneralAddressInstance?.state}"/>
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
                        <g:message code="vendorGeneralAddress.addressLine2.label" default="Address Line2" />
                    </label>
                    <g:textField tabindex="3" placeholder="${g.message(code:'vendorGeneralAddress.addressLine2.label')}" name="addressLine2" style="overflow: hidden;" value="${vendorGeneralAddressInstance?.address_line2}"/>
                </div>
            </div>

           <div class="rowContainer">
                <div class="fieldContainer required fcInputText">
                    <label for="city">
                        <g:message code="vendorGeneralAddress.city.label" default="City" />
                        <span class="required-indicator">*</span>
                    </label>
                    <g:textField tabindex="5" placeholder="${g.message(code:'vendorGeneralAddress.city.label')}" name="city" value="${vendorGeneralAddressInstance?.city}"/>
                 </div>
            </div>

            <div class="rowContainer">
                <div class="fieldContainer required fcCombo">
                    <label for="country">
                        <g:message code="vendorGeneralAddress.country.label" default="Country" />
                        <span class="required-indicator">*</span>
                    </label>
                    <% def contryId = '2';
                        if(vendorGeneralAddressInstance?.country_id){
                            contryId = vendorGeneralAddressInstance?.country_id
                        }
                    %>
                    <%= "${new CoreParamsHelperTagLib().showCountryList("countryId","${contryId.toString()}")}" %>
                </div>
            </div>

        </div> <!--fieldContainerRight-->

        <div class="fieldContainerRight">

            <div class="rowContainer">
                <div class="emptyDiv"></div>
            </div>

            <div class="rowContainer">
                <div class="emptyDiv"></div>
            </div>
        </div>   <!--fieldContainerRight-->

    </div> <!--upperContainer-->
</div>   <!---main-container--->

<g:hiddenField name="vendor_id" value="${params.id}" />
<g:hiddenField name="id" value="${vendorGeneralAddressInstance?.id}" />

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