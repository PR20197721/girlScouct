<%@page import="java.util.Set,
	java.util.Arrays,
	java.util.Iterator,
	javax.servlet.jsp.PageContext,
	javax.servlet.jsp.JspWriter,
	org.slf4j.Logger,
	org.slf4j.LoggerFactory,
	org.apache.sling.api.resource.ResourceResolver,
	com.day.cq.dam.commons.util.PrefixRenditionPicker,
	com.day.cq.dam.api.Asset,
	com.day.cq.dam.api.Rendition,
	com.day.cq.wcm.api.Page,
	com.day.cq.wcm.api.components.IncludeOptions,
	java.util.Calendar,
	java.util.Date,
	java.util.List,
	java.util.Map,
	java.util.HashMap,
	java.util.regex.*,
	java.util.Random,
	java.text.DateFormat,
	com.day.cq.search.Query,
	javax.jcr.Session,
	com.day.cq.search.result.SearchResult,
	com.day.cq.search.PredicateGroup,
	com.day.cq.search.QueryBuilder,
	com.day.cq.search.result.Hit,
	javax.jcr.query.RowIterator,
	org.girlscouts.common.osgi.component.GirlscoutsImagePathProvider" %>
<%
Page currentHomepage = currentPage.getAbsoluteParent(2);
ValueMap currentSite = currentHomepage.getContentResource().adaptTo(ValueMap.class);
GirlscoutsImagePathProvider gsImagePathProvider = sling.getService(GirlscoutsImagePathProvider.class);
%>
<%!
private static Logger log = LoggerFactory.getLogger("gsusa.components.global");

public String genLink(ResourceResolver rr, String link) {
    // This is a Page resource but yet not end with ".html": append ".html"
    if (!link.contains(".html") && rr.resolve(link).getResourceType().equals("cq:Page")  ) {
        return link + ".html";
    // Well, do nothing
    } else {
        return link;
    }
}

public String getImageRenditionSrc(ResourceResolver rr, String imagePath, String renditionStr) {
	if (renditionStr == null) return imagePath;
	StringBuffer returnImage = new StringBuffer("");
	try {
		Resource imgResource = rr.resolve(imagePath);
		ValueMap properties = imgResource.adaptTo(ValueMap.class);
		
		String fileReference = properties.get("fileReference", "");
		Asset asset;
		if (!fileReference.isEmpty()) {
			asset = rr.resolve(fileReference).adaptTo(Asset.class);
		} else {
			asset = imgResource.adaptTo(Asset.class);
		}

		boolean isOriginal = false;
		Rendition rendition = asset.getRendition(new PrefixRenditionPicker(renditionStr));
		if (rendition == null) {
			isOriginal = true;
			rendition = asset.getOriginal();
		}
		
		returnImage.append(rendition.getPath());
	} catch (Exception e) {
		log.error("Cannot include an image rendition: " + imagePath + "|" + renditionStr);
		return "";
	}
	return returnImage.toString();
}

