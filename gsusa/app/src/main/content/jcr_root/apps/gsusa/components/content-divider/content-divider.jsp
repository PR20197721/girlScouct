<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/gsusa/components/global.jsp" %>
<cq:includeClientLib categories="apps.gsusa.content-divider"/>

<style>
		.content-divider--solid{
            margin:0;
            border:1px solid #DDDDDD;
            <%= properties.get("borderLineSolid","20")%>
            <%= properties.get("borderLineColorSolid","20")%>
            <%= properties.get("borderLineWidthSolid","20")%>
        }
        .content-divider--dotted{
            margin:0;
            border:1px dotted #DDDDDD;

            <%= properties.get("borderLineDotted","20")%>
            <%= properties.get("borderLineColorDotted","20")%>
            <%= properties.get("borderLineWidthDotted","20")%>
        }
        .content-divider--dashed{
            margin:0;
            border:1px dashed #DDDDDD;
			<%= properties.get("borderLineDashed","20")%>
            <%= properties.get("borderLineColorDashed","20")%>
            <%= properties.get("borderLineWidthDashed","20")%>
        }
        .content-divider--image{
            border: 15px solid transparent;
            border-image: url(<%= properties.get("borderImage","/etc/designs/gsusa/clientlibs/images/blockquote_bg.png")%>) 30 / 19px round;

        }
        .content-divider--padding{
			padding-top: <%= properties.get("paddingTop","20")%>px; <%-- 20 is default value--%>
			padding-bottom: <%= properties.get("paddingBottom","20")%>px; <%-- 20 is default value--%>
			padding-left: <%= properties.get("paddingLeft","0")%>px; <%-- 0 is default value--%>
			padding-right: <%= properties.get("paddingRight","0")%>px; <%-- 0 is default value--%>
        }

</style>

<div id="content-divider-config"
	 data-enableHorizontalBorder="<%= properties.get("horizontalBorder","false")%>"
	 data-borderLineStyle="<%= properties.get("borderLineStyle","solid")%>"
	 class ='content-divider--padding'>
     <span>Border Text</span>
</div>