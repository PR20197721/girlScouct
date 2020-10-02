<%@include file="/libs/foundation/global.jsp"%>
<%
Page newCurrentPage = (Page)request.getAttribute("newCurrentPage");
Design newCurrentDesign= (Design)request.getAttribute("newCurrentDesign");
if (newCurrentPage != null) {
    currentPage = newCurrentPage;
}
%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@page session="false" %>
<%
String id = currentSite.get("googleAnalyticsId", "");
Boolean displayfeatures = currentSite.get("./googleDisplayFeatures", Boolean.FALSE);
 
String googleOptimizeId = currentSite.get("googleOptimizeId", "");
Boolean googleOptimize = currentSite.get("./googleOptimize", Boolean.FALSE);


if (!id.isEmpty()) {
%>
<!-- Google Analytics Tracking -->
    <script type="text/javascript">
	$(document).ready(function() {
		(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function()
		{ (i[r].q=i[r].q||[]).push(arguments)}
		,i[r].l=1*new Date();a=s.createElement(o),
		m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
		})(window,document,'script','//www.google-analytics.com/analytics.js','ga');
		ga('create', '<%=id%>', 'auto');
			<%if(displayfeatures){%>
				ga('require', 'displayfeatures'); 
			<%}%>
			<%if(googleOptimize){%>
				ga('require', '<%=googleOptimizeId%>'); 
			<%}%>
		ga('send', 'pageview');
	});
	</script>
<!-- END GA Tracking -->
<% } %>