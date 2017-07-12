window.fbAsyncInit = function () {
    $(window).on("fb-lazy-load", function () {
        FB.init({
            appId: '419540344831322',
            xfbml: true,
            version: 'v2.3'
        })
    });
};

(function (d, s, id) {
    var js, fjs = d.getElementsByTagName(s)[0];
    if (d.getElementById(id)) return;
    js = d.createElement(s);
    js.id = id;
    js.src = "//connect.facebook.net/en_US/all.js";
    fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));