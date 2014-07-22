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
<li>
<div class="row">
	<div class="small-8 medium-4 large-4 columns news-image">
		<% if(!imgPath.isEmpty()){ %>
        				<%= displayRendition(resourceResolver, imgPath, "cq5dam.web.120.80") %>
                  <%} else if(imgPath.isEmpty()) { %>
                      <img src="/content/dam/all_icons/icons_64/globe_64.png">
          <% } %>
	
	</div>
   	<div class="small-16 medium-20 large-20 columns news-data">
   	   <h3>
   	   	 <%if(!external_url.isEmpty()){ %>
   	   	 	<a href="<%= external_url %>" target="_blank"><%= newsTitle %></a>
   	   	 <%}else{%>
   	   		 <a href="<%= newsLink %>"><%= newsTitle %></a>
   	   	 <%} %>
   	   </h3>
   	  	<p><%= newsDateStr %></p>
  			<p><%=newsDesc%></p>
   	</div>
</div>
</li>
	  
