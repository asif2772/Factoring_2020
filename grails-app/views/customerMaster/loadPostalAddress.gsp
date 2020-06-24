<%@ page import="factoring.CoreParamsHelperTagLib; factoring.CustomerPostalAddress;" %>

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

<g:hiddenField name="customer_id" value="${customerId}" />
<g:hiddenField name="id" value="${customerPostalAddressInstance?.id}" />

<fieldset class="tabFieldset buttons_new updateLinkBtn">
    <div class="btnDivStyle tabFieldsetDiv">
        <% if (!customerPostalAddressInstance?.id) { %>
        <g:submitButton name="create" class="greenBtn tabFieldsetPosition" value="${message(code: 'default.button.create.label', default: 'Create')}"/>
        <%} else { %>
        <g:submitButton name="create" class="greenBtn tabFieldsetPosition"  params="[fInv: params.fInv, incBItem: params.incBItem]" value="${message(code: 'default.button.update.label', default: 'Update')}"/>
        <% } %>
    </div>
</fieldset>
