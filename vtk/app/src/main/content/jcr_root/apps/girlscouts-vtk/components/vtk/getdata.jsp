<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig,  org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<%
	String toRet="";
	String query = request.getParameter("q");

	
	
	List<org.girlscouts.vtk.models.Search> countries = yearPlanUtil.getData(user,troop, query);
	if( countries!=null && countries.size()<=0  ){
		session.setAttribute("search", countries); 
		return;
	}
	
	Iterator<org.girlscouts.vtk.models.Search> iterator = countries.iterator();
	while(iterator.hasNext()) {
		org.girlscouts.vtk.models.Search search = (org.girlscouts.vtk.models.Search) iterator.next();
		session.setAttribute("search", countries);
	}
%>



[<%=toRet %>{"label" : "alex"}]
