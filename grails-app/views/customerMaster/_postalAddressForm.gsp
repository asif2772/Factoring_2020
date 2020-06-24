<%@ page import="factoring.CoreParamsHelperTagLib;factoring.CustomerPostalAddress;" %>


<div class="main-container">

    <div class="codeContainerSections">
        %{--<div class="fcCodeNumber">--}%
        <div class="codeFieldLeft">
            <div class="codeContainer">
                <div class="fieldContainer required">
                    <label for="customerCode">
                        <g:message code="customerMaster.customerCode.label" default="Customer Code"/><b>:</b>
                        <span class="required-indicator">*</span>
                    </label>

                    <p class="labelCode">${customerPrefix +"-"+ customerMasterInstance?.customer_code}</p>
                </div>
            </div>  <!--codeContainer-->
        </div>  <!--fieldContainerLeft-->

        <div class="codeFieldMiddle">
            <div class="codeContainer">
                <div class="fieldContainer required">
                    <label for="customerName">
                        <g:message code="debtorMaster.debtorName.label" default="Debtor"/><b>:</b>
                        <span class="required-indicator">*</span>
                    </label>
                    %{--<g:message code="customerMaster.lastName.label" default="Last Name"/>--}%
                    <p class="labelCode">${customerMasterInstance?.customer_name}</p>

                    %{--<p class="labelCode">${customerPrefix + customerMasterInstance?.customer_code}</p>--}%
                </div>
            </div>  <!--codeContainer-->
        </div>  <!--codeFieldMiddle-->

        <div class="codeFieldRight">
            <div class="codeContainer">
                <div class="fieldContainer required">
                    %{--<label for="companyName">--}%
                        %{--<g:message code="customerMaster.companyName.label" default="Company Name"/><b>:</b>--}%
                        %{--<span class="required-indicator">*</span>--}%
                    %{--</label>--}%
                    %{--<p class="labelCode">${customerMasterInstance?.company_name}</p>--}%
                </div>
            </div>  <!--codeContainer-->
        </div>  <!--codeFieldRight-->

    </div>  <!--codeContainerSections-->

    <div class="singleContainer">

        <div id="tabPageLoadData3" class="InvoiceAmountTabl">
            <div class="spinLoader fieldContainer">
                <asset:image src="spinner.gif" alt="${message(code:'spinner.alt.Loading',default:'Loading...')}" />
            </div>
        </div>

    </div>

</div>   <!---main-container--->

