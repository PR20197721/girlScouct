<%@include file="/libs/foundation/global.jsp"%>
<%@page import="org.apache.sling.commons.json.*,
				java.io.*,
				java.util.regex.*,
				java.net.*,
				org.apache.sling.commons.json.*" %>
<%@page session="false" %>

<%!
public String extract(String url){
	if (url.indexOf("youtube") != -1) {
		return "https://i1.ytimg.com/vi/" + extractYTId(url) +"/mqdefault.jpg";
	} else if (url.indexOf("vimeo") != -1) {
		try{
			String vimeoId = extractVimeoId(url);
			String jsonOutput = readUrlFile("http://vimeo.com/api/v2/video/" + vimeoId + ".json");
			if (!"".equals(jsonOutput)) {
				JSONArray json = new JSONArray(jsonOutput);
				if (!json.isNull(0)) {
					return (String)json.getJSONObject(0).getString("thumbnail_large");
				}
			}
		} catch (Exception e) {
			return "";
		}
	}
	return "";
}

public String extractYTId(String ytUrl) {
	String vId = null;
	Pattern pattern = Pattern.compile(".*(?:youtu.be\\/|v\\/|u\\/\\w\\/|embed\\/|watch\\?v=)([^#\\&\\?]*).*");
	Matcher matcher = pattern.matcher(ytUrl);
	if (matcher.matches()){
		vId = matcher.group(1);
	}
	return vId;
}

public String extractVimeoId(String vimeoUrl) {
	String vId = null;
	Pattern pattern = Pattern.compile(".*(?:vimeo.com.*/)(\\d+)");
	Matcher matcher = pattern.matcher(vimeoUrl);
	if (matcher.matches()){
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

<div class="video-slider-wrapper">
<%
	String[] links = properties.get("links",String[].class);
	String alt = "";
	if(links != null && links.length > 0) {
		for(int i = 0; i < links.length; i++) {
			if(resourceResolver.resolve(links[i]).getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
				String thumbnail = extract(links[i]);
				if(!thumbnail.equals("")) { %>
					<div>
						<div class="show-for-small thumbnail"><a href="<%= links[i] %>" title="video thumbnail"><img src="<%= thumbnail %>" /></a></div>
					   	<div class="show-for-medium-up"><iframe src="<%= links[i] %>" frameborder="0" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></div>
					</div>
				<% } else { %>
					<div>*** Format not supported ***</div>
				<% }
			} else {
				alt = "Image slider " + i; %>
				<div><img src="<%= links[i] %>" alt="<%= alt %>" /></div>
		<% }
		}
	} else { %>
		<div>***** Please add a video or image *****</div>
		<div>***** Please add a video or image *****</div>
	<% } %>
</div>