window.BadgePdfLoadingWidget = (function(){

	var overlay = $('<div>').addClass('BadgePdfProgressOverlay').hide().appendTo('body');

	var infoContainerOuter = $('<div>').addClass('BadgePdfProgressInfoContainerOuter').hide().appendTo('body');
	var infoContainerInner = $('<div>').addClass('BadgePdfProgressInfoContainerInner').appendTo(infoContainerOuter);
	var messageContainer = $('<div>').addClass('BadgePdfProgressMessageContainer').appendTo(infoContainerInner);
	var loadingBarOuter = $('<div>').addClass('BadgePdfProgressBarOuter').appendTo(infoContainerInner);
	var loadingBarInner = $('<div>').addClass('BadgePdfProgressBarInner').appendTo(loadingBarOuter);
	var allowSet = true;

	function _updateProgress(progressPercent, message){
		if(progressPercent == 0){
			var previousDuration = loadingBarInner.css('transition-duration');
			loadingBarInner.css({transitionDuration : '0.0s'});
			allowSet = false;
			window.setTimeout(function(){
				loadingBarInner.css({maxWidth: loadingBarOuter.width() * progressPercent});
			}, 5);
			window.setTimeout(function(){
				loadingBarInner.css({transitionDuration : previousDuration});
				allowSet = true;
			}, 10);
		}else if(allowSet){
			loadingBarInner.css({maxWidth: loadingBarOuter.width() * progressPercent});
		}
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
	};

})();

