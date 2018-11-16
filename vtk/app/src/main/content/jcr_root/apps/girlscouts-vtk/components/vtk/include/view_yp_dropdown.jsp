<section class="yp-wrapper">
    <div id="vtk-yp-main"></div>
</section>

<script>


  var ________app________ = "<%=troop.getSfTroopAge()%>".substring("<%=troop.getSfTroopAge()%>".indexOf("-")+1).toLowerCase();
  var ________app1________ = "<%=troop.getYearPlan()==null ? "" : troop.getYearPlan().getRefId()%>";
  var ________currentYearPlanName________ = "<%=troop.getYearPlan()!=null ? troop.getYearPlan().getName() : "" %>";

  var ________isYearPlan________ = <%=troop.getYearPlan()!=null ? true: false %>;
  var ________troopName________ = "<%=troop.getSfTroopName() %>";  




  window.onload = function () {
  if(IE()){


    $('#vtk-yp-main').html('<div class="columns small-20 small-centered" style="text-align:center"><br /><br /><p>You are attempting to access the Volunteer Toolkit with an internet browser that is unsupported. <br /> Please use Chrome as the preferred Volunteer Toolkit browser for the best experience.<br /> <b>Thank you!</b></p><br /><br /></div>')

    }else{
       startYPApp();
    }
   
    $('#panelWrapper').css({'padding-bottom':'20px'})

 }

</script>






<div id="modal_custom_year_plan" class="reveal-modal" data-reveal></div>
<script>getCngYearPlan();</script>