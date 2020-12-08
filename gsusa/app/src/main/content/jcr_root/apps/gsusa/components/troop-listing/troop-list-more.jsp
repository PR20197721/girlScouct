<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>

{{#each booths}}
	<cq:include script="troop-item.jsp" />
{{/each}} 