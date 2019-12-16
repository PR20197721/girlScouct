(function(document, $) {
    "use strict";
    //download action
    $(document).on('foundation-selections-change', '.foundation-collection', function() {
    	var collection = $(this); 
    	var cancelScheduledDeactivationActivator = ".cq-siteadmin-admin-actions-cancel-scheduled-workflow-activator";
    	$(cancelScheduledDeactivationActivator).unbind('click').click(function(e) {
            e.stopPropagation();
			e.preventDefault();
    		var path;
    		var activator = $(this);
    		var selectedItem = collection.find(".foundation-selections-item")[0];
    		if(selectedItem){
	        	path = $(selectedItem).data("foundation-collection-item-id");
	        }
    		var action = Granite.HTTP.externalize(activator.data("workflow-type"));
    		var uri = "/bin/querybuilder.json?" + 
    		"1_property=data/payload/path" +
    		"&1_property.value=" + path +
    		"&2_property=status" +
    		"&2_property.value=RUNNING" +
    		"&3_property=modelId" +
    		"&3_property.value=/var/workflow/models/scheduled_" + action +
    		"&path=/var/workflow";
    		$.getJSON(uri, function(data){ processResponse(action, path, data) });    		    		
            return false;
    	});
    });
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
})(document, Granite.$);