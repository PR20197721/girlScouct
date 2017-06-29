package org.girlscouts.web.search;

import java.net.URLDecoder;
import javax.jcr.query.*;

import org.apache.sling.api.SlingHttpServletRequest;

import javax.jcr.Session;

public class GSJcrSearchProvider {

	private static final String QUERY_LANGUAGE = "JCR-SQL2";
	private static final String EXPRESSION = "SELECT [jcr:score], [jcr:path], [jcr:primaryType] FROM [%s] AS s WHERE ISDESCENDANTNODE([%s]) and CONTAINS(s.*, '%s')";

	private final Session session;

	public GSJcrSearchProvider(SlingHttpServletRequest slingRequest) {
		this.session = slingRequest.getResourceResolver().adaptTo(Session.class);
	}

	public QueryResult search(String path, String type, String searchText) {
		QueryResult result = null;
		try {
			String decodedSearchText = URLDecoder.decode(searchText, "UTF-8");
			String query = String.format(EXPRESSION, type, path, decodedSearchText);
			QueryManager queryManager = session.getWorkspace().getQueryManager();
			Query sql2Query = queryManager.createQuery(query, QUERY_LANGUAGE);
			return sql2Query.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
