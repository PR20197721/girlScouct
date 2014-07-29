package org.girlscouts.web.search.formsdocuments.impl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.girlscouts.web.events.search.FacetBuilder;
import org.girlscouts.web.events.search.FacetsInfo;
import org.girlscouts.web.events.search.SearchResultsInfo;
import org.girlscouts.web.search.DocHit;
import org.girlscouts.web.search.formsdocuments.FormsDocumentsSearch;
import org.girlscouts.web.search.utils.SearchUtils;
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

@Component
@Service
public class FormsDocumentsSearchImpl implements FormsDocumentsSearch {

	@Reference
	FacetBuilder facetBuilder;

	private static Logger log = LoggerFactory.getLogger(FormsDocumentsSearchImpl.class);
	private SlingHttpServletRequest slingRequest;
	private QueryBuilder queryBuilder;
	HashMap<String, List<FacetsInfo>> facets;

	//TODO: Default path if not provided in the JSP.
	private String FACETS_PATH = "/etc/tags/girlscouts";
	private SearchResultsInfo searchResultsInfo;

	private Map<String, ArrayList<String>> facetsQryBuilder = new HashMap<String, ArrayList<String>>();
	public FormsDocumentsSearchImpl(){}

	private HashMap<String, List<FacetsInfo>> createFacet(SlingHttpServletRequest slingRequest, QueryBuilder queryBuilder){
		HashMap<String, List<FacetsInfo>> fts = null;
		try{
			fts = facetBuilder.getFacets(slingRequest, queryBuilder, FACETS_PATH);

		}catch(Exception e){
			//TODO: Handle this null condition. So Page shouldn't explode.
			log.info("Facets Tag do not exists: Please create Tags"+e.getMessage());
			return null;
		}
		return fts;
	}

	public void executeSearch(SlingHttpServletRequest slingRequest, QueryBuilder queryBuilder, String q, String path,String[] tags){
		//TODO : HANDLE NULL FOR THIS REQUEST
		this.queryBuilder = queryBuilder;
		this.slingRequest = slingRequest;
		this.facets = createFacet(slingRequest, queryBuilder);
		try{
			documentsSearch(path,q,tags,this.facets);

		}catch(RepositoryException re){}

	}

	private void documentsSearch(String path,String q, String[] tags, HashMap<String, List<FacetsInfo>> facets2) throws RepositoryException{
		LinkedHashMap<String, String> searchQuery = new LinkedHashMap<String, String>();

		List<String> relts = new ArrayList<String>(); 
		List<Hit> hits = new ArrayList<Hit>();
		List<Hit> searchTermHits = new ArrayList<Hit>();
		List<Hit> tagHits = new ArrayList<Hit>();
		searchResultsInfo = new SearchResultsInfo();
		Map<String,String> pth1 = new HashMap<String, String>();
		searchQuery.put("type","nt:hierarchyNode");
		searchQuery.put("path",path);


		searchQuery.put("1_property","jcr:content/metadata/cq:tags");
		searchQuery.put("p.limit","-1");		
		if(q!=null && !q.isEmpty()){

			log.info("Search Query Term [" +q +"]");
			searchQuery.put("fulltext", q);
			searchQuery.put("fulltext.relPath", "jcr:content");
			searchTermHits = performContentSearch(searchQuery,slingRequest,this.queryBuilder,null,searchResultsInfo,q);
			searchResultsInfo.setResultsHits(searchTermHits);

		}

		if(tags!=null){
			addToDefaultQuery(searchQuery,tags);
			tagHits = performContentSearch(searchQuery,slingRequest,this.queryBuilder,null,searchResultsInfo,q);
			searchResultsInfo.setResultsHits(tagHits);	

		}
		if((q!=null && !q.isEmpty()) && (tags!=null && tags.length>0)){
			// Now combine 2 different hits;
			searchResultsInfo.setResultsHits(combineHits(searchTermHits, tags));	
		}
		searchResultsInfo = SearchUtils.combineSearchTagsCounts(searchResultsInfo,facets2);
	}

	private List<Hit> combineHits(List<Hit> searchTermHits, String[] tags) throws RepositoryException{
		String valueString="";
		HashSet<Hit> hitSet = new HashSet<Hit>();
		HashSet<String> set = new HashSet<String>();
		for (String words : tags){
			set.add(words);

		}
		for(Hit hit:searchTermHits){	
			DocHit docHit = new DocHit(hit);
			Node node = slingRequest.getResourceResolver().getResource(docHit.getURL()+"/jcr:content/metadata").adaptTo(Node.class);
			if(node.hasProperty("cq:tags")){
				Value[] value = node.getProperty("cq:tags").getValues();
				for(Value val : value){
					valueString = val.getString();
					if(set.contains(valueString)){
						hitSet.add(hit);
					}
				}

			}
		}
		List<Hit> htArray = new ArrayList<Hit>(hitSet); 
		return htArray;

	}	

