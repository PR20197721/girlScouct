<%
final org.girlscouts.vtk.dao.MeetingDAO gg = sling.getService(org.girlscouts.vtk.dao.MeetingDAO.class);
java.util.List <org.girlscouts.vtk.models.Note> notes = gg.getNotes(  user,  troop, meeting.getUid());// meeting.getPath() );
%>


<script>
    var appVtk;
  $(function(){
    appVTK = initNotes;
    appVTK.getNotes('<%=meeting.getUid()%>','<%=user.getApiConfig().getUser().getSfUserId()%>').done(function(json){
        appVTK.interateNotes(json);
    });
  })
</script>
<%
if (user != null  && !userUtil.hasPermission(troop, Permission.PERMISSION_CREATE_MEETING_ID) ){
    %>No permission to view, edit, remove Notes <%
    return;
}
%>


 <form>
<div class="vtk-notes-wrap  small-20 columns small-offset-2">

<div class="row">
    <div class=" small-24 columns  ">
        <h6>MEETING NOTES</h6>
    </div>
</div>

<div class="row">

<div id="vtk-notes" class=" small-24 columns ">
  <div class="row">
     <div class="row">
        <div class="add-notes-area small-24 columns" style="display:none">
            <div class="add-note">
                <i class="icon-speech-bubbles"></i> Add A Note
            </div>
            <div class="add-note-detail" style="display:none">
                <div class="input-note">
                    <div class="input-content" contenteditable="true"></div>

                    <div class="note-loading">

                    </div>
                </div>
                 <div class="input-save" onclick="appVTK.addNote('<%=meeting.getUid()%>')">
                    Save
                 </div>
            </div>
         </div>
        </div>


    <div class="you-reach-25 small-24 columns" style="display:none">
      <h4>
        You Reach the Maximun amount of notes
      </h4>
    </div>

    <ul class="vtk-notes_list_container small-24 columns"></ul>
  </div>
</div>

</div>

</div>
 </form>


