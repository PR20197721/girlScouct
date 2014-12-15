<html>
      <script src="http://cdnjs.cloudflare.com/ajax/libs/jquery/1.8.2/jquery.min.js" type="text/javascript"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.4.2/underscore-min.js" type="text/javascript"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/backbone.js/0.9.2/backbone-min.js"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/handlebars.js/1.3.0/handlebars.js"></script>

    <style>
        table,
        td {
            border: 1px solid #000;
        }
    </style>
    <body></body>
    <script>
    
   
    
    /*
 // A rendering of a collection of tweets.
    var TweetsView = Backbone.View.extend({
      tagName: "ul",
      className : "tweets",
      initialize: function(options) {
        // Bind on initialization rather than rendering. This might seem
        // counter-intuitive because we are effectively "rendering" this
        // view by creating other views. The reason we are doing this here
        // is because we only want to bind to "add" once, but effectively we should
        // be able to call render multiple times without subscribing to "add" more
        // than once.
        this.collection.bind("add", function(model) {
          var tweetView = new TweetView({
            model: model
          });
     
          $(this.el).prepend(tweetView.render().el);
        }, this);
        
        
        
      },
      render: function() {
        return this;
      }
    });
    */
    
    
    
    
    
    
    
 // A container for a tweet object.
    var Tweet = Backbone.Model.extend({});

    // A basic view rendering a single tweet
    var TweetView = Backbone.View.extend({
        tagName: "li",
        className: "tweet",

        render: function() {
console.log("render")
            // just render the tweet text as the content of this element.
            $(this.el).html(this.model.get("usid"));
            return this;
        }
    });

    // A collection holding many tweet objects.
    // also responsible for performing the
    // search that fetches them.
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

            // note that the original result contains tweets inside of a results array, not at 
            // the root of the response.
      console.log( data )      
            //return data.results;
      return data;
        }
    });

    // A rendering of a collection of tweets.
    var TweetsView = Backbone.View.extend({
        tagName: "ul",
        className: "tweets",
        render: function() {

            // for each tweet, create a view and prepend it to the list.
            this.collection.each(function(tweet) {
                var tweetView = new TweetView({
                    model: tweet
                });
                
                $(this.el).prepend(tweetView.render().el);
            }, this);

            return this;
        }
    });

/*
    // Create a new cat tweet collection
    var catTweets = new Tweets([], {
        query: "cats"
    });

    // create a view that will contain our tweets
    var catTweetsView = new TweetsView({
        collection: catTweets
    });

    // on a successful fetch, update the collection.
    catTweets.fetch({
        success: function(collection) {
            $('#example_content').html(catTweetsView.render().el);
        }
    });
    */
    
    
    
    var catTweets = new Tweets([], { query : "cats" });
    var catTweetsView = new TweetsView({ collection : catTweets });
     
    // Before we even start our fetch, we append the initial view so that it serves as a 
    // placeholder for all the tweets we will be adding.
    $('#example_content').html(catTweetsView.render().el);
     
    // Note that we've added the "add" property to the fetch options 
    // and set it to true.
    // This will ensure each additionally fetched tweet will be _added_ to the collection
    // rather than overwrite its existing contents.
    var updateTweets = function() {
      catTweets.fetch({
        add: true
      });
      setTimeout(updateTweets, 1000);
    };
    updateTweets();
    
    
    </script>
    
    
    <h1> Yearplan  ... </h1>
<div id="example_content"></div>
</html>