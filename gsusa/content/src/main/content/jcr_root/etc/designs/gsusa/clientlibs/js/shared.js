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

function vtk_accordion() {
    $('.accordion dt > :first-child').on('click', function(e) {
       if($('.inner-wrap').hasClass('cookie-page')) {
    	$('.cookie-page .accordion dt > :first-child').each(function(i, value) {
    		var parsysID = $(value).parent().data('target');
    		//Necessary for authoring mode. See main.js:toggleParsys
        if(window[parsysID] != null){
     	      window[parsysID].hideParsys();
        }
    	});
        $('.cookie-page .accordion dd .content').slideUp('slow');
        $('.cookie-page .accordion dt > :first-child').removeClass('on');
        $('.cookie-page .accordion dt').removeClass('on');
       }
      var target = $(this).parent().next().find('.content');
      var toggle = $(this);
      var parsysID = $(this).parent().data('target');

      if(target.is(':visible')) {
        toggle.removeClass('on');
        target.slideUp();
        $(this).parent().removeClass('on');
      } else {
        toggle.addClass('on');
        target.slideDown();
        $(this).parent().addClass('on');
      }
      if(window[parsysID] != null){
    	   window[parsysID].toggleParsys();
       }
      return false;
    });
  }

$(document).ready(function(){
  vtk_accordion();
});