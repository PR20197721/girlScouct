package org.girlscouts.web.search.formsdocuments.impl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import com.adobe.granite.testing.client.security.Group;
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
	private static String FACETS_PATH = "/etc/tags/girlscouts";

	private final String COUNCIL_SPE_PATH = "/etc/tags/";

	private SlingHttpServletRequest slingRequest;
	private QueryBuilder queryBuilder;

	private Map<String, List<FacetsInfo>> facets;
	private SearchResultsInfo searchResultsInfo;
	
	private final String FORM_DOC_CATEGORY = "forms_documents";

	public FormsDocumentsSearchImpl(){}

	private Map<String, List<FacetsInfo>> loadFacets(String councilSpPath){
		Map<String, List<FacetsInfo>> fts = null;
		try{
			log.debug("councilSpPath  [" +councilSpPath +"]");
			fts = facetBuilder.getFacets(this.slingRequest, this.queryBuilder, councilSpPath);

		}catch(Exception e){
			log.info("Facets Tag do not exists: Please create Tags"+e.getMessage());
			throw null;
		}
		return fts;
	}
	
	public void executeSearch(SlingHttpServletRequest slingRequest, QueryBuilder queryBuilder, String q, String path,String[] checkedTags,String councilSpecificPath, String formDocumentContentPath){
		this.queryBuilder = queryBuilder;
		this.slingRequest = slingRequest;
		String councilSpPath = "";
		if(!councilSpecificPath.isEmpty() && councilSpecificPath!=null){
			councilSpecificPath=COUNCIL_SPE_PATH+councilSpecificPath;
			try{
				this.facets = loadFacets(councilSpPath);
			}catch(Exception e){
				log.error("Facets [" +COUNCIL_SPE_PATH +"] does not exists fall-back to default" );
				this.facets = loadFacets(FACETS_PATH);
			}
		}
		try{
			documentsSearch(path,q,checkedTags,formDocumentContentPath);
		}catch(RepositoryException re){}

	}

	private void documentsSearch(String path,String q, String[] tags,String formDocumentContentPath) throws RepositoryException{
		LinkedHashMap<String, String> searchQuery = new LinkedHashMap<String, String>();

		List<String> relts = new ArrayList<String>(); 
		List<Hit> hits = new ArrayList<Hit>();
		List<Hit> searchTermHits = new ArrayList<Hit>();
		List<Hit> tagHits = new ArrayList<Hit>();
		searchResultsInfo = new SearchResultsInfo();
		
		
		Map<String,String> pth1 = new HashMap<String, String>();
		Map<String,String> mapPath = new HashMap <String,String>();
		Map<String,String> mapFullText = new HashMap<String,String>();
		Map<String,String> checkedTagMap = new HashMap<String,String>();
		Map<String,String> mapContentDoc = new HashMap <String,String>();
		
		
		/*searchQuery.put("path", path);
		searchQuery.put("type","nt:hierarchyNode");
		searchQuery.put("p.limit","-1");
		*/
		
		searchQuery.put("group.p.or","true");
		searchQuery.put("group.1_path", formDocumentContentPath);
		searchQuery.put("group.1_type","cq:Page");
		searchQuery.put("group.2_path", path);
		searchQuery.put("group.2_type","nt:hierarchyNode");
		searchQuery.put("p.limit","-1");
		
		
		
		/*mapPath.put("group.1_group.type", "cq:Page");
		mapPath.put("group.1_group.path", formDocumentContentPath);
		mapPath.put("p.limit","-1");
		*/
		
		
		Map<String,String> masterMap  = new HashMap<String,String>();
		PredicateGroup master = PredicateGroup.create(masterMap); 
		
		
		
		
		if(q!=null && !q.isEmpty())
		{
			log.info("Search Query Term [" +q +"]");
			mapFullText.put("group.p.or","true" );
			mapFullText.put("group.1_fulltext", q);
			mapFullText.put("group.1_fulltext.relPath", "@jcr:content");
			mapFullText.put("group.2_fulltext", q);
			mapFullText.put("group.2_fulltext.relPath", "@jcr:content/metadata/dc:title");
			mapFullText.put("group.3_fulltext", q);
			mapFullText.put("group.3_fulltext.relPath", "@jcr:content/metadata/dc:description");
			PredicateGroup predicateFullText = PredicateGroup.create(mapFullText);
			master.add(predicateFullText);
			
		}
		if(tags.length > 0){
			checkedTagMap = addToDefaultQuery(tags);
			PredicateGroup predicateCheckedTags = PredicateGroup.create(checkedTagMap);
			master.add(predicateCheckedTags);
		}
		//searchTermHits = performContentSearch(master,q);
		//this.searchResultsInfo.setResultsHits(searchTermHits);
		//this.searchResultsInfo = combineSearchTagsCounts();
		
		//searchQuery.put("group.p.or", "true")
		
		searchTermHits = performContentSearch(searchQuery,q);
		this.searchResultsInfo.setResultsHits(searchTermHits);
		this.searchResultsInfo = combineSearchTagsCounts();
		
	}
	
	private SearchResultsInfo combineSearchTagsCounts() throws RepositoryException
	{
		
		System.out.println("What is going on ..............");
		
		
		Iterator <String> everyThingFacets=null;
		List<FacetsInfo> facetsInfo = null;
		try{
			 facetsInfo = this.facets.get(FORM_DOC_CATEGORY);
		}catch(Exception e){
			log.error("Exception Caught" +e.getMessage());
		}
		List<Hit> searchTermHits = this.searchResultsInfo.getResultsHits();
		Set<String> set = new HashSet<String>();
		
		//Adding Tag to the set.
		for(FacetsInfo info : facetsInfo){
			set.add(info.getFacetsTagId());
		}
		
		// Duplicate Documents containing the same path are return when performing a set due to different renditions.
		// So put the hit in to the uni TreeMap to remove duplicates.
		Map<String, DocHit> unq= new java.util.TreeMap<String,DocHit>();
		for(Hit hit:searchTermHits)
		{
			DocHit docHit = new DocHit(hit);
			System.out.println(docHit.getURL());
			if(!unq.containsKey(docHit.getURL())){
				unq.put(docHit.getURL(), docHit);
			}
		}
		
		System.out.println("What is the Unique lenght of the treeMap" +unq.size());
		
		Iterator<String> uniIterator = unq.keySet().iterator();
		Map<String,Long> facetWithCount = new HashMap<String, Long>();
		while(uniIterator.hasNext()){
			Node node = null;
			String valueString="";
			Value[] value = null;
			try{
				// Get the path of the hits
				String cPath = unq.get(uniIterator.next()).getURL();
				//System.out.println("What is the path" +cPath);
				node = this.slingRequest.getResourceResolver().getResource(cPath+"/jcr:content").adaptTo(Node.class);
				if(node.hasNode("metadata")){
					node = node.getNode("metadata");
					
				}
				if(node.hasProperty("cq:tags")){
					// We need to check for the multiple properties.
					Property tagProps = node.getProperty("cq:tags");
					if(tagProps.isMultiple()){
						value = tagProps.getValues();
					}else{
						value = new Value[]{tagProps.getValue()};
					}
					for(Value val:value){
						valueString = val.getString();
						if(facetWithCount.containsKey(valueString)){
							facetWithCount.put(valueString, facetWithCount.get(valueString)+1);
						}else{
							facetWithCount.put(valueString, new Long(1));
						}
					}
				}
			}catch(Exception e){
				log.info("System.out.println" +e.getMessage());
			}
		}
		for(FacetsInfo info : facetsInfo){
			if(facetWithCount.containsKey(info.getFacetsTagId())){   
				info.setChecked(true);						
				info.setCount(facetWithCount.get(info.getFacetsTagId()));
			}
		}
		return this.searchResultsInfo;	
	}
	private Map<String,String> addToDefaultQuery(String[] tags) throws RepositoryException{
		
		Map<String,String> tagSearch = new HashMap<String,String>();
		tagSearch.put("1_property","jcr:content/metadata/cq:tags");
		tagSearch.put("1_property.or","true");
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
				tagSearch.put(propertyCounter+"_property."+count+++"_value", tagPath);
			}

		}
		return tagSearch;
	}

	public Map<String, List<FacetsInfo>> getFacets(){
		return this.facets;
	}
	public SearchResultsInfo getSearchResultsInfo(){
		return searchResultsInfo;
	}

	private List<Hit> performContentSearch(LinkedHashMap<String, String> searchQuery,String q) throws RepositoryException{
		
		PredicateGroup predicateGroup = PredicateGroup.create(searchQuery);
		Query query = this.queryBuilder.createQuery(predicateGroup,slingRequest.getResourceResolver().adaptTo(Session.class));
		query.setExcerpt(true);
		System.out.println("***SQL:******* "+searchQuery.toString());
		SearchResult searchResults=null;
		try{
			searchResults = query.getResult();
		}catch(Exception e){
			log.error("Error Generated performContentSearch" +e.getStackTrace());
		}
		this.searchResultsInfo.setSearchResults(searchResults);
		java.util.List<Hit> hits = searchResults.getHits();
		System.out.println("What is the hit size" +hits.size());
		return hits;
	}

	private void populateFacets(String bucketValue,Long counts){
		TagManager tagMgr = this.slingRequest.getResourceResolver().adaptTo(TagManager.class);
		Tag tag = tagMgr.resolve(bucketValue);
		if (tag != null){
			this.searchResultsInfo.createFacetsWithTag(tag.getParent().getName(),tag.getTitle(), counts);
		} else {
			log.error(">>>>> Unable to resolve tag " + bucketValue);
		}
	}

}

