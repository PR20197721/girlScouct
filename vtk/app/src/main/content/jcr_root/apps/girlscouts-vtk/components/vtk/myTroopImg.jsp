<%
    String troopId= troop.getTroop().getTroopId();
    if( troopId ==null || troopId.trim().equals("") ) { %>
      <span class="error">Warning: no troop is specified.</span>
    <% return; }
     String troopPhotoUrl = "/content/dam/girlscouts-vtk/troop-data/"+ troop.getTroop().getCouncilCode() +"/" + troopId + "/imgLib/troop_pic.png";
     
    %>
<div id="vtkNav"></div>


  <div class="hero-image">
  
    <%@include file='include/modals/modal_upload_img.jsp' %>
    <%@include file="image-tool.jsp"%>
    <script>
    $('#modal_upload_image').bind('opened',function(){
        uploadInit();
        $('.vtk-body').css("overflow", "scroll");
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

