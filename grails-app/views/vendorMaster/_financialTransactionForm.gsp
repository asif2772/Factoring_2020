<%@ page import="factoring.CoreParamsHelperTagLib; java.text.SimpleDateFormat;" %>
<% def contextPath = request.getServletContext().getContextPath() %>
<script type="text/javascript">
    function isWriteOffClicked(){
        var isValid = false;
        $('input:checked').each(function() {
            $("#writeOffLoader").show();
            isValid = true;
        });
        return isValid;
    }

    $(function () {
        setTimeout(function () {
            $("#msgdiv").hide('blind', {}, 1000);
        }, 3000)
    })

    $(document).ready(function () {
        $('#customerFinTransTable').dataTable({
            "paging":   false,
            "aoColumnDefs" : [ {
                "bSortable" : false,
                "aTargets" : [ "no-sort" ]
            } ]
        });
        // restrict the navigation by setting min and max properties.
        $("#transDate").datepicker({
            dateFormat: 'dd-mm-yy',
            showOn: "button"}).datepicker("setDate", new Date());

        var sendData = {customerId:${vendorMasterInstance?.id?vendorMasterInstance?.id:venId}};
        var appContextUrl = '${request.contextPath}';
        var bankLoadDataUrl = appContextUrl+"/VendorMaster/loadBankDataInfo";
        $.ajax({
            url: bankLoadDataUrl,
            data: sendData,
            success: function(data) {
                if(data)
                    $("#loadBankDataInfo").html(data);
            },
            error: function(){
            }
        });
    });
