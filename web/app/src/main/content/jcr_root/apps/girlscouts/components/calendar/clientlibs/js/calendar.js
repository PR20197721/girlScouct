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
    var date;
    if ($("#calendar-events").attr("data-date") === undefined || !$("#calendar-events").attr("data-date").length) {
        date = moment()._d;
    } else {
        date = $("#calendar-events").attr("data-date");
    }

    var calendarEl = document.getElementById('fullcalendar');
    var calendar = new FullCalendar.Calendar(calendarEl, {
        plugins: ['dayGrid'],
        header: {
            left: 'prev',
            center: 'title',
            right: 'next'
        },
        height: 'auto',
        defaultView: 'dayGridMonth',
        defaultDate: $("#calendar-events").attr("data-date"),
        eventRender: function (info) {
			//GSWP-2198-To hide past events on the calendar
            if (info.event.extendedProps.show != undefined && "false" == info.event.extendedProps.show) {
	            var eventEndDate = moment(info.event.end);
	            var currentDate = moment();
	            if (eventEndDate.diff(currentDate, 'seconds') <= 0) {
					return false;
	            }
            }
            //GSWP-2198-End-To hide past events on the calendar

            var content = '<div class="row"><div class="small-24 large-24 medium-24 columns"><span class="calTitle"><h6><a target="_blank" href="' + info.event.extendedProps.path +
                '"><span class="calTitle">' + info.event.title + '</a></h6></span></div></div>' +
                '<div class="row"><div class="small-4 large-4 medium-4 columns" style="padding-right:0px"><b>Date:</b></div><div class="small-14 large-14 medium-14 columns" style="padding:0px"><b>' + info.event.extendedProps.displayDate + '</b></div><div class="small-6 large-6 medium-6 columns">&nbsp;</div></div>' +
                //'<div class="row"><div class="small-6 large-6 medium-6 columns" style="padding-right:0px"><b>Location:</b></div><div class="small-12 large-12 medium-12 columns" style="padding:0px"><b>' + info.event.extendedProps.location + '</b></div><div class="small-6 large-6 medium-6 columns">&nbsp;</div></div>' + // GSAWDO-85-remove-location-field-from-all-council-websites
                '<div class="row"><div class="small-24 large-24 medium-24 columns">' + info.event.extendedProps.description + '</div></div>' +
                '<div class="row"><div class="small-24 large-24 medium-24 columns">&nbsp</div></div>';
            var tooltip = new Tooltip(info.el, {
                title: content,
                placement: 'top',
                trigger: 'manual',
                html: true,
                container: 'body'
            });
            tooltips.push(tooltip);
            $(info.el).on("click", function (event) {
                event.stopPropagation();
                tooltip.show();
                for (var i = 0; i < tooltips.length; i++) {
                    if (tooltip !== tooltips[i]) {
                        tooltips[i].hide();
                    }
                }
            });
        },
        events: JSON.parse($("#calendar-events").attr("data-event"))
    });

    calendar.render();
    $("#fullcalendar").on("click", function () {
        for (var i = 0; i < tooltips.length; i++) {
            tooltips[i].hide();
        }
    });
}