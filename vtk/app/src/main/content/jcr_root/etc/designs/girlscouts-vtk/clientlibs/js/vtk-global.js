var $ = jQuery.noConflict();
//all function calls should go here
(function($) {
	function showError(msg, msgId) {
		var targetNode = "#error_msg";
		if (msgId) {
			targetNode = msgId;
		}
		if (msg) {
			$(targetNode).html(msg);
			$(targetNode).show();
		} else {
			// clear
			$(targetNode).html("");
			$(targetNode).hide();
		}
	}

	// function modal_height() {
	//  	var window_h = $(window).height();
	//  	// var popup_top = $(window_h -$('.reveal-modal').height()/2);
	//  	var popup_h = (window_h - 50);
	// 	$('.reveal-modal').css('top', 0);
	// 	$('.reveal-modal').css('height' , window_h + 'px');
	// 	$('.scroll').css('max-height' , popup_h +' px');
	// }
    /* In Koo removed this because it is duplicated in /etc/designs/girlscouts/clientlibs/js/footer.js
      function vtk_accordion() {
        if ($(".accordion").length > 0) {
            $('.accordion dt > :first-child').on('click', function(e) {
            e.stopPropagation();
              var target = $(this).parent().data('target');
              var toggle = $(this);
              $('#' + target).slideToggle('slow');
              $(toggle).toggleClass('on');
              return false;
            });
          }
      }
    */

	function modal_height_on_open() {
		$(document).on('opened.fndtn.reveal', '[data-reveal]', function() {
			var window_h = $(window).height();
			var popup_h = (window_h - 75);
			$(this).find('.scroll').css('max-height', popup_h + 'px');
			var browser = navigator.userAgent.match(/(msie\ [0-9]{1})/i);
			if (browser != null && browser[0].split(" ")[1] == 9) {
				// alert(navigator.userAgent.match(/msie/i));
				placeholder_IE9();
				$('select').css('background-image', 'none');
			}

			//adding a heights to popups with two scrollable content.
			$('.scroll_2').css('max-height', popup_h - $('.scroll_1').height() + 'px');

		});
	}

	function modal_height_resize() {
		var window_h = $(window).height();
		var popup_h = (window_h - 75);
		$('.scroll').css('max-height', popup_h + 'px');
		$('.modalWrap').css('max-height', $(window).height() + 'px');
		$('.ui-dialog').css('left', $(window).width() < 920 ? 0 : ($(window).width() - 920) / 2)
		//adding a heights to popups with two scrollable content.
		$('.scroll_2').css('max-height', ($(window).height() - 75) - $('.scroll_1').height() + 'px');
	}

	function validate_image() {
		$('form#frmImg').submit(function(e) {
			var $this = $(this);
			var $input = $this.find('input[type="file"]').val();
			if ($input == '') {
				alert("you must choose a image");

				e.preventDefault();
				return false;
			}
		});
	}

	//used if using select instead of tabs for small, screens was removed.
	function select_tabs() {
		$("select.tabs").on('change', function(index) {
			if ($(this).index() !== 0) {
				window.location.href = $(this).val();
			}
		});
		var path = location.pathname;
		$('select.tabs option').each(function() {
			if ($(this).val() == path) {
				$(this).prop("selected", true);
			}
		});
	}

	function add_placeholdersIE9() {
		function add() {
			if ($(this).val() === '') {
				$(this).val($(this).attr('placeholder')).addClass('placeholder');
			}
		}

		function remove() {
			if ($(this).val() === $(this).attr('placeholder')) {
				$(this).val('').removeClass('placeholder');
			}
		}

		// Create a dummy element for feature detection
		if (!('placeholder' in $('<input>')[0])) {

			// Select the elements that have a placeholder attribute
			$('input[placeholder], textarea[placeholder]').blur(add).focus(remove).each(add);

			// Remove the placeholder text before the form is submitted
			$('form').submit(function() {
				$(this).find('input[placeholder], textarea[placeholder]').each(remove);
			});
		}
	}

	//	  $(document).ready(function() {
	$(document).foundation({
		reveal: {
			animation: 'fade',
			root_element: 'window',
			close_on_background_click: false,
			opened: function() {
				var window_h = $(window).height();
				var popup_h = (window_h - 75);
				$('#modal_popup').find('.scroll').css('max-height', popup_h + 'px');
				var scroll = $('#modal_popup').find('.scroll');
				scroll.html(checkMeetingAidsRefs(scroll.html()));
			},
			open: function() {
				$('body').css({ 'overflow': 'hidden' });
				if (navigator.userAgent.match(/msie/i)) {
					// alert(navigator.userAgent.match(/msie/i));
					add_placeholdersIE9();
				}
			},
			close: function() {
				$('body').css({ 'overflow': 'inherit' })
			},
		}
	});
	modal_height_on_open();
	validate_image();
	// resizeWindow();
	if ($('.tabs dd').length == 6) {
		$('.tabs dd').css('width', '100%');
	}
	if ($('.tabs dd').length == 5) {
		$('.tabs dd').css('min-width', '20%');
	}
	//  });

	$(window).resize(function() {
		modal_height_resize();
	});

})($);

/**
 * VTK Data Worker
 *
 * Fetch VTK data first and then call the callback
 * if the returned status code is not 304.
 */

var VTKDataWorker;
(function() {
	var BASE_PATH = '/vtk-data';

	function _getTroopDataToken() {
		// Ref: https://developer.mozilla.org/en-US/docs/Web/API/Document/cookie
		// Get cookie: troopDataToken
		var hash = document.cookie.replace(/(?:(?:^|.*;\s*)troopDataToken\s*\=\s*([^;]*).*$)|^.*$/, "$1");
		return hash;
	}

	function _checkShouldSkipFirst() {
		// Get cookie: troopDataToken
		var readonly = document.cookie.replace(/(?:(?:^|.*;\s*)VTKReadonlyMode\s*\=\s*([^;]*).*$)|^.*$/, "$1");
		return !(readonly == 'true');
	}

	function _VTKDataWorker(path, that, success, interval) {
		this.path = path;
		this.that = that;
		this.successResponse = success;
		this.interval = interval;
		this.url = BASE_PATH + '/' + _getTroopDataToken() + '/' + path;
		this.shouldSkipFirst = _checkShouldSkipFirst();
		this.eTag = null;
	}

	_VTKDataWorker.prototype.getData = function(shouldSkip) {
		var url = this.url;
		if (shouldSkip) {
			url += "?_=" + (new Date()).getTime();
			if (this.intervalId) {
				clearInterval(this.intervalId);
				this.setInterval();
			}
		}

		if (typeof VTKDataWorkerShouldSkipNextPoll !== 'undefined' && VTKDataWorkerShouldSkipNextPoll) {
			VTKDataWorkerShouldSkipNextPoll = false;
			return;
		}


		$.ajax({
			url: url,
			dataType: 'json',
			success: function(data, textStatus, jqXHR) {
				var eTag = jqXHR.getResponseHeader("ETag");
				if (eTag) {
					this.eTag = eTag;
				}
				// Only call the callback if the status code is 200.
				if (this.successResponse && jqXHR.status == 200) {
					this.successResponse(data)
				}
			}.bind(this),
			beforeSend: function(request) {
				if (this.eTag != null) {
					request.setRequestHeader('If-None-Match', this.eTag);
				}
			}.bind(this)
		});
	};

	_VTKDataWorker.prototype.setInterval = function() {
		this.intervalId = setInterval(this.getData.bind(this), this.interval);
	}

	_VTKDataWorker.prototype.start = function() {
		this.getData(true);
		this.setInterval();
	};

	// Expose the function to global namespace
	VTKDataWorker = _VTKDataWorker;
})();


