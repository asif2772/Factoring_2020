<%@ page import="factoring.CoreParamsHelperTagLib;" %>
<%
    def officeContryId = customerGeneralAddressArr ? customerGeneralAddressArr[8] : '2'
    def postalContryId = customerPostalAddressArr ? customerPostalAddressArr[6] : '2'
%>

<div class="changeReqBtnDiv">
    <button onclick="showCngReqBtn()" type="button" class="changeReqBtn" value="Wijzig gegevens">WIJZIG GEGEVENS</button>
</div>


<div class="upperContainer" style="margin-top: 25px; height: 75px;">

    <div class="fieldContainerLeft">
        <div class="rowContainer">
            <div class="fieldContainer required fcInputText">
                <label for="customerName">
                    <span class="fieldsName">Bedrijfsnaam<b>:</b></span>
                </label>
                <input readonly class="fieldDsn" style="border-radius: 0 !important;" type="text" tabindex=""
                       placeholder="" name="customerName"
                       value="${customerMasterArr ? customerMasterArr[20] : ""}" id="customerName" />
            </div>
        </div>
    </div>

    <!--fieldContainerLeft-->

    <div class="fieldContainerRight">
        <div class="rowContainer">
            <div class="fieldContainer required fcInputText">
                <label for="chamOfCommerce">
                    <span class="fieldsName">KvK nummer<b>:</b></span>
                </label>
                <input readonly class="fieldDsn" style="border-radius: 0 !important;" type="text" tabindex="6"
                       placeholder="" name="chamOfCommerce"
                       value="${customerMasterArr ? customerMasterArr[3] : ""}" id="chamOfCommerce" />
            </div>
        </div>
    </div>

    <!--fieldContainerRight-->

</div>

<h3><b>Kantooradres</b></h3>
<br>
<hr>

<div class="upperContainer" style="margin-top: 25px;">

    <div class="fieldContainerLeft">
        <div class="rowContainer">
            <div class="fieldContainer required fcInputText">
                <label for="officeAddLine1">
                    <span class="fieldsName">Straatnaam<b>:</b></span>
                </label>
                <input class="fieldDsn" style="border-radius: 0 !important;" type="text" tabindex="6"
                       onkeydown="checkActualVal(event,this.id,this.value)"
                       onkeyup="checkChangeVal(event,this.id,this.value)"
                       placeholder="" name="officeAddLine1"
                       value="${customerGeneralAddressArr ? customerGeneralAddressArr[2] : ""}" id="officeAddLine1" />
            </div>
        </div>
        <div class="rowContainer">
            <div class="fieldContainer required fcInputText">
                <label for="officePostalCode">
                    <span class="fieldsName">Postcode<b>:</b></span>
                </label>
                <input class="fieldDsn" style="border-radius: 0 !important;" type="text" tabindex=""
                       onkeydown="checkActualVal(event,this.id,this.value)"
                       onkeyup="checkChangeVal(event,this.id,this.value)"
                       placeholder="" name="officePostalCode"
                       value="${customerGeneralAddressArr ? customerGeneralAddressArr[12] : ""}" id="officePostalCode"/>
            </div>
        </div>
    </div>

    <!--fieldContainerLeft-->

    <div class="fieldContainerRight">
        <div class="rowContainer">
            <div class="fieldContainer required fcInputText">
                <label for="officeAddLine2">
                    <span class="fieldsName">Huisnummer<b>:</b></span>
                </label>
                <input class="fieldDsn" style="border-radius: 0 !important;" type="text" tabindex=""
                       onkeydown="checkActualVal(event,this.id,this.value)"
                       onkeyup="checkChangeVal(event,this.id,this.value)"
                       placeholder="" name="officeAddLine2"
                       value="${customerGeneralAddressArr ? customerGeneralAddressArr[3] : ""}" id="officeAddLine2" />
            </div>
        </div>
        <div class="rowContainer">
            <div class="fieldContainer required fcInputText">
                <label for="city">
                    <span class="fieldsName">Woonplaats<b>:</b></span>
                </label>
                <input class="fieldDsn" style="border-radius: 0 !important;" type="text" tabindex="6"
                       onkeydown="checkActualVal(event,this.id,this.value)"
                       onkeyup="checkChangeVal(event,this.id,this.value)"
                       placeholder="" name="officePostalCity"
                       value="${customerGeneralAddressArr ? customerGeneralAddressArr[4] : ""}" id="officePostalCity" />
            </div>
        </div>
    </div>

    <!--fieldContainerRight-->

    <div class="fieldContainerRight">
        <div class="rowContainer">

        </div>
        <div class="rowContainer">
            <div class="fieldContainer required fcInputNumber">
                <label for="officeCountryId">
                    <span class="fieldsName">Land<b>:</b></span>
                </label>
                <%= "${new CoreParamsHelperTagLib().showCountryList("officeCountryId","${officeContryId.toString()}")}" %>
            </div>
        </div>
    </div>

    <!--fieldContainerRight-->

