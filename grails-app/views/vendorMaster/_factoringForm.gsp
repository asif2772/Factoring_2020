<%@ page import="factoring.CoreParamsHelperTagLib;" %>
%{--Start my code now--}%
<%
    def contextPath = request.getServletContext().getContextPath()
%>

<div class="main-container">

    <div class="codeContainerSections">
        <div class="codeFieldLeft">
            <div class="codeContainer">
                <div class="fieldContainer required">
                    <label for="vendorCode">
                        <g:message code="vendorMaster.vendorCode.label" default="Vendor Code"/>
                        <span class="required-indicator">*</span>
                    </label>
                    <p class="labelCode">${vendorPrefix + "-" + vendorMasterInstance?.vendor_code}</p>
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
                    <p class="labelCode">${vendorMasterInstance?.vendor_name}</p>
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
                %{--<% if(vendorFactoringInstance?.subcriptionDate == "") { %>
                <bv:datePicker name="subcriptionDate" value="${new Date()}"/>
                <% }else { %>
                <bv:datePicker name="subcriptionDate" value="${vendorFactoringInstance?.subcriptionDate}"/>
                <% } %>--}%
            </div>
        </div>

    </div> <!--fieldContainerRight-->

</div> <!--upperContainer-->

<% if(vendorMasterInstance?.vendor_type == 'cn'){%>
<sec:ifNotGranted roles="ROLE_CUSTOMER">
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
                   %{-- <% if(vendorFactoringInstance?.brokerDate == null || vendorFactoringInstance?.brokerDate == "") { %>
                        <bv:datePicker name="brokerDate" value="${new Date()}"/>
                    <% }else { %>
                        <bv:datePicker name="brokerDate" value="${vendorFactoringInstance?.brokerDate}"/>
                    <% } %>--}%
                </div>
            </div>
        </div> <!--fieldContainerRight-->
    </div> <!--fieldContainer-->
</div>  <!--lowerContainer-->

<div class="lowerContainer">
    <div class="fieldContainer"  style="float: left;">
        <div class="fieldContainerLeft">

            <label class="explanationLabel optionAndImg" style="margin-left: -60px">
                <br>
                <p><g:message code="bv.customerMaster.statistics.total.inv.amount" default="Total Invoice Amount"/>:</p><br>
                <p><g:message code="bv.customerMaster.statistics.total.outstanding.inv" default="Total Outstanding Invoices"/>: </p><br>
                <p><g:message code="bv.customerMaster.statistics.total.paid.inv" default="Total Paid Invoices"/>: </p><br>
                <p><g:message code="bv.customerMaster.statistics.avg.outstanding.days" default="Average Days Outstanding"/>: </p><br>
                <p><g:message code="bv.customerMaster.statistics.avg.six.months.outstanding.days" default="Average Days During Last 6 Months"/>: </p><br>
                <p><g:message code="bv.customerMaster.statistics.avg.inv.amount" default="Average Invoice Amount"/>: </p><br>
            </label>

            <table id="customerStatistics">
                    <tr>
                        <th>Year</th>
                        <th>Volume</th>
                        <th>Revenue</th>
                        <th></th>
                    </tr>

                    <g:set var="counter" value="${revenuePerCustomerKeyArr.size()}"/>
                    <g:set var="i" value="${0}"/>

                    <g:while test="${i < counter}">
                        <tr>
                            <td> ${revenuePerCustomerKeyArr[i]} </td>
                            <td> <bv:euroDotCommaDecimalFormat decimalData="${invAmountValArr[i]}"/> </td>
                            <td> <bv:euroDotCommaDecimalFormat decimalData="${revenuePerCustomerValArr[i]}"/> </td>
                            <td> <bv:roundingValuesToTwoDigits decimalData="${100*(revenuePerCustomerValArr[i]/invAmountValArr[i])}"/> % </td>
                            <g:set var="i" value="${i+1}"/>
                        </tr>
                    </g:while>
            </table>
        </div> <!--fieldContainerLeft-->

        <div class="fieldContainerRight">

            <g:if test="${totalInvAmount == 0.0}">
                <g:set var="avgAmount" value="${0.0}"/>
            </g:if>
            <g:else test="${totalInvAmount > 0.0 || totalInvAmount < 0.0 }">
                <g:set var="avgAmount" value="${totalInvAmount/totalInvoices}"/>
            </g:else>

            <label class="explanationLabel optionAndImg" style="margin-left: -100px">
                <br>
                <p><bv:euroDotCommaDecimalFormat decimalData="${totalInvAmount}" /> (${totalInvoices})<br></p><br>
                <p><bv:euroDotCommaDecimalFormat decimalData="${totalOutstandingInvAmount}" /> (${totalOutstandingInv})<br> </p><br>
                <p><bv:euroDotCommaDecimalFormat decimalData="${totalPaidInvAmount}" /> (${totalPaidInv})<br></p><br>
                <p>${avgOutstandingDays}<br></p><br>
                <p>${avgDaysLastSixMonths}<br></p><br>
                <p><bv:euroDotCommaDecimalFormat decimalData="${avgAmount}"/></p><br>
            </label>
        </div>  <!--fieldContainerRight-->
    </div>
</div>
</sec:ifNotGranted><!--lowerContainer-->
<% } %>
</div>   <!---main-container--->

<g:hiddenField name="customerId" value="${params.id}" />
<g:hiddenField name="id" value="${vendorFactoringInstance?.id}" />

