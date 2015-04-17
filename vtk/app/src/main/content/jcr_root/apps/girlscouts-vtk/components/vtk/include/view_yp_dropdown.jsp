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
     
      //boolean isLibYearPlan= false;
      java.util.Iterator<YearPlan> yearPlans = yearPlanUtil.getAllYearPlans(user, ageLevel).listIterator();
        while (yearPlans.hasNext()) {
          YearPlan yearPlan = yearPlans.next();
         // if(troop.getYearPlan()!=null && (yearPlan.getName().equals(troop.getYearPlan().getName()))){isLibYearPlan=true;}
      %>
      <div class="row">
        <div class="columns large-push-2 medium-2 medium-push-2 small-2">
           <input type="radio" <%=( troop.getYearPlan()!=null && (yearPlan.getName().equals(troop.getYearPlan().getName()))) ? " checked " : "" %> 
           id="r_<%=yearPlan.getId()%>" class="radio1" name="group1" onclick="chgYearPlan('<%=yearPlan.getId()%>', '<%=yearPlan.getPath()%>', '<%=confMsg%>', '<%=yearPlan.getName()%>')" />
            <label for="r_<%=yearPlan.getId()%>"></label>
            
        </div>
        <div class="small-18 columns large-pull-2 medium-pull-2 small-pull-2">
			<a href="#" onclick="chgYearPlan('<%=yearPlan.getId()%>', '<%=yearPlan.getPath()%>', '<%=confMsg%>', '<%=yearPlan.getName()%>')"><%=yearPlan.getName()%></a>
			<p><%=yearPlan.getDesc()%></p>
        </div>
      </div><!--/row-->
      <% }  %>
      
      
        <div class="row">
        <div class="columns large-push-2 medium-2 medium-push-2 small-2">
           <input type="radio" <%=( troop.getYearPlan()!=null && (troop.getYearPlan().getName().equals("Custom Year Plan"))) ? " checked " : "" %> id="r_0" class="radio1" name="group1"  onclick="chgCustYearPlan('<%=troop.getYearPlan().getId()%>', '<%=troop.getYearPlan().getPath()%>', '<%=confMsg%>', '<%=troop.getYearPlan().getName()%>')" />
            <label for="r_0"></label>
        </div>
        <div class="small-18 columns large-pull-2 medium-pull-2 small-pull-2">
            <a href="/content/girlscouts-vtk/controllers/vtk.include.modals.modal_custom_year_plan.html" data-reveal-ajax="true" data-reveal-id="modal_custom_year_plan" onclick="return chgCustYearPlan('<%=troop.getYearPlan().getId()%>', '<%=troop.getYearPlan().getPath()%>', '<%=confMsg%>', '<%=troop.getYearPlan().getName()%>')">Create Your Own Year Plan</a>
            <p>Choose this option to create your own year plan using meetings from  our meeting library</p>
        </div>
      </div><!--/row-->
      
      
      
  </div><!--/yearPlanSelection-->
</section>
<script>
function chgYearPlan(planId, planPath, confirmMsg, planName){
	<%if( troop.getYearPlan()!=null ){%>
		if(planName==='<%=troop.getYearPlan().getName()%>'){
			confirmMsg ="Are you sure to reset the yearplan?";
		}
	<%}%>
	x(planId, planPath, confirmMsg, planName);
};

function chgCustYearPlan(planId, planPath, confirmMsg, planName) {
    <%if( troop.getYearPlan()!=null ){%>
        if(planName==='<%=troop.getYearPlan().getName()%>'){
            confirmMsg ="Are you sure to reset the yearplan?";
        }
    <%}%>
    //x(planId, planPath, confirmMsg, planName);
 
    $('#modal_custom_year_plan').foundation('reveal', 'open', {
        url: "/content/girlscouts-vtk/controllers/vtk.include.modals.modal_custom_year_plan.html",
        //data: {param1: 'value1', param2: 'value2'}
         success: function(data) {
          var min_height = $('#sortable1').height()-71;
          $("#sortable2").css('min-height', min_height);  
        }
    });
};

</script>
<div id="modal_custom_year_plan" class="reveal-modal" data-reveal></div>