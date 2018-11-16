(function (document, $, ns) {
	//window.top.Granite.author.ContentFrame.postMessage('cq-contentframe-layout-change', null, -1);
	window.top.Granite.author.EditorFrame.container.trigger('cq-contentframe-layout-change');
})(jQuery, Granite.author, jQuery(document));

