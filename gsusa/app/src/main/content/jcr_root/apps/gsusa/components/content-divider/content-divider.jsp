<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/gsusa/components/global.jsp" %>
<cq:includeClientLib categories="apps.gsusa.content-divider"/>

<style>
		.content-divider--solid{
            border:<%= properties.get("borderLineSolid","1")%>px solid <%= properties.get("borderLineColorSolid","#DDDDDD")%>;
			width:<%= properties.get("borderLineWidthSolid","100")%>%;
        }
        .content-divider--dotted{
			border:<%= properties.get("borderLineDotted","1")%>px dotted <%= properties.get("borderLineColorDotted","#DDDDDD")%>;
            width:<%= properties.get("borderLineWidthDotted","100")%>%;
        }
        .content-divider--dashed{
            border:<%= properties.get("borderLineDashed","1")%>px dashed <%= properties.get("borderLineColorDashed","#DDDDDD")%>;
            width:<%= properties.get("borderLineWidthDashed","100")%>%;
        }
        .content-divider--image{
            border:1px solid transparent;
            border-image: url(<%= properties.get("borderImage","/etc/designs/gsusa/clientlibs/images/blockquote_bg.png")%>) 30 / 19px round;

        }
        .content-divider--padding{
			padding-top: <%= properties.get("paddingTop","20")%>px; <%-- 20 is default value--%>
			padding-bottom: <%= properties.get("paddingBottom","20")%>px; <%-- 20 is default value--%>
			padding-left: <%= properties.get("paddingLeft","0")%>px; <%-- 0 is default value--%>
			padding-right: <%= properties.get("paddingRight","0")%>px; <%-- 0 is default value--%>
        }

</style>
<%
String  borderLineStyle  = properties.get("borderLineStyle","solid");
%>
<div data-enableHorizontalBorder="<%= properties.get("horizontalBorder","false")%>"
	 class ='content-divider-config content-divider--padding content-divider--<%= borderLineStyle %>'>
     <span class="bg-image">Border Text</span>
</div>