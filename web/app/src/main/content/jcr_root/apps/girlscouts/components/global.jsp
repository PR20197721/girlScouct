<%@page import="java.util.Set,
	java.util.Arrays,
	java.io.PrintWriter,
	org.slf4j.Logger,
	org.slf4j.LoggerFactory,
	org.apache.sling.api.resource.ResourceResolver,
	com.day.cq.dam.commons.util.PrefixRenditionPicker,
	com.day.cq.dam.api.Asset,
	com.day.cq.dam.api.Rendition,
	com.day.cq.wcm.api.Page,
	com.day.cq.wcm.api.components.IncludeOptions" %>
<%
Page homepage = currentPage.getAbsoluteParent(2);
ValueMap currentSite = homepage.getContentResource().adaptTo(ValueMap.class);
%>
<%!
private static Logger log = LoggerFactory.getLogger("girlscouts.components.global");
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

public void displayRendition(ResourceResolver rr, String imagePath, String renditionStr, HttpServletResponse response) {
	displayRendition(rr, imagePath, renditionStr, response, null);
}

public void displayRendition(ResourceResolver rr, String imagePath, String renditionStr, HttpServletResponse response, String additionalCss) {
	if (renditionStr == null) return;
	
	try {
		Resource imgResource = rr.resolve(imagePath);
		ValueMap properties = imgResource.adaptTo(ValueMap.class);
		
		String fileReference = properties.get("fileReference", "");
		Asset asset;
		if (!fileReference.isEmpty()) {
		    // fileRefence. Assuming this resource is an image component instance.
			asset = rr.resolve(fileReference).adaptTo(Asset.class);
		} else {
		    // fileRefence empty. Assuming this resource is a DAM asset.
		    asset = imgResource.adaptTo(Asset.class);
		}
		
		boolean isOriginal = false;
		Rendition rendition = asset.getRendition(new PrefixRenditionPicker(renditionStr));
		if (rendition == null) {
		    isOriginal = true;
		    rendition = asset.getOriginal();
		}
		String src = "src=\"" + rendition.getPath() + "\" ";
		
		String alt = properties.get("alt", "");
		if (!alt.isEmpty()) {
		    alt = "alt=\"" + alt + "\" ";
		}
		String title = properties.get("jcr:title", "");
		if (!title.isEmpty()) {
		    title= "title=\"" + title+ "\" ";
		}

        PrintWriter out = response.getWriter();

		String width = "";
		String height = "";
		if (isOriginal) {
		    String[] renditionParams = renditionStr.split("\\.");
		    if (renditionParams.length >= 4) {
		        width = "width=\"" + renditionParams[2] + "\" ";
		        height = "height=\"" + renditionParams[3] + "\" ";
		    }
		}
		
		String css = "";
		if (additionalCss != null) {
			css = "class=\"" + additionalCss + "\" ";
		}

		out.print("<img ");
		out.print(css);
		out.print(title);
		out.print(alt);
		out.print(width);
		out.print(height);
		out.print(src);
		out.print("/>");
	} catch (Exception e) {
	    log.error("Cannot include an image rendition: " + imagePath + "|" + renditionStr);
	}
}

%>
