
<div style="background-color:red;">


<a href="javascript:void(0)" onclick="revertAgenda( thisMeetingPath )">Revert to Original Agenda</a>
<br />
<a href="javascript:void(0)" onclick="loadModal('#newMeetingAgenda', true, 'Agenda', false);">Add Agenda Items</a>
<div id="newMeetingAgenda" style="display: none;">



<h1>Add New Agenda Item</h1>



Enter Agenda Item Name:<br /> <input type="text"

id="newCustAgendaName" value="" /> <br />Time Allotment: <select

id="newCustAgendaDuration">

<option value="5">5</option>

<option value="10">10</option>

<option value="15">15</option>

<option value="20">20</option>

<option value="25">25</option>

<option value="30">30</option>

</select> <br />Description:

<textarea id="newCustAgendaTxt"></textarea>

<br /> <br />

<div class="linkButtonWrapper">

<input type="button" value="save"

onclick="createCustAgendaItem2('<%=planView.getSearchDate().getTime()%>', '<%=newActivityDate.getTime()%>', thisMeetingPath)"

class="button linkButton" />


</div>
</div>
    </div>