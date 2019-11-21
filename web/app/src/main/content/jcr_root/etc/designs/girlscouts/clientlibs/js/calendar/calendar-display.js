function calendarDisplay(month,year,jsonEvents){
	
	var date = new Date();
    var d = date.getDate();
    var m = date.getMonth();
    var y = date.getFullYear();
	
	if(month!=null && year!=null){
		m = month;
		y = year;
	}
	var isiOS = navigator.userAgent.match(/(iPad)|(iPhone OS)/i) != null;
    var tooltip = $('<div/>').qtip({
    	id: 'fullcalendar',
        prerender: true,
        content: {
            text: 'Does this show up on click '
            
        },
        
        position: {
            my: 'bottom center',
            at: 'top center',
            target: 'mouse',
            viewport: $('#fullcalendar'),
            adjust: {
                //resize:true,
                mouse: false,
                scroll: false,
                x:5,
                y:5
            }
        },
        show:false, // Show on mouse over by default
        hide: false,
        style: {
           classes:'qtip-light',
           width:500,
           tip:{
              width:40,
              height:24
            }
         }

        
    }).qtip('api');

$('#fullcalendar').fullCalendar({
        
        height: 500,
        month : m,
        year : y,
        header: {
            left: 'prev',
            center: 'title',
            right: 'next'  
        },
        eventClick: function(data, event, view) {
            
                var start = new Date(data.start);
                var end = new Date(data.end);
                var description = data.description.substring(0,100);
                var content = '<div class="row"><div class="small-24 large-24 medium-24 columns"><span class="calTitle"><h6><a target="_blank" href="'+data.path+'"><span class="calTitle">'+data.title+'</a></h6></span></div></div>' +
                '<div class="row"><div class="small-4 large-4 medium-4 columns" style="padding-right:0px"><b>Date:</b></div><div class="small-14 large-14 medium-14 columns" style="padding:0px"><b>'+data.displayDate+'</b></div><div class="small-6 large-6 medium-6 columns">&nbsp;</div></div>'+'<div class="row"><div class="small-6 large-6 medium-6 columns" style="padding-right:0px"><b>Location:</b></div><div class="small-12 large-12 medium-12 columns" style="padding:0px"><b>'+data.location+'</b></div><div class="small-6 large-6 medium-6 columns">&nbsp;</div></div>'+'<div class="row"><div class="small-24 large-24 medium-24 columns">'+description+'</div></div>'+'<div class="row"><div class="small-24 large-24 medium-24 columns">&nbsp</div></div>';

            tooltip.set({
                'content.text': content
            })
            .reposition(event).show(event);
            
        },
        dayClick: function() { tooltip.hide() },
        eventResizeStart: function() { tooltip.hide() },
       // eventDragStart: function() { tooltip.hide() },
        viewDisplay: function() { tooltip.hide() },
        events: jsonEvents
    });
   
	
} 