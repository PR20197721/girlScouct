<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@page import="com.day.cq.wcm.api.WCMMode,
org.girlscouts.vtk.ejb.*,
org.girlscouts.vtk.helpers.*,
org.girlscouts.vtk.dao.*,
org.girlscouts.vtk.models.User" %>

<div id="event-cart">
</div>
<%
HttpSession session = request.getSession();
%><script> retrieveEvents("/content/girlscouts-shared/event-cart.html"); </script>