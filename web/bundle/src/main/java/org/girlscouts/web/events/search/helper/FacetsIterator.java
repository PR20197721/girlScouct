package org.girlscouts.web.events.search.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FacetsIterator {
	
	Map<String, ArrayList<String>> facetsBuilder;
	public FacetsIterator(Map<String, ArrayList<String>> facetsBuilder){
		this.facetsBuilder = facetsBuilder;
		
	}
	
	
	
	public ArrayList<String> getList(String key){
		 if(facetsBuilder.containsKey(key)){
			 ArrayList<String> temp = facetsBuilder.get(key);
			 facetsBuilder.remove(key);
			 return temp;
		 }
	  
		return null;
	}

	public Object[] getKeys(){
		return facetsBuilder.keySet().toArray();
	}
	
}
