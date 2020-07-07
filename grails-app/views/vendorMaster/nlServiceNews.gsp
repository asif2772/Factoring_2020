<%@ page import="factoring.CoreParamsHelperTagLib;" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="mainCustomerPortal">
    <g:set var="entityName" value="${message(code: 'bv.menu.Customers', default: 'CustomerMaster')}"/>
    <title><g:message code="bv.menu.Customers" args="[entityName]"/></title>
    <style>
        .feed-container {
            border: none;
        }
        .feed-container .header {
            background-color: #f1ff33;
        }
    </style>
</head>
<% def contextPath = request.getServletContext().getContextPath()%>

<body>

<!-- start sw-rss-feed code -->
<div class="companyForm" style="margin-top: 40px; border: none;">

<script type="text/javascript">
    <!--
    rssfeed_url = new Array();
    rssfeed_url[0]="https://nlcreditservices.nl/feed/";
    rssfeed_frame_width="1204";
    rssfeed_frame_height="400";
    rssfeed_scroll="on";
    rssfeed_scroll_step="5";
    rssfeed_scroll_bar="off";
    rssfeed_target="_blank";
    rssfeed_font_size="13";
    rssfeed_font_face="Open Sans', sans-serif";
    rssfeed_border="off";
    rssfeed_css_url="";
    rssfeed_title="on";
    rssfeed_title_name="";
    rssfeed_title_bgcolor="rgba(4,125,184,255)";
    rssfeed_title_color="#fff";
    rssfeed_title_bgimage="";
    rssfeed_footer="off";
    rssfeed_footer_name="rss feed";
    rssfeed_footer_bgcolor="#fff";
    rssfeed_footer_color="#333";
    rssfeed_footer_bgimage="";
    rssfeed_item_title_length="100";
    rssfeed_item_title_color="#666";
    rssfeed_item_bgcolor="#fff";
    rssfeed_item_bgimage="";
    rssfeed_item_border_bottom="on";
    rssfeed_item_source_icon="off";
    rssfeed_item_date="off";
    rssfeed_item_description="on";
    rssfeed_item_description_length="300";
    rssfeed_item_description_color="#666";
    rssfeed_item_description_link_color="#333";
    rssfeed_item_description_tag="off";
    rssfeed_no_items="0";
    rssfeed_cache = "150005c00ed0d540f3f516e72172b50f";
    //-->
</script>
<script type="text/javascript" src="//feed.surfing-waves.com/js/rss-feed.js"></script>
<!-- The link below helps keep this service FREE, and helps other people find the SW widget. Please be cool and keep it! Thanks. -->
%{--<div style="color:#ccc;font-size:10px; text-align:right; width:1204px;">powered by <a href="https://surfing-waves.com" rel="noopener" target="_blank" style="color:#ccc;">Surfing Waves</a></div>--}%
<!-- end sw-rss-feed code -->

</div>

<g:javascript>

    $(document).ready(function() {
        $('#menuNews').addClass('menuOrangeBeam');
    });




</g:javascript>

</body>
</html>