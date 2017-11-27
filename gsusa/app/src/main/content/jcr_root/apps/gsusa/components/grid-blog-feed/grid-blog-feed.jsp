<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@page import="com.day.cq.wcm.api.WCMMode,
				org.apache.sling.commons.json.*, 
				java.io.*, 
				java.net.*" %>

<%
	//String id = properties.get("id","7441709438919444345");
	//String key = properties.get("key", "AIzaSyDyEWV7rt41tGxPcIXZ04kG38-ZNxkBrM0");
	//String url = "https://www.googleapis.com/blogger/v3/blogs/" + id + "/posts?key=" + key;
	
	// Dialog Values
	// General Tab
	String endpointurl = properties.get("endpointurl", "");
	String blogurl = properties.get("blogurl", "");
	String count = properties.get("count", "10");
	String iconurl = properties.get("iconurl","");
	String pinID1 = properties.get("postid1","");
	String pinID2 = properties.get("postid2","");
	String pinID3 = properties.get("postid3","");
	
	// Desktop Tab 
	String desktopimagesize = properties.get("desktopimagesize", "");
	String desktoptitlelines = properties.get("desktoptitlelines", "");
	String desktopsnippetlines = properties.get("desktopsnippetlines", "");
	String desktoptitlefont = properties.get("desktoptitlefont", "");
	String desktoptitlelineheight = properties.get("desktoptitlelineheight", "");
	String desktopsnippetfont = properties.get("desktopsnippetfont", "");
	String desktopsnippetlineheight = properties.get("desktopsnippetlineheight", "");
	
	// Mobile Tab 
	String mobileimagesize = properties.get("mobileimagesize", "");
	String mobiletitlelines = properties.get("mobiletitlelines", "");
	String mobilesnippetlines = properties.get("mobilesnippetlines", "");
	String mobiletitlefont = properties.get("mobiletitlefont", "");
	String mobiletitlelineheight = properties.get("mobiletitleheight", "");
	String mobilesnippetfont = properties.get("mobilesnippetfont", "");
	String mobilesnippetlineheight = properties.get("mobilesnippetlineheight", "");


	// EDIT, DESIGN, PREVIEW, READ_ONLY
	// DISABLED, ANALYTICS
	String WCMmode = WCMMode.fromRequest(request).name(); 
	boolean displaySampleFeed = false;
	if (WCMmode.equalsIgnoreCase("EDIT") || WCMmode.equalsIgnoreCase("DESIGN") ||
		WCMmode.equalsIgnoreCase("PREVIEW") || WCMmode.equalsIgnoreCase("READ_ONLY")) {
		displaySampleFeed = true;
	}
	
%>
<div id="tag_social_feed_blog" class="feedwrapper clearfix">
	<div class="blogfeedicon"><!-- img src="/content/dam/girlscouts-shared/images/Icons/social-media/blogger.png"> -->
		<img src="<%= iconurl %>">
	</div>
	<div class="feed-block">
		<div class="block-area">
			<div class="blog-feed-image-head-area centered"></div>
			<ul class="blog-feed-area"></ul>
			<div class="blog-see-more centered"><a href="<%= blogurl %>" title="see more on blogger">See more</a></div>
		</div>
	</div>
