$(document).ready(function(){
    var logoutRedirect = "/content/girlscouts-vtk/sso/landing.html";
    var url = getCookieValue('girl-scout-logout');
    if(url != null){
        logoutRedirect = url;
    }
    var cookies = document.cookie.split(";");
    for (var i = 0; i < cookies.length; i++) {
        var cookie = cookies[i];
        var eqPos = cookie.indexOf("=");
        var name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
        document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT";
    }
    window.location.href = logoutRedirect;
    function getCookieValue(a) {
        var b = document.cookie.match('(^|;)\\s*' + a + '\\s*=\\s*([^;]+)');
        return decodeURIComponent(b ? b.pop() : '');
    }
});