
<!DOCTYPE html>
<html lang="en">
<head>

  <meta charset="UTF-8">
  <title>React Sortable</title>
  <link href="http://webcloud.se/react-sortable/" rel="stylesheet"/>
</head>
<body>

<a href="../">webcloud</a> / React Sortable
<h1>React Sortable</h1>
<p>View source and documentation on <a href="https://github.com/danielstocks/react-sortable/">Github</a>.
<br>Check out the <a href="nested.html">nested data structure</a> demo</p>

<p>You can also try dragging items between the grid and the list</p>

<div id="app"></div>

<script src="http://webcloud.se/react-sortable/bower_components/react/react.js"></script>
<script src="http://webcloud.se/react-sortable/bower_components/react/JSXTransformer.js"></script>

<script src="http://webcloud.se/react-sortable/src/Sortable.js" type="text/jsx"></script>

<script src="http://webcloud.se/react-sortable/demo/gridlist/App.js" type="text/jsx"></script>
<script src="http://webcloud.se/react-sortable/demo/gridlist/SortableListItem.js" type="text/jsx"></script>
<script src="http://webcloud.se/react-sortable/demo/gridlist/SortableGridItem.js" type="text/jsx"></script>
<script src="http://webcloud.se/react-sortable/demo/StateView.js" type="text/jsx"></script>

<script type="text/jsx">

var Sortable = {
  getDefaultProps: function() {
    return {
      "data-id" : this.props.key,
      draggable : true,
      onDragEnd: this.sortEnd.bind(this),
      onDragOver: this.dragOver.bind(this),
      onDragStart: this.sortStart.bind(this)
    }
  },
  update: function(to, from) {
    var data = this.props.data.items;
    data.splice(to, 0, data.splice(from,1)[0]);
    this.props.sort(data, to);
  },
  sortEnd: function() {
    this.props.sort(this.props.data.items, undefined);
  },
  sortStart: function(e) {
    this.dragged = e.currentTarget.dataset ?
      e.currentTarget.dataset.id :
      e.currentTarget.getAttribute('data-id');
    e.dataTransfer.effectAllowed = 'move';
    try {
      e.dataTransfer.setData('text/html', null);
    } catch (ex) {
      e.dataTransfer.setData('text', '');
    }
  },
  move: function(over,append) {
    var to = Number(over.dataset.id);
    var from = this.props.data.dragging != undefined ? this.props.data.dragging : Number(this.dragged);
    if(append) to++;
    if(from < to) to--;
    this.update(to,from);
  },
  dragOver: function(e) {
    e.preventDefault();
    var over = e.currentTarget
    var relX = e.clientX - over.getBoundingClientRect().left;
    var relY = e.clientY - over.getBoundingClientRect().top;
    var height = over.offsetHeight / 2;
    var placement = this.placement ? this.placement(relX, relY, over) : relY > height
    this.move(over, placement);
  },
  isDragging: function() {
    return this.props.data.dragging == this.props.key
  }
}



/** @jsx React.DOM */
var data = {
  items: [
    "Gold",
    "Crimson",
    "Hotpink",
    "Blueviolet",
    "Cornflowerblue",
    "Skyblue",
    "Lightblue",
    "Aquamarine",
    "Burlywood"
  ]
};
React.renderComponent(
  <App data={data} />,
  document.getElementById("app")
);
</script>

<script>
if(/webcloud\.se$/.test(window.location.hostname)) {
  var _gaq = _gaq || [];
    _gaq.push(['_setAccount', 'UA-4323373-5']);
    _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();
}
</script>


</body>
</html>
