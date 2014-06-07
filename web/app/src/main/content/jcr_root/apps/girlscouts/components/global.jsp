<%@page import="java.util.Set,
	java.util.Arrays,
	org.apache.sling.api.resource.ResourceResolver,
	com.day.cq.wcm.api.Page,
	com.day.cq.wcm.api.components.IncludeOptions" %>
<%
final string IMAGE_RENDITION_ATTR = "org.girlscouts.image.rendition";

Page homepage = currentPage.getAbsoluteParent(2);
ValueMap currentSite = homepage.getContentResource().adaptTo(ValueMap.class);
%>
<%!
public void setCssClasses(String tags, HttpServletRequest request) {
	IncludeOptions opt = IncludeOptions.getOptions(request, true);
	Set<String> classes = opt.getCssClassNames();
	classes.addAll(Arrays.asList(tags.split(" ")));
}

public void setHtmlTag(String tag, HttpServletRequest request) {
	IncludeOptions opt = IncludeOptions.getOptions(request, true);
	opt.setDecorationTagName(tag);
}

public String genLink(ResourceResolver rr, String link) {
    // This is a Page resource but yet not end with ".html": append ".html"
    if (rr.resolve(link).getResourceType().equals("cq:Page") && !link.endsWith(".html")) {
        return link + ".html";
    // Well, do nothing
    } else {
        return link;
    }
}

public String displayRendition(ResourceResolver rr, String imagePath, String rendition, HttpServletResponse response) {
	if (rendition == null) return;
	
	try {
		Asset asset = resourceResolver.resolve(imagePath).adaptTo(Asset.class);
		
		boolean isOriginal = false;
		Asset renditionAsset = asset.getRendition(new PrefixRenditionPicker(rendition));
		if (rendition == null) {
		    isOriginal = true;
		    renditionAsset = asset.getOriginal();
		}
		String src = "src=\"" + renditionAsset.getPath() + "\" ";
		
		String alt = properties.get("alt", "");
		if (!alt.isEmpty()) {
		    alt = "alt=\"" + alt + "\" ";
		}
		String title = properties.get("jcr:title", "");
		if (!title.isEmpty()) {
		    title= "title=\"" + title+ "\" ";
		}
		String width, height;
		if (isOriginal) {
		    String[] renditionParams = rendition.split("\.");
		    if (renditionParams.length >= 4) {
		        width = "width=\"" + renditionParams[2] + "\" ";
		        height = "height=\"" + renditionParams[3] + "\" ";
		    }
		}
		
		PrintWriter out = response.getWriter();
		out.print("<img ");
		out.print(title);
		out.print(alt);
		out.print(width);
		out.print(height);
		out.print(src);
		out.print("/>");
	} catch (Exception e) {
	    log.error("Canot include an image rendition: " + e.getMessage());
	}
}

%>