</div>


<h3><b>Postadres</b></h3>
<br>

<hr>

<div id="checkboxId" hidden>
    <input type="checkbox" class="checkboxPos" id="" name=""/>
    <label for="scales" class="checkboxLabel">Gebruik</label>
</div>

<div class="upperContainer" style="margin: 25px 0 90px 0;">

    <div class="fieldContainerLeft">
        <div class="rowContainer">
            <div class="fieldContainer required fcInputText">
                <label for="postalAddLine1">
                    <span class="fieldsName">Straatnaam<b>:</b></span>
                </label>
                <input class="fieldDsn" style="border-radius: 0 !important;" type="text" tabindex="6"
                       onkeydown="checkActualVal(event,this.id,this.value)"
                       onkeyup="checkChangeVal(event,this.id,this.value)"
                       placeholder="" name="postalAddLine1"
                       value="${customerPostalAddressArr ? customerPostalAddressArr[2] : ""}" id="postalAddLine1" />
            </div>
        </div>
        <div class="rowContainer">
            <div class="fieldContainer required fcInputText">
                <label for="postalPostCode">
                    <span class="fieldsName">Postcode<b>:</b></span>
                </label>
                <input class="fieldDsn" style="border-radius: 0 !important;" type="text" tabindex=""
                       onkeydown="checkActualVal(event,this.id,this.value)"
                       onkeyup="checkChangeVal(event,this.id,this.value)"
                       placeholder="" name="postalPostCode"
                       value="${customerPostalAddressArr ? customerPostalAddressArr[7] : ""}" id="postalPostCode" />
            </div>
        </div>
    </div>

    <!--fieldContainerLeft-->

    <div class="fieldContainerRight">
        <div class="rowContainer">
            <div class="fieldContainer required fcInputText">
                <label for="postalAddLine2">
                    <span class="fieldsName">Huisnummer<b>:</b></span>
                </label>
                <input class="fieldDsn" style="border-radius: 0 !important;" type="text" tabindex=""
                       onkeydown="checkActualVal(event,this.id,this.value)"
                       onkeyup="checkChangeVal(event,this.id,this.value)"
                       placeholder="" name="postalAddLine2"
                       value="${customerPostalAddressArr ? customerPostalAddressArr[3] : ""}" id="postalAddLine2" />
            </div>
        </div>
        <div class="rowContainer">
            <div class="fieldContainer required fcInputText">
                <label for="postalCity">
                    <span class="fieldsName">Woonplaats<b>:</b></span>
                </label>
                <input onkeydown="checkActualVal(event,this.id,this.value)"
                       onkeyup="checkChangeVal(event,this.id,this.value)"
                       class="fieldDsn" style="border-radius: 0 !important;" type="text" tabindex="6"
                       placeholder="" name="postalCity"
                       value="${customerPostalAddressArr ? customerPostalAddressArr[4] : ""}" id="postalCity" />
            </div>
        </div>
    </div>

    <!--fieldContainerRight-->

    <div class="fieldContainerRight">
        <div class="rowContainer">

        </div>
        <div class="rowContainer">
            <div class="fieldContainer required fcInputNumber">
                <label for="postalCountryId">
                    <span class="fieldsName">Land<b>:</b></span>
                </label>
                <%= "${new CoreParamsHelperTagLib().showCountryList("postalCountryId","${postalContryId.toString()}")}" %>
            </div>
        </div>

        <g:if test="${flashSuccess1}">
            <div class="rectangle_122" style="margin-left: -645px; width: 943px">
                <div class="msgAlign">
                    <span class="succMgsH"><strong>${flashSuccess1}</strong></span><br>
                    <span>${flashSuccess2}</span>
                </div>
            </div>
        </g:if>

        <g:if test="${flashError1}">
            <div class="rectangle_123" style="margin-left: -645px; width: 943px">
                <div class="msgAlign">
                    <span class="errorMsgH"><strong>${flashError1}</strong></span><br>
                    <span>${flashError2}</span>
                </div>
            </div>
        </g:if>

    </div>

    <!--fieldContainerRight-->

</div>

<div id="changeReqBtnGroup1" class="changeReqConfirmBtn">
    <button onclick="hideChngRwqBtn()" type="button" class="chancelBtn2">
        <span >Annuleer</span>
    </button>
    <button class="submitBtn" type="submit">
        <span >Opslaan</span>
        %{--<g:actionSubmit class="submitBtn" controller ="vendorMaster" action="changeBankAccountInformation" value="${message(code: 'bv.change.request.customer', default: 'Change')}"/>--}%
    </button>
</div>

<input name="officeAddId" value="${customerGeneralAddressArr ? customerGeneralAddressArr[0] : ""}" hidden>
<input name="postalAddId" value="${customerPostalAddressArr ? customerPostalAddressArr[0] : ""}" hidden>