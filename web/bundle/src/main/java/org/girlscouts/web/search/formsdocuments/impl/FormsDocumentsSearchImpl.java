package org.girlscouts.web.search.formsdocuments.impl;


import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.girlscouts.web.events.search.EventsSrch;
import org.girlscouts.web.events.search.FacetBuilder;
import org.girlscouts.web.events.search.impl.FacetBuilderImpl;
import org.girlscouts.web.search.formsdocuments.FormsDocumentsSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.QueryBuilder;

@Component
@Service
public class FormsDocumentsSearchImpl implements FormsDocumentsSearch {

	@Reference
	FacetBuilder facetBuilder;
	
	private static Logger log = LoggerFactory.getLogger(EventsSrch.class);
	private SlingHttpServletRequest slingRequest;
	private QueryBuilder queryBuilder;
	
	//Default path if not provided in the JSP.
	private String FACETS_PATH = "/etc/tags/girlscouts/forms_documents";
	
	public FormsDocumentsSearchImpl(){}
	// I need the slingRequest, queryBuilder and Facet_path;
	public FormsDocumentsSearchImpl(SlingHttpServletRequest slingRequest,QueryBuilder builder, String facetsPath){
		this.slingRequest = slingRequest;
		this.queryBuilder = builder;
		if(!facetsPath.isEmpty()){
			this.FACETS_PATH = facetsPath;
		}
		
		
		
	}
	
	// create the Facets
	public void createFacet(){
		facetBuilder.getFacets(slingRequest, queryBuilder, FACETS_PATH);
		
	}
	
	
	
	
	
	
}
