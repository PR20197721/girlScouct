<%@include file="/libs/foundation/global.jsp" %>
<!-- header-nav item -->
<%
    final String DATA_KEY = "gsusa.header-nav.item.data";
    String data = (String)request.getAttribute(DATA_KEY);
    if (data != null) {
%>
    <li class="has-submenu">
        <a href="" title="<%= data %>"> <%= data %></a>
        <ul class="right-submenu">
            <li class="back"><a href="#">Back</a></li>
            <li><a href="#">Link 1</a></li>
            <li><a href="#">Link 2</a></li>
            <li><a href="#">Link 1</a></li>
            <li><a href="#">Link 2</a></li>
            <li><a href="#">Link 1</a></li>
            <li><a href="#">Link 2</a></li>
      </ul>
    </li>
<%
    }
%>
<!--/of header-nav item -->