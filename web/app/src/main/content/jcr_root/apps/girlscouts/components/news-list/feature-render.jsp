<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>

<%
String imgPath = (String)request.getAttribute("imgPath");
String newsLink = (String)request.getAttribute("newsLink");
String newsTitle = (String)request.getAttribute("newsTitle");
String newsDateStr = (String)request.getAttribute("newsDateStr");
String newsDesc = (String)request.getAttribute("newsDesc");
String external_url=(String)request.getAttribute("external_url");

%>
<div class="row collapse news-rows">
	<div class="column medium-3 large-3 small-22 lists-image">
		<% if(!imgPath.isEmpty()){ %>
        <%= displayRendition(resourceResolver, imgPath, "cq5dam.web.120.80") %>
      <%} else if(imgPath.isEmpty()) { %>
        <img src="/content/dam/all_icons/icons_64/news_icon.jpg" alt="news icon"/>
      <% } %>
	</div>
 	<div class="column small-22 medium-14 large-14 large-pull-6 medium-pull-6 small-pull-2">
 	   <p>
 	   	 <%if(!external_url.isEmpty()){ %>
 	   	 	 <a href="<%= external_url %>" target="_blank"><%= newsTitle %></a>
 	   	 <%}else{%>
 	   		 <a href="<%= newsLink %>"><%= newsTitle %></a>
 	   	 <%} %>
 	   </p>
		  <p><%= newsDateStr %></p>
      <% if(!newsDesc.isEmpty()) { %>
			<p><%=newsDesc%></p>
      <%}%>
 	</div>
</div>
	  
