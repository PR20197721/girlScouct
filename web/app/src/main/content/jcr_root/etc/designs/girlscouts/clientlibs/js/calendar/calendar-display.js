function calendarDisplay(month,year,jsonEvents){
	
	var date = new Date();
    var d = date.getDate();
    var m = date.getMonth();
    var y = date.getFullYear();
	
	if(month!=null && year!=null){
		m = month;
		y = year;
	}
	
    var tooltip = $('<div/>').qtip({
        id: 'fullcalendar',
        prerender: true,
        content: {
            text: ' '
            
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
        show: false,
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
                var content = '<h3><a href="'+data.path+'">'+data.title+'</a></h3>' + '<br/><b>Time: </b>'+data.time+'<br/>'+
                '<b>Date:</b> '+$.datepicker.formatDate('mm/dd/yy',start)+ 
                (data.end && ' to '+$.datepicker.formatDate('mm/dd/yy',end) || '')+'<br/>'+description;

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