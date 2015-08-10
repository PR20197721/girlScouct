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
    $('.accordion dt > :first-child').on('click', function() {
      var target = $(this).parent().data('target');
      var toggle = $(this);
      $('#' + target).slideToggle('slow');
      $(toggle).toggleClass('on');
      if(window[ target ] != null && window[ target ].toggle != null){
    	  window[ target ].toggle();
      }
        return false;
    });
  }
$(document).ready(function(){
 vtk_accordion();
});