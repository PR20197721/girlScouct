<%@include file="/libs/foundation/global.jsp"%>
<%@ page import="java.util.List,
java.util.ArrayList,
com.day.cq.commons.TidyJSONWriter,
org.apache.sling.commons.json.JSONObject,
org.apache.sling.commons.json.JSONArray" %>

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
	writer.setTidy("true".equals(request.getParameter("tidy")));
	writer.object().key("data").array();
	pathsAndNames = (ArrayList<Tuple<String,String>>)session.getAttribute("event-cart");
	for(Tuple<String,String> t : pathsAndNames){
		writer.value(writer.object().key("href").value(t.getX()).key("name").value(t.getY()).endObject());
	}
	writer.endArray();
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