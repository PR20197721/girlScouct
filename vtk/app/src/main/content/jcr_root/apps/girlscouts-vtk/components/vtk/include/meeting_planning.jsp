<section className="column large-20 medium-20 large-centered medium-centered">
  <h6>planning materials</h6>
  <ul>
   <li>
    <a data-reveal-id="modal_popup" data-reveal-ajax="true" href={"/content/girlscouts-vtk/controllers/vtk.include.modals.modal_agenda_edit.html?mid=<%=mid%>&isOverview=true"}>Meeting Overview</a>    
  </li>
  <%if( hasPermission(troop, Permission.PERMISSION_EDIT_MEETING_ID ) ) {%>    
   <li>
    <a data-reveal-id="modal_popup" data-reveal-ajax="true" href={"/content/girlscouts-vtk/controllers/vtk.include.modals.modal_agenda_edit.html?mid=<%=mid%>&isActivity=true"}>Activity Plan</a>            
   </li>
   <% } %>
   <li>
    <a data-reveal-id="modal_popup" data-reveal-ajax="true" href={"/content/girlscouts-vtk/controllers/vtk.include.modals.modal_agenda_edit.html?mid=<%=mid%>&isMaterials=true"}>Materials List</a>
  </li>
  </ul>
</section>