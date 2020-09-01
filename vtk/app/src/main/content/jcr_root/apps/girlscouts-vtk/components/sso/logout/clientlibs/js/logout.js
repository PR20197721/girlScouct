$(document).ready(function(){
    gigya.accounts.logout({callback:logoutAEM});

});
function logoutAEM(response){
    if ( response.errorCode == 0 ) {
        var cookies = document.cookie.split(";");
        for (var i = 0; i < cookies.length; i++) {
            var cookie = cookies[i];
            var eqPos = cookie.indexOf("=");
            var name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
            document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT";
        }
        window.location.href = "/system/sling/logout?resource=/content/girlscouts-vtk";
    }
    else {
        alert('Error :' + response.errorMessage);
    }
}