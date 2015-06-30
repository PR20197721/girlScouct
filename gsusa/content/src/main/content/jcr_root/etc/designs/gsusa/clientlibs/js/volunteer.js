$('.FormVolunteer').submit(function () {

    console.info("heeeeo");

    strGACampaign = "a"; //getParameterByName("utm_campaign");
    strGAMedium = "b"; //getParameterByName("utm_medium");
    strGASource = "c"; //getParameterByName("utm_source");  
//
    $.post('/includes/join/join_ajax_GetCouncilInfo.asp',{
        zipcode:$('#ZipVolunteer').val(),
	source:"homepage",
	actiontype:"volunteer",
	GACampaign:strGACampaign,
	GAMedium:strGAMedium,
	GASource:strGASource
    }, function(txt) {

    
        console.info(txt);
        var found = true;
        //submission page contains either welcome or thank you
        //no need to check since it is not critical
        if (txt.search(/INSERTED/i) == -1) {
            alert('Sorry, there is no local council serving zipcode '+$('#ZipVolunteer').val());
            //spinner.stop(spinner_div);
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
            var curZipcode = $('#ZipVolunteer').val();
            _gaq.push(['_trackPageview','/gsrecruitmentcampaign/volunteer/zipcode_entered'+"/"+curZipcode]);
        
            submit_facebook_conversion_pixel("volunteer/"+src);
            //put delay so that google analytics and facebook conversion pixel registers successfully
            window.setTimeout("redirect_to_council('"+result[2]+"')",1500);
        } else {
            // invalidate the zipcode field manually
            $("#ZipVolunteer").val('Invalid');
            // .valid() should work but somehow with edge animation, it doesn't work.. so instead, using .select()
            $("#ZipVolunteer").select();		
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

function isNumber(n) {
  return !isNaN(parseFloat(n)) && isFinite(n);
}
