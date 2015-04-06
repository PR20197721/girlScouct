<%@include file="include/session.jsp"%>

<div id="image-tool"></div>

<script>

var imageTool = document.getElementById("image-tool");
var uploadTool, croppingTool, currentDisplay;

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

    //Webcam support for laptop/some devices

//support for multiple browsers
navigator.getUserMedia = ( navigator.getUserMedia ||
                       navigator.webkitGetUserMedia ||
                       navigator.mozGetUserMedia ||
                       navigator.msGetUserMedia);

var imgPath = "/content/dam/girlscouts-vtk/camera-test/troop-data/"+ troop.getTroop().getCouncilCode() +"/" + troop.getTroop().getTroopId() + "/imgLib/troop_pic.png";
var uncroppedPath = "/content/dam/girlscouts-vtk/camera-test/troop-data/"+ troop.getTroop().getCouncilCode() +"/" + troop.getTroop().getTroopId() + "/imgLib/troop_pic_uncropped.png";

var displayCurrent = function(){
    currentDisplay = document.createElement("div");
    currentDisplay.id = "current-display";
    var currentPic = document.createElement("img");
    currentPic.id = "current-picture";
    currentPic.src = imgPath;

    var loadUncroppedButton = document.createElement("button");
	loadUncroppedButton.id = "load-uncropped";
    loadUncroppedButton.style.float = "left";
	var loadText = document.createTextNode("Uncropped Image Found");
    loadUncroppedButton.appendChild(loadText);

    var newButton = document.createElement("button");
	newButton.id = "new-image";
    newButton.style.float = "left";
	var newButtonText = document.createTextNode("Upload New Image");
    newButton.appendChild(newButtonText);

    loadUncroppedButton.addEventListener('click', loadUncropped, false);
    newButton.addEventListener('click', uploadNew, false);

    var currentUncropped = document.createElement("img");

    currentPic.onload = function(){
        if(currentPic.height != 0) { //check if img exists yet
			currentDisplay.appendChild(currentPic);
        }
		currentDisplay.appendChild(currentPic);
        currentUncropped.src = uncroppedPath;
        currentUncropped.onload = function(){
			if(currentUncropped.height != 0) { //check if img exists yet
                currentDisplay.appendChild(loadUncropped);
        	}
            currentDisplay.appendChild(newButton);
        }
    }

    var loadUncropped = function(){
    	removeCurrent();
    	resizeableImage(currentUncropped);
    }

    var newButton = function(){
		removeCurrent();
		uploadInit();
    }

    imageTool.appendChild(currentDisplay);
}

var removeCurrent = function(){
	$('#currentDisplay').remove();
}

var uploadInit = function(){

	uploadTool = document.createElement("div");
    uploadTool.id = "upload-tool"
    uploadTool.style.display = "hidden";
    uploadTool.style.width = "960px";

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
    video.setAttribute("width","100%");
    video.id = "video";
    video.style.maxWidth = $('#upload-tool').width() + "px";
	video.style.maxHeight = $('#upload-tool').height() + "px";
    video.style.display = "none";

	var canvas = document.createElement("canvas");
    canvas.id = "canvas";
    canvas.setAttribute("width","100%");
    canvas.style.maxWidth = $('#upload-tool').width() + "px";
	canvas.style.maxHeight = $('#upload-tool').height() + "px";
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

    imageTool.appendChild(uploadTool);

    uploadTool.appendChild(uploadMsg);
	uploadTool.appendChild(imageLoader);
    uploadTool.appendChild(video);
    uploadTool.appendChild(canvas);
    uploadTool.appendChild(takeShot);
    uploadTool.appendChild(retakeShot);
    uploadTool.appendChild(submitShot);
    uploadTool.appendChild(switchButton);

    if(window.innerHeight < 340 || window.innerWidth < 960){
		text4.data = "Submit";
    }

    function handleImage(imageEvent){
    	var reader = new FileReader();
    	reader.onload = function(readerEvent){
        	img = new Image();
        	img.onload = function(){
            	img.src = readerEvent.target.result;
            	canvas.width = img.width;
            	canvas.height = img.height;
                if(window.orientation == 0 || window.orientation == 180){
					/// translate so rotation happens at center of image
					context.translate(img.width * 0.5, img.height * 0.5);

					/// rotate canvas context
					context.rotate(0.5 * Math.PI); /// 90deg clock-wise

					/// translate back so next draw op happens in upper left corner
					context.translate(-img.width * 0.5, -img.height * 0.5);
                }
            	context.drawImage(img, 0, 0, img.width, img.height, 0, 0, canvas.width, canvas.height);
            	if(canvas.toDataURL() == "data:,"){//mobile safari
                	aspectWeirdness = true;
                	aspectRatio = img.width/img.height;
                	if(window.innerWidth < 960){
    					canvas.style.maxWidth = window.innerWidth + "px";
            		}
            		else if(window.innerWidth > 960){
    					canvas.style.maxWidth = "960px";
            		}
            		if(window.innerHeight < 340){
    					canvas.style.maxHeight = window.innerHeight + "px";
            		}
    				else if(window.innerHeight > 340){
    					canvas.style.maxHeight = "340px";
            		}
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

                    if(window.orientation == 0 || window.orientation == 180){
						/// translate so rotation happens at center of image
                        context.translate(img.width * 0.5, img.height * 0.5);

						/// rotate canvas context
						context.rotate(0.5 * Math.PI); /// 90deg clock-wise

						/// translate back so next draw op happens in upper left corner
                        context.translate(-img.width * 0.5, -img.height * 0.5);
                	}

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
        	takeShot.style.display='none';
			switchButton.innerHTML = "Switch to camera";
			if(img != null){
            	canvas.width = img.width;
            	canvas.height = img.height;
            	context.drawImage(img, 0, 0, img.width, img.height, 0, 0, canvas.width, canvas.height);
            	canvas.style.display = 'block';
            	submitShot.style.display='block';
        	}
        	tookPic = false;
		}
		else{
			video.style.display = 'block';
			takeShot.style.display='block';
        	canvas.style.display = 'none';
        	submitShot.style.display = 'none';
        	if(img != null){
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
    	retakeShot.style.display='none';
    	submitShot.style.display='none';
		video.style.display='block';
    	takeShot.style.display='block';
	}

    function resizeUpload(){
        if((window.innerHeight < 340 || window.innerWidth < 960) && submitShot.style.display == "block"){
            retakeShot.style.display = "none";
            switchButton.style.display = "none";
            submitShot.disabled = true;
            text4.data = "Uploading...";
            submitUncropped(canvas.toDataURL("image/png",1.0));
        }
        else{
            uploadTool.style.display = "none";
        	resizeableImage(canvas.toDataURL("image/png",1.0));
        }
    }

    function submitUncropped(dataURL){
		var dataURL = canvas.toDataURL("image/png",1.0); //change the second parameter to reduce quality
    	if(!tookPic && !uploadedCheck){
    		alert("Image Error: no image data detected");
    	}
    	else{
    		tookPic = false;

    		if (!Date.now) {
    			Date.now = function() { return new Date().getTime(); }
			}

            successMsg = "Your browser window is too small for the image cropping tool. Your picture will be stored but not set, and can be accessed from the My Troop VTK Page."; 
	
			$.ajax({
  				method: "POST",
    	    	url: "/content/girlscouts-vtk/controllers/vtk.include.imageStore.html?" + Date.now(), //random string to prevent ajax caching
                data: { imageData: dataURL, uncropped: "true" }
			})
  			.done(function( msg ) {
    	    	console.log( "Uploaded");
  			});
    	}
    }
    
    $(window).resize(function() {
        if(canvas.style.display == 'block'){
            if(window.innerWidth < 960){
    			canvas.style.maxWidth = window.innerWidth + "px";
            }
            else if(window.innerWidth > 960){
    			canvas.style.maxWidth = "960px";
            }
            if(window.innerHeight < 340){
    			canvas.style.maxHeight = window.innerHeight + "px";
            }
    		else if(window.innerHeight > 340){
    			canvas.style.maxHeight = "340px";
            }
      		// Resize original canvas
            if(tookPic && picData != null){
    			context.putImageData(picData, 0, 0);
            }
            else if(!aspectWeirdness){
                if(window.orientation == 0 || window.orientation == 180){
					/// translate so rotation happens at center of image
					context.translate(img.width * 0.5, img.height * 0.5);

					/// rotate canvas context
					context.rotate(0.5 * Math.PI); /// 90deg clock-wise

					/// translate back so next draw op happens in upper left corner
					context.translate(-img.width * 0.5, -img.height * 0.5);
                }
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
                if(window.orientation == 0 || window.orientation == 180){
					/// translate so rotation happens at center of image
					context.translate(img.width * 0.5, img.height * 0.5);

					/// rotate canvas context
					context.rotate(0.5 * Math.PI); /// 90deg clock-wise

					/// translate back so next draw op happens in upper left corner
					context.translate(-img.width * 0.5, -img.height * 0.5);
                }
                context.drawImage(img, 0, 0, img.width, img.height);
            }
        }
        if(window.innerHeight < 340 || window.innerWidth < 960){
            if(submitShot.style.display == "block"){
            	submitShot.style.display = "hidden";
            }
			text4.data = "Submit";
        }
        else if(window.innerHeight >= 340 || window.innerWidth >= 960){
            if(submitShot.style.display == "hidden"){
            	submitShot.style.display = "block";
            }
            text4.data = "Select this picture";
        }

    });

    imageLoader.addEventListener('change', handleImage, false);
    takeShot.addEventListener('click', snapshot, false);
    retakeShot.addEventListener('click', retake, false);
    switchButton.addEventListener('click', switchCam, false);
    submitShot.addEventListener('click', resizeUpload, false);
}

var resizeableImage = function(image_data){

	croppingTool = document.createElement("div");
    croppingTool.id = "cropping-tool";
    croppingTool.style.overflow = "hidden";
    croppingTool.style.width = "100%";
    croppingTool.style.position = "relative";

	var overlay = document.createElement("div");
    overlay.className = "overlay";

	var overlayInner = document.createElement("div");
    overlayInner.className = "overlay-inner";

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

    croppingTool.appendChild(overlay);

    overlay.appendChild(overlayInner);

    cropButtons.appendChild(backToUpload);
    cropButtons.appendChild(submitCrop);

    var image_target = document.createElement("img");
    	image_target.id = "resize-image";
    	image_target.src = image_data;

    croppingTool.appendChild(image_target);

    if(window.innerWidth < 960){
		croppingTool.style.maxWidth = window.innerWidth + "px";
    }
    if(window.innerHeight < 340){
		croppingTool.style.maxHeight = window.innerHeight + "px";
    }

    var $container,
        orig_src = new Image(),
        image_target = $(image_target).get(0),
        event_state = {},
        constrain = false,
        min_width = 120, //change as required
        min_height = 120,
        max_width = 1220, //change as required
        max_height = 1080,
        resize_canvas = document.createElement('canvas');

    init = function(){

        //When resizing, we will always use this copy of the original as the base
        orig_src.src = image_target.src;

        //Wrap the image with the container and add resize handles
        $(image_target).wrap('<div class="resize-container"></div>')
    .before('<span class="resize-handle resize-handle-nw"></span>')
    .before('<span class="resize-handle resize-handle-ne"></span>')
    .after('<span class="resize-handle resize-handle-se"></span>')
    .after('<span class="resize-handle resize-handle-sw"></span>');

        //Assign the container to a variable
        $container = $(image_target).parent('.resize-container');

        $('#cropping-tool').css({"min-width": $('#cropping-tool').width(), "min-height": $('#cropping-tool').height()});

        overlayOffset();

        //Add events
        $container.on('mousedown touchstart', '.resize-handle', startResize);
        $container.on('mousedown touchstart', 'img', startMoving);

        submitCrop.addEventListener('click', crop, false);
        backToUpload.addEventListener('click', back, false);
    };

    startResize = function(e){
		e.preventDefault();
        e.stopPropagation();
        saveEventState(e);
        $(document).on('mousemove touchmove', resizing);
        $(document).on('mouseup touchend', endResize);
    };

    endResize = function(e){
		e.preventDefault();
        $(document).off('mouseup touchend', endResize);
        $(document).off('mousemove touchmove', resizing);
    };

    saveEventState = function(e){
        //Save the initial event details and container state
        event_state.container_width = $container.width();
        event_state.container_height = $container.height();
        event_state.container_left = $container.offset().left;
        event_state.container_top = $container.offset().top;
        event_state.mouse_x = (e.clientX || e.pageX || e.originalEvent.touches[0].clientX) + $(window).scrollLeft();
        event_state.mouse_y = (e.clientY || e.pageY || e.originalEvent.touches[0].clientY) + $(window).scrollTop();

        //This is a fix for mobile safari
        //For some reason it does not allow a direct copy of the touches property
        if(typeof e.originalEvent.touches != 'undefined'){
            event_state.touches = [];
            $.each(e.originalEvent.touches, function(i, ob){
                event_state.touches[i] = {};
                event_state.touches[i].clientX = 0+ob.clientX;
                event_state.touches[i].clientY = 0+ob.clientY;
            });
        }

        event_state.evnt = e;
    };

    resizing = function(e){
        var mouse = {},width,height,left,top,offset=$container.offset();
        if(e.originalEvent.touches != undefined){
            if(e.originalEvent.touches[0] != undefined){
                mouse.x = (e.clientX || e.pageX || e.originalEvent.touches[0].clientX) + $(window).scrollLeft();
                mouse.y = (e.clientY || e.pageY || e.originalEvent.touches[0].clientY) + $(window).scrollTop();
            }
        }
        else{
            mouse.x = (e.clientX || e.pageX) + $(window).scrollLeft();
            mouse.y = (e.clientY || en.pageY) + $(window).scrollTop();
        }

        //Position image differently depending on the corner dragged and constraints
        if( $(event_state.evnt.target).hasClass('resize-handle-se') ){
            width = mouse.x - event_state.container_left;
            height = mouse.y - event_state.container_top;
            left = event_state.container_top;
            top = event_state.container_top;
        } else if($(event_state.evnt.target).hasClass('resize-handle-sw')){
            width = event_state.container_width - (mouse.x - event_state.container_left);
            height = mouse.y - event_state.container_top;
            left = mouse.x;
            top = event_state.container_top;
        } else if($(event_state.evnt.target).hasClass('resize-handle-nw')){
            width = event_state.container_width - (mouse.x - event_state.container_left);
            height = event_state.container_height - (mouse.y - event_state.container_top);
            left = mouse.x;
            top = mouse.y;
            if(constrain || e.shiftKey){
                top = mouse.y - ((width / orig_src.width * orig_src.height) - height);
            }
        } else if($(event_state.evnt.target).hasClass('resize-handle-ne')){
            width = mouse.x - event_state.container_left;
            height = event_state.container_height - (mouse.y - event_state.container_top);
            left = event_state.container_left;
            top = mouse.y;
            if(constrain || e.shiftKey){
                top = mouse.y - ((width / orig_src.width * orig_src.height) - height);
            }
        }

        //Optionally maintain aspect ratio
        if(constrain || e.shiftKey){
            height = width / orig_src.width * orig_src.height;
        }

        if(width > min_width && height > min_height && width < max_width && height < max_height){
            //To improve performance you might limit how often resizeImage() is called
            resizeImage(width,height);
            //Without this Firefox will not recalculate the image dimensions until drag end
            $container.offset({'left': left, 'top': top});
        }
    }

    resizeImage = function(width, height){
        resize_canvas.width = width;
        resize_canvas.height = height;
        resize_canvas.getContext('2d').drawImage(orig_src, 0, 0, width, height);
        $(image_target).attr('src', resize_canvas.toDataURL("image/png"));
    };

    overlayOffset = function(){
		var overTop = ($('#cropping-tool').height() - $('.overlay').height())/(2 * $('#cropping-tool').height()) * 100;
        overTop = overTop + "%";
        var overLeft = ($('#cropping-tool').width() - $('.overlay').width())/(2 * $('#cropping-tool').width()) * 100;
        overLeft = overLeft + "%";
        $('.overlay').css({top: overTop, left: overLeft});
    };

    startMoving = function(e){
        e.preventDefault();
        e.stopPropagation();
        saveEventState(e);
        $(document).on('mousemove touchmove', moving);
        $(document).on('mouseup touchend', endMoving);
    };

    endMoving = function(e){
		e.preventDefault();
        $(document).off('mouseup touchend', endMoving);
        $(document).off('mousemove touchmove', moving);
    };

    moving = function(e){
        var mouse={}, touches;
        e.preventDefault();
        e.stopPropagation();

        touches = e.originalEvent.touches;
        if(touches != undefined){
            if(touches[0] != undefined){
                mouse.x = (e.clientX || e.pageX || touches[0].clientX) + $(window).scrollLeft();
                mouse.y = (e.clientY || e.pageY || touches[0].clientY) + $(window).scrollTop();
            }
        }
        else{
            mouse.x = (e.clientX || e.pageX) + $(window).scrollLeft();
            mouse.y = (e.clientY || e.pageY) + $(window).scrollTop();
        }
        $container.offset({
            'left': mouse.x - (event_state.mouse_x - event_state.container_left),
            'top': mouse.y - (event_state.mouse_y - event_state.container_top)
        });

        //Watch for pinch zoom gesture qhile moving
        if(event_state.touches && event_state.touches.length > 1 && touches.length > 1){
            var width = event_state.container_width, height = event_state.container_height;
            var a = event_state.touches[0].clientX - event_state.touches[1].clientX;
            a = a * a;
            var b = event_state.touches[0].clientY - event_state.touches[1].clientY;
            b = b * b;
            var dist1 = Math.sqrt(a + b);

            a = e.originalEvent.touches[0].clientX - touches[1].clientX;
            a = a * a;
            b = e.originalEvent.touches[0].clientY - touches[1].clientY;
            b = b * b;
            var dist2 = Math.sqrt(a + b);

            var ratio = dist2/dist1;

            width = width * ratio;
            height = height * ratio;
            //To improve performance you might limit how often resizeImage() is called
            resizeImage(width, height);
        }
    };

    crop = function(){
        if(localMediaStream != null && localMediaStream != undefined){
			localMediaStream.stop();
        }
		$('#upload-tool').remove();

        //Find the part of the image that is inside the crop box
        var overlay;
        var crop_canvas,
            left = $('.overlay').offset().left - $container.offset().left,
            top = $('.overlay').offset().top - $container.offset().top,
            width = $('.overlay').width(),
            height = $('.overlay').height();

        crop_canvas = document.createElement('canvas');
        crop_canvas.width = width;
        crop_canvas.height = height;

        crop_canvas.getContext('2d').drawImage(image_target, left, top, width, height, 0, 0, width, height);
        if(aspectWeirdness){
        	left = left - (2*window.scrollX);
            aspectRatio = image_target.width/image_target.height;
            if(image_target.width > image_target.height){
				image_target.height = croppingTool.style.maxHeight.replace("px","");
                image_target.width = image_target.height*aspectRatio;
            }
            else{
				image_target.width = croppingTool.style.maxWidth.replace("px","");
            	image_target.height = image_target.width/aspectRatio;
            }
            crop_canvas.width = image_target.width;
            crop_canvas.height = image_target.height;
            crop_canvas.getContext('2d').drawImage(image_target, left, top, width, height, 0, 0, width, height);
        }
        upload(crop_canvas.toDataURL("image/png"));
    };

    upload = function(dataURL){
		if(!tookPic && !uploadedCheck){
    		alert("Image Error: no image data detected");
    	}
    	else{

            $('#crop-buttons').remove();

    		tookPic = false;

    		if (!Date.now) {
    			Date.now = function() { return new Date().getTime(); }
			}

            var ret = new Image();
            ret.src = dataURL;
            ret.width = "960px";
            ret.height = "340px";
            dataURL = ret.src;

			$.ajax({
  				method: "POST",
    	   		url: "/content/girlscouts-vtk/controllers/vtk.include.imageStore.html?" + Date.now(), //random string to prevent ajax caching
    	    	data: { imageData: dataURL }
			})
  			.done(function( msg ) {
    	   		console.log( "Uploaded");
  			});
    	};
	}

    back = function(){
        $('#cropping-tool').remove();
        $('#crop-buttons').remove();
        uploadTool.style.display = "block";
    }

    $(window).resize(function() {
        overlayOffset();
        if(aspectWeirdness){
			if(image_target.width > image_target.height){
				image_target.height = croppingTool.style.maxHeight.replace("px","");
                image_target.width = image_target.height*aspectRatio;
            }
            else{
				image_target.width = croppingTool.style.maxWidth.replace("px","");
            	image_target.height = image_target.width/aspectRatio;
            }
        }
        if(window.innerHeight < 340 || window.innerWidth < 960){
			back();
        }
    });

    init();
};

$(document).ajaxSuccess(function() {
  alert(successMsg);
    $('#upload-tool').remove();
    $('#cropping-tool').remove();
});

window.onload=function() {
	displayCurrent();
}

</script>