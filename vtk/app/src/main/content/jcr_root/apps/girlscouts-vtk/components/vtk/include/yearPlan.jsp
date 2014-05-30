<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*, org.girlscouts.vtk.ejb.*" %>



<br/><br/>To Start choose plan
<div style="background-color:gray; height:20px; color:#FFF;">
	Year Plan Library
</div>

<%
	YearPlanDAO yearPlanDAO = sling.getService(YearPlanDAO.class);

	YearPlanDAO yearPlanDAO = new YearPlanDAOImpl();
	java.util.Iterator <YearPlan>yearPlans =yearPlanDAO.getAllYearPlans( request.getParameter("ageLevel")).listIterator();

	while(yearPlans.hasNext()){
		YearPlan yearPlan = yearPlans.next();
		%>
		<div>
			<input type="submit" name="" value="<%=yearPlan.getName()%>" onclick="x('<%=yearPlan.getId()%>', '<%=yearPlan.getPath()%>')"/> <%=yearPlan.getDesc() %>-<%=yearPlan.getId()%>
		
		</div>
		<% 
	}
	
%>
