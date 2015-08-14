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
	public Tuple(X x, Y y){
		this.x = x;
		this.y = y;
	}
	public X getX(){
		return x;
	}
	public Y getY(){
		return y;
	}
	public void setX(X a){
		x = a;
	}
	public void setY(Y b){
		y = b;
	}
}

public void update(TidyJSONWriter writer, List<Tuple<String,String>> pathsAndNames, String successMsg, HttpSession session, HttpServletRequest request){
	writer.setTidy("true".equals(request.getParameter("tidy")));
	writer.object().key("data").array();
	pathsAndNames = (ArrayList<Tuple<String,String>>)session.getAttribute("event-cart");
	for(Tuple<String,String> t : pathsAndNames){
		writer.value(writer.object().key("href").value(t.getX()).key("name").value(t.getY()).endObject());
	}
	writer.endArray();
	writer.key("output").value(successMsg);
	writer.endObject();
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
	update(writer, pathsAndNames, "Backend Update Successful", session, request);
} else if(action.equals("add")){
	String path = request.getParameter("eventPath");
	if(path == null || path.equals("")){
		output = "Backend Add Failed. Invalid Path";
		writer.setTidy("true".equals(request.getParameter("tidy")));
		writer.object();
		writer.key("output").value(output);
		writer.endObject();
	} else{
		Node n = resourceResolver.resolve(path).adaptTo(Node.class);
		String name = n.getProperty("jcr:content/jcr:title").getString();
		Tuple<String,String> newEvent = new Tuple<String,String> (name, path);
		pathsAndNames.add(newEvent);
		update(writer, pathsAndNames, "Backend Add to Cart Successful", session, request);
	}
}
%>