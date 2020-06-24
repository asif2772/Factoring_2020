<%
    def companyName
    if(session.companyInfo)
        companyName = session.companyInfo[0].company_full_name
    def currentUrl = params.action
 %>
<div id="footer">
    <div id="footer-inner">
        <div id="footer-inner-left" class="footer-text-white">
            Copyright &copy; 2016 NL Credit Services
        </div>
        <div id="footer-inner-right" class="footer-text-white">
          <g:if test="${currentUrl != 'user'}">
              ${companyName}
          </g:if>
        </div>
    </div>
</div>

<asset:javascript src="web/jquery.hoverIntent.minified.js" />
<asset:javascript src="web/jquery.dcmegamenu.1.3.3.js" />

<script type="text/javascript">
    $(document).ready(function(){

        //Mega menu init
        $('#mega-menu-1').dcMegaMenu({
            rowItems: '3',
            speed: 'fast',
            effect: 'slide'});

        $('.inner').corner('3px');
        //$('.pagination').corner('bottom');
        $('.content').corner('3px');
        $('.buttons').corner('3px');
        $('.save').corner('3px');
        $('#income').corner('3px');
        $('#footer').corner('bottom');
        $('.allIncomeHead').corner('top');
        $('input').corner('3px');

        //Help dialog init
        $( "#helpinfodlg" ).dialog({ autoOpen: false, show : "scale", hide : "scale",height: 'auto',width: 450,});
        $( "#helpBtn" ).click(function() {
            var position =  $("#helpBtn").position();
            $("#helpinfodlg").dialog("option", "position", [position.left-40, position.top+70]);
            $( "#helpinfodlg" ).dialog("open");
        });

//        $('table.highchart').highchartTable();
    });


</script>