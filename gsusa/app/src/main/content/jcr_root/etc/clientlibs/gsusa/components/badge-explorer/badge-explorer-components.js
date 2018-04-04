(function(){
	
	Vue.component('badge-explorer-pdf', {
		template: '#badge-explorer-pdf-template',
		props: ['badge-data']
	});
	
	Vue.component('badge-explorer-element-pdf', {
		template: '#badge-explorer-element-pdf-template',
		props: ['title', 'rank', 'image', 'description', 'link', 'other-tags']
	});
	
})();