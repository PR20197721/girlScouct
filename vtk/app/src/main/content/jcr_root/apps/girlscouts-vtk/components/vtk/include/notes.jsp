<%
org.girlscouts.vtk.models.Note note = meeting.getNote();

%>
<div style="background-color:yellow;">
 <form>
    <textarea cols="5" rows="10" id="note"><%=note==null ? "" : note.getMessage()%></textarea>
    
    <br/><input type="button" value="Save" onclick="editNote('<%=meeting.getUid()%>')"/>
    
 </form>
</div>