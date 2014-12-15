 
 <script src="https://cloud.github.com/downloads/knockout/knockout/knockout-2.0.0.js"></script>
 
 <h1>YEarPl</h1>
<span data-bind="text: greet"></span>
<span data-bind="text: time"></span>
 <script>
 
 	var model = {
		    greet: "Hi!",
		    time: ko.computed(function() { return new Date().getTime(); })
		}

		ko.applyBindings(model);
		
 	
 	var x;
 	
 	$.getJSON("http://localhost:4503/content/girlscouts-vtk/controllers/vtk.rand.test0.html", function(data) { 
 	    // Now use this data to update your view models, 
 	    // and Knockout will update your UI automatically 
 	    x= data;
 	})
		
 	
 	var viewModel = {
    firstName : ko.observable("Bert"),
    lastName : ko.observable("Smith"),
    pets : ko.observableArray(["Cat", "Dog", "Fish"]),
    type : "Customer"
};
viewModel.hasALotOfPets = ko.computed(function() {
    return this.pets().length > 2
}, viewModel)


var jsonData = ko.toJSON(viewModel);



//Load and parse the JSON
var someJSON = '{"firstName":"Bert","lastName":"Smith","pets":["Cat","Dog","Fish"],"type":"Customer","hasALotOfPets":true}';
var parsed = JSON.parse(someJSON);
 
// Update view model properties
viewModel.firstName(parsed.firstName);
viewModel.pets(parsed.pets);
 </script>
 <pre data-bind="text: ko.toJSON($root, null, 2)"></pre>
 
 Hey<span data-bind="text: firstName"></span>