function recaptchaCallback(){
    var timestamp = new Date().getTime();
    var ts = $("input#g-recaptcha-ts");
    var response = $("[name='g-recaptcha-response']");
    if(response.val() != null && $(response).val().trim().length > 0){
        $(ts).val(timestamp);
        console.log("recaptcha validated at: "+timestamp);
    }
}
