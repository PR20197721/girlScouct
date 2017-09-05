<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%
String backgroundPath = currentSite.get("bgImagePath",String.class);
if(backgroundPath != null && backgroundPath.trim().length() > 0){
	try{
		Resource content = resourceResolver.getResource(backgroundPath);	
		if(content != null && content.hasChildren()){
			StringBuffer sb = new StringBuffer();
			Iterable<Resource> backgrounds = content.getChildren();
			for (Resource background : backgrounds) {
				if(background.isResourceType("dam:Asset")){
					sb.append("\"").append(background.getPath()).append("\",");
				}			
			}
			%>
            <div class="main-bg">
                <img />
            </div>
            <script>
                // Don't include "src" attribute in img or broken link icon will appear until the image is loaded
                (function () {
                    "use strict";

                    // Set random full-page background image
                    // Select from an array to allow for custom image names
                    var images = [
                            <%=sb.substring(0, sb.length()-1)%>
                        ],
                        rand = Math.floor(Math.random() * images.length), // Returns 0 through length-1
                        bg = $(".main-bg"),
                        bgImage = $(".main-bg > img"),
                        header = {
                            wrapper: undefined,
                            banner: undefined,
                            wrapperHeight: 0,
                            bannerHeight: 0
                        };
                        
                    function fixWhitespace() {
                        var wrapperHeight = header.wrapper.outerHeight(true),
                            bannerHeight = header.banner.outerHeight(true);
                        
                        // Check to see if height needs to be changed before calling expensive CSS function
                        if (header.wrapperHeight !== wrapperHeight || header.bannerHeight !== bannerHeight) {
                            bg.css({
                                "min-height": wrapperHeight + bannerHeight + 6 // 6px extra for #main border-radius
                            });
                            header.wrapperHeight = wrapperHeight;
                            header.bannerHeight = bannerHeight;
                        }
                    }

                    if (images.length > 0) {
                        // Change background color
                        document.body.style.background = "#000000";

                        // Add class for fade-in effect on image load
                        bgImage.on("load", function () {
                            bg.addClass("show");
                        });

                        // Load image
                        bgImage[0].src = images[rand];

                        // Adjust min-height for mobile to prevent whitespace
                        $(function () { // Call once on document ready
                            header.wrapper = $(".header-wrapper");
                            header.banner = $(".page-banner-title h1");
                            fixWhitespace();
                        });
                        $(window).resize(function () { // Call again whenever the window is resized
                            fixWhitespace();
                        });
                    }
                }());
            </script>
		<%
		}
	}catch(Exception e){
		System.err.println("Error occurred while rendering background images at "+backgroundPath);
		e.printStackTrace();
	}
}
%>

