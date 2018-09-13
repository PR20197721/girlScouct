package org.girlscouts.common.search.formsdocuments;

import java.util.Map;
import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.girlscouts.common.events.search.FacetsInfo;
import org.girlscouts.common.events.search.SearchResultsInfo;

public interface FormsDocumentsSearch {
	public Map<String, List<FacetsInfo>> loadFacets(SlingHttpServletRequest slingRequest,String councilSpPath);
	public SearchResultsInfo getSearchResultsInfo();
	public void executeSearch(ResourceResolver resourceResolver, String q, String path,String[] tags,String formDocumentContentPath,Map<String, List<FacetsInfo>> facetsAndTags);

}
