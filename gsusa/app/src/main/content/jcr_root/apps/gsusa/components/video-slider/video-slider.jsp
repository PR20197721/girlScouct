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
		String ytid = extractYTId(url);
		return new String[]{"https://www.youtube.com/embed/" + ytid + "?enablejsapi=1&rel=0&autoplay=0&wmode=transparent" , "https://i1.ytimg.com/vi/" + ytid +"/mqdefault.jpg", "youtube", generateId(), ytid};
	} else if (url.indexOf("vimeo") != -1) {
		try{
			String vimeoId = extractVimeoId(url);
			String jsonOutput = readUrlFile("http://vimeo.com/api/v2/video/" + vimeoId + ".json");
			if (!"".equals(jsonOutput)) {
				JSONArray json = new JSONArray(jsonOutput);
				if (!json.isNull(0)) {
					String id = generateId();
					return new String[]{"https://player.vimeo.com/video/"+ vimeoId + "?api=1&player_id=" + id, (String)json.getJSONObject(0).getString("thumbnail_large"), "vimeo", id};
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
final int timedelay = properties.get("timedelay", 2000);
final boolean autoscroll = properties.get("autoscroll", false);
String[] links = properties.get("links", String[].class);

if (links == null && WCMMode.fromRequest(request) == WCMMode.EDIT) {
   %><p> Video Slider - Please select at least one link to display</p><% 
} else {
	%><div class="video-slider-wrapper" slick-options='{"autoplay":<%=autoscroll%>, "autoplaySpeed":<%=timedelay%>}'><%
        String[] urls = null;
        for (int i = 0; i < links.length; i++) {
            String[] split = links[i].split("\\|\\|\\|");
            String title = split.length >= 1 ? split[0] : "";
            String path = split.length >= 2 ? split[1] : "";
            if (resourceResolver.resolve(path).getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
                urls = extract(path);
                if (urls.length >= 4) {
                    %><div>
                        <%--<div class="show-for-small thumbnail">
                            <a href="<%= path %>" target="_blank" title="video thumbnail">
                                <img src="<%= urls[1] %>" />
                            </a>
                        </div>--%>
                        <div class="vid-slide-wrapper"><%
                            if (urls.length == 5) {
                                %><iframe id="<%=urls[3]%>_<%=urls[2]%>" class="vid-player" data-src="<%=urls[0]%>" title="<%=title%>" width="480" height="225" frameborder="0" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe><%
                            } else {
                                %><iframe id="<%=urls[3]%>_<%=urls[2]%>" class="vid-player" data-src="<%=urls[0]%>" width="480" height="225" frameborder="0" allowfullscreen webkitallowfullscreen mozallowfullscreen></iframe><%
                            }
                            %><a class="vid-placeholder" data-href="<%=path%>" target="_blank" title="video thumbnail">
                                <img src="<%=urls[1]%>" />
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
