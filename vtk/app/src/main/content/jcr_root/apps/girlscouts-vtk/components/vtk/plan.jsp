
 <%@ page import="org.girlsscout.vtk.models.user.*, org.girlsscout.vtk.models.*,org.girlsscout.vtk.dao.*,org.girlsscout.vtk.ejb.*" %>
<%
User user= (User)session.getValue("VTK_user");
if( user ==null){
	user= new UserDAOImpl().getUser( request.getParameter("userId"));
	session.putValue("VTK_user", user);
}
%>
<%@include file="include/headerDev.jsi" %>     
<script type="text/javascript" src="js/vtk/plan.js"></script>        
</head>
<body>


<div id="errInfo" title="tmp"> </div>
<div id="newActivity" title="New Activity"> </div>
<div id="newLocationCal" title="New Location & Calendar"> </div>
<div style="background-color:#FFF;">
                
   <h1>Welcome user:<%=user.getId() %></h1>
               
<dl class="tabs" data-tab >
  <dd ><a href="#panel2-1">My Troup</a></dd>
  <dd class="active"><a href="#panel2-2">Year Plan</a></dd>
  <dd><a href="planView.jsp" >Meeting Plan</a></dd>
  <dd><a href="#panel2-4">Resources</a></dd>
  <dd><a href="#panel2-5">Community</a></dd>
</dl>
<div class="tabs-content">
  <div class="content" id="panel2-1">
    <p>First panel content goes here...
   
    <a href="javascript:void(0)" onclick="x()">test</a>
    
    
    
    </p>
  </div>
  <div class="content active" id="panel2-2">
  <a href="javascript:void(0)" onclick="newActivity()">Add Activity</a> ||
<a href="javascript:void(0)" onclick="newLocCal()">Specify Meeting Dates and Locations</a> ||
<a href="meetingLibrary.jsp" >Add Meeting From Library</a> 
  <div style="color:#FFF; background-color:gray;">Year Plan LIBRARY<a href="javascript:void(0)" onclick="yesPlan()">reveal</a></div>
  <% if(user.getYearPlan()!=null){%>
  	<div id="div2" style="display:none;">
  <%}else{ %>
  	<div id="div2" >
  <%} %>


						<br /> <br />To Start choose plan
						<div style="background-color: gray; height: 20px; color: #FFF;">
							Year Plan Library</div>

						<%
							YearPlanDAO yearPlanDAO = new YearPlanDAOImpl();
							java.util.Iterator<YearPlan> yearPlans = yearPlanDAO
									.getAllYearPlans(request.getParameter("ageLevel"))
									.listIterator();

							while (yearPlans.hasNext()) {
								YearPlan yearPlan = yearPlans.next();
						%>
						<div>
							<input type="submit" name="" value="<%=yearPlan.getName()%>"
								onclick="x('<%=yearPlan.getId()%>', '<%=yearPlan.getPath()%>')" />
							<%=yearPlan.getDesc()%>-<%=yearPlan.getId()%>

						</div>
							<%}%>


	</div>
    
    
    
  <div id="div1">
  <% if(user.getYearPlan()!=null){%>
	<script>loadMeetings()</script>
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
<%@include file="include/footer.jsi" %>