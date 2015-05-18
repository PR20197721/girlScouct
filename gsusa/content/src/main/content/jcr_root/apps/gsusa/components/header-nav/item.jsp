<%@include file="/libs/foundation/global.jsp" %>
<!-- header-nav item -->
<%
    final String DATA_KEY = "gsusa.header-nav.item.data";
    String data = (String)request.getAttribute(DATA_KEY);
    if (data != null) {
%>
        <li class="header-nav-item">
          <a href="" title="<%= data %>"> <%= data %></a>
        </li>
<%
    }
%>
<!--/of header-nav item -->