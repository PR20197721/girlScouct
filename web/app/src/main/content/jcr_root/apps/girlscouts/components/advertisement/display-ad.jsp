<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>

<%
final String AD_ATTR = "apps.girlscouts.components.advertisement.currentAd";
Page currentAd = (Page)request.getAttribute(AD_ATTR);
if (currentAd != null) {
	String adName = currentAd.getProperties().get("jcr:title", "");
	String path = currentAd.getPath();
	String adLink = currentAd.getProperties().get("link", "");
	if (adLink != null && !adLink.isEmpty()) {
		adLink = genLink(resourceResolver, adLink);
	} else {
		adLink = genLink(resourceResolver, path);	
	}
	
	String displayPage1 = currentAd.getProperties().get("text/displayPage1", "");
	String displayPage2 = currentAd.getProperties().get("text/displayPage2", "");
	String displayPage3 = currentAd.getProperties().get("text/displayPage3", "");
	String displayPage4 = currentAd.getProperties().get("text/displayPage4", "");
	String displayPage5 = currentAd.getProperties().get("text/displayPage5", "");
	String displayPage6 = currentAd.getProperties().get("text/displayPage6", "");
	String displayPage7 = currentAd.getProperties().get("text/displayPage7", "");
	String displayPage8 = currentAd.getProperties().get("text/displayPage8", "");
	String displayPage9 = currentAd.getProperties().get("text/displayPage9", "");
	String displayPage10 = currentAd.getProperties().get("text/displayPage10", "");
	String displayPage11 = currentAd.getProperties().get("text/displayPage11", "");
	String displayPage12 = currentAd.getProperties().get("text/displayPage12", "");
	String displayPage13 = currentAd.getProperties().get("text/displayPage13", "");
	String displayPage14 = currentAd.getProperties().get("text/displayPage14", "");
	String displayPage15 = currentAd.getProperties().get("text/displayPage15", "");
	String displayPage16 = currentAd.getProperties().get("text/displayPage16", "");
	String displayPage17 = currentAd.getProperties().get("text/displayPage17", "");
	String displayPage18 = currentAd.getProperties().get("text/displayPage18", "");
	String displayPage19 = currentAd.getProperties().get("text/displayPage19", "");
	String displayPage20 = currentAd.getProperties().get("text/displayPage20", "");
	
	// loop through and add them to hashtable
	// if the count is at least one or above, then only show when there is a match

	boolean customURLSet = false;
	boolean displayThisAd = false;

	// has custom urls been set?	
	if (displayPage1.length() > 0
		|| displayPage2.length() > 0
		|| displayPage3.length() > 0
		|| displayPage4.length() > 0
		|| displayPage5.length() > 0
		|| displayPage6.length() > 0
		|| displayPage7.length() > 0
		|| displayPage8.length() > 0
		|| displayPage9.length() > 0
		|| displayPage10.length() > 0
		|| displayPage11.length() > 0
		|| displayPage12.length() > 0
		|| displayPage13.length() > 0
		|| displayPage14.length() > 0
		|| displayPage15.length() > 0
		|| displayPage16.length() > 0
		|| displayPage17.length() > 0
		|| displayPage18.length() > 0
		|| displayPage19.length() > 0
		|| displayPage20.length() > 0 
	) {
		
		customURLSet = true;	
	}

	// if so, then do we display that ad on this page?
	if (customURLSet) {
		if (displayPage1.equals(currentPage.getPath())
			|| displayPage2.equals(currentPage.getPath())
			|| displayPage3.equals(currentPage.getPath())
			|| displayPage4.equals(currentPage.getPath())
			|| displayPage5.equals(currentPage.getPath())
			|| displayPage6.equals(currentPage.getPath())
			|| displayPage7.equals(currentPage.getPath())
			|| displayPage8.equals(currentPage.getPath())
			|| displayPage9.equals(currentPage.getPath())
			|| displayPage10.equals(currentPage.getPath())
			|| displayPage11.equals(currentPage.getPath())
			|| displayPage12.equals(currentPage.getPath())
			|| displayPage13.equals(currentPage.getPath())
			|| displayPage14.equals(currentPage.getPath())
			|| displayPage15.equals(currentPage.getPath())
			|| displayPage16.equals(currentPage.getPath())
			|| displayPage17.equals(currentPage.getPath())
			|| displayPage18.equals(currentPage.getPath())
			|| displayPage19.equals(currentPage.getPath())
			|| displayPage20.equals(currentPage.getPath())
		) {
			displayThisAd = true;
		}
	}

	// display the ad only if customURL is NOT set or there is a match
	if(customURLSet == false || displayThisAd) {
%>
	<a href="<%=adLink%>"><cq:include path= "<%=path +"/jcr:content/image"%>" resourceType="girlscouts/components/image" /></a>
<%
	}

	request.removeAttribute(AD_ATTR);
} 
%>