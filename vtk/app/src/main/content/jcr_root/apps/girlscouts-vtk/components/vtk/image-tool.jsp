<div id="image-tool" style="width:100%"></div>

<script>

var imageTool = document.getElementById("image-tool");
var uploadTool, croppingTool, currentDisplay;

var cancelButton = document.createElement("Button");
var cancelText = document.createTextNode("Cancel");
    cancelButton.appendChild(cancelText);
    cancelButton.className = "cancel";
    cancelButton.style.float = "right";

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

var imgPath = "<%= "/content/dam/girlscouts-vtk/camera-test/troop-data/"+ troop.getTroop().getCouncilCode() +"/" + troop.getTroop().getTroopId() + "/imgLib/troop_pic.png?" %>";

var displayCurrent = function(){
    currentDisplay = document.createElement("div");
    currentDisplay.id = "current-display";
    var currentPic = document.createElement("img");
    currentPic.id = "current-picture";
    
    currentPic.style.maxWidth = imgMaxW + "px";
    currentPic.style.maxHeight = imgMaxH + "px";
    
    if (!Date.now) {
		Date.now = function() { return new Date().getTime(); }
	}
    
    currentPic.src = imgPath + Date.now();
    currentPic.style.float = "left";

    var displayButtons = document.createElement("div");
    displayButtons.id="display-buttons";
    displayButtons.style.float = "left";
    displayButtons.style.clear = "both";
    displayButtons.style.overflow = "hidden";

    var newButton = document.createElement("button");
	newButton.id = "new-image";
	var newButtonText = document.createTextNode("Upload New Image");
    newButton.appendChild(newButtonText);
    
    displayButtons.appendChild(newButton);

    newButton.addEventListener('click', uploadNew, false);

    var clearBoth = document.createElement("div");
    clearBoth.id = "clear-both";
    clearBoth.style.clear = "both";

    currentPic.onload = function(){
		currentDisplay.appendChild(currentPic);
        currentDisplay.appendChild(displayButtons);
        
        if($(currentPic).width() < maxWidth){
        	$(currentPic).width(maxWidth);
        	$(currentPic).height(maxHeight);
        }
    }
    currentPic.onerror = function(){
        currentDisplay.appendChild(displayButtons);
    }

    function uploadNew(){
		removeCurrent();
		uploadInit();
    }

    imageTool.appendChild(currentDisplay);
    imageTool.appendChild(clearBoth);
}

var removeCurrent = function(){
	$('#current-display').remove();
	$('#clear-both').remove();
}

