//Shared with council components
if (!Date.now) {
    Date.now = function now() {
        return new Date().getTime();
    };
}


function toggleParsys(s) {
    var componentPath = s;

    this.toggle = function() {
        if (componentPath) {
            var parsysComp = CQ.WCM.getEditable(componentPath);

            if (parsysComp.hidden == true) {
                parsysComp.show();
            } else {
                parsysComp.hide();
            }
        }
    };

    this.hideParsys = function() {
        if (componentPath) {
            var parsysComp = CQ.WCM.getEditable(componentPath);

            if (parsysComp) {
                parsysComp.hide();
            }
        }
    };

    this.showParsys = function() {
        if (componentPath) {
            var parsysComp = CQ.WCM.getEditable(componentPath);

            if (parsysComp) {
                parsysComp.show();
            }
        }
    };

    return this;
};

function anchorCheck() {
   //Take the Location
   var hash = window.location.hash.replace('$$$','');

   if($('.accordion').length > 0){ // check is the class acordion exist in the page
        if(hash!=="" && $(hash).length > 0){ //check the hash exist
            setTimeout(function() {
                $(hash).children('h6').trigger('click'); // open the accordion in the accordion
            }, 200);
        }
   }
}

function inPageAnchorCheck(e) {
    $('.cookie-page .accordion dt > :first-child').each(function(i, value) {
        var target = $(this).parent().next().find('.content');
        var toggle = $(this);
        var parsysID = $(this).parent().data('target');
        toggle.removeClass('on');
        target.slideUp();
        $(this).parent().removeClass('on');
        if (window[parsysID] != null && window[parsysID].hideParsys != undefined) {
            window[parsysID].hideParsys();
        }
    });
    $('.cookie-page .accordion dt > :first-child').each(function(i, value) {
        var parsysID = $(value).parent().data('target');
        var target = $(this).parent().next().find('.content');
        var toggle = $(this);
        var parsysID = $(this).parent().data('target');
        var anchor = $(this).parent().attr('id');
        if (anchor != "" && e.target.href.substring(e.target.href.indexOf("#")).replace("#", "") == anchor) {
            // toggle.addClass('on');
            // target.slideDown();
            // $(this).parent().addClass('on');
            $("#" + anchor).trigger('click');
        }
    });
}


