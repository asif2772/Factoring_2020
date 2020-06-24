<%@ page import="factoring.CoreParamsHelperTagLib" %>
<script type="text/javascript">
    $(document).ready(function () {
        $('.explanationLabel').click(function () {
            $('.slideOptionDiv').slideToggle('slow');
            $(this).toggleClass('slideSign2');
            return false;
        });
        $('.collaps2').click(function () {
            $('.slideOptionDiv').slideToggle('slow');
            $('.explanationLabel').toggleClass('slideSign2');
            return false;
        });
    });
</script>

<div class="companySetup">
    <div class="main-container">
        %{--<div class="singleContainer">--}%
        <div class="upperContainer" style="height: 148px;  margin-bottom: 23px;">
            <div class="fieldContainerLeft">

                <div class="rowContainer">
                    <div class="fieldContainer fcInputText">
                        <label for="postalAddress">
                             <g:message code="companySetup.postalAddressLine1.label" default="Postal Address"/>
                        </label>
                        <g:textField tabindex="1" placeholder="${g.message(code: 'companySetup.postalAddressLine1.label')}" name="postalAddressLine1" value="${companySetupInstance?.postal_address_line1}"/>
                    </div>
                </div>

                <div class="rowContainer">
                    <div class="fieldContainer fcInputText">
                        <label for="postalZipCode">
                            <g:message code="companySetup.postalZipCode.label" default="Postal Zip Code"/>
                        </label>
                        <g:textField tabindex="3" placeholder="${g.message(code: 'companySetup.postalZipCode.label')}" name="postalZipCode" value="${companySetupInstance?.postal_zip_code}"/>
                    </div>
                </div>
            </div>  <!--fieldContainerLeft-->

            <div class="fieldContainerRight">
                <div class="rowContainer">
                    <div class="fieldContainer fcInputText">
                        <label for="Postal State">
                            <g:message code="bv.companySetup.postalState.label" default="Postal State"/>
                        </label>
                        <g:textField tabindex="2" placeholder="${g.message(code: 'customerPostalAddress.postalState.label')}" name="postalState" value="${companySetupInstance?.postal_state}"/>
                    </div>
                </div>

            <div class="rowContainer">
                <div class="fieldContainer fcInputText">
                    <label for="postalCity">
                        <g:message code="companySetup.postalCity.label" default="Postal City"/>
                    </label>
                    <g:textField tabindex="4" placeholder="${g.message(code: 'companySetup.postalCity.label')}" name="postalCity" value="${companySetupInstance?.postal_city}"/>
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
                        <g:message code="companySetup.postalCountry.label" default="Postal Country"/>
                        <span class="required-indicator">*</span>
                    </label>

                    <%="${new CoreParamsHelperTagLib().getCountryPrintableDropDown('postalCountry', companySetupInstance.postal_country? companySetupInstance.postal_country : 'Netherlands')}"%>
                </div>
            </div>
            </div> <!--fieldContainerRight-->
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
                            %{--<div class="fieldContainer fcInputText">--}%
                                %{--<label for="email">--}%
                                    %{--<g:message code="vendorMaster.email.label" default="Email"/><b>:</b>--}%
                                %{--</label>--}%
                                %{--<g:textField tabindex="6" placeholder="${g.message(code: '##vendorMaster.email.label')}" name="email" value="${companySetupInstance?.email_address}"/>--}%
                            %{--</div>--}%
                        %{--</div>  <!--rowContainer-->--}%
                    %{--</div>  <!--fieldContainerLeft-->--}%

                    %{--<div class="fieldContainerRight">--}%

                    %{--<div class="rowContainer">--}%
                        %{--<div class="fieldContainer fcInputText">--}%
                            %{--<label for="phoneNo">--}%
                                %{--<g:message code="customerGeneralAddress.phoneNo.label" default="Phone No" /><b>:</b>--}%
                            %{--</label>--}%
                            %{--<g:textField tabindex="7" placeholder="${g.message(code: '##customerGeneralAddress.phoneNo.label')}" name="phoneNo" value="${companySetupInstance?.phone_no}"/>--}%
                        %{--</div>--}%
                    %{--</div>--}%
                    %{--</div>--}%

                    %{--<div class="fieldContainerRight">--}%
                        %{--<div class="rowContainer">--}%
                            %{--<div class="fieldContainer fcInputText">--}%
                                %{--<label for="websiteAddress">--}%
                                    %{--<g:message code="customerGeneralAddress.websiteAddress.label" default="Website Address" /><b>:</b>--}%
                                %{--</label>--}%
                                %{--<g:textField tabindex="8" placeholder="${g.message(code: '##customerGeneralAddress.websiteAddress.label')}" name="websiteAddress" value="${companySetupInstance?.website_address}"/>--}%
                            %{--</div>--}%
                        %{--</div>--}%
                    %{--</div>--}%

                %{--</div>--}%
            %{--</div> <!--fieldContainer-->--}%

        %{--</div>--}%
    %{--</div><!--lower fieldContainer-->--}%

    </div>  <!---mainContainerWithoutBG-->
</div>  <!-- companySetup -->