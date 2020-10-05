<%@page session="false" %><%@include file="/libs/foundation/global.jsp"%>
Join/Volunteer/Renew Links Migration Script: <br/>
<form id="form" method="post">
<%
ServletContext ctxt = application.getContext("/apps/girlscouts/components/join-volunteer-vs2-migration");
boolean stopThread = (request.getParameter("cmd") != null && request.getParameter("cmd").equals("stop"));
boolean threadExists = ctxt.getAttribute("thread") != null;
boolean threadIsAlive = threadExists && ((Thread)(ctxt.getAttribute("thread"))).isAlive();
%>
Migrate Join Links: <input name="updateJoin" type="checkbox" <%= (request.getParameter("updateJoin")!=null)?"checked":""%> /><br/>
Migrate Volunteer Links: <input name="updateVolunteer" type="checkbox" <%= (request.getParameter("updateVolunteer")!=null)?"checked":""%> /><br/>
Migrate Renew Links: <input name="updateRenew" type="checkbox" <%= (request.getParameter("updateRenew")!=null)?"checked":""%> /><br/>
Dry Run?: <input name="dry_run" type="checkbox" <%= (request.getParameter("dry_run")!=null)?"checked":""%> /><br/>
<input type="hidden" name="cmd" value="run" />
</form>