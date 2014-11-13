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
 
<script id="vtk-template" type="text/x-handlebars-template">
 
<table>
  <thead>
    <tr>
      <th>date</th>
      <th>meeting name</th>
     
    </tr>
  </thead>
  <tbody>
      {{#each []}}
      <tr>
 <td>--</td>         
 <td>{{this.usid}}</td>
         
        
      </tr>
      {{/each}}
  </tbody>
</table>
</script>
 
</script>
  
  
<script type="text/javascript">
 
 


 
var YearPlan = Backbone.Model.extend({
 
    defaults:{
    	usid: 'New YearPlan',
    	yp_cng:"true"
        
    },
 
    initialize: function() {
        console.log('New artist created...');
    }
 
});

/*
window.x = new YearPlan();
x.fetch({ url: "http://localhost:4503/content/girlscouts-vtk/en/vtk.expiredcheck.json?sid=test&ypid=test"});
console.log(x);
 */
 
 
 /*
var YearPlans = Backbone.Collection.extend({
	 
    model: YearPlan,
 
    initialize: function() {
        console.log('New collection initialized...');
    }
});  
*/


var YearPlanCollection = Backbone.Collection.extend({
    defaults: {
        model: YearPlan
    },
    model: YearPlan,
   url: 'http://localhost:4503/content/girlscouts-vtk/en/vtk.expiredcheck.json?sid=test&ypid=test',
//url: 'http://localhost:4503/content/girlscouts-vtk/controllers/vtk.rand.test0.html',
    parse: function(response){
       return response.items;
    },
    
    initialize: function() {
        console.log('New collection initialized...');
    }
});

var y= new YearPlanCollection();
y.fetch();
for (variable in y) {
	console.log(">>"+variable+" : "+y[variable]);
}



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
    	console.log("Rendering....");
        this.render();
    },
    render: function () {
        var source = $('#vtk-template').html();
        var template = Handlebars.compile(source);
        var html = template(y.toJSON());
        this.$el.html(html);
        
        
       
    }
});
 
var artistListView = new ArtistListView();
 
</script>
</body>
</html> 