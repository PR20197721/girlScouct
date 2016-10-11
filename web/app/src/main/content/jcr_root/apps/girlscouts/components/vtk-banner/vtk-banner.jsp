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


<div class="vtk-banner-image">
	<a href="#" data-reveal-id="vtk-banner-modal">
		<img src="<%= filePath %>" alt="<%=imageAlt %>" title="<%=imageTitle %>" >
	</a>

	<div class="vtk-banner-button">
			<i class="icon-button-circle-cross"></i>
	</div>

</div>





<div id="vtk-banner-modal" class="reveal-modal tiny" data-reveal aria-labelledby="modalTitle" aria-hidden="true" role="dialog">
	<div class="header clearfix">
		<h3 id="modalTitle"><%=modalTitle %></h2>
			 <a class="close-reveal-modal" aria-label="Close"><i class="icon-button-circle-cross"></i></a>
	</div>
	<img style="width:100%;height:auto;" src="<%= filePath %>" alt="<%=imageAlt %>" title="<%=imageTitle %>" >
	<div class="scroll content">

		<div><%=text %></div>


		<p style="text-align: center; font-size: 10px;"><img src="" alt="" /> Get Outdoors! is made possible by the <a target="_blank" href="http://fdnweb.org/rkmf/">Richard King Mellon Foundation.</a></p>
	</div>





</div>
