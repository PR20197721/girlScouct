<%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %><%
%><%
String id = currentSite.get("googleAnalyticsId", "");
if (!id.isEmpty()) {
%>

<script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
try{
var pageTracker = _gat._getTracker("<%= id %>");
pageTracker._trackPageview();
} catch(err) {}</script>
<% } %>