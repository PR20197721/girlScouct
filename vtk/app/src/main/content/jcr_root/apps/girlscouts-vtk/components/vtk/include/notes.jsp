<%
java.util.List <org.girlscouts.vtk.models.Note> notes = meeting.getNotes();

%>

 <form>
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
