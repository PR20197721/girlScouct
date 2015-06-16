var// If checkPhoto is set to true, make sure to have window.BaseUrl defined.
    feederize = function (fbAppId, checkPhoto) {

    var elements = $('.addthis_button_facebook');

    function buildObj(title, description, link, image) {
        var obj = {
            method: 'feed',
            link: link,
            picture: image,
            name: title,
            description: description
        };
        return obj;
    }

    function postToFeed(obj) {
		if (FB){
			FB.ui(obj);
        }
    }

    function getSiteUrl() {
        return window.location.protocol + "//" + window.location.host;
    }

    setTimeout(function () {
        $.each(elements, function (index, element) {
            element.onclick = null;
            
            element = $(element);
            
            element.click(function (e) {
                var obj = buildObj(element.attr('addthis:title'), element.attr('addthis:description'), element.attr('addthis:url'), element.attr('addthis:image'));
                if (checkPhoto) {
                    var photoPost = element.parents(".post-container").children("[class^=post-photo]");
                    if (photoPost.length > 0) {
                        var imgPath = photoPost.children(".post-photo").children("img").attr("src");
                        imgPath = getSiteUrl() + window.BaseUrl + imgPath;
                        obj.picture = imgPath;
                    }
                }
                postToFeed(obj);
                e.stopPropagation();
                return false;
            });
        });
    }, 10);
};