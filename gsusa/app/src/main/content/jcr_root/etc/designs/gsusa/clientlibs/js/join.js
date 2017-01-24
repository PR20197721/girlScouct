$('.formJoin, .formHeaderJoin, .bottom-overlay-join').submit(function (event) {
    strGACampaign = getParameterByName("utm_campaign");
    strGAMedium = getParameterByName("utm_medium");
    strGASource = getParameterByName("utm_source");  

	joinForm = this;
    me = event.target;
	zipValue = $(me).find("[name='ZipJoin']").val();
	source = $(me).find("[name='source']").val();
	
	// spin variables - start
	opts = {
	  lines: 11, // The number of lines to draw
	  length: 15, // The length of each line
	  width: 10, // The line thickness
	  radius: 30, // The radius of the inner circle
	  corners: 1, // Corner roundness (0..1)
	  rotate: 0, // The rotation offset
	  direction: 1, // 1: clockwise, -1: counterclockwise
	  color: '#000', // #rgb or #rrggbb
	  speed: 0.6, // Rounds per second
	  trail: 60, // Afterglow percentage
	  shadow: false, // Whether to render a shadow
	  hwaccel: false, // Whether to use hardware acceleration
	  className: 'spinner', // The CSS class to assign to the spinner
	  top: 'auto', // Top position relative to parent in px
	  left: 'auto' // Left position relative to parent in px
	};
	spinner = null;
	spinner_div = $('#spinner').get(0);
	spinner = new Spinner(opts).spin(spinner_div);
	
	$.ajax({
		type: "POST",
		url: '/includes/join/join_ajax_GetCouncilInfo.asp',
		async: false,
		data: {
	        zipcode: zipValue,
	        source: source,
	        actiontype: "join",
	        GACampaign: strGACampaign,
	        GAMedium: strGAMedium,
	        GASource: strGASource
	    },
		success: function(txt) {
	        var found = true;
	        //submission page contains either welcome or thank you
	        //no need to check since it is not critical
	        if (txt.search(/INSERTED/i) == -1) {
	            alert('Sorry, there is no local council serving zipcode '+ zipValue);
	            spinner.stop(spinner_div);
	        }

	        //see if we can still parse and process url
	        var result = txt.split(",");
	        if(!isNumber(result[0])) {
	            found = false;
	        } else {
	        // finally, see if we have the url
	            if(!result[2]) {
	                found = false;
	            }
	        }

	        if(found) {
	        	//We are putting window.open here because either the _gaq.push or submit_facebook_conversion_pixel is async, causing browser to block pop-up window, thus, we have to open the window first
	        	//However, if we want to open the redirect link in the same browser tab, then we will need to first call the functions, then redirect, so there is another if case at the end of this part
	        	if ($(joinForm).hasClass("bottom-overlay-join")) {
	        		window.open(result[2], '_blank');
	        	}
	            //register zipcode entered to google analytics
	            //var curZipcode = $(me).find("[name='ZipJoin']").val();
	            spinner.stop(spinner_div);
				ga('send', 'pageview', '/gsrecruitmentcampaign/join/zipcode_entered'+"/"+zipValue);
	        
	            submit_facebook_conversion_pixel("join/"+"homepage");
	            //put delay so that google analytics and facebook conversion pixel registers successfully
	            if (!$(joinForm).hasClass("bottom-overlay-join")) {
					window.setTimeout("redirect_to_council('"+result[2]+"')",1500);
				}
	        } else {
	            // invalidate the zipcode field manually
	            $(me).find("[name='ZipJoin']").val('Invalid');
	            spinner.stop(spinner_div);
	            // .valid() should work but somehow with edge animation, it doesn't work.. so instead, using .select()
	            $(me).find("[name='ZipJoin']").select();		
	        }
	    }
	});

    return false;
});

//function redirect_to_council_new_window(url) {
//	window.open(url, "newPage");
//}

function redirect_to_council(url) {
    parent.window.location = url;
}

function submit_facebook_conversion_pixel(src) {
    facebookTrackingSendClick();
}

function facebookTrackingSendClick() {
    // because image based version didn't fire well, below is using the full javascript
    var _fbq = window._fbq || (window._fbq = []);
    if (!_fbq.loaded) {
    var fbds = document.createElement('script');
        fbds.async = true;
	fbds.src = '//connect.facebook.net/en_US/fbds.js';
    	var s = document.getElementsByTagName('script')[0];
	s.parentNode.insertBefore(fbds, s);
        _fbq.loaded = true;
    }
    window._fbq = window._fbq || [];
    window._fbq.push(['track', '6012336501089', {'value':'0.00','currency':'USD'}]);
}


