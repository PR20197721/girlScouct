<%@include file="/libs/foundation/global.jsp" %>

<%@page import="com.day.cq.wcm.api.WCMMode,
    java.util.ArrayList,
    java.util.Iterator,
    java.util.Collections,
    java.util.regex.Pattern,
    java.util.regex.Matcher,
    java.io.BufferedReader,
    java.net.*,
    java.io.InputStreamReader,
    org.apache.jackrabbit.commons.json.JsonParser,
    org.apache.sling.commons.json.*,
    com.day.cq.wcm.foundation.Image" %>
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

<%@page session="false"%><%
    String path = properties.get("path", "");
    String[] storyPathArray = properties.get("storypath", String[].class);
    String selected = properties.get("source", "");
    ArrayList<String> validStoryPath = new ArrayList<String>();
    Boolean firstTimeInit = true;
    PageManager pm = resourceResolver.adaptTo(PageManager.class);

    if (selected.equals("dir")) { //user picks a directory
        if (!path.isEmpty()) {
            //grab the directory and get all its children
            firstTimeInit = false;
            Page parent = pm.getPage(path);
            Iterator<Page> children = parent.listChildren();
            while (children.hasNext()){
                Page child = children.next();
                Node childNode = child.adaptTo(Node.class);
                Node contentNode = childNode.getNode("jcr:content");
                //TODO: we need a more robust validation.
                //Currently only check to see if there is a description
    
                if(contentNode.hasProperty("description")){
                    validStoryPath.add(child.getPath());
                }
            }
        }
    } else { //manually picked
        if (storyPathArray != null) {
            for(String storyPath: storyPathArray){
                if (!storyPath.isEmpty()) {
                    firstTimeInit = false;
                    Page p = pm.getPage(storyPath);
                    Node pageNode = p.adaptTo(Node.class);
                    Node contentNode = pageNode.getNode("jcr:content");
                    //TODO: we need a more robust validation.
                    //Currently only check to see if there is a description

                    if(contentNode.hasProperty("description")){
                        validStoryPath.add(storyPath);
                    }
                }
            }
        }
    }

    //if the validStoryPath is still empty after all the data processing
    //the user input is likely to be invalid. Set it to firstTimeInit
    if (validStoryPath.isEmpty()) {
        firstTimeInit = true;
    }
    %>

    <%if (!firstTimeInit) { %>
    	<div>
        <ul class="our-stories-block"><%
        //use validStoryPath to generate the our story component
        for (String storyPath: validStoryPath) {
            Resource res = resourceResolver.resolve(storyPath + "/jcr:content");
            ValueMap resProp  = res.adaptTo(ValueMap.class);
            if (resProp.get("type", "").equals("video")) {
                String videoCompPath = storyPath + "/jcr:content/video";
                String videoPath = res.adaptTo(Node.class).getNode("video").getProperty("asset").getString();
                String imagePath = videoPath + "/jcr:content/renditions/cq5dam.thumbnail.319.319.png";
                String description = resProp.get("description", "");
                String modalId = "modal-" + Integer.toString((int)(Math.random() * 900) + 100);
                %>
                  <div id="<%= modalId %>" class="reveal-modal our-story-general-popup" data-reveal aria-labelledby="modalTitle" aria-hidden="true" role="dialog">
                    <cq:include path="<%= videoCompPath %>" resourceType="gsusa/components/video" />
                  </div>
                  <li>
                    <div>
                      <img src="<%= imagePath %>" alt="<%= description %>"/>
                      <p><a href="#" data-reveal-id="<%= modalId %>" title="story title"><%= description %></a></p>
                    </div>
                  </li>
                <%
            } else if (resProp.get("type", "").equals("external-video")) {
            	if (resProp.get("externalVideo", "").indexOf("youtube") != -1) {
	                String description = resProp.get("description", "");
	                String ytId = extractYTId(resProp.get("externalVideo", ""));
                    String imagePath = "https://i1.ytimg.com/vi/" + ytId +"/hqdefault.jpg";
                    String modalId3 = "modalVideo-" + Integer.toString((int)(Math.random() * 10000) + 1000);%>
                    <div id="<%= modalId3 %>" class="reveal-modal large our-story-video-popup" data-reveal aria-labelledby="modalTitle" aria-hidden="true" role="dialog">
                    	<iframe align="middle" width="100%" height="auto" overflow="scroll" src="https://www.youtube.com/embed/<%= ytId %>" frameborder="0" allowfullscreen></iframe>
                    </div>
                    <li>
                      <div>
                        <img src="<%= imagePath %>" alt="<%= description %>" height=200px width=200px/>
                        <p><a href="#" data-reveal-id="<%= modalId3 %>" title="story title"><%= description %></a></p>
                      </div>
                    </li><%
            	} else if (resProp.get("externalVideo", "").indexOf("vimeo") != -1) {
            		String description = resProp.get("description", "");
                	String vimeoId = extractVimeoId(resProp.get("externalVideo", ""));
                    String jsonOutput = readUrlFile("http://vimeo.com/api/v2/video/" + vimeoId + ".json");
                    String modalId4 = "modalVimeo-" + Integer.toString((int)(Math.random() * 10000) + 1000);
                    JSONArray json = new JSONArray(jsonOutput);
                    String imagePath = "";
                    if (!json.isNull(0)) {
                        imagePath = json.getJSONObject(0).getString("thumbnail_large");
                    }%>
                    <div id="<%= modalId4 %>" class="reveal-modal our-story-video-popup" data-reveal aria-labelledby="modalTitle" aria-hidden="true" role="dialog">
                    	<iframe src="https://player.vimeo.com/video/<%=vimeoId %>?badge=0" width="500" height="281" frameborder="0" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>
                    </div>
                    <li>
                      <div>
                        <img src="<%= imagePath %>" alt="<%= description %>" height=200px width=200px/>
                        <p><a href="#" data-reveal-id="<%= modalId4 %>" title="story title"><%= description %></a></p>
                      </div>
                    </li><%
            	} else {
            		//not supported
            		String description = resProp.get("description", "");
                	//something is wrong: this video is not supported%>
                    <li>
                      <div>
                          <p>This story does not have a supported video.</p>
                        <p><a href="#" title="story title"><%= description %></a></p>
                      </div>
                    </li><%
            	}
            } else {
	            if (res != null && !res.getResourceType().equals("sling:nonexisting")) {
	                ValueMap vm = (ValueMap) res.adaptTo(ValueMap.class);
	                Resource imageRes = resourceResolver.resolve(storyPath + "/jcr:content/image");
	                
	                if (imageRes != null && !imageRes.getResourceType().equals("sling:nonexisting")) {
	                    ValueMap imageVm = (ValueMap) imageRes.adaptTo(ValueMap.class);
	                    String description = vm.get("description", "");
	                    String modalId2 = "modalImage-" + Integer.toString((int)(Math.random() * 10000) + 1000);
	                    
	                    if (!"".equals(imageVm.get("fileReference", ""))) { //it has an image
	                        String imagePath = storyPath + "/jcr:content/image.img.png";%>
	                        <div id="<%= modalId2 %>" class="reveal-modal large our-story-general-popup" data-reveal aria-labelledby="modalTitle" aria-hidden="true" role="dialog">
							  <nav class="top-bar" data-topbar role="navigation">
								  <ul class="title-area">
								    <li class="name">
								      <img src="/content/dam/girlscouts-gsusa/images/logo/logo.png" alt="gsusa home"/>
								    </li>
								  </ul>
								
								  <section class="top-bar-section">
								    <ul class="right">
								      <li><span class="icon-cross"></span></li>
								    </ul>
								  </section>
								</nav>
							  <img src="<%= imagePath %>" alt="<%= description %>"/>
							  <p>Hed - dek</p>
							  <p>Lorem ipsum dolor sit amet ..Lorem ipsum dolor sit amet .. Lorem ipsum dolor sit amet ..Lorem ipsum dolor sit amet .. Lorem ipsum dolor sit amet ..Lorem ipsum dolor sit amet .. Lorem ipsum dolor sit amet ..Lorem ipsum dolor sit amet ..</p>
							  <p>Lorem ipsum dolor sit amet ..Lorem ipsum dolor sit amet ..</p>
			                </div>
	                        <li>
	                          <div>
	                            <img src="<%= imagePath %>" alt="<%= description %>"/>
	                              <p><a href="#" data-reveal-id="<%= modalId2 %>" title="story title"><%= description %></a></p>
	                          </div>
	                        </li>
	                        <script>
	                          $("#<%= modalId2 %>").on("click", function (e) {
                              	$("#<%= modalId2 %>").foundation('reveal', 'close');
							  });
	                        </script><%
	                    } else {
	                        //something is wrong: it has no image, video, or video path%>
	                        <li>
	                          <div>
	                              <p>This story does not have an image, a video link or a video path</p>
	                            <p><a href="#" title="story title"><%= description %></a></p>
	                          </div>
	                        </li><%
	                    }
	                }
	            }
	        }
        }%>
        </ul></div><%
        return;
    } else if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
        %>Please click here to edit. <%
        return;
    }

%>

