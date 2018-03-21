
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

$.fn.lazyLoad = function () {
    var self = this,
        attr = {
            "src": "data-src",
            "href": "data-href"
        },
        src;

    for (src in attr) {
        if (attr.hasOwnProperty(src) && (!self.attr(src) || self.attr(src) === "") && self.attr(attr[src])) {
            // If the key is undefined or empty and the value exists, set the key to the value
            self.attr(src, self.attr(attr[src]));
        }
    }

    return self;
};

var SlideShowManager = (function(){
	

    // YouTube API loaded
    function ytLoaded() {
        return window['YT'] && YT.Player;
    }
    
    function vimeoLoaded(){
    		return window['Vimeo'] && Vimeo.Player;
    }

    if (ytLoaded()) {
        $(window).trigger("ytLoaded");
    } else {
        window.onYouTubeIframeAPIReady = function () {
            $(window).trigger("ytLoaded");
        };
    }

    if (vimeoLoaded()) {
        $(window).trigger("vimeoLoaded");
    } else {
    		// Vimeo doesn't provide a callback so we need to poll.
    		var vimeoIntervalId = window.setInterval(function(){
    			if(vimeoLoaded()){
    		        $(window).trigger("vimeoLoaded");
    		        window.clearInterval(vimeoIntervalId);
    			}
    		}, 100);
    }
    
	var mobile = true;

	// Copied From GSUSA - app.js
	SlickPlayer = {
	    init: function (params) {
	        var self = this; // Lexical closure

	        self.iframe = params.iframe;
	        self.slick = params.slick;
	        self.autoplay = params.autoplay;
	        self.type = self.iframe.attr('id').toLowerCase();
	        self.underbar = params.underbar;
	        self.placeholder = self.iframe.siblings(".vid-placeholder");
	        self.thumbnail = self.placeholder.find("img");

	        // Set config from component
	        self.config = { // Determine how player behaves
	            thumbnail: { // If there is a thumbnail, do not interact with the player until the user requests it
	                desktop: false,
	                mobile: false,
	                isActive: self.isActive
	            },
	            link: { // If the thumbnail opens the video in a new tab, there is no need to interact with the player
	                desktop: false,
	                mobile: false,
	                isActive: self.isActive
	            }
	        };
	        switch (params.config.desktop) {
	        case "thumbnail":
	            self.config.thumbnail.desktop = true;
	            self.config.link.desktop = false;
	            break;
	        case "link":
	            self.config.thumbnail.desktop = true;
	            self.config.link.desktop = true;
	            break;
	        }
	        switch (params.config.mobile) {
	        case "thumbnail":
	            self.config.thumbnail.mobile = true;
	            self.config.link.mobile = false;
	            break;
	        case "link":
	            self.config.thumbnail.mobile = true;
	            self.config.link.mobile = true;
	            break;
	        }

	        // Lazy load thumbnail and link if used
	        if (self.config.thumbnail.desktop || self.config.thumbnail.mobile) {
	            self.thumbnail.lazyLoad();
	            self.toggleThumbnail();
	            $(window).on("breakpoint", function () {
	                self.toggleThumbnail();
	            });
	        } else {
	            self.createPlayer(); // Load video right away if no thumbnail
	        }
	        if (self.config.link.desktop || self.config.link.mobile) {
	            self.placeholder.lazyLoad();
	        }

	        // Underbar events
	        self.underbar.input.on("focus", function () {
	            self.stopSlider();
	        }).on("focusout", function () {
	            self.startSlider();
	        });

	        // Placeholder events
	        self.placeholder.on("click", function (event) {
	            if (!self.config.link.isActive()) { // If using the thumbnail as a lazyload placeholder
	                event.preventDefault(); // Prevent anchor tag from opening link
	                event.stopPropagation();
	                self.play();
	            }
	        });
	    },
	    playing: false,
	    playVideo: function () {}, // Wrapper for API call
	    unloadVideo: function () {}, // Wrapper for API call
	    isActive: function () {
	        return (this.desktop && !mobile) || (this.mobile && mobile);
	    },
	    toggleThumbnail: function () {
	        if (this.config.thumbnail.isActive()) {
	            this.slick.addClass("thumbnail");
	        } else {
	            this.slick.removeClass("thumbnail");
	            this.createPlayer(); // Load video right away if no thumbnail
	        }
	    },
	    stopSlider: function () {
	        if (this.autoplay) {
	            this.slick.slickPause();
	        }
	        if (!this.underbar.isFocused() && !this.config.link.isActive()) {
	            this.underbar.hide();
	        }
	    },
	    startSlider: function () {
	        if (this.autoplay) {
	            this.slick.slickPlay();
	        }
	        if (!this.underbar.isFocused() && !this.config.link.isActive()) {
	            this.underbar.show();
	        }
	    },
	    createPlayer: function () {
	        var self = this;

	        if (self.iframe.attr("src")) { // Prevent player reinstantiation 
	            return;
	        }

	        // Assign embed url
	        self.iframe.lazyLoad();

	        // Load event
	        self.iframe.on("playerLoad", function () {
	            if (self.config.thumbnail.isActive()) { // If using thumbnail for lazy load, call play functions when thumbnail is clicked
	                self.play();
	            }
	        });

	        // Play event
	        self.iframe.on("play", function () {
	            if (!self.config.thumbnail.isActive()) { // If not using a thumbnail for lazy load, call play functions when video is played
	                self.play();
	            }
	        });

	        // Unload event
	        self.slick.on('beforeChange', function (event, slick, currentSlide, nextSlide) {
	            if (self.playing) {
	            		try{
	            			self.unload();
	            		}catch(er){}
	            }
	        });

	        // Instantiate player
	        if (self.type.indexOf('vimeo') > -1) { // Check for a Vimeo player
	        		if(vimeoLoaded()){
	    	            self.createVimeoPlayer();
	        		}else {
	        			$(window).on('vimeoLoaded', function(){
	        	            self.createVimeoPlayer();
	        			});
	        		}
	        } else if (self.type.indexOf('youtube') > -1) { // Check for a YouTube player
	            if (ytLoaded()) {
	                self.createYTPlayer();
	            } else {
	                $(window).on("ytLoaded", function () { // Wait until API script loads if it has not already
	                    self.createYTPlayer();
	                });
	            }
	        }
	    },
	    play: function () {
	        var self = this;

	        self.createPlayer(); // Load player if not already loaded
	        self.stopSlider();
	        if (!mobile) { // Browsers will block programmatic playback on mobile anyway, prevent Vimeo bug by manually prohibiting
	            self.playVideo();
	        }
	        self.slick.addClass("playing");
	        self.playing = true;
	    },
	    unload: function () {
	        var self = this;

	        // Don't start the auto slide back up if the user has already interacted with a video.
	        //self.startSlider();
	        
	        self.unloadVideo();
	        self.slick.removeClass("playing");
	        self.playing = false;
	    },
	    createVimeoPlayer: function () {
	        var self = this;
	        self.player = new Vimeo.Player(self.iframe.attr('id'));

	        self.player.on("loaded", function () {
	            // State functions
	            self.playVideo = function () {
	                self.player.play();
	            };
	            self.unloadVideo = function () {
	                self.player.unload();
	            };

	            // Listener events
	            self.player.on('play', function () {
	                self.iframe.trigger("play");
	            });

	            // Load
	            self.iframe.trigger("playerLoad");
	        });
	    },
	    createYTPlayer: function () {
	        var self = this;
	        self.player = new YT.Player(self.iframe.attr('id'));

	        self.player.addEventListener("onReady", function () {
	            // State functions
	            self.playVideo = function () {
	                self.player.playVideo();
	            };
	            self.unloadVideo = function () {
	                self.player.stopVideo();
	            };

	            // Listener events
	            self.player.addEventListener("onStateChange", function (event) {
	                if (event.data == YT.PlayerState.BUFFERING) { // Bind to buffering event to prevent delay before triggering play state
	                    self.iframe.trigger("play");
	                }
	            });

	            // Load
	            self.iframe.trigger("playerLoad");
	        });
	    }
	};

	// Copied From GSUSA - app.js
	var Underbar = {
	    init: function (el) {
	        this.el = el;
	        this.input = el.find('input');
	    },
	    show: function () {
	        if (this.el.length && !mobile) { // Desktop only
	            this.el.slideDown(1000);
	        }
	    },
	    hide: function () {
	        if (this.el.length && !mobile) {
	            this.el.slideUp(0);
	        }
	    },
	    isFocused: function () {
	        if (this.input.length) {
	            return this.input.is(":focus");
	        }
	    }

	};
	
	function readCookie(name) {
	    var nameEQ = name + "=";
	    var ca = document.cookie.split(';');
	    for(var i=0;i < ca.length;i++) {
	        var c = ca[i];
	        while (c.charAt(0)==' ') c = c.substring(1,c.length);
	        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
	    }
	    return null;
	}

	var target = null;
	var currentSize = null;
	var editMode = false;
	var elementsAdded = 0;
	var videoElements = [];
	
	var _elements = {
		small: [],
		medium: [],
		regular: []
	};
	
	function addElement(newElement, targetDiv){
		try{
			if(_elements[newElement.size] == null){
				_elements[newElement.size] = [];
			}
			newElement.targetDiv = targetDiv;
			_elements[newElement.size].push(newElement);
		}catch(err){
			console.warn('Bad element size: "small", "medium", "normal" supported.', err);
		}
	}
	
	function _addElementSet(newElements, targetDiv){
		if(!newElements){
			return;
		}
		for(var i = 0; i < newElements.length; i++){
			addElement(newElements[i], targetDiv);
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

	var vimeoPlayerLoadStarted = false;
	function loadVimeoPlayer(){
		if(!vimeoPlayerLoadStarted){
			$.getScript('https://player.vimeo.com/api/player.js', {cache: true});
			vimeoPlayerLoadStarted = true;
		}
	}
	
	var youtubePlayerLoadStarted = false;
	function loadYoutubePlayer(){
		if(!youtubePlayerLoadStarted){
			$.getScript('https://www.youtube.com/iframe_api', {cache: true});
			youtubePlayerLoadStarted = true;
		}
	}
	
	function createVideoElement(elementConfig){
		
		if(elementConfig.type == 'VIMEO'){
			loadVimeoPlayer();
		}else if(elementConfig.type == 'YOUTUBE'){
			loadYoutubePlayer();
		}

		var element = $('<div>').addClass('videoWrapper');
		
		var iframe = $('<iframe width="100%" frameborder="0" webkitallowfullscreen mozallowfullscreen allowfullscreen>')
				.attr('id', elementConfig.playerId)
				.addClass('vid-player')
				.attr('data-src', elementConfig.iFrameUrl);
		
		var link = $('<a class="vid-placeholder" target="_blank">')
				.attr('data-href', elementConfig.sourceUrl)
				.append($('<p>').text(elementConfig.title))
				.append($('<img>').attr('width', '100%').attr('data-src', elementConfig.thumbnail));
		
		var nonUnderbar = Object.create(Underbar);
		nonUnderbar.init($());
		
		videoElements.push({
			iframe: iframe,
			autoplay: true,
			underbar: nonUnderbar
		});
		
		element.append(iframe).append(link);
		
		return $('#' + elementConfig.targetDiv).empty().append($('<div>').append(element));
	}
	
	function createSlideElement(elementConfig){
		var element = $('<div>').addClass('slide-show-element');
		
		// Append raw html (probably message).
		if(elementConfig.text[0] != '/'){
			element.append(elementConfig.text)
			return $('#' + elementConfig.targetDiv).empty().append(element);
		}
		
		var imageElement = $('<img>')
			.attr('alt', elementConfig.alt)
			.attr('width', elementConfig.width + 'px');
		
		if(elementsAdded++ < 1){
			imageElement.attr('src', elementConfig.text);
		}else{
			imageElement.attr('data-lazy', elementConfig.text);
		}
		
		if(elementConfig.linkUrl){
			var anchorTag = $('<a>').attr('href', elementConfig.linkUrl);
			
			// Open non-relative links in new window.
			if(elementConfig.forceNewWindow){
				anchorTag.attr('target', '_new');
			}
			element.append(anchorTag.append(imageElement));
		}else{
			element.append(imageElement);
		}
		
		return $('#' + elementConfig.targetDiv).empty().append(element);
	}
	
	var fallBackPriority = {
		'small' : ['medium', 'regular'],
		'medium' : ['regular', 'small'],
		'regular' : ['medium', 'small']
	};
	
	function findBestElementsToDisplay(size){
		
		// Slice to duplicate list and not modify original.
		var returner = _elements[currentSize].slice();
		
		// traverse the fallbacks looking for the first viable option.
		var fallBackList = fallBackPriority[size];
		for(var i = 0; i < returner.length; i++){
			if(returner[i].text == 'MISSING'){
				for(var j = 0; j < fallBackList.length; j++){
					if(_elements[fallBackList[j]][i].text != 'MISSING'){
						returner[i] = _elements[fallBackList[j]][i];
						break;
					}
				}
				if(returner[i].text == 'MISSING'){
					returner = returner.splice(i, 1);
				}
			}
		}
		
		return returner;
	}
	
	function createDisplay(reinit){

		var elementsToDisplay = findBestElementsToDisplay(currentSize);
		target.removeClass('slick-initialized slick-slider').empty();
		
		for(var i = 0; i < elementsToDisplay.length; i++){
			// Create the individual display elements before adding to page.
			var newElement;
			if(elementsToDisplay[i].playerId){
				newElement = createVideoElement(elementsToDisplay[i]).children();
			}else{
				newElement = createSlideElement(elementsToDisplay[i]).children();
			}
			
			
			if(editMode){				
				// Give the author something to select.
				$('<div>')
					.css({
						'background-color': '#c7c7c7', 
						'font-size': '18px', 
						'color': 'green', 
						'text-align': 'center',
						'padding' : '5px',
						'border': '1px solid grey'
					})
					.text('Slide Show Element #' + (i + 1))
					.insertBefore(newElement);
				
				target.append(newElement);
			}else{
				target.append(newElement);
			}
		}
		
		if(reinit){
			target.slickPause();
		}
		
		// Initialize Slick Slider.
		createSlick(target, reinit);
	}
		
	function createSlick(target){
		target.slick({
		    autoplay: !editMode,
		    autoplaySpeed: 6000,
		    arrows: true,
		    dots: false,
		    fade: true,
		    infinite: true,
		    slidesToShow: 1,
		    slidesToScroll: 1,
		    lazyLoad: 'ondemand',
		    
		    onBeforeChange: function(){
		    		if(this == target[0].slick){
		    			target.trigger('beforeChange');
		    		}
		    }
		});
		
		// Start up the slick players.
		for(var i = 0; i < videoElements.length; i++){
			Object.create(SlickPlayer).init({
                iframe: videoElements[i].iframe,
                slick: target,
                autoplay: videoElements[i].autoplay,
                underbar: videoElements[i].underbar,
                //config: {desktop: 'thumbnail', mobile: 'thumbnail'}
                config: {desktop: 'default', mobile: 'default'}
            });
		}
	}
	
	function _removeAll(){
		_elements = [];
	}
	function reset(){
		elementsAdded = 0;
		videoElements = [];
		createDisplay(true);
	}
	
	function _init(targetClass, setupInEditMode){
		
		// Setup the initial display.
		editMode = !!setupInEditMode;
		target = $('.' + targetClass);
		currentSize = determineSize();
		createDisplay();
		
		// Listen for window size changes and orientation changes to re-init.
		// Listen for edit vs preview mode changing to re-init.
		// Use interval rather than event to avoid running more often than needed. 
		window.setInterval(function(){
			var newSize = determineSize();
			if(newSize != currentSize){
				currentSize = newSize;
				reset();
			}
			
			// Check if the edit / preview cookie has changed.
			var wcmCookie = readCookie('wcmmode');
			if(!!editMode != (wcmCookie == 'edit')){
				editMode = wcmCookie == 'edit';
				reset();
			}
			
		}, 500);
	}
	
	return {
		addElementSet: _addElementSet,
		init: _init,
		removeAll: _removeAll
	};
	
})();
