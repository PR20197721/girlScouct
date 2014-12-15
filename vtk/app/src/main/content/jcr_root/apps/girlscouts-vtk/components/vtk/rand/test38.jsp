  
   <script src="http://fb.me/react-0.12.1.js"></script>
<script src="http://fb.me/JSXTransformer-0.12.1.js"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/react/0.12.1/react-with-addons.js"></script>
   <script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.touch-punch.min.js"></script>
  <link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />

 
  <style>
        li.placeholder { background: rgb(255,240,120);}
  li.placeholder:before {content: "Drop here"; color: rgb(225,210,90);}
  ul { list-style: none; margin:0; padding:0}
  li {padding: 5px; background:#eee}

  
  </style>
  
  
  
 <script type="text/jsx">

/** @jsx React.DOM */

React.initializeTouchEvents(true);


var TouchMixin = {
  touched: false,
  handleTouch: function(fn) {
    this.touched = true;
    typeof fn === 'string' ? this[fn]() : this.event(fn);
  },
  handleClick: function(fn) {
    if (this.touched) return this.touched = false;
    typeof fn === 'string' ? this[fn]() : this.event(fn);
  }
};


var placeholder = document.createElement("li");
placeholder.className = "placeholder";

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
    if(this.nodePlacement == "after") to++;
    data.splice(to, 0, data.splice(from, 1)[0]);
    this.setState({data: data});
  },
  dragOver: function(e) {
    e.preventDefault();
    this.dragged.style.display = "none";
    if(e.target.className == "placeholder") return;
    this.over = e.target;
    // Inside the dragOver method
    var relY = e.clientY - this.over.offsetTop;
    var height = this.over.offsetHeight / 2;
    var parent = e.target.parentNode;
    
    if(relY > height) {
      this.nodePlacement = "after";
      parent.insertBefore(placeholder, e.target.nextElementSibling);
    }
    else if(relY < height) {
      this.nodePlacement = "before"
      parent.insertBefore(placeholder, e.target);
    }
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
React.initializeTouchEvents(true);
React.renderComponent(
  <List data={colors} />,
  document.body
);
React.initializeTouchEvents(true);
</script>