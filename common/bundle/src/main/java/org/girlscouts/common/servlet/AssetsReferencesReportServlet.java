package org.girlscouts.common.servlet;

import com.day.text.csv.Csv;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.wcm.commons.ReferenceSearch;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.OptingServlet;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SlingServlet(
		label = "Girl Scouts Assets References Report Servlet", description = "Generates a csv file report of all pages where asset is being used", paths = {},
        methods = { "GET", "POST" }, // Ignored if paths is set - Defaults to GET if not specified
		resourceTypes = { "gsusa/servlets/assetreferences" }, // Ignored if
																// paths is set
		selectors = {}, // Ignored if paths is set
        extensions = { "html", "htm" }  // Ignored if paths is set
)
public class AssetsReferencesReportServlet extends SlingAllMethodsServlet implements OptingServlet {
	private static final Logger log = LoggerFactory.getLogger(AssetsReferencesReportServlet.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -1341523521748738122L;

	@Reference
	private SlingSettingsService settingsService;

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	private void processRequest(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws IOException, ServletException {
		if (!isPublisher()) {
			String[] paths = request.getParameterValues("path");
			if (paths == null || paths.length == 0) {
				response.sendError(400, "missing path parameter");
			}
			final Csv csv = new Csv();
			try {
				final Writer writer = response.getWriter();
				csv.writeInit(writer);
				response.setContentType("text/csv");
				String disposition = "attachment; fileName=references.csv";
				response.setHeader("Content-Disposition", disposition);
				List<List<String>> table = new ArrayList<List<String>>();
				ResourceResolver resolver = request.getResourceResolver();
				for (String path : paths) {
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
								Resource resource = resolver.resolve(p);
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
			if (resource.isResourceType("sling:OrderedFolder") || resource.isResourceType("sling:Folder")) {
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

	/** OptingServlet Acceptance Method **/

    @Override
    public final boolean accepts(SlingHttpServletRequest request) {
        /*
         * Add logic which inspects the request which determines if this servlet
         * should handle the request. This will only be executed if the
         * Service Configuration's paths/resourcesTypes/selectors accept the request.
         */
        return true;
    }

}