package org.girlscouts.web.components.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.*;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.*;
import java.util.*;

@Model(adaptables = SlingHttpServletRequest.class)
public class PageCounter {
    @Inject
    private ResourceResolverFactory resolverFactory;
    @Self
    private SlingHttpServletRequest request;
    @Inject
    private Page currentPage;
    @Inject
    private Resource resource;
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private ArrayList<String> councilTemplatePages = new ArrayList<String>();
    private ArrayList<String> councilAddedPages = new ArrayList<String>();
    private ArrayList<String> allPages = new ArrayList<String>();
    private ArrayList<String> nationalTemplatePages = new ArrayList<String>();
    private ArrayList<String> countedResourceTypes = new ArrayList<String>();
    private ArrayList<String> exceptionPages = new ArrayList<String>();
    private ArrayList<String> exceptionDirectories = new ArrayList<String>();
    private ArrayList<String> thankYouPages = new ArrayList<String>();
    private ArrayList<String> defaultValues = new ArrayList<String>();
    private ArrayList<String> overrides = new ArrayList<String>();
    private ArrayList<String> nonCountPages = new ArrayList<String>();
    private Map<String, String> nonCountPagesMap = new HashMap<String, String>();
    private ValueMap properties;
    private String councilName;
    private String councilTitle;
    public static final String RESOURCE_TYPES = "foundation/components/page, girlscouts/components/homepage, girlscouts/components/one-column-page, girlscouts/components/three-column-page, girlscouts/components/placeholder-page";
    public static final String FOOTER_LINK_FILTERs = "Terms, Conditions, Privacy, Policy, Social";
    private static final String formQuery = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([{path}]) and s.[sling:resourceType]= 'foundation/components/form/start'";