public static final Pattern DIACRITICS_AND_FRIENDS = Pattern.compile("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");
public String linkifyString (String string) {
	return linkifyString (string, -1);
}
public String linkifyString (String string, int maxChars) {
	// This method takes in a string and turns it something that can be used in a link
	// e.g. linkifyString("Every Good Boy Does Fine.") --> "every-good-boy-does-fine"
	// e.g. linkifyString("What! Who's going to fix all the bugs?", 25) --> "what-whos-going-to-fix"
	try {
		Pattern pattern = Pattern.compile("[^0-9a-z_]", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(DIACRITICS_AND_FRIENDS.matcher(java.text.Normalizer.normalize(org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4(string.toLowerCase().trim()), java.text.Normalizer.Form.NFD)).replaceAll(""));
		StringBuffer returnStr = new StringBuffer();
		if (matcher.find()) {
			String[] segments = matcher.replaceAll("-").split("-");
			for (String segment: segments) {
				if (maxChars >0) {
					if (returnStr.length() > maxChars) {
						return returnStr.toString();	
					}
				}
				if (returnStr.length() > 0) {
					returnStr.append('-'); 
				}
				returnStr.append(segment);
			}
			return returnStr.toString();
		}
	} catch (Exception e) {
		// if this throws exception, return original string and die quietly
	}
	return string.toLowerCase().trim();
}

public boolean isCookiePage(Page currentPage) {
	String isCookiePage = currentPage.getProperties().get("isCookiePage", "derived");
	if ("true".equals(isCookiePage)) {
		return true;
	} else if ("false".equals(isCookiePage)) {
		return false;
	} else {
		Page parentPage = currentPage.getParent();
		return parentPage == null ? false : isCookiePage(parentPage);
	}
}

public boolean isContentHub(Page currentPage) {
	String isContentHub = currentPage.getProperties().get("isContentHub", "derived");
	if ("true".equals(isContentHub)) {
		return true;
	} else if ("false".equals(isContentHub)) {
		return false;
	} else {
		Page parentPage = currentPage.getParent();
		return parentPage == null ? false : isContentHub(parentPage);
	}
}

public String getResourceLocation(Resource r){
	String path = r.getPath();
	if(path.indexOf("jcr:content/content/middle/par") != -1){
		return "cq5dam.npd.middle.";
	} else if(path.indexOf("jcr:content/content/top/par") != -1){
		return "cq5dam.npd.top.";
	} else if(path.indexOf("jcr:content/content/left/par") != -1){
		return "cq5dam.npd.left.";
	} else if(path.indexOf("jcr:content/content/right/par") != -1){
		return "cq5dam.npd.right.";
	} else if(path.indexOf("jcr:content/content/hero/par") != -1){
		return "cq5dam.npd.hero.";
	} else {
		return "original";
	}
}

public String get2xPath(String path) {
	int lastIndex = path.lastIndexOf('.');
	if(lastIndex != -1 && path.indexOf("cq5dam.npd") != -1){
		return path.substring(0,lastIndex) + "@2x" + path.substring(lastIndex);
	}
	else{
		return path;
	}
}

public String genId() {
	Random rand=new Random();
	String possibleLetters = "0123456789abcdefghijklmnopqrstuvwxyz";
	StringBuilder sb = new StringBuilder(6);
	for(int i = 0; i < 6; i++)
	    sb.append(possibleLetters.charAt(rand.nextInt(possibleLetters.length())));
	return sb.toString();
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

public List<Hit> getAllArticles(int limit, ResourceResolver resourceResolver, QueryBuilder builder, String sortByPriority){
	Map<String, String> map = new HashMap<String, String>();
	map.put("type","cq:Page");


	map.put("property", "@jcr:content/cq:scaffolding");
    map.put("property.value", "/etc/scaffolding/gsusa/article");
    
	map.put("p.limit",limit + "");
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
	return sr.getHits();

}

public String getArticleCategoryPagePath(String[] tags, Session session) {
        // SELECT [jcr:path] FROM [cq:PageContent] WHERE CONTAINS([cq:tags], 'gsusa:content-hub/girls') AND CONTAINS([cq:tags], 'gsusa:content-hub/girls/stem') AND NOT [cq:scaffolding] = '/etc/scaffolding/gsusa/article'
        try {
                StringBuilder builder = new StringBuilder();
                builder.append("SELECT [jcr:path] FROM [cq:PageContent] WHERE");
                for (String tag : tags) {
                        builder.append(" CONTAINS([cq:tags], 'gsusa:content-hub/").append(tag).append("') AND");
                }
                if (tags.length == 0) {
                        builder.append(" AND");
                }
                builder.append(" NOT [cq:scaffolding] = '/etc/scaffolding/gsusa/article'");
 
                String queryStr = builder.toString();
                javax.jcr.query.Query query = session.getWorkspace().getQueryManager().createQuery(queryStr, javax.jcr.query.Query.JCR_SQL2);
                query.setLimit(1);
                RowIterator iter = query.execute().getRows();
 
                while (iter.hasNext()) {
                        String path = iter.nextRow().getPath();
                        if (path.endsWith("/jcr:content")) {
                                path = path.substring(0, path.length() - "/jcr:content".length());
                        }
                        return path;
                }
        } catch (Exception e) {
                e.printStackTrace();
        }
        return null;
}
%>
