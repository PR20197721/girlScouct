<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="org.girlscouts.vtk.ejb.Search"%>

<%@ page import="org.girlscouts.vtk.models.user.*,org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>

<%
HttpSession session = request.getSession();
org.girlscouts.vtk.dao.MeetingDAO db = sling.getService(org.girlscouts.vtk.dao.MeetingDAO.class);
	//Search db = new Search();

	String toRet="";
	String query = request.getParameter("q");
System.err.println("q=" + query);
	List<org.girlscouts.vtk.models.Search> countries = db.getData(query);	
	Iterator<org.girlscouts.vtk.models.Search> iterator = countries.iterator();
	while(iterator.hasNext()) {
		/*
		String country = (String)iterator.next();
		System.err.println(country);
		toRet += "{\"label\" : \""+  country +"\"},";
		*/
		
		
		org.girlscouts.vtk.models.Search search = (org.girlscouts.vtk.models.Search) iterator.next();
		System.err.println(search.getPath());
		//toRet += "{\"label\" : \""+  "" +"\", \"value\" : \""+  search.getContent() +"\"},";
		session.putValue("search", countries);
	}
%>



[<%=toRet %>{"label" : "alex"}]