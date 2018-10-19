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
	java.text.DateFormat,
	org.girlscouts.common.events.search.*,
	org.girlscouts.web.search.*,
	org.girlscouts.common.osgi.component.GirlscoutsImagePathProvider" %>
<%! GirlscoutsImagePathProvider gsImagePathProvider;%>
	

<%
Page homepage = currentPage.getAbsoluteParent(2);
ValueMap currentSite = homepage.getContentResource().adaptTo(ValueMap.class);
gsImagePathProvider = sling.getService(GirlscoutsImagePathProvider.class);
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
    if (!link.contains(".html") && rr.resolve(link).getResourceType().equals("cq:Page")  ) {
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
  String title,alt;
  try {
    Resource imgResource = rr.resolve(imagePath);
    ValueMap properties = imgResource.adaptTo(ValueMap.class);
    title = properties.get("imgtitle", "");
    if(title.isEmpty()){
    title = properties.get("jcr:title", "");
    }
    
    alt = properties.get("alt", "");
    } catch (Exception e) {
    log.error("Cannot include an image rendition: " + imagePath + "|" + renditionStr);
    return "";
  }


    return displayRendition(rr,imagePath,renditionStr,additionalCss,imageWidth,alt,title);
}
%>
<%!
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
<%!
public Asset getImageAsset(ResourceResolver rr, String imagePath){

	Resource imgResource = rr.resolve(imagePath);
	ValueMap properties = imgResource.adaptTo(ValueMap.class);
	if(properties == null) {
		return null;
	}
	
	String fileReference = properties.get("fileReference", "");
	Asset asset;
	if (!fileReference.isEmpty()) {
	    // fileRefence. Assuming this resource is an image component instance.
		asset = rr.resolve(fileReference).adaptTo(Asset.class);
	} else {
	    // fileRefence empty. Assuming this resource is a DAM asset.
	    asset = imgResource.adaptTo(Asset.class);
	}
	if(asset == null) {
		return null;
	}
	return asset;
}
%>
<%!
public String displayRendition(ResourceResolver rr, String imagePath, String renditionStr, String additionalCss, int imageWidth,String altString,String titleString) {
	if (renditionStr == null) return null;
	StringBuffer returnImage = new StringBuffer("<img ");
	try {
		Asset asset = getImageAsset(rr, imagePath);		
		String src = "src=\"" + gsImagePathProvider.getImagePath(asset,renditionStr) + "\" ";
	    if(altString==null || altString.isEmpty()){
			altString="image description unavailable";
		}
	    String alt = "alt=\"" + altString + "\" ";
	    String title = "";
	    if(titleString!=null && !titleString.isEmpty()){
	        title= "title=\"" + titleString + "\" ";
		}
		String width = "";
		String height = "";
        if (imageWidth > 0) {
            width = "width=\"" + imageWidth + "\" ";
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
		return "";
	}
	returnImage.append("/>");
    return returnImage.toString();
}
%>
<%!
	public String createHref(Page page) {
		String href = "<a href=" + page.getPath() + ".html" + ">"
				+ page.getTitle() + "</a>";
		return href;
}%>


<%!
public static String getDateTime(Date startDate, Date endDate,DateFormat dateFormat,DateFormat timeFormat,String dateStr){
	 Calendar cal2 = Calendar.getInstance();
     Calendar cal3 = Calendar.getInstance();
     cal2.setTime(startDate);
     cal3.setTime(endDate);
     boolean sameDay = cal2.get(Calendar.YEAR) == cal3.get(Calendar.YEAR) &&
                       cal2.get(Calendar.DAY_OF_YEAR) == cal3.get(Calendar.DAY_OF_YEAR);
     String endDateStr = dateFormat.format(endDate);
     String endTimeStr = timeFormat.format(endDate);
     if (!sameDay) {
 	      dateStr += " - " + endDateStr +", " + endTimeStr;
 	   }else
 	   {
 		   dateStr += " - " + endTimeStr;

 		}
	return dateStr;
}

public static String getDateTime(GSDateTime startDate, GSDateTime endDate, GSDateTimeFormatter dateFormat, GSDateTimeFormatter timeFormat,String dateStr,String timeZoneShortLabel){
    boolean sameDay = startDate.year() == endDate.year() &&
                      startDate.dayOfYear() == endDate.dayOfYear();
    String endDateStr = dateFormat.print(endDate);
    String endTimeStr = timeFormat.print(endDate);
    if (!sameDay) {
	      dateStr += " - " + endDateStr +", " + endTimeStr;
	   }else
	   {
		   dateStr += " - " + endTimeStr;

		}
	return dateStr + " " + timeZoneShortLabel;
}

public static String getDateTime(GSLocalDateTime startDate, GSLocalDateTime endDate, GSDateTimeFormatter dateFormat, GSDateTimeFormatter timeFormat,String dateStr,String timeZoneShortLabel){
    boolean sameDay = startDate.year() == endDate.year() &&
                      startDate.dayOfYear() == endDate.dayOfYear();
    String endDateStr = dateFormat.print(endDate);
    String endTimeStr = timeFormat.print(endDate);
    if (!sameDay) {
	      dateStr += " - " + endDateStr +", " + endTimeStr;
	   }else
	   {
		   dateStr += " - " + endTimeStr;

		}
	return dateStr + " " + timeZoneShortLabel;
}

public static boolean isSameDate(GSDateTime d1, GSDateTime d2) {
	return (d1.getYear() == d2.getYear() && d1.monthOfYear() == d2.monthOfYear() && d1.dayOfMonth() == d2.dayOfMonth());
}


%>
