<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/gsusa/components/global.jsp" %>
<cq:includeClientLib js="video" />
<%@page import="org.apache.sling.commons.json.*, 
                java.io.*, 
                java.util.regex.*,
                java.net.*, 
                org.apache.sling.api.request.RequestDispatcherOptions, 
                com.day.cq.wcm.api.components.IncludeOptions, 
                org.apache.sling.jcr.api.SlingRepository,
				java.util.ArrayList,
				java.util.List" %>
<%@page session="false" %>
<%!
public String extractYTId(String ytUrl) {
	String vId = null;
	Pattern pattern = Pattern.compile(".*(?:youtu\\.be\\/|v\\/|u\\/w\\/|embed\\/|watch\\?v=)([^#\\&\\?]*).*");
	Matcher matcher = pattern.matcher(ytUrl);
	if (matcher.find()){
		vId = matcher.group(1);
	}
	return vId;
}

public String extractVimeoId(String vimeoUrl) {
	String vId = null;
	Pattern pattern = Pattern.compile(".*(?:vimeo.com.*/)(\\d+)");
	Matcher matcher = pattern.matcher(vimeoUrl);
	if (matcher.find()){
		vId = matcher.group(1);
	}
	return vId;
}

public  String readUrlFile(String urlString) throws Exception {
	BufferedReader reader = null;
	try {
		URL url = new URL(urlString);
		reader = new BufferedReader(new InputStreamReader(url.openStream()));
		StringBuffer buffer = new StringBuffer();
		int read;
		char[] chars = new char[1024];
		while ((read = reader.read(chars)) != -1)
			buffer.append(chars, 0, read);
		return buffer.toString();
	} catch (java.net.UnknownHostException uhe) {
		return "";
	} finally {
		if (reader != null) {
			reader.close();
		}
	}
}

