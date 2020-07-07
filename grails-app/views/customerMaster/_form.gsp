<%@ page import="factoring.CoreParamsHelperTagLib; factoring.BudgetViewDatabaseService;" %>
<div id="helpinfodlg" title="<g:message code="customerMaster.defaultGlAccount.label" default="Default Gl Account" />" >
    <g:message code="bv.customerMaster.helpInfo.label" default="Customer GL Account" />
</div>
<% if (customerMasterInstance?.id) { %>

%{--Start my code now--}%

<div class="main-container">

<div class="codeContainerSections">
    %{--<div class="fcCodeNumber">--}%
    <div class="codeContainer">
        <div class="fieldContainer required">

            <label for="customerCode">
                <g:message code="customerMaster.customerCode.label" default="Customer Code"/><b>:</b>
                <span class="required-indicator">*</span>
            </label>

            <p class="labelCode">${customerPrefix + "-" + customerMasterInstance?.customer_code}</p>

            %{--${customerPrefix + customerMasterInstance?.customer_code}--}%
        </div>
    </div>  <!--codeContainer-->
</div>  <!--codeContainerSections-->

<div class="upperContainer">
    <div class="fieldContainerLeft">
        <div class="rowContainer">
            <div class="fieldContainer required fcInputText borderRed">
                <label for="companyName">
                    <g:message code="customerMaster.customerBudgetName.label" default="Debtor Name"/><b>:</b>
                    <span class="required-indicator">*</span>
                </label>
                <g:textField tabindex="5" placeholder="${g.message(code: 'customerMaster.companyName.label')}"
                             name="customerName" value="${customerMasterInstance?.customer_name}"/>
            </div>
        </div>

        <div class="rowContainer">
            <div class="fieldContainer required fcCombo">
                <label for="vat">
                    <g:message code="customerMaster.vat.label" default="Vat"/><b>:</b>
                </label>
                <%
                    def vatCategory = new CoreParamsHelperTagLib().getVatCategory('vat', (customerMasterInstance?.vat) ? (Integer.parseInt(customerMasterInstance?.vat)) : '0')
                %>
                <select class="styled sidebr01" name="vat" id="vat" tabindex="8">
                %{--                    <option value="">- no select -</option>--}%
                    <g:each in="${vatCategory}" var="vat">
                        <g:if test="${vatCategory.size() > 0}">
                            <option value="${vat.id}">${vat.category} (${vat.rate}%)</option>
                        </g:if>
                        <g:else>
                            <option value="0">"No vat.."</option>
                        </g:else>
                    </g:each>
                </select>
            </div>
        </div>
    </div>  <!--fieldContainerLeft-->

    <div class="fieldContainerRight">
        <div class="rowContainer">
            <div class="fieldContainer required fcInputText">
                <label for="chamOfCommerce">
                    <g:message code="customerMaster.chamOfCommerce.label" default="Cham Of Commerce"/><b>:</b>
                    %{--<span class="required-indicator">*</span>--}%
                </label>
                <g:textField tabindex="6" placeholder="${g.message(code: 'companySetup.chamberOfCommerceNo.label')}"
                             name="chamOfCommerce" value="${customerMasterInstance?.cham_of_commerce}"/>
            </div>
        </div>

        <div class="rowContainer">
            <div class="fieldContainer required fcCombo">
                <label for="paymentTerm">
                    <g:message code="customerMaster.paymentTerm.label" default="Payment Term"/><b>:</b>
                    %{--<span class="required-indicator">*</span>--}%
                </label>

                <%
                        def paymentTerm = new CoreParamsHelperTagLib().paymentTermDropdownList("paymentTerm.id", customerMasterInstance?.payment_term_id)
                %>

                <select class="styled sidebr01" name="paymentTerm" id="paymentTerm.id">
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
    </div>  <!--fieldContainerRight-->


    <div class="fieldContainerRight">

        <div class="rowContainer">
            <div class="emptyDiv"></div>

            <div class="fieldContainer required fcInputNumber" >
                <label for="defaultGlAccount">
                    <g:message code="customerMaster.creditLimit.label" default="Credit Limit"/><b>:</b>
                </label>
                <g:field type="number" name="creditLimit"  tabindex="4" onfocus="clearPlaceHolder(this)" onblur="setPlaceHolder(this)"
                         value="${customerMasterInstance?.credit_limit}"/>
            </div>
        </div>

        <div class="rowContainer">
            <div class="fieldContainer required fcInputNumber" >
                <label for="defaultGlAccount">
                    <g:message code="customerMaster.insuadAmount.label" default="Insuad Amount"/><b>:</b>
                </label>
                <g:field type="number" name="insuadAmout"  tabindex="4" onfocus="clearPlaceHolder(this)" onblur="setPlaceHolder(this)"
                         value="${customerMasterInstance?.insuad_amout}"/>
            </div>
        </div>
    </div>  <!--fieldContainerRight-->
</div>  <!--upperContainer-->
%{----####-----End upperContainer-----#######------}%

<div class="hrLineCusVen"></div>

<script type="text/javascript">
    $(document).ready(function () {
//                 $('.slideToggler2').show();
        $('.explanationLabel').click(function () {
            $('.slideOptionDiv').slideToggle('slow');
            $(this).toggleClass('slideSign2');
            $('.holdingData').css('display','block');
            return false;
        });
        $('.collaps2').click(function () {
            $('.slideOptionDiv').slideToggle('slow');
            $('.explanationLabel').toggleClass('slideSign2');
            return false;
        });
        $("#holding_date").datepicker({
            dateFormat: 'dd-mm-yy',
            showOn: "button"}).datepicker("setDate", new Date());
    });
</script>
%{--************************************************************************************--}%
<div class="lowerContainer">

    <label class="explanationLabel optionAndImg">
        <span class="explanationArrow"></span>

        <p><g:message code="companySetup.moreOptions.label" default="More options"/></p>
    </label>

    <div class="fieldContainer">
        <div class="slideOptionDiv" style="display: none;">

            <div class="fieldContainer" style="float: left;">
                <div class="fieldContainerLeft">

                    <div class="rowContainer">

                        <div class="smallCombo">
                            <div class="fieldContainer smallComboText">
                                <label for="salutaion">
                                    <g:message code="####customerMaster.firstName.label" default="Salutaion"/><b>:</b>
                                </label>

                                <%
                                    def genderSel = new CoreParamsHelperTagLib().CoreParamsDropDown('GENDER', 'gender', "${customerMasterInstance?.gender}")
                                %>

                                <select class="styled sidebr01" name="gender" id="gender">
                                %{-- <option value="">- no select -</option>--}%
                                    <g:each in="${genderSel}" var="gen">
                                        <g:if test="${genderSel.size() > 0}">
                                            <g:if test="${gen.inSelected}">
                                                <option selected value="${gen.value}">${gen.index}</option>
                                            </g:if>
                                            <g:else>
                                                <option value="${gen.value}">${gen.index}</option>
                                            </g:else>
                                        </g:if>
                                        <g:else>
                                            <option value="0">"No items.."</option>
                                        </g:else>
                                    </g:each>
                                </select>

                            </div>
                        </div>

                        <div class="fcInputfield">
                            <div class="fieldContainer smallInputText">
                                <label class="" for="firstName">
                                    <g:message code="customerMaster.firstName.label" default="First Name"/><b>:</b>
                                </label>
                                <g:textField tabindex="12"
                                             placeholder="${g.message(code: 'customerMaster.firstName.label')}"
                                             name="firstName" value="${customerMasterInstance?.first_name}"/>
                            </div>
                        </div>

                    </div>  <!--rowContainer-->

                    <div class="rowContainer">
                        <div class="fieldContainer fcInputText">
                            <label for="email">
                                <g:message code="customerMaster.email.label" default="Email"/><b>:</b>
                            </label>
                            <g:textField tabindex="15" placeholder="${g.message(code: 'customerMaster.email.label')}"
                                         name="email" value="${customerMasterInstance?.email}"/>
                        </div>
                    </div>

                    <div class="rowContainer">
                        <div class="fieldContainer fcInputText">
                            <label for="vatNumber">
                                <g:message code="customerMaster.vatNumber.label"
                                           default="Vat Number"/><b>:</b>
                            </label>
                            <g:textField tabindex="17" placeholder="Vat Number" name="vatNumber"
                                         value="${customerMasterInstance?.vat_number}"/>
                        </div>
                    </div>

                </div>  <!--fieldContainerLeft-->
                <div class="fieldContainerRight">
                    <div class="rowContainer">
                        <div class="fieldContainer fcInputText">
                            <label for="middleName">
                                <g:message code="customerMaster.middleName.label" default="Middle Name"/><b>:</b>
                            </label>
                            <g:textField tabindex="13" placeholder="${g.message(code: 'vendorMaster.middleName.label')}"
                                         name="middleName" value="${customerMasterInstance?.middle_name}"/>
                        </div>
                    </div>

                    <div class="rowContainer">
                        <div class="fieldContainer fcInputText">
                            <label for="phoneNo">
                                <g:message code="customerGeneralAddress.phoneNo.label" default="Phone No"/><b>:</b>
                            </label>
                            <g:textField tabindex="16" placeholder="Phone No" name="phoneNo"
                                         value="${customerGeneralAddressInstance?.phone_no}"/>
                        </div>
                    </div>

                    <div class="rowContainer">
                        <div class="fieldContainer required fcCombo">
                            <label for="status">
                                <g:message code="customerMaster.status.label" default="Status"/><b>:</b>
                                %{--<span class="required-indicator">*</span>--}%
                            </label>

                            <%
                                    def statusOptn = new CoreParamsHelperTagLib().StatusDropDown("status", customerMasterInstance?.status,null)
                            %>

                            <select class="styled sidebr01" name="status" id="status">
                            %{-- <option value="">- no select -</option>--}%
                                <g:each in="${statusOptn}" var="ststus">
                                    <g:if test="${statusOptn.size() > 0}">
                                        <g:if test="${ststus.inSelected}">
                                            <option selected value="${ststus.value}">${ststus.index}</option>
                                        </g:if>
                                        <g:else>
                                            <option value="${ststus.value}">${ststus.index}</option>
                                        </g:else>
                                    </g:if>
                                    <g:else>
                                        <option value="0">"No items.."</option>
                                    </g:else>
                                </g:each>
                            </select>
                        </div>
                    </div>
                </div>

                <div class="fieldContainerRight">

                    <div class="rowContainer">
                        <div class="fieldContainer fcInputText">
                            <label for="lastName">
                                <g:message code="customerMaster.lastName.label" default="Last Name"/><b>:</b>
                            </label>
                            <g:textField tabindex="14" placeholder="${g.message(code: 'vendorMaster.lastName.label')}"
                                         name="lastName" value="${customerMasterInstance?.last_name}"/>
                        </div>
                    </div>

                    <div class="rowContainer">
                        <div class="fieldContainer fcInputText">
                            <label for="websiteAddress">
                                <g:message code="customerGeneralAddress.websiteAddress.label"
                                           default="Website Address"/><b>:</b>
                            </label>
                            <g:textField tabindex="17" placeholder="Website Address" name="websiteAddress"
                                         value="${customerGeneralAddressInstance?.website_address}"/>
                        </div>
                    </div>
                </div>  <!--fieldContainerRight-->

            </div> <!--fieldContainer-->

            <div class="textAreaContainer">
                <div class="fieldContainer fcTextArea textBottomArea">
                    <label for="comments">
                        <g:message code="customerMaster.comments.label" default="Comments"/><b>:</b>
                    </label>
                    <g:textArea tabindex="18" name="comments" onkeyup="autoresize(this)"
                                value="${customerMasterInstance?.comments}"/>
                </div>
            </div>
            %{--<div class="textAreaContainer">
                <div class="fieldContainer fcTextArea textBottomArea">
                    <label for="notes">
                        <g:message code="customerMaster.notes.label" default="Notes"/><b>:</b>
                    </label>
                    <g:textArea tabindex="18" name="notes" onkeyup="autoresize(this)"/>
                </div>
            </div>--}%

            %{--<div class="textAreaContainer">
                <div class="fieldContainer fcTextArea textBottomArea">
                    <label for="holding_date">
                        <g:message code="customerMaster.holding.date.label" default="Do not send reminder"/><b>:</b>
                    </label>
                    <div class="required DatePickDiv holdingData">
                        <input class="datePickInput" type="text" id="holding_date" name="holding_date">
                    </div>
                </div>
            </div>--}%
        </div>  <!--slideOptionDiv-->
    </div> <!--fieldContainer-->

</div>  <!--lowerContainer-->

</div> <!--main container-->
%{--<!--mc end--->--}%
%{--end my code now--}%

<g:hiddenField name="id" value="${customerMasterInstance?.id}"/>
<g:hiddenField name="currCode" value="EUR"/>
<g:hiddenField name="creditStatus" value="Good History"/>

<g:hiddenField name="invEditId" value="${params.invEditId}"/>

<% } else { %>

<div class="main-container">
    <div class="fieldContainerLeft">
        <div class="rowContainer">
            <div class="fieldContainer required fcInputText borderRed">
                <label for="customerName">
                    <g:message code="customerMaster.customerBudgetName.label" default="Debtor Name"/><b>:</b>
                    <span class="required-indicator">*</span>
                </label>
                <g:textField tabindex="1" placeholder="${g.message(code: 'debtorMaster.debtorName.label')}"
                             name="customerName" required="" value="${customerMasterInstance?.customer_name}"/>
            </div>
        </div>

        <div class="rowContainer">
            <div class="fieldContainer fcCombo borderRed">
                <label for="vat">
                    <g:message code="customerMaster.vat.label" default="Vat"/><b>:</b>
                </label>

                <%
                    def vatCategory = new CoreParamsHelperTagLib().getVatCategory('vat', (customerMasterInstance?.vat) ? (Integer.parseInt(customerMasterInstance?.vat)) : '0')
                %>

                <select class="styled sidebr01" name="vat" id="vat" tabindex="8">
%{--                    <option value="">- no select -</option>--}%
                    <g:each in="${vatCategory}" var="vat">
                        <g:if test="${vatCategory.size() > 0}">
                            <option value="${vat.id}">${vat.category} (${vat.rate}%)</option>
                        </g:if>
                        <g:else>
                            <option value="0">"No vat.."</option>
                        </g:else>
                    </g:each>
                </select>
            </div>
        </div>
    </div> <!--fieldContainerLeft-->

    <div class="fieldContainerRight">
        <div class="rowContainer">
            <div class="fieldContainer required fcInputNumber" >
                <label for="defaultGlAccount">
                    <g:message code="customerMaster.creditLimit.label" default="Credit Limit"/><b>:</b>
                </label>
                <g:field type="number" name="creditLimit"  tabindex="4" onfocus="clearPlaceHolder(this)" onblur="setPlaceHolder(this)"
                         value="1000.0"/>
            </div>
        </div>

        <div class="rowContainer">
            <div class="fieldContainer fcCombo borderRed">
                <label for="paymentTerm">
                    <g:message code="customerMaster.paymentTerm.label" default="Payment Term"/><b>:</b>
                    %{--<span class="required-indicator">*</span>--}%
                </label>

                <%
                        def paymentTerm = new CoreParamsHelperTagLib().paymentTermDropdownList("paymentTerm.id", customerMasterInstance?.payment_term_id)
                %>

                <select class="styled sidebr01" name="paymentTerm" id="paymentTerm.id">
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
    </div>  <!--fieldContainerRight-->
</div> <!--main-container-->

<g:hiddenField name="id" value="${customerMasterInstance?.id}"/>
<g:hiddenField name="creditStatus" value="Good History"/>
<g:hiddenField name="currCode" value="EUR"/>

<% if (params.fInv) { %>
<g:hiddenField name="bookInvoiceId" value="${params.bookInvoiceId}"/>
<g:hiddenField name="bookingPeriod" value="${params.bookingPeriod}"/>
<g:hiddenField name="customerId" value="${params.customerId}"/>
<g:hiddenField name="budgetCustomerId" value="${params.budgetCustomerId}"/>
<g:hiddenField name="budgetItemDetailsId" value="${params.budgetItemDetailsId}"/>
<g:hiddenField name="fInv" value="${params.fInv}"/>
<g:hiddenField name="invEditId" value="${params.invEditId}"/>
<% } %>

<% if (params.incBItem) { %>
<g:hiddenField name="bookingPeriod" value="${params.bookingPeriod}"/>
<g:hiddenField name="customerId" value="${params.customerId}"/>
<g:hiddenField name="budgetCustomerId" value="${params.budgetCustomerId}"/>
<g:hiddenField name="journalId" value="${params.journalId}"/>
<g:hiddenField name="incBItem" value="${params.incBItem}"/>
<% } %>

<% } %>

