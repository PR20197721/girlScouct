<%@page import="org.girlscouts.vtk.utils.imports.ImportGSDocs" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="../include/session.jsp" %>
<%@include file="../admin/toolbar.jsp" %>
<%
    final org.apache.sling.jcr.api.SlingRepository repos = sling.getService(org.apache.sling.jcr.api.SlingRepository.class);
    javax.jcr.Session _session = repos.loginAdministrative(null);
    String mid = request.getParameter("mid");
    new ImportGSDocs(_session).getMeetings(mid);
%>