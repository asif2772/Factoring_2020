<%@ page contentType="text/html" %>
<html>
<head>
    <title><g:message code="default.customer.create.welcome.label" default="Welcome" /></title>
    <meta name='layout' content='main' />
</head>
<body>
<div style="font-family: Arial, Helvetica, sans-serif;font-size: 13.4px;">

    Beste ${firstName} ${lastName},<br/><br/>


    <p>Welkom bij het klantenportaal van NL Credit Services.</p>
    <p>U kunt inloggen met de volgende gegevens:</p>

    <p>Gebruikersnaam: ${email} </p>
    <p>Wachtwoord: ${password} </p>

    <p>Mocht u vragen hebben over het gebruik van ons klantenportaal, verzoeken wij u vriendelijk contact op te nemen met uw accountmanager.</p>

    <br/><br/>

    <div class="mailSignature" style="color:#a6a6a6">NL Credit Services B.V.</div>
    <div class="mailSignature">Gooimeer 3</div>
    <div class="mailSignature">1411 DC Naarden</div>
    <div class="mailSignature">T: 085-2733222</div>
    <div class="mailSignature"><a href="www.nlcreditservices.nl">www.nlcreditservices.nl</a></div><br/><br/>
</div>

<img src="cid:mailSignatureImg" />
</body>
</html>