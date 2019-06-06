<%@page import="com.day.cq.wcm.api.WCMMode,
                com.day.cq.wcm.foundation.Placeholder,
                java.util.Random,
                java.util.Map,
                java.util.HashMap,
                com.day.cq.search.QueryBuilder,
                com.day.cq.search.Query,
                com.day.cq.search.PredicateGroup,
                com.day.cq.search.result.SearchResult"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/global.jsp"%>

<!-- header -->
<%
// All pages share the same header from the site root
String headerPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/header";
String logoPath = headerPath + "/logo";
String headerNavPath = headerPath + "/header-nav";
String eyebrowNavPath = headerPath + "/eyebrow-nav";
String headerSearchPath = headerPath + "/search";
String joinPath = headerPath + "/join";
String volunteerPath = headerPath + "/volunteer";
//The cookie header will be created under its own parent, so that all the children of this parent will share the same mobile header
//String cookieHeaderPath = currentPage. + "/cookie-header";
%>
                
<div class="top-header row">
    <section class="logo-section">
<%
    ValueMap logoProps = resourceResolver.resolve(logoPath).adaptTo(ValueMap.class);
    String logoLink = logoProps.get("logoLink", "") +  ".html";
%>
        <span id="menuIcon"class="mobileIcons">
            <a class="side-nav-toggle menu-icon"><img src="/etc/designs/girlscouts/images/hamburger.png" width="30" height="28" alt="right side menu hamburger icon"/></a>
        </span>
        <a href="<%= logoLink %>" tabindex="1">
            <cq:include path="<%= logoPath %>" resourceType="gsusa/components/logo" />
        </a>
        <span id="searchIcon" class="mobileIcons" searchShown="false">
            <a class="mobile-search-icon"><img src="/etc/designs/girlscouts/images/search_white.png" width="30" height="21" alt="search icon"/></a>
        </span>
    </section>
    <section class="utility show-for-medium-up">
        <div style="" class="clearfix">
            <div id='spinner' class='spinner'></div>
            <div style="" class="join-buttons">
                <cq:include path="<%=joinPath %>" resourceType="gsusa/components/header-join-now" />
                <cq:include path="<%=volunteerPath %>" resourceType="gsusa/components/header-volunteer-now" />
            </div>
            <cq:include path="<%=eyebrowNavPath %>" resourceType="gsusa/components/eyebrow-nav" />
        </div>
        <div class="clearfix float-right">
            <cq:include path="<%= headerSearchPath %>" resourceType="gsusa/components/search-box" />
        </div>
        <cq:include path="content/middle/pdf-print" resourceType="girlscouts/components/pdf-print" />
    </section>
</div>

<cq:include path="<%= headerNavPath %>" resourceType="gsusa/components/header-nav" />

<% 
if (isCookiePage(currentPage)) { 
    String cookiePlaceholderPath = currentPage.getContentResource().getPath();
    Page cp = currentPage;
    while (cp.getParent() != null) {
        cp = cp.getParent();
        if (isCookiePage(cp)) {
            cookiePlaceholderPath = cp.getContentResource().getPath();
        }
    }
    %><cq:include path="<%= cookiePlaceholderPath %>" resourceType="gsusa/components/cookie-header" /><%
}
%>
    
<cq:include script="camp-header.jsp" />
<!--/header -->