<!DOCTYPE html>
<html lang="en">
<head>
    <title>AngularJS Routes example</title>
    <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.5/angular.min.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.5/angular-route.min.js"></script>
</head>

<body ng-app="sampleApp">

<a href="#/route1/abcd">Route 1 + param</a><br/>
<a href="#/route2/1234">Route 2 + param</a><br/>


<div ng-view></div>

<script>
    var module = angular.module("sampleApp", ['ngRoute']);

    module.config(['$routeProvider',
        function($routeProvider) {
            $routeProvider.
                    when('/route1/:param', {
                        templateUrl: 'vtk.rand.view1.html',
                        controller: 'RouteController'
                    }).
                    when('/route2/:param', {
                        templateUrl: 'vtk.rand.view2.html',
                        controller: 'RouteController'
                    }).
                    otherwise({
                        redirectTo: '/'
                    });
        }]);

    module.controller("RouteController", function($scope, $routeParams) {
        $scope.param = $routeParams.param;
    })
</script>



</body>
</html>    