<html>
      <script src="http://cdnjs.cloudflare.com/ajax/libs/jquery/1.8.2/jquery.min.js" type="text/javascript"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.4.2/underscore-min.js" type="text/javascript"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/backbone.js/0.9.2/backbone-min.js"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/handlebars.js/1.3.0/handlebars.js"></script>

    
    <script>
 // A container for a tweet object.
    var Tweet = Backbone.Model.extend({});

    // A basic view rendering a single tweet
    var TweetView = Backbone.View.extend({
        tagName: "li",
        className: "tweet",
        render: function() {
alert(this.model.get("text"));
            // just render the tweet text as the content of this element.
            $(this.el).html(this.model.id + ": " + this.model.get("text"));
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
            return "http://localhost:4503/content/girlscouts-vtk/controllers/vtk.rand.test13.html";
        },
        parse: function(data) {

            // note that the original result contains tweets inside of a results array, not at 
            // the root of the response.
            //return data.results;
            console.log(data)
            return data;
        }
    });

    // A rendering of a collection of tweets.
    var TweetsView = Backbone.View.extend({
        tagName: "ul",
        className: "tweets",
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
console.log(model.collection)
                $(this.el).prepend(tweetView.render().el);
            }, this);
        },
        render: function() {
        	
      	
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

    // We now render this view regardless of the fact it still
    // hasn't been fetched. This is because we want to bind to
    // the collection and be ready to create the tweet views
    // as they come in.
    $('#example_content').html(catTweetsView.render().el);

    var updateTweets = function() {
        catTweets.fetch({
            add: true
        });
        console.log(1)
        setTimeout(updateTweets, 1000);
    };
    updateTweets();
    
    
    </script>
    <h1> VTK1: </h1>
<div id="example_content">asdfasdf</div>


    </html>