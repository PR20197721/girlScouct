<%@include file="/libs/foundation/global.jsp" %>
<%@page import="org.apache.sling.commons.json.*,
				java.io.*,
				java.util.regex.*,
				java.net.*,
				org.apache.sling.commons.json.*" %>

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
    } finally {
        if (reader != null)
            reader.close();
    }
}
%>


<%
	String[] emptyArray = new String[1];
    String content = properties.get("content", "Join Now");
    String btnName = properties.get("button", "Explore Girl Scouts");
    String title = properties.get("title", "Introduce girls to");
    String[] imagePathArray = properties.get("imagePath", emptyArray);
    
    String[] content2 = properties.get("content2", emptyArray);
    String[] imagePath2 = properties.get("imagePath2", emptyArray);
    String[] subtitle2 = properties.get("subtitle2", emptyArray);
    String title2 = properties.get("title2", "");
    
    String[] content3 = properties.get("content3", emptyArray);
    String[] imagePath3 = properties.get("imagePath3", emptyArray);
    String[] subtitle3 = properties.get("subtitle3", emptyArray);
    String title3 = properties.get("title3", "");
    
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
    
    String title6 = properties.get("title6", "");
    String content6 = properties.get("content6", "");
    String imagePath6 = properties.get("imagePath6", "");
    
    for (int i = 0 ; i < 4; i++ ){
    	if ("link".equals(videoType5[i])) {
    		String link = properties.get("videoLink5" + i, "");
    		if (link.indexOf("youtube") != -1) {
    			String ytId = extractYTId(link);
    			videoId[i] = ytId;
    			videoThumbNail[i] = "https://i1.ytimg.com/vi/" + ytId +"/hqdefault.jpg";
    			embeded[i] = "<iframe width=\"420\" height=\"315\" src=\"https://www.youtube.com/embed/" + ytId + "\" frameborder=\"0\" allowfullscreen></iframe>";
    		} else if (link.indexOf("vimeo") != -1) {
    			String vimeoId = extractVimeoId(link);
    			videoId[i] = vimeoId;
    			String jsonOutput = readUrlFile("http://vimeo.com/api/v2/video/" + vimeoId + ".json");
	            JSONArray json = new JSONArray(jsonOutput);
	            if (!json.isNull(0)) {
	    			videoThumbNail[i] = json.getJSONObject(0).getString("thumbnail_large");
        		}
	            embeded[i] = "<iframe src=\"https://player.vimeo.com/video/"+ vimeoId +"\" width=\"500\" height=\"281\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe> <p><a href=\"https://vimeo.com/"+ vimeoId +"\">Spheres</a> from <a href=\"https://vimeo.com/regishervagault\">Regis Hervagault</a> on <a href=\"https://vimeo.com\">Vimeo</a>.</p>";
    		} else {
    			videoThumbNail[i] = "not supported";
    		}
    	}
    }
%>


<div class="hero-feature">
    <div class="overlay"></div>
    <ul class="main-slider">
<% 	for (int i = 0 ; i < imagePathArray.length; i++) {%>
        <li>
            <img src="<%=imagePathArray[i] %>" class="slide-thumb"/>
        </li>
    <%}
%>
    </ul>
    <div class="hero-text first">
        <section>
            <img src="/etc/designs/gsusa/clientlibs/images/white_trefoil.png" alt="icon" />
            <h2><%= title %></h2>
            <p><%= content %></p>
            <a href="#" class="button explore"><%= btnName %></a>
        </section>
    </div>
    <div class="position">
        <div class="inner-sliders">
            <ul class="inner">
                <li>
                    <ul class="slide-1"> 
                    <% 	for (int i = 0 ; i < imagePath2.length; i++) {%>
                        <li>
                            <h3><%= title2 %></h3>
                            <div class="text white">
                                <h4><%= subtitle2[i] %></h4>
                                <p><%= content2[i] %></p>
                            </div>
                            <img src="<%= imagePath2[i] %>" alt="" class="slide-thumb"/>
                        </li>
                        <%} %>
                    </ul>
                </li>
                <li>
                    <ul class="slide-2">
                    <% 	for (int i = 0 ; i < imagePath3.length; i++) {%>
                        <li>
                            <h3><%= title3 %></h3>
                            <div class="text white">
                                <h4><%= subtitle3[i] %></h4>
                                <p><%= content3[i] %></p>
                            </div>
                            <img src="<%= imagePath3[i] %>" alt="" class="slide-thumb"/>
                        </li>
                    <%} %>
                    </ul>
                    <li>
                        <ul class="slide-3">
                            <li>
                                <h3><%= title4 %></h3>
                                <cq:include path="content/facebook-feed" resourceType="gsusa/components/facebook-feed" />
                            </li>
                        </ul>
                    </li>
                    <li>
                        <ul class="slide-4">
                        <%
                        for (int i = 0 ; i < 4; i++) {
                            if ("link".equals(videoType5[i])) {%>
		                        <li>
		                            <h3><%= title5 %></h3>
		                            <div class="video-wrapper">
		                                <div class="video">
		                                <img src="<%= videoThumbNail[i]%>" alt="" class="slide-thumb"/>
		                                <%= embeded[i] %>
		                                </div>
		                                <div class="video-article">
		                                    <h4><%= subtitle5[i] %></h4>
		                                    <p><%= content5[i]%></p>
		                                </div>
		                            </div>
		                        </li>
                        <%	} else { 
                        	//path
                        	}
                        }
                        %>
                        
                    </ul>
                    </li>
                </li>
            </ul>
        </div>
    </div>
    <div class="final-comp">
        <div class="hero-text">
                <section>
                    <img src="/etc/designs/gsusa/clientlibs/images/white_trefoil.png" alt="icon">
                    <h2><%= title6%></h2>
                    <p><%= content6 %> </p>
                    <form action="#" name="join-now" class="join-now-form clearfix">
                        <input type="text" class="join-text hide" placeholder="Enter Zip code">
                        <a href="#" class="button join-now">Join Now</a>
                    </form>
                </section>
            </div>
        <img src="<%= imagePath6 %>" alt="" class="main-image" />
    </div>
    <cq:include path="content/zip-council" resourceType="gsusa/components/zip-council" />
</div>
