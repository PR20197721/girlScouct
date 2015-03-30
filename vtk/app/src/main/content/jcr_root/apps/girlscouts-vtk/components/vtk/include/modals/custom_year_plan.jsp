<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="../session.jsp"%>

<%

String ageLevel=  troop.getTroop().getGradeLevel();
ageLevel= ageLevel.substring( ageLevel.indexOf("-")+1).toLowerCase().trim();
java.util.List<Meeting> meetings =yearPlanUtil.getAllMeetings(user, ageLevel);
%>

 <link rel="stylesheet" href="//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
  <script src="//code.jquery.com/jquery-1.10.2.js"></script>
  <script src="//code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
  <link rel="stylesheet" href="/resources/demos/style.css">
  <style>
  #sortable1, #sortable2 {
    border: 1px solid #eee;
    width: 142px;
    min-height: 20px;
    list-style-type: none;
    margin: 0;
    padding: 5px 0 0 0;
    float: left;
    margin-right: 10px;
  }
  #sortable1 li, #sortable2 li {
    margin: 0 5px 5px 5px;
    padding: 5px;
    font-size: 1.2em;
    width: 120px;
  }
  </style>
  <script>
  $(function() {
    $( "#sortable1, #sortable2" ).sortable({
      connectWith: ".connectedSortable",
      stop : function(event, ui) {
    	 
    	  var sortedIDs = $( "#sortable2" ).sortable( "toArray" );
    	  console.log("New rpt: "+sortedIDs); 
      }
    
    }).disableSelection();
  });
  
  </script>
</head>
<body>
 
<ul id="sortable1" class="connectedSortable">
  
  <c:forEach var="i" begin="1" end="5">

   Item <c:out value="${i}"/><p>

  </c:forEach

<% for(int i=0;i<meetings.size();i++){ %>
    <li class="ui-state-default" id="<%=meetings.get(i).getId()%>"><%=meetings.get(i).getName() %></li>
<% } %>
</ul>
 
<ul id="sortable2" class="connectedSortable">
  
</ul>
 
 
</body>
</html>