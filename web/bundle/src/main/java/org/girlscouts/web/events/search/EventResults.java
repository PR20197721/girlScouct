package org.girlscouts.web.events.search;

import java.util.Map;

import javax.jcr.RepositoryException;

import org.apache.sling.api.SlingHttpServletRequest;

import com.day.cq.search.QueryBuilder;

public interface EventResults {
	public SearchResultsInfo performContentSearch(Map<String, String> map,
			SlingHttpServletRequest slingRequest, QueryBuilder builder, String offset) throws RepositoryException;
}
