<%@include file="/libs/foundation/global.jsp" %>
<%
	String text = properties.get("text","");
    String modalTitle = properties.get("modalTitle","");
	String imageAlt = properties.get("imageAlt","");
	String imageTitle = properties.get("imageTitle","");
	Resource thumbnail = resource.getChild("thumbnail");
	String filePath = "";
	if(thumbnail != null) {
		filePath = ((ValueMap)thumbnail.adaptTo(ValueMap.class)).get("fileReference", "");
	}

%>


<div>
	<a href="#" data-reveal-id="vtk-banner-modal">
		<img src="<%= filePath %>" alt="<%=imageAlt %>" title="<%=imageTitle %>" >
	</a>
	<img src="https://developer.blackberry.com/devzone/files/design/bb10/images/icon_close.png">
</div>



<div id="vtk-banner-modal" class="reveal-modal tiny" data-reveal aria-labelledby="modalTitle" aria-hidden="true" role="dialog">
   <h2 id="modalTitle"><%=modalTitle %></h2>
   <img src="<%= filePath %>" alt="<%=imageAlt %>" title="<%=imageTitle %>" >
   <p><%=text %></p>
 
   <a class="close-reveal-modal" aria-label="Close">&#215;</a>

</div>


