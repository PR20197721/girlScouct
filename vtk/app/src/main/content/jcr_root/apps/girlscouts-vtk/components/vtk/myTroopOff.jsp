  <!-- Image Crop and Accordion Sample -->
  
   <%
    String troopId= troop.getTroop().getTroopId();
    if( troopId ==null || troopId.trim().equals("") ) { %>
      <span class="error">Warning: no troop is specified.</span>
    <% return; }
     String troopPhotoUrl = "/content/dam/girlscouts-vtk/troop-data/"+ troop.getTroop().getCouncilCode() +"/" + troopId + "/imgLib/troop_pic.png";
     //String troopPhotoUrl = "/vtk/"+ troop.getTroop().getCouncilCode() +"/troops/" + troopId + "/resources/troop_pic.png";
    %>
<div id="vtkNav"></div>


  <div class="hero-image">
   <!-- <%
            if (!resourceResolver.resolve(troopPhotoUrl).getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
		if (request.getParameter("newTroopPhoto") != null) {
			Random r  = new Random();
			troopPhotoUrl += "?pid=";
			troopPhotoUrl += r.nextInt();
		}
    %>
        <img src="<%=troopPhotoUrl %>" alt="GirlScouts Troop <%=troop.getTroop().getTroopName()%> Photo" />
       
       <%if(hasPermission(troop, Permission.PERMISSION_EDIT_TROOP_ID)){ %>
        <a data-reveal-id="modal_upload_image" title="update photo" href="#nogo" title="upload image"><i class="icon-photo-camera"></i></a>
        <%} %>
    <%
    	}
    %> -->
    <%@include file='include/modals/modal_upload_img.jsp' %>
    <%@include file="image-tool.jsp"%>
    <script>
    $('#modal_upload_image').bind('opened',function(){
    	uploadInit();
    	$('.vtk-body').css("overflow", "scroll");
    	$('#modal_upload_image').css("top", "0px");
    });
    $('#modal_upload_image').bind('closed',function(){
    	cancel();
    });
    
	function cancel(){
		$('#cropping-tool').remove();
		$('#crop-buttons').remove();
		$('#upload-tool').remove();
		$('.clearfix.btn-wrap').remove();
		$('#modal_upload_image').foundation('reveal', 'close');
		displayCurrent();
	    if(localMediaStream != null && localMediaStream != undefined){
				localMediaStream.stop();
	        }
	}
	
	function uploadSuccess() {
	  alert(successMsg);
	  $('#upload-tool').remove();
	  $('#cropping-tool').remove();
	  $('#crop-buttons').remove();
	  $('.clearfix.btn-wrap').remove();
	  $('#modal_upload_image').foundation('reveal', 'close');
	  displayCurrent();
	};
    </script>
  </div>
  
    <div class="column large-24 large-centered mytroop">
    <dl class="accordion" data-accordion>
      <dt data-target="panel1"><h3 class="on"><%=troop.getSfTroopName() %> INFO</h3>
      </dt>
    </dl>
  </div>
  
  	<h3>Coming in future releases:</h3> 
		<ul>
			<li>- Manage your troop information</li>
			<li>- Send emails to troop members </li>
			<li>- Upload your troop photo</li>
		</ul>
