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
<div class="row news-rows">
		<% if(!imgPath.isEmpty()){ %>
    <div class="column medium-3 large-3 small-22 lists-image">
        <%= displayRendition(resourceResolver, imgPath, "cq5dam.web.120.80") %>
    </div>
      <%} //else if(imgPath.isEmpty()) { %>
        <!--img src="/content/dam/all_icons/icons_64/news_icon.jpg" alt="news icon"/-->
      <% //} %>
 	<div class="column small-24 medium-21 large-21 list-text">
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
	  
