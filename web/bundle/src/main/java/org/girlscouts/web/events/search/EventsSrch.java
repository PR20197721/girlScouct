package org.girlscouts.web.events.search;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.sling.api.SlingHttpServletRequest;
import org.girlscouts.web.events.search.impl.FacetBuilderImpl;
import org.girlscouts.web.search.utils.SearchUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;

public class EventsSrch  
{
	private static Logger log = LoggerFactory.getLogger(EventsSrch.class);
	private SlingHttpServletRequest slingRequest;
	private QueryBuilder queryBuilder;
	private Map<String,List<FacetsInfo>> facetAndTags = new HashMap<String,List<FacetsInfo>>();
	
	private static String FACETS_PATH = "/etc/tags/girlscouts";
	
	private final String COUNCIL_SPE_PATH = "/etc/tags/";
	private static String EVENTS_PROP="jcr:content/cq:tags";
	
	
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
		try {
System.err.println("tata evnt search 1 :"+ new java.util.Date() );			
			createFacets(facetsPath);
System.err.println("tata evnt search 2 :"+ new java.util.Date() );
			eventResults(q,offset,month,year,startdtRange,enddtRange,region,tags,path);
System.err.println("tata evnt search 3 :"+ new java.util.Date() );
			// Don't delete this code if in future client need count for the tagging just enable this functionality.
			// searchResultsInfo = SearchUtils.combineSearchTagsCounts(searchResultsInfo,facetAndTags);
			
		} catch (RepositoryException e) {
			log.error("Error Generated in the search() of EventSrch Class");
			e.printStackTrace();
		}
	}
	
	public Map<String,List<FacetsInfo>> getFacets() {
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
Session session= slingRequest.getResourceResolver().adaptTo(Session.class);
System.err.println("tata evnt search 6 :"+ new java.util.Date() );
		this.searchResultsInfo = new SearchResultsInfo();
		
		// Generate Regions
		searchResultsInfo.setRegion(eventRegions(path));

		if(q!=null && !q.isEmpty()){
			log.info("Search Query Term [" +q +"]");
			searchQuery.put("fulltext",q);
			searchQuery.put("p.limit", "2000");
			searchQuery.put("type", "cq:Page");
			searchQuery.put("p.offset", "0");
			
		}else{
			searchQuery.put("p.limit", "200");
			searchQuery.put("p.offset", "0");
			//searchQuery.put("type", "cq:Page");
			if( tags ==null ||  tags.length <= 0 ){
				searchQuery.put("2_property", "@jcr:content/data/start");
				searchQuery.put("2_property.operation", "exists");
			}
			
		}
System.err.println("tata ** evnt search 7 :"+ new java.util.Date() );
			
		
		searchQuery.put("path",path);
System.err.println("tata eventSearch: "+ path +" : "+q);		

//--searchQuery.put("p.limit", "-1");

//searchQuery.put("orderby","@jcr:content/data/start");
searchQuery.put("orderby","@start");
searchQuery.put("orderby.sort", "asc");
		log.debug("Query Parameter : " +q);
System.err.println("tata evnt search 8 :"+ new java.util.Date() );
		if(tags.length > 0) {
			try{
				searchQuery.put("1_property",EVENTS_PROP);
				addToDefaultQuery(searchQuery,tags);
			}catch(Exception e){
				log.error("Tagging could be parsed correctly");
			}
		}
System.err.println("tata evnt search 9 :"+ new java.util.Date() );
		if((startdtRange!=null  && !startdtRange.isEmpty()) && (enddtRange!=null && !enddtRange.isEmpty())){
			addDateRangesToQuery(startdtRange, enddtRange,searchQuery);
		}
		
		if(region!=null && !region.isEmpty() && !region.equals("choose")){
			addRegionToQuery(region);
		}
System.err.println("tata evnt search 10 :"+ new java.util.Date() );
		List<String> relts = new ArrayList<String>(); 
		
		
		//List<Hit>hits = SearchUtils.performContentSearch(searchQuery,slingRequest,this.queryBuilder);
		
		PredicateGroup predicateGroup = PredicateGroup.create(searchQuery);
		Query query =this.queryBuilder.createQuery(predicateGroup, session);
		query.setExcerpt(true);
System.err.println("tata "+predicateGroup.toString());
		SearchResult searchResults=query.getResult();
		java.util.List<Hit> hits = searchResults.getHits();
		
		
		if(hits!=null && !hits.isEmpty()){
System.err.println("tata size: "+ hits.size() );			
			for(int i=0;i<hits.size();i++){
				
				Hit ht = hits.get(i);
				String pth = ht.getNode().isNodeType("cq:Page") ? ht.getPath() : ht.getPath();
System.err.println("*** tata*** "+ pth);				
if( pth.trim().endsWith("/jcr:content/data") )
	pth= pth.replace("/jcr:content/data", "");
System.err.println("tata -- "+ pth);	
				relts.add(pth);
			}
		}		
System.err.println("tata evnt search 11 :"+ new java.util.Date() );	
		searchResultsInfo.setResults(relts);
System.err.println("tata evnt search 12 :"+ new java.util.Date() );
	}
	
	public SearchResultsInfo getSearchResultsInfo(){
		return this.searchResultsInfo;
	}
	
	private void addToDefaultQuery(Map<String, String> searchQuery,String[] tags) throws UnsupportedEncodingException{
		
		Map<String, List<String>> facetsQryBuilder = new HashMap<String, List<String>>();
		for(String s:tags) {
			String temp = URLDecoder.decode(s,"UTF-8");
			// categories/badge
			String key = temp.substring(temp.indexOf(":")+1,temp.length());
			String category = key.substring(0,key.indexOf("/"));
			if(facetsQryBuilder.containsKey(category)){
				facetsQryBuilder.get(category).add(temp);
			}else{
				List<String> value = new ArrayList<String>();
				value.add(s);
				facetsQryBuilder.put(category, value);
			}
		}
		Object[] categories = facetsQryBuilder.keySet().toArray();
		for(int i=0;i<categories.length;i++){
			List<String> tagsPath = facetsQryBuilder.get(categories[i].toString());
			int count=1;
			if(i>0){
				searchQuery.put("gsproperty", "jcr:content/cq:tags");
				searchQuery.put("gsproperty.or", "true");
				for(String tagPath:tagsPath ) {
					searchQuery.put("gsproperty."+count+++"_value", tagPath);
				}
			}else{
					searchQuery.put("1_property.or", "true");
					for(String tagPath:tagsPath) {
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
			if(!startdtRange.isEmpty()) {
				String startDtDecoder = URLDecoder.decode(startdtRange,"UTF-8");
				startRange = (Date)parse.parse(startDtDecoder);
				strRange = formatter.format(startRange);
			}
			if(!enddtRange.isEmpty()) {
				String endDtDecoder= URLDecoder.decode(enddtRange,"UTF-8");
				endRange = (Date)parse.parse(endDtDecoder);
				edRange = formatter.format(endRange);
			}
		}catch (Exception e) {
			log.error("Error ::::::::::::::[" +e.getMessage() +"]");
			
		}
		addDateRangeQuery(strRange,edRange);
		
	}
	
	private List<String> eventRegions(String path) {
		Map<String, String> region = new LinkedHashMap<String, String>();
		
//if( true )return new ArrayList<String>();
		
System.err.println("tata evnt search 6_1 :"+ new java.util.Date() );
		// As compare after() depends on the today which keeps on changing as it has a time format in it
		// So we need to convert it into the date format yyyy-MM-dd so event and today return the same 
		Date today = new Date();
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String startDateStr = formatter.format(today);
		try{
			today = formatter.parse(startDateStr);
		}catch(Exception e){
			
		}
		region.put("type", "cq:Page");
		region.put("path",path);
System.err.println("tata evnt search 6_1_1 :"+ new java.util.Date() +" : "+ path );	
		region.put("p.limit", "-1");
		
		Set<String> regions = new HashSet<String>();
		long startTime = System.nanoTime();
		Date eventDate = null;
		String eventDt = "";
System.err.println("tata evnt search 6_2 :"+ new java.util.Date() );
		try {
			List<Hit> hits = SearchUtils.performContentSearch(region,slingRequest,this.queryBuilder);
System.err.println("tata evnt search 6_3 :"+ new java.util.Date() );
			for(Hit hts : hits) {
				eventDate = null;
				eventDt = "";
				Node node = hts.getNode();
				if(node.hasNode("jcr:content/data")) {
					try {
						Node propNode = node.getNode("jcr:content/data");
						if(propNode.hasProperty("region")) {
							if(propNode.hasProperty("end")){
								eventDate = propNode.getProperty("end").getDate().getTime();
							}
							else if(propNode.hasProperty("start")) {
								eventDate = propNode.getProperty("start").getDate().getTime();
							}
							eventDt = formatter.format(eventDate);
								// As compare after() depends on the today which keeps on changing as it has a time format in it
								// So we need to convert it into the date format yyyy-MM-dd so event and today return the same 
							try{
								eventDate = formatter.parse(eventDt);
							}catch(Exception e){
								log.error("couldn't parse the date");
							}
								//System.out.println("EventDate" +eventDate  +today);
							if(eventDate.after(today) || eventDate.equals(today)) {
								regions.add(propNode.getProperty("region").getString());
							}
						}
System.err.println("tata evnt search 6_4 :"+ new java.util.Date() );
					}catch(Exception e) {
						log.error("Event Node doesn't contains required jcr:content/data Node" +e.getMessage());
					}
				}
			}
		} catch (Exception e) {
			log.error(" Problem Occured whne performing search" +e.getMessage());
		}
		List<String> list = new ArrayList<String>(regions);
		Collections.sort(list);
System.err.println("tata evnt search 6_5 :"+ new java.util.Date() );
		long endTime = System.nanoTime();
		log.debug("-------------Time Take in Milliseconds --------------" +(endTime - startTime)/1000000);
		return list;
		
	}
	

}
