<%@page import="org.girlscouts.vtk.helpers.ConfigManager,
                org.girlscouts.vtk.helpers.CouncilMapper" %>
<%@include file="/libs/foundation/global.jsp" %>
 
 
 <!-- 
 
 <a href="/content/girlscouts-vtk/en/vtk.html">go to VTK</a>
 
 <br/><br/>
 
 <a href="https://gsuat-gsmembers.cs11.force.com/members/">Community</a>
 
 -->
 
<%
HttpSession session = request.getSession();

org.girlscouts.vtk.auth.models.ApiConfig apiConfig =null;

try{

apiConfig = ((org.girlscouts.vtk.auth.models.ApiConfig)session.getAttribute(org.girlscouts.vtk.auth.models.ApiConfig.class.getName()));

}catch(Exception e){e.printStackTrace();}

int councilIdInt = 0;
try {
	councilIdInt = apiConfig.getTroops().get(0).getCouncilCode();
} catch (Exception e) {
    e.printStackTrace();
}

String councilId = Integer.toString(councilIdInt);
CouncilMapper mapper = sling.getService(CouncilMapper.class);
String branch = mapper.getCouncilBranch(councilId);
// language
branch += "/en/jcr:content";

ValueMap valueMap = (ValueMap)resourceResolver.resolve(branch).adaptTo(ValueMap.class);
boolean isHideSignIn = valueMap.get("hideVTKButton", "").equals("true");
boolean isHideMember = valueMap.get("hideMemberButton", "").equals("true");

// Get URL for community page
ConfigManager configManager = (ConfigManager)sling.getService(ConfigManager.class);
String communityUrl = "";
if (configManager != null) {
    communityUrl = configManager.getConfig("communityUrl");
}
%>

<!--
<% 
	out.print(councilId); 
	System.err.println("### Council Id: " + councilId);
%>
-->
 
<!-- apps/girlscouts/components/three-column-page/content.jsp -->
<!--PAGE STRUCTURE: MAIN-->
<div id="main" class="row">
<!--PAGE STRUCTURE: LEFT CONTENT START-->
<div class="large-5 hide-for-medium hide-for-small columns mainLeft">
<div id="leftContent">





<!-- apps/girlscouts/components/three-column-page/left.jsp -->
<div class="cascading-menus">













</ul>
<script>
$(document).ready(function() {
$('#main .side-nav li.active.current').parent().parent().find(">div>a").css({"font-weight":"bold", "color":"#414141"});
});
</script>
</div>

<div class="par parsys">
</div>


</div>
</div>
<!--PAGE STRUCTURE: LEFT CONTENT STOP-->

<!--PAGE STRUCTURE: MAIN CONTENT START-->
<div class="large-19 medium-24 small-24 columns mainRight">
<div class="breadcrumbWrapper">
<div class="breadcrumb-trail breadcrumb">






<nav class="breadcrumbs">

</nav>
</div>

</div>
<div class="row mainRightBottom">
<div class="large-18 medium-18 small-24 columns rightBodyLeft">
<!--PAGE STRUCTURE: MIDDLE CONTENT START-->





<!-- apps/girlscouts/components/three-column-page/middle.jsp -->
<div id="mainContent">
<div class="par parsys"><div class="text parbase section">
<h1>Welcome.</h1>



</div>
<div class="text parbase section">
<p><br />
<br />
</p>



</div>
<div class="grid-system nopadding section">





<ul class="large-block-grid-2 medium-block-grid-2 small-block-grid-1 "><li><div class="text parbase nopadding section">
<% if (!isHideSignIn) { %>
<!-- Begin of VTK icon -->
<table border="0" cellpadding="0" cellspacing="0"
style="border: none;">
<tbody><tr><td>
<a href="/content/girlscouts-vtk/en/vtk.html">
<img src="/etc/designs/girlscouts-vtk/images/btn_VTK.jpg"/>
</a>
</td>
</tr><tr style="background-color: white;border: none;"><td>If you&rsquo;re a Daisy, Brownie, or Junior troop leader, go here for access to an action-packed year of activities. You&rsquo;ll find everything you need for a fun-filled year all in one place&mdash;including meeting-by-meeting breakdowns of what to do, resources, meeting aids, and more!</td>
</tr></tbody></table>
<!-- End of VTK icon -->
<% } %>


</div>
</li><li><div class="text parbase nopadding section">
<% if (!isHideMember) { %>
<table border="0" cellpadding="0" cellspacing="0"
style="border: none;">
<tbody><tr><td>
<a href="<%= communityUrl %>"><img src="/etc/designs/girlscouts-vtk/images/btn_member_profile.jpg"/></a>
</td>
</tr><tr style="background-color: white;border: none;"><td>Do you want to change your member profile or contact details? Do you need to renew a membership? Go to the Girl Scout Member Community for access to your member profile.</td>
</tr></tbody></table>
<%}//edn if %>



</div>
</li><div style="clear:both"></div>
</ul></div>
<div class="text parbase section">

</div>

</div>

</div>

<!--PAGE STRUCTURE: MIDDLE CONTENT STOP-->
</div>
<!--PAGE STRUCTURE: RIGHT CONTENT START-->
<div id="rightContent" class="large-6 medium-6 small-24 columns">





<!-- apps/girlscouts/components/three-column-page/right.jsp -->
<div class="advertisement">










</div>


</div>
<!--PAGE STRUCTURE: RIGHT CONTENT STOP-->
</div>
</div>
<!--PAGE STRUCTURE: MAIN CONTENT STOP-->
</div>

<script type="text/javascript">
	var resizeWindow = function(){
		if(fixVerticalSizing) {
			var currentMainHeight = $('#main').height();
			var targetMainHeight = $(this).height() - $("#header").height() - $("#headerBar").height() - $("#footer").height() - 15;
			if (targetMainHeight > 1.1 * currentMainHeight) {
				$('#main').height(targetMainHeight);
			}
		}
	};
	window.onload = resizeWindow;
	$(window).resize(resizeWindow);
</script>