girlscouts.components.login = {};
girlscouts.components.login.genCode = function(target, referer, language) {
	var randNum = new Date().getTime().toString() + Math.floor((Math.random() * 1000) + 1).toString();
	var script = document.createElement('script')
	script.type = 'text/javascript'
	script.src = target + '?referer=' + referer + '&lang=' + language + '&rand=' + randNum;
	document.getElementsByTagName('head')[0].appendChild(script);
}