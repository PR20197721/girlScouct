package org.girlscouts.web.events.search;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.girlscouts.web.events.search.impl.FacetBuilderImpl;
import org.girlscouts.web.search.utils.SearchUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;

public class EventsSrch  
{
	private static Logger log = LoggerFactory.getLogger(EventsSrch.class);
	private SlingHttpServletRequest slingRequest;
	private QueryBuilder queryBuilder;
	private Map<String,List<FacetsInfo>> facetAndTags = new HashMap<String,List<FacetsInfo>>();
	
	// ReFractrating the code to accomdate Form & Documents
	private static String FACETS_PATH = "/etc/tags/girlscouts";
	
	private final String COUNCIL_SPE_PATH = "/etc/tags/";
	private static String EVENTS_PROP="jcr:content/cq:tags";
	private Map<String, ArrayList<String>> facetsQryBuilder = new HashMap<String, ArrayList<String>>();
	
 	private SearchResultsInfo searchResultsInfo;
 	int propertyCounter = 1;
 	LinkedHashMap<String, String> searchQuery = new LinkedHashMap<String, String>();
	
	// Which return the object of Facets as Well as the Results;
	
	public EventsSrch(SlingHttpServletRequest slingRequest,QueryBuilder builder){
		this.slingRequest = slingRequest;
		this.queryBuilder = builder;
	}
	
	public void search(String q,String[] tags,String offset, String month,String year, String startdtRange, String enddtRange, String region,String path,String facetsPath )
	{
		try 
		{
			
			createFacets(facetsPath);
			eventResults(q,offset,month,year,startdtRange,enddtRange,region,tags,path);
			searchResultsInfo = SearchUtils.combineSearchTagsCounts(searchResultsInfo,facetAndTags);
			
		} catch (RepositoryException e)
		{
			log.error("Error Generated in the search() of EventSrch Class");
			e.printStackTrace();
		}
	}
	
	
	public Map<String,List<FacetsInfo>> getFacets()
	{
		return facetAndTags;
	}
	
	private void createFacets(String facetPath) throws RepositoryException{
		FacetBuilder facetBuilder = new FacetBuilderImpl();
		if(facetPath!=null && !facetPath.isEmpty()){
			facetAndTags = facetBuilder.getFacets(slingRequest, queryBuilder, COUNCIL_SPE_PATH+facetPath );
		}
		if(facetAndTags==null){
			facetAndTags = facetBuilder.getFacets(slingRequest, queryBuilder, FACETS_PATH );
		}
	}
	
	
	private void eventResults(String q,String offset,String month,String year, String startdtRange, String enddtRange,String region,String[] tags, String path) throws RepositoryException{

		List<String> relts = new ArrayList<String>(); 
		this.searchResultsInfo = new SearchResultsInfo();
		
		// Generate Regions
		searchResultsInfo.setRegion(eventGeneration(path));

		if(q!=null && !q.isEmpty()){
			log.info("Search Query Term [" +q +"]");
			searchQuery.put("fulltext",q);
		}

		searchQuery.put("type", "cq:Page");
		searchQuery.put("path",path);
		searchQuery.put("1_property",EVENTS_PROP);
		searchQuery.put(++propertyCounter+"_boolproperty","jcr:content/hideInNav");
		searchQuery.put(propertyCounter+"_boolproperty.value","false");
		searchQuery.put("p.limit", "-1");
		searchQuery.put("orderby","@jcr:content/data/start");
		searchQuery.put("orderby.sort", "asc");
		
		
		log.debug("Query Parameter : " +q);
		
		if(tags!=null){
			try{
				addToDefaultQuery(searchQuery,tags);
			}catch(Exception e){
				log.error("Tagging could be parsed correctly");
			}
				// All search query to the map 
		}
		
		if((startdtRange!=null  && !startdtRange.isEmpty()) && (enddtRange!=null && !enddtRange.isEmpty())){
			addDateRangesToQuery(startdtRange, enddtRange,searchQuery);
		}
		
		if(region!=null && !region.isEmpty() && !region.equals("choose")){
			addRegionToQuery(region);
		}
		
		List<Hit> hits = SearchUtils.performContentSearch(searchQuery,slingRequest,this.queryBuilder,offset,searchResultsInfo);
		for(int i=0;i<hits.size();i++){
			Hit ht = hits.get(i);
			String pth = ht.getNode().isNodeType("cq:Page")?ht.getPath():ht.getPath();
			relts.add(pth);
			}
				
		searchResultsInfo.setResults(relts);
		
	}
	
