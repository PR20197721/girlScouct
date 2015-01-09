<div class="hide-for-print crumbs">
  <div class="column large-20 medium-24 large-centered medium-centered">
    <div class="row">
      <div class="columns large-20 medium-20">
        <ul id="sub-nav" class="inline-list hide-for-print">
          <!--if on YP page this menu shows-->
		  <% if("plan".equals(activeTab)) { %>
          <li><a href="#" onclick="newLocCal()" data-reveal-id="modal_meeting" title="Metting Dates and Location">Meeting Dates and Locations</a></li>
          <li><a href="#" onclick="doMeetingLib()" title="Add Meeting">Add Meeting</a></li>
          <li><a href="#" onclick="newActivity()" title="Add Activity">Add Activity</a></li>
          <% } %>
          <!-- if on Meeting Detail Page-->
		  <% if("planView".equals(activeTab)) { %>
          <li><a data-reveal-id="modal_activity" title="Replace this meeting">Replace this meeting</a></li>
          <% } %>
          <!-- if on a My Troop page-->
		  <% if("myTroop".equals(activeTab)) { %>
          <li><a data-reveal-id="modal_upload_image" title="update photo">add/change a photo of your troop</a></li>
          <% } %>
        </ul>
      </div>
      <div class="columns large-4 medium-4">
       <ul class="inline-list" id="util-links">
          <li><a class="icon" onclick="javascript:window.help()" title="help"><i class="icon-questions-answers"></i></a></li>
		  <% if("plan".equals(activeTab)) { %>
          <li><a class="icon" onclick="javascript:window.download()" title="help"><i class="icon-download"></i></a>
          <li><a class="icon" onclick="javascript:window.print()" title="help"><i class="icon-printer"></i></a>
          <% } %>
        </ul>
      </div>
    </div>
  </div>
</div>