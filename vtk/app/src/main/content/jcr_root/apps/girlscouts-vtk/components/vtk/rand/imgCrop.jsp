<link rel="stylesheet" type="text/css" href="/etc/designs/girlscouts-vtk/clientlibs/css/crop/jquery.simpleimagecrop.css" media="all">
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/crop/jquery.simpleimagecrop.js"></script>



<script type="text/javascript">
    $(window).load(function() {
        $('#crop_container').simpleImageCrop({
            maxPreviewImageWidth: 700,
            maxPreviewImageHeight: 700,
            newImageHeight: 300,
            newImageWidth: 400,
            imageDestination: 'test2', // destination from the folder where the php script is located (without extension). if the same as the source file the image will be overwritten.
            phpScriptLocation: '/content/girlscouts-vtk/controllers/auth.asset.html',
            successMessage: 'The image has been cropped!',
            warningMessage: 'Warning: Selected area is too small. The image will be blurry.',
troopId:'701G0000000uQzTIAU',
upldTroopPic:"tata"
        });         
    });   
</script>

  <noscript>Please enable JavaScript to use the Simple Image Crop plugin.</noscript>
<div id="crop_container" class="hidden">
    <div id="crop_box">
        <div id="resize_icon"></div>
    </div>
    <div id="crop_message">Leave this text here</div>
    <img id="image_to_crop" src="http://localhost:4503/content/dam/girlscouts-shared/images/banners/large/HOME_HERO-DONATE.png/_jcr_content/renditions/cq5dam.web.960.420.png" alt="">    
    <div id="crop_button">
        <img src="img/cut_icon.png" alt="Crop">
    </div>
</div>
 
<!-- div below can be put apart from the crop container -->
<div id="crop_preview" class="hidden">
    <div id="preview_message">Preview</div>
    <img id="image_preview" alt="Crop Preview">   
</div>
