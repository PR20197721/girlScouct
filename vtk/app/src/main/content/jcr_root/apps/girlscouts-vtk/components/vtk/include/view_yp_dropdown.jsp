<%@ page import="java.util.regex.Pattern, java.util.regex.Matcher" %><%
    String level = selectedTroop.getSfTroopAge().substring(selectedTroop.getSfTroopAge().indexOf("-") + 1).toLowerCase();
    if("SUM".equals(selectedTroop.getCouncilCode()) || (selectedTroop.getParticipationCode() != null && "IRM".equals(selectedTroop.getParticipationCode()))){
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
<%--
<section class="grade-levels">
    <div class="__padding">
        <div class="columns small-22 medium-20 small-centered medium-centered" style="padding: 0px;">
            <div class="row">
                <div class="small-24  columns">
                    <p style="margin-bottom: 30px;">Select a level you would like to see.</p>
                </div>
            </div>
        </div>
        <div class="columns small-22 medium-20 small-centered medium-centered" style="padding: 0px;">
            <ul>
                <li class="grade-level daisy<%="daisy".equals(level)? " selected": ""%>"><a onclick="exploreSelectGradeLevel($(this))" data-grade-level="daisy">Daisy</a></li>
                <li class="grade-level brownie<%="brownie".equals(level)? " selected": ""%>"><a onclick="exploreSelectGradeLevel($(this))" data-grade-level="brownie">Brownie</a></li>
                <li class="grade-level junior<%="junior".equals(level)? " selected": ""%>"><a onclick="exploreSelectGradeLevel($(this))" data-grade-level="junior">Junior</a></li>
                <li class="grade-level cadette<%="cadette".equals(level)? " selected": ""%>"><a onclick="exploreSelectGradeLevel($(this))" data-grade-level="cadette" >Cadette</a></li>
                <li class="grade-level senior<%="senior".equals(level)? " selected": ""%>"><a onclick="exploreSelectGradeLevel($(this))" data-grade-level="senior" >Senior</a></li>
                <li class="grade-level ambassador<%="ambassador".equals(level)? " selected": ""%>"><a onclick="exploreSelectGradeLevel($(this))" data-grade-level="ambassador" >Ambassador</a></li>
                <li class="grade-level multi-level<%="multi-level".equals(level)? " selected": ""%>"><a onclick="exploreSelectGradeLevel($(this))" data-grade-level="multi-level" >Multi-level</a></li>
            </ul>
            <div style="clear: both"></div>
        </div>
    </div>
</section>
--%>
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
    var ________defaultGradeLevel________ = "<%=level %>";
    var ________isSUM________ = <%="SUM".equals(selectedTroop.getCouncilCode()) %>;
    var ________isIRM________ = <%= (selectedTroop.getParticipationCode() != null && "IRM".equals(selectedTroop.getParticipationCode())) %>;
    function exploreSelectGradeLevel($el) {
        var selectedGradeLevel = $el.data('grade-level');
        try{
            $("#explore-close-preview").click();
        }catch(error){
        }
        $( "li.grade-level" ).each(function( index ) {
            if($(this).find('a').data('grade-level') == selectedGradeLevel){
                $( this ).addClass( "selected" );
            }else{
                $( this ).removeClass( "selected" );
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