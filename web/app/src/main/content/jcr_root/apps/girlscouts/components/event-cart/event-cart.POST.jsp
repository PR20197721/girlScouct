<%@include file="/libs/foundation/global.jsp"%>
<%@ page import="java.util.List,
java.util.ArrayList,
com.day.cq.commons.TidyJSONWriter" %>

<%!
public class Tuple<X,Y>{
	public X x;
	public Y y;
	public X getX(){
		return x;
	}
	public Y getY(){
		return y;
	}
}
%>

<%
HttpSession session = request.getSession();
List<Tuple<String,String>> pathsAndNames = new ArrayList<Tuple<String,String>>();
final TidyJSONWriter writer = new TidyJSONWriter(response.getWriter());
String output = "Backend Create Successful";
String action = request.getParameter("action");
if(action.equals("create")){
	try{
		session.setAttribute("event-cart",pathsAndNames);
	} catch(Exception e){
		output = "Backend Create Failed";
	} finally{
		writer.setTidy("true".equals(request.getParameter("tidy")));
		writer.object();
		writer.key("output").value(output);
		writer.endObject();
	}
} else if(action.equals("update")){
	//TODO
	writer.setTidy("true".equals(request.getParameter("tidy")));
	writer.object();
	writer.key("output").value("Backend Update Successful");
	writer.endObject();
} else if(action.equals("add")){
	//TODO
	writer.setTidy("true".equals(request.getParameter("tidy")));
	writer.object();
	writer.key("output").value("Backend Add Successful");
	writer.endObject();
}
%>