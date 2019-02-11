<%@ page
	import="java.text.Format,
	java.text.ParseException,
	java.lang.Exception,
	java.util.Map,
	java.util.HashMap,
	java.util.TreeMap,
	java.util.List,
	java.util.Arrays,
	java.util.ArrayList,
	java.util.Iterator,
	java.util.Comparator,
	java.util.Date,
	java.util.Collections,
	java.util.LinkedHashMap,
	java.util.LinkedList,
	java.util.Set,
	com.day.cq.tagging.TagManager,
	com.day.cq.tagging.Tag,
	com.day.cq.dam.api.Asset,
	com.day.cq.commons.Externalizer,
	com.day.cq.wcm.api.WCMMode,
	org.girlscouts.vtk.utils.VtkUtil,
	org.girlscouts.vtk.models.User,
	javax.servlet.http.HttpSession,
	org.girlscouts.common.events.search.*,
	java.text.SimpleDateFormat,
	java.util.Calendar,
	org.apache.sling.api.SlingHttpServletRequest,
	org.apache.sling.api.scripting.SlingBindings,
	org.apache.commons.lang3.StringUtils,
	org.girlscouts.gsusa.components.rssfeed.RssFeedPathItem
	"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/global.jsp" %>

<cq:defineObjects />

<%!

	// must use ArrayList. simple array is not thread safe
	HashMap<String, HashMap<String,String>> feedPages = new HashMap<String,HashMap<String,String>>();
	HashMap<String, HashMap<String,String>> sortedFeedPages;
	ArrayList<String> feedDirectories = new ArrayList<String>();
	Externalizer externalizer;


	String feedTitle;
	String feedLandingPage;
	String feedDesc;
	String feedScaffoldingPath;
	int feedMaxItems = 0; 
	Node feedPaths;
	String rssFeedLink;
	Date latestFeedDate = null;
	int feedPageCount = 0;
	
	ArrayList<String> listToArray(String list) {
		String[] lists = list.split(",");
		ArrayList<String> arrayList = new ArrayList<String>();
		for (String str: lists) {
			arrayList.add(str.trim());
		}
		return arrayList;
	}


	String trimTopLevel(String path, int num) {
		
		String[] values = path.split("/");
		String newPath = "";
		
		if (values[0].isEmpty()) {
			num++;
		}
		
		if (num <= values.length) {
			for (int i = 0; i < values.length; i++) {
				if (i >= num) {
					newPath += "/" + values[i];
				}
			}
		} else {
			newPath = path;
		}
		return newPath;
	}

	
	String format(String label, String path, String page, String subdir) {
		String str = label.trim() + "|||" + path.trim() + "|||"
				+ page.trim() + "|||" + subdir.trim();		
		return str;
	}


	// parse out feed path
	void processPaths(List<RssFeedPathItem> feedPathItems) {
		for(RssFeedPathItem item : feedPathItems){
			if(item.isPage()){
				HashMap<String,String> thisFeedPage = new HashMap<String,String>();
				feedPages.put(item.getPath(), thisFeedPage);
			}
			if(item.isSubDir()){
				feedDirectories.add(item.getPath().trim());
			}
		}
	}
	

	// recurse into feed path and its children pages
	void recurse(ResourceResolver rr, String pagePath) {
		Page thisPage = rr.resolve(pagePath).adaptTo(Page.class);

		if(thisPage != null){
			for (Iterator<Page> iterator = thisPage.listChildren(); iterator.hasNext();) {
				String childPagePath = iterator.next().getPath();
				
				HashMap<String,String> thisFeedPage = new HashMap<String,String>();
				feedPages.put(childPagePath, thisFeedPage);
	
				recurse(rr, childPagePath);
			}
		}
	}

	void setLatestFeedDate(Date thisDate) {
		if(latestFeedDate == null) {
			latestFeedDate = thisDate;
		} else {
			if(thisDate.after(latestFeedDate)) {
				latestFeedDate = thisDate;
			}
		} 
	}


	// parse into a page, grab all RSS info
	void fetchPageData(SlingBindings bindings, SlingHttpServletRequest slingRequest, ResourceResolver resourceResolver, String pagePath, GirlScoutsImagePathProvider gsImagePathProvider) {

		// get page properties		
		Page thisPage = resourceResolver.resolve(pagePath).adaptTo(Page.class);
		String thisPath = thisPage.getPath();
		//out.print("path : " + thisPath + "<br>");
		ValueMap thisProperties = thisPage.getProperties();
		// get node
		Node thisNode = resourceResolver.getResource(pagePath).adaptTo(Node.class);
		// get prop of the node
		Node thisPropNode = null;
		try {
			thisPropNode = thisNode.getNode("jcr:content");
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		// check to see if this has matching cq:scaffolding path
		String thisScaffoldingPath = thisProperties.get("cq:scaffolding", "");
		//out.print("scaffolding path : " + thisScaffoldingPath + "<br>");
		if (feedScaffoldingPath.length() > 0 && feedScaffoldingPath.equals(thisScaffoldingPath)) {
		
			//out.print("scaffolding matched<br>");
			HashMap<String,String> thisFeedPageData = new HashMap<String,String>();

			// grab title
			String thisTitle = thisProperties.get("jcr:title", "");
			//out.print("title : " + thisTitle + "<br>");
			thisFeedPageData.put("title", thisTitle);
		
			// grab absolute external link
			String thisExternalUrl = externalizer.absoluteLink(slingRequest, slingRequest.getScheme(), thisPath) + ".html";
			//out.print("link : " + thisExternalUrl + "<br>");
			thisFeedPageData.put("externalUrl", thisExternalUrl);
			
			// grab published date
			String thisEditedDateStr = thisProperties.get("editedDate", "");
			//out.print("pub date : " + thisEditedDateStr + "<br>");
			thisFeedPageData.put("editedDate", thisEditedDateStr);
			try {
				// add date as seconds so that we can sort it later
				Date thisEditedDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse(thisEditedDateStr);
				thisFeedPageData.put("editedDateInMilliseconds",String.valueOf(thisEditedDate.getTime()));
				setLatestFeedDate(thisEditedDate);
			} catch (Exception e) {
				//out.print("could not parse date string\n\n" + e);
 				e.printStackTrace();
			}

			// grab tags for RSS categories
			String[] thisTags = thisProperties.get("cq:tags", String[].class);
			//out.print("tags : " + thisTags + "<br>");
			thisFeedPageData.put("tags", StringUtils.join(thisTags,","));

			// grab image link
			String thisArticleType = "";
			String thisVideoLink = "";
			String thisImageSrc = "";
			String thisImgAlt = "";
			String thisImageFinalSource = "";
			String thisImageFinalSource2X = "";
			String thisImageFinalSourceExternalUrl = ""; 
			String thisImageFinalSource2XExternalUrl = ""; 
	
			try {
				if(thisPropNode.hasProperty("type")) {
 			      	thisArticleType = thisPropNode.getProperty("type").getString();
				}
  			    if(thisPropNode.hasProperty("videoLink")) {
  					thisVideoLink = thisPropNode.getProperty("videoLink").getString();
 				}
   				if (thisPropNode.hasProperty("imgAlt")) {
  					thisImgAlt = thisPropNode.getProperty("imgAlt").getString();
  				}
        
				Node thisImageNode = thisPropNode.getNode("image");
 			    thisImageSrc = thisImageNode.getProperty("fileReference").getString();
			} catch(Exception e){
 				e.printStackTrace();
			}

			if(thisArticleType.equals("video")){
				thisFeedPageData.put("videoLink", thisVideoLink);
 			} else {
				thisImageFinalSource = gsImagePathProvider.getImagePath(thisImageSrc,"cq5dam.npd.hubHero");
				thisImageFinalSourceExternalUrl = externalizer.absoluteLink(slingRequest, slingRequest.getScheme(), thisImageFinalSource);
				thisImageFinalSource2X = gsImagePathProvider.getImagePath(thisImageSrc,"cq5dam.npd.hubHero@2x");
				thisImageFinalSource2XExternalUrl = externalizer.absoluteLink(slingRequest, slingRequest.getScheme(), thisImageFinalSource2X);
				//out.print("image link : " + thisImageFinalSourceExternalUrl + "<br>");
				//out.print("image link @2X : " + thisImageFinalSource2XExternalUrl + "<br>");
				thisFeedPageData.put("imgSrc", thisImageFinalSourceExternalUrl);
				thisFeedPageData.put("imgSrc2X", thisImageFinalSource2XExternalUrl);
				thisFeedPageData.put("imgAlt", thisImgAlt);
   			}
			
			// grab description
			ValueMap thisArticleTextProp = resourceResolver.resolve(thisPath + "/jcr:content/content/middle/par/article_text").adaptTo(ValueMap.class);
			String thisDesc = thisArticleTextProp.get("text", "");
			//out.print("desc : " + thisDesc.substring(0, 20) + "...<br>");
			thisFeedPageData.put("desc", thisDesc);
			
			// description with image link on top
			String thisDescWithImg = "<img style=\"max-width:750px;\" src=\"" + thisImageFinalSourceExternalUrl + "\" alt=\""+ thisImgAlt + "\" />" + thisDesc;
			//out.print("desc with img: " + thisDescWithImg.substring(0, 500) + "...<br>");
			thisFeedPageData.put("descWithImage", thisDescWithImg);
			
			// update the data back to array
			feedPages.replace(pagePath, thisFeedPageData);
		}		
	
	}
 
		
	HashMap sortRSSFeedByDate(HashMap map) { 
		List list = new LinkedList(map.entrySet());

		// Defined Custom Comparator here
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				HashMap h1 = (HashMap)((Map.Entry) (o1)).getValue();
				HashMap h2 = (HashMap)((Map.Entry) (o2)).getValue();
				return ((Comparable) h2.get("editedDateInMilliseconds")).compareTo(h1.get("editedDateInMilliseconds"));
			}
		});

		// Here I am copying the sorted list in HashMap
		// using LinkedHashMap to preserve the insertion order
		HashMap sortedHashMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		} 
		return sortedHashMap;
	}
	
