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
        	console.log("Response: " + json.output);
        	var navList = "";
        	for(var i=0; i < json.data.length; i++){
        		navList = navList + "<li><a href=\"" + json.data[i].href + "\">" + json.data[i].name + "</li>";
        	}
        	$("#event-cart-nav-list").html(navList);
        }
	})
	.fail(function(msg){
		console.log("Event Cart update failed");
	});
}

function createCart(path){
	$.ajax({
		type: "POST",
   		url: path,
        data: { action: "create" },
        success: function(data){
        	var json = JSON.parse(data);
        	console.log("Response: " + json.output);
        }
	})
	.fail(function(msg){
		console.log("Event Cart creation failed");
	});
}

function addToCart(path, eventPath){
	$.ajax({
		type: "POST",
   		url: path,
        data: { action: "add" },
        success: function(data){
        	var json = JSON.parse(data);
        	console.log("Response: " + json.output);
        	var navList = "";
        	for(var i=0; i < json.data.length; i++){
        		navList = navList + "<li><a href=\"" + json.data[i].href + "\">" + json.data[i].name + "</li>";
        	}
        	$("#event-cart-nav-list").html(navList);
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