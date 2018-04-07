//Girlscouts Event List lazy loading code 
//GSWP-1173
function EventLoader(jsonPath, containerObj) {
  	var d = new Date();
  	var path = jsonPath+".more."+(d.getMonth()+1)+d.getDate()+".";
  	var eventsOffset = 0;
  	var monthYearLabel = "";
  	var container = containerObj;
  	var loader = $("<div>",{"id":"infiniteLoader"}).append($("<p>",{"style":"text-align: center;"}).append("Loading..."));
  	var isMore = true;
  	var isProcessing = false;
  	
  	containerObj.after(loader);
  	loadMoreEvents();
  	addLoadMoreButton();
  	
  	function loadMoreEvents(){
  		if(isMore && !isProcessing){
  			isProcessing = true;
  			loader.show();
  			$.getJSON(path+eventsOffset+".html", function (data) {
  				try{
  					if(parseInt(data.resultCount,10) < 10){
  						isMore=false;
  					} else {
  						eventsOffset = parseInt(data.newOffset, 10);
  					}
  					$.each(data.results, function (index, result) {
  						try{
  							if(monthYearLabel != result.monthYearLabel){
  								monthYearLabel = result.monthYearLabel;
  								container.append("<div class=\"eventsList monthSection\"><div class=\"leftCol\"><b>"+monthYearLabel.toUpperCase()+"</b></div><div class=\"rightCol horizontalRule\">&nbsp;</div></div><br/><br/>");
  							}
  						}catch(err){}
  						container.append(getEventContent(result));
  						container.append($("<div>", {"class": "eventsList bottomPadding"}));
  					});
  				}catch(err){}
  				loader.hide();
  				isProcessing = false;
  			});
  			
  		}
  	}
  	
  	function addLoadMoreButton(){
  		var $buttonDiv = $("<div>",{"id":"loadMoreEvents"});
  		var $buttonPar = $("<p>",{"style":"text-align: center;"});
  		var $buttonAnchor = $("<a>",{"class":"button", "style":"padding: 0.6rem 2rem; font-size: 0.95em; font-weight:bold;","href":"javascript:;"});
  		$buttonAnchor.click(function(e){
  			e.preventDefault();
  			loadMoreEvents(); 
  			bindScroll();
  			$("#loadMoreEvents").remove();
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
 	  				loadMoreEvents();
 	  			}	  			
  			});
        }else{
            $(window).on('scroll', function(){
                var hT = container.offset().top,
                   hH = container.outerHeight(),
                   wH = $(window).height(),
                   wS = $(this).scrollTop();
                if(wS > (hT+hH-wH)){
                    loadMoreEvents();
                }
            });
        }
  	}
  	
  	function getEventContent(event){
  		var $eventDiv = $("<div>", {"class": "eventsList eventSection","itemtype":"http://schema.org/ItemList"});
  		$eventDiv.append(getEventImage(event));
  		var $rightColDiv = $("<div>", {"class": "rightCol"});
  		$rightColDiv.append(getEventTitle(event));
  		$rightColDiv.append(getEventMembershipRequired(event));
  		$rightColDiv.append(getEventDate(event));
  		$rightColDiv.append(getEventRegion(event));
  		$rightColDiv.append(getEventLocation(event));
  		$rightColDiv.append(getEventDescription(event));
  		$rightColDiv.append(getEventRegistration(event));
  		$eventDiv.append($rightColDiv);
  	return $eventDiv;
  	}
  	
  	function getEventImage(event){
  		try{
  			if(event.imgPath){
  				var $imgDiv = $("<div>", {"class": "leftCol", "itemprop":"image"});
  				$imgDiv.append("<img src=\""+event.imgPath+"\"/>");
  				return $imgDiv;
  			}else{
  				return "";
  			}
  		}catch(err){}
  	}
  	
  	function getEventTitle(event){
  		try{
  			return "<h6><a class=\"bold\" href=\""+event.path+".html\" itemprop=\"name\">"+event.jcr_title+"</a>";
  		}catch(err){}
  	}
  	
  	function getEventMembershipRequired(event){
  		try{
  			if(event.memberOnly && memberOnly == 'true'){
  				return "<p class=\"bold\">MEMBERSHIP REQUIRED</p>";
  			}else{
  				return "";
  			}
  		}catch(err){}
  	}
  	
  	function getEventDate(event){
  		try{
  			if(event.formattedStartDate != undefined){
  				var $p = $("<p>", {"class":"bold"});
  				$p.append("Date: ");
  				$p.append("<span itemprop=\"startDate\" itemscope=\"\" itemtype=\"http://schema.org/Event\" content=\""+event.utfStartDate+"\">"+event.formattedStartDate+"</span>");
  				if(event.formattedEndDate != undefined){
  					$p.append("<span itemprop=\"stopDate\" itemscope=\"\" itemtype=\"http://schema.org/Event\" content=\""+event.utfEndDate+"\">"+event.formattedEndDate+"</span>");
  				}
  				return $p;
  			}
  		}catch(err){}
  	}
  	
  	function getEventRegion(event){
  		try{
  			if(event.region){
  				var $p = $("<p>", {"class":"bold", "itemprop":"region", "itemscope":"", "itemptype":"http://schema.org/Place"});
  				$p.append("Region: ");
  				$p.append("<span itempropr=\"name\">"+event.region+"</span>");
  				return $p;
  			}else{
  				return "";
  			}
  		}catch(err){}
  	}
  	
  	function getEventLocation(event){
  		try{
  			if(event.locationLabel){
  				var $p = $("<p>", {"class":"bold", "itemprop":"location", "itemscope":"", "itemptype":"http://schema.org/Place"});
  				$p.append("Location: ");
  				$p.append("<span itemprop=\"name\">"+event.locationLabel+"</span>");
  				return $p;
  			}else{
  				return "";
  			}
  		}catch(err){}
  	}
  	
  	function getEventDescription(event){
  		try{
  			if(event.srchdisp){
  				var $p = $("<p>", {"itemprop":"description"});
  				$p.append(event.srchdisp);
  				return $p;
  			}else{
  				return "";
  			}
  		}catch(err){}
  	}
  	
  	function getEventRegistration(event){
  		try{
  			if(event.includeCart == true){
  				var eid = -1;
  				var title = "";
  				if(event.eid){
  					eid = event.eid;
  				}
  				var $div = $("<div>", {"class":"eventDetailsRegisterLink"});
  				if(event.registerLink){
  					var $registerLink =  $("<a>", {"href":event.registerLink}).append("Register Now");
  					$div.append($registerLink);
  				}
  				title = event.jcr_title;
  				title = title.replace(/"\""/g, "&quot");
  				title = title.replace(/"\'"/g, "\\\\'");
  				var addToCartFunc = "addToCart('"+title+"','"+eid+"','"+event.path+".html', '" + event.registerLink + "'); return false;";
  				var $addToCartLink =  $("<a>", {"onclick":addToCartFunc}).append("Add to MyActivities");
  				$div.append($addToCartLink);
  				return $div;
  			}
  		}catch(err){}
  	}
  	
  }

