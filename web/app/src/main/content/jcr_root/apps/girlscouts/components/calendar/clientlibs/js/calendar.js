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
  },
  {
    "title": "Online Gold Award Orientation",
    "displayDate": "Wed, Jan 1, 2020, 12:00 AM EST",
    "location": "Online",
    "color": "#F27536",
    "description": "This online orientation is designed to prepare Girl Scouts for each Gold Award step.",
    "start": "2020-01-01",
    "end": "2020-02-29",
    "path": "/content/girlscoutseasternmass/en/events-repository/2019/online_gold.html"
  },
  {
    "title": "Bronze and Silver Award Orientation - LEAD Online!",
    "displayDate": "Wed, Jan 1, 2020, 12:00 AM EST",
    "location": "Online",
    "color": "#F27536",
    "description": "Bronze and Silver Award Orientation now online! Strongly recommended for volunteers who have girls who want to earn their Bronze or Silver Award!",
    "start": "2020-01-01",
    "end": "2020-02-29",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/bronze-silver-lead.html"
  },
  {
    "title": "MEdia Online Journey",
    "displayDate": "Tue, Jan 14, 2020, 6:30 PM - Tue, Feb 11, 2020, 7:30 PM EST",
    "location": "Online",
    "color": "#AB218E",
    "description": "In this five week online journey we will meet every Tuesday night to discuss media: how we consume it, interpret it, and create it.",
    "start": "2020-01-14",
    "end": "2020-02-11",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/online_journey_s.html"
  },
  {
    "title": "Senior Think Like a Programmer Online Journey",
    "displayDate": "Wed, Jan 15, 2020, 6:30 PM - Wed, Feb 12, 2020, 7:30 PM EST",
    "location": "Online",
    "color": "#AB218E",
    "description": "In this five week long online journey, you will virtually join together on Wednesday nights to work through the Think Like a Programmer journey.",
    "start": "2020-01-15",
    "end": "2020-02-12",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/online_journey_a.html"
  },
  {
    "title": "Ambassador Think Like a Programmer Online Journey",
    "displayDate": "Thu, Jan 16, 2020, 6:30 PM - Thu, Feb 13, 2020, 7:30 PM EST",
    "location": "Online",
    "color": "#AB218E",
    "description": "In this five week long online journey, you will virtually join together on Thursday nights to work through the Think Like a Programmer journey.",
    "start": "2020-01-16",
    "end": "2020-02-13",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/online_journey_c.html"
  },
  {
    "title": "Robotics Badge Series",
    "displayDate": "Sun, Jan 26, 2020, 3:30 PM - Sun, Mar 8, 2020, 5:30 PM EST",
    "location": "Empow Studios - Lexington",
    "color": "#AB218E",
    "description": "Earn all three Robotics Badges in this series!",
    "start": "2020-01-26",
    "end": "2020-03-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/robotics_series.html"
  },
  {
    "title": "Mean Girls",
    "displayDate": "Tue, Jan 28, 2020, 7:30 PM - Sun, Feb 9, 2020, 6:30 PM EST",
    "location": "Citizens Bank Opera House (Boston)",
    "color": "#DD3640",
    "description": "Special offer for Girl Scouts!",
    "start": "2020-01-28",
    "end": "2020-02-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/mean_girls.html"
  },
  {
    "title": "January/February Gold Award Interviews and Final Presentation",
    "displayDate": "Thu, Jan 30, 2020, 12:00 AM EST",
    "location": "GSEMA Service Centers",
    "color": "#eb0789",
    "description": "Each Girl Scout is asked to participate in an interview to discuss their project plan for approval and to make a final presentation after their final report is submitted.",
    "start": "2020-01-30",
    "end": "2020-02-06",
    "path": "/content/girlscoutseasternmass/en/events-repository/2019/goldaward-interview2.html"
  },
  {
    "title": "Arlington Sample Troop Meeting ",
    "displayDate": "Thu, Jan 30, 2020, 5:30 PM - 6:30 PM",
    "location": "Fox Library, Lower Level Entrance",
    "color": "#00ae58",
    "description": "Sample Girl Scout meeting for girls in grades K and 1.",
    "start": "2020-01-30",
    "end": "2020-01-30",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/arlington_sample_tro.html"
  },
  {
    "title": "Discover Middleton Girl Scouts",
    "displayDate": "Thu, Jan 30, 2020, 6:00 PM - 7:00 PM",
    "location": "Flint Library",
    "color": "#00ae58",
    "description": "Sample Girl Scout meeting for kindergarten girls.",
    "start": "2020-01-30",
    "end": "2020-01-30",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/discover_middleton_g.html"
  },
  {
    "title": "Canvas Painting",
    "displayDate": "Fri, Jan 31, 2020, 4:30 PM - 6:00 PM EST",
    "location": "Art Signals (Maynard)",
    "color": "#f9cd00",
    "description": "Come get creative! We will walk you through how to make your own canvas painting.",
    "start": "2020-01-31",
    "end": "2020-01-31",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/canvas_painting_1_31.html"
  },
  {
    "title": "Caring for Stranded Ocean Animals: Brownie",
    "displayDate": "Sat, Feb 1, 2020, 9:00 AM - 12:00 PM EST",
    "location": "National Marine Life Center",
    "color": "#AB218E",
    "description": "Learn about marine animal rescue, rehabilitation, and release through hands-on activities and specimen exploration. ",
    "start": "2020-02-01",
    "end": "2020-02-01",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/caring_ocean_2_1.html"
  },
  {
    "title": "Girl Scouts Engineer Magic",
    "displayDate": "Sat, Feb 1, 2020, 9:30 AM - 3:00 PM EST",
    "location": "Wentworth Institute of Technology",
    "color": "#AB218E",
    "description": "Discover the exciting world of science and engineering through workshops hosted by Wentworth Institute of Technology and the Society of Women Engineers. ",
    "start": "2020-02-01",
    "end": "2020-02-01",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/engineer_magic.html"
  },
  {
    "title": "Junior First Aid Badge",
    "displayDate": "Sat, Feb 1, 2020, 10:00 AM - 12:00 PM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#68c8c6",
    "description": "You’ll leave with a mini first aid kit, a packet of helpful information, a First Aid badge, and lots of skills!",
    "start": "2020-02-01",
    "end": "2020-02-01",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/cadettefa_2_1.html"
  },
  {
    "title": "Cadette First Aid Badge",
    "displayDate": "Sat, Feb 1, 2020, 10:00 AM - 12:00 PM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#68c8c6",
    "description": "Learn how to be prepared for most urgent first aid issues. ",
    "start": "2020-02-01",
    "end": "2020-02-01",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/cadette_first_aid.html"
  },
  {
    "title": "What Robots Do Badge",
    "displayDate": "Sat, Feb 1, 2020, 10:00 AM - 12:30 PM EST",
    "location": "Adventure Code Academy",
    "color": "#AB218E",
    "description": "Learn about the many things that robots can be designed to do while participating in exciting hands-on activities.",
    "start": "2020-02-01",
    "end": "2020-02-01",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/robots_2_1.html"
  },
  {
    "title": "Glass Pendant Making",
    "displayDate": "Sat, Feb 1, 2020, 12:00 PM - 2:00 PM EST",
    "location": "North Shore Glass School",
    "color": "#F9CD00",
    "description": "Learn the ancient art of flameworking and make your own beautiful pendant.",
    "start": "2020-02-01",
    "end": "2020-02-01",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/glass_pendant_2_1.html"
  },
  {
    "title": "Winter Explorers",
    "displayDate": "Sat, Feb 1, 2020, 12:00 PM - 2:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#C4D82E",
    "description": "Take a nature hike, play some games, and learn about local wildlife while exploring camp",
    "start": "2020-02-01",
    "end": "2020-02-01",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/winter_explorers_2_1.html"
  },
  {
    "title": "Maple Sugaring Time",
    "displayDate": "Sat, Feb 1, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#F9CD00",
    "description": "Identify sugar maples and make your own syrup!",
    "start": "2020-02-01",
    "end": "2020-02-01",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/maple_sugaring_2_1.html"
  },
  {
    "title": "Junior First Aid Badge",
    "displayDate": "Sat, Feb 1, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#68c8c6",
    "description": "This interactive class will review and practice the handling of urgent situations.",
    "start": "2020-02-01",
    "end": "2020-02-01",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/juniorfa_3_14.html"
  },
  {
    "title": "Programming Robots Badge",
    "displayDate": "Sat, Feb 1, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Adventure Code Academy",
    "color": "#AB218E",
    "description": "You’ll engage in activities to create a program that can be run by a robot!",
    "start": "2020-02-01",
    "end": "2020-02-01",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/programming_robots_2_1.html"
  },
  {
    "title": "Caring for Stranded Ocean Animals: Junior",
    "displayDate": "Sat, Feb 1, 2020, 1:00 PM - 4:00 PM EST",
    "location": "National Marine Life Center",
    "color": "#AB218E",
    "description": "Learn about marine animal rescue, rehabilitation, and release through hands-on activities and specimen exploration. ",
    "start": "2020-02-01",
    "end": "2020-02-01",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/caring_ocean_2_11.html"
  },
  {
    "title": "Framingham 1st Grade Daisy Troop Info Session",
    "displayDate": "Sat, Feb 1, 2020, 3:00 PM - 4:00 PM",
    "location": "McAuliffe Branch Library, Children's Room",
    "color": "#00ae58",
    "description": "",
    "start": "2020-02-01",
    "end": "2020-02-01",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/framingham_1st_grade.html"
  },
  {
    "title": "Winter Trekkers",
    "displayDate": "Sat, Feb 1, 2020, 3:00 PM - 5:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#C4D82E",
    "description": "Explore camp in a new way by hiking while hunting for geocaches.",
    "start": "2020-02-01",
    "end": "2020-02-01",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/winter_trekkers_2_1.html"
  },
  {
    "title": "Canvas Painting",
    "displayDate": "Sat, Feb 1, 2020, 3:30 PM - 5:00 PM EST",
    "location": "Look What I Made (N. Reading)",
    "color": "#f9cd00",
    "description": "Come get creative! We will walk you through how to make your own canvas painting.",
    "start": "2020-02-01",
    "end": "2020-02-01",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/canvas_painting_2_1.html"
  },
  {
    "title": "Family Winter Adventure",
    "displayDate": "Sat, Feb 1, 2020, 6:00 PM - 8:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#C4D82E",
    "description": "Come explore fields and trails as a family on a night hike around camp. ",
    "start": "2020-02-01",
    "end": "2020-02-01",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/family_winter_2_1.html"
  },
  {
    "title": "Winter Trekkers",
    "displayDate": "Sun, Feb 2, 2020, 10:00 AM - 12:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#C4D82E",
    "description": "Explore camp in a new way by hiking while hunting for geocaches.",
    "start": "2020-02-02",
    "end": "2020-02-02",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/winter_trekkers_2_2.html"
  },
  {
    "title": "Dancer Badge with Abilities Dance",
    "displayDate": "Sun, Feb 2, 2020, 1:00 PM - 2:30 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#F9CD00",
    "description": "Take a dance class with the professional dancers of Abilities Dance—no experience required!",
    "start": "2020-02-02",
    "end": "2020-02-02",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/dancer_2_2.html"
  },
  {
    "title": "Staying Fit Badge",
    "displayDate": "Sun, Feb 2, 2020, 1:00 PM - 2:30 PM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#F9CD00",
    "description": "Earn your staying fit badge with the team at Bring the Hoopla. ",
    "start": "2020-02-02",
    "end": "2020-02-02",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/staying_fit.html"
  },
  {
    "title": "Winter Explorers",
    "displayDate": "Sun, Feb 2, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#C4D82E",
    "description": "Take a nature hike, play some games, and learn about local wildlife while exploring camp",
    "start": "2020-02-02",
    "end": "2020-02-02",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/winter_explorers_2_2.html"
  },
  {
    "title": "Natick Sample Meeting",
    "displayDate": "Sun, Feb 2, 2020, 3:00 PM - 4:00 PM",
    "location": "Morse Institute Library, Community Room",
    "color": "#00ae58",
    "description": "",
    "start": "2020-02-02",
    "end": "2020-02-02",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/natick_sample_meetin.html"
  },
  {
    "title": "Fair Play Badge",
    "displayDate": "Sun, Feb 2, 2020, 3:00 PM - 4:30 PM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#68c8c6",
    "description": "Girls will be treated to a special Hoopla demonstration, warm-up and stretching, and lots of hoopin’ fun!",
    "start": "2020-02-02",
    "end": "2020-02-02",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/fair_play_2_2.html"
  },
  {
    "title": "Glass Fusing",
    "displayDate": "Sun, Feb 2, 2020, 3:30 PM - 4:30 PM EST",
    "location": "Art Signals (Maynard)",
    "color": "#f9cd00",
    "description": "Learn to make your own fused glass masterpiece! ",
    "start": "2020-02-02",
    "end": "2020-02-02",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/glass_fusing_2_2.html"
  },
  {
    "title": "Programming Robots Badge: Junior",
    "displayDate": "Sun, Feb 2, 2020, 3:30 PM - 5:30 PM EST",
    "location": "Empow Studios - Newton",
    "color": "#AB218E",
    "description": "Earn the Programming Robots badge while using LEGO® EV3 MINDSTORMS.",
    "start": "2020-02-02",
    "end": "2020-02-02",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/programming_robots_2_2.html"
  },
  {
    "title": "Programming Robots Badge: Brownie",
    "displayDate": "Sun, Feb 2, 2020, 3:30 PM - 5:30 PM EST",
    "location": "Empow Studios - Lexington",
    "color": "#AB218E",
    "description": "Earn the Programming Robots badge while using LEGO® EV3 MINDSTORMS.",
    "start": "2020-02-02",
    "end": "2020-02-02",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/programming_robotsl_2_2.html"
  },
  {
    "title": "Camp Information Night",
    "displayDate": "Wed, Feb 5, 2020, 6:30 PM - 8:00 PM",
    "location": "Community Room, McAuliffe Branch Framingham Library",
    "color": "#00ae58",
    "description": "",
    "start": "2020-02-05",
    "end": "2020-02-05",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/camp_information_nig.html"
  },
  {
    "title": "Summer Camp & Troop Info Night",
    "displayDate": "Thu, Feb 6, 2020, 6:00 PM - 7:00 PM",
    "location": "Milton Public Library",
    "color": "#00ae58",
    "description": "Learn about camp and troop opportunities.",
    "start": "2020-02-06",
    "end": "2020-02-06",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/summer_camp_troop_in_1509185101.html"
  },
  {
    "title": "Potter Badge",
    "displayDate": "Fri, Feb 7, 2020, 4:00 PM - 5:30 PM EST",
    "location": "Look What I Made (N. Reading)",
    "color": "#f9cd00",
    "description": "Come have some fun getting your hands dirty and earn your Potter Badge!",
    "start": "2020-02-07",
    "end": "2020-02-07",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/potter_badge_2_7.html"
  },
  {
    "title": "Sewing: Girl Scout Fabric Bag",
    "displayDate": "Fri, Feb 7, 2020, 6:00 PM - 8:00 PM EST",
    "location": "Sandi's Sewing and Design (Bridgewater)",
    "color": "#68c8c6",
    "description": "Make your own tote bag from Girl Scout fabric and then wear it with Girl Scout pride.",
    "start": "2020-02-07",
    "end": "2020-02-07",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/sandis_sewing_2_7.html"
  },
  {
    "title": "Dive-in Movie",
    "displayDate": "Fri, Feb 7, 2020, 6:30 PM - 8:15 PM EST",
    "location": "Raynham Athletic Club",
    "color": "#C4D82E",
    "description": "Enjoy a movie on the big screen while swimming in a heated pool. ",
    "start": "2020-02-07",
    "end": "2020-02-07",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/dive_in_2_7.html"
  },
  {
    "title": "Junior Safety Award",
    "displayDate": "Sat, Feb 8, 2020, 9:00 AM - 11:00 AM EST",
    "location": "Camp Cedar Hill",
    "color": "#68C8C6",
    "description": "Earn your Safety Award with experts from the Injury Prevention Team at Boston Children’s Hospital. ",
    "start": "2020-02-08",
    "end": "2020-02-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/junior_safety_2_8.html"
  },
  {
    "title": "MEDIC First Aid and CPR Recertification",
    "displayDate": "Sat, Feb 8, 2020, 9:00 AM - 12:00 PM EST",
    "location": "GSEMA Service Center - Middleboro",
    "color": "#F27536",
    "description": "This course is for those whose First Aid and CPR certifications are about to expire or have expired within 30 days of the listed session.",
    "start": "2020-02-08",
    "end": "2020-02-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/medic_recertification.html"
  },
  {
    "title": "Program Aid Core and Camp Training Overnight",
    "displayDate": "Sat, Feb 8, 2020, 9:00 AM - Sun, Feb 9, 2020, 10:00 AM EST",
    "location": "Camp Maude Eaton",
    "color": "#C4D82E",
    "description": "This program combines Program Aide Core Training and Program Aide Camp Skills Training into one overnight experience.",
    "start": "2020-02-08",
    "end": "2020-02-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/program_aid.html"
  },
  {
    "title": "Celebrating Community and Good Neighbor Badges",
    "displayDate": "Sat, Feb 8, 2020, 9:30 AM - 11:30 AM EST",
    "location": "The Edward M Kennedy Institute for the US Senate",
    "color": "#68c8c6",
    "description": "With your Girl Scout sisters, read a story about the Statue of Liberty and learn how Lady Liberty welcomes diverse neighbors into our American communities. ",
    "start": "2020-02-08",
    "end": "2020-02-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/celebrating_community.html"
  },
  {
    "title": "Winter Explorers",
    "displayDate": "Sat, Feb 8, 2020, 10:00 AM - 12:00 PM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#C4D82E",
    "description": "Take a nature hike, play some games, and learn about local wildlife while exploring camp",
    "start": "2020-02-08",
    "end": "2020-02-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/winter_explorers_2_8.html"
  },
  {
    "title": "Expedition to Mars",
    "displayDate": "Sat, Feb 8, 2020, 10:00 AM - 12:00 PM EST",
    "location": "Christa McCauliffe Center at Framingham State University",
    "color": "#AB218E",
    "description": "Experience space adventure at the Christa McAuliffe Center at Framingham State University.",
    "start": "2020-02-08",
    "end": "2020-02-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/expedition_2_8.html"
  },
  {
    "title": "Glass Suncatcher",
    "displayDate": "Sat, Feb 8, 2020, 10:00 AM - 12:00 PM EST",
    "location": "The Glass Bar",
    "color": "#F9CD00",
    "description": "Develop your own designs as you learn the basics of cutting glass and fusing it in the kiln. ",
    "start": "2020-02-08",
    "end": "2020-02-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/glass_suncatcher_2_81.html"
  },
  {
    "title": "What Robots Do Badge",
    "displayDate": "Sat, Feb 8, 2020, 10:00 AM - 12:30 PM EST",
    "location": "Adventure Code Academy",
    "color": "#AB218E",
    "description": "Learn about the many things that robots can be designed to do while participating in exciting hands-on activities.",
    "start": "2020-02-08",
    "end": "2020-02-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/robots_2_8.html"
  },
  {
    "title": "Blankets for Boston",
    "displayDate": "Sat, Feb 8, 2020, 11:00 AM - 1:00 PM EST",
    "location": "Boston University",
    "color": "#68c8c6",
    "description": "Join the Kappa Deltas in learning about the community issue of homelessness and what you can do to help. ",
    "start": "2020-02-08",
    "end": "2020-02-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/blankets.html"
  },
  {
    "title": "Brownie Safety Award",
    "displayDate": "Sat, Feb 8, 2020, 12:00 PM - 2:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#68C8C6",
    "description": "Earn your Safety Award with experts from the Injury Prevention Team at Boston Children’s Hospital. ",
    "start": "2020-02-08",
    "end": "2020-02-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/brownie_safety_2_8.html"
  },
  {
    "title": "Glass Suncatcher",
    "displayDate": "Sat, Feb 8, 2020, 12:00 PM - 2:00 PM EST",
    "location": "North Shore Glass School",
    "color": "#F9CD00",
    "description": "Learn how to make stunning suncatchers to place in your window! ",
    "start": "2020-02-08",
    "end": "2020-02-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/glass_suncatcher_2_8.html"
  },
  {
    "title": "Winter Trekkers",
    "displayDate": "Sat, Feb 8, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#C4D82E",
    "description": "Explore camp in a new way by hiking while hunting for geocaches.",
    "start": "2020-02-08",
    "end": "2020-02-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/winter_trekkers_2_8.html"
  },
  {
    "title": "Designing Robots Badge",
    "displayDate": "Sat, Feb 8, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Adventure Code Academy",
    "color": "#AB218E",
    "description": "Earn the Designing Robots badge through hands-on creative activities.",
    "start": "2020-02-08",
    "end": "2020-02-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/designing_robots_2_8.html"
  },
  {
    "title": "Sewing: Hair Bows, Headbands and Scrunchies",
    "displayDate": "Sat, Feb 8, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Sandi's Sewing and Design (Bridgewater)",
    "color": "#F9CD00",
    "description": "Learn quick sewing techniques for making some fun hair accessories. ",
    "start": "2020-02-08",
    "end": "2020-02-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/sandis_sewing_2_8.html"
  },
  {
    "title": "MEDIC First Aid and CPR Blended Learning",
    "displayDate": "Sat, Feb 8, 2020, 1:00 PM - 5:00 PM EST",
    "location": "GSEMA Service Center - Middleboro",
    "color": "#F27536",
    "description": "This course is offered in two parts: an online program and a hands-on skills session with an instructor. ",
    "start": "2020-02-08",
    "end": "2020-02-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/medic_blended4.html"
  },
  {
    "title": "Wheelock Family Theatre: Little Women",
    "displayDate": "Sat, Feb 8, 2020, 2:00 PM - 4:00 PM EST",
    "location": "Wheelock Family Theatre",
    "color": "#f9cd00",
    "description": "Roald Dahl’s timeless musical story of the world-famous candy man and his quest to find an heir comes to chocolate-covered life. ",
    "start": "2020-02-08",
    "end": "2020-02-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/little_women.html"
  },
  {
    "title": "Daisy Climbing Adventure Badge",
    "displayDate": "Sat, Feb 8, 2020, 4:00 PM - 5:30 PM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#C4D82E",
    "description": "Girls will spend the first half of the program on the ground learning the lingo and how to climb safely. Then they will boulder across our indoor wall while building up their skills.",
    "start": "2020-02-08",
    "end": "2020-02-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/daisy_climbing.html"
  },
  {
    "title": "Winter Explorers",
    "displayDate": "Sun, Feb 9, 2020, 10:00 AM - 3:00 PM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#C4D82E",
    "description": "Take a nature hike, play some games, and learn about local wildlife while exploring camp. 2 sessions.",
    "start": "2020-02-09",
    "end": "2020-02-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/winter_explorers_2_9.html"
  },
  {
    "title": "Puzzle Break: The Grimm Escape",
    "displayDate": "Sun, Feb 9, 2020, 12:00 PM - 1:00 PM EST",
    "location": "Puzzle Break",
    "color": "#F9CD00",
    "description": "Solve puzzles, decode clues, and work together to “escape the room” at Puzzle Break",
    "start": "2020-02-09",
    "end": "2020-02-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/puzzle_break_2_9.html"
  },
  {
    "title": "Snow Much Fun: Pembroke",
    "displayDate": "Sun, Feb 9, 2020, 1:00 PM - 3:00 PM",
    "location": "Pembroke Public Library",
    "color": "#00ae58",
    "description": "Come join us at our Snow Much Fun family event for girls in kindergarten through third grade. ",
    "start": "2020-02-09",
    "end": "2020-02-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/snow_much_fun_pembro.html"
  },
  {
    "title": "Glass Pendant Making",
    "displayDate": "Sun, Feb 9, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Pairpoint Glass School",
    "color": "#F9CD00",
    "description": "Try your hand at making your own glass pendant with the experts at Pairpoint Glass School.",
    "start": "2020-02-09",
    "end": "2020-02-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/glass_pendant_2_9.html"
  },
  {
    "title": "Maple Sugaring Time",
    "displayDate": "Sun, Feb 9, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Camp Runels",
    "color": "#F9CD00",
    "description": "Identify sugar maples and make your own syrup!",
    "start": "2020-02-09",
    "end": "2020-02-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/maple_sugaring_2_9.html"
  },
  {
    "title": "Scribe: Jelly Bean Reviews",
    "displayDate": "Sun, Feb 9, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#F9CD00",
    "description": "Piece together crumpled letters, movie ticket stubs, and a slew of other items to write a short script or piece of fiction about the person who threw away these items.",
    "start": "2020-02-09",
    "end": "2020-02-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/scribe.html"
  },
  {
    "title": "Polished, Put-Together, and Proud",
    "displayDate": "Sun, Feb 9, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Fashion Focus",
    "color": "#68c8c6",
    "description": "With your Girl Scout sisters, explore the power of self-care, dressing for success, personal hygiene, and a good first impression.",
    "start": "2020-02-09",
    "end": "2020-02-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/fashion_focus.html"
  },
  {
    "title": "Programming Robots Badge at New England Sci-Tech",
    "displayDate": "Sun, Feb 9, 2020, 1:00 PM - 3:00 PM EST",
    "location": "New England Sci-Tech",
    "color": "#AB218E",
    "description": "Have fun while you develop your design and programming skills by creating software to control a robot.",
    "start": "2020-02-09",
    "end": "2020-02-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/prog_robots.html"
  },
  {
    "title": "Book Artist Badge with Children's Art Lab",
    "displayDate": "Sun, Feb 9, 2020, 1:00 PM - 3:00 PM EST",
    "location": "GSEMA Service Center - Middleboro",
    "color": "#F9CD00",
    "description": "Explore the art of book binding! ",
    "start": "2020-02-09",
    "end": "2020-02-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/childrens_art_lab_2_9.html"
  },
  {
    "title": "Camp Maude Eaton Reunion",
    "displayDate": "Sun, Feb 9, 2020, 1:00 PM - 5:00 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#C4D82E",
    "description": "Come back to camp for a day of fun with your summer camp friends and counselors. ",
    "start": "2020-02-09",
    "end": "2020-02-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/maude_reunion.html"
  },
  {
    "title": "New Cuisines Badge",
    "displayDate": "Sun, Feb 9, 2020, 2:00 PM - 3:30 PM EST",
    "location": "Taste Buds Kitchen (Beverly)",
    "color": "#F9CD00",
    "description": "Create authentic Thai dishes that will please your every tastebud and impress your family and friends.",
    "start": "2020-02-09",
    "end": "2020-02-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/taste_buds_2_9.html"
  },
  {
    "title": "Mystery Basket",
    "displayDate": "Sun, Feb 9, 2020, 2:00 PM - 3:30 PM EST",
    "location": "Taste Buds Kitchen (North Andover)",
    "color": "#F9CD00",
    "description": "Add your own spin on some classic recipes to create a delicious one-of-a-kind dish.",
    "start": "2020-02-09",
    "end": "2020-02-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/taste_buds_2_91.html"
  },
  {
    "title": "Glass Fusing",
    "displayDate": "Sun, Feb 9, 2020, 3:30 PM - 4:30 PM EST",
    "location": "Look What I Made (N. Reading)",
    "color": "#f9cd00",
    "description": "Learn to make your own fused glass masterpiece! ",
    "start": "2020-02-09",
    "end": "2020-02-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/glass_fusing_2_9.html"
  },
  {
    "title": "Programming Robots Badge: Senior/Ambassador",
    "displayDate": "Sun, Feb 9, 2020, 3:30 PM - 5:30 PM EST",
    "location": "Empow Studios - Newton",
    "color": "#AB218E",
    "description": "Earn the Programming Robots badge while using LEGO® EV3 MINDSTORMS.",
    "start": "2020-02-09",
    "end": "2020-02-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/programming_robots_2_9.html"
  },
  {
    "title": "Polished, Put-Together, and Proud",
    "displayDate": "Sun, Feb 9, 2020, 3:30 PM - 5:30 PM EST",
    "location": "Fashion Focus",
    "color": "#68c8c6",
    "description": "With your Girl Scout sisters, explore the power of self-care, dressing for success, personal hygiene, and a good first impression.",
    "start": "2020-02-09",
    "end": "2020-02-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/fashion_focus1.html"
  },
  {
    "title": "Screenwriter Badge: “What’s in the Trash?”",
    "displayDate": "Sun, Feb 9, 2020, 3:30 PM - 5:30 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#F9CD00",
    "description": "Piece together crumpled letters, movie ticket stubs, and a slew of other items to write a short script or piece of fiction about the person who threw away these items.",
    "start": "2020-02-09",
    "end": "2020-02-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/screenwriter.html"
  },
  {
    "title": "New Cuisine Badge",
    "displayDate": "Sun, Feb 9, 2020, 4:00 PM - 5:30 PM EST",
    "location": "Mix It Up Kitchen",
    "color": "#68c8c6",
    "description": "Leave your suitcase behind but bring along your appetite!",
    "start": "2020-02-09",
    "end": "2020-02-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/new_cuisine_2_9.html"
  },
  {
    "title": "Scuba Diving with East Coast Divers",
    "displayDate": "Sun, Feb 9, 2020, 5:00 PM - 7:30 PM EST",
    "location": "Evelyn Kirrane Aquatic Center",
    "color": "#f9cd00",
    "description": " Learn the basics of using scuba gear and beginner scuba skills, then play underwater games. 2 sessions.",
    "start": "2020-02-09",
    "end": "2020-02-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/scuba_2_9.html"
  },
  {
    "title": "Framingham Camp Info Night",
    "displayDate": "Mon, Feb 10, 2020, 6:30 PM - 8:00 PM",
    "location": "McAuliffe Branch, Community Room",
    "color": "#00ae58",
    "description": "",
    "start": "2020-02-10",
    "end": "2020-02-10",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/framingham_camp_info.html"
  },
  {
    "title": "Intro to Flameworking",
    "displayDate": "Sat, Feb 15, 2020, 12:00 PM - 2:00 PM EST",
    "location": "North Shore Glass School",
    "color": "#F9CD00",
    "description": "Try your hand at the ancient art of flameworking using borosilicate glass! ",
    "start": "2020-02-15",
    "end": "2020-02-15",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/flameworking_2_15.html"
  },
  {
    "title": "Sewing: Harry Potter Cinch Backpack",
    "displayDate": "Sat, Feb 15, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Sandi's Sewing and Design (Bridgewater)",
    "color": "#68c8c6",
    "description": "You’ll always be ready for a magical adventure with a Harry Potter themed backpack! ",
    "start": "2020-02-15",
    "end": "2020-02-15",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/sandis_sewing_2_15.html"
  },
  {
    "title": "Boston Pride Pro Women's Hockey",
    "displayDate": "Sat, Feb 15, 2020, 6:30 PM EST",
    "location": "Warrior Ice Arena - Brighton",
    "color": "#DD3640",
    "description": "Girl Scouts, friends and family save on tickets!",
    "start": "2020-02-15",
    "end": "2020-02-15",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/pride_hockey11.html"
  },
  {
    "title": "Monster Jam",
    "displayDate": "Sun, Feb 16, 2020, 1:00 PM EST",
    "location": "DCU Center - Worcester",
    "color": "#DD3640",
    "description": "Special offer for Girl Scouts!",
    "start": "2020-02-16",
    "end": "2020-02-16",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/monster_jam.html"
  },
  {
    "title": "We Did It For You! Women's Journey Through History",
    "displayDate": "Sun, Feb 16, 2020, 1:00 PM EST",
    "location": "Marilyn Rodman Performing Arts Center",
    "color": "#DD3640",
    "description": "Special offer for Girl Scouts!",
    "start": "2020-02-16",
    "end": "2020-02-16",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/we_did_it_for_you.html"
  },
  {
    "title": "Go for Gold: Writing a Gold Award Proposal",
    "displayDate": "Sun, Feb 16, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#eb0789",
    "description": "Got an idea for a Gold Award Project? Come meet with members of the GSEMA Gold Award Committee for support!",
    "start": "2020-02-16",
    "end": "2020-02-16",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/gold_workshop_2_16.html"
  },
  {
    "title": "Synchronized Swimming",
    "displayDate": "Sun, Feb 16, 2020, 2:00 PM - 4:00 PM EST",
    "location": "Andover/N. Andover YMCA",
    "color": "#f9cd00",
    "description": "Learn basic skills, such as ballet legs and pop-ups, and even create your own routine.",
    "start": "2020-02-16",
    "end": "2020-02-16",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/synchronized_2_9.html"
  },
  {
    "title": "Winter Vacation Camp",
    "displayDate": "Mon, Feb 17, 2020, 12:30 PM - Fri, Feb 21, 2020, 4:00 PM EST",
    "location": "Camp Wabasso",
    "color": "#C4D82E",
    "description": "Get the most out of your winter vacation week at Camp Wabasso!",
    "start": "2020-02-17",
    "end": "2020-02-21",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/winter_camp.html"
  },
  {
    "title": "Save the Date! Winter Vacation Adventures",
    "displayDate": "Tue, Feb 18, 2020, 9:00 AM - Fri, Feb 21, 2020, 4:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#C4D82E",
    "description": "Join us for a healthy, technology-free way to spend your week off from school.",
    "start": "2020-02-18",
    "end": "2020-02-21",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/winter_adv.html"
  },
  {
    "title": "Great Race Challenge",
    "displayDate": "Tue, Feb 18, 2020, 9:00 AM - Fri, Feb 21, 2020, 4:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#ab218e",
    "description": "Discover the exhilaration of racing as you ignite your creativity designing the ultimate wooden race car, paper airplane, balloon car, and rope runner!",
    "start": "2020-02-18",
    "end": "2020-02-21",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/great_race.html"
  },
  {
    "title": "Winter Vacation Adventures",
    "displayDate": "Tue, Feb 18, 2020, 9:00 AM - Fri, Feb 21, 2020, 4:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#C4D82E",
    "description": "Escape those winter-blues by joining us for the best winter break ever!",
    "start": "2020-02-18",
    "end": "2020-02-21",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/winter_adventures.html"
  },
  {
    "title": "Geneaology: Researching Your Roots",
    "displayDate": "Tue, Feb 18, 2020, 9:30 AM - 1:00 PM EST",
    "location": "New England Historical Genealogical Society (Boston)",
    "color": "#f9cd00",
    "description": "Discover your family story and meet the hundreds of ancestors who came before you!",
    "start": "2020-02-18",
    "end": "2020-02-18",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/geneaology.html"
  },
  {
    "title": "My Family Story Badge",
    "displayDate": "Tue, Feb 18, 2020, 11:00 AM - 1:00 PM EST",
    "location": "New England Historical Genealogical Society (Boston)",
    "color": "#f9cd00",
    "description": "Become a family history detective!",
    "start": "2020-02-18",
    "end": "2020-02-18",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/my_family_story.html"
  },
  {
    "title": "Snow Much Fun: Plymouth",
    "displayDate": "Tue, Feb 18, 2020, 1:00 PM - 3:00 PM",
    "location": "Camp Wind-in-the-Pines",
    "color": "#00ae58",
    "description": "Come join us at our Snow Much Fun family event for girls in kindergarten through third grade. ",
    "start": "2020-02-18",
    "end": "2020-02-18",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/snow_much_fun_plymou.html"
  },
  {
    "title": "Wayland Daisy Sampler",
    "displayDate": "Wed, Feb 19, 2020, 6:00 PM - 7:00 PM",
    "location": "Wayland Free Public Library",
    "color": "#00ae58",
    "description": "",
    "start": "2020-02-19",
    "end": "2020-02-19",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/wayland_daisy_sample.html"
  },
  {
    "title": "Leadership Development Day",
    "displayDate": "Thu, Feb 20, 2020, 12:00 PM - 3:00 PM EST",
    "location": "State Street",
    "color": "#F27536",
    "description": "Whether you’re interested in earning the Gold Award or already looking ahead to college and careers, female leaders from State Street Corporation are eager to mentor you.",
    "start": "2020-02-20",
    "end": "2020-02-20",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/leadership_development.html"
  },
  {
    "title": "Bronze and Silver Award Online Informational Session",
    "displayDate": "Thu, Feb 20, 2020, 6:00 PM - 7:00 PM EST",
    "location": "Online",
    "color": "#eb0789",
    "description": "Join us for an online session to learn more about the Bronze and Silver Award.",
    "start": "2020-02-20",
    "end": "2020-02-20",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/bronze_silver_info_2_20.html"
  },
  {
    "title": "Think Like an Engineer at Eversource",
    "displayDate": "Fri, Feb 21, 2020, 10:00 AM - 4:00 PM EST",
    "location": "Eversource",
    "color": "#f9cd00",
    "description": "Girls discover how to think like an engineer by participating in hands-on design challenges.",
    "start": "2020-02-21",
    "end": "2020-02-21",
    "path": "/content/girlscoutseasternmass/en/events-repository/2019/eversource.html"
  },
  {
    "title": "Youth Mental Health First Aid",
    "displayDate": "Sat, Feb 22, 2020, 8:00 AM - 6:00 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#BCBEC0",
    "description": "Introduces participants to the unique risk factors and warning signs of mental health problems in adolescents, builds understanding of the importance of early intervention, and teaches individuals how to help an adolescent in crisis or experiencing a mental health challenge. ",
    "start": "2020-02-22",
    "end": "2020-02-22",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/youth-mental-health11.html"
  },
  {
    "title": "Adventures in Storytelling",
    "displayDate": "Sat, Feb 22, 2020, 10:00 AM - 2:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#f9cd00",
    "description": "Write your own book with Kara LaReau, award-winning author of the Infamous Ratsos series.",
    "start": "2020-02-22",
    "end": "2020-02-22",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/adventures_storytelling.html"
  },
  {
    "title": "Adventures in Storytelling",
    "displayDate": "Sat, Feb 22, 2020, 3:00 PM - 5:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#f9cd00",
    "description": "Meet Kara LaReau, award-winning author of the Infamous Ratsos series.",
    "start": "2020-02-22",
    "end": "2020-02-22",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/adventures_storytelling1.html"
  },
  {
    "title": "Innocent or Guilty",
    "displayDate": "Sun, Feb 23, 2020, 9:00 AM - 2:00 PM EST",
    "location": "Northeastern University",
    "color": "#68c8c6",
    "description": "Learn what it’s like to serve on a jury panel as you work together to decide the verdict.",
    "start": "2020-02-23",
    "end": "2020-02-23",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/innocent_guilty.html"
  },
  {
    "title": "Art and Yoga: Daisy/Brownie",
    "displayDate": "Sun, Feb 23, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#F9CD00",
    "description": "Use your imagination and experiment with various materials to create a work of art. Then, try a yoga class specifically designed for girls your age. ",
    "start": "2020-02-23",
    "end": "2020-02-23",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/art_yoga_db_2_23.html"
  },
  {
    "title": "Netiquette Badge",
    "displayDate": "Sun, Feb 23, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#68c8c6",
    "description": "With your Girl Scout sisters, discuss the power of social media and how we can encourage authenticity, positivity, and empowerment online.",
    "start": "2020-02-23",
    "end": "2020-02-23",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/netiquette.html"
  },
  {
    "title": "Mystery Basket",
    "displayDate": "Sun, Feb 23, 2020, 2:00 PM - 3:30 PM EST",
    "location": "Taste Buds Kitchen (Beverly)",
    "color": "#F9CD00",
    "description": "Add your own spin on some classic recipes to create a delicious one-of-a-kind dish.",
    "start": "2020-02-23",
    "end": "2020-02-23",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/taste_buds_2_23.html"
  },
  {
    "title": "Cupcake Challenge",
    "displayDate": "Sun, Feb 23, 2020, 2:00 PM - 3:30 PM EST",
    "location": "Taste Buds Kitchen (North Andover)",
    "color": "#F9CD00",
    "description": "Put your culinary creativity to the test!",
    "start": "2020-02-23",
    "end": "2020-02-23",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/taste_buds_2_231.html"
  },
  {
    "title": "Art and Yoga: Junior",
    "displayDate": "Sun, Feb 23, 2020, 3:30 PM - 5:30 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#F9CD00",
    "description": "Use your imagination and experiment with various materials to create a work of art. Then, try a yoga class specifically designed for girls your age. ",
    "start": "2020-02-23",
    "end": "2020-02-23",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/art_yoga_j_2_23.html"
  },
  {
    "title": "Art of Glass",
    "displayDate": "Sun, Feb 23, 2020, 3:30 PM - 5:30 PM EST",
    "location": "Diablo Glass School",
    "color": "#F9CD00",
    "description": "Experience a live glassblowing demonstration, and create your own fused glass pendant using flame-working techniques.",
    "start": "2020-02-23",
    "end": "2020-02-23",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/art_glass1.html"
  },
  {
    "title": "Watertown Daisy Sampler",
    "displayDate": "Mon, Feb 24, 2020, 5:00 PM - 6:00 PM",
    "location": "Watertown Public Library",
    "color": "#00ae58",
    "description": "",
    "start": "2020-02-24",
    "end": "2020-02-24",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/watertown_daisy_samp.html"
  },
  {
    "title": "Lexington Daisy Sampler",
    "displayDate": "Tue, Feb 25, 2020, 6:00 PM - 7:00 PM",
    "location": "Cary Library",
    "color": "#00ae58",
    "description": "",
    "start": "2020-02-25",
    "end": "2020-02-25",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/lexington_daisy_samp.html"
  },
  {
    "title": "MEDIC First Aid and CPR Blended Learning",
    "displayDate": "Wed, Feb 26, 2020, 10:00 AM - 2:00 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#F27536",
    "description": "This course is offered in two parts: an online program and a hands-on skills session with an instructor. ",
    "start": "2020-02-26",
    "end": "2020-02-26",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/medic_blended1.html"
  },
  {
    "title": "Deadline for Gold Award Project Proposals and Final Reports",
    "displayDate": "Fri, Feb 28, 2020, 5:00 PM EST",
    "location": "Deadline",
    "color": "#eb0789",
    "description": "GSEMA uses deadlines to collect both Gold Award project proposals and Gold Award final reports. In order to be considered for an upcoming in-person interview or final presentation, you must submit by the deadline before.",
    "start": "2020-02-28",
    "end": "2020-02-28",
    "path": "/content/girlscoutseasternmass/en/events-repository/2019/goldaward-deadline3.html"
  },
  {
    "title": "Sewing: Sleepover Pillowcase",
    "displayDate": "Sat, Feb 29, 2020, 9:00 AM - 11:30 AM EST",
    "location": "Sew Studio of Southborough",
    "color": "#68c8c6",
    "description": "Sew a sleepover pillowcase: a unique drawstring bag to carry your overnight essentials—including your pillow! ",
    "start": "2020-02-29",
    "end": "2020-02-29",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/sew_studio_2_29.html"
  },
  {
    "title": "Home Alone Staying Safe",
    "displayDate": "Sat, Feb 29, 2020, 9:00 AM - 12:00 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#68C8C6",
    "description": "This dynamic and interactive session will prepare you to handle most home-alone situations, from an unexpected knock on the door to an emergency 911 call.",
    "start": "2020-02-29",
    "end": "2020-02-29",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/home_alone_2_29.html"
  },
  {
    "title": "Engineering Design Challenge: Marine Animal Prosthetics",
    "displayDate": "Sat, Feb 29, 2020, 9:00 AM - 12:30 PM EST",
    "location": "National Marine Wildlife Center",
    "color": "#ab218e",
    "description": "Discover how technology is being used to help disabled animals, and then complete an engineering challenge to design your own prosthetic device for a marine animal.",
    "start": "2020-02-29",
    "end": "2020-02-29",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/engineering_2_29.html"
  },
  {
    "title": "Winter Explorers",
    "displayDate": "Sat, Feb 29, 2020, 10:00 AM - 12:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#C4D82E",
    "description": "Take a nature hike, play some games, and learn about local wildlife while exploring camp",
    "start": "2020-02-29",
    "end": "2020-02-29",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/winter_explorers_2_29.html"
  },
  {
    "title": "Computer Expert Badge",
    "displayDate": "Sat, Feb 29, 2020, 10:00 AM - 12:00 PM EST",
    "location": "Microsoft Store (The Mall at Rockingham Park)",
    "color": "#AB218E",
    "description": "Get hands-on experience with the latest technology from Microsoft.",
    "start": "2020-02-29",
    "end": "2020-02-29",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/computer_expert_2_29.html"
  },
  {
    "title": "Chocolatier for the Day",
    "displayDate": "Sat, Feb 29, 2020, 10:00 AM - 1:30 PM EST",
    "location": "Chocolate Therapy",
    "color": "#F9CD00",
    "description": "Sample different types of chocolate while learning about its benefits, its history and how it’s made. 2 sessions.",
    "start": "2020-02-29",
    "end": "2020-02-29",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/chocolatier_2_29.html"
  },
  {
    "title": "Journey Overnight: Junior",
    "displayDate": "Sat, Feb 29, 2020, 10:00 AM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#ab218e",
    "description": "Girls will work together to earn the Energize Award and Investigate Award in the Get Moving! Journey.",
    "start": "2020-02-29",
    "end": "2020-03-01",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/journey_overnight.html"
  },
  {
    "title": "MEdia Journey with StrongHER",
    "displayDate": "Sat, Feb 29, 2020, 12:00 PM - 5:00 PM EST",
    "location": "Camp Rice Moody",
    "color": "#AB218E",
    "description": "The workshop is focused on building stress-management and self-empowerment skills through physical and mindful activities and exercises.",
    "start": "2020-02-29",
    "end": "2020-02-29",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/media_journey.html"
  },
  {
    "title": "Musician Badge with Tau Beta Sigma from the UMass Minuteman Marching Band",
    "displayDate": "Sat, Feb 29, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#F9CD00",
    "description": "Ever wonder what it’s like to conduct an orchestra? ",
    "start": "2020-02-29",
    "end": "2020-02-29",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/musician.html"
  },
  {
    "title": "Winter Trekkers",
    "displayDate": "Sat, Feb 29, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#C4D82E",
    "description": "Explore camp in a new way by hiking while hunting for geocaches.",
    "start": "2020-02-29",
    "end": "2020-02-29",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/winter_trekkers_2_29.html"
  },
  {
    "title": "Digital Photographer Badge",
    "displayDate": "Sat, Feb 29, 2020, 1:00 PM - 4:00 PM EST",
    "location": "Microsoft Store (The Mall at Rockingham Park)",
    "color": "#AB218E",
    "description": "Get hands-on experience capturing and editing photos with the latest technology from Microsoft.",
    "start": "2020-02-29",
    "end": "2020-02-29",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/digital_photographer_2_29.html"
  },
  {
    "title": "Babysitter Badge",
    "displayDate": "Sat, Feb 29, 2020, 1:00 PM - 4:00 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#68C8C6",
    "description": " You’ll gain the skills and confidence for dealing with the challenges—from accidents to tantrums—of caring for children of all ages and stages.",
    "start": "2020-02-29",
    "end": "2020-02-29",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/babysitte_badge_2_29.html"
  },
  {
    "title": "MEDIC First Aid and CPR Blended Learning",
    "displayDate": "Sat, Feb 29, 2020, 1:00 PM - 5:00 PM EST",
    "location": "Rowley Public Library",
    "color": "#F27536",
    "description": "This course is offered in two parts: an online program and a hands-on skills session with an instructor. ",
    "start": "2020-02-29",
    "end": "2020-02-29",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/medic_blended16.html"
  },
  {
    "title": "Advanced Chocolate Workshop",
    "displayDate": "Sat, Feb 29, 2020, 2:00 PM - 3:30 PM EST",
    "location": "Chocolate Therapy",
    "color": "#68C8C6",
    "description": "Girls will learn about the history of chocolate, where it comes from, how it's grown, harvested and gets turned into the sweet confections we love to consume. ",
    "start": "2020-02-29",
    "end": "2020-02-29",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/advanced_chocolate_2_29.html"
  },
  {
    "title": "Winter Trekkers",
    "displayDate": "Sun, Mar 1, 2020, 10:00 AM - 12:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#C4D82E",
    "description": "Explore camp in a new way by hiking while hunting for geocaches.",
    "start": "2020-03-01",
    "end": "2020-03-01",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/winter_trekkers_3_1.html"
  },
  {
    "title": "Scuba Diving",
    "displayDate": "Sun, Mar 1, 2020, 10:30 AM - 2:00 PM EST",
    "location": "Andover/N.Andover YMCA",
    "color": "#f9cd00",
    "description": "Take the plunge into new underwater adventures! 3 sessions.",
    "start": "2020-03-01",
    "end": "2020-03-01",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/scuba_3_1.html"
  },
  {
    "title": "To Space! A STEM Workshop",
    "displayDate": "Sun, Mar 1, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#AB218E",
    "description": "Discover the challenges of space travel with hands-on activities and demonstrations led by Katie Slivensky, STEM educator and author of The Countdown Conspiracy. ",
    "start": "2020-03-01",
    "end": "2020-03-01",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/to_space.html"
  },
  {
    "title": "Entrepreneur Badge",
    "displayDate": "Sun, Mar 1, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#054e98",
    "description": "Earn the Entrepreneur badge and gain confidence in your ability to bring an idea to life! ",
    "start": "2020-03-01",
    "end": "2020-03-01",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/entrepreneur.html"
  },
  {
    "title": "Winter Explorers",
    "displayDate": "Sun, Mar 1, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#C4D82E",
    "description": "Take a nature hike, play some games, and learn about local wildlife while exploring camp",
    "start": "2020-03-01",
    "end": "2020-03-01",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/winter_explorers_3_1.html"
  },
  {
    "title": "Art and Yoga: Daisy/Brownie",
    "displayDate": "Sun, Mar 1, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Camp Rice Moody",
    "color": "#F9CD00",
    "description": "Use your imagination and experiment with various materials to create a work of art. Then, try a yoga class specifically designed for girls your age. ",
    "start": "2020-03-01",
    "end": "2020-03-01",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/art_yoga_db_3_1.html"
  },
  {
    "title": "Me and My Bear Tea Party",
    "displayDate": "Sun, Mar 1, 2020, 1:00 PM - 3:30 PM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#F9CD00",
    "description": "Bring your favorite bear (or other stuffed animal) to a special Girl Scout tea party hosted by the Girl Scout Museum team.",
    "start": "2020-03-01",
    "end": "2020-03-01",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/tea_party_3_1.html"
  },
  {
    "title": "Scuba Diving Adventure",
    "displayDate": "Sun, Mar 1, 2020, 1:30 PM - 3:00 PM EST",
    "location": "Andover/N.Andover YMCA",
    "color": "#f9cd00",
    "description": "Take your skills to the next level. ",
    "start": "2020-03-01",
    "end": "2020-03-01",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/scuba_adv_3_1.html"
  },
  {
    "title": "Designing Robots Badge: Brownie",
    "displayDate": "Sun, Mar 1, 2020, 3:30 PM - 5:30 PM EST",
    "location": "Empow Studios - Lexington",
    "color": "#AB218E",
    "description": "Learn how some robots imitate nature and build your own walking robot using LEGO® MINDSTORMS® EV3.",
    "start": "2020-03-01",
    "end": "2020-03-01",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/designing_robotsl_3_1.html"
  },
  {
    "title": "Designing Robots Badge: Junior",
    "displayDate": "Sun, Mar 1, 2020, 3:30 PM - 5:30 PM EST",
    "location": "Empow Studios (Newton)",
    "color": "#AB218E",
    "description": "Learn how some robots imitate nature and build your own walking robot using LEGO® MINDSTORMS® EV3.",
    "start": "2020-03-01",
    "end": "2020-03-01",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/designing_robots_3_1.html"
  },
  {
    "title": "Social Innovator Badge",
    "displayDate": "Sun, Mar 1, 2020, 3:30 PM - 5:30 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#054e98",
    "description": "Develop a solution for your community utilizing your passions and skills! ",
    "start": "2020-03-01",
    "end": "2020-03-01",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/social_innovator.html"
  },
  {
    "title": "Art and Yoga: Junior",
    "displayDate": "Sun, Mar 1, 2020, 3:30 PM - 5:30 PM EST",
    "location": "Camp Rice Moody",
    "color": "#F9CD00",
    "description": "Use your imagination and experiment with various materials to create a work of art. Then, try a yoga class specifically designed for girls your age. ",
    "start": "2020-03-01",
    "end": "2020-03-01",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/art_yoga_j_3_1.html"
  },
  {
    "title": "Mermaid Explorer Session",
    "displayDate": "Sun, Mar 1, 2020, 5:00 PM - 6:15 PM EST",
    "location": "Evelyn Kirrane Aquatic Center (Brookline)",
    "color": "#f9cd00",
    "description": "You’ll learn how to swim with a mermaid tail on, basic mermaiding safety, and some mermaid tricks. ",
    "start": "2020-03-01",
    "end": "2020-03-01",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/mermaid_3_11.html"
  },
  {
    "title": "Mermaid Explorer Session",
    "displayDate": "Sun, Mar 1, 2020, 6:15 PM - 7:30 PM EST",
    "location": "Evelyn Kirrane Aquatic Center (Brookline)",
    "color": "#f9cd00",
    "description": "You’ll learn how to swim with a mermaid tail on, basic mermaiding safety, and some mermaid tricks. ",
    "start": "2020-03-01",
    "end": "2020-03-01",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/mermaid_3_1.html"
  },
  {
    "title": "Weston Daisy Sampler",
    "displayDate": "Wed, Mar 4, 2020, 6:00 PM - 7:00 PM",
    "location": "Weston Scout House",
    "color": "#00ae58",
    "description": "",
    "start": "2020-03-04",
    "end": "2020-03-04",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/weston_daisy_sampler.html"
  },
  {
    "title": "Leading Women",
    "displayDate": "Thu, Mar 5, 2020, 7:00 AM - 9:00 AM",
    "location": "Boston Marriot Copley Place",
    "color": "#00AE58",
    "description": "Leading Women Awards celebrate the outstanding commitment and contributions of women in our community",
    "start": "2020-03-05",
    "end": "2020-03-05",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/leading_women.html"
  },
  {
    "title": "Museum of Science Overnight",
    "displayDate": "Fri, Mar 6, 2020, 5:00 PM - Sat, Mar 7, 2020, 11:00 AM EST",
    "location": "Museum of Science",
    "color": "#ab218e",
    "description": "Spend a night at the museum!",
    "start": "2020-03-06",
    "end": "2020-03-07",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/mos_overnight.html"
  },
  {
    "title": "Junior Safety Award",
    "displayDate": "Sat, Mar 7, 2020, 9:00 AM - 11:00 AM EST",
    "location": "Camp Rice Moody",
    "color": "#68C8C6",
    "description": "Earn your Safety Award with experts from the Injury Prevention Team at Boston Children’s Hospital. ",
    "start": "2020-03-07",
    "end": "2020-03-07",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/junior_safety_3_7.html"
  },
  {
    "title": "Book Artist",
    "displayDate": "Sat, Mar 7, 2020, 9:00 AM - 12:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#F9CD00",
    "description": "Explore book art techniques and styles.",
    "start": "2020-03-07",
    "end": "2020-03-07",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/book_artist_3_7.html"
  },
  {
    "title": "MEDIC First Aid and CPR Recertification",
    "displayDate": "Sat, Mar 7, 2020, 9:00 AM - 12:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#F27536",
    "description": "This course is for those whose First Aid and CPR certifications are about to expire or have expired within 30 days of the listed session.",
    "start": "2020-03-07",
    "end": "2020-03-07",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/medic_recertification1.html"
  },
  {
    "title": "Glass Pendant Making",
    "displayDate": "Sat, Mar 7, 2020, 10:00 AM - 12:00 PM EST",
    "location": "The Glass Bar",
    "color": "#F9CD00",
    "description": "Learn glass-cutting techniques and the basics of fusing in a jewelry kiln as you create a glass pendant. ",
    "start": "2020-03-07",
    "end": "2020-03-07",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/glass_pendant_3_71.html"
  },
  {
    "title": "What Robots Do Badge",
    "displayDate": "Sat, Mar 7, 2020, 10:00 AM - 12:30 PM EST",
    "location": "Adventure Code Academy",
    "color": "#AB218E",
    "description": "Learn about the many things that robots can be designed to do while participating in exciting hands-on activities.",
    "start": "2020-03-07",
    "end": "2020-03-07",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/robots_3_7.html"
  },
  {
    "title": "Sweet Confections",
    "displayDate": "Sat, Mar 7, 2020, 11:00 AM - 12:30 PM EST",
    "location": "Sweet Mimis",
    "color": "#F9CD00",
    "description": "Girls will learn about the history of chocolate, where it comes from, how it's grown, harvested and gets turned into the sweet confections we love to consume. ",
    "start": "2020-03-07",
    "end": "2020-03-07",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/sweet_confections_3_7.html"
  },
  {
    "title": "Glass Pendant Making",
    "displayDate": "Sat, Mar 7, 2020, 12:00 PM - 2:00 PM EST",
    "location": "North Shore Glass School",
    "color": "#F9CD00",
    "description": "Learn the ancient art of flameworking and make your own beautiful pendant.",
    "start": "2020-03-07",
    "end": "2020-03-07",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/glass_pendant_3_7.html"
  },
  {
    "title": "Winter Explorers",
    "displayDate": "Sat, Mar 7, 2020, 12:00 PM - 2:00 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#C4D82E",
    "description": "Take a nature hike, play some games, and learn about local wildlife while exploring camp",
    "start": "2020-03-07",
    "end": "2020-03-07",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/winter_explorers_3_7.html"
  },
  {
    "title": "Brownie Safety Award",
    "displayDate": "Sat, Mar 7, 2020, 12:00 PM - 2:00 PM EST",
    "location": "Camp Rice Moody",
    "color": "#68C8C6",
    "description": "Earn your Safety Award with experts from the Injury Prevention Team at Boston Children’s Hospital. ",
    "start": "2020-03-07",
    "end": "2020-03-07",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/brownie_safety_3_7.html"
  },
  {
    "title": "Book Artist: Brownie/Junior",
    "displayDate": "Sat, Mar 7, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#F9CD00",
    "description": "Combine your love of books with your crafting skills. ",
    "start": "2020-03-07",
    "end": "2020-03-07",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/book_artist_bj_3_7.html"
  },
  {
    "title": "Sewing: Girl Scout Fabric Bag",
    "displayDate": "Sat, Mar 7, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Sandi's Sewing and Design (Bridgewater)",
    "color": "#68c8c6",
    "description": "Make your own tote bag from Girl Scout fabric and then wear it with Girl Scout pride.",
    "start": "2020-03-07",
    "end": "2020-03-07",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/sandis_sewing_3_6.html"
  },
  {
    "title": "Programming Robots Badge",
    "displayDate": "Sat, Mar 7, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Adventure Code Academy",
    "color": "#AB218E",
    "description": "You’ll engage in activities to create a program that can be run by a robot!",
    "start": "2020-03-07",
    "end": "2020-03-07",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/programming_robots_3_7.html"
  },
  {
    "title": "MEDIC First Aid and CPR Blended Learning",
    "displayDate": "Sat, Mar 7, 2020, 1:00 PM - 5:00 PM EST",
    "location": "GSEMA Service Center - Waltham",
    "color": "#F27536",
    "description": "This course is offered in two parts: an online program and a hands-on skills session with an instructor. ",
    "start": "2020-03-07",
    "end": "2020-03-07",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/medic_blended9.html"
  },
  {
    "title": "Winter Explorers",
    "displayDate": "Sat, Mar 7, 2020, 3:00 PM - 5:00 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#C4D82E",
    "description": "Take a nature hike, play some games, and learn about local wildlife while exploring camp",
    "start": "2020-03-07",
    "end": "2020-03-07",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/winter_explorers_3_71.html"
  },
  {
    "title": "Winter Trekkers",
    "displayDate": "Sat, Mar 7, 2020, 3:00 PM - 5:00 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#C4D82E",
    "description": "Explore camp in a new way by hiking while hunting for geocaches.",
    "start": "2020-03-07",
    "end": "2020-03-07",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/winter_trekkers_3_7.html"
  },
  {
    "title": "Pottery Painting",
    "displayDate": "Sat, Mar 7, 2020, 3:30 PM - 4:30 PM EST",
    "location": "Look What I Made (N. Reading)",
    "color": "#f9cd00",
    "description": "You will get to pick from a variety of pottery pieces to paint. Everything will be glazed and fired in our kiln and ready to pick up in one week. ",
    "start": "2020-03-07",
    "end": "2020-03-07",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/pottery_painting_3_7.html"
  },
  {
    "title": "Family Winter Adventure",
    "displayDate": "Sat, Mar 7, 2020, 6:00 PM - 8:00 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#C4D82E",
    "description": "Come explore fields and trails as a family on a night hike around camp. ",
    "start": "2020-03-07",
    "end": "2020-03-07",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/family_winter_1_25.html"
  },
  {
    "title": "Mystic Aquarium Overnight",
    "displayDate": "Sat, Mar 7, 2020, 7:00 PM - Sun, Mar 8, 2020, 8:00 AM EST",
    "location": "Mystic Aquarium",
    "color": "#AB218E",
    "description": "Dissect a squid, touch live invertebrates, tour Exploration: Wild!, participate in a scavenger hunt, and more during this aquatic overnight. ",
    "start": "2020-03-07",
    "end": "2020-03-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/mystic_3_7.html"
  },
  {
    "title": "Simple Meals Badge",
    "displayDate": "Sun, Mar 8, 2020, 9:00 AM - 10:30 AM EST",
    "location": "Mix It Up Kitchen",
    "color": "#68c8c6",
    "description": "Join us to make some simple yet delicious breakfast items that will help fuel you up!",
    "start": "2020-03-08",
    "end": "2020-03-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/simple_meals_3_8.html"
  },
  {
    "title": "Animal Habitats Badge at Buttonwood Park Zoo",
    "displayDate": "Sun, Mar 8, 2020, 10:00 AM - 12:00 PM EST",
    "location": "Buttonwood Park Zoo",
    "color": "#AB218E",
    "description": "Explore various habitats throughout the zoo and compare wild and domestic animals.",
    "start": "2020-03-08",
    "end": "2020-03-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/animal_habitats_3_8.html"
  },
  {
    "title": "Winter Trekkers",
    "displayDate": "Sun, Mar 8, 2020, 10:00 AM - 12:00 PM EST",
    "location": "Camp Runels",
    "color": "#C4D82E",
    "description": "Explore camp in a new way by hiking while hunting for geocaches.",
    "start": "2020-03-08",
    "end": "2020-03-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/winter_trekkers_3_8.html"
  },
  {
    "title": "Puzzle Break: Escape the Midnight Carnival",
    "displayDate": "Sun, Mar 8, 2020, 12:00 PM - 1:00 PM EST",
    "location": "Puzzle Break",
    "color": "#F9CD00",
    "description": "Solve puzzles, decode clues, and work together to “escape the room” at Puzzle Break",
    "start": "2020-03-08",
    "end": "2020-03-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/puzzle_break_3_8.html"
  },
  {
    "title": "Staying Fit Badge",
    "displayDate": "Sun, Mar 8, 2020, 1:00 PM - 2:30 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#F9CD00",
    "description": "Earn your staying fit badge with the team at Bring the Hoopla. ",
    "start": "2020-03-08",
    "end": "2020-03-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/staying_fit1.html"
  },
  {
    "title": "Maple Sugaring Time",
    "displayDate": "Sun, Mar 8, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#F9CD00",
    "description": "Identify sugar maples and make your own syrup!",
    "start": "2020-03-08",
    "end": "2020-03-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/maple_sugaring_3_8.html"
  },
  {
    "title": "Let’s Talk About It: Body Image, Self-Esteem, and Healthy Relationships",
    "displayDate": "Sun, Mar 8, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Camp Rice Moody",
    "color": "#68c8c6",
    "description": "Explore your relationships with personal body image, discuss common characteristics of healthy relationships, and consider the relationships you have in your life.",
    "start": "2020-03-08",
    "end": "2020-03-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/talk_about_it.html"
  },
  {
    "title": "Mad Science",
    "displayDate": "Sun, Mar 8, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Plymouth",
    "color": "#f9cd00",
    "description": "See a science show and try some activities at this fun program.",
    "start": "2020-03-08",
    "end": "2020-03-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/mad_science.html"
  },
  {
    "title": "Winter Explorers",
    "displayDate": "Sun, Mar 8, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Camp Runels",
    "color": "#C4D82E",
    "description": "Take a nature hike, play some games, and learn about local wildlife while exploring camp",
    "start": "2020-03-08",
    "end": "2020-03-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/winter_explorers_3_8.html"
  },
  {
    "title": "Drawing Badge",
    "displayDate": "Sun, Mar 8, 2020, 1:00 PM - 3:00 PM EST",
    "location": "New Bedford Art Museum/Art Works!",
    "color": "#F9CD00",
    "description": "See what can happen when you put pencil to paper and add your imagination.",
    "start": "2020-03-08",
    "end": "2020-03-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/art_works_3_8.html"
  },
  {
    "title": "Painting Badge with Children's Art Lab",
    "displayDate": "Sun, Mar 8, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Camp Wind in the Pines",
    "color": "#F9CD00",
    "description": "Learn about different styles of painting, try your hand at painting a landscape, and experiment and innovate with creative painting tools!",
    "start": "2020-03-08",
    "end": "2020-03-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/childrens_art_lab_3_8.html"
  },
  {
    "title": "Fair Play Badge",
    "displayDate": "Sun, Mar 8, 2020, 3:00 PM - 4:30 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#68c8c6",
    "description": "Girls will be treated to a special Hoopla demonstration, warm-up and stretching, and lots of hoopin’ fun!",
    "start": "2020-03-08",
    "end": "2020-03-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/fair_play_3_8.html"
  },
  {
    "title": "Let’s Talk About It: Body Image, Self-Esteem, and Healthy Relationships",
    "displayDate": "Sun, Mar 8, 2020, 3:30 PM - 5:30 PM EST",
    "location": "Camp Rice Moody",
    "color": "#68c8c6",
    "description": "Explore your relationships with personal body image, discuss common characteristics of healthy relationships, and consider the relationships you have in your life.",
    "start": "2020-03-08",
    "end": "2020-03-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/talk_about_it1.html"
  },
  {
    "title": "Computer Expert Badge",
    "displayDate": "Sun, Mar 8, 2020, 3:30 PM - 5:30 PM EST",
    "location": "Empow Studios - Newton",
    "color": "#AB218E",
    "description": "Learn about different computer programs and use the internet to plan your own virtual trip.",
    "start": "2020-03-08",
    "end": "2020-03-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/computer_expert_3_8.html"
  },
  {
    "title": "Gold Award Information Session",
    "displayDate": "Tue, Mar 10, 2020, 6:30 PM - 8:00 PM EST",
    "location": "Online",
    "color": "#F27536",
    "description": "Join us for an online session to learn more about the Gold Award.",
    "start": "2020-03-10",
    "end": "2020-03-10",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/gold-award-info1.html"
  },
  {
    "title": "Potter Badge",
    "displayDate": "Fri, Mar 13, 2020, 4:00 PM - 5:30 PM EST",
    "location": "Look What I Made (N. Reading)",
    "color": "#f9cd00",
    "description": "Come have some fun getting your hands dirty and earn your Potter Badge!",
    "start": "2020-03-13",
    "end": "2020-03-13",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/potter_badge_3_13.html"
  },
  {
    "title": "Dive-in Movie",
    "displayDate": "Fri, Mar 13, 2020, 6:30 PM - 8:15 PM EST",
    "location": "Raynham Athletic Club",
    "color": "#C4D82E",
    "description": "Enjoy a movie on the big screen while swimming in a heated pool. ",
    "start": "2020-03-13",
    "end": "2020-03-13",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/dive_in_3_13.html"
  },
  {
    "title": "Caring for Stranded Ocean Animals: Brownie",
    "displayDate": "Sat, Mar 14, 2020, 9:00 AM - 12:00 PM EST",
    "location": "National Marine Life Center",
    "color": "#AB218E",
    "description": "Learn about marine animal rescue, rehabilitation, and release through hands-on activities and specimen exploration. ",
    "start": "2020-03-14",
    "end": "2020-03-14",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/caring_ocean_3_14.html"
  },
  {
    "title": "STEM Conference and Expo",
    "displayDate": "Sat, Mar 14, 2020, 9:00 AM - 5:00 PM EST",
    "location": "Framingham State University",
    "color": "#AB218E",
    "description": "At the 8th annual STEM Conference and Expo, choose three workshops based on your interests and spend the day on campus learning and exploring through hands-on activities. ",
    "start": "2020-03-14",
    "end": "2020-03-14",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/stem_conference.html"
  },
  {
    "title": "Wilderness First Aid",
    "displayDate": "Sat, Mar 14, 2020, 9:00 AM - Sun, Mar 15, 2020, 6:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#BCBEC0",
    "description": "This course qualifies you to be a Wilderness First Aider and is required training for volunteers taking girls more than 30 minutes outside of medical care.",
    "start": "2020-03-14",
    "end": "2020-03-15",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/wilderness.html"
  },
  {
    "title": "Drawing Badge",
    "displayDate": "Sat, Mar 14, 2020, 10:00 AM - 12:00 PM EST",
    "location": "Fuller Craft Museum",
    "color": "#f9cd00",
    "description": "Create a still life display and explore perspective in artwork.",
    "start": "2020-03-14",
    "end": "2020-03-14",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/drawing_3_14.html"
  },
  {
    "title": "Brownie First Aid Badge",
    "displayDate": "Sat, Mar 14, 2020, 10:00 AM - 12:00 PM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#68c8c6",
    "description": "Practice basic indoor and outdoor first aid techniques during this fun, interactive class. ",
    "start": "2020-03-14",
    "end": "2020-03-14",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/browniefa_3_14.html"
  },
  {
    "title": "Girl Scout Day at the MIT Museum: Physics and Photography",
    "displayDate": "Sat, Mar 14, 2020, 10:00 AM - 12:00 PM EST",
    "location": "MIT Museum",
    "color": "#AB218E",
    "description": "Explore how cameras and light interact to capture images of events that happen quicker than a blink of an eye. ",
    "start": "2020-03-14",
    "end": "2020-03-14",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/MIT_museum.html"
  },
  {
    "title": "What Robots Do Badge",
    "displayDate": "Sat, Mar 14, 2020, 10:00 AM - 12:30 PM EST",
    "location": "Adventure Code Academy",
    "color": "#AB218E",
    "description": "Learn about the many things that robots can be designed to do while participating in exciting hands-on activities.",
    "start": "2020-03-14",
    "end": "2020-03-14",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/robots_3_14.html"
  },
  {
    "title": "Glass Suncatcher",
    "displayDate": "Sat, Mar 14, 2020, 12:00 PM - 2:00 PM EST",
    "location": "North Shore Glass School",
    "color": "#F9CD00",
    "description": "Learn how to make stunning suncatchers to place in your window! ",
    "start": "2020-03-14",
    "end": "2020-03-14",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/glass_suncatcher_3_14.html"
  },
  {
    "title": "Painting Badge",
    "displayDate": "Sat, Mar 14, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Fuller Craft Museum",
    "color": "# f9cd00",
    "description": "Create your own masterpiece on canvas and learn about where artists find their ideas.",
    "start": "2020-03-14",
    "end": "2020-03-14",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/painting_badge_3_14.html"
  },
  {
    "title": "Designing Robots Badge",
    "displayDate": "Sat, Mar 14, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Adventure Code Academy",
    "color": "#AB218E",
    "description": "Earn the Designing Robots badge through hands-on creative activities.",
    "start": "2020-03-14",
    "end": "2020-03-14",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/designing_robots_3_14.html"
  },
  {
    "title": "Caring for Stranded Ocean Animals: Junior",
    "displayDate": "Sat, Mar 14, 2020, 1:00 PM - 4:00 PM EST",
    "location": "National Marine Life Center",
    "color": "#AB218E",
    "description": "Learn about marine animal rescue, rehabilitation, and release through hands-on activities and specimen exploration. ",
    "start": "2020-03-14",
    "end": "2020-03-14",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/caring_ocean_3_141.html"
  },
  {
    "title": "Programming Robots Badge",
    "displayDate": "Sat, Mar 14, 2020, 1:15 PM - 2:45 PM EST",
    "location": "North Shore Sylvan Learning",
    "color": "#AB218E",
    "description": "Develop strong innovation skills as you work in pairs to build a robot using LEGO® bricks.",
    "start": "2020-03-14",
    "end": "2020-03-14",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/programming_robots_3_14.html"
  },
  {
    "title": "Aerial Yoga: Junior",
    "displayDate": "Sat, Mar 14, 2020, 2:00 PM - 3:30 PM EST",
    "location": "On Common Ground Yoga",
    "color": "#F9CD00",
    "description": "Fly high! Stretch, strengthen, relax, and have fun with aerial silks.",
    "start": "2020-03-14",
    "end": "2020-03-14",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/aerial_yoga_3_141.html"
  },
  {
    "title": "Daisy Climbing Adventure Badge",
    "displayDate": "Sat, Mar 14, 2020, 3:00 PM - 4:30 PM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#C4D82E",
    "description": "Girls will spend the first half of the program on the ground learning the lingo and how to climb safely. Then they will boulder across our indoor wall while building up their skills.",
    "start": "2020-03-14",
    "end": "2020-03-14",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/daisy_climbing1.html"
  },
  {
    "title": "Aerial Yoga: Cadette",
    "displayDate": "Sat, Mar 14, 2020, 4:00 PM - 5:30 PM EST",
    "location": "On Common Ground Yoga",
    "color": "#F9CD00",
    "description": "Fly high! Stretch, strengthen, relax, and have fun with aerial silks.",
    "start": "2020-03-14",
    "end": "2020-03-14",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/aerial_yoga_3_14.html"
  },
  {
    "title": "Museum of Science Overnight",
    "displayDate": "Sat, Mar 14, 2020, 5:00 PM - Sun, Mar 15, 2020, 11:00 AM EST",
    "location": "Museum of Science",
    "color": "#ab218e",
    "description": "Spend a night at the museum!",
    "start": "2020-03-14",
    "end": "2020-03-15",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/mos_overnight_3_6.html"
  },
  {
    "title": "Glow-in-the-Dark Lock-in",
    "displayDate": "Sat, Mar 14, 2020, 6:00 PM - Sun, Mar 15, 2020, 9:00 AM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#C4D82E",
    "description": "Get ready to have some glow-in-the-dark fun!",
    "start": "2020-03-14",
    "end": "2020-03-15",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/glow_lock_in_3_14.html"
  },
  {
    "title": "Cozy Campers",
    "displayDate": "Sun, Mar 15, 2020, 10:00 AM - 3:00 PM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#C4D82E",
    "description": "Come to camp for some frosty fun as you experiment with ice and snow, and learn from woodland critters who are active all year long.",
    "start": "2020-03-15",
    "end": "2020-03-15",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/cozy_campers_3_15.html"
  },
  {
    "title": "Mass Escape Room",
    "displayDate": "Sun, Mar 15, 2020, 11:00 AM - 1:00 PM EST",
    "location": "Mass Escape Room",
    "color": "#F9CD00",
    "description": "Unleash your inner go-getter at this hands-on mental adventure game.",
    "start": "2020-03-15",
    "end": "2020-03-15",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/mass_escape_3_15.html"
  },
  {
    "title": "Snacks Badge",
    "displayDate": "Sun, Mar 15, 2020, 1:00 PM - 2:30 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#68c8c6",
    "description": "Take your palate on a world tour and as you chop, mix, and roll your way to earning your Snacks badge!",
    "start": "2020-03-15",
    "end": "2020-03-15",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/snacks.html"
  },
  {
    "title": "Playing the Past Badge",
    "displayDate": "Sun, Mar 15, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#F9CD00",
    "description": "Discover what life was like on an 18th-century New England farm through activities including carding, spinning and weaving wool, and processing flax.",
    "start": "2020-03-15",
    "end": "2020-03-15",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/past_3_15.html"
  },
  {
    "title": "Potter Badge",
    "displayDate": "Sun, Mar 15, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Fuller Craft Museum",
    "color": "#f9cd00",
    "description": "Learn about the different materials, techniques and skills to make pottery and then create your own masterpiece.",
    "start": "2020-03-15",
    "end": "2020-03-15",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/potter_3_15.html"
  },
  {
    "title": "Dancer Badge",
    "displayDate": "Sun, Mar 15, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Rachel Park Dance Center",
    "color": "#F9CD00",
    "description": "Participate in a warm-up, practice basic ballet and jazz skills, and learn a group dance. ",
    "start": "2020-03-15",
    "end": "2020-03-15",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/dancer_3_15.html"
  },
  {
    "title": "Jeweler Badge",
    "displayDate": "Sun, Mar 15, 2020, 1:00 PM - 3:30 PM EST",
    "location": "Camp Rice Moody",
    "color": "#F9CD00",
    "description": "Discover how to upcycle paper, metal, and other natural objects into one-of-a-kind wearable art",
    "start": "2020-03-15",
    "end": "2020-03-15",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/jeweler.html"
  },
  {
    "title": "Glass Fusing",
    "displayDate": "Sun, Mar 15, 2020, 3:30 PM - 4:30 PM EST",
    "location": "Look What I Made (N. Reading)",
    "color": "#f9cd00",
    "description": "Learn to make your own fused glass masterpiece! ",
    "start": "2020-03-15",
    "end": "2020-03-15",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/glass_fusing_3_15.html"
  },
  {
    "title": "Simple Meals Badge",
    "displayDate": "Sun, Mar 15, 2020, 3:30 PM - 5:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#68c8c6",
    "description": "Earning your Simple Meals badge has never tasted so good!",
    "start": "2020-03-15",
    "end": "2020-03-15",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/simple_meals.html"
  },
  {
    "title": "Jewelery Making",
    "displayDate": "Sun, Mar 15, 2020, 3:30 PM - 5:30 PM EST",
    "location": "Camp Rice Moody",
    "color": "#F9CD00",
    "description": "Discover how to upcycle paper, metal, and other natural objects into one-of-a-kind wearable art",
    "start": "2020-03-15",
    "end": "2020-03-15",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/jewelery_making.html"
  },
  {
    "title": "Designing Robots Badge: Junior",
    "displayDate": "Sun, Mar 15, 2020, 3:30 PM - 5:30 PM EST",
    "location": "Empow Studios (Lexington)",
    "color": "#AB218E",
    "description": "Learn how some robots imitate nature and build your own walking robot using LEGO® MINDSTORMS® EV3.",
    "start": "2020-03-15",
    "end": "2020-03-15",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/designing_robotsl_3_15.html"
  },
  {
    "title": "Designing Robots Badge: Brownie",
    "displayDate": "Sun, Mar 15, 2020, 3:30 PM - 5:30 PM EST",
    "location": "Empow Studios (Newton)",
    "color": "#AB218E",
    "description": "Learn how some robots imitate nature and build your own walking robot using LEGO® MINDSTORMS® EV3.",
    "start": "2020-03-15",
    "end": "2020-03-15",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/designing_robots_3_15.html"
  },
  {
    "title": "Potter Badge",
    "displayDate": "Sun, Mar 15, 2020, 4:00 PM - 5:30 PM EST",
    "location": "Art Signals (Maynard)",
    "color": "#f9cd00",
    "description": "Come have some fun getting your hands dirty and earn your Potter Badge!",
    "start": "2020-03-15",
    "end": "2020-03-15",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/potter_badge_3_15.html"
  },
  {
    "title": "First Contact: Amateur Radio",
    "displayDate": "Tue, Mar 17, 2020, 6:00 PM - 8:00 PM EST",
    "location": "New England Sci-Tech",
    "color": "#AB218E",
    "description": "Get on the radio waves and talk to people all over the world! ",
    "start": "2020-03-17",
    "end": "2020-03-17",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/first_contact_3_17.html"
  },
  {
    "title": "Black Light Painting",
    "displayDate": "Wed, Mar 18, 2020, 4:00 PM - 5:30 PM EST",
    "location": "Art Signals (Maynard)",
    "color": "#f9cd00",
    "description": "Enjoy a unique painting experience as you paint in the dark under black light with special fluorescent acrylic paint. ",
    "start": "2020-03-18",
    "end": "2020-03-18",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/black_light_3_18.html"
  },
  {
    "title": "Bronze Award Orientation",
    "displayDate": "Thu, Mar 19, 2020, 6:00 PM - 8:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#F27536",
    "description": "This free two-hour orientation prepares Juniors to brainstorm, develop and implement a successful Bronze Award project, and teaches adults how best to support them.",
    "start": "2020-03-19",
    "end": "2020-03-19",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/bronze-award-orientation.html"
  },
  {
    "title": "Museum of Science Overnight",
    "displayDate": "Fri, Mar 20, 2020, 5:00 PM - Sat, Mar 21, 2020, 11:00 AM EST",
    "location": "Museum of Science",
    "color": "#ab218e",
    "description": "Spend a night at the museum!",
    "start": "2020-03-20",
    "end": "2020-03-21",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/mos_overnight_3_20.html"
  },
  {
    "title": "Sewing: Fox Stuffed Animal",
    "displayDate": "Sat, Mar 21, 2020, 9:00 AM - 11:30 AM EST",
    "location": "Sew Studio of Southborough",
    "color": "#68c8c6",
    "description": "Sew a Fox, and you'll have a new friend to cuddle! ",
    "start": "2020-03-21",
    "end": "2020-03-21",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/sew_studio_3_21.html"
  },
  {
    "title": "MEDIC First Aid and CPR Recertification",
    "displayDate": "Sat, Mar 21, 2020, 9:00 AM - 12:00 PM EST",
    "location": "GSEMA Service Center - Middleboro",
    "color": "#F27536",
    "description": "This course is for those whose First Aid and CPR certifications are about to expire or have expired within 30 days of the listed session.",
    "start": "2020-03-21",
    "end": "2020-03-21",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/medic_recertification2.html"
  },
  {
    "title": "Finding Common Ground Badge",
    "displayDate": "Sat, Mar 21, 2020, 9:30 AM - 12:00 PM EST",
    "location": "The Edward M Kennedy Institute for the US Senate",
    "color": "#68c8c6",
    "description": "Learn about the art of negotiation at the Edward M. Kennedy Institute by taking on the role of a senator working on a voting rights bill.",
    "start": "2020-03-21",
    "end": "2020-03-21",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/common_ground.html"
  },
  {
    "title": "Playing the Past Badge",
    "displayDate": "Sat, Mar 21, 2020, 9:30 AM - 12:30 PM EST",
    "location": "Centerville Historical Museum",
    "color": "#F9CD00",
    "description": "What was life like for a girl in a Cape Cod seafaring town in the 1800s? ",
    "start": "2020-03-21",
    "end": "2020-03-21",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/past_3_21.html"
  },
  {
    "title": "Glass Suncatcher",
    "displayDate": "Sat, Mar 21, 2020, 10:00 AM - 12:00 PM EST",
    "location": "The Glass Bar",
    "color": "#F9CD00",
    "description": "Develop your own designs as you learn the basics of cutting glass and fusing it in the kiln. ",
    "start": "2020-03-21",
    "end": "2020-03-21",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/glass_suncatcher_3_21.html"
  },
  {
    "title": "Pets Badge",
    "displayDate": "Sat, Mar 21, 2020, 10:00 AM - 12:00 PM EST",
    "location": "Norfolk Agricultural High School",
    "color": "#68c8c6",
    "description": "Learn how staff and students care for various animals.",
    "start": "2020-03-21",
    "end": "2020-03-21",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/pets_badge_3_21.html"
  },
  {
    "title": "Build Your Outdoor Skills: Beginner Fire-building",
    "displayDate": "Sat, Mar 21, 2020, 10:00 AM - 12:00 PM EST",
    "location": "Camp Runels",
    "color": "#C4D82E",
    "description": "Gain confidence building a successful fire and get comfortable with fire safety.",
    "start": "2020-03-21",
    "end": "2020-03-21",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/outdoor_skills_3_21.html"
  },
  {
    "title": "Chocolatier for the Day",
    "displayDate": "Sat, Mar 21, 2020, 10:00 AM - 1:30 PM EST",
    "location": "Chocolate Therapy",
    "color": "#F9CD00",
    "description": "Sample different types of chocolate while learning about its benefits, its history and how it’s made. 2 sessions.",
    "start": "2020-03-21",
    "end": "2020-03-21",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/chocolatier_3_21.html"
  },
  {
    "title": "Robotics Badge Day",
    "displayDate": "Sat, Mar 21, 2020, 10:00 AM - 2:30 PM EST",
    "location": "Notre Dame Academy (Hingham)",
    "color": "#AB218E",
    "description": "Girls will will learn all about robots through hands on activities and will build and showcase their robots.  ",
    "start": "2020-03-21",
    "end": "2020-03-21",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/robotics.html"
  },
  {
    "title": "Gals' Night Out: Cabins",
    "displayDate": "Sat, Mar 21, 2020, 10:00 AM EST",
    "location": "Camp Cedar Hill",
    "color": "#C4D82E",
    "description": "Spend a night at camp with your mom or another special female adult!",
    "start": "2020-03-21",
    "end": "2020-03-22",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/gals_3_21.html"
  },
  {
    "title": "Animal Habitats Badge at Norfolk Agricultural School",
    "displayDate": "Sat, Mar 21, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Norfolk County Agricultural School",
    "color": "#AB218E",
    "description": "Discover what you can do to improve the lives of animals.",
    "start": "2020-03-21",
    "end": "2020-03-21",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/animal_habitats_3_21.html"
  },
  {
    "title": "Designing Robots Badge",
    "displayDate": "Sat, Mar 21, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Adventure Code Academy",
    "color": "#AB218E",
    "description": "Earn the Designing Robots badge through hands-on creative activities.",
    "start": "2020-03-21",
    "end": "2020-03-21",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/designing_robots_3_21.html"
  },
  {
    "title": "Programming Robots Badge",
    "displayDate": "Sat, Mar 21, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Adventure Code Academy",
    "color": "#AB218E",
    "description": "You’ll engage in activities to create a program that can be run by a robot!",
    "start": "2020-03-21",
    "end": "2020-03-21",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/programming_robots_3_21.html"
  },
  {
    "title": "Glass Pendant Making",
    "displayDate": "Sat, Mar 21, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Pairpoint Glass School",
    "color": "#F9CD00",
    "description": "Try your hand at making your own glass pendant with the experts at Pairpoint Glass School.",
    "start": "2020-03-21",
    "end": "2020-03-21",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/glass_pendant_3_21.html"
  },
  {
    "title": "Behind the Ballot and Public Policy Badges",
    "displayDate": "Sat, Mar 21, 2020, 1:00 PM - 3:00 PM EST",
    "location": "The Edward M Kennedy Institute for the US Senate",
    "color": "#68c8c6",
    "description": "Explore voting rights, elections, and the legislation that governs our representative democracy. ",
    "start": "2020-03-21",
    "end": "2020-03-21",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/behind_ballot.html"
  },
  {
    "title": "Build Your Outdoor Skills: Cooking",
    "displayDate": "Sat, Mar 21, 2020, 1:00 PM - 4:00 PM EST",
    "location": "Camp Runels",
    "color": "#C4D82E",
    "description": "Learn to prepare delicious dishes using a variety of outdoor cooking methods, all while practicing ethical outdoor preservation.",
    "start": "2020-03-21",
    "end": "2020-03-21",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/outdoor_skills_3_211.html"
  },
  {
    "title": "MEDIC First Aid and CPR Blended Learning",
    "displayDate": "Sat, Mar 21, 2020, 1:00 PM - 5:00 PM EST",
    "location": "GSEMA Service Center - Middleboro",
    "color": "#F27536",
    "description": "This course is offered in two parts: an online program and a hands-on skills session with an instructor. ",
    "start": "2020-03-21",
    "end": "2020-03-21",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/medic_blended5.html"
  },
  {
    "title": "Advanced Chocolate Workshop",
    "displayDate": "Sat, Mar 21, 2020, 2:00 PM - 3:30 PM EST",
    "location": "Chocolate Therapy",
    "color": "#68C8C6",
    "description": "Girls will learn about the history of chocolate, where it comes from, how it's grown, harvested and gets turned into the sweet confections we love to consume. ",
    "start": "2020-03-21",
    "end": "2020-03-21",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/advanced_chocolate_3_21.html"
  },
  {
    "title": "Pottery Painting",
    "displayDate": "Sat, Mar 21, 2020, 4:00 PM - 5:00 PM EST",
    "location": "Art Signals (Maynard)",
    "color": "#f9cd00",
    "description": "You will get to pick from a variety of pottery pieces to paint. Everything will be glazed and fired in our kiln and ready to pick up in one week. ",
    "start": "2020-03-21",
    "end": "2020-03-21",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/pottery_painting_3_21.html"
  },
  {
    "title": "Overnight at PiNZ Kingston",
    "displayDate": "Sat, Mar 21, 2020, 7:00 PM - Sun, Mar 22, 2020, 8:00 AM EST",
    "location": "PiNZ Kingston",
    "color": "#ab218e",
    "description": "Strike up some fun on the bowling lanes, challenge your friends to an arcade showdown, and play  games including ping pong, billiards, and cornhole! ",
    "start": "2020-03-21",
    "end": "2020-03-22",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/pinz.html"
  },
  {
    "title": "Pets Badge",
    "displayDate": "Sun, Mar 22, 2020, 10:00 AM - 11:30 AM EST",
    "location": "Buttonwood Park Zoo",
    "color": "#68c8c6",
    "description": "Learn what it means to be a good pet owner and how to care for different animal species.",
    "start": "2020-03-22",
    "end": "2020-03-22",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/pets_badge_3_22.html"
  },
  {
    "title": "Build Your Outdoor Skills: Shelter Building",
    "displayDate": "Sun, Mar 22, 2020, 10:00 AM - 12:00 PM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#C4D82E",
    "description": "Learn how to build an emergency shelter using materials found in the woods and around camp.",
    "start": "2020-03-22",
    "end": "2020-03-22",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/outdoor_skills_3_221.html"
  },
  {
    "title": "Dancer Badge with Abilities Dance",
    "displayDate": "Sun, Mar 22, 2020, 1:00 PM - 2:30 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#F9CD00",
    "description": "Take a dance class with the professional dancers of Abilities Dance—no experience required!",
    "start": "2020-03-22",
    "end": "2020-03-22",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/dancer_3_22.html"
  },
  {
    "title": "Design a Wooden Race Car at New England Sci-Tech",
    "displayDate": "Sun, Mar 22, 2020, 1:00 PM - 3:00 PM EST",
    "location": "New England Sci-Tech",
    "color": "#AB218E",
    "description": "A woodworking expert will help you master the basics of hand tools for measuring, sanding, and building with wood. ",
    "start": "2020-03-22",
    "end": "2020-03-22",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/race_car.html"
  },
  {
    "title": "Horse Sampler",
    "displayDate": "Sun, Mar 22, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Rocking Horse Farms",
    "color": "#F9CD00",
    "description": "Learn the basics of horses at this fun program.",
    "start": "2020-03-22",
    "end": "2020-03-22",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/horse_sampler2.html"
  },
  {
    "title": "Build Your Outdoor Skills: Cooking",
    "displayDate": "Sun, Mar 22, 2020, 1:00 PM - 4:00 PM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#C4D82E",
    "description": "Learn to prepare delicious dishes using a variety of outdoor cooking methods, all while practicing ethical outdoor preservation.",
    "start": "2020-03-22",
    "end": "2020-03-22",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/outdoor_skills_3_22.html"
  },
  {
    "title": "Be an Empathy Superhero",
    "displayDate": "Sun, Mar 22, 2020, 1:00 PM - 4:30 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#68c8c6",
    "description": "Through giggles, games, and giving, you’ll exercise your “empathy muscle” and experience first-hand how you can make a meaningful difference. 2 sessions.",
    "start": "2020-03-22",
    "end": "2020-03-22",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/empathy_hero_3_22.html"
  },
  {
    "title": "Glass Fusing",
    "displayDate": "Sun, Mar 22, 2020, 3:30 PM - 4:30 PM EST",
    "location": "Art Signals (Maynard)",
    "color": "#f9cd00",
    "description": "Learn to make your own fused glass masterpiece! ",
    "start": "2020-03-22",
    "end": "2020-03-22",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/glass_fusing_3_22.html"
  },
  {
    "title": "Programming Robots Badge: Cadette",
    "displayDate": "Sun, Mar 22, 2020, 3:30 PM - 5:30 PM EST",
    "location": "Empow Studios - Newton",
    "color": "#AB218E",
    "description": "Earn the Programming Robots badge while using LEGO® EV3 MINDSTORMS.",
    "start": "2020-03-22",
    "end": "2020-03-22",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/programming_robots_3_22.html"
  },
  {
    "title": "March Gold Award Interviews and Final Presentation",
    "displayDate": "Mon, Mar 23, 2020, 12:00 AM EST",
    "location": "GSEMA Service Centers",
    "color": "#eb0789",
    "description": "Each Girl Scout is asked to participate in an interview to discuss their project plan for approval and to make a final presentation after their final report is submitted.",
    "start": "2020-03-23",
    "end": "2020-03-28",
    "path": "/content/girlscoutseasternmass/en/events-repository/2019/goldaward-interview3.html"
  },
  {
    "title": "MEDIC First Aid and CPR Recertification",
    "displayDate": "Wed, Mar 25, 2020, 6:00 PM - 9:00 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#F27536",
    "description": "This course is for those whose First Aid and CPR certifications are about to expire or have expired within 30 days of the listed session.",
    "start": "2020-03-25",
    "end": "2020-03-25",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/medic_recertification3.html"
  },
  {
    "title": "MEDIC First Aid and CPR Recertification",
    "displayDate": "Wed, Mar 25, 2020, 6:00 PM - 9:00 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#F27536",
    "description": "This course is for those whose First Aid and CPR certifications are about to expire or have expired within 30 days of the listed session.",
    "start": "2020-03-25",
    "end": "2020-03-25",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/medic_recertification8.html"
  },
  {
    "title": "Encampment Director Training",
    "displayDate": "Thu, Mar 26, 2020, 6:30 PM - 8:30 PM EST",
    "location": "Online",
    "color": "#BCBEC0",
    "description": "This training is required for coordinators of service unit or large group camp experiences. ",
    "start": "2020-03-26",
    "end": "2020-03-26",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/encampment1.html"
  },
  {
    "title": "Build Your Outdoor Skills: Navigation",
    "displayDate": "Sat, Mar 28, 2020, 10:00 AM - 12:00 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#C4D82E",
    "description": "Discover the many ways you can navigate through camp using trail signs, compasses, GPS units, and maps.",
    "start": "2020-03-28",
    "end": "2020-03-28",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/outdoor_skills_3_281.html"
  },
  {
    "title": "Zoologist",
    "displayDate": "Sat, Mar 28, 2020, 10:00 AM - 12:00 PM EST",
    "location": "Buttonwood Park Zoo",
    "color": "#AB218E",
    "description": "Earn your Animal Helpers or Voice for Animals badge as you learn about the animals’ daily activities and the history of animals on a working farm. ",
    "start": "2020-03-28",
    "end": "2020-03-28",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/zoologist_3_28.html"
  },
  {
    "title": "Girl Scout Empowerment",
    "displayDate": "Sat, Mar 28, 2020, 10:00 AM - 12:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#68c8c6",
    "description": "This empowerment session includes a facilitated discussion on current issues and the challenges girls are facing.",
    "start": "2020-03-28",
    "end": "2020-03-28",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/empowerment_3_28.html"
  },
  {
    "title": "Put Yourself in the Past",
    "displayDate": "Sat, Mar 28, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Jackson Homestead ",
    "color": "#F9CD00",
    "description": "Imagine yourself in the 1800s. What would your typical day at the historic Jackson Homestead be like?",
    "start": "2020-03-28",
    "end": "2020-03-28",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/past_3_28.html"
  },
  {
    "title": "Sewing: Sit-upons",
    "displayDate": "Sat, Mar 28, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Sandi's Sewing and Design (Bridgewater)",
    "color": "#68c8c6",
    "description": "Sit-upons have been used by Girl Scouts for many years, but this one will be unique because you’ll create it yourself! ",
    "start": "2020-03-28",
    "end": "2020-03-28",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/sandis_sewing_3_27.html"
  },
  {
    "title": "Build Your Outdoor Skills: Cooking",
    "displayDate": "Sat, Mar 28, 2020, 1:00 PM - 4:00 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#C4D82E",
    "description": "Learn to prepare delicious dishes using a variety of outdoor cooking methods, all while practicing ethical outdoor preservation.",
    "start": "2020-03-28",
    "end": "2020-03-28",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/outdoor_skills_3_28.html"
  },
  {
    "title": "Museum of Science Overnight",
    "displayDate": "Sat, Mar 28, 2020, 5:00 PM - Sun, Mar 29, 2020, 11:00 AM EST",
    "location": "Museum of Science",
    "color": "#ab218e",
    "description": "Spend a night at the museum!",
    "start": "2020-03-28",
    "end": "2020-03-29",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/mos_overnight_3_28.html"
  },
  {
    "title": "Build Your Outdoor Skills: Advanced Fire-building",
    "displayDate": "Sun, Mar 29, 2020, 10:00 AM - 12:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#C4D82E",
    "description": "Challenge yourself to start a fire with different ignition techniques, including flint and steel, bow drills, and even batteries.",
    "start": "2020-03-29",
    "end": "2020-03-29",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/outdoor_skills_3_29.html"
  },
  {
    "title": "Comic Artist Badge",
    "displayDate": "Sun, Mar 29, 2020, 12:00 PM - 3:00 PM EST",
    "location": "New Bedford Art Museum/Art Works!",
    "color": "#F9CD00",
    "description": "Explore the world of comics and cartooning using your own characters and stories. ",
    "start": "2020-03-29",
    "end": "2020-03-29",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/art_works_3_29.html"
  },
  {
    "title": "Build Your Outdoor Skills: Knots and Lashing",
    "displayDate": "Sun, Mar 29, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#C4D82E",
    "description": "Learn the traditional Girl Scout skills of knot tying and lashing. ",
    "start": "2020-03-29",
    "end": "2020-03-29",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/outdoor_skills_3_291.html"
  },
  {
    "title": "Northeastern Campus Tour",
    "displayDate": "Sun, Mar 29, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Northeastern University",
    "color": "#f9cd00",
    "description": "The sisters of Kappa Delta will host the Girl Scouts for a fun and educational tour of Northeastern University’s campus.",
    "start": "2020-03-29",
    "end": "2020-03-29",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/campus_tour.html"
  },
  {
    "title": "Cybersecurity Badge: Junior",
    "displayDate": "Sun, Mar 29, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#AB218E",
    "description": "Do you love to solve puzzles, figure out mysteries, and break codes? ",
    "start": "2020-03-29",
    "end": "2020-03-29",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/cybersecurity_3_29.html"
  },
  {
    "title": "Horseback Riding",
    "displayDate": "Sun, Mar 29, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Rocking Horse Farms",
    "color": "#F9CD00",
    "description": "Learn about the basic care of horses, functions of your tack, how to control a horse and then practice riding.",
    "start": "2020-03-29",
    "end": "2020-03-29",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/horseback_riding.html"
  },
  {
    "title": "Handmade Pasta",
    "displayDate": "Sun, Mar 29, 2020, 2:00 PM - 3:30 PM EST",
    "location": "Taste Buds Kitchen (Beverly)",
    "color": "#F9CD00",
    "description": "Discover the secret recipe for making a delicious and easy signature pasta dish!",
    "start": "2020-03-29",
    "end": "2020-03-29",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/taste_buds_3_29.html"
  },
  {
    "title": "Canvas Painting",
    "displayDate": "Sun, Mar 29, 2020, 3:30 PM - 5:00 PM EST",
    "location": "Look What I Made (N. Reading)",
    "color": "#f9cd00",
    "description": "Come get creative! We will walk you through how to make your own canvas painting.",
    "start": "2020-03-29",
    "end": "2020-03-29",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/canvas_painting_329.html"
  },
  {
    "title": "Programming Robots Badge: Junior",
    "displayDate": "Sun, Mar 29, 2020, 3:30 PM - 5:30 PM EST",
    "location": "Empow Studios - Newton",
    "color": "#AB218E",
    "description": "Earn the Programming Robots badge while using LEGO® EV3 MINDSTORMS.",
    "start": "2020-03-29",
    "end": "2020-03-29",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/programming_robots_3_29.html"
  },
  {
    "title": "Designing Robots Badge: Senior/Ambassador",
    "displayDate": "Sun, Mar 29, 2020, 3:30 PM - 5:30 PM EST",
    "location": "Empow Studios - Lexington",
    "color": "#AB218E",
    "description": "Discuss how robots sense, think, and act to help us, then design and build a prototype of your own problem-solving robot.",
    "start": "2020-03-29",
    "end": "2020-03-29",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/designing_robots_sa_3_29.html"
  },
  {
    "title": "Mexican Fiesta",
    "displayDate": "Sun, Mar 29, 2020, 4:00 PM - 5:30 PM EST",
    "location": "Mix It Up Kitchen",
    "color": "#68c8c6",
    "description": "Leave your suitcase behind but bring along your appetite!",
    "start": "2020-03-29",
    "end": "2020-03-29",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/mexican_fiesta.html"
  },
  {
    "title": "Canvas Painting",
    "displayDate": "Sun, Mar 29, 2020, 4:30 PM - 6:00 PM EST",
    "location": "Art Signals (Maynard)",
    "color": "#f9cd00",
    "description": "Come get creative! We will walk you through how to make your own canvas painting.",
    "start": "2020-03-29",
    "end": "2020-03-29",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/canvas_painting_3_29.html"
  },
  {
    "title": "Gold Award Orientation",
    "displayDate": "Tue, Mar 31, 2020, 6:00 PM - 8:00 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#F27536",
    "description": "This free two-hour orientation prepares Seniors and Ambassadors to brainstorm, develop and implement a successful Gold Award project, and teaches adults how best to support them.",
    "start": "2020-03-31",
    "end": "2020-03-31",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/gold-award-orientation11.html"
  },
  {
    "title": "Deadline for Gold Award Project Proposals and Final Reports",
    "displayDate": "Fri, Apr 3, 2020, 5:00 PM EST",
    "location": "Deadline",
    "color": "#eb0789",
    "description": "GSEMA uses deadlines to collect both Gold Award project proposals and Gold Award final reports. In order to be considered for an upcoming in-person interview or final presentation, you must submit by the deadline before.",
    "start": "2020-04-03",
    "end": "2020-04-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2019/goldaward-deadline4.html"
  },
  {
    "title": "Space Science Investigator Badge",
    "displayDate": "Fri, Apr 3, 2020, 5:00 PM - 7:00 PM EST",
    "location": "New England Sci-Tech",
    "color": "#AB218E",
    "description": "Discover the wonders of our celestial universe while earning your Space Science Investigator badge.",
    "start": "2020-04-03",
    "end": "2020-04-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/space_science_4_3.html"
  },
  {
    "title": "Dive-in Movie",
    "displayDate": "Fri, Apr 3, 2020, 6:30 PM - 8:15 PM EST",
    "location": "Raynham Athletic Club",
    "color": "#C4D82E",
    "description": "Enjoy a movie on the big screen while swimming in a heated pool. ",
    "start": "2020-04-03",
    "end": "2020-04-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/dive_in_4_3.html"
  },
  {
    "title": "Sewing: Owl Pillow Saturday",
    "displayDate": "Sat, Apr 4, 2020, 9:00 AM - 11:30 AM EST",
    "location": "Sew Studio of Southborough",
    "color": "#68c8c6",
    "description": "Sew your own owl pillow to celebrate being a Girl Scout! ",
    "start": "2020-04-04",
    "end": "2020-04-04",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/sew_studio_4_4.html"
  },
  {
    "title": "MEDIC First Aid and CPR Recertification",
    "displayDate": "Sat, Apr 4, 2020, 9:00 AM - 12:00 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#F27536",
    "description": "This course is for those whose First Aid and CPR certifications are about to expire or have expired within 30 days of the listed session.",
    "start": "2020-04-04",
    "end": "2020-04-04",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/medic_recertification4.html"
  },
  {
    "title": "Caring for Stranded Ocean Animals: Cadette/Senior/Ambassador",
    "displayDate": "Sat, Apr 4, 2020, 9:00 AM - 12:00 PM EST",
    "location": "National Marine Life Center",
    "color": "#AB218E",
    "description": "Learn about marine animal rescue, rehabilitation, and release through hands-on activities and specimen exploration. ",
    "start": "2020-04-04",
    "end": "2020-04-04",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/caring_ocean_4_41.html"
  },
  {
    "title": "Glass Coaster",
    "displayDate": "Sat, Apr 4, 2020, 10:00 AM - 12:00 PM EST",
    "location": "The Glass Bar",
    "color": "#F9CD00",
    "description": "Create a fused glass coaster!",
    "start": "2020-04-04",
    "end": "2020-04-04",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/coaster_4_4.html"
  },
  {
    "title": "Computer Expert Badge",
    "displayDate": "Sat, Apr 4, 2020, 10:00 AM - 12:00 PM EST",
    "location": "Microsoft Store (The Mall at Rockingham Park)",
    "color": "#AB218E",
    "description": "Get hands-on experience with the latest technology from Microsoft.",
    "start": "2020-04-04",
    "end": "2020-04-04",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/computer_expert_4_4.html"
  },
  {
    "title": "Outdoor Art Creator and Outdoor Art Explorer",
    "displayDate": "Sat, Apr 4, 2020, 10:00 AM - 12:00 PM EST",
    "location": "Soule Homestead",
    "color": "#C4D82E",
    "description": "Fresh air, open space, and the beauty of nature are the perfect ingredients for discovering outdoor art. ",
    "start": "2020-04-04",
    "end": "2020-04-04",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/outdor_art_4_4.html"
  },
  {
    "title": "Build Your Outdoor Skills: Beginner Fire-building",
    "displayDate": "Sat, Apr 4, 2020, 10:00 AM - 12:00 PM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#C4D82E",
    "description": "Gain confidence building a successful fire and get comfortable with fire safety.",
    "start": "2020-04-04",
    "end": "2020-04-04",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/outdoor_skills_4_41.html"
  },
  {
    "title": "Boston University Campus Tour",
    "displayDate": "Sat, Apr 4, 2020, 11:00 AM - 1:00 PM EST",
    "location": "Boston University",
    "color": "#F9CD00",
    "description": "Join the Kappa Deltas for a small group tour of Boston University. ",
    "start": "2020-04-04",
    "end": "2020-04-04",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/bu_campus.html"
  },
  {
    "title": "Super G.I.R.L Race: DIY",
    "displayDate": "Sat, Apr 4, 2020, 11:00 AM - 2:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#AB218E",
    "description": "Engineer your own wooden race car and participate in a GSEMA Super G.I.R.L. Race!",
    "start": "2020-04-04",
    "end": "2020-04-04",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/super_girl.html"
  },
  {
    "title": "Camp Skills Day",
    "displayDate": "Sat, Apr 4, 2020, 11:00 AM - 4:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#C4D82E",
    "description": "Practice your outdoor skills with a day at camp!",
    "start": "2020-04-04",
    "end": "2020-04-04",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/camp_skills_day.html"
  },
  {
    "title": "Put Yourself in the Past",
    "displayDate": "Sat, Apr 4, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Jackson Homestead ",
    "color": "#F9CD00",
    "description": "Imagine yourself in the 1800s. What would your typical day at the historic Jackson Homestead be like?",
    "start": "2020-04-04",
    "end": "2020-04-04",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/past_4_12.html"
  },
  {
    "title": "Public Speaker Badge",
    "displayDate": "Sat, Apr 4, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Camp Rice Moody",
    "color": "#68c8c6",
    "description": "Led by TEDx speaker and MOVE co-founder Ashley Olafsen, girls will brainstorm, write, revise, and present a short speech on a topic of their choosing.",
    "start": "2020-04-04",
    "end": "2020-04-04",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/public_speaker.html"
  },
  {
    "title": "Build Your Outdoor Skills: Jackknives",
    "displayDate": "Sat, Apr 4, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#C4D82E",
    "description": "Learn about knife safety and the usefulness of knives while camping. ",
    "start": "2020-04-04",
    "end": "2020-04-04",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/outdoor_skills_4_4.html"
  },
  {
    "title": "Digital Photographer Badge",
    "displayDate": "Sat, Apr 4, 2020, 1:00 PM - 4:00 PM EST",
    "location": "Microsoft Store (The Mall at Rockingham Park)",
    "color": "#AB218E",
    "description": "Get hands-on experience capturing and editing photos with the latest technology from Microsoft.",
    "start": "2020-04-04",
    "end": "2020-04-04",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/digital_photographer_4_4.html"
  },
  {
    "title": "Caring for Stranded Ocean Animals: Brownie/Junior",
    "displayDate": "Sat, Apr 4, 2020, 1:00 PM - 4:00 PM EST",
    "location": "National Marine Life Center",
    "color": "#AB218E",
    "description": "Learn about marine animal rescue, rehabilitation, and release through hands-on activities and specimen exploration. ",
    "start": "2020-04-04",
    "end": "2020-04-04",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/caring_ocean_4_4.html"
  },
  {
    "title": "MEDIC First Aid and CPR Blended Learning",
    "displayDate": "Sat, Apr 4, 2020, 1:00 PM - 5:00 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#F27536",
    "description": "This course is offered in two parts: an online program and a hands-on skills session with an instructor. ",
    "start": "2020-04-04",
    "end": "2020-04-04",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/medic_blended2.html"
  },
  {
    "title": "Women's Health Badge",
    "displayDate": "Sat, Apr 4, 2020, 3:30 PM - 5:30 PM EST",
    "location": "Camp Rice Moody",
    "color": "#68c8c6",
    "description": "Earn a Women’s Health Badge, while learning about relevant health topics that affect young women! ",
    "start": "2020-04-04",
    "end": "2020-04-04",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/womens_health.html"
  },
  {
    "title": "Sundown Sing-along",
    "displayDate": "Sat, Apr 4, 2020, 6:00 PM - 7:30 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#C4D82E",
    "description": "Bring your whole family to soak up the beauty of camp. ",
    "start": "2020-04-04",
    "end": "2020-04-04",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/sundown_sing_4_4.html"
  },
  {
    "title": "Mystic Aquarium Overnight",
    "displayDate": "Sat, Apr 4, 2020, 7:00 PM - Sun, Apr 5, 2020, 8:00 AM EST",
    "location": "Mystic Aquarium",
    "color": "#AB218E",
    "description": "Dissect a squid, touch live invertebrates, tour Exploration: Wild!, participate in a scavenger hunt, and more during this aquatic overnight. ",
    "start": "2020-04-04",
    "end": "2020-04-05",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/mystic_4_4.html"
  },
  {
    "title": "Letterboxing",
    "displayDate": "Sun, Apr 5, 2020, 9:00 AM - 12:00 PM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#C4D82E",
    "description": "Work as a team to navigate a course in search of sneaky hidden letterboxes.",
    "start": "2020-04-05",
    "end": "2020-04-05",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/letterboxing.html"
  },
  {
    "title": "Art in the Outdoors at deCordova Scultpure Park and Museum",
    "displayDate": "Sun, Apr 5, 2020, 10:30 AM - 12:30 PM EST",
    "location": "deCordova Scultpure Park and Museum",
    "color": "#EC008C",
    "description": "Get inspired by the outdoor artwork at the deCordova Sculpture Park. ",
    "start": "2020-04-05",
    "end": "2020-04-05",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/art_outdoors.html"
  },
  {
    "title": "Dandelion Lotion Workshop",
    "displayDate": "Sun, Apr 5, 2020, 12:00 PM - 2:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#F9CD00",
    "description": "Make all-natural lotion with dandelion-infused oils in this wild herbal workshop. ",
    "start": "2020-04-05",
    "end": "2020-04-05",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/lotion_4_5.html"
  },
  {
    "title": "Drawing Badge with Children's Art Lab",
    "displayDate": "Sun, Apr 5, 2020, 1:00 PM - 3:00 PM EST",
    "location": "GSEMA Service Center - Middleboro",
    "color": "#F9CD00",
    "description": "Embrace innovation and creativity as you design a book cover, draw a logo, or create a comic! ",
    "start": "2020-04-05",
    "end": "2020-04-05",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/childrens_art_lab_4_5.html"
  },
  {
    "title": "Horse Sampler",
    "displayDate": "Sun, Apr 5, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Rocking Horse Farms",
    "color": "#F9CD00",
    "description": "Learn the basics of horses at this fun program.",
    "start": "2020-04-05",
    "end": "2020-04-05",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/horse_sampler3.html"
  },
  {
    "title": "Cybersecurity Badge: Junior",
    "displayDate": "Sun, Apr 5, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#AB218E",
    "description": "Do you love to solve puzzles, figure out mysteries, and break codes? ",
    "start": "2020-04-05",
    "end": "2020-04-05",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/cybersecurity_4_5.html"
  },
  {
    "title": "Book Artist Badge with Second Nature Arts",
    "displayDate": "Sun, Apr 5, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Camp Rice Moody",
    "color": "#F9CD00",
    "description": "Explore different binding techniques and use found text, images, and natural watercolors to make unique artist books. ",
    "start": "2020-04-05",
    "end": "2020-04-05",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/book_artist_badge_4_5.html"
  },
  {
    "title": "GPS Geocaching",
    "displayDate": "Sun, Apr 5, 2020, 1:00 PM - 4:00 PM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#C4D82E",
    "description": "Use GPS technology to navigate your way to hidden treasure.",
    "start": "2020-04-05",
    "end": "2020-04-05",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/gps.html"
  },
  {
    "title": "Girl Scout Day at Fenway Park",
    "displayDate": "Sun, Apr 5, 2020, 1:05 PM EST",
    "location": "Fenway Park",
    "color": "#DD3640",
    "description": "Special offer for Girl Scouts!",
    "start": "2020-04-05",
    "end": "2020-04-05",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/red_sox_4_5.html"
  },
  {
    "title": "Art in the Outdoors at deCordova Scultpure Park and Museum",
    "displayDate": "Sun, Apr 5, 2020, 1:30 PM - 3:30 PM EST",
    "location": "deCordova Scultpure Park and Museum",
    "color": "#EC008C",
    "description": "Get inspired by the outdoor artwork at the deCordova Sculpture Park. ",
    "start": "2020-04-05",
    "end": "2020-04-05",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/art_outdoors1.html"
  },
  {
    "title": "Cupcake Challenge",
    "displayDate": "Sun, Apr 5, 2020, 2:00 PM - 3:30 PM EST",
    "location": "Taste Buds Kitchen (Beverly)",
    "color": "#F9CD00",
    "description": "Put your culinary creativity to the test!",
    "start": "2020-04-05",
    "end": "2020-04-05",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/taste_buds_4_5.html"
  },
  {
    "title": "Go for Gold: Writing a Gold Award Proposal",
    "displayDate": "Sun, Apr 5, 2020, 2:00 PM - 4:00 PM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#eb0789",
    "description": "Got an idea for a Gold Award Project? Come meet with members of the GSEMA Gold Award Committee for support!",
    "start": "2020-04-05",
    "end": "2020-04-05",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/gold_workshop_4_5.html"
  },
  {
    "title": "Wild Herbal First Aid Soap",
    "displayDate": "Sun, Apr 5, 2020, 3:00 PM - 5:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#F9CD00",
    "description": "Make soap from wild herbs known for soothing poison ivy, cuts, eczema, burns, and bug bites.",
    "start": "2020-04-05",
    "end": "2020-04-05",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/soap_4_5.html"
  },
  {
    "title": "Programming Robots Badge: Brownie",
    "displayDate": "Sun, Apr 5, 2020, 3:30 PM - 5:30 PM EST",
    "location": "Empow Studios - Newton",
    "color": "#AB218E",
    "description": "Earn the Programming Robots badge while using LEGO® EV3 MINDSTORMS.",
    "start": "2020-04-05",
    "end": "2020-04-05",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/programming_robots_4_5.html"
  },
  {
    "title": "Book Artist with Second Nature Arts",
    "displayDate": "Sun, Apr 5, 2020, 3:30 PM - 5:30 PM EST",
    "location": "Camp Rice Moody",
    "color": "#F9CD00",
    "description": "Explore different binding techniques and use found text, images, and natural watercolors to make unique artist books. ",
    "start": "2020-04-05",
    "end": "2020-04-05",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/book_artist_4_5.html"
  },
  {
    "title": "Programming Robots Badge: Junior",
    "displayDate": "Sun, Apr 5, 2020, 3:30 PM - 5:30 PM EST",
    "location": "Empow Studios - Lexington",
    "color": "#AB218E",
    "description": "Earn the Programming Robots badge while using LEGO® EV3 MINDSTORMS.",
    "start": "2020-04-05",
    "end": "2020-04-05",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/programming_robotsl_4_5.html"
  },
  {
    "title": "Potter Badge",
    "displayDate": "Wed, Apr 8, 2020, 4:00 PM - 5:30 PM EST",
    "location": "Look What I Made (N. Reading)",
    "color": "#f9cd00",
    "description": "Come have some fun getting your hands dirty and earn your Potter Badge!",
    "start": "2020-04-08",
    "end": "2020-04-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/potter_badge_4_8.html"
  },
  {
    "title": "Snacks Badge",
    "displayDate": "Wed, Apr 8, 2020, 4:30 PM - 5:45 PM EST",
    "location": "Mix It Up Kitchen",
    "color": "#68c8c6",
    "description": "We’ll create 4-5 simple snacks that are healthy yet satisfying. ",
    "start": "2020-04-08",
    "end": "2020-04-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/snacks_4_8.html"
  },
  {
    "title": "Outdoor Basics: Cooking",
    "displayDate": "Sat, Apr 11, 2020, 9:00 AM - 4:00 PM EST",
    "location": "Camp Runels",
    "color": "#BCBEC0",
    "description": "This training is required for troop camping trips with lodge accommodation and will prepare you to plan a girl-led lodge camping trip.",
    "start": "2020-04-11",
    "end": "2020-04-11",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/outdoor_basics_cook_4_11.html"
  },
  {
    "title": "Archery Training",
    "displayDate": "Sat, Apr 11, 2020, 9:00 AM - 5:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#C4D82E",
    "description": "Take your skills to the next level. Learn range and equipment set-up, safety procedures, and proper archer technique.",
    "start": "2020-04-11",
    "end": "2020-04-11",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/archery-training.html"
  },
  {
    "title": "Brownie First Aid Badge",
    "displayDate": "Sat, Apr 11, 2020, 10:00 AM - 12:00 PM EST",
    "location": "Camp Rice Moody",
    "color": "#68c8c6",
    "description": "Practice basic indoor and outdoor first aid techniques during this fun, interactive class. ",
    "start": "2020-04-11",
    "end": "2020-04-11",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/browniefa_4_11.html"
  },
  {
    "title": "Ice Chips Show of Champions",
    "displayDate": "Sat, Apr 11, 2020, 12:00 PM - 2:00 PM EST",
    "location": "Harvard's Bright-Landry Arena",
    "color": "#f9cd00",
    "description": "The longest-running ice show in the country features a dramatic display of colors, lights and outstanding figure skating from some of the best-know names in the sport – both locally and around the globe. ",
    "start": "2020-04-11",
    "end": "2020-04-11",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/ice_chips.html"
  },
  {
    "title": "Potter Badge",
    "displayDate": "Sat, Apr 11, 2020, 12:00 PM - 3:00 PM EST",
    "location": "New Bedford Art Museum/Art Works!",
    "color": "#F9CD00",
    "description": "Get your hands messy in the clay studio as you learn about different types of pots and their uses. ",
    "start": "2020-04-11",
    "end": "2020-04-11",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/art_works_4_12.html"
  },
  {
    "title": "Put Yourself in the Past",
    "displayDate": "Sat, Apr 11, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Jackson Homestead ",
    "color": "#F9CD00",
    "description": "Imagine yourself in the 1800s. What would your typical day at the historic Jackson Homestead be like?",
    "start": "2020-04-11",
    "end": "2020-04-11",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/past_4_11.html"
  },
  {
    "title": "Celebrating Community Badge",
    "displayDate": "Sat, Apr 11, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Old Colony History Museum",
    "color": "#68c8c6",
    "description": "Join the Old Colony History Museum in celebrating Taunton’s revolutionary history",
    "start": "2020-04-11",
    "end": "2020-04-11",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/celebratingcommunity.html"
  },
  {
    "title": "Camp Explorers",
    "displayDate": "Sat, Apr 11, 2020, 1:00 PM - 4:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#C4D82E",
    "description": "Get a head-start on a variety of Outdoor badges, and then round out the day with songs around the campfire.",
    "start": "2020-04-11",
    "end": "2020-04-11",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/camp_explorers_4_11.html"
  },
  {
    "title": "Ice Chips Show of Champions",
    "displayDate": "Sat, Apr 11, 2020, 6:00 PM - 8:00 PM EST",
    "location": "Harvard's Bright-Landry Arena",
    "color": "#f9cd00",
    "description": "The longest-running ice show in the country features a dramatic display of colors, lights and outstanding figure skating from some of the best-know names in the sport – both locally and around the globe. ",
    "start": "2020-04-11",
    "end": "2020-04-11",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/ice_chips1.html"
  },
  {
    "title": "MEDIC First Aid and CPR Blended Learning",
    "displayDate": "Tue, Apr 14, 2020, 10:00 AM - 2:00 PM EST",
    "location": "GSEMA Service Center - Middleboro",
    "color": "#F27536",
    "description": "This course is offered in two parts: an online program and a hands-on skills session with an instructor. ",
    "start": "2020-04-14",
    "end": "2020-04-14",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/medic_blended6.html"
  },
  {
    "title": "Gold Award Orientation",
    "displayDate": "Tue, Apr 14, 2020, 6:30 PM - 8:30 PM EST",
    "location": "GSEMA Service Center - Middleboro",
    "color": "#F27536",
    "description": "This free two-hour orientation prepares Seniors and Ambassadors to brainstorm, develop and implement a successful Gold Award project, and teaches adults how best to support them.",
    "start": "2020-04-14",
    "end": "2020-04-14",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/gold-award-orientation2.html"
  },
  {
    "title": "Potter Badge",
    "displayDate": "Thu, Apr 16, 2020, 4:00 PM - 5:30 PM EST",
    "location": "Art Signals (Maynard)",
    "color": "#f9cd00",
    "description": "Come have some fun getting your hands dirty and earn your Potter Badge!",
    "start": "2020-04-16",
    "end": "2020-04-16",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/potter_badge_4_16.html"
  },
  {
    "title": "Silver Award Orientation",
    "displayDate": "Thu, Apr 16, 2020, 6:30 PM - 8:30 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#F27536",
    "description": "This free two-hour orientation prepares Cadettes to brainstorm, develop and implement a successful Silver Award project, and teaches adults how best to support them.",
    "start": "2020-04-16",
    "end": "2020-04-16",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/silver-award-orientation1.html"
  },
  {
    "title": "Glass Plate",
    "displayDate": "Sat, Apr 18, 2020, 10:00 AM - 12:00 PM EST",
    "location": "The Glass Bar",
    "color": "#F9CD00",
    "description": "Develop your own designs as you learn the basics of cutting glass and fusing it in the kiln. ",
    "start": "2020-04-18",
    "end": "2020-04-18",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/glass_plate_4_16.html"
  },
  {
    "title": "Rocky Shore Tidepooling: Cadette/Senior/Ambassador",
    "displayDate": "Sat, Apr 18, 2020, 1:00 PM - 4:00 PM EST",
    "location": "NEU Marine Science Center (Nahant)",
    "color": "#AB218E",
    "description": "Earn the Oceanography patch as you explore the rocky shore.",
    "start": "2020-04-18",
    "end": "2020-04-18",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/rockyshore_4_18.html"
  },
  {
    "title": "Pottery Making",
    "displayDate": "Sun, Apr 19, 2020, 12:00 PM - 3:00 PM EST",
    "location": "New Bedford Art Museum/Art Works!",
    "color": "#F9CD00",
    "description": "Get your hands messy in the clay studio as you learn about different types of pots and their uses. ",
    "start": "2020-04-19",
    "end": "2020-04-19",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/art_works_4_19.html"
  },
  {
    "title": "Girl Scout Day at Fenway Park",
    "displayDate": "Sun, Apr 19, 2020, 1:05 PM EST",
    "location": "Fenway Park",
    "color": "#DD3640",
    "description": "Special offer for Girl Scouts!",
    "start": "2020-04-19",
    "end": "2020-04-19",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/red_sox_4_19.html"
  },
  {
    "title": "Handmade Pasta",
    "displayDate": "Sun, Apr 19, 2020, 2:00 PM - 3:30 PM EST",
    "location": "Taste Buds Kitchen (Beverly)",
    "color": "#F9CD00",
    "description": "Discover the secret recipe for making a delicious and easy signature pasta dish!",
    "start": "2020-04-19",
    "end": "2020-04-19",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/taste_buds_4_19.html"
  },
  {
    "title": "New Cuisines Badge",
    "displayDate": "Sun, Apr 19, 2020, 2:00 PM - 3:30 PM EST",
    "location": "Taste Buds Kitchen (North Andover)",
    "color": "#F9CD00",
    "description": "Create authentic Thai dishes that will please your every tastebud and impress your family and friends.",
    "start": "2020-04-19",
    "end": "2020-04-19",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/taste_buds_4_191.html"
  },
  {
    "title": "Eco Trekker Badge with Mass Audubon",
    "displayDate": "Sun, Apr 19, 2020, 2:00 PM - 4:00 PM EST",
    "location": "Mass Audubon Tidmarsh Wildlife Sanctuary ",
    "color": "#C4D82E",
    "description": "Come to Tidmarsh Wildlife Sanctuary for a hike that will let you discover your important role in nature and explore environmental issues in your community! ",
    "start": "2020-04-19",
    "end": "2020-04-19",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/eco_trekker.html"
  },
  {
    "title": "April Vacation Adventures",
    "displayDate": "Tue, Apr 21, 2020, 9:00 AM - Fri, Apr 24, 2020, 4:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#C4D82E",
    "description": "Experience camp and meet new friends during April vacation week.",
    "start": "2020-04-21",
    "end": "2020-04-24",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/apr_vac.html"
  },
  {
    "title": "G.I.R.L. Leadership Academy",
    "displayDate": "Tue, Apr 21, 2020, 9:00 AM - Fri, Apr 24, 2020, 4:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#eb0789",
    "description": "Take a week to take the lead!",
    "start": "2020-04-21",
    "end": "2020-04-24",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/lead_academy.html"
  },
  {
    "title": "Girl Power on the Hill",
    "displayDate": "Wed, Apr 22, 2020, 9:00 AM - 4:00 PM EST",
    "location": "Massachusetts State House",
    "color": "#F27536",
    "description": "Join girls from across the state to learn how to use your voice for change.",
    "start": "2020-04-22",
    "end": "2020-04-22",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/girl_power.html"
  },
  {
    "title": "Challenge Accepted Overnight",
    "displayDate": "Fri, Apr 24, 2020, 10:00 AM - Sat, Apr 25, 2020, 11:00 AM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#C4D82E",
    "description": "Risk-takers unite for the ultimate challenge course adventure!",
    "start": "2020-04-24",
    "end": "2020-04-25",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/challenge_overnight.html"
  },
  {
    "title": "Detective and Spies Overnight",
    "displayDate": "Fri, Apr 24, 2020, 5:30 PM - Sat, Apr 25, 2020, 8:30 AM EST",
    "location": "EcoTarium",
    "color": "#AB218E",
    "description": "Embrace an evening full of intrigue and investigation, then finish the night with a show in the planetarium! ",
    "start": "2020-04-24",
    "end": "2020-04-25",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/detective_and_spies_4_24.html"
  },
  {
    "title": "Outdoor Basics: Cooking",
    "displayDate": "Sat, Apr 25, 2020, 9:00 AM - 4:00 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#BCBEC0",
    "description": "This training is required for troop camping trips with lodge accommodation and will prepare you to plan a girl-led lodge camping trip.",
    "start": "2020-04-25",
    "end": "2020-04-25",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/outdoor_basics_cook_4_251.html"
  },
  {
    "title": "Outdoor Basics: Cooking",
    "displayDate": "Sat, Apr 25, 2020, 9:00 AM - 4:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#BCBEC0",
    "description": "This training is required for troop camping trips with lodge accommodation and will prepare you to plan a girl-led lodge camping trip.",
    "start": "2020-04-25",
    "end": "2020-04-25",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/outdoor_basics_cook_4_25.html"
  },
  {
    "title": "Electronics Workshop at New England Sci-Tech",
    "displayDate": "Sat, Apr 25, 2020, 10:00 AM - 12:00 PM EST",
    "location": "New England Sci-Tech",
    "color": "#AB218E",
    "description": "Make cool light-up gadgets as you explore the basic components and functions of electronics and direct current circuitry.",
    "start": "2020-04-25",
    "end": "2020-04-25",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/electronics_4_25.html"
  },
  {
    "title": "Youth Mental Health First Aid",
    "displayDate": "Sat, Apr 25, 2020, 10:00 AM - 7:00 PM EST",
    "location": "GSEMA Service Center - Middleboro",
    "color": "#BCBEC0",
    "description": "Introduces participants to the unique risk factors and warning signs of mental health problems in adolescents, builds understanding of the importance of early intervention, and teaches individuals how to help an adolescent in crisis or experiencing a mental health challenge. ",
    "start": "2020-04-25",
    "end": "2020-04-25",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/youth-mental-health111.html"
  },
  {
    "title": "Youth Mental Health First Aid",
    "displayDate": "Sat, Apr 25, 2020, 10:00 AM - 7:00 PM EST",
    "location": "GSEMA Service Center - Middleboro",
    "color": "#BCBEC0",
    "description": "Introduces participants to the unique risk factors and warning signs of mental health problems in adolescents, builds understanding of the importance of early intervention, and teaches individuals how to help an adolescent in crisis or experiencing a mental health challenge. ",
    "start": "2020-04-25",
    "end": "2020-04-25",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/youth-mental-health1.html"
  },
  {
    "title": "Glass Sandblasting Class",
    "displayDate": "Sat, Apr 25, 2020, 12:00 PM - 2:00 PM EST",
    "location": "North Shore Glass School",
    "color": "#F9CD00",
    "description": "You will learn how to safely use a sandblaster to let your design shine. ",
    "start": "2020-04-25",
    "end": "2020-04-25",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/sandblasting_4_25.html"
  },
  {
    "title": "Outdoor Basics: Camping",
    "displayDate": "Sat, Apr 25, 2020, 3:30 PM - Sun, Apr 26, 2020, 10:00 AM EST",
    "location": "Camp Maude Eaton",
    "color": "#BCBEC0",
    "description": "This training is required for troop camping trips with tent accommodation and will prepare you to plan a girl-led camping trip in either lodges or tents",
    "start": "2020-04-25",
    "end": "2020-04-26",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/outdoor_basics_4_25.html"
  },
  {
    "title": "Outdoor Basics: Camping",
    "displayDate": "Sat, Apr 25, 2020, 3:30 PM - Sun, Apr 26, 2020, 10:00 AM EST",
    "location": "Camp Cedar Hill",
    "color": "#BCBEC0",
    "description": "This training is required for troop camping trips with tent accommodation and will prepare you to plan a girl-led camping trip in either lodges or tents",
    "start": "2020-04-25",
    "end": "2020-04-26",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/outdoor_basics_4_251.html"
  },
  {
    "title": "The Mighty Sandwich11",
    "displayDate": "Sun, Apr 26, 2020, 11:00 AM - 12:30 PM EST",
    "location": "Milk Street Kitchen",
    "color": "#68c8c6",
    "description": "Explore sandwiches across the world.",
    "start": "2020-04-26",
    "end": "2020-04-26",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/mighty_sandwich1.html"
  },
  {
    "title": "Boston Ballet: Dancer Badge",
    "displayDate": "Sun, Apr 26, 2020, 11:00 AM - 12:30 PM EST",
    "location": "Boston Ballet School",
    "color": "#F9CD00",
    "description": "Participate in a warm-up and skill-building exercises, then learn a group dance.",
    "start": "2020-04-26",
    "end": "2020-04-26",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/dancer_4_26.html"
  },
  {
    "title": "Mass Escape Room",
    "displayDate": "Sun, Apr 26, 2020, 11:00 AM - 1:00 PM EST",
    "location": "Mass Escape Room",
    "color": "#F9CD00",
    "description": "Unleash your inner go-getter at this hands-on mental adventure game.",
    "start": "2020-04-26",
    "end": "2020-04-26",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/mass_escape_4_26.html"
  },
  {
    "title": "Playing the Past Badge",
    "displayDate": "Sun, Apr 26, 2020, 11:00 AM - 2:00 PM EST",
    "location": "Lexington Historical Society",
    "color": "#F9CD00",
    "description": "Experience life as a Lexington resident in 1775!",
    "start": "2020-04-26",
    "end": "2020-04-26",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/past_4_26.html"
  },
  {
    "title": "Programming Robots Badge at New England Sci-Tech",
    "displayDate": "Sun, Apr 26, 2020, 1:00 PM - 3:00 PM EST",
    "location": "New England Sci-Tech",
    "color": "#AB218E",
    "description": "Have fun while you develop your design and programming skills by creating software to control a robot.",
    "start": "2020-04-26",
    "end": "2020-04-26",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/prog_robots1.html"
  },
  {
    "title": "Eco Explorer and Eco Advocate Badges with Mass Audobon",
    "displayDate": "Sun, Apr 26, 2020, 2:00 PM - 4:00 PM EST",
    "location": "Mass Audubon Tidmarsh Wildlife Sanctuary ",
    "color": "#AB218E",
    "description": "Become an Eco-Explorer by exploring the biodiversity at Tidmarsh Wildlife Sanctuary, why it matters, and how you can help!",
    "start": "2020-04-26",
    "end": "2020-04-26",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/eco_4_26.html"
  },
  {
    "title": "Junior Olympic Archery Development Spring Session",
    "displayDate": "Sun, Apr 26, 2020, 3:00 PM - Sun, Jun 7, 2020, 5:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#C4D82E",
    "description": "JOAD is designed to monitor and enhance each archer’s growth and development in this great sport.",
    "start": "2020-04-26",
    "end": "2020-06-07",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/joad.html"
  },
  {
    "title": "Horse Sampler",
    "displayDate": "Sun, Apr 26, 2020, 3:00 PM - 5:00 PM EST",
    "location": "Lil' Folk Farm",
    "color": "#F9CD00",
    "description": "Learn about horse grooming, care and feeding, and how to lead, mount, and steer a horse in this introduction to horseback riding.",
    "start": "2020-04-26",
    "end": "2020-04-26",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/horse_sampler.html"
  },
  {
    "title": "Pottery Painting",
    "displayDate": "Sun, Apr 26, 2020, 3:30 PM - 4:30 PM EST",
    "location": "Look What I Made (N. Reading)",
    "color": "#f9cd00",
    "description": "You will get to pick from a variety of pottery pieces to paint. Everything will be glazed and fired in our kiln and ready to pick up in one week. ",
    "start": "2020-04-26",
    "end": "2020-04-26",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/pottery_painting_4_26.html"
  },
  {
    "title": "Art of Glass",
    "displayDate": "Sun, Apr 26, 2020, 3:30 PM - 5:30 PM EST",
    "location": "Diablo Glass School",
    "color": "#F9CD00",
    "description": "Experience a live glassblowing demonstration, and create your own fused glass pendant using flame-working techniques.",
    "start": "2020-04-26",
    "end": "2020-04-26",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/art_glass_4_26.html"
  },
  {
    "title": "Pottery Painting",
    "displayDate": "Sun, Apr 26, 2020, 4:00 PM - 5:00 PM EST",
    "location": "Art Signals (Maynard)",
    "color": "#f9cd00",
    "description": "You will get to pick from a variety of pottery pieces to paint. Everything will be glazed and fired in our kiln and ready to pick up in one week. ",
    "start": "2020-04-26",
    "end": "2020-04-26",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/pottery_painting_4_261.html"
  },
  {
    "title": "Bronze and Silver Award Online Informational Session",
    "displayDate": "Wed, Apr 29, 2020, 6:00 PM - 7:00 PM EST",
    "location": "Online",
    "color": "#eb0789",
    "description": "Join us for an online session to learn more about the Bronze and Silver Award.",
    "start": "2020-04-29",
    "end": "2020-04-29",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/bronze_silver_info_4_29.html"
  },
  {
    "title": "Potter Badge",
    "displayDate": "Fri, May 1, 2020, 4:00 PM - 5:30 PM EST",
    "location": "Look What I Made (N. Reading)",
    "color": "#f9cd00",
    "description": "Come have some fun getting your hands dirty and earn your Potter Badge!",
    "start": "2020-05-01",
    "end": "2020-05-01",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/potter_badge_5_1.html"
  },
  {
    "title": "Science for Wizards Overnight",
    "displayDate": "Fri, May 1, 2020, 5:30 PM - Sat, May 2, 2020, 8:30 AM EST",
    "location": "EcoTarium",
    "color": "#AB218E",
    "description": "Consider yourself enrolled at EcoTarium: School of Science-craft and Magical Inquiry!",
    "start": "2020-05-01",
    "end": "2020-05-02",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/science_wizards_5_1.html"
  },
  {
    "title": "May Gold Award Interviews and Final Presentation",
    "displayDate": "Sat, May 2, 2020, 12:00 AM EST",
    "location": "GSEMA Service Centers",
    "color": "#eb0789",
    "description": "Each Girl Scout is asked to participate in an interview to discuss their project plan for approval and to make a final presentation after their final report is submitted.",
    "start": "2020-05-02",
    "end": "2020-05-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2019/goldaward-interview4.html"
  },
  {
    "title": "Save the Date! Cookies for a Cause",
    "displayDate": "Sat, May 2, 2020, 9:00 AM - 10:30 AM EST",
    "location": "Hanscom Air Force Base",
    "color": "#00AE58",
    "description": "Cookies for Cause Event on May 2, 2020 for cookie donations to military personnel at Hanscom AFB.",
    "start": "2020-05-02",
    "end": "2020-05-02",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/cookies_cause.html"
  },
  {
    "title": "They Persisted Patch Program at Edward M Kennedy Institute for the US Senate",
    "displayDate": "Sat, May 2, 2020, 9:00 AM - 12:00 PM EST",
    "location": "The Edward M Kennedy Institute for the US Senate",
    "color": "#68c8c6",
    "description": "Learn more about some legendary female go-getters and risk-takers from throughout history.",
    "start": "2020-05-02",
    "end": "2020-05-02",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/they_persisted.html"
  },
  {
    "title": "Outdoor Basics: Cooking",
    "displayDate": "Sat, May 2, 2020, 9:00 AM - 4:00 PM EST",
    "location": "Camp Runels",
    "color": "#BCBEC0",
    "description": "This training is required for troop camping trips with lodge accommodation and will prepare you to plan a girl-led lodge camping trip.",
    "start": "2020-05-02",
    "end": "2020-05-02",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/outdoor_basics_cook_5_21.html"
  },
  {
    "title": "Outdoor Basics: Cooking",
    "displayDate": "Sat, May 2, 2020, 9:00 AM - 4:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#BCBEC0",
    "description": "This training is required for troop camping trips with lodge accommodation and will prepare you to plan a girl-led lodge camping trip.",
    "start": "2020-05-02",
    "end": "2020-05-02",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/outdoor_basics_cook_5_2.html"
  },
  {
    "title": "Glass Coaster",
    "displayDate": "Sat, May 2, 2020, 10:00 AM - 12:00 PM EST",
    "location": "The Glass Bar",
    "color": "#F9CD00",
    "description": "Create a fused glass coaster!",
    "start": "2020-05-02",
    "end": "2020-05-02",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/coaster_5_2.html"
  },
  {
    "title": "Put Yourself in the Past",
    "displayDate": "Sat, May 2, 2020, 10:00 AM - 12:00 PM EST",
    "location": "Jackson Homestead ",
    "color": "#F9CD00",
    "description": "Imagine yourself in the 1800s. What would your typical day at the historic Jackson Homestead be like?",
    "start": "2020-05-02",
    "end": "2020-05-02",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/past_5_2.html"
  },
  {
    "title": "Expedition to Mars",
    "displayDate": "Sat, May 2, 2020, 10:00 AM - 12:00 PM EST",
    "location": "Christa McCauliffe Center at Framingham State University",
    "color": "#AB218E",
    "description": "Experience space adventure at the Christa McAuliffe Center at Framingham State University.",
    "start": "2020-05-02",
    "end": "2020-05-02",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/expedition_5_2.html"
  },
  {
    "title": "Textile Artist Badge: A Century of Fashion Design 1850-1960",
    "displayDate": "Sat, May 2, 2020, 10:00 AM - 12:00 PM EST",
    "location": "Centerville Historical Museum ",
    "color": "#f9cd00",
    "description": "Using selections from the museum's extensive historic costume collection, you’ll examine some of the significant changes in fashion over more than a century’s time. ",
    "start": "2020-05-02",
    "end": "2020-05-02",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/textile_artist_5_2.html"
  },
  {
    "title": "Drawing Badge",
    "displayDate": "Sat, May 2, 2020, 10:00 AM - 12:00 PM EST",
    "location": "Fuller Craft Museum",
    "color": "#f9cd00",
    "description": "Create a still life display and explore perspective in artwork.",
    "start": "2020-05-02",
    "end": "2020-05-02",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/drawing_5_2.html"
  },
  {
    "title": "Gals' Night Out: Platform Tents",
    "displayDate": "Sat, May 2, 2020, 10:00 AM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#C4D82E",
    "description": "Spend a night at camp with your mom or another special female adult!",
    "start": "2020-05-02",
    "end": "2020-05-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/gals_5_2.html"
  },
  {
    "title": "Boston North End History and Pizza Tour",
    "displayDate": "Sat, May 2, 2020, 11:00 AM - 1:00 PM EST",
    "location": "Rose Kennedy Greenway",
    "color": "#F9CD00",
    "description": "Discover historic Boston slice by delicious slice!",
    "start": "2020-05-02",
    "end": "2020-05-02",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/pizza_tour_5_2.html"
  },
  {
    "title": "Glass Pendant Making",
    "displayDate": "Sat, May 2, 2020, 12:00 PM - 2:00 PM EST",
    "location": "North Shore Glass School",
    "color": "#F9CD00",
    "description": "Learn the ancient art of flameworking and make your own beautiful pendant.",
    "start": "2020-05-02",
    "end": "2020-05-02",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/glass_pendant_5_2.html"
  },
  {
    "title": "Painting Badge",
    "displayDate": "Sat, May 2, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Fuller Craft Museum",
    "color": "# f9cd00",
    "description": "Create your own masterpiece on canvas and learn about where artists find their ideas.",
    "start": "2020-05-02",
    "end": "2020-05-02",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/painting_badge_5_2.html"
  },
  {
    "title": "Bugs Badge",
    "displayDate": "Sat, May 2, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Buttonwood Park Zoo",
    "color": "#AB218E",
    "description": "Explore various habitats throughout the zoo and compare wild and domestic animals.",
    "start": "2020-05-02",
    "end": "2020-05-02",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/bugs_badge_5_2.html"
  },
  {
    "title": "100th Anniversary of Women's Suffrage Event at The Edward M Kennedy Institute for the US Senate",
    "displayDate": "Sat, May 2, 2020, 1:00 PM - 4:00 PM EST",
    "location": "The Edward M Kennedy Institute for the US Senate",
    "color": "#68c8c6",
    "description": "Learn about some of the legislative milestones that led to the passage of the 19th Amendment.",
    "start": "2020-05-02",
    "end": "2020-05-02",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/suffrage_event.html"
  },
  {
    "title": "Aerial Yoga: Junior",
    "displayDate": "Sat, May 2, 2020, 2:00 PM - 3:30 PM EST",
    "location": "On Common Ground Yoga",
    "color": "#F9CD00",
    "description": "Fly high! Stretch, strengthen, relax, and have fun with aerial silks.",
    "start": "2020-05-02",
    "end": "2020-05-02",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/aerial_yoga_5_21.html"
  },
  {
    "title": "Outdoor Basics: Camping",
    "displayDate": "Sat, May 2, 2020, 3:30 PM - Sun, May 3, 2020, 10:00 AM EST",
    "location": "Camp Cedar Hill",
    "color": "#BCBEC0",
    "description": "This training is required for troop camping trips with tent accommodation and will prepare you to plan a girl-led camping trip in either lodges or tents",
    "start": "2020-05-02",
    "end": "2020-05-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/outdoor_basics_5_2.html"
  },
  {
    "title": "Outdoor Basics: Camping",
    "displayDate": "Sat, May 2, 2020, 3:30 PM - Sun, May 3, 2020, 10:00 AM EST",
    "location": "Camp Runels",
    "color": "#BCBEC0",
    "description": "This training is required for troop camping trips with tent accommodation and will prepare you to plan a girl-led camping trip in either lodges or tents",
    "start": "2020-05-02",
    "end": "2020-05-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/outdoor_basics_5_21.html"
  },
  {
    "title": "Aerial Yoga: Cadette",
    "displayDate": "Sat, May 2, 2020, 4:00 PM - 5:30 PM EST",
    "location": "On Common Ground Yoga",
    "color": "#F9CD00",
    "description": "Fly high! Stretch, strengthen, relax, and have fun with aerial silks.",
    "start": "2020-05-02",
    "end": "2020-05-02",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/aerial_yoga_5_2.html"
  },
  {
    "title": "Museum of Science Overnight",
    "displayDate": "Sat, May 2, 2020, 5:00 PM - Sun, May 3, 2020, 11:00 AM EST",
    "location": "Museum of Science",
    "color": "#ab218e",
    "description": "Spend a night at the museum!",
    "start": "2020-05-02",
    "end": "2020-05-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/mos_overnight_5_2.html"
  },
  {
    "title": "Glow-in-the-Dark Lock-in",
    "displayDate": "Sat, May 2, 2020, 6:00 PM - Sun, May 3, 2020, 9:00 AM EST",
    "location": "Camp Cedar Hill",
    "color": "#C4D82E",
    "description": "Get ready to have some glow-in-the-dark fun!",
    "start": "2020-05-02",
    "end": "2020-05-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/glow_lock_in_5.html"
  },
  {
    "title": "Crazy About Critters Camp-in",
    "displayDate": "Sat, May 2, 2020, 6:00 PM - Sun, May 3, 2020, 9:00 AM EST",
    "location": "Buttonwood Park Zoo",
    "color": "#AB218E",
    "description": "Spend the night at Buttonwood Park Zoo and learn what it takes to run a zoo.",
    "start": "2020-05-02",
    "end": "2020-05-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/critter_campin_5_2.html"
  },
  {
    "title": "Snorin' Roarin' Overnight",
    "displayDate": "Sat, May 2, 2020, 7:00 PM - Sun, May 3, 2020, 9:00 AM EST",
    "location": "Franklin Park Zoo",
    "color": "#AB218E",
    "description": "See your favorite zoo creatures in a whole new light—the dark!",
    "start": "2020-05-02",
    "end": "2020-05-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/snorin_roarin_5_2.html"
  },
  {
    "title": "Challenge Course: Low",
    "displayDate": "Sun, May 3, 2020, 10:00 AM - 12:00 PM EST",
    "location": "Camp Runels",
    "color": "#C4D82E",
    "description": "Build your teamwork skills through action-oriented learning as you test your abilities on the low elements of the challenge course.",
    "start": "2020-05-03",
    "end": "2020-05-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/challenge_low_53.html"
  },
  {
    "title": "Canoe Fun",
    "displayDate": "Sun, May 3, 2020, 10:00 AM - 12:00 PM EST",
    "location": "Camp Favorite",
    "color": "#C4D82E",
    "description": "Learn basic paddling techniques, how to get in and out of a canoe, and how to properly put on a personal flotation device. ",
    "start": "2020-05-03",
    "end": "2020-05-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/canoe_fun_5_3.html"
  },
  {
    "title": "Challenge Course: Zip Line",
    "displayDate": "Sun, May 3, 2020, 10:00 AM - 3:00 PM EST",
    "location": "Camp Runels",
    "color": "#C4D82E",
    "description": "Climb up to a platform among the trees and soar through the air down the zip line, cheered on by your fellow Girl Scouts. 2 sessions.",
    "start": "2020-05-03",
    "end": "2020-05-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/zipline.html"
  },
  {
    "title": "Painting Badge",
    "displayDate": "Sun, May 3, 2020, 10:30 AM - 12:30 PM EST",
    "location": "deCordova Scultpure Park and Museum",
    "color": "#EC008C",
    "description": "Let deCordova’s unique 30-acre sculpture park and museum galleries inspire your inner artist!",
    "start": "2020-05-03",
    "end": "2020-05-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/painting.html"
  },
  {
    "title": "Scuba Diving",
    "displayDate": "Sun, May 3, 2020, 10:30 AM - 2:00 PM EST",
    "location": "Andover/N.Andover YMCA",
    "color": "#f9cd00",
    "description": "Take the plunge into new underwater adventures! 3 sessions.",
    "start": "2020-05-03",
    "end": "2020-05-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/scuba_5_3.html"
  },
  {
    "title": "Boston Ballet: Dancer Badge",
    "displayDate": "Sun, May 3, 2020, 11:00 AM - 12:30 PM EST",
    "location": "Boston Ballet School",
    "color": "#F9CD00",
    "description": "Participate in a warm-up and skill-building exercises, then learn a group dance.",
    "start": "2020-05-03",
    "end": "2020-05-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/dancer_5_3.html"
  },
  {
    "title": "Bridging to Brownies",
    "displayDate": "Sun, May 3, 2020, 11:00 AM - 1:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#F9CD00",
    "description": " Join us for National Bridging Week and engage with older girls while you try fun Girl Scout Brownie activities and share your favorite part of being a Daisy.",
    "start": "2020-05-03",
    "end": "2020-05-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/bridging_brownies.html"
  },
  {
    "title": "Playing the Past Badge",
    "displayDate": "Sun, May 3, 2020, 11:00 AM - 2:00 PM EST",
    "location": "Lexington Historical Society",
    "color": "#F9CD00",
    "description": "Experience life as a Lexington resident in 1775!",
    "start": "2020-05-03",
    "end": "2020-05-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/past_5_3.html"
  },
  {
    "title": "Puzzle Break: The Grimm Escape",
    "displayDate": "Sun, May 3, 2020, 12:00 PM - 1:00 PM EST",
    "location": "Puzzle Break",
    "color": "#F9CD00",
    "description": "Solve puzzles, decode clues, and work together to “escape the room” at Puzzle Break",
    "start": "2020-05-03",
    "end": "2020-05-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/puzzle_break_5_3.html"
  },
  {
    "title": "Painting Badge",
    "displayDate": "Sun, May 3, 2020, 1:00 PM - 3:00 PM EST",
    "location": "New Bedford Art Museum/Art Works!",
    "color": "#F9CD00",
    "description": "Discover your inner artist at the New Bedford Art Museum.",
    "start": "2020-05-03",
    "end": "2020-05-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/art_works_5_3.html"
  },
  {
    "title": "To Space! A STEM Workshop",
    "displayDate": "Sun, May 3, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#AB218E",
    "description": "Discover the challenges of space travel with hands-on activities and demonstrations led by Katie Slivensky, STEM educator and author of The Countdown Conspiracy. ",
    "start": "2020-05-03",
    "end": "2020-05-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/to_space1.html"
  },
  {
    "title": "Glass Pendant Making",
    "displayDate": "Sun, May 3, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Pairpoint Glass School",
    "color": "#F9CD00",
    "description": "Try your hand at making your own glass pendant with the experts at Pairpoint Glass School.",
    "start": "2020-05-03",
    "end": "2020-05-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/glass_pendant_5_3.html"
  },
  {
    "title": "Horseback Riding",
    "displayDate": "Sun, May 3, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Rocking Horse Farms",
    "color": "#F9CD00",
    "description": "Learn about the basic care of horses, functions of your tack, how to control a horse and then practice riding.",
    "start": "2020-05-03",
    "end": "2020-05-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/horseback_riding1.html"
  },
  {
    "title": "Finding Common Ground Badge",
    "displayDate": "Sun, May 3, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#68c8c6",
    "description": "Learn how to handle conflict in a constructive way.",
    "start": "2020-05-03",
    "end": "2020-05-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/finding_common_ground.html"
  },
  {
    "title": "Archery",
    "displayDate": "Sun, May 3, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Camp Favorite",
    "color": "#C4D82E",
    "description": "Learn the basics of bows and arrows, and how to aim for a bull’s-eye.",
    "start": "2020-05-03",
    "end": "2020-05-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/archery_5_3_fav.html"
  },
  {
    "title": "Pets Badge",
    "displayDate": "Sun, May 3, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Buttonwood Park Zoo",
    "color": "#68c8c6",
    "description": "Learn what it means to be a good pet owner and how to care for different animal species.",
    "start": "2020-05-03",
    "end": "2020-05-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/pets_badge_5_3.html"
  },
  {
    "title": "Camp Explorers",
    "displayDate": "Sun, May 3, 2020, 1:00 PM - 4:00 PM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#C4D82E",
    "description": "Get a head-start on a variety of Outdoor badges, and then round out the day with songs around the campfire.",
    "start": "2020-05-03",
    "end": "2020-05-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/camp_explorers_5_3.html"
  },
  {
    "title": "Confident in Your Own Skin",
    "displayDate": "Sun, May 3, 2020, 1:00 PM - 5:30 PM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#68c8c6",
    "description": "Learn and practice strategies and tools for confidence, social awareness, and self-care. ",
    "start": "2020-05-03",
    "end": "2020-05-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/confident_5_3.html"
  },
  {
    "title": "Eco-lanterns",
    "displayDate": "Sun, May 3, 2020, 1:00 PM - 5:30 PM EST",
    "location": "Camp Rice Moody",
    "color": "#F9CD00",
    "description": "Bring your vision to life by upcycling materials and transforming a coffee can or Mason jar into a glowing winter wonder-lantern.",
    "start": "2020-05-03",
    "end": "2020-05-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/eco_lanterns.html"
  },
  {
    "title": "Archery",
    "displayDate": "Sun, May 3, 2020, 1:00 PM - 6:00 PM EST",
    "location": "Camp Runels",
    "color": "#C4D82E",
    "description": "Learn the basics of bows and arrows, and how to aim for a bull’s-eye. 2 sessions.",
    "start": "2020-05-03",
    "end": "2020-05-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/archery_5_3.html"
  },
  {
    "title": "Scuba Diving Adventure",
    "displayDate": "Sun, May 3, 2020, 1:30 PM - 3:00 PM EST",
    "location": "Andover/N.Andover YMCA",
    "color": "#f9cd00",
    "description": "Take your skills to the next level. ",
    "start": "2020-05-03",
    "end": "2020-05-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/scuba_adv_5_3.html"
  },
  {
    "title": "Painting Badge",
    "displayDate": "Sun, May 3, 2020, 1:30 PM - 3:30 PM EST",
    "location": "deCordova Scultpure Park and Museum",
    "color": "#EC008C",
    "description": "Let deCordova’s unique 30-acre sculpture park and museum galleries inspire your inner artist!",
    "start": "2020-05-03",
    "end": "2020-05-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/painting1.html"
  },
  {
    "title": "Young Farmers",
    "displayDate": "Sun, May 3, 2020, 3:00 PM - 5:00 PM EST",
    "location": "Lil' Folk Farm",
    "color": "#F9CD00",
    "description": "Spend time with animals in the Lil’ Folk Farm petting area. ",
    "start": "2020-05-03",
    "end": "2020-05-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/young_farmers.html"
  },
  {
    "title": "Designing Robots Badge: Junior",
    "displayDate": "Sun, May 3, 2020, 3:30 PM - 5:30 PM EST",
    "location": "Empow Studios (Lexington)",
    "color": "#AB218E",
    "description": "Learn how some robots imitate nature and build your own walking robot using LEGO® MINDSTORMS® EV3.",
    "start": "2020-05-03",
    "end": "2020-05-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/designing_robotsl_5_3.html"
  },
  {
    "title": "Designing Robots Badge: Brownie",
    "displayDate": "Sun, May 3, 2020, 3:30 PM - 5:30 PM EST",
    "location": "Empow Studios (Newton)",
    "color": "#AB218E",
    "description": "Learn how some robots imitate nature and build your own walking robot using LEGO® MINDSTORMS® EV3.",
    "start": "2020-05-03",
    "end": "2020-05-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/designing_robots_5_3.html"
  },
  {
    "title": "Challenge Course: Low",
    "displayDate": "Sun, May 3, 2020, 4:00 PM - 6:00 PM EST",
    "location": "Camp Favorite",
    "color": "#C4D82E",
    "description": "Build your teamwork skills through action-oriented learning as you test your abilities on the low elements of the challenge course.",
    "start": "2020-05-03",
    "end": "2020-05-03",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/challenge_low_5_3.html"
  },
  {
    "title": "Space Science Investigator Badge",
    "displayDate": "Tue, May 5, 2020, 5:00 PM - 7:00 PM EST",
    "location": "New England Sci-Tech",
    "color": "#AB218E",
    "description": "Discover the wonders of our celestial universe while earning your Space Science Investigator badge.",
    "start": "2020-05-05",
    "end": "2020-05-05",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/space_science_5_5.html"
  },
  {
    "title": "Teen Escape: Drop-off Option",
    "displayDate": "Fri, May 8, 2020, 4:00 PM - Sun, May 10, 2020, 10:00 AM EST",
    "location": "Camp Runels",
    "color": "#C4D82E",
    "description": "Join us for a thrilling weekend at camp!",
    "start": "2020-05-08",
    "end": "2020-05-10",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/teen_escape2.html"
  },
  {
    "title": "Teen Escape",
    "displayDate": "Fri, May 8, 2020, 4:00 PM - Sun, May 10, 2020, 10:00 AM EST",
    "location": "Camp Runels",
    "color": "#C4D82E",
    "description": "Join us for a thrilling weekend at camp!",
    "start": "2020-05-08",
    "end": "2020-05-10",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/teen_escape.html"
  },
  {
    "title": "Citizen Scientist Journey at Teen Escape",
    "displayDate": "Fri, May 8, 2020, 4:00 PM - Sun, May 10, 2020, 10:00 AM EST",
    "location": "Camp Runels",
    "color": "#C4D82E",
    "description": "Join us for a thrilling weekend at camp!",
    "start": "2020-05-08",
    "end": "2020-05-10",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/teen_escape1.html"
  },
  {
    "title": "Sundown Sing-along",
    "displayDate": "Fri, May 8, 2020, 6:00 PM - 7:30 PM EST",
    "location": "Camp Runels",
    "color": "#C4D82E",
    "description": "Bring your whole family to soak up the beauty of camp. ",
    "start": "2020-05-08",
    "end": "2020-05-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/sundown_sing_5_8.html"
  },
  {
    "title": "Crazy About Critters Camp-in",
    "displayDate": "Fri, May 8, 2020, 6:00 PM - Sat, May 9, 2020, 9:00 AM EST",
    "location": "Buttonwood Park Zoo",
    "color": "#AB218E",
    "description": "Spend the night at Buttonwood Park Zoo and learn what it takes to run a zoo.",
    "start": "2020-05-08",
    "end": "2020-05-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/critter_campin_5_8.html"
  },
  {
    "title": "Dive-in Movie",
    "displayDate": "Fri, May 8, 2020, 6:30 PM - 8:15 PM EST",
    "location": "Raynham Athletic Club",
    "color": "#C4D82E",
    "description": "Enjoy a movie on the big screen while swimming in a heated pool. ",
    "start": "2020-05-08",
    "end": "2020-05-08",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/dive_in_5_8.html"
  },
  {
    "title": "Salt Marsh Science",
    "displayDate": "Sat, May 9, 2020, 9:00 AM - 11:00 AM EST",
    "location": "Forest River Conservation Area",
    "color": "#AB218E",
    "description": "Explore this coastal habitat and learn about water and soil quality and what factors make the marsh able to support life.",
    "start": "2020-05-09",
    "end": "2020-05-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/salt_marsh.html"
  },
  {
    "title": "Sewing: Mini Backpack",
    "displayDate": "Sat, May 9, 2020, 9:00 AM - 11:30 AM EST",
    "location": "Sew Studio of Southborough",
    "color": "#68c8c6",
    "description": "Mini backpacks are cute and functional at the same time! Sew your own and decorate it to match your unique style. ",
    "start": "2020-05-09",
    "end": "2020-05-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/sew_studio_5_9.html"
  },
  {
    "title": "Caring for Stranded Ocean Animals: Brownie",
    "displayDate": "Sat, May 9, 2020, 9:00 AM - 12:00 PM EST",
    "location": "National Marine Life Center",
    "color": "#AB218E",
    "description": "Learn about marine animal rescue, rehabilitation, and release through hands-on activities and specimen exploration. ",
    "start": "2020-05-09",
    "end": "2020-05-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/caring_ocean_5_9.html"
  },
  {
    "title": "Home Alone Staying Safe",
    "displayDate": "Sat, May 9, 2020, 9:00 AM - 12:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#68C8C6",
    "description": "This dynamic and interactive session will prepare you to handle most home-alone situations, from an unexpected knock on the door to an emergency 911 call.",
    "start": "2020-05-09",
    "end": "2020-05-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/home_alone_5_9.html"
  },
  {
    "title": "MEDIC First Aid and CPR Recertification",
    "displayDate": "Sat, May 9, 2020, 9:00 AM - 12:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#F27536",
    "description": "This course is for those whose First Aid and CPR certifications are about to expire or have expired within 30 days of the listed session.",
    "start": "2020-05-09",
    "end": "2020-05-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/medic_recertification6.html"
  },
  {
    "title": "Outdoor Basics: Cooking",
    "displayDate": "Sat, May 9, 2020, 9:00 AM - 4:00 PM EST",
    "location": "Camp Wind-in-the-Pines (Day Side)",
    "color": "#BCBEC0",
    "description": "This training is required for troop camping trips with lodge accommodation and will prepare you to plan a girl-led lodge camping trip.",
    "start": "2020-05-09",
    "end": "2020-05-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/outdoor_basics_cook_5_8.html"
  },
  {
    "title": "Challenge Course: Low",
    "displayDate": "Sat, May 9, 2020, 10:00 AM - 12:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#C4D82E",
    "description": "Build your teamwork skills through action-oriented learning as you test your abilities on the low elements of the challenge course.",
    "start": "2020-05-09",
    "end": "2020-05-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/challenge_low_5_9.html"
  },
  {
    "title": "Archery",
    "displayDate": "Sat, May 9, 2020, 10:00 AM - 6:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#C4D82E",
    "description": "Learn the basics of bows and arrows, and how to aim for a bull’s-eye. 3 sessions.",
    "start": "2020-05-09",
    "end": "2020-05-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/archery_5_9.html"
  },
  {
    "title": "Dandelion Lotion Workshop",
    "displayDate": "Sat, May 9, 2020, 12:00 PM - 2:00 PM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#F9CD00",
    "description": "Make all-natural lotion with dandelion-infused oils in this wild herbal workshop. ",
    "start": "2020-05-09",
    "end": "2020-05-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/lotion_5_9.html"
  },
  {
    "title": "Glass Suncatcher",
    "displayDate": "Sat, May 9, 2020, 12:00 PM - 2:00 PM EST",
    "location": "North Shore Glass School",
    "color": "#F9CD00",
    "description": "Learn how to make stunning suncatchers to place in your window! ",
    "start": "2020-05-09",
    "end": "2020-05-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/glass_suncatcher_5_9.html"
  },
  {
    "title": "Me and My Bear Tea Party",
    "displayDate": "Sat, May 9, 2020, 1:00 PM - 3:30 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#F9CD00",
    "description": "Bring your favorite bear (or other stuffed animal) to a special Girl Scout tea party hosted by the Girl Scout Museum team.",
    "start": "2020-05-09",
    "end": "2020-05-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/tea_party_5_9.html"
  },
  {
    "title": "Caring for Stranded Ocean Animals: Junior",
    "displayDate": "Sat, May 9, 2020, 1:00 PM - 4:00 PM EST",
    "location": "National Marine Life Center",
    "color": "#AB218E",
    "description": "Learn about marine animal rescue, rehabilitation, and release through hands-on activities and specimen exploration. ",
    "start": "2020-05-09",
    "end": "2020-05-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/caring_ocean_5_91.html"
  },
  {
    "title": "Babysitter Badge",
    "displayDate": "Sat, May 9, 2020, 1:00 PM - 4:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#68C8C6",
    "description": " You’ll gain the skills and confidence for dealing with the challenges—from accidents to tantrums—of caring for children of all ages and stages.",
    "start": "2020-05-09",
    "end": "2020-05-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/babysitte_badge_5_9.html"
  },
  {
    "title": "MEDIC First Aid and CPR Blended Learning",
    "displayDate": "Sat, May 9, 2020, 1:00 PM - 5:00 PM EST",
    "location": "GSEMA Service Center - Waltham",
    "color": "#F27536",
    "description": "This course is offered in two parts: an online program and a hands-on skills session with an instructor. ",
    "start": "2020-05-09",
    "end": "2020-05-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/medic_blended11.html"
  },
  {
    "title": "Challenge Course: High",
    "displayDate": "Sat, May 9, 2020, 1:00 PM - 6:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#C4D82E",
    "description": "Build your confidence as a team by conquering the more challenging high elements of the course. 2 session.",
    "start": "2020-05-09",
    "end": "2020-05-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/challenge_high_5_9.html"
  },
  {
    "title": "Programming Robots Badge",
    "displayDate": "Sat, May 9, 2020, 1:15 PM - 2:45 PM EST",
    "location": "North Shore Sylvan Learning",
    "color": "#AB218E",
    "description": "Develop strong innovation skills as you work in pairs to build a robot using LEGO® bricks.",
    "start": "2020-05-09",
    "end": "2020-05-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/programming_robots_5_9.html"
  },
  {
    "title": "Wild Herbal First Aid Soap",
    "displayDate": "Sat, May 9, 2020, 3:00 PM - 5:00 PM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#F9CD00",
    "description": "Make soap from wild herbs known for soothing poison ivy, cuts, eczema, burns, and bug bites.",
    "start": "2020-05-09",
    "end": "2020-05-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/soap_5_9.html"
  },
  {
    "title": "Glass Fusing",
    "displayDate": "Sat, May 9, 2020, 3:30 PM - 4:30 PM EST",
    "location": "Look What I Made (N. Reading)",
    "color": "#f9cd00",
    "description": "Learn to make your own fused glass masterpiece! ",
    "start": "2020-05-09",
    "end": "2020-05-09",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/glass_fusing_5_9.html"
  },
  {
    "title": "Outdoor Basics: Camping",
    "displayDate": "Sat, May 9, 2020, 3:30 PM - Sun, May 10, 2020, 10:00 AM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#BCBEC0",
    "description": "This training is required for troop camping trips with tent accommodation and will prepare you to plan a girl-led camping trip in either lodges or tents",
    "start": "2020-05-09",
    "end": "2020-05-10",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/outdoor_basics_5_8.html"
  },
  {
    "title": "Programming Robots Badge: Brownie",
    "displayDate": "Sun, May 10, 2020, 3:30 PM - 5:30 PM EST",
    "location": "Empow Studios - Newton",
    "color": "#AB218E",
    "description": "Earn the Programming Robots badge while using LEGO® EV3 MINDSTORMS.",
    "start": "2020-05-10",
    "end": "2020-05-10",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/programming_robots_5_10.html"
  },
  {
    "title": "Designing Robots Badge: Brownie",
    "displayDate": "Sun, May 10, 2020, 3:30 PM - 5:30 PM EST",
    "location": "Empow Studios (Lexington)",
    "color": "#AB218E",
    "description": "Learn how some robots imitate nature and build your own walking robot using LEGO® MINDSTORMS® EV3.",
    "start": "2020-05-10",
    "end": "2020-05-10",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/designing_robotsl_5_10.html"
  },
  {
    "title": "Pottery Painting",
    "displayDate": "Sun, May 10, 2020, 4:00 PM - 5:00 PM EST",
    "location": "Art Signals (Maynard)",
    "color": "#f9cd00",
    "description": "You will get to pick from a variety of pottery pieces to paint. Everything will be glazed and fired in our kiln and ready to pick up in one week. ",
    "start": "2020-05-10",
    "end": "2020-05-10",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/pottery_painting_5_10.html"
  },
  {
    "title": "Silver Award Orientation",
    "displayDate": "Tue, May 12, 2020, 6:00 PM - 8:00 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#F27536",
    "description": "This free two-hour orientation prepares Cadettes to brainstorm, develop and implement a successful Silver Award project, and teaches adults how best to support them.",
    "start": "2020-05-12",
    "end": "2020-05-12",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/silver-award-orientation2.html"
  },
  {
    "title": "Encampment Director Training",
    "displayDate": "Thu, May 14, 2020, 6:30 PM - 8:30 PM EST",
    "location": "Online",
    "color": "#BCBEC0",
    "description": "This training is required for coordinators of service unit or large group camp experiences. ",
    "start": "2020-05-14",
    "end": "2020-05-14",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/encampment11.html"
  },
  {
    "title": "Troop Camping Weekend: Tents",
    "displayDate": "Fri, May 15, 2020, 5:00 PM - Sun, May 17, 2020, 11:00 AM EST",
    "location": "Camp Cedar Hill",
    "color": "#C4D82E",
    "description": "Experience the fun of camping with your troop!",
    "start": "2020-05-15",
    "end": "2020-05-17",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/troop_camping_weekend.html"
  },
  {
    "title": "Science for Wizards Overnight",
    "displayDate": "Fri, May 15, 2020, 5:30 PM - Sat, May 16, 2020, 8:30 AM EST",
    "location": "EcoTarium",
    "color": "#AB218E",
    "description": "Consider yourself enrolled at EcoTarium: School of Science-craft and Magical Inquiry!",
    "start": "2020-05-15",
    "end": "2020-05-16",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/science_wizards_5_29.html"
  },
  {
    "title": "New York City Adventure",
    "displayDate": "Sat, May 16, 2020, 6:00 AM - 11:00 PM EST",
    "location": "West Bridgewater to NYC",
    "color": "#F9CD00",
    "description": "Meet author Jen Malone on a day trip to New York City!",
    "start": "2020-05-16",
    "end": "2020-05-16",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/nyc_adventure.html"
  },
  {
    "title": "MEDIC First Aid and CPR Recertification",
    "displayDate": "Sat, May 16, 2020, 9:00 AM - 12:00 PM EST",
    "location": "GSEMA Service Center - Middleboro",
    "color": "#F27536",
    "description": "This course is for those whose First Aid and CPR certifications are about to expire or have expired within 30 days of the listed session.",
    "start": "2020-05-16",
    "end": "2020-05-16",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/medic_recertification10.html"
  },
  {
    "title": "MEDIC First Aid and CPR Recertification",
    "displayDate": "Sat, May 16, 2020, 9:00 AM - 12:00 PM EST",
    "location": "GSEMA Service Center - Middleboro",
    "color": "#F27536",
    "description": "This course is for those whose First Aid and CPR certifications are about to expire or have expired within 30 days of the listed session.",
    "start": "2020-05-16",
    "end": "2020-05-16",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/medic_recertification9.html"
  },
  {
    "title": "Engineering Design Challenge: Marine Animal Prosthetics",
    "displayDate": "Sat, May 16, 2020, 9:00 AM - 12:30 PM EST",
    "location": "National Marine Wildlife Center",
    "color": "#ab218e",
    "description": "Discover how technology is being used to help disabled animals, and then complete an engineering challenge to design your own prosthetic device for a marine animal.",
    "start": "2020-05-16",
    "end": "2020-05-16",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/engineering_5_16.html"
  },
  {
    "title": "Playing the Past Badge",
    "displayDate": "Sat, May 16, 2020, 9:30 AM - 12:30 PM EST",
    "location": "Centerville Historical Museum",
    "color": "#F9CD00",
    "description": "What was life like for a girl in a Cape Cod seafaring town in the 1800s? ",
    "start": "2020-05-16",
    "end": "2020-05-16",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/past_5_16.html"
  },
  {
    "title": "Glass Plate",
    "displayDate": "Sat, May 16, 2020, 10:00 AM - 12:00 PM EST",
    "location": "The Glass Bar",
    "color": "#F9CD00",
    "description": "Develop your own designs as you learn the basics of cutting glass and fusing it in the kiln. ",
    "start": "2020-05-16",
    "end": "2020-05-16",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/glass_plate_5_16.html"
  },
  {
    "title": "Animal Meet and Greet",
    "displayDate": "Sat, May 16, 2020, 10:00 AM - 12:00 PM EST",
    "location": "Soule Homestead",
    "color": "#F9CD00",
    "description": "Meet farm animals and learn about their many roles and far-reaching impact.",
    "start": "2020-05-16",
    "end": "2020-05-16",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/animal_meet_5_16.html"
  },
  {
    "title": "Chocolatier for the Day",
    "displayDate": "Sat, May 16, 2020, 10:00 AM - 1:30 PM EST",
    "location": "Chocolate Therapy",
    "color": "#F9CD00",
    "description": "Sample different types of chocolate while learning about its benefits, its history and how it’s made. 2 sessions.",
    "start": "2020-05-16",
    "end": "2020-05-16",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/chocolatier_5_16.html"
  },
  {
    "title": "Intro to Flameworking",
    "displayDate": "Sat, May 16, 2020, 12:00 PM - 2:00 PM EST",
    "location": "North Shore Glass School",
    "color": "#F9CD00",
    "description": "Try your hand at the ancient art of flameworking using borosilicate glass! ",
    "start": "2020-05-16",
    "end": "2020-05-16",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/flameworking_5_16.html"
  },
  {
    "title": "Playing the Past Badge",
    "displayDate": "Sat, May 16, 2020, 1:00 PM - 3:00 PM EST",
    "location": "GSEMA Girl Scout Museum",
    "color": "#F9CD00",
    "description": "Join the Girl Scout Museum team to explore the past of Girl Scouts of Eastern Massachusetts as you earn your Playing the Past badge.",
    "start": "2020-05-16",
    "end": "2020-05-16",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/past_museum_5_16.html"
  },
  {
    "title": "Advanced Chocolate Workshop",
    "displayDate": "Sat, May 16, 2020, 2:00 PM - 3:30 PM EST",
    "location": "Chocolate Therapy",
    "color": "#68C8C6",
    "description": "Girls will learn about the history of chocolate, where it comes from, how it's grown, harvested and gets turned into the sweet confections we love to consume. ",
    "start": "2020-05-16",
    "end": "2020-05-16",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/advanced_chocolate_5_16.html"
  },
  {
    "title": "Glass Fusing",
    "displayDate": "Sat, May 16, 2020, 4:30 PM - 5:30 PM EST",
    "location": "Art Signals (Maynard)",
    "color": "#f9cd00",
    "description": "Learn to make your own fused glass masterpiece! ",
    "start": "2020-05-16",
    "end": "2020-05-16",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/glass_fusing_5_16.html"
  },
  {
    "title": "Museum of Science Overnight",
    "displayDate": "Sat, May 16, 2020, 5:00 PM - Sun, May 17, 2020, 11:00 AM EST",
    "location": "Museum of Science",
    "color": "#ab218e",
    "description": "Spend a night at the museum!",
    "start": "2020-05-16",
    "end": "2020-05-17",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/mos_overnight_5_16.html"
  },
  {
    "title": "Challenge Course: Low",
    "displayDate": "Sun, May 17, 2020, 10:00 AM - 3:00 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#C4D82E",
    "description": "Build your teamwork skills through action-oriented learning as you test your abilities on the low elements of the challenge course. 2 sessions.",
    "start": "2020-05-17",
    "end": "2020-05-17",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/challenge_low_5_17.html"
  },
  {
    "title": "Archery",
    "displayDate": "Sun, May 17, 2020, 10:00 AM - 6:00 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#C4D82E",
    "description": "Learn the basics of bows and arrows, and how to aim for a bull’s-eye. 3 sessions.",
    "start": "2020-05-17",
    "end": "2020-05-17",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/archery_5_17.html"
  },
  {
    "title": "Boston North End History and Pizza Tour",
    "displayDate": "Sun, May 17, 2020, 11:00 AM - 1:00 PM EST",
    "location": "Rose Kennedy Greenway",
    "color": "#F9CD00",
    "description": "Discover historic Boston slice by delicious slice!",
    "start": "2020-05-17",
    "end": "2020-05-17",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/pizza_tour_5_17.html"
  },
  {
    "title": "Mass Escape Room",
    "displayDate": "Sun, May 17, 2020, 11:00 AM - 1:00 PM EST",
    "location": "Mass Escape Room",
    "color": "#F9CD00",
    "description": "Unleash your inner go-getter at this hands-on mental adventure game.",
    "start": "2020-05-17",
    "end": "2020-05-17",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/mass_escape_5_17.html"
  },
  {
    "title": "Pottery Making",
    "displayDate": "Sun, May 17, 2020, 12:00 PM - 3:00 PM EST",
    "location": "New Bedford Art Museum/Art Works!",
    "color": "#F9CD00",
    "description": "Get your hands messy in the clay studio as you learn about different types of pots and their uses. ",
    "start": "2020-05-17",
    "end": "2020-05-17",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/art_works_5_17.html"
  },
  {
    "title": "Potter Badge",
    "displayDate": "Sun, May 17, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Fuller Craft Museum",
    "color": "#f9cd00",
    "description": "Learn about the different materials, techniques and skills to make pottery and then create your own masterpiece.",
    "start": "2020-05-17",
    "end": "2020-05-17",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/potter_5_17.html"
  },
  {
    "title": "Powerhouse Program with Fashion Focus",
    "displayDate": "Sun, May 17, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#F9CD00",
    "description": "To build inner strength and confidence in children and teens with the hope that no child feels powerless in any situation.",
    "start": "2020-05-17",
    "end": "2020-05-17",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/powerhouse.html"
  },
  {
    "title": "Fairytales and STEM",
    "displayDate": "Sun, May 17, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#AB218E",
    "description": "Girls will practice creative problem solving and perseverance to solve challenges presented in classic and modern fairy tales.",
    "start": "2020-05-17",
    "end": "2020-05-17",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/fairytales_stem.html"
  },
  {
    "title": "Bugs Badge",
    "displayDate": "Sun, May 17, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Buttonwood Park Zoo",
    "color": "#AB218E",
    "description": "Explore various habitats throughout the zoo and compare wild and domestic animals.",
    "start": "2020-05-17",
    "end": "2020-05-17",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/bugs_badge_5_17.html"
  },
  {
    "title": "Horseback Riding",
    "displayDate": "Sun, May 17, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Rocking Horse Farms",
    "color": "#F9CD00",
    "description": "Learn about the basic care of horses, functions of your tack, how to control a horse and then practice riding.",
    "start": "2020-05-17",
    "end": "2020-05-17",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/horseback_riding11.html"
  },
  {
    "title": "Fairytales and STEM",
    "displayDate": "Sun, May 17, 2020, 1:00 PM - 4:00 PM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#AB218E",
    "description": "Girls will practice creative problem solving and perseverance to solve challenges presented in classic and modern fairy tales.",
    "start": "2020-05-17",
    "end": "2020-05-17",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/fairytales_stem1.html"
  },
  {
    "title": "Rocky Shore Tidepooling: Daisy/Brownie",
    "displayDate": "Sun, May 17, 2020, 1:00 PM - 4:00 PM EST",
    "location": "NEU Marine Science Center (Nahant)",
    "color": "#AB218E",
    "description": "Earn the Oceanography patch as you explore the rocky shore.",
    "start": "2020-05-17",
    "end": "2020-05-17",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/rockyshore_5_17.html"
  },
  {
    "title": "Eco Friend Badge with Mass Audubon",
    "displayDate": "Sun, May 17, 2020, 2:00 PM - 4:00 PM EST",
    "location": "Mass Audubon Tidmarsh Wildlife Sanctuary ",
    "color": "#C4D82E",
    "description": "Girls will go on a guided hike, learn how to safely explore the outdoors, and work together to earn their Eco-Friend badge! ",
    "start": "2020-05-17",
    "end": "2020-05-17",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/eco_friend.html"
  },
  {
    "title": "Horse Sampler",
    "displayDate": "Sun, May 17, 2020, 3:00 PM - 5:00 PM EST",
    "location": "Lil' Folk Farm",
    "color": "#F9CD00",
    "description": "Learn about horse grooming, care and feeding, and how to lead, mount, and steer a horse in this introduction to horseback riding.",
    "start": "2020-05-17",
    "end": "2020-05-17",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/horse_sampler1.html"
  },
  {
    "title": "Pottery Painting",
    "displayDate": "Sun, May 17, 2020, 3:30 PM - 4:30 PM EST",
    "location": "Look What I Made (N. Reading)",
    "color": "#f9cd00",
    "description": "You will get to pick from a variety of pottery pieces to paint. Everything will be glazed and fired in our kiln and ready to pick up in one week. ",
    "start": "2020-05-17",
    "end": "2020-05-17",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/pottery_painting_5_17.html"
  },
  {
    "title": "Designing Robots Badge: Cadette",
    "displayDate": "Sun, May 17, 2020, 3:30 PM - 5:30 PM EST",
    "location": "Empow Studios (Lexington)",
    "color": "#AB218E",
    "description": " Discuss how robots sense, think, and act to help us, then design and build a prototype of your own problem-solving robot.",
    "start": "2020-05-17",
    "end": "2020-05-17",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/designing_robots_cad_5_17.html"
  },
  {
    "title": "Designing Robots Badge: Senior/Ambassador",
    "displayDate": "Sun, May 17, 2020, 3:30 PM - 5:30 PM EST",
    "location": "Empow Studios - Newton",
    "color": "#AB218E",
    "description": "Discuss how robots sense, think, and act to help us, then design and build a prototype of your own problem-solving robot.",
    "start": "2020-05-17",
    "end": "2020-05-17",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/designing_robots_sa_5_17.html"
  },
  {
    "title": "Potter Badge",
    "displayDate": "Sun, May 17, 2020, 4:00 PM - 5:30 PM EST",
    "location": "Art Signals (Maynard)",
    "color": "#f9cd00",
    "description": "Come have some fun getting your hands dirty and earn your Potter Badge!",
    "start": "2020-05-17",
    "end": "2020-05-17",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/potter_badge_5_17.html"
  },
  {
    "title": "Challenge Course: High",
    "displayDate": "Sun, May 17, 2020, 4:00 PM - 6:00 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#C4D82E",
    "description": "Build your confidence as a team by conquering the more challenging high elements of the course.",
    "start": "2020-05-17",
    "end": "2020-05-17",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/challenge_high_5_17.html"
  },
  {
    "title": "Deadline for Gold Award Project Proposals and Final Reports",
    "displayDate": "Mon, May 18, 2020, 5:00 PM EST",
    "location": "Deadline",
    "color": "#eb0789",
    "description": "GSEMA uses deadlines to collect both Gold Award project proposals and Gold Award final reports. In order to be considered for an upcoming in-person interview or final presentation, you must submit by the deadline before.",
    "start": "2020-05-18",
    "end": "2020-05-18",
    "path": "/content/girlscoutseasternmass/en/events-repository/2019/goldaward-deadline41.html"
  },
  {
    "title": "First Contact: Amateur Radio",
    "displayDate": "Tue, May 19, 2020, 6:00 PM - 8:00 PM EST",
    "location": "New England Sci-Tech",
    "color": "#AB218E",
    "description": "Get on the radio waves and talk to people all over the world! ",
    "start": "2020-05-19",
    "end": "2020-05-19",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/first_contact_5_19.html"
  },
  {
    "title": "Black Light Painting",
    "displayDate": "Fri, May 22, 2020, 4:30 PM - 6:00 PM EST",
    "location": "Art Signals (Maynard)",
    "color": "#f9cd00",
    "description": "Enjoy a unique painting experience as you paint in the dark under black light with special fluorescent acrylic paint. ",
    "start": "2020-05-22",
    "end": "2020-05-22",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/black_light_5_22.html"
  },
  {
    "title": "Detective and Spies Overnight",
    "displayDate": "Fri, May 22, 2020, 5:30 PM - Sat, May 23, 2020, 8:30 AM EST",
    "location": "EcoTarium",
    "color": "#AB218E",
    "description": "Embrace an evening full of intrigue and investigation, then finish the night with a show in the planetarium! ",
    "start": "2020-05-22",
    "end": "2020-05-23",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/detective_and_spies_5_22.html"
  },
  {
    "title": "Glass Sandblasting Class",
    "displayDate": "Sat, May 23, 2020, 12:00 PM - 2:00 PM EST",
    "location": "North Shore Glass School",
    "color": "#F9CD00",
    "description": "You will learn how to safely use a sandblaster to let your design shine. ",
    "start": "2020-05-23",
    "end": "2020-05-23",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/sandblasting_5_23.html"
  },
  {
    "title": "Outdoor Art Apprentice Badge",
    "displayDate": "Sun, May 24, 2020, 12:00 PM - 3:00 PM EST",
    "location": "New Bedford Art Museum/Art Works!",
    "color": "#F9CD00",
    "description": "Make your own eco-friendly art supplies such as brushes, glue, paints, and mini sketchbooks!",
    "start": "2020-05-24",
    "end": "2020-05-24",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/art_works_5_24.html"
  },
  {
    "title": "Spring Resident Camp for Troops",
    "displayDate": "Fri, May 29, 2020, 5:00 PM - Sun, May 31, 2020, 10:00 AM EST",
    "location": "Camp Runels",
    "color": "#C4D82E",
    "description": "Join us with your troop to get a taste of resident camp adventures! ",
    "start": "2020-05-29",
    "end": "2020-05-31",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/troop_camping_tents.html"
  },
  {
    "title": "Rocky Shore Tidepooling: Junior/Cadette",
    "displayDate": "Sat, May 30, 2020, 9:00 AM - 12:00 PM EST",
    "location": "NEU Marine Science Center (Nahant)",
    "color": "#AB218E",
    "description": "Earn the Oceanography patch as you explore the rocky shore.",
    "start": "2020-05-30",
    "end": "2020-05-30",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/rockyshore_5_30.html"
  },
  {
    "title": "Whale Watch",
    "displayDate": "Sat, May 30, 2020, 1:00 PM - 5:30 PM EST",
    "location": "Gloucester",
    "color": "#F9CD00",
    "description": "Learn about whales, then set sail to spot them in their natural habitat!",
    "start": "2020-05-30",
    "end": "2020-05-30",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/whale_watch.html"
  },
  {
    "title": "Museum of Science Overnight",
    "displayDate": "Sat, May 30, 2020, 5:00 PM - Sun, May 31, 2020, 11:00 AM EST",
    "location": "Museum of Science",
    "color": "#ab218e",
    "description": "Spend a night at the museum!",
    "start": "2020-05-30",
    "end": "2020-05-31",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/mos_overnight_5_30.html"
  },
  {
    "title": "Snorin' Roarin' Overnight",
    "displayDate": "Sat, May 30, 2020, 7:00 PM - Sun, May 31, 2020, 9:00 AM EST",
    "location": "Franklin Park Zoo",
    "color": "#AB218E",
    "description": "See your favorite zoo creatures in a whole new light—the dark!",
    "start": "2020-05-30",
    "end": "2020-05-31",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/snorin_roarin_5_30.html"
  },
  {
    "title": "Archery",
    "displayDate": "Sun, May 31, 2020, 10:00 AM - 3:00 PM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#C4D82E",
    "description": "Learn the basics of bows and arrows, and how to aim for a bull’s-eye. 2 sessions.",
    "start": "2020-05-31",
    "end": "2020-05-31",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/archery_5_31.html"
  },
  {
    "title": "Challenge Course: High",
    "displayDate": "Sun, May 31, 2020, 10:00 AM - 6:00 PM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#C4D82E",
    "description": "Build your confidence as a team by conquering the more challenging high elements of the course. 2 sessions.",
    "start": "2020-05-31",
    "end": "2020-05-31",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/challenge_high_5_31.html"
  },
  {
    "title": "Playing the Past Badge",
    "displayDate": "Sun, May 31, 2020, 11:00 AM - 2:00 PM EST",
    "location": "Lexington Historical Society",
    "color": "#F9CD00",
    "description": "Experience life as a Lexington resident in 1775!",
    "start": "2020-05-31",
    "end": "2020-05-31",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/past_5_31.html"
  },
  {
    "title": "Challenge Course: Low",
    "displayDate": "Sun, May 31, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#C4D82E",
    "description": "Build your teamwork skills through action-oriented learning as you test your abilities on the low elements of the challenge course.",
    "start": "2020-05-31",
    "end": "2020-05-31",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/challenge_low_5_31.html"
  },
  {
    "title": "Be an Empathy Superhero",
    "displayDate": "Sun, May 31, 2020, 1:00 PM - 4:30 PM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#68c8c6",
    "description": "Through giggles, games, and giving, you’ll exercise your “empathy muscle” and experience first-hand how you can make a meaningful difference. 2 sessions.",
    "start": "2020-05-31",
    "end": "2020-05-31",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/empathy_hero_5_31.html"
  },
  {
    "title": "Computer Expert Badge",
    "displayDate": "Sun, May 31, 2020, 3:30 PM - 5:30 PM EST",
    "location": "Empow Studios - Lexington",
    "color": "#AB218E",
    "description": "Learn about different computer programs and use the internet to plan your own virtual trip.",
    "start": "2020-05-31",
    "end": "2020-05-31",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/computer_expert_5_31.html"
  },
  {
    "title": "Designing Robots Badge: Cadette",
    "displayDate": "Sun, May 31, 2020, 3:30 PM - 5:30 PM EST",
    "location": "Empow Studios (Newton)",
    "color": "#AB218E",
    "description": " Discuss how robots sense, think, and act to help us, then design and build a prototype of your own problem-solving robot.",
    "start": "2020-05-31",
    "end": "2020-05-31",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/designing_robots_cad_5_31.html"
  },
  {
    "title": "Kayaking",
    "displayDate": "Sun, May 31, 2020, 4:00 PM - 6:00 PM EST",
    "location": "Camp Wind-in-the-Pines",
    "color": "#C4D82E",
    "description": "Be a go-getter on the water!",
    "start": "2020-05-31",
    "end": "2020-05-31",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/kayaking.html"
  },
  {
    "title": "Museum of Science Overnight",
    "displayDate": "Fri, Jun 5, 2020, 5:00 PM - Sat, Jun 6, 2020, 11:00 AM EST",
    "location": "Museum of Science",
    "color": "#ab218e",
    "description": "Spend a night at the museum!",
    "start": "2020-06-05",
    "end": "2020-06-06",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/mos_overnight_6_5.html"
  },
  {
    "title": "MEDIC First Aid and CPR Recertification",
    "displayDate": "Sat, Jun 6, 2020, 9:00 AM - 12:00 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#F27536",
    "description": "This course is for those whose First Aid and CPR certifications are about to expire or have expired within 30 days of the listed session.",
    "start": "2020-06-06",
    "end": "2020-06-06",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/medic_recertification5.html"
  },
  {
    "title": "Eco Friend and Eco Learner Badges",
    "displayDate": "Sat, Jun 6, 2020, 10:00 AM - 12:00 PM EST",
    "location": "Buttonwood Park Zoo",
    "color": "#AB218E",
    "description": "Learn what it takes to protect the environment as you explore nature. ",
    "start": "2020-06-06",
    "end": "2020-06-06",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/eco_6_6.html"
  },
  {
    "title": "Gals' Night Out: Platform Tents",
    "displayDate": "Sat, Jun 6, 2020, 10:00 AM EST",
    "location": "Camp Favorite",
    "color": "#C4D82E",
    "description": "Spend a night at camp with your mom or another special female adult!",
    "start": "2020-06-06",
    "end": "2020-06-07",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/gals_6_6.html"
  },
  {
    "title": "Boston North End History and Pizza Tour",
    "displayDate": "Sat, Jun 6, 2020, 11:00 AM - 1:00 PM EST",
    "location": "Rose Kennedy Greenway",
    "color": "#F9CD00",
    "description": "Discover historic Boston slice by delicious slice!",
    "start": "2020-06-06",
    "end": "2020-06-06",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/pizza_tour_6_6.html"
  },
  {
    "title": "Amazing Race: Salem",
    "displayDate": "Sat, Jun 6, 2020, 1:00 PM - 4:00 PM EST",
    "location": "Salem",
    "color": "#C4D82E",
    "description": "The race is on—this is the urban adventure of the year! ",
    "start": "2020-06-06",
    "end": "2020-06-06",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/amazing_race_salem.html"
  },
  {
    "title": "MEDIC First Aid and CPR Blended Learning",
    "displayDate": "Sat, Jun 6, 2020, 1:00 PM - 5:00 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#F27536",
    "description": "This course is offered in two parts: an online program and a hands-on skills session with an instructor. ",
    "start": "2020-06-06",
    "end": "2020-06-06",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/medic_blended3.html"
  },
  {
    "title": "Stars and Space Overnight",
    "displayDate": "Sat, Jun 6, 2020, 5:30 PM - Sun, Jun 7, 2020, 8:30 AM EST",
    "location": "EcoTarium",
    "color": "#AB218E",
    "description": "Take a tour through the solar system, land a probe on Mars, and observe the night sky through telescopes (weather permitting) during this exciting night at the museum! ",
    "start": "2020-06-06",
    "end": "2020-06-07",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/stars_space_6_6.html"
  },
  {
    "title": "Snorin' Roarin' Overnight",
    "displayDate": "Sat, Jun 6, 2020, 7:00 PM - Sun, Jun 7, 2020, 9:00 AM EST",
    "location": "Franklin Park Zoo",
    "color": "#AB218E",
    "description": "See your favorite zoo creatures in a whole new light—the dark!",
    "start": "2020-06-06",
    "end": "2020-06-07",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/snorin_roarin_6_6.html"
  },
  {
    "title": "Outdoor Art Creator Badge",
    "displayDate": "Sun, Jun 7, 2020, 1:00 PM - 3:00 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#F9CD00",
    "description": "Make your own eco-friendly art supplies such as brushes, glue, paints, and mini sketchbooks!",
    "start": "2020-06-07",
    "end": "2020-06-07",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/art_explorer.html"
  },
  {
    "title": "Go for Gold: Writing a Gold Award Proposal",
    "displayDate": "Sun, Jun 7, 2020, 2:00 PM - 4:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#eb0789",
    "description": "Got an idea for a Gold Award Project? Come meet with members of the GSEMA Gold Award Committee for support!",
    "start": "2020-06-07",
    "end": "2020-06-07",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/gold_workshop_6_7.html"
  },
  {
    "title": "Outdoor Art Explorer Badge",
    "displayDate": "Sun, Jun 7, 2020, 3:30 PM - 5:30 PM EST",
    "location": "Camp Maude Eaton",
    "color": "#F9CD00",
    "description": "Make your own eco-friendly art supplies such as brushes, glue, paints, and mini sketchbooks!",
    "start": "2020-06-07",
    "end": "2020-06-07",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/art_explorer1.html"
  },
  {
    "title": "Museum of Science Overnight",
    "displayDate": "Fri, Jun 12, 2020, 5:00 PM - Sat, Jun 13, 2020, 11:00 AM EST",
    "location": "Museum of Science",
    "color": "#ab218e",
    "description": "Spend a night at the museum!",
    "start": "2020-06-12",
    "end": "2020-06-13",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/mos_overnight_6_12.html"
  },
  {
    "title": "Detective and Spies Overnight",
    "displayDate": "Fri, Jun 12, 2020, 5:30 PM - Sat, Jun 13, 2020, 8:30 AM EST",
    "location": "EcoTarium",
    "color": "#AB218E",
    "description": "Embrace an evening full of intrigue and investigation, then finish the night with a show in the planetarium! ",
    "start": "2020-06-12",
    "end": "2020-06-13",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/detective_and_spies_6_12.html"
  },
  {
    "title": "Stars and Space Overnight",
    "displayDate": "Sat, Jun 13, 2020, 5:30 PM - Sun, Jun 14, 2020, 8:30 AM EST",
    "location": "EcoTarium",
    "color": "#AB218E",
    "description": "Take a tour through the solar system, land a probe on Mars, and observe the night sky through telescopes (weather permitting) during this exciting night at the museum! ",
    "start": "2020-06-13",
    "end": "2020-06-14",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/stars_space_6_13.html"
  },
  {
    "title": "Rocky Shore Tidepooling: Daisy/Brownie",
    "displayDate": "Sun, Jun 14, 2020, 1:00 PM - 4:00 PM EST",
    "location": "NEU Marine Science Center (Nahant)",
    "color": "#AB218E",
    "description": "Earn the Oceanography patch as you explore the rocky shore.",
    "start": "2020-06-14",
    "end": "2020-06-14",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/rockyshore_6_14.html"
  },
  {
    "title": "June Gold Award Interviews and Final Presentation",
    "displayDate": "Thu, Jun 18, 2020, 12:00 AM EST",
    "location": "GSEMA Service Centers",
    "color": "#eb0789",
    "description": "Each Girl Scout is asked to participate in an interview to discuss their project plan for approval and to make a final presentation after their final report is submitted.",
    "start": "2020-06-18",
    "end": "2020-06-27",
    "path": "/content/girlscoutseasternmass/en/events-repository/2019/goldaward-interview5.html"
  },
  {
    "title": "Family Camping: Mom and Me",
    "displayDate": "Fri, Jun 19, 2020, 2:00 PM EST",
    "location": "Camp Menotomy, Meredith, NH",
    "color": "#C4D82E",
    "description": "Bring your mom (or other adult female role model) to camp for a weekend all about the girls.",
    "start": "2020-06-19",
    "end": "2020-06-21",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/mom_and_me_6_19.html"
  },
  {
    "title": "Salt Marsh Science",
    "displayDate": "Sat, Jun 20, 2020, 9:00 AM - 11:00 AM EST",
    "location": "Forest River Conservation Area",
    "color": "#AB218E",
    "description": "Explore this coastal habitat and learn about water and soil quality and what factors make the marsh able to support life.",
    "start": "2020-06-20",
    "end": "2020-06-20",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/salt_marsh1.html"
  },
  {
    "title": "MEDIC First Aid and CPR Recertification",
    "displayDate": "Sat, Jun 20, 2020, 9:00 AM - 12:00 PM EST",
    "location": "Camp Cedar Hill",
    "color": "#F27536",
    "description": "This course is for those whose First Aid and CPR certifications are about to expire or have expired within 30 days of the listed session.",
    "start": "2020-06-20",
    "end": "2020-06-20",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/medic_recertification7.html"
  },
  {
    "title": "MEDIC First Aid and CPR Blended Learning",
    "displayDate": "Sat, Jun 20, 2020, 1:00 PM - 5:00 PM EST",
    "location": "GSEMA Service Center - Waltham",
    "color": "#F27536",
    "description": "This course is offered in two parts: an online program and a hands-on skills session with an instructor. ",
    "start": "2020-06-20",
    "end": "2020-06-20",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/medic_blended13.html"
  },
  {
    "title": "Family Camping: Fun for the Whole Family",
    "displayDate": "Fri, Jun 26, 2020, 2:00 PM EST",
    "location": "Camp Menotomy, Meredith, NH",
    "color": "#C4D82E",
    "description": " Try boating, swimming, archery, hiking, campfires— share the fun of summer camp with your entire family!",
    "start": "2020-06-26",
    "end": "2020-06-28",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/fun_family_6_26.html"
  },
  {
    "title": "Family Camping: Fun for the Whole Family",
    "displayDate": "Fri, Jul 3, 2020, 2:00 PM EST",
    "location": "Camp Menotomy, Meredith, NH",
    "color": "#C4D82E",
    "description": " Try boating, swimming, archery, hiking, campfires— share the fun of summer camp with your entire family!",
    "start": "2020-07-03",
    "end": "2020-07-05",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/fun_family_7_3.html"
  },
  {
    "title": "Family Camping: Mom and Me",
    "displayDate": "Fri, Jul 10, 2020, 2:00 PM EST",
    "location": "Camp Menotomy, Meredith, NH",
    "color": "#C4D82E",
    "description": "Bring your mom (or other adult female role model) to camp for a weekend all about the girls.",
    "start": "2020-07-10",
    "end": "2020-07-12",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/mom_and_me_7_10.html"
  },
  {
    "title": "MEDIC First Aid and CPR Recertification",
    "displayDate": "Sat, Jul 11, 2020, 9:00 AM - 12:00 PM EST",
    "location": "GSEMA Service Center - Middleboro",
    "color": "#F27536",
    "description": "This course is for those whose First Aid and CPR certifications are about to expire or have expired within 30 days of the listed session.",
    "start": "2020-07-11",
    "end": "2020-07-11",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/medic_recertification11.html"
  },
  {
    "title": "MEDIC First Aid and CPR Blended Learning",
    "displayDate": "Sat, Jul 11, 2020, 1:00 PM - 5:00 PM EST",
    "location": "GSEMA Service Center - Middleboro",
    "color": "#F27536",
    "description": "This course is offered in two parts: an online program and a hands-on skills session with an instructor. ",
    "start": "2020-07-11",
    "end": "2020-07-11",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/medic_blended7.html"
  },
  {
    "title": "Troop/Group Camping Weekend",
    "displayDate": "Fri, Jul 17, 2020, 2:00 PM EST",
    "location": "Camp Menotomy, Meredith, NH",
    "color": "#C4D82E",
    "description": "If you’re eager to try overnight camping, but not sure you’re ready to do it on your own, Camp Menotomy is the perfect place to start. ",
    "start": "2020-07-17",
    "end": "2020-07-19",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/troop_group_camping.html"
  },
  {
    "title": "MEDIC First Aid and CPR Blended Learning",
    "displayDate": "Wed, Jul 22, 2020, 10:00 AM - 2:00 PM EST",
    "location": "GSEMA Service Center - Waltham",
    "color": "#F27536",
    "description": "This course is offered in two parts: an online program and a hands-on skills session with an instructor. ",
    "start": "2020-07-22",
    "end": "2020-07-22",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/medic_blended12.html"
  },
  {
    "title": "Family Camping: Fun for the Whole Family",
    "displayDate": "Fri, Jul 24, 2020, 2:00 PM EST",
    "location": "Camp Menotomy, Meredith, NH",
    "color": "#C4D82E",
    "description": " Try boating, swimming, archery, hiking, campfires— share the fun of summer camp with your entire family!",
    "start": "2020-07-24",
    "end": "2020-07-26",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/fun_family_7_24.html"
  },
  {
    "title": "MEDIC First Aid and CPR Blended Learning",
    "displayDate": "Sat, Aug 1, 2020, 1:00 PM - 5:00 PM EST",
    "location": "GSEMA Service Center - Waltham",
    "color": "#F27536",
    "description": "This course is offered in two parts: an online program and a hands-on skills session with an instructor. ",
    "start": "2020-08-01",
    "end": "2020-08-01",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/medic_blended14.html"
  },
  {
    "title": "MEDIC First Aid and CPR Blended Learning",
    "displayDate": "Sat, Aug 15, 2020, 1:00 PM - 5:00 PM EST",
    "location": "GSEMA Service Center - Middleboro",
    "color": "#F27536",
    "description": "This course is offered in two parts: an online program and a hands-on skills session with an instructor. ",
    "start": "2020-08-15",
    "end": "2020-08-15",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/medic_blended8.html"
  },
  {
    "title": "MEDIC First Aid and CPR Blended Learning",
    "displayDate": "Wed, Sep 16, 2020, 10:00 AM - 2:00 PM EST",
    "location": "GSEMA Service Center - Waltham",
    "color": "#F27536",
    "description": "This course is offered in two parts: an online program and a hands-on skills session with an instructor. ",
    "start": "2020-09-16",
    "end": "2020-09-16",
    "path": "/content/girlscoutseasternmass/en/events-repository/2020/medic_blended15.html"
  },
  {
    "title": "Deadline for Gold Award Project Proposals and Final Reports",
    "displayDate": "Wed, Sep 30, 2020, 5:00 PM EST",
    "location": "Deadline",
    "color": "#eb0789",
    "description": "GSEMA uses deadlines to collect both Gold Award project proposals and Gold Award final reports. In order to be considered for an upcoming in-person interview or final presentation, you must submit by the deadline before.",
    "start": "2020-09-30",
    "end": "2020-09-30",
    "path": "/content/girlscoutseasternmass/en/events-repository/2019/goldaward-deadline5.html"
  }
]
    });

    calendar.render();
  });