</div>
<script>
//http://spin.js.org/#v2.3.2
//!function(a,b){"object"==typeof module&&module.exports?module.exports=b():"function"==typeof define&&define.amd?define(b):a.Spinner=b()}(this,function(){"use strict";function a(a,b){var c,d=document.createElement(a||"div");for(c in b)d[c]=b[c];return d}function b(a){for(var b=1,c=arguments.length;c>b;b++)a.appendChild(arguments[b]);return a}function c(a,b,c,d){var e=["opacity",b,~~(100*a),c,d].join("-"),f=.01+c/d*100,g=Math.max(1-(1-a)/b*(100-f),a),h=j.substring(0,j.indexOf("Animation")).toLowerCase(),i=h&&"-"+h+"-"||"";return m[e]||(k.insertRule("@"+i+"keyframes "+e+"{0%{opacity:"+g+"}"+f+"%{opacity:"+a+"}"+(f+.01)+"%{opacity:1}"+(f+b)%100+"%{opacity:"+a+"}100%{opacity:"+g+"}}",k.cssRules.length),m[e]=1),e}function d(a,b){var c,d,e=a.style;if(b=b.charAt(0).toUpperCase()+b.slice(1),void 0!==e[b])return b;for(d=0;d<l.length;d++)if(c=l[d]+b,void 0!==e[c])return c}function e(a,b){for(var c in b)a.style[d(a,c)||c]=b[c];return a}function f(a){for(var b=1;b<arguments.length;b++){var c=arguments[b];for(var d in c)void 0===a[d]&&(a[d]=c[d])}return a}function g(a,b){return"string"==typeof a?a:a[b%a.length]}function h(a){this.opts=f(a||{},h.defaults,n)}function i(){function c(b,c){return a("<"+b+' xmlns="urn:schemas-microsoft.com:vml" class="spin-vml">',c)}k.addRule(".spin-vml","behavior:url(#default#VML)"),h.prototype.lines=function(a,d){function f(){return e(c("group",{coordsize:k+" "+k,coordorigin:-j+" "+-j}),{width:k,height:k})}function h(a,h,i){b(m,b(e(f(),{rotation:360/d.lines*a+"deg",left:~~h}),b(e(c("roundrect",{arcsize:d.corners}),{width:j,height:d.scale*d.width,left:d.scale*d.radius,top:-d.scale*d.width>>1,filter:i}),c("fill",{color:g(d.color,a),opacity:d.opacity}),c("stroke",{opacity:0}))))}var i,j=d.scale*(d.length+d.width),k=2*d.scale*j,l=-(d.width+d.length)*d.scale*2+"px",m=e(f(),{position:"absolute",top:l,left:l});if(d.shadow)for(i=1;i<=d.lines;i++)h(i,-2,"progid:DXImageTransform.Microsoft.Blur(pixelradius=2,makeshadow=1,shadowopacity=.3)");for(i=1;i<=d.lines;i++)h(i);return b(a,m)},h.prototype.opacity=function(a,b,c,d){var e=a.firstChild;d=d.shadow&&d.lines||0,e&&b+d<e.childNodes.length&&(e=e.childNodes[b+d],e=e&&e.firstChild,e=e&&e.firstChild,e&&(e.opacity=c))}}var j,k,l=["webkit","Moz","ms","O"],m={},n={lines:12,length:7,width:5,radius:10,scale:1,corners:1,color:"#000",opacity:.25,rotate:0,direction:1,speed:1,trail:100,fps:20,zIndex:2e9,className:"spinner",top:"50%",left:"50%",shadow:!1,hwaccel:!1,position:"absolute"};if(h.defaults={},f(h.prototype,{spin:function(b){this.stop();var c=this,d=c.opts,f=c.el=a(null,{className:d.className});if(e(f,{position:d.position,width:0,zIndex:d.zIndex,left:d.left,top:d.top}),b&&b.insertBefore(f,b.firstChild||null),f.setAttribute("role","progressbar"),c.lines(f,c.opts),!j){var g,h=0,i=(d.lines-1)*(1-d.direction)/2,k=d.fps,l=k/d.speed,m=(1-d.opacity)/(l*d.trail/100),n=l/d.lines;!function o(){h++;for(var a=0;a<d.lines;a++)g=Math.max(1-(h+(d.lines-a)*n)%l*m,d.opacity),c.opacity(f,a*d.direction+i,g,d);c.timeout=c.el&&setTimeout(o,~~(1e3/k))}()}return c},stop:function(){var a=this.el;return a&&(clearTimeout(this.timeout),a.parentNode&&a.parentNode.removeChild(a),this.el=void 0),this},lines:function(d,f){function h(b,c){return e(a(),{position:"absolute",width:f.scale*(f.length+f.width)+"px",height:f.scale*f.width+"px",background:b,boxShadow:c,transformOrigin:"left",transform:"rotate("+~~(360/f.lines*k+f.rotate)+"deg) translate("+f.scale*f.radius+"px,0)",borderRadius:(f.corners*f.scale*f.width>>1)+"px"})}for(var i,k=0,l=(f.lines-1)*(1-f.direction)/2;k<f.lines;k++)i=e(a(),{position:"absolute",top:1+~(f.scale*f.width/2)+"px",transform:f.hwaccel?"translate3d(0,0,0)":"",opacity:f.opacity,animation:j&&c(f.opacity,f.trail,l+k*f.direction,f.lines)+" "+1/f.speed+"s linear infinite"}),f.shadow&&b(i,e(h("#000","0 0 4px #000"),{top:"2px"})),b(d,b(i,h(g(f.color,k),"0 0 1px rgba(0,0,0,.1)")));return d},opacity:function(a,b,c){b<a.childNodes.length&&(a.childNodes[b].style.opacity=c)}}),"undefined"!=typeof document){k=function(){var c=a("style",{type:"text/css"});return b(document.getElementsByTagName("head")[0],c),c.sheet||c.styleSheet}();var o=e(a("group"),{behavior:"url(#default#VML)"});!d(o,"transform")&&o.adj?i():j=d(o,"animation")}return h});
</script>
<script type="text/javascript">

