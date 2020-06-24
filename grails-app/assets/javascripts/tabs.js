window.onload=function() {
//    alert("okk");
    // get tab container

  	var container = document.getElementById("tabContainer");
	var tabcon = document.getElementById("tabscontent");
	//alert(tabcon.childNodes.item(1));
    // set current tab
    var navitem = document.getElementById("tabHeader_1");

    //store which tab we are on
    var ident = navitem.id.split("_")[1];
    //alert(ident)
    navitem.parentNode.setAttribute("data-current",ident);
    //set current tab with class of activetabheader
    navitem.setAttribute("class","tabActiveHeader");

    //hide two tab contents we don't need
    var current = navitem.parentNode.getAttribute("data-current");
   	var pages = tabcon.getElementsByTagName("div");

    for (var i = 1; i < pages.length; i++) {
         var trimmed = pages.item(i).className.replace(/^\s+|\s+$/g, '') ;
         if( trimmed == "fieldContainer" || trimmed == "fieldContainer  required"){
             pages.item(i).style.display = "block";
         }else{
             pages.item(i).style.display = "none";
         }
    }
    //this adds click event to tabs
    var tabs = container.getElementsByTagName("li");
    for (var i = 0; i < tabs.length; i++) {
        tabs[i].onclick=displayPage;
    }
}

// on click of one of tabs
function displayPage() {
  var current = this.parentNode.getAttribute("data-current");
  //alert(current)
  //remove class of activetabheader and hide old contents
  document.getElementById("tabHeader_" + current).removeAttribute("class");
  document.getElementById("tabpage_" + current).style.display="none";

  var ident = this.id.split("_")[1];
  //add class of activetabheader to new active tab and show contents
  this.setAttribute("class","tabActiveHeader");
  document.getElementById("tabpage_" + ident).style.display="block";
  this.parentNode.setAttribute("data-current",ident);
  tabAjaxLoadData(ident);
}

function tabAjaxLoadData(tabPageId){
    if(factorPage == "debtor"){
        var tabLoadDataUrl = "";
        var sendData = {debtorId:debId};
        if(tabPageId==8){
            tabLoadDataUrl = appContext+"/customerMaster/loadPaidInvoices";
        }
        else if(tabPageId==9){
            tabLoadDataUrl = appContext+"/customerMaster/loadMoveHistoryTransaction";
        }
        else if(tabPageId==7){
            tabLoadDataUrl = appContext+"/customerMaster/loadOutstandingInvoices";
        }
        else if(tabPageId==6){
            tabLoadDataUrl = appContext+"/customerMaster/loadFinancialTransaction";
        }
        else if(tabPageId==3){
            tabLoadDataUrl = appContext+"/customerMaster/loadPostalAddress";
        }
        else if(tabPageId==2){
            tabLoadDataUrl = appContext+"/customerMaster/loadGeneralAddress";
        }
        if(!isAlreadyLoaded[tabPageId]){
            $.ajax({
                url: tabLoadDataUrl,
                data: sendData,
                success: function(data) {
                    $("#tabPageLoadData"+tabPageId).html(data);
                    isAlreadyLoaded[tabPageId] = true;
                },
                error: function(){
                }
            });
        }

    }
    else if(factorPage == "customer"){
        var tabLoadDataUrl = "";
        sendData = {customerId:cusId,venId:dvenId,venPrefix:dvenPrefix,venCode:dvenCode,venName:dvenName};
        if(tabPageId==2){
            tabLoadDataUrl = appContext+"/vendorMaster/loadCustomerGeneralAddress";
        }
        else if(tabPageId==3){
            tabLoadDataUrl = appContext+"/vendorMaster/loadCustomerPostalAddress";
        }
        else if(tabPageId==6){
            tabLoadDataUrl = appContext+"/vendorMaster/loadCustomerFinancialTransaction";
        }
        else if(tabPageId==7){
            tabLoadDataUrl = appContext+"/vendorMaster/loadCustomerOutstandingInvoices";
        }
        else if(tabPageId==8){
            tabLoadDataUrl = appContext+"/vendorMaster/loadCustomerPaidInvoices";
        }
        if(!isAlreadyLoaded[tabPageId]){
            $.ajax({
                url: tabLoadDataUrl,
                data: sendData,
                success: function(data) {
                    if(tabPageId=="6")
                        $("#financialTabInfoData").html(data);
                    else
                        $("#tabPageLoadData"+tabPageId).html(data);
                    isAlreadyLoaded[tabPageId] = true;
                },
                error: function(){
                }
            });
        }

    }
}

function showTabPage(tabIndex) {

//    alert(current)
    //remove class of activetabheader and hide old contents
    document.getElementById("tabHeader_" + tabIndex).removeAttribute("class");
    document.getElementById("tabpage_" + tabIndex).style.display="none";

    var ident = this.id.split("_")[1];
    //add class of activetabheader to new active tab and show contents
    this.setAttribute("class","tabActiveHeader");
    document.getElementById("tabpage_" + ident).style.display="block";
    this.parentNode.setAttribute("data-current",ident);
}

