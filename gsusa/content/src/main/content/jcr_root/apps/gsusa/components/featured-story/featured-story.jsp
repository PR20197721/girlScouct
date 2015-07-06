<%@page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp" %>
<%
	if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
	   %><cq:includeClientLib categories="apps.gsusa.authoring" /><% 
	}
	String title = properties.get("title", "FEATURED STORY");
	String icon = properties.get("icon", "icon-photo-camera");
	String theme = properties.get("theme", "classic");

	String description = properties.get("description", "");
	if (WCMMode.fromRequest(request) == WCMMode.EDIT && description.isEmpty()) {
	    description = "No Description";
	}
	String bgcolor = properties.get("bgcolor", "E70C82");
	bgcolor = "rgba(" + hexToDec(bgcolor.substring(0, 2)) + ','
	        + hexToDec(bgcolor.substring(2, 4)) + ','
	        + hexToDec(bgcolor.substring(4, 6)) + ", .8)";

	String bg = "";
	try {
		bg = ((ValueMap)resource.getChild("bg").adaptTo(ValueMap.class)).get("fileReference", "");
	} catch (Exception e) {}
	
	if(theme.equals("classic")){
%>
<!-- <div><%= title %></div> -->
<div class="thumb" style="background-color: <%= bgcolor %>">
    <span class="<%= icon %>"></span>
    <div class="contents">
        <h3><%= title %></h3>
        <p class="dek"><%=description%></p>
    </div>
</div>
<section class="story" data-target="story_0"  style="background: url('<%=bg%>') no-repeat transparent 0 50% / cover">
    <div class="bg-wrapper" style="background-color: <%= bgcolor %>">
        <div class="header">
            <div class="left-wrapper">
                <span class="<%= icon %>"></span>
                <h3><%= title %></h3>
                <p class="dek"><%=description%></p>
            </div>
            <span class="icon-cross"></span>
        </div>
        <div class="contents clearfix">
            <cq:include path="par" resourceType="foundation/components/parsys" />
        </div>
    </div>
</section>
<% }else if(theme.equals("colorless")) { %>

<% } %>

<%
	// Get ready to hide parsys.
	if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
%>
		<script>
		    CQ.WCM.on("editablesready", function(){
	            var toggle = new gsusa.functions.ToggleParsys("<%= resource.getPath() %>/par");
	            gsusa.functions.ToggleParsysAll.refs.push(toggle);
	            toggle.hideParsys();
	        });
		</script>
<%
	}
%>

<%!
    public String hexToDec(String hex) {
    	return Integer.toString(Integer.parseInt(hex.trim(), 16));
    }
%>