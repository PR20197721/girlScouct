<div class="row">
  <div class="column large-20 large-centered">
    <div class="row">
      <dl class="accordion-inner clearfix" data-accordion>
        <ul class="inline-list"> 
         <% for(int y=0;y<infos.size();y++) { 
             if(infos.get(y).isAchievement() && infos.get(y).getYearPlanComponent().getType()== YearPlanComponentType.MEETING) {   
         %>
                 <li><img src="/content/dam/girlscouts-vtk/local/icon/meetings/<%= ((MeetingE) infos.get(y).getYearPlanComponent()).getMeetingInfo().getId()%>.png" alt="imagetitle"/></li>
          <% 
             }
           }
         %>
        </ul>
      </dl>
    </div>
  </div>
</div>