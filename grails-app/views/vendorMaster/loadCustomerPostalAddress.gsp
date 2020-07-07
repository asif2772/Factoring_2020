<%@ page import="factoring.CoreParamsHelperTagLib; factoring.VendorPostalAddress" %>

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

<g:hiddenField name="vendor_id" value="${customerId}" />
<g:hiddenField name="id" value="${vendorPostalAddressInstance?.id}" />

<div style="clear: both;"></div>
<fieldset class="buttons_new updateLinkBtn">
    <% if (!vendorPostalAddressInstance?.id) { %>
    <g:submitButton name="create" class="save updateBtn"  value="${message(code: 'default.button.create.label', default: 'Create')}"/>
    <% }else { %>
    <g:submitButton name="create" class="update save updateBtn" value="${message(code: 'default.button.update.label', default: 'Update')}"/>
    <% } %>
</fieldset>

