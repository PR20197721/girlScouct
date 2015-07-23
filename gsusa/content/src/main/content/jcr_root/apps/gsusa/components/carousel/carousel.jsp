<%@include file="/libs/foundation/global.jsp" %>
<%@page import="org.apache.sling.commons.json.*, java.io.*, java.util.regex.*, java.net.*, org.apache.sling.commons.json.*, org.apache.sling.api.request.RequestDispatcherOptions, com.day.cq.wcm.api.components.IncludeOptions, org.apache.sling.jcr.api.SlingRepository" %>
<%@page session="false" %>
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

%>
<%
	String[] emptyArray = new String[1];
	String content = properties.get("content", "Join Now");
	String btnName = properties.get("button", "Explore Girl Scouts");
	String title = properties.get("title", "Introduce girls to");
	String[] imagePathArray = properties.get("imagePath", emptyArray);
    Integer interval = properties.get("interval", 1000);
    String[] imageAlt = properties.get("imageAlt", emptyArray);


	String[] content2 = properties.get("content2", emptyArray);
	String[] imagePath2 = properties.get("imagePath2", emptyArray);
	String[] subtitle2 = properties.get("subtitle2", emptyArray);
	String title2 = properties.get("title2", "");
    String[] imageAlt2 = properties.get("imageAlt2", emptyArray);

	String[] content3 = properties.get("content3", emptyArray);
	String[] imagePath3 = properties.get("imagePath3", emptyArray);
	String[] subtitle3 = properties.get("subtitle3", emptyArray);
	String title3 = properties.get("title3", "");
    String[] imageAlt3 = properties.get("imageAlt3", emptyArray);


	String title4 = properties.get("title4", "");

	String title5 = properties.get("title5", "");
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

	String title6 = properties.get("title6", "");
	String content6 = properties.get("content6", "");
	String imagePath6 = properties.get("imagePath6", "");
    String imageAlt6 = properties.get("imageAlt6", "");
	String closingSource6 = properties.get("closingSource6", "homepage");

	String source7 = properties.get("source7", "homepage");

	//passing this to another jsp
	request.setAttribute("source7", source7);

	//validation
	String errorMessage = "";

    if (imagePathArray.length != imageAlt.length) {
    	errorMessage += "The number of images and \"image alts\" need to be the same in the \"Opening Page\" tab <br>";
    }
    if (content2.length != imagePath2.length || imagePath2.length != subtitle2.length || subtitle2.length != imageAlt2.length) {
    	errorMessage += "The number of images/image alts/subtitles/content need to be the same in the \"First Page (Image)\" tab <br>";
	}
    if (content3.length != imagePath3.length || imagePath3.length != subtitle3.length || subtitle3.length != imageAlt3.length) {
    	errorMessage += "The number of images/image alts/subtitles/content need to be the same in the \"Second Page (Image)\" tab <br>";
	}
	if (!"".equals(errorMessage)) {
    	errorMessage += "Please right click on this message and edit the carousel component.";
%>
		<p class="error"> The following errors occur: <br> <%= errorMessage %></p>  
<%
		return;
	}

	//now get all the variables
	for (int i = 0 ; i < 4; i++ ){
		if ("link".equals(videoType5[i])) {
			String link = properties.get("videoLink5" + i, "");
			if (link.indexOf("youtube") != -1) {
				String ytId = extractYTId(link);
				videoId[i] = ytId;
				videoThumbNail[i] = "https://i1.ytimg.com/vi/" + ytId +"/hqdefault.jpg";
				embeded[i] = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/" + ytId + "\" frameborder=\"0\" allowfullscreen></iframe>";
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
		} else if("photo".equals(videoType5[i])){
			String photoPath = properties.get("newsPic5" + i, "");
			videoThumbNail[i] = photoPath;
			//done adding video.
			embeded[i] = "";
		} else {
			//videoType5[i] equals "none". Do nothing

		}
	}
%>
<script>
		var isRetina = (
			window.devicePixelRatio > 1 || (window.matchMedia && window.matchMedia("(-webkit-min-device-pixel-ratio: 1.5),(-moz-min-device-pixel-ratio: 1.5),(min-device-pixel-ratio: 1.5)").matches)
		);


//this value is used to adjust the speed of hte carousel on the first opening page.
interval = <%= interval %>;
</script>
<div class="hero-feature">
	<div class="overlay"></div>
	<ul class="main-slider">
<%
	for (int i = 0 ; i < imagePathArray.length; i++) {
%>
		<li><img src="<%=imagePathArray[i] %>/jcr:content/renditions/cq5dam.web.1280.1280.jpeg" alt="<%= imageAlt[i] %>" class="slide-thumb"/></li>
<%
	}
%>
	</ul>
	<div class="hero-text first">
		<section>
			<img src="/etc/designs/gsusa/clientlibs/images/white_trefoil.png" alt="icon" />
			<h2><%= title %></h2>
			<p><%= content %></p>
			<a href="#" class="button explore" tabindex="51"><%= btnName %></a>
		</section>
	</div>
	<div class="position">
		<div class="inner-sliders">
			<ul class="inner">
				<li>
					<ul class="slide-1">
<%
	for (int i = 0 ; i < imagePath2.length; i++) {
%>
						<li>
							<h3><%= title2 %></h3>
							<div class="text white">
								<h4><%= subtitle2[i] %></h4>
								<p><%= content2[i] %></p>
							</div>
							<img src="<%= imagePath2[i] %>" alt="<%= imageAlt2[i] %>"class="slide-thumb"/>
						</li>
<%
	}
%>
					</ul>
				</li>
				<li>
					<ul class="slide-2">
<%
	for (int i = 0 ; i < imagePath3.length; i++) {
%>
						<li>
							<h3><%= title3 %></h3>
							<div class="text white">
								<h4><%= subtitle3[i] %></h4>
								<p><%= content3[i] %></p>
							</div>
							<img src="<%= imagePath3[i] %>" alt="<%= imageAlt3[i] %>" class="slide-thumb"/>
						</li>
<%
	}
%>
					</ul>
				</li>
				<li>
					<ul class="slide-3">
						<li>
							<h3><%= title4 %></h3>
							<cq:include path="blog-feed" resourceType="gsusa/components/blog-feed" />
						</li>
					</ul>
				</li>
				<li>
					<ul class="slide-4">
<%
	for (int i = 0 ; i < 4; i++) {
		if("photo".equals(videoType5[i])){
%>
						<li>
							<h3><%= title5 %></h3>
							<div class="video-wrapper">
								<div class="video video-embed">
									<img src="<%= videoThumbNail[i]%>" alt="" class="slide-thumb news-pic"/>
								</div>
								<div class="video-article">
									<h4><%= subtitle5[i] %></h4>
									<p><%= content5[i]%></p>
								</div>
							</div>
						</li>
<%
		}else if ("link".equals(videoType5[i])) {
%>
						<li>
							<h3><%= title5 %></h3>
							<div class="video-wrapper">
		                                <div class="video-embed">
										<img src="<%= videoThumbNail[i]%>" alt="" class="slide-thumb"/>
										<%= embeded[i] %>
								</div>
								<div class="video-article">
										<h4><%= subtitle5[i] %></h4>
										<p><%= content5[i]%></p>
								</div>
							</div>
						</li>
<%
		} else if ("path".equals(videoType5[i])) {
%>
						<li>
							<h3><%= title5 %></h3>
							<div class="video-wrapper">
								<div class="video video-embed">
									<img src="<%= videoThumbNail[i]%>" alt="" class="slide-thumb"/>
									<cq:include path="<%=vidNames[i] %>" resourceType="gsusa/components/video" />
								</div>
								<div class="video-article">
									<h4><%= subtitle5[i] %></h4>
									<p><%= content5[i]%></p>
								</div>
							</div>
						</li>
<%
		} else {
//none
		}
	}
%>
					</ul>
				</li>
			</ul>
		</div>
	</div>
	<div class="final-comp">
		<div class="hero-text">
			<section>
				<img src="/etc/designs/gsusa/clientlibs/images/white_trefoil.png" alt="icon"/>
				<h2><%= title6%></h2>
				<p><%= content6 %></p>
				<form action="#" name="join-now" class="formJoin join-now-form clearfix">
					<input type="text" name="ZipJoin" maxlength="5" class="join-text hide" placeholder="Enter Zip code">
					<input type="hidden" name="source" value="<%= closingSource6 %>">
					<a href="#" class="button join-now">Join Now</a>
				</form>
			</section>
		</div>
		<img src="<%= imagePath6 %>" alt="<%= imageAlt6 %>" class="main-image" />
	</div>
	<cq:include path="zip-council" resourceType="gsusa/components/zip-council" />
</div>
<%
	request.removeAttribute("source7");
%>
