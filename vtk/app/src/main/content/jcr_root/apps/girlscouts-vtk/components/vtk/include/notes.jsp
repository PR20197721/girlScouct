<%
final org.girlscouts.vtk.dao.MeetingDAO gg = sling.getService(org.girlscouts.vtk.dao.MeetingDAO.class);
java.util.List <org.girlscouts.vtk.models.Note> notes = gg.getNotes(  user,  troop, meeting.getUid());// meeting.getPath() );
%>


<script>
    var appVtk;
  $(function(){
    appVTK = initNotes;
    appVTK.getNotes('<%=meeting.getUid()%>','<%=user.getApiConfig().getUser().getSfUserId()%>');
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
      <!--   <%=meeting.getPath()%>
     ***<%=notes==null  ? "No notes found." : "Found: "+notes.size() +" notes."%> -->


      <div class="add-notes-area small-24 columns" style="display:none">
        <div class="add-note">
          <i class="icon-speech-bubbles"></i> Add A Note
        </div>
      <div class="add-note-detail" style="display:none">

          <div class="input-note">

          <div class="input-content" contenteditable="true"></div>

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

    <ul class="vtk-notes_list  small-24 columns ">
        <!-- Notes Here -->
    </ul>


  </div>
</div>

</div>

</div>
<!--

 <%=meeting.getPath()%>

 ***<%=notes==null  ? "No notes found." : "Found: "+notes.size() +" notes."%>

 <ul>
 <%
 if( notes!=null)
  for(int nt = 0; nt < notes.size(); nt++){
    Note note = notes.get(nt);
    %>
        <li>
            <%=new java.util.Date(note.getCreateTime())%> :: <%=note.getCreatedByUserName()%>:: <%=note.getCreatedByUserId()%> ::

            <%if(note.getCreatedByUserId().equals(user.getApiConfig().getUser().getSfUserId()) ){%>
                <input type="text" name="" value="<%=note.getMessage()%>" id="note<%=note.getUid()%>"/>

                <%if(user.getCurrentYear().equals( VtkUtil.getCurrentGSYear()+"") ){%>
                    <a href="javascript:void(0)" onclick="rmNote('<%=note.getUid()%>')">delete</a> ||
                    <a href="javascript:void(0)" onclick="editNote('<%=note.getUid()%>')">edit</a>
                <%}%>

            <%}else{%>
                <%=note.getMessage()%>
            <%}//end else%>
        </li>
    <%
  }//edn for

 %>
 </ul>



<%if( user.getCurrentYear().equals( VtkUtil.getCurrentGSYear()+"") &&  notes.size()<=25) {%>

     <br/><b>Create new Note:</b>
     <br/><input type="text" id="note" value=""/>
     <br/><input type="button" value="Save" onclick="addNote('<%=meeting.getUid()%>')"/>
 <%}else{%>
    Max number of notes 25
 <%}//edn else%>-->
 </form>