/*
	CALL ABND APPEND THE  DOME WITH HTML ELEMENTS AND TRIGGER THE SLIDER
*/

function callExecuteBannerSlider(tabNavLoaded) {

	tabNavLoaded = tabNavLoaded || new $.Deferred().resolve();

	async function loadSlick() {
		// https://stackoverflow.com/questions/11071314/javascript-execute-after-all-images-have-loaded/60949881#60949881
		// Initialize slick after banner images have loaded
		// Use overflow hidden to gracefully hide loading images
		$('.vtk-banner-container').parent().attr('style', 'height:0; overflow:hidden;');
		Promise.all(Array.from(document.querySelectorAll('.vtk-banner-image img'))
			.filter(img => !img.complete)
			.map(img => new Promise(resolve => { img.onload = img.onerror = resolve; })))
			.then(() => {
				//VTK BANNER SLIDER SETTING
				$('.vtk-banner-container').slick({
					slidesToScroll: 1,
					adaptiveHeight: true,
					autoplaySpeed: 10000,
					autoplay: true
				}).parent().removeAttr('style');
			});
	}

	function processVtkBannerResponse(result) {

		if ($("#vtk_banner2234").data('cached') === 'no') {
			$("#vtk_banner2234").show();
		}

		var htmlResults = $(result[0]);
		var vtkBannerSections = htmlResults.find('.vtk-banner.section');

		//REMOVE BANNERS THAT ARE DISABLED.
		vtkBannerSections.each(function(x, y) {
			if ($(y).find('.vtk-banner-disabled').length) {
				$(this).remove();
			}
		});

		//APPEND TO THE  BANNER
		$("#vtk_banner2234").append(htmlResults);

		//CLOSE BANNER
		$('.vtk-banner-button').click(function() {
			$.ajax({
				url: '/content/girlscouts-vtk/controllers/vtk.controller.html?act=hideVtkBanner',
				dataType: 'html',
			}).done(function() {
				$(".slick-next").css('display', 'none');
				$(".slick-prev").css('display', 'none');
               	$('.vtk-banner-container').slideUp();
                $("#vtk_banner2234").css('margin-top', 0);
			})
		});

		loadSlick();
	}

	var bannerLoaded = $.ajax({
		url: '/content/vtkcontent/en/vtk-banner.simple.html',
		type: 'GET',
		dataType: 'html',
		data: {
			a: Date.now()
		}
	});

	$.when(bannerLoaded, tabNavLoaded).then(processVtkBannerResponse);

}

function callFoundationModal(e, id) {
	$(document).foundation();
	e.preventDefault();
	console.log('#' + id)

	// $(e.target).trigger('click')

	$('#' + id).foundation('reveal', 'open');
}


function setHeigthPropertiesToBanner(p) {

	var image = $(p).find('.banner-image');
	var scroll = $(p).find('.scroll-banner');
	var height = $(window).height();
	var modalwidth = $(p).innerWidth();
	var imageHeight = image.height();
	scroll.css(
		{
			'maxHeight': $(window).height() - imageHeight - 75 + 'px',
			'overflow-y': 'auto'
		}
	);
}


$(function() {

	var TIME_FOR_LOG_OUT_IN_MINUTES = 10;

	var signOutModal = new ModalVtk('Signout', true);

	signOutModal.init();

	function singOut(timeinminutes, _this) {

		var timeoutId = undefined, secondTimeOutId = undefined, timeinmillisecond = timeinminutes * 60 * 1000;

		function startTimeout() {
			timeoutId = setTimeout(goInactive, timeinmillisecond);
		}

		function init() {

			window.addEventListener("mousedown", resetTimeout, false);
			window.addEventListener("keypress", resetTimeout, false);
			window.addEventListener("DOMMouseScroll", resetTimeout, false);
			window.addEventListener("mousewheel", resetTimeout, false);
			startTimeout();
		}

		function goInactive() {
			clearTimeout(timeoutId);
			window.removeEventListener("mousedown", resetTimeout, false);
			window.removeEventListener("keypress", resetTimeout, false);
			window.removeEventListener("DOMMouseScroll", resetTimeout, false);
			window.removeEventListener("mousewheel", resetTimeout, false);
			secondTimeOutId = setTimeout(onCB, 30000 /* in millisecons */);
			signOutModal.confirm('LOGGING OUT', '<p>You are about to be logged out due to inactivity. Click "Keep Working" to stay logged in.</p>', onCB, cCBack);
		}

		function onCB() {
			girlscouts.components.login.signOut();
		}

		function cCBack() {

			clearTimeout(secondTimeOutId);
			init();
			signOutModal.close();
		}

		function resetTimeout() {
			clearTimeout(timeoutId);
			startTimeout();
		}

		init();
	}

	if (!~document.cookie.indexOf('wcmmode')) { //Return Boolean
		singOut(TIME_FOR_LOG_OUT_IN_MINUTES, this);
	}

})
$(window).resize(function() {
	if ($("#mobileView").css('display') != 'none') {
		$("#breakingNews").css('margin-left', '0');
		$("#breakingNews").css('width', '100%');
	}
	if ($("#mobileView").css('display') == 'none') {
		$("#breakingNews").css('margin-left', '45px');
		$("#breakingNews").css('width', '90.6%');
	}

});

function exploreReset() {
	var notice = $("#exploreModal");
	notice.css('display', 'block');
	$("#exploreModalClose").click(function() {
		notice.css('display', 'none');
	});
	$(document).click(function() {
		notice.css('display', 'none');
	});
	$(".maintenance-content").click(function(event) {
		event.stopPropagation();
	});
}

function exploreResetClose() {
	$("#exploreModal").css('display', 'none');
}

function exploreResetConfirm() {
	$("#vtk-loading").css("display", "block");
	$.ajax({
		url: '/content/girlscouts-vtk/service/reset-year-plan-servlet.html',
		type: 'POST',
		success: function(result) {
			var elem = $("#reloginid").val();
			$.ajax({
				url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand=' + Date.now(),
				type: 'POST',
				data: {
					act: 'ReLogin',
					loginAs: elem,
					a: Date.now()
				},
				success: function(result) {
					$("#vtk-loading").css("display", "none");
					vtkTrackerPushAction('ChangeTroop');
					document.location = "/content/girlscouts-vtk/en/vtk.html";
				}
			});
		},
		error: function(result) {
			$("#vtk-loading").css("display", "none");
			console.log("YEAR PLAN FAILED TO DELETE");
		}
	});
}

function checkNews() {
	if ($(".breaking-news").is(":visible")) {
		//Breaking News Maintenance Close:
		$('.breaking-news .vtk-breaking-news-button').click(function() {
			$.ajax({
				url: '/content/girlscouts-vtk/controllers/vtk.controller.html?act=hideVtkMaintenance',
				dataType: 'html',
			}).done(function() {
				$('#vtkBreakingNews').slideUp();
			})
		});
	} else {
		setTimeout(checkNews, 50);
	}

}

function addNewTags() {
	//"NEW" tag list
	var newMeetings = ["Outdoor", "Badges_for_2020-2021", "STEM", "Journey|Outdoor", "Journey|STEM", "Badges_Petals|Badges_for_2020-2021", "Life_Skills"];
	for (var tag in newMeetings) {
		var el = document.getElementById("category" + newMeetings[tag]);
		$(el).parent().find("p").append("<span style='font-size:10px;color:#F9A61A;font-weight:bold;background:none;display:inline-block; padding-top: 8px;'>NEW</span>")


	}


}

