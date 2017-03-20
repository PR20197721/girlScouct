<%@include file="/libs/foundation/global.jsp"%>
<cq:includeClientLib js="video" />
<%@page import="org.apache.sling.commons.json.*,
				java.io.*,
				java.util.regex.*,
				java.net.*,
				org.apache.sling.commons.json.*, 
				java.util.Random,
				com.day.cq.wcm.api.WCMMode" %>
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

    function getInternetExplorerVersion()
    //Returns the version of Internet Explorer or a -1
    //(indicating the use of another browser).
    {
        var rv = -1; // Return value assumes failure.
        if (navigator.appName == 'Microsoft Internet Explorer') {
            var ua = navigator.userAgent;
            var re = new RegExp("MSIE ([0-9]{1,}[\.0-9]{0,})");
            if (re.exec(ua) != null)
                rv = parseFloat(RegExp.$1);
        }
        return rv;
    }

    function checkVersion() {
        var ver = getInternetExplorerVersion();

        if (ver > -1) {
            if (ver <= 9.0) {
                console.log("No auto slide due to browser incompatibility");
                videoSliderAuto = false;
            }
        }
    }

    checkVersion();
</script>
<%
	//TODO: Optimize following javascript
	String[] links = properties.get("links", String[].class);
	if (links == null && WCMMode.fromRequest(request) == WCMMode.EDIT) {
%>
<p> Video Slider - Please select at least one link to display</p>
<% }else {
	%><div class="video-slider-wrapper"><%
	String alt = "";
	String[] urls = null;
	for (int i = 0; i < links.length; i++) {
	    String[] split = links[i].split("\\|\\|\\|");
	    String title = split.length >= 1 ? split[0] : "";
	    String path = split.length >= 2 ? split[1] : "";
		if(resourceResolver.resolve(path).getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
			urls = extract(path);
			if(urls.length >= 4){
				%><div>
					<%if(urls.length == 5){
						%>
						<script>
                            var ytid = '<%= urls[4] %>',
                                id = '<%= urls[3] %>';
                            youtubeIDs.push([ytid, id]);
						</script>
						<%
					} %>
					<div class="show-for-small thumbnail">
						<a href="<%= path %>" target="_blank" title="video thumbnail">
							<img src="<%= urls[1] %>" />
						</a>
					</div>
				  	<div class="vid-slide-wrapper show-for-medium-up">
				  		<% if(urls.length == 5) { %>
				  			<% if(!title.equals("")){ %>
				  			   <div class="lazyYT" data-id="<%= urls[3] %>" data-youtube-id="<%= urls[4]%>" data-display-title="true" title="<%= title %>"></div>
				  			<% } else { %>
				  			   <div class="lazyYT" data-id="<%= urls[3] %>" data-youtube-id="<%= urls[4]%>"></div>
				  			<% } %>
			  			<% } else { %>
				  			<iframe id="<%= urls[3] %>" class="<%= urls[2] %>" src="<%= urls[0] %>" frameborder="0" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>
			  			<% } %>
		  			</div>
		  			<script type="text/javascript">
                        $(function() {
                            
                            var slick = $('.video-slider-wrapper'),
                                //slides = $(".video-slider .slick-slide"),
                                //iframes = $('.vid-slide-wrapper iframe'),
                                vimeoPlayer = $(".vimeo"),
                                youtubePlayer = $('.lazyYT > iframe');
                            /*
                            slides.css({
                                height: slides.width() * 9 / 16,
                                paddingBottom: 0
                            });
                            */
                            function stopSlider() {
                                if (slick != undefined && slick.slick != undefined) {
                                    slick.slick('slickPause');
                                    slick.slick('slickSetOption', 'autoplay', false, false);
                                    slick.slick('autoPlay', $.noop);
                                }
                            }

                            function pauseVideoSliderVideosVimeo() {
                                if (typeof ($f) !== "undefined") {
                                    $.each(vimeoPlayer, function (i, iframe) {
                                        $f(iframe).api('unload');
                                    });
                                }
                            };

                            function pauseVideoSliderVideosYoutube() {
                                $.each(youtubePlayer, function (i, iframe) {
                                    iframe.contentWindow.postMessage('{"event":"command","func":"stopVideo","args":""}', '*');
                                });
                            }

                            slick.on('afterChange', function (event, slick, currentSlide) {
                                pauseVideoSliderVideosYoutube();
                                pauseVideoSliderVideosVimeo();
                            });

                            // Add player listeners
                            //$('#<%=urls[3]%>').on("load", function () {
                            if (typeof ($f) !== "undefined") {
                                $.each(vimeoPlayer, function (i, iframe) {
                                    $f(iframe).addEvent('playProgress', function () {
                                        stopSlider();
                                    });
                                });
                            }
                            //});

                        });
                    </script>
	  			</div>
			<% } else { %>
				<div>*** Format not supported ***</div>
			<% }
		} else {
			alt = "Image slider " + i; %>
			<div><img src="<%= path %>" alt="<%= alt %>" /></div>
	<% }
	}
	%></div><%
	}%>
