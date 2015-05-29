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
        <ul class="inline-list">
            <li><a href="#" title="Join">Join</a></li>
            <li><a href="#" title="Volunteer">Volunteer</a></li>
            <li><a href="#" title="Invest in Girls">Invest in Girls</a></li>
            <li><a href="#" title="En Espanol">En Espa&#241;ol</a></li>
        </ul>
        <form action="/content/gateway/en/site-search.html" method="get" class="search-form">
            <input type="text" name="q" placeholder="Search" />
        </form>
    </section>
</div>
<cq:include path="<%= headerNavPath %>" resourceType="gsusa/components/header-nav" />
<!--/header -->