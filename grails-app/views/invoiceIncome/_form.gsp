<%@ page import="java.text.SimpleDateFormat; factoring.BudgetViewDatabaseService; factoring.PaymentTerms; factoring.CustomerMaster; factoring.CoreParamsHelperTagLib; factoring.InvoiceIncome;" %>

<g:javascript>

    $(document).ready(function () {
        // $( "#transDate" ).datepicker();
        $('#transDate').datepicker({ dateFormat: 'dd-mm-yy' }).val();
    });

    function setPaymentRefToSession(){
        saveDataToSession();
        //alert("this.value"+$('#paymentRef').val());
    }

</g:javascript>

<%
    def ActiveFiscalYear = new CoreParamsHelperTagLib().getActiveFiscalYear()
    def FiscalYearInfo = new CoreParamsHelperTagLib().getActiveFiscalYearInformation(ActiveFiscalYear)
    def contextPath = request.getServletContext().getContextPath()
%>

<div class="navigation">
    <div class="incomeExpenseWizard">
        <div class="navigationbtn" style="float: right;">
            %{--<g:link controller="uploadPdf" action="uploadPdf" ><g:message code="bv.uploadPDF.label" default="Upload PDF" /></g:link>--}%
        </div>
    </div>
</div> <!--navigation-->

<div id="msgdivAnother" class="paymntRefMessage"></div>
<div id="cusDebInactiveMsg" class="alertMsg">
    Attention! Selected debtor is inactive.
</div>
<div class="formInvoiceField">

    <div class="formInvoiceContainer">
        <div class="fieldContainerLeft">
            <div class="rowContainer">
                <div class="fieldContainer required fcSpecialCombo">
                    <label for="customerId">
                        <g:message code="bv.invoiceExpense.vendorId.label" default="Vendor Name"/><b>:</b>
                        <span class="required-indicator">*</span>
                    </label>

                    <%
                        def customerId = 0
                        if (params.editId) {
                            customerId = params.customerId
                        }else {

                            if (params.newCusId && params.newCusId !='') {
                                customerId = params.newCusId
                            }else {
                                if(params.keepSession == "1"){
                                    customerId = session.selCustomerId;
                                }
                            }
                        }
                    %>
                    <%
                        def cusListFromDebCus = new CoreParamsHelperTagLib().getCustomerListDropDownFromDebtorCustomer('customerId', customerId, contextPath)
                    %>
                    <select onchange="selectVATDropDown();" class="styled sidebr01" name="customerId" id="customerId">
                    %{-- <option value="">- no select -</option>--}%
                        <g:each in="${cusListFromDebCus}" var="opt">
                            <g:if test="${cusListFromDebCus.size() > 0}">
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

                        <g:link controller="debtorCustomer" action="index"
                                params="[invEditId:params.editId,customerId: customerId,fInv: 1]"
                                title='${message(code: 'bv.invoiceIncome.addADebtorCustomer.blank', default: 'Add New Debtor Customer')}'>
                            <span class="additionSign"></span>
                        </g:link>
                </div>

            </div>

            <div class="rowContainer">
                <div class="fieldContainer required fcInputText borderRed">
                    <label for="paymentRef">
                        <g:message code="bv.invoiceIncome.paymentRef.label" default="Payment Reference"/>
                        <span class="required-indicator">*</span>
                    </label>
                    <g:textField name="paymentRef" required="" id="paymentRef"
                                 oninvalid="this.setCustomValidity('${message(code: 'bv.invoiceIncome.paymentRef.blank', default: 'Please give Payment Reference')}')"
                                 oninput="setCustomValidity('')"
                                 onblur="setPaymentRefToSession()"
                                 title="${message(code: 'bv.invoiceIncome.paymentRef.blank', default: 'Please give Payment Reference')}"
                                 value="${invoiceIncomeInstance?.paymentRef}" style="border: 1px solid #ffaaaa"
                                 placeholder="${g.message(code: 'bv.invoiceExpense.paymentRef.label')}"/>
                </div>
            </div>

            <div class="rowContainer">
                <div class="fieldContainer required fcInputText borderRed">
                    <label>
                        <g:message code="bv.invoiceIncome.transDate.label" default="Invoice Date"/>
                        <span class="required-indicator">*</span>
                    </label>
                    <%
                            if(!invoiceIncomeInstance?.transDate) {
                                Date today = new Date()
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(today);

                                int bookingDay = cal.get(Calendar.DAY_OF_MONTH);
                                int bookingPeriod = cal.get(Calendar.MONTH) + 1;//Integer.parseInt(params.bookingPeriod);
                                //println("Day: "+bookingDay + " Month: "+bookingPeriod + " Date :"+today.getDateString());
//                                def transDate =  "15-" + (bookingPeriod>9 ? bookingPeriod : ("0"+ bookingPeriod))  + '-' + FiscalYearInfo[0][4];
                                def dueDate =  bookingDay + '-' + (bookingPeriod>9 ? (bookingPeriod + 1) : ("0"+ (bookingPeriod + 1)))  + '-' + FiscalYearInfo[0][4];
//                                invoiceIncomeInstance.transDate = new SimpleDateFormat("dd-MM-yyyy").parse(transDate);
                                invoiceIncomeInstance.dueDate = new SimpleDateFormat("dd-MM-yyyy").parse(dueDate);
                            }
                    %>
                    <% if(invoiceIncomeInstance?.transDate == "") {
                        Date today = new Date()
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(today);
                        int bookingDay = cal.get(Calendar.DAY_OF_MONTH);
                        int bookingPeriod = cal.get(Calendar.MONTH) + 1 //Integer.parseInt(params.bookingPeriod);
                        def transDate =  bookingDay + '-' + bookingPeriod + '-' + FiscalYearInfo[0][4];
                        invoiceIncomeInstance.transDate = new SimpleDateFormat("dd-MM-yyyy").parse(transDate);
                    %>
%{--                        <bv:datePicker id="transDate" name="transDate" value="${invoiceIncomeInstance?.transDate}"/>--}%
                    <g:textField type="text" name="transDate" required="" id="transDate"
                                 value="${invoiceIncomeInstance?.transDate}" style="border: 1px solid #ffaaaa"/>
                    <% }else { %>
%{--                        <bv:datePicker id="transDate" name="transDate" value="${invoiceIncomeInstance?.transDate}"/>--}%
                    <g:textField type="text" name="transDate" required="" id="transDate"
                                 value="${invoiceIncomeInstance?.transDate}" style="border: 1px solid #ffaaaa"/>
                    <% } %>
