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
    </script>
    
    
    <h1> Yearplan  ... </h1>
<div id="example_content"></div>
</html>