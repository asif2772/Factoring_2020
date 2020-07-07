<%@ page import="factoring.CoreParamsHelperTagLib; factoring.InvoiceIncomeDetails;" %>

<div class="fieldcontain" style="width: 60px;text-align:right;margin-top:3px;margin-left:-6px">
        <g:field name="vatAmount" id="vatAmount" value="0.00"
                 required=""
                 style="width:60px;margin-top:-4px;text-align:right;margin-left:6px"/>
</div>
<script type="text/javascript">
    calPriceByVAT('${InvoiceIncomeInstanceVatRate}');
</script>

