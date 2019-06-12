package org.girlscouts.gsusa.services;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.WCMMode;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.OptingServlet;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.girlscouts.common.osgi.component.GirlscoutsImagePathProvider;
import org.girlscouts.gsusa.access.ResolverAccessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SlingServlet(
        label = "GSUSA Dynamic Tag servlet", description = "Gets resourceType property for Article Tile and Dynamic Tag Carousel", paths = {},
        methods = { "GET" }, // Ignored if paths is set - Defaults to POST if not specified
        resourceTypes = { "gsusa/servlet/article-detail-carousel-content" }, // Ignored if
        // paths is set
        selectors = {}, // Ignored if paths is set
        extensions = { "html", "htm" }  // Ignored if paths is set
)
public class DynamicTagServlet extends SlingAllMethodsServlet implements OptingServlet {

    private static final Logger log = LoggerFactory.getLogger(DynamicTagServlet.class);

    @Reference
    private transient QueryBuilder queryBuilder;

    @Reference
    private transient GirlscoutsImagePathProvider gsImagePathProvider;

    @Reference
    private transient ResolverAccessService resolverAccessService;

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
            try {
                articleTile(sb, h.getPath(), request);
            }catch (Exception e){
                log.error("Error including article tile: ",e);
            }
            //sb.append("<cq:include path=\"article-tile\" resourceType=\"gsusa/components/article-tile\" />");
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
    public void articleTile(StringBuilder sb, String articlePathStr, SlingHttpServletRequest request){
        String articlePath = (String)request.getAttribute("articlePath");
        if (articlePath == null) {
            articlePath = request.getParameter("articlePath");
            if (articlePath.endsWith(".html")) {
                articlePath = articlePath.substring(0, articlePath.length() - 5);
            }
            if (!articlePath.startsWith("/content/gsusa")) {
                articlePath = "/content/gsusa" + articlePath;
            }
        }
        String linkTagAnchors = (String)request.getAttribute("linkTagAnchors");

        String tileTitle = "";
        String tileText = "";
        long priority = 0;
        String type = "";
        String videoLink = "";
        String externalLink = "";
        String editedDate = "";

        boolean playOnClick = false;
        boolean openInNewWindow = false;

        String divId = (String)request.getAttribute("tileModalDivId");

        String imageSrc = "";
        String image2xSrc = "";

        String hexColor = "FFFFFF";

        String rgba = "rgba(166, 206, 56, 0.8)";

        Value[] tags = null;

        String linkToArticle = "";

        try{

            Node node =   request.getResourceResolver().resolve(articlePath).adaptTo(Node.class);
            Node propNode = node.getNode("jcr:content");
            linkToArticle = node.getPath() + ".html";

            if(propNode.hasProperty("jcr:title"))
                tileTitle = propNode.getProperty("jcr:title").getString();

            if(propNode.hasProperty("shortTitle"))
                tileTitle = propNode.getProperty("shortTitle").getString();

            if(propNode.hasProperty("jcr:description"))
                tileText = propNode.getProperty("jcr:description").getString();

            if(propNode.hasProperty("type"))
                type = propNode.getProperty("type").getString();

            if(propNode.hasProperty("videoLink"))
                videoLink = propNode.getProperty("videoLink").getString();

            if(propNode.hasProperty("externalLink"))
                externalLink = propNode.getProperty("externalLink").getString();

            if(propNode.hasProperty("editedDate"))
                editedDate = propNode.getProperty("editedDate").getString();

            if(propNode.hasProperty("cq:tags"))
                tags = propNode.getProperty("cq:tags").getValues();

            if(propNode.hasProperty("playOnClick")){
                String isOn = propNode.getProperty("playOnClick").getString();
                if(isOn.equals("on"))
                    playOnClick = true;
            }

            if(propNode.hasProperty("openInNewWindow")){
                String openIn = propNode.getProperty("openInNewWindow").getString();
                if(openIn.equals("on"))
                    openInNewWindow = true;
            }

            if(propNode.hasNode("tileimage")){
                Node thumbnailNode = propNode.getNode("tileimage");
                imageSrc = thumbnailNode.getPath() + ".img.png";
                image2xSrc = thumbnailNode.getPath() + "2x.img.png";
            } else{
                Node imageNode = propNode.getNode("image");
                imageSrc = imageNode.getProperty("fileReference").getString();
                image2xSrc = gsImagePathProvider.getImagePath(imageSrc,"cq5dam.npd.tile@2x");
                imageSrc = gsImagePathProvider.getImagePath(imageSrc,"cq5dam.npd.tile");
            }

        } catch(Exception e){
            log.error("Error retrieving node properties: ", e);
        }

        if(!articlePath.isEmpty())
            articlePath = articlePath + ".html";

        if(tags != null && tags.length > 0){
            String primaryTagId = "";
            try{
                primaryTagId = tags[0].getString();
                String tagColor  = resolverAccessService.getColorFromTagNode(primaryTagId);
                if(tagColor != null ){
                    rgba = tagColor;
                }
            }catch(Exception e){
                log.error("Tag: " + primaryTagId + " is not found for article: " + articlePath, e);
            }
        }
        if(linkTagAnchors != null){
            linkToArticle += linkTagAnchors;
        }
        articleTileHtml(sb, type, playOnClick, videoLink, linkToArticle, openInNewWindow, tileTitle, tileText, imageSrc, image2xSrc, rgba, request.getResourceResolver(), externalLink);

    }
    private void articleTileHtml(StringBuilder sb, String type, boolean playOnClick, String videoLink, String linkToArticle, boolean openInNewWindow, String tileTitle, String tileText, String imageSrc, String image2xSrc, String rgba, ResourceResolver resourceResolver, String externalLink){
        sb.append("<div class=\"article-tile\">");
        sb.append("<section>");
        if(type.equals("video")){
            if(playOnClick){
                sb.append("<a class=\"video\" href=\"\" onclick=\"populateVideoIntoModal('gsusaHiddenModal','" + StringEscapeUtils.escapeHtml4(videoLink) + "','#FFFFFF')\" data-reveal-id=\"gsusaHiddenModal\">");
            } else {
                sb.append("<a class=\"video non-click\" href=\"" + linkToArticle + "\">");
            }
        } else if(type.equals("link")){
            if(openInNewWindow){
                sb.append("<a x-cq-linkchecker=\"valid\" href=\"" + genLink(resourceResolver, externalLink) + "\" target=\"_blank\">");
            } else {
                sb.append("<a x-cq-linkchecker=\"valid\" href=\"" + genLink(resourceResolver, externalLink) + "\">");
            }
        } else {
            sb.append("<a class=\"photo\" href=\"" + linkToArticle + "\">");
        }
        sb.append("<img src=\"" + imageSrc + "\" data-at2x=\"" + image2xSrc  + "\"/>");
        sb.append("<div class=\"text-content\" style=\"background: " + rgba + "\">");
        sb.append("<div class=\"text-wrapper\">");
        sb.append("<div class=\"text-inner\">");
        sb.append("<h3>");
        sb.append(tileTitle);
        sb.append("<p>" + tileText + "</p>");
        sb.append("</h3>");
        sb.append("</div>");
        sb.append("</div>");
        sb.append("</div>");
        sb.append("</a>");
        sb.append("</section>");
        sb.append("</div>");
    }
    public String genLink(ResourceResolver rr, String link) {
        // This is a Page resource but yet not end with ".html": append ".html"
        if (!link.contains(".html") && rr.resolve(link).getResourceType().equals("cq:Page")  ) {
            return link + ".html";
            // Well, do nothing
        } else {
            return link;
        }
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
