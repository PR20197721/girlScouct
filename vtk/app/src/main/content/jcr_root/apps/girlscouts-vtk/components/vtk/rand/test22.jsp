
<html>
<head>
	<meta charset="utf-8">
	<title></title>
</head>
<body>
	
	
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
    	
        return _.template($('#' + id).html());
    };


// Person Model
    App.Models.Person = Backbone.Model.extend({
    	
        defaults: {
        	idAttribute: 'id_event',
        	id: 'id',
        	name: 'Guest User',
            age: 30
            
        }
        
       
    });

// A List of People
    App.Collections.People = Backbone.Collection.extend({
        model: App.Models.Person ,
        
        
            url: 'http://localhost:4503/content/girlscouts-vtk/controllers/vtk.rand.test13.html',
            parse: function(data) {

          		console.log( data )   
          		this.render;
                //return data.results;
          		return data;
            }
                
               
    });

   
    
    
    
    
    

// View for all people
    App.Views.People = Backbone.View.extend({
        tagName: 'ul',
        initialize: function() {
        	
        	  
              
            this.collection.on('add', this.addOne, this);
            this.collection.on('remove', this.removeOne, this);
            this.collection.on('change', this.test, this);
            
            this.collection.fetch({data:{fetch:true}});
            this.collection.bind('reset', this.render, this);
          
          
        },
        test : function(){console.log(123);},
        render: function() {
        	
        	
           
    console.log("render called...");    	
           this.collection.each(this.addOne, this);
           //this.collection.each(this.render, this);
           //this.$el.html(this.render().el);
          //- this.collection.each(this.doRender, this);
       
          
          
          /*
          var renderedContent = this.collection.toJSON();
          console.log(renderedContent);
          $(this.el).html(renderedContent);
         */
            return this;
        },
        removeOne: function(ticket) {
        	console.log("rmmmm...");
        },
        addOne: function(person) {
        	
        	console.log("add event.. ");
            var personView = new App.Views.Person({model: person});
            this.$el.append(personView.render().el);
        },
        
        doRm: function(person) {
        	console.log("rm event..");
        },
        
        doChange: function(person) {
           console.log("change event..");
        }
    });
    

// The View for a Person
    App.Views.Person = Backbone.View.extend({
        tagName: 'li',
        template: template('personTemplate'),
        initialize: function() {
            this.model.on('remove', this.removeOne, this);
            this.model.on('destroy', this.remove, this);
            
           
           // this.render();
        },
        test: function (){console.log("YES!!!");},
        render: function() {
      console.log("rendering person "+ (this.template(this.model.toJSON())) );  	
            this.$el.html(this.template(this.model.toJSON()));
            return this;
        },
        events: {
            "change" : "change"
           
        },
        change: function(){alert(1);}

    });



     var peopleCollection = new App.Collections.People([
{
    name: 'Mohit Jain',
    age: 26,
    idAttribute: 1,
	id: 4,
},
{
    name: 'Taroon Tyagi',
    age: 25,
    occupation: 'web designer',
    idAttribute: 11,
	id: 42,
},

    ]);
    
    

       
 
    
    
    
    
    
    
    
   // var addPersonView = new App.Views.AddPerson({collection: peopleCollection});
    
   //peopleView = new App.Views.People({collection: peopleCollection});
   //$(document.body).append(peopleView.render().el);
   
    //var x = new App.Views.People();
    
    
var people = new    App.Collections.People();
people.reset();
var peopleView = new App.Views.People({
	collection: people
});
//$(document.body).append(peopleView.render().el);
   
    x(peopleCollection, peopleView);
})();

//-----------------------------------------------------------------------------
function x(peopleCollection, peopleView){
	
	//var person = new App.Models.Person({ name: "alex" });
	//peopleCollection.add(person);
	
	//console.log( peopleCollection );
	
	//peopleView = new App.Views.People({collection: peopleCollection});
	peopleCollection.fetch({
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
    
    
	setTimeout(function(){x(peopleCollection, peopleView);}, 5000);
}

</script>