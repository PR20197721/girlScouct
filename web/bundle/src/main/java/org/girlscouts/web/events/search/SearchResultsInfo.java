package org.girlscouts.web.events.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.day.cq.search.result.SearchResult;

public class SearchResultsInfo {
	private Map<String, String> results;
	private Map<String,Map<String,Long>> facetsWithCount;
	private Map<String,ArrayList<String>> facts;
	private long hitCounts ;
	private SearchResult searchResults;
	
	public SearchResultsInfo(){
		results = new HashMap<String,String>();
		facetsWithCount = new HashMap<String,Map<String,Long>>();
		facts = new HashMap<String,ArrayList<String>>();
		
		
	}
	public void setResults(Map<String,String>results){
		this.results = results;
	}

	public Map<String,String> getResults(){
		return results;
	}
	
	public void setHitCounts(long l){
		this.hitCounts = l;
		
	}
	
	public long getHitCounts(){
		return hitCounts;
	}
	
	public void setFacetsWithCounts(Map<String,Map<String,Long>> facets){
		this.facetsWithCount = facets;
	}
	
	public void createFacetsWithTag(String key, String value, Long count){
		if(facetsWithCount.containsKey(key)){
			 facetsWithCount.get(key).put(value, count);
		}else{
			facetsWithCount.put(key,new HashMap<String,Long>());
			 facetsWithCount.get(key).put(value, count);
		}
	}
	
	public Map<String, ArrayList<String>> getFacts(){
		return this.facts;
	}
	
	public Map<String,Map<String,Long>> getFacetsWithCount(){
		return facetsWithCount;
	}
	
	public void setSearchResults(SearchResult searchResults){
		this.searchResults = searchResults;
	}
	
	public SearchResult getSearchResults(){
		return searchResults;
	}
	
	
	

}
