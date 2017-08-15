<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%
	final String EYEBROW_PROPS_KEY = "gsusa.eyebrow.items.data";
	ValueMap extProperties = (ValueMap)request.getAttribute(EYEBROW_PROPS_KEY);
	if (extProperties != null) {
		properties = extProperties;
	}
	final String[] navs = properties.get("navs", String[].class);
    final Boolean[] hideInStickyNav = {false, true, true}; // PLACEHOLDER ------------------------
	
	if (navs != null) {
		for (int i = 0; i < navs.length; i++) {
			String[] split = navs[i].split("\\|\\|\\|");
			String label = split.length >= 1 ? split[0] : "";
			String link = split.length >= 2 ? split[1] : "";
			String target = "";
			boolean openInNewWindow = split.length >= 3 ? Boolean.parseBoolean(split[2]) : false;
			String cssClass = split.length >= 4 ? split[3] : "";
            String stickyClass = (hideInStickyNav != null && hideInStickyNav.length == navs.length && hideInStickyNav[i]) ? "hide-in-sticky-nav" : "";
			Integer eyebrowTabIndex = 10 + i;
			String cssId = "eyebrow";

			Page linkPage = resourceResolver.resolve(link).adaptTo(Page.class);
			if (linkPage != null && !link.contains(".html")) {
				link += ".html";
			}
			if (openInNewWindow) {
				target = "target=\"_blank\"";
			}
			%><li class="<%=stickyClass%>">
                <a <%= target %>id="tag_eyebrow_<%= linkifyString(label, 25)%>" class="<%= cssClass %>" href="<%= link %>" title="<%= label %>" tabindex="<%= eyebrowTabIndex++ %>"><%= label %></a>
            </li><%
		}
	}
%>
