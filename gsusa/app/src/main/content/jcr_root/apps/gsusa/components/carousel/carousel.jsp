<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/gsusa/components/global.jsp" %>
<cq:includeClientLib js="video" />
<%@page import="org.apache.sling.commons.json.*, 
                java.io.*, 
                java.util.regex.*,
                java.net.*, 
                org.apache.sling.api.request.RequestDispatcherOptions, 
                com.day.cq.wcm.api.components.IncludeOptions, 
                org.apache.sling.jcr.api.SlingRepository" %>
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
	final String[] carouselList = properties.get("carouselList", String[].class);
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
			
			try{

				//now check if the link is youtube/vimeo
				if (link[i].indexOf("youtu") != -1) { // Needs to be "youtu" to account for "youtu.be" links
					String ytid = extractYTId(link[i]);
					videoId[i] = ytid;
                    String jsonOutput = readUrlFile("https://www.googleapis.com/youtube/v3/videos?part=snippet&id=" + ytid + "&key=AIzaSyBMs9oY1vT7DuNXAkGuKk2-5ScGMprtN-Y"); // Getting 403, check restrictions: AIzaSyD5AjIEx35bBXxpvwPghtCzjrFNAWuLj8I
                    // Get a sample response here: https://developers.google.com/youtube/v3/docs/videos/list					
                    if (!"".equals(jsonOutput)) {
						JSONObject json = new JSONObject(jsonOutput);
				        if (json != null) {
                            JSONObject snippet = json.getJSONArray("items").getJSONObject(0).getJSONObject("snippet");
							videoThumbNail[i] = snippet.getJSONObject("thumbnails").getJSONObject("high").getString("url"); // default, medium
                            title[i] = !"".equals(title[i]) ? title[i] : snippet.getString("title");
						}
					}
					link[i] = "https://www.youtube.com/embed/" + ytid + "?enablejsapi=1&rel=0&autoplay=0&wmode=transparent";
				} else if (link[i].indexOf("vimeo") != -1) {
					String vimeoId = extractVimeoId(link[i]);
					videoId[i] = vimeoId;
					String jsonOutput = readUrlFile("http://vimeo.com/api/v2/video/" + vimeoId + ".json");
					if (!"".equals(jsonOutput)) {
						JSONArray json = new JSONArray(jsonOutput);
						if (!json.isNull(0)) {
                            JSONObject snippet = json.getJSONObject(0);
							videoThumbNail[i] = snippet.getString("thumbnail_large");
                            title[i] = !"".equals(title[i]) ? title[i] : snippet.getString("title");
						}
					}
					link[i] = "https://player.vimeo.com/video/" + vimeoId + "?api=1&player_id=" + "vimeoPlayer" + i ;
				} else {
					videoThumbNail[i] = "not supported";
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}

	String source7 = properties.get("source7", "not_set");
	String blogBgImage = properties.get("blogbgimage", "");
	String hideZIPCode = properties.get("hideZIPCode", "false");
    
    JSONObject slickOptions = new JSONObject();
    slickOptions.put("speed", properties.get("homecarouseltimedelay", 1000));
    slickOptions.put("autoplay", properties.get("homecarouselautoscroll", false));
    slickOptions.put("autoplaySpeed", properties.get("homecarouselautoplayspeed", 2000));
    
    JSONObject playerConfig = new JSONObject();
    playerConfig.put("thumbnailDesktop", properties.get("thumbnailDesktop", false));
    playerConfig.put("thumbnailMobile", properties.get("thumbnailMobile", false));
    playerConfig.put("linkDesktop", properties.get("linkDesktop", false));
    playerConfig.put("linkMobile", properties.get("linkMobile", false));

	//passing this to another jsp
	request.setAttribute("source7", source7);
%>

<div class="hero-feature">
	<ul class="main-slider" slick-options='<%=slickOptions.toString()%>' player-config='<%=playerConfig.toString()%>'><%
        for (int i = 0; i < numberOfImages; i++) { 
			if (!tempHidden[i]) {
                %><li id="tag_explore_main_<%=i%>"><%
                    if (link[i].indexOf("https://www.youtube.com") != -1) { 
                        %><div class="videoWrapper">
                            <iframe id="youtubePlayer<%=i%>" class="vid-player" data-src="<%=link[i]%>" width="100%" height="560" frameborder="0" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>
                            <a class="vid-placeholder" data-href="<%=sourceLink[i]%>" target="_blank" title="<%=title[i]%>">
                                <p><%=title[i]%></p>
                                <img data-src="<%=videoThumbNail[i]%>" />
                            </a>
                        </div><% 
                    } else if (link[i].indexOf("https://player.vimeo.com/video/") != -1) {
                        %><div class="videoWrapper">
                            <iframe id="vimeoPlayer<%=i%>" class="vid-player" data-src="<%=link[i]%>" width="100%" height="560" frameborder="0" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>
                            <a class="vid-placeholder" data-href="<%=sourceLink[i]%>" target="_blank" title="<%=title[i]%>">
                                <p><%=title[i]%></p>
                                <img data-src="<%=videoThumbNail[i]%>" />
                            </a>
                        </div><%
                    } else {
                        %><a href="<%=link[i]%>" title="<%=title[i]%>" <%=openInNewWindow[i]%>>
                            <img src="<%= getImageRenditionSrc(resourceResolver, imagePath[i], "cq5dam.npd.top.")%>" alt="<%=alt[i]%>" class="slide-thumb tag_explore_image_hero_<%=i%>"/>
                        </a><%
                    }
                %></li><%
            } 
		}
    %></ul><%
    if (hideZIPCode == "false") {
        %><cq:include path="zip-council" resourceType="gsusa/components/zip-council" /><%
    }
%></div>

<%request.removeAttribute("source7");%>
