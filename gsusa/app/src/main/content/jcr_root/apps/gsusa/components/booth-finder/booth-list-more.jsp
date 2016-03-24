<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>

{{#each booths}}
	<cq:include script="booth-item.jsp" />
{{/each}}