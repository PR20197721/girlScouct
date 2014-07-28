$(function() {
	
	  //set variable size based upon window viewport size
	  var largerSize = 686; //desktop and larger size
	  var smallerSize = 599; //mobile max size
	  var mobileMin = 477; //mobile min anomaly size
	  var tinySize = 288; //super small size just in case
	  var containerHeight = 0;

	  //function that assigns variable based on viewport size
	  function assignment() {
	    var viewportWidth = ($(window).width() + 15);
	    switch(true) {
	      case (viewportWidth < tinySize):
	        containerHeight = '400px';
	        break;
	      case (viewportWidth < mobileMin):
	        // TODO: find a more efficient way to resolve screensize 
	    	containerHeight = '390px';
	        break;
	      case (viewportWidth < smallerSize):
	        containerHeight = '375px';
	        break;
	      case (viewportWidth > largerSize):
	        containerHeight = '350px';
	        break;
	      default:
	        containerHeight = '300px';
	        break;
	    }
	  };

	  //assign variables on window load
	  $(document).ready(function() {
	    assignment();
	  });

	  //assign variable on window resize
	  $(window).resize(function() {
	    assignment();
	  });


	  //necessary for view-b
	  $(document).on("click", "#SUP1", function(){
	    //look for class to apply code to, if found open and flip arrow 180 degrees
	    if ($(this).hasClass("toggled-off"))
	      $(this).animate({"height": containerHeight}).removeClass("toggled-off").addClass("toggled-on"),
	      $('#SUP1 .rotate-img').css('-webkit-transform','none'),
	      $('#SUP1 .rotate-img').css('transform','none'),
	      $('#SUP1 .rotate-img').css('-ms-transform','none');
	    else
	      //close panel and return arrow to 0 degrees
	      $(this).animate({"height": "125px"}).removeClass("toggled-on").addClass("toggled-off"),
	      $('#SUP1 .rotate-img').css('-webkit-transform','rotate(180deg)'),
	      $('#SUP1 .rotate-img').css('transform','rotate(180deg)'),
	      $('#SUP1 .rotate-img').css('-ms-transform','rotate(180deg)');
	  });

	  //slide right panel up on click event and rotate arrow image
	  $(document).on("click", "#SUP2", function(){
	    //look for class to apply code to, if found open and flip arrow 180 degrees
	    if ($(this).hasClass("toggled-off"))
	      $(this).animate({"height": containerHeight}).removeClass("toggled-off").addClass("toggled-on"),
	      $('#SUP2 .rotate-img').css('-webkit-transform','none'),
	      $('#SUP2 .rotate-img').css('transform','none'),
	      $('#SUP2 .rotate-img').css('-ms-transform','none');
	    else
	      //close panel and return arrow to 0 degrees
	      $(this).animate({"height": "125px"}).removeClass("toggled-on").addClass("toggled-off"),
	      $('#SUP2 .rotate-img').css('-webkit-transform','rotate(180deg)'),
	      $('#SUP2 .rotate-img').css('transform','rotate(180deg)'),
	      $('#SUP2 .rotate-img').css('-ms-transform','rotate(180deg)');
	  });

	  //script to disable the hover/active state of the carousel buttons on click
	  $(document).on("click", ".slick-prev, .slick-next", function(){
	    $(this).blur();
	  });


});

function setTimer(timer,play){
	//alert("Hello I am here relaxing" +autoplay +timer);
	var pboolean = play =='true';
	var playspeed = parseInt(timer);
	$('.meow').slick({
	    autoplay: pboolean,
	    autoplaySpeed: playspeed,
	    arrows: true,
	    dots: false,
	    fade: true,
	    infinite: true,
	    slidesToShow: 1,
	    slidesToScroll: 1,
	  });
	
	
}

    
