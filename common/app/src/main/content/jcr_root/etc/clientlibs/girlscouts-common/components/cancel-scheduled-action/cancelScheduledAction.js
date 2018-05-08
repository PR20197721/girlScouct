var cancel_action;
(function(){
	var path;
	$(document).on("foundation-selections-change", ".foundation-collection", function(e) {
		try{	
	        var selectedItem = $(this).find(".foundation-selections-item")[0];
	        if(selectedItem){
	        	path = $(selectedItem).data("foundation-collection-item-id");
	        }
		}catch(err){		
		}		
	});
	cancel_action = function(action) {	
		var uri = "/bin/querybuilder.json?" + 
		"1_property=data/payload/path" +
		"&1_property.value=" + path +
		"&2_property=status" +
		"&2_property.value=RUNNING" +
		"&3_property=modelId" +
		"&3_property.value=/etc/workflow/models/scheduled_" + action + "/jcr:content/model" +
		"&path=/etc/workflow";
		$.getJSON(uri, function(data){processResponse(action, path, data)});
	}
	function processResponse(action, path, response) {
		var hits = response.hits;
		var notificationTitle = "Cancellation of scheduled "+action;
		if (hits != undefined && hits.length >= 1) {						
			var workflowPath = hits[0].path;
			$.post(workflowPath, {
				state: "ABORTED",
				terminateComment: "Scheduled " + action + " canceled using siteadmin dropdown menu."
			}).done(function(response2){
				$(window).adaptTo("foundation-ui").notify(notificationTitle,"Scheduled " + action + " successfully canceled for "+path+".","success");	
			}).fail(function(response2){
				$(window).adaptTo("foundation-ui").notify(notificationTitle,"Failed to cancel scheduled " + action + " workflows for "+path+".","error");	
			});		
		} else {
			$(window).adaptTo("foundation-ui").notify(notificationTitle,"There are no pending " + action + " workflows for "+path+".","info");		
		}
	}
})();