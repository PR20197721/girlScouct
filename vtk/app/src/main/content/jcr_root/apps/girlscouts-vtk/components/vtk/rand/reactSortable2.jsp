
     <script src="http://cdnjs.cloudflare.com/ajax/libs/react/0.12.1/react-with-addons.js"></script>
    <script src="http://fb.me/JSXTransformer-0.5.1.js"></script>
    
    <h1>ALEX9</h1>
    <style>
  li.placeholder { background: rgb(255,240,120);}
  li.placeholder:before {content: "Drop here"; color: rgb(225,210,90);}
  ul { list-style: none; margin:0; padding:0}
  li {padding: 5px; background:#eee}
</style>
<script type="text/jsx">
/** @jsx React.DOM */

var placeholder = document.createElement("li");
placeholder.className = "placeholder";

var Alex = React.createClass({
  getInitialState: function() {
    return {data: this.props.data};
 //return null;
  },

  dragStart: function(e) {
    this.dragged = e.currentTarget;
    e.dataTransfer.effectAllowed = 'move';
    
    // Firefox requires dataTransfer data to be set
    e.dataTransfer.setData("text/html", e.currentTarget);
  },
  dragEnd: function(e) {

    this.dragged.style.display = "block";
    this.dragged.parentNode.removeChild(placeholder);

    // Update data
    var data = this.props.data;//this.state.data;
console.log(22);
console.log( data);
console.log( this.props.data);
    var from = Number(this.dragged.dataset.id);
    var to = Number(this.over.dataset.id);
    if(from < to) to--;
    data.splice(to, 0, data.splice(from, 1)[0]);
console.log( data);
    this.setState({data: data});
  },
  dragOver: function(e) {
    e.preventDefault();
    this.dragged.style.display = "none";
    if(e.target.className == "placeholder") return;
    this.over = e.target;
    e.target.parentNode.insertBefore(placeholder, e.target);
  },
  render: function() {
    var commentNodes = this.props.data.map((function (comment ,i ) {




      return (
       <li data-id={i}
            key={i}
            draggable="true"
            onDragEnd={this.dragEnd}
            onDragStart={this.dragStart}>
          {comment}
        </li>

      );

    }).bind(this));
  
    return (
      
 		 <ul onDragOver={this.dragOver}>{commentNodes}</ul>
    );
  }
});





var CommentBox = React.createClass({

 loadCommentsFromServer: function( isFirst ) {
   $.ajax({
      url: 'http://localhost:4503/content/girlscouts-vtk/controllers/vtk.rand.test13_4.html' ,
      dataType: 'json',
	  cache: false,
      success: function(data) {
console.log(4);
console.log(data);
       this.setState({data: data});
 	   

      }.bind(this),
      error: function(xhr, status, err) {
        console.error(this.props.url, status, err.toString());
      }.bind(this)
    });
 },

  getInitialState: function() {
    return {data: []};
  },
  componentDidMount: function() {

    this.loadCommentsFromServer(1);
    setInterval( this.loadCommentsFromServer, this.props.pollInterval);
  },
  render: function() {

	//var y = ["Redx","Green","Blue","Yellow","Black","White","Orange"];
var x = this.state.data ;
console.log(2)
console.log(x);
    return (
      
        
       
 			<Alex data={x} />
       		
    
    );
  }
});


var List = React.createClass({
  getInitialState: function() {
    return {data: this.props.data};
  },
  dragStart: function(e) {
    this.dragged = e.currentTarget;
    e.dataTransfer.effectAllowed = 'move';
    
    // Firefox requires dataTransfer data to be set
    e.dataTransfer.setData("text/html", e.currentTarget);
  },
  dragEnd: function(e) {

    this.dragged.style.display = "block";
    this.dragged.parentNode.removeChild(placeholder);

    // Update data
    var data = this.state.data;
    var from = Number(this.dragged.dataset.id);
    var to = Number(this.over.dataset.id);
    if(from < to) to--;
    data.splice(to, 0, data.splice(from, 1)[0]);
    this.setState({data: data});
  },
  dragOver: function(e) {
    e.preventDefault();
    this.dragged.style.display = "none";
    if(e.target.className == "placeholder") return;
    this.over = e.target;
    e.target.parentNode.insertBefore(placeholder, e.target);
  },
  render: function() {
    var listItems = this.state.data.map((function(item, i) {
      return (
        <li data-id={i}
            key={i}
            draggable="true"
            onDragEnd={this.dragEnd}
            onDragStart={this.dragStart}>
          {item}
        </li>
      );
    }).bind(this));

    return <ul onDragOver={this.dragOver}>{listItems}</ul>
  }
});

var colors = ["Red","Green","Blue","Yellow","Black","White","Orange"];

/*
React.renderComponent(
  <List data={colors} />,
  document.getElementById('content1')
);
*/

React.renderComponent(
<CommentBox url="" pollInterval={20000} />,
  document.getElementById('content1')
);

/*
React.renderComponent(
  <Alex data={colors} />,
  document.getElementById('content1')
);
*/
</script>

    <div id="content"></div>
    <div style="border:5px solid green;" id="content1"></div>