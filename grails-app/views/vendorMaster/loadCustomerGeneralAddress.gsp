<%@ page import="factoring.CoreParamsHelperTagLib; factoring.VendorGeneralAddress" %>

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
<div style="clear: both;"></div>
<fieldset class="buttons_new updateLinkBtn">
    <% if (!vendorGeneralAddressInstance?.id) { %>
    <g:submitButton name="create" class="save updateBtn 2" value="${message(code: 'default.button.create.label', default: 'Create')}"/>
    <% } else { %>
    <g:submitButton name="create" class="update updateBtn" value="${message(code: 'default.button.update.label', default: 'Update')}"/>
    <% } %>
</fieldset>
<g:hiddenField name="vendor_id" value="${customerId}" />
<g:hiddenField name="id" value="${vendorGeneralAddressInstance?.id}" />

