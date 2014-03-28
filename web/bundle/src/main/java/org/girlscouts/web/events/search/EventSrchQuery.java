package org.girlscouts.web.events.search;



import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class EventSrchQuery {
	static String PATH_1 = "/content/girlscouts-usa/en";
	
	static String EVENTS_COND ="true";

	public static LinkedHashMap<String, String>  defaultQueryMap(){
		LinkedHashMap<String, String> searchQuery = new LinkedHashMap<String, String>();
		/*Map<String, String> searchQuery = new HashMap<String,String>();
		searchQuery.put("path",PATH_1);
		searchQuery.put("type",EVENTS_TYPE);
		searchQuery.put("property",EVENTS_PROP);
		*/
		
		return searchQuery;
	}	
	
	
	
	
}
