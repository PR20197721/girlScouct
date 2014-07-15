<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*, org.girlscouts.vtk.ejb.*" %>

<br/><br/>To Start choose plan
<div style="background-color:gray; height:20px; color:#FFF;">
	Year Plan Library
</div>

<%


	String confMsg="TEST";
	if( user.getYearPlan()!=null ){
		if( user.getYearPlan().getIsAltered()  &&  
				( isDtMeetings(user.getYearPlan().getSchedule(), 1) || user.getYearPlan().getSchedule()==null ) )
			{confMsg ="Are You Sure? You will lose customizations that you have made";confMsg+= isDtMeetings(user.getYearPlan().getSchedule(), 1) ;}
		else if( isDtMeetings(user.getYearPlan().getSchedule(), 0))
			{confMsg ="Are You Sure? This will modify plans on /after [date]. Any customization for meeting(s) will be lost.";confMsg+=isDtMeetings(user.getYearPlan().getSchedule(), 0); }
	}
	
	java.util.Iterator <YearPlan>yearPlans =yearPlanDAO.getAllYearPlans( request.getParameter("ageLevel")).listIterator();

	while(yearPlans.hasNext()){
		YearPlan yearPlan = yearPlans.next();
		%>
		<div>
			<input type="submit" name="" value="*** <%=yearPlan.getName()%>" onclick="x('<%=yearPlan.getId()%>', '<%=yearPlan.getPath()%>', '<%=confMsg%>') "/> <%=yearPlan.getDesc() %>
		
		</div>
		<% 
	}
	
%>


<%!

public boolean isDtMeetings(Cal cal, int x){
	
	String dates= cal.getDates();
	java.util.StringTokenizer t= new java.util.StringTokenizer(dates, ",");
	if( x==0 ){
		while( t.hasMoreElements() )
			if( new java.util.Date().before( new java.util.Date(t.nextToken()) ) )
				return true;
		
	}else{
		while( t.hasMoreElements() )
			if( new java.util.Date().after( new java.util.Date(t.nextToken()) ) )
				return true;
		
	}
}

%>
