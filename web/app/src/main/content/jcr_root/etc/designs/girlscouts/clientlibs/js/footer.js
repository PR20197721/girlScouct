/*global $, console */
/*jslint eqeq:true */

var resizeWindow = function (e) {
    "use strict";
    //make sure fixVertical is defined.
    //if(typeof fixVerticalSizing != 'undefined' && fixVerticalSizing === true) {
    //get height of the actual page
    var currentMainHeight = $('.inner-wrap').height(),
        windowHeight = $(window).height(), //get the height of the window
        targetMainHeight = (windowHeight - currentMainHeight);
    //if the content of the page is not to the bottom of the window add this padding, note the row that is the wrapper
    //must have class content
    $('.vtk-body #main .row.content').css('padding-bottom', '');
    $('#main.content').css('padding-bottom', '');
    if (targetMainHeight > 0) {
        $('.vtk-body #main .row.content').first().css('padding-bottom', targetMainHeight + "px");
        // per GSDO-808 - this line over-stretches short pages in AEM 6.3 
        //$('#main.content').css('padding-bottom', targetMainHeight + "px");
    }
    // else {
    //  $('.vtk-body #main .row.content').css('padding-bottom','');
    //  $('#main.content').css('padding-bottom','');
    // }
};
//need to add class for small screens only on the footer links.
function addClassGrid() {
    "use strict";
    if ($(window).width() < 640) {
        $('.footer-navigation > div:nth-of-type(1) ul').addClass('small-block-grid-2');
        $('.footer-navigation > div:nth-of-type(2) ul').css('text-align', 'center');
    } else {
        $('.footer-navigation > div:nth-of-type(1) ul').removeClass('small-block-grid-2');
        $('.footer-navigation > div:nth-of-type(2) ul').css('text-align', 'right');

    }
}

function link_bg_square() {
    "use strict";
    $(".meeting").each(function () {
        var test = $(this).find('.subtitle a').attr('href');

        $(this).find('.bg-square').on('click', function () {
            location.href = test;
        });
    });
}

function attendance_popup_width() {
    "use strict";
    var modal = $(".modal-attendance").parent(),
        wd_wdth = $(window).width(),
        middle;
    modal.addClass('small');
    modal.width("40%");
    if ($(window).width() > 641) {
        if (modal.width() < wd_wdth) {
            middle = modal.width() / 2;
            modal.css('margin-left', "-" + middle + 'px');
        }
    } else {
        modal.width("100%");
        modal.css('margin-left', '');
    }
}

var openClass = "on";

//accordion logic mostly moved to common/app/src/main/content/jcr_root/etc/clientlibs/girlscouts-common/components/accordion/accordion.js
//toggleTab and anchorcheck are kept in this file

function toggleTab(panel) {
    "use strict";

    if (!panel.tab.length) {
        return;
    }

    var targetHeight,
        fixHeight;
    if (panel.action == "collapse") {
        targetHeight = function () {
            return 0;
        };
        fixHeight = 0;
    } else if (panel.action == "expand") {
        targetHeight = function () { // Calculate height after parsys is shown
            return this.body.children().outerHeight(true);
        };

    }

    // Set custom values or use defaults
    panel = {
        tab: panel.tab,
        action: panel.action,
        header: panel.header || panel.tab.find("> :first-child"),
        body: panel.body || panel.tab.next(),
        targetHeight: panel.targetHeight || targetHeight,
        fixHeight: panel.fixHeight || fixHeight,
        parsysID: panel.parsysID || panel.tab.attr("data-target")
    };

    // Necessary for authoring mode. See main.js:toggleParsys
    if (window[panel.parsysID] && window[panel.parsysID].toggle) {
        window[panel.parsysID].toggle();
        panel.body.find(".accordion dt").each(function () { // Hide child parsys
            window[this.getAttribute("data-target")].hideParsys();
        });
    }

    // Toggle classes and animate
    panel.tab.toggleClass(openClass);
    panel.header.toggleClass(openClass, function(){
        //if the header is below the view, scrolls up until it is placed in view
        if (scrolledUnder(panel.header) && (panel.action == "collapse")){
            $('html, body').animate( {
                scrollTop: panel.header.offset().top,
            }, {
            duration: "slow",
            queue: false
            });
        }
    });

    panel.body.animate({
        "height": panel.targetHeight(),
    }, {
        duration: "slow", // 600ms
        queue: false,
        complete: function () { // Allow for responsive content height when expanded
            panel.body.css("height", panel.fixHeight);

        }
    });
}


function anchorCheck() {
    "use strict";
    if (window.location.hash) {
        toggleTab({
            tab: $(".accordion dt[id=" + window.location.hash.replace("#", "") + "]"),
            action: "expand"
        });
    }
}



function adjust_pdf_links() {
    "use strict";
    $('a').each(function (index, link) {
        var href = $(link).attr('href'),
            SUFFIX = '.pdf?download=true',
            suffixIndex,
            newHref;

        if (!href) {
            return;
        }
        suffixIndex = href.indexOf(SUFFIX);
        newHref = href.substring(0, href.length - SUFFIX.length + 4); // 4 = lengthOf('.pdf')

        if (suffixIndex != -1 && suffixIndex == href.length - SUFFIX.length) {
            $(link).attr('href', newHref);
            $(link).attr('download', '');
        }
    });
}

$(window).load(function () {
    "use strict";
    var currentMainHeight = $('.inner-wrap').height(),
        windowHeight = $(window).height(), //get the height of the window
        targetMainHeight = (windowHeight - currentMainHeight);
    if (targetMainHeight != 0) {
        resizeWindow();
        addClassGrid();
        attendance_popup_width();
    }
});

$(document).ready(function () {
    "use strict";
    resizeWindow();
    addClassGrid();
    vtk_accordion();
    anchorCheck();
    attendance_popup_width();
    adjust_pdf_links();

    $(window).on('hashchange', function () {
        $(location.hash).children('h6').trigger('click');
    });
});
