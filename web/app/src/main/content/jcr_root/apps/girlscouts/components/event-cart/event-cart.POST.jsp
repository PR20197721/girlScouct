<%@include file="/libs/foundation/global.jsp"%>
<%@ page import="java.util.Set,
java.util.HashSet,
com.day.cq.commons.TidyJSONWriter,
org.apache.sling.commons.json.JSONObject,
org.apache.sling.commons.json.JSONArray" %>

<%!
public class Event{
	private String x;
	private String y;
	public Event(String x, String y){
		this.x = x;
		this.y = y;
	}
	public String getX(){
		return x;
	}
	public String getY(){
		return y;
	}
	@Override
	public int hashCode() {
	    return x.hashCode() & y.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
	        return true;
	    if (obj == null)
	        return false;
	    if (getClass() != obj.getClass())
	        return false;
	    Event other = (Event) obj;
	    if (!x.equals(other.x))
	        return false;
	    return true;
	}     
}

public void update(TidyJSONWriter writer, Set<Event> pathsAndNames, String successMsg, HttpSession session, HttpServletRequest request) throws Exception{
	if(pathsAndNames != null){
		writer.object().key("data").array();
		pathsAndNames = (HashSet<Event>)session.getAttribute("event-cart");
		for(Event event : pathsAndNames){
			writer.value(writer.object().key("href").value(event.getX()).key("name").value(event.getY()).endObject());
		}
		writer.endArray();
		writer.key("output").value(successMsg);
		writer.endObject();
	} else{
		writer.object();
		writer.key("output").value("Event Cart Empty. Did not update");
		writer.endObject();
	}
}
%>

<%
HttpSession session = request.getSession();
Set<Event> pathsAndNames = (HashSet<Event>)session.getAttribute("event-cart");
final TidyJSONWriter writer = new TidyJSONWriter(response.getWriter());
writer.setTidy("true".equals(request.getParameter("tidy")));
String output = "Backend Create Successful";
String action = request.getParameter("action");
if(action.equals("update")){
	update(writer, pathsAndNames, "Backend Update Successful", session, request);
} else if(action.equals("add")){
	String path = request.getParameter("eventPath");
	if(path == null || path.equals("")){
		output = "Backend Add Failed. Invalid Path";
		writer.object();
		writer.key("output").value(output);
		writer.endObject();
	} else{
		Node n = resourceResolver.resolve(path).adaptTo(Node.class);
		String name = n.getProperty("jcr:content/jcr:title").getString();
		Event newEvent = new Event(path, name);
		if(pathsAndNames == null){
			pathsAndNames = new HashSet<Event>();
		}
		pathsAndNames.add(newEvent);
		session.setAttribute("event-cart",pathsAndNames);
		update(writer, pathsAndNames, "Backend Add to Cart Successful", session, request);
	}
}
%>