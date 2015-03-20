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
  
    <!--  Annas stuff
     <h4>Lorem ipsum dolor sit amet</h4>
     <p>Donec a diam lectus. Sed sit amet ipsum mauris. Maecenas congue ligula ac quam viverra nec consectetur ante hendrerit. Donec et mollis dolor. Praesent et diam eget libero egestas mattis sit amet vitae augue. Nam tincidunt congue enim, ut porta lorem lacinia consectetur. Donec ut libero sed arcu vehicula ultricies a non tortor.</p>
      <ul>
        <li>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean ut gravida lorem. Ut turpis felis,</li>
        <li>Pulvinar a semper sed, adipiscing id dolor. Pellentesque auctor nisi id magna consequat sagittis.</li>
        <li>Curabitur dapibus enim sit amet elit pharetra tincidunt feugiat nisl imperdiet.</li>
      </ul>
      <p>Ut convallis libero in urna ultrices accumsan. Donec sed odio eros. Donec viverra mi quis quam pulvinar at malesuada arcu rhoncus. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. In rutrum accumsan ultricies. Mauris vitae nisi at sem facilisis semper ac in est.</p>
    -->
    </div>
  </div>
</div>
