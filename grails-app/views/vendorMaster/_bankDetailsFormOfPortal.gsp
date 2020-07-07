
<div class="changeReqBtnDiv">
    <button onclick="showCngReqBtn()" type="button" class="changeReqBtn" value="Wijzig gegevens">WIJZIG GEGEVENS</button>
</div>

<div class="upperContainer" style="margin-top: 25px; height: 260px;">

    <div class="fieldContainerLeft">
        <div class="rowContainer">
            <div class="fieldContainer required fcInputText">
                <label for="bankAccName">
                    <span class="fieldsName">Tenaamstelling bankrekening<b>:</b></span>
                </label>
                <input class="fieldDsn" style="border-radius: 0 !important;" type="text" tabindex=""
                       onkeydown="checkActualVal(event,this.id,this.value)"
                       onkeyup="checkChangeVal(event,this.id,this.value)"
                       placeholder="" name="bankAccName"
                       value="${customerBankAccArr ? customerBankAccArr [2] : ""}" id="bankAccName" />
            </div>
        </div>
        <div class="rowContainer">
            <div class="fieldContainer required fcInputText">
                <label for="iban">
                    <span class="fieldsName">IBAN<b>:</b></span>
                </label>
                <input class="fieldDsn" style="border-radius: 0 !important;" type="text" tabindex=""
                       onkeydown="checkActualVal(event,this.id,this.value)"
                       onkeyup="checkChangeVal(event,this.id,this.value)"
                       placeholder="" name="iban"
                       value="${customerBankAccArr ? customerBankAccArr [4] : ""}" id="iban" />
            </div>
        </div>
    </div>

    <!--fieldContainerLeft-->

    <div class="fieldContainerRight">
        <div class="rowContainer">

        </div>
        <div class="rowContainer">
            <div class="fieldContainer required fcInputText">
                <label for="bankAccNo">
                    <span class="fieldsName">Bankrekeningnummer<b>:</b></span>
                </label>
                <input class="fieldDsn" style="border-radius: 0 !important;" type="text" tabindex="6"
                       onkeydown="checkActualVal(event,this.id,this.value)"
                       onkeyup="checkChangeVal(event,this.id,this.value)"
                       placeholder="" name="bankAccNo"
                       value="${customerBankAccArr ? customerBankAccArr [3] : ""}" id="bankAccNo" />
            </div>
        </div>

    <g:if test="${flashSuccess1}">
        <div class="rectangle_122">
            <div class="msgAlign">
               <span class="succMgsH"><strong>${flashSuccess1}</strong></span><br>
               <span>${flashSuccess2}</span>
            </div>
        </div>
    </g:if>

    <g:if test="${flashError1}">
        <div class="rectangle_123">
             <div class="msgAlign">
                <span class="errorMsgH"><strong>${flashError1}</strong></span><br>
                <span>${flashError2}</span>
             </div>
        </div>
    </g:if>
    </div>

    <!--fieldContainerRight-->
</div>

<input type="text" name="bankNo" value="${customerBankAccArr[0]}" hidden>

<div id="changeReqBtnGroup3" class="changeReqConfirmBtn">
    <button onclick="hideChngRwqBtn()" type="button" class="chancelBtn2">
        <span >Annuleer</span>
    </button>
    <button class="submitBtn" type="submit">
        <span >Opslaan</span>
        %{--<g:actionSubmit class="submitBtn" controller ="vendorMaster" action="changeBankAccountInformation"
        value="${message(code: 'bv.change.request.customer', default: 'Change')}"/>--}%
    </button>
</div>