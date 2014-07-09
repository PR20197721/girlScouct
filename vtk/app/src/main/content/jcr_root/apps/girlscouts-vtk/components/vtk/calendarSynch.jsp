
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<% 
response.setHeader ("Content-Disposition", "attachment;filename=\"mycalendar.ics\"");
response.setContentType("text/calendar");
%>
<%@page import="java.util.Iterator,org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>




<%
MeetingDAO meetingDAO = sling.getService(MeetingDAO.class);
net.fortuna.ical4j.model.Calendar calendar = meetingDAO.yearPlanCal(user );




/*
java.io.FileOutputStream fout = new java.io.FileOutputStream("mycalendar.ics");
CalendarOutputter outputter = new CalendarOutputter();
outputter.output(calendar, fout);
*/

ServletOutputStream fout = response.getOutputStream();
net.fortuna.ical4j.data.CalendarOutputter outputter = new net.fortuna.ical4j.data.CalendarOutputter();
outputter.output(calendar, fout);
fout.flush();
%>

