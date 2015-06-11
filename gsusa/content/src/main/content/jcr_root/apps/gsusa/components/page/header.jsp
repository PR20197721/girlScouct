<%@include file="/libs/foundation/global.jsp" %>
<!-- header -->
<%
    // All pages share the same header from the site root.
    String headerPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/header";
    String logoPath = headerPath + "/logo";
    String headerNavPath = headerPath + "/header-nav";
%>
<div class="top-header row">
    <section class="logo-section">
        <a href="/" title="girscouts.com home">
            <cq:include path="<%= logoPath %>" resourceType="gsusa/components/logo" />
        </a>
    </section>
    <section class="utility show-for-medium-up">
    	<div>
    	    <cq:include path="eyebrow-nav" resourceType="gsusa/components/eyebrow-nav" />
        </div>
        <form action="/content/gateway/en/site-search.html" method="get" class="search-form">
            <input type="search" name="q" placeholder="Search" />
        </form>
    </section>
</div>
<cq:include path="<%= headerNavPath %>" resourceType="gsusa/components/header-nav" />
<!--/header -->