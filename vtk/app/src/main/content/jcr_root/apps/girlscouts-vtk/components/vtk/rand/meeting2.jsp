<script src="http://fb.me/react-0.10.0.js"></script>
<script src="http://fb.me/JSXTransformer-0.10.0.js"></script>
<script src="http://code.jquery.com/jquery-1.11.0.min.js"></script>

<script type="text/jsx">
	/*** @jsx React.DOM */
	var weatherWidget = React.createClass({
		loadData: function(){
		    $.ajax({
				url: '/content/girlscouts-vtk/controllers/vtk.rand.test13_3.html',
		    	dataType : "json",
		    	cache: false,
		    	success: function(data) {
			      	console.log(data)
                    this.setState({data: data.yearPlan});
		      }.bind(this)
		    });
		},
		getInitialState: function(){
			return {item: null};
			},
			componentWillMount: function(){
				this.loadData();
			},
 		render: function(){
            var degrees = this.state.data ? this.state.data.test.uid : 'loading6...';
			return(
				<div className="ww-container">
					<div className="ww-current-condition">
						<div className="ww-current-temperture">{degrees}&deg;</div>
					</div>
				</div>
			)
		}
	});

	React.renderComponent(<weatherWidget />, document.body);
</script>