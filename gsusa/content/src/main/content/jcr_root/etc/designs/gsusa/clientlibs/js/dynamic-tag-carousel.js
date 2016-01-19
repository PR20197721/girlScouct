(function(){
	//const END_POINT = '/content/gsusa/components/article-hub/dynamic-tag-carousel';
	const END_POINT = '/content/gsusa/en/jcr:content/header/logo';

	DynamicTagCarousel = function(id, num) {
		this.id = id;
		this.num = num;
		
		$(document).ready(function(){
		});
	};
	
	console.info('Dynamic Tag Carousel definded.');

	DynamicTagCarousel.prototype.load = function() {
		var hash = window.location.hash;
		hash = hash.indexOf('#') == 0 ? hash.substring(1) : hash;
			
		var selector = '#' + this.id;
		var url = END_POINT + '.' + hash + '.' + this.num + '.html';
		$(selector).load(url);
	};
})();