function appendMeetingCategories(result, level, group) {
	$("#vtk-meeting-group-categories").html("");
	$("#vtk-meeting-category-parent").find(".vtk-meeting-filter_title").html("")
	var categoryGroups = [];
	for (var category in result[level].subFilterOptions[group].subFilterOptions) {
		if (!categoryGroups.includes((result[level].subFilterOptions[group].subFilterOptions[category].value).replace(new RegExp('_', 'g'), " "))) {
			var categoryName = result[level].subFilterOptions[group].subFilterOptions[category].value;
			categoryGroups.push(categoryName.replace(new RegExp('_', 'g'), " "));
		}
	}
	if (categoryGroups.length) {
		$("#vtk-meeting-category-parent").find(".vtk-meeting-filter_title").html("<span>3.</span><span id=\"cat_selected\" style=\"font-size:14px !important;\">Select your categories</span>");
	}
	categoryGroups.sort();
	for (var cat in categoryGroups) {
		var categoryTag = categoryGroups[cat].replace(new RegExp(' ', 'g'), "_");
		$("#vtk-meeting-group-categories").append("<div class='small-24 medium-12 large-6 column selection-box  ' style='min-height: 70px;float: left;'><input type='checkbox' name='_tag_c' id=\"category" + categoryTag + "\" value=\"" + categoryTag + "\"><label for=\"category" + categoryTag + "\"><span></span><p>" + categoryGroups[cat] + "</p></label></div>")
	}
	addNewTags();
}

function appendMeetingGroups(result, level) {
	$("#vtk-group-section").html("");
	var meetingGroups = [];
	for (var meetingGroup in result[level].subFilterOptions) {
		if (!meetingGroups.includes((result[level].subFilterOptions[meetingGroup].value).replace(new RegExp('_', 'g'), " "))) {
			var groupName = result[level].subFilterOptions[meetingGroup].value;
			meetingGroups.push(groupName.replace(new RegExp('_', 'g'), " "));
		}
	}

	meetingGroups.sort(function(a, b) {
		var subFilters = ["Intro/Family Meeting", "Badges Petals", "Journey", "Award Earning", "Closing/Bridging"];
		var aValue, bValue = subFilters.length;
		for (var i = 0; i < subFilters.length; i++) {
			if (a === subFilters[i]) {
				aValue = i;
			}
			if (b === subFilters[i]) {
				bValue = i;
			}
		}
		if (aValue < bValue) {
			return -1;
		}
		if (aValue > bValue) {
			return 1;
		}
		if ((a === "Journey: Daisies - Juniors" && b === "Journey: Cadettes - Ambassadors") || (b === "Journey: Daisies - Juniors" && a === "Journey: Cadettes - Ambassadors")) {
			return -1;
		} else {
			if (a > b) {
				return 1;
			} else if (a < b) {
				return -1;
			} else {
				return 0;
			}
		}
	});
	for (var meeting in meetingGroups) {
		$("#vtk-group-section").append("<div class='small-24 medium-6 column selection-box ' style='min-height: 60px;float: left;'><input type='radio' name='_tag_t' id='group" + meetingGroups[meeting] + "' value='" + meetingGroups[meeting] + "'><label for='group" + meetingGroups[meeting] + "'><span></span><p> " + meetingGroups[meeting] + " </p></label></div>");
	}
	$("#vtk-group-section .selection-box input").on("click", function() {
		appendMeetingCategories(result, level, $(this).attr("value").replace(new RegExp(' ', 'g'), "_"))
		if ($("#vtk-meeting-category-parent").css("display") === "none") {
			$("#vtk-meeting-category-parent").slideDown();
		}
	});

}

function getMeetingResponse() {
	$("#vtk-loading").css("display", "block");
	$.ajax({
		url: '/bin/vtk/v1/meetingFilter',
		type: 'GET',
		success: function(result) {
			$("#vtk-loading").css("display", "none");
			$(".gradeLevelSelect").on("click", function() {
				if ($("#vtk-meeting-category-parent").css("display") !== "none") {
					$("#vtk-meeting-category-parent").slideUp(400, function() {
						$("#vtk-meeting-group-categories").html("");
					});
				}
				var level = $(this).attr("value").replace("_", "");
				appendMeetingGroups(result, level);
				if ($("#vtk-meeting-group-type").css("display") === "none") {
					$("#vtk-meeting-group-type").slideDown();
				}
			});

			$("#showHideReveal").toggleClass('open');
			$('.vtk-meeting-group').slideToggle();
			$('.vtk-dropdown_options').hide();
			$('#vtk-meeting-report').hide();
		}
	});

}
function syncCheckboxesAndAddToYearPlan(input){
    var checked = input.checked;
    var id = $(input).attr("id");
    var mid = $(input).data("mtg-id");
    if(id.startsWith('header-add-')){
        $('#slider-add-'+mid).prop("checked", checked);
    }else{
        $('#header-add-'+mid).prop("checked", checked);
    }
    addToYearPlan()
}
function addToYearPlan() {
    console.log("selected to add to year");
	var enablebuttom = $('.meeting-item:visible input[name="addMeetingMulti"]').toArray().some(function(i, e, a) {
			return i.checked;
	});
	if (enablebuttom) {
		$('.clear-meeting-filter-result').removeClass('inactive-button');
		$('.add-to-year-plan').removeClass('inactive-button');
	} else {
		$('.clear-meeting-filter-result').addClass('inactive-button');
		$('.add-to-year-plan').addClass('inactive-button');
	}
}

