
<% 
/*if(user.isAdmin() && (apiConfig.getTroops() == null
            || apiConfig.getTroops().size() <= 0
            || (apiConfig.getTroops().get(0).getType() == 1))) {*/
	if(false){%>
            
                <%@include file="admin_reports.jsp" %>
            <% }else{ %> 

                <%@include file="plan.jsp" %>
            <% } %>
