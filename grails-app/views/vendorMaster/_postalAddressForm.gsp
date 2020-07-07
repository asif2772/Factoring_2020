<%@ page import="factoring.CoreParamsHelperTagLib; factoring.VendorPostalAddress;" %>

<fieldset class="form" >
    <div class="main-container">

        <div class="codeContainerSections">
            <div class="codeFieldLeft">
                <div class="codeContainer">
                    <div class="fieldContainer required">
                        <label for="vendorCode">
                            <g:message code="vendorMaster.vendorCode.label" default="Vendor Code"/>
                            <span class="required-indicator">*</span>
                        </label>
                        <p class="labelCode">${vendorPrefix ? vendorPrefix : venPrefix}${"-"}${vendorMasterInstance?.vendor_code ? vendorMasterInstance?.vendor_code : venCode}</p>
                    </div>
                </div>  <!--codeContainer-->
            </div>  <!--fieldContainerLeft-->

            <div class="codeFieldMiddle">
                <div class="codeContainer">
                    <div class="fieldContainer required">
                        <label for="vendorName">
                            <g:message code="vendorMaster.vendorName.label" default="Vendor Name"/>
                        <span class="required-indicator">*</span>
                    </label>
                        <p class="labelCode">${vendorMasterInstance?.vendor_name ? vendorMasterInstance?.vendor_name : venName}</p>
                    </div>
                </div>  <!--codeContainer-->
            </div>  <!--codeFieldMiddle-->

            <div class="codeFieldRight">
                <div class="codeContainer">
                    <div class="emptyDiv"></div>
                </div>  <!--codeContainer-->
            </div>  <!--codeFieldRight-->

        </div>  <!--codeContainerSections-->


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
                            <span class="required-indicator">*</span>
                        </label>
                        <g:textField tabindex="2" placeholder="${g.message(code:'vendorPostalAddress.postalAddressLine1.label')}" name="postalAddressLine1" style="overflow: hidden;" value="${vendorPostalAddressInstance?.postal_address_line1}" required='required'/>
                    </div>
                </div>

                <div class="rowContainer">
                    <div class="fieldContainer fcInputText">
                        <label for="postalPostcode">
                            <g:message code="vendorPostalAddress.postalPostcode.label" default="Postal Postcode" />
                            <span class="required-indicator">*</span>
                        </label>
                        <g:textField tabindex="4" placeholder="${g.message(code:'vendorPostalAddress.postalPostcode.label')}" name="postalPostcode" value="${vendorPostalAddressInstance?.postal_postcode}" required='required'/>
                    </div>
                </div>

                <div class="rowContainer">
                    <div class="fieldContainer required fcCombo">
                        <label for="postalCountry">
                            <g:message code="vendorPostalAddress.postalCountry.label" name="postalCountry" default="Postal Country" />
                            <span class="required-indicator">*</span>
                        </label>
                        <% def contryId = '2'
                        if(vendorPostalAddressInstance?.postal_country_id){
                            contryId = vendorPostalAddressInstance?.postal_country_id
                        }
                        %>
                        <%
                            def countryIds = new CoreParamsHelperTagLib().showCountryList("postalCountry.id","${contryId}")
                        %>

                        <select class="styled sidebr01" name="postalCountry.id" id="postalCountry.id">
                        %{-- <option value="">- no select -</option>--}%
                            <g:each in="${countryIds}" var="con">
                                <g:if test="${countryIds.size() > 0}">
                                    <g:if test="${con.isExist}">
                                        <option selected value="${con.value}">${con.index}</option>
                                    </g:if>
                                    <g:else>
                                        <option value="${con.value}">${con.index}</option>
                                    </g:else>
                                </g:if>
                                <g:else>
                                    <option value="0">"No items.."</option>
                                </g:else>
                            </g:each>
                        </select>
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
                            <span class="required-indicator">*</span>
                        </label>
                        <g:textField tabindex="3" placeholder="${g.message(code:'vendorPostalAddress.postalAddressLine2.label')}" name="postalAddressLine2" style="overflow: hidden;" value="${vendorPostalAddressInstance?.postal_address_line2}" required='required'/>
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
                    <div class="fieldContainer fcInputText">
                        <label for="postalEmail">
                            <g:message code="souls.email.label" default="Email" />
                        </label>
                        <g:textField tabindex="5" placeholder="${g.message(code:'souls.email.label')}" name="postalEmail" value="${vendorPostalAddressInstance?.postal_email}"/>
                    </div>
                </div>

            </div> <!--fieldContainerRight-->
        </div>  <!---upperContainer--->
    </div>   <!---main-container--->
</fieldset>
<sec:ifNotGranted roles="ROLE_CUSTOMER">
    <fieldset class="buttons_new updateLinkBtn">
        <% if (!vendorPostalAddressInstance?.id) { %>
        <g:actionSubmit name="create" class="save updateBtn"  controller ="vendorMaster" action="updatepostal"  value="${message(code: 'default.button.create.label', default: 'Create')}"/>
        <% }else { %>
        <g:actionSubmit name="create" class="update save updateBtn" controller ="vendorMaster"  action="updatepostal" value="${message(code: 'default.button.update.label', default: 'Update')}"/>
        <% } %>
    </fieldset>
</sec:ifNotGranted>
<sec:ifNotGranted roles="ROLE_ADMIN,ROLE_ACCOUNTANT,ROLE_USER">
<fieldset class="buttons_new updateLinkBtn">
    <g:actionSubmit class="orangeBtn" style="height: 30px; margin-right: 12px" controller ="vendorMaster" action="changePostalAddress" value="${message(code: 'bv.change.request.customer', default: 'Change')}"/>
</fieldset>
</sec:ifNotGranted>
<g:hiddenField name="vendor_id" value="${venId}" />
<g:hiddenField name="id" value="${vendorPostalAddressInstance?.id}" />
