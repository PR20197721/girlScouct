window.AccordionWidgetManager = (function(window, document, $){

	function toggleAccordionElement(){

		var accordionElement = $(this).data('accordion-element') || $(this).parent('.accordion');
		var headerElements = accordionElement.find('.accordionComponentHeader');
		var contentElements = accordionElement.find('.accordion-navigation .content');

		var show = !$(this).data('accordion-element-shown');

		// Get the target content elements.
		var contentElement = $(this).next('.accordion-navigation').children('.content');

		// Fix the classes.
		headerElements.add(contentElements).removeClass('on accordionHeaderShown').data('accordion-element-shown', false);
		if(show){
			$(this).add(contentElement).addClass('on accordionHeaderShown').data('accordion-element-shown', true);
		}

		// Toggle parsys if we have one after the animation is complete.
		headerElements.not($(this)).each(function(){
			var nonActiveToggleParsys = $(this).data('toggle-parsys');
			if(nonActiveToggleParsys && show){
				nonActiveToggleParsys.hideParsys();
			}
		});

		// Show the parsys after open.
		var activeToggleParsys = $(this).data('toggle-parsys');
		if(activeToggleParsys){
			if(show){
				activeToggleParsys.showParsys();
			}else{
				activeToggleParsys.hideParsys();
			}
		}

		// Hide other elements and show the selected.
		if(show){
			contentElements.not(contentElement).slideUp('slow');
			contentElement.slideDown('slow');


            //if the current window has scrolled below the top of the accordion, then selecting
            //an accordion element will cause the screen to scroll to the top of the accordion\
            //because it uses the $(html,body) target, this does not occur in the edit or preview modes in AEM
			if (scrolledUnder(headerElements.first())){
                $('html, body').animate( {
                    scrollTop: headerElements.first().offset().top - 50,
                }, {
                    duration: "slow",
                    queue: true
                });
            }
		}else{
			contentElements.slideUp('slow');
			if (scrolledUnder(headerElements.last())){
			    $('html, body').animate( {
                    scrollTop: headerElements.last().offset().top - 50,
                }, {
                    duration: "slow",
                    queue: false
                });
			}
		}


	}

	function scrolledUnder(elem)
    {
        var docViewTop = $(window).scrollTop();
        var elemTop = $(elem).offset().top;
        return (elemTop <= docViewTop);
    }

	function _initAccordionComponent(element){

		var headerElements = element.children('.accordionComponentHeader');

		var isEdit = false;
		try{
			isEdit = readCookie('wcmmode') == 'edit';
		}catch(err){}

		var toggleParsysExists = false;
		try{
			toggleParsysExists = !!TouchUI.Utils.ToggleParsys;
		}catch(er){}

		if(isEdit && toggleParsysExists){
			headerElements.each(function(){
				var parsysIdentifier = $(this).data('parsys-identifier');
				var toggleParsys;
				if($(this).data('toggle-parsys')){
					toggleParsys = $(this).data('toggle-parsys');
				}else{
					toggleParsys = TouchUI.Utils.ToggleParsys(parsysIdentifier);
					$(this).data('toggle-parsys', toggleParsys);
				}
				// Initialize hidden.
				toggleParsys.hideParsys();
			});
		}

		// Apply the header elements click functions.
		headerElements
			.data('accordion-element', $(element))
			.data('accordion-element-shown', false)
			.off('click.accordionHeader')
			.on('click.accordionHeader', toggleAccordionElement);

		// Open accordion with ID = hash.
		var currentHash = document.location.hash;
		if(currentHash && currentHash.length > 1){
			currentHash = currentHash.replace('#', '');
		}

		var thisHeader = headerElements.filter(function(){
			return currentHash && currentHash.length > 0 && $(this).attr('id') == currentHash;
		});

		if(thisHeader.length > 0){

			var parentHeaders = thisHeader
				.parents('.accordion-navigation')
				.prev();

			for(var i = parentHeaders.length - 1; i >= 0; i--){
				$(parentHeaders[i]).trigger('click.accordionHeader');
			}

			thisHeader.trigger('click.accordionHeader');
			window.setTimeout(function(){
				var targetHeader = $('#' + currentHash);
				targetHeader.next('.accordion-navigation').children('.content').promise().then(function(){
					$('html, body').animate({scrollTop: $('#' + currentHash).offset().top - 200});
				});
			}, 100);
		}

	}

	function init(){
		$('.accordionComponent').filter(function(){
			return !$(this).data('accordion-initialized');
		}).each(function(){
			AccordionWidgetManager.initAccordionComponent($(this).data('accordion-initialized', true));
		});
	}

	// Init everything that's already on the page.
	$(init);

	return {
		// Expose so this can be used for async accordions.
		initAccordionComponent : _initAccordionComponent
	}



})(window, document, $);

