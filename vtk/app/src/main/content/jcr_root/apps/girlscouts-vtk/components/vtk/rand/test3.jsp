  <script src="http://cdnjs.cloudflare.com/ajax/libs/jquery/1.8.2/jquery.min.js" type="text/javascript"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.4.2/underscore-min.js" type="text/javascript"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/backbone.js/0.9.2/backbone-min.js"></script>

<script>

window.YearPlan = Backbone.Model.extend({
});

window.x = new YearPlan();
x.fetch({ url: "http://localhost:4503/content/girlscouts-vtk/en/vtk.expiredcheck.json?sid=test&ypid=test"});
console.log('***YEarPlan', x);
//console.log("&& "+ x.models[0].get("usid"));
  </script>
  
  
  
  
  
<%String name="1"; 
int age=2;%>
    <style>
        table,
        td {
            border: 1px solid #000;
        }
    </style>
    <body></body>
    
    
    
    <script type="text/x-template" id="edit-user-template">
        <form class="edit-user-form">
<span>Hello, {{name}} - <@=name@> ...</span>
        </form>
</script>
    
    <script>
    _.templateSettings = {
    	    interpolate: /\<\@\=(.+?)\@\>/gim,
    	    evaluate: /\<\@([\s\S]+?)\@\>/gim,
    	    escape: /\<\@\-(.+?)\@\>/gim
    	};
    
        var rowTemplate = "<tr><td class='name'><%= name %></td><td class='age'><%= age %></td></tr>";
        //var rowTemplate = "<tr><td class='name'> </td><td class='age'></td></tr>";
        var data = [
                {
                    'name': 'Bert',
                    'age' : 6
                }, {
                    'name': 'Ernie',
                    'age' : 7
                }, {
                    'name': 'test',
                    'age' : 744
                }
            ];

        /** Collection of models to draw */
        var peopleCollection = new Backbone.Collection(data);
      //console.log( ">> "+peopleCollection.getName() )
        console.log(">>>> "+peopleCollection.models[0].get("name"));
        console.log(">>>> "+peopleCollection.models[1].get("name") +" : "+ peopleCollection.models[1].get("age"));
        console.log(">>>> "+peopleCollection.models[2].get("name"));
       
      
      
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
                
                
               // var template: _.template($('#edit-user-template').html(),  {} ),
                
                model: peopleCollection.models,
                render: function () {
                   // var html = _.template(template);//, this.model.toJSON());
                    //this.$el.html(template);
                    var template = _.template( $("#edit-user-template").html(), {} );
            		this.$el.html( template );
                    
                    return this;
                },
                
            });

        
        var tableView = new TableView({
                collection: peopleCollection
        	
            });
        $("body").append(tableView.render().$el);

        console.log(peopleCollection.models[0].set({
            'name': 'Statler',
            'age' : 100
        }));
        
        console.log(peopleCollection.models[0]);
        console.log(">>>> "+peopleCollection.models[0].get("name"));
        console.log(">>>> "+peopleCollection.models[1].get("name") +" : "+ peopleCollection.models[1].get("age"));
        console.log(">>>> "+peopleCollection.models[2].get("name"));
        
    </script>
</html>