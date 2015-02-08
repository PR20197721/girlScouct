  //seems to be only used in VTK sections
  var resizeWindow = function() {
    //make sure fixVertical is defined.
    //if(typeof fixVerticalSizing != 'undefined' && fixVerticalSizing === true) {
      //get height of the actual page
      var currentMainHeight = $('.inner-wrap').height(),
          windowHeight = $(window).height(), //get the height of the window
          targetMainHeight = (windowHeight-currentMainHeight);
      //if the content of the page is not to the bottom of the window add this padding, note the row that is the wrapper
      //must have class content
      $('.vtk-body #main .row.content').css('padding-bottom','');
      $('#main.content').css('padding-bottom',''); 
      if(targetMainHeight > 0) {
        $('.vtk-body #main .row.content').first().css('padding-bottom',targetMainHeight + "px");
        $('#main.content').css('padding-bottom',targetMainHeight + "px");
      }
      else {
       $('.vtk-body #main .row.content').css('padding-bottom','');
       $('#main.content').css('padding-bottom','');
      }
  };
//need to add class for small screens only on the footer links.
  function addClassGrid() {
    if ($(window).width() < 640) {
        $('.footer-navigation > div:nth-of-type(1) ul').addClass('small-block-grid-2');
        $('.footer-navigation > div:nth-of-type(2) ul').css('text-align', 'center');
       }
       else {
        $('.footer-navigation > div:nth-of-type(1) ul').removeClass('small-block-grid-2');
        $('.footer-navigation > div:nth-of-type(2) ul').css('text-align', 'right');
      }
  }

$(document).ready(function(){
 resizeWindow();
 addClassGrid();
})
$(window).load(function(){
  var currentMainHeight = $('.inner-wrap').height();
  //get the height of the window
  var windowHeight = $(window).height();
  var targetMainHeight = (windowHeight-currentMainHeight);
  if(targetMainHeight != 0) {
    resizeWindow();
  }
})
$(window).resize(function() {
 //first remove the padding added after reload.
  $('.vtk-body #main .row.content').css('padding-bottom','');
  $('#main.content').css('padding-bottom','');
  resizeWindow();
  addClassGrid();
});