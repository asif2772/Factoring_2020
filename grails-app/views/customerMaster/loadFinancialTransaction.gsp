<%@ page import="bv.CoreParamsHelperTagLib;java.text.SimpleDateFormat;" %>
<% def contextPath = request.getServletContext().getContextPath()%>
<script type="text/javascript">
    function isWriteOffClicked(){
        var isValid = false;
        $('input:checked').each(function() {
            $("#writeOffLoader").show();
            isValid = true;
        });
        return isValid;
    }
    (function(){
        $('#debtorFinTransTable').dataTable({
            "paging":   false,
            "aoColumnDefs" : [ {
                "bSortable" : false,
                "aTargets" : [ "no-sort" ]
            } ]
        });
        setTimeout(function () {
            $("#msgdiv").hide('blind', {}, 1000);
        }, 3000);

        $("#transDate").datepicker({
            dateFormat: 'dd-mm-yy',
            showOn: "button"}).datepicker("setDate", new Date());
        var sendData = {debtorId:${customerMasterInstance?.id}};
        var appContextUrl = '${request.contextPath}';
        var bankLoadDataUrl = appContextUrl+"/customerMaster/loadBankDataInfo";
        $.ajax({
            url: bankLoadDataUrl,
            data: sendData,
            success: function(data) {
                if(data)
                    $("#tabpage_6 #loadBankDataInfo").html(data);
            },
            error: function(){
            }
        });
    })();

    function checkMoveHistoryAll() {
        if($("#checkAll").is(':checked'))
            $(".moveHistoryCheck").prop('checked', true);
        else
            $(".moveHistoryCheck").prop('checked', false);

    }

    function redirectMoveList() {
        $("#tabPageLoadData9").html($("#cusLoadSpinner").html());
        isAlreadyLoaded[9] = false;
        isAlreadyLoaded[6] = false;
        $("#tabHeader_9").click()
    }

</script>