var uploadInit = function(){

    localMediaStream = null;
    hasCamera = false;
    uploadedCheck = false;
    tookPic = false;
    aspectWeirdness = false;

	uploadTool = document.createElement("div");
    uploadTool.id = "upload-tool"
    uploadTool.style.display = "hidden";

	var uploadMsg = document.createElement("p");
    var text = document.createTextNode("Upload an image from your phone or computer");
    uploadMsg.appendChild(text);

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
    
	var canvas = document.createElement("canvas");
    canvas.id = "canvas";
    canvas.setAttribute("width","100%");
    canvas.style.maxWidth = maxWidth + "px"; 
	canvas.style.maxHeight = maxHeight + "px";
    canvas.style.display = "none";

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
	var text4 = document.createTextNode("Select this picture");
    submitShot.appendChild(text4);

	var switchButton = document.createElement("button");
    switchButton.id = "switchCam";
    switchButton.style.float = "left";
    switchButton.style.display = "none";
	var switchText = document.createTextNode("Switch to Camera");
    switchButton.appendChild(switchText);
    
    var rotateButton = document.createElement("button");
    rotateButton.id = "rotate-button";
    rotateButton.style.float = "left";
    rotateButton.style.display = "none";
    var rotateText = document.createTextNode("Rotate");
    rotateButton.appendChild(rotateText);
    
    var uploadButtons = document.createElement("div");
    uploadButtons.id = "upload-buttons";
    uploadButtons.style.overflow = "hidden";

    imageTool.appendChild(uploadTool);

    uploadTool.appendChild(uploadMsg);
	uploadTool.appendChild(imageLoader);
    uploadTool.appendChild(video);
    uploadTool.appendChild(canvas);
    uploadTool.appendChild(uploadButtons);
    uploadButtons.appendChild(takeShot);
    uploadButtons.appendChild(retakeShot);
    uploadButtons.appendChild(submitShot);
    uploadButtons.appendChild(switchButton);
    uploadButtons.appendChild(rotateButton);
    uploadButtons.appendChild(cancelButton);

    function handleImage(imageEvent){
    	var reader = new FileReader();
    	reader.onload = function(readerEvent){
        	img = new Image();
        	img.style.maxWidth = imgMaxW;
        	img.style.maxHeight = imgMaxH;
        	img.onload = function(){
            	img.src = readerEvent.target.result;
            	canvas.width = img.width;
            	canvas.height = img.height;
            	context.drawImage(img, 0, 0, img.width, img.height, 0, 0, canvas.width, canvas.height);
                if(window.innerWidth < maxWidth){
    				canvas.style.maxWidth = window.innerWidth;
           	 	}
            	else if(window.innerWidth > maxWidth){
    				canvas.style.maxWidth = maxWidth;
            	}
            	if(window.innerHeight < maxHeight){
    				canvas.style.maxHeight = window.innerHeight;
            	}
    			else if(window.innerHeight > maxHeight){
    				canvas.style.maxHeight = maxHeight;
            	}
            	if(canvas.toDataURL() == "data:,"){//mobile safari
                	aspectWeirdness = true;
                	aspectRatio = img.width/img.height;
                	if(img.width > img.height){
    					img.height = canvas.height;
                    	img.width = img.height*aspectRatio;
                	}
                	else{
    					img.width = canvas.width;
                		img.height = img.width/aspectRatio;
                	}
                	canvas.width = img.width;
                    canvas.height = img.height;
                	context.drawImage(img, 0, 0, img.width, img.height);
                }
        	}
        	img.src = readerEvent.target.result;
        	uploadedCheck = true;
    	}
    	reader.readAsDataURL(imageEvent.target.files[0]);
    	
    	canvas.style.display='block';
    	switchButton.innerHTML = "Switch to camera";
    	if(hasCamera == true){
			switchButton.style.display='block';
    	}
    	rotateButton.style.display = 'block';
    	submitShot.style.display='block';
    	takeShot.style.display='none';
    	retakeShot.style.display='none';
    	if(video.style.display == 'block'){
    		video.style.display = 'none';
    	}
	}

	function switchCam(){
    	context.clearRect ( 0 , 0 , canvas.width, canvas.height );
		if(video.style.display == 'block' || tookPic){
			video.style.display = 'none';
        	retakeShot.style.display='none';
        	rotateButton.style.display='none';
        	takeShot.style.display='none';
			switchButton.innerHTML = "Switch to camera";
			if(img != null){
            	canvas.width = img.width;
            	canvas.height = img.height;
            	context.drawImage(img, 0, 0, img.width, img.height, 0, 0, canvas.width, canvas.height);
            	canvas.style.display = 'block';
            	rotateButton.style.display = 'block';
            	submitShot.style.display='block';
        	}
        	tookPic = false;
		}
		else{
			video.style.display = 'block';
			takeShot.style.display='block';
        	canvas.style.display = 'none';
        	rotateButton.style.display = 'none';
        	submitShot.style.display = 'none';
        	if(img != null){
        		rotateButton.style.display = 'block';
        		switchButton.style.display='block';
        	}
			switchButton.innerHTML = "Switch to uploaded image";
        	tookPic = false;
		}
	}    
    
    var uploadText;

	//website requests permission to use your webcam
	if (navigator.getUserMedia) {
        uploadText = document.createTextNode(" or take a photo from your webcam");
		uploadMsg.appendChild(uploadText);
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
	} else {
   		console.log("getUserMedia not supported");
	}

	function snapshot() {
    	if (localMediaStream) {
        	tookPic = true;
        	canvas.width = video.getBoundingClientRect().width;
        	canvas.height = video.getBoundingClientRect().height;
        	video.style.display='none';
        	takeShot.style.display='none';
        	canvas.style.display='block';
        	rotateButton.style.display = 'block';
			retakeShot.style.display='block';
        	submitShot.style.display='block';
        	if(img != null){
        		switchButton.innerHTML="Switch to uploaded image";
        		switchButton.style.display='block';
        	}
        	context.drawImage(video, 0, 0);
        	picData = context.getImageData(0,0, canvas.width, canvas.height);
    	}
	}

	function retake() {
    	tookPic = false;
    	canvas.style.display='none';
    	rotateButton.style.display = 'none';
    	retakeShot.style.display='none';
    	submitShot.style.display='none';
		video.style.display='block';
    	takeShot.style.display='block';
	}

    function resizeUpload(){
        uploadTool.style.display = "none";
        resizeableImage(canvas.toDataURL("image/png",1.0));
    }
    
    function rotate(){
    	context.clearRect ( 0 , 0 , canvas.width, canvas.height );
    	/// translate so rotation happens at center of image
		context.translate(img.width * 0.5, img.height * 0.5);

		/// rotate canvas context
		context.rotate(0.5 * Math.PI); /// 90deg clock-wise

		/// translate back so next draw op happens in upper left corner
		context.translate(-img.width * 0.5, -img.height * 0.5);
		
		if(!aspectWeirdness){
        	context.drawImage(img, 0, 0, img.width, img.height, 0, 0, canvas.width, canvas.height);
        }
        else{
        	context.drawImage(img, 0, 0, img.width, img.height);
        }
    }
    
    $(window).resize(function() {
        if(canvas.style.display == 'block'){
            if(window.innerWidth < maxWidth){
    			canvas.style.maxWidth = window.innerWidth + "px";
            }
            else if(window.innerWidth > maxWidth){
    			canvas.style.maxWidth = maxWidth + "px";
            }
            if(window.innerHeight < maxHeight){
    			canvas.style.maxHeight = window.innerHeight + "px";
            }
    		else if(window.innerHeight > maxHeight){
    			canvas.style.maxHeight = maxHeight + "px";
            }
      		// Resize original canvas
            if(tookPic && picData != null){
    			context.putImageData(picData, 0, 0);
            }
            else if(!aspectWeirdness){
        		context.drawImage(img, 0, 0, img.width, img.height, 0, 0, canvas.width, canvas.height);
            }
            else{
    			if(img.width > img.height){
    				img.height = canvas.style.maxHeight.replace("px","");
                    img.width = img.height*aspectRatio;
                }
                else{
    				img.width = canvas.style.maxWidth.replace("px","");
                	img.height = img.width/aspectRatio;
                }
                canvas.width = img.width;
                canvas.height = img.height;
                context.drawImage(img, 0, 0, img.width, img.height);
            }
        }

    });

    imageLoader.addEventListener('change', handleImage, false);
    takeShot.addEventListener('click', snapshot, false);
    retakeShot.addEventListener('click', retake, false);
    switchButton.addEventListener('click', switchCam, false);
    submitShot.addEventListener('click', resizeUpload, false);
    rotateButton.addEventListener('click', rotate, false);
}

