<%@include file="/libs/foundation/global.jsp"%>
Event Review
<cq:include path="data" resourceType="girlscouts/components/event" />
<% String eventLink = currentPage.getPath() + ".html"; %>
<a href="<%= eventLink %>" target="_blank" onclick="window.parent.location='<%= eventLink %>'; return false;">Go to the Event</a>