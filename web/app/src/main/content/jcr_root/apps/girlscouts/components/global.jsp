<%@page import="java.util.Set,
	java.util.Arrays,
	javax.servlet.jsp.PageContext,
	javax.servlet.jsp.JspWriter,
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
public static final int BREAKPOINT_MAX_LARGE = 1120;
public static final int BREAKPOINT_MAX_MEDIUM = 1024;
public static final int BREAKPOINT_MAX_SMALL = 640;

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

public String displayRendition(ResourceResolver rr, String imagePath, String renditionStr) {
	return displayRendition(rr, imagePath, renditionStr, null, -1);
}

public String displayRendition(ResourceResolver rr, String imagePath, String renditionStr, String additionalCss, int imageWidth) {
	if (renditionStr == null) return null;
	StringBuffer returnImage = new StringBuffer("<img ");
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

		String width = "";
		String height = "";
                if (imageWidth > 0) {
                    width = "width=\"" + imageWidth + "\" ";
                } else {
			if (isOriginal) {
			    String[] renditionParams = renditionStr.split("\\.");
			    if (renditionParams.length >= 4) {
				width = "width=\"" + renditionParams[2] + "\" ";
				height = "height=\"" + renditionParams[3] + "\" ";
			    }
			}
                }
		
		String css = "";
		if (additionalCss != null) {
			css = "class=\"" + additionalCss + "\" ";
		}

		returnImage.append(css);
		returnImage.append(title);
		returnImage.append(alt);
		returnImage.append(width);
		returnImage.append(height);
		returnImage.append(src);
	} catch (Exception e) {
	    log.error("Cannot include an image rendition: " + imagePath + "|" + renditionStr);
	}
	returnImage.append("/>");
        return returnImage.toString();
}
%>
