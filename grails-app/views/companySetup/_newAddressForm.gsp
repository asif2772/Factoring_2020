<%@ page import="factoring.CoreParamsHelperTagLib" %>
<div class="companySetup">
    <div class="main-container">
        <div class="upperContainer" style="height: 148px;  margin-bottom: 23px;">
            <div class="fieldContainerLeft">

                <div class="rowContainer">
                    <div class="fieldContainer fcInputText">
                        <label for="addressLine1">
                            <g:message code="companySetup.addressLine1.label" default="Address Line 1"/>
                        </label>
                        <g:textField tabindex="1" placeholder="${g.message(code: 'companySetup.addressLine1.label')}"
                                     name="addressLine1" value="${companySetupInstance?.address_line1}"/>
                    </div>
                </div>

                <div class="rowContainer">
                    <div class="fieldContainer fcInputText">
                        <label>
                            <g:message code="companySetup.generalPostalCode.label" default="Post Code"/>
                        </label>
                        <g:textField tabindex="3" placeholder="${g.message(code: 'companySetup.generalPostalCode.label')}"
                                     name="postCode" value="${companySetupInstance?.general_postal_code}"/>
                    </div>
                </div>
            </div>  <!--fieldContainerLeft-->


            <div class="fieldContainerRight">

                <div class="rowContainer">
                    <div class="fieldContainer fcInputText">
                        <label for="addressLine2">
                            <g:message code="companySetup.addressLine2.label" default="Address Line 2"/>
                        </label>
                        <g:textField tabindex="2" placeholder="${g.message(code: 'companySetup.addressLine2.label')}"
                                     name="addressLine2" value="${companySetupInstance?.address_line2}"/>
                    </div>
                </div>

                <div class="rowContainer">
                    <div class="fieldContainer fcInputText">
                        <label for="city">
                            <g:message code="companySetup.city.label" default="City"/>
                        </label>
                        <g:textField tabindex="4" placeholder="${g.message(code: 'companySetup.city.label')}" name="city"
                                     value="${companySetupInstance?.city}"/>
                    </div>
                </div>

            </div> <!--fieldContainerRight-->

            <div class="fieldContainerRight">
                <div class="rowContainer">
                    <div class="emptyDiv"></div>
                </div>

                <div class="rowContainer">
                    <div class="fieldContainer required fcCombo">
                        <label for="postalCountry">
                            <g:message code="ccompanySetup.country.label" default="Country"/>
                            <span class="required-indicator">*</span>
                        </label>
                        <%="${new CoreParamsHelperTagLib().getCountryPrintableDropDown('country', "${(companySetupInstance?.country) ? (companySetupInstance?.country) : 'Netherlands'}")}"%>
                    </div>
                </div>
            </div>
        </div>   <!---upperContainer-->

    %{----####-----End upperContainer-----#######------}%

        %{--<div class="lowerContainer">--}%

            %{--<label class="explanationLabel optionAndImg">--}%
                %{--<span class="explanationArrow"></span>--}%

                %{--<p><g:message code="companySetup.moreOptions.label" default="More options"/></p>--}%
            %{--</label>--}%

            %{--<div class="contactPerson">--}%

                %{--<div class="fieldContainer">--}%
                    %{--<div class="slideOptionDiv" style="display: none;">--}%
                        %{--<label class="contactLabel">Contact Person</label>--}%

                        %{--<div class="fieldContainer contactLine">--}%
                            %{--<g:message code="companySetup.contactPerson.label" default="Contact Person"/>--}%
                        %{--</div>--}%

                        %{--<div class="fieldContainerLeft">--}%
                            %{--<div class="rowContainer">--}%
                                %{--<div class="smallCombo">--}%
                                    %{--<div class="fieldContainer smallComboText">--}%
                                        %{--<label for="salutaion">--}%
                                            %{--<g:message code="####customerMaster.firstName.label"--}%
                                                       %{--default="Salutaion"/><b>:</b>--}%
                                        %{--</label>--}%
                                        %{--<%="${new CoreParamsHelperTagLib().CoreParamsDropDown('GENDER', 'salutaionAddress', "${companySetupInstance?.company_full_name}")}"%>--}%
                                    %{--</div>--}%
                                %{--</div>--}%

                                %{--<div class="fcInputfield">--}%
                                    %{--<div class="fieldContainer smallInputText">--}%
                                        %{--<label class="" for="firstName">--}%
                                            %{--<g:message code="customerMaster.firstName.label"--}%
                                                       %{--default="First Name"/><b>:</b>--}%
                                        %{--</label>--}%
                                        %{--<g:textField tabindex="7" placeholder="${g.message(code: 'customerMaster.firstName.label')}"--}%
                                                     %{--name="firstNameAddress"--}%
                                                     %{--value="${companySetupInstance?.company_full_name}"/>--}%
                                    %{--</div>--}%
                                %{--</div>--}%
                            %{--</div>  <!--rowContainer-->--}%
                        %{--</div>  <!--fieldContainerLeft-->--}%

                        %{--<div class="fieldContainerRight">--}%

                            %{--<div class="rowContainer">--}%
                                %{--<div class="fieldContainer fcInputText">--}%
                                    %{--<label for="middleName">--}%
                                        %{--<g:message code="vendorMaster.middleName.label" default="Middle Name"/><b>:</b>--}%
                                    %{--</label>--}%
                                    %{--<g:textField tabindex="8" placeholder="${g.message(code: 'customerMaster.middleName.label')}"--}%
                                                 %{--name="middleName" value="${companySetupInstance?.company_full_name}"/>--}%
                                %{--</div>--}%
                            %{--</div>--}%
                        %{--</div>--}%

                        %{--<div class="fieldContainerRight">--}%
                            %{--<div class="rowContainer">--}%
                                %{--<div class="fieldContainer fcInputText">--}%
                                    %{--<label for="lastName">--}%
                                        %{--<g:message code="vendorMaster.lastName.label" default="Last Name"/><b>:</b>--}%
                                    %{--</label>--}%
                                    %{--<g:textField tabindex="9" placeholder="${g.message(code: 'customerMaster.lastName.label')}"--}%
                                                 %{--name="lastName" value="${companySetupInstance?.company_full_name}"/>--}%
                                %{--</div>--}%
                            %{--</div>--}%
                        %{--</div>--}%

                    %{--</div>--}%
                %{--</div> <!--fieldContainer-->--}%
            %{--</div>--}%
        %{--</div>    <!---lowerContainer-->--}%

        <div class="lowerContainer">
            <label class="explanationLabel optionAndImg">
                <span class="explanationArrow"></span>
                <p><g:message code="companySetup.moreOptions.label" default="More options"/></p>
            </label>

            <div class="contactPerson">

                <div class="fieldContainer">
                    <div class="slideOptionDiv" style="display: none;">
                        %{--<label class="contactLabel">Contact Person</label>--}%

                        <div class="fieldContainer contactLine">
                            <g:message code="companySetup.contactPerson.label" default="Contact Person"/>
                        </div>

                        <div class="fieldContainerLeft">
                            <div class="rowContainer">
                                <div class="fieldContainer fcInputText">
                                    <label for="email">
                                        <g:message code="vendorMaster.email.label" default="Email"/><b>:</b>
                                    </label>
                                    <g:textField tabindex="6" placeholder="${g.message(code: 'vendorMaster.email.label')}" name="email" value="${companySetupInstance?.email_address}"/>
                                </div>
                            </div>  <!--rowContainer-->
                        </div>  <!--fieldContainerLeft-->

                        <div class="fieldContainerRight">

                            <div class="rowContainer">
                                <div class="fieldContainer fcInputText">
                                    <label for="phoneNo">
                                        <g:message code="customerGeneralAddress.phoneNo.label" default="Phone No" /><b>:</b>
                                    </label>
                                    <g:textField tabindex="7" placeholder="${g.message(code: 'customerGeneralAddress.phoneNo.label')}" name="phoneNo" value="${companySetupInstance?.phone_no}"/>
                                </div>
                            </div>
                        </div>

                        <div class="fieldContainerRight">
                            <div class="rowContainer">
                                <div class="fieldContainer fcInputText">
                                    <label for="websiteAddress">
                                        <g:message code="customerGeneralAddress.websiteAddress.label" default="Website Address" /><b>:</b>
                                    </label>
                                    <g:textField tabindex="8" placeholder="${g.message(code: 'customerGeneralAddress.websiteAddress.label')}" name="websiteAddress" value="${companySetupInstance?.website_address}"/>
                                </div>
                            </div>
                        </div>

                    </div>
                </div> <!--fieldContainer-->

            </div>
        </div><!--lower fieldContainer-->

    </div>  <!---main-container-->
</div>  <!-- companySetup -->