<g:formRemote name="writeOffForm" url="[controller:'customerMaster',action:'writeOffInvoicePaymentCustomer']" >
    <%
        def rowNo = 0;
    %>
    <p class="ashBtnDiv" style="float: right; margin-right: -99px; padding-right: 120px; margin-bottom: 15px;">
        <g:submitToRemote class="ashBtn" style="line-height: 22px;" onerror="redirectList()" onSuccess="redirectMoveList()" url="[controller:'customerMaster',action:'moveHistory']" value="Move History"/>
    </p>
    <div class="InvoiceAmountTabl">

        %{--1st--}%
        <% if(invoiceCustomerDetailsInstance !=[] ){ %>

        <div id="tableHeadForQuickEntry" class="financeTranTablHead">
            %{--<hr style="color: #fdfcfc;  margin-bottom: -1px;">--}%
            <table id="debtorFinTransTable" class="createReceipt createReceiptFinanceHead" width="100%" style="border: 1px solid #fdfcfc;">
                <thead>
                <tr>
                    <th width="120" style="padding-left: 20px;"><g:message code="invoiceExpense.gridList.invoiceNumber.label"  default="Invoice Number"/></th>
                    <th width="120"><g:message code="customerMaster.short.paymentRefference.label" default="Payment Ref"/></th>
                    <th width="120"><g:message code="bv.autoReconciliationOpenInvoicesTransactionDate.label" default="Transaction Date"/></th>
                    <th width="120"><g:message code="report.aging.dueDate.label" default="Due Date"/></th>
                    <th width="120" style="text-align: center"><g:message code="report.aging.lastPaymentDate.label" default="LastPayment"/></th>
                    <th width="125" style="text-align: right;"><g:message code="report.aging.invoiceAmount.label" default="Invoice Amount"/></th>
                    <th width="120" style="text-align: right"><g:message code="report.aging.dueAmount.label" default="Due Amount"/></th>
                    <th width="20" class="no-sort" style="">&nbsp;</th>
                    <th width="80"><g:message code="report.aging.paidStatus.label" default="Paid Status"/></th>
                    <th width="80" class="no-sort"><g:message code="report.aging.moveHistory.label" default="Move History"/></th>
                    <th width="20" class="no-sort"><input style="margin-top: 7px;" onclick="checkMoveHistoryAll()" name="checkAll" id="checkAll" type="checkbox"></th>

                </tr>
                </thead>

                <tbody>
                <g:each in="${invoiceCustomerDetailsInstance}" status="i" var="invoiceCustomerDetails">
                    <%
                            BigDecimal showInvoiceAmount = new BigDecimal(invoiceCustomerDetails[4])
                            BigDecimal showPaidAmount = new BigDecimal(invoiceCustomerDetails[5])
                            BigDecimal showDueAmount = new BigDecimal(invoiceCustomerDetails[6])

                            def notPaidFlag = false;
                            Double dInvAmount = showInvoiceAmount.toDouble();
                            Double dPaidAmount = showPaidAmount.toDouble();
                            Double dDueAmount = showDueAmount.toDouble();
                            String strPaidStatus = "Not Paid";
                            if(dDueAmount == 0.00){
                                strPaidStatus = "Paid";
                            }
                            else if(dDueAmount < 0.00 && dInvAmount < dPaidAmount){
                                strPaidStatus = "Over Paid";
                                notPaidFlag = true
                            }else{
                                notPaidFlag = true;
                            }

                            def bookingMonth = new CoreParamsHelperTagLib().monthNameShow(Integer.parseInt(invoiceCustomerDetails[3]))
                    %>
                    <tr style="background-color: #f9f9f9;">
                        <td width="115" style="padding-left: 20px;">${invoiceCustomerDetails[0]}</td>
                        <td width="110">
                            <g:link style="color: #1870b3;text-align: left;" action="outstandingInvoicesDetails"  controller="reportCustomerSettlement" id="${invoiceCustomerDetails[7]}" >${invoiceCustomerDetails[1]}</g:link>
                        </td>
                        <td width="110">${new SimpleDateFormat("dd MMM yyyy").format(invoiceCustomerDetails[2])}</td>
                        <td width="115">${new SimpleDateFormat("dd MMM yyyy").format(invoiceCustomerDetails[9])}</td>
                        <td width="105"><g:if test="${invoiceCustomerDetails[10]}">${new SimpleDateFormat("dd MMM yyyy").format(invoiceCustomerDetails[10])}</g:if></td>
                        <td width="100" style="text-align: center"><bv:euroDotCommaDecimalFormat decimalData="${showInvoiceAmount.setScale(2, BigDecimal.ROUND_HALF_UP)}" /></td>
                        <td width="105" style="text-align: center"><bv:euroDotCommaDecimalFormat decimalData="${showDueAmount.setScale(2, BigDecimal.ROUND_HALF_UP)}" /></td>
                        <% if(notPaidFlag){ %>
                        <td width="20" style="text-align: right"><input type="checkbox" name = "invBankStatement" value="${rowNo}"/></td>
                        <% }else{ %>
                        <td width="20" style="text-align: right"></td>
                        <%}%>
                        <td width="80" style="text-align: center;">${strPaidStatus}</td>
                        <td width="80" style="text-align: center;">
                            <% if( strPaidStatus != 'Not Paid') {%>
                            <input class="moveHistoryCheck" type="checkbox" name="checkMoveHistory" value="${invoiceCustomerDetails[7]}" id="checkMoveHistory"/>
                            <%}%>
                        </td>
                        <td></td>
                    </tr>
                    <% rowNo++; %>
                </g:each>
                </tbody>
            </table>
        </div>


        <div class="fieldContainer fieldContainerRightOffDiv" style="display: block !important;height: 70px;">
            <div class="fieldContainerRightOffDiv wrightOffDateDiv" style="">

                <div class="fieldContainer onStyle">
                    <g:message code="vendorCustomerMasterOn.label" default="Booking Date On"/>
                </div>

                <div  class="required fieldContainer manualEntryDatePick datePicVenCus">
                    <input type="text" id="transDate" name="transDate">
                </div>
                %{--<div class="fieldContainerRightOffDiv writeOff2Button" style="padding-right: 0px;">
                    <div class="fieldContainer updateLinkBtn ashBtnDiv writeOffBtn" style="margin: 25px 10px 0px;">
                        <g:submitToRemote style="width:100px;" class="ashBtn" action="writeOffInvoicePaymentCustomer" update="tabpage_6" value="${message(code: 'customerMaster.financeTranTab.writeOff.label', default: 'Write Off')}"/>
                        <input type="text" id="writeOffAmount" name="writeOffAmount" style="width:100px;margin-left: 6px;" placeholder="0.00">
                    </div>
                </div>--}%
                <div class="fieldContainerRightOffDiv writeOff2Button" style="padding-right: 0px;">
                    <div class="fieldContainer updateLinkBtn ashBtnDiv writeOffBtn" style="margin: 25px 10px 0px;">
                        <img id="writeOffLoader" style="display: none;margin-left: -22px;" src="${assetPath(src:'spinner.gif')}" alt="${message(code:'spinner.alt.Loading',default:'Loading...')}" />
                        <g:submitToRemote before="if(isWriteOffClicked()){" after="}" style="width:100px;" class="ashBtn" action="writeOffInvoicePaymentCustomer" update="tabpage_6" value="${message(code: 'customerMaster.financeTranTab.writeOff.label', default: 'Write Off')}"/>
                        <g:link class="ashBtn" style="line-height: 22px;display:inline-block;position: relative;top: 1px;" action="showUndoWriteOff"  id="${customerMasterInstance?.id?customerMasterInstance?.id:cusId}" ><g:message code="customerMaster.undo.writeoff.label" default="Undo Write Off" /></g:link>
                        <div class="" style="padding-top: 10px; padding-bottom: 10px;width: 262px;">
                            <input type="text" id="writeOffAmount" name="writeOffAmount" placeholder="0.00">
                        </div>
                        <div class="" style="padding-bottom: 10px;width: 262px;">
                            <input type="text" class="" style="width:220px" name="writeOffDescription" id="writeOffDescription" placeholder="Description">
                        </div>
                    </div>
                </div>

                <%}else{%>
                <div class="fieldContainer" style="text-align:center;margin-top: 11px;font-weight:bold;font-size: 14px;">
                    <g:message code="vendorMaster.financialTab.paidInvoice.label"/>
                </div>
                <%}%>

            </div>


        </div>  <!--fieldContainer-->

    </div>

    <g:hiddenField name="id" value="${customerMasterInstance?.id?customerMasterInstance?.id:cusId}" />
    <g:hiddenField name="customerPrefix" value="${customerPrefix?customerPrefix:cusPrefix}" />
    <g:hiddenField name="customerCode" value="${customerMasterInstance?.customer_code?customerMasterInstance?.customer_code:cusCode}" />
    <g:hiddenField name="customerName" value="${customerMasterInstance?.customer_name?customerMasterInstance?.customer_name:cusName}" />
</g:formRemote>


%{--2nd Table--}%


<div class="InvoiceAmountTabl" style="margin-top: 40px;">
    <div style="clear:both"></div>
    <p style="font-weight: bold;font-size: 14px;margin-bottom: 7px; margin-top: 17px;">
        <g:message code="customerMaster.financeTranTableHeader2.label" default="Bank Transaction That were Reconciliated for These Invoice"/>
    </p>
    <div id="loadBankDataInfo">
        <div class="spinLoader fieldContainer">
            <img src="${resource(dir:'images',file:'spinner.gif')}" alt="${message(code:'spinner.alt.Loading',default:'Loading...')}" />
        </div>
    </div>
</div>


