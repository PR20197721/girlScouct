package org.girlscouts.web.events.search.impl;

import java.util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.girlscouts.web.events.search.EventsSrch;
import org.girlscouts.web.events.search.FacetBuilder;
import org.girlscouts.web.events.search.FacetsInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.tagging.TagManager;
import com.day.cq.tagging.Tag;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.Query;
import com.day.cq.search.facets.Facet;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;

@Component
@Service
public class FacetBuilderImpl implements FacetBuilder{
	private static Logger log = LoggerFactory.getLogger(FacetBuilderImpl.class);
	public Map<String, List<FacetsInfo>> getFacets(SlingHttpServletRequest slingRequest, QueryBuilder queryBuilder, String FACETS_PATH ) {
		ResourceResolver resourceResolver = slingRequest.getResourceResolver();
		log.debug("Building Facets ");
		TagManager tagMgr = resourceResolver.adaptTo(TagManager.class);
		Resource tagResource = resourceResolver.getResource(FACETS_PATH);
		if (tagResource == null) {
			log.error("The repository requires " + FACETS_PATH + " to function properly to support tagging.");
//			throw new IllegalArgumentException("The facet path " + FACETS_PATH + " does not exit.");
			return null;
		}
		Iterator<Resource> resources = tagResource.listChildren();
		Map<String,List<FacetsInfo>> facets = new HashMap<String, List<FacetsInfo>>();
		while(resources.hasNext()) {
			try {
				Resource resource = resources.next();
				Tag tag = tagMgr.resolve(resource.getPath());
				List<FacetsInfo> tagItems = new ArrayList<FacetsInfo>();
				TreeSet<String> tagTree = new TreeSet<String>();
				Iterator<Resource> childFacets = resourceResolver.listChildren(resource);
				while (childFacets.hasNext()) {
					try {
						Tag cTag = tagMgr.resolve(childFacets.next().getPath());
						if (!tagTree.contains(cTag.getTagID())) {
							tagItems.add(new FacetsInfo(cTag.getTitle(), cTag.getTagID(), false, 0L));
							tagTree.add(cTag.getTagID());
						}
					} catch (Exception e1) {
						log.error("FacetBuilderImpl encountered an error: ", e1);
					}
				}
				facets.put(tag.getName(), tagItems);
			} catch (Exception e2) {
				log.error("FacetBuilderImpl encountered an error: ", e2);
			}
		}
		return facets;
	}
}
