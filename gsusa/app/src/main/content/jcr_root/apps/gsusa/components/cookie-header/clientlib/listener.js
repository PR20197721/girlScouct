(function(){
	try {
		// Disable cookie header for everywhere except homepage.
		$(document).on('cq-editables-loaded cq-editables-loaded cq-editor-loaded cq-layer-activated', function () {
			Granite.author.page.loadPageInfo().then(function(){
				if (Granite.author.page.path != '/content/gsusa/en') {
					for (var i = 0; i < Granite.author.editables.length; i++) {
						var editable = Granite.author.editables[i];
						if(editable.type == 'gsusa/components/cookie-header'){
							// Attempt to disable
							try{
								editable.overlay.setDisabled(true)
							}catch(er1){}
						}
					}
				}
			})
		});
	}catch(er){}
})();

