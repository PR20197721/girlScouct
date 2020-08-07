$(document).ready(function(){
    gigya.accounts.logout({callback:logoutAEM});
});
function logoutAEM(response){
    if ( response.errorCode == 0 ) {
        window.location.href = "/system/sling/logout?resource=/content/girlscouts-vtk";
    }
    else {
        alert('Error :' + response.errorMessage);
    }
}