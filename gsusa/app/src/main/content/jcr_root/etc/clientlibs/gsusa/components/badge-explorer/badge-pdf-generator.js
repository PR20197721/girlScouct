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
			window.requestAnimationFrame(function(){
				loadingBarInner.css({maxWidth: loadingBarOuter.width() * progressPercent});
				window.requestAnimationFrame(function(){
					loadingBarInner.css({transitionDuration : previousDuration});
					allowSet = true;
				});
			});
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
		xhr.open('POST', '/etc/servlets/pdf/badge-report', true);
		xhr.responseType = 'arraybuffer';

		BadgePdfLoadingWidget.updateProgress(0, 'Downloading...');
		var returner = new Promise(function(res, rej) {
			xhr.onload = function () {
				if (this.status === 200) {
					var filename = "BadgeReport";
					BadgePdfLoadingWidget.updateProgress(1, 'Ready for Save');
					var type = "application/pdf";

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

						var buttonContainer = $('<div>')
							.addClass('BadgePdfProgressButtonContainer');

						var openButton = $('<a>')
							.addClass('BadgePdfProgressButton')
							.addClass('BadgePdfOpenButton')
							.attr('target', '_blank')
							.on('click.pdfOpen', function(){
								buttonContainer.remove();
								BadgePdfLoadingWidget.hide();
								window.open(downloadUrl);
							})
							.text('Open');

                        //selecting this creates a prompt that asks for a the user to name the file
                        //after the user names the file, it creates then clicks an <a> element
                        //that has the download information
                        var outerSaveButton = $('<a>')
                            .addClass('BadgePdfProgressButton')
                            .addClass('BadgePdfSaveButton')
                            .on('click.pdfOuterSave', function(){
                                var dname = prompt('Please enter a filename', filename + ".pdf");
                                if (dname != null){

                                if (!dname.endsWith(".pdf")){
                                        dname+=".pdf";
                                    }
                                    var innerSaveButton = $('<a>').attr('download',dname).attr('href', downloadUrl);
                                    buttonContainer.append(innerSaveButton);
                                    innerSaveButton[0].click();
                                    buttonContainer.remove();
                                    BadgePdfLoadingWidget.hide();
                                }
                            })
                            .text('Save As');

						var cancelButton = $('<a>')
							.addClass('BadgePdfProgressButton')
							.addClass('BadgePdfCancelButton')
							.attr('href', 'javascript: void(0);')
							.text('Cancel')
							.on('click.pdfCancel', function(){
								buttonContainer.remove();
								BadgePdfLoadingWidget.hide();
							});

						buttonContainer.append(openButton.add(outerSaveButton).add(cancelButton));

						$('.BadgePdfProgressInfoContainerInner').append(buttonContainer);

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

					// Unwarp the iTags
					pdfHtmlWrapper.find('i').each(function() {
						$(this).replaceWith(this.childNodes);
					});

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
							pdfHtmlWrapper.find('.PdfBadgeGridContainer').css('width', '700px');
							pdfHtmlWrapper.find('b').each(function() {
								$(this).replaceWith(this.childNodes);
							});

							var badgeHtml = pdfHtmlWrapper[0].innerHTML;
							pdfHtmlWrapper.remove();

							getBadgePdfItext(badgeHtml).catch(function(){
								alert('Unable to create PDF.')
							});
						}, 10);
					});

				}, 10);
				BadgePdfLoadingWidget.updateProgress(0.4, 'Fetching Badges...');
			});
		}, 20);
	};

	/*
	 * Separates pages to fit in a standard PDF.  Pages are allowed max of 840px not including header.
	 */
	var MAX_PDF_PAGE_HEIGHT = 825;
	function separatePages(source){
		var allElements = source.find('.BadgeExplorerElementPdfComponent');
		var headerDom = $('.BadgePdfLogoWrapper').add('.BadgePdfHeaderTitle').add('.BadgePdfHeader');

		var runningTotalHeight = 0, pageNumber = 0;
		var elementHeight;
		for(var i = 0; i < allElements.length; i++){
			elementHeight =  $(allElements[i]).outerHeight(true);
			if(runningTotalHeight + elementHeight > MAX_PDF_PAGE_HEIGHT){
				var previousElement = $(allElements[i-1]);

				var pageNumberElement = $('<div>').addClass('PDFPageNumber').css({
					'min-height': '1em',
					'position': 'relative'
				});
				pageNumberElement
					.append($('<div>').addClass('PDFPageNumberWrapper').css({
						'padding-top': (MAX_PDF_PAGE_HEIGHT - runningTotalHeight) + 'px',
						'padding-left' : '630px',
						bottom: 0,
						right: 0
					})
						.append($('<span>').addClass('PDFCurrentPageNumber').text(++pageNumber))
						.append($('<span>').addClass('PDFTotalPageNumber')));
				pageNumberElement.insertAfter(previousElement);
				$('<gs-custom-new-page/>').attr('page-number', pageNumber).insertAfter(pageNumberElement);
				var headerDomClone = headerDom.clone(true);
				headerDomClone.insertAfter(previousElement.next().next());

				runningTotalHeight = elementHeight;
			}else{
				runningTotalHeight += elementHeight;
			}
		}
		var pageNumberElement = $('<div>').addClass('PDFPageNumber').css({
			'min-height': '1em',
			'position': 'relative'
		});
		pageNumberElement
			.append($('<div>').addClass('PDFPageNumberWrapper').css({
				bottom: 0,
				'padding-top': (MAX_PDF_PAGE_HEIGHT - runningTotalHeight) + 'px',
				'padding-left' : '630px',
				right: 0
			})
				.append($('<span>').addClass('PDFCurrentPageNumber').text(++pageNumber))
				.append($('<span>').addClass('PDFTotalPageNumber')));
		pageNumberElement.insertAfter($(allElements[allElements.length - 1]));

		source.find('.PDFTotalPageNumber').text(' of ' + pageNumber);
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
