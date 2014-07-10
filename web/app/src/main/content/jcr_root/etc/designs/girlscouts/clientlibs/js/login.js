girlscouts.components.login = {
    signInUrl: null,
    signOutUrl: null,
    language: null,
    init: function(language, signInUrl, signOutUrl) {
    	this.language = language;
    	this.signInUrl = signInUrl;
    	this.signOutUrl = signOutUrl;
    },
    
    genCode: function(target) {
	    var randNum = new Date().getTime().toString() + Math.floor((Math.random() * 1000) + 1).toString();
	    var script = document.createElement('script');
	    script.type = 'text/javascript';
	    script.src = target + '?rand=' + randNum;
	    document.getElementsByTagName('head')[0].appendChild(script);
    },
    
    signOut: function() {
    	$.removeCookie('girl-scout-name', {path: '/'});
    	window.location.replace(this.signOutUrl);
    },

    sayHello: function(state, name) {
	    // TODO: Add language
	    var html;
	    if (state === 'signedin') {
		    html = '<span>' + 'Hello ' + name + 
		    ' <a href="javascript:void(0)" onclick="girlscouts.components.login.signOut();" class="signout">SIGN OUT</a></span>';
	    } else if (state === 'signedout') {
		    html = '<span><a href="' + this.signInUrl + '" class="signin">' + 'SIGN IN' + '</a></span>';
    	}
	    $('.login').html(html);
    }
};