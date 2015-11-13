<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>

<div class="video-slider-wrapper">
<%
	String[] links = properties.get("links",String[].class);
	String alt = "";
	if(links != null && links.length > 0){
		for(int i = 0; i < links.length; i++){
			if(resourceResolver.resolve(links[i]).getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
				%><div><iframe src="<%= links[i] %>" frameborder="0" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></div><%
			} else{
				alt = "Image slider " + i;
				%><div><img src="<%= links[i] %>" alt="<%= alt %>" /></div> <%
			}
		}
	} else{
		%> <div>***** Please add a video or image *****</div>
		<div>***** Please add a video or image *****</div> <%
	}
%>
</div>