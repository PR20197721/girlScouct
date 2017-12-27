CQ.wcm.SiteAdmin.cancelScheduledActivation = function() {
	var path = this.getSelectedPages()[0].id;
	CQ.shared.HTTP.get("/bin/querybuilder.json?" + 
			"1_property=data/payload/path" +
			"&1_property.value=" + path +
			"&2_property=status" +
			"&2_property.value=RUNNING" +
			"&3_property=modelId" +
			"&3_property.value=/etc/workflow/models/scheduled_activation/jcr:content/model" +
			"&path=/etc/workflow",
		function(options, success, response) {
		var result = JSON.parse(response.body);
		var hits = result.hits;
		if (hits != undefined && hits.length >= 1) {
			var workflowPath = hits[0].path;
			CQ.shared.HTTP.post(workflowPath, function(options, success, response){
				CQ.Ext.Msg.show({
			        title: CQ.I18n.getMessage('Cancellation'),
			        msg: CQ.I18n.getMessage('Scheduled activation successfully canceled.'),
			        buttons: CQ.Ext.Msg.OK,
			        icon: CQ.Ext.Msg.INFO,
			        fn: function(){location.reload()}
			    });
			}, {
				state: "ABORTED"
			})
		}
	});
}