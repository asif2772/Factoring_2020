<%@ page import="bv.CoreParamsHelperTagLib;java.text.SimpleDateFormat;" %>
<% def contextPath = request.getServletContext().getContextPath()%>
<script type="text/javascript">

    $('#debtorHistoryTable').dataTable({
        "paging":   false
    });

    function isWriteOffClicked(){
        var isValid = false;
        $('input:checked').each(function() {
            $("#writeOffLoader").show();
            isValid = true;
        });
        return isValid;
    }
    (function(){
        setTimeout(function () {
           $("#msgdiv").hide('blind', {}, 1000);
        }, 3000);

        $("#transDate").datepicker({
            dateFormat: 'dd-mm-yy',
            showOn: "button"}).datepicker("setDate", new Date());
        var sendData = {debtorId:${customerMasterInstance?.id}};
        var appContextUrl = '${request.contextPath}';
        var bankLoadDataUrl = appContextUrl+"/customerMaster/loadMoveHistoryBankDataInfo";
        $.ajax({
            url: bankLoadDataUrl,
            data: sendData,
            success: function(data) {
                if(data)
                    $("#tabpage_9 #loadBankDataInfo").html(data);
            },
            error: function(){
            }
        });
    })();

    function isCheckedForMoveHistory() {

        if($('#checkMoveHistory').is(':checked')){
        }

    }

    function redirectList() {
        $("#tabPageLoadData6").html($("#cusLoadSpinner").html());
        isAlreadyLoaded[9] = false;
        isAlreadyLoaded[6] = false;
        $("#tabHeader_6").click()
    }

</script>

    <g:formRemote name="writeOffForm" url="[controller:'customerMaster',action:'writeOffInvoicePaymentCustomer']" >
        <%
            def rowNo = 0;
        %>
        <p class="ashBtnDiv" style="float: right; margin-top: -33px; margin-right: 22px;">
            <g:submitToRemote class="ashBtn" style="line-height: 22px;" onerror="redirectList()" onSuccess="redirectList()" url="[controller:'customerMaster',action:'moveHistoryToFinancialTrans']" value="Move Financial Transaction"/>
        </p>
        <div class="InvoiceAmountTabl">
            <div id="tableHeadForQuickEntry" class="financeTranTablHead">
                %{--<hr style="color: #fdfcfc;  margin-bottom: -1px;">--}%
                <table id="debtorHistoryTable" class="createReceipt createReceiptFinanceHead" width="100%" style="border: 1px solid #fdfcfc;">
                    <thead>
                    <tr>
                        <th width="190" style="padding-left: 20px;"><g:message code="invoiceExpense.gridList.invoiceNumber.label"  default="Invoice Number"/></th>
                        <th width="155"><g:message code="customerMaster.paymentRefference.label" default="Payment Refference"/></th>
                        <th width="120"><g:message code="bv.autoReconciliationOpenInvoicesTransactionDate.label" default="Transaction Date"/></th>
                        <th width="120" style="text-align: center"><g:message code="report.aging.dueDate.label" default="Due Date"/></th>
                        <th width="120" style="text-align: center"><g:message code="report.aging.lastPaymentDate.label" default="Last Payment Date"/></th>
                        <th width="130" style="text-align: right;"><g:message code="report.aging.invoiceAmount.label" default="Invoice Amount"/></th>
                        <th width="130" style="text-align: right"><g:message code="report.aging.dueAmount.label" default="Due Amount"/></th>
                        <th width="" style="margin-right: 50px;text-align: center;width: 96px;"><g:message code="report.aging.paidStatus.label" default="Paid Status"/></th>
                        <th width="" style="margin-right: 50px;text-align: center;width: 96px;">Move History</th>
                    </tr>
                    </thead>
                %{--1st--}%
                <% if(invoiceCustomerDetailsInstance !=[] ){ %>

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
                        <td width="190" style="padding-left: 20px;">${invoiceCustomerDetails[0]}</td>
                        <td width="155">
                            <g:link style="color: #1870b3;text-align: left;" action="outstandingInvoicesDetails"  controller="reportCustomerSettlement" id="${invoiceCustomerDetails[7]}" >${invoiceCustomerDetails[1]}</g:link>
                        </td>
                        <td width="120">${new SimpleDateFormat("dd MMM yyyy").format(invoiceCustomerDetails[2])}</td>
                        <td width="120" style="text-align: center">${new SimpleDateFormat("dd MMM yyyy").format(invoiceCustomerDetails[9])}</td>
                        <td width="120" style="text-align: center"><g:if test="${invoiceCustomerDetails[10]}">${new SimpleDateFormat("dd MMM yyyy").format(invoiceCustomerDetails[10])}</g:if> </td>
                        <td width="130" style="text-align: right;padding-right: 4px;"><bv:euroDotCommaDecimalFormat decimalData="${showInvoiceAmount.setScale(2, BigDecimal.ROUND_HALF_UP)}" /></td>
                        <td width="130" style="text-align: right;padding-right: 2px;"><bv:euroDotCommaDecimalFormat decimalData="${showDueAmount.setScale(2, BigDecimal.ROUND_HALF_UP)}" /></td>
                        <% if(notPaidFlag){ %>
                        <td width="20" style="text-align: right"><input type="checkbox" name = "invBankStatement" value="${rowNo}"/></td>
                        <% }else{ %>
                        <td width="20" style="text-align: right"></td>
                        <%}%>
                        <td width="" style="text-align: center;width: 96px;">${strPaidStatus}</td>
                        <td width="" style="text-align: center;width: 96px;"><input type="checkbox" name="checkMoveHistory" value="${invoiceCustomerDetails[7]}" id="checkMoveHistory"/></td>
                    </tr>
                    <% rowNo++; %>
                </g:each>
                </tbody>

                <%}else{%>
                <div class="fieldContainer" style="text-align:center;margin-top: 11px;font-weight:bold;font-size: 14px;">
                    <g:message code="vendorMaster.financialTab.paidInvoice.label"/>
                </div>
                <%}%>
                </table>
            </div>



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


