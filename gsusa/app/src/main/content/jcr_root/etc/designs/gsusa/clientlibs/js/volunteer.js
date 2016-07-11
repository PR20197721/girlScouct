$('.formVol, .formHeaderVolunteer').submit(function (event) {
    strGACampaign = getParameterByName("utm_campaign");
    strGAMedium = getParameterByName("utm_medium");
    strGASource = getParameterByName("utm_source");  

    me = event.target;
	zipValue = $(me).find("[name='ZipVolunteer']").val();
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
	  zIndex: 2e9, // The z-index (defaults to 2000000000)
	  top: 'auto', // Top position relative to parent in px
	  left: 'auto' // Left position relative to parent in px
	};
	spinner = null;
	spinner_div = $('#spinner').get(0);
	spinner = new Spinner(opts).spin(spinner_div);


    $.post('/includes/join/join_ajax_GetCouncilInfo.asp',{
        zipcode: zipValue,
        source: source,
        actiontype: "volunteer",
        GACampaign: strGACampaign,
        GAMedium: strGAMedium,
        GASource: strGASource
    }, function(txt) {
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
            //register zipcode entered to google analytics
            //var curZipcode = $(me).find("[name='ZipVolunteer']").val();
            spinner.stop(spinner_div);
            _gaq.push(['_trackPageview','/gsrecruitmentcampaign/volunteer/zipcode_entered'+"/"+zipValue]);
        
            submit_facebook_conversion_pixel("volunteer/"+"homepage");
            //put delay so that google analytics and facebook conversion pixel registers successfully
            window.setTimeout("redirect_to_council('"+result[2]+"')",1500);
        } else {
            // invalidate the zipcode field manually
            $(me).find("[name='ZipVolunteer']").val('Invalid');
            spinner.stop(spinner_div);
            // .valid() should work but somehow with edge animation, it doesn't work.. so instead, using .select()
            $(me).find("[name='ZipVolunteer']").select();		
        }
    });

    return false;
});

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


