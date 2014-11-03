<html>
  

    <script src="/etc/designs/girlscouts-vtk/clientlibs/js/angular.min.js"></script>
    <script src="/etc/designs/girlscouts-vtk/clientlibs/js/angular-route.min.js"></script>

  



<script>
    var module       = angular.module("myapp", []);
    var myController1 = module.controller("myController", function($scope) {
 
        $scope.data = { time : new Date() };

        $scope.updateTime = function() {
            $scope.data.time = new Date();
        }

        document.getElementById("updateTimeButton")
                .addEventListener('click', function() {
            console.log("update time clicked");
            $scope.data.time = new Date();
        });
        
        
        
        
        
  
    
    
    $scope.$watch(function() {},
            function() {}
           );
    
    $scope.$watch(function(scope) { return scope.data.myVar },
            function(newValue, oldValue) {
                document.getElementById("").innerHTML =
                    "" + newValue + "";
            }
           );
    
    $scope.$apply(function() {
        $scope.data.myVar = "Another value";
    });
    
    
    });
</script>

<div ng-controller="myController">
    {{data.time}} --

    <br/>
    <button ng-click="updateTime()">update time - ng-click</button>
    <button id="updateTimeButton"  >update time</button>
</div>
</html>