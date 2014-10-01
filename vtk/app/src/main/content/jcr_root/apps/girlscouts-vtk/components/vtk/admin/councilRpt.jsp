
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@ page import="com.day.cq.wcm.foundation.Search,
org.girlscouts.web.search.DocHit,
com.day.cq.search.eval.JcrPropertyPredicateEvaluator,com.day.cq.search.eval.FulltextPredicateEvaluator,
com.day.cq.tagging.TagManager,
java.util.Locale,com.day.cq.search.QueryBuilder,javax.jcr.Node,
java.util.ResourceBundle,com.day.cq.search.PredicateGroup,
com.day.cq.search.Predicate,com.day.cq.search.result.Hit,
com.day.cq.i18n.I18n,com.day.cq.search.Query,com.day.cq.search.result.SearchResult,
java.util.Map,java.util.HashMap,java.util.List" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="../include/session.jsp"%>

<%@include file="../admin/toolbar.jsp"%>
<h1>Council Report</h1>
 <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.5/angular.min.js"></script>
 <script src="http://code.highcharts.com/highcharts.js"></script>
<script src="https://rawgit.com/pablojim/highcharts-ng/master/src/highcharts-ng.js"></script>
<link rel="stylesheet" type="text/css" href="http://netdna.bootstrapcdn.com/bootstrap/3.0.2/css/bootstrap.min.css">

<%

java.util.Map<String, String> cTrans = new java.util.TreeMap();

cTrans.put("597", "Girl Scouts of Northeast Texas"); 

cTrans.put("477", "Girl Scouts of Minnesota and Wisconsin River Valleys, Inc.");

cTrans.put("465", "Girl Scouts of Southeastern Michigan"); 

cTrans.put("367", "Girl Scouts - North Carolina Coastal Pines, Inc.");

cTrans.put("320", "Girl Scouts of West Central Florida, Inc.");

cTrans.put("388", "Girl Scout Council of the Southern Appalachians, Inc.");

cTrans.put("313", "Girl Scouts of Gateway Council, Inc.");




		javax.jcr.Session s= (slingRequest.getResourceResolver().adaptTo(Session.class));
		
		
		String sql="select  sfTroopAge,jcr:path, sfCouncil,excerpt(.) from nt:base where jcr:path like '/vtk/%' and contains(*, 'org.girlscouts.vtk.models.Troop ') ";
		
		
		javax.jcr.query.QueryManager qm = s.getWorkspace().getQueryManager();
		javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL); 
   		
		int count=0 ;
		
		java.util.List unCouncil= new java.util.ArrayList();
		//java.util.Map containeR= new java.util.HashMap();
	
		org.apache.commons.collections.MultiMap containeR= new org.apache.commons.collections.map.MultiValueMap();
		
		
		
		
		
		javax.jcr.query.QueryResult result = q.execute();
		for (javax.jcr.query.RowIterator it = result.getRows(); it.hasNext(); ) {
			javax.jcr.query.Row r = it.nextRow();
			String path = r.getValue("jcr:path").getString() ;
			String sfCouncil = null, sfTroopAge=null;
			try{ sfCouncil =r.getValue("sfCouncil").getString() ;}catch(Exception e){}
			
			if( sfCouncil ==null ){
				StringTokenizer t= new StringTokenizer( path , "/");
				t.nextToken();
				sfCouncil = t.nextToken();
			}
			
		    try{sfTroopAge= r.getValue("sfTroopAge").getString();}catch(Exception e){}
		    
		    
		    if( sfTroopAge==null){
		    	Node node = r.getNode().getNode("yearPlan/meetingEvents/").getNodes().nextNode();	
		    	
		    	//out.println("** "+ (node==null) );
		    	String refId= node.getProperty("refId").getString();
		    	String planId= refId.substring( refId.lastIndexOf("/") +1).toLowerCase();
		    	
		    	if( planId.startsWith("d"))
		    		sfTroopAge=("1-Daisy");
				else if( planId.startsWith("b"))
					sfTroopAge=("2-Brownie");
				else if( planId.startsWith("j"))
					sfTroopAge=("3-Junior");
		    	
		    }
		    
		    
		    if( !unCouncil.contains(sfCouncil) )
		    	unCouncil.add(sfCouncil);
		    
  		   	containeR.put( sfCouncil,sfTroopAge );
		    
  		   	
  		   	
  		   	
			//out.println("<br/>"+ sfCouncil +" : " +sfTroopAge );
		       count++;
		}
		out.println("Total: "+count);
		%>
		<table>
		<tr>
		<th>Council</th>
		<th>Junior</th>
		<th>Brownie</th>
		<th>Daisy</th>
		
		</tr>
