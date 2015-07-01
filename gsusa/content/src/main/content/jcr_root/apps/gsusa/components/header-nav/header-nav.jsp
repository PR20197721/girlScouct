<%@page import="java.util.*,
                com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<%
    final String LABEL_KEY = "gsusa.header-nav.item.label";
    final String LINK_KEY = "gsusa.header-nav.item.link";
    final String[] navs = properties.get("navs", String[].class);
    List<String> labels = new ArrayList<String>();
    List<String> links = new ArrayList<String>();
    
    if (navs != null) {
	    for (int i = 0; i < navs.length; i++) {
		    String[] split = navs[i].split("\\|\\|\\|");
		    String label = split.length >= 1 ? split[0] : "";
		    String link = split.length >= 2 ? split[1] : "";
		    labels.add(label);
		    links.add(link);
	    }
%>
    <nav class="top-bar show-for-medium-up large-19 medium-23 columns small-24 large-push-5" data-topbar role="navigation">
        <section class="top-bar-section">
            <ul>
                <% for (int i = 0; i < navs.length; i++) {
                    request.setAttribute(LABEL_KEY, labels.get(i));
                    request.setAttribute(LINK_KEY, links.get(i));
                %>
                    <cq:include script="item.jsp" />
                <% } %>
            </ul>
        </section>
    </nav>

<!-- OFF CANVAS MENU BAR -->
    <nav class="tab-bar hide-for-medium-up">
        <section>
            <form action="/content/gsusa/en/site-search.html" method="get" class="search-form">
                <input type="search" name="q" placeholder="Search" />
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
                request.setAttribute(LABEL_KEY, labels.get(i));
                request.setAttribute(LINK_KEY, links.get(i));
            %>
            <cq:include script="item.jsp" />
            <% } %>
        </ul>
<%
        // This is a workaround to include the eyebrow nav without adding the editor bar.
        // It leaves the editing bar on the main area so authors can edit. 
        final String EYEBROW_PROPS_KEY = "gsusa.eyebrow.items.data";
        final String EYEBROW_PATH = "/content/gsusa/en/jcr:content/header/eyebrow-nav";
        ValueMap eyebrowProps = (ValueMap)resourceResolver.resolve(EYEBROW_PATH).adaptTo(ValueMap.class);
        request.setAttribute(EYEBROW_PROPS_KEY, eyebrowProps);
%>
        <ul class="off-canvas-list">
            <cq:include script="/apps/gsusa/components/eyebrow-nav/items.jsp" />
        </ul>
<%
        request.removeAttribute(EYEBROW_PROPS_KEY);
%>
    </nav>
<%
	    request.removeAttribute(LABEL_KEY);
	    request.removeAttribute(LINK_KEY);
    } else if (WCMMode.fromRequest(request) == WCMMode.EDIT){
        %>Double click here to edit header navigation.<%
    }
%>