function generateMeetingHtml(data, meeting) {
	$("#meetingSelect").append("<div class='meeting-item column small-24' data-url='" + data[meeting].path + "' data-meetingid='" + data[meeting].id + "'></div>");
	$("[data-meetingid=" + data[meeting].id + "]").append("<div class='row'><div>");
	$("[data-meetingid=" + data[meeting].id + "]").find(".row").append("<div class='column small-24 medium-14'><div style='display:table;min-height:110px'><div style='display:table-cell;height:inherit;vertical-align:middle;'><p class='title'>" + data[meeting].name + "</p><p class='blurb'>" + data[meeting].blurb + "</p><p class='tags'> <span></span></p></div></div></div>");


	if (data[meeting].included) {
		$("[data-meetingid=" + data[meeting].id + "]").find(".row").append("<div class='column small-24 medium-6'><div style='display:table;min-height:110px; width: inherit;'><div style=\"display:table-cell;height:inherit;vertical-align:middle; text-align:center;\"><img src=\"/etc/designs/girlscouts-vtk/clientlibs/css/images/check.png\" width='10' height='15'> <i class=\"included\">Included in Year Plan</i></div></div></div>");
	} else {
		if (window.location.href.includes(".details")) {
			$("[data-meetingid=" + data[meeting].id + "]").find(".row").append("<div class='column small-24 medium-6'><div style='display:table;min-height:110px; width: inherit;'><div style='display:table-cell;height:inherit;vertical-align:middle; text-align:center;'><div class='middle-checkbox' style='text-align:center;'><table><tbody><tr><td><span></span></label></td><td><p style='cursor:pointer;'class=\"select-meeting-withaction\" onclick=\"cngMeeting('" + data[meeting].path + "')\">SELECT MEETING</p></td></tr></tbody></table></div></div></div></div>");
		} else {
			$("[data-meetingid=" + data[meeting].id + "]").find(".row").append("<div class='column small-24 medium-6'><div style='display:table;min-height:110px; width: inherit;'><div style='display:table-cell;height:inherit;vertical-align:middle; text-align:center;'><div class='middle-checkbox' style='text-align:center;'><table><tbody><tr><td><input onclick='addToYearPlan();' type='checkbox' name='addMeetingMulti' id=" + data[meeting].id + " value=" + data[meeting].path + "><label for=" + data[meeting].id + "><span></span></label></td><td><p style='color:#000;'>SELECT MEETING</p></td></tr></tbody></table></div></div></div></div>");
		}
	}


	if (!(data[meeting].req !== null && data[meeting].req !== undefined && data[meeting].req !== "")) {
		$("[data-meetingid=" + data[meeting].id + "]").find(".row").append("<div class='column small-24 medium-4'><div style='min-height:110px; width:100%'><div style='height:inherit;vertical-align:middle; text-align:center;width:100%'><img width='100' class='image  _no_requirement_modal' height='100' src='/content/girlscouts-vtk/service/meeting/icon." + data[meeting].id + ".png'></div></div></div>");
	} else {
		$("[data-meetingid=" + data[meeting].id + "]").find(".row").append("<div class='column small-24 medium-4'><div style='min-height:110px; width:100%'><div style='height:inherit;vertical-align:middle; text-align:center;width:100%'><img width='100' onclick='openRequirementDetail(this)' class='image  _requirement_modal' height='100' src='/content/girlscouts-vtk/service/meeting/icon." + data[meeting].id + ".png'></div></div></div>");
		$("[data-meetingid=" + data[meeting].id + "]").append("<div class='__requiments_details row' style='display:none'><div class='column small-24' style='padding:10px;'><div class='_requiments_description'><p style='margin-bottom: 5px'><b>" + data[meeting].reqTitle + "</b></p>" + data[meeting].req + "</div><p style='text-align:center; margin-top:20px'><span class='vtk-button' style='cursor:pointer;' onclick='_closeME(this)'>&nbsp;&nbsp;&nbsp;CLOSE&nbsp;&nbsp;&nbsp;</span></p></div></div>");
	}
	if (data[meeting].hasGlobal === true) {
		$("[data-meetingid=" + data[meeting].id + "]").find(".title").append("<img data-tooltip='' aria-haspopup='true' class='has-tip tip-top radius meeting_library' style='width:30px;vertical-align:top;padding-top:2px;cursor:auto;border:none' src='/etc/designs/girlscouts-vtk/clientlibs/css/images/globe_selected.png' data-selector='tooltip-jyhe4u6u1' title=''>");
	}
	if (data[meeting].hasOutdoor === true) {
		$("[data-meetingid=" + data[meeting].id + "]").find(".title").append("<img data-tooltip='' aria-haspopup='true' class='has-tip tip-top radius meeting_library' style='width:30px;vertical-align:bottom;cursor:auto;border:none' src='/etc/designs/girlscouts-vtk/clientlibs/css/images/outdoor.png' data-selector='tooltip-jyhelib9j' title=''>");
	}
	if (data[meeting].hasVirtual === true) {
		$("[data-meetingid=" + data[meeting].id + "]").find(".title").append("<img data-tooltip='' aria-haspopup='true' class='has-tip tip-top radius meeting_library' style='width:30px;vertical-align:bottom;cursor:auto;border:none' src='/etc/designs/girlscouts-vtk/clientlibs/css/images/virtual_selected.png' data-selector='tooltip-jyhelib9j' title=''>");
	}

}

function appendMeetings(data) {
	var ambassadorMeetings = [];
	var brownieMeetings = [];
	var daisyMeetings = [];
	var cadetteMeetings = [];
	var juniorMeetings = [];
	var seniorMeetings = [];
	var multiMeetings = [];
	$("#no-of-meeting").find("p").text(data.length + " Meeting Plans");
	$("#no-of-meeting").css("display", "block");

	for (var meeting in data) {
		var meetingLevel = data[meeting].level;
		if (meetingLevel === "Ambassador") {
			ambassadorMeetings.push(data[meeting]);
		} else if (meetingLevel === "Brownie") {
			brownieMeetings.push(data[meeting]);
		} else if (meetingLevel === "Cadette") {
			cadetteMeetings.push(data[meeting]);
		} else if (meetingLevel === "Daisy") {
			daisyMeetings.push(data[meeting]);
		} else if (meetingLevel === "Junior") {
			juniorMeetings.push(data[meeting]);
		} else if (meetingLevel === "Senior") {
			seniorMeetings.push(data[meeting]);
		} else if (meetingLevel === "Multi-level") {
			multiMeetings.push(data[meeting]);
		}
	}
	if (ambassadorMeetings.length) {
		$("#meetingSelect").append("<div class='meeting-age-separator column small-24 levelNav_Ambassador' id='levelNav_Ambassador'>Ambassador</div>");
		for (var meeting in ambassadorMeetings) {
			generateMeetingHtml(ambassadorMeetings, meeting);
		}
	}
	if (brownieMeetings.length) {
		$("#meetingSelect").append("<div class='meeting-age-separator column small-24 levelNav_Brownie' id='levelNav_Brownie'>Brownie</div>");
		for (var meeting in brownieMeetings) {
			generateMeetingHtml(brownieMeetings, meeting);
		}
	}
	if (cadetteMeetings.length) {
		$("#meetingSelect").append("<div class='meeting-age-separator column small-24 levelNav_Cadette' id='levelNav_Cadette'>Cadette</div>");
		for (var meeting in cadetteMeetings) {
			generateMeetingHtml(cadetteMeetings, meeting);
		}
	}
	if (daisyMeetings.length) {
		$("#meetingSelect").append("<div class='meeting-age-separator column small-24 levelNav_Daisy' id='levelNav_Daisy'>Daisy</div>");
		for (var meeting in daisyMeetings) {
			generateMeetingHtml(daisyMeetings, meeting);
		}
	}
	if (juniorMeetings.length) {
		$("#meetingSelect").append("<div class='meeting-age-separator column small-24 levelNav_Junior' id='levelNav_Junior'>Junior</div>");
		for (var meeting in juniorMeetings) {
			generateMeetingHtml(juniorMeetings, meeting);
		}
	}
	if (multiMeetings.length) {
		$("#meetingSelect").append("<div class='meeting-age-separator column small-24 levelNav_Multi_level' id='levelNav_Multi_level'>Multi-Level</div>");
		for (var meeting in multiMeetings) {
			generateMeetingHtml(multiMeetings, meeting);
		}
	}
	if (seniorMeetings.length) {
		$("#meetingSelect").append("<div class='meeting-age-separator column small-24 levelNav_Senior' id='levelNav_Senior'>Senior</div>");
		for (var meeting in seniorMeetings) {
			generateMeetingHtml(seniorMeetings, meeting);
		}
	}
	$("#meetingSelect").addClass("showResults");

}

