/**
 * Extension to the standard dropdown/select component. It enabled hidding/unhidding of other components based on the
 * selection made in the dropdown/select.
 *
 * How to use:
 *
 * - add the class cq-dialog-dropdown-showhide to the dropdown/select element
 * - add the data attribute cq-dialog-dropdown-showhide-target to the dropdown/select element, value should be the
 *   selector, usually a specific class name, to find all possible target elements that can be shown/hidden.
 * - add the target class to each target component that can be shown/hidden
 * - add the class hidden to each target component to make them initially hidden
 * - add the attribute showhidetargetvalue to each target component, the value should equal the value of the select
 *   option that will unhide this element.
 */
(function (document, $, ns) {
    "use strict";


    $(document).on("dialog-success", function() {
		var $resourceType = $("coral-dialog-content").find("[name='./sling:resourceType']").val();
        if("girlscouts/components/news-list" == $resourceType){
        	window.location.reload(false);
        }

	});




})(document, Granite.$);
//Girlscouts News List lazy loading code 
//GSWP-1212
function NewsLoader(jsonPath, containerObj, renderedFeatureNews) {
	var d = new Date();
	var path = jsonPath+".more."+(d.getMonth()+1)+d.getDate()+".";
	var newssOffset = 0;
	var monthYearLabel = "";
	var container = containerObj;
	var loader = $("<div>",{"id":"infiniteLoader"}).append($("<p>",{"style":"text-align: center;"}).append("Loading..."));
	var isMore = true;
	var isProcessing = false;
	var newsCount = renderedFeatureNews;
	
	containerObj.on("child_added", function(){
		setReadMore();
	});
	containerObj.after(loader);
	loadMore();
	addLoadMoreButton();
	
	function loadMore(){
		if(isMore && !isProcessing){
			isProcessing = true;
			loader.show();
			$.getJSON(path+newssOffset+".html", function (data) {
				try{
					if(parseInt(data.resultCount,10) < 10){
						isMore=false;
					} else {
						newssOffset = parseInt(data.newOffset, 10);
					}
					$.each(data.results, function (index, result) {
						try{
							containerObj.append(getNewsLi(result));
							newsCount++;
						}catch(e){};
					});
				}catch(err){}
				loader.hide();
				isProcessing = false;
				containerObj.trigger('child_added');
				if(newsCount == 0){
					containerObj.replaceWith("<div class=\"row\"><div class=\"small-24 large-24 medium-24 columns\"><h4>News Component Empty:</h4><h5>No News Available</h5></div></div>");
				}
			});
			
		}
	}
	
	function addLoadMoreButton(){
		var $buttonDiv = $("<div>",{"id":"loadMoreNews"});
		var $buttonPar = $("<p>",{"style":"text-align: center;"});
		var $buttonAnchor = $("<a>",{"class":"button", "style":"padding: 0.6rem 2rem; font-size: 0.95em; font-weight:bold;","href":"javascript:;"});
		$buttonAnchor.click(function(e){
			e.preventDefault();
			loadMore(); 
			bindScroll();
			$("#loadMoreNews").remove();
		});
		$buttonAnchor.append("LOAD MORE");
		$buttonPar.append($buttonAnchor);
		$buttonDiv.append($buttonPar);
		container.after($buttonDiv);
	}
	
	function bindScroll(){
  		var $parent = $(window.top);
  		var $contentScrollView;
  		try{
  			$contentScrollView= $parent["0"].Granite.author.ContentFrame.scrollView;
  		}catch(err){
  			
  		}
        if($contentScrollView){
             console.log($contentScrollView);
             $contentScrollView.scroll( function(){
            	 var scrollTop = $contentScrollView.scrollTop();
            	 var innerHeight = $contentScrollView.innerHeight();
            	 var scrollHeight = $contentScrollView[0].scrollHeight;
            	 console.log((scrollTop+innerHeight)+">="+scrollHeight+((scrollTop+innerHeight) >=scrollHeight));
            	 if((scrollTop+innerHeight) >=scrollHeight){
 	  				loadMore();
 	  			}	  			
  			});
        }else{
            $(window).on('scroll', function(){
                var hT = container.offset().top,
                   hH = container.outerHeight(),
                   wH = $(window).height(),
                   wS = $(this).scrollTop();
                if(wS > (hT+hH-wH)){
                    loadMore();
                }
            });
        }
  	}
	
	function getNewsLi(result){
		try{
			var $newsLi = $("<li>", {"itemtype":"http://schema.org/ListItem","itemscope":"", "itemprop":"itemListElement"});
			var $a = $("<a>", {"itemprop":"name"}).append(result.title);
			if(result.externalUrl != null && result.externalUrl.length > 0){
				$a.attr("href",result.externalUrl);
				$a.attr("target","_blank");
			}else{
				$a.attr("href",result.url);
				$a.attr("target","_self");
			}
			$newsLi.append($("<h2>").append($a));
			if(result.date != null && result.date.length > 0){
				var $date = $("<span>",{"itemprop":"datePublished", "content":result.date}).append(result.date);
				$newsLi.append($date.append("<br/>"));
			}
			var $article = $("<article>",{"class":"newsArticle","itemprop":"description"}).append(result.text);
			$newsLi.append($article);
			return $newsLi;
		}catch(e){}
	}
	
	function setReadMore(){
		try{
			var p =$(".searchResultsList article p").first();
			var lineHeight = Number($(p).css("line-height").match(/\d+/g)[0]); // Match digits within string (ignore "px")
			$(".searchResultsList article").readmore({
				speed: 75,
				maxHeight: lineHeight * 6, // line-height of content * 6 visible lines
				heightMargin: 16,
				moreLink: '<a href="#">Read more</a>',
				lessLink: '<a href="#">Close</a>',
				embedCSS: true,
				sectionCSS: 'display: block; width: 100%;',
				expandedClass: 'readmore-js-expanded',
				collapsedClass: 'readmore-js-collapsed'
			});
		}catch(e){
			
		}
	}
}