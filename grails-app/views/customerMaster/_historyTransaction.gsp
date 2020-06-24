<%@ page import="factoring.CoreParamsHelperTagLib; java.text.SimpleDateFormat;" %>
<% if (executeMessage) { %>
<div id="msgdiv" style="text-align: center;height:40px;margin-top:15px">
    <b style="font-size:17px;color:${executeMessageColor};"><g:message code="${executeMessageCode}" default="${executeMessage}"/></b>
</div>
<% } %>
<div class="main-container">
    <div class="codeContainerSections">

        <div class="codeFieldLeft">
            <div class="codeContainer">
                <div class="fieldContainer required">
                    <label for="customerCode">
                        <g:message code="customerMaster.customerCode.label" default="Customer Code"/><b>:</b>
                        <span class="required-indicator">*</span>
                    </label>
                    <p class="labelCode">${customerPrefix?customerPrefix:cusPrefix}${"-"}${customerMasterInstance?.customer_code?customerMasterInstance?.customer_code:cusCode}</p>
                </div>
            </div>  <!--codeContainer-->
        </div>  <!--fieldContainerLeft-->

        <div class="codeFieldMiddle">
            <div class="codeContainer">
                <div class="fieldContainer required">
                    <label for="customerName">
                        <g:message code="customerMaster.budgetName.label" default="Budget Name"/><b>:</b>
                        <span class="required-indicator">*</span>
                    </label>
                    %{--<g:message code="customerMaster.lastName.label" default="Last Name"/>--}%
                    <p class="labelCode">${customerMasterInstance?.customer_name?customerMasterInstance?.customer_name:cusName}</p>

                    %{--<p class="labelCode">${customerPrefix + customerMasterInstance?.customer_code}</p>--}%
                </div>
            </div>  <!--codeContainer-->
        </div>  <!--codeFieldMiddle-->

        <div class="codeFieldRight">
            <div class="codeContainer">
                <div class="fieldContainer required">
                </div>
            </div>  <!--codeContainer-->
        </div>  <!--codeFieldRight-->

    </div>  <!--codeContainerSections-->


    <div class="singleContainer">
        <div id="tabPageLoadData9" class="fieldContainer">
            <div class="spinLoader fieldContainer">
                <asset:image src="spinner.gif" alt="${message(code:'spinner.alt.Loading',default:'Loading...')}" />
            </div>
        </div>

    </div>  <!--singleContainer-->
</div> <!---main-container--->
