window.BadgePdfGenerator = (function(window, $, document){
	
	var defaultElementInitialized = new $.Deferred();
	var pdfBadgeGridContainer;
	var currentVue = null;
	
	function getSelectedBadges(){
		var returner = [];
		$('.badge-grid .badge-block').not('.hide').each(function(){
			returner.push($(this).data('badge-info'));
		});
		return returner;
	};
		
	function removeBadgeGrid(target){
		if(currentVue){
			// Break down in case we add any state data later.
			//currentVue.destroy();
		}
		target.empty();
	};
	
	function createPdf(target){
		target = $(target);
		
		// Clean out any previous runs.
		removeBadgeGrid(target);
		
		// Normalize the description with real html.
		var selectedBadges = [].concat(getSelectedBadges());
		for(var i = 0; i < selectedBadges.length; i++){
			try{
				if(DomUtils.htmlDecode(selectedBadges[i].description)){
					selectedBadges[i].description = DomUtils.htmlDecode(selectedBadges[i].description);
				}
			}catch(err){}
		}
		
		// Create the dom for the PDF.
		var id = 'PdfBadgeGrid_' + Math.floor(Math.random() * Math.floor(1000));
		target.append($('<div>').attr('id', id).addClass('PdfBadgeGridInner').append('<badge-explorer-pdf :badge-data="badgeData">'));
		currentVue = new Vue({
			el: '#' + id,
			data: {
				badgeData: selectedBadges
			}
		});
		
		// Write the elements to the page then resolve the returner after the pdf has been created.
		return Vue.nextTick().then(function(){
//			html2pdf(target[0], {
//				margin: 0.5,
//				filename: 'SelectedBadges.pdf',
//				image: {type: 'jpeg', quality: 0.98},
//				html2canvas: {
//					dpi: 192, 
//					letterRendering: true,
//					imageTimeout: 30000 // 30 seconds.
//				},
//				jsPDF: {unit: 'in', format: 'letter', orientation: 'portrait'},
//				enableLinks: true
//			});
//			html2canvas(target[0]).then(function(canvas) {
//			    document.body.appendChild(canvas);
//			});
			
			// Create an area for all the images to go in.
			$('.CanvasImageOutputArea').remove();
			var canvasImageOutputArea = $('<div>').addClass('CanvasImageOutputArea');
						
			var canvasOutputElements = target.find('.canvasOutputElement');
			
			// recursive function to process all elements.
			processCanvasElement(canvasOutputElements, 0, []).then(function(processedImages){
				var doc = new jsPDF();
				doc.addImage(imgData, 'JPEG', 15, 40, 180, 160)
			});
			
			// Append everything to the body to make sure it all displays.
			// $('body').append(canvasImageOutputArea);
			
			// 
			
		});
	};
	
	function processCanvasElement(canvasOutputElements, index, images){
		if(index == canvasOutputElements.length){
			return new $.Deferred().resolve(images);
		}
		return html2canvas(canvasOutputElements[index], {
			y: $(canvasOutputElements[index]).offset().top - 20
		}).then(function(canvas) {
			var width = $(canvasOutputElements[index]).outerWidth(true);
			var height = $(canvasOutputElements[index]).outerHeight(true);
			var image = new Image(width, height);
			image.src = canvas.toDataURL("image/png");
			images.push[image];
			return processCanvasElement(canvasOutputElements, index +1, images);
		});
	}
	
	function _generateBadgePdf(targetElement){
		// Possible click function is called before initialization.
		
		var initializationPromise;
		if(targetElement != undefined && $(targetElement).length > 0){
			initializationPromise = new $.Deferred().resolve(targetElement);
		}else{
			initializationPromise = defaultElementInitialized;
		}
		if(initializationPromise.state() == 'pending'){
			return initializationPromise.then(createPdf);
		}else{
			return createPdf(targetElement || pdfBadgeGridContainer);
		}
		
	};
	
	function init(){
		pdfBadgeGridContainer = $("<div>").addClass('PdfBadgeGridContainer');
		pdfBadgeGridContainer.appendTo('body');
		defaultElementInitialized.resolve(pdfBadgeGridContainer);
	};

	// Init on dom load.
	$(init);
	
	return {
		generateBadgePdf : _generateBadgePdf
	};
	
})(window, $, document);