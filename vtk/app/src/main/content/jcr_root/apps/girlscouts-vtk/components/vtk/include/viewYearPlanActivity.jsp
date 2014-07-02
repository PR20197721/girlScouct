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


<br/><br/>Cost:<%=activity.getCost() %>

<div style="background-color:#efefef"><%=activity.getContent()%></div>

        </div>
        <div class="small-4 columns">
<%if( activity.getDate().after( new java.util.Date())){ %>
<input type="button" value="delete this activity" onclick="rmCustActivity12('<%=activity.getPath()%>')"/>
<%} %>
        </div>
        
        
        
        
        
        
       <div style="background-color:lightblue;">
        	<h4>Upload File**</h4>
        		<%String aassetId = new java.util.Date().getTime() +"_"+ Math.random(); %>
    
   
  
              <form action="/vtk/<%=user.getTroop().getCouncilCode()%>/<%=user.getTroop().getTroopName() %>/assets/<%=aassetId %>" method="post"
                       onsubmit="return bindAssetToYPC( '/vtk/<%=user.getTroop().getCouncilCode()%>/<%=user.getTroop().getTroopName() %>/assets/<%=aassetId %>/custasset', '<%=activity.getUid() %>' )"   enctype="multipart/form-data">
                       
               <input type="hidden" name="id" value="<%=aassetId%>"/>      
               <input type="hidden" name="owner" value="<%=user.getId()%>"/>
               <input type="hidden" name="createTime" value="<%=new java.util.Date()%>"/>         
			   <input type="file" name="custasset" size="50" />
               <br />
                <input type="submit" value="Upload File" />
         </form>
        </div>
        
        
        
     
     <ul style="background-color:red;">
     <% 
     List<Asset> a_aidTags = activity.getAssets();

if( a_aidTags!=null )
 for(int i=0;i<a_aidTags.size();i++){
	%><li> <%=a_aidTags.get(i).getType()%> <a href="<%=a_aidTags.get(i).getRefId()%>"><%=a_aidTags.get(i).getRefId()%></a> </li><% 
 }
     %>
     </ul>
     
     
     
     
     
     
        
</div>

<a href="javascript:void(0)" onclick="openClose('editCustActiv')">EDIT ACTIVITY</a>
<div id="editCustActiv" style="background-color:pink; display:none;">

<form>
	
	<h2>Edit Activity</h2>
	<a class="closeText" href="#" onclick="$('#editCustActiv').dialog('close')">Return to Plan</a>
	<div class="sectionBar">Edit Custom Activity</div>
	<div id="newCustActivity_err" style="color:red;"></div>
        <div class="row">
                <div class="small-6 columns">
			<font color="red">*</font> <input type="text" id="newCustActivity_name" value="<%=activity.getName() %>" style="width:200px;" placeholder="Name of Activity"/>
		</div>
                <div class="small-6 columns">
			Date: ex:05/07/2014<input type="text"  id="newCustActivity_date" value="<%=fmtDate.format(activity.getDate()) %>" style="width:160px;"/>
                </div>  
                <div class="small-6 columns">
			Start Time: ex: 18:15<input type="text" id="newCustActivity_startTime" value="<%= fmtHr1.format(activity.getDate())%>" style="width:100px;" />
			<select id="newCustActivity_startTime_AP">
			 
			 <option value="am" <%=  dateFormat41.format(activity.getDate()).toUpperCase().trim().equals("AM") ? "SELECTED" : "" %>>am</option>
			<option value="pm" <%=  dateFormat41.format(activity.getDate()).toUpperCase().trim().equals("PM") ? "SELECTED" : "" %>>pm</option>
			 </select> </div>  
                <div class="small-6 columns">
			End Time: ex: 09:10<input type="text" id="newCustActivity_endTime" value="<%=fmtHr1.format(activity.getEndDate()) %>"  style="width:100px;"/>
			<select id="newCustActivity_endTime_AP">
			<option value="am" <%=  dateFormat41.format(activity.getEndDate()).toUpperCase().trim().equals("AM") ? "SELECTED" : "" %>>am</option>
			<option value="pm" <%=  dateFormat41.format(activity.getEndDate()).toUpperCase().trim().equals("PM") ? "SELECTED" : "" %>>pm</option></select>
                </div> 
	</div>
        <div class="row">
                <div class="small-12 columns">
			Location Name <input type="text" id="newCustActivity_locName" value="<%=activity.getLocationName() %>" style="width:100px;"/>
		</div>
                <div class="small-12 columns">
			Location Address <input type="text" id="newCustActivity_locAddr" value="<%=activity.getLocationAddress() %>" style="width:100px;"/>
                </div>
	</div>
        <div class="row">
                <div class="small-16 columns">
			<textarea id="newCustActivity_txt" rows="4" cols="5" " style="width:300px;"><%=activity.getContent() %></textarea>
                </div>
                <div class="small-8 columns">
                
                
              <div style="background-color:red;">Cost: <input type="text" id="newCustActivity_cost" value="<%=activity.getCost()%>"/></div>
			<input type="button" value="Edit Activity" id="newCustActivity" onclick="editNewCustActivity('<%=activity.getUid()%>')"/>
                </div>
        </div>
	 </form>

</div>
