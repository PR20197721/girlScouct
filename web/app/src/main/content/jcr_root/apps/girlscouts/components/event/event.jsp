<%@ page import="java.text.Format,java.text.SimpleDateFormat,java.util.Date,com.day.cq.dam.commons.util.DateParser,java.text.DateFormat"%>

<%@include file="/libs/foundation/global.jsp"%>

<%@page session="false" %>
<cq:defineObjects/>
<%


   DateFormat fromFormat = new SimpleDateFormat("mm/dd/yy");
   DateFormat toFormat = new SimpleDateFormat("EEE dd MMM yyyy");
  
   String fromdate = properties.get("fromdate","");
   String todate = properties.get("todate","");
   
   
   

   //String date = properties.get("date",String.class);
   String details = properties.get("details"," ");
   String time = properties.get("time", " ");
   String location = properties.get("location", "");
   String fileReference = properties.get("fileReference", "");
   String imgWidth = properties.get("width", "");
   String imgHeight = properties.get("height", "");
   String imgAlt = properties.get("alt", "");
   String imgTitle = properties.get("imgtitle", "");
 
   Page pg=null;
   
   if(time!=null && time.length() > 0)
	    time=xssAPI.filterHTML(time);
	
   
    if(details!=null && details.length()>0)
    	details = xssAPI.filterHTML(details);
   
    if(location!=null && location.length()>0){
    	pg =  resourceResolver.getResource(location).adaptTo(Page.class);
       
    }
%>
<%if(time!=null && time.length() >0){ %>
<Strong>Time:</Strong> <%=time %>
<% }if(fromdate!=null && fromdate.length() > 0){
	  Date fdt = fromFormat.parse(fromdate);
	  
	%>

   <br/><Strong>Date :</Strong><%=toFormat.format(fdt) %>
<%} %>
<%if(todate!=null && todate.length() > 0){
	 Date tdt = fromFormat.parse(todate);
	%>
     to <%=toFormat.format(tdt) %>
<%} %>
<%if(location!=null && location.length() >0){ %>
    <br/>  <Strong>Location :</Strong><%=pg.getTitle()%>
<%} %>
<%if(details!=null && details.length() >0){ %>
    <br/> <%=details %>
<%} %>
<%if(fileReference!=null && fileReference.length()>0){ %>
   <img src="<%=fileReference%>" alt="<%if(imgAlt!=null && imgAlt.length()>0){%><%=imgAlt%><%}%>" width=<%=imgWidth%> height="<%=imgHeight%>"/>

<%} %>
