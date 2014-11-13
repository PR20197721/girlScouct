<!DOCTYPE html>
<html>
<head>
<title>Backbone Application</title>

  <script src="http://cdnjs.cloudflare.com/ajax/libs/jquery/1.8.2/jquery.min.js" type="text/javascript"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.4.2/underscore-min.js" type="text/javascript"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/backbone.js/0.9.2/backbone-min.js"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/handlebars.js/1.3.0/handlebars.js"></script>
</head>
<body>
  
<div class="content"></div>
 
<script id="artist-list-template" type="text/x-handlebars-template">
 
<table>
  <thead>
    <tr>
      <th>Name</th>
      <th>Hometown</th>
      <th>Favorite Color</th>
    </tr>
  </thead>
  <tbody>
      {{#each []}}
      <tr>
          <td>{{this.name}}</td>
          <td>{{this.hometown}}</td>
          <td>{{this.favoriteColor}}</td>
      </tr>
      {{/each}}
  </tbody>
</table>
</script>
 
</script>
  
  
<script type="text/javascript">
 
 


 
var YearPlan = Backbone.Model.extend({
 
    defaults:{
        name: 'New Year Plan',
        
    },
 
    initialize: function() {
        console.log('New artist created...');
    }
 
});

window.x = new YearPlan();
x.fetch({ url: "http://localhost:4503/content/girlscouts-vtk/en/vtk.expiredcheck.json?sid=test&ypid=test"});

 

 /*
var biggie = new Artist({ id: 1, name: 'Notorious BIG', birthday: 'May 21, 1972', hometown: 'Brooklyn, NY', favoriteColor: 'green' });
var mike = new Artist({ id: 2, name: 'Mike Jones', birthday: 'January 6, 1981', hometown: 'Houston, TX', favoriteColor: 'blue' });
var taylor = new Artist({ id: 3, name: 'Taylor Swift', birthday: 'December 13, 1989', hometown: 'Reading, PA', favoriteColor: 'red' });
 
var Artists = Backbone.Collection.extend({
 
    model: Artist,
 
    initialize: function() {
        console.log('New collection initialized...');
    }
});  
 
var artistArray = [biggie, mike, taylor];
var artists = new Artists(artistArray);  
 */
 
 
//var peopleCollection = new Backbone.Collection(data);
//artists.fetch({ url: "http://localhost:4503/content/girlscouts-vtk/en/vtk.expiredcheck.json?sid=test&ypid=test"});

 
var ArtistListView = Backbone.View.extend({
    el: '.content',
 
    initialize:function(){
        this.render();
    },
    render: function () {
        var source = $('#artist-list-template').html();
        var template = Handlebars.compile(source);
        var html = template(x.toJSON());
       
        this.$el.html(html);
    }
});
 
var artistListView = new ArtistListView();
 
</script>
</body>
</html> 