(function (document, $, ns) {
    "use strict";
    //validation check
	jQuery.validator.register({
      selector: "input",
      validate: function(el) {
          var val = el.val();
          var field = el.attr("name");
          if(field.includes("slideshowtimer") && showD){
              if(isNaN(val) && !val.includes("ms") && !val.includes("milliseconds")){
             	 return "Not a number, Please input a number that represents slider time in milliseconds (Ex: 6000)";
              }else{
                 if(val > 180000){
                 return "Timer limit exceeded, please specify a time less than 3 minutes (180000 ms)";
                 }
             }
          }
      }
    });
	var showD = false;
    $(document).on("click", ".cq-dialog-submit", function (e) {
        showD = true;
    });
})(document, Granite.$, Granite.author);