%{--                    <bv:datePicker id="transDate" name="transDate" value="${(invoiceIncomeInstance?.transDate) ? (invoiceIncomeInstance?.transDate) : ''}"/>--}%
                </div>
            </div>


            <div class="rowContainer">
                <div class="fieldContainer required fcCombo">
                    <label>
                        <g:message code="budgetItemIncome.bookingPeriodForChange.label" default="Booking Period"/>
                        <span class="required-indicator">*</span>
                    </label>
                    <%
                        def bookingPeriod = new CoreParamsHelperTagLib().showBookingPeriod('bookingPeriod',invoiceIncomeInstance?.bookingPeriod)
                    %>
                    <select class="styled sidebr01" name="bookingPeriod" id="bookingPeriod">
                    %{-- <option value="">- no select -</option>--}%
                        <g:each in="${bookingPeriod}" var="opt">
                            <g:if test="${bookingPeriod.size() > 0}">
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

        </div>

        <div class="fieldContainerRight">

            <div class="rowContainer">
                <div class="fieldContainer required fcSpecialCombo">
                    <label for="customerId">
                        <g:message code="bv.invoiceIncome.customerId.label" default="Debtor Name"/><b>:</b>
                        <span class="required-indicator">*</span>
                    </label>
                    <div class="fieldContainer required fcSpecialCombo" id="debtorList"></div>
                    <div id="debtorActiveInactive" hidden><p><span style="color:red">Debtor is Inactive</span></p></div>
                </div>
            </div>  <!--rowContainer-->

            <div class="rowContainer">
                <div class="fieldContainer required fcCombo">
                    <label for="customerId">
                        <g:message code="bv.invoiceIncome.documentChecked.label" default="Document Checked"/><b>:</b>
                        <span class="required-indicator">*</span>
                    </label>

                    <%
                        def yesNoSelection = new CoreParamsHelperTagLib().getYesNoSelectionDropDown("documentChecked", invoiceIncomeInstance?.allDocsOk)
                    %>
                    <select class="styled sidebr01" name="documentChecked" id="documentChecked">
                    %{-- <option value="">- no select -</option>--}%
                        <g:each in="${yesNoSelection}" var="opt">
                            <g:if test="${yesNoSelection.size() > 0}">
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
            </div>  <!--rowContainer-->

            <div class="rowContainer">
                <div class="fieldContainer required fcCombo">
                    <label for="terms">
                        <g:message code="invoiceIncome.terms.label" default="Terms"/>
                        <span class="required-indicator">*</span>
                    </label>

                    <%
                        def paymentTerm = new CoreParamsHelperTagLib().paymentTermDropdownList("paymentTerm.id", invoiceIncomeInstance?.termsId)
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
                <div class="fieldContainer">
                    <label for="customerId">
                        <g:message code="bv.invoiceIncome.debtor.remainingInsuranceAmount.label" default="Remaining Insurance Amount"/><b>:</b>
                    </label>

                    <div id="insuadAmountDebtor"></div>
                </div>
            </div>  <!--rowContainer-->

            <div class="rowContainer">
            <div class="rowContainer">
                <div class="fieldContainer">
                    <label for="customerId">
                        <g:message code="bv.invoiceIncome.customer.remainingInsuranceAmount.label" default="Remaining Insurance Amount"/><b>:</b>
                    </label>
                    %{--<p id="insuadAmount" class="labelCode" style="margin-top:18px;font-size:16px;color:'#FF0000'">€ 5000</p>--}%
                    <div id="insuadAmountCustomer"></div>
                </div>
            </div>  <!--rowContainer-->
            </div>

            <div class="rowContainer">
                <div class="fieldContainer required fcInputText borderRed">
                    <label for="dueDate">
                        <g:message code="invoiceIncome.dueDate.label" default="Due Date"/>
                        <span class="required-indicator">*</span>
                    </label>

