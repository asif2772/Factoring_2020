<%@ page import="factoring.CoreParamsHelperTagLib;"%>
<!doctype html>
<html>
<head>
    <meta name="layout" content="mainCustomerPortal">
    <g:set var="entityName" value="${message(code: 'bv.menu.Customers', default: 'CustomerMaster')}"/>
    <title><g:message code="bv.menu.Customers" args="[entityName]"/></title>
</head>
<% def contextPath = request.getServletContext().getContextPath()%>

<body>
<g:if test="${flash.invalidToken}">
    Don't click the button twice!
</g:if>

<div>
    <div class="wrapper">
        <div class="bodyHead">
            <h2 class="h2 head2Cls">Mijn gegevens</h2>
            <p class="headPcls">Al uw gegevens in één oogopslag.</p>
        </div>
        <div class="btn-group">
                <button id="customerInfo1" onclick="companyInfoForm()" class="btna" style="border: none; outline: none">
                    <span class="menuItem" style="color: #0c0c0c">Bedrijfsgegevens</span>
                </button>
            <button id="customerInfo2"  onclick="personalInfoForm()" style="border: none; margin-left: 24px;outline: none">
                <span class="menuItem" style="color: #0c0c0c">Contactgegevens</span>
            </button>
            <button id="customerInfo3"  onclick="bankInfoForm()" style="border: none;margin-left: 24px;outline: none">
                <span class="menuItem" style="color: #0c0c0c">Bankgegevens</span>
            </button>
            <button id="customerInfo4"  onclick="loginInfoForm()" style="border: none; margin-left: 24px;outline: none">
                <span class="menuItem" style="color: #0c0c0c">Inloggegevens</span>
            </button>
        </div>
    </div>

    <div style="margin: 0 auto; width: 1502px;">
        <div  id="companyInfoForm" class="companyForm">
            <g:form style="margin-left: 10px; margin-right: 45px;;" controller="vendorMaster" action="changeAddress" useToken="true">
                <g:render template="companyDetailsFormOfPortal"></g:render>
            </g:form>
        </div>


        <div id="personalInfoForm" class="companyForm">
            <g:form style="margin-left: 10px; margin-right: 45px;" controller="vendorMaster" action="changeCustomerInfo" useToken="true">
                <g:render template="personalDetailsFormOfPortal"></g:render>
            </g:form>
        </div>

        <div id="bankInfoForm" class="companyForm">
            <g:form style="margin-left: 10px; margin-right: 45px;" controller="vendorMaster" action="changeBankAccountInformation" useToken="true">
                <g:render template="bankDetailsFormOfPortal"></g:render>
            </g:form>
        </div>

        <div id="loginInfoForm" class="companyForm">
            <g:form style="margin-left: 10px; margin-right: 45px;" controller="dashboard" action="updatePassword" useToken="true">
                <g:render template="loginDetailsFormOfPortal"></g:render>
            </g:form>
        </div>
    </div>


    <div class="wrapper" id="">
        <div class="">

        </div>
    </div>

</div>



