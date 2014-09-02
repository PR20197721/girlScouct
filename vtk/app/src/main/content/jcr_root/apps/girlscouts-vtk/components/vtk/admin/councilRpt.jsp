
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="../include/session.jsp"%>

<%@include file="../admin/toolbar.jsp"%>
<h1>Council Report</h1>
 <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.5/angular.min.js"></script>
<script src="http://code.highcharts.com/highcharts.js"></script>
<link rel="stylesheet" type="text/css" href="http://netdna.bootstrapcdn.com/bootstrap/3.0.2/css/bootstrap.min.css">
<script src="https://rawgit.com/pablojim/highcharts-ng/master/src/highcharts-ng.js"></script>



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
       
      
        
        series: [{
            name: 'Brownie',
            data: [7, 6, 9, 14]
        }, {
            name: 'Daisy',
            data: [2, 3, 5, 11]
        }, {
            name: 'Junior',
            data: [9, 6, 5, 8]
        }, {
            name: 'Cadet',
            data: [3, 4, 5, 8]
        }],
        
        title: {
            text: ''
        },
        xAxis: {
            categories: ['603', '358', '111', '2222']
        },
        
        loading: false
    }

});

/*
 * $http.get('datatest').success(function(data) {
     $scope.data = data;
 });
 */
</script>
<div ng-app="myapp">
    <div ng-controller="myctrl">
       
        <div class="row">
            <highchart id="chart1" config="chartConfig" class="span10"></highchart>
        </div>
    </div>
</div>




<%
	java.util.List<User> users = userDAO.getUsers();
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

	public java.util.Map parseData( java.util.List <User> users){
	
		java.util.Map container = new java.util.TreeMap();
		for(int i=0;i<users.size();i++){
			
		}
		return container;
			
	
}
%>
