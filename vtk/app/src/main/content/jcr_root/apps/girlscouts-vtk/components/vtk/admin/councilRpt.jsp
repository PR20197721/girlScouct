
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="../include/session.jsp"%>

<%@include file="../admin/toolbar.jsp"%>
<h1>Council Report</h1>
 <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.5/angular.min.js"></script>
 <script src="http://code.highcharts.com/highcharts.js"></script>
<script src="https://rawgit.com/pablojim/highcharts-ng/master/src/highcharts-ng.js"></script>
<link rel="stylesheet" type="text/css" href="http://netdna.bootstrapcdn.com/bootstrap/3.0.2/css/bootstrap.min.css">


<%java.util.List<User> users = userDAO.getUsers();
users = doFix(users);
%>


<script>


//See: https://github.com/pablojim/highcharts-ng
var myapp = angular.module('myapp', ["highcharts-ng"]);

myapp.controller('myctrl', function ($scope) {
/*
    $scope.addPoints = function () {
        var seriesArray = $scope.chartConfig.series
        var rndIdx = Math.floor(Math.random() * seriesArray.length);
        seriesArray[rndIdx].data = seriesArray[rndIdx].data.concat([1, 10, 20])
    };

    $scope.addSeries = function () {
        var rnd = []
      
        for (var i = 0; i < 10; i++) {
            rnd.push(Math.floor(Math.random() * 20) + 1)
        }
       
        $scope.chartConfig.series.push({
            data: rnd
        })
       
    }

    $scope.removeRandomSeries = function () {
        var seriesArray = $scope.chartConfig.series
        var rndIdx = Math.floor(Math.random() * seriesArray.length);
        seriesArray.splice(rndIdx, 1)
    }

    $scope.toggleLoading = function () {
        this.chartConfig.loading = !this.chartConfig.loading
    }
*/
    $scope.chartConfig = {
        options: {
            chart: {
                type: 'line',
                zoomType: 'x'
            }
        },
       
      
        
        series: [
                 
                 
        <%
        
        	java.util.Map <String, Map>container = parseData(users);
        /*	java.util.Iterator itr= container.keySet().iterator();
        	while( itr.hasNext() ){
        		String lvl = (String) itr.next();
        		%>{name: '<%=lvl%>',<%
        		java.util.Map cnl = (java.util.Map) container.get(lvl);
        		
        		%>data: <%= doConv(cnl)%>},<%
        		
        	}
        	*/
        	
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




<%
	
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
