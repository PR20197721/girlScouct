<%@include file="/libs/foundation/global.jsp" %>
<%
    final String DATA_KEY = "gsusa.header-nav.item.data";
    final String[] navs = properties.get("navs", String[].class);
    if (navs != null) {
%>
    <nav class="top-bar show-for-medium-up" data-topbar role="navigation">
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
            <a class="right-off-canvas-toggle menu-icon"><span></span></a>
        </section>
    </nav> <!-- END NAV.TAB-BAR HIDE-FOR-LARGE-UP -->

    <!-- OFF CANVAS MENU -->
    <aside class="right-off-canvas-menu">
        <ul class="off-canvas-list">
            <% for (int i = 0; i < navs.length; i++) {
                request.setAttribute(DATA_KEY, navs[i]);
            %>
            <cq:include script="item.jsp" />
            <% } %>
        </ul>
    </aside>