    @PostConstruct
    public void init() {
        ResourceResolver adminResourceResolver = null;
        try {
            Map<String, Object> serviceParams = new HashMap<String, Object>();
            serviceParams.put(ResourceResolverFactory.SUBSERVICE, "workflow-process-service");
            adminResourceResolver = resolverFactory.getServiceResourceResolver(serviceParams);
            properties = resource.getValueMap();
            Node node = resource.adaptTo(Node.class);
            Page top = currentPage.getAbsoluteParent(1);        // /content/<council>
            Page en = currentPage.getAbsoluteParent(2);            // /content/<council>/en
            councilTitle = top.getTitle();
            councilName = top.getName();
            String councilPath = top.getPath();
            ArrayList<String> links = new ArrayList<String>();
            // If paths value is empty set default
            // Retrieve paths set by homepage component in en/jcr:content to set as defaults
            ValueMap enProperties = en.getProperties();
            Node filtersNode = null;
            if (node.hasNode("filters")) {
                filtersNode = node.getNode("filters");
            }
            String resourceTypes = properties.get("resourceTypes", "");
            String[] overridePaths = properties.get("overrides", String[].class);
            if (resourceTypes.isEmpty()) {
                resourceTypes = RESOURCE_TYPES;
                node.setProperty("resourceTypes", resourceTypes);
                node.getSession().save();
            }
            if (filtersNode == null || !filtersNode.hasNodes()) {
                String eventRepoURL = enProperties.get("eventPath", "");
                String eventCalendarURL = enProperties.get("calendarPath", "");
                String newsURL = enProperties.get("newsPath", "");
                String sitesearchURL = enProperties.get("globalLanding", "");
                defaultValues.add(format("Homepage", councilPath + "/en", "true", "false"));
                defaultValues.add(format("Resources", councilPath + "/en/resources", "true", "true"));
                defaultValues.add(format("Event Repository", eventRepoURL, "true", "true"));
                defaultValues.add(format("News", newsURL, "false", "true"));
                defaultValues.add(format("Site Search", sitesearchURL, "true", "false"));
                defaultValues.add(format("Email Templates", councilPath + "/en/email-templates", "true", "false"));
                // Get some links from homepage footer such as Terms and Conditions, Policy
                ArrayList<String> footerLinkFilters = listToArray(FOOTER_LINK_FILTERs);
                String footernavnodepath = top.getPath() + "/en/jcr:content/footer/nav";
                Node fnode = adminResourceResolver.getResource(footernavnodepath).adaptTo(Node.class);
                Value[] linkValues = fnode.getProperty("links").getValues();
                ;
                for (int i = 0; i < linkValues.length; i++) {
                    links.add(linkValues[i].toString());
                }
                for (String s : links) {
                    String[] values = s.split("\\|\\|\\|");
                    String label = values[0];
                    String path = values.length >= 2 ? values[1] : "";
                    // If the label or path/url contains words in footer link filters,
                    // Add to filter. Else discard
                    for (int j = 0; j < footerLinkFilters.size(); j++) {
                        if (label.contains(footerLinkFilters.get(j))) {
                            defaultValues.add(format(label, path, "true", "false"));
                            break;
                        } else if (path.contains(footerLinkFilters.get(j).toLowerCase())) {
                            defaultValues.add(format(label, path, "true", "false"));
                            break;
                        }
                    }

                }
                if (!node.hasNode("filters")) {
                    filtersNode = node.addNode("filters", "nt:unstructured");
                } else {
                    filtersNode = node.getNode("filters");
                }
                for (int i = 0; i < defaultValues.size(); i++) {
                    try {
                        String filter = defaultValues.get(i);
                        String[] filterProperties = filter.split("\\|\\|\\|");
                        Node itemNode = null;
                        if (!filtersNode.hasNode("item" + i)) {
                            itemNode = filtersNode.addNode("item" + i, "nt:unstructured");
                        } else {
                            itemNode = filtersNode.getNode("item" + i);
                        }
                        itemNode.setProperty("label", filterProperties[0]);
                        itemNode.setProperty("path", filterProperties[1]);
                        itemNode.setProperty("pageOnly", filterProperties[2]);
                        itemNode.setProperty("subDirOnly", filterProperties[3]);
                        filtersNode.getSession().save();
                    } catch (Exception e) {
                    }
                }
            }
            countedResourceTypes = listToArray(resourceTypes);
            processFilterPaths(filtersNode);
            processOverridePaths(overridePaths);
            // These pages belong to council template pages
            // They aren't inherited so no mixins to check
            String eventlist = enProperties.get("eventLanding", "");
            String eventcalendar = enProperties.get("calendarPath", "");
            nationalTemplatePages.add(eventlist);
            nationalTemplatePages.add(eventcalendar);
            // Go through council directory to count pages
            recurse(adminResourceResolver, top);
            allPages.remove(0); // removes /content/<council>
            LiveRelationshipManager lrm = adminResourceResolver.adaptTo(LiveRelationshipManager.class);
            for (int i = 0; i < allPages.size(); i++) {
                processPage(adminResourceResolver, allPages.get(i), lrm);
            }
            // If overrides are set, add override paths
            overridePages();
            nonCountPages = new ArrayList<>();
            for (String page : nonCountPagesMap.keySet()) {
                nonCountPages.add(linkify(page, nonCountPagesMap.get(page)));
            }
        } catch (Exception e) {
            logger.error("Encountered error: ", e);
        } finally {
            try {
                adminResourceResolver.close();
            } catch (Exception e) {
                logger.error("Error while closing resource resolver: ", e);
            }
        }

    }

    private ArrayList<String> listToArray(String list) {
        String[] lists = list.split(",");
        ArrayList<String> arrayList = new ArrayList<String>();
        for (String str : lists) {
            arrayList.add(str.trim());
        }
        return arrayList;
    }

