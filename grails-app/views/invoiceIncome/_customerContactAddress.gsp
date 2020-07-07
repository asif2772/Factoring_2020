<%
    def budgetCustomerName = ""
    if(incomeHeadData){
        budgetCustomerName = incomeHeadData[0][2]
    }else{
        budgetCustomerName = customerName
    }

    def customerId
    if(customerID){
        customerId = customerID
    }else{
        customerId = customerIdParams
    }
%>
<p>${budgetCustomerName}</p>
<div style="border:1px solid; width: 48%;">
    <table style="margin-left: -20px; width: 105%;">
        <tbody>
        <tr>
            <td><input type="text" style="width: 95%;" name="contactName"
                       placeholder="${g.message(code:'customerGeneralAddress.contactPersonName.label')}"
                       value="${customerGeneralAddressMap.contactPersonName}" id="contactName"/></td>
            <td style="float: right;" class="splitApplyButton splitBtnDivStyle">
                %{--<div class="" style="float: right;height: 28px;">  <!--tickCrossRight-->--}%
                    %{--<img alt="Edit" src="../images/cross.png">--}%
                %{--</div>--}%
                <div class="" style="float: right; margin-right: 1px;height: 28px;;">   <!--tickCross-->
                    <img alt="Edit" src="../images/right.png" onclick="onChangeContactInfo()">
                </div>
            </td>
        </tr>
        <tr>
            <td><input type="text" style="width: 95%;" name="address1"
                       placeholder="${g.message(code: 'customerGeneralAddress.addressLine1.label')}"
                       value="${customerGeneralAddressMap.addressLine1}" id="address1" /></td>
            <td><input type="text" style="width: 95%;" name="address2" placeholder="${g.message(code: 'customerGeneralAddress.addressLine2.label')}" name="addressLine2"
                       value="${customerGeneralAddressMap.addressLine2}" id="address2" /></td>
        </tr>
        <tr>
            <td><input type="text" style="width: 95%;" name="postcode" placeholder="${g.message(code: 'customerGeneralAddress.postalCode.label')}"
                       value="${customerGeneralAddressMap.postalCode}" id="postcode" /></td>
            <td><input type="text" style="width: 95%;" name="city" placeholder="${g.message(code: 'customerGeneralAddress.city.label')}"
                       value="${customerGeneralAddressMap.city}" id="city" /></td>
        </tr>
        </tbody>
    </table>
</div>

<%
    def contextPath = request.getServletContext().getContextPath()
%>
<script type="text/javascript">

    function onChangeContactInfo(){

        var joinedAddressInfo = ""
        joinedAddressInfo = "${budgetCustomerName}" + "::";
        joinedAddressInfo += $("#contactName").val() + "::";
        joinedAddressInfo += $("#address1").val() + "::";
        joinedAddressInfo += $("#address2").val() + "::";
        joinedAddressInfo += $("#postcode").val() + "::";
        joinedAddressInfo += $("#city").val() + "::";
        joinedAddressInfo += "${customerId}";

        //alert(joinedAddressInfo);

        $.ajax({type:'POST',
            data:'addressInfo=' + joinedAddressInfo,
            url:'${contextPath}/invoiceIncome/updateContactAddress',
            success:function(data,textStatus){
                $("#customerContactAddress").html(data);
            },
            error: function() {
                $("#customerContactAddress").text('An error occurred.');
            }
        });
    }

</script>