<%--
  blockquote component.
--%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %>
<%
	String text = properties.get("text", "Please enter your quote.");
	String style = properties.get("style", "bg-color fuchsia");
	boolean isQuote = properties.get("isQuote", false);
	boolean hasQuotee = properties.get("hasQuotee", false);
	String quotee = properties.get("quotee", "");
	boolean icons = properties.get("icons",false);
%>
<% if(isQuote){ %>
<blockquote class="quotes <%= style %>">
<% }else{ %>
<blockquote class="<%= style %>">
<% } %>
    <%= text %>
    <% if(isQuote && hasQuotee && !quotee.equals("")){ %>
    <p class="quotee">&#8212; <%= quotee %></p>
    <% } else { %>
    <% } if(icons) { %>
        <section class="clearfix">
            <ul class="social-icons inline-list">
                <li><a href="https://www.facebook.com/GirlScoutsUSA" class="icon-social-facebook"></a></li>
                <li><a href="https://twitter.com/girlscouts" class="icon-social-twitter-tweet-bird"></a></li>
                <li><a href="http://instagram.com/girlscouts" class="icon-social-instagram"></a></li>
            </ul>
        </section>
    <% } else { %>
    	<section>
	    <ul></ul>
	    </section>
    <% } %>
</blockquote>