%>
<% 
    List<String> linksList = new ArrayList<String>();
	if(currentNode.hasNode("carouselList")){
        Node links = currentNode.getNode("carouselList");
        NodeIterator iter = links.getNodes();
        while(iter.hasNext()){
            Node linkNode = iter.nextNode();
            if(linkNode.hasProperty("title") && (linkNode.hasProperty("imagepath") || linkNode.hasProperty("link"))){
                String title = linkNode.getProperty("title").getString();
                String imagepath = linkNode.hasProperty("imagepath") ? linkNode.getProperty("imagepath").getString():"";

                String link = "";
                if(linkNode.hasProperty("link")){
                    link = linkNode.getProperty("link").getString();
                }

                String alt = "";
                if(linkNode.hasProperty("alt")){
                    alt = linkNode.getProperty("alt").getString();
                }

                String newWindow = "";
                if(linkNode.hasProperty("newWindow")){
                    newWindow = linkNode.getProperty("newWindow").getString();
                }

                String tempHidden = "";
                if(linkNode.hasProperty("tempHidden")){
                    tempHidden = linkNode.getProperty("tempHidden").getString();
                }

				String listItem = title + "|||" + alt + "|||" + link + "|||" + imagepath + "|||" + newWindow + "|||" + tempHidden;
                linksList.add(listItem);
            }
    
        }
	}else{%>
        <div data-emptytext="<%=component.getTitle()%>" class="cq-placeholder"></div>
     	<%}

	String[] carouselList = new String[0];
	if(!linksList.isEmpty()){
		carouselList = linksList.toArray(new String[0]);
    } else{
		carouselList = properties.get("carouselList", String[].class);
    }

	int numberOfImages = 0;
	if (carouselList != null) {
		numberOfImages = carouselList.length;
	}
	String title[] = new String[numberOfImages];
	String alt[] = new String[numberOfImages];
	String link[] = new String[numberOfImages];
    String sourceLink[] = new String[numberOfImages];
	String imagePath[] = new String[numberOfImages];
	String target[] = new String[numberOfImages];
	String openInNewWindow[] = new String[numberOfImages];
	String videoId[] = new String[numberOfImages];
	String videoThumbNail[] = new String[numberOfImages];
	boolean tempHidden[] = new boolean[numberOfImages];

	for (int i = 0; i < numberOfImages; i++) {
		String[] split = carouselList[i].split("\\|\\|\\|");
		tempHidden[i] = split.length >= 6 ? Boolean.parseBoolean(split[5]) : false;
		if (!tempHidden[i]) {
			title[i] = split.length >= 1 ? split[0] : "";
			alt[i] = split.length >= 2 ? split[1] : "";
			link[i] = split.length >= 3 ? split[2] : "";
			imagePath[i] = split.length >= 4 ? split[3] : "";
			target[i] = "";
            sourceLink[i] = link[i];
			if (split.length >= 5 && Boolean.parseBoolean(split[4])) {
				openInNewWindow[i] = "target=\"_blank\"";
			} else {
				openInNewWindow[i] = "";
			}

			//process the data from above, first check if the link is external
			Page linkPage = resourceResolver.resolve(link[i]).adaptTo(Page.class);
			if (linkPage != null && !link[i].contains(".html")) {
				link[i] += ".html";
			}
			
		}
	}

	String profilePath = properties.get("profilepath", "");
	int numberOfProfiles = properties.get("numprofile", 3);
	String test = "";
	
	
	
	
	if (profilePath != null && profilePath.length() > 0) {
		Page parent = resourceResolver.adaptTo(PageManager.class).getPage(profilePath);	
		Iterator<Page> profiles = parent.listChildren();
		ArrayList<String> profileImagePaths = new ArrayList<String>();
		ArrayList<String> profilePagePaths = new ArrayList<String>();	
		
		while (profiles.hasNext()) 
		{
			Page profile = profiles.next();
			Node profileNode = profile.adaptTo(Node.class);
			Node contentNode = profile.adaptTo(Node.class).getNode("jcr:content");
			Node imageNode  = contentNode.getNode("image");
			
			String profilePagePath = profile.getPath();		
			if (profilePagePath != null && !profilePagePath.isEmpty()) {
				profilePagePaths.add(profilePagePath + ".html");
			} else {
				profilePagePaths.add("");
			}
					
			String profileImagePath = imageNode.getProperty("fileReference").getValue().toString();	
			if (profileImagePath != null && !profileImagePath.isEmpty()) {
				profileImagePaths.add(profileImagePath);
			} else {
				profileImagePaths.add("");
			}
			
		}
	
		if (numberOfProfiles > profilePagePaths.size()) {
			//numberOfProfiles = profilePagePaths.size();
		}
		int totalProfileNumber = profilePagePaths.size();
	
		// get random ints
		Random random = new Random();
		ArrayList<Integer> randomProfiles = new ArrayList<Integer>();	
		test += "num: " + numberOfProfiles + " - ";
			
		int numprof = numberOfProfiles;
		while (numprof > 0) {
			int randomInt = random.nextInt(totalProfileNumber);
			
			if (!randomProfiles.contains(randomInt)) {
				randomProfiles.add(randomInt);
				test += randomInt + " ";
				numprof--;
			}		
			if (numprof > 15 || numprof > numberOfProfiles) break;
		}
	
		numberOfImages = profilePagePaths.size();
		
		//String source7 = properties.get("source7", "not_set");
		//String blogBgImage = properties.get("blogbgimage", "");
		//String hideZIPCode = properties.get("hideZIPCode", "false");
	    
	    JSONObject slickOptions = new JSONObject();
	    slickOptions.put("speed", properties.get("homecarouseltimedelay", 1000));
	    slickOptions.put("autoplay", properties.get("homecarouselautoscroll", false));
	    slickOptions.put("autoplaySpeed", properties.get("homecarouselautoplayspeed", 2000));
	    
	    JSONObject playerConfig = new JSONObject();
	    playerConfig.put("desktop", properties.get("videoConfigDesktop", "default")); // Values are: "default", "thumbnail", "link"
	    playerConfig.put("mobile", properties.get("videoConfigMobile", "default")); // Values are: "default", "thumbnail", "link"
	
		//passing this to another jsp
		//request.setAttribute("source7", source7);
%>

<div class="hero-feature">
	<ul class="main-slider" slick-options='<%=slickOptions.toString()%>' player-config='<%=playerConfig.toString()%>'><%
        for (int i = 0; i < numberOfImages; i++) { 
        	if (randomProfiles.contains(i)) {
            %><li id="tag_explore_main_<%=i%>"><%
                
                %><a href="<%= profilePagePaths.get(i) %>" title="title goes here" >
                    <img src="<%= profileImagePaths.get(i) %>" alt="alt goes here" class="slide-thumb tag_explore_image_hero_<%=i%>"/>
                </a><%
                
            %></li><%
        	}
		}

	%></ul>
</div> <% } %>

