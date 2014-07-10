<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*, org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<%!
	boolean showVtkNav = true;
        String activeTab = "resource";
%>
<%
        String meetingPath = request.getParameter("mpath");
        if( meetingPath==null || meetingPath.equals("null") || meetingPath.equals("")) meetingPath=null;

        if(meetingPath != null){
                showVtkNav =  false;
        }
%>

<%@include file="include/vtk-nav.jsp"%>
<div class="tabs-content">
    <div class="content" id="panel2-1"></div>
    <div class="content" id="panel2-2"></div>
    <div class="content" id="panel2-3"></div>
    <div class="content" id="panel2-4"></div>
    <div class="content" id="panel2-5"></div>
</div>

<%
	YearPlanDAO yearPlanDAO = sling.getService(YearPlanDAO.class);
	MeetingDAO meetingDAO = sling.getService(MeetingDAO.class);
	//java.util.List< Meeting> meetings=  meetingDAO.search();
	java.util.List<Meeting> meetings =null;
	
	String ageLevel=  user.getTroop().getGradeLevel();
    ageLevel= ageLevel.substring( ageLevel.indexOf("-")+1).toLowerCase().trim();

	java.util.Iterator<YearPlan> yearPlans =  yearPlanDAO.getAllYearPlans(ageLevel).listIterator();
	
	String find= request.getParameter("ypname");
	if( find==null || find.trim().equals("")){find = user.getYearPlan().getName();}
	find= find.trim();
	
	%><h1><%=user.getYearPlan().getName()%></h1><%
	while (yearPlans.hasNext()) {
		
		YearPlan yearPlan = yearPlans.next();
		
		
		//System.err.println("SAME? "+ find.trim().equals(yearPlan.getName().trim())+" : " +yearPlan.getName() +" find: "+ find);
			
		if( find.equals(yearPlan.getName().trim()) ){
			
			%><br/>:: <%=yearPlan.getName()%>  <% 
			if( yearPlan.getName().trim().equals(user.getYearPlan().getName().trim() ) ){%> CURRENT<% }
			
			java.util.List<MeetingE> meetingEs = meetingDAO.getAllEventMeetings_byPath( yearPlan.getPath() +"/meetings/" );
			
			//System.err.println("Size me: "+ meetingEs.size());
			meetings = new java.util.ArrayList();
			for(int i=0;i<meetingEs.size();i++){
				meetings.add(  meetingDAO.getMeeting(  meetingEs.get(i).getRefId() ) );
			}
		}else{
			String url ="?ypname="+java.net.URLEncoder.encode(yearPlan.getName());
			url+= request.getParameter("xx")==null ? "" : "&xx="+java.net.URLEncoder.encode(request.getParameter("xx"))  ;
			url+= request.getParameter("mpath")==null ? "" : "&mpath="+java.net.URLEncoder.encode(request.getParameter("mpath"));
			
			
			%><br/>
			
			 <a href="/content/girlscouts-vtk/en/vtk.meetingLibrary.html<%=url%>"><%=yearPlan.getName()%></a> <% 
					if( yearPlan.getName().trim().equals(user.getYearPlan().getName().trim() ) ){%> CURRENT<% }
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	java.util.List<String> myMeetingIds= new java.util.ArrayList();
	java.util.List<MeetingE> myMeetings = user.getYearPlan().getMeetingEvents();
	
	if( find.equals(user.getYearPlan().getName().trim() ) )
	  for(int i=0;i< myMeetings.size();i++){
		
		if( myMeetings.get(i).getCancelled()!=null && myMeetings.get(i).getCancelled().equals("true")) continue;
		
		String meetingId = myMeetings.get(i).getRefId();
		meetingId= meetingId.substring(meetingId.lastIndexOf("/") +1).trim().toLowerCase();
		myMeetingIds.add( meetingId );
	}

%>

<script>
function cngMeeting(mPath){
	
	$( "#cngMeet" ).load( "/content/girlscouts-vtk/controllers/vtk.controller.html?<%=meetingPath ==null ? "addMeeting" : "cngMeeting"%>=true&fromPath=<%=meetingPath%>&toPath="+mPath,function( html ) {
		 
		 
		 	 <%if( request.getParameter("xx") ==null ){//meetingPath==null){%>
				    document.location="/content/girlscouts-vtk/en/vtk.plan.html";
				 <%}else{%>
				 	document.location="/content/girlscouts-vtk/en/vtk.planView.html?elem=<%=request.getParameter("xx")%>";
				 <%}%>
	  });
}
</script>
<div id="cngMeet"></div>
<hr/>
 <a href="<%= meetingPath==null ? "/content/girlscouts-vtk/en/vtk.plan.html" : "/content/girlscouts-vtk/en/vtk.planView.html"%>">exit meeting library</a>
<div style="background-color:gray;">Meeting Library</div>
<p>Browse meetings, and select them to review the details</p>
<div>
<%
	for(int i=0;i<meetings.size();i++){
		Meeting meeting = meetings.get(i);
		%> <div style="border:1px solid #000;">
			<div>#<%=i+1 %></div>
			<div><%=meeting.getName()%></div>
			<%=meeting.getBlurb() %>


			<%  if( !myMeetingIds.contains( meeting.getId().trim().toLowerCase()) ){ %>
				
				<input type="button" value="select this plan" onclick="cngMeeting('<%=meeting.getPath()%>')"/>
			
			<% }else{%>
			
				<span style="background-color:red;">EXISTING MEETING</span>
			<% }%>



    <%=meeting.getAidTags() %>
			</div>
	    <% 
	}
%>
</div>


