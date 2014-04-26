<%@ page import="java.text.Format,
	java.text.ParseException,
	java.lang.Exception,
	java.text.SimpleDateFormat,
	java.util.Date"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />

<%
   DateFormat fromFormat = new SimpleDateFormat("mm/dd/yy");
   DateFormat toFormat = new SimpleDateFormat("mm/dd/yy");

   String fromdate = properties.get("fromdate","");
   String todate = properties.get("todate","");
   String stamp = properties.get("stamp", "");
   String lastModified = properties.get("jcr:lastModified", "");
   String start = properties.get("start", "");
   String end = properties.get("end", "");
   String created = properties.get("jcr:created", "");

   //String date = properties.get("date",String.class);
   String details = properties.get("details"," ");
   String title = properties.get("title","");
   String time = properties.get("time", "");
   String location = properties.get("location", "");
   String fileReference = properties.get("fileReference", "");
   String imgWidth = properties.get("width", "");
   String imgHeight = properties.get("height", "");
   String imgAlt = properties.get("alt", "");
   String imgTitle = properties.get("imgtitle", "");
   String url = properties.get("url", "");
   String isDate = properties.get("isDate", "");
   String jcrTitle = properties.get("jcr:title", "");
   String sequence = properties.get("sequence", "");
   String uid = properties.get("uid", "");
   String lastModifiedBy = properties.get("jcr:lastModifiedby", "");
   String createdBy = properties.get("jcr:createdBy", "");
   String primaryType = properties.get("jcr:primaryType", "");
   String distribute = properties.get("cq:distribute", "");

   stamp = stamp.replace("T", " ");
   lastModified = lastModified.replace("T", " ");
   created = created.replace("T", " ");

   Page pg=null;

   if(time!=null && time.length() > 0)
	    time=xssAPI.filterHTML(time);


    if(details!=null && details.length()>0)
    	details = xssAPI.filterHTML(details);

//if(location!=null && location.length()>0){
//  	pg =  resourceResolver.getResource(location).adaptTo(Page.class);

//    }

    %>
<style>
h1 {
	color: #000000
}
</style>
<div id="tl">
	<h1><%=jcrTitle%></h1>
</div>
<%if(fileReference!=null && fileReference.length()>0){ %>
<img src="<%=fileReference%>"
	alt="<%if(imgAlt!=null && imgAlt.length()>0){%><%=imgAlt%><%}%>"
	width=<%=imgWidth%> height="<%=imgHeight%>" />

<%} %>
<p>
	<%if(time!=null && time.length()>0){ %>
	<br /> <Strong>Time: </Strong><%=time%>
	<%} %>
	<%if(fromdate!=null && fromdate.length() > 0){
	  Date fdt = fromFormat.parse(fromdate);

	%>
	<br />
	<Strong>Date:</Strong>
	<%= toFormat.format(fdt) %>
	<%} %>
	<%if(todate!=null && todate.length() > 0){
	 Date tdt = fromFormat.parse(todate);
	%>
	to
	<%=toFormat.format(tdt) %>
	<%} %>
	<%if(location!=null && location.length() >0){ %>
	<br /> <Strong>Location: </Strong><%=location%>
	<%} %>
	<%if(details!=null && details.length() >0){ %>
	<br />
	<%=details %>
	<%} %>
	<br>
</p>