function callToServer() {
	$('#meeting-library-no-content').hide();
	$('.meeting-library #vtk-meeting-filter').fadeOut();
	$('.meeting-library .list-of-buttons').fadeOut();
	$('.meeting-library .loading-meeting').show();
	$('.vtk-body .ui-dialog.modalWrap .scroll').css('overflow', 'none');
	$('#meetingSelect').slideUp();
	var call = $.ajax({
		url: "/bin/vtk/v1/meetingSearch",
		dataType: "json",
		type: 'POST',
		contentType: "application/json",
		cache: false,
		data: JSON.stringify(params.get())
	})
	call.done(function(data) {
		$("#meetingSelect").children().each(function() {
			if ($(this).hasClass("meeting-age-separator") || $(this).hasClass("meeting-item")) {
				$(this).remove();
			}
		});
		appendMeetings(data);
		$('.meeting-library .loading-meeting').hide()
		$('.vtk-body .ui-dialog.modalWrap .scroll').css('overflow', 'auto');
		$('.meeting-library #vtk-meeting-filter').fadeIn();
		$('.meeting-library .list-of-buttons').fadeIn();

	});

	call.fail(function(err) {
		console.error('error:', err);
		$('.meeting-library .loading-meeting').hide()
		$('.vtk-body .ui-dialog.modalWrap .scroll').css('overflow', 'auto');
		$('.meeting-library #vtk-meeting-filter').fadeIn();
		$('.meeting-library .list-of-buttons').fadeIn();
		$("#meetingSelect").css("display", "block");
	})
}
function generatePreviewMeetingResults() {
	$('#meeting-library-no-content').hide();
	$('.meeting-library #vtk-meeting-filter').fadeOut();
	$('.meeting-library .list-of-buttons').fadeOut();
	$('.meeting-library .loading-meeting').show();
	$('.vtk-body .ui-dialog.modalWrap .scroll').css('overflow', 'none');
	$('#meetingSelect').slideUp();
	var call = $.ajax({
		url: "/bin/vtk/v1/meetingSearch",
		dataType: "json",
		type: 'POST',
		contentType: "application/json",
		cache: false,
		data: JSON.stringify(params.get())
	})
	call.done(function(data) {
		$("#meetingSelect").children().each(function() {
			if ($(this).hasClass("meeting-age-separator") || $(this).hasClass("meeting-item")) {
				$(this).remove();
			}
		});
		appendPreviewMeetings(data);
		$('.meeting-library .loading-meeting').hide()
		$('.vtk-body .ui-dialog.modalWrap .scroll').css('overflow', 'none');
		$('.meeting-library #vtk-meeting-filter').fadeIn();
		$('.meeting-library .list-of-buttons').fadeIn();

	});

	call.fail(function(err) {
		console.error('error:', err);
		$('.meeting-library .loading-meeting').hide()
		$('.vtk-body .ui-dialog.modalWrap .scroll').css('overflow', 'auto');
		$('.meeting-library #vtk-meeting-filter').fadeIn();
		$('.meeting-library .list-of-buttons').fadeIn();
		$("#meetingSelect").css("display", "block");
	})
}
function appendPreviewMeetings(data) {

	var ambassadorMeetings = [];
	var brownieMeetings = [];
	var daisyMeetings = [];
	var cadetteMeetings = [];
	var juniorMeetings = [];
	var seniorMeetings = [];
	var multiMeetings = [];
	$("#no-of-meeting").find("p").text(data.length + " Meeting Plans");
	$("#no-of-meeting").css("display", "block");

	for (var meeting in data) {
		var meetingLevel = data[meeting].level;
		if (meetingLevel === "Ambassador") {
			ambassadorMeetings.push(data[meeting]);
		} else if (meetingLevel === "Brownie") {
			brownieMeetings.push(data[meeting]);
		} else if (meetingLevel === "Cadette") {
			cadetteMeetings.push(data[meeting]);
		} else if (meetingLevel === "Daisy") {
			daisyMeetings.push(data[meeting]);
		} else if (meetingLevel === "Junior") {
			juniorMeetings.push(data[meeting]);
		} else if (meetingLevel === "Senior") {
			seniorMeetings.push(data[meeting]);
		} else if (meetingLevel === "Multi-level") {
			multiMeetings.push(data[meeting]);
		}
	}
	if (ambassadorMeetings.length) {
		$("#meetingSelect").append("<div class='meeting-age-separator column small-24 levelNav_Ambassador' id='levelNav_Ambassador'>Ambassador</div>");
		for (var meeting in ambassadorMeetings) {
			generatePreviewMeetingHtml(ambassadorMeetings, meeting);
		}
	}
	if (brownieMeetings.length) {
		$("#meetingSelect").append("<div class='meeting-age-separator column small-24 levelNav_Brownie' id='levelNav_Brownie'>Brownie</div>");
		for (var meeting in brownieMeetings) {
			generatePreviewMeetingHtml(brownieMeetings, meeting);
		}
	}
	if (cadetteMeetings.length) {
		$("#meetingSelect").append("<div class='meeting-age-separator column small-24 levelNav_Cadette' id='levelNav_Cadette'>Cadette</div>");
		for (var meeting in cadetteMeetings) {
			generatePreviewMeetingHtml(cadetteMeetings, meeting);
		}
	}
	if (daisyMeetings.length) {
		$("#meetingSelect").append("<div class='meeting-age-separator column small-24 levelNav_Daisy' id='levelNav_Daisy'>Daisy</div>");
		for (var meeting in daisyMeetings) {
			generatePreviewMeetingHtml(daisyMeetings, meeting);
		}
	}
	if (juniorMeetings.length) {
		$("#meetingSelect").append("<div class='meeting-age-separator column small-24 levelNav_Junior' id='levelNav_Junior'>Junior</div>");
		for (var meeting in juniorMeetings) {
			generatePreviewMeetingHtml(juniorMeetings, meeting);
		}
	}
	if (multiMeetings.length) {
		$("#meetingSelect").append("<div class='meeting-age-separator column small-24 levelNav_Multi_level' id='levelNav_Multi_level'>Multi-Level</div>");
		for (var meeting in multiMeetings) {
			generatePreviewMeetingHtml(multiMeetings, meeting);
		}
	}
	if (seniorMeetings.length) {
		$("#meetingSelect").append("<div class='meeting-age-separator column small-24 levelNav_Senior' id='levelNav_Senior'>Senior</div>");
		for (var meeting in seniorMeetings) {
			generatePreviewMeetingHtml(seniorMeetings, meeting);
		}
	}
	$("#meetingSelect").addClass("showResults");

}
function generatePreviewMeetingHtml(data, meeting) {

	$("#meetingSelect").append("<div class='meeting-item column small-24' id='vtk-mtg-" + data[meeting].id + "' data-url='" + data[meeting].path + "' data-meetingid='" + data[meeting].id + "'></div>")
	$("[data-meetingid=" + data[meeting].id + "]").append("<div class='row' style='background:#f6f6f6;'><div>");
	if (!(data[meeting].req !== null && data[meeting].req !== undefined && data[meeting].req !== "")) {
		$("[data-meetingid=" + data[meeting].id + "]").find(".row").append("<div class='column small-24 medium-4'><div style='min-height:110px; width:100%'><div style='height:inherit;vertical-align:middle; text-align:center;width:100%'><img width='100' onclick='openRequirementDetail(this)' class='image  _requirement_modal' height='100' src='/content/girlscouts-vtk/service/meeting/icon." + data[meeting].id + ".png'></div></div></div>");
	} else {
		$("[data-meetingid=" + data[meeting].id + "]").find(".row").append("<div class='column small-24 medium-4'><div style='min-height:110px; width:100%'><div style='height:inherit;vertical-align:middle; text-align:center;width:100%'><img width='100' onclick='openRequirementDetail(this)' class='image  _requirement_modal' height='100' src='/content/girlscouts-vtk/service/meeting/icon." + data[meeting].id + ".png'></div></div></div>");
		$("[data-meetingid=" + data[meeting].id + "]").append("<div class='__requiments_details row' style='display:none'><div class='column small-24' style='padding:10px;'><div class='_requiments_description'><p style='margin-bottom: 5px'><b>" + data[meeting].reqTitle + "</b></p>" + data[meeting].req + "</div><p style='text-align:center; margin-top:20px'><span class='vtk-button' style='cursor:pointer;' onclick='_closeME(this)'>&nbsp;&nbsp;&nbsp;CLOSE&nbsp;&nbsp;&nbsp;</span></p></div></div>");
	}

	$("[data-meetingid=" + data[meeting].id + "]").find(".row").append("<div class='column small-24 medium-14'><div style='display:table;min-height:110px'><div style='display:table-cell;height:inherit;vertical-align:middle;'><p class='title'>" + data[meeting].name + "</p><p class='blurb'>" + data[meeting].blurb + "</p><p class='tags'> <span></span></p></div></div></div>");

	$("[data-meetingid=" + data[meeting].id + "]").find(".row").append("<div class='column small-24 medium-6'><div class='middle-checkbox' style='text-align:center;'><table style='background:none'><tbody style='background:none'><tr style='background:none'><td style='padding:0'><p style='color:#000;'>SELECT TO ADD MEETING</p></td><td><input onclick='syncCheckboxesAndAddToYearPlan(this);' type='checkbox' name='addMeetingMulti' data-mtg-id=" + data[meeting].id + " id='header-add-" + data[meeting].id + "' value=" + data[meeting].path + "><label for='header-add-" + data[meeting].id + "'><span></span></label></td></tr><tr style='background:none'><td colspan='2' style='border:1px solid lightgray;text-align: center;'><div class='vtk-meeting-preview-btn' data-mtgid='" + data[meeting].id + "' data-path ='" + data[meeting].path + "'onclick='previewMeetingInfo()'>PREVIEW</div></td></tr></tbody></table></div></div>");

    $("#meetingSelect").append("<div class='meeting-item column small-24 medium-24'id='vtk-mtg-preview-" + data[meeting].id + "' style='display:none;background:none;padding:0 5px;'></div>");
	if (data[meeting].hasGlobal === true) {
		$("[data-meetingid=" + data[meeting].id + "]").find(".title").append("<img data-tooltip='' aria-haspopup='true' class='has-tip tip-top radius meeting_library' style='width:30px;vertical-align:top;padding-top:2px;cursor:auto;border:none' src='/etc/designs/girlscouts-vtk/clientlibs/css/images/globe_selected.png' data-selector='tooltip-jyhe4u6u1' title=''>");
	}
	if (data[meeting].hasOutdoor === true) {
		$("[data-meetingid=" + data[meeting].id + "]").find(".title").append("<img data-tooltip='' aria-haspopup='true' class='has-tip tip-top radius meeting_library' style='width:30px;vertical-align:bottom;cursor:auto;border:none' src='/etc/designs/girlscouts-vtk/clientlibs/css/images/outdoor.png' data-selector='tooltip-jyhelib9j' title=''>");
	}
	if (data[meeting].hasVirtual === true) {
		$("[data-meetingid=" + data[meeting].id + "]").find(".title").append("<img data-tooltip='' aria-haspopup='true' class='has-tip tip-top radius meeting_library' style='width:30px;vertical-align:bottom;cursor:auto;border:none' src='/etc/designs/girlscouts-vtk/clientlibs/css/images/virtual_selected.png' data-selector='tooltip-jyhelib9j' title=''>");
	}
}
function attendanceSelect() {
	var checkboxesAttendance = document.getElementsByName('attendance');
	var checkboxesAchievements = document.getElementsByName('achievement');
	for (var i = 0; i < checkboxesAttendance.length; i++) {
		if (checkboxesAttendance[i] !== undefined)
			checkboxesAttendance[i].checked = true;
		if (checkboxesAchievements[i] !== undefined)
			checkboxesAchievements[i].checked = true;
	}
}

