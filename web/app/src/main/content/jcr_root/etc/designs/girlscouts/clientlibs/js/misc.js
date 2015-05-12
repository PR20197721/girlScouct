$(function() {
  $('.search-icon').click(function(event) {
	$('.vtk-login').addClass('hide');
	$('.srch-box').removeClass('hide');
  });
  $(document).click(function(event) {
    if($(window).width() < 640) {
     var target = $(event.target);
       if (target.closest('.topMessage').length == 0) {
        $('.srch-box').addClass('hide');
        $('.vtk-login').removeClass('hide');
       }
    }
  });
});