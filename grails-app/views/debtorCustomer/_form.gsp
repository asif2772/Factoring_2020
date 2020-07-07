<%@ page import="factoring.CoreParamsHelperTagLib" %>
%{--Start my code now--}%
<%
    def contextPath = request.getServletContext().getContextPath()
%>

<div id="ajaxGlAccountRelatedInformation">
    <div class="main-container" >

        <div class="outsideContainerWithoutBG">
            <div class="fieldContainerLeft">

                <div class="rowContainer">
                    <div class="fieldContainer required fcSpecialCombo">
                        <label for="vendorId">
                            <g:message code="bv.invoiceExpense.vendorId.label" default="Customer Name"/>
                            <span class="required-indicator">*</span>
                        </label><br>
                        <% def customerId = ""+debtorCustomerInstance?.customerId
                            //println("customerId "+customerId)
                        %>
                        <%
                            def customerExpanseInvoice = new CoreParamsHelperTagLib().getCustomerExpanseInvoiceDropDownWithSelect('customerId',customerId, contextPath)
                        %>
                        <select class="styled sidebr01" name="customerId" id="customerId">
                        %{-- <option value="">- no select -</option>--}%
                            <g:each in="${customerExpanseInvoice}" var="opt">
                                <g:if test="${customerExpanseInvoice.size() > 0}">
                                    <g:if test="${opt.inSelected}">
                                        <option selected value="${opt.value}">${opt.index}</option>
                                    </g:if>
                                    <g:else>
                                        <option value="${opt.value}">${opt.index}</option>
                                    </g:else>
                                </g:if>
                                <g:else>
                                    <option value="0">"No items.."</option>
                                </g:else>
                            </g:each>
                        </select>
                    </div>
                </div>

                <div class="rowContainer">

                </div>


                <div class="rowContainer">
                    <div class="fieldContainer required fcInputNumber">
                        <label for="defaultFee">
                            <g:message code="debtorCustomer.defaultFee.label" default="Default Fee"/><b>:(%)</b>
                            %{--<span class="required-indicator">*</span>--}%
                        </label>
                        <g:field type="number" step="any" id="defaultFee" name="defaultFee"  tabindex="4" onfocus="clearPlaceHolder(this)" onblur="setPlaceHolder(this)"
                                     value="${debtorCustomerInstance?.defaultFee}"/>
                    </div>
                </div>

                <div class="rowContainer">
                    <div class="fieldContainer required fcInputNumber">
                        <label for="insuranceLimit">
                            <g:message code="debtorCustomer.insurancelimit.label" default="Insurance Limit"/><b>:</b>
                            %{--<span class="required-indicator">*</span>--}%
                        </label>
                        <g:field type="number" id="insuaranceLimit" name="insuaranceLimit"  tabindex="4" onfocus="clearPlaceHolder(this)" onblur="setPlaceHolder(this)"
                                     value="${debtorCustomerInstance?.insuaranceLimit}"/>
                    </div>
                </div>

                <div class="rowContainer">
                    <div class="fieldContainer required fcInputNumber">
                        <label for="outpayment">
                            <g:message code="debtorCustomer.outpayment.label" default="Outpayment"/><b>:(%)</b>
                            %{--<span class="required-indicator">*</span>--}%
                        </label>
                        <g:field type="number" step="any" id="outpayment" name="outpayment"  tabindex="4" min="0.0" max="100.0"
                                     value="${debtorCustomerInstance?.outpayment}"/>
                    </div>
                </div>

                <div class="rowContainer">
                    <div class="fieldContainer required fcInputText">
                        <label for="adminCost">
                            <g:message code="debtorCustomer.adminCost.label" default="Admin Cost"/><b>:</b>
                            %{--<span class="required-indicator">*</span>--}%
                        </label>
                        <g:textField id="adminCost" name="adminCost"  tabindex="4"
                                     value="${debtorCustomerInstance?.adminCost}"/>
                    </div>
                </div>

                <div class="rowContainer">
                    <div class="fieldContainer required fcInputText">
                        <label for="acceptanceFee">
                            <g:message code="debtorCustomer.acceptanceFee.label" default="Acceptance Fee"/><b>:</b>
                            %{--<span class="required-indicator">*</span>--}%
                        </label>
                        <g:textField id="acceptanceFee" name="acceptanceFee"  tabindex="4"
                                     value="${debtorCustomerInstance?.acceptanceFee}" onblur="setZero(this.value);"/>
                    </div>
                </div>

                <div class="rowContainer">
                    <div class="fieldContainer fcInputText">
                        <label for="acceptanceDate">
                            <g:message code="debtorCustomer.acceptanceDate.label" default="Acceptance Date" /><b>:</b>
                        </label>
                        %{--<% if(debtorCustomerInstance?.acceptenceDate == "") { %>
                            <bv:datePicker name="acceptanceDate" value="${new Date()}"/>
                        <% }else { %>
                            <bv:datePicker name="acceptanceDate" value="${debtorCustomerInstance?.acceptenceDate}"/>
                        <% } %>--}%
                    </div>
                </div>

            </div>  <!--fieldContainerLeft-->

            <div class="fieldContainerRight">

            <div class="rowContainer">
                <div class="fieldContainer required fcSpecialCombo">
                    <label for="customerId">
                        <g:message code="bv.invoiceIncome.customerId.label" default="Debtor Name"/><b>:</b>
                        <span class="required-indicator">*</span>
                    </label>
                    <br>
                    <% def customerName = ""+strSearch
                    //println(" name "+customerName)
                    %>

                    <% def debtorId = ""+debtorCustomerInstance?.debtorId
                    //println("debtorId "+debtorId)
                    %>
                    <%
                        def debtorExpanseInvoice = new CoreParamsHelperTagLib().getDebtorIncomeInvoiceDropDownWithSelect('debtorId',debtorId, contextPath, customerName)
                    %>
                    <select class="styled sidebr01" name="debtorId" id="debtorId">
                    %{-- <option value="">- no select -</option>--}%
                        <g:each in="${debtorExpanseInvoice}" var="opt">
                            <g:if test="${debtorExpanseInvoice.size() > 0}">
                                <g:if test="${opt.isSelected}">
                                    <option selected value="${opt.value}">${opt.index}</option>
                                </g:if>
                                <g:else>
                                    <option value="${opt.value}">${opt.index}</option>
                                </g:else>
                            </g:if>
                            <g:else>
                                <option value="0">"No items.."</option>
                            </g:else>
                        </g:each>
                    </select>
                </div>
            </div>

            <div class="rowContainer">

            </div>

            <div class="rowContainer">
                    <div class="fieldContainer required fcSpecialCombo">
                        <label for="debtorTerms">
                            <g:message code="debtorCustomer.debtorTerms.label" default="debtorTerms"/><b>:</b>
                        </label>
                        <%
                            def paymentTerm = new CoreParamsHelperTagLib().paymentTermDropdownList("debtorTermsId", debtorCustomerInstance?.debtorTermsId)
                        %>

                        <select class="styled sidebr01" name="debtorTermsId" id="debtorTermsId">
                        %{-- <option value="">- no select -</option>--}%
                            <g:each in="${paymentTerm}" var="terms">
                                <g:if test="${paymentTerm.size() > 0}">
                                    <option value="${terms.id}">${terms.terms}</option>
                                </g:if>
                                <g:else>
                                    <option value="0">"No terms.."</option>
                                </g:else>
                            </g:each>
                        </select>
                    </div>
                </div>

            <div class="rowContainer">
                <div class="fieldContainer required fcSpecialCombo">
                    <label for="firstReminder">
                        <g:message code="debtorCustomer.firstReminder.label" default="First Reminder"/><b> (days):</b>
                    </label>
                    <%
                        def firstRem = new CoreParamsHelperTagLib().IntegerDropDown(0,90,1,debtorCustomerInstance?.firstReminder,"firstReminder",'')
                    %>
                    <select class="styled sidebr01" name="firstReminder" id="firstReminder">
                    %{-- <option value="">- no select -</option>--}%
                        <g:each in="${firstRem}" var="opt">
                            <g:if test="${firstRem.size() > 0}">
                                <g:if test="${opt.isSelected}">
                                    <option selected value="${opt.value}">${opt.index}</option>
                                </g:if>
                                <g:else>
                                    <option value="${opt.value}">${opt.index}</option>
                                </g:else>
                            </g:if>
                            <g:else>
                                <option value="0">"No items.."</option>
                            </g:else>
                        </g:each>
                    </select>
                </div>
            </div>

            <div class="rowContainer">
                <div class="fieldContainer required fcSpecialCombo">
                    <label for="debtorTerms">
                        <g:message code="debtorCustomer.secondReminder.label" default="secondReminder"/><b> (days):</b>
                    </label>
                    <%
                        def secRem = new CoreParamsHelperTagLib().IntegerDropDown(0,90,1,debtorCustomerInstance?.secondReminder,"secondReminder",'')
                    %>
                    <select class="styled sidebr01" name="secondReminder" id="secondReminder">
                    %{-- <option value="">- no select -</option>--}%
                        <g:each in="${secRem}" var="opt">
                            <g:if test="${secRem.size() > 0}">
                                <g:if test="${opt.isSelected}">
                                    <option selected value="${opt.value}">${opt.index}</option>
                                </g:if>
                                <g:else>
                                    <option value="${opt.value}">${opt.index}</option>
                                </g:else>
                            </g:if>
                            <g:else>
                                <option value="0">"No items.."</option>
                            </g:else>
                        </g:each>
                    </select>
                </div>
            </div>

            <div class="rowContainer">
                <div class="fieldContainer required fcSpecialCombo">
                    <label for="debtorTerms">
                        <g:message code="debtorCustomer.thirdReminder.label" default="thirdReminder"/><b> (days):</b>
                    </label>
                    <%
                        def thirdRem = new CoreParamsHelperTagLib().IntegerDropDown(0,90,1,debtorCustomerInstance?.thirdReminder,"thirdReminder",'')
                    %>
                    <select class="styled sidebr01" name="thirdReminder" id="thirdReminder">
                    %{-- <option value="">- no select -</option>--}%
                        <g:each in="${thirdRem}" var="opt">
                            <g:if test="${thirdRem.size() > 0}">
                                <g:if test="${opt.isSelected}">
                                    <option selected value="${opt.value}">${opt.index}</option>
                                </g:if>
                                <g:else>
                                    <option value="${opt.value}">${opt.index}</option>
                                </g:else>
                            </g:if>
                            <g:else>
                                <option value="0">"No items.."</option>
                            </g:else>
                        </g:each>
                    </select>
                </div>
            </div>

            <div class="rowContainer">
                <div class="fieldContainer required fcSpecialCombo">
                    <label for="debtorTerms">
                        <g:message code="debtorCustomer.finalReminder.label" default="finalReminder"/><b> (days):</b>
                    </label>
                    <%
                        def finalRem = new CoreParamsHelperTagLib().IntegerDropDown(0,90,1,debtorCustomerInstance?.finalReminder,"finalReminder",'')
                    %>
                    <select class="styled sidebr01" name="finalReminder" id="finalReminder">
                    %{-- <option value="">- no select -</option>--}%
                        <g:each in="${finalRem}" var="opt">
                            <g:if test="${finalRem.size() > 0}">
                                <g:if test="${opt.isSelected}">
                                    <option selected value="${opt.value}">${opt.index}</option>
                                </g:if>
                                <g:else>
                                    <option value="${opt.value}">${opt.index}</option>
                                </g:else>
                            </g:if>
                            <g:else>
                                <option value="0">"No items.."</option>
                            </g:else>
                        </g:each>
                    </select>
                </div>
            </div>

            </div>  <!--fieldContainerRight-->
        </div>  <!--outsideContainerWithoutBG-->
    </div> <!--main container-->
</div>
