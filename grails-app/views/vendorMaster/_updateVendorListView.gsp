<%@ page import="factoring.CoreParamsHelperTagLib; factoring.VendorMaster;" %>
<!doctype html>
<html>
<head>
%{--    <r:require modules="flexiGrid"/>--}%
%{--    <r:layoutResources/>--}%
</head>

<body>

<g:javascript>
    $(document).ready(function () {
        $("#defaultGlAccount").css("border", "1px solid #ffaaaa");
    });

    $("#flex1").flexigrid({

        url: "${resource(dir: 'vendorMaster', file: 'searchVendor')}?searchInput=${params.searchInput}&glAccountSearch=${params.glAccountSearch}",
        dataType: 'json',
        colModel : [
            {display: 'SL#',name : 'id',width : 50, sortable : true, align: 'center'},
            {display: '${message(code: 'vendorMaster.vendorCode.label', default: 'Vendor Code')}',name : 'vendor_code',width : 200, sortable : true, align: 'left'},
            {display: '${message(code: 'vendorMaster.vendorName.label', default: 'Vendor Name')}',name : 'vendor_name',width : 200, sortable : true, align: 'left'},
            {display: '${message(code: 'vendorMaster.email.label', default: 'Email Address')}',  name : 'email', width : 120, sortable : true, align: 'left'},
            {display: '${message(code: 'vendorMaster.vendorType.label', default: 'Vendor Type')}',  name : 'vendor_type',width : 130, sortable : true, align: 'left'},
            {display: '${message(code: 'vendorMaster.creditStatus.label', default: 'Credit Status')}',name : 'credit_status',width : 120, sortable : true, align: 'left'},
            {display: '${message(code: 'invoiceExpense.gridList.action.label', default: 'Action')}',  name : 'edit',width : 100, sortable : true, align: 'left'}

        ],

        sortname: "id",
        sortorder: "desc",
        usepager: true,
        title: false,
        useRp: true,
        rp: 15,
        showTableToggleBtn: true,
        width: "1000",
        singleSelect: true,
        height: "auto"
    });

      function editUrl(userId,liveUrl){
                var redirectUrl=liveUrl+"/vendorMaster/list/"+userId+"?/eid="+userId
                window.location.replace(redirectUrl);
            }

</g:javascript>

    <div id="vendorList" class="content scaffold-create" role="main"
        style="margin-top: 0px;margin-bottom: 0px;">
        <table id="flex1" style="display: none"></table>
    </div>
    <r:layoutResources/>
</body>
</html>