    private void processFilterPaths(Node filtersNode) {
        try {
            NodeIterator iterator = filtersNode.getNodes();
            while (iterator.hasNext()) {
                try {
                    Node filter = iterator.nextNode();
                    String path = filter.getProperty("path").getString().trim();
                    if (path.length() > 0) {
                        String pageOnly = filter.getProperty("pageOnly").getString();
                        String subDirOnly = filter.getProperty("subDirOnly").getString();
                        if (pageOnly.equals("true")) {
                            exceptionPages.add(path.trim());
                        }
                        if (subDirOnly.equals("true")) {
                            exceptionDirectories.add(path.trim());
                        }
                    }
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
        }
    }

    private void processOverridePaths(String[] list) {
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                //String[] values = list[i].split("\\|\\|\\|");
                //String label = values[0];
                //String path = values.length > 1 ? values[1] : "";
                //String page = values.length > 2 ? values[2] : "";
                //String subdir = values.length > 3 ? values [3] : "";
                overrides.add(list[i].trim());
            }
        }
    }

    private String linkify(String path) {
        return "<a  target=\"_blank\"  href=\"" + path.trim() + ".html\">" + trimTopLevel(path, 2) + "</a>";
    }

    private String linkify(String path, String reason) {
        return linkify(path) + " (" + reason.trim() + ")";
    }

    private String trimTopLevel(String path, int num) {
        String[] values = path.split("/");
        String newPath = "";
        if (values[0].isEmpty()) {
            num++;
        }
        if (num <= values.length) {
            for (int i = 0; i < values.length; i++) {
                if (i >= num) {
                    newPath += "/" + values[i];
                }
            }
        } else {
            newPath = path;
        }
        return newPath;
    }

    private String format(String label, String path, String page, String subdir) {
        String str = label.trim() + "|||" + path.trim() + "|||" + page.trim() + "|||" + subdir.trim();
        return str;
    }

