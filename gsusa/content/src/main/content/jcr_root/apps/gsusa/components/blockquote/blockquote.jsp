<%--

  blockquote component.

  

--%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %>
<%
	String text = properties.get("text", "Please enter your quote.");
	String style = properties.get("style", "bg-color fuchsia");
%>

<blockquote class="<%= style %>">
    <p class="white"><%= text %></p>
    <ul class="social-icons inline-list white">
        <li><a href="https://www.facebook.com/gsgcfl" class="icon-social-facebook"></a></li>
        <li><a href="https://twitter.com/gsgc" class="icon-social-twitter-tweet-bird"></a></li>
        <li><a href="https://www.flickr.com/photos/gsgc/" class="icon-social-instagram"></a></li>
    </ul>
</blockquote>