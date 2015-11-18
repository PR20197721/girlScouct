<%@page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp" %>
<%
String[] cookies = properties.get("cookies", String[].class);
if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
    %><cq:includeClientLib categories="apps.gsusa.authoring" /><%
	if (cookies == null || cookies.length == 0) {
		%>Meet The Cookies: Double click here to edit.<%
	}
}

if (cookies != null && cookies.length != 0) {
    %><div id="meet-cookie-layout"><%
    for (String cookie : cookies) {
        String[] infos = cookie.split("\\|\\|\\|");
        String title = "", image = "", description = "", buttonTitle = "", buttonLink = "";
        try {
        	title = infos[0];
        	image = infos[1];
        	description = infos[2];
        	buttonTitle = infos[3];
        	buttonLink = infos[4];
        } catch (Exception e) {}
%>
        <div>
            <img src="<%= image %>" alt="" />
            <div class="wrapper">
                <h4><%= title %></h4>
                <section>
                    <p>
                        <%= description %>
                    </p>
                    <a href="<%= buttonLink %>" title="<%= buttonTitle %>" class="button white"><%= buttonTitle %></a>
                </section>
            </div>
        </div>
<%
    }
%>
    </div>
    <script>
    	$(document).ready(function(){
	        $('#meet-cookie-layout').layout({
	            itemPadding: 0
	        });
    	})
    </script>
<%
}
%>