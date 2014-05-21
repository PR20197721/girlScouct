girlscouts.components.login = {
    loginUrl: null,
    logoutUrl: null,
    language: null,
    init: function(language, loginUrl, logoutUrl) {
    	this.language = language;
    	this.loginUrl = loginUrl;
    	this.logoutUrl = logoutUrl;
    },
    
    genCode: function(target) {
	    var randNum = new Date().getTime().toString() + Math.floor((Math.random() * 1000) + 1).toString();
	    var script = document.createElement('script');
	    script.type = 'text/javascript';
	    script.src = target + '?rand=' + randNum;
	    document.getElementsByTagName('head')[0].appendChild(script);
    },

    sayHello: function(state, name) {
	    // TODO: Add language
	    var html;
	    if (state === 'loggedin') {
		    html = '<span>' + 'Hello ' + name + ' <a href="' + this.logoutUrl+ '" class="signout">' + 'SIGN OUT' + '</a></span>';
	    } else if (state === 'loggedout') {
		    html = '<span><a href="' + this.loginUrl + '" class="signin">' + 'SIGN IN' + '</a></span>';
    	}
	    $('.login').html(html);
    }
};