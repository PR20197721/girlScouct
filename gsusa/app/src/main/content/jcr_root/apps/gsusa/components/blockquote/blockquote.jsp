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
	String textAlignment = properties.get("textalignment", "");
	String textSize = properties.get("textsize", "");
	String lineHeight = properties.get("lineheight", "");
	String textColor = properties.get("textcolor", "");
	String backgroundColor = properties.get("backgroundcolor", "");
	String borderColor = properties.get("bordercolor", "");
%>
<blockquote class="<% if(isQuote){ %>quotes <%}%><%= style %>" style="

    <% if (backgroundColor.length()>0) { %>
    	background: #<%=backgroundColor%>;
    <% } %>

    <% if (borderColor.length()>0) { %>
    	border-color: #<%=borderColor%>;
    <% } %>

">
    <p style="

	<% if(isQuote) { %>
		padding-top: 4px;

		<% if(hasQuotee) { %>
			padding-bottom: 8px; 
	    <% } %>
    <% } %>
    
    <% if (textAlignment.length()>0) { %>
    	text-align: <%=textAlignment%>;
    <% } %>

    <% if (textSize.length()>0) { %>
    	font-size: <%=textSize%>;
    <% } %>
    
    <% if (lineHeight.length()>0) { %>
    	line-height: <%=lineHeight%>;
    <% } %>

    <% if (textColor.length()>0) { %>
    	color: #<%=textColor%>;
    <% } %>

    "><%= text %></p>
    <% if(isQuote && hasQuotee && !quotee.equals("")){ %>
    	<p class="quotee" style="
    	
	    <% if (textSize.length()>0) { %>
	    	font-size: <%=textSize%>;
	    <% } %>
    
	    <% if (lineHeight.length()>0) { %>
    		line-height: <%=lineHeight%>;
	    <% } %>
	
    	<% if (textColor.length()>0) { %>
	    	color: #<%=textColor%>;
	    <% } %>
    	
    	">&#8212; <%= quotee %></p>
    <% } %>
</blockquote>