%>



<%
	if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
		out.print("<h3>RSS Feed</h3>");
	}
 
	// grab values set from dialog
	feedTitle = properties.get("feedTitle", "");
	feedLandingPage = properties.get("feedLandingPage", "");
	feedDesc = properties.get("feedDesc", "");
	feedScaffoldingPath = properties.get("feedScaffoldingType", "").trim();
	feedMaxItems = ((int)properties.get("feedMaxItems", 9999));
	
   	Resource feedPathsResource = resource.getChild("feedItems");
   	List<RssFeedPathItem> feedPathItems = new ArrayList<>();
	if (feedPathsResource != null && !feedPathsResource.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
		Iterator<Resource> items = feedPathsResource.listChildren(); 
		if(items != null){
			while(items.hasNext()){
				feedPathItems.add(RssFeedPathItem.fromNode(items.next().adaptTo(Node.class)));
			}
		}
	}
	externalizer = bindings.getSling().getService(Externalizer.class);
	rssFeedLink = externalizer.absoluteLink(slingRequest, slingRequest.getScheme(), currentNode.getPath()) + ".html";

	if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
		out.print("RSS Link : " + rssFeedLink + "<br>");
		out.print("Title : " + feedTitle + "<br>");
		out.print("Landing Page : " + feedLandingPage + "<br>");
		out.print("Desc : " + feedDesc + "<br>");
		out.print("Scaffolding Path : " + feedDesc + "<br>");
		if(feedMaxItems < 0) {
			out.print("Max Items : All<br>");
		} else {
			out.print("Max Items : " + feedMaxItems + "<br>");
		}
		out.print("<br>");
	}


	// take the multifield values and process them to result feedPages and feedDirectories
	if (feedPathItems.size() < 1) {

		if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
			out.print("Feed Paths not defined. Please edit this component and add feed paths.");
		}
	
	} else {
		
		processPaths(feedPathItems);
      
		for (String dir : feedDirectories ) {
			//out.print("dir : " + dir + "<br>");
			recurse(resourceResolver, dir);
		}

		// process and get page data
		for (Map.Entry<String, HashMap<String,String>> feedPage : feedPages.entrySet()) {
			String feedPageKey = feedPage.getKey();
			//out.print("fetching data : " + feedPageKey + "<br>");
			fetchPageData(bindings, slingRequest, resourceResolver, feedPageKey, gsImagePathProvider);
		}

		// need to create a copy, and use it to iterate, otherwise, deleting any items while processing it will cause ConcurrentModificationException
		HashMap<String, HashMap<String,String>> feedPagesCopy = new HashMap<String,HashMap<String,String>>();
		feedPagesCopy.putAll(feedPages);
		// delete ones that are null 
		for (Map.Entry<String, HashMap<String,String>> feedPage : feedPagesCopy.entrySet()) {
			String feedPageKey = feedPage.getKey();
			HashMap feedPageValue = feedPage.getValue();
			if (feedPageValue.get("editedDate") == null) {
				feedPages.remove(feedPageKey); 
			}
		}

		// here are all pages fetched if we need to display them
		//out.print("--processed--<br>");	
		/*
		for (Map.Entry<String, HashMap<String,String>> feedPage : feedPages.entrySet()) {
			String feedPageKey = feedPage.getKey();
			HashMap feedPageValue = feedPage.getValue();
			out.print("page : " + feedPageKey + " - " + feedPageValue.get("tags") + "<br>");
		}
		*/

		// sort the pages
		sortedFeedPages = sortRSSFeedByDate(feedPages);
		
		// only in author mode - list the pages
		if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
			out.print("-- Feed Pages --<br>");	
			int numItemsProcessed = 0;
			for (Map.Entry<String, HashMap<String,String>> feedPage : sortedFeedPages.entrySet()) {
				if(numItemsProcessed >= feedMaxItems) break;
				String feedPageKey = feedPage.getKey();
				HashMap feedPageValue = feedPage.getValue();
				String thisDesc = (String)feedPageValue.get("desc");
				out.print("<span style='font-size:12px;'>" + feedPageValue.get("editedDate") + " - " + feedPageKey + "</span><br>");
				numItemsProcessed++;		
			}
		}
	
		
		// only NOT in author mode - output RSS feed
		if (WCMMode.fromRequest(request) != WCMMode.EDIT) {
	
			String rssOutput;
			SimpleDateFormat rssDateFormatter = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss Z");
			
			
			rssOutput = "<?xml version=\"1.0\"?>\r\n";
			rssOutput = rssOutput + "<rss version=\"2.0\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\r\n";
			rssOutput = rssOutput + "<channel>\r\n";
			rssOutput = rssOutput + "    <title><![CDATA[" + feedTitle + "]]></title>\r\n";
			rssOutput = rssOutput + "    <link>" + feedLandingPage + "</link>\r\n";
			rssOutput = rssOutput + "    <description><![CDATA[" + feedDesc + "]]></description>\r\n";
			rssOutput = rssOutput + "    <pubDate>" + rssDateFormatter.format(latestFeedDate) + "</pubDate>\r\n";
	
			int numItemsProcessed = 0;
			for (Map.Entry<String, HashMap<String,String>> feedPage : sortedFeedPages.entrySet()) {

				if(numItemsProcessed >= feedMaxItems) break;
			
				String feedPageKey = feedPage.getKey();
				HashMap feedPageValue = feedPage.getValue();
				
				rssOutput = rssOutput + "    <item>\r\n";
				rssOutput = rssOutput + "		<title><![CDATA[" + feedPageValue.get("title") + "]]></title>\r\n";
				rssOutput = rssOutput + "		<link>" + feedPageValue.get("externalUrl") + "</link>\r\n";
				String thisEditedDateStr = (String)feedPageValue.get("editedDate");	
				try {
					Date thisEditedDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse(thisEditedDateStr);
					rssOutput = rssOutput + "		<pubDate>" + rssDateFormatter.format(thisEditedDate) + "</pubDate>\r\n";
				} catch (Exception e) {
					//out.print("could not parse date string\n\n" + e);
				}

				ArrayList<String> thisTags = listToArray((String)feedPageValue.get("tags"));
				if(thisTags != null){
					for(String thisCategory : thisTags) {
						rssOutput = rssOutput + "		<category><![CDATA[" + thisCategory + "]]></category>\r\n";
					}
				}
				//out.println("		<dc:creator><![CDATA[]]></dc:creator>");
				rssOutput = rssOutput + "		<description><![CDATA[" + feedPageValue.get("descWithImage") + "]]></description>\r\n";
				rssOutput = rssOutput + "    </item>\r\n";
				
				numItemsProcessed++;		
			}
	
			rssOutput = rssOutput + "  </channel>\r\n";
			rssOutput = rssOutput + "</rss>\r\n";
			out.print(rssOutput);
 		}
	}


%>

