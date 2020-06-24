<%@ page import="bv.CoreParamsHelperTagLib;" %>
<!doctype html>
<html>
<head>
    <r:require modules="flexiGrid"/>
    <r:layoutResources/>
</head>

<body>
<g:javascript>
       $("#flex1").flexigrid({

        url: "${resource(dir: 'customerMaster', file: 'searchCustomer')}?searchInput=${params.searchInput}&glAccountSearch=${params.glAccountSearch}",
        dataType: 'json',
        colModel : [
            {display: 'SL#',name : 'id',width : 100, sortable : true, align: 'center'},
            {display: '${message(code: 'customerMaster.customerCode.label', default: 'Code')}',name : 'customer_code',width : 200, sortable : true, align: 'left'},
            {display: '${message(code: 'debtorMaster.debtorName.label', default: 'Debtor Name')}',name : 'customer_name',width : 200, sortable : true, align: 'left'},
            {display: '${message(code: 'customerMaster.email.label', default: 'Email Address')}',  name : 'email',width : 160, sortable : true, align: 'left'},
            {display: '${message(code: 'customerMaster.customerType.label', default: 'Customer Type')}',  name : 'customer_type', width : 180, sortable : true, align: 'left'},
            {display: '${message(code: 'common.gridAction.label', default: 'Action')}',  name : 'edit',width : 150, sortable : true, align: 'left'}

        ],

        sortname: "id",
        sortorder: "DESC",
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
                var redirectUrl=liveUrl+"/customerMaster/list/"+userId+"?eid="+userId;
                window.location.replace(redirectUrl);
            }

</g:javascript>

        <div id="customerList" class="content scaffold-create" role="main"
             style="margin-top: 0px;margin-bottom: 0px;">
            <table id="flex1" style="display: none"></table>
        </div>
<r:layoutResources/>
</body>
</html>