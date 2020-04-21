$(document).ready(function() {
    var slickCtrlCheck = $('.main-slider').data('slickControl');
	console.log('slickCtrlCheck '+slickCtrlCheck);
    if (!slickCtrlCheck) {
        $(".main-slider .slick-next").hide();
        $(".main-slider .slick-prev").hide();
    } else {
        $(".main-slider .slick-next").show();
        $(".main-slider .slick-prev").show();
    }
})