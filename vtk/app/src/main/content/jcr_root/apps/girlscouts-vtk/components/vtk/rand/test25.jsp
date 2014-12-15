<html>
  <head>
    <title>Backbone.js view!</title>
  </head>
  <body>

    <div id="main">
    </div>

   <script src="http://cdnjs.cloudflare.com/ajax/libs/jquery/1.8.2/jquery.min.js" type="text/javascript"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.4.2/underscore-min.js" type="text/javascript"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/backbone.js/0.9.2/backbone-min.js"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/handlebars.js/1.3.0/handlebars.js"></script>
<script id="personTemplate" type="text/template">
	<span><strong><@= name @></strong> (<@= age @>) </span> 
	<button class="edit">Edit</button>
	<button class="delete">Delete</button>
</script>

    <script>
     
      
    	  $(function() {

    		  "use strict";

    		    var PostModel = Backbone.Model.extend({});

    		    var PostCollection = Backbone.Collection.extend({
    		        model: PostModel,
    		        url: 'http://localhost:4503/content/girlscouts-vtk/controllers/vtk.rand.test13.html'
    		    });

    		    
    		    
    		    
    		    var PostView = Backbone.View.extend({
    		        el: "#posts-editor",        

    		        initialize: function(){
    		            this.template = _.template($("#personTemplate").html());
    		           // this.collection.fetch({data:{fetch:true, type:"post", page:1}});
    		            
    		         //  this.listenTo( this.collection, 'reset add change remove', this.render, this );
this.collection.fetch({ data: { fetch:true, type:"post", page:1 } });
    		           
    		           this.collection.bind('reset', this.render, this);
    		        },

    		        render: function(){
    		            var renderedContent = this.collection.toJSON();
    		            console.log(renderedContent);
    		            $(this.el).html(renderedContent);
    		            return this;
    		        }
    		    });

    		    var postList = new PostCollection();
    		    postList.reset();
    		    var postView = new PostView({
    		        collection: postList
    		    });
    	  });
     
    /*
      function x(test){
    	  console.log("new...");
    	  test.load();
    	  setTimeout(function(){x(test);}, 5000);
      }
    */
    </script>
  
  </body>
</html>