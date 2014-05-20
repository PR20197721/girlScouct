girlscouts.components.login = {};
girlscouts.components.login.genCode = function(target) {
	var randNum = new Date().getTime().toString() + Math.floor((Math.random() * 1000) + 1).toString();
	var sc = document.createElement('script')
	sc.type = 'text/javascript'
	sc.src = target + '?rand=' + randNum;
	document.getElementsByTagName('head')[0].appendChild(sc)
}