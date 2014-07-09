package org.girlscouts.web.search.formsdocuments.impl;


import java.util.HashMap;
import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.girlscouts.web.events.search.FacetBuilder;
import org.girlscouts.web.events.search.FacetsInfo;
import org.girlscouts.web.search.formsdocuments.FormsDocumentsSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.QueryBuilder;

@Component
@Service
public class FormsDocumentsSearchImpl implements FormsDocumentsSearch {

	@Reference
	FacetBuilder facetBuilder;
	
	private static Logger log = LoggerFactory.getLogger(FormsDocumentsSearchImpl.class);
	private SlingHttpServletRequest slingRequest;
	private QueryBuilder queryBuilder;
	
	//Default path if not provided in the JSP.
	private String FACETS_PATH = "/etc/tags/girlscouts";
	
	public FormsDocumentsSearchImpl(){}
	
	// I need the slingRequest, queryBuilder and Facet_path;
	public FormsDocumentsSearchImpl(SlingHttpServletRequest slingRequest,QueryBuilder builder, String facetsPath){
		this.slingRequest = slingRequest;
		this.queryBuilder = builder;
		if(!facetsPath.isEmpty()){
			this.FACETS_PATH = facetsPath;
		}
		
		
		
	}
	
	
	public void documentSearch(){
		
		
	}
	
	// create the Facets
	public void createFacet(){
		HashMap<String, List<FacetsInfo>> facets = facetBuilder.getFacets(slingRequest, queryBuilder, FACETS_PATH);
		
	}

	public void test() {
		
		System.out.println("This worked as necessary");
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
	
}
