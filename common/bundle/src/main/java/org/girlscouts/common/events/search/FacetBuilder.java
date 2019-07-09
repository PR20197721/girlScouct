package org.girlscouts.common.events.search;

import java.util.Map;
import java.util.List;

import javax.jcr.RepositoryException;

import org.apache.sling.api.SlingHttpServletRequest;

import com.day.cq.search.QueryBuilder;

public interface FacetBuilder{
	Map<String, List<FacetsInfo>> getFacets(SlingHttpServletRequest slingRequest, QueryBuilder queryBuilder, String FACETS_PATH);
	FacetsInfo getFacet(SlingHttpServletRequest slingRequest, String FACET_PATH);
}
