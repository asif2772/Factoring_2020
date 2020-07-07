<%@ page import="factoring.CoreParamsHelperTagLib; factoring.VendorMaster" %>
<% if(params.id=='vn'){%>
<g:textField id="searchInput" name="searchInput" value="" style="margin-left: 90px;" />
<g:remoteLink action="updateVendorListView"  update="vendorList" params="{searchInput:\$('#searchInput').val()}"  >
    <button><g:message code="bv.searchButton.search" default="Search" /></button>
</g:remoteLink>
<%}%>
<% if(params.id=='gl'){%>
<div style=" margin-left: 90px; ">
    <%="${new CoreParamsHelperTagLib().getBudgetChartGroupDropDownExpanse("glAccountSearch", selectedGLAccount ? selectedGLAccount : (budgetItemExpenseDetailsInstance?.purchaseAccountCode))}"%>
    <g:remoteLink action="updateVendorListView"  update="vendorList" params="{glAccountSearch:\$('#glAccountSearch').val()}"  >
        <button><g:message code="bv.searchButton.search" default="Search" /></button>
    </g:remoteLink>

</div>
<%}%>