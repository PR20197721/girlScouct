<%
//final org.girlscouts.vtk.dao.MeetingDAO gg = sling.getService(org.girlscouts.vtk.dao.MeetingDAO.class);
//MeetingE xx= gg.getMeetingE( user,  troop,  meeting.getPath());
//java.util.List <org.girlscouts.vtk.models.Note> notes = xx.getNotes();

//-java.util.List <org.girlscouts.vtk.models.Note> notes = meeting.getNotes();

final org.girlscouts.vtk.dao.MeetingDAO gg = sling.getService(org.girlscouts.vtk.dao.MeetingDAO.class);
java.util.List <org.girlscouts.vtk.models.Note> notes = gg.getNotes(  user,  troop, meeting.getUid());// meeting.getPath() );
%>


<script>
  $(function(){
    getNotes('<%=meeting.getUid()%>');

  })  
</script>


 <form>


<div id="vtk-notes" class="columns small-20 small-centered">
  <div class="row">
        <%=meeting.getPath()%>
     ***<%=notes==null  ? "No notes found." : "Found: "+notes.size() +" notes."%>


      <div class="add-notes-area">
      <div class="add-note">
       +
      </div>
      <div class="add-note-detail">
                  <b>Create new Note:</b>
       <br/><input type="text" id="note" value=""/>
       <br/><input type="button" value="Save" onclick="addNote('<%=meeting.getUid()%>')"/>

      </div>
    </div>

    <ul class="vtk-notes_list columns small-24 small-centered">
    <div class="row">
      
    </div>
    </ul>


   

  </div>


</div>

<form>

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
                <a href="javascript:void(0)" onclick="rmNote('<%=note.getUid()%>')">delete</a> ||
                <a href="javascript:void(0)" onclick="editNote('<%=note.getUid()%>')">edit</a>

            <%}else{%>
                <%=note.getMessage()%>
            <%}//end else%>
        </li>
    <%
  }//edn for
 %>
 </ul>

 <%if(notes.size()<=25){%>
     <br/><b>Create new Note:</b>
     <br/><input type="text" id="note" value=""/>
     <br/><input type="button" value="Save" onclick="addNote('<%=meeting.getUid()%>')"/>
 <%}else{%>
    Max number of notes 25
 <%}//edn else%>
 </form>


