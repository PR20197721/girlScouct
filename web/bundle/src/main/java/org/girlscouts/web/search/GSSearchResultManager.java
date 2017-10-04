package org.girlscouts.web.search;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.query.QueryResult;
import javax.jcr.query.RowIterator;
import javax.jcr.query.Row;

import org.girlscouts.web.events.search.GSDateTime;
import org.girlscouts.web.events.search.GSDateTimeFormat;
import org.girlscouts.web.events.search.GSDateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GSSearchResultManager {

    private static final Logger log = LoggerFactory.getLogger(GSSearchResultManager.class);

	private Map<String, GSSearchResult> searchResults;

	private String[] resourceTypeFilters = new String[] { "girlscouts/components/contact-placeholder-page",
			"girlscouts/components/contact-page" };

	public GSSearchResultManager() {
		this.searchResults = new HashMap<String, GSSearchResult>();

    }

	public void add(QueryResult result) {
		if (result != null) {
			try {
				RowIterator rowIter = result.getRows();
				while (rowIter.hasNext()) {
					Row row = rowIter.nextRow();
					add(new GSSearchResult(row));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void add(GSSearchResult result) {
		if (result != null) {
			if (this.searchResults.containsKey(result.getPath())) {
				GSSearchResult existingResult = this.searchResults.get(result.getPath());
				if (result.getScore().compareTo(existingResult.getScore()) > 0) {
					this.searchResults.replace(result.getPath(), result);
				}
			} else {
				this.searchResults.put(result.getPath(), result);
			}
		}
	}

	public int size() {
		return searchResults.size();
	}

	public List<GSSearchResult> getResults() {
		return new ArrayList<GSSearchResult>(searchResults.values());
	}

	public List<GSSearchResult> getResultsSortedByScore() {
		List<GSSearchResult> results = new ArrayList<GSSearchResult>(searchResults.values());
		Collections.sort(results, new GSSearchResultComparator());
		Collections.reverse(results);
		return results;
	}

	public void filter() {
		Set<String> keys = new HashSet<String>();
		keys.addAll(this.searchResults.keySet());
		for (String key : keys) {
			try {
				GSSearchResult result = this.searchResults.get(key);
				Node resultNode = result.getResultNode();
				if (resultNode.hasNode("jcr:content")) {
					Node jcrContentNode = resultNode.getNode("jcr:content");
					if (isFilterByResourceType(jcrContentNode) || isFilterByProperty(jcrContentNode)) {
						this.searchResults.remove(key);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class GSSearchResultComparator implements Comparator<GSSearchResult> {

		@Override
		public int compare(GSSearchResult result1, GSSearchResult result2) {
			int result = 0;
			Double score1 = result1.getScore();
			Double score2 = result2.getScore();
			result = score1.compareTo(score2);
			if (result != 0) {
				return result;
			} else {
				try {
					Calendar date1 = result1.getResultNode().getProperty("jcr:created").getDate();
					Calendar date2 = result2.getResultNode().getProperty("jcr:created").getDate();
					result = date1.compareTo(date2);
				} catch (Exception e) {

				}
				return result;
			}

		}

	}

	private boolean isFilterByResourceType(Node jcrContentNode) {
		try {
			if (jcrContentNode.hasProperty("sling:resourceType")) {
				String resourceType = jcrContentNode.getProperty("sling:resourceType").getString();
				for (String rtFilter : resourceTypeFilters) {
					if (resourceType.equals(rtFilter)) {
						return true;
					}
				}
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean isFilterByProperty(Node jcrContentNode) {
		try {
			GSDateTime today = new GSDateTime();
			GSDateTimeFormatter dtfIn = GSDateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			if (jcrContentNode.hasNode("data")) {
				Node dataNode = jcrContentNode.getNode("data");
				if (dataNode.hasProperty("visibleDate")) {
					try {
						String visibleDate = dataNode.getProperty("visibleDate").getString();
						GSDateTime vis = GSDateTime.parse(visibleDate, dtfIn);
						if (vis.isAfter(today)) {
							return true;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (dataNode.hasProperty("end")) {
					try {
						String endDateString = dataNode.getProperty("end").getString();
						GSDateTime endDate = GSDateTime.parse(endDateString, dtfIn);
						if (today.isAfter(endDate)) {
							return true;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return false;
	}
}