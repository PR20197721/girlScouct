 <%@page import="java.util.Iterator,org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>

<script src="/etc/designs/girlscouts-vtk/clientlibs/js/dropzone.js"></script>


<style>
/* The MIT License */
.dropzone,
.dropzone *,
.dropzone-previews,
.dropzone-previews * {
  -webkit-box-sizing: border-box;
  -moz-box-sizing: border-box;
  box-sizing: border-box;
}
.dropzone {
  position: relative;
  border: 1px solid rgba(0,0,0,0.08);
  background: rgba(0,0,0,0.02);
  padding: 1em;
}
.dropzone.dz-clickable {
  cursor: pointer;
}
.dropzone.dz-clickable .dz-message,
.dropzone.dz-clickable .dz-message span {
  cursor: pointer;
}
.dropzone.dz-clickable * {
  cursor: default;
}
.dropzone .dz-message {
  opacity: 1;
  -ms-filter: none;
  filter: none;
}
.dropzone.dz-drag-hover {
  border-color: rgba(0,0,0,0.15);
  background: rgba(0,0,0,0.04);
}
.dropzone.dz-started .dz-message {
  display: none;
}
.dropzone .dz-preview,
.dropzone-previews .dz-preview {
  background: rgba(255,255,255,0.8);
  position: relative;
  display: inline-block;
  margin: 17px;
  vertical-align: top;
  border: 1px solid #acacac;
  padding: 6px 6px 6px 6px;
}
.dropzone .dz-preview.dz-file-preview [data-dz-thumbnail],
.dropzone-previews .dz-preview.dz-file-preview [data-dz-thumbnail] {
  display: none;
}
.dropzone .dz-preview .dz-details,
.dropzone-previews .dz-preview .dz-details {
  width: 100px;
  height: 100px;
  position: relative;
  background: #ebebeb;
  padding: 5px;
  margin-bottom: 22px;
}
.dropzone .dz-preview .dz-details .dz-filename,
.dropzone-previews .dz-preview .dz-details .dz-filename {
  overflow: hidden;
  height: 100%;
}
.dropzone .dz-preview .dz-details img,
.dropzone-previews .dz-preview .dz-details img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100px;
  height: 100px;
}
.dropzone .dz-preview .dz-details .dz-size,
.dropzone-previews .dz-preview .dz-details .dz-size {
  position: absolute;
  bottom: -28px;
  left: 3px;
  height: 28px;
  line-height: 28px;
}
.dropzone .dz-preview.dz-error .dz-error-mark,
.dropzone-previews .dz-preview.dz-error .dz-error-mark {
  display: block;
}
.dropzone .dz-preview.dz-success .dz-success-mark,
.dropzone-previews .dz-preview.dz-success .dz-success-mark {
  display: block;
}
.dropzone .dz-preview:hover .dz-details img,
.dropzone-previews .dz-preview:hover .dz-details img {
  display: none;
}
.dropzone .dz-preview .dz-success-mark,
.dropzone-previews .dz-preview .dz-success-mark,
.dropzone .dz-preview .dz-error-mark,
.dropzone-previews .dz-preview .dz-error-mark {
  display: none;
  position: absolute;
  width: 40px;
  height: 40px;
  font-size: 30px;
  text-align: center;
  right: -10px;
  top: -10px;
}
.dropzone .dz-preview .dz-success-mark,
.dropzone-previews .dz-preview .dz-success-mark {
  color: #8cc657;
}
.dropzone .dz-preview .dz-error-mark,
.dropzone-previews .dz-preview .dz-error-mark {
  color: #ee162d;
}
.dropzone .dz-preview .dz-progress,
.dropzone-previews .dz-preview .dz-progress {
  position: absolute;
  top: 100px;
  left: 6px;
  right: 6px;
  height: 6px;
  background: #d7d7d7;
  display: none;
}
.dropzone .dz-preview .dz-progress .dz-upload,
.dropzone-previews .dz-preview .dz-progress .dz-upload {
  display: block;
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  width: 0%;
  background-color: #8cc657;
}
.dropzone .dz-preview.dz-processing .dz-progress,
.dropzone-previews .dz-preview.dz-processing .dz-progress {
  display: block;
}
.dropzone .dz-preview .dz-error-message,
.dropzone-previews .dz-preview .dz-error-message {
  display: none;
  position: absolute;
  top: -5px;
  left: -20px;
  background: rgba(245,245,245,0.8);
  padding: 8px 10px;
  color: #800;
  min-width: 140px;
  max-width: 500px;
  z-index: 500;
}
.dropzone .dz-preview:hover.dz-error .dz-error-message,
.dropzone-previews .dz-preview:hover.dz-error .dz-error-message {
  display: block;
}
.dropzone {
  border: 1px solid rgba(0,0,0,0.03);
  min-height: 360px;
  -webkit-border-radius: 3px;
  border-radius: 3px;
  background: rgba(0,0,0,0.03);
  padding: 23px;
}
.dropzone .dz-default.dz-message {
  opacity: 1;
  -ms-filter: none;
  filter: none;
  -webkit-transition: opacity 0.3s ease-in-out;
  -moz-transition: opacity 0.3s ease-in-out;
  -o-transition: opacity 0.3s ease-in-out;
  -ms-transition: opacity 0.3s ease-in-out;
  transition: opacity 0.3s ease-in-out;
  background-image: url("../images/spritemap.png");
  background-repeat: no-repeat;
  background-position: 0 0;
  position: absolute;
  width: 428px;
  height: 123px;
  margin-left: -214px;
  margin-top: -61.5px;
  top: 50%;
  left: 50%;
}
@media all and (-webkit-min-device-pixel-ratio:1.5),(min--moz-device-pixel-ratio:1.5),(-o-min-device-pixel-ratio:1.5/1),(min-device-pixel-ratio:1.5),(min-resolution:138dpi),(min-resolution:1.5dppx) {
  .dropzone .dz-default.dz-message {
    background-image: url("../images/spritemap@2x.png");
    -webkit-background-size: 428px 406px;
    -moz-background-size: 428px 406px;
    background-size: 428px 406px;
  }
}
.dropzone .dz-default.dz-message span {
  display: none;
}
.dropzone.dz-square .dz-default.dz-message {
  background-position: 0 -123px;
  width: 268px;
  margin-left: -134px;
  height: 174px;
  margin-top: -87px;
}
.dropzone.dz-drag-hover .dz-message {
  opacity: 0.15;
  -ms-filter: "progid:DXImageTransform.Microsoft.Alpha(Opacity=15)";
  filter: alpha(opacity=15);
}
.dropzone.dz-started .dz-message {
  display: block;
  opacity: 0;
  -ms-filter: "progid:DXImageTransform.Microsoft.Alpha(Opacity=0)";
  filter: alpha(opacity=0);
}
.dropzone .dz-preview,
.dropzone-previews .dz-preview {
  -webkit-box-shadow: 1px 1px 4px rgba(0,0,0,0.16);
  box-shadow: 1px 1px 4px rgba(0,0,0,0.16);
  font-size: 14px;
}
.dropzone .dz-preview.dz-image-preview:hover .dz-details img,
.dropzone-previews .dz-preview.dz-image-preview:hover .dz-details img {
  display: block;
  opacity: 0.1;
  -ms-filter: "progid:DXImageTransform.Microsoft.Alpha(Opacity=10)";
  filter: alpha(opacity=10);
}
.dropzone .dz-preview.dz-success .dz-success-mark,
.dropzone-previews .dz-preview.dz-success .dz-success-mark {
  opacity: 1;
  -ms-filter: none;
  filter: none;
}
.dropzone .dz-preview.dz-error .dz-error-mark,
.dropzone-previews .dz-preview.dz-error .dz-error-mark {
  opacity: 1;
  -ms-filter: none;
  filter: none;
}
.dropzone .dz-preview.dz-error .dz-progress .dz-upload,
.dropzone-previews .dz-preview.dz-error .dz-progress .dz-upload {
  background: #ee1e2d;
}
.dropzone .dz-preview .dz-error-mark,
.dropzone-previews .dz-preview .dz-error-mark,
.dropzone .dz-preview .dz-success-mark,
.dropzone-previews .dz-preview .dz-success-mark {
  display: block;
  opacity: 0;
  -ms-filter: "progid:DXImageTransform.Microsoft.Alpha(Opacity=0)";
  filter: alpha(opacity=0);
  -webkit-transition: opacity 0.4s ease-in-out;
  -moz-transition: opacity 0.4s ease-in-out;
  -o-transition: opacity 0.4s ease-in-out;
  -ms-transition: opacity 0.4s ease-in-out;
  transition: opacity 0.4s ease-in-out;
  background-image: url("../images/spritemap.png");
  background-repeat: no-repeat;
}
@media all and (-webkit-min-device-pixel-ratio:1.5),(min--moz-device-pixel-ratio:1.5),(-o-min-device-pixel-ratio:1.5/1),(min-device-pixel-ratio:1.5),(min-resolution:138dpi),(min-resolution:1.5dppx) {
  .dropzone .dz-preview .dz-error-mark,
  .dropzone-previews .dz-preview .dz-error-mark,
  .dropzone .dz-preview .dz-success-mark,
  .dropzone-previews .dz-preview .dz-success-mark {
    background-image: url("../images/spritemap@2x.png");
    -webkit-background-size: 428px 406px;
    -moz-background-size: 428px 406px;
    background-size: 428px 406px;
  }
}
.dropzone .dz-preview .dz-error-mark span,
.dropzone-previews .dz-preview .dz-error-mark span,
.dropzone .dz-preview .dz-success-mark span,
.dropzone-previews .dz-preview .dz-success-mark span {
  display: none;
}
.dropzone .dz-preview .dz-error-mark,
.dropzone-previews .dz-preview .dz-error-mark {
  background-position: -268px -123px;
}
.dropzone .dz-preview .dz-success-mark,
.dropzone-previews .dz-preview .dz-success-mark {
  background-position: -268px -163px;
}
.dropzone .dz-preview .dz-progress .dz-upload,
.dropzone-previews .dz-preview .dz-progress .dz-upload {
  -webkit-animation: loading 0.4s linear infinite;
  -moz-animation: loading 0.4s linear infinite;
  -o-animation: loading 0.4s linear infinite;
  -ms-animation: loading 0.4s linear infinite;
  animation: loading 0.4s linear infinite;
  -webkit-transition: width 0.3s ease-in-out;
  -moz-transition: width 0.3s ease-in-out;
  -o-transition: width 0.3s ease-in-out;
  -ms-transition: width 0.3s ease-in-out;
  transition: width 0.3s ease-in-out;
  -webkit-border-radius: 2px;
  border-radius: 2px;
  position: absolute;
  top: 0;
  left: 0;
  width: 0%;
  height: 100%;
  background-image: url("../images/spritemap.png");
  background-repeat: repeat-x;
  background-position: 0px -400px;
}
@media all and (-webkit-min-device-pixel-ratio:1.5),(min--moz-device-pixel-ratio:1.5),(-o-min-device-pixel-ratio:1.5/1),(min-device-pixel-ratio:1.5),(min-resolution:138dpi),(min-resolution:1.5dppx) {
  .dropzone .dz-preview .dz-progress .dz-upload,
  .dropzone-previews .dz-preview .dz-progress .dz-upload {
    background-image: url("../images/spritemap@2x.png");
    -webkit-background-size: 428px 406px;
    -moz-background-size: 428px 406px;
    background-size: 428px 406px;
  }
}
.dropzone .dz-preview.dz-success .dz-progress,
.dropzone-previews .dz-preview.dz-success .dz-progress {
  display: block;
  opacity: 0;
  -ms-filter: "progid:DXImageTransform.Microsoft.Alpha(Opacity=0)";
  filter: alpha(opacity=0);
  -webkit-transition: opacity 0.4s ease-in-out;
  -moz-transition: opacity 0.4s ease-in-out;
  -o-transition: opacity 0.4s ease-in-out;
  -ms-transition: opacity 0.4s ease-in-out;
  transition: opacity 0.4s ease-in-out;
}
.dropzone .dz-preview .dz-error-message,
.dropzone-previews .dz-preview .dz-error-message {
  display: block;
  opacity: 0;
  -ms-filter: "progid:DXImageTransform.Microsoft.Alpha(Opacity=0)";
  filter: alpha(opacity=0);
  -webkit-transition: opacity 0.3s ease-in-out;
  -moz-transition: opacity 0.3s ease-in-out;
  -o-transition: opacity 0.3s ease-in-out;
  -ms-transition: opacity 0.3s ease-in-out;
  transition: opacity 0.3s ease-in-out;
}
.dropzone .dz-preview:hover.dz-error .dz-error-message,
.dropzone-previews .dz-preview:hover.dz-error .dz-error-message {
  opacity: 1;
  -ms-filter: none;
  filter: none;
}
.dropzone a.dz-remove,
.dropzone-previews a.dz-remove {
  background-image: -webkit-linear-gradient(top, #fafafa, #eee);
  background-image: -moz-linear-gradient(top, #fafafa, #eee);
  background-image: -o-linear-gradient(top, #fafafa, #eee);
  background-image: -ms-linear-gradient(top, #fafafa, #eee);
  background-image: linear-gradient(to bottom, #fafafa, #eee);
  -webkit-border-radius: 2px;
  border-radius: 2px;
  border: 1px solid #eee;
  text-decoration: none;
  display: block;
  padding: 4px 5px;
  text-align: center;
  color: #aaa;
  margin-top: 26px;
}
.dropzone a.dz-remove:hover,
.dropzone-previews a.dz-remove:hover {
  color: #666;
}
@-moz-keyframes loading {
  from {
    background-position: 0 -400px;
  }
  to {
    background-position: -7px -400px;
  }
}
@-webkit-keyframes loading {
  from {
    background-position: 0 -400px;
  }
  to {
    background-position: -7px -400px;
  }
}
@-o-keyframes loading {
  from {
    background-position: 0 -400px;
  }
  to {
    background-position: -7px -400px;
  }
}
@keyframes loading {
  from {
    background-position: 0 -400px;
  }
  to {
    background-position: -7px -400px;
  }
}

</style>

<%String assetId = new java.util.Date().getTime() +"_"+ Math.random(); %>
      
<script>
         
         function bindAssetToYPC(assetId, ypcId){
        	
        	 var assetDesc = document.getElementById("assetDesc").value;
        	 //var custasset = document.getElementById("file").value;
        	 
        	 ///if( $.trim(custasset)=='' ){alert('Please select file to upload');return false;}
        	 if( $.trim(assetDesc)=='' ){alert('Please enter name of asset');return false;}
        	 
        	 
        	 $.ajax({
        			url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand='+Date.now(),
        			type: 'POST',
        			data: { 
        				bindAssetToYPC:assetId,
        				ypcId:ypcId,
        				assetDesc:assetDesc,
        				a:Date.now()
        			},
        			success: function(result) {
        				
        			}
        		});
        	 document.getElementById("my-awesome-dropzone").submit();
         }
       
// good $("div#myId").dropzone({ url: "/file/post" });

Dropzone.options.myAwesomeDropzone = {
		
		alert(1);
  paramName: "file", // The name that will be used to transfer the file
  maxFilesize: 2, // MB
  accept: function(file, done) {
	  alert(2)
    if (file.name == "justinbieber.jpg") {
      done("Naha, you don't.");
    }
    else { done(); }
  }
};


		
</script>
<div id="myId"> 
<form 
      class="dropzone"
      action="/content/girlscouts-vtk/controllers/auth.asset.html" method="post"
      onsubmit="return bindAssetToYPC( '/vtk/<%=user.getTroop().getCouncilCode()%>/<%=user.getTroop().getTroopId() %>/assets/<%=assetId %>', '<%=request.getParameter("refId")%>' )" 
      id="my-awesome-dropzone">
      
      
        	<input type="hidden" name="loc" value="/vtk/<%=user.getTroop().getCouncilCode()%>/<%=user.getTroop().getTroopId() %>/assets"/>
              Asset Name: <input type="text" id="assetDesc" name="assetDesc" value="" />
               <input type="hidden" name="id" value="<%=assetId %>"/>     
                <input type="hidden" name="me" value="<%=request.getParameter("myId")%>"/>      
               <input type="hidden" name="owner" value="<%=user.getId()%>"/>
               <input type="hidden" name="createTime" value="<%=new java.util.Date()%>"/>  
                <br />
                <input type="button" value="Upload File" onclick="bindAssetToYPC( '/vtk/<%=user.getTroop().getCouncilCode()%>/<%=user.getTroop().getTroopId() %>/assets/<%=assetId %>', '<%=request.getParameter("refId")%>' )" /> 
      </form>
      </div> 