	public SearchResultsInfo getSearchResultsInfo(){
		return this.searchResultsInfo;
	}
	
	private void addToDefaultQuery(Map<String, String> searchQuery,String[] tags) throws UnsupportedEncodingException{
		for(String s:tags) {
			String temp = URLDecoder.decode(s,"UTF-8");
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
					searchQuery.put("1_property.or", "true");
					for(String tagPath:tagsPath){
						searchQuery.put("1_property."+count+++"_value", tagPath);
					}
				}
		}
		
	}
	
	private void addRegionToQuery(String region){
		searchQuery.put(++propertyCounter+"_property", "jcr:content/data/region"); 
		searchQuery.put(propertyCounter+"_property.value", region);
		
	}
	
	private void addDateRangeQuery(String lowerBound, String upperBound){
		searchQuery.put("daterange.property","jcr:content/data/start" );
		if(!lowerBound.isEmpty()){
			searchQuery.put("daterange.lowerBound",lowerBound);
		}
		if(!upperBound.isEmpty()){	
			searchQuery.put("daterange.upperBound",upperBound);
		}
		
	}
	private void addDateRangesToQuery(String startdtRange, String enddtRange,Map<String, String> searchQuery){
		log.debug("startdtRange" +startdtRange  +"enddtRange" +enddtRange);
		DateFormat parse = new SimpleDateFormat("MM/dd/yyyy");
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date startRange = null;
		Date endRange =null;
		String strRange="";
		String edRange="";
		try {
			if(!startdtRange.isEmpty()){
				String startDtDecoder = URLDecoder.decode(startdtRange,"UTF-8");
				startRange = (Date)parse.parse(startDtDecoder);
				strRange = formatter.format(startRange);
			}
			if(!enddtRange.isEmpty()){
				String endDtDecoder= URLDecoder.decode(enddtRange,"UTF-8");
				endRange = (Date)parse.parse(endDtDecoder);
				edRange = formatter.format(endRange);
			}
		}catch (Exception e) {
			log.error("Error ::::::::::::::[" +e.getMessage() +"]");
			
		}
		addDateRangeQuery(strRange,edRange);
		
	}
	
	private Set<String> eventGeneration(String path){
		
		LinkedHashMap<String, String> region = new LinkedHashMap<String, String>();
		Date today = new Date();
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(today);
		
		//Set today system date
		Date startDt = cal1.getTime();
		Calendar cal2 = Calendar.getInstance();
		cal2.add(Calendar.DAY_OF_MONTH,+365);
		
		//Adding 365 days to the todays date
		Date after365days = cal2.getTime();
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		region.put("type", "cq:Page");
		region.put("path",path);

		//Adding date as we always display 1 year events: So the region should be populated accordingly
		region.put("daterange.property","jcr:content/data/start" );
		region.put("daterange.lowerBound",formatter.format(startDt));
		region.put("daterange.upperBound",formatter.format(after365days));
		
		region.put("1_property","@jcr:content/data/region");
		region.put(++propertyCounter+"_boolproperty","jcr:content/hideInNav");
		region.put(propertyCounter+"_boolproperty.value","false");
		region.put("p.limit", "-1");
		region.put("orderby","@jcr:content/data/region");
		region.put("orderby.sort", "asc");
		Set<String> regions = new HashSet<String>();
		//Set<String> vtkRegion = new HashSet<String>();
		long startTime = System.nanoTime();
		try {
			List<Hit> hits = SearchUtils.performContentSearch(region,slingRequest,this.queryBuilder,"0",searchResultsInfo);
			for(Hit hts : hits){
				Node node = hts.getNode();
				if(node.hasNode("jcr:content/data")){
					try{
						Node propNode = node.getNode("jcr:content/data");
						if(propNode.hasProperty("region")){
							regions.add(propNode.getProperty("region").getString());
						}
					}catch(Exception e){
						log.error("Event Node doesn't contains jcr:content/data Node" +e.getMessage());
					}
				}
			}
			
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		log.debug("Time Take in Milliseconds #######################" +(endTime - startTime)/1000000);
		
		return regions;
		
	}
	

}
