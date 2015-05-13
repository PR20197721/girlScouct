<%@include file="/libs/foundation/global.jsp" %>
<%
    final String DATA_KEY = "gsusa.header-nav.item.data";
    final String[] navs = properties.get("navs", String[].class);
    if (navs != null) {
%>
        <nav class="top-bar" data-topbar role="navigation">
            <ul class="title-area">
               <li class="name">
                 <h1><a href="#">My Site</a></h1>
               </li>
                <!-- Remove the class "menu-icon" to get rid of menu icon. Take out "Menu" to just have icon alone -->
               <li class="toggle-topbar menu-icon"><a href="#"><span><!-- Menu --></span></a></li>
             </ul>
            <section class="top-bar-section">
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
            </section>
        </nav>
<%
    request.removeAttribute(DATA_KEY);
    }
%>