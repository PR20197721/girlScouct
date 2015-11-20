<%@page import="org.girlscouts.web.gsusa.component.boothfinder.BoothFinder.Council" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/booth-finder/replace-council-info.jsp"%>
<%@page session="false" %>
<%
Council council = (Council)request.getAttribute("gsusa_council_info");
String defaultText = "The cookie sale is underway for the <a href=\"{{url}}\" target=\"_blank\"><strong>{{name}}</strong></a>. " +
	"No cookie booth sales are currently scheduled, but keep checking back for updated results.";
String text = properties.get("path1Text", defaultText);
if (council == null) {
	council = new Council();
}
%>
<h1>Cookie sale is underway.</h1>
<p><%= replaceCouncilInfo(text, council) %></p>