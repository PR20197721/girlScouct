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
	java.util.regex.*,
	java.util.Random,
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

%>
