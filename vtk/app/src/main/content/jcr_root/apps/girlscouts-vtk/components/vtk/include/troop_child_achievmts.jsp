<%@ page import="org.girlscouts.vtk.osgi.component.dao.YearPlanComponentType" %>
<div class="row">
    <div class="column large-20 large-centered">
        <div class="row">
            <dl class="accordion-inner clearfix" data-accordion>
                <ul class="inline-list">
                    <% for (int y = 0; y < infos.size(); y++) {
                        if (infos.get(y).isAchievement() && infos.get(y).getYearPlanComponent().getType() == YearPlanComponentType.MEETING) {
                    %>
                    <li><img
                            src="/content/girlscouts-vtk/service/meeting/icon.<%= ((MeetingE) infos.get(y).getYearPlanComponent()).getMeetingInfo().getId()%>.png"
                            alt="imagetitle"/>
                        <% String achievementName = ((MeetingE) infos.get(y).getYearPlanComponent()).getMeetingInfo().getName();
                            achievementName = achievementName.replaceAll("\\d+$", "");
                        %>
                        <p><%= achievementName %></p>
                        <p><%=VtkUtil.formatDate(VtkUtil.FORMAT_MMddYYYY, (java.util.Date) ((MeetingE) infos.get(y).getYearPlanComponent()).getAchievement().getCreatedDate().getTime())%></p>
                    </li>
                    <%
                            }
                        }
                    %>
                </ul>
            </dl>
        </div>
    </div>
</div>