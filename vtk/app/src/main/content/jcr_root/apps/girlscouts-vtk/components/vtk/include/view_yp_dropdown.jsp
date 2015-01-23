<section class="yp-wrapper">
  <div class="sectionHeader">
    <div class="column large-20 medium-20 large-centered medium-centered">
      <p id="showHideReveal" onclick="yesPlan()" class="hide-for-print">VIEW YEAR PLAN LIBRARY</p>
    </div>
  </div>

  <div id="yearPlanSelection" <%= (troop.getYearPlan()!=null) ? "style=\"display: none\"":" " %> class="columns">
    <div class="row">
      <p class="large-20 medium-20 large-centered medium-centered columns">To start planning your year, select a Year Plan.</p>
    </div>
    <%
      String ageLevel=  troop.getTroop().getGradeLevel();
      ageLevel= ageLevel.substring( ageLevel.indexOf("-")+1);
      ageLevel=ageLevel.toLowerCase().trim();

      String confMsg="";
      if( troop.getYearPlan()!=null ){
        if( troop.getYearPlan().getAltered()!=null && troop.getYearPlan().getAltered().equals("true") ){
          confMsg ="Are You Sure? You will lose customizations that you have made";
        } 
      }
      
      java.util.Iterator<YearPlan> yearPlans = yearPlanUtil.getAllYearPlans(user, ageLevel).listIterator();
        while (yearPlans.hasNext()) {
          YearPlan yearPlan = yearPlans.next();
          
      %>
      <div class="row">
        <div class="large-2 columns large-push-2">
            <input type="radio" <%=( troop.getYearPlan()!=null && (yearPlan.getId().equals(troop.getYearPlan().getId()))) ? " checked " : "" %> id="r_<%=yearPlan.getId()%>" class="radio1" name="group1" onclick="x('<%=yearPlan.getId()%>', '<%=yearPlan.getPath()%>', '<%=confMsg%>', '<%=yearPlan.getName()%>')" />
            <label for="r_<%=yearPlan.getId()%>"></label>
            <!-- <input type="submit" name="" value="<%=yearPlan.getName()%>" onclick="x('<%=yearPlan.getId()%>', '<%=yearPlan.getPath()%>', '<%=confMsg%>', '<%=yearPlan.getName()%>')" /> -->
        </div>
        <div class="large-18 columns large-pull-2">
          <a href="#" onclick="x('<%=yearPlan.getId()%>', '<%=yearPlan.getPath()%>', '<%=confMsg%>', '<%=yearPlan.getName()%>')"><%=yearPlan.getName()%></a>
          <p><%=yearPlan.getDesc()%></p>
        </div>
      </div><!--/row-->
      <% }  %>
  </div><!--/yearPlanSelection-->
</section>