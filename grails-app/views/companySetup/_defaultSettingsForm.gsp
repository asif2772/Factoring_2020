<%@ page import="factoring.CoreParamsHelperTagLib; org.grails.plugins.web.taglib.ValidationTagLib;" %>
<div class="companySetup">
    <div class="main-container">
        <div class="upperContainer" style="height: 148px;margin-bottom: 24px;">

            <div class="fieldContainerLeft">

                <div class="rowContainer">
                    <div class="fieldContainer fcCombo borderRed">
                        <label for="LANGUAGE">
                            <g:message code="companySetup.LANGUAGE.label" default="LANGUAGE"/>
                        </label>
                        <%="${new CoreParamsHelperTagLib().CoreParamsDropDown('LANGUAGE', 'LANGUAGE', "${companySetupInstance?.language}")}"%>
                    </div>
                </div>

                %{--<div class="rowContainer">--}%
                    %{--<div class="fieldContainer required fcCombo borderRed">--}%
                        %{--<label for="currency">--}%
                            %{--<g:message code="bv.companySetup.currency.label" default="Default Currency"/>--}%
                            %{--<span class="required-indicator">*</span>--}%
                        %{--</label>--}%
                        %{--<%="${new CoreParamsHelperTagLib().getCurrencyDropDown('currency.id', "${companySetupInstance?.currency_id}")}"%>--}%
                    %{--</div>--}%
                %{--</div>--}%


             </div>  <!--fieldContainerLeft-->


            <div class="fieldContainerRight">

                <div class="rowContainer">
                    <div class="fieldContainer required fcCombo borderRed">
                        <label for="defaultVat">
                            <g:message code="bv.companySetup.vatCategory.label" default="Default Vat"/>
                            <span class="required-indicator">*</span>
                        </label>
                        <%="${new CoreParamsHelperTagLib().getVatCategory('defaultVATId', companySetupInstance?.vat_category_id)}"%>
                    </div>
                </div>

            <div class="rowContainer">
                <div class="fieldContainer fcSpecialTextBox borderRed">
                    <label for="taxReservation">
                        <g:message code="bv.companySetup.taxReservation.label" default="Tax Reservation"/>
                    </label>
                    <g:textField tabindex="11" placeholder="${g.message(code: 'bv.companySetup.taxReservation11.label')}" name="incomeTaxReservation" value="${companySetupInstance?.income_tax_reservation}"/>
                    <span class="spcialBtn" style=" margin-right: 0;margin-top: 16px;">&nbsp;%</span>
                </div>
            </div>



            </div> <!--fieldContainerRight-->

            <div class="fieldContainerRight">
                <div class="rowContainer">
                    <div class="fieldContainer required fcCombo">
                        <label for="Payment Term">
                            <g:message code="bv.companySetup.paymentTerm.label" default="Payment Term"/>
                            <span class="required-indicator">*</span>
                        </label>
                        <%="${new CoreParamsHelperTagLib().paymentTermDropdownList("paymentTermId", companySetupInstance?.payment_term_id)}"%>
                        %{--<%="${new CoreParamsHelperTagLib().CoreParamsDropDown('INVOICE_FREQUENCE', 'paymentTerm.id', "${companySetupInstance?.payment_term_id}")}"%>--}%
                    </div>
                    </div>

                    %{--<div class="rowContainer">--}%
                        %{--<div class="fieldContainer fcCombo">--}%
                            %{--<label>--}%
                                %{--<g:message code="bv.companySetup.momentOfSendInvoice.label" default="Invoice S. Moment"/>--}%
                            %{--</label>--}%
                            %{--<%="${new CoreParamsHelperTagLib().CoreParamsDropDown('CREDIT_STATUS', 'momentOfSendingInvoice', "${companySetupInstance?.moment_of_sending_invoice_id}")}"%>--}%
                            %{--<%="${new CoreParamsHelperTagLib().getMomentOfSendingInvoice('momentOfSendingInvoice.id', companySetupInstance?.moment_of_sending_invoice_id)}"%>--}%
                        %{--</div>--}%
                    %{--</div>--}%

            </div> <!--fieldContainerRight-->

        </div>   <!---upperContainer-->

%{----####-----End upperContainer-----#######------}%

    <div class="lowerContainer">

        %{--<div class="contactPersnLabel">--}%
        <div class="fieldContainer contactLine" style="line-height: 0; margin-bottom: 20px; ">
            <label class="">Reporting</label>
        </div>

        <div class="fieldContainerLeft">
            <div class="rowContainer">
                    <div class="fieldContainer required fcCombo">
                        <label for="reportPageSize">
                            <g:message code="companySetup.reportPageSize.label" default="Report Page Size"/>
                        </label>
                        <%="${new CoreParamsHelperTagLib().PageSizeDropDown('reportPageSize', "${companySetupInstance?.report_page_size}",false)}"%>
                    </div>
                </div>
         </div>  <!--fieldContainerLeft-->

        <div class="fieldContainerRight">
            <div class="rowContainer">
                <div class="fieldContainer required fcCombo">
                    <label for="showGlcodeInReport">
                        <g:message code="companySetup.showGlcodeInReport.label" default="Show Gl code In Report"/>
                        <span class="required-indicator">*</span>
                    </label>
                    <% def g=new ValidationTagLib() %>
                    <g:select tabindex="14" selected="true" name="showGlcodeInReport"  from="${[g.message(code:'bv.comboOption.yes.label'), g.message(code:'bv.comboOption.no.label')]}" value="${companySetupInstance?.show_glcode_in_report}"/>

                </div>
            </div>
        </div>   <!--fieldContainerRight-->

        <div class="fieldContainerRight">
            <div class="rowContainer">
                <div class="fieldContainer required fcCombo">
                    <label for="itemNumberMention">
                        <g:message code="bv.companySetup.showItemcodeInReport.label" default="Item # on Report"/>
                        <span class="required-indicator">*</span>
                    </label>
                    <g:select tabindex="15" selected="true" name="itemNumberMention" from="${[g.message(code:'bv.comboOption.yes.label'), g.message(code:'bv.comboOption.no.label')]}" value="${companySetupInstance?.show_glcode_in_report}"/>
                </div>
            </div>
        </div>   <!--fieldContainerRight-->
    </div>   <!---lowerContainer-->

    </div>  <!--main-container-->

</div>  <!-- companySetup -->