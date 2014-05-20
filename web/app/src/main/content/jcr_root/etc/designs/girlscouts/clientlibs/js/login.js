girlscouts.components.login = {};
girlscouts.components.login.genCode = function(target) {
	var randNum = new Date().getTime().toString() + Math.floor((Math.random() * 1000) + 1).toString();
	var script = document.createElement('script')
	script.type = 'text/javascript'
	script.src = target + '?rand=' + randNum;
	document.getElementsByTagName('head')[0].appendChild(script);
}

girlscouts.components.login.updateLogin = function(state, name) {
	if (state === 'authenticated') {
	}
}