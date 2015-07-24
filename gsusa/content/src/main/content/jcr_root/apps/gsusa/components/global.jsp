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
	java.text.DateFormat" %>
<%!
private static Logger log = LoggerFactory.getLogger("gsusa.components.global");

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
%>