window.BadgePdfGenerator = (function(window, $, document){

	function getBadgePdfItext(pdfHtml){
		var xhr = new XMLHttpRequest();
		xhr.open('POST', '/bin/pdf/generate_badge_report', true);
		xhr.responseType = 'arraybuffer';

		BadgePdfLoadingWidget.updateProgress(0, 'Downloading...');
		var returner = new Promise(function(res, rej) {
			xhr.onload = function () {
				if (this.status === 200) {
					var filename = "BadgeReport";
					BadgePdfLoadingWidget.updateProgress(1, 'Ready for Save');
					var type = "octet/stream";
					// var type = xhr.getResponseHeader('Content-Type');

					var blob;
					try{
						blob = new File([this.response], filename, {type: type})
					}catch(err){
						// IE / Safari dont' like creating files.
						blob = new Blob([this.response], {type: type});
					}
					if (typeof window.navigator.msSaveBlob !== 'undefined') {
						// IE workaround for "HTML7007: One or more blob URLs were revoked by closing the blob for which they were created. These URLs will no longer resolve as the data backing the URL has been freed."
						window.navigator.msSaveBlob(blob, filename + '.pdf');
					} else {
						var URL = window.URL || window.webkitURL;
						var downloadUrl = URL.createObjectURL(blob);

						// use HTML5 a[download] attribute to specify filename
						var a = document.createElement("a");
						document.body.appendChild(a);
						a.style = "display: none";

						// Prevent the link click handler from taking over.
						a.onclick = function(e){e.stopPropagation();};
						a.href = downloadUrl;
						a.download = "badge-report.pdf";
						a.click();

						setTimeout(function () {
							URL.revokeObjectURL(downloadUrl);
						}, 300); // cleanup
					}
					res();
				}else{
					rej();
				}
			};
		});
		xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');

		xhr.upload.addEventListener("progress", function(oEvent){
			if (oEvent.lengthComputable) {
				console.log('Upload Progress: ' + (oEvent.loaded / oEvent.total * 100));
				BadgePdfLoadingWidget.updateProgress(((oEvent.loaded / oEvent.total) * 0.75), 'Downloading...');
			}
		});
		xhr.addEventListener("progress", function(oEvent){
			if (oEvent.lengthComputable) {
				console.log('Download Progress: ' + ( oEvent.loaded / oEvent.total * 100));
				BadgePdfLoadingWidget.updateProgress(((oEvent.loaded / oEvent.total) * 0.75) + 0.75, 'Downloading...');
			}
		});

		xhr.send($.param({html: pdfHtml}));

		return returner;
	}

	function _generateBadgePdf(targetElement){
		// Possible click function is called before initialization.
		var target;
		if(targetElement && $(targetElement).length > 0){
			target = $(targetElement);
		}else{
			// Remove existing.
			$('.PdfBadgeGridContainer').remove();
			target = $("<div>").addClass('PdfBadgeGridContainer');
			target.appendTo('body');
		}

		BadgePdfLoadingWidget.updateProgress(0.1, 'Starting...');
		BadgePdfLoadingWidget.show();
		BadgePdfLoadingWidget.updateProgress(0.3, 'Starting...');

		window.setTimeout(function(){

			// Clean out any previous runs.
			target.empty();

			// Normalize the description with real html.
			var selectedBadges = [].concat(getSelectedBadges());

			// Create the dom for the PDF.
			var id = 'PdfBadgeGrid_' + Math.floor(Math.random() * Math.floor(1000));
			target.append($('<div>').attr('id', id).addClass('PdfBadgeGridInner').append('<badge-explorer-pdf :badge-data="badgeData">'));
			new Vue({
				el: '#' + id,
				data: {
					badgeData: selectedBadges.reverse()
				}
			});

			// Write the elements to the page then resolve the returner after the pdf has been created.
			return Vue.nextTick().then(function(){

				// Make all the relative links absolute.
				$('.PdfBadgeGridContainer').find('a').each(function(){
					if($(this).attr('href').indexOf('/') === 0){
						$(this).attr('href', window.location.origin + $(this).attr('href'));
					}
				});

				// Wrap in a div so the innerHTML contains everything.
				var pdfHtmlWrapper = $('.PdfBadgeGridContainer').wrap('<div>').parent();
				var stylerPromise = pdfHtmlWrapper.inlineStyler();

				window.setTimeout(function(){

					// Inline all styles for use server-side.
					stylerPromise.then(function(){
						// Insert Page Breaks & Headers.
						separatePages(pdfHtmlWrapper);
						BadgePdfLoadingWidget.updateProgress(0.9, 'Formatting PDF...');

						window.setTimeout(function(){
							// Fix some mistakes by inliner.
							pdfHtmlWrapper.find('.BadgeExplorerElementPdfComponent, .BadgePdfHeader').css({display: 'block', borderBottom: '2px solid #bbb'});
							pdfHtmlWrapper.find('.BadgePdfDescriptionColumn').css({float: 'right'});
							pdfHtmlWrapper.find('.BadgePdfImageColumn').css({float: 'left'});
							pdfHtmlWrapper.find('.BadgePdfDescriptionColumn, .BadgePdfImageColumn').css({display: 'block'});
							pdfHtmlWrapper.find('.BadgeExplorerPdfComponent').css('height', 'auto').parent().css('height', 'auto').parent().css('height', 'auto');
							pdfHtmlWrapper.find('.PdfBadgeGridContainer').css('width', '720px');

							var badgeHtml = pdfHtmlWrapper[0].innerHTML;
							pdfHtmlWrapper.remove();

							getBadgePdfItext(badgeHtml).catch(function(){
								alert('Unable to create PDF.')
							}).then(function(){
								window.setTimeout(function(){
									BadgePdfLoadingWidget.updateProgress(0, '');
									BadgePdfLoadingWidget.hide();
								}, 1000);
							});
						}, 10);
					});

				}, 10);
				BadgePdfLoadingWidget.updateProgress(0.4, 'Fetching Badges...');
			});
			BadgePdfLoadingWidget.updateProgress(0.2, 'Fetching Badges...');
		}, 20);
	};

	/*
	 * Separates pages to fit in a standard PDF.  Pages are allowed max of 840px not including header.
	 */
	var MAX_PDF_PAGE_HEIGHT = 840;
	function separatePages(source){
		var allElements = source.find('.BadgeExplorerElementPdfComponent');
		var headerDom = $('.BadgePdfLogoWrapper').add('.BadgePdfHeaderTitle').add('.BadgePdfHeader');

		var runningTotalHeight = 0, pageNumber = 0;
		var elementHeight;
		for(var i = 0; i < allElements.length; i++){
			elementHeight =  $(allElements[i]).outerHeight(true);
			if(runningTotalHeight + elementHeight > MAX_PDF_PAGE_HEIGHT){
				var previousElement = $(allElements[i-1]);
				$('<gs-custom-new-page/>').attr('page-number', ++pageNumber).insertAfter(previousElement);
				var headerDomClone = headerDom.clone(true);
				headerDomClone.insertAfter(previousElement.next());

				runningTotalHeight = elementHeight;
			}else{
				runningTotalHeight += elementHeight;
			}
		}
	}

	var currentSelectedBadges = [];
	function getSelectedBadges(){
		return currentSelectedBadges;
	};

	function _setSelectedBadges(newSelectedBadges){
		currentSelectedBadges = newSelectedBadges;
	}

	/*
	 * Only expose method to generate badge pdf.  Other functions are inter-reliant.
	 */
	return {
		generateBadgePdf : _generateBadgePdf,
		setSelectedBadges : _setSelectedBadges
	};

})(window, $, document);
