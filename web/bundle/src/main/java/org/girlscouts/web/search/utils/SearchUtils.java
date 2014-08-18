package org.girlscouts.web.search.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.sling.api.SlingHttpServletRequest;
import org.girlscouts.web.events.search.FacetsInfo;
import org.girlscouts.web.events.search.SearchResultsInfo;
import org.girlscouts.web.search.DocHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.facets.Bucket;
import com.day.cq.search.facets.Facet;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;

public class SearchUtils {
	private static Logger log = LoggerFactory.getLogger(SearchUtils.class);
	
	public static SearchResultsInfo combineSearchTagsCounts(SearchResultsInfo searchResultsInfo,Map<String,List<FacetsInfo>> facetAndTags)
	{
		if(searchResultsInfo.getFacetsWithCount().isEmpty())
		{
			return searchResultsInfo;
		}
		Iterator <String> everyThingFacets=null;
		try{
			everyThingFacets = facetAndTags.keySet().iterator();
		}catch(Exception e){
			log.error("Exception Caught" +e.getMessage());
		}
		Map<String, Map<String, Long>> facetsWithCounts = searchResultsInfo.getFacetsWithCount();
		while(everyThingFacets.hasNext())
		{
			String facetName = everyThingFacets.next();
			List <FacetsInfo> facetInfo = facetAndTags.get(facetName);
			log.debug("Facets Name ["+facetsWithCounts.toString() +"]");
			if(facetsWithCounts.containsKey(facetName))
			{
				Map<String, Long> fwc = facetsWithCounts.get(facetName);
				for(int i=0;i<facetInfo.size();i++)
				{
					if(fwc.containsKey(facetInfo.get(i).getFacetsTitle()))
					{
						facetInfo.get(i).setChecked(true);						
						facetInfo.get(i).setCount(fwc.get(facetInfo.get(i).getFacetsTitle()));
					}
				}
			}
			
		}
		return searchResultsInfo;	
	}
	public static List<Hit> performContentSearch(Map<String, String> map, SlingHttpServletRequest slingRequest, QueryBuilder builder,String offset,SearchResultsInfo sResults) throws RepositoryException
	{
		
		
		//SearchResultsInfo sResults; sResults = new SearchResultsInfo();
		List<String> Sresults = new ArrayList<String>();		
		PredicateGroup predicateGroup = PredicateGroup.create(map);
		Query query = builder.createQuery(predicateGroup,slingRequest.getResourceResolver().adaptTo(Session.class));
		query.setExcerpt(true);
		
		// Adding offset for pagination.
		if(offset!=null){
			query.setStart(Long.parseLong(offset));
		}
		SearchResult searchResults=null;
		try{
		 searchResults = query.getResult();
		}catch(Exception e)
		{
			log.error("Error Generated performContentSearch" +e.getStackTrace());
		}
		sResults.setSearchResults(searchResults);
			
		java.util.List<Hit> hits = searchResults.getHits();
		System.out.println("How manu search results" +hits.size());
		
		Map<String, Facet> facets = searchResults.getFacets();
		log.info("# of page displayed" +searchResults.getResultPages().size());
		log.debug("This is the facets  [" +facets.toString() +"]"  + " TotalHitMatches [" +searchResults.getTotalMatches() +"]");
		
		sResults.setHitCounts(searchResults.getTotalMatches());
		
		String facetsKey = "1_property";
		if(!facets.containsKey("1_property")){
			facetsKey="type";
		}
		for(String key:facets.keySet())
		{
			Facet fat = facets.get(key);
			log.debug("Key Name  [" +key +" ]   Count[" +fat.getBuckets().size() +"]");
			
			if(fat.getContainsHit() && key.equals(facetsKey))
			{
				for(Bucket bucket: fat.getBuckets()){
					TagManager tagMgr = slingRequest.getResourceResolver().adaptTo(TagManager.class);
					Tag tag = tagMgr.resolve(bucket.getValue());
                    if (tag != null){
                    		sResults.createFacetsWithTag(tag.getParent().getName(),tag.getTitle(), bucket.getCount());
                       } else {
                            log.error(">>>>> Unable to resolve tag " + bucket.getValue());
                      }
				}
			}	
		}
		
		return hits;
		
   }
	
	
}
