<%@include file="/libs/foundation/global.jsp" %>
<!-- header -->
<%
    // All pages share the same header from the site root.
    String headerPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/header";
    String logoPath = headerPath + "/logo";
    String headerNavPath = headerPath + "/header-nav";
    String headerJoinPath = headerPath + "/join";
%>
<div class="top-header row">
    <section class="logo-section">
        <a href="/content/gsusa/en.html" title="girscouts.com home">
            <cq:include path="<%= logoPath %>" resourceType="gsusa/components/logo" />
        </a>
    </section>
    <section class="utility show-for-medium-up">
    	<div>
	        <cq:include path="<%=headerNavPath %>" resourceType="gsusa/components/eyebrow-nav" />
	        <cq:include path="header/join" resourceType="gsusa/components/header-join-now"/>
	        <cq:include path="header/volunteer" resourceType="gsusa/components/header-volunteer-now"/>
        </div>
        <div>
    	    <cq:include path="header/search" resourceType="gsusa/components/search-box" />
        </div>
    </section>
</div>
<cq:include path="<%= headerNavPath %>" resourceType="gsusa/components/header-nav" />
<!--/header -->