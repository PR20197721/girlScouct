document.addEventListener('DOMContentLoaded', function() {
    initCalendar();

    // iOS touch fix
    var plat = navigator.platform;
    if( plat.indexOf("iPad") != -1 || plat.indexOf("iPhone") != -1 || plat.indexOf("iPod") != -1 ) {
        $(".fc-event-title").bind('touchend', function() {
            $(this).click();
        });
    }

  });
  function initCalendar() {
    var tooltips = [];
    var calendarEl = document.getElementById('fullcalendar');
    var calendar = new FullCalendar.Calendar(calendarEl, {
      plugins: [ 'dayGrid' ],
      header: {
          left: 'prev',
          center: 'title',
          right : 'next'
      },
      defaultView: 'dayGridMonth',
      eventRender: function(info) {
        var content = '<div class="row"><div class="small-24 large-24 medium-24 columns"><span class="calTitle"><h6><a target="_blank" href="'+info.event.extendedProps.path+
                        '"><span class="calTitle">'+info.event.title+'</a></h6></span></div></div>' +
                        '<div class="row"><div class="small-4 large-4 medium-4 columns" style="padding-right:0px"><b>Date:</b></div><div class="small-14 large-14 medium-14 columns" style="padding:0px"><b>'+info.event.extendedProps.displayDate+'</b></div><div class="small-6 large-6 medium-6 columns">&nbsp;</div></div>'+
                        '<div class="row"><div class="small-6 large-6 medium-6 columns" style="padding-right:0px"><b>Location:</b></div><div class="small-12 large-12 medium-12 columns" style="padding:0px"><b>'+info.event.extendedProps.location+'</b></div><div class="small-6 large-6 medium-6 columns">&nbsp;</div></div>'+
                        '<div class="row"><div class="small-24 large-24 medium-24 columns">'+info.event.extendedProps.description+'</div></div>'+
                        '<div class="row"><div class="small-24 large-24 medium-24 columns">&nbsp</div></div>';




        var tooltip = new Tooltip(info.el, {
          title: content,
          placement: 'top',
          trigger: 'manual',
          html: true,
          container: 'body'
        });
        tooltips.push(tooltip);
        $(info.el).on("click", function(event) {
            event.stopPropagation();
            tooltip.show();
            for(var i = 0; tooltips.length; i++) {
                if(tooltip !== tooltips[i]) {
                    tooltips[i].hide();
                }
             }
        });
        console.log(info.el);
      },
      events: JSON.parse($("#calendar-events").attr("data-event"))
    });

    calendar.render();
    $("#fullcalendar").on("click", function() {
      for(var i = 0; tooltips.length; i++) {
        tooltips[i].hide();
      }
    });
  }