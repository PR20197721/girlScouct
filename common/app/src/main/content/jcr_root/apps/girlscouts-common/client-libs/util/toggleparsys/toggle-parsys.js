window.TouchUI = window.TouchUI || {};
window.TouchUI.Utils = window.TouchUI.Utils || {};

window.TouchUI.Utils.ToggleParsys = function(name) {
	
    var editablesReady = new $.Deferred();
    
	$(document).on('cq-editables-loaded', function(event, editablesObject){ 
		editablesReady.resolve(editablesObject.editables); 
	});
	
	function findEditable(){

		var returner = new $.Deferred();
		editablesReady.then(function(editables){
			try{
				returner.resolve(editables.filter(etb => etb.path == name)[0]);
			}catch(err){
				returner.reject(err);
			}
		});
		return returner;
	}

    function _toggle(){
    		try{
    			findEditable().then(edb => $(edb.dom).toggle());
    		} catch (err){
    			console.warn("couldn't toggle parsys");
    		}
    };

    function _hideParsys(){
    		try{
    			findEditable().then(edb => $(edb.dom).hide());
    		} catch (err){
			console.warn("couldn't hide parsys");
		}
    };

    function _showParsys(){
    		try{ 
    			findEditable().then(edb => $(edb.dom).show());
    		} catch (err){
    			console.warn("couldn't show parsys");
    		}
    };

    return {
    		toggle: _toggle,
    		hideParsys: _hideParsys,
    		showParsys: _showParsys
    };
};