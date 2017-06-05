/*global $, console, vtk_accordion, adjust_pdf_links*/
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
        $('#main.content').css('padding-bottom', targetMainHeight + "px");
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

function anchorCheck() {
    "use strict";
    $('.accordion dt > :first-child').each(function (i, value) {
        var //parsysID = $(value).parent().data('target'),
            target = $(this).parent().next().find('.content'),
            toggle = $(this),
            parsysID = $(this).parent().data('target'),
            anchor = $(this).parent().attr('id');
        if (anchor != "" && window.location.hash.replace("#", "") == anchor) {
            toggle.addClass('on');
            target.slideDown();
            $(this).parent().addClass('on');
        }
    });
}

function vtk_accordion_main() {
    "use strict";
    $('.accordion dt > :first-child').on('click', function (e) {
        e.stopPropagation();

        var target = $(this).parent().data('target'),
            toggle = $(this);
        $('#' + target).slideToggle('slow');
        $(toggle).toggleClass('on');

        //For Web Component. See main.js:toggleParsys
        if (window[target] !== null && window[target].hasOwnProperty('toggle')) {
            window[target].toggle();
        }

        return false;
    });
}

function web_accordion_main() {
    "use strict";
    var openClass = "on";

    function toggleTab(panel) {
        panel = {
            tab: panel.tab,
            header: panel.header || panel.tab.find("> :first-child"),
            body: panel.body || panel.tab.next(),
            targetHeight: panel.targetHeight,
            fixHeight: panel.fixHeight,
            parsysID: panel.parsysID || panel.tab.attr("data-target")
        };

        // Necessary for authoring mode. See main.js:toggleParsys
        if (window[panel.parsysID] && window[panel.parsysID].toggle) {
            window[panel.parsysID].toggle();
        }
        panel.tab.toggleClass(openClass);
        panel.header.toggleClass(openClass);
        panel.body.animate({
            "height": panel.targetHeight()
        }, {
            duration: "slow", // 600ms
            queue: false,
            complete: function () { // Allow for responsive content height when expanded
                panel.body.css("height", panel.fixHeight);
            }
        });
    }

    $(".accordion dt").on("click", function () {
        var oldPanel = {
                tab: $(".accordion > dt." + openClass),
                targetHeight: function () {
                    return 0;
                },
                fixHeight: 0
            },
            newPanel = {
                tab: $(this),
                targetHeight: function () { // Calculate height after parsys is shown
                    return this.body.children().outerHeight(true);
                },
                fixHeight: "auto"
            };

        if (oldPanel.tab.is(newPanel.tab)) {
            toggleTab(oldPanel); // Close current tab
        } else {
            toggleTab(oldPanel); // Close old tab
            toggleTab(newPanel); // Open new tab
        }
    });

    //anchorCheck();

}

/* ============

Girl Scouts: Web PlatformGSWP-542
SUPPORT 5526-10047044 Accordion Module Issue

==============================================
 Work Around to fix the Accordion problem.
============================================== */

function vtk_accordion() {
    "use strict";
    if ($('.accordion').length) { //Check if there is any accordion in the page
        if ($('body').has('.vtk').length) { //check if the user is in VTK
            vtk_accordion_main();
        } else {
            web_accordion_main();
        }
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
    anchorCheck();
});

$(document).ready(function () {
    "use strict";
    resizeWindow();
    addClassGrid();
    vtk_accordion();
    attendance_popup_width();
    adjust_pdf_links();

    $(window).on('hashchange', function () {
        $(location.hash).children('h6').trigger('click');
    });
});