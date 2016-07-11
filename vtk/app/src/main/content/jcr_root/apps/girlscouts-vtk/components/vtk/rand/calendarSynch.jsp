<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig,  org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<% 
response.setHeader ("Content-Disposition", "attachment;filename=\"mycalendar.ics\"");
response.setContentType("text/calendar");
%>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>

<%
//-MeetingDAO meetingDAO = sling.getService(MeetingDAO.class);
net.fortuna.ical4j.model.Calendar calendar = yearPlanUtil.yearPlanCal(user );

ServletOutputStream fout = response.getOutputStream();
net.fortuna.ical4j.data.CalendarOutputter outputter = new net.fortuna.ical4j.data.CalendarOutputter();
outputter.output(calendar, fout);
fout.flush();
%>

