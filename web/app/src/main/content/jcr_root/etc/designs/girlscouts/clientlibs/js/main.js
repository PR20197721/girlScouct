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