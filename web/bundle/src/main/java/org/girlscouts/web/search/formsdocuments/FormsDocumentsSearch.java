package org.girlscouts.web.search.formsdocuments;

import java.util.HashMap;
import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;
import org.girlscouts.web.events.search.FacetsInfo;
import org.girlscouts.web.events.search.SearchResultsInfo;

import com.day.cq.search.QueryBuilder;

public interface FormsDocumentsSearch {
	
	public void test();
	public HashMap<String, List<FacetsInfo>> getFacets();
	public SearchResultsInfo getSearchResultsInfo();
	public void executeSearch(SlingHttpServletRequest slingRequest, QueryBuilder queryBuilder, String q, String path,String[] tags,String councilSpName);

}