var resizeableImage = function(image_data){

	croppingTool = document.createElement("div");
    croppingTool.id = "cropping-tool";
    croppingTool.style.maxWidth = window.innerWidth + "px";
    croppingTool.style.position = "relative";

	var backToUpload = document.createElement("button");
    backToUpload.id = "back-button";
	backToUpload.style.float = "left";
	var backText = document.createTextNode("Choose another picture");
    backToUpload.appendChild(backText);

	var cropButtons = document.createElement("div");
    cropButtons.id = "crop-buttons";
    cropButtons.style.overflow = "hidden";
    cropButtons.style.width = "100%";
    cropButtons.style.position = "relative";

	var submitCrop = document.createElement("button");
    submitCrop.id = "submitCrop";
    submitCrop.style.float = "left";
	var submitText = document.createTextNode("Submit");
    submitCrop.appendChild(submitText);

    imageTool.appendChild(croppingTool);
    imageTool.appendChild(cropButtons);

    cropButtons.appendChild(backToUpload);
    cropButtons.appendChild(submitCrop);
    
    cropButtons.appendChild(cancelButton);

    var image_target = document.createElement("img");
    	image_target.id = "resize-image";
    	image_target.src = image_data;

    croppingTool.appendChild(image_target);

    init = function(){

    	$('#resize-image').imgAreaSelect({
    		handles: true,
    		aspectRatio: "48:17",
    		onSelectChange: storeCoords,
    		parent: "#cropping-tool"
    	});

        submitCrop.addEventListener('click', crop, false);
        backToUpload.addEventListener('click', back, false);
        
    };
    
    var x1, x2, y1, y2, width, height;
    
    storeCoords = function(img, selection){
    	console.log(x1 + "," + y1 + " -> " + x2 + "," + y2);
    	x1 = selection.x1;
    	x2 = selection.x2;
    	y1 = selection.y1;
    	y2 = selection.y2;
    	width = selection.width;
    	height = selection.height;
    };

    crop = function(){
        if(localMediaStream != null && localMediaStream != undefined){
			localMediaStream.stop();
        }
		$('#upload-tool').remove();

        upload();
    };

    upload = function(){
    	
    	var dataURL = image_target.src;
    	
		if(!tookPic && !uploadedCheck){
    		alert("Image Error: no image data detected");
    	}
    	else{

            $('#crop-buttons').remove();

    		tookPic = false;

    		if (!Date.now) {
    			Date.now = function() { return new Date().getTime(); }
			}
    		
    		var coordsArray = [x1, y1, x2, y2, width, height];

			$.ajax({
  				method: "POST",
    	   		url: "/content/girlscouts-vtk/controllers/vtk.include.imageStore.html?" + Date.now(), //random string to prevent ajax caching
    	    	data: { imageData: dataURL, coords: coordsArray.toString() }
			})
  			.done(function( msg ) {
    	   		console.log( "Uploaded");
  			})
  			.fail(function(msg){
  				alert("Upload failed");
  				cancel();
  			});
    	};
	}

    back = function(){
        $('#cropping-tool').remove();
        $('#crop-buttons').remove();
        uploadTool.style.display = "block";
        uploadTool.appendChild(cancelButton);  
    }

    $(window).resize(function() {
        croppingTool.style.maxWidth = window.innerWidth;
    });

    init();
};

cancel = function(){
	$('#cropping-tool').remove();
	$('#crop-buttons').remove();
	$('#upload-tool').remove();
	displayCurrent();
}

$(document).ajaxSuccess(function() {
  alert(successMsg);
  $('#upload-tool').remove();
  $('#cropping-tool').remove();
  displayCurrent();
});

window.onload=function() {
	cancelButton.addEventListener('click',cancel, false);
	displayCurrent();
}

</script>