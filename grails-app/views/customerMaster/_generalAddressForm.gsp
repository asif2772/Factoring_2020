<%@ page import="bv.CoreParamsHelperTagLib; bv.CustomerGeneralAddress" %>

<div class="main-container">

    <div class="codeContainerSections">
       <div class="codeFieldLeft">
            <div class="codeContainer">
                <div class="fieldContainer required">
                    <label for="customerCode">
                        <g:message code="customerMaster.customerCode.label" default="Customer Code"/><b>:</b>
                        <span class="required-indicator">*</span>
                    </label>

                    <p class="labelCode">${customerPrefix + "-" + customerMasterInstance?.customer_code}</p>
                </div>
            </div>  <!--codeContainer-->
        </div>  <!--codeFieldLeft-->

        <div class="codeFieldMiddle">
            <div class="codeContainer">
                <div class="fieldContainer required">

                    <label for="customerName">
                        <g:message code="debtorMaster.debtorName.label" default="Debtor Name"/><b>:</b>
                        <span class="required-indicator">*</span>
                    </label>
                    <p class="labelCode">${customerMasterInstance?.customer_name}</p>
                </div>
            </div>  <!--codeContainer-->
        </div>  <!--codeFieldMiddle-->

        <div class="codeFieldRight">
            <div class="codeContainer">
                <div class="emptyDiv"></div>
            </div>
        </div>
    </div>  <!--codeContainerSections-->

    <div class="singleContainer">
        <div id="tabPageLoadData2" class="InvoiceAmountTabl">
            <div class="spinLoader fieldContainer">
                <img src="${resource(dir:'images',file:'spinner.gif')}" alt="${message(code:'spinner.alt.Loading',default:'Loading...')}" />
            </div>
        </div>
    </div>
</div>   <!---main-container--->



