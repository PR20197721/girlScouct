<html>
  
   <head>
    <script src="/etc/designs/girlscouts-vtk/clientlibs/js/angular.min.js"></script>
    <script src="/etc/designs/girlscouts-vtk/clientlibs/js/angular-route.min.js"></script>
  </head>
  
<script>

var phonecatApp = angular.module('phonecatApp', []);

phonecatApp.controller('PhoneListCtrl', function ($scope, $http) {
  $http.get('/content/girlscouts-vtk/controllers/vtk.controller.html?test=anglr1').success(function(data) {
    $scope.phones = data.yearPlan.meetingEvents;
    
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