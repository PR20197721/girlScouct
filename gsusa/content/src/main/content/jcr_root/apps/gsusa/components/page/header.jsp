<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%@page import="com.day.cq.wcm.api.WCMMode" %>
<!-- header -->
<%
    // All pages share the same header from the site root, except Join and Volunteer!
    String headerPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/header";
    String logoPath = headerPath + "/logo";
    String headerNavPath = headerPath + "/header-nav";
    String eyebrowNavPath = headerPath + "/eyebrow-nav";
    String headerSearchPath = headerPath + "/search";
    System.out.println(headerSearchPath);
    //The cookie header will be created under its own parent, so that all the children of this parent will share the same mobile header
    //String cookieHeaderPath = currentPage. + "/cookie-header";
%>
<div class="top-header row">
    <section class="logo-section">
        <a href="/content/gsusa/en.html" tabindex="1">
            <cq:include path="<%= logoPath %>" resourceType="gsusa/components/logo" />
        </a>
    </section>
    <section class="utility show-for-medium-up">
    	<div style="" class="clearfix">
            <div id='spinner' class='spinner'></div>
            <div style="" class="join-buttons">
    	        <cq:include path="header/join" resourceType="gsusa/components/header-join-now"/>
    	        <cq:include path="header/volunteer" resourceType="gsusa/components/header-volunteer-now"/>
            </div>
            <cq:include path="<%=eyebrowNavPath %>" resourceType="gsusa/components/eyebrow-nav" />
	</div>
        <div class="clearfix float-right">
    	    <cq:include path="<%= headerSearchPath %>" resourceType="gsusa/components/search-box" />
        </div>
    </section>
</div>
<cq:include path="<%= headerNavPath %>" resourceType="gsusa/components/header-nav" />
<% if (isCookiePage(currentPage)) { 
	Page cp = currentPage;
	while (cp != null && cp.getContentResource() != null && !"girlscouts/components/placeholder-page".equals(cp.getContentResource().getResourceType())) {
		cp = cp.getParent();
	}
	String cookiePlaceholderPath = cp.getContentResource().getPath();
%>
	<cq:include path="<%= cookiePlaceholderPath %>" resourceType="gsusa/components/cookie-header" />
<%
} %>
<!--/header -->
