<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="net.viralpatel.autocomplete.DummyDB"%>
<%
	DummyDB db = new DummyDB();

	String query = request.getParameter("q");
	System.err.println(1);
	List<String> countries = db.getData(query);
	System.err.println(12);
	Iterator<String> iterator = countries.iterator();
	while(iterator.hasNext()) {
		String country = (String)iterator.next();
		out.println(country);
	}
%>