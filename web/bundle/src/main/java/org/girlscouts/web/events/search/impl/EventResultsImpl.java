package org.girlscouts.web.events.search.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.sling.api.SlingHttpServletRequest;
import org.girlscouts.web.events.search.EventResults;
import org.girlscouts.web.events.search.FacetsInfo;
import org.girlscouts.web.events.search.SearchResultsInfo;

import org.girlscouts.web.search.eval.WapperJcrPropertyPredicateEvaluator;

import com.day.cq.tagging.TagManager;
import com.day.cq.tagging.Tag;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.facets.Bucket;
import com.day.cq.search.facets.Facet;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.search.Query;

public class EventResultsImpl implements EventResults {

	private SearchResultsInfo sResults; 
	private HashMap<String,List<FacetsInfo>> facetsWithCounts = new HashMap<String, List<FacetsInfo>>();
	
	public SearchResultsInfo performContentSearch(Map<String, String> map, SlingHttpServletRequest slingRequest, QueryBuilder builder,String offset) throws RepositoryException{
		sResults = new SearchResultsInfo();
		Map<String,String> Sresults = new HashMap<String,String>();		
		PredicateGroup predicateGroup = PredicateGroup.create(map);
		Query query = builder.createQuery(predicateGroup,slingRequest.getResourceResolver().adaptTo(Session.class));
		
		// Adding offset for pagination.
		if(offset!=null){
			query.setStart(Long.parseLong(offset));
		}
		
		//query.setHitsPerPage(20);
		//System.out.println("Search Results  ------------");
		SearchResult searchResults=null;
		try{
		 searchResults = query.getResult();
		}catch(Exception e)
		{
			System.out.println("Exception caught"+e.getMessage());
		}
		//System.out.println("Search Results  ----" +searchResults.getHits().size());
		
		
		sResults.setSearchResults(searchResults);
			
		java.util.List<Hit> hits = searchResults.getHits();
		Map<String, Facet> facets = searchResults.getFacets();
		System.out.println("# of page displayed" +searchResults.getResultPages().size());
		//System.out.println("This is the facets[" +facets.toString() +"]" +"    TotalHitMatches [" +searchResults.getTotalMatches() +"]");
		
		sResults.setHitCounts(searchResults.getTotalMatches());
		
		
		for(String key:facets.keySet())
		{
			System.out.println("Key " +key);
			Facet fat = facets.get(key);
			System.out.println("Key Name" +key +"Count" +fat.getBuckets().size());
			if(fat.getContainsHit() && key.equals("1_property"))
			{
				for(Bucket bucket: fat.getBuckets()){
					//String facetsAndTags = bucket.getValue().substring(bucket.getValue().indexOf(":")+1,bucket.getValue().length());
					TagManager tagMgr = slingRequest.getResourceResolver().adaptTo(TagManager.class);
					Tag tag = tagMgr.resolve(bucket.getValue());
					sResults.createFacetsWithTag(tag.getParent().getName(),tag.getTitle(), bucket.getCount());
					
				}
			}	
		}
		//sResults.setFacetsWithCount(facetsWithCounts);
		for(int i=0;i<hits.size();i++)
		{
			
			//System.out.println("Node Type" +hits.get(i).getNode().isNodeType("cq:Page"));
			Hit ht = hits.get(i);
			String path = ht.getNode().isNodeType("cq:Page")?ht.getPath()+".html":ht.getPath();
			Sresults.put(ht.getTitle(), path);
			
		}
		
		sResults.setResults(Sresults);
		return sResults;
			
			
			
		}
		
	
	
	

}
 