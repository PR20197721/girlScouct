<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>

{{#each Troops}}
	<cq:include script="troop-item.jsp" />
{{/each}}