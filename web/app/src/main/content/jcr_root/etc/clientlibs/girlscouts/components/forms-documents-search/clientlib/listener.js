//Girlscouts Forms and Documents search lazy loading code 
//GSWP-1166
function FormsDocsLoader(jsonPath, containerObj, query, tags) {
	var path = jsonPath+".more";
	var q = query;
	var tags = tags;
	var offset = 0;
	var container = containerObj;
	var loader = $("<div>",{"id":"infiniteLoader"}).append($("<p>",{"style":"text-align: center;"}).append("Loading..."));
	var isMore = true;
	var isProcessing = false;
	
	containerObj.after(loader);
	loadMore();
	addLoadMoreButton();
	
	function loadMore(){
		if(isMore && !isProcessing){
			isProcessing = true;
			loader.show();
			var url = path + "?q="+q+"&offset="+ offset;
			if(tags != null && tags.length > 0){
				for (i = 0; i < tags.length; i++) { 
					url += "&tags="+tags[i];
				}
			}
			$.getJSON(url, function (data) {
				try{
					if(parseInt(data.resultCount,10) < 10){
						isMore=false;
						$("#loadMore").remove();
					} else {
						offset = parseInt(data.newOffset, 10);
					}
					$.each(data.results, function (index, result) {
						container.append($("<br/>"));
						try{
							if(result.extension && result.extension!='html'){
								var $span = $("<span>", {"class": "icon type_"+result.extension});
								$span.append("<img src=\"/etc/designs/default/0.gif\" alt=\"*\"/>");
								container.append($span);
							}
						}catch(err){}
						try{
							var newWindow="";
							if(result.extension != "html"){
								newWindow=" target=\"_blank\"";
							}
							container.append("<a href=\""+result.url+"\""+newWindow+">"+result.title+"</a>");
						}catch(err){}
						try{
							if(result.description){
								container.append("<div>"+result.description+"</div>");
							}else{
								container.append("<div>"+result.excerpt+"</div>");
							}
						}catch(err){}
						container.append("<!--"+result.score+"-->");
						container.append($("<br/>"));
					});
				}catch(err){}
				loader.hide();
				isProcessing = false;
			});
		}
	}
	function addLoadMoreButton(){
		var $buttonDiv = $("<div>",{"id":"loadMoreForms"});
		var $buttonPar = $("<p>",{"style":"text-align: center;"});
		var $buttonAnchor = $("<a>",{"class":"button", "style":"padding: 0.6rem 2rem; font-size: 0.95em; font-weight:bold;","href":"javascript:;"});
		$buttonAnchor.click(function(e){
			e.preventDefault();
			loadMore(); 
			bindScroll();
			$("#loadMoreForms").remove();
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
}