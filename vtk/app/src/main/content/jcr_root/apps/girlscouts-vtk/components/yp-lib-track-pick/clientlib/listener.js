(function (document, $, ns) {
    "use strict";
    $(document).on("dialog-ready", function() {
    	var $dialog = $("coral-dialog-content");
		var $resourceType = $dialog.find("[name='./sling:resourceType']").val();
        if("girlscouts-vtk/components/yp-lib-track-pick" == $resourceType){ 
        	var $yearSelect =  new Coral.Select().set({
        		name:"./year",
        		placeholder:"Choose a year"
        	});
        	var $trackSelect = new Coral.Select().set({
        		name:"./track",
        		placeholder:"Choose a track"
        	});
        	var $yearHidden = $dialog.find("input[name='./year']");
        	var selectedYear = parseInt($yearHidden.val());
        	var $trackHidden = $dialog.find("input[name='./track']");
        	var selectedTrack = $trackHidden.val(); 
        	if(isNaN(selectedYear)){
        		try{
        			selectedYear = parseInt(String(selectedTrack.match(/yearplan[^d][^d][^d][^d]/)).replace(/yearplan/,""));	
        		}catch(err){}
        	}        	
        	var currentYear = (new Date()).getFullYear();
        	var startYear = (!isNaN(selectedYear != null) && currentYear-1 > selectedYear ) ? selectedYear : currentYear-1;        	
        	for(var i = startYear; i <= currentYear+1; i++){
        		var selected = selectedYear == i ? true : false;
        		$yearSelect.items.add({
        	        value: i,
        	        selected:selected, 
        	        content: {
        	          textContent: i
        	        }
        	    });
        	}
        	$yearHidden.after($("<div class='coral-Form-fieldwrapper'>").append($yearSelect));
        	$trackHidden.after($("<div class='coral-Form-fieldwrapper'>").append($trackSelect));
        	loadYearPlan($yearSelect, $trackSelect, selectedTrack); 
        	$yearSelect.on("change", function(){
        		loadYearPlan($yearSelect, $trackSelect, selectedTrack)
        	});
        }
    });
    function loadYearPlan($yearSelect, $trackSelect, selectedTrack){
    	try{ 
	    	if($yearSelect.selectedItem){
	    		$trackSelect.items.clear();
	    		$.getJSON("/bin/vtk/v1/scaffoldingdata.json?yearplan=yearplan"+$yearSelect.selectedItem.value).done(function(data){
	        		$.each( data.yearplan, function( key, val ) {    
	        			var selected = selectedTrack == val.data ? true:false;
	        			try {  
	        				$trackSelect.items.add({
	        					value: val.data,
	                	        selected:selected, 
	                	        content: {
	                	          textContent: val.title
	                	        }	
	        				});
	        			} catch(err){}
	        		  });
	        	});
			}
    	}catch(err){}
    }
})(document, Granite.$, Granite.author);
