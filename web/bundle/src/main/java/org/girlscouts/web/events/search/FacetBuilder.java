package org.girlscouts.web.events.search;

import java.util.HashMap;
import java.util.List;

import javax.jcr.RepositoryException;

import org.apache.sling.api.SlingHttpServletRequest;

import com.day.cq.search.QueryBuilder;

public interface FacetBuilder{
	HashMap<String, List<FacetsInfo>> getFacets(SlingHttpServletRequest slingRequest, QueryBuilder queryBuilder, String FACETS_PATH);	

}