%{--                    <bv:datePicker name="dueDate" value="${invoiceIncomeInstance?.dueDate}"/>--}%
                </div>
            </div>
        </div>  <!--fieldContainerRight-->
    </div>  <!--labelFieldContainer-->
</div>   <!--labelFieldDetail-->


%{--######## /////////// End new Code /////////// #############--}%
<g:hiddenField name="reverseByInvoiceId" type="number" value="0" required="" style="width: 117px"/>
%{--<g:hiddenField name="bookingPeriod" value="${bookingPeriod}"/>--}%
<g:hiddenField name="bookingYear" value="${FiscalYearInfo[0][4]}"/>
<g:hiddenField name="comments" value="test"/>

<% if (params.editId) { %>
<g:hiddenField name="InvoiceIncomeEditId" value="${params.editId}"/>
<g:hiddenField name="editId" value="${params.editId}"/>
<% } %>

<div id="tableHeadForQuickEntry" style="float: left; width: 1000px">
    %{--<table class="createReceipt tableLeftSpace">--}%
    <table class="createReceipt tableLeftSpace">
        <thead>
        <tr>
            <th width="289"><g:message code="bv.invoiceIncomeDetails.accountCode.label" default="Account Head"/> <span class="required-indicator">*</span></th>
            <th width="234"><g:message code="bv.InvoiceExpenseDetails.note.label" default="Note"/></th>
            <th width="150"><g:message code="budgetItemIncomeDetails.vatCategoryId.label" default="Vat Category"/><span class="required-indicator">*</span></th>
            <th width="91" style="text-align: right;"><g:message code="bv.invoiceIncomeDetails.unitPrice.label" default="Price"/></th>
            <th width="80" style="text-align: right;"><g:message code="bv.InvoiceExpenseDetails.vat.label" default="VAT"/></th>
            <th width="" style=" padding-left: 15px;text-align: center;"><g:message code="invoiceIncome.action.label" default="Action"/></th>
        </tr>
        </thead>
    </table>
</div>

<div id="updateItemList" style="float: left; width: 1000px">
    <g:render template="linelist"/>
</div>


