
  <html>
  
   <head>
    <script src="/etc/designs/girlscouts-vtk/clientlibs/js/angular.min.js"></script>
  </head>
  
  <script>
  var phonecatApp = angular.module('phonecatApp', []);

  phonecatApp.controller('PhoneListCtrl', function ($scope) {
    $scope.phones = [
      {'name': 'Nexus S',
       'snippet': 'Fast just got faster with Nexus S.',
       'age': 1},
      {'name': 'Motorola XOO with Wi-Fi',
       'snippet': 'The Next, Next Generation tablet.',
       'age': 2},
      {'name': 'MOTOROLA XOOM',
       'snippet': 'The Next, Next Generation tablet.',
       'age': 3}
    ];

    $scope.orderProp = 'age';
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
            <span>{{phone.name}}</span>
            <p>{{phone.snippet}}</p>
          </li>
        </ul>

      </div>
    </div>
  </div>
  
  
  </body>
  </html>