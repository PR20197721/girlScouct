<html>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/jquery/1.8.2/jquery.min.js" type="text/javascript"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.4.2/underscore-min.js" type="text/javascript"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/backbone.js/0.9.2/backbone-min.js" type="text/javascript"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/handlebars.js/1.3.0/handlebars.js" type="text/javascript"></script>

     <style>
        table,
        td {
            border: 1px solid #000;
        }
    </style>
    <body></body>
    <script>
    

    var Tweet = Backbone.Model.extend({});


   


 
    
    
    
    var TweetView = Backbone.View.extend({
        tagName: "li",
        id: "example_content",
        className: "tweet",
        render: function() {
console.log("render")
      
           // $(this.el).html(this.model.id + ": " + this.model.get("usid"));
           
      console.log("_..__"+this.model.get("usid")); 
      
  			//$(this.el).text(this.model.get("usid"));
 			$(this.el).html(this.model.get("usid"));
 			
 			
            return this;
        }
    });

    
    
    
    

    var Tweets = Backbone.Collection.extend({
        model: Tweet,
        initialize: function(models, options) {
            this.query = options.query;
        },
        url: function() {
        	
     console.log("url")
            return "http://localhost:4503/content/girlscouts-vtk/controllers/vtk.rand.test0.html";
        },
        parse: function(data) {
console.log("parse")
         
    console.log(data)
            return data; //.results;
        }
    });

    
    
    
    
    var TweetsView = Backbone.View.extend({
        tagName: "ul",
        className: "tweets",
        initialize: function(options) {
        	
      console.log("init")  	
           
            this.collection.bind("add", function(model) {

                var tweetView = new TweetView({
                    model: model
                });
console.log(2);
                $(this.el).prepend(tweetView.render().el);
            }, this);
        },
        render: function() {
        	console.log(".......................")
            return this;
        }
    });

    
    
    
    
    var catTweets = new Tweets([], {
        query: "cats"
    });

    
    
    
    var catTweetsView = new TweetsView({
    	
        collection: catTweets
    });

    
   
    
    
    
   var updateTweets = function() {
    	
        catTweets.fetch({
        	
        	success: function(collection) {
              // $('#example_content').html(catTweetsView.render().el);
              // $(this.el).html(this.model.get("usid"));
           
        	   $('#example_content').html(test.render().el);
               console.log("YES")
            },
            add: true
        });
        
        setTimeout(updateTweets, 3000);
    };
    updateTweets();

    $('#example_content').html(catTweetsView.render().el);
    </script>
    
  
    <h1> Yearplan 1 ... </h1>
    <div id="example_content">1R</div>

</html>