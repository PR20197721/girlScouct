
    
 <!DOCTYPE html>
<html>
 <head>
  <title>Hello React</title>
  <script src="http://fb.me/react-0.12.1.js"></script>
  <script src="http://fb.me/JSXTransformer-0.12.1.js"></script>
  <script src="http://code.jquery.com/jquery-1.10.0.min.js"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/showdown/0.3.1/showdown.min.js"></script>
</head>
  <body>
  <h1>test</h1>
    <div id="content"></div>
  <script type="text/jsx">

    var data = [
                {author: "Pete Hunt", text: "This is one comment"},
                {author: "Jordan Walke", text: "This is *another* comment"}
              ];
    
    var CommentBox = React.createClass({
    	  getInitialState: function() {
    	    return {data: []};
    	  },
    	  componentDidMount: function() {
    	    $.ajax({
    	      url: '/content/girlscouts-vtk/controllers/vtk.rand.test13_2.html',
    	      dataType: 'json',
    	      success: function(data) {
alert(1)
console.log(data)
    	        this.setState({data: data});
    	      }.bind(this),
    	      error: function(xhr, status, err) {
alert(2);    	
        console.error(this.props.url, status, err.toString());
    	      }.bind(this)
    	    });
    	  },
    	  render: function() {
    	    return (
    	      <div className="commentBox">
    	      
    	        {this.state.data}
    	       
    	      </div>
    	    );
    	  }
    	});

    
    React.render(
    		  <CommentBox data={data} />,
    		 $('#content').get(0)
    		);
    </script>
  </body>
</html>
</html>

