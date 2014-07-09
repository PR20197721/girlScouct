<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="org.girlscouts.vtk.ejb.Search"%>

<%@ page import="org.girlscouts.vtk.models.user.*,org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>

<%
	HttpSession session = request.getSession();
	org.girlscouts.vtk.dao.MeetingDAO db = sling.getService(org.girlscouts.vtk.dao.MeetingDAO.class);

	String toRet="";
	String query = request.getParameter("q");

	List<org.girlscouts.vtk.models.Search> countries = db.getData(query);
	
	
	if( countries.size()<=0  ){
		session.putValue("search", countries); 

		return;
		}
	
	Iterator<org.girlscouts.vtk.models.Search> iterator = countries.iterator();
	while(iterator.hasNext()) {
		org.girlscouts.vtk.models.Search search = (org.girlscouts.vtk.models.Search) iterator.next();
		session.putValue("search", countries);
	}
%>



[<%=toRet %>{"label" : "alex"}]