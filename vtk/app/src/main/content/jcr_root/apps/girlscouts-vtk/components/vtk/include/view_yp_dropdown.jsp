<section class="yp-wrapper">
  <div class="sectionHeader">
    <div class="column large-20 medium-20 large-centered medium-centered" style="display:table; padding-left:0;">
    <span class="vkt-book-icon"></span> 
      <p id="showHideReveal" onclick="yesPlan()" class="hide-for-print">SEE YEAR PLAN LIBRARY</p>
    </div>
  </div>

  <div id="yearPlanSelection" <%= (troop.getYearPlan()!=null) ? "style=\"display: none\"":" " %> class="columns">
    <div class="row">
      <p class="large-20 medium-20 large-centered medium-centered columns">To start planning your year, select a Year Plan.</p>
    </div> 
    <div id="cngYearPlan"></div>
  </div><!--/yearPlanSelection-->
</section>



<div id="vtk-yp-main"></div>



<div id="modal_custom_year_plan" class="reveal-modal" data-reveal></div>
<script>getCngYearPlan();</script>