(function(){
	var END_POINT = '/content/gsusa/en/components/article-hub/dynamic-tag-carousel';

	DynamicTagCarousel = function(id, num, defaultTag) {
		this.id = id;
		this.num = num;
		this.defaultTag = defaultTag;
	};

	DynamicTagCarousel.prototype.load = function() {
		var hash = window.location.hash;

		if (hash) {
			hash = hash.indexOf('#') == 0 ? hash.substring(1) : hash;
		} else {
			hash = "";
		}

		var result = hash.split('$$$');
		var tags = hash && result.length >= 1 ? result[0] : this.defaultTag;

		var selector = '#' + this.id;
		var url = END_POINT + '.' + tags + '.' + this.num + '.html';

		// After Load the content to the selector trigger the callback
		//  in this case going to be the article style in app.js
		$(selector).load(url,article_tiles);
	};
})();