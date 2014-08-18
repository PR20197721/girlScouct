package org.girlscouts.web.events.search;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
		
		List<Hit> hits = SearchUtils.performContentSearch(searchQuery,slingRequest,this.queryBuilder,offset,searchResultsInfo);
		for(int i=0;i<hits.size();i++){
			Hit ht = hits.get(i);
			String pth = ht.getNode().isNodeType("cq:Page")?ht.getPath():ht.getPath();
			relts.add(pth);
			}
				
		searchResultsInfo.setResults(relts);
		
		/*Iterator searchIterator = searchResultsInfo.getFacetsWithCount().keySet().iterator();
		
		while(searchIterator.hasNext())
		{
			String key = (String)searchIterator.next();
			Map<String,Long> Categoriestags = searchResultsInfo.getFacetsWithCount().get(key);
			Iterator search= Categoriestags.keySet().iterator();
			while(search.hasNext()){
				String key1 =(String) search.next();
			}
		}*/
		
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
				searchQuery.put("1_property.or", "true");
				for(String tagPath:tagsPath){
					searchQuery.put("1_property."+count+++"_value", tagPath);
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
		if(!lowerBound.isEmpty()){
			searchQuery.put("daterange.lowerBound",lowerBound);
		}
		if(!upperBound.isEmpty()){	
			searchQuery.put("daterange.upperBound",upperBound);
		}
		
	}
	public void addDateRangesToQuery(String startdtRange, String enddtRange,Map<String, String> searchQuery){
		log.debug("startdtRange" +startdtRange  +"enddtRange" +enddtRange);
		DateFormat parse = new SimpleDateFormat("MM-dd-yyyy");
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date startRange = null;
		Date endRange =null;
		String strRange="";
		String edRange="";
		
		
		
		try {
			if(!startdtRange.isEmpty()){
				String startDtDecoder = URLDecoder.decode(startdtRange,"UTF-8");
				System.out.println("What is the decorder start date:::::" +startDtDecoder);
				if(startdtRange.indexOf("%2F")>0){
					startdtRange = startdtRange.replace("%2F", "-");
				}else{
					startdtRange = startdtRange.replaceAll("/", "-");
				}
				startRange = (Date)parse.parse(startdtRange);
				strRange = formatter.format(startRange);
				System.out.println(strRange);
			}
			if(!enddtRange.isEmpty()){
				String endDtDecoder= URLDecoder.decode(enddtRange,"UTF-8");
				System.out.println("What is the decorder start date:::::" +endDtDecoder);
				if(enddtRange.indexOf("%2F")>0){
					enddtRange = enddtRange.replace("%2F", "-");
				}else{
					enddtRange = enddtRange.replaceAll("/", "-");
				}
				endRange = (Date)parse.parse(enddtRange.replaceAll("%2F", "-"));
				edRange = formatter.format(endRange);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("Error" +e.getMessage());
			
		}
		addDateRangeQuery(strRange,edRange);
		
	}
	
	
	

}
