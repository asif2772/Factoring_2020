<%--
  Created by IntelliJ IDEA.
  User: asif
  Date: 3/11/19
  Time: 11:27 AM
--%>
<% def contextPath = request.getServletContext().getContextPath()%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="bv.CoreParamsHelperTagLib;"%>

<g:javascript>

     $(document).ready(function() {

         $.fn.dataTable.moment('DD-MM-YYYY');
         $.fn.dataTable.moment('MMM-YYYY');
         $('#creditLimitsTable').dataTable( {

              "ajax":"${contextPath}/customerMaster/getCreditLimitsInfo",

             "bProcessing": true,
             "bAutoWidth":true,
             "aLengthMenu": [[10,50,75,100,150,200,300], [10,50,75,100,150,200,300]],
             "iDisplayLength": 50,
              "aaSorting": [],

             "fnRowCallback": function( nRow, aData, iDisplayIndex, iDisplayIndexFull ) {
                $(nRow).find('td:eq(3)').css('color', 'red');
                },
             "columns": [
                    { "data": "debtorName" },
                    { "data": "creditLimit" },
                    { "data": "outStandingAmount" },
                    { "data": "overDue" }
                ]

        } );

    });


</g:javascript>

<html>

<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'bv.creditLimit.statusCreditLimits.label', default: 'Status credit limits')}"/>
    <title><g:message code="bv.creditLimit.statusCreditLimits.label" default='Status credit limits'/></title>
</head>

<body>

<div id="create-budgetItemExpenseDetails" class="content white-background scaffold-list" role="main">
    <div id="list-page">

        <div class="budgetHeader">
            <div class="headerMainLeft" style="width: 60%;">
                <label class="">
                    <div class="list-boxtitle-left">
                        <g:message code="default.name.label" args="[entityName]"/>
                    </div>
                </label>
            </div>
            <div class="headerMainMiddleTwo">
            </div>
        </div>  <!--budgetHeader-->

        <div class="explanationDiv">
            <h4 class="slideToggler2 slideSign2">
                <label class="explanationLabel">
                    <span class="explanationArrow"></span>
                    <p><g:message code="bv.dashboard.explanation.label" default="Explanation" /></p>
                </label>
            </h4>

            <div class="slideContent2 explanationSlideContainer">
                <ul class="explanationList">
                    <li><g:remoteLink params="[manualProperties:1]" controller="explanation" action="whyBankingProcessing"  update="changeForExplanation"><g:message code="explanation.manualReconciliation.label.no1" /></g:remoteLink></li>
                </ul>
            </div>
        </div>  <!--explanationDiv-->


        <div class="navigation">
            <div class="incomeExpenseWizard">
                <div class="navigationbtn" style="float: right;  margin-right: 42px;">

                </div>
            </div>
        </div> <!--navigation-->

        <div>
        <table id="creditLimitsTable" class="display" cellspacing="0" style="width: 99.7%">
            <thead>
            <tr>
                <th><g:message code="bv.dashboard.newDebtor.label" default="Debtor" /></th>
                <th><g:message code="bv.dashboard.creditLimit.label" default="Credit Limit" /></th>
                <th><g:message code="customerMaster.outstandingInvoices.label" default="Outstanding" /></th>
                <th><g:message code="bv.datatable.actionList.overDue.label" default="Overdue" /></th>
            </tr>
            </thead>
        </table>
    </div>


    </div>   <!--id=list-page-->
</div>  <!--content-->



</body>
</html>