</script>
<% if (executeMessage) { %>
    <div id="msgdiv" style="text-align: center;height:40px;margin-top:15px">
        <b style="font-size:17px;color:${executeMessageColor};"><g:message code="${executeMessageCode}" default="${executeMessage}"/></b>
    </div>
<% } %>
<div class="main-container" style="margin-bottom: 24px;">
    <div class="codeContainerSections">
        %{--<div class="fcCodeNumber">--}%
        <div class="codeFieldLeft">
            <div class="codeContainer">
                <div class="fieldContainer required">
                    <label for="vendorCode">
                        <g:message code="vendorMaster.vendorCode.label" default="Vendor Code"/>
                        <span class="required-indicator">*</span>
                    </label>
                    <p class="labelCode">${vendorPrefix ? vendorPrefix : venPrefix}${"-"}${vendorMasterInstance?.vendor_code ? vendorMasterInstance?.vendor_code : venCode}</p>

                </div>
            </div>  <!--codeContainer-->
        </div>  <!--fieldContainerLeft-->

        <div class="codeFieldMiddle">
            <div class="codeContainer">
                <div class="fieldContainer required">
                    <label for="vendorName">
                        <g:message code="vendorMaster.vendorName.label" default="Vendor Name"/>
                    <span class="required-indicator">*</span>
                </label>
                    <p class="labelCode">${vendorMasterInstance?.vendor_name ? vendorMasterInstance?.vendor_name : venName}</p>
                </div>
            </div>  <!--codeContainer-->
        </div>  <!--codeFieldMiddle-->

        <div class="codeFieldRight">
            <div class="codeContainer">
                <div class="fieldContainer required">
                </div>
            </div>  <!--codeContainer-->
        </div>  <!--codeFieldRight-->
    </div>  <!--codeContainerSections-->


    <div class="singleContainer" style="">

    %{--1st table--}%
        <div class="fieldContainer" style="display: block; height: 36px;">
            <p style="font-weight: bold;font-size: 14px;margin-bottom: 7px;">
                <g:message code="customerMaster.financeTranTableHeader1.label" default="Invoices Including Paid Amount"/>
            </p>
            %{--<div class="navigationbtn" style="float: right;21px; margin-top:8px; ">--}%
            <p class="ashBtnDiv" style="float: right; margin-top: -23px; margin-right: 22px;">
                <g:link class="ashBtn" style="line-height: 22px;" action="exportExcelofBankTransaction"  id="${vendorMasterInstance?.id?vendorMasterInstance?.id:venId}" ><g:message code="bv.vendorMaster.excelReportButton.lable" default="Excel Report" /></g:link>
            </p>
            %{--</div>--}%
        </div>

         <g:formRemote name="writeOffForm" url="[controller:'vendorMaster',action:'writeOffInvoicePaymentVendor' ]">
         <%
               def rowNo = 0;
               def multipleElements=1
         %>


        <div class="InvoiceAmountTabl">
            <div id="tableHeadForQuickEntry" class="financeTranTablHead">
                %{--<hr style="color: #fdfcfc;  margin-bottom: -1px;">--}%
                <table id="customerFinTransTable" class="createReceipt createReceiptFinanceHead" width="100%" style="border: 1px solid #fdfcfc;">
                    <thead>
                    %{--1st table TABLE--}%
                        <tr>
                            <th width="197" style="padding-left: 20px;"><g:message code="invoiceExpense.gridList.invoiceNumber.label"  default="Invoice Number"/></th>
                            <th width="163" style="padding-left: 0px;"><g:message code="customerMaster.paymentRefference.label" default="Payment Refference"/></th>
                            <th width="120" style="padding-left: 0px;"><g:message code="bv.autoReconciliationOpenInvoicesTransactionDate.label" default="Transaction Date"/></th>
                            <th width="129" style="text-align: center"><g:message code="DashboardListIncome.BookingPeriod.list.label" default="Booking Period"/></th>
                            <th width="130" style="text-align: right"><g:message code="report.aging.paidAmount.label" default="Paid Amount"/></th>
                            <th width="130" style="text-align: right"><g:message code="report.aging.dueAmount.label" default="Due Amount"/></th>
                            <th class="no-sort"></th>
                            <th width="" style="text-align: right"><g:message code="report.aging.paidStatus.label" default="Paid Status"/></th>
                        </tr>
                    </thead>
                %{--1st--}%
                <%if(invoiceVendorDetailsInstance !=[] ){%>

                    <tbody>
                    <g:each in="${invoiceVendorDetailsInstance}" status="i" var="invoiceVendorDetails">
                        <%
                                BigDecimal showInvoiceAmount = new BigDecimal(invoiceVendorDetails[4])
                                BigDecimal showPaidAmount = new BigDecimal(invoiceVendorDetails[5])
                                BigDecimal showDueAmount = new BigDecimal(invoiceVendorDetails[6])

                                def notPaidFlag = false;
                                Double dInvAmount = showInvoiceAmount.toDouble();
                                Double dPaidAmount = showPaidAmount.toDouble();
                                Double dDueAmount = showDueAmount.toDouble();
                                String strPaidStatus = "Not Paid";
                                if(dDueAmount == 0.00)
                                {
                                    strPaidStatus = "Paid";
                                }
                                else if(dDueAmount < 0.00 && dInvAmount < dPaidAmount)
                                {
                                    strPaidStatus = "Over Paid";
                                    notPaidFlag = true
                                }
                                else{
                                    notPaidFlag = true;
                                }

                                def bookingMonth = new CoreParamsHelperTagLib().monthNameShow(Integer.parseInt(invoiceVendorDetails[3]))
                        %>
                    %{--1st--}%
                        <tr style="background-color: #f9f9f9;">
                            <td width="185" style="padding-left: 20px;">${invoiceVendorDetails[0]}</td>
                            <td width="155" style="padding-left: 1px;">${invoiceVendorDetails[1]}</td>
                            <td width="120" style="padding-left: 7px;"><g:if test="${!strPaidStatus.equals('Not Paid')}">${new SimpleDateFormat("dd MMM yyyy").format(invoiceVendorDetails[2])}</g:if></td>
                            <td width="120" style="text-align: center">${bookingMonth}</td>
                            <td width="130" style="text-align: right"><bv:euroDotCommaDecimalFormat decimalData="${showPaidAmount.setScale(2, BigDecimal.ROUND_HALF_UP)}" /></td>
                            <td width="130" style="text-align: right"><bv:euroDotCommaDecimalFormat decimalData="${showDueAmount.setScale(2, BigDecimal.ROUND_HALF_UP)}" /></td>
                            <% if(notPaidFlag){ %>
                            <td width="20" style=""><input type="checkbox" name = "invBankStatement" value="${rowNo}"/></td>
                            <% }else{ %>
                            <td width="20" style="text-align: right"></td>
                            <%}%>
                            <td width="" style="text-align: center;width: 96px;">${strPaidStatus}</td>
                        </tr>
                        <%rowNo++;%>
                    </g:each>
                    </tbody>

                </table>
            </div>
                %{--<hr style="border-top: 1px solid #8e8e8e;">--}%

             <div class="fieldContainer fieldContainerRightOffDiv" style="display: block !important;height: 70px;">
                 <div class="fieldContainerRightOffDiv wrightOffDateDiv">

                     <div class="fieldContainer onStyle">
                         <g:message code="vendorCustomerMasterOn.label" default="Booking Date On"/>
                     </div>

                     <div class="required fieldContainer manualEntryDatePick datePicVenCus">
                         <input type="text" id="transDate" name="transDate">
                     </div>
                     <div class="fieldContainerRightOffDiv writeOff2Button" style="padding-right: 0px;">
                         <div class="fieldContainer updateLinkBtn ashBtnDiv writeOffBtn" style="margin: 25px 10px 0px;">
                             <img id="writeOffLoader" style="display: none;margin-left: -22px;" src="${assetPath(src:'spinner.gif')}" alt="${message(code:'spinner.alt.Loading',default:'Loading...')}" />
                             <g:submitToRemote before="if(isWriteOffClicked()){" after="}" class="ashBtn" action="writeOffInvoicePaymentVendor" update="financialTabInfoData"  value="${message(code: 'customerMaster.financeTranTab.writeOff.label', default: 'Write Off')}"/>
                             <g:link class="ashBtn" style="line-height: 22px;display:inline-block;position: relative;top: 1px;" action="showUndoWriteOff"  id="${vendorMasterInstance?.id?vendorMasterInstance?.id:venId}" ><g:message code="customerMaster.undo.writeoff.label" default="Undo Write Off" /></g:link>
                             <div class="" style="padding-top: 10px; padding-bottom: 10px;width: 262px;">
                                 <input type="text" id="writeOffAmount" name="writeOffAmount" placeholder="0.00">
                             </div>
                             <div class="" style="padding-bottom: 10px;width: 262px;">
                                 <input type="text" class="" style="width:220px" name="writeOffDescription" id="writeOffDescription" placeholder="Description">
                             </div>
                         </div>
                     </div>
                   <%}else{%>
                   <div class="fieldContainer" style="text-align:center;margin-top: 11px;font-weight:bold;font-size: 14px; ">
                        <g:message code="vendorMaster.financialTab.paidInvoice.label"/>
                    </div>
                   <%}%>

                 </div>  <!--fieldContainerRightOffDiv-->
             </div>  <!--fieldContainer-->

        </div>  <!--InvoiceAmountTabl-->
        <%
    //      }
        %>
          <g:hiddenField name="id" value="${vendorMasterInstance?.id?vendorMasterInstance?.id:venId}" />
          <g:hiddenField name="vendorPrefix" value="${vendorPrefix?vendorPrefix:venPrefix}" />
          <g:hiddenField name="vendorCode" value="${vendorMasterInstance?.vendor_code?vendorMasterInstance?.vendor_code:venCode}" />
          <g:hiddenField name="vendorName" value="${vendorMasterInstance?.vendor_name?vendorMasterInstance?.vendor_name:venName}" />
        </g:formRemote>
    %{--2nd Table--}%
        <div class="InvoiceAmountTabl" style="margin-top: 40px;">
            <div style="clear:both"></div>
            <p style="font-weight: bold;font-size: 14px;margin-bottom: 7px; margin-top: 30px;">
                <g:message code="customerMaster.financeTranTableHeader2.label" default="Bank Transaction That were Reconciliated for These Invoice"/>
            </p>
            <div id="loadBankDataInfo">
                <div class="spinLoader fieldContainer">
                    <img src="${resource(dir:'images',file:'spinner.gif')}" alt="${message(code:'spinner.alt.Loading',default:'Loading...')}" />
                </div>
            </div>
        </div>
    </div>  <!--singleContainer-->
</div> <!---main-container--->
