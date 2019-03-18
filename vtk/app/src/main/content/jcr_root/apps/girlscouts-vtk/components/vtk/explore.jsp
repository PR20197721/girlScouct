
<%@ page
  import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<!-- PAGEID :: ./app/src/main/content/jcr_root/apps/girlscouts-vtk/components/vtk/vtk.jsp -->
<cq:defineObjects />
<%@include file="include/session.jsp"%>

<div id="error-message"></div>      

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


      var requirementsModal = function(binder, isOutdoorAvailable, isOutdoor, , isGlobalAvailable, isGlobal){
          var imgName;
           if (isOutdoor) {
                 imgName =  "outdoor.png";
              } else {
                  imgName = "indoor.png";
              }

        var globalImageName;
        if (isGlobbal) {
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


