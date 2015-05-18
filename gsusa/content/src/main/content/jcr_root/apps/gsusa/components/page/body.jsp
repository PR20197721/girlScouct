<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<!-- body -->
<body>
    <!--Set CSS classes for the including div, separated by spaces.-->
    <cq:include script="header.jsp"/>
    <cq:include script="content.jsp"/>
    <% setCssClasses("clearfix", request);%>
    <cq:include script="footer.jsp"/>
    <cq:include script="bodylibs.jsp"/>
</body>
<!-- /of body -->
