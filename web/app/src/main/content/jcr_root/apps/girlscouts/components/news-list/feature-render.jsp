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
		
  <div class="column medium-3 large-3 small-8 lists-image">
  <% if(!imgPath.isEmpty()){ %>
    <%= displayRendition(resourceResolver, imgPath, "cq5dam.web.120.80") %>
  <%} else { %>
    <img src="/content/dam/girlscouts-shared/images/Icons/jolly-icons-64/news_icon.jpg" alt="news icon"/>
  <% } %>
  </div>
 	<div class="column large-20 medium-20 small-15 list-text">
 	   <p>
 	   	<a href="<%=!external_url.isEmpty() ? external_url : newsLink%>" <%=!external_url.isEmpty() ?  "target='_blank'": " "%> title="<%= newsTitle %>"><%= newsTitle %></a>
 	   </p>
		  <p><%= newsDateStr %></p>
      <% if(!newsDesc.isEmpty()) { %>
			<p><%=newsDesc%></p>
      <%}%>
 	</div>
</div>
	  
