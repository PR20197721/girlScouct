<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<div style="background-color:yellow;">

	<div style="float:left; background-color:blue; color:#FFF;"><%= activity.getDate()%></div>
	<h1><%=activity.getName() %></h1>
	<p>
		<%=activity.getContent() %>
	</p>
	
	<br/>Date: <%=dateFormat1.format(activity.getDate()) %>
	<br/>Time: <%=dateFormat2.format(activity.getDate()) %>
	<br/>Age Range:
	<br/>Location:
	<br/>Cost:
	<div id="addExistActivity_err_<%=activity.getId()%>" style="color:red;"></div>
	<input type="button" value="Add Activity" onclick="addExistActivity('<%=activity.getId()%>')"/>
	
</div>