//Adapted from jquery.ellipsis.js
function truncate(el, lines) {

	// if 0, display all
	if (lines == 0) return;
	
	var $container = $(el),
		className = "trunc",
		$elspan = $('<span class="' + className + '" />'),
		spantag = '<span style="white-space: nowrap;">',
		currLine = 0,
		currOffset,
		containerHeight,
		containerWidth,
		setStartEllipAt,
        startEllipAt = 0,
        endEllipAt,
		words,
		baseText = $container.text();
		
	$elspan.text(baseText);	
	$container.empty().append($elspan);
	
	containerHeight = $container.height();
	containerWidth = $container.width();
	
	words = $.trim(baseText).split(/\s+/);
	$elspan.html(spantag + words.join('</span> ' + spantag) + '</span>');
	
	var setStartEllipByLine = function(i, word) {
		var $word = $(word),
			top = $word.position().top;
		if (top != currOffset) {
			currOffset = top;
			currLine += 1;			
			if (currLine <= lines) {
				startEllipAt = i;
			} else if (currLine > lines) {
				endEllipAt = i-1;
				return;
			}			
		}
	};
	$elspan.find('span').each(setStartEllipByLine);

	function getLastLine(start, end) {
		var lastline = "";
		
		for(var i = 0; i < start; i++) {
			lastline += words[i] + " ";
		}
		lastline += '<span class="truncline">'; 
		if (end != undefined) {
			for(var i = start; i < end; i++) {
				lastline += words[i] + " ";
			}
			lastline += words[end] + "...";
		} else {
			for(var i = start; i < words.length; i++) {
				lastline += words[i] + " ";
			}
		}
		lastline += "</span>";
		return lastline;			
	}
	
	function updateText(start, end) {		
		//console.log("updateText(" + start + ", " + end + ")");
		//words[start] = '<span class="' + 'truncline' + '">' + words[start];
		//if (endEllipAt != undefined) {
		//	words[endEllipAt] = words[endEllipAt] + '...' + '</span>';
		//	words.length = endEllipAt+1;
		//}
		//words.push('</span>');
		//$elspan.html(words.join(' '));
		
		var lastline = getLastLine(start, end);
		$elspan.html(lastline);		
		
		while ($elspan.width() > containerWidth) {
			updateText(start, end-1);
		}
	}	
	if (startEllipAt != null) {
        updateText(startEllipAt, endEllipAt);
    }
	
}



