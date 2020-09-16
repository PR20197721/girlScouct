package org.girlscouts.common.servlet;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.OptingServlet;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.text.csv.Csv;

@SlingServlet(label = "Girl Scouts Assets Tracking Report Servlet", description = "Generates a csv report of assets historical data", paths = {}, methods = {
		"GET",
		"POST" }, resourceTypes = { "gsusa/servlets/assettracking" }, selectors = {}, extensions = { "html", "htm" })
public class AssetTrackingReportServlet extends SlingAllMethodsServlet implements OptingServlet {

	private static final long serialVersionUID = 7900321078910970391L;
	private static final Logger logger = LoggerFactory.getLogger(AssetTrackingReportServlet.class);

	@Override
	public boolean accepts(SlingHttpServletRequest request) {
		return true;
	}

	@Reference
	private SlingSettingsService settingsService;

	@Reference
	private QueryBuilder queryBuilder;

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
		logger.info("Processing Request");
		ResourceResolver resourceResolver = request.getResourceResolver();
		Session session = resourceResolver.adaptTo(Session.class);

		if (!isPublisher()) {
			String[] paths = request.getParameterValues("path");
			if (paths == null || paths.length == 0) {
				try {
					response.sendError(400, "missing path parameter");
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
			final Csv csv = new Csv();
			Writer writer;
			try {
				writer = response.getWriter();
				csv.writeInit(writer);
				response.setContentType("text/csv");
				String disposition = "attachment; fileName=references.csv";
				response.setHeader("Content-Disposition", disposition);
				List<List<String>> table = new ArrayList<List<String>>();
				// List<String> assetPaths = new ArrayList<String>();
				List<String> assetPaths = null;
				String[] header = { "Asset", "Date-Time", "Component", "Action" };
				csv.writeRow(header);
				ResourceResolver resolver = request.getResourceResolver();
				for (String path : paths) {
					Resource target = resolver.resolve(path);
					assetPaths = new ArrayList<String>();
					if (!target.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
						if (target.isResourceType("dam:Asset")) {
							if(path.startsWith("/content/dam/girlscouts-shared")) {
							 assetPaths.add(path);
							}
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
					if (null != assetPaths && !assetPaths.isEmpty()) {
						writeToFile(session, csv, assetPaths);
					}
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			} finally {
				csv.close();
			}
		}
	}

	private void writeToFile(Session session, final Csv csv, List<String> assetPaths) throws IOException {
		for (String str : assetPaths) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("path", str);
			map.put("property", "councilPageUrl");
			map.put("property.operation", "exists");
			Query query = queryBuilder.createQuery(PredicateGroup.create(map), session);
			SearchResult result = query.getResult();
			Iterator<Resource> it = result.getResources();
			if (it != null) {
				while (it.hasNext()) {
					Resource resource = it.next();
					logger.info("In getResource, returning {}", resource.getPath());
					List<String> row = new ArrayList<String>();
					Node prppertyNode = null;
					String dateTime = null;
					String componentPath = null;
					String eventType = null;
					try {
						prppertyNode = resource.adaptTo(Node.class);
						dateTime = prppertyNode.getProperty("assetUsedDate").getValue().getString();
						componentPath = prppertyNode.getProperty("componentPath").getValue()
								.getString();
						eventType = prppertyNode.getProperty("eventType").getValue().getString();
					} catch (Exception e) {

					}
					logger.debug("Date string form the node--->"+dateTime);
					String date = getDateLiterals(dateTime);
					String assetpath = StringUtils.substring(resource.getPath(), 0,
							resource.getPath().indexOf("jcr:content"));
					row.add(assetpath);
					row.add(date);
					row.add(componentPath);
					row.add(eventType);
					csv.writeRow(row.toArray(new String[row.size()]));
				}
			}
		}
	}

	private boolean isPublisher() {

		return settingsService.getRunModes().contains("publish");
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
							if(child.getPath().startsWith("/content/dam/girlscouts-shared")) {
							   results.add(child.getPath());
							}
						} else {
							results.addAll(getAssets(child));
						}
					}
				}
			}
		}
		return results;
	}

	private String getDateLiterals(String dateString) {
		String dateTime = dateString.substring(0, 19);
		dateTime = dateTime.replace("T", " ");
		/*String[] dateAndTime = dateTime.split(" ");
		String[] elements = dateAndTime[0].split("-");
		String result = new String();
		for (int i = 2; i > -1; i--) {
			if (i != 0) {
				result = result + elements[i] + "-";
			} else {
				result = result + elements[i];
			}
		}*/
		return dateTime;
	}
}
