<%@ page import="java.util.regex.Matcher, java.util.regex.Pattern" %>
<%if (selectedTroop.getIsLoadedManualy()) {%>
<div class="explore-info-message">
    <div class="column small-24 large-centered large-20">
        <p>We're preparing to release exciting new and refreshed content for the 2020-2021 Girl Scout year! Because we've extended the amount of time you have access to the Volunteer Toolkit this year, we've had to make a few adjustments to how the system works from now through the end of July. Here's what's changed:</p>
        <ul>
            <li>The VTK 'Year Plan' runs only until June 30, but our new content isn't quite ready yet. So until July 21, you won't be able to reset, or add meetings, but you can access any Meetings you already have in your Year Plan.</li>
            <li>Troop members change on July 1, so you'll see your registered members for 2021 on the My Troop Tab, but you won't be able to track attendance or achievements until July 21.</li>
            <li>You still have access to all your current meetings, and Meeting Plan resources, as well as your Finance Tab until we roll out new content and archive the system on July 21.</li>
        </ul>
        <p>So much has happened this year, and we want to give you as much access as possible through the end of July.</p>
        <p>If you have questions, please reach out to your Customer Care team.</p>
    </div>
</div>
<%
} else {
    String level = selectedTroop.getSfTroopAge().substring(selectedTroop.getSfTroopAge().indexOf("-") + 1).toLowerCase();
    if ((selectedTroop.getIsSUM() && "DP".equals(selectedTroop.getRole())) || selectedTroop.getIsIRM()) {
        if (selectedTroop.getYearPlan() != null && selectedTroop.getYearPlan().getRefId() != null) {
            String refId = selectedTroop.getYearPlan().getRefId();
            if (!refId.isEmpty()) {
                level = refId.substring(refId.lastIndexOf("/"));
                String regex = "\\/content\\/girlscouts-vtk\\/yearPlanTemplates\\/(library|yearplan20\\d\\d)\\/(.*)\\/yearPlan.*";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(refId);
                if (matcher.find()) {
                    level = matcher.group(2);
                }
            }
        }
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
    var ________defaultGradeLevel________ = "<%=level %>";
    var ________isSUM________ = <%=selectedTroop.getIsSUM() && "DP".equals(selectedTroop.getRole()) %>;
    var ________isIRM________ = <%= (selectedTroop.getParticipationCode() != null && selectedTroop.getIsIRM()) %>;

    function exploreSelectGradeLevel($el) {
        var selectedGradeLevel = $el.data('grade-level');
        try {
            $("#explore-close-preview").click();
        } catch (error) {
        }
        $("li.grade-level").each(function (index) {
            if ($(this).find('a').data('grade-level') == selectedGradeLevel) {
                $(this).addClass("selected");
            } else {
                $(this).removeClass("selected");
            }
        });
        $('#vtk-yp-main').data('level', selectedGradeLevel);
        startVtkYpApp();
    }

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
<%}%>