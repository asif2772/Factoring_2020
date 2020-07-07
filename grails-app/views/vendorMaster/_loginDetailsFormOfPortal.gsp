<div class="changeReqBtnDiv">
    <button onclick="showCngReqBtn()" type="button" class="changeReqBtn" value="Wijzig gegevens">WIJZIG GEGEVENS</button>
</div>

<div class="upperContainer" style="margin-top: 25px; height: 360px;">

    <div class="fieldContainerLeft">
        <div class="rowContainer">
            <div class="fieldContainer required fcInputText">
                <label for="email">
                    <span class="fieldsName">E-mailadres<b>:</b></span>
                </label>
                <input class="fieldDsn" style="border-radius: 0 !important;" type="text" tabindex=""
                       onkeydown="checkActualVal(event,this.id,this.value)"
                       onkeyup="checkChangeVal(event,this.id,this.value)"
                       placeholder="" name="email"
                       value="${session.customerPortalMail}" id="email" />
            </div>
        </div>
        <div class="rowContainer">
            <div class="fieldContainer required fcInputText">
                <label for="password">
                    <span class="fieldsName">Nieuw wachtwoord<b>:</b></span>
                </label>
                <input class="fieldDsn passWordFld" style="border-radius: 0 !important;" type="password" tabindex=""
                       onkeydown="checkActualVal(event,this.id,this.value)"
                       onkeyup="checkChangeVal(event,this.id,this.value)"
                       placeholder="" name="newPassword"
                       value="" id="newPassword" />

            </div>
        </div>
        <div class="rowContainer">
            <b>Kies</b> een sterk en uniek wachtwoord. Wij adviseren min. 8 karakters <b>waarvan minimaal één</b>
            hoofdletter en <b>één</b> cijfer.
        </div>
    </div>

    <!--fieldContainerLeft-->

    <div class="fieldContainerRight">

        <div class="rowContainer">
            <div class="fieldContainer required fcInputText">
                <label for="rePassword">
                    <span class="fieldsName">Huidig wachtwoord<b>:</b></span>
                </label>
                <input class="fieldDsn passWordFld" style="border-radius: 0 !important;" type="password" tabindex=""
                       onkeydown="checkActualVal(event,this.id,this.value)"
                       onkeyup="checkChangeVal(event,this.id,this.value)"
                       placeholder="" name="currentPassword"
                       value="" id="currentPassword" />
            </div>
        </div>

        <div class="rowContainer">
            <div class="fieldContainer required fcInputText">
                <label for="rePassword">
                    <span class="fieldsName">Herhaal nieuw wachtwoord<b>:</b></span>
                </label>
                <input class="fieldDsn passWordFld" style="border-radius: 0 !important;" type="password" tabindex=""
                       onkeydown="checkActualVal(event,this.id,this.value)"
                       onkeyup="checkChangeVal(event,this.id,this.value)"
                       placeholder="" name="confirmPassword"
                       value="" id="confirmPassword" />
            </div>
        </div>

    <g:if test="${flashSuccess1}">
        <div id="sucId" class="rectangle_122" style="margin-top: 90px">
            <div class="msgAlign">
                <span class="succMgsH"><strong>${flashSuccess1}</strong></span><br>
                <span>${flashSuccess2}</span>
            </div>
        </div>
    </g:if>

    <g:if test="${flashError1}">
        <div id="errorId" class="rectangle_123" style="margin-top: 90px">
            <div class="msgAlign">
                <span class="errorMsgH"><strong>${flashError1}</strong></span><br>
                <span>${flashError2}</span>
            </div>
        </div>
    </g:if>
    </div>

    <!--fieldContainerRight-->

</div>

<div id="changeReqBtnGroup4" class="changeReqConfirmBtn">
    <button onclick="hideChngRwqBtn()" type="button" class="chancelBtn2">
        <span >Annuleer</span>
    </button>
    <button class="submitBtn" type="submit">
        <span >Opslaan</span>
        %{--<g:actionSubmit class="submitBtn" controller ="vendorMaster" action="changeBankAccountInformation" value="${message(code: 'bv.change.request.customer', default: 'Change')}"/>--}%
    </button>
</div>
