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
    <div id="cngYearPlan">
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
        <div class="columns large-push-2 medium-2 medium-push-2 small-2">
       <input type="radio" <%=( troop.getYearPlan()!=null && (yearPlan.getName().equals(troop.getYearPlan().getName()))) ? " checked " : "" %> 
           id="r_<%=yearPlan.getId()%>" class="radio1" name="group1" onclick="chgYearPlan('<%=yearPlan.getId()%>', '<%=yearPlan.getPath()%>', '<%=confMsg%>', '<%=yearPlan.getName()%>', <%=troop.getYearPlan()!=null ? true: false %> ,'<%=troop.getYearPlan().getName()%>' )" />
            <label for="r_<%=yearPlan.getId()%>"></label>
            
        </div>
        <div class="small-18 columns large-pull-2 medium-pull-2 small-pull-2">
			<a href="#" onclick="chgYearPlan('<%=yearPlan.getId()%>', '<%=yearPlan.getPath()%>', '<%=confMsg%>', '<%=yearPlan.getName()%>')"><%=yearPlan.getName()%>, , <%=troop.getYearPlan()!=null ? true: false %> ,'<%=troop.getYearPlan().getName()%>'</a>
			<p><%=yearPlan.getDesc()%></p>
        </div>
      </div><!--/row-->
      <% }  %>
      
      
        <div class="row">
        <div class="columns large-push-2 medium-2 medium-push-2 small-2">
           <input type="radio" <%=( troop.getYearPlan()!=null && (troop.getYearPlan().getName().equals("Custom Year Plan"))) ? " checked " : "" %> id="r_0" class="radio1" name="group1"  onclick="chgCustYearPlan('<%=troop.getYearPlan()==null ? "" : troop.getYearPlan().getId()%>', '<%=troop.getYearPlan()==null ? "" :troop.getYearPlan().getPath()%>', '<%=confMsg%>', '<%=troop.getYearPlan()==null ? "" :troop.getYearPlan().getName()%>')" />
            <label for="r_0"></label>
        </div>
        <div class="small-18 columns large-pull-2 medium-pull-2 small-pull-2">
            <a onclick="return chgCustYearPlan('<%=troop.getYearPlan()==null ? "" : troop.getYearPlan().getId()%>', '<%=troop.getYearPlan()==null ? "" : troop.getYearPlan().getPath()%>', '<%=confMsg%>', '<%=troop.getYearPlan()==null ? "" : troop.getYearPlan().getName()%>')">Create Your Own Year Plan</a>
            <p>Choose this option to create your own year plan using meetings from  our meeting library</p>
        </div>
      </div><!--/row-->
      
      
      
      </div>
  </div><!--/yearPlanSelection-->
</section>

<div id="modal_custom_year_plan" class="reveal-modal" data-reveal></div>
<script>getCngYearPlan();</script>