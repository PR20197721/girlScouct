window.BadgePdfLoadingWidget = (function(){
	
	var overlay = $('<div>').addClass('BadgePdfProgressOverlay').hide().appendTo('body');
	
	var infoContainerOuter = $('<div>').addClass('BadgePdfProgressInfoContainerOuter').hide().appendTo('body');
	var infoContainerInner = $('<div>').addClass('BadgePdfProgressInfoContainerInner').appendTo(infoContainerOuter);
	var messageContainer = $('<div>').addClass('BadgePdfProgressMessageContainer').appendTo(infoContainerInner);
	var loadingBarOuter = $('<div>').addClass('BadgePdfProgressBarOuter').appendTo(infoContainerInner);
	var loadingBarInner = $('<div>').addClass('BadgePdfProgressBarInner').appendTo(loadingBarOuter);
	
	function _updateProgress(progressPercent, message){
		loadingBarInner.css({maxWidth: loadingBarOuter.width() * progressPercent});
		if(message){
			_setMessage(message);
		}
	}
	
	function _setMessage(message){
		messageContainer.text(message);
	}
	
	function _show(){
		overlay.add(infoContainerOuter).show();
	}
	
	function _hide(){
		overlay.add(infoContainerOuter).hide();
	}
	
	return{
		show: _show,
		hide: _hide,
		setMessage: _setMessage,
		updateProgress: _updateProgress
	}
})();

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
		target.empty();
	};
	
	function createPdf(target){
		target = $(target);
		
		BadgePdfLoadingWidget.show();
		BadgePdfLoadingWidget.updateProgress(0.01, 'Collecting Badge Information...');
		
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
			// Create an area for all the images to go in.
			$('.CanvasImageOutputArea').remove();
			$('iframe').attr('data-html2canvas-ignore', true);
			var canvasImageOutputArea = $('<div>').addClass('CanvasImageOutputArea').appendTo('body');
						
			var canvasOutputElements = target.find('.canvasOutputElement');
			
			processCanvasElement(canvasOutputElements, 0, []).then(createPdfFromImages);
		});
	};

	var MARGIN = 0.12; // 0.5"
	function createPdfFromImages(processedImages){
		
		var doc = new jsPDF({format: 'letter', unit: 'in'});
		var headerElements = processedImages.splice(0, 3);
		var totalHeaderHeight = appendHeaderElements(headerElements, doc);
		
		var heightAvailablePerPage = 2.52; // 10.5" - 0.5" for footer margin.
		
		var currentTop = totalHeaderHeight;
		BadgePdfLoadingWidget.updateProgress(1, 'Finalizing...');
		for(var i = 0; i < processedImages.length; i++){
			var imageHeight = (processedImages[i].height / 400);
			if(currentTop + imageHeight > heightAvailablePerPage){
				doc.addPage();
				appendHeaderElements(headerElements, doc);
				currentTop = totalHeaderHeight;
			}
			doc.addImage(processedImages[i].src, 'PNG', MARGIN, currentTop, (processedImages[i].width / 400),  imageHeight);
			currentTop += imageHeight;
		}

		BadgePdfLoadingWidget.hide();
		BadgePdfLoadingWidget.updateProgress(0, '');
		removeBadgeGrid($('.PdfBadgeGridContainer'));
		
		doc.save('BadgesReport.pdf');
	}
	
	function appendHeaderElements(headerElements, doc){
		var currentTop = MARGIN;
		for(var i = 0; i < headerElements.length; i++){
			var elementHeight = (headerElements[i].height / 400);
			doc.addImage(headerElements[i].src, 'PNG', MARGIN, currentTop, (headerElements[i].width / 400), elementHeight);
			currentTop += elementHeight;
		}
		return currentTop;
	}

	// recursive function to process all elements.
	function processCanvasElement(canvasOutputElements, index, images){
		if(index == canvasOutputElements.length){
			return new $.Deferred().resolve(images);
		}
		BadgePdfLoadingWidget.updateProgress(((index + 1) /canvasOutputElements.length ), 'Processing Badge: ' + (index + 1) + ' of ' + canvasOutputElements.length );
		return html2canvas(canvasOutputElements[index], {
			y: $(canvasOutputElements[index]).offset().top - 30,
			dpi: 192,
			letterRendering: true,
			removeContainer: false
		}).then(function(canvas) {
			var width = $(canvasOutputElements[index]).outerWidth(true);
			var height = $(canvasOutputElements[index]).outerHeight(true);
			var image = new Image(width, height);
			image.src = canvas.toDataURL();
			images.push(image);
			$('.CanvasImageOutputArea').append(image);
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