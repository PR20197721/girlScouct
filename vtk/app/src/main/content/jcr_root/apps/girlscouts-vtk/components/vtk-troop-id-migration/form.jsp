<%@page session="false" %><%@include file="/libs/foundation/global.jsp"%>
VTK Year Plan Data Migration Script: <br/>
<form id="form" method="post">
<%
ServletContext ctxt = application.getContext("/apps/girlscouts-vtk/components/vtk-troop-id-migration");
boolean stopThread = (request.getParameter("cmd") != null && request.getParameter("cmd").equals("stop"));
boolean threadExists = ctxt.getAttribute("thread") != null;
boolean threadIsAlive = threadExists && ((Thread)(ctxt.getAttribute("thread"))).isAlive();
%>
Dry Run?: <input name="dry_run" type="checkbox" "<%= (request.getParameter("dry_run")!=null)?request.getParameter("dry_run"):""%>" /><br/>
Backup?: <input name="backup" type="checkbox" "<%= (request.getParameter("backup")!=null)?request.getParameter("backup"):""%>" /><br/>
<input type="hidden" name="cmd" value="run" />
</form>