    private void checkForms(ResourceResolver rr, Page currentPage) {
        try {
            QueryResult forms = searchForms(rr, currentPage.getContentResource());
            if (forms != null) {
                RowIterator rowIter = forms.getRows();
                while (rowIter.hasNext()) {
                    try {
                        Row row = rowIter.nextRow();
                        Node node = row.getNode();
                        if (node.hasProperty("redirect")) {
                            String redirect = node.getProperty("redirect").getString();
                            if (!redirect.isEmpty()) {
                                thankYouPages.add(redirect + " " + currentPage.getPath());
                            }
                        }
                        if (node.hasProperty("actionType")) {
                            String action = node.getProperty("actionType").getString();
                            if (action != null && (action.equals("girlscouts/components/form/actions/web-to-case") || action.equals("girlscouts/components/form/actions/web-to-lead"))) {
                                exceptionPages.add(currentPage.getPath());
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private QueryResult searchForms(ResourceResolver rr, Resource currentPageContent) {
        QueryResult result = null;
        try {
            Session session = rr.adaptTo(Session.class);
            QueryManager queryManager = session.getWorkspace().getQueryManager();
            Query sql2Query = queryManager.createQuery(formQuery.replace("{path}", currentPageContent.getPath()), "JCR-SQL2");
            return sql2Query.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void recurse(ResourceResolver rr, Page currentPage) {
        String path = currentPage.getPath();
        ValueMap properties = currentPage.getProperties();
        // If it is a page, then its resourceType must be one of the below
        //  foundation/components/page (/content/<council>)
        //  girlscouts/components/homepage
        // 	girlscouts/components/one-column-page
        // 	girlscouts/components/three-column-page
        //  girlscouts/components/placeholder-page
        String resourceType = properties.get("sling:resourceType", "");
        if (countedResourceTypes.contains(resourceType)) {
            allPages.add(path.trim());
            // Check forms for Thank You Page and web-to-case forms
            checkForms(rr, currentPage);
            for (Iterator<Page> iterator = currentPage.listChildren(); iterator.hasNext(); ) {
                recurse(rr, iterator.next());
            }
        }
    }

    private void processPage(ResourceResolver rr, String path, LiveRelationshipManager lrm) {
        Page page = rr.getResource(path).adaptTo(Page.class);
        String reason;
        ValueMap properties = page.getProperties();
        // Active Pages
        String lastReplicationAction = properties.get("cq:lastReplicationAction", "");
        if (!lastReplicationAction.equals("Activate")) {
            //noncountPages.add(path + " | NonActive");
            nonCountPagesMap.put(linkify(path), "NonActivate");
            return;
        }
        // Exception Pages
        if (exceptionPages.contains(path)) {
            //noncountPages.add(path + " | ExceptionPages");
            nonCountPagesMap.put(linkify(path), "ExceptionPages");
            return;
        }
        // Exception Directories
        for (int i = 0; i < exceptionDirectories.size(); i++) {
            String dir = exceptionDirectories.get(i);
            if (!path.equals(dir) && path.contains(dir)) {
                //noncountPages.add(path + " | ExceptionDirectories");
                nonCountPagesMap.put(linkify(path), "ExceptionDirectories");
                return;
            }
        }
        // Belongs to Template Pages
        if (nationalTemplatePages.contains(path)) {
            councilTemplatePages.add(linkify(path));
            return;
        }
        // Placeholder Page
        String resourceType = properties.get("sling:resourceType", "");
        if (resourceType.equals("girlscouts/components/placeholder-page")) {
            //noncountPages.add(path + " PlaceHolder"); // not *really* a page
            return;
        }
        // Redirect Page
        if (resourceType.endsWith("/redirect")) {
            //noncountPages.add(path + " Redirect"); // not *really* a page
            return;
        }
        // Thank You Pages
        for (int i = 0; i < thankYouPages.size(); i++) {
            String[] val = thankYouPages.get(i).split(" ");
            if (path.equals(val[0])) {
                nonCountPagesMap.put(linkify(path), "ThankYou " + val[1]);
                return;
            }
        }
        // If a page has jcr:mixinTypes of either LiveRelationship or LiveSync,
        // it's inherited from national templates
        try {
            Resource pageRes = page.adaptTo(Resource.class);
            LiveRelationship relationship = lrm.getLiveRelationship(pageRes, false);
            if (relationship != null) {
                String srcPath = relationship.getSourcePath();
                Resource srcRes = rr.resolve(srcPath);
                if (srcRes != null && !ResourceUtil.isNonExistingResource(srcRes)) {
                    if (srcPath != null && srcPath.startsWith("/content/girlscouts-template")) {
                        councilTemplatePages.add(linkify(path));
                        return;
                    } else {
                        if (srcPath != null && srcPath.startsWith("/content/webtocase")) {
                            nonCountPagesMap.put(linkify(path), "ExceptionPages");
                            return;
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        councilAddedPages.add(linkify(path));
    }

    private void overridePages() {
        for (String path : overrides) {
            if (nonCountPagesMap.containsKey(path)) {
                councilAddedPages.add(linkify(path));
                nonCountPagesMap.remove(path);
            }
        }
    }

    public ArrayList<String> getCouncilTemplatePages() {
        return councilTemplatePages;
    }

    public int getCouncilTemplatePagesSize() {
        return councilTemplatePages.size();
    }

    public ArrayList<String> getCouncilAddedPages() {
        return councilAddedPages;
    }

    public int getCouncilAddedPagesSize() {
        return councilAddedPages.size();
    }

    public ArrayList<String> getAllPages() {
        return allPages;
    }

    public ArrayList<String> getNationalTemplatePages() {
        return nationalTemplatePages;
    }

    public ArrayList<String> getCountedResourceTypes() {
        return countedResourceTypes;
    }

    public ArrayList<String> getExceptionPages() {
        return exceptionPages;
    }

    public ArrayList<String> getExceptionDirectories() {
        return exceptionDirectories;
    }

    public ArrayList<String> getThankYouPages() {
        return thankYouPages;
    }

    public ArrayList<String> getDefaultValues() {
        return defaultValues;
    }

    public ArrayList<String> getNonCountPages() {
        return nonCountPages;
    }

    public int getNonCountPagesSize() {
        return nonCountPages.size();
    }

    public String getCouncilName() {
        return councilName;
    }

    public String getCouncilTitle() {
        return councilTitle;
    }

    public int getTotalPageCount() {
        return councilTemplatePages.size() + councilAddedPages.size();
    }
}

