<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>

<div id="booth-list">
{{#each booths}}
	<cq:include script="booth-item.jsp" />
{{/each}}
</div>