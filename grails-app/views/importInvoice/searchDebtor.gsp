<%  def contextPath = request.getServletContext().getContextPath() %>
<g:each in="${debtorList}" var="debtor">
    <div class="nSearchItem">
        <div class="sResDebtor">
            ${debtor[8]}
        </div>
        <div class="sResDebtorAccept">
            <g:link onclick="javascript:closeSearchBox()" class="dbtBtn debOvr debacc" controller="importInvoice" action="changeImportInvoice" params="[originalDebtor: debtorName,toChangeDebtor: debtor[8]]">Accept</g:link>
        </div>
        <div style="clear: both;"></div>
    </div>
</g:each>