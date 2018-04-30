<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/gsusa/components/global.jsp" %>
<!-- content -->
<cq:include path="content/breaking-news" resourceType="gsusa/components/breaking-news" />
<cq:include script="camp-header.jsp"/>
<% 
	String cookiePlaceholderPath = currentPage.getContentResource().getPath()+ "/mobile-cookie-header";
	Page cp = currentPage;
	while (cp.getParent() != null) {
		cp = cp.getParent();
		if (isCookiePage(cp)) {
			cookiePlaceholderPath = cp.getContentResource().getPath()+ "/mobile-cookie-header";;
		}
	}	
%>
<cq:include path="<%= cookiePlaceholderPath %>" resourceType="gsusa/components/cookie-header" />
<cq:include path="content/top" resourceType="girlscouts-common/components/styled-parsys" />
<cq:include path="content/carousel" resourceType="gsusa/components/carousel" /> 
<cq:include path="content/par" resourceType="girlscouts-common/components/styled-parsys" />
<!-- END of content -->
