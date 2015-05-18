<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<!-- body -->
<body>
  <cq:include script="header.jsp"/>
  <cq:include script="content.jsp"/>
  <% setCssClasses("foo bar", request); // Set CSS classes for the including div, separated by spaces. %>
  <cq:include script="footer.jsp"/>
  <cq:include script="bodylibs.jsp"/>
</body>
<!-- END of body -->
