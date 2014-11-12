

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  
</head>
<body>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/2.0.3/jquery.js"></script>
  <script src="http://ajax.cdnjs.com/ajax/libs/json2/20110223/json2.js"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.5.2/underscore.js"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/backbone.js/1.1.0/backbone-min.js"></script>

 


<script>


(function($){
var ListView = Backbone.View.extend({
 el: $('body'), // el attaches to existing element
 // `events`: Where DOM events are bound to View methods. Backbone doesn't have a separate controller to handle such bindings; it all happens in a View.
 events: {
   'click button#add': 'addItem'
 },
 initialize: function(){
   _.bindAll(this, 'render', 'addItem'); // every function that uses 'this' as the current object should be in here

   this.counter = 0; // total number of items added thus far
   this.render();
 },
 // `render()` now introduces a button to add a new list item.
 render: function(){
   $(this.el).append("<button id='add'>Add list item</button>");
   $(this.el).append("<ul></ul>");
 },
 // `addItem()`: Custom function called via `click` event above.
 addItem: function(){
   this.counter++;
   $('ul', this.el).append("<li>hello world"+this.counter+"</li>");
 }
});

var listView = new ListView();
})(jQuery);


</script>



</body>
</html>