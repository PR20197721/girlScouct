package org.girlscouts.web.events.search;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.girlscouts.web.events.search.impl.EventResultsImpl;
import org.girlscouts.web.events.search.impl.FacetBuilderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.QueryBuilder;

public class EventsSrch  
{
	private static Logger log = LoggerFactory.getLogger(EventsSrch.class);
	private SlingHttpServletRequest slingRequest;
	private QueryBuilder queryBuilder;
	private HashMap<String,List<FacetsInfo>> facetAndTags = new HashMap<String,List<FacetsInfo>>();
	
	// ReFractrating the code to accomdate Form & Documents
	private static String FACETS_PATH = "/etc/tags/girlscouts";
	
	
	private static String EVENTS_PROP="jcr:content/cq:tags";
	//private static String PATH_1 = "/content/girlscouts-usa/en/events/";
	private Map<String, ArrayList<String>> facetsQryBuilder = new HashMap<String, ArrayList<String>>();
	
	
 	private SearchResultsInfo searchResultsInfo;
 	int propertyCounter = 0;
 	LinkedHashMap<String, String> searchQuery = new LinkedHashMap<String, String>();
	
	// Which return the object of Facets as Well as the Results;
	
	public EventsSrch(SlingHttpServletRequest slingRequest,QueryBuilder builder){
		this.slingRequest = slingRequest;
		this.queryBuilder = builder;
	}
	
	public void search(String q,String[] tags,String offset, String month,String year, String startdtRange, String enddtRange, String region,String path )
	{
		try 
		{
			createFacets();
			eventResults(q,offset,month,year,startdtRange,enddtRange,region,tags,path);
			combineSearchTagsCounts();
		} catch (RepositoryException e)
		{
			log.error("Error Generated in the search() of EventSrch Class");
			e.printStackTrace();
		}
	}
	
	
	public HashMap<String,List<FacetsInfo>> getFacets()
	{
		return facetAndTags;
	}
	
	private void createFacets() throws RepositoryException{
		FacetBuilder facetBuilder = new FacetBuilderImpl();
		facetAndTags = facetBuilder.getFacets(slingRequest, queryBuilder, FACETS_PATH );
		}
	
	
	private void eventResults(String q,String offset,String month,String year, String startdtRange, String enddtRange,String region,String[] tags, String path) throws RepositoryException{
		EventResults eventResults = new EventResultsImpl();

		
		if(q!=null && !q.isEmpty()){
			log.info("Search Query Term [" +q +"]");
			searchQuery.put("fulltext",q);
		}

		searchQuery.put("type", "cq:Page");
		searchQuery.put("path",path);
		searchQuery.put("2_boolproperty","jcr:content/hideInNav");
		searchQuery.put("2_boolproperty.value","false");
		searchQuery.put(++propertyCounter+"_property",EVENTS_PROP);
		searchQuery.put("p.limit", "-1");
		searchQuery.put("orderby","@jcr:content/data/start");
		searchQuery.put("orderby.sort", "asc");

		
		log.debug("Query Parameter : " +q);
		
		if(tags!=null){
			addToDefaultQuery(searchQuery,tags);
			// All search query to the map 
		}
		
		if((year!=null && !year.isEmpty()) &&  (month!=null && !month.isEmpty())){
			addMonthYearToQuery(month,year,searchQuery);
		}
		
		if((startdtRange!=null  && !startdtRange.isEmpty()) && (enddtRange!=null && !enddtRange.isEmpty())){
			addDateRangesToQuery(startdtRange, enddtRange,searchQuery);
		}
		
		if(region!=null && !region.isEmpty()){
			addRegionToQuery(region);
		}
		
		//performContentSearch(searchQuery);
		log.debug("SearchQuery  ["   +searchQuery  +"]");
		searchResultsInfo = eventResults.performContentSearch(searchQuery,slingRequest,this.queryBuilder,offset);
		Iterator searchIterator = searchResultsInfo.getFacetsWithCount().keySet().iterator();
		while(searchIterator.hasNext())
		{
			String key = (String)searchIterator.next();
			Map<String,Long> Categoriestags = searchResultsInfo.getFacetsWithCount().get(key);
			Iterator search= Categoriestags.keySet().iterator();
			while(search.hasNext()){
				String key1 =(String) search.next();
				log.debug("tags key [ " +key1 +" ] Counts [" +Categoriestags.get(key1) +"]");
			}
		}
		
	}
	
	public SearchResultsInfo getSearchResultsInfo(){
		return this.searchResultsInfo;
	}
	
	public void addToDefaultQuery(Map<String, String> searchQuery,String[] tags){
		for(String s:tags)
		{
			String temp = s.replaceAll("%3A", ":").replaceAll("%2F", "/");
			// categories/badge
			String key = temp.substring(temp.indexOf(":")+1,temp.length());
			String category = key.substring(0,key.indexOf("/"));
			if(facetsQryBuilder.containsKey(category)){
				facetsQryBuilder.get(category).add(temp);
			}else{
				
				ArrayList<String> value = new ArrayList<String>();
				value.add(s);
				facetsQryBuilder.put(category, value);
			}
		}
		Object[] categories = facetsQryBuilder.keySet().toArray();
		
		for(int i=0;i<categories.length;i++){
			ArrayList<String> tagsPath = facetsQryBuilder.get(categories[i].toString());
			if(i>0)
			{
				
				searchQuery.put("gsproperty", "jcr:content/cq:tags");
				searchQuery.put("gsproperty.or", "true");
				int count=1;
				for(String tagPath:tagsPath ){
					searchQuery.put("gsproperty."+count+++"_value", tagPath);
				}
			}else{
				int count = 1;
				searchQuery.put(propertyCounter+"_property.or", "true");
				for(String tagPath:tagsPath){
					searchQuery.put(propertyCounter+"_property."+count+++"_value", tagPath);
				}
				
				
			}
			
		}
		
	}
	
	public void addRegionToQuery(String region){
		searchQuery.put(++propertyCounter+"_property", "jcr:content/data/region"); 
		searchQuery.put(propertyCounter+"_property.value", region);
		
	}
	
	public void addMonthYearToQuery(String month, String year,Map<String, String> searchQuery){
		String mthYr = month+" "+year;
		log.debug("Year and the Month Paramter ["  +mthYr  +"]");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.set(Integer.parseInt(year),Integer.parseInt(month),0);
		c.set(Calendar.DAY_OF_MONTH,c.getActualMinimum(Calendar.DAY_OF_MONTH));
		String lowerBound = formatter.format(c.getTime()).toString();
		log.debug("Lower Bound Value [" +lowerBound +"]");
		c.set(Calendar.DAY_OF_MONTH,c.getActualMaximum(Calendar.DAY_OF_MONTH));
		String upperBound=formatter.format(c.getTime()).toString();
		log.debug("Upper Bound Value [" +upperBound +"]");
		addDateRangeQuery(lowerBound,upperBound);
		
	}
	public void addDateRangeQuery(String lowerBound, String upperBound){
		searchQuery.put("daterange.property","jcr:content/data/start" );
		searchQuery.put("daterange.lowerBound",lowerBound);	
		searchQuery.put("daterange.upperBound",upperBound);
		
	}
	public void addDateRangesToQuery(String startdtRange, String enddtRange,Map<String, String> searchQuery){
		addDateRangeQuery(startdtRange,enddtRange);
		
	}
	
	public void combineSearchTagsCounts()
	{
		
		if(searchResultsInfo.getFacetsWithCount().isEmpty())
		{
			return;
		}
		
		Iterator <String> everyThingFacets = this.facetAndTags.keySet().iterator();
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
	}
	

}
