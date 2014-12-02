<html>
 
  <title>Hello React</title>
  <script src="http://fb.me/react-0.12.1.js"></script>
  <script src="http://fb.me/JSXTransformer-0.12.1.js"></script>
  <script src="http://code.jquery.com/jquery-1.10.0.min.js"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/showdown/0.3.1/showdown.min.js"></script>
 
  
</head>
    
    
  </head>
  <body>
  <h1>VTK Meeting22</h1>
    <div id="content"></div>
    <script type="text/jsx">



var MeetingList = React.createClass({

 getInitialState: function() {
    return { show: false };
  },

  componentWillMount: function() {
    setInterval(this.toggle, 1500);
  },
  toggle: function() {
    this.setState({ show: !this.state.show });
  },


  render: function() {

console.log(5)
console.log( this.props)
console.log( this.props.data );
console.log( this.props.data.yearPlan );
console.log(6);


    var commentNodes = this.props.data.map(function (comment ,i ) {
console.log( comment.uid );


if(comment.uid=='M1415995827107_0.49422035430244315'){
console.log(11)
console.log(comment.meetingInfo)
console.log(comment.meetingInfo.type)
console.log(comment)

      return (

        <YearPlan  item={comment} key={i} author={comment.uid}>
			{comment.locationRef}
          {comment.meetingInfo.name}
		{comment.meetingInfo.meetingInfo["meeting short description"].str}
        </YearPlan>

      );
}
    });
    return (

      <div className="commentList">

        {commentNodes}

      </div>
    );
  }
});






var YearPlanComponent = React.createClass({

 getInitialState: function() {
    return { show: false };
  },

  componentWillMount: function() {
    setInterval(this.toggle, 1500);
  },
  toggle: function() {
    this.setState({ show: !this.state.show });
  },


  render: function() {

    var commentNodes = this.props.data;


    return (
      <div className="commentList">
        {commentNodes}
      </div>
    );
  }
});



var YearPlan = React.createClass({
  render: function() {
    return (
      <div className="comment">
        <h2 className="commentAuthor">
          {this.props.author}
        </h2>
        {this.props.children}
      </div>
    );
  }
});








var CommentBox = React.createClass({
  loadCommentsFromServer: function() {
    $.ajax({
      url: this.props.url,
      dataType: 'json',
      success: function(data) {
       this.setState({data: data.yearPlan.meetingEvents});
 	   this.setState({yp: data});

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
    this.loadCommentsFromServer();
    setInterval(this.loadCommentsFromServer, this.props.pollInterval);
  },
  render: function() {

	var x = this.state.data ? this.state.data : 'loading...';
	var y = this.state.yp ? this.state.yp.uid : 'loading year plan...';

    return (

      <div className="commentBox">
        <h1>View Meeting</h1>
       
 			<MeetingList data={x} />
       		<YearPlanComponent data={y} />
      </div>
    );
  }
});





React.render(
<CommentBox url="/content/girlscouts-vtk/controllers/vtk.controller.html?reactjs=asdf" pollInterval={20000} />,
  document.getElementById('content')
);




    </script>
  </body>
</html>

