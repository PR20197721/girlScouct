//Shared with council components
if (!Date.now) {
      Date.now = function now() {
        return new Date().getTime();
      };
    }


function toggleParsys(s)
{
    var componentPath = s;

    this.toggle = function()
    {
        if (componentPath)
        {
            var parsysComp = CQ.WCM.getEditable(componentPath);

            if(parsysComp.hidden == true){
                parsysComp.show();
            }
            else{
                parsysComp.hide();
            }
        }
    };

    this.hideParsys = function()
    {
        if (componentPath)
        {
            var parsysComp = CQ.WCM.getEditable(componentPath);

            if (parsysComp)
            {
                parsysComp.hide();
            }
        }
    };

    this.showParsys = function()
    {
        if (componentPath)
        {
            var parsysComp = CQ.WCM.getEditable(componentPath);

            if (parsysComp)
            {
                parsysComp.show();
            }
        }
    };

    return this;
};

function anchorCheck() {
    $('.accordion dt > :first-child').each(function(i, value) {
        var parsysID = $(value).parent().data('target');
        var target = $(this).parent().next().find('.content');
        var toggle = $(this);
        var parsysID = $(this).parent().data('target');
        var anchor = $(this).parent().attr('id');
        if(anchor != "" && window.location.hash.replace("#","") == anchor){
            toggle.addClass('on');
            target.slideDown();
            $(this).parent().addClass('on');
        }
    });
}

function inPageAnchorCheck(e){
    $('.cookie-page .accordion dt > :first-child').each(function(i, value){
        var target = $(this).parent().next().find('.content');
        var toggle = $(this);
        var parsysID = $(this).parent().data('target');
        toggle.removeClass('on');
        target.slideUp();
        $(this).parent().removeClass('on');
        if(window[parsysID] != null && window[parsysID].hideParsys != undefined){
            window[parsysID].hideParsys();
        }
    });
    $('.cookie-page .accordion dt > :first-child').each(function(i, value) {
        var parsysID = $(value).parent().data('target');
        var target = $(this).parent().next().find('.content');
        var toggle = $(this);
        var parsysID = $(this).parent().data('target');
        var anchor = $(this).parent().attr('id');
        if(anchor != "" && e.target.href.substring(e.target.href.indexOf("#")).replace("#","") == anchor){
            toggle.addClass('on');
            target.slideDown();
            $(this).parent().addClass('on');
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


function vtk_accordion() {
  $('.accordion dt > :first-child').on('click', function(e) {
    e.stopPropagation();

    //Catch Elements
    var target = $(this).parent().data('target');
    var toggle = $(this);

    //Reset State
    $('.accordion-navigation > .content').slideUp('slow');
    $('[data-target]').removeClass('on');
    $('[data-target]').children('h6').removeClass('on');

        
     //Open/close  the Accordion 
    $('#' + target).slideToggle('slow');
    

    //Add the Class to give some formatt to the accordion
    $(toggle).parents('dt').toggleClass('on');
    $(toggle).toggleClass('on');


    //For Web Component. See main.js:toggleParsys
     if(window[ target ] != null){
       window[ target ].toggle();
     }
      return false;
  });
}

$(document).ready(function(){
  vtk_accordion(); 
  $('.accordion a').click(inPageAnchorCheck);
});