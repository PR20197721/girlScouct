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


    <script>
      //Generic code starting our view when document is ready http://api.jquery.com/ready/
      $(function(){
      
        //Backbone code - begin



        //The model that will be used for every item inside the collection
        //(Backbone will traw to map every item into this model)
        var RealWorldItemModel = Backbone.Model.extend({
          defaults : {
            name   : '',
            color   : ''
          }
        });
        var RealWorldItemView = Backbone.View.extend({
          tagName   : 'li',
          template   : null,
          events     : {
          },
          
          initialize : function(){
            //This is useful to bind(or delegate) the this keyword inside all the function objects to the view
            //Read more here: http://documentcloud.github.com/underscore/#bindAll
            _.bindAll(this);

            //later we will see complex template engines, but is the basic from underscore
            this.template = _.template('<span>Name: <strong><@= name @></strong> - <@= color @> </span>');

          },
          render : function(){
            $(this.el).html( this.template( this.model.toJSON() ) );
            return this;
          }
        });

        //We define the collection, associate the map for every item in the list
        //and define the url that fetch will be using to load remote data
        var RealWorldCollection = Backbone.Collection.extend({
          model: RealWorldItemModel,
          url: 'http://localhost:4503/content/girlscouts-vtk/controllers/vtk.rand.test13.html'
        });

        //Main View for the list
        var TestView = Backbone.View.extend({
          id         : "real-world-id",
          //because it is a list we define the tag as ul 
          tagName     : "ul", 
          className     : "real-world", 

          events : {
          },
          
          initialize : function(){
            //This is useful to bind(or delegate) the this keyword inside all the function objects to the view
            //Read more here: http://documentcloud.github.com/underscore/#bindAll
            _.bindAll(this);
            this.collection.bind('add', this.addItemHandler);

          },

          load : function(){

            //here we do the AJAX Request to get our json file, also provide a success and error callbacks
            this.collection.fetch({
              add: true,
              success: this.loadCompleteHandler,
              error: this.errorHandler
            });

          },  

          //we arrived here one time per item in our list, so if we received 4 items we
          //will arrive into this function 4 times
          addItemHandler : function(model){
            //model is an instance of RealWorldItemModel

            var myItemView = new RealWorldItemView({model:model});
            myItemView.render();
            $(this.el).append(myItemView.el);

          },

          loadCompleteHandler : function(){
            console.log('Awesome everything was loaded without errors!');
            this.render();
          },

          errorHandler : function(){
            throw "Error loading JSON file";
          },

          render : function(){

            //we assign our element into the available dom element
            $('#main').append($(this.el));
            
              return this;
          }
        });
        //Backbone code - end

        //We create the instance of our collection:
        var myCollection = new RealWorldCollection();

        //we pass the collection to our testing View
        var test = new TestView({collection: myCollection});
        //test.load();
        x(test);
      })
      
      
      function x(test){
    	  console.log("new...");
    	  test.load();
    	  setTimeout(function(){x(test);}, 5000);
      }
    </script>
  </body>
</html>