<%
		
		for(int i=0;i<unCouncil.size();i++){
			String council= (String)unCouncil.get(i);
			
			String councilId_str = cTrans.get(council);
			
			java.util.Map <String, Integer> xx= new java.util.TreeMap();
			
			java.util.Iterator itr= containeR.keySet().iterator();
			while( itr.hasNext() ){
				String _council= (String) itr.next();
				java.util.List lvl= (java.util.List)containeR.get( council );
				if( council.equals(_council) ){
					//out.println("YEs --" + lvl);
					for(int y=0;y<lvl.size();y++){
						String l = (String) lvl.get(y);
						if( xx.get(l)==null )
							xx.put(l, 1);
						else
							xx.put(l,  xx.get(l) +1 );
						
					}
					
				}
			}
			
			
			
			%>
				<tr>
				<td><%=councilId_str ==null ? council : councilId_str%></td>
				<td><%= xx.get("3-Junior")%> </td>
				<td><%= xx.get("2-Brownie")%></td>
				<td><%= xx.get("1-Daisy")%></td>
				</tr>
			<%
		}
		out.println("</table>");
		
		
		
		
		
		
		
		
		
		if(true)return;








java.util.List<User> users = userDAO.getUsers();
users = doFix(users);



%>


<script>



var myapp = angular.module('myapp', ["highcharts-ng"]);

myapp.controller('myctrl', function ($scope) {

    $scope.chartConfig = {
        options: {
            chart: {
                type: 'chart',
                zoomType: 'x'
            }
        },
       
      
        
        series: [
                 
                 
        <%
        
        	java.util.Map <String, Map>container = parseData(users);
      
        	
        	out.println("{name: 'Brownie', data:[");
        	java.util.Map <String, Integer>lvl = container.get("2-Brownie");
        	if( lvl!=null)
        	 for(int i=0;i< unqCouncil.size();i++){
        		Integer x = lvl.get( unqCouncil.get(i)  );
        		if( x == null)
        			out.println("0,");
        		else
        			out.println(x+",");
        		
        	 }
        	out.println("]},");
        	
        	
        	
        	out.println("{name: 'Daisy', data:[");
        	lvl = container.get("1-Daisy");
        	if( lvl!=null)
        	  for(int i=0;i< unqCouncil.size();i++){
        		Integer x = lvl.get( unqCouncil.get(i)  );
        		if( x == null)
        			out.println("0,");
        		else
        			out.println(x+",");
        		
        	  }
        	 out.println("]},");
        	 
        	 
        	 out.println("{name: 'Junior', data:[");
         	lvl = container.get("3-Junior");
         	if( lvl!=null)
         	  for(int i=0;i< unqCouncil.size();i++){
         		Integer x = lvl.get( unqCouncil.get(i)  );
         		if( x == null)
         			out.println("0,");
         		else
         			out.println(x+",");
         		
         	  }
         	 out.println("]}");
        	
        	
        	
        %>         
        ],
        
        title: {
            text: ''
        },
        xAxis: {
            categories: <%=unqCouncil%>
        },
        
        loading: false
    }

});


</script>

<div ng-app="myapp">
    <div ng-controller="myctrl">
       
        <div class="row">
            <highchart id="chart1" config="chartConfig" class="span10"></highchart>
        </div>
    </div>
</div>


