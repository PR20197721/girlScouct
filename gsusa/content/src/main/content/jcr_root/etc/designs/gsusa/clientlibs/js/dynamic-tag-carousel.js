(function(){
	const END_POINT = '/content/gsusa/en/components/article-hub/dynamic-tag-carousel';

	DynamicTagCarousel = function(id, num, defaultTag) {
		this.id = id;
		this.num = num;
		this.defaultTag;
		
		$(document).ready(function(){
		});
	};
	
	console.info('Dynamic Tag Carousel definded.');

	DynamicTagCarousel.prototype.load = function() {
		var hash = window.location.hash;
		
		if (hash) {
			hash = hash.indexOf('#') == 0 ? hash.substring(1) : hash;
		} else {
			hash = this.defaultTag;
		}
			
		var selector = '#' + this.id;
		var url = END_POINT + '.' + hash + '.' + this.num + '.html';
		$(selector).load(url);
	};
})();