<html>
  
   <head>
    <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.5/angular.min.js">      </script>
    <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.5/angular-route.min.js"></script>
  </head>
  
<script>

var phonecatApp = angular.module('phonecatApp', []);

phonecatApp.controller('PhoneListCtrl', function xxx($scope, $http) {
	
	
  		$http.get('/content/girlscouts-vtk/controllers/vtk.controller.html?test=anglr1')
  		.success(function (data) {	  
   			 $scope.phones = data.yearPlan['meetingEvents'];
    		 window.setTimeout( xxx($scope,$http), 10000);
   		 });


  $scope.orderProp = 'id';
});




</script>




  <body  ng-app="phonecatApp">

  <div class="container-fluid" ng-controller="PhoneListCtrl">
    <div class="row">
      <div class="col-md-2">
        <!--Sidebar content-->

        Search: <input ng-model="query">
        Sort by:
        <select ng-model="orderProp">
          <option value="name">Alphabetical</option>
          <option value="age">Newest</option>
        </select>

      </div>
      <div class="col-md-10">
        <!--Body content-->

        <ul class="phones">
          <li ng-repeat="phone in phones | filter:query | orderBy:orderProp">
            <span>ID:{{phone.id}}</span>
            <p>Path{{phone.path}}</p>
            <p>Type: {{phone.type}}</p>
            
            
            
            
          </li>
        </ul>

      </div>
    </div>
  </div>
  
  
  
  </body>
  </html>