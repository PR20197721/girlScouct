<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@page import="com.day.cq.wcm.api.WCMMode" %>

<cq:includeClientLib categories="girlscouts.components.hero-banner"/>

<script>
	SlideShowManager.removeAll('<%= resource.getPath() + "_slideshow" %>');
</script>

<%
	Iterator<Resource> images = resource.getChild("slides").listChildren();
	while(images.hasNext()){
		Resource imgResource = images.next();
%>
<div>
	<div>
		<cq:include path="<%=imgResource.getPath()%>" resourceType="girlscouts/components/hero-slideshow-images"/>
	</div>
</div>
<%
	}
%>

<div class="slide-show-target" data-slide-show-path="<%= resource.getPath() + "_slideshow" %>"></div>
<script>
	SlideShowManager.init("slide-show-target", '<%= resource.getPath() + "_slideshow" %>', <%= WCMMode.fromRequest(request) == WCMMode.EDIT %>, '<%= request.getAttribute("HeroBannerTimer").toString() %>');
</script>