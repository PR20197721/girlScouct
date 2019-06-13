<%@ page import="java.util.regex.Pattern, java.util.regex.Matcher" %><%
    String level = selectedTroop.getSfTroopAge().substring(selectedTroop.getSfTroopAge().indexOf("-") + 1).toLowerCase();
    if("SUM".equals(selectedTroop.getCouncilCode())){
        if(selectedTroop.getYearPlan() != null && selectedTroop.getYearPlan().getRefId() != null ){
            String refId = selectedTroop.getYearPlan().getRefId();
            if(!refId.isEmpty()){
                level = refId.substring(refId.lastIndexOf("/"));
                String regex = "\\/content\\/girlscouts-vtk\\/yearPlanTemplates\\/yearplan20\\d\\d\\/(.*)\\/yearPlan.*";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(refId);
                if (matcher.find()){
                    level = matcher.group(1);
                }
            }
        }
        %>
        <div class="columns medium-push-2 large-push-2 large-4 medium-4 small-4" style="float: none;">
            <select onchange="$('#vtk-yp-main').data('level', $(this).val()); startVtkYpApp();">
                <option value="daisy" <%="daisy".equals(level)? "selected": ""%>>Daisy</option>
                <option value="brownie" <%="brownie".equals(level)? "selected": ""%>>Brownie</option>
                <option value="junior" <%="junior".equals(level)? "selected": ""%>>Junior</option>
                <option value="cadette" <%="cadette".equals(level)? "selected": ""%>>Cadette</option>
                <option value="senior" <%="senior".equals(level)? "selected": ""%>>Senior</option>
                <option value="ambassador" <%="ambassador".equals(level)? "selected": ""%>>Ambassador</option>
                <option value="multi-level" <%="multi-level".equals(level)? "selected": ""%>>multi-level</option>
            </select>
        </div>
        <%
    }
%>
<section class="yp-wrapper">
    <div id="vtk-yp-main" data-level="<%=level%>"></div>
</section>
<script>
    var ________app________ = "";
    var ________app1________ = "<%=selectedTroop.getYearPlan()==null ? "" : selectedTroop.getYearPlan().getRefId()%>";
    var ________currentYearPlanName________ = "<%=selectedTroop.getYearPlan()!=null ? selectedTroop.getYearPlan().getName() : "" %>";
    var ________isYearPlan________ = <%=selectedTroop.getYearPlan() != null %>;
    var ________troopName________ = "<%=selectedTroop.getSfTroopName() %>";
    function startVtkYpApp() {
        ________app________ = $('#vtk-yp-main').data('level');
        if (IE()) {
            $('#vtk-yp-main').html('<div class="columns small-20 small-centered" style="text-align:center"><br /><br /><p>You are attempting to access the Volunteer Toolkit with an internet browser that is unsupported. <br /> Please use Chrome as the preferred Volunteer Toolkit browser for the best experience.<br /> <b>Thank you!</b></p><br /><br /></div>')
        } else {
            startYPApp();
        }
        $('#panelWrapper').css({'padding-bottom': '20px'})
    }
    window.onload = startVtkYpApp();
</script>
<div id="modal_custom_year_plan" class="reveal-modal" data-reveal></div>
<script>getCngYearPlan();</script>