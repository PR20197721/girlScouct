  <script src="http://fb.me/react-0.12.1.js"></script>
  <script src="http://fb.me/JSXTransformer-0.12.1.js"></script>
 
  
  
<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.touch-punch.min.js"></script>


<script type="text/jsx">

var SortableList = React.createClass({
	getInitialState: function() {
		return {
			items: this.props.items 
		};
	},

	onReorder: function (order) {
console.log("onReorder");
console.log( order );
		this.setState({items: order});
	},

	render: function () {
console.log(33);
console.log( this.state.items );
		return <SortableListItems items={this.state.items} onReorder={this.onReorder}/>;
	}
});

var SortableListItems = React.createClass({
 	render: function() {
        return <ul className="list-group">
        	{this.props.items.map(function(item) {
        		return <li id={item} className="list-group-item">
                   {item}
                </li>;
        	})}
        </ul>;
    },
    componentDidMount: function() {
console.log(1);
    	var dom = $(this.getDOMNode());
    	var onReorder = this.props.onReorder;
        dom.sortable({
        	stop: function (event, ui) {
console.log(2);
        		var order = dom.sortable("toArray", {attribute: "id"});
        		dom.sortable("cancel");
        		onReorder(order);
        	}
        });
    }
});

 
React.renderComponent(<SortableList items={['item 1', 'item 2', 'item 3']}/>, document.body);

</script>