<table>
<tr>
<th>Council</th>
<th>Brownie</th>
<th>Daisy</th>
<th>Junior</th>
</tr>
<%
/*

java.util.Map<String, String> cTrans = new java.util.TreeMap();

cTrans.put("597", "Girl Scouts of Northeast Texas"); 

cTrans.put("477", "Girl Scouts of Minnesota and Wisconsin River Valleys, Inc.");

cTrans.put("465", "Girl Scouts of Southeastern Michigan"); 

cTrans.put("367", "Girl Scouts - North Carolina Coastal Pines, Inc.");

cTrans.put("320", "Girl Scouts of West Central Florida, Inc.");

cTrans.put("388", "Girl Scout Council of the Southern Appalachians, Inc.");

cTrans.put("313", "Girl Scouts of Gateway Council, Inc.");

*/



	for(int i=0;i<unqCouncil.size();i++){
		
		String councilId= (String) unqCouncil.get(i);
		String councilId_str = cTrans.get(councilId);
		int das = 0, jun=0,brow=0;
		for(int u=0;u< users.size();u++){
			
			 User uu= users.get(u);
			
			 if( uu.getSfCouncil().equals( councilId )){
			 	if( uu.getSfTroopAge().toLowerCase().contains("brownie" ) )
			 		brow ++;
			 	else if( uu.getSfTroopAge().toLowerCase().contains("junior" ) )
			 		jun++;
			 	else if( uu.getSfTroopAge().toLowerCase().contains("daisy" ) )
			 		das++;
			 
			 }
			 
	  	}//edn for
	  	
		 %>
		 <tr>
		 	<td><%=councilId_str ==null ? councilId : councilId_str%></td>
		 	<td><%=brow %></td>
		 	<td><%=das %></td>
		 	<td><%=jun %></td>
		 </tr>
		 <%
		 
		 
	}
	
 out.println("</table>");

	  		
	  			
	  			
	  			/*
	out.println("Total users: "+users.size());	
	%><table><tr><th>Council</th><th>Age Group</th></tr><% 
	
	
	for(User uu: users){
		%>
		<tr>
		<td><%= uu.getSfCouncil()%></td>
		<td><%= uu.getSfTroopAge()%></td>
		</tr>
  <% }%>
  </table>
  
  <% */ %>

<%!



	public java.util.Map parseData( java.util.List <User> users, String age){
	
		
		
		java.util.Map <String, Integer>container = new java.util.TreeMap();
		for(int i=0;i<users.size();i++){
			
			if( users.get(i).getSfTroopAge().toLowerCase().contains(age.toLowerCase()) ){
				if( !container.containsKey(age)){
					container.put(users.get(i).getSfCouncil(), 1);
		
				}else{
					
					container.put(users.get(i).getSfCouncil(), container.get(users.get(i).getSfCouncil()) +1);
					
				}
				
			}
			
		}
		return container;
			
	}


	public java.util.List doConv(java.util.Map map){
		
		java.util.List container = new java.util.ArrayList();
		java.util.Iterator itr= map.keySet().iterator();
		while( itr.hasNext())
			container.add( map.get( itr.next() ) );
		
		return container;
		
	}
	
	java.util.List unqCouncil;
	
	public java.util.Map parseData( java.util.List <User> users){
		
		 unqCouncil = new java.util.ArrayList();
		java.util.Map<String, Map> main = new java.util.TreeMap<String, Map>();		
		for(int i=0;i<users.size();i++){
			
			if( !unqCouncil.contains(users.get(i).getSfCouncil()) )
				unqCouncil.add(users.get(i).getSfCouncil());
			
			java.util.Map <String, Integer> container = main.get( users.get(i).getSfTroopAge() );
			if(container==null){				
				container = new java.util.TreeMap<String, Integer>();
				container.put(users.get(i).getSfCouncil(), 1);				
			}else{
				Integer curCount = container.get(users.get(i).getSfCouncil());
				if( curCount==null) curCount=0;
				//container.put(users.get(i).getSfCouncil(), container.get(users.get(i).getSfCouncil()) +1);
				container.put(users.get(i).getSfCouncil(), (curCount +1));
			}
			
			main.put(users.get(i).getSfTroopAge(), container);
			
			
		}
		return main;
			
	}
	
	private java.util.List<User> doFix( java.util.List<User> users){
		
		for(int i=0;i< users.size();i++){
		  try{
			  
			  
			if( users.get(i).getSfCouncil()==null ){
				java.util.StringTokenizer t= new java.util.StringTokenizer(users.get(i).getPath(), "/");
				t.nextToken();
				users.get(i).setSfCouncil(t.nextToken() );
			}
			
			
			
			if( users.get(i).getSfTroopAge()==null ){
				//System.err.println("testss : "+ (users.get(i).getYearPlan()==null ));
				String ref= users.get(i).getYearPlan().getMeetingEvents().get(0).getRefId();
				//System.err.println("REf: "+ ref);
			//System.err.println("test: "+ users.get(i).getRefId());	
				String planId = ref.substring( ref.lastIndexOf("/") +1).toLowerCase();
			//System.err.println( "plaI: " +planId );
				
				if( planId.startsWith("d"))
					users.get(i).setSfTroopAge("1-Daisy");
				else if( planId.startsWith("b"))
					users.get(i).setSfTroopAge("2-Brownie");
				else if( planId.startsWith("j"))
					users.get(i).setSfTroopAge("3-Junior");
			}
			System.err.println( users.get(i).getSfTroopAge() );
		  }catch(Exception e){e.printStackTrace();}
		}
		
		return users;
	}

%>
