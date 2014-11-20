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
    
    _.templateSettings = {
    	    interpolate: /\<\@\=(.+?)\@\>/gim,
    	    evaluate: /\<\@([\s\S]+?)\@\>/gim,
    	    escape: /\<\@\-(.+?)\@\>/gim
    	};
        var rowTemplate = "<tr><td class='name'><@= name @></td><td class='age'><@= age @></td></tr>";

        var data = [
                {
                    'name': 'Bert',
                    'age' : 6
                }, {
                    'name': 'Ernie',
                    'age' : 7
                }
            ];

        /** Collection of models to draw */
        var peopleCollection = new Backbone.Collection(data);

        /** View representing a table */
        var TableView = Backbone.View.extend({
                tagName: 'table',
                initialize: function () {
                    _.bindAll(this, 'render', 'renderOne');
                    if (this.model) {
                        this.model.on('change', this.render, this);
                        console.log(this.model);
                    }
                },
                render: function () {
                    this.collection.each(this.renderOne);
                    return this;
                },
                renderOne: function (model) {
                    var row = new RowView({
                            model: model
                        });
                    this.$el.append(row.render().$el);
                    return this;
                }
            });

        /** View representing a row of that table */
        var RowView = Backbone.View.extend({
                events: {
                    "click .age": function () {
                        console.log(this.model.get("name"));
                    }
                },
                initialize: function () {
                    this.model.on('change', this.render, this);
                },
                model: peopleCollection.models,
                render: function () {
                    var html = _.template(rowTemplate, this.model.toJSON());
                    this.$el.html(html);
                    return this;
                },
            });

        var tableView = new TableView({
                collection: peopleCollection
            });
        
        $("body").append(tableView.render().$el);

        
        setTimeout(x1, 2000)
        function x(){
        	
        	console.log(peopleCollection.models[1].set({
                'name': 'Statler',
                'age' : 100
            }));
            console.log(peopleCollection.models[1]);
        }
        
        function x1(){
        	
        	 data = [
                        {
                            'name': 'Bert',
                            'age' : 6
                        }, 
                        
                        {
                            'name': 'test',
                            'age' : 66
                        },
                        
                        {
                            'name': 'Ernie',
                            'age' : 7
                        }
                    ];
        	 
        	 peopleCollection = new Backbone.Collection(data);
        	 console.log(peopleCollection)
        	 tableView = new TableView({
                 collection: peopleCollection
             });
         
        	 $("body").append(tableView.render().$el);
        	 tableView.render()
        	// tableView.render().$el
        }
    </script>
</html>