<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp" %>

<cq:includeClientLib categories="apps.girlscouts" />
<cq:defineObjects />

<%
   Boolean isInHamburger = true;
   request.setAttribute("isInHamburger", isInHamburger);
%>
   <cq:include script="main.jsp"/>
    