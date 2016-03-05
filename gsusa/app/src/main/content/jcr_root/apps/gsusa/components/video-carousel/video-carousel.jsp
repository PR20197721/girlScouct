<%@include file="/libs/foundation/global.jsp" %>
<%@page import="org.apache.sling.commons.json.*,
				java.io.*,
				java.util.regex.*,
				java.net.*,
				org.apache.sling.commons.json.*,
				org.apache.sling.api.request.RequestDispatcherOptions,
                com.day.cq.wcm.api.components.IncludeOptions,
                org.apache.sling.jcr.api.SlingRepository,
                com.day.cq.wcm.api.WCMMode" %>

<%!
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
public void addVideoNode(String videoPath, String videoName) {

    }
    %>
    <%
    String videoType50 = properties.get("videoType50", "");
    String videoType51 = properties.get("videoType51", "");
    String videoType52 = properties.get("videoType52", "");
    String videoType53 = properties.get("videoType53", "");
    String[] videoType5 = {videoType50, videoType51, videoType52, videoType53};
    String[] videoThumbNail = new String[4];
    String[] videoId = new String[4];
    String[] embeded = new String[4];
    String[] subtitle5 = {properties.get("subtitle50", ""), properties.get("subtitle51", ""), properties.get("subtitle52", ""), properties.get("subtitle53", "")};
    String[] content5 = {properties.get("content50", ""), properties.get("content51", ""), properties.get("content52", ""), properties.get("content53", "")};
    String [] vidNames = {"vid0", "vid1", "vid2", "vid3"};
    int count = 0;


//now get all the variables
for (int i = 0 ; i < 4; i++ ){
	if ("link".equals(videoType5[i])) {
		String link = properties.get("videoLink5" + i, "");
		if (link.indexOf("youtube") != -1) {
			String ytId = extractYTId(link);
			videoId[i] = ytId;
			videoThumbNail[i] = "https://i1.ytimg.com/vi/" + ytId +"/mqdefault.jpg";
			embeded[i] = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/" + ytId + "?enablejsapi=1&rel=0&autoplay=0&wmode=transparent\" frameborder=\"0\" allowfullscreen></iframe>";
		} else if (link.indexOf("vimeo") != -1) {
			String vimeoId = extractVimeoId(link);
			videoId[i] = vimeoId;
			String jsonOutput = readUrlFile("http://vimeo.com/api/v2/video/" + vimeoId + ".json");
			if (!"".equals(jsonOutput)) {
				JSONArray json = new JSONArray(jsonOutput);
				if (!json.isNull(0)) {
					videoThumbNail[i] = json.getJSONObject(0).getString("thumbnail_large");
				}
			}
			embeded[i] = "<iframe src=\"https://player.vimeo.com/video/"+ vimeoId +"\" width=\"100%\" height=\"100%\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>";
		} else {
			videoThumbNail[i] = "not supported";
		}
	} else if ("path".equals(videoType5[i])) {
		String videoPath = properties.get("videoPath5" + i, "");
		videoThumbNail[i] = videoPath + "/jcr:content/renditions/cq5dam.thumbnail.319.319.png";

		//add video node
		if (currentNode != null) {
			SlingRepository repository = (SlingRepository)sling.getService(SlingRepository.class);
			Session session = repository.loginAdministrative(null);

			Node vid = resourceResolver.resolve(resource.getPath() + "/" + "").adaptTo(Node.class);
			if (resourceResolver.resolve(resource.getPath() + "/" + vidNames[i]).getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
				vid = session.getNode(resource.getPath()).addNode(vidNames[i], "nt:unstructured");
				vid.setProperty("asset", videoPath);
				vid.setProperty("sling:resourceType", "gsusa/components/video");
			} else {
				vid = session.getNode(resource.getPath() + "/" + vidNames[i]);
				vid.setProperty("asset", videoPath);
				vid.setProperty("sling:resourceType", "gsusa/components/video");
			}

			session.save();
			session.logout();
		}
		//done adding video.
		embeded[i] = "";
	} else {
		//videoType5[i] equals "none". Do nothing

	}
}

if(videoThumbNail[0] == null && WCMMode.fromRequest(request) == WCMMode.EDIT){
	%> It looks like you're missing content here. Please edit this component. <%
}
%>
<div class="feature-video-slider">
    <div class="slide-5">
    <%
    	for (int i = 0 ; i < 4; i++) {
    		count++;
    		if ("link".equals(videoType5[i])) {
    %>
			<div>
				<div class="video-wrapper">
					<div class="video-embed">
							<img src="<%= videoThumbNail[i]%>" alt="" class="slide-thumb"/>
							<%= embeded[i] %>
					</div>
				</div>
			</div>
<%
    		} else if ("path".equals(videoType5[i])) {
    %>
			<div>
				<div class="video-wrapper">
					<div class="video-embed">
						<img src="<%= videoThumbNail[i]%>" alt="" class="slide-thumb"/>
						<cq:include path="<%=vidNames[i] %>" resourceType="gsusa/components/video" />
					</div>
				</div>
			</div>
    <%
    		} else {
    			count--;
    		}
    	}
    %>
    </div>
</div>
<% if(count == 1 && WCMMode.fromRequest(request) == WCMMode.EDIT){ %>
<div>
Right-click here to edit
</div>
<% } %>