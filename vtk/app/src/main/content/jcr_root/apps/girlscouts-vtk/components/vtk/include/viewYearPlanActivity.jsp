<%
Activity activity = (Activity) _comp;
%>
<br/>
<div class="caca row meetingDetailHeader">
        <div class="small-2 columns previous">
<%if( prevDate!=0 ){ %>
                <a class="direction" href="/content/girlscouts-vtk/en/vtk.planView.html?elem=<%=prevDate%>"><img width="40" height="100" src="/etc/designs/girlscouts-vtk/clientlibs/css/images/previous.png"/></a>
<%} %>
        </div>
        <div class="small-4 columns">
                <div class="planSquare">
        <%if( user.getYearPlan().getSchedule()!=null ) {%>
                        <%=fmt.format(searchDate) %>
        <%}else{ out.println( fmtX.format(searchDate) ); } %>
                </div>
        </div>
        <div class="small-2 columns next">
<%if( nextDate!=0 ){ %>
                <a class="direction" href="/content/girlscouts-vtk/en/vtk.planView.html?elem=<%=nextDate%>"><img width="40" height="100" src="/etc/designs/girlscouts-vtk/clientlibs/css/images/next.png"/></a>
<%} %>
        </div>
        <div class="small-12 columns">
                <h1>Activity: <%= activity.getName() %></h1>

<br/><br/>Date: <%=fmtDate.format(activity.getDate()) %>
<br/><br/>Time: <%=fmtHr.format(activity.getDate()) %> - <%= fmtHr.format(activity.getEndDate()) %>


<%
String ageLevel=  user.getTroop().getGradeLevel();
ageLevel= ageLevel.substring( ageLevel.indexOf("-")+1);
ageLevel=ageLevel.toLowerCase().trim();
%>
<br/><br/>Age range: <%= ageLevel%>
<br/><br/>Location:
<%= activity.getLocationName() %>
<%=activity.getLocationAddress()%>


<br/><br/>Cost:

<div style="background-color:#efefef"><%=activity.getContent()%></div>

        </div>
        <div class="small-4 columns">
<%if( activity.getDate().after( new java.util.Date())){ %>
<input type="button" value="delete this activity" onclick="rmCustActivity12('<%=activity.getPath()%>')"/>
<%} %>
        </div>
</div>