<g:javascript>

    $(document).ready(function() {

        if (<%= form == 'bankForm'%> ) {

              $('#personalInfoForm').hide();
              $('#bankInfoForm').show();
              $('#loginInfoForm').hide();
              $('#companyInfoForm').hide();

              $('#customerInfo1').removeClass('activeBtn');
              $('#customerInfo3').addClass('activeBtn');
              $('#customerInfo4').removeClass('activeBtn');
              $('#customerInfo2').removeClass('activeBtn');

        } else if(<%= form == 'companyForm'%> ) {

              $('#personalInfoForm').hide();
              $('#bankInfoForm').hide();
              $('#loginInfoForm').hide();
              $('#companyInfoForm').show();

              $('#customerInfo1').addClass('activeBtn');
              $('#customerInfo3').removeClass('activeBtn');
              $('#customerInfo4').removeClass('activeBtn');
              $('#customerInfo2').removeClass('activeBtn');

        } else if(<%= form == 'personalForm'%>) {

              $('#personalInfoForm').show();
              $('#bankInfoForm').hide();
              $('#loginInfoForm').hide();
              $('#companyInfoForm').hide();

              $('#customerInfo1').removeClass('activeBtn');
              $('#customerInfo3').removeClass('activeBtn');
              $('#customerInfo4').removeClass('activeBtn');
              $('#customerInfo2').addClass('activeBtn');

        } else if(<%= form == 'loginForm'%>) {

              $('#personalInfoForm').hide();
              $('#bankInfoForm').hide();
              $('#loginInfoForm').show();
              $('#companyInfoForm').hide();

              $('#customerInfo1').removeClass('activeBtn');
              $('#customerInfo3').removeClass('activeBtn');
              $('#customerInfo4').addClass('activeBtn');
              $('#customerInfo2').removeClass('activeBtn');

        } else {
              $('#personalInfoForm').hide();
              $('#bankInfoForm').hide();
              $('#loginInfoForm').hide();

              $('#customerInfo1').addClass('activeBtn');
              $('#customerInfo3').removeClass('activeBtn');
              $('#customerInfo4').removeClass('activeBtn');
              $('#customerInfo2').removeClass('activeBtn');
        }

        $('#changeReqBtnGroup1').hide();
        $('#changeReqBtnGroup2').hide();
        $('#changeReqBtnGroup3').hide();
        $('#changeReqBtnGroup4').hide();

        $('[name="postalCountryId"]').css({
            "border": "1px solid #cccccc",
            "height":"30.5px",
            "margin-top": "10px",
            "padding-left": "5px",
            "width": "100%",
            "border-radius": "0",
            "background-color": "#e7e7e7"
        });

          $('[name="officeCountryId"]').css({
            "border": "1px solid #cccccc",
            "height":"30.5px",
            "margin-top": "10px",
            "padding-left": "5px",
            "width": "100%",
            "border-radius": "0",
            "background-color": "#e7e7e7"
        });

        $('#menuInfo').addClass('menuOrangeBeam');

    });

    function showCngReqBtn() {

        $('#changeReqBtnGroup1').show();
        $('#changeReqBtnGroup2').show();
        $('#changeReqBtnGroup3').show();
        $('#changeReqBtnGroup4').show();

      var changeElm =  (document).getElementsByClassName('inputChangeField');
      var changeIds = new Array();

      if (changeElm.length > 0) {
          for (var i = 0; changeElm.length > i; i++) {
              changeIds.push(changeElm[i].id)
          }
      }

      if (changeIds.length > 0) {
          var idStr = "";

          for (var i = 0; changeIds.length > i; i++) {
              if (idStr == "") {
                   idStr = '#'+changeIds[i]+',';
              } else {
                  idStr += '#'+changeIds[i]+','
              }
            }

            idStr = idStr.slice(0, -1)
      }

        $('input:not('+idStr+')').addClass('inputBgColor');
          console.log(changeIds);
          console.log(idStr);

        if (!$('[name="countryId"]').hasClass('inputChangeField')) {
             $('countryId').addClass('inputBgColor');
        }

    }

    function hideChngRwqBtn() {
        $('#changeReqBtnGroup1').hide();
        $('#changeReqBtnGroup2').hide();
        $('#changeReqBtnGroup3').hide();
        $('#changeReqBtnGroup4').hide();

        $('input').removeClass('inputBgColor');
    }


    function companyInfoForm() {
        $('#companyInfoForm').show();
        $('#bankInfoForm').hide();
        $('#loginInfoForm').hide();
        $('#personalInfoForm').hide();

        $('#customerInfo1').addClass('activeBtn');
        $('#customerInfo3').removeClass('activeBtn');
        $('#customerInfo4').removeClass('activeBtn');
        $('#customerInfo2').removeClass('activeBtn');

        $('.rectangle_122').hide();
        $('.rectangle_123').hide();
    }

    function personalInfoForm() {
        $('#companyInfoForm').hide();
        $('#bankInfoForm').hide();
        $('#loginInfoForm').hide();
        $('#personalInfoForm').show();

        $('#customerInfo1').removeClass('activeBtn');
        $('#customerInfo3').removeClass('activeBtn');
        $('#customerInfo4').removeClass('activeBtn');
        $('#customerInfo2').addClass('activeBtn');

        $('.rectangle_122').hide();
        $('.rectangle_123').hide();
    }

    function bankInfoForm() {
        $('#companyInfoForm').hide();
        $('#loginInfoForm').hide();
        $('#personalInfoForm').hide();
        $('#bankInfoForm').show();

        $('#customerInfo1').removeClass('activeBtn');
        $('#customerInfo3').addClass('activeBtn');
        $('#customerInfo4').removeClass('activeBtn');
        $('#customerInfo2').removeClass('activeBtn');

        $('.rectangle_122').hide();
        $('.rectangle_123').hide();
    }

    function loginInfoForm() {
        $('#companyInfoForm').hide();
        $('#bankInfoForm').hide();
        $('#personalInfoForm').hide();
        $('#loginInfoForm').show();

        $('#customerInfo1').removeClass('activeBtn');
        $('#customerInfo3').removeClass('activeBtn');
        $('#customerInfo4').addClass('activeBtn');
        $('#customerInfo2').removeClass('activeBtn');

        $('.rectangle_122').hide();
        $('.rectangle_123').hide();
    }


    var actualVal = '';

    function checkChangeVal(event,a,c) {

        var trimActualVal = $.trim(actualVal);
        var cc = $.trim(c);
        if (cc !== trimActualVal) {
            $('#'+a).removeClass('inputBgColor');
            $('#'+a).addClass('inputChangeField');

        } else{
            $('#'+a).removeClass('inputChangeField');
        }
    }

    function checkActualVal(event,a,c) {
        if (!actualVal || actualVal == '') {
            actualVal = c;
        }
    }

</g:javascript>
<style>
    #rectangle_1 {
        margin-top: 0px;
    }
</style>

</body>
</html>