	private void addToDefaultQuery(Map<String, String> searchQuery,String[] tags) throws RepositoryException{

		searchQuery.put("1_property","jcr:content/metadata/cq:tags");
		searchQuery.put("1_property.or","true");
		Map<String, ArrayList<String>> facetsQryBuilder = new HashMap<String, ArrayList<String>>();
		int propertyCounter = 1;
		for(String s:tags)
		{
			String temp = s.replaceAll("%3A", ":").replaceAll("%2F", "/");
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
		int count = 1;
		for(int i=0;i<categories.length;i++){
			ArrayList<String> tagsPath = facetsQryBuilder.get(categories[i].toString());

			for(String tagPath:tagsPath){
				log.info("TagPath   ["+tagPath +"]");
				searchQuery.put(propertyCounter+"_property."+count+++"_value", tagPath);
			}

		}



	}
	public HashMap<String, List<FacetsInfo>> getFacets(){
		return facets;
	}

	public SearchResultsInfo getSearchResultsInfo(){
		return searchResultsInfo;

	}
	public void test() {

	}


	public List<Hit> performContentSearch(Map<String, String> map, SlingHttpServletRequest slingRequest, QueryBuilder builder,String offset,SearchResultsInfo sResults,String q) throws RepositoryException
	{
		//SearchResultsInfo sResults; sResults = new SearchResultsInfo();
		List<String> Sresults = new ArrayList<String>();		
		PredicateGroup predicateGroup = PredicateGroup.create(map);
		Query query = builder.createQuery(predicateGroup,slingRequest.getResourceResolver().adaptTo(Session.class));
		query.setExcerpt(true);

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
		Map<String, Facet> facets = searchResults.getFacets();
		log.info("# of page displayed" +searchResults.getResultPages().size());
		log.info("This is the facets  [" +facets.toString() +"]"  + " TotalHitMatches [" +searchResults.getTotalMatches() +"]");
		sResults.setHitCounts(searchResults.getTotalMatches());
		if(!facets.containsKey("1_property") && (q!=null && !q.isEmpty()))
		{
			// When Only q term is search it doesn't return the property and facets to get the 
			//Facets and count we are using this method
			getFacetsForSearchTerm(sResults,hits);
		}else{
			for(String key:facets.keySet())
			{
				Facet fat = facets.get(key);
				log.info("Key Name  [" +key +" ]   Count[" +fat.getBuckets().size() +"]");
				if(fat.getContainsHit() && key.equals("1_property")){
					for(Bucket bucket: fat.getBuckets()){
						populateFacets(bucket.getValue(), bucket.getCount(), sResults);

					}
				}	

			}

		}

		return hits;
	}

	public void getFacetsForSearchTerm(SearchResultsInfo sResults, java.util.List<Hit> hits) throws RepositoryException{

		String valueString;
		Map<String,Long> facetsCounts = new HashMap<String,Long>();
		for(Hit hit:hits){
			DocHit docHit = new DocHit(hit);
			Node node = slingRequest.getResourceResolver().getResource(docHit.getURL()+"/jcr:content/metadata").adaptTo(Node.class);
			if(node.hasProperty("cq:tags")){
			    Property tagProp = node.getProperty("cq:tags");
			    Value[] value = null;
			    if (!tagProp.isMultiple()) {
			        value = new Value[]{node.getProperty("cq:tags").getValue()};
			    } else {
			        value = node.getProperty("cq:tags").getValues();
			    }
				for(Value val : value){
					valueString = val.getString();
					if(facetsCounts.containsKey(valueString)){
						Long count = facetsCounts.get(valueString);
						facetsCounts.put(valueString, count++);
					}else{
						facetsCounts.put(valueString,(long) 1);
					}
				}
				for(Map.Entry<String, Long> entry: facetsCounts.entrySet()){
					populateFacets(entry.getKey().toString(), entry.getValue(), sResults);
				}
			}
		}

	}


	public void populateFacets(String bucketValue,Long counts, SearchResultsInfo sResults){
		TagManager tagMgr = slingRequest.getResourceResolver().adaptTo(TagManager.class);
		Tag tag = tagMgr.resolve(bucketValue);
		if (tag != null){
			sResults.createFacetsWithTag(tag.getParent().getName(),tag.getTitle(), counts);
		} else {
			log.error(">>>>> Unable to resolve tag " + bucketValue);
		}
	}

}

