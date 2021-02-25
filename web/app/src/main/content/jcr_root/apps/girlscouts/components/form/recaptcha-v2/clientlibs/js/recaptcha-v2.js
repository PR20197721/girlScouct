$(document).ready(function () {
    const recaptchaError = localStorage.getItem("recaptchaError");
    if(recaptchaError) {
		$("#recaptcha-error").show();
        localStorage.removeItem("recaptchaError");
    }
    function timestamp() {
        currentTimestamp += 500;
        var response = document.getElementById("g-recaptcha-response");
        if (response == null || response.value.trim() == "") {
            var elems = JSON.parse(document.getElementsByName("captcha_settings")[0].value);
            elems["ts"] = JSON.stringify(currentTimestamp);
            document.getElementsByName("captcha_settings")[0].value = JSON.stringify(elems);
        }
    }
    var currentTimestamp = new Date().getTime();
    $.getJSON("http://icanhazepoch.com/", function (data) {
        currentTimestamp = data * 1000;
    }).always(function (){
        setInterval(timestamp, 500);
    });

    $("input[type='submit']").bind("click",function(e){
      var response = grecaptcha.getResponse();
      if(response.length == 0) {
        localStorage.setItem("recaptchaError", true); 
        e.preventDefault();
        return false;
      }
    });
});

