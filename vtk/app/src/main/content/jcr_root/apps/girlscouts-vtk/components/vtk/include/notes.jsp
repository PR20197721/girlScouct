<%
//final org.girlscouts.vtk.dao.MeetingDAO gg = sling.getService(org.girlscouts.vtk.dao.MeetingDAO.class);
//MeetingE xx= gg.getMeetingE( user,  troop,  meeting.getPath());
//java.util.List <org.girlscouts.vtk.models.Note> notes = xx.getNotes();

//-java.util.List <org.girlscouts.vtk.models.Note> notes = meeting.getNotes();

final org.girlscouts.vtk.dao.MeetingDAO gg = sling.getService(org.girlscouts.vtk.dao.MeetingDAO.class);
java.util.List <org.girlscouts.vtk.models.Note> notes = gg.getNotes(  user,  troop,  meeting.getPath() );
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
        <li><%=note.getMessage()%></li>
    <%
  }//edn for
 %>
 </ul>
 
 <br/><b>Create new Note:</b>
 <br/><input type="text" id="note" value=""/>
 <br/><input type="button" value="Save" onclick="editNote('<%=meeting.getUid()%>')"/>
 </form>
