<%@include file="/libs/foundation/global.jsp" %>
<%
    final String DATA_KEY = "gsusa.header-nav.item.data";
    final String[] navs = properties.get("navs", String[].class);
    if (navs != null) {
%>
    <nav class="top-bar show-for-medium-up large-19 medium-23 columns small-24 large-push-5" data-topbar role="navigation">
        <section class="top-bar-section">
            <ul>
                <% for (int i = 0; i < navs.length; i++) {
                    request.setAttribute(DATA_KEY, navs[i]);
                %>
                    <cq:include script="item.jsp" />
                <% } %>
            </ul>
        </section>
    </nav>

<%
    request.removeAttribute(DATA_KEY);
}
%>
<!-- OFF CANVAS MENU BAR -->
    <nav class="tab-bar hide-for-medium-up">
        <section>
            <form action="/content/gateway/en/site-search.html" method="get" class="search-form">
                <input type="text" name="q" placeholder="Search" />
                <span class="icon-search-magnifying-glass"></span>
            </form>
        </section>
        <section class="right-small">
            <a class="right-off-canvas-toggle menu-icon" role="button" href="#"><span></span></a>
        </section>
    </nav> <!-- END NAV.TAB-BAR HIDE-FOR-LARGE-UP -->

    <!-- OFF CANVAS MENU -->
    <nav class="right-off-canvas-menu">
        <ul class="off-canvas-list">
            <% for (int i = 0; i < navs.length; i++) {
                request.setAttribute(DATA_KEY, navs[i]);
            %>
            <cq:include script="item.jsp" />
            <% } %>
        </ul>
        <ul class="off-canvas-list">
            <li><a href="#" title="Join">Join</a></li>
            <li><a href="#" title="Volunteer">Volunteer</a></li>
            <li><a href="#" title="Invest in Girls">Invest in Girls</a></li>
            <li><a href="#" title="En Espanol">En Espa&#241;ol</a></li>
        </ul>
    </nav>