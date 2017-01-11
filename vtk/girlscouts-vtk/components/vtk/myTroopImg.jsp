<%
    String troopId= troop.getTroop().getTroopId();
    if( troopId ==null || troopId.trim().equals("") ) { %>
      <span class="error">Warning: no troop is specified.</span>
    <% return;
     }else{
        if( troopId!=null && troopId.indexOf("_")!=-1){
            troopId= troopId.substring( troopId.lastIndexOf("_")+1 );
        }
     }
     String troopPhotoUrl = "/content/dam/girlscouts-vtk/troop-data"+VtkUtil.getCurrentGSYear()+"/"+ troop.getTroop().getCouncilCode() +"/" + troopId + "/imgLib/troop_pic.png";

     boolean isImgExists= false;
     Resource res = resourceResolver.resolve("/content/dam/girlscouts-vtk/troop-data"+VtkUtil.getCurrentGSYear()+"/"+ troop.getTroop().getCouncilCode() +"/" + troopId + "/imgLib/troop_pic.png");
     if (res != null && !res.getResourceType().equals("sling:nonexisting")){
         isImgExists=true;
     }
     
    %>



  <div class="hero-image">

    <%@include file='include/modals/modal_upload_img.jsp' %>
    <%@include file="image-tool.jsp"%>
    <script>
    $('#modal_upload_image').bind('opened',function(){
        uploadInit();
        $('.vtk-body').css("overflow", "hidden");
        // $('#modal_upload_image').css("top", "0px");
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
        if( <%=isImgExists%>){displayCurrent();}
        if(localMediaStream != null && localMediaStream != undefined){

	        	try{
	                localMediaStream.stop();
	            }catch(err){ cleanUp(); }

            }
    }

    function uploadSuccess() {
      alert(successMsg);
      $('#upload-tool').remove();
      $('#cropping-tool').remove();
      $('#crop-buttons').remove();
      $('.clearfix.btn-wrap').remove();
      $('#modal_upload_image').foundation('reveal', 'close');
      displayCurrent(true);
    };
    </script>
  </div>

