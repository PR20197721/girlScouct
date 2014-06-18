<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="org.girlscouts.vtk.ejb.Search"%>

<%@ page import="org.girlscouts.vtk.models.user.*,org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>

<%
org.girlscouts.vtk.dao.MeetingDAO db = sling.getService(org.girlscouts.vtk.dao.MeetingDAO.class);
	//Search db = new Search();

	String query = request.getParameter("q");

	List<String> countries = db.getData(query);	
	Iterator<String> iterator = countries.iterator();
	while(iterator.hasNext()) {
		String country = (String)iterator.next();
		out.println(country);
	}
%>