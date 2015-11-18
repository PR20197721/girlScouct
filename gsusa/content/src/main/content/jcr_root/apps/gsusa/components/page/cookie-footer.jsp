<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/gsusa/components/global.jsp" %>
<!-- footer -->
<% if (isCookiePage(currentPage)) { 
	String cookiePlaceholderPath = currentPage.getContentResource().getPath();
	Page cp = currentPage;
	while (cp.getParent() != null) {
		cp = cp.getParent();
		if (isCookiePage(cp)) {
			cookiePlaceholderPath = cp.getContentResource().getPath();
		}
	}
%>
	  <cq:include path="<%= cookiePlaceholderPath %>" resourceType="gsusa/components/cookie-footer" />
<% } %>
