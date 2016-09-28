<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/gsusa/components/global.jsp" %>
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
	final String[] carouselList = properties.get("carouselList", String[].class);
	int numberOfImages = 0;
	if (carouselList != null) {
		numberOfImages = carouselList.length;
	}
	String title[] = new String[numberOfImages];
	String alt[] = new String[numberOfImages];
	String link[] = new String[numberOfImages];
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

			//now check if the link is youtube/vimeo
			if (link[i].indexOf("youtube") != -1) {
				String ytId = extractYTId(link[i]);
				videoId[i] = ytId;
				videoThumbNail[i] = "https://i1.ytimg.com/vi/" + ytId +"/mqdefault.jpg";
//				link[i] = "https://www.youtube.com/watch?v=" + ytId + "?enablejsapi=1&rel=0&autoplay=0&wmode=transparent";
				link[i] = "https://www.youtube.com/embed/" + ytId + "?enablejsapi=1&rel=0&autoplay=0&wmode=transparent";
			} else if (link[i].indexOf("vimeo") != -1) {
				String vimeoId = extractVimeoId(link[i]);
				videoId[i] = vimeoId;
				String jsonOutput = readUrlFile("http://vimeo.com/api/v2/video/" + vimeoId + ".json");
				if (!"".equals(jsonOutput)) {
					JSONArray json = new JSONArray(jsonOutput);
					if (!json.isNull(0)) {
						videoThumbNail[i] = json.getJSONObject(0).getString("thumbnail_large");
					}
				}
				link[i] = "https://player.vimeo.com/video/" + vimeoId + "?api=1&player_id=" + "vimeoPlayer" + i ;
			} else {
				videoThumbNail[i] = "not supported";
			}
		}
	}

	String source7 = properties.get("source7", "not_set");
	String homeCarouselAutoscroll = properties.get("homecarouselautoscroll", "false");
	String homeCarouselTimeDelay = properties.get("homecarouseltimedelay", "1000");
	String homeCarouselAutoPlaySpeed = properties.get("homecarouselautoplayspeed", "2000");
	String blogBgImage = properties.get("blogbgimage", "");

	//passing this to another jsp
	request.setAttribute("source7", source7);

%>
<script type="text/javascript">
	$(document).ready(function() {			
		function pauseVideoSliderVideosYoutube() {
			if($('.lazyYT > iframe').length > 0) {
			    $.each($('.lazyYT > iframe'), function( i, val ) {
			    	var iframe = val;
			    	iframe.contentWindow.postMessage('{"event":"command","func":"stopVideo","args":""}', '*');
			    });
			}
		}
		
		function pauseVideoSliderVideosVimeo(){
			$('[id*="vimeoPlayer"]').each(function (i, val) {
				// if(typeof($f) !== "undefined"){
				// 	$f(val).api('pause');
				// }
			});
		}
		
		$('.main-slider').on('afterChange', function (event, slick, currentSlide) {
			pauseVideoSliderVideosYoutube();
			pauseVideoSliderVideosVimeo();
			$(".zip-council").slideDown();
		});
		
		stopSlider = function() {
			var slick = $('.main-slider');
			if (slick != undefined && slick.slick != undefined) {
				slick.slick('slickPause');
				slick.slick('slickSetOption', 'autoplay', false, false);
				slick.slick('autoPlay',$.noop);
			}
		};

		startSlider = function() {
			var slick = $('.main-slider');
			if (slick != undefined && slick.slick != undefined) {
				slick.slick('slickPlay');
				// slick.slick('slickSetOption', 'autoplay', true, false);
				// slick.slick('autoPlay',$.noop);
			}
		}

		for (var i = 0; i < <%=numberOfImages%>; i++ ) {
			if ($('#vimeoPlayer' + i).length > 0) {
				$('#vimeoPlayer' + i).load(function() {
					$.getScript('https://player.vimeo.com/api/player.js', function() {
						function attachListenerToVideoSlider () {
							for (var k = 0; k < $('.main-slider iframe').length; k ++) {
								var iframe = $('.main-slider iframe')[k], 
									player, 
									vPlayerId = $(iframe).attr('id');
								if (iframe.id != undefined) {
									player = new Vimeo.Player(vPlayerId);
									player.ready().then( function() {
										player.playbar(false);
									});
									player.on('play', function() {
										stopSlider();
										$('.zip-council').css('display','none');
									});
									player.on('pause', function() {
										$('.zip-council').css('display','block');
									});
									$('.main-slider button').click( function() {
										player.pause();
										startSlider();
									});
								}
							}
						}
						attachListenerToVideoSlider();
					});
				});
			}
		}
		
	});
	var isRetina = (
		window.devicePixelRatio > 1 || (window.matchMedia && window.matchMedia("(-webkit-min-device-pixel-ratio: 1.5),(-moz-min-device-pixel-ratio: 1.5),(min-device-pixel-ratio: 1.5)").matches)
	);

	//this value is used to adjust the speed of the carousel on the first opening page.
	homeCarouselAutoScroll = <%=homeCarouselAutoscroll%>;
	homeCarouselTimeDelay = <%=homeCarouselTimeDelay%>;
	homeCarouselAutoPlaySpeed = <%=homeCarouselAutoPlaySpeed%>;
	
</script>
<div class="hero-feature">
	<ul class="main-slider">
		<% 
		for (int i = 0 ; i < numberOfImages; i++) { 
			if (!tempHidden[i]) {%>
		<li id="tag_explore_main_<%=i%>">
			<% 
				if (link[i].indexOf("https://www.youtube.com") != -1) {%>
					<div class="videoWrapper show-for-small thumbnail">
						<iframe id="youtubePlayer<%=i%>" width="100%" height="560" src="<%=link[i]%>" frameborder="0" allowfullscreen></iframe>
					</div>
						
					<div class="show-for-medium-up">
					<% if(!"".equals(title[i])){ %>
						<div id="youtubePlayer<%=i%>" class="lazyYT" data-id="youtubePlayer<%=i%>" data-ratio="16:9" data-youtube-id="<%= videoId[i]%>" data-display-title="true" title="<%=title[i]%>"></div>
		  			<% } else { %>
			  			<div id="youtubePlayer<%=i%>" class="lazyYT" data-id="youtubePlayer<%=i%>" data-ratio="16:9" data-youtube-id="<%= videoId[i]%>"></div>
		  			<% } %>
					</div>
			<% } else if (link[i].indexOf("https://player.vimeo.com/video/") != -1) {%>

					<div class="videoWrapper"><iframe id="vimeoPlayer<%=i%>" src="<%=link[i]%>" width="100%" height="560" frameborder="0" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></div>

			<% } else {%>

					<a href="<%=link[i]%>" title="<%=title[i]%>" <%=openInNewWindow[i]%>>
						<img src="<%= getImageRenditionSrc(resourceResolver, imagePath[i], "cq5dam.npd.top.")%>" alt="<%= alt[i] %>" class="slide-thumb tag_explore_image_hero_<%=i%>"/>
					</a>

			<% }%>

		</li>
		<% } 
		}
		%>
	</ul>
	<cq:include path="zip-council" resourceType="gsusa/components/zip-council" />
</div>
<%
	request.removeAttribute("source7");
%>
