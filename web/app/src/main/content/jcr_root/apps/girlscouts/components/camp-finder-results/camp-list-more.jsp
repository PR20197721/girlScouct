<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>

{{#each camps}}
	<cq:include script="camp-item.jsp" />
{{/each}}