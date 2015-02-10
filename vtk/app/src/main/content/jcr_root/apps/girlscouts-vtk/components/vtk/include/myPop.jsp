
<div style="background-color:red;">
  <a href="javascript:void(0)" onclick="revertAgenda( thisMeetingPath )">Revert to Original Agenda</a>

  <a href="javascript:void(0)" onclick="loadModal('#newMeetingAgenda', true, 'Agenda', false);">Add Agenda Items</a>
  <div id="newMeetingAgenda" style="display: none;">
    <h1>Add New Agenda Item</h1>



    <p>Enter Agenda Item Name:</p>
    <input type="text" id="newCustAgendaName" value="" />
    <p>Time Allotment:</p>
    <select id="newCustAgendaDuration">
      <option value="5">5</option>

      <option value="10">10</option>

      <option value="15">15</option>

      <option value="20">20</option>

      <option value="25">25</option>

      <option value="30">30</option>

    </select> 
    <p>Description:</p>

    <textarea id="newCustAgendaTxt"></textarea>


    <div class="linkButtonWrapper">
      <input type="button" value="save" onclick="createCustAgendaItem2('<%=planView.getSearchDate().getTime()%>', '<%=newActivityDate.getTime()%>', thisMeetingPath)" class="button linkButton" />
    </div>
  </div>
</div>