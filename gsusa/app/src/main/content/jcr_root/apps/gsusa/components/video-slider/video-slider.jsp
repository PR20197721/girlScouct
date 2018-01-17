<%@include file="/libs/foundation/global.jsp"%>
<cq:includeClientLib js="video" />
<%@page import="org.apache.sling.commons.json.*,
				java.io.*,
				java.util.regex.*,
				java.net.*,
				java.util.Random,
				com.day.cq.wcm.api.WCMMode" %>
<%@page session="false" %>

<%!
public String[] extract(String url){
	if (url.indexOf("youtu") != -1) { // Needs to be "youtu" to account for "youtu.be" links
        try {
			String ytid = extractYTId(url);
			String jsonOutput = readUrlFile("https://www.googleapis.com/youtube/v3/videos?part=snippet&id=" + ytid + "&key=AIzaSyBMs9oY1vT7DuNXAkGuKk2-5ScGMprtN-Y"); // Getting 403, check restrictions: AIzaSyD5AjIEx35bBXxpvwPghtCzjrFNAWuLj8I
            // Get a sample response here: https://developers.google.com/youtube/v3/docs/videos/list
			if (!"".equals(jsonOutput)) {
				JSONObject json = new JSONObject(jsonOutput);
				if (json != null) {
                    JSONObject snippet = json.getJSONArray("items").getJSONObject(0).getJSONObject("snippet");
					return new String[] {
                        "https://www.youtube.com/embed/" + ytid + "?enablejsapi=1&rel=0&autoplay=0&wmode=transparent", 
                        snippet.getJSONObject("thumbnails").getJSONObject("high").getString("url"), // default, medium
                        "youtube", 
                        generateId(),
                        snippet.getString("title")
                    };
				}
			}
		} catch (Exception e) {
			return new String[0];
		}
	} else if (url.indexOf("vimeo") != -1) {
		try {
			String vimeoId = extractVimeoId(url);
			String jsonOutput = readUrlFile("http://vimeo.com/api/v2/video/" + vimeoId + ".json");
			if (!"".equals(jsonOutput)) {
				JSONArray json = new JSONArray(jsonOutput);
				if (!json.isNull(0)) {
                    String id = generateId();
                    JSONObject snippet = json.getJSONObject(0);
					return new String[] {
                        "https://player.vimeo.com/video/"+ vimeoId + "?api=1&player_id=" + id, 
                        snippet.getString("thumbnail_large"), 
                        "vimeo", 
                        id, 
                        snippet.getString("title")
                    };
				}
			}
		} catch (Exception e) {
			return new String[0];
		}
	}
	return new String[0];
}

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

public String generateId() {
	Random rand=new Random();
	String possibleLetters = "abcdefghijklmnopqrstuvwxyz";
	StringBuilder sb = new StringBuilder(6);
	for(int i = 0; i < 6; i++)
	    sb.append(possibleLetters.charAt(rand.nextInt(possibleLetters.length())));
	return sb.toString();
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
String[] links = properties.get("links", String[].class);

JSONObject slickOptions = new JSONObject();
slickOptions.put("autoplay", properties.get("autoscroll", false));
slickOptions.put("autoplaySpeed", properties.get("timedelay", 2000));
   
JSONObject playerConfig = new JSONObject();
playerConfig.put("desktop", properties.get("videoConfigDesktop", "default")); // Values are: "default", "thumbnail", "link"
playerConfig.put("mobile", properties.get("videoConfigMobile", "default")); // Values are: "default", "thumbnail", "link"

if (links == null && WCMMode.fromRequest(request) == WCMMode.EDIT) {
   %><p> Video Slider - Please select at least one link to display</p><% 
} else {
	%><div class="video-slider-wrapper" slick-options='<%=slickOptions.toString()%>' player-config='<%=playerConfig.toString()%>'><%
        String[] urls = null;
        for (int i = 0; i < links.length; i++) {
            String[] split = links[i].split("\\|\\|\\|");
            String path = split.length >= 2 ? split[1] : "";
            if (resourceResolver.resolve(path).getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
                urls = extract(path);
                if (urls.length > 4) {
                    String title = split.length >= 1 ? split[0] : "";
                    title = !"".equals(title) ? title : urls[4];
                    %><div>
                        <div class="vid-slide-wrapper">
                            <iframe id="<%=urls[3]%>_<%=urls[2]%>" class="vid-player" data-src="<%=urls[0]%>" width="480" height="225" frameborder="0" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>
                            <a class="vid-placeholder" data-href="<%=path%>" target="_blank" title="<%=title%>">
                                <p><%=title%></p>
                                <img data-src="<%=urls[1]%>" />
                            </a>
                        </div>
                    </div><%
                } else {
                    %><div>*** Format not supported ***</div><%
                }
            } else {
                %><div><img src="<%=path%>" alt="Image slider <%=i%>" /></div><%
            }
        }
	%></div><%
}
%>
