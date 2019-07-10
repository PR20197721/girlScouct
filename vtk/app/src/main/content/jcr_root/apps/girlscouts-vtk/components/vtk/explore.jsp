<%@ page
  import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.utils.VtkUtil, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<!-- PAGEID :: ./app/src/main/content/jcr_root/apps/girlscouts-vtk/components/vtk/vtk.jsp -->
<cq:defineObjects />
<%@include file="include/session.jsp"%>

<div id="error-message"></div>
<!--PAGE STRUCTURE: reset modal-->
 <div id="exploreModal" class="exploreReset">

   <!-- Modal content -->
   <div class="explore-content">
       <div class="explore-modal-header">
             <span id="exploreModalClose">X</span>
          <div class="exploreHeader"></br></div>
      </div>
     <div class="explore-modal-body">
       <div id="explore-modal-text">
        <strong id="exploreBody">Are you sure you want to reset your Year Plan?</strong>
        <br><br>
        <p>Resetting your Year Plan will erase all current meeting details, including attendance and achievements.</p>
       </div>
       <a href="javascript:exploreResetClose();" className="btn button btn-default resetExploreButton" style="max-width: 300px; color: #00a850; background-color: white; border: 1px solid #00a850; padding: 8px 15px 8px 15px; display: inline-block;" >No, Never Mind</a>
       <br id="exploreModalBreak">
       <a href="javascript:exploreResetConfirm();" id="selectedExploreButtonConfirm" className="btn button btn-default selectedExploreButton" style="max-width: 300px; color: white; background: #18aa51; padding: 8px 34px 8px 34px; display: inline-block;">Yes, Reset</a>
    </div>
    <div class="explore-modal-footer">

    </div>

   </div>

 </div>
<%
  String activeTab = "explore";
  boolean showVtkNav = true;
  String sectionClassDefinition="";
%>

<%@include file="include/bodyTop.jsp" %>

<!-- start body -->
<%@include file="include/view_yp_dropdown.jsp"%>
<!-- end body -->


<div id="requirementsModal" class="reveal-modal" data-reveal=""></div>

<script>
loadNav('explore')


      var requirementsModal = function(binder, isOutdoorAvailable, isOutdoor, isGlobalAvailable, isGlobal){
          var imgName;
           if (isOutdoor) {
                 imgName =  "outdoor.png";
              } else {
                  imgName = "indoor.png";
              }

        var globalImageName;
        if (isGlobal) {
        	globalImageName =  "globe_selected.png";
        } else {
        	globalImageName = "globe_unselected.png";
        }
        var modal = $('#requirementsModal');

        var image = (isOutdoorAvailable)? '<img style="width:44px" src="/etc/designs/girlscouts-vtk/clientlibs/css/images/'+imgName+'" />':' ';
        if(isGlobalAvailable){
        	if(image.trim().length > 0){
        		image +=" ";
        	}
        	image +='<img style="width:44px" src="/etc/designs/girlscouts-vtk/clientlibs/css/images/'+globalImageName+'" />';
        }

        var _template = '<div class="modal_resource">'+
                        '<div class="header clearfix">'+
                          '<h3 class="columns large-22">REQUIREMENTS</h3>'+
                          '<a class="close-reveal-modal columns large-2" href="#">'+
                              '<i class="icon-button-circle-cross"></i>'+
                          '</a>'+
                        '</div>'+
                        '<div class="scroll content">'+
                          '<section class="content">'+
                            '<div style="text-align:center">'+
                              '<img src="/content/dam/girlscouts-vtk/local/icon/meetings/'+binder.id+'.png" />'+
                              '<h3>'+ binder.reqTitle+'</h3><br />'+
                            '</div>'+
                            '<div>'+ binder.req +
                            '</div><p style="text-align:center">'+
                              image +
                           '</p></section>'+
                        '</div>'+
                      '</div>';
                      $(document).foundation()
                      modal.html(_template)
                      .foundation('reveal','open');
      }

var modalAlert = new ModalVtk('Alert',true);
modalAlert.init();

</script>
<%@include file="include/bodyBottom.jsp" %>


