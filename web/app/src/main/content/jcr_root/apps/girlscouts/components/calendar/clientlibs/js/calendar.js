document.addEventListener('DOMContentLoaded', function() {
    var calendarEl = document.getElementById('fullcalendar');
    var calendar = new FullCalendar.Calendar(calendarEl, {
      plugins: [ 'dayGrid' ],
      defaultView: 'dayGridMonth',
      eventRender: function(info) {
        var tooltip = new Tooltip(info.el, {
          title: info.event.extendedProps.description,
          placement: 'top',
          trigger: 'hover',
          container: 'body'
        });
      },
      events: [
                {
                  "title": "Bronze and Silver Award Orientation - LEAD Online!",
                  "displayDate": "Sun, Dec 1, 2019, 12:00 AM EST",
                  "location": "Online",
                  "color": "#F27536",
                  "description": "Bronze and Silver Award Orientation now online! Strongly recommended for volunteers who have girls who want to earn their Bronze or Silver Award!",
                  "start": "2019-12-01",
                  "end": "2020-01-31",
                  "path": "/content/girlscoutseasternmass/en/events-repository/2019/bronze-silver-lead.html"
                },
                {
                  "title": "Pats Peek",
                  "displayDate": "Wed, Jan 1, 2020, 12:00 AM EST",
                  "location": "Pats Peak",
                  "color": "#DD3640",
                  "description": "Girl Scouts, family and friends save!",
                  "start": "2020-01-01",
                  "end": "2020-03-29",
                  "path": "/content/girlscoutseasternmass/en/events-repository/2020/pats-peak.html"
                }
                ]
    });

    calendar.render();
  });