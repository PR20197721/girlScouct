<%@ page
  import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />

<%@include file="include/session.jsp"%>
<%
  String activeTab = "planView";
    boolean showVtkNav = true;
    
	

%>


<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />
<script src="http://fb.me/react-0.12.1.js"></script>
<script src="http://fb.me/JSXTransformer-0.12.1.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.touch-punch.min.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/planView.js"></script>
<script src="http://fb.me/react-with-addons-0.12.1.js"></script>

<%@include file="include/tab_navigation.jsp"%>



    
     
<div id="panelWrapper" class="row content meeting-detail">

  <script type="text/jsx">

	var isActivNew;
	var isFirst;

    var CommentBox = React.createClass({
     loadCommentsFromServer: function( isFirst ) {
	   console.log("loading..");
       $.ajax({

          url: this.props.url + 
    		(isActivNew==1 ? ("&isActivNew="+ isActivNew) : '')+
    		(isFirst ==1 ? ("&isFirst="+ isFirst) : ''),
          dataType: 'json',
          cache: false,
          success: function(data) {
          	this.setState({data:data.yearPlan});
      	  }.bind(this),
          error: function(xhr, status, err) {
            //-console.error(this.props.url, status, err.toString());
          }.bind(this)
        });
    	if( isActivNew ==1 ){
    		isActivNew=2;
    	}else if( isActivNew ==2 ){
    		isActivNew=0;
    	}
     },
      getInitialState: function() {
        return {data: []};
      },
      componentDidMount: function() {
        this.loadCommentsFromServer(1);
        setInterval( this.loadCommentsFromServer, this.props.pollInterval);
        setInterval( this.checkLocalUpdate, 1000);
      },
      checkLocalUpdate: function(){
      	if( (isActivNew == 1) || (isActivNew == 2) )
    			{ this.loadCommentsFromServer() ; }
      },
      render: function() {
    	var x;
    	var sched;
    	if( this.state.data.meetingEvents!=null){
    		x =  this.state.data.meetingEvents;
            sched = this.state.data.schedule;
    		return (
     			 <YearPlanComponents data={x} schedule={sched} /> 
    	    );
    	}else{
    		return <div>loading meeting plans...</div>;
    	}
      }
    });



 var YearPlanComponents = React.createClass({
      getInitialState: function() {
        return { show: false };
      },
      toggle: function() {
        this.setState({ show: !this.state.show });
      },
      render: function() {
       var scheduleDates = this.props.schedule.dates;
       var commentNodes = this.props.data.map(function (comment ,i ) {
      

console.log( comment );
       if( scheduleDates !=null ){
    		var scheduleDatesArray = scheduleDates.split(',');
    		thisMeetingDate =  scheduleDatesArray[comment.id] ;
    	}

    	/*
		thisMeetingRefId  = comment.refId;
    	thisMeetingPath  = comment.path;
    	thisMeetingImg   = "/content/dam/girlscouts-vtk/local/icon/meetings/"+ comment.meetingInfo.id +".png";
    	thisMeetingDate = new Date( Number(thisMeetingDate) );
*/
        return (
            <div>{comment.meetingInfo.name}</div>
          );

     

        }); //end of loop

        return ( <div className="commentList">{commentNodes}</div>    );
      } //end of render
    });



    
    React.render(
    <CommentBox url="/content/girlscouts-vtk/controllers/vtk.controller.html?reactjs=asdf" pollInterval={10000} />,
      document.getElementById('panelWrapper')
    );


    </script>
   
  
</div><!--/panelWrapper-->