function attendanceClear() {
	var checkboxesAttendance = document.getElementsByName('attendance');
	var checkboxesAchievements = document.getElementsByName('achievement');
	for (var i = 0; i < checkboxesAttendance.length; i++) {
		if (checkboxesAttendance[i] !== undefined)
			checkboxesAttendance[i].checked = false;
		if (checkboxesAchievements[i] !== undefined)
			checkboxesAchievements[i].checked = false;
	}
}

$(document).ready(checkNews());

$(window).load(function() {
	var notice = $("#maintenanceModal");
	notice.css('display', 'block');
	$(".vtk-maintenance-news-button").click(function() {
		notice.css('display', 'none');
	});
	$(document).click(function() {
		notice.css('display', 'none');
	});
	$(".maintenance-content").click(function(event) {
		event.stopPropagation();
	});
});
// Re-Foundation on page load and dom load for modals that were added dynamically.
$(function() {
	$(document).foundation();
});
$(window).load(function() {
	$(document).foundation();
});

function escapeHTML(text) {
	return $("<div>").text(text).html();
}

function checkMeetingAidsRefs(innerText) {
	let newHtml = innerText || "";
	if (!newHtml.length) return "";

	$(".__list_of_assets li a").each((i, el) => {
		let text = this.escapeHTML($(el).text().trim()) || "",
			href = $(el).attr("href") || "",
			link = `<a href='${href}' target='_blank'>${text}</a>`;
		if (text.length && href.length) {
			// Replace modal description text with asset links
			newHtml = newHtml.split(text).join(link);
		}
	});

	return newHtml;
}

