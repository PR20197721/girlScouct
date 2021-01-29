<div class="content clearfix" id="panel3">
    <% if (selectedTroop.getYearPlan().getActivities() != null && selectedTroop.getYearPlan().getActivities().size() > 0) { %>
    <table>
        <%
            for (int t = 0; t < selectedTroop.getYearPlan().getActivities().size(); t++) {
        %>
        <tr>
            <td><strong><%=selectedTroop.getYearPlan().getActivities().get(t).getDate() %>
            </strong></td>
            <td><%=selectedTroop.getYearPlan().getActivities().get(t).getName() %>
            </td>
            <!-- <td><a href="javascript:void(0)"
                   onclick="rmCustActivity('<%=selectedTroop.getYearPlan().getActivities().get(t).getPath()%>')"
                   title="remove">Remove</a></td> -->

             <td><a href="javascript:void(0)"
                    onclick="rmActivities('<%=selectedTroop.getYearPlan().getActivities().get(t).getPath()%>')"
                    title="remove">Remove</a></td>
            <div id ="remove-activities"></div>
        </tr>
        <% } %>
    </table>
    <% } else { %>
    <div>
        <p>There are no activities in your Year Plan.</p>
        <div style="float:right;">
            <button class="btn right" onclick="newActivity()">ADD ACTIVITY</button>
        </div>
    </div>
    <% } %>
</div>
<!--/content-3-->