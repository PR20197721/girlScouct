
<html>
<head>
	<meta charset="utf-8">
	<title></title>
</head>
<body>
	<h1>VTK</h1>
	
<script id="personTemplate" type="text/template">
	<span><strong><@= name @></strong> (<@= age @>) </span> 
	<button class="edit">Edit</button>
	<button class="delete">Delete</button>
</script>

   <script src="http://cdnjs.cloudflare.com/ajax/libs/jquery/1.8.2/jquery.min.js" type="text/javascript"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.4.2/underscore-min.js" type="text/javascript"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/backbone.js/0.9.2/backbone-min.js"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/handlebars.js/1.3.0/handlebars.js"></script>


</body>
</html>


<script>

_.templateSettings = {
	    interpolate: /\<\@\=(.+?)\@\>/gim,
	    evaluate: /\<\@([\s\S]+?)\@\>/gim,
	    escape: /\<\@\-(.+?)\@\>/gim
	};

(function() {

	window.App = {
		Models: {},
		Collections: {},
		Views: {}
	};

	window.template = function(id) {
		return _.template( $('#' + id).html() );
	};


	// Person Model
	App.Models.Person = Backbone.Model.extend({
		defaults: {
			name: 'Guest User',
			age: 30,
			occupation: 'worker'
		}
	});

	// A List of People
	App.Collections.People = Backbone.Collection.extend({
		model: App.Models.Person,
		
		 url: 'http://localhost:4503/content/girlscouts-vtk/controllers/vtk.rand.test13.html',
         parse: function(data) {

       		console.log( data )   
       	this.render;
            //return data.results;
       		return data;
         },
         
       initialize: function()
         {
             this.on( "change:name", this.changeName, this);
             
         },
     changeName:
         function( model, val, options)
         {
    	 console.log(222);
             var prev = model.previousAttributes();
             console.log( model.get("name") + " changed his name from " + prev.name);
         }
	});


	// View for all people
	App.Views.People = Backbone.View.extend({
		tagName: 'ul',
		
		initialize: function() {
			this.collection.on('add', this.addOne, this);
			//this.collection.on('add', this.render, this);
			this.collection.on('change', this.changeOne, this);
			this.collection.on('remove', this.removeOne, this);
			this.collection.on('reset', this.resetOne, this);
		},
		
		render: function() {
	console.log("reset all people...");		
			this.collection.each(this.addOne, this);

			return this;
		},
		removeOne: function(person) {
			
			console.log("REMOVEEEEE");			
			
					},
		addOne: function(person) {
			
console.log("ADDDDDDD");			
			var personView = new App.Views.Person({ model: person });
console.log(person)			
			this.$el.append(personView.render().el);
		},
		changeOne: function(person) {
			
			console.log("CHANGE.....");			
			
		},
		resetOne: function(person){
			console.log("RESET.....");	
		}
		
	});

	// The View for a Person
	App.Views.Person = Backbone.View.extend({
		tagName: 'li',

		template: template('personTemplate'),	
		
		initialize: function(){
			this.model.on('change', this.render, this);
			this.model.on('destroy', this.remove, this);
			this.model.on('add', this.render, this);
		},
		
		events: {
		 'click .edit' : 'editPerson',
		 'click .delete' : 'DestroyPerson'	
		
		},
		
		editPerson: function(){
		console.log(1)
			var newName = prompt("Please enter the new name", this.model.get('name'));
			if (!newName) return;
			console.log( newName);
			this.model.set('name', newName);
		},
		
		DestroyPerson: function(){
			this.model.destroy();
		},
		
		remove: function(){
			this.$el.remove();
		},
		
		render: function() {
			
			console.log("rendering person...");
			console.log(this.model.toJSON());
			//console.log("**");
			//console.log(this.$el.html);
			//console.log("..");
			//console.log(this.$el);
	 
			this.$el.html( this.template(this.model.toJSON()) );
			
			
			
			return this;
		}
	});


	App.Views.AddPerson = Backbone.View.extend({
		el: '#addPerson',

		events: {
			'submit': 'submit'
		},

		submit: function(e) {
			e.preventDefault();
			var newPersonName = $(e.currentTarget).find('input[type=text]').val();
			var person = new App.Models.Person({ name: newPersonName });
			this.collection.add(person);

		}
	});


	var peopleCollection = new App.Collections.People([
		{
			name: 'Mohit Jain1',
			age: 26,
			idAttribute: 110,
			id: 110,
		},
		{
			name: 'Taroon Tyagi',
			age: 25,
			occupation: 'web designer',
			idAttribute: 120,
			id: 120,
			
		},
		{
			name: 'Rahul Narang',
			age: 26,
			occupation: 'Java Developer',
			idAttribute: 130,
			id: 130,
			
		}
	]);
	
	var addPersonView = new App.Views.AddPerson({ collection: peopleCollection });
	peopleView = new App.Views.People({ collection: peopleCollection });
	
	/*
	var person = new App.Models.Person({ name: "alex" });
	peopleCollection.add(person);
	*/
	$(document.body).html(peopleView.render().el);
	y(peopleCollection);
	
	})();
	/*
peopleView.on("change:name", function(a, b){
	console.log(1)
});

var myModel = new App.Models.Person();
myModel.trigger("change:name", "foo", "bar");


                                       	




peopleCollection = new App.Collections.People([
		
		{
			name: 'alexasdf',
			age: 25,
			occupation: 'web designer',
			idAttribute: 2,
			id: 2
		},
		{
			name: 'mikeaa',
			age: 26,
			occupation: 'Java Developer',
			idAttribute: 3,
			id: 3
		}
	]);
	
//peopleView = new App.Views.People({ collection: peopleCollection });
//$(document.body).html(peopleView.render().el);
	*/
	

