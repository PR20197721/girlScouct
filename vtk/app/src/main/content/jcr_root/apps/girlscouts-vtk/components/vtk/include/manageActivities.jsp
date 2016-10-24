
<div class="content clearfix" id="panel3">
<% if( troop.getYearPlan().getActivities()!=null && troop.getYearPlan().getActivities().size()>0) { %>
  <table>
    <%
      for(int t=0;t<troop.getYearPlan().getActivities().size(); t++){
    %>
      <tr>
        <td><strong><%=troop.getYearPlan().getActivities().get(t).getDate() %></strong></td>
        <td><%=troop.getYearPlan().getActivities().get(t).getName() %></td>
        <td><a href="javascript:void(0)"onclick="rmCustActivity('<%=troop.getYearPlan().getActivities().get(t).getPath()%>')" title="remove">Remove</a></td>
      </tr>
    <% } %>
  </table>
  <% } else {  %> <p>No activities.</p> <% } %>
</div><!--/content-3-->