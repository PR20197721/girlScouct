<%@include file="/libs/foundation/global.jsp" %>
<%
    final String DATA_KEY = "gsusa.header-nav.item.data";
    final String[] navs = properties.get("navs", String[].class);
    if (navs != null) {
%>
        <ul class="header-nav">
<%
        for (int i = 0; i < navs.length; i++) {
            request.setAttribute(DATA_KEY, navs[i]);
%>
            <cq:include script="item.jsp" />
<%
        }
%>
        </ul>
<%
        request.removeAttribute(DATA_KEY);
    }
%>