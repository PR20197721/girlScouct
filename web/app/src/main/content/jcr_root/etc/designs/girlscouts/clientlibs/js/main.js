$(document).foundation();

var girlscouts = girlscouts || {};
girlscouts.components = girlscouts.components || {};
girlscouts.functions = girlscouts.functions || {};

if (!Date.now) {
	  Date.now = function now() {
	    return new Date().getTime();
	  };
	}

function retrieveEvents(path){
	$.ajax({
		type: "POST",
   		url: path,
        data: { action: "update" },
        success: function(data){
        	var json = JSON.parse(data);
        	console.log("Response: " + json.output);
        	if(json.data != undefined && json.data.length > 0){
	        	var navList = "<dl class=\"accordion\" data-accordion><dt data-target=\"drop-down-cart\"><h6 class = \"on\">My Events</h6></dt><dd class=\"event-cart-navigation\"><ul id=\"event-cart-nav-list\">";
	        	for(var i=0; i < json.data.length; i++){
	        		navList = navList + "<li><i class=\"icon-cross delete-event\" onclick=\"console.log(\'Delete Clicked\'); return false\"; /><a href=\"" + json.data[i].href + "\">" + json.data[i].name + "</li>";
	        	}
	        	navList = navList + "</ul></dd></dl><div class=\"button register-all\" onclick=\"console.log('Register All Clicked'); return false;\">Register</div>";
	        	$("#event-cart").html(navList);
        	}
        }
	})
	.fail(function(msg){
		console.log("Event Cart update failed");
	});
}

function addToCart(path, eventPath){
	$.ajax({
		type: "POST",
   		url: path,
        data: { action: "add", eventPath: eventPath },
        success: function(data){
        	var json = JSON.parse(data);
        	console.log("Response: " + json.output);
        	console.log(json);
        	if(json.data != undefined && json.data.length > 0){
	        	var navList = "<dl class=\"accordion\" data-accordion><dt data-target=\"drop-down-cart\"><h6 class = \"on\">My Events</h6></dt><dd class=\"event-cart-navigation\"><ul id=\"event-cart-nav-list\">";
	        	for(var i=0; i < json.data.length; i++){
	        		navList = navList + "<li><i class=\"icon-cross delete-event\" onclick=\"console.log(\'Delete Clicked\'); return false\"; /><a href=\"" + json.data[i].href + "\">" + json.data[i].name + "</li>";
	        	}
	        	navList = navList + "</ul></dd></dl><div class=\"button register-all\" onclick=\"console.log('Register All Clicked'); return false;\">Register</div>";
	        	$("#event-cart").html(navList);
        	}
        }
	})
	.fail(function(msg){
		console.log("Add to Cart failed");
	});
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