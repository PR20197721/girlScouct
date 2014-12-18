//all jQuery code that can be loaded in the footer should
//go here.

//seems to be only used in mostly in VTK sections
var resizeWindow = function(){
  if(fixVerticalSizing) {
    //get height of the actual page
    var currentMainHeight = $('.inner-wrap').height();
    //get the height of the window
    var windowHeight = $(window).height();
    var targetMainHeight = (windowHeight-currentMainHeight);
    //if the content of the page is not to the bottom of the window add this padding, note the row that is the wrapper
    //must have class content
    if(targetMainHeight > 0) {
      $('#main .row.content').css('padding-bottom',targetMainHeight + "px");
    }
  }
};
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

//doc.ready function and all the calls.
$(function() {
  resizeWindow();
  addClassGrid();
});
$( window ).resize(function() {
$('#main .row.content').css('padding-bottom',0);
  resizeWindow();
  addClassGrid();
});