function previewMeetingInfo() {
    var meetingId = event.target.dataset.mtgid;
    var url = "/content/girlscouts-vtk/service/react/getMeetingPreview.html?meetingPath="+event.target.dataset.path;

    if ($("#vtk-mtg-preview-" + meetingId).html() == "") {
        $.ajax({
            method: 'GET',
            url: url,
            dataType: 'JSON',
            success: function (response) {
                preparePreviewData(response);
            }
        });
    } else {
        /*$(".meeting-item").toggleClass('open_slide');
        $(".meeting-item").find('#vtk-mtg-preview-container-' + meetingId).toggle();*/
        $("#vtk-mtg-preview-" + meetingId).toggle();
    }
}
function showActivityPreview(index){
    resetModalPage();
    $("#gsModal").html("<div class=\"header clearfix\">" +
        "<h3 class=\"columns small-21\">Activity</h3>" +
        "<span style=\"position:absolute; top:-5px; right:9px; color:black; font-size:22px; cursor:pointer; font-family: 'Trefoil Sans Web', 'Open Sans', Arial, sans-serif;\" onclick=\"(function(){$('#gsModal').dialog('close')})()\">X</span>" +
        "</div>" +
        "<div class=\"scroll\" style=\"max-height:601px\">" +
        "    <!-- Content -->" +
            "<div class='columns small-22 small-centered activity-time'><b>"+$("#activity-preview-time-"+index).html()+"</b> Recommended time</div>" +
            "<div class=\"columns small-22 small-centered activity-name\"><h5>" + $("#activity-preview-title-"+index).html() + "</h5></div>" +
            "<div class=\"columns small-22 small-centered activity-desc\">" + $("#activity-preview-description-"+index).html()+"</div>" +
        "</div>");
    $("#vtk-loading").css("display","none");//hides loading animation
    loadModal("#gsModal", false, "Activity", true, false);
    $('#gsModal').children('.scroll').css('maxHeight', '601px');
    $(document).foundation();
}
function convertMinsToHrsMins(mins) {
    var h = Math.floor(mins / 60);
    var m = mins % 60;
    h = h > 12 ? h - 12 : h;
    m = m < 10 ? '0' + m : m;
    return h + ":" + m;
}
function preparePreviewData(response) {
    var meetingData = response.meeting;
    var meetingAidsData = response.meetingAids;
    var path = meetingData.path
    console.log("response:" + response);
    var mtgId = meetingData.id;
    var checked = "";
    if(document.getElementById("header-add-"+mtgId)){
        if(document.getElementById("header-add-"+mtgId).checked){
            checked = "checked";
        }
    }
    $("#vtk-mtg-preview-" + mtgId).show();
    var title = meetingData.name;
    var blurb = meetingData.blurb;
    var meetingOverviewHTML = meetingData.meetingInfo.overview.str;
    var activities = meetingData.activities;
    var activitiesPlanHTML = "";
    var materialsListHTML = "";
    var meetingAidsHTML="";
    var agendaHTML = "";
    var agendaIndex = 0;
    var timeOptions = {"5": '00:05', "10":'00:10', "15":'00:15', "20": '00:20',"25":'00:25', "30":'00:30', "35":'00:35', "40":'00:40', "45":'00:45', "50":'00:50', "55":'00:55', "60":'00:60', "65":'01:05', "70":'01:10', "75":'01:15', "80":'01:20', "85":'01:25', "90":'01:30', "95":'01:35', "100": '01:40' };
    var totalAgendaTime = 0;
    agendaHTML="<ul class=\"__agenda-items\">";
    activities.sort(function(a,b){
        return parseInt(a.activityNumber)-parseInt(b.activityNumber);
    });
    $.each(activities, function (index, entry) {
        var agendaTitle = null;
        var agendaDuration = null;
        if (null != entry.activityNumber) {
            var multiactiviews = entry.multiactivities;
            var isMulti = multiactiviews.length > 1;
            activitiesPlanHTML += "<div class='agenda-preview'>";
            if(isMulti){
                activitiesPlanHTML += "<div class='agenda-preview-title'><h4>Activity " + entry.activityNumber + "</h4></div>";
                multiactiviews.sort(function(a,b){
                    return parseInt(a.activityNumber)-parseInt(b.activityNumber);
                });
                agendaHTML += "<li class=\"__agenda-item __multiple\">" +
                    "<div class=\"__main column small-12 medium-12\" style='background:none;'>" +
                        "<div class=\"__time_counter column small-1 medium-1\">"+entry.activityNumber+"</div>" +
                        "<div class=\"__description column small-9 medium-9\" style='flex-grow: unset;width:330px;'>" +
                            "<div class=\"__title\">" +
                                "<div class=\"__text\">Select an activity</div>";
                                $.each(multiactiviews, function (actKey, actValue) {
                                    if (null != actValue.activityDescription && null != actValue.name) {
                                        var name = actValue.name ? actValue.name : entry.name;
                                        var description = actValue.activityDescription ? actValue.activityDescription : entry.activityDescription;
                                        activitiesPlanHTML += "<div class='agenda-choice-preview multi'>";
                                            activitiesPlanHTML += "<div class='agenda-choice-preview-title'><h5>Choice "+ actValue.activityNumber+" : " + name + "</h5></div>";
                                            activitiesPlanHTML += "<div>" + description + "</div>";
                                        activitiesPlanHTML += "</div>";
                                        if (actValue.materials) {
                                            materialsListHTML += actValue.materials;
                                        }
                                        agendaHTML += "<div class=\"__text\">" +
                                            "<div style='width:60px;padding-right:5px;float:left;'>";
                                                var indicatorsHTML = "";
                                                if(actValue.global){
                                                    indicatorsHTML+="<img data-tooltip=\"\" aria-haspopup=\"true\" class=\"has-tip tip-top radius meeting_library\" style=\"float:right;width:20px;vertical-align:top;padding-top:2px;cursor:auto;border:none\" src=\"/etc/designs/girlscouts-vtk/clientlibs/css/images/globe_selected.png\" data-selector=\"tooltip-jyhe4u6u1\" title=\"\">";
                                                }
                                                if(actValue.virtual){
                                                    indicatorsHTML+="<img data-tooltip=\"\" aria-haspopup=\"true\" class=\"has-tip tip-top radius meeting_library\" style=\"float:right;width:20px;vertical-align:bottom;cursor:auto;border:none\" src=\"/etc/designs/girlscouts-vtk/clientlibs/css/images/virtual_selected.png\" data-selector=\"tooltip-jyhelib9j\" title=\"\">";
                                                }
                                                if(actValue.outdoor){
                                                    indicatorsHTML+="<img data-tooltip=\"\" aria-haspopup=\"true\" class=\"has-tip tip-top radius meeting_library\" style=\"float:right;width:20px;vertical-align:bottom;cursor:auto;border:none\" src=\"/etc/designs/girlscouts-vtk/clientlibs/css/images/outdoor.png\" data-selector=\"tooltip-jyhelib9j\" title=\"\">";
                                                }
                                                if(indicatorsHTML==""){
                                                    indicatorsHTML+="&nbsp;";
                                                }
                                                agendaHTML += indicatorsHTML;
                                                agendaHTML += "</div>" +
                                                "<a href='javascript:void(0);' onclick='showActivityPreview(" + agendaIndex + ");'>" + actValue.name + "</a>" +
                                            "</div>" +
                                            "<div id='activity-preview-title-"+agendaIndex+"' style='display:none !important'>"+name+"</div>" +
                                            "<div id='activity-preview-description-"+agendaIndex+"' style='display:none !important'>"+description+"</div>" +
                                            "<div id='activity-preview-time-"+agendaIndex+"' style='display:none !important'>"+timeOptions[entry.duration]+"</div>";
                                        agendaIndex++;
                                    }
                                });
                    agendaHTML += "</div>" + "</div>" +
                        "<div class=\"__time column small-2 medium-2\">"+timeOptions[entry.duration]+"</div>"+
                    "</div>" +
                    "</li>";
                totalAgendaTime+=parseInt(entry.duration);
            }else{
                var name = multiactiviews[0].name ? multiactiviews[0].name : entry.name;
                var description = multiactiviews[0].activityDescription ? multiactiviews[0].activityDescription : entry.activityDescription;
                activitiesPlanHTML += "<div class='agenda-preview-title'><h4>Activity " + entry.activityNumber  + "</h4></div>";
                activitiesPlanHTML += "<div class='agenda-choice-preview single'>";
                    activitiesPlanHTML += "<div class='agenda-choice-preview-title'><h5>" + name + "</h5></div>";
                    activitiesPlanHTML += "<div class='agenda-choice-preview-description'>" + description + "</div>";
                activitiesPlanHTML += "</div>";
                agendaHTML+=
                    "<li class=\"__agenda-item __single\" style='margin-bottom: 5px;'>" +
                        "<div class=\"__main column small-12 medium-12\" style='background:none;'>" +
                            "<div class=\"__time_counter column small-1 medium-1\">"+entry.activityNumber+"</div>"+
                            "<div class=\"__description column small-9 medium-9\"  style='flex-grow: unset;width:330px;'>" +
                                "<div class=\"__title\">" +
                                    "<div class=\"__text\">" +
                                        "<div style='width:60px;padding-right:5px;float:left;' >";
                                            var indicatorsHTML = "";
                                            if(multiactiviews[0].global){
                                                indicatorsHTML+="<img data-tooltip=\"\" aria-haspopup=\"true\" class=\"has-tip tip-top radius meeting_library\" style=\"float:right;width:20px;vertical-align:top;padding-top:2px;cursor:auto;border:none\" src=\"/etc/designs/girlscouts-vtk/clientlibs/css/images/globe_selected.png\" data-selector=\"tooltip-jyhe4u6u1\" title=\"\">";
                                            }
                                            if(multiactiviews[0].virtual){
                                                indicatorsHTML+="<img data-tooltip=\"\" aria-haspopup=\"true\" class=\"has-tip tip-top radius meeting_library\" style=\"float:right;width:20px;vertical-align:bottom;cursor:auto;border:none\" src=\"/etc/designs/girlscouts-vtk/clientlibs/css/images/virtual_selected.png\" data-selector=\"tooltip-jyhelib9j\" title=\"\">";
                                            }
                                            if(multiactiviews[0].outdoor){
                                                indicatorsHTML+="<img data-tooltip=\"\" aria-haspopup=\"true\" class=\"has-tip tip-top radius meeting_library\" style=\"float:right;width:20px;vertical-align:bottom;cursor:auto;border:none\" src=\"/etc/designs/girlscouts-vtk/clientlibs/css/images/outdoor.png\" data-selector=\"tooltip-jyhelib9j\" title=\"\">";
                                            }
                                            if(indicatorsHTML==""){
                                                indicatorsHTML+="&nbsp;";
                                            }
                                            agendaHTML += indicatorsHTML;
                                            agendaHTML += "</div>" +
                                            "<a href='javascript:void(0);' onclick='showActivityPreview("+agendaIndex+")'>" + name + "</a>" +
                                    "</div>" +
                                "</div>" +
                            "</div>" +
                            "<div class=\"__time  column small-2 medium-2\">"+timeOptions[entry.duration]+"</div>"+
                        "</div>" +
                        "<div id='activity-preview-title-"+agendaIndex+"' style='display:none !important'>"+name+"</div>" +
                        "<div id='activity-preview-description-"+agendaIndex+"' style='display:none !important'>"+description+"</div>" +
                        "<div id='activity-preview-time-"+agendaIndex+"' style='display:none !important'>"+timeOptions[entry.duration]+"</div>" +
                    "</li>";
                agendaIndex++;
                totalAgendaTime+=parseInt(entry.duration);
                if (multiactiviews[0].materials) {
                    materialsListHTML += multiactiviews[0].materials;
                }
            }
        }
    });
    agendaHTML+="<li class=\"__agenda-item __single\" style='margin-bottom: 5px;'>" +
                    "<div class=\"__main column small-12 medium-12\" style='background:none;'>" +
                        "<div class=\"__time_counter column small-1 medium-1\">&nbsp;</div>"+
                        "<div class=\"__description column small-9 medium-9\"  style='flex-grow: unset;width:330px;'>" +
                            "<div class=\"__title\">" +
                                "<div class=\"__text\">" +
                                    "<div style='float:right;' >Total Time:</div>" +
                                "</div>" +
                            "</div>" +
                        "</div>" +
                        "<div class=\"__time  column small-2 medium-2\"><b>"+convertMinsToHrsMins(totalAgendaTime)+"</b></div>"+
                    "</div>" +
                "</li>"+
            "</ul>";
    meetingAidsHTML+="<ul class=\"__list_of_assets large-block-grid-2 medium-block-grid-2 small-block-grid-2\">";
        $.each(meetingAidsData, function (index, entry) {
            meetingAidsHTML += "" +
                "<li>" +
                "<div style=\"display: table;\"><div style=\"display: table-cell;\">" +
                "<a href=\"" + entry.refId + "\" title=\"Meeting Asset\" target=\"_blank\" class=\"icon " + entry.docType + "\" style=\"font-weight: bold;\">" +
                entry.title + "<span></span>" +
                "</a>" +
                "<p class=\"info\">" + (entry.description ? entry.description:"") + "</p>" +
                "</div></div></li>";
        });
    meetingAidsHTML+="</ul>";
    $("#vtk-mtg-preview-" + mtgId).html("<div class='row'>" +
        "<div style='padding-top:20px' class='column small-24 medium-24'>" +
            "<div class='column small-18 medium-18'>" +
                "<div style='min-height:110px; width:100%'>" +
                    "<div style='height:inherit;vertical-align:middle; text-align:center;width:100%'> " +
                        "<img style='float:left' width='100' onclick='openRequirementDetail(this)' class='image _requirement_modal' height='100' src='/content/girlscouts-vtk/service/meeting/icon." + mtgId + ".png'/>" +
                    "</div>" +
                "</div>" +
            "</div>" +
            "<div class='column small-6 medium-6'>" +
                "<div class='middle-checkbox' style='text-align:center; '>" +
                    "<table style='background:none;'>" +
                        "<tbody style='background:none;'>" +
                            "<tr style='background:none;'>" +
                                "<td colspan='2' style='border:1px solid lightgray;text-align: center;'>" +
                                    "<div class='vtk-meeting-preview-btn' data-mtgid='" + mtgId + "' data-path='" + path + "' onclick='previewMeetingInfo()'>CLOSE PREVIEW</div>" +
                                "</td>" +
                            "</tr style='background:none;'>" +
                            "<tr style='background:none;'>" +
                                "<td style='padding:0'>" +
                                    "<p style='color:#000;'>SELECT TO ADD MEETING</p>" +
                                "</td>" +
                                "<td>" +
                                    "<input onclick='syncCheckboxesAndAddToYearPlan(this);'  id='slider-add-"+mtgId+"' "+ checked +" type='checkbox' name='addMeetingMulti' data-mtg-id=" + mtgId + " value=" + path + ">" +
                                    "<label for='slider-add-"+ mtgId + "'>" +
                                        "<span></span>" +
                                    "</label>" +
                                "</td>" +
                            "</tr>" +
                        "</tbody>" +
                    "</table>" +
                "</div>" +
            "</div>" +
            "<div class='column small-24 medium-24' style='border-bottom:1px solid #000000; margin-bottom:20px;'>" +
                "<div class='preview-mtg-title'>" + title + "</div>" +
                "<div class='preview-mtg-desc'>" + blurb + "</div> " +
                "<details> " +
                    "<summary id='"+mtgId+"-meeting-overview-summary'>Meeting Overview</summary><div class='summary-wrap'>" + meetingOverviewHTML + "</div> " +
                "</details> " +
                "<details> " +
                    "<summary id='"+mtgId+"-activity-plan-summary'>Activity Plan</summary><div class='summary-wrap'>" + activitiesPlanHTML + "</div>" +
                "</details> " +
                "<details> " +
                    "<summary id='"+mtgId+"-materials-list-summary'>Materials List</summary><div class='summary-wrap'>" + materialsListHTML + "</div> " +
                "</details>" +
                "<details> " +
                    "<summary id='"+mtgId+"-meeting-aids-summary'>Meeting Aids</summary><div class='summary-wrap'>" + meetingAidsHTML + "</div> " +
                "</details>" +
                "<details> " +
                    "<summary id='"+mtgId+"-agenda-summary'>Agenda</summary><div class='summary-wrap'>" + agendaHTML + "</div> " +
                "</details>"+
                "</div>" +
                "<div class='column small-9 medium-9'>&nbsp;</div>" +
                "<div class='column small-6 medium-6'>" +
                "<table style='background:none;'>" +
                "<tbody style='background:none;'>" +
                "<tr style='background:none;'>" +
                "<td colspan='2' style='border:1px solid lightgray;text-align: center;'>" +
                "<div class='vtk-meeting-preview-btn' data-mtgid='" + mtgId + "' data-path='" + path + "' onclick='previewMeetingInfo()'>CLOSE PREVIEW</div>" +
                "</td>" +
                "</tr style='background:none;'>" +
                "</tbody>" +
                "</table>" +
                "</div>" +
                "<div class='column small-9 medium-9'>&nbsp;</div>" +
            "</div>" +
        "</div>") ;
        $("#"+mtgId+"-meeting-aids-summary").click();
        $("#"+mtgId+"-agenda-summary").click();

}
function closeSearchResults(){	
	$('#searchByMeetingTitle').val('');
	$("#meetingSelect").removeClass("showResults");
	$('#vtk-meeting-search-btn').addClass('disabled');
}
