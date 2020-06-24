<%@ page import="factoring.CoreParamsHelperTagLib; factoring.CustomerGeneralAddress" %>
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

<g:hiddenField name="customer_id" value="${customerId}" />
<g:hiddenField name="id" value="${customerGeneralAddressInstance?.id}" />
<fieldset class="tabFieldset buttons_new updateLinkBtn" style="margin-bottom: 13px;">  <!--buttons_new updateLinkBtn-->
    <div class="btnDivStyle tabFieldsetDiv">
        %{--<g:hiddenField name="budgetCustomerId" value="${params.budgetCustomerId}"/>--}%
        <% if (!customerGeneralAddressInstance?.id) { %>
        <g:submitButton name="create" class="greenBtn tabFieldsetPosition" value="${message(code: 'default.button.create.label', default: 'Create')}"/>
        <%} else { %>
        <g:submitButton name="create" class="greenBtn tabFieldsetPosition" value="${message(code: 'default.button.update.label', default: 'Update')}"/>
        <% } %>
    </div>
</fieldset>