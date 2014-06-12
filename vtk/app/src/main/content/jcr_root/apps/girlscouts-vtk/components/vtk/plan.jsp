
 <%@ page import="org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%


HttpSession session = request.getSession();
ApiConfig apiConfig =null;
	if( session.getAttribute(org.girlscouts.vtk.auth.models.ApiConfig.class.getName())!=null ){
	   apiConfig = ((org.girlscouts.vtk.auth.models.ApiConfig)session.getAttribute(org.girlscouts.vtk.auth.models.ApiConfig.class.getName()));
	
		
	}else{
		
		out.println("loging first");
		return;
	}



YearPlanDAO yearPlanDAO = sling.getService(YearPlanDAO.class);
UserDAO userDAO = sling.getService(UserDAO.class);

//HttpSession session = request.getSession();

User user= (User)session.getValue("VTK_user");
if( user ==null){
	//user= userDAO.getUser( request.getParameter("userId"));
	user= userDAO.getUser( apiConfig.getUserId() );
	
	//first time - new user
	if( user==null ){
//System.err.println("User is null.Setting new "+ apiConfig.getUserId());		
		user = new User(apiConfig.getUserId());
		//user.setId( apiConfig.getUserId());
	}
	
	
	session.putValue("VTK_user", user);

}
%>
</head>
<body>


<div id="errInfo" title="tmp"> </div>
<div id="newActivity" title="New Activity"> </div>
<div id="newLocationCal" title="New Location & Calendar"> </div>
<div style="background-color:#FFF;">
                
  
               
<dl class="tabs" data-tab >
  <dd ><a href="#panel2-1">My Troup</a></dd>
  <dd class="active"><a href="#panel2-2">Year Plan</a></dd>
  
  
  <dd>
  <%if( user.getYearPlan()!=null ){ %>
  	<a href="/content/girlscouts-vtk/en/vtk.planView.html" >Meeting Plan</a>
  <%}else{%> <a href="#">Meeting Plan</a> <%} %>
  </dd>
  
  
  
  <dd><a href="/content/girlscouts-vtk/en/vtk.resource.html">Resources</a></dd>
  <dd><a href="#panel2-5">Community</a></dd>
</dl>
<div class="tabs-content">
  <div class="content" id="panel2-1">
    <p>First panel content goes here...
   
    <a href="javascript:void(0)" onclick="x()">test</a>
    
    
    
    </p>
  </div>
  <div class="content active" id="panel2-2">
  
<% if( user.getYearPlan()!=null){ %> 
	<a href="javascript:void(0)" onclick="newActivity()">Add Activity</a> ||
	<a href="javascript:void(0)" onclick="newLocCal()">Specify Meeting Dates and Locations</a> ||
	<a href="/content/girlscouts-vtk/en/vtk.meetingLibrary.html" >Add Meeting From Library</a> 
<%} %>

  <div style="color:#FFF; background-color:gray;">Year Plan LIBRARY
  	<% if(user.getYearPlan()!=null){%><a href="javascript:void(0)" onclick="yesPlan()">reveal</a><%} %>
  </div>
  <% if(user.getYearPlan()!=null){%>
  	<div id="div2" style="display:none;">
  <%}else{ %>
  	<div id="div2" >
  <%} %>


						<br /> <br />To start planning your year, select a Year Plan
						<a href="javascript:void(0)" id="plan_hlp_hrf">help</a>
						<div style="background-color: gray; height: 20px; color: #FFF;">
							Year Plan Library</div>

						<%

							java.util.Iterator<YearPlan> yearPlans = yearPlanDAO
									.getAllYearPlans(apiConfig.getUser().getAgeLevel())
									.listIterator();


							while (yearPlans.hasNext()) {
								YearPlan yearPlan = yearPlans.next();
						%>
						<div>
							<input type="submit" name="" value="<%=yearPlan.getName()%>"
								onclick="x('<%=yearPlan.getId()%>', '<%=yearPlan.getPath()%>')" />
							<%=yearPlan.getDesc()%>-<%=yearPlan.getId()%>

						</div>
							<%}
							
							%>


	</div>
    
    
    
  <div id="div1" style="display:<%=(user.getYearPlan()!=null) ? "block" : "none" %>">
  <% if(user.getYearPlan()!=null){%>
	<script>$(document).ready(function(){loadMeetings();});</script>
  <% } %>
  
  </div>
  
     
     
    </p>
  </div>
  
    
 
  <div class="content" id="panel2-3">
    <p>Third panel content goes here...</p>
  </div>
  <div class="content" id="panel2-4">
    <p>Fourth panel content goes here...</p>
  </div>
  <div class="content" id="panel2-5">
    <p>Fourth panel content goes here...</p>
  </div>
</div>

<br/></br>

    

                </div>
                
                
                <div id="plan_hlp" style="display:none;"><h1>Year Plan Help:</h1><ul><li>asdf></li><li>asdf></li><li>asdf></li></ul></div>