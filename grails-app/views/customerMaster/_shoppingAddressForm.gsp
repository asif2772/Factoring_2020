<%@ page import="bv.CoreParamsHelperTagLib; bv.CustomerShipmentAddress" %>

<div class="fieldcontain ${hasErrors(bean: customerShipmentAddressInstance, field: 'shipContactName', 'error')} required">
    <label for="shipContactName">
        <g:message code="customerShipmentAddress.shipContactName.label" default="Contact Name" />
        <span class="required-indicator">*</span>
    </label>
    <g:textField name="shipContactName" maxlength="255" required="" value="${customerShipmentAddressInstance?.ship_contact_name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: customerShipmentAddressInstance, field: 'shipPostCode', 'error')} ">
    <label for="shipPostCode">
        <g:message code="customerShipmentAddress.shipPostCode.label" default="Ship Post Code" />

    </label>
    <g:textField name="shipPostCode" maxlength="50" value="${customerShipmentAddressInstance?.ship_post_code}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: customerShipmentAddressInstance, field: 'shipAddLine1', 'error')} ">
    <label for="shipAddLine1">
        <g:message code="customerShipmentAddress.shipAddLine1.label" default="Shipping Address Line1" />
    </label>
    <g:textArea onkeyup="autoresize(this)"  name="shipAddLine1" cols="40" rows="2" maxlength="255" style=" width:150px; overflow: hidden;" value="${customerShipmentAddressInstance?.ship_add_line1}"/>
   %{-- <g:textField name="shipAddLine1" maxlength="255"  required="" value="${customerShipmentAddressInstance?.shipAddLine1}"/>--}%
</div>

<div class="fieldcontain ${hasErrors(bean: customerShipmentAddressInstance, field: 'shipAddLine2', 'error')} ">
    <label for="shipAddLine2">
        <g:message code="customerShipmentAddress.shipAddLine2.label" default="Shipping Address Line2" />

    </label>
    <g:textArea onkeyup="autoresize(this)"  name="shipAddLine2" cols="40" rows="2" maxlength="255" style=" width:150px; overflow: hidden;" value="${customerShipmentAddressInstance?.ship_add_line2}"/>
    %{--<g:textField name="shipAddLine2" maxlength="255" value="${customerShipmentAddressInstance?.shipAddLine2}"/>--}%
</div>

<div class="fieldcontain ${hasErrors(bean: customerShipmentAddressInstance, field: 'shipPhoneNo1', 'error')} ">
    <label for="shipPhoneNo1">
        <g:message code="customerShipmentAddress.shipPhoneNo1.label" default="Ship Phone No1" />

    </label>
    <g:textField name="shipPhoneNo1" maxlength="100" value="${customerShipmentAddressInstance?.ship_phone_no1}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: customerShipmentAddressInstance, field: 'shipPhoneNo2', 'error')} ">
    <label for="shipPhoneNo2">
        <g:message code="customerShipmentAddress.shipPhoneNo2.label" default="Ship Phone No2" />

    </label>
    <g:textField name="shipPhoneNo2" maxlength="50" value="${customerShipmentAddressInstance?.ship_phone_no2}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: customerShipmentAddressInstance, field: 'shipFax', 'error')} ">
    <label for="shipFax">
        <g:message code="customerShipmentAddress.shipFax.label" default="Ship Fax" />

    </label>
    <g:textField name="shipFax" maxlength="50" value="${customerShipmentAddressInstance?.ship_fax}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: customerShipmentAddressInstance, field: 'shipEmail', 'error')} ">
    <label for="shipEmail">
        <g:message code="customerShipmentAddress.shipEmail.label" default="Ship Email" />

    </label>
    <g:textField name="shipEmail" maxlength="50" value="${customerShipmentAddressInstance?.ship_email}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: customerShipmentAddressInstance, field: 'shipWebsite', 'error')} ">
    <label for="shipWebsite">
        <g:message code="customerShipmentAddress.shipWebsite.label" default="Ship Website" />

    </label>
    <g:textField name="shipWebsite" maxlength="50" value="${customerShipmentAddressInstance?.ship_website}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: customerShipmentAddressInstance, field: 'shipCity', 'error')} ">
    <label for="shipCity">
        <g:message code="customerShipmentAddress.shipCity.label" default="Ship City" />
    </label>
    <g:textField name="shipCity" maxlength="50" value="${customerShipmentAddressInstance?.ship_city}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: customerShipmentAddressInstance, field: 'shipState', 'error')} ">
    <label for="shipState">
        <g:message code="customerShipmentAddress.shipState.label" default="Ship State" />
    </label>
    <g:textField name="shipState" maxlength="50" value="${customerShipmentAddressInstance?.ship_state}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: customerShipmentAddressInstance, field: 'shipCountry', 'error')} required">
    <label for="shipCountry">
        <g:message code="customerShipmentAddress.shipCountry.label" default="Ship Country" />
        <span class="required-indicator">*</span>
    </label>
    %{--<g:select id="shipCountry" name="shipCountry.id" from="${bv.Countries.list()}" optionKey="id" required="" value="${customerShipmentAddressInstance?.ship_country_id}" class="many-to-one"/>--}%
    <%= "${new CoreParamsHelperTagLib().showCountryList("shipCountry.id","${customerShipmentAddressInstance?.ship_country_id}")}" %>
</div>

<div class="fieldcontain ${hasErrors(bean: customerShipmentAddressInstance, field: 'note', 'error')} ">
    <label for="note">
        <g:message code="customerShipmentAddressInstance.note.label" default="Note" />
    </label>
    <g:textArea name="note" onkeyup="autoresize(this)" style="width: 150px; border-radius: 6px 6px 6px 6px; overflow: hidden;"  maxlength="256" value="${customerShipmentAddressInstance?.note}"/>
</div>


<div class="fieldcontain ${hasErrors(bean: customerShipmentAddressInstance, field: 'status', 'error')} ">
    <label for="status">
        <g:message code="customerShipmentAddress.status.label" default="Status" />

    </label>
    <%= "${new CoreParamsHelperTagLib().StatusDropDown("status","${customerShipmentAddressInstance?.status}",false)}" %>
</div>
%{--

<div class="fieldcontain ${hasErrors(bean: customerShipmentAddressInstance, field: 'customer', 'error')} required">
    <label for="customer">
        <g:message code="customerShipmentAddress.customer.label" default="Customer" />
        <span class="required-indicator">*</span>
    </label>
    <g:select id="customer" name="customer.id" from="${bv.CustomerMaster.list()}" optionKey="id" required="" value="${customerShipmentAddressInstance?.customer?.id}" class="many-to-one"/>
</div>
--}%

<g:hiddenField name="customer_id" value="${params.id}" />
<g:hiddenField name="id" value="${customerShipmentAddressInstance?.id}" />