function vtk_accordion() {
    "use strict";
    if ($('.accordion').length) { //Check if there is any accordion in the page
        if ($('body').has('.vtk').length) { //check if the user is in VTK
            //vtk_accordion_main();
        } else {
            web_accordion_main();
        }
    }
}

function web_accordion_main() {
    "use strict";
    $(".accordion dt").on("click", function () {
        var oldPanel = {
                tab: $(this).parent().find("dt." + openClass), // Select siblings and all children, change to "> dt." to only select siblings
                action: "collapse"
            },
            newPanel = {
                tab: $(this),
                action: "expand"
            };

        if (oldPanel.tab.is(newPanel.tab)) {
            toggleTab(oldPanel); // Close current tab
        } else {
            toggleTab(oldPanel); // Close old tab
            toggleTab(newPanel); // Open new tab
        }

    });
}

/*
function vtk_accordion_main() {
    "use strict";
    toggleTab({
        tab: $(".accordion dt"),
        action: "expand"
    });
    $(".accordion dt").on("click", function (e) {
        e.stopPropagation();

        toggleTab({
            tab: $(this),
            action: $(this).hasClass(openClass) ? "collapse" : "expand"
        });

        return false;
    });
}*/

function toggleTab(panel) {
    "use strict";

    if (!panel.tab.length) {
        return;
    }

    var targetHeight,
        fixHeight;
    if (panel.action == "collapse") {
        targetHeight = function () {
            return 0;
        };
        fixHeight = 0;
    } else if (panel.action == "expand") {
        targetHeight = function () { // Calculate height after parsys is shown
            return this.body.children().outerHeight(true);
        };
        fixHeight = "auto";

    }

    // Set custom values or use defaults
    panel = {
        tab: panel.tab,
        action: panel.action,
        header: panel.header || panel.tab.find("> :first-child"),
        body: panel.body || panel.tab.next(),
        targetHeight: panel.targetHeight || targetHeight,
        fixHeight: panel.fixHeight || fixHeight,
        parsysID: panel.parsysID || panel.tab.attr("data-target")
    };

    // Necessary for authoring mode. See main.js:toggleParsys
    if (window[panel.parsysID] && window[panel.parsysID].toggle) {
        window[panel.parsysID].toggle();
        panel.body.find(".accordion dt").each(function () { // Hide child parsys
            window[this.getAttribute("data-target")].hideParsys();
        });
    }

    // Toggle classes and animate
    panel.tab.toggleClass(openClass);
    panel.header.toggleClass(openClass);

    panel.body.animate({
        "height": panel.targetHeight(),
    }, {
        duration: "slow", // 600ms
        queue: false,
        complete: function () { // Allow for responsive content height when expanded
            panel.body.css("height", panel.fixHeight);

        }
    });
}

//checks if the element that is being expanded/collapsed is above the view
function scrolledUnder(elem)
{
    var docViewTop = $(window).scrollTop();
    var elemTop = $(elem).offset().top;
    return (elemTop <= docViewTop);
}


