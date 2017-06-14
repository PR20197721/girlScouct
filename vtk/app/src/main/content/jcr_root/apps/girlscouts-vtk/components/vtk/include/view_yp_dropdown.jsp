<section class="yp-wrapper">
  <div class="sectionHeader">
    <div class="column large-20 medium-20 large-centered medium-centered" style="display:table; padding-left:0;">
    <span class="vkt-book-icon"></span> 
      <p id="showHideReveal" onclick="yesPlan.click()" class="hide-for-print">EXPLORE THE YEAR PLAN LIBRARY</p>
    </div>
  </div>

  <div id="yearPlanSelection_" <%= (troop.getYearPlan()!=null) ? "style=\"display: none\"":" " %> >

    <div id="vtk-yp-main"></div>

  </div>
</section>

<script>
  var ________app________ = "<%=troop.getSfTroopAge()%>".substring("<%=troop.getSfTroopAge()%>".indexOf("-")+1).toLowerCase();
  var ________app1________ = "<%=troop.getYearPlan()==null ? "" : troop.getYearPlan().getRefId()%>";
  var ________currentYearPlanName________ = "<%=troop.getYearPlan()!=null ? troop.getYearPlan().getName() : "" %>";

  var ________isYearPlan________ = <%=troop.getYearPlan()!=null ? true: false %>;
  var ________troopName________ = "<%=troop.getSfTroopName() %>";  

  function IE(v) {
    return RegExp('msie' + (!isNaN(v)?('\\s'+v):''), 'i').test(navigator.userAgent);
  }


  window.onload = function () {
    
    if(IE(10)||IE(11)){
      gsDialog({
		content: 'You are attempting to access the Volunteer Toolkit with an internet browser that is unsupported. Please use Chrome as the preferred Volunteer Toolkit browser for the best experience. Thank you!',
		headerText: '--- Atention ---',
		buttons : [	{
					text: "OK",
					click: function () {
						$(this).dialog("close");
					}
		}]
	})
    }else{
       startYPApp();
    }
   
 

 }

</script>






<div id="modal_custom_year_plan" class="reveal-modal" data-reveal></div>
<script>getCngYearPlan();</script>