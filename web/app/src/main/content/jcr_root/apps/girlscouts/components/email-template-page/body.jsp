<%@include file="/libs/foundation/global.jsp" %>

<%
// To properly reflect the contents of an html email, all CSS and Javascript are removed from this page type
%>
<body>
<%= properties.get("data/content","") %>
<hr>
Please use <a href="<%= currentPage.getPath() + ".scaffolding.html" %>">scaffolding</a> to edit
</body>
