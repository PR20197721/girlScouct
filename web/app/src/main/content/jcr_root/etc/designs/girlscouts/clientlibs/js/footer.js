var resizeWindow = function(e) {
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
    if ($(window).width() < 640) {
        $('.footer-navigation > div:nth-of-type(1) ul').addClass('small-block-grid-2');
        $('.footer-navigation > div:nth-of-type(2) ul').css('text-align', 'center');
    } else {
        $('.footer-navigation > div:nth-of-type(1) ul').removeClass('small-block-grid-2');
        $('.footer-navigation > div:nth-of-type(2) ul').css('text-align', 'right');

    }
}

function link_bg_square() {

    $(".meeting").each(function() {
        var test = $(this).find('.subtitle a').attr('href');

        $(this).find('.bg-square').on('click', function() {
            location.href = test;
        })
    });
}

function attendance_popup_width() {
    var modal = $(".modal-attendance").parent();
    modal.addClass('small');
    var wd_wdth = $(window).width();
    modal.width("40%");
    if ($(window).width() > 641) {
        if (modal.width() < wd_wdth) {
            var middle = modal.width() / 2;
            modal.css('margin-left', "-" + middle + 'px');
        }
    } else {
        modal.width("100%");
        modal.css('margin-left', '');
    }
}

function anchorCheck() {
    $('.accordion dt > :first-child').each(function(i, value) {
        var parsysID = $(value).parent().data('target');
        var target = $(this).parent().next().find('.content');
        var toggle = $(this);
        var parsysID = $(this).parent().data('target');
        var anchor = $(this).parent().attr('id');
        if (anchor != "" && window.location.hash.replace("#", "") == anchor) {
            toggle.addClass('on');
            target.slideDown();
            $(this).parent().addClass('on');
        }
    });
}



$(document).ready(function() {
    resizeWindow();
    addClassGrid();
    vtk_accordion();
    attendance_popup_width();
    adjust_pdf_links();
});


$(window).load(function() {
    var currentMainHeight = $('.inner-wrap').height();
    //get the height of the window
    var windowHeight = $(window).height();
    var targetMainHeight = (windowHeight - currentMainHeight);
    if (targetMainHeight != 0) {
        resizeWindow();
        addClassGrid();
        attendance_popup_width();
    }
    anchorCheck();
});


function vtk_accordion_main() {
    $('.accordion dt > :first-child').on('click', function(e) {

        e.stopPropagation();

        var target = $(this).parent().data('target');

        var toggle = $(this);

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

    $('.accordion dt > :first-child').on('click', function(e) {
            //close and remove classes first
            $('.accordion dd .content').slideUp('slow');
            $('.accordion dt > :first-child').removeClass('on');
            $('.accordion dt').removeClass('on');

            $('.accordion dt > :first-child').each(function(i, value) {
                var parsysID = $(value).parent().data('target');
                //Necessary for authoring mode. See main.js:toggleParsys
                if (window[parsysID] != null && window[parsysID].hideParsys != undefined) {
                    window[parsysID].hideParsys();
                }
            });

            var target = $(this).parent().next().find('.content');
            var toggle = $(this);
            var parsysID = $(this).parent().data('target');

            if (target.is(':visible')) {
                toggle.removeClass('on');
                target.slideUp();
                $(this).parent().removeClass('on');

                if (window[parsysID] != null && window[parsysID].hideParsys != undefined) {
                    window[parsysID].hideParsys();
                }

            } else {

                toggle.addClass('on');
                target.slideDown();
                $(this).parent().addClass('on');

                if (window[parsysID] != null && window[parsysID].showParsys != undefined) {
                    window[parsysID].showParsys();
                }
            }

            return false;
        });


        anchorCheck();

}

/* ============

Girl Scouts: Web PlatformGSWP-542
SUPPORT 5526-10047044 Accordion Module Issue

==============================================
 Work Around to fix the Accordion problem.
============================================== */

function vtk_accordion() {
        if ($('.accordion').length) { //Check if there is any accordion in the page
            if ($('body').has('.vtk').length) { //check if the user is in VTK
                vtk_accordion_main();
            } else {
                web_accordion_main();
            }
        }
    }

function adjust_pdf_links() {
    $('a').each(function(index, link){
        var href = $(link).attr('href');
        if (!href) {
            return;
        }
        var SUFFIX = '.pdf?download=true';
        var suffixIndex = href.indexOf(SUFFIX);
        if (suffixIndex != -1 && suffixIndex == href.length - SUFFIX.length) {
            var newHref = href.substring(0, href.length - SUFFIX.length + 4); // 4 = lengthOf('.pdf')
            $(link).attr('href', newHref);
            $(link).attr('download', '');
        }
    });
}




$(function(){
    $(window).on('hashchange', function(){
        $(location.hash).children('h6').trigger('click');
    });
});