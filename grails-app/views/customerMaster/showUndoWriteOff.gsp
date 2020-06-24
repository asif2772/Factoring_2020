<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="bv.CoreParamsHelperTagLib;"%>
<% def contextPath = request.getServletContext().getContextPath() %>
<html>

<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'customerMaster.undo.writeoff.label', default: 'Undo Write Off')}"/>
    <title><g:message code="customerMaster.undo.writeoff.label" default='Undo Write Off' args="[entityName]"/></title>
</head>
<body>

<div id="create-budgetItemExpenseDetails" class="content white-background scaffold-list" role="main">
    <div id="list-page">
        <div class="budgetHeader">
            <div class="headerMainLeft" style="width: 60%;">
                <label class="">
                    <div class="list-boxtitle-left">
                        <g:message code="customerMaster.undo.writeoff.label" default='Undo Write Off' args="[entityName]"/>
                    </div>
                </label>
            </div>
            <div class="headerMainMiddleTwo">
                <div class="ashBtnDiv">
                    <g:link class="ashBtn" style="height: 28px;line-height: 28px;display:inline-block;font-size: 18px;color:#1870b3" action="list"  id="${debtorId}" >Back</g:link>
                </div>
            </div>
        </div>  <!--budgetHeader-->
        <div class="singleContainer" >
            <p style="margin-top: 0px;">
                &nbsp;
            </p>
            <g:if test="${session.isFiscalYearClosed}">
                <div class="alertMsg" style="display:block">
                    Attention! Fiscal year is in closed year.
                </div>
                <% session.isFiscalYearClosed = false %>
            </g:if>
            <div class="InvoiceAmountTabl">
                <div class="financeTranTablHead" style="margin-left: 0px;width: 100%">
                    <table class="createReceipt createReceiptFinanceHead" width="99%" style="border: 1px solid #fdfcfc;">
                        <thead>
                            <tr>
                                <th width="25%"><g:message code="customerMaster.undo.writeoff.invoice.number.label"  default="Invoice Number"/></th>
                                <th width="25%"><g:message code="customerMaster.undo.writeoff.invoice.date.label" default="Date"/></th>
                                <th width="25%"><g:message code="customerMaster.undo.writeoff.invoice.amount.label" default="Amount"/></th>
                                <th width="25%"><g:message code="customerMaster.undo.writeoff.label" default="Undo Write Off"/></th>
                            </tr>
                        </thead>
                    </table>
                </div>
                <g:if test="${debtorWriteOffs}">
                    <div class="financeTranTablHead" style="margin-left: 0px;width: 100%">
                        <table style="border-bottom: 1px solid #a5a5a5;">
                            <tbody>
                            <g:each in="${debtorWriteOffs}" status="j" var="debtorWriteOff">
                                <%
                                    def invoiceNo = debtorWriteOff[1]
                                    if(invoiceNo){
                                        invoiceNo = invoiceNo.substring(0,invoiceNo.length()-2)
                                    }
                                %>
                                <tr style="background-color: #f9f9f9;">
                                    <td width="25%">${invoiceNo}</td>
                                    <td width="25%">${debtorWriteOff[2]}</td>
                                    <td width="25%"><bv:euroDotCommaDecimalFormat decimalData="${debtorWriteOff[3]}" /></td>
                                    <td width="25%"><a class="dbtBtn debOvr undwr" href="${contextPath}/CustomerMaster/undoWriteOff?invoiceNo=${debtorWriteOff[0]}&customerId=${debtorId}&incomeInvoiceId=${invoiceNo}">Undo Write off</a></td>
                                </tr>
                            </g:each>
                            </tbody>
                        </table>
                    </div>
                </g:if>
                <g:else>
                    <div class="fieldContainer" style="margin-top: 11px;margin-left:364px;font-weight:bold;font-size: 14px; "><g:message code="customerMaster.undo.writeoff.nodata.label"/></div>
                </g:else>
            </div>
        </div>
    </div>   <!--id=list-page-->
</div>  <!--content-->



</body>
</html>