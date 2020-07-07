<%@ page import="factoring.CoreParamsHelperTagLib; factoring.BudgetViewDatabaseService; factoring.VendorMaster;" %>
<%@ page import="factoring.CoreParamsHelperTagLib" %>
<div id="helpinfodlg" title="<g:message code="customerMaster.defaultGlAccount.label" default="Default Gl Account" />" >
    <g:message code="bv.vendorMaster.helpInfo.label" default="Vendor GL Account" />
</div>

<% if (vendorMasterInstance?.id) { %>

%{--Start my code now--}%
<div class="main-container">

<div class="codeContainerSections">
    <div class="codeContainer">
        <div class="fieldContainer required">
            <label for="vendorCode">
                <g:message code="vendorMaster.vendorCode.label" default="Customer Code"/><b>:</b>
                <span class="required-indicator">*</span>
            </label>
            <p class="labelCode">${vendorPrefix + "-" + vendorMasterInstance?.vendor_code}</p>
        </div>
    </div>  <!--codeContainer-->
</div>  <!--codeContainerSections-->

<div class="upperContainer">
    <div class="fieldContainerLeft">
        <div class="rowContainer">
            <div class="fieldContainer required fcInputText borderRed">
                <label for="vendorName">
                    <g:message code="vendorMaster.vendorName.label" default="Customer Name"/><b>:</b>
                    <span class="required-indicator">*</span>
                </label>
                <g:textField tabindex="5" placeholder="${g.message(code: 'vendorMaster.vendorName.label')}" name="vendorName" required="" value="${vendorMasterInstance?.vendor_name}"/>
             </div>
        </div>

        <div class="rowContainer">
            <div class="fieldContainer required fcCombo">
                <label for="vat">
                    <g:message code="vendorMaster.vat.label" default="Vat"/><b>:</b>
                    <span class="required-indicator">*</span>
                </label>
               <%
                   def vatCategory = new CoreParamsHelperTagLib().getVatCategory('vat', (vendorMasterInstance?.vat) ? (Integer.parseInt(vendorMasterInstance?.vat)) : '0')
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
                    <g:message code="vendorMaster.chamOfCommerce.label" default="Cham Of Commerce"/><b>:</b>
                    <span class="required-indicator">*</span>
                </label>
                <g:textField tabindex="6" placeholder="${g.message(code: 'vendorMaster.chamOfCommerce.label')}" name="chamOfCommerce" value="${vendorMasterInstance?.cham_of_commerce}"/>
            </div>
        </div>

    <sec:ifNotGranted roles="ROLE_CUSTOMER">
        <div class="rowContainer">
            <div class="fieldContainer required fcCombo">
                <label for="payment_term">
                    <g:message code="vendorMaster.payment_term.label" default="Payment term"/><b>:</b>
                    <span class="required-indicator">*</span>
                </label>
                <%
                        def paymentTerm = new CoreParamsHelperTagLib().paymentTermDropdownList("paymentTerm.id", (vendorMasterInstance?.payment_term_id) ? (vendorMasterInstance?.payment_term_id) : '2')
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
    </sec:ifNotGranted>
    </div>  <!--fieldContainerRight-->

    <div class="fieldContainerRight">
    <sec:ifNotGranted roles="ROLE_CUSTOMER">
        <div class="rowContainer">
            <div class="emptyDiv"></div>
            <div class="fieldContainer required fcCombo">
                <label>
                    <g:message code="customer.Type.label" default="Type"/>
                    <span class="required-indicator">*</span>
                </label>
                <%
                        def genderSel = new CoreParamsHelperTagLib().CoreParamsDropDown('GENDER', 'gender', "${vendorMasterInstance?.vendor_type}")
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
    </sec:ifNotGranted>
        <div class="rowContainer">
            <div class="fieldContainer required fcInputNumber">
                <label for="defaultGlAccount">
                    <g:message code="vendorMaster.creditLimit.label" default="Credit Limit"/><b>:</b>
                </label>
                <g:field type="number" name="creditLimit"  tabindex="4" onfocus="clearPlaceHolder(this)" onblur="setPlaceHolder(this)"
                         value="${vendorMasterInstance?.credit_limit}"/>
                %{--value="${vendorMasterInstance?.credit_limit}"/>--}%
                %{--<%="${new CoreParamsHelperTagLib().getBudgetChartGroupDropDownExpanse('defaultGlAccount', "${vendorMasterInstance?.default_gl_account}")}"%>--}%
            </div>
        </div>
    </div>  <!--fieldContainerRight-->
</div>  <!--upperContainer-->
%{----####-----End upperContainer-----#######------}%

<div class="hrLineCusVen"></div>

%{--********************************************* JQuery *********************************--}%
    <script type="text/javascript">
        $(document).ready(function() {
//                 $('.slideToggler2').show();
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
%{--************************************************************************************--}%

<sec:ifNotGranted roles="ROLE_CUSTOMER">
    <div class="lowerContainer">

        <label class="explanationLabel optionAndImg">
            <span class="explanationArrow"></span>
            <p><g:message code="companySetup.moreOptions.label" default="More options"/></p>
        </label>

        <div class="fieldContainer">
            <div class="slideOptionDiv" style="display: none;">

        <div class="fieldContainer"  style="float: left;">
        <div class="fieldContainerLeft">

            <div class="rowContainer">

                <div class="smallCombo">
                    <div class="fieldContainer smallComboText">
                        <label for="salutaion">
                            <g:message code="customerMaster.salutaion.label" default="Salutaion"/><b>:</b>
                        </label>
                        <%
                                def genders = new CoreParamsHelperTagLib().CoreParamsDropDown('GENDER', 'gender', "${vendorMasterInstance?.gender}")
                        %>
                        <select class="styled sidebr01" name="gender" id="gender">
                        %{-- <option value="">- no select -</option>--}%
                            <g:each in="${genders}" var="gen">
                                <g:if test="${genders.size() > 0}">
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
                        <label for="firstName">
                            <g:message code="vendorMaster.firstName.label" default="First Name"/><b>:</b>
                        </label>
                        <g:textField tabindex="12" placeholder="${g.message(code: 'vendorMaster.firstName.label')}" name="firstName" value="${vendorMasterInstance?.first_name}" required="required"/>
                    </div>
                </div>

            </div>  <!--rowContainer-->

            <div class="rowContainer">
                <div class="fieldContainer fcInputText" style="width: 83%;">
                    <label for="email">
                        <g:message code="vendorMaster.email.label" default="Email"/><b>:</b>
                    </label>
                    <g:textField tabindex="15" placeholder="${g.message(code: 'vendorMaster.email.label')}" name="email" value="${vendorMasterInstance?.email}" required="required"/>
                </div>
                <label class="switch createCusToggle" title="Do you want to create user?" >
                    <input name="isCustomerPortalUser" id="isCustomerPortalUser" type="checkbox">
                    <span class="slider round"></span>
                </label>
            <input name="isCustomerPortalUser" value="off" hidden>

        </div>
            <div class="rowContainer">
                <div class="fieldContainer fcInputText">
                    <label for="optionalEmail">
                        <g:message code="vendorMaster.email.optional.label" default="Optional Email"/><b>:</b>
                    </label>
                    <g:textField tabindex="15" placeholder="${g.message(code: 'vendorMaster.email.optional.label')}" name="optionalEmail" value="${vendorMasterInstance?.optional_email}"/>
                </div>
            </div>
            <div class="rowContainer">
                <div class="fieldContainer fcInputText">
                    <label for="vatNumber">
                        <g:message code="customerMaster.vatNumber.label"
                                   default="Vat Number"/><b>:</b>
                    </label>
                    <g:textField tabindex="17" placeholder="Vat Number" name="vatNumber"
                                 value="${vendorMasterInstance?.vat_number}"/>
                </div>
            </div>
        </div>  <!--fieldContainerLeft-->



        <div class="fieldContainerRight">

            <div class="rowContainer">
                <div class="fieldContainer fcInputText">
                    <label for="middleName">
                        <g:message code="vendorMaster.middleName.label" default="Middle Name"/><b>:</b>
                    </label>
                    <g:textField tabindex="13" placeholder="${g.message(code: 'vendorMaster.middleName.label')}" name="middleName" value="${vendorMasterInstance?.middle_name}"/>
                </div>
            </div>

            <div class="rowContainer">
                <div class="fieldContainer fcInputText">
                    <label for="phoneNo">
                        <g:message code="customerGeneralAddress.phoneNo.label" default="Phone No" /><b>:</b>
                    </label>
                    <g:textField tabindex="16" placeholder="${g.message(code: 'customerGeneralAddress.phoneNo.label')}" name="phoneNo" value="${vendorGeneralAddressInstance?.phone_no}"/>
                </div>
            </div>
            <div class="rowContainer">
                <div class="fieldContainer required fcCombo">
                    <label for="status">
                        <g:message code="customerMaster.status.label" default="Status"/><b>:</b>
                        %{--<span class="required-indicator">*</span>--}%
                    </label>
                    <%
                            def statusOptn = new CoreParamsHelperTagLib().StatusDropDown("status", vendorMasterInstance?.status,null)
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
                        <g:message code="vendorMaster.lastName.label" default="Last Name"/><b>:</b>
                    </label>
                    <g:textField tabindex="14" placeholder="${g.message(code: 'vendorMaster.lastName.label')}" name="lastName" value="${vendorMasterInstance?.last_name}" required="required"/>
                </div>
            </div>

            <div class="rowContainer">
                <div class="fieldContainer fcInputText">
                    <label for="websiteAddress">
                        <g:message code="customerGeneralAddress.websiteAddress.label" default="Website Address" /><b>:</b>
                    </label>
                    <g:textField tabindex="17" placeholder="${g.message(code: 'customerGeneralAddress.websiteAddress.label')}" name="websiteAddress" value="${vendorGeneralAddressInstance?.website_address}"/>
                </div>
            </div>

         </div>  <!--fieldContainerRight-->
       </div>  <!--fieldContainer-->

        <div class="textAreaContainer">
            <div class="fieldContainer fcTextArea textBottomArea">
                <label for="comments">
                    <g:message code="vendorMaster.comments.label" default="Comments"/><b>:</b>
                </label>
                <g:textArea tabindex="18" name="comments" onkeyup="autoresize(this)" value="${vendorMasterInstance?.comments}"/>
            </div>
        </div>
    </div>  <!--slideOptionDiv-->
    </div> <!--fieldContainer-->

    </div>
</sec:ifNotGranted><!--lowerContainer-->
</div> <!--main container-->
%{--<!--mc end--->--}%
%{--end my code now--}%

<g:hiddenField name="id" value="${vendorMasterInstance?.id}"/>
<g:hiddenField name="currCode" value="EUR"/>
<g:hiddenField name="creditStatus" value="Good History"/>

<% } else {
%>

<!--Basic Code Start-->

<div class="main-container">
    <div class="fieldContainerLeft">

        <div class="rowContainer">
            <div class="fieldContainer required fcInputText borderRed">
                <label for="vendorName">
                    <g:message code="vendorMaster.vendorName.label" default="Vendor Name"/><b>:</b>
                    <span class="required-indicator">*</span>
                </label>
                <g:textField placeholder="${g.message(code:'vendorMaster.vendorName.label')}" name="vendorName" required="" value="${vendorMasterInstance?.vendor_name}"/>
            </div>
        </div>

        <div class="rowContainer">
            <div class="fieldContainer fcCombo borderRed">
                <label for="vat">
                    <g:message code="customerMaster.vat.label" default="Vat"/><b>:</b>
                </label>
                <%
                    def vatCategory = new CoreParamsHelperTagLib().getVatCategory('vat', (vendorMasterInstance?.vat) ? (Integer.parseInt(vendorMasterInstance?.vat)) : '0')
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
                </select>            </div>
        </div>
    </div> <!--fieldContainerLeft-->

    <div class="fieldContainerRight">
        <div class="rowContainer">
            <div class="fieldContainer required fcInputNumber" >
                <label for="defaultGlAccount">
                    <g:message code="vendorMaster.creditLimit.label" default="Credit Limit"/><b>:</b>
                </label>
                <g:field type="number" name="creditLimit"  tabindex="2" onfocus="clearPlaceHolder(this)" onblur="setPlaceHolder(this)"
                         value="1000.0"/>
            </div>
        </div>



        <div class="rowContainer">
            <div class="fieldContainer fcCombo borderRed">
                <label for="payment_term">
                    <g:message code="vendorMaster.payment_term.label" default="Payment term"/><b>:</b>
                    <span class="required-indicator">*</span>
                </label>
                <%
                        def paymentTerm = new CoreParamsHelperTagLib().paymentTermDropdownList("payment_term.id", (vendorMasterInstance?.payment_term_id) ? (vendorMasterInstance?.payment_term_id) : '2')
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


<% if (params.expBItem) { %>
<div class="fieldcontain ${hasErrors(bean: vendorBankAccountInstance, field: 'vendorType', 'error')} required"
     style="width: 50%;">
    <label for="vendorType" style="width: 150px;">
        <g:message code="vendorType.Type.label" default="Type"/>
        <span class="required-indicator">*</span>
    </label>
    <% if (params.sid == 'sn') { %>
    <%="${new CoreParamsHelperTagLib().CoreParamsDropDown('VENDOR_TYPE', 'vendorType', params.sid)}"%>
    <% } else { %>
    <% if (params.expBItem) { %>
    <%="${new CoreParamsHelperTagLib().CoreParamsDropDownNotSnVendorType('VENDOR_TYPE', 'vendorType', "bn")}"%>
    <% } else if (params.sid) { %>
    <%="${new CoreParamsHelperTagLib().CoreParamsDropDownShopType('VENDOR_TYPE', 'vendorType', "sn")}"%>
    <% } else if (params.fInv) { %>

    <%="${new CoreParamsHelperTagLib().CoreParamsDropDown('VENDOR_TYPE', 'vendorType', "vn")}"%>
    <% } else { %>
    <%=
            "${new CoreParamsHelperTagLib().CoreParamsDropDown('VENDOR_TYPE', 'vendorType', "${vendorMasterInstance?.vendor_type}")}"%>
    <% }
    } %>

</div>
<% } %>

<g:hiddenField name="id" value="${vendorMasterInstance?.id}"/>
<g:hiddenField name="creditStatus" value="Good History"/>
<g:hiddenField name="currCode" value="EUR"/>

<% if (params.fInv) { %>
<g:hiddenField name="bookInvoiceId" value="${params.bookInvoiceId}"/>
<g:hiddenField name="bookingPeriod" value="${params.bookingPeriod}"/>
<g:hiddenField name="vendorId" value="${params.vendorId}"/>
<g:hiddenField name="budgetItemDetailsId" value="${params.budgetItemDetailsId}"/>
<g:hiddenField name="fInv" value="${params.fInv}"/>
<% } %>

<% if (params.sid) { %>
<g:hiddenField name="bookInvoiceId" value="${params.bookInvoiceId}"/>
<g:hiddenField name="bookingPeriod" value="${params.bookingPeriod}"/>
<g:hiddenField name="vendorId" value="${params.vendorId}"/>
<g:hiddenField name="budgetItemDetailsId" value="${params.budgetItemDetailsId}"/>
<g:hiddenField name="sid" value="${params.sid}"/>
<% } %>

<% if (params.expBItem) { %>
<g:hiddenField name="bookingPeriod" value="${params.bookingPeriod}"/>
<g:hiddenField name="vendorId" value="${params.vendorId}"/>
<g:hiddenField name="journalId" value="${params.journalId}"/>
<g:hiddenField name="expBItem" value="${params.expBItem}"/>
<% } %>

<% } %>