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
<!-- Global site tag (gtag.js) - Google Analytics -->
    <script async src="https://www.googletagmanager.com/gtag/js?id=<%=id%>"></script>
    <script>
    window.dataLayer = window.dataLayer || [];
    function gtag(){dataLayer.push(arguments);}
    gtag('js', new Date());
    gtag('config', '<%=id%>');
    </script>

<!-- END GA Tracking -->
<% } %>