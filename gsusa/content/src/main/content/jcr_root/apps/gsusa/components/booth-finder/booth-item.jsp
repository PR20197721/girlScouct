<%@page import="org.girlscouts.web.gsusa.component.boothfinder.BoothFinder.BoothBasic,
                java.text.DateFormat,
                java.text.SimpleDateFormat" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
BoothBasic booth = (BoothBasic)request.getAttribute("gsusa-booth-list-item");
if (booth != null) {
    DateFormat inputFormat = new SimpleDateFormat("M/d/yyyy");
    DateFormat outputFormat = new SimpleDateFormat("EEEE, MMMM d");
    String startDate = outputFormat.format(inputFormat.parse(booth.dateStart));
%>
    <div>
        <div><%= booth.location %></div>
        <div><%= booth.address1 %></div>
        <div><%= booth.address2 %></div>
    </div>    
    <div>
        <div><%= startDate %></div>
        <div><%= booth.timeOpen %>-<%= booth.timeClose %></div>
    </div>
    <div>
        <div><%= booth.distance %> Miles</div>
    </div>
<% 
}
%>