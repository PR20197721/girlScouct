$(document).foundation();

var girlscouts = girlscouts || {};
girlscouts.components = girlscouts.components || {};
girlscouts.functions = girlscouts.functions || {};

if (!Date.now) {
	  Date.now = function now() {
	    return new Date().getTime();
	  };
	}

if (!String.prototype.endsWith){
    String.prototype.endsWith = function(suffix) {
        return this.indexOf(suffix, this.length - suffix.length) !== -1;
    };
}

function clearCart(){
    $.removeCookie("event-cart", { path : '/' } );
}

function retrieveEvents(){
    var $eventsCookie;
    if($.cookie("event-cart") != undefined){
        $eventsCookie = JSON.parse($.cookie("event-cart"));
        console.log("Cookie Found");
    }
    else{
        $("#appended-event-cart").html("");
        console.log("No Cookie");
        return 0;
    }
    if($eventsCookie.events != undefined && $eventsCookie.events.length > 0){
        var eventIds = "";
        var navList = "<div id=\"event-cart\"><dl class=\"accordion\" data-accordion><dt data-target=\"drop-down-cart\"><h6 class=\"on\">My Activities</h6></dt><dd class=\"event-cart-navigation\" id=\"drop-down-cart\"><ul id=\"event-cart-nav-list\">";
        for(var i=0; i < $eventsCookie.events.length; i++){
            var nameEscaped = $eventsCookie.events[i][1].replace(new RegExp("'", 'g'), "\\'").replace(new RegExp("\"",'g'),"&quot");
            if(i>0){
                eventIds = eventIds + ",";
            }
            eventIds=eventIds + $eventsCookie.events[i][0];
            navList = navList + "<li><i class=\"icon-cross delete-event\" onclick=\"deleteEvent('" + $eventsCookie.events[i][0] + "', '" + nameEscaped + "', '" + $eventsCookie.events[i][3] + "'); return false\"; /><a href=\"" + $eventsCookie.events[i][2] + "\">" + $eventsCookie.events[i][1] + "</li>";
        }
        navList = navList + "</ul><a class=\"button register-all\" onclick=\"clearCart()\" href=\"" + eventToSalesforce + eventIds + "\">REGISTER</a></dd></dl></div>";
        $("#appended-event-cart").html(navList);
        console.log("Cart loaded");
    }
    else {
        $("#appended-event-cart").html("");
        console.log("Cart empty");
        return 0;
    }   
}

function compareEvents(a, b){
    if( a[1] < b[1] ){
        return -1;
    }
    else if( a[1] == b[1] ){
        return 0;
    }
    else {
        return 1;
    }
}

function addToCart(name, eventID, href, register){
    if(eventID == -1){
        console.log("This event has an invalid event ID");
        return -1;
    }
    var $eventsCookie;
    var hrefParsed = href.endsWith(".html") ? href : href + ".html";
    var nameTrimmed = name.trim();
    var nameEscaped = nameTrimmed.replace(new RegExp("'", 'g'), "\\'").replace(new RegExp("\"",'g'),"&quot");
    if($.cookie("event-cart") != undefined){
        $eventsCookie = JSON.parse($.cookie("event-cart"));
    }else{
        $eventsCookie = { events : [[eventID + "", nameTrimmed, hrefParsed, register]] };
        console.log("Event added to new cart");
        $.cookie("event-cart", JSON.stringify($eventsCookie), {expires: 7, path : "/"});
        var navList = "<div id=\"event-cart\"><dl class=\"accordion\" data-accordion><dt data-target=\"drop-down-cart\"><h6 class=\"on\">My Activities</h6></dt><dd class=\"event-cart-navigation\" id=\"drop-down-cart\"><ul id=\"event-cart-nav-list\">";
        for(var i=0; i < $eventsCookie.events.length; i++){
            navList = navList + "<li><i class=\"icon-cross delete-event\" onclick=\"deleteEvent('" + $eventsCookie.events[i][0] + "', '" + nameEscaped + "', '" + register + "'); return false\"; /><a href=\"" + $eventsCookie.events[i][2] + "\">" + $eventsCookie.events[i][1] + "</li>";
        }
        navList = navList + "</ul><a class=\"button register-all\" onclick=\"clearCart()\" href=\"" + register + "\">REGISTER</a></dd></dl></div>";
        $("#appended-event-cart").html(navList);
        vtk_accordion();
        return 0;
    }
    for(var i = 0; i < $eventsCookie.events.length; i++){
        if($eventsCookie.events[i][0] == eventID + ""){
            console.log("An event with this ID is already in cart");
            return 0;
        }
        /*if($eventsCookie.events[i][1] == nameTrimmed){
            console.log("An event with this name is already in cart");
            return 0;
        }*/
    }
    var eventIds = "";
    $eventsCookie.events.push([eventID + "", nameTrimmed, hrefParsed]);
    $eventsCookie.events.sort(compareEvents);
    $.cookie("event-cart", JSON.stringify($eventsCookie), {expires: 7, path : "/"});
    console.log("Event added to cart");
    var navList = "<div id=\"event-cart\"><dl class=\"accordion\" data-accordion><dt data-target=\"drop-down-cart\"><h6 class=\"on\">My Activities</h6></dt><dd class=\"event-cart-navigation\" id=\"drop-down-cart\"><ul id=\"event-cart-nav-list\">";
    for(var i=0; i < $eventsCookie.events.length; i++){
        var nameEscaped2 = $eventsCookie.events[i][1].replace(new RegExp("'", 'g'), "\\'").replace(new RegExp("\"",'g'),"&quot");
        if(i>0){
            eventIds = eventIds + ",";
        }
        eventIds=eventIds + $eventsCookie.events[i][0];
        navList = navList + "<li><i class=\"icon-cross delete-event\" onclick=\"deleteEvent('" + $eventsCookie.events[i][0] + "', '" + nameEscaped2 + "', '" + register + "'); return false\"; /><a href=\"" + $eventsCookie.events[i][2] + "\">" + $eventsCookie.events[i][1] + "</li>";
    }
    navList = navList + "</ul><a class=\"button register-all\" onclick=\"clearCart()\" href=\"" + register + "\">REGISTER</a></dd></dl></div>";
    $("#appended-event-cart").html(navList);
    vtk_accordion();
    return 0;
}


function deleteEvent(eventID, name, register){
    var nameTrimmed = name.trim();
    var $eventsCookie;
    if($.cookie("event-cart") != undefined){
        var eventIds = "";
        $eventsCookie = JSON.parse($.cookie("event-cart"));
    }else{
        console.log("Cookie error - delete");
        return -1;
    }
    for(var i = 0; i < $eventsCookie.events.length; i++){
        if($eventsCookie.events[i][0] == eventID && $eventsCookie.events[i][1] == nameTrimmed){
            $eventsCookie.events.splice(i,1);
            console.log("Event removed");
            if($eventsCookie.events != undefined && $eventsCookie.events.length > 0){
                var navList = "<div id=\"event-cart\"><dl class=\"accordion\" data-accordion><dt data-target=\"drop-down-cart\"><h6 class=\"on\">My Activities</h6></dt><dd class=\"event-cart-navigation\" id=\"drop-down-cart\"><ul id=\"event-cart-nav-list\">";
                for(var i=0; i < $eventsCookie.events.length; i++){
                    var nameEscaped = $eventsCookie.events[i][1].replace(new RegExp("'", 'g'), "\\'").replace(new RegExp("\"",'g'),"&quot");
                    if(i>0){
                        eventIds = eventIds + ",";
                    }
                    eventIds=eventIds + $eventsCookie.events[i][0];
                    navList = navList + "<li><i class=\"icon-cross delete-event\" onclick=\"deleteEvent('" + $eventsCookie.events[i][0] + "', '" + nameEscaped + "', '" + register + "'); return false\"; /><a href=\"" + $eventsCookie.events[i][2] + "\">" + $eventsCookie.events[i][1] + "</li>";
                }
                navList = navList + "</ul><a class=\"button register-all\" onclick=\"clearCart()\" href=\"" + eventToSalesforce + eventIds + "\">REGISTER</a></dd></dl></div>";
                $("#appended-event-cart").html(navList);
                vtk_accordion();
                console.log("Cart loaded");
            }
            else {
                $("#appended-event-cart").html("");
                console.log("Cart empty");
            }
            $.cookie("event-cart", JSON.stringify($eventsCookie), {expires: 7, path : "/"});
            return 0;
        }
    }
    console.log("Couldn't find the event to delete");
    return -1;
}




function toggleParsys(s)
{
    var componentPath = s;

    this.toggle = function()
    {
    	if (componentPath)
        {
    		var parsysComp = CQ.WCM.getEditable(componentPath);

    		if(parsysComp.hidden == true){
    			parsysComp.show();
    		}
    		else{
    			parsysComp.hide();
    		}
        }
    };

    this.hideParsys = function()
    {
        if (componentPath)
        {
            var parsysComp = CQ.WCM.getEditable(componentPath);

            if (parsysComp)
            {
                parsysComp.hide();
            }
        }
    };

    this.showParsys = function()
    {
        if (componentPath)
        {
            var parsysComp = CQ.WCM.getEditable(componentPath);

            if (parsysComp)
            {
                parsysComp.show();
            }
        }
    };

    return this;
};

//Girlscouts Event List lazy loading code 
//GSWP-1173
function EventLoader(jsonPath, containerObj, loaderObj) {
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

$(document).ready(function() {
	$("input:hidden[name='file-upload-max-size']").each(function(index) {
		var maxSize = parseInt($(this).val(), 10);
		if(maxSize > -1){
			$(this).closest("form").find("input:file").change(function() {
				if(typeof this.files[0] !== 'undefined'){
		            if(maxSize > -1){
		                size = this.files[0].size;
		                if(maxSize * 1000000 < size){
		                	alert("File size cannot be larger than "+maxSize+" MB.");
		                	$(this).val('');
		                }
					}
				}
			});
		}
	});
});