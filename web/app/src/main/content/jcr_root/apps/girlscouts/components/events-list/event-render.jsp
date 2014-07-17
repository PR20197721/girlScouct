
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%
   String href=(String)request.getAttribute("imgPath");
   String iconPath = (String)request.getAttribute("iconPath");
   String dateStr = (String)request.getAttribute("dateStr");
   String locationLabel = (String)request.getAttribute("locationLabel");
   String imgPath = (String)request.getAttribute("imgPath");
   String title = (String)request.getAttribute("title");
%>
 <li>
   <div class="row">
     <div class="small-24 medium-12 large-8 columns">
         <%
			if(!iconPath.isEmpty()){ %>
          		<%= displayRendition(resourceResolver, imgPath, "cq5dam.web.120.80") %>
             <%} else if(iconPath.isEmpty()) { %>
					<img src="/content/dam/all_icons/icons_64/calendar_64.png/jcr:content/renditions/cq5dam.web.120.80.png">
             <% } %>
     </div>
      <div class="small-24 medium-12 large-16 columns">
         <h3><a href="<%= href %>"><%= title %></a></h3>
         <p>Date: <%= dateStr %> </p>
         <p>Location: <%= locationLabel %></p>
      </div>
   </div>
</li>  







