<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%@ taglib prefix="gsusa" uri="https://girlscouts.org/gsusa/taglib" %>


<%
	String imgAlt = properties.get("imageAlt", "");
%>
	<gsusa:image relativePath="image" styleId="mainGSLogo" alt="<%=imgAlt%>" title="<%=imgAlt%>" />
<%
	String headerNavPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/header/header-nav";   
	ValueMap headerNavProps = resourceResolver.resolve(headerNavPath).adaptTo(ValueMap.class);
	try {
	    if (headerNavProps.get("displayStickyNav", false)) {
	        %><div class="logo">
				<gsusa:image relativePath="stickyNavImage" styleClass="sticky-nav-GS-logo" alt="<%=imgAlt%>" title="<%=imgAlt%>" />
	        </div><%
	   }
	} catch(Exception e) {}
%>