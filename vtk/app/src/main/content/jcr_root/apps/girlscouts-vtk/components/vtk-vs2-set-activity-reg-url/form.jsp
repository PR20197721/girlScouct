<%@page session="false" %><%@include file="/libs/foundation/global.jsp"%>
VTK VS 2.0 Troop ID, User ID, Contact ID Migration Script: <br/>
<form id="form" method="post">
<%
ServletContext ctxt = application.getContext("/apps/girlscouts-vtk/components/vtk-vs2-set-activity-reg-url");
boolean stopThread = (request.getParameter("cmd") != null && request.getParameter("cmd").equals("stop"));
boolean threadExists = ctxt.getAttribute("thread") != null;
boolean threadIsAlive = threadExists && ((Thread)(ctxt.getAttribute("thread"))).isAlive();
%>
Dry Run?: <input name="dry_run" type="checkbox" <%= (request.getParameter("dry_run")!=null)?request.getParameter("dry_run"):""%> /><br/>
<input type="hidden" name="cmd" value="run" />
</form>