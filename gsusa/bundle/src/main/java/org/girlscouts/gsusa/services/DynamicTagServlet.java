package org.girlscouts.gsusa.services;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.WCMMode;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.OptingServlet;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SlingServlet(
        label = "GSUSA ResourceType servlet", description = "Gets resourceType property for Article Tile and Dynamic Tag Carousel", paths = {"/content/gsusa/servlets/DynamicTagServlet"},
        methods = { "GET" }, // Ignored if paths is set - Defaults to POST if not specified
        resourceTypes = { "" }, // Ignored if
        // paths is set
        selectors = {}, // Ignored if paths is set
        extensions = { "html", "htm" }  // Ignored if paths is set
)
public class DynamicTagServlet extends SlingAllMethodsServlet implements OptingServlet {

    private static final Logger log = LoggerFactory.getLogger(DynamicTagServlet.class);

    @Reference
    private transient QueryBuilder queryBuilder;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        StringBuilder sb = new StringBuilder();
        String [] selectors = request.getRequestPathInfo().getSelectors();
        boolean editMode = false;
        String tag = selectors.length >= 1 ? selectors[0] : "articles";
        if(!tag.equals("articles"))
            request.setAttribute("linkTagAnchors", "#" + tag);

        if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
            editMode = true;
        }

        int num = 20;
        try {
            if (selectors.length >= 2) {
                num = Integer.parseInt(selectors[1]);
            }
        } catch (java.lang.NumberFormatException e) {}

        // If num is less than zero, it means it should be sorted by priority.
        String sortByPriority = "false";
        if (num < 0) {
            sortByPriority = "true";
            num = num * -1;
        }

        List<String> tagIds = new ArrayList<String>();
        for (String singleTag : tag.split("\\|")) {
            tagIds.add("gsusa:content-hub/" + singleTag);
        }
        List<Hit> hits = getTaggedArticles(tagIds, num, request.getResourceResolver(), queryBuilder, sortByPriority);
        sb.append("<div class=\"article-detail-carousel\">");
        sb.append(" <div class=\"article-slider\">");

        for (Hit h : hits){
            try {
                request.setAttribute("articlePath", h.getPath());
            }catch (Exception e){
                log.error("Error setting request attribute articlePath: ",e);
            }
            sb.append("<div>");
            sb.append("<cq:include path=\"article-tile\" resourceType=\"gsusa/components/article-tile\" />");
            sb.append("</div>");
        }
        sb.append("<div>");
            sb.append("<div class=\"article-tile last\">");
                sb.append("<section><a>See More</a></section>");
            sb.append("</div>");

        sb.append("</div>");
        sb.append("</div>");
        sb.append("</div>");


        response.setHeader("Content-Type", "text/html");
        response.getWriter().write(sb.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }
    public List<Hit> getTaggedArticles(List<String> tagIds, int limit, ResourceResolver resourceResolver, QueryBuilder builder, String sortByPriority){
        SearchResult sr = getArticlesWithPaging(tagIds, limit, resourceResolver, builder, sortByPriority, 0);
        return sr.getHits();
    }
    public SearchResult getArticlesWithPaging(List<String> tagIds, int limit, ResourceResolver resourceResolver, QueryBuilder builder, String sortByPriority, int offset){
        Map<String, String> map = new HashMap<String, String>();
        map.put("type","cq:Page");

        int i = 1;


        map.put("1_property", "@jcr:content/cq:scaffolding");
        map.put("1_property.value", "/etc/scaffolding/gsusa/article");
        map.put("property","jcr:content/cq:tags");
        map.put("property.and","true");

        for(String tag: tagIds){
            map.put("property."+ i +"_value",tag);
            i++;
        }
        map.put("p.limit",limit + "");
        map.put("p.offset", offset + "");
        if(sortByPriority.equals("true")){
            map.put("orderby","@jcr:content/articlePriority");
            map.put("orderby.sort","desc");
            map.put("2_orderby","@jcr:content/editedDate");
            map.put("2_orderby.sort","desc");
        } else {
            map.put("orderby","@jcr:content/editedDate");
            map.put("orderby.sort","desc");
        }

        Query query = builder.createQuery(PredicateGroup.create(map), resourceResolver.adaptTo(Session.class));
        SearchResult sr = query.getResult();
        return sr;

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
