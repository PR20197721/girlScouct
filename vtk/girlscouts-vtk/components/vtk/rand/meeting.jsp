
<%@ page
	import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.troop.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />

<%@include file="include/session.jsp"%>
<%

	org.girlscouts.vtk.models.PlanView planView = meetingUtil.planView1(  user,  troop,  request);
	//String mid= "M1417631220300_0.6255638440068995";
	String mid=planView.getYearPlanComponent().getUid();

%>

<html>
 
  <title>Hello </title>
  <!-- script src="http://fb.me/react-0.12.1.js"></script -->
  <!-- script src="http://fb.me/JSXTransformer-0.12.1.js"></script -->
  <!-- script src="http://code.jquery.com/jquery-1.10.0.min.js"></script -->
  <!-- script src="http://cdnjs.cloudflare.com/ajax/libs/showdown/0.3.1/showdown.min.js"></script -->
 
  
    
    
  <body>
  <h1>VTK Meeting <%=mid %></h1>
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


    var commentNodes = this.props.data.map(function (comment ,i ) {



if(comment.uid=='<%=mid%>'){

      return (

        <YearPlan  item={comment} key={i} author={comment.uid}>
			<br/>Location: {comment.locationRef}
            <br/>Name: {comment.meetingInfo.name}
		    <br/>Blurb: {comment.meetingInfo.meetingInfo["meeting short description"].str}

			<MeetingActivities data={comment.meetingInfo.activities} />

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








var MeetingActivities = React.createClass({

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

	
		  <YearPlan  item={comment.activityNumber} key={comment.activityNumber} author={comment.name}>
        <li key='{comment.activityNumber}' >
			
			{comment.duration} ... {comment.activityNumber}
        </li>
	</YearPlan>


      );

    });
    return (
      <ol>
        {commentNodes}
      </ol>
    );
  }
});









var YearPlanComponent = React.createClass({

 getInitialState: function() {
    return { show: false };
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

 loadCommentsFromServer: function( isFirst ) {


   $.ajax({
      url: this.props.url + (isFirst==1 ? ("&isFirst="+ isFirst) : ''),
      dataType: 'json',
cache: false,
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
    this.loadCommentsFromServer(1);
    setInterval( this.loadCommentsFromServer, this.props.pollInterval);
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

