<%
//final org.girlscouts.vtk.dao.MeetingDAO gg = sling.getService(org.girlscouts.vtk.dao.MeetingDAO.class);
//MeetingE xx= gg.getMeetingE( user,  troop,  meeting.getPath());
//java.util.List <org.girlscouts.vtk.models.Note> notes = xx.getNotes();

//-java.util.List <org.girlscouts.vtk.models.Note> notes = meeting.getNotes();

final org.girlscouts.vtk.dao.MeetingDAO gg = sling.getService(org.girlscouts.vtk.dao.MeetingDAO.class);
java.util.List <org.girlscouts.vtk.models.Note> notes = gg.getNotes(  user,  troop, meeting.getUid());// meeting.getPath() );
%>

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
