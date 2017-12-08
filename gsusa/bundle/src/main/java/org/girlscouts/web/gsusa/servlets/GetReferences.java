package org.girlscouts.web.gsusa.servlets;

import com.day.text.csv.Csv;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.wcm.commons.ReferenceSearch;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.settings.SlingSettingsService;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Iterator;

@SlingServlet(name = "GetReferencesServlet", resourceTypes = "gsusa/components/references", extensions = "html", methods = "GET", metatype = true)
public class GetReferences extends SlingAllMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1341523521748738122L;

	@Reference
	private SlingSettingsService settingsService;

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		if (!isPublisher()) {
			response.setContentType("text/csv");
			String disposition = "attachment; fileName=references.csv";
			response.setHeader("Content-Disposition", disposition);
			final Writer writer = response.getWriter();
			final Csv csv = new Csv();
			csv.writeInit(writer);
			String path = request.getParameter("path");

			if (path == null || path.trim().length() == 0) {
				throw new ServletException("Empty path");
			}

			try {
				List<List<String>> table = new ArrayList<List<String>>();
				ResourceResolver resolver = request.getResourceResolver();
				Resource target = resolver.resolve(path);
				List<String> assetPaths = new ArrayList<String>();
				if (target != null && !target.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
					if (target.isResourceType("dam:Asset")) {
						assetPaths.add(path);
					} else {
						int level = getLevel(path);
						if (level > 4) {
							assetPaths = getAssets(target);
						} else {
							List<String> row = new ArrayList<String>();
							row.add("Selected folder could have negative implications on server performance. Please select a folder "
									+ (5 - level) + " level/s down.");
							table.add(row);
						}
					}
				}
				ReferenceSearch referenceSearch = new ReferenceSearch();
				referenceSearch.setExact(true);
				referenceSearch.setHollow(true);
				referenceSearch.setMaxReferencesPerPage(-1);

				for (String asset : assetPaths) {
					List<String> row = new ArrayList<String>();
					Set<String> councils = new TreeSet<String>();
					row.add(asset);  
					Collection<ReferenceSearch.Info> resultSet = referenceSearch.search(resolver, asset).values();
					for (ReferenceSearch.Info info : resultSet) {
						for (String p : info.getProperties()) {
							Resource resource  = resolver.resolve(p);
							Resource page = getPage(resource);
							if (page != null) {
								if (isActive(page)) {
									p = page.getPath() + " (Active)";
								} else {
									p = page.getPath() + " (Not Active)";
								}
							}
							councils.add(p);

						}
					}
					row.addAll(councils);
					table.add(row);
				}
				Collections.sort(table, new Comparator<List<String>>() {
					public int compare(List<String> a1, List<String> a2) {
						return a2.size() - a1.size();
					}
				});
				table = transpose(table);
				for (List<String> row : table) {
					csv.writeRow(row.toArray(new String[row.size()]));
				}
			} catch (Exception e) {
				throw new ServletException("Error getting references", e);
			} finally {
				csv.close();
			}
		}
	}

	private boolean isActive(Resource page) {
		if (page != null) {
			ReplicationStatus replicationStatus = page.adaptTo(ReplicationStatus.class);
			return replicationStatus.isActivated();
		}
		return false;
	}

	private Resource getPage(Resource resource) {
		Resource parent = resource.getParent();
		if (parent != null && !resource.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
			if (parent.isResourceType("cq:Page")) {
				return parent;
			} else {
				return getPage(parent);
			}
		}
		return null;
	}

	private List<List<String>> transpose(List<List<String>> table) {
		List<List<String>> ret = new ArrayList<List<String>>();
		int max = 0;
		for (List<String> row : table) {
			if (row.size() > max) {
				max = row.size();
			}
		}
		for (int i = 0; i < max; i++) {
			List<String> col = new ArrayList<String>();
			for (List<String> row : table) {
				if (i < row.size()) {
					col.add(row.get(i));
				} else {
					col.add("");
				}
			}
			ret.add(col);
		}
		return ret;
	}

	private List<String> getAssets(Resource resource) {
		List<String> results = new ArrayList<String>();
		if (resource != null && !resource.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
			if (resource.isResourceType("sling:OrderedFolder")) {
				Iterable<Resource> children = resource.getChildren();
				Iterator<Resource> it = children.iterator();
				if (it != null) {
					while (it.hasNext()) {
						Resource child = it.next();
						if (child.isResourceType("dam:Asset")) {
							results.add(child.getPath());
						} else {
							results.addAll(getAssets(child));
						}
					}
				}
			}
		}
		return results;
	}

	private boolean isPublisher() {
		if (settingsService.getRunModes().contains("publish")) {
			return true;
		}
		return false;
	}

	private int getLevel(String path) {
		int counter = 0;
		for (int i = 0; i < path.length(); i++) {
			if (path.charAt(i) == '/') {
				counter++;
			}
		}
		return counter;
	}

}
