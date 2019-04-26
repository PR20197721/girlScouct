<%
    if (troops != null && troops.size() > 1) {
        Cookie cookie = new Cookie("vtk_prefTroop", troop.getGradeLevel());
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
%>
<div id="troop" class="row hide-for-print">
    <div class="columns large-7 medium-7 right">
        <select id="reloginid" onchange="relogin()">
            <%
                for (Troop userTroop : troops) {
            %>
            <option value="<%=userTroop.getHash()%>"<%=troop.getHash().equals(userTroop.getHash()) ? " selected" : ""%>><%=userTroop.getTroopName()%>
                : <%=userTroop.getGradeLevel()%>
            </option>
            <%
                }
            %>
        </select>
    </div>
</div>
<%
    }
%>
<div class="hide-for-print tab-wrapper">
    <div class="row">
        <div class="columns">
            <dl class="tabs">
                <% if (VtkUtil.hasPermission(troop, Permission.PERMISSION_VIEW_TROOP_ID)) { %>
                <dd <%= "myTroop".equals(activeTab) ? "class='active'" : "" %>>
                    <a href="/content/girlscouts-vtk/en/vtk.myTroop.html">My Troop</a>
                </dd>
                <% } %>
                <% if (VtkUtil.hasPermission(troop, Permission.PERMISSION_VIEW_YEARPLAN_ID)) { %>
                <dd <%= "plan".equals(activeTab) ? "class='active'" : "" %>>
                    <a href="/content/girlscouts-vtk/en/vtk.html">Year Plan</a>
                </dd>
                <% } %>
                <% if (VtkUtil.hasPermission(troop, Permission.PERMISSION_VIEW_MEETING_ID)) { %>
                <dd <%= "planView".equals(activeTab) ? "class='active'" : "" %>>
                    <a <%= troop.getYearPlan() != null ? "href='/content/girlscouts-vtk/en/vtk.meeting_react2.html'" : "href='#' onClick='alert(\"Please select a year plan\")'"  %>>Meeting
                        Plan</a>
                </dd>
                <% } %>
                <dd <%= "resource".equals(activeTab) ? "class='active'" : "" %>>
                    <a href="/content/girlscouts-vtk/en/myvtk/<%= troop.getSfCouncil() %>/vtk.resource.html">Resources</a>
                </dd>
                <% if (VtkUtil.hasPermission(troop, Permission.PERMISSION_VIEW_FINANCE_ID)) { %>
                <dd <%= "finances".equals(activeTab) ? "class='active'" : "" %>>
                    <a href="/content/girlscouts-vtk/en/vtk.finances.html">Finances</a>
                </dd>
                <% } %>
                <dd <%= "profile".equals(activeTab) ? "class='active'" : "" %>>
                    <a href="/content/girlscouts-vtk/en/vtk.profile.html">Profile</a>
                </dd>
            </dl>
        </div><!--/columns-->
    </div><!--/row-->
</div>