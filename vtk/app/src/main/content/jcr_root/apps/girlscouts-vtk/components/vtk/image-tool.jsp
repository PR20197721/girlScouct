<div id="image-tool"></div>

<video autoplay id="video" width="100%" style="display:none; max-width:640px; max-height:480px;"></video>
<canvas id="canvas" width="100%" style="display:none; max-width:640px; max-height:480px;"></canvas>


<script>

var uploadMsg = document.createElement("p");
    var text = document.createTextNode("Upload an image from your phone or computer");
    uploadMsg.appendChild(text);

var imageLoader = document.createElement("input");
    imageLoader.id = "imageLoader";
    imageLoader.type = "file";
    imageLoader.accept = "image/*";
    imageLoader.setAttribute("capture","camera");

var video = document.getElementById("video");

var croppingTool = document.createElement("div");
    croppingTool.id = "cropping-tool";
    croppingTool.style="overflow:hidden; height:480px; width:960px; position:relative;";

var canvas = document.getElementById("canvas");

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
var text4 = document.createTextNode("Submit Picture");
    submitShot.appendChild(text4);

var switchButton = document.createElement("button");
    switchButton.id = "switchCam";
    switchButton.style.float = "left";
    switchButton.style.display = "none";
var switchText = document.createTextNode("Switch to Camera");
    switchButton.appendChild(switchText);

var imageTool = document.getElementById("image-tool");

    imageTool.appendChild(uploadMsg);
	imageTool.appendChild(imageLoader);
    imageTool.appendChild(video);
    imageTool.appendChild(croppingTool);
    imageTool.appendChild(takeShot);
    imageTool.appendChild(retakeShot);
    imageTool.appendChild(submitShot);
    imageTool.appendChild(switchButton);
    croppingTool.appendChild(canvas);

var localMediaStream = null;
var context = canvas.getContext('2d');
var hasCamera = false;
var uploadedCheck = false;
var tookPic = false;
var img;
var picData;
var aspectWeirdness = false; //applies to mobile safari, which has resizing issues
var aspectRatio = 1;

    //Webcam support for laptop/some devices

//support for multiple browsers
navigator.getUserMedia = ( navigator.getUserMedia ||
                       navigator.webkitGetUserMedia ||
                       navigator.mozGetUserMedia ||
                       navigator.msGetUserMedia);

function handleImage(imageEvent){
    var reader = new FileReader();
    reader.onload = function(readerEvent){
        img = new Image();
        img.onload = function(){
            img.src = readerEvent.target.result;
            canvas.width = img.width;
            canvas.height = img.height;
            context.drawImage(img, 0, 0, img.width, img.height, 0, 0, canvas.width, canvas.height);
            if(canvas.toDataURL() == "data:,"){//mobile safari
                aspectWeirdness = true;
                aspectRatio = img.width/img.height;
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
        img.src = readerEvent.target.result;
        uploadedCheck = true;
    }
    reader.readAsDataURL(imageEvent.target.files[0]);
    canvas.style.display='block';
    switchText = "Switch to camera";
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
		switchText = "Switch to camera";
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
		switchText = "Switch to uploaded image";
        tookPic = false;
	}
}    
    
//website requests permission to use your webcam
if (navigator.getUserMedia) {
    var camFoundText = document.createTextNode("Or take a photo from your webcam");
	uploadMsg.appendChild(document.createElement("br"));
    uploadMsg.appendChild(camFoundText);
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
          output.innerHTML = "";
          console.log("The following error occurred: " + err);
      }
   );
} else {
    output.innerHTML = "";
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
        	switchText="Switch to uploaded image";
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

function upload() {
    var dataURL = canvas.toDataURL("image/png",1.0); //change the second parameter to reduce quality
    if(!tookPic && !uploadedCheck){
    	alert("Image Error: no image data detected");
    }
    else{
    	tookPic = false;

    	if (!Date.now) {
    		Date.now = function() { return new Date().getTime(); }
		}
	
		$.ajax({
  			method: "POST",
    	    url: "/content/girlscouts-vtk/controllers/vtk.include.imageStore.html?" + Date.now(), //random string to prevent ajax caching
    	    data: { imageData: dataURL }
		})
  		.done(function( msg ) {
    	    console.log( "Uploaded");
  		});
    }
}

$(document).ajaxSuccess(function() {
  alert("Your image has been uploaded");
        imageTool.style.display = "none";
});

$(window).resize(function() {
    if(canvas.style.display == 'block'){
        if(window.innerWidth < 640){
			canvas.style.maxWidth = window.innerWidth + "px";
        }
        else if(window.innerWidth > 640){
			canvas.style.maxWidth = "640px";
        }
        if(window.innerHeight < 480){
			canvas.style.maxHeight = window.innerHeight + "px";
        }
		else if(window.innerHeight > 480){
			canvas.style.maxHeight = "480px";
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

window.onload=function() {
    if(window.innerWidth < 640){
		canvas.style.maxWidth = window.innerWidth + "px";
    }
    if(window.innerHeight < 480){
		canvas.style.maxHeight = window.innerHeight + "px";
    }
	imageLoader.addEventListener('change', handleImage, false);
    takeShot.addEventListener('click', snapshot, false);
    retakeShot.addEventListener('click', retake, false);
    submitShot.addEventListener('click', upload, false);
    switchButton.addEventListener('click', switchCam, false);
}

</script>