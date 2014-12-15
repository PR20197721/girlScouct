<html>
<body>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/jquery/1.8.2/jquery.min.js" type="text/javascript"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.4.2/underscore-min.js" type="text/javascript"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/backbone.js/0.9.2/backbone-min.js"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/handlebars.js/1.3.0/handlebars.js"></script>
  <!--  script src="http://localhost:4503/etc/designs/girlscouts-vtk/clientlib s/js/test.js"></script -->

<script>


_.templateSettings = {
	    interpolate: /\<\@\=(.+?)\@\>/gim,
	    evaluate: /\<\@([\s\S]+?)\@\>/gim,
	    escape: /\<\@\-(.+?)\@\>/gim
	};
	
	
// Define the model
Tweet = Backbone.Model.extend();

// Define the collection
Tweets = Backbone.Collection.extend(
    {
        model: Tweet,
        // Url to request when fetch() is called
        url: 'http://localhost:4503/content/girlscouts-vtk/controllers/vtk.rand.test0.html',
        parse: function(response) {
            return response.results;
        },
        // Overwrite the sync method to pass over the Same Origin Policy
        sync: function(method, model, options) {
            var that = this;
                var params = _.extend({
                    type: 'GET',
                    dataType: 'jsonp',
                    url: that.url,
                    processData: false
                }, options);

            return $.ajax(params);
        }
    });

// Define the View
TweetsView = Backbone.View.extend({
    initialize: function() {
      _.bindAll(this, 'render');
      // create a collection
      this.collection = new Tweets;
      // Fetch the collection and call render() method
      var that = this;
      this.collection.fetch({
        success: function () {
            that.render();
        }
      });
    },
    // Use an extern template
    template: _.template($('#tweetsTemplate').html()),

    render: function() {
        // Fill the html with the template and the collection
        $(this.el).html(this.template({ tweets: this.collection.toJSON() }));
    }
});

var app = new TweetsView({
    // define the el where the view will render
    el: $('body')
});
</script>


<!-- Template of the Tweets View -->


<style>
.tweet {
    background: rgb(245,245,245);
    border-bottom: 1px rgb(200,200,200) solid;
    padding: 5px;
}

.text {
    color: rgb(80,80,80);
}
</style>


<script type="text/template" id="tweetsTemplate">
    <@ _.each(tweets, function (tweet) { @>
    <div class="tweet">
        <p>@<@= tweet.usid @></p>
       
    <@ }); @>
</script>
</body>
</html>