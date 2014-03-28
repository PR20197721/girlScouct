package org.girlscouts.web.events.search.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.girlscouts.web.events.search.FacetBuilder;
import org.girlscouts.web.events.search.FacetsInfo;
import com.day.cq.tagging.TagManager;
import com.day.cq.tagging.Tag;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.Query;
import com.day.cq.search.facets.Facet;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;

public class FacetBuilderImpl implements FacetBuilder{
	
	private Map<String, String> queryBuilder = new HashMap<String, String>();
	private PredicateGroup predicateGroup = new PredicateGroup();
	private static String FACETS_PATH = "/etc/tags/girlscouts"; 
	private HashMap<String,List<FacetsInfo>> facets = new HashMap<String, List<FacetsInfo>>();
	
	private void buildFacets(SlingHttpServletRequest slingRequest,QueryBuilder builder){
		
		TagManager tagMgr = slingRequest.getResourceResolver().adaptTo(TagManager.class);
		ResourceResolver resourceResolver = slingRequest.getResourceResolver();
		Iterator<Resource> resources = resourceResolver.getResource(FACETS_PATH).listChildren();
		
		while(resources.hasNext())
		{
			Resource resource = resources.next();			
		    Tag tag = tagMgr.resolve(resource.getPath());
		    //System.out.println("Title" +tag.getTitle());
			facets.put(tag.getName(), new ArrayList<FacetsInfo>());
			Iterator <Resource> childFactes= resourceResolver.listChildren(resource);
		
			while(childFactes.hasNext())
			{
				if(facets.containsKey(tag.getName())){
					Tag cTag = tagMgr.resolve(childFactes.next().getPath());
					facets.get(tag.getName()).add(new FacetsInfo(cTag.getTitle(),cTag.getTagID(),false,0L));
					
				}
			}
			
		}
		
		
	}


	public HashMap<String, List<FacetsInfo>> getFacets(SlingHttpServletRequest slingRequest,QueryBuilder builder) {
		// TODO Auto-generated method stub
		buildFacets(slingRequest,builder);
		return facets;
	}
	
	
	

}
