$(document).ready(function() {
    var slickCtrlCheck = $('#slick-control').data('slickControl');

    if (!slickCtrlCheck) {
        $(".main-slider .slick-next").hide();
        $(".main-slider .slick-prev").hide();
    } else {
        $(".main-slider .slick-next").show();
        $(".main-slider .slick-prev").show();
    }
})