package org.girlscouts.web.gsusa.servlets;

import com.day.text.csv.Csv;
import com.day.cq.wcm.commons.ReferenceSearch;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
 
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Iterator;

@SlingServlet
@Properties({
        @Property(name = "sling.servlet.methods", value = {"GET"}, propertyPrivate = true),
		@Property(name = "sling.servlet.paths", value = "/bin/gsusa/references", propertyPrivate = true),
		@Property(name = "sling.servlet.extensions", value = "csv", propertyPrivate = true) })
public class GetReferences extends SlingAllMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1341523521748738122L;

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
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
			ResourceResolver resolver = request.getResourceResolver();
			List<String> assetPaths = getAssets(resolver, path);
			ReferenceSearch referenceSearch = new ReferenceSearch();
			referenceSearch.setExact(true);
			referenceSearch.setHollow(true);
			referenceSearch.setMaxReferencesPerPage(-1);
			List<List<String>> table = new ArrayList<List<String>>();
			for (String asset : assetPaths) {
				List<String> row = new ArrayList<String>();
				Set<String> councils = new HashSet<String>();
				row.add(asset);
				Collection<ReferenceSearch.Info> resultSet = referenceSearch.search(resolver, asset).values();
				for (ReferenceSearch.Info info : resultSet) {
					for (String p : info.getProperties()) {
						int councilNameIndex = p.indexOf("/", "/content/".length());
						String councilPath = p.substring(0, councilNameIndex);
						councils.add(councilPath);
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

	private List<String> getAssets(ResourceResolver resolver, String path) {
		List<String> results = new ArrayList<String>();
		Resource resource = resolver.resolve(path);
		if (resource != null && !resource.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
			Iterable<Resource> children = resource.getChildren();
			Iterator<Resource> it = children.iterator();
			if (it != null) {
				while (it.hasNext()) {
					Resource child = it.next();
					if (child.isResourceType("dam:Asset")) {
						results.add(child.getPath());
					} else {
						if (child.isResourceType("sling:OrderedFolder")) {
							results.addAll(getAssets(resolver, child.getPath()));
						}
					}
				}
			}
		}
		return results;
	}

}
