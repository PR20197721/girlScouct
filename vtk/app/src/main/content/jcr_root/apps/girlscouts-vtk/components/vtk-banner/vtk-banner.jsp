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
	
	Resource modalImage = resource.getChild("modal-image");
	String modalImagePath = "";
	if(modalImage != null) {
		modalImagePath = ((ValueMap)modalImage.adaptTo(ValueMap.class)).get("fileReference", "");
	}

	Resource sponsorImage = resource.getChild("sponsor-image");
	String sponsorImagePath = "";
	if(thumbnail != null) {
		sponsorImagePath = ((ValueMap)sponsorImage.adaptTo(ValueMap.class)).get("fileReference", "");
	}
	String sponsorText = properties.get("sponsorText", "");

%>


<div class="vtk-banner-image">
	<a href="#" data-reveal-id="vtk-banner-modal-<%=resource.getName()%>" data-effect="modal" data-reveal-init data-options="animation:'none'" >
		<img src="<%= filePath %>" alt="<%=imageAlt %>" title="<%=imageTitle %>" >
	</a>

	<div class="vtk-banner-button">
			<i class="icon-button-circle-cross"></i>
	</div>

</div>


<div id="vtk-banner-modal-<%=resource.getName()%>" data-reveal data-options="close_on_background_click:false; close_on_esc: false;" class="reveal-modal" aria-labelledby="modalTitle" aria-hidden="true" role="dialog">
	<div class="header clearfix">
		<h3 id="modalTitle"><%=modalTitle %></h3>
	    <a class="close-reveal-modal" aria-label="Close"><i class="icon-button-circle-cross"></i></a>
	</div>
	<div>
			<img id="banner-image" class="banner-image" draggable="false" style="width:100%;height:auto;pointer-events: none" src="<%= modalImagePath %>"   alt="<%=imageAlt %>" title="<%=imageTitle %>" >
	</div>

	<div class="scroll-banner content">
		

		<div class="reset"><%=text %></div>

			<% if (!"".equals(sponsorImagePath) || !"".equals(sponsorImageTitle)) { %>
					<div class="sponsor">
						<p style="text-align: center; font-size: 12px;">
							<img src="<%=sponsorImagePath %>" style="margin-right: 5px;" align="middle" width="50px" alt="<%=sponsorImageAlt %>" title="<%= sponsorImageTitle%>">
							<%=sponsorText %>
						</p>
					</div>
			<%} %>
	</div>
</div>


<script>



$(function(){

	overFlowY = false;

	function setHeightSS(p){
		
		var image = $('.banner-image');
		var scroll = $('.scroll-banner');
		var height = $(window).height();
		var imageHeight;
		var modalwidth = $('#vtk-banner-modal-<%=resource.getName()%>').innerWidth();
		var realimgheight = document.getElementById('banner-image').height;
		var realimgwidth = document.getElementById('banner-image').width;


			imageHeight = image.height();


		scroll.css(
			{
				'maxHeight':$(window).height()-imageHeight-75+'px',
				'overflow-y':'auto'
			}
		);




	}

	$(window).on('resize',function(){
			setHeightSS();
		})	



	$('#vtk-banner-modal-<%=resource.getName()%>"').bind('opened', function() {
  		setHeightSS(true);
  	
	});



	
		



});
</script>


