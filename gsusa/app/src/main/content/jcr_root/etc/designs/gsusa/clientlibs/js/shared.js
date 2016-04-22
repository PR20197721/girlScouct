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
   var hash = window.location.hash;
   if(hash!=="" && $(hash).length > 0){
    setTimeout(function() {
        $(hash).children('h6').trigger('click');
    }, 200);
    
   
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

// function vtk_accordion() {
//     $('.accordion dt > :first-child').on('click', function (e) {
//         //close and remove classes first
//         $('.accordion dd .content').slideUp('slow');
//         $('.accordion dt > :first-child').removeClass('on');
//         $('.accordion dt').removeClass('on');

//      $('.accordion dt > :first-child').each(function (i, value) {
//          var parsysID = $(value).parent().data('target');
//          //Necessary for authoring mode. See main.js:toggleParsys
//             if(window[parsysID] != null && window[parsysID].hideParsys != undefined) {
//                    window[parsysID].hideParsys();
//             }
//      });

//         var target = $(this).parent().next().find('.content');
//         var toggle = $(this);
//         var parsysID = $(this).parent().data('target');

//         if(target.is(':visible')) {
//         toggle.removeClass('on');
//         target.slideUp();
//         $(this).parent().removeClass('on');
//             if(window[parsysID] != null && window[parsysID].hideParsys != undefined){
//                 window[parsysID].hideParsys();
//             }
//         } else {
//         toggle.addClass('on');
//         target.slideDown();
//         $(this).parent().addClass('on');
//             if(window[parsysID] != null && window[parsysID].showParsys != undefined){
//                 window[parsysID].showParsys();
//             }
//         }
//         return false;
//     });
//     anchorCheck();
// }
// 
// 
// 
// 
// 
// 



//  Arguments: null;
//  function handled the click event in the accordion let it open and close.
//  vtk_accordion():void
//

var CHECK;

function closeAllAccordion() {
    $('.accordion-navigation > .content').slideUp('slow');
    $('[data-target]').removeClass('on');
    $('[data-target]').children('h6').removeClass('on');
}

function toggleAccordion(target, that) {
    if (CHECK !== that) {

        $('#' + target).slideToggle('slow');
        //Add the Class to give some formatt to the accordion
        $(that).parents('dt').toggleClass('on');
        $(that).toggleClass('on');
        CHECK = that;
        
    }else{
        CHECK=undefined;
    }
}

function vtk_accordion() {
    $('.accordion dt > :first-child').on('click', function(e) {
        e.stopPropagation();

        //Catch Elements
        var target = $(this).parent().data('target');
        var toggle = $(this);

        //Reset State
        closeAllAccordion();

        //Open/close  the Accordion 
        toggleAccordion(target, this);

        //For Web Component. See main.js:toggleParsys
        if (window[target].hasOwnProperty('toggle')) {
            window[target].toggle();
        }

    });

    anchorCheck();
}

$(document).ready(function() {
    vtk_accordion();
    $('.accordion a').click(inPageAnchorCheck);

    //Check if the hash change without reload the page

    $(window).on('hashchange', function() {
      anchorCheck();
    });
});



