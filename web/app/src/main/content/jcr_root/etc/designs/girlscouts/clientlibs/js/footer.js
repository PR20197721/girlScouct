  //seems to be only used in VTK sections
  var resizeWindow = function(){
    //make sure fixVertical is defined.
   if(typeof fixVerticalSizing != 'undefined' && fixVerticalSizing === true) {
      //get height of the actual page
      var currentMainHeight = $('.inner-wrap').height();
      //get the height of the window
      var windowHeight = $(window).height();
      var targetMainHeight = (windowHeight-currentMainHeight);
      //if the content of the page is not to the bottom of the window add this padding, note the row that is the wrapper
      //must have class content
      if(targetMainHeight > 0) {
        $('.vtk-body #main .row.content').css('padding-bottom',targetMainHeight + "px");
        $('#main.content').css('padding-bottom',targetMainHeight + "px");
      }
   }
  };
    $(window).load(function(){
        resizeWindow(); 
    })
  $( window ).resize(function() {
     //first remove the padding added after reload.
      $('#main .row.content').css('padding-bottom','');
      $('#main.content').css('padding-bottom','');
      resizeWindow(); 
  });