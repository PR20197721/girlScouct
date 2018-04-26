(function (document, $, ns) { 
    "use strict";
    $(document).on("dialog-ready", function() { 
        try{ 
        	alert("dialog ready");
        }catch(err){
        	
        }
    });
})(document, Granite.$, Granite.author);