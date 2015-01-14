    <%
    String troopId= troop.getTroop().getTroopId();
    if( troopId ==null || troopId.trim().equals("") ) { %>
      <span class="error">Warning: no troop is specified.</span>
    <% return; }
     String troopPhotoUrl = "/content/dam/girlscouts-vtk/troops/" + troopId + "/imgLib/troop_pic.png/troop_pic.png";
    %>
  <div id="modal_upload_image" class="reveal-modal" data-reveal>
    <div class="header clearfix">
      <h3 class="columns large-22"><%=troop.getTroop().getTroopName()%> photo</h3>
      <a class="close-reveal-modal columns large-2" href="#"><i class="icon-button-circle-cross"></i></a>
    </div>
    <div class="content row">
      <form action="/content/girlscouts-vtk/controllers/auth.asset.html" method="post" id="frmImg" name="frmImg" enctype="multipart/form-data">
        <input type="hidden" name="troopId"  value="<%=troopId%>"/>
        <input type="file" name="upldTroopPic" value="Upload Image" />
        <input type="submit" value="UPLOAD PHOTO" class="button btn" />
      </form>
    </div>
  </div>
  