<!doctype html>
<html ng-app="Demo" ng-controller="DemoController">
<head>
    <meta charset="utf-8" />
 
    <title>Rendering DOM Elements With ngRepeat In AngularJS</title>
</head>
<body>
 
    <h1>
        Rendering DOM Elements With ngRepeat In AngularJS
    </h1>
 
    <p>
        <a ng-click="rebuild()">Rebuild Friends</a>
    </p>
 
    <div
        ng-repeat="friend in friends"
        bn-log-dom-creation>
 
        {{ friend.id }}. {{ friend.name }}
 
    </div>
 
 
    <!-- Load AngularJS from the CDN. -->
    <script
        type="text/javascript"
        src="//ajax.googleapis.com/ajax/libs/angularjs/1.0.2/angular.min.js">
    </script>
    <script type="text/javascript">
 
 
        // Create an application module for our demo.
        var Demo = angular.module( "Demo", [] );
 
 
        // -------------------------------------------------- //
        // -------------------------------------------------- //
 
 
        // Define the root-level controller for the application.
        Demo.controller(
            "DemoController",
            function( $scope ) {
 
 
                // I create and return an array of the friends. One
                // of the friends is passed by reference; one of the
                // friends is passed by value.
                function build() {
 
                    var friends = [
                        tricia,
                        angular.copy( joanna ) // NOTE: By-value.
                    ];
 
                    return( friends );
 
                }
 
 
                // I re-assemble the collection of friends.
                $scope.rebuild = function() {
 
                    // Update the ID so we can see *some* change.
                    joanna.id++;
 
                    // Update-Copy the joanna friend back into the
                    // collection.
                    update( $scope.friends[ 1 ], joanna );
 
                };
 
 
                // I update the values in the destination object
                // using the values in the source object, but I
                // ignore any proprietary keys used by AngularJS.
                function update( destination, source ) {
 
                    var angularJSKeyPattern = /^\$\$/i;
 
                    for ( var name in source ) {
 
                        if (
                            source.hasOwnProperty( name ) &&
                            destination.hasOwnProperty( name ) &&
                            ! angularJSKeyPattern.test( name )
                            ) {
 
                            destination[ name ] = source[ name ];
 
                        }
 
                    }
 
                }
 
 
                // ---
 
 
                var tricia = {
                    id: 1,
                    name: "Tricia"
                };
 
                var joanna = {
                    id: 2,
                    name: "Joanna"
                };
 
                $scope.friends = build();
 
 
            }
        );
 
 
        // -------------------------------------------------- //
        // -------------------------------------------------- //
 
 
        // I log the invocation of the LINK function. Since the Link
        // function is invoked whenever its contextual DOM element is
        // created, this actually logs DOM creation.
        Demo.directive(
            "bnLogDomCreation",
            function() {
 
 
                // I link the DOM element to the view model.
                function link( $scope, element, attributes ) {
 
                    console.log(
                        "Link Executed:",
                        $scope.friend.name,
                        $scope.friend
                    );
 
                }
 
 
                // Return directive configuration.
                return({
                    link: link,
                    restrict: "A"
                });
 
 
            }
        );
 
 
    </script>
 
</body>
</html>