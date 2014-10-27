<!doctype html>
<html>
  <head>
    <script src="/etc/designs/girlscouts-vtk/clientlibs/js/angular.min.js"></script>
  </head>
  <body ng-app="myapp">
    <div>
      <label>Name:</label>
      <input type="text" ng-model="yourName" placeholder="Enter a name here">
      <hr>
      <h1>Hello {{yourName}}!</h1>
    </div>
    
    
    <div ng-controller="MyController" >
    1  <span>{{myData.textf()}}</span>
     <hr/>
    2  <span ng-bind="myData.textf()"></span>
      <hr/>
      
    3  <span ng-bind-html-unsafe="myData.textf1()"></span>
  </div>

  <script>
  
  
 
  
    angular.module("myapp", [])
    .controller("MyController", function($scope) {
      $scope.myData = {};
      $scope.myData.textf = function() { return "A text <a href=''>from</a> function"; };
      $scope.myData.textf1 = function() { return "A text fr function"; };
      $scope.myData.showIt = true;
      $scope.myData.switch = 2;
      $scope.myData.theDate = new Date();
      $scope.textToInsert="alex"
    });
    
    myapp = angular.module("myapp", []);
    myapp.directive('caca', function() {
        var directive = {};
       

        directive.restrict = 'E'; /* restrict this directive to elements */

        //directive.template = "My first directive: {{textToInsert}}";
        directive.templateUrl = "/content/girlscouts-vtk/controllers/vtk.test11.html";
       
        return directive;
    });
    
    
  </script>
  
 
  
  <div ng-controller="MyController" >
      <span ng-show="myData.showIt">asdfShow</span>
      <span ng-hide="myData.showIt">asdfNo</span>
  </div>

<div ng-controller="MyController" >
    <div ng-switch on="myData.switch">
        <div ng-switch-when="1">Shown when switch is 1</div>
        <div ng-switch-when="2">Shown when switch is 2</div>
        <div ng-switch-default>Shown when switch is anything else than 1 and 2</div>
    </div>
</div>
  
  
  
  
  
  <span>{{myData.theDate | date: 'dd-MM-yyyy'}}</span>
  
  
  
  <div ng-controller="MyController" >
    <caca>This div will be replaced</caca>
  </div>
  

  
  
  
  
  
  
  
  
  </body>
  
  
  
  
  
  
  
</html>