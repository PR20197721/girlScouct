<%@include file="/libs/foundation/global.jsp"%>
<%@ page import="java.util.Set,
java.util.Comparator,
java.util.TreeSet,
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
		pathsAndNames = (TreeSet<Event>)session.getAttribute("event-cart");
		for(Event event : pathsAndNames){
			JSONObject jo = new JSONObject();
			jo.put("href",event.getX());
			jo.put("name",event.getY());
			writer.value(jo);
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

class EventComparator implements Comparator<Event>{

	public int compare(Event e1, Event e2){
		return e1.getX().compareTo(e2.getX());
	}
}
%>

<%
HttpSession session = request.getSession();
Set<Event> pathsAndNames = (TreeSet<Event>)session.getAttribute("event-cart");
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
			pathsAndNames = new TreeSet<Event>(new EventComparator());
		}
		pathsAndNames.add(newEvent);
		session.setAttribute("event-cart",pathsAndNames);
		update(writer, pathsAndNames, "Backend Add to Cart Successful", session, request);
	}
} else if(action.equals("delete")){
	String href = request.getParameter("href");
	output = "Event Deletion Error - Event " + href + " not Found";
	for(Event e : pathsAndNames){
		if(e.getX().equals(href)){
			pathsAndNames.remove(e);
			session.setAttribute("event-cart",pathsAndNames);
			output = "Event Succesfully Removed from Cart";
			break;
		}
	}
	update(writer, pathsAndNames, output, session, request);
}
%>