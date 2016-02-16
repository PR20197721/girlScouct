(function(){
	const END_POINT = '/content/gsusa/en/components/article-hub/dynamic-tag-carousel';

	DynamicTagCarousel = function(id, num, defaultTag) {
		this.id = id;
		this.num = num;
		this.defaultTag;
	};
	
	DynamicTagCarousel.prototype.load = function() {
		var hash = window.location.hash;
		
		if (hash) {
			hash = hash.indexOf('#') == 0 ? hash.substring(1) : hash;
		} else {
			hash = "";
		}
		
		var result = hash.split('$$$');
		var tags = result.length >= 1 ? result[0] : this.defaultTag;
		
		var selector = '#' + this.id;
		var url = END_POINT + '.' + tags + '.' + this.num + '.html';
		$(selector).load(url);
	};
})();