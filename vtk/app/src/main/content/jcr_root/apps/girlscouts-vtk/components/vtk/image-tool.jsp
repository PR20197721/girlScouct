<img id="current-picture" src="<%= "/content/dam/girlscouts-vtk/camera-test/troop-data/"+ troop.getTroop().getCouncilCode() +"/" + troop.getTroop().getTroopId() + "/imgLib/troop_pic.png?" %>" style="margin-left: auto; margin-right: auto; max-width: 100%"/>
<div id="image-tool" style="width:100%"></div>

<script>

var imageTool = document.getElementById("image-tool");

var modal = document.getElementById("upload-crop-area");
document.getElementById("modal_upload_image").appendChild(modal);

var uploadTool, croppingTool, currentDisplay, uploadButtons;

var cancelButton = document.createElement("Button");
var cancelText = document.createTextNode("Cancel");
    cancelButton.appendChild(cancelText);
    cancelButton.className = "cancel";
    cancelButton.style.float = "right";
    
var instructions = document.getElementById("instructions");

var localMediaStream = null;
var hasCamera = false;
var uploadedCheck = false;
var tookPic = false;
var img;
var picData;
var aspectWeirdness = false; //applies to mobile safari, which has resizing issues
var aspectRatio = 1;
var resizeImageInstance;
var successMsg = "You image has been uploaded. ";

var maxWidth = 960;
var maxHeight = 340;

var imgMaxW = 1920;
var imgMaxH = 680;

    //Webcam support for laptop/some devices

//support for multiple browsers
navigator.getUserMedia = ( navigator.getUserMedia ||
                       navigator.webkitGetUserMedia ||
                       navigator.mozGetUserMedia ||
                       navigator.msGetUserMedia);

var imgPath = "<%= "/content/dam/girlscouts-vtk/troop-data/"+ troop.getTroop().getCouncilCode() +"/" + troop.getTroop().getTroopId() + "/imgLib/troop_pic.png?pid=" %>";

var displayCurrent = function(){
    currentDisplay = document.createElement("div");
    currentDisplay.id = "current-display";

	var currentPic = document.getElementById("current-picture");

    if (!Date.now) {
		Date.now = function() { return new Date().getTime(); }
	}
    
    currentDisplay.appendChild(currentPic);
    
    currentPic.onerror = function(){
    	$('#current-picture').css("display", "none");
    	$('#current-display').css("margin-top", "20px");
    	$('.icon-photo-camera').css("color","black");        
    }
    
    currentPic.onload = function(){
    	$('#current-picture').css("display", "auto");
    	$('#current-display').css("margin-top", "0px");
    	$('.icon-photo-camera').css("color","white"); 
    }
      
    currentPic.src = imgPath + Date.now();
    currentPic.style.float = "left";

    var clearBoth = document.createElement("div");
    clearBoth.id = "clear-both";
    clearBoth.style.clear = "both";

    imageTool.appendChild(currentDisplay);
    imageTool.appendChild(clearBoth);
    
    <%if(hasPermission(troop, Permission.PERMISSION_EDIT_TROOP_ID)){ %>
    	$("<a data-reveal-id=\"modal_upload_image\" title=\"update photo\" href=\"#nogo\" title=\"Upload a new Photo\"><i class=\"icon-photo-camera\"></i></a>").insertAfter($('#current-picture'));
	<%} %>
}

<%if(hasPermission(troop, Permission.PERMISSION_EDIT_TROOP_ID)){ %>
	var removeCurrent = function(){
		$('#current-display').remove();
		$('#clear-both').remove();
	}
	
	uploadInit = function(){
	
	    localMediaStream = null;
	    hasCamera = false;
	    uploadedCheck = false;
	    tookPic = false;
	    aspectWeirdness = false;
	    var directUploadRatio = 1;
	    var directUploadOk = false;
	
		uploadTool = document.createElement("div");
	    uploadTool.id = "upload-tool"
	    uploadTool.style.display = "hidden";
	
		var imageLoader = document.createElement("input");
	    imageLoader.id = "imageLoader";
	    imageLoader.type = "file";
	    imageLoader.accept = "image/*";
	    imageLoader.setAttribute("capture","camera");
	
		var video = document.createElement("video");
	    video.autoplay = true;
	    video.id = "video";
	    video.style.maxWidth = $('#upload-tool').width() + "px";
		video.style.maxHeight = $('#upload-tool').height() + "px";
	    video.style.display = "none";
	    video.style.marginLeft = "auto";
	    video.style.marginRight = "auto";
	    
		var canvas = document.createElement("canvas");
	    canvas.id = "canvas";
	    canvas.setAttribute("width","100%");
	    canvas.style.maxWidth = maxWidth + "px"; 
		canvas.style.maxHeight = maxHeight + "px";
	    canvas.style.display = "none";
	    canvas.style.marginLeft = "auto";
	    canvas.style.marginRight = "auto";
	
	    var context = canvas.getContext('2d');
	
		var takeShot = document.createElement("button");
	    takeShot.id = "takeShot";
	    takeShot.style.float = "left";
	    takeShot.style.display = "none";
		var text2 = document.createTextNode("Take Picture");
	    takeShot.appendChild(text2);
	
		var retakeShot = document.createElement("button");
	    retakeShot.id = "retakeShot";
	    retakeShot.style.float = "left";
	    retakeShot.style.display = "none";
		var text3 = document.createTextNode("Retake Picture");
	    retakeShot.appendChild(text3);
	
		var submitShot = document.createElement("button");
	    submitShot.id = "submitShot";
	    submitShot.style.float = "left";
	    submitShot.style.display = "none";
		var text4 = document.createTextNode("Crop this picture");
	    submitShot.appendChild(text4);
	
		var switchButton = document.createElement("button");
	    switchButton.id = "switchCam";
	    switchButton.style.float = "left";
	    switchButton.style.display = "none";
		var switchText = document.createTextNode("Switch to Camera");
	    switchButton.appendChild(switchText);
	
		var videoLoader = document.createElement("button");
		videoLoader.id = "load-video";
		videoLoader.style.display = "none";
		var vidLoadText = document.createTextNode("Use Webcam");
		videoLoader.appendChild(vidLoadText);
		
		var transform = "none";
		
		var directUploadButton = document.createElement("button");
		directUploadButton.id = "direct-upload";
		directUploadButton.disabled = true;
		directUploadButton.style.float = "left";
		directUploadButton.style.display = "none";
		var directUploadText = document.createTextNode("Upload Without Cropping");
		directUploadButton.appendChild(directUploadText);
	    
	    uploadButtons = document.createElement("div");
	    uploadButtons.id = "upload-buttons";
	    uploadButtons.style.overflow = "hidden";
	
	    modal.appendChild(uploadTool);
	
		uploadTool.appendChild(imageLoader);
		uploadTool.appendChild(videoLoader);
	    uploadTool.appendChild(video);
	    uploadTool.appendChild(canvas);
	    uploadTool.appendChild(uploadButtons);
	    uploadButtons.appendChild(takeShot);
	    uploadButtons.appendChild(retakeShot);
	    uploadButtons.appendChild(switchButton); 
	    uploadButtons.appendChild(submitShot);      
	    uploadButtons.appendChild(cancelButton);
	    uploadButtons.appendChild(directUploadButton);
	    
	    instructions.innerHTML = "Instructions: Please choose a file that you would like to upload. \nWhen you are ready to upload your image, please select \"Crop this picture\". If the image you upload has an aspect ratio of 48:17 (960px x 340px), you can upload it directly without cropping by selecting \"Upload Without Cropping\"";
	
	    function handleImage(){
	    	var file = this.files[0];
	    	directUploadAvailable = false;
	    	var dataReader = new FileReader();	
	    	dataReader.onload = function(readerEvent){
	    		img = new Image();
	    		img.setAttribute("exif","true");
	        	img.style.maxWidth = imgMaxW;
	        	img.style.maxHeight = imgMaxH;
	        	img.onload = function(){
	        		
	        		var binaryReader = new FileReader();
	        		var binWidth = img.width;
	        		var binHeight = img.height;
	        		
	        		canvas.width = binWidth;
	            	canvas.height = binHeight;
	        		
	        		binaryReader.onloadend = function(d){
	        			
	        			//Check if the photo is rotated via EXIF header tags (pertains mainly to iphone)
	        			var exif, transform = "none";
	        			exif = EXIF.readFromBinaryFile(createBinaryFile(d.target.result));
	        			
	        			if(exif.Orientation === 8){
	        				binWidth = img.height;
	        				binHeight = img.width;
	        				transform = "left";
	        			} else if(exif.Orientation === 6){
	        				binWidth = img.height;
	        				binHeight = img.width;
	        				transform = "right";
	        			} else if(exif.Orientation === 1){
	        				binWidth = img.width;
	        				binHeight = img.height;
	        			} else if(exif.Orientation === 3){
	        				binWidth = img.width;
	        				binHeight = img.height;
	        				transform = "flip";
	        			} else{
	        				binWidth = img.width;
	        				binHeight = img.height;
	        			}
	        			
	        			//Rotate and draw
	        			
	        			console.log("Transform: " + transform);
	        			canvas.width = binWidth;
		            	canvas.height = binHeight;
	        			if(transform === 'left'){
	        				context.setTransform(0, -1, 1, 0, 0, binHeight);
	        				context.drawImage(img, 0, 0, img.width, img.height, 0, 0, canvas.height, canvas.width);
	        			} else if(transform === 'right'){
	        				context.setTransform(0, 1, -1, 0, binWidth, 0);
	        				context.drawImage(img, 0, 0, img.width, img.height, 0, 0, canvas.height, canvas.width);
	        			} else if(transform === 'flip'){
	        				context.setTransform(1, 0, 0, -1, 0, binHeight);
	        				context.drawImage(img, 0, 0, img.width, img.height, 0, 0, canvas.width, canvas.height);        				
	        			} else{
	        				context.setTransform(1, 0, 0, 1, 0, 0);
	        				context.drawImage(img, 0, 0, img.width, img.height, 0, 0, canvas.width, canvas.height);
	        			}
	        			
	        			
		                if($('#modal_upload_image').innerWidth() < maxWidth){
		    				canvas.style.maxWidth = $('#modal_upload_image').innerWidth() + "px";
		           	 	}
		            	else if($('#modal_upload_image').innerWidth() > maxWidth){
		    				canvas.style.maxWidth = maxWidth + "px";
		            	}
		            	if($('#modal_upload_image').innerHeight() < maxHeight){
		    				canvas.style.maxHeight = $('#modal_upload_image').innerHeight() + "px";
		            	}
		    			else if($('#modal_upload_image').innerHeight() > maxHeight){
		    				canvas.style.maxHeight = maxHeight + "px";
		            	}
		            	aspectRatio = img.width/img.height;
		            	if(canvas.toDataURL() == "data:,"){//mobile safari
		                	aspectWeirdness = true;             	
		                	if(img.width > img.height && (transform === 'left' || transform === 'right')){
								img.height = canvas.style.maxWidth.replace("px","");
			                	img.width = img.height*aspectRatio;
			            	}
			            	else if(img.width < img.height && (transform === 'left' || transform === 'right')){
								img.width = canvas.style.maxHeight.replace("px","");
			            		img.height = img.width/aspectRatio;
			            	}
			            	else if(img.width > img.height){
		    					img.height = canvas.style.maxHeight.replace("px","");
		                    	img.width = img.height*aspectRatio;
		                	}
		                	else{
		    					img.width = canvas.style.maxWidth.replace("px","");
		                		img.height = img.width/aspectRatio;
		                	}
		                	if(transform === 'left'){
		                		canvas.width = img.height;
			                	canvas.height = img.width;
		                		context.setTransform(0, -1, 1, 0, 0, canvas.height);
		                		context.drawImage(img, 0, 0, canvas.height, canvas.width);
		                	}
		                	else if(transform === 'right'){
		                		canvas.width = img.height;
			                	canvas.height = img.width;
		                		context.setTransform(0, 1, -1, 0, canvas.width, 0);
		                		context.drawImage(img, 0, 0, canvas.height, canvas.width);
		                	}
		                	else if(transform === 'flip'){
		                		canvas.width = img.width;
			                	canvas.height = img.height;
		                		context.setTransform(1, 0, 0, -1, 0, canvas.height);
		                		context.drawImage(img, 0, 0, canvas.width, canvas.height);
		                	}
		                	else{
		                		canvas.width = img.width;
			                	canvas.height = img.height;
		                		context.setTransform(1, 0, 0, 1, 0, 0);
		                		context.drawImage(img, 0, 0, canvas.width, canvas.height);
		                	}
		                }
		            	directUploadTest();
	        			
	        		};
	        		
	        		binaryReader.readAsArrayBuffer(file);
	        		
	        	}
	        	img.src = readerEvent.target.result;
	        	uploadedCheck = true;
	    	}
	    	dataReader.readAsDataURL(file);
	    	
	    	canvas.style.display='block';
	    	switchButton.innerHTML = "Switch to camera";
	    	if(hasCamera == true){
				switchButton.style.display='block';
	    	}
	    	submitShot.style.display='block';
	    	directUploadButton.style.display = 'block';
	    	takeShot.style.display='none';
	    	retakeShot.style.display='none';
	    	if(video.style.display == 'block'){
	    		video.style.display = 'none';
	    	}
		}
	    
	 // Wrapper around MPL-licensed http://www.nihilogic.dk/labs/binaryajax/binaryajax.js
	 // to support JavaScript typed arrays since binary strings are not supported in IE 10
	 var createBinaryFile = function(uintArray) {
	     var data = new Uint8Array(uintArray);
	     var file = new BinaryFile(data);

	     file.getByteAt = function(iOffset) {
	         return data[iOffset];
	     };

	     file.getBytesAt = function(iOffset, iLength) {
	         var aBytes = [];
	         for (var i = 0; i < iLength; i++) {
	             aBytes[i] = data[iOffset  + i];
	         }
	         return aBytes;
	     };

	     file.getLength = function() {
	         return data.length;
	     };

	     return file;
	 };
	    
	    function directUploadTest(){
	    	if(!tookPic && video.style.display == 'none'){
	    		directUploadRatio = img.width/img.height;
	    		if(directUploadRatio == (48/17)){
		    		directUploadOk = true;
		    		directUploadButton.disabled = false;
		    	}
		    	else{
		    		directUploadOk = false;
		    		directUploadButton.disabled = true;
		    	}
		    }
	    	else{
	    		directUploadOk = false;
	    		directUploadButton.disabled = true;
	    	}
	    }
	
		function switchCam(){
	    	context.clearRect ( 0 , 0 , canvas.width, canvas.height );
			if(video.style.display == 'block' || tookPic){
				video.style.display = 'none';
	        	retakeShot.style.display='none';
	        	takeShot.style.display='none';
				switchButton.innerHTML = "Switch to camera";
				if(img != null){
	            	canvas.width = img.width;
	            	canvas.height = img.height;
	            	context.drawImage(img, 0, 0, img.width, img.height, 0, 0, canvas.width, canvas.height);
	            	canvas.style.display = 'block';
	            	submitShot.style.display='block';
	            	directUploadButton.style.display = 'block';
	        	}
	        	tookPic = false;
			}
			else{
				video.style.display = 'block';
				takeShot.style.display='block';
	        	canvas.style.display = 'none';
	        	submitShot.style.display = 'none';
	        	directUploadButton.style.display = 'none';
	        	if(img != null){
	        		switchButton.style.display='block';
	        	}
				switchButton.innerHTML = "Switch to uploaded image";
	        	tookPic = false;
			}
			directUploadTest();
		}    
	    
	    var uploadText;
	
		//website requests permission to use your webcam
	       	if (navigator.getUserMedia) {
	       		instructions.innerHTML = "Instructions: Please choose a file that you would like to upload. You can also take a photo from your webcam. \nWhen you are ready to upload your image, please select \"Crop this picture\". If the image you upload has an aspect ratio of 48:17 (960px x 340px), you can upload it directly without cropping by selecting \"Upload Without Cropping\"";
				videoLoader.style.display = "block";
	    	}else {
	            console.log("getUserMedia not supported");
	        }
	
		function loadVideo(){
				videoLoader.style.display = "none";
	            navigator.getUserMedia (
	    
	            // constraints
	            {
	                video: true,
	                audio: false
	            },
	    
	            // successCallback
	            function(stream) {
	                hasCamera = true;
	                console.log("Camera Detected");
	                switchCam();
	                video.src = window.URL.createObjectURL(stream);
	                localMediaStream = stream;
	            },
	    
	            // errorCallback
	            function(err) {
	                hasCamera = false;
	                uploadText.data = "";
	                console.log("The following error occurred: " + err);
	            });
	    }
	
		function snapshot() {
	    	if (localMediaStream) {
	        	tookPic = true;
	        	canvas.width = video.getBoundingClientRect().width;
	        	canvas.height = video.getBoundingClientRect().height;
	        	video.style.display='none';
	        	takeShot.style.display='none';
	        	canvas.style.display='block';
				retakeShot.style.display='block';
	        	submitShot.style.display='block';
	        	directUploadButton.style.display = 'block';
	        	if(img != null){
	        		switchButton.innerHTML="Switch to uploaded image";
	        		switchButton.style.display='block';
	        	}
	        	context.drawImage(video, 0, 0);
	        	picData = context.getImageData(0,0, canvas.width, canvas.height);
	    	}
	    	directUploadTest();
		}
	
		function retake() {
	    	tookPic = false;
	    	canvas.style.display='none';
	    	retakeShot.style.display='none';
	    	submitShot.style.display='none';
	    	directUploadButton.style.display = 'none';
			video.style.display='block';
	    	takeShot.style.display='block';
		}
	
	    function resizeUpload(){
	        uploadTool.style.display = "none";
	        resizeableImage(canvas.toDataURL("image/png",1.0));
	    }
	    
	    $(window).resize(function() {
	    	if(canvas.style.display == 'block'){
		    	
	            if($('#modal_upload_image').innerWidth() < maxWidth){
					canvas.style.maxWidth = $('#modal_upload_image').innerWidth() + "px";
	       	 	}
	        	else if($('#modal_upload_image').innerWidth() > maxWidth){
					canvas.style.maxWidth = maxWidth + "px";
	        	}
	        	if($('#modal_upload_image').innerHeight() < maxHeight){
					canvas.style.maxHeight = $('#modal_upload_image').innerHeight() + "px";
	        	}
				else if($('#modal_upload_image').innerHeight() > maxHeight){
					canvas.style.maxHeight = maxHeight + "px";
	        	}
	    	}
	    });
	    
	    function directUpload(){
			var dataURL = canvas.toDataURL("image/png",1.0);
	    	
			if((!tookPic && !uploadedCheck) || !directUploadOk){
	    		alert("Image Error: no image data detected");
	    	}
	    	else{
	
	    		tookPic = false;
	
	    		if (!Date.now) {
	    			Date.now = function() { return new Date().getTime(); }
				}
	
	    		var uploadableImage = new Image();
	    		uploadableImage.id = "uploadable-image";
	    		uploadableImage.onload = function(){
	    			var ajaxW = $('#uploadable-image').width();
	
	        		var coordsArray = [0, 0, $('#canvas').width(), $('#canvas').height(), $('#canvas').width(), $('#canvas').height(), ajaxW];
	                console.log(coordsArray);
	
	    			$.ajax({
	      				method: "POST",
	        	   		url: "/content/girlscouts-vtk/controllers/vtk.include.imageStore.html?" + Date.now(), //random string to prevent ajax caching
	                    data: { imageData: dataURL, coords: coordsArray.toString() },
	    				success: uploadSuccess
	    			})
	      			.done(function( msg ) {
	        	   		console.log( "Uploaded");
	      			})
	      			.fail(function(msg){
	      				alert("Upload failed");
	      				cancel();
	      			});
	    		}
	    		uploadableImage.src = dataURL;
	            
	    	}
	    }
	
	    imageLoader.addEventListener('change', handleImage, false);
	    takeShot.addEventListener('click', snapshot, false);
	    retakeShot.addEventListener('click', retake, false);
	    switchButton.addEventListener('click', switchCam, false);
	    submitShot.addEventListener('click', resizeUpload, false);
	    videoLoader.addEventListener('click', loadVideo, false);
	    directUploadButton.addEventListener('click', directUpload, false);
	}
	
	var resizeableImage = function(image_data){
		
		var coordsSelected = false;
	
		croppingTool = document.createElement("div");
	    croppingTool.id = "cropping-tool";
	    croppingTool.style.position = "relative";
	    croppingTool.style.marginLeft = "auto";
		croppingTool.style.marginRight = "auto";
	    
		var cropButtons = document.createElement("div");
	    cropButtons.id = "crop-buttons";
	    cropButtons.style.overflow = "hidden";
	    cropButtons.style.width = "100%";
	    cropButtons.style.position = "relative";
	
		var submitCrop = document.createElement("button");
	    submitCrop.id = "submitCrop";
	    submitCrop.style.float = "left";
		var submitText = document.createTextNode("Crop & Select");
	    submitCrop.appendChild(submitText);
	    submitCrop.disabled = true;
	
	    modal.appendChild(croppingTool);
	    modal.appendChild(cropButtons);
	
	    cropButtons.appendChild(submitCrop);
	    
	    cropButtons.appendChild(cancelButton);
	
	    var image_target = document.createElement("img");
	    	image_target.id = "resize-image";
	
	    croppingTool.maxWidth = maxWidth;
	    croppingTool.maxHeight = maxHeight;
	
	
	    init = function(){
	    	
	    	instructions.innerHTML = "Instructions: Click and drag across the image to select the area you would like to use for your troop photo. Note that you will not be able to change the aspect ratio. When finished, click \"Crop and Select\" to upload the image. You will receive an alert when the upload is complete."
	
	        submitCrop.addEventListener('click', crop, false);
	        croppingTool.appendChild(image_target);
	        $(croppingTool).width($(image_target).width());
	    	$(croppingTool).height($(image_target).height());
	        
	    };
	
	    var x1, x2, y1, y2, width, height;
	
	    storeCoords = function(img, selection){
	    	//console.log(x1 + "," + y1 + " -> " + x2 + "," + y2);
	    	x1 = selection.x1;
	    	x2 = selection.x2;
	    	y1 = selection.y1;
	    	y2 = selection.y2;
	    	width = selection.width;
	    	height = selection.height;
	    	submitCrop.disabled = false;
	    	coordsSelected = true;
	    };
	
	    crop = function(){
	    	if(!coordsSelected){
	    		alert("Please crop the part of the image you would like to upload by clicking and dragging across the picture");
	    	}
	    	else{
	    		
	        	submitText.data="Uploading...";
	        	submitCrop.disabled = true;
	        	
		        if(localMediaStream != null && localMediaStream != undefined){
					localMediaStream.stop();
		        }
				$('#upload-tool').remove();
		
		        upload();
	    	}
	    };
	
	    upload = function(){
	    	
	    	var dataURL = image_target.src;
	    	
			if(!tookPic && !uploadedCheck){
	    		alert("Image Error: no image data detected");
	    	}
	    	else{
	
	    		tookPic = false;
	
	    		if (!Date.now) {
	    			Date.now = function() { return new Date().getTime(); }
				}
	
	            var ajaxW = $('#resize-image').width();
	
	    		var coordsArray = [x1, y1, x2, y2, width, height, ajaxW];
	            console.log(coordsArray);
	
				$.ajax({
	  				method: "POST",
	    	   		url: "/content/girlscouts-vtk/controllers/vtk.include.imageStore.html?" + Date.now(), //random string to prevent ajax caching
	                data: { imageData: dataURL, coords: coordsArray.toString() },
					success: uploadSuccess
				})
	  			.done(function( msg ) {
	    	   		console.log( "Uploaded");
	  			})
	  			.fail(function(msg){
	  				alert("Upload failed");
	  				cancel();
	  			});
	    	}
		}
	
	    $(window).resize(function() {
	        $('#cropping-tool').css("max-width", $('#modal_upload_image').innerWidth());
	        $('#cropping-tool').css("max-height", $('#resize-image').height())
	    });
	
	    image_target.onload = function(){
	        init();
	        $('#resize-image').imgAreaSelect({
	    		handles: true,
	    		aspectRatio: "48:17",
	    		onSelectChange: storeCoords,
	    		parent: "#cropping-tool"
	    	});
	    };
	
		image_target.src = image_data;
	
	};
	
	window.onload=function() {
		cancelButton.addEventListener('click',cancel, false);
		displayCurrent();
	}
<%} else{
	%>
	window.onload=function() {
		displayCurrent();
	}<%
}%>

</script>