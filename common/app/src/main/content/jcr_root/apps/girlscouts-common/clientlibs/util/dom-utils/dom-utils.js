window.DomUtils = (function(){
	
	/*
	 * Decodes things like '%lt;' to html markup like '<'
	 */
	function _htmlDecode(input){
		var e = document.createElement('div');
		e.innerHTML = input;
		return e.childNodes[0].nodeValue;
	}
	
	return {
		htmlDecode : _htmlDecode
	};
})();