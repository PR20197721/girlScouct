<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <title>People list</title>
  <link rel="stylesheet" href="http://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/2.1.1/css/bootstrap.min.css">
</head>


  
  
  <script>

    // MODEL MODEL MODEL
    // MODEL MODEL MODEL

    var Person = Backbone.Model;

    // COLLECTION COLLECTION COLLECTION
    // COLLECTION COLLECTION COLLECTION

    var PersonCollection = Backbone.Collection.extend({
      model: Person,
      url: '/people.json',
      parse: function (response) {
        return response
}
    });

    // VIEWS VIEWS VIEWS
    // VIEWS VIEWS VIEWS

    var About = Backbone.View.extend ({
      el: '.page',
      render: function () {
        var that = this;
        var people = new PersonCollection();
        people.fetch({
          success: function (PersonCollection) {
            var template = _.template($('#people-template').html(), {PersonCollection: PersonCollection.models});
            that.$el.html(template);
          }
        })
      }
    });  


    var About = new About ();

    // ROUTES ROUTES ROUTES
    // ROUTES ROUTES ROUTES    

    var Router = Backbone.Router.extend({
      routes: {
        '': 'home'
      }
    });

    var router = new Router();
    router.on('route:home', function () {
      About.render();
    });

    Backbone.history.start();

  </script>
  
  
  
<body>


  <div class="container">
    <h1>People list</h1>
    <hr />
    <div class="page"></div>
  </div>


  <script type="text/template" id="people-template">

    <table class="table striped">
      <thead>
        <tr>
          <th>First Name</th>
          <th>Last Name</th>
          <th>Age</th>
          <th>Photo</th>
          <th>Video</th>
        </tr>
      </thead>
      <tbody>
        <% _.each(PersonCollection, function(Person) { %>
          <tr>
            <td><%= Person.get("firstName") %></td>
            <td><%= Person.get("lastName") %></td>
            <td><%= Person.get("age") %></td>
            <td><%= Person.get("photo") %></td>
            <td><%= Person.get("video") %></td>
          </tr>
        <% }); %>

      </tbody>
    </table>  
  </script>

  </body>

  <script src="http://cdnjs.cloudflare.com/ajax/libs/jquery/1.8.2/jquery.min.js" type="text/javascript"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.4.2/underscore-min.js" type="text/javascript"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/backbone.js/0.9.2/backbone-min.js"></script>
