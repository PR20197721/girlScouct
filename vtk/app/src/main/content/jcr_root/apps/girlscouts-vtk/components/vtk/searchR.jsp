<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" import="org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*, org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>


<%

//HttpSession session = request.getSession();
java.util.List<org.girlscouts.vtk.models.Search> searchs = (java.util.List<org.girlscouts.vtk.models.Search>)session.getValue("search");
for(int i=0;i<searchs.size();i++){
	
	org.girlscouts.vtk.models.Search search = searchs.get(i);
	%>
	
		<div style="padding-top:10px;">
			<div><a style="color:green" href="<%=search.getPath() %>"><%=search.getPath() %></a></div>
			<div style="background-color:#efefef;"><%=search.getContent() %></div>
		</div>
	<%
	
}
%>