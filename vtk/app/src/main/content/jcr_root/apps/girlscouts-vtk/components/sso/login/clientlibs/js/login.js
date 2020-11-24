$(document).ready(function(){
    $('.toggle-password-icon').click(function(e) {
        var password = $('input[name=password]');
        var type = password.attr('type') === 'password' ? 'text' : 'password';
        password.attr('type', type);
        var svg = $('svg#password-eye');
        if(svg.hasClass("fa-eye")){
            svg.removeClass("fa-eye");
            svg.addClass('fa-eye-slash');
        }else{
            svg.addClass("fa-eye");
            svg.removeClass('fa-eye-slash');
        }
    })
    $('#log-in-btn').click(login);
    $('#forgot-password-btn').click(forgotPassword);
    var forms = document.getElementsByClassName('needs-validation');
    var validation = Array.prototype.filter.call(forms, function(form) {
        form.addEventListener('submit', function(event) {
            if (form.checkValidity() === false) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });
    gigya.socialize.addEventHandlers({
        onLogin: function() {
            gigya.fidm.saml.continueSSO();
        }
    });
});
function handleGigyaCallback(response){
    if(response != null){
        if(response.errorCode == 0){
            console.log("call "+response.callId+" successful");
            $('#error').html("");
            gigya.fidm.saml.continueSSO();
        } else {
            console.log("login failed errorCode="+response.errorCode+" errorMessage="+response.errorMessage);
            if (response.errorCode == 403042) {
                $('#error').html("<i class=\"fas fa-exclamation-circle\"></i> Please check your username and password. If you still can't log in, contact your Salesforce administrator.");
            }
        }
    }
}
function login(){
    var loginID = $('#loginID').val();
    var password = $('#password').val();
    var remember = $('#remember-me:checkbox:checked').length > 0;
    if(remember){
        gigya.accounts.login({
            "loginID":loginID,
            "password":password,
            "callback":handleGigyaCallback,
            "sessionExpiration":-2
        });
    }else{
        gigya.accounts.login({
            "loginID":loginID,
            "password":password,
            "callback":handleGigyaCallback
        });
    }
    return false;
}
function forgotPassword(){
    var email = $('#forgot-password-email').val();
    gigya.accounts.resetPassword({"loginID":email, "callback":handleGigyaCallback});
    return false;
}
