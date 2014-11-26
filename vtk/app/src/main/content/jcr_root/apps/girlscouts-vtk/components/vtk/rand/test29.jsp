<html>
 
  <title>Hello React</title>
  <script src="http://fb.me/react-0.12.1.js"></script>
  <script src="http://fb.me/JSXTransformer-0.12.1.js"></script>
  <script src="http://code.jquery.com/jquery-1.10.0.min.js"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/showdown/0.3.1/showdown.min.js"></script>
  
  <script src="http://cdnjs.cloudflare.com/ajax/libs/react/0.12.1/react-with-addons.js"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/react/0.12.1/JSXTransformer.js"></script>
  
</head>
    
    <style>
    
    .example-enter {
  opacity: 0.01;
  transition: opacity .5s ease-in;
}

.example-enter.example-enter-active {
  opacity: 1;
}

.example-leave {
  opacity: 1;
  transition: opacity .5s ease-in;
}

.example-leave.example-leave-active {
  opacity: 0.01;
}
    </style>
  </head>
  <body>
    <div id="content"></div>
    <script type="text/jsx">

var ReactCSSTransitionGroup = React.addons.CSSTransitionGroup;
   

var CommentForm = React.createClass({
  render: function() {
    return (
      <div className="commentForm">
        Hello, world! I am a CommentForm.
      </div>
    );
  }
});





// tutorial10.js
var CommentList = React.createClass({

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
    var commentNodes = this.props.data.map(function (comment ,i ) {
      return (

        <Comment item={comment} key={i} author={comment.author}>
          {comment.text}
        </Comment>

      );
    });
    return (

      <div className="commentList">
<ReactCSSTransitionGroup transitionName="example">
        {commentNodes}

 </ReactCSSTransitionGroup>
      </div>
    );
  }
});

// tutorial5.js
var Comment = React.createClass({
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

// tutorial6.js
var converter = new Showdown.converter();
var Comment = React.createClass({
  render: function() {
    return (
      <div className="comment">
        <h2 className="commentAuthor">
          {this.props.author}
        </h2>
        {converter.makeHtml(this.props.children.toString())}
      </div>
    );
  }
});

// tutorial7.js
var converter = new Showdown.converter();
var Comment = React.createClass({
  render: function() {
    var rawMarkup = converter.makeHtml(this.props.children.toString());
    return (
      <div className="comment">
        <h2 className="commentAuthor">
          {this.props.author}
        </h2>
        <span dangerouslySetInnerHTML={{__html: rawMarkup}} />
      </div>
    );
  }
});





// tutorial14.js
var CommentBox = React.createClass({
  loadCommentsFromServer: function() {
    $.ajax({
      url: this.props.url,
      dataType: 'json',
      success: function(data) {
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
    this.loadCommentsFromServer();
    setInterval(this.loadCommentsFromServer, this.props.pollInterval);
  },
  render: function() {
    return (
      <div className="commentBox">
        <h1>Comments</h1>
        <CommentList data={this.state.data} />
        <CommentForm />
      </div>
    );
  }
});



React.render(

<CommentBox url="/content/girlscouts-vtk/controllers/vtk.rand.test13.html" pollInterval={2000} />,
  document.getElementById('content')
);

function x(){
console.log(1 +" : "+ new Date());
var data = [
  {author: "asdfasdPete Hunt <%= new java.util.Date()%>", text: "This is one comment"},
  {author: "Jordan Walke", text: "This is *another* comment"}
];



React.render(
//<CommentBox url="/content/girlscouts-vtk/controllers/vtk.rand.test13.html" />,
//console.log(data); 
 // <CommentBox data={data} />,
 // document.getElementById('content')

<CommentBox url="/content/girlscouts-vtk/controllers/vtk.rand.test13.html" pollInterval={2000} />,
  document.getElementById('content')
);
	

//setTimeout(function(){x();}, 5000);
}


    </script>
  </body>
</html>

