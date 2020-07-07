<%@ page import="factoring.CoreParamsHelperTagLib; factoring.VendorMaster" %>
%{--Start my code now--}%
<%
    def contextPath = request.getServletContext().getContextPath()
%>


<div class="upperContainer">
    <div class="fieldContainerLeft">

        <div class="rowContainer">
            <div class="fieldContainer fcInputText">
                <label for="defaultFee">
                    <g:message code="debtorCustomer.defaultFee.label" default="Default Fee"/><b>:(%)</b>
                    %{--<span class="required-indicator">*</span>--}%
                </label>
                <g:textField name="defaultFee"  tabindex="1" value="${vendorFactoringInstance?.defaultFee}" onfocus="clearPlaceHolder(this)" onblur="setPlaceHolder(this)"/>
            </div>
        </div>

        <div class="rowContainer">
            <div class="fieldContainer required fcInputText">
                <label for="outpayment">
                    <g:message code="debtorCustomer.outpayment.label" default="Outpayment"/><b>:(%)</b>
                    %{--<span class="required-indicator">*</span>--}%
                </label>
                <g:textField name="outpayment"  tabindex="2" value="${vendorFactoringInstance?.outpayment}"/>
            </div>
        </div>


    </div>  <!--fieldContainerLeft-->

    <div class="fieldContainerRight">

        <div class="rowContainer">
            <div class="fieldContainer required fcInputText">
                <label for="acceptanceFee">
                    <g:message code="debtorCustomer.acceptanceFee.label" default="Acceptance Fee"/><b>:</b>
                    %{--<span class="required-indicator">*</span>--}%
                </label>
                <g:textField name="acceptenceFee"  tabindex="4"
                             value="${vendorFactoringInstance?.acceptenceFee}"/>
            </div>
        </div>

        <div class="rowContainer">
            <div class="fieldContainer required fcInputText">
                <label for="subcriptionAmount">
                    <g:message code="masterCustomer.subcriptionAmount.label" default="Subcription Amount"/><b>:</b>
                    %{--<span class="required-indicator">*</span>--}%
                </label>
                <g:textField name="subcriptionAmount"  tabindex="5"
                             value="${vendorFactoringInstance?.subcriptionAmount}"/>
            </div>
        </div>

    </div> <!--fieldContainerRight-->


    <div class="fieldContainerRight">

        <div class="rowContainer">
            <div class="fieldContainer required fcInputText">
                <label for="adminCost">
                    <g:message code="debtorCustomer.adminCost.label" default="Admin Cost"/><b>:</b>
                    %{--<span class="required-indicator">*</span>--}%
                </label>
                <g:textField name="adminCost"  tabindex="3" value="${vendorFactoringInstance?.adminCost}"/>
            </div>
        </div>

        <div class="rowContainer">
            <div class="fieldContainer required fcInputText">
                <label for="subcriptionDate">
                    <g:message code="masterCustomer.subcriptionDate.label" default="Subcription Date" /><b>:</b>
                </label>
                <% if(vendorFactoringInstance?.subcriptionDate == "") { %>
                <bv:datePicker name="subcriptionDate" value="${new Date()}"/>
                <% }else { %>
                <bv:datePicker name="subcriptionDate" value="${vendorFactoringInstance?.subcriptionDate}"/>
                <% } %>
            </div>
        </div>

    </div> <!--fieldContainerRight-->

</div> <!--upperContainer-->

<% if(vendorMasterInstance?.vendor_type == 'cn'){%>
<div class="hrLineCusVen"></div>

<div class="lowerContainer">

    <label class="explanationLabel optionAndImg" style="margin-left: -60px">
%{--<span class="explanationArrow"></span>--}%
<p><g:message code="vendorMaster.broker.options.label" default="Broker options"/></p>
</label>
<div class="fieldContainer"  style="float: left;">
    <div class="fieldContainerLeft">

        <div class="rowContainer">
            <div class="fieldContainer required fcCombo">
                <label for="vat">
                    <g:message code="vendorMaster.broker.name.label" default="Broker Name"/><b>:</b>
                    %{--<span class="required-indicator">*</span>--}%
                </label>
                <% def brokerId = vendorFactoringInstance?.brokerId + ""; %>
                <%="${new CoreParamsHelperTagLib().getBrokerListDropDown('brokerId',brokerId , contextPath)} "%>
            </div>
        </div>
    </div>  <!--fieldContainerLeft-->

    <div class="fieldContainerRight">
        <div class="rowContainer">
            <div class="fieldContainer required fcInputText">
                <label for="outpayment">
                    <g:message code="vendorMaster.broker.fee.label" default="Broker Fee"/><b>:(%)</b>
                    %{--<span class="required-indicator">*</span>--}%
                </label>
                <g:textField name="brokerFee"  tabindex="2" value="${vendorFactoringInstance?.brokerFee}"/>
            </div>
        </div>

    </div>  <!--fieldContainerRight-->

    <div class="fieldContainerRight">
        <div class="rowContainer">
            <div class="fieldContainer required fcInputText">
                <label for="brokerDate">
                    <g:message code="vendorMaster.broker.endDate.label" default="Broker End Date" /><b>:</b>
                </label>
                <% if(vendorFactoringInstance?.brokerDate == null || vendorFactoringInstance?.brokerDate == "") { %>
                <bv:datePicker name="brokerDate" value="${new Date()}"/>
                <% }else { %>
                <bv:datePicker name="brokerDate" value="${vendorFactoringInstance?.brokerDate}"/>
                <% } %>
            </div>
        </div>
    </div>  <!--fieldContainerRight-->
</div>  <!--lowerContainer-->
<% } %>


<g:hiddenField name="customerId" value="${customerId}" />
<g:hiddenField name="id" value="${vendorFactoringInstance?.id}" />

