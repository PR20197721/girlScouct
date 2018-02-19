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

var SlideShowManager = (function(){
	
	var target = null;
	var currentSize = null;
	var editMode = false;
	
	var _elements = {
		small: [],
		medium: [],
		regular: []
	};
	
	function _addElementSet(newElements){
		if(!newElements){
			return;
		}
		for(var i = 0; i < newElements.length; i++){
			try{
				if(_elements[newElements[i].size] == null){
					_elements[newElements[i].size] = [];
				}
				_elements[newElements[i].size].push(newElements[i]);
			}catch(err){
				console.warn('Bad element size: "small", "medium", "normal" supported.', err);
			}
		}
	}
	
	function determineSize(){
		var windowWidthEms = $(window).width() / parseFloat($("body").css("font-size"));
		
		// Using same logic as Breakpoints in CSS
		if(windowWidthEms < 40.063){
			return 'small'
		}else if (windowWidthEms < 60.063){
			return 'medium';
		}else{
			return 'regular';
		}
	}
	
	function createDisplay(){

		var elementsToDisplay = _elements[currentSize];
		var output = $();
		
		// Create the individual display elements.
		for(var i = 0; i < elementsToDisplay.length; i++){
			var element = $('<div>').addClass('slide-show-element');
			
			// Append raw html (probably message).
			if(elementsToDisplay[i].text[0] != '/'){
				output.append(element.append(elementsToDisplay[i].text));
				continue;
			}
			
			var imageElement = $('<img>')
				.attr('src', elementsToDisplay[i].text)
				.attr('alt', elementsToDisplay[i].alt)
				.attr('width', elementsToDisplay[i].width + 'px');
			
			if(elementsToDisplay[i].linkUrl){
				var anchorTag = $('<a>').attr('href', elementsToDisplay[i].linkUrl);
				
				// Open non-relative links in new window.
				if(elementsToDisplay[i].forceNewWindow){
					anchorTag.attr('target', '_new');
				}
				element.append(anchorTag.append(imageElement));
			}else{
				element.append(imageElement);
			}
			
			output = output.add(element);
		}
		
		// Append everything to the page.
		target.append(output).slick({
		    autoplay: editMode,
		    autoplaySpeed: 6000,
		    arrows: true,
		    dots: false,
		    fade: true,
		    infinite: true,
		    slidesToShow: 1,
		    slidesToScroll: 1,
		    lazyLoad: 'ondemand'
		});
		
	}
	
	function _init(targetClass){
		
		// Setup the initial display.
		target = $('.' + targetClass);
		currentSize = determineSize();
		createDisplay();
		
		// Listen for window size changes and orientation changes to re-init.
		// Use interval rather than event to avoid running more often than needed. 
		window.setInterval(function(){
			var newSize = determineSize();
			if(newSize != currentSize){
				currentSize = newSize;
				$('<div>').addClass(targetClass).insertAfter(target);
				target.remove();
				target = $('.' + targetClass);
				createDisplay();
			}
		}, 500);
	}
	
	function _setEditMode(mode){
		editMode = mode;
	}
	
	return {
		addElementSet: _addElementSet,
		init: _init,
		setEditMode: _setEditMode
	};
	
})();
