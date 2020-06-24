<%@ page import="factoring.CoreParamsHelperTagLib; factoring.CompanySetup" %>

<% if(companySetupInstance){%>
<div id="company-address" class="companySetup">
    <div class="main-container">
            <div class="fieldContainerLeft">
                <div class="rowContainer">
                    <div class="fieldContainer required fcInputText borderRed">
                        <label for="companyFullName">
                            <g:message code="companySetup.companyFullName.label" default="Company Full Name"/>
                        </label>
                        <g:textField tabindex="1" required="" placeholder="${g.message(code: 'companySetup.companyFullName.label')}" name="companyFullName" value="${companySetupInstance?.company_full_name}"/>
                     </div>
                </div>

                <div class="rowContainer">
                    <div class="fieldContainer fcInputText">
                        <label for="chamberCommerceNo">
                            <g:message code="companySetup.chamberOfCommerceNo.label" default="Cham Of Commerce"/>
                        </label>
                        <g:textField tabindex="3" placeholder="${g.message(code: 'vendorMaster.chamOfCommerce.label')}" name="chamberCommerceNo" value="${companySetupInstance?.chamber_commerce_no}"/>
                    </div>
                </div>

                <div class="rowContainer">
                    <div class="fieldContainer fcInputText">
                        <label for="iban">
                            <g:message code="companyBankAccounts.iban.label" default="IBAN"/>
                        </label>
                        <g:textField tabindex="3" placeholder="${g.message(code: 'companyBankAccounts.iban.label')}" name="iban" value="${companySetupInstance?.iban}"/>
                    </div>
                </div>
            </div>  <!--fieldContainerLeft-->

            <div class="fieldContainerRight">
                <div class="rowContainer">
                    <div class="fieldContainer fcInputText">
                        <label for="companyShortName">
                            <g:message code="companySetup.shortName.label" default="Company Short Name"/>
                        </label>
                        <g:textField tabindex="2" placeholder="${g.message(code: 'companySetup.shortName.label')}" name="companyShortName" value="${companySetupInstance?.company_short_name}"/>
                    </div>
                </div>


                <div class="rowContainer">
                    <div class="fieldContainer fcInputText">
                        <label for="vatCertificate">
                            <g:message code="companySetup.vatNo.label" default="Vat Certificate"/>
                        </label>
                        <g:textField tabindex="4" placeholder="${g.message(code: 'companySetup.vatNo.label')}" name="vatNo" value="${companySetupInstance?.vat_no}"/>
                    </div>
                </div>

                <div class="rowContainer">
                    <div class="fieldContainer fcInputText logoImageDiv">
                        <label for="logo">
                            <g:message code="companySetup.company.logo.label" default="Company Logo"/>
                        </label>
                        <% if (companySetupInstance.logo_file) {%>
                        <input type="file" name="logoFileSelector" value="${companySetupInstance?.logo_file}"/>
                        <input type="hidden" name="logoFile" value="${companySetupInstance?.logo_file}" />
                        <% }else {%>
                        <input type="file" name="logoFile" value=""/>
                        <% }%>
                    </div>
                </div>
              </div> <!--fieldContainerRight-->

        <div class="fieldContainerRight">
            <div class="rowContainer">
                <div class="fieldContainer fcInputText">
                    <label for="bic">
                        <g:message code="companySetup.bic.label" default="BIC"/>
                    </label>
                    <g:textField tabindex="3" placeholder="${g.message(code: 'companySetup.bic.label')}" name="bic" value="${companySetupInstance?.bic}"/>
                </div>
            </div>

            <div class="rowContainer">
               <div class="fieldContainer fcInputText">
                    <label for="taxCertificate">
                        <g:message code="companySetup.taxNo.label" default="Tax Certificate"/>
                    </label>
                    <g:textField tabindex="5" placeholder="${g.message(code: 'companySetup.taxNo.label')}" name="taxNo" value="${companySetupInstance?.tax_no}"/>
                </div>
            </div>

        <div class="rowContainer">
            <div class="fieldContainer fcInputText">
                <label for="taxCertificate">
                    <g:message code="companySetup.paymentIban.label" default="Payment IBAN"/>
                </label>
                <g:textField tabindex="5" placeholder="${g.message(code: 'companySetup.paymentIban.label')}" name="paymentIban" value="${companySetupInstance?.payment_iban}"/>
            </div>
        </div>
        </div>
   </div>
</div>

<% } else{%>
<div class="rowContainer">
    <div class="fieldContainer fcInputText">
        Company setup instance null.
    </div>
</div>
<%}%>
%{--<% } %>--}%
%{--####   End MC ####--}%
