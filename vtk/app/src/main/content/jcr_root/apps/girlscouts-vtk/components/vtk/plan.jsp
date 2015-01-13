<%@ page
  import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<!-- PAGEID :: ./app/src/main/content/jcr_root/apps/girlscouts-vtk/components/vtk/plan.jsp -->
<cq:defineObjects />
<%@include file="include/session.jsp"%>
<%
    String activeTab = "plan";
    boolean showVtkNav = true;
%>
<!-- <script src="http://code.jquery.com/jquery-1.9.1.js"></script> -->
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />
<!-- <link rel="stylesheet" href="/etc/designs/girlscouts-vtk/clientlibs/css/vtk-style.css" /> -->
<script src="http://fb.me/react-0.12.1.js"></script>
<script src="http://fb.me/JSXTransformer-0.12.1.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.touch-punch.min.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/planView.js"></script>
<script src="http://fb.me/react-with-addons-0.12.1.js"></script>

<%@include file="include/tab_navigation.jsp"%>
 
 <div id="panelWrapper" class="row meeting-detail content">
 <%@include file="include/utility_nav.jsp"%>
 <%@include file="include/view_yp_dropdown.jsp"%>

<div id="thePlan">

  <script type="text/jsx">
  	var isActivNew;
  	var isFirst=1;
    
    var CommentBox = React.createClass({
     loadCommentsFromServer: function( isFirst ) {
     console.log("loading.." + (isActivNew) +" : "+isFirst+ " : "+(isFirst ==1));
       $.ajax({
          url: this.props.url + 
    		( (isActivNew==1 || isActivNew==2) ? ("&isActivNew="+ isActivNew) : '')+
    		(isFirst ==1 ? ("&isFirst="+ isFirst) : ''),
          dataType: 'json',
          cache: false,
          success: function(data) {
          	this.setState({data:data});

		if( isActivNew ==1 ){
    		isActivNew=2;
    	}else if( isActivNew ==2 ){
    		isActivNew=0;
    	}

      	  }.bind(this),
          error: function(xhr, status, err) {
            //-console.error(this.props.url, status, err.toString());
          }.bind(this)
        });
/*
    	if( isActivNew ==1 ){
    		isActivNew=2;
    	}else if( isActivNew ==2 ){
    		isActivNew=0;
    	}
*/
     },
      getInitialState: function() {
        return {data: []};
      },
      componentDidMount: function() {
        this.loadCommentsFromServer(1);
        setInterval( this.loadCommentsFromServer, this.props.pollInterval);
        setInterval( this.checkLocalUpdate, 100);
      },
      checkLocalUpdate: function(){
      	if( (isActivNew == 1) || (isActivNew == 2) )
    			{ console.log("code: "+isActivNew); this.loadCommentsFromServer() ; }
      },
      render: function() {
      	var x, yearPlanName;   	
      	if( this.state.data.schedule!=null){
      		x =  this.state.data.schedule;
			yearPlanName = this.state.data.yearPlan;       
      		return (
       			 <YearPlanComponents yearPlanName={yearPlanName} data={x} /> 
      	    );
      	} else {
      		return <div>loading meeting plans...</div>;
      	}
      }
    });
   var YearPlanComponents = React.createClass({      
      onReorder: function (order) {
		isActivNew=1;
      },
      render: function() {
        return ( 
			<div id="yearPlanMeetings" className="columns">
				  <div className="row">
				    <div className="column large-20 medium-20 large-centered medium-centered">
  					  <h1 className="yearPlanTitle">{this.props.yearPlanName}</h1>
  					  <p className="hide-for-print">Drag and drop to reorder meetings</p> 
					  </div>
				  </div>
					<MeetingComponent key={this.props.data} data={this.props.data} onReorder={this.onReorder} /> 
			</div>			
	    );
      } //end of render
    });

  var MeetingComponent = React.createClass({
    render: function() {
		if( this.props.data!=null){
			var keys =  Object.keys( this.props.data );
			var obj = this.props.data;
			return (<ul>
        
						{ keys.map( function (comment ,i ) {
							  if( obj[comment].type == 'MEETING' ){
									return <%@include file="include/view_meeting.jsp" %> 
							  }else if( obj[comment].type == 'ACTIVITY' ){
									return <%@include file="include/view_activity.jsp" %>
							  }else if( obj[comment].type == 'MILESTONE' ){
									return <%@include file="include/view_milestone.jsp" %>
							  }
						   })
						}
					</ul>
 			);
         }else{
		 	
         	 return <div><img src="http://sgsitsindore.in/Images/wait.gif"/></div>
        
		 }  		
        },
    onReorder: function(order) {
		isActivNew=1;
alert(1);
    },
    componentDidMount: function() {
        var dom = $(this.getDOMNode());
        var onReorder = this.props.onReorder;
        dom.sortable({
          stop: function (event, ui) {
            var order = dom.sortable("toArray", {attribute: "id"});
            var yy  = order.toString().replace('"',''); 
	
			doUpdMeeting1(yy);
            onReorder(order);
          }
        });
    },
    componentWillUpdate: function() {
      var dom = $(this.getDOMNode());
      var onReorder = this.props.onReorder;
      dom.sortable({
          stop: function (event, ui) {
          	var order = dom.sortable("toArray", {attribute: "id"});
          	var yy  = order.toString().replace('"','');
			doUpdMeeting1(yy); 
  			onReorder(order);
          }
      });
    }
  });

  var MeetingImg = React.createClass({
      render: function() {
		var src= "/content/dam/girlscouts-vtk/local/icon/meetings/"+ this.props.mid +".png";
        return (
    		<img src={src}/>
        );
      }
    });

  var ViewMeeting = React.createClass({
      render: function() {
		var src= "/content/girlscouts-vtk/en/vtk.details.html?elem="+ new Date(this.props.date).getTime();
        return (
    		<a href={src}>{this.props.name}</a>
        );
      }
    });

function doUpdMeeting1(newVals){
	var x =$.ajax({ 
		url: '/content/girlscouts-vtk/controllers/vtk.controller.html?act=ChangeMeetingPositions&isMeetingCngAjax='+ newVals, // JQuery loads serverside.php
		data: '', 
		dataType: 'html', 
	}).done(function( html ) { });
		
		
}
  React.render(
    <CommentBox url="/content/girlscouts-vtk/controllers/vtk.controller.html?yearPlanSched=X" pollInterval={10000} />,
      document.getElementById('thePlan')
    );
  </script>  
</div>
</div><!--/panelWrapper-->