//$(document.body).html(allView.render().el);

	/*
	
//peopleView = new App.Views.People({ collection: peopleCollection });
//$(document.body).html(peopleView.render().el);
peopleView = new App.Views.People({ collection: peopleCollection });
peopleView.trigger("change");
peopleView.model.trigger('change'); 
peopleView.model.set({rand:Math.random()});
*/
/*
for( var i in  peopleView ){
	//console.log("_________________________________ "+i);
	console.log(  peopleView[i]);
	
}
*/



function y(collect){
	console.log(window.document);
	
	//var collect = new App.Collections.People();
	//var allView = new App.Views.People({collection: collect});
	//$(document.body).html(allView.render().el);
/*
	collect = new App.Collections.People([
{
	name: 'Mohit Jain1',
	age: 26,
	idAttribute: 1,
	id: 1
},
{
	name: 'Taroon Tyagi',
	age: 25,
	occupation: 'web designer',
	idAttribute: 2,
	id: 2
},
{
	name: 'Rahul Narang',
	age: 26,
	occupation: 'Java Developer',
	idAttribute: 3,
	id: 3
},
	                                       		{
	                                       			name: 'alexasdf',
	                                       			age: 25,
	                                       			occupation: 'web designer',
	                                       			idAttribute: 2,
	                                       			id: 2
	                                       		},
	                                       		{
	                                       			name: 'mikeaa',
	                                       			age: 26,
	                                       			occupation: 'Java Developer',
	                                       			idAttribute: 33,
	                                       			id: 33
	                                       		}
	                                       	]);
	                                       	
	                                       	*/
	                                       	
	                                       	
	                                       	
	                                       	
	   collect.fetch({
	                                     	  
	                                     	   add: true,
	                                     	   
	                                       success: function(model) {
	                                         //console.log(model)
	                                     	 // console.log( ">> "+model.get('name') ); 
	                                          
	                                          
	                                   		 //peopleView.render().el;
	                                    
	                                       },
	                                       error: function(errorResponse){//model, xhr, options) {
	                                         console.log('Fetch error');
	                                        // console.log(errorResponse);
	                                        // console.log(model);
	                                        // console.log(xhr );
	                                       }
	                                     });
	                                     
	                                     
	//allView = new App.Views.People({collection: collect});
	//console.log("calling refresh...");
	//allView.render().el;
	//allView.render();
	//allView.model.trigger('change'); 
	//$(document.body).append(allView.render().el);
	//-allView.render().el
	//console.log(window.document);
	
	
	
	setTimeout(function(){y(collect);}, 10000);
}



//-----------------------------------------------------------------------------
function x(peopleCollection){
	
	//var person = new App.Models.Person({ name: "alex" });
	//peopleCollection.add(person);
	
	//console.log( peopleCollection );
	
	//peopleView = new App.Views.People({collection: peopleCollection});
	peopleCollection.fetch({
		reset: true,
	
    	   update:true,
    	   remove: true,
    	   add: true,
    	   change: true,
      success: function(model) {
         // console.log(model)
    	 // console.log( ">> "+model.get('name') ); 
         
         
  		 //peopleView.render().el;
   
      },
      error: function(errorResponse){//model, xhr, options) {
        console.log('Fetch error');
       // console.log(errorResponse);
       // console.log(model);
       // console.log(xhr );
      }
    });
    
    
	setTimeout(function(){x(peopleCollection);}, 5000);
}

	
</script>


