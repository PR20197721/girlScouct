<%
  if (troops != null && troops.size() > 1) {
    Cookie cookie = new Cookie("vtk_prefTroop", troop.getTroop().getGradeLevel());
    cookie.setMaxAge(-1);
    response.addCookie(cookie);
%>

<div id="troop" class="row hide-for-print">
  <div class="columns large-7 medium-7 right">
    <select id="reloginid" onchange="relogin()">
      <%
        for (int i = 0; i < troops.size(); i++) {
      %>
      <option value="<%=troops.get(i).getTroopId()%>"
        <%=troop.getTroop().getTroopId()
              .equals(troops.get(i).getTroopId()) ? "SELECTED"
              : ""%>><%=troops.get(i).getTroopName()%>
        :
        <%=troops.get(i).getGradeLevel()%></option>
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
  <%
    //if (troop.getYearPlan() != null) {
  %>
  <div class="row">
    <div class="columns large-22 large-centered small-24">
      <%
      //  }
      %>
      <dl class="tabs hide-for-small">
        <% if(hasPermission(troop, Permission.PERMISSION_VIEW_TROOP_ID)) { %>
          <dd <%= "myTroop".equals(activeTab) ? "class='active'" : "" %>>
            <a href="/content/girlscouts-vtk/en/vtk.mytroop_react.html">My Troop</a>
          </dd>
        <% } %>
        <% if(hasPermission(troop, Permission.PERMISSION_VIEW_YEARPLAN_ID)) { %>
          <dd <%= "plan".equals(activeTab) ? "class='active'" : "" %>>
           <!--  <a href="/content/girlscouts-vtk/en/vtk.plan.html">Year Plan</a> -->
           <a href="/content/girlscouts-vtk/en/vtk.html">Year Plan</a>
          </dd>
        <% } %>
        <% if(hasPermission(troop, Permission.PERMISSION_VIEW_MEETING_ID)) { %>
        <dd <%= "planView".equals(activeTab) ? "class='active'" : "" %>>
           <a <%= troop.getYearPlan() != null ? "href='/content/girlscouts-vtk/en/vtk.details.html'" :  "href='#' onClick='alert(\"Please select a year plan\")'"  %>>Meeting Plan</a>
        </dd>
        <%  } %>
        <dd <%= "resource".equals(activeTab) ? "class='active'" : "" %>>
          <a href="/content/girlscouts-vtk/en/vtk.resource.html">Resources</a>
        </dd>
        <% if( hasPermission(troop, Permission.PERMISSION_VIEW_FINANCE_ID) ){ %>
          <dd <%= "finances".equals(activeTab) ? "class='active'" : "" %>>
            <a href="/content/girlscouts-vtk/en/vtk.finances.html">Finances</a>
          </dd>
        <% }  %>
        <!-- % // to do add this to javA if(hasPermission(troop, Permission.PERMISSION_VIEW_PROFILE) ) { %-->
        <dd <%= "profile".equals(activeTab) ? "class='active'" : "" %>>
          <a href="/content/girlscouts-vtk/en/vtk.profile.html">Profile</a>
        </dd>
        <!-- % // }   %-->
      </dl>
      <select class="tabs show-for-small">
        <% if(hasPermission(troop, Permission.PERMISSION_VIEW_TROOP_ID)) { %>
          <option value="/content/girlscouts-vtk/en/vtk.mytroop_react.html">My Troop</option>
        <% } %>
        <% if(hasPermission(troop, Permission.PERMISSION_VIEW_YEARPLAN_ID)) { %>
          <option value="/content/girlscouts-vtk/en/vtk.html">Year Plan</option>
        <% } %>
        <% if(hasPermission(troop, Permission.PERMISSION_VIEW_MEETING_ID)) { 
          String ref = "/content/girlscouts-vtk/en/vtk.details.html";
          if(troop.getYearPlan() == null) {
          ref = "onClick=alert(\"Please select a year plan\")";
          }
        %>
          <option value="<%=ref%>">Meeting Plan</option>
        <%  } %>
        <% if( hasPermission(troop, Permission.PERMISSION_VIEW_FINANCE_ID) ){ %>
          <option value="/content/girlscouts-vtk/en/vtk.finances.html">Finances</option>
        <% }  %>
          <option value="/content/girlscouts-vtk/en/vtk.profile.html">Profile</option>
      </select>
      <%
      //  if (troop.getYearPlan() != null) {
      %>
    </div><!--/columns-->
  </div><!--/row-->
  <%
    //}
  %>
</div><!--/hide-for-print-->
