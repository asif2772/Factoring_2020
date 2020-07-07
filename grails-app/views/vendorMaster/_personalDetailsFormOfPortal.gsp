<%@ page import="factoring.CoreParamsHelperTagLib;" %>

<% def paymentTerm = customerMasterArr[15]
   def customerType = customerMasterArr[21]

    def lastName = customerMasterArr[12]
        if (customerMasterArr[13]) {
            lastName = customerMasterArr[13] +" "+customerMasterArr[12]
        }

%>

<div class="changeReqBtnDiv">
    <button onclick="showCngReqBtn()" type="button" class="changeReqBtn" value="Wijzig gegevens">WIJZIG GEGEVENS</button>
</div>

<div class="upperContainer" style="margin-top: 25px; height: 360px;">

    <div class="fieldContainerLeft">
        <div class="rowContainer">
            <div class="fieldContainer required fcInputText">
                <label for="firstName">
                    <span class="fieldsName">Voornaam<b>:</b></span>
                </label>
                <input class="fieldDsn" style="border-radius: 0 !important;" type="text" tabindex=""
                       onkeydown="checkActualVal(event,this.id,this.value)"
                       onkeyup="checkChangeVal(event,this.id,this.value)"
                       placeholder="" name="firstName"
                       value="${customerMasterArr ? customerMasterArr[10] : ""}" id="firstName" />
            </div>
        </div>
        <div class="rowContainer">
            <div class="fieldContainer required fcInputText">
                <label for="businessMail">
                    <span class="fieldsName">E-mail zakelijk<b>:</b></span>
                </label>
                <input class="fieldDsn" style="border-radius: 0 !important;" type="text" tabindex=""
                       onkeydown="checkActualVal(event,this.id,this.value)"
                       onkeyup="checkChangeVal(event,this.id,this.value)"
                       placeholder="" name="businessMail"
                       value="${customerMasterArr ? customerMasterArr[9] : ""}" id="businessMail" />
            </div>
        </div>
        <div class="rowContainer">
            <div class="fieldContainer required fcInputText">
                <label for="phoneNumber">
                    <span class="fieldsName">Vast telefoonnummer<b>:</b></span>
                </label>
                <input class="fieldDsn" style="border-radius: 0 !important;" type="text" tabindex=""
                       onkeydown="checkActualVal(event,this.id,this.value)"
                       onkeyup="checkChangeVal(event,this.id,this.value)"
                       placeholder="" name="phoneNumber"
                       value="${customerGeneralAddressArr ? customerGeneralAddressArr[11] : ""}" id="phoneNumber" />
            </div>
        </div>
    </div>

    <!--fieldContainerLeft-->

    <div class="fieldContainerRight">
        <div class="rowContainer">
            <div class="fieldContainer required fcInputText">
                <label for="lastName">
                    <span class="fieldsName">Achternaam<b>:</b></span>
                </label>
                <input class="fieldDsn" style="border-radius: 0 !important;" type="text" tabindex=""
                       onkeydown="checkActualVal(event,this.id,this.value)"
                       onkeyup="checkChangeVal(event,this.id,this.value)"
                       placeholder="" name="lastName"
                       value="${lastName ? lastName : ""}" id="lastName" />
            </div>
        </div>
        <div class="rowContainer">
            <div class="fieldContainer required fcInputText">
                <label for="privateMail">
                    <span class="fieldsName">E-mail privé<b>:</b></span>
                </label>
                <input class="fieldDsn" style="border-radius: 0 !important;" type="text" tabindex="6"
                       onkeydown="checkActualVal(event,this.id,this.value)"
                       onkeyup="checkChangeVal(event,this.id,this.value)"
                       placeholder="" name="privateMail"
                       value="${customerMasterArr ? customerMasterArr[23] : ""}" id="privateMail" />
            </div>
        </div>

    <g:if test="${flashSuccess1}">
        <div class="rectangle_122" style="margin-top: 105px;">
            <div class="msgAlign">
                <span class="succMgsH"><strong>${flashSuccess1}</strong></span><br>
                <span>${flashSuccess2}</span>
            </div>
        </div>
    </g:if>

    <g:if test="${flashError1}">
        <div class="rectangle_123" style="margin-top: 105px;">
            <div class="msgAlign">
                <span class="errorMsgH"><strong>${flashError1}</strong></span><br>
                <span>${flashError2}</span>
            </div>
        </div>
    </g:if>

    </div>

    <!--fieldContainerRight-->

</div>


<div id="changeReqBtnGroup2" class="changeReqConfirmBtn">
    <button onclick="hideChngRwqBtn()" type="button" class="chancelBtn2">
        <span >Annuleer</span>
    </button>
    <button class="submitBtn" type="submit">
        <span >Opslaan</span>
        %{--<g:actionSubmit class="submitBtn" controller ="vendorMaster" action="changeBankAccountInformation" value="${message(code: 'bv.change.request.customer', default: 'Change')}"/>--}%
    </button>
</div>


<input name="customerId" value="${customerMasterArr[0]}" hidden>
<input name="officeAddId" value="${customerGeneralAddressArr[0]}" hidden>