<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%
Random rand = new Random();
int randomNo = rand.nextInt(900000) + 100000;
%>
<cq:includeClientLib categories="apps.gsusa.content-divider"/>

<style>
		.content-divider--solid_<%=randomNo%>{
            border:<%= properties.get("borderLineSolid","1")%>px solid <%= properties.get("borderLineColorSolid","#DDDDDD")%>;
			width:<%= properties.get("borderLineWidthSolid","100")%>%;
        }
        .content-divider--dotted_<%=randomNo%>{
			border:<%= properties.get("borderLineDotted","1")%>px dotted <%= properties.get("borderLineColorDotted","#DDDDDD")%>;
            width:<%= properties.get("borderLineWidthDotted","100")%>%;
        }
        .content-divider--dashed_<%=randomNo%>{
            border:<%= properties.get("borderLineDashed","1")%>px dashed <%= properties.get("borderLineColorDashed","#DDDDDD")%>;
            width:<%= properties.get("borderLineWidthDashed","100")%>%;
        }
        .content-divider--image_<%=randomNo%>:before , .content-divider--image_<%=randomNo%>:after{
		    content: "";
		    background-image: url(<%= properties.get("borderImage","/etc/designs/gsusa/clientlibs/images/blockquote_bg.png")%>);
		    height: 0.5625rem;
		    width: 100%;
		    display: table;
        }

        .content-divider--padding_<%=randomNo%>{
			margin-top: <%= properties.get("paddingTop","20")%>px; <%-- 20 is default value--%>
			margin-bottom: <%= properties.get("paddingBottom","20")%>px; <%-- 20 is default value--%>
			margin-left: <%= properties.get("paddingLeft","0")%>px; <%-- 0 is default value--%>
			margin-right: <%= properties.get("paddingRight","0")%>px; <%-- 0 is default value--%>
        }
        .disable-border_<%=randomNo%>{
            border-top :0 !important;
            border-bottom:0 !important;
        }

</style>
<%
String  borderLineStyle  = properties.get("borderLineStyle","solid");
Boolean  horizontalBorder  = properties.get("horizontalBorder",false);
String disableBorderClass="";
if(horizontalBorder){
	disableBorderClass="disable-border";
}
%>
<div class ='content-divider-config content-divider--padding_<%=randomNo%> content-divider--<%= borderLineStyle %>_<%=randomNo%> <%= disableBorderClass %>_<%=randomNo%>'>
     <span></span>
</div>