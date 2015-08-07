<%@page import="com.day.cq.wcm.api.WCMMode, com.day.cq.wcm.foundation.Placeholder" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%!
	public String hexToDec(String hex) {
		return Integer.toString(Integer.parseInt(hex.trim(), 16));
	}
%>
<%
	if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
	   %><cq:includeClientLib categories="apps.gsusa.authoring" /><%
	}
	String title = properties.get("title", "FEATURED STORY");
	String icon = properties.get("icon", "icon-photo-camera");
	String theme = properties.get("theme", "classic");
	String btnText = properties.get("buttonText", "");
	String btnLink = properties.get("buttonLink", "#");
	int featureIndex = 60 + (Integer) request.getAttribute("index");
	String text = properties.get("text", "");
	if(text.length() > 350){
		text = text.substring(0,350) + "...";
	}

	String description = properties.get("description", "");
	if (WCMMode.fromRequest(request) == WCMMode.EDIT && description.isEmpty()) {
		description = "No Description";
	}
	String bgcolor = properties.get("bgcolor", "E70C82");

	String bgcolorClassic = "rgba(" + hexToDec(bgcolor.substring(0, 2)) + ','
			+ hexToDec(bgcolor.substring(2, 4)) + ','
			+ hexToDec(bgcolor.substring(4, 6)) + ", .9)";

	String bgcolorCL = "rgba(" + hexToDec(bgcolor.substring(0, 2)) + ','
			+ hexToDec(bgcolor.substring(2, 4)) + ','
			+ hexToDec(bgcolor.substring(4, 6)) + ", 0)";

	String bg = "";
        try {
            bg = ((ValueMap)resource.getChild("bg").adaptTo(ValueMap.class)).get("fileReference", "");
        } catch (Exception e) {}

	String noPadding = "";
	if (properties.get("noPadding", "false").equals("true")) {
		noPadding = " no-padding";
	}

%>
<!-- <div><%= title %></div> -->
<div class="thumb" style="background-color: <%= bgcolorClassic %>">
	<span class="<%= icon %>"></span>
	<div class="contents" tabindex="<%= featureIndex %>">
		<h3><%= title %></h3>
		<p class="dek"><%=description%></p>
	</div>
</div>

<%
if(theme.equals("classic")){
	try {
		bg = getImageRenditionSrc(resourceResolver, bg, "cq5dam.npd.hero.");
	} catch (Exception e) {}
%>
<section class="story<%= noPadding %>" data-target="story_0"  style="background: url('<%=bg%>') no-repeat transparent center center / cover">
	<div id="tag_tile_<%= linkifyString(title, 25)%>" class="bg-wrapper" style="background-color: <%= bgcolorClassic %>">
		<div class="header">
			<div class="left-wrapper">
				<span class="<%= icon %>"></span>
				<h3><%= title %></h3>
				<%= text %>
			</div>
			<span class="icon-cross"></span>
		</div>
		<div class="contents clearfix">
			<cq:include path="par" resourceType="foundation/components/parsys" />
		</div>
	</div>
</section>

<%
} else if(theme.equals("colorless")) {
        try {
                bg = getImageRenditionSrc(resourceResolver, bg, "cq5dam.npd.top.");
        } catch (Exception e) {}
%>
<section class="story colorless<%= noPadding %>" data-target="story_0"  style="background: url('<%=bg%>') no-repeat transparent center center / cover">
	<div id="tag_tile_<%= linkifyString(title, 25)%>" class="bg-wrapper" style="background-color: <%= bgcolorCL %>">
		<div class="header">
			<span class="icon-cross"></span>
		</div>
		<div class="contents">
			<div class="left-wrapper" style="background-color: <%= bgcolorClassic %>">
				<span class="<%= icon %>"></span>
				<h3><%= title %></h3>
				<div class="text">
					<%= text %>
				</div>
				<a  id="tag_tile_button_<%= linkifyString(title, 25)%>" href="<%= btnLink %>" class="button" style="background-color: <%= bgcolorClassic %>"><%= btnText %></a>
			</div>
		</div>
	</div>
</section>
<%
} else if(theme.equals("shop")){
        try {
                bg = getImageRenditionSrc(resourceResolver, bg, "cq5dam.npd.right.");
        } catch (Exception e) {}
%>
<section class="story<%= noPadding %>" data-target="story_0"  style="background: url('<%=bg%>') no-repeat transparent center center / cover">
	<div id="tag_tile_<%= linkifyString(title, 25)%>" class="bg-wrapper" style="background-color: <%= bgcolorClassic %>">
		<div class="header">
			<div class="left-wrapper">
				<span class="<%= icon %>"></span>
				<h3><%= title %></h3>
			</div>
			<span class="icon-cross"></span>
		</div>
		<div class="contents clearfix">
			<cq:include path="shop-tile" resourceType="gsusa/components/shop-tile" />
		</div>
	</div>
</section>
<%
} else if(theme.equals("social")){
        try {
                bg = getImageRenditionSrc(resourceResolver, bg, "cq5dam.npd.right.");
        } catch (Exception e) {}
%>
<section class="story<%= noPadding %>" data-target="story_0"  style="background: url('<%=bg%>') no-repeat transparent center center / cover">
	<div  id="tag_tile_<%= linkifyString(title, 25)%>" class="bg-wrapper" style="background-color: <%= bgcolorClassic %>">
		<div class="header">
			<div class="left-wrapper">
				<span class="<%= icon %>"></span>
				<h3><%= title %></h3>
			</div>
			<span class="icon-cross"></span>
		</div>
		<div class="contents clearfix">
			<cq:include path="social-feed" resourceType="gsusa/components/social-feed-tile" />
		</div>
	</div>
</section>
<%
} else if(theme.equals("video")){
        try {
                bg = getImageRenditionSrc(resourceResolver, bg, "cq5dam.npd.middle.");
        } catch (Exception e) {}
%>
<section class="story video<%= noPadding %>" data-target="story_0"  style="background: url('<%=bg%>') no-repeat transparent center center / cover">
	<div  id="tag_tile_<%= linkifyString(title, 25)%>" class="bg-wrapper" style="background-color: <%= bgcolorClassic %>">
		<div class="header">
			<div class="left-wrapper hide-for-small">
				<span class="<%= icon %>"></span>
				<h3><%= title %></h3>
			</div>
			<span class="icon-cross"></span>
		</div>
		<div class="contents clearfix">
			<div class="right-float">
				<div class="video">
					<cq:include path="video-carousel" resourceType="gsusa/components/video-carousel" />
				</div>
			</div>
			<div class="left-float mobile-header">
				 <div class="show-for-small">
					<span class="<%= icon %>"></span>
					<h3><%= title %></h3>
				</div>
				<div class="text">
					<%= text %>
				</div>
				<div class="mobile-centered">
					<a id="tag_tile_button_<%= linkifyString(title, 25)%>" href="<%= btnLink %>" class="button" style="background-color: <%= bgcolorClassic %>"><%= btnText %></a>
				</div>
			</div>

		</div>
	</div>
</section>
<%
	}
	// Get ready to hide parsys.
	if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
%>
		<script type="text/javascript">
			CQ.WCM.on("editablesready", function(){
				var toggle = new gsusa.functions.ToggleParsys("<%= resource.getPath() %>/par");
				gsusa.functions.ToggleParsysAll.refs.push(toggle);
				toggle.hideParsys();
			});
		</script>
<%
	}
%>
