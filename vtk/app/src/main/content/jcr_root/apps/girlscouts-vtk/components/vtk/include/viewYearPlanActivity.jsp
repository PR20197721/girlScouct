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
        
        
        
        <div id="assets">
        
        <ul>
      <% 
	java.util.List<Asset> aassets = activity.getAssets();
	if( aassets!=null)
	 for(int i=0;i< aassets.size(); i++){
		%><li style="background-color:lightblue;"><%=aassets.get(i).getType()%>: <a href="<%=aassets.get(i).getRefId() %>"><%=aassets.get(i).getRefId() %></a></li><a href="javascript:void(0)" onclick="rmAsset('<%=activity.getUid()%>', '<%=aassets.get(i).getUid()%>')" style="background-color:red;">remove</a><% 
	 }
	%>
	</ul>
        
        </div>
        
        
        
        <div>
        	<h4>Upload File</h4>
        	<!--  <form action="/content/dam/girlscouts-vtk/troopLibrary/<%=user.getTroop().getTroopName() %>/" method="post"
                        enctype="multipart/form-data">
                       
                        
                        // user prefs
                        // /vtk/<%=user.getTroop().getCouncilCode()%>/<%=user.getTroop().getTroopName() %>/<%=user.getId() %>
                         -->
       <form action="/vtk/<%=user.getTroop().getCouncilCode()%>/<%=user.getTroop().getTroopName() %>/assets/" method="post"
                        enctype="multipart/form-data">
                       
                        <input type="hidden" name="refId" value="<%=activity.getUid()%>"/>
               <input type="hidden" name="owner" value="<%=user.getId()%>"/>
               <input type="hidden" name="createTime" value="<%=new java.util.Date()%>"/>         
<input type="file" name="custasset" size="50" />
<br />
<input type="submit" value="Upload File" />
</form>
        </div>
        
        <% 
        List<org.girlscouts.vtk.models.Search>  _custassets = sling.getService(MeetingDAO.class).getAidTag_custasset(activity.getUid());
	for(int i=0;i<_custassets.size();i++){
		%> <div style="background-color:yellow;">custasset:<a href="<%=_custassets.get(i).getPath() %>"><%=_custassets.get(i).getPath() %></a></div><%
	}
        %>
        
</div>

