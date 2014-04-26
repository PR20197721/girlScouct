<%@include file="/libs/foundation/global.jsp"%>
<% String eventLink = currentPage.getPath() + ".html"; %>
<cq:include path="data" resourceType="girlscouts/components/event" />
<a href="<%= eventLink %>" target="_blank" onclick="window.parent.location='<%= eventLink %>'; return false;">Go to the Event</a>