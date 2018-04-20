<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/gsusa/components/global.jsp" %>

<!-- right -->
<%
	if (isCookiePage(currentPage)) {
		// All cookie pages share the same right rail.
        String cookieRightPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/cookie-right";
        %>
        <cq:include path="cookieRightPath" resourceType="girlscouts-common/components/styled-parsys" /><%
	} else {
%>
        <div id="rightContent">
             <cq:include path="content/right/par" resourceType="girlscouts-common/components/styled-parsys" />
        </div>
<%
	}
%>
<!-- END of right -->