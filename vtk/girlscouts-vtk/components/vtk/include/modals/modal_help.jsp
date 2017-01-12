<div id="modal_help" class="reveal-modal" data-reveal>
  <div class="header clearfix">
    <h3 class="columns large-22">Help</h3>
    <a class="close-reveal-modal columns large-2" href="#"><i class="icon-button-circle-cross"></i></a>
  </div>
  <div class="scroll">
    <div class="content">

        <%if( activeTab!=null && activeTab.equals("plan")  ){ %>
             <h4>YEAR PLAN</h4>
             <p>This is "mission control" for your year's meetings. You choose them, reorder them, cancel them, set their locations and times, and add activities. You can add events, synch your plan with your calendar, and print it all out.</p>
        
        <%}else if( activeTab!=null && activeTab.equals("planView") ){ %> 
             <h4>MEETING PLAN</h4>
             <p>Here you manage the details of the meeting. You can change, add, and delete agenda items. You can view the directions to the meeting location. You can download and print meeting aids and planning materials.</p>
        
        <%} %>
    </div>
  </div>
</div>
