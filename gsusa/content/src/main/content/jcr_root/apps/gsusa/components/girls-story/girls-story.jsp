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
    } finally {
        if (reader != null)
            reader.close();
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
        <ul class="gs-stories-block"><%
        //use validStoryPath to generate the girls story component
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
                  <div id="<%= modalId %>" class="reveal-modal" data-reveal aria-labelledby="modalTitle" aria-hidden="true" role="dialog">
                    <cq:include path="<%= videoCompPath %>" resourceType="gsusa/components/video" />
                  </div>
                              
                  <li>
                    <div>
                      <img src="<%= imagePath %>" alt="<%= description %>"/>
                      <p><a href="#" data-reveal-id="<%= modalId %>" title="story title"><%= description %></a></p>
                    </div>
                  </li>
                <%
                
            } else {
            
            if (res != null && !res.getResourceType().equals("sling:nonexisting")) {
                ValueMap vm = (ValueMap) res.adaptTo(ValueMap.class);
                Resource imageRes = resourceResolver.resolve(storyPath + "/jcr:content/image");
                
                if (imageRes != null && !imageRes.getResourceType().equals("sling:nonexisting")) {
                    ValueMap imageVm = (ValueMap) imageRes.adaptTo(ValueMap.class);
                    String description = vm.get("description", "");
                    
                    if (!"".equals(imageVm.get("fileReference", ""))) { //it has an image
                        String imagePath = storyPath + "/jcr:content/image.img.png";%>
                        <li>
                          <div>
                            <img src="<%= imagePath %>" alt="<%= description %>"/>
                              <p><a href="#" title="story title"><%= description %></a></p>
                          </div>
                        </li><%
                    } else if (!"".equals(vm.get("jcr:videoLink", ""))) { //it has an video link
                        if ((vm.get("jcr:videoLink", "").indexOf("youtube")) != -1) { //youtube video
                            String ytId = extractYTId(vm.get("jcr:videoLink", ""));
                            String imagePath = "https://i1.ytimg.com/vi/" + ytId +"/hqdefault.jpg";%>
                            <span class="icon-play"></span>
                            <li>
                              <div>
                                <img src="<%= imagePath %>" alt="<%= description %>" height=200px width=200px/>
                                <p><a href="#" title="story title"><%= description %></a></p>
                              </div>
                            </li><%
                        } else if ((vm.get("jcr:videoLink", "").indexOf("vimeo")) != -1) { //vimeo
                            String vimeoId = extractVimeoId(vm.get("jcr:videoLink", ""));
                            String jsonOutput = readUrlFile("http://vimeo.com/api/v2/video/" + vimeoId + ".json");
                            JSONArray json = new JSONArray(jsonOutput);
                            String imagePath = "";
                            if (!json.isNull(0)) {
                                imagePath = json.getJSONObject(0).getString("thumbnail_large");
                            }%>
                            <span class="icon-play"></span>
                            <li>
                              <div>
                                <img src="<%= imagePath %>" alt="<%= description %>" height=200px width=200px/>
                                <p><a href="#" title="story title"><%= description %></a></p>
                              </div>
                            </li><%
                        } else {%>
                            <li>
                              <div>
                                  <p>We do not support this video link</p>
                                <p><a href="#" title="story title"><%= description %></a></p>
                              </div>
                            </li><%
                        }
                      } else if (!"".equals(vm.get("jcr:videoPath", ""))) { //it has an video
                    } else {
                        //something is wrong: it has no image, video, or video path%>
                        <li>
                          <div>
                              <p>This girl story does not have an image, a video link or a video path</p>
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

