<%@include file="/libs/foundation/global.jsp" %>
<%
	String text = properties.get("text","");
    String modalTitle = properties.get("modalTitle","");
	String imageAlt = properties.get("imageAlt","");
	String imageTitle = properties.get("imageTitle","");
	String sponsorImageTitle = properties.get("sponsorImageTitle","");
	String sponsorImageAlt = properties.get("sponsorImageAlt","");
	Resource thumbnail = resource.getChild("thumbnail");
	String filePath = "";
	if(thumbnail != null) {
		filePath = ((ValueMap)thumbnail.adaptTo(ValueMap.class)).get("fileReference", "");
	}
	
	Resource sponsorImage = resource.getChild("sponsor-image");
	String sponsorImagePath = "";
	if(thumbnail != null) {
		sponsorImagePath = ((ValueMap)sponsorImage.adaptTo(ValueMap.class)).get("fileReference", "");
	}
	String sponsorText = properties.get("sponsorText", "");

%>


<div class="vtk-banner-image">
	<a href="#" data-reveal-id="vtk-banner-modal">
		<img src="<%= filePath %>" alt="<%=imageAlt %>" title="<%=imageTitle %>" >
	</a>

	<div class="vtk-banner-button">
			<i class="icon-button-circle-cross"></i>
	</div>

</div>





<div id="vtk-banner-modal" class="reveal-modal" data-reveal aria-labelledby="modalTitle" aria-hidden="true" role="dialog">
	<div class="header clearfix">
		<h3 id="modalTitle"><%=modalTitle %></h2>
			 <a class="close-reveal-modal" aria-label="Close"><i class="icon-button-circle-cross"></i></a>
	</div>
	<img style="width:100%;height:auto;" src="<%= filePath %>" alt="<%=imageAlt %>" title="<%=imageTitle %>" >
	<div class="scroll content">

		<div><%=text %></div>
		
		<div class="sponsor">
			<p style="text-align: center; font-size: 12px;"><img src="<%=sponsorImagePath %>" style="margin-right: 5px;" align="middle" width="50px" alt="<%=sponsorImageAlt %>" title="<%= sponsorImageTitle%>"><%=sponsorText %></p>
		</div>
	</div>





</div>
