<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@page import="com.day.cq.wcm.api.WCMMode" %>
<%@ page import="java.util.*" %>

<cq:includeClientLib categories="girlscouts.components.hero-banner"/>

<script>
	SlideShowManager.removeAll('<%= resource.getPath() + "_slideshow" %>');
</script>

<%
    Resource slides = resource.getChild("slides");
	Iterator<Resource> images = Optional.ofNullable(slides).map(Resource::listChildren).orElse(new ArrayList().iterator());
	while(images.hasNext()){
		Resource imgResource = images.next();
		if(!imgResource.getValueMap().get("hidden", Boolean.FALSE)){
			%>
			<div>
				<div>
					<cq:include path="<%=imgResource.getPath()%>" resourceType="girlscouts/components/hero-slideshow-images"/>
				</div>
			</div>
			<%
		}
	}
%>
<div class="slide-show-target" data-slide-show-path="<%= resource.getPath() + "_slideshow" %>"></div>
<script>
	SlideShowManager.init("slide-show-target", '<%= resource.getPath() + "_slideshow" %>', <%= WCMMode.fromRequest(request) == WCMMode.EDIT %>, '<%= request.getAttribute("HeroBannerTimer").toString() %>');
</script>