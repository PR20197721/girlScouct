
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%
   String href=(String)request.getAttribute("href");
   String iconPath = (String)request.getAttribute("iconPath");
   String dateStr = (String)request.getAttribute("dateStr");
   String locationLabel = (String)request.getAttribute("locationLabel");
   String imgPath = (String)request.getAttribute("imgPath");
   String title = (String)request.getAttribute("title");
%>
 <li>
   <div class="row">
     <div class="small-8 medium-8 large-8 columns events-image">
         <%
			if(!iconPath.isEmpty()){ %>
          		<%= displayRendition(resourceResolver, imgPath, "cq5dam.web.120.80") %>
             <%} else if(iconPath.isEmpty()) { %>
					<img src="/content/dam/all_icons/icons_64/events_icon.jpg" alt="events icon"/>
             <% } %>
     </div>
      <div class="small-16 medium-16 large-16 columns events-data">
         <h3><a href="<%= href %>"><%= title %></a></h3>
         <p><b>Date: <%= dateStr %></b> </p>
         <p><b>Location: <%= locationLabel %></b></p>
      </div>
   </div>
</li>  







