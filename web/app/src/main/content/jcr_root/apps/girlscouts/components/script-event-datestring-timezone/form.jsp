<%@page session="false" %><%@include file="/libs/foundation/global.jsp"%>
Web Event Date Strings to Dates and Timezone Property addition<br/>
<form id="form" method="post">
<%
ServletContext ctxt = application.getContext("/apps/girlscouts/components/script-event-datestring-timezone");
boolean stopThread = (request.getParameter("cmd") != null && request.getParameter("cmd").equals("stop"));
boolean threadExists = ctxt.getAttribute("thread") != null;
boolean threadIsAlive = threadExists && ((Thread)(ctxt.getAttribute("thread"))).isAlive();
%>
Dry Run?: <input name="dry_run" type="checkbox" "<%= (request.getParameter("dry_run")!=null)?request.getParameter("dry_run"):""%>" /><br/>
Backup?: <input name="backup" type="checkbox" "<%= (request.getParameter("backup")!=null)?request.getParameter("backup"):""%>" /><br/>
<input type="hidden" name="cmd" value="run" />
</form>