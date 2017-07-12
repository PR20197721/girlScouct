<%@include file="/libs/foundation/global.jsp" %>

	<div id="results-area"></div>
	<script type="text/javascript">
		var res = $("#results-area");
		var display = function(data){
			var json = JSON.parse(data);
			if (json.councils == undefined){
				res.html("<p>It looks like something went wrong with your search.<br>Please try again.</p>");
			}
			else if(json.councils.length == 0){
				res.html("<p>No results found.</p>");
			}
			else{
				var result = "<ul class=\"councils\">";
				for(var i=0; i < json.councils.length; i++) {
					result += "<li><h5>" + json.councils[i].councilFullName + "</h5>";
					result += "<section><p>" + json.councils[i].city + ", " + json.councils[i].state + " " + json.councils[i].zipcode + "</p>";
					result += "<p>" + json.councils[i].phone + "</p>";
					if(json.councils[i].tollFreePhone != undefined && json.councils[i].tollFreePhone != "") {
						result += "<p>" + json.councils[i].tollFreePhone + " - Local Toll Free Phone</p>";
					}
					var siteURL = json.councils[i].website;
					var shortSite = siteURL;
					var email = json.councils[i].email;
					var fbURL = json.councils[i].facebook;
					var shortFB = fbURL;
					var twitterURL = json.councils[i].twitter;
					var shortTwitter = twitterURL;
					var q;
					if(siteURL != undefined){
						q = siteURL.indexOf('?');
						if(q != -1){
							shortSite = siteURL.substring(0,q);
						}
					}
					if(fbURL != undefined){
						q = fbURL.indexOf('?');
						if(q != -1){
							shortFB = fbURL.substring(0,q);
						}
					}
					if(twitterURL != undefined){
						q = twitterURL.indexOf('?');
						if(q != -1){
							shortTwitter = twitterURL.substring(0,q);
						}
					}
					if(email != undefined){
						result += "<p>Email: <a href=\"mailto:" + email + "\">" + email + "</a></p>";
					}
					if(siteURL != undefined){
						result += "<p>Website: <a href=\"" + siteURL + "\">" + shortSite + "</a></p>";
					}
					if(fbURL != undefined){
						result += "<p>Facebook: <a href=\"" + fbURL + "\">" + shortFB + "</a></p>";
					}
					if(shortTwitter != undefined){
						result += "<p>Twitter: <a href=\"" + twitterURL + "\">" + shortTwitter + "</a></p></section>";
					}
					if(json.councils[i].joinUrl != undefined){
						result += "<a class=\"button small radius\" href=\"" + json.councils[i].joinUrl + "\">Join</a>";
					}
					if(json.councils[i].volunteerUrl != undefined){
						result += "<a class=\"button small radius\" href=\"" + json.councils[i].volunteerUrl + "\">Volunteer</a>";
					}
					if(json.councils[i].onlineRegistrationUrl){
						result += "<a class=\"button small radius\" href=\"" + json.councils[i].onlineRegistrationUrl + "\">Online Registration</a></li>";
					}
				}
				result += "</ul>";
				res.html(result);
			}
		};


	// get zip
	var zip;
	zip = (function(zip){
		var hash = window.location.hash;
		if (hash.indexOf('#') == 0) {
			hash = hash.substring(1);
		}
		var zipRegex = /[0-9]{5}/;
		return zipRegex.test(hash) ? hash : zip;
	})();

	// get state
	var state;
	state = (function(state){
		var hash = window.location.hash;
		if (hash.indexOf('#') == 0) {
			hash = hash.substring(1);
		}
		var stateRegex = /[A-Z]{2}/;
		return stateRegex.test(hash) ? hash : state;
	})();

	// get code
	var code;
	code = (function(code){
		var hash = window.location.hash;
		if (hash.indexOf('#') == 0) {
			hash = hash.substring(1);
		}
		var codeRegex = /[0-9]{3}/;
		return codeRegex.test(hash) ? hash : code;
	})();

	var url = "";
	if (zip != undefined) {
		url = "/councilfinder/ajax_results.asp?zip=" + zip;
	} else if (state != undefined) {
		url = "/councilfinder/ajax_results.asp?state=" + state;
	} else if (code != undefined) {
		url = "/councilfinder/ajax_results.asp?code=" + code;
	}

		$(document).ready(function() {
			$.get(url,display);
		});
</script>

