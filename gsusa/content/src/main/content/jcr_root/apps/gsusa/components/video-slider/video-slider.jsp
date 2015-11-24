<%@include file="/libs/foundation/global.jsp"%>
<%@page import="org.apache.sling.commons.json.*,
				java.io.*,
				java.util.regex.*,
				java.net.*,
				org.apache.sling.commons.json.*, 
				java.util.Random" %>
<%@page session="false" %>

<%!
public String[] extract(String url){
	if (url.indexOf("youtube") != -1) {
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
    %>
<script>
videoSliderDelay = <%= timedelay %>;
videoSliderAuto = <%= autoscroll %>;
var youtubeIDs = [];
</script>

<div class="video-slider-wrapper">
<%
	//TODO: Optimize following javascript
	String[] links = properties.get("links",String[].class);
	String alt = "";
	String[] urls = null;
	if(links != null && links.length > 0) {
		for(int i = 0; i < links.length; i++) {
			if(resourceResolver.resolve(links[i]).getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
				urls = extract(links[i]);
				if(urls.length >= 4){
					%><div>
						<%if(urls.length == 5){
							%>
							<script>
							var ytid = '<%= urls[4] %>';
							var id = '<%= urls[3] %>';
							youtubeIDs.push([ytid, id]);
							</script>
							<%
						} %>
						<div class="show-for-small thumbnail">
							<a href="<%= links[i] %>" target="_blank" title="video thumbnail">
								<img src="<%= urls[1] %>" />
							</a>
						</div>
					  	<div class="vid-slide-wrapper show-for-medium-up">
					  		<iframe id="<%= urls[3] %>" class="<%= urls[2] %>" src="<%= urls[0] %>" frameborder="0" webkitallowfullscreen mozallowfullscreen allowfullscreen>
				  			</iframe>
			  			</div>
			  			<script type="text/javascript">
						$('#<%=urls[3]%>').load(function() {
							  
							
							$.getScript('https://f.vimeocdn.com/js/froogaloop2.min.js', function() {
								  
								  function pauseVideoSliderVideos() {
									  if($('.vimeo').length > 0){
										  $.each($(".vimeo"), function( i, val ) { 
									    	  $f(val).api('unload');
									      });
									  } if($('.youtube').length > 0) {
									      $.each($('.youtube'), function( i, val ) {
									    	  val.contentWindow.postMessage('{"event":"command","func":"stopVideo","args":""}', '*');
									      });
								  	}
								  }
								  
								  function stopSlider() {
										var slick = $('.video-slider-wrapper');
										if(slick != undefined && slick.slick != undefined){
											slick.slick('slickPause');
											slick.slick('slickSetOption', 'autoplay', false, false);
											slick.slick('autoPlay',$.noop);
										}
									}
								  
								  $('.video-slider-wrapper').on('afterChange', function (event, slick, currentSlide) {
									    pauseVideoSliderVideos();
									});
								  
								  function attachListenerToVideoSlider () {
									    for (var i = 0; i < $('.vid-slide-wrapper iframe').length; i ++) {
									    	var iframe = $('.vid-slide-wrapper iframe')[i],
									    		player;
									    	if ($(iframe).hasClass("vimeo")) {
									    		player = $f(iframe);
								    			player.addEvent('playProgress', function() {
								    				stopSlider();
									    		}); 
									    	}
									    }   
									}
						
									function loadYTScript() {
									    if (typeof(YT) == 'undefined' || typeof(YT.Player) == 'undefined') {
									        var tag = document.createElement('script');
									        tag.src = "https://www.youtube.com/iframe_api";
									        var firstScriptTag = document.getElementsByTagName('script')[0];
									        firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
									    }
									}
						
									function loadPlayer() {
									        window.onYouTubePlayerAPIReady = function() {
									        	if(typeof youtubeIDs != undefined){
									        		for(var i = 0; i < youtubeIDs.length; i++){
									        			createPlayer(youtubeIDs[i]);
									        		}
									        	}
									        };
									}
						
									function createPlayer(id){
										var player = new YT.Player(id[1], {
											videoId: id[0],
											events: {
												'onStateChange': stopSlider
											}
										});
									}
									
									attachListenerToVideoSlider();
									loadYTScript();
									loadPlayer();
							});
						  
						  });
					</script>
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