$(document).ready(function() {

	var editMode = '<%= WCMmode %>';
	console.log("WCMMode: " + editMode + "!");
	var displaySampleFeed = <%= displaySampleFeed %>;
	console.log("DISPLAY?: " + displaySampleFeed);
	
	var endpointurl = '<%= endpointurl %>';
	var blogCount = '<%= count %>';
	var pinPost1 = '<%= pinID1 %>';
	var pinPost2 = '<%= pinID2 %>';
	var pinPost3 = '<%= pinID3 %>';

	var desktopimagesize = '<%= desktopimagesize %>';
	var desktoptitlelines = '<%= desktoptitlelines %>';
	var desktopsnippetlines  = '<%= desktopsnippetlines %>';
	var desktoptitlefont = '<%= desktoptitlefont %>';
	var desktopsnippetfont = '<%= desktopsnippetfont %>';
	var desktoptitlelineheight = '<%= desktoptitlelineheight %>';
	var desktopsnippetlineheight = '<%= desktopsnippetlineheight %>';

	var mobileimagesize = '<%= mobileimagesize %>';
	var mobiletitlelines = '<%= mobiletitlelines %>';
	var mobilesnippetlines = '<%= mobilesnippetlines %>';
	var mobiletitlefont = '<%= mobiletitlefont %>';
	var mobilesnippetfont = '<%= mobilesnippetfont %>';
	var mobiletitlelineheight = '<%= mobiletitlelineheight %>';
	var mobilesnippetlineheight = '<%= mobilesnippetlineheight %>';

	var $blogFeedArea = $(".blog-feed-area");

	
	var opts = {
			  lines: 11 // The number of lines to draw
			, length: 11 // The length of each line
			, width: 7 // The line thickness
			, radius: 23 // The radius of the inner circle
			, scale: 0.5 // Scales overall size of the spinner
			, corners: 1 // Corner roundness (0..1)
			, color: '#bbb' // #rgb or #rrggbb or array of colors
			, opacity: 0.25 // Opacity of the lines
			, rotate: 0 // The rotation offset
			, direction: 1 // 1: clockwise, -1: counterclockwise
			, speed: 0.7 // Rounds per second
			, trail: 60 // Afterglow percentage
			, fps: 20 // Frames per second when using setTimeout() as a fallback for CSS
			, zIndex: 2e9 // The z-index (defaults to 2000000000)
			, className: 'spinner' // The CSS class to assign to the spinner
			, top: '50%' // Top position relative to parent
			, left: '50%' // Left position relative to parent
			, shadow: false // Whether to render a shadow
			, hwaccel: false // Whether to use hardware acceleration
			, position: 'absolute' // Element positioning
			}; 
	//var spinner = new Spinner(opts).spin($blogFeedArea); 
	
	if (displaySampleFeed === true) {
		console.log("SAMPLE: " + displaySampleFeed);
		var result1 =  { items: [{
				url: "http://blog.girlscouts.org",
				title: "This Is A Sample Blog Feed!",
				id: "6847912407976159730",
				image: "https://1.bp.blogspot.com/-ZegxhMM_BXY/WfhWL5oYrtI/AAAAAAAABRI/yTqkBqd9204mSSQggfFGrDeHQDSRnmDCgCLcBGAs/s400/gs_social_jgl_girl_v2.jpg",
				content: "Blog content goes here. This is a sample blog feed to help author style how blog feeds will appear. Once published, all the feeds will be replaced with real blog feeds."
			}, {
				url: "http://blog.girlscouts.org",
				title: "Another Sample Feed Title",
				id: "6847912407976159730",
				image: "https://1.bp.blogspot.com/-7_4STvYA4kA/WgzWKjeLNHI/AAAAAAAAAvA/qlNm3bsExzoDYMuQBphmaGf_1jzmayzvACLcBGAs/s640/IMG_1169.JPG",
				content: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
			}, {
				url: "http://blog.girlscouts.org/2017/10/today-october-31-we-celebrate-birthday.html",
				title: "Honoring Female Leadership: The Case for a Juliette Gordon Low Bridge in Savannah",
				id: "6847912407976159730",
				image: "https://1.bp.blogspot.com/-ZegxhMM_BXY/WfhWL5oYrtI/AAAAAAAABRI/yTqkBqd9204mSSQggfFGrDeHQDSRnmDCgCLcBGAs/s400/gs_social_jgl_girl_v2.jpg",
				content: "Today, October 31, we celebrate the birthday of Juliette Gordon Low, a pioneering woman from Savannah, Georgia, who more than a century ago started the Girl Scout Movement. As the founder of the largest girl leadership development organization in the world, Juliette had, and through her legacy continues to have, an extraordinary influence on the lives of millions of girls across the country. To honor this remarkable Savannah native and original G.I.R.L. (Go-getter, Innovator, Risk-taker, Leader), Girl Scouts is asking the Georgia General Assembly and Governor Nathan Deal to change the name of the Talmadge Memorial Bridge in Savannah to the Juliette Gordon Low Bridge. As background, on September 28, 2017, the Savannah City Council passed a r"
			}, {
				url: "http://blog.girlscouts.org/2017/11/richard-king-mellon-foundation-and-girl.html",
				title: "Get Parade Ready This Thanksgiving",
				id: "3444336854489813652",
				image: "https://1.bp.blogspot.com/-7_4STvYA4kA/WgzWKjeLNHI/AAAAAAAAAvA/qlNm3bsExzoDYMuQBphmaGf_1jzmayzvACLcBGAs/s640/IMG_1169.JPG",
				content: "Mark your calendars! Girl Scouts will be once again be showcasing their leadership style at the Annual Macy's Thanksgiving Day Parade! No matter where you are, join Girl Scouts, R\u0026amp;B platinum-selling artist Andra Day, and rap superstar Common to stand up for something you believe in. When you spy the Girl Scout float, take to social media and share what you stand for using #GIRLagenda and #StandUpForSomething. If you plan to be in New York City on Thanksgiving morning, use this list to make the most of the inspiring celebration. Let's do this! Pick a viewing location.\u0026nbsp; A Girl Scout is always prepared!\u0026nbsp;Our map\u0026nbsp;will help you scope out the best views of the parade-don't forget to get there early. For more "
			}, {
				url: "http://blog.girlscouts.org/2017/11/richard-king-mellon-foundation-and-girl.html",
				title: "Richard King Mellon Foundation and Girl Scouts: Want More Girls Outdoors? Train the Adults!",
				id: "4905006845068892112",
				image: "https://1.bp.blogspot.com/-7_4STvYA4kA/WgzWKjeLNHI/AAAAAAAAAvA/qlNm3bsExzoDYMuQBphmaGf_1jzmayzvACLcBGAs/s640/IMG_1169.JPG",
				content: "Before any troop leader heads out in the great wide open with her girls, she needs some basic outdoor skills. For volunteers who were Girl Scouts themselves, first aid, building a campfire, and planning a day hike may already be in their toolkit. But for volunteers who didn't grow up spending time in the outdoors, the thought of planning such an excursion with their troop can be intimidating. In fact, we've learnedthat some adults have such high expectations of themselves, fearing they'll provide a less-than-perfect experience, that they almost never take their girls outdoors. That's where events like Girl Scouts Western Pennsylvania's Outdoor Training Summit come into play. With support from the Richard King Mellon Foundation, in September"
			}, {
				url: "http://blog.girlscouts.org/2017/11/stand-up-for-something-this-thanksgiving_8.html",
				title: "Stand Up for Something This Thanksgiving ",
				id: "7031490092168828012",
				image: "https://1.bp.blogspot.com/-hmE8XL9YbKU/WgHs1plwoSI/AAAAAAAAnuE/mHFv9nHZsT4mnYJqaPR9Xsi9fJFVCoEbACEwYBhgL/s640/386559.2.jpg",
				content: "We have exciting news! Girl Scouts of the USA's \"Building a Better World\" float will return to the 91st Annual Macy's Thanksgiving Day Parade® on Thursday, November 23, 2017. Performing on the Girl Scout float this year will be Grammy Award-nominated singer-songwriter Andra Day, who will sing her moving anthem, \"Stand Up for Something,\" from the film Marshall. She will also be accompanied by Grammy and Academy Award-winning artist and actor, Common, who co-wrote and is featured in the song. The Girl Scout float design showcases a century of Girl Scout traditions and highlights today's girls as capable of making great changes to benefit society-echoing the chorus of Andra Day's and Common's song: \"It all means nothing, If you don\u0027t stan"
			}, {
				url: "http://blog.girlscouts.org/2017/11/closing-stem-gender-gap-one-girl-scout.html",
				title: "Closing the STEM Gender Gap, One Girl Scout Badge at a Time",
				id: "4252891479333419113",
				image: "https://2.bp.blogspot.com/-a6yLOTybU10/WgMRgH505vI/AAAAAAAABTA/DO1--GxlqBsZ9EKosjOebTCozEfulw2FACLcBGAs/s640/KTP_GSA_Steam_0489.jpg",
				content: "It's no secret that there are fewer women than men in science, technology, engineering, and math (STEM) fields today. In fact, women hold less than 25 percent of STEM jobs, despite filling close to half of all jobs in the U.S. economy. And women who do hold STEM jobs earn 33 percent more than those in other industries—making the gender wage gap comparatively smaller in STEM fields. At Girl Scouts, we’re more than ready for a change—and STEM leaders start here, with us. Since our founding in 1912, Girl Scouts has introduced girls of all ages, from five-year-old Daisies to high school Ambassadors, to these important fields to help them see for themselves how they can improve the world using valuable STEM skills. We are the foremost experts in"
			}, {
				url: "http://blog.girlscouts.org/2017/11/brand-new-girl-scout-central-flagship.html",
				title: "Brand New Girl Scout Central Flagship Store in Midtown NYC Opens November 20!",
				id: "3980929136087518980",
				image: "https://3.bp.blogspot.com/-Dcd3WWDpRnw/Wg8DrtzFU1I/AAAAAAAAnvo/AiA_2tUBzwQbcpZHF3AhLOmCXgPhSPsSwCLcBGAs/s640/SM_Blog_Twitter_GS-Central.jpg",
				content: "Are you local to the New York City area, or planning to visit this holiday season? Don’t miss a chance to experience the brand new Girl Scout Central flagship store opening Monday, November 20 at 8:30 AM. Our new space is filled with exciting, new exhibitions jam-packed with all of the Girl Scout history, pride, and G.I.R.L. (Go-getter, Innovator, Risk-taker, Leader)™ shine you can handle, plus more of the great Girl Scouts tools and swag you already love to choose from. It’s an adventure sure to inspire adults and girls alike. And for for a limited time, all Girl Scout Central shoppers will receive a FREE gift with purchase! But wait. There’s more! Girls will be selling Girl Scout Cookies at the store for two days only, November 24 and 25,"
			}, {
				url: "http://blog.girlscouts.org/2017/11/fall-back-change-world.html",
				title: "Fall Back, Change the World",
				id: "8121441777739477944",
				image: "https://2.bp.blogspot.com/-YR1xP-Mkthg/Wf5pswpYZXI/AAAAAAAAntQ/LSrwlEdk5TwDSJDJPkT84wBWXzSpy8DIwCLcBGAs/s640/Yosemite15_L3316.jpg",
				content: "Aren't we always saying we wish we had more time? Well with daylight savings, today we do. It may just be one hour—but Girl Scouts know how to get creative to make that hour count. So let’s use the extra time to do what Girl Scouts love most—change the world! Here are four ways to make the world a better place today (and every day): \u0026nbsp;Donate an hour. Instead of letting this gift of time fly past you, offer to help a friend with a project; volunteer at a local community center, animal shelter, or charity event; initiate an impromptu trash cleanup in your neighborhood… There are so many options! Whatever you choose, doing something thoughtful with that extra hour can go a long way.\u0026nbsp;\u0026nbsp;Have a lunch pack party. Feed s"
			}, {
				url: "http://blog.girlscouts.org/2017/11/celebrating-native-american-heritage.html",
				title: "Celebrating Native American Heritage Month Like a G.I.R.L.",
				id: "1763069877440354670",
				image: "https://4.bp.blogspot.com/-jhlHSHwkRMI/WfoLmKplYgI/AAAAAAAABRk/P8SieQ5t_tIQsBnGgldTETCQse6SfXw8QCLcBGAs/s640/17_GSUSA_Blog_Native-American-Heritage-Month_960x500.jpg",
				content: "November is Native American Indian Heritage Month! Throughout the month, we celebrate Native Americans’ diverse cultures and traditions and highlight the many contributions they’ve made throughout history—and at Girl Scouts, we of course especially focus on the Native American heroines. All month long, join Girl Scouts as we honor the amazing G.I.R.L. (Go-getter, Innovator, Risk-taker, Leader)™ spirit of Native American culture. The Go-GettersSacagswea Image via Library of CongressSacagaweaDuring the Lewis and Clark Expedition, Sacagawea served as a guide and interpreter whose mission was to find a water route through North America and explore the uncharted West. During this journey of more than two years, she interpreted the Mandan and Sho"
			}]
		};	
		
		addFeed(result1);
		styleFeed();
		trimFeed();
		
	} else {
		//var spinner = new Spinner(opts).spin($blogFeedArea);
		
		$.ajax({
			url: endpointurl,
			data: {
				count: blogCount, 
				pin1: pinPost1,
				pin2: pinPost2,
				pin3: pinPost3
				},
			dataType: "json",
			success: function(result) {
				console.log('GRIDBLOG');
				$blogFeedArea.empty();
				addFeed(result);
				styleFeed();
				trimFeed();			
				},
			error: function(jqXHR, status, error) {
				console.log('GRID BLOG FEED');
				console.log('ERROR: ' + error);
				console.log('STATUS: ' + status);
				},
			complete: function() {
				//spinner.stop();
				}
		});
	}

	function addFeed (result) {
		var startTime = new Date(),
			endTime;
		for (var i = 0; i < result.items.length; i++) {
			var data = result.items[i];
			var liwrapper = 
				'<li class="blogger">' + 
					'<a href="' + data.url + '" target="_blank">' + 
						'<div class="blogfeedwrapper">' +
							'<div class="blogfeedimage" style="' + 
								'background-image:url(\'' + data.image + '\');" ' +
							'>' +
								//'<img src="' + data.image + '" />' +
							'</div>' +
							'<div class="blogfeedcontent">' +
								'<div class="blogfeedtitle">' + data.title + '</div>' +
								'<div class="blogfeedsnippet">' + data.content + '</div>' +
							'</div>' +
						'</div>' + 
					'</a>' + 
				'</li>';			
			$blogFeedArea.append(liwrapper);	
		}
		endTime = new Date();
		console.log("addFeed() took " + (endTime-startTime)/1000 + " seconds");
	}

	function styleFeed() {
		var startTime = new Date(),
			endTime;
		console.log("feed");
		var imagesize, 
			titlelines, snippetlines, 
			titlefont, snippetfont, 
			titlelineheight, snippetlineheight;
		
		var feedWidth = $('.blog-feed-area').width();
		
		console.log("feed");
		if ($(window).width() > 768) {		// 768 breakpoint for mobile and desktop
			imagesize = desktopimagesize;
			titlelines = desktoptitlelines;
			snippetlines = desktopsnippetlines;
			titlefont = desktoptitlefont+'px';
			snippetfont = desktopsnippetfont+'px';
			titlelineheight = desktoptitlelineheight+'px';
			snippetlineheight = desktopsnippetlineheight+'px';
		} else {
			imagesize = mobileimagesize;
			titlelines = mobiletitlelines;
			snippetlines = mobilesnippetlines;
			titlefont = mobiletitlefont+'px';
			snippetfont = mobilesnippetfont+'px';
			titlelineheight = mobiletitlelineheight+'px';
			snippetlineheight = mobilesnippetlineheight+'px';
		}
		console.log("feed");
		$('.blogfeedimage').css({'width': imagesize + '%', 
								 'padding-bottom': imagesize + '%'});
		$('.blogfeedcontent').css('width', 100-imagesize + '%');
		$('.blogfeedtitle').css({'font-size': titlefont,
								 'line-height': titlelineheight});
		$('.blogfeedsnippet').css({'font-size': snippetfont,
								   'line-height': snippetlineheight});
		console.log("feed");
		endTime = new Date();
		console.log("styleFeed() took " + (endTime-startTime)/1000 + " seconds");
	}

	function trimFeed() {
		var startTime = new Date(),
			endTime;
		console.log("trim");
		var feedWidth = $('.blog-feed-area').width();
		console.log("trim");
		if ($(window).width() > 768) {		// 768 breakpoint for mobile and desktop
			titlelines = desktoptitlelines;
			snippetlines = desktopsnippetlines;
		} else {
			titlelines = mobiletitlelines;
			snippetlines = mobilesnippetlines;
		}
		$('.blogfeedtitle').each(function() {
			truncate(this, titlelines);
			});
		$('.blogfeedsnippet').each(function() {
			truncate(this, snippetlines);
			});
		console.log("trim");
		endTime = new Date();
		console.log("trimFeed() took " + (endTime-startTime)/1000 + " seconds");
	}
	
	
})

</script>
