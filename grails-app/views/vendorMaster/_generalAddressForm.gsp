<%@ page import="factoring.CoreParamsHelperTagLib; factoring.VendorGeneralAddress;" %>

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
            </div>  <!--codeFieldLeft-->

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
                </div>  <!--rowContainer-->
            </div>  <!--codeFieldRight-->

        </div>  <!--codeContainerSections-->
        <div class="upperContainer">
            <div class="fieldContainerLeft">

                <div class="rowContainer">
                    <div class="fieldContainer required fcInputText">
                        <label for="contactPersonName">
                            <g:message code="vendorGeneralAddress.contactPersonName.label" default="Contact Person Name" />
                        </label>
                        <g:textField tabindex="1" placeholder="${g.message(code:'vendorGeneralAddress.contactPersonName.label')}" name="contactPersonName" value="${vendorGeneralAddressInstance?.contact_person_name}"/>
                    </div>
                </div>

                <div class="rowContainer">
                    <div class="fieldContainer fcInputText">
                        <label for="addressLine1">
                            <g:message code="vendorGeneralAddress.addressLine1.label" default="Address Line1" />
                            <span class="required-indicator">*</span>
                        </label>
                        <g:textField tabindex="2" placeholder="${g.message(code:'vendorGeneralAddress.addressLine1.label')}" name="addressLine1" style="overflow: hidden;" value="${vendorGeneralAddressInstance?.address_line1}"  required='required'/>
                    </div>
                </div>

                <div class="rowContainer">
                    <div class="fieldContainer required fcInputText">
                        <label for="postalCode">
                            <g:message code="vendorGeneralAddress.postalCode.label" default="Postal Code" />
                            <span class="required-indicator">*</span>
                        </label>
                        <g:textField required='required' tabindex="4" placeholder="${g.message(code:'vendorGeneralAddress.postalCode.label')}" name="postalCode" oninvalid="this.setCustomValidity('${message(code: 'vendorGeneralAddress.postalCode.blank',default:'Please give Postal Code' )}')" oninput="setCustomValidity('')" title="${message(code: 'vendorGeneralAddress.postalCode.blank',default:'Please give Postal Code' )}" value="${vendorGeneralAddressInstance?.postal_code}"/>
                    </div>
                </div>

                <div class="rowContainer">
                    <div class="fieldContainer required fcCombo">
                        <label for="country">
                            <g:message code="vendorGeneralAddress.country.label" default="Country" />
                            <span class="required-indicator">*</span>
                        </label>
                        <% def contryId = '2'
                        if(vendorGeneralAddressInstance?.country_id){
                            contryId = vendorGeneralAddressInstance?.country_id
                        }
                        %>
                        <%
                            def countryIds = new CoreParamsHelperTagLib().showCountryList("countryId","${contryId.toString()}")
                        %>

                        <select class="styled sidebr01" name="countryId" id="countryId">
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
                        <label for="addressLine2">
                            <g:message code="vendorGeneralAddress.addressLine2.label" default="Address Line2" />
                            <span class="required-indicator">*</span>
                        </label>
                        <g:textField tabindex="3" placeholder="${g.message(code:'vendorGeneralAddress.addressLine2.label')}" name="addressLine2" style="overflow: hidden;" value="${vendorGeneralAddressInstance?.address_line2}"  required='required'/>
                    </div>
                </div>

                <div class="rowContainer">
                    <div class="fieldContainer required fcInputText">
                        <label for="city">
                            <g:message code="vendorGeneralAddress.city.label" default="City" />
                        </label>
                        <g:textField tabindex="5" placeholder="${g.message(code:'vendorGeneralAddress.city.label')}" name="city" value="${vendorGeneralAddressInstance?.city}"/>
                    </div>
                </div>

                <div class="rowContainer">
                    <div class="fieldContainer required fcInputText">
                        <label for="officeEmail">
                            <g:message code="souls.email.label" default="Email" />
                        </label>
                        <g:textField tabindex="5" placeholder="${g.message(code:'souls.email.label')}" name="officeEmail" value="${vendorGeneralAddressInstance?.second_email}"/>
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
</fieldset>
<sec:ifNotGranted roles="ROLE_CUSTOMER">
<fieldset class="buttons_new updateLinkBtn">
    <% if (!vendorGeneralAddressInstance?.id) { %>
    <g:actionSubmit name="create" class="save updateBtn 2" controller ="vendorMaster" action="updategeneral" value="${message(code: 'default.button.create.label', default: 'Create')}"/>
    <% } else { %>
    <g:actionSubmit name="create" class="update updateBtn" controller ="vendorMaster" action="updategeneral" value="${message(code: 'default.button.update.label', default: 'Update')}"/>
    <% } %>
</fieldset>
</sec:ifNotGranted>
<sec:ifNotGranted roles="ROLE_ADMIN,ROLE_ACCOUNTANT,ROLE_USER">
    <fieldset class="buttons_new updateLinkBtn">
        <g:actionSubmit class="orangeBtn" style="height: 30px; margin-right: 12px" controller ="vendorMaster" action="changeGeneralAddress" value="${message(code: 'bv.change.request.customer', default: 'Change')}"/>
    </fieldset>
</sec:ifNotGranted>
<g:hiddenField name="vendor_id" value="${venId}" />
<g:hiddenField name="id" value="${vendorGeneralAddressInstance?.id}" />

