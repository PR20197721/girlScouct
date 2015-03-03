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

<!--  2/1/15 link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" / --> 
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.touch-punch.min.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/planView.js"></script>

 

<%@include file="include/tab_navigation.jsp"%>


 <div id="panelWrapper" class="row meeting-detail content">
   <%@include file="include/utility_nav.jsp"%>
   <%@include file="include/view_yp_dropdown.jsp"%>
  <div id="yearPlanMeetings">
    <div id="thePlan">

    <script type="text/jsx">
    	var isActivNew;
    	var isFirst=1;
      var meetingPassed=false;
      var scrollTarget = "";
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
      			{  this.loadCommentsFromServer() ; }
        },
        render: function() {
        	var x, yearPlanName;   	
        	if( this.state.data.schedule!=null) {
        	   x =  this.state.data.schedule;
  			     yearPlanName = this.state.data.yearPlan;       
        		  return (
         			  <YearPlanComponents yearPlanName={yearPlanName} data={x} /> 
        	    );
        	} else {
        		return <div></div>;
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
      					<MeetingComponent key={this.props.data} data={this.props.data} onReorder={this.onReorder}/> 
      			</div>			
  	       );
        } //end of render
      });

      var MeetingComponent = React.createClass({
        render: function() {
    		if( this.props.data!=null){
    			var keys =  Object.keys( this.props.data );
    			var obj = this.props.data;
    			return (<ul id="sortable123">
            
    						{ keys.map( function (comment ,i ) {

                    if(  ( obj[comment].type == 'MEETING')  &&
                      moment(comment) < moment( new Date()) )
                        {meetingPassed= true;}


    							  if( obj[comment].type == 'MEETING' ){
    									return <%@include file="include/view_meeting.jsp" %> 
    							  }else if( obj[comment].type == 'ACTIVITY' ){
    									return <%@include file="include/view_activity.jsp" %>
    							  }else if( obj[comment].type == 'MILESTONE' && obj[comment].show){
    									return <%@include file="include/view_milestone.jsp" %>
    							  }


    						   })
    						}
    					</ul>
     			);
        }else{
          return <div><img src="/etc/designs/girlscouts-vtk/images/loading.gif"/></div>        
    		 }  		
        },
      onReorder: function(order) {
  		isActivNew=1;
        alert(1);
      },
      componentDidMount: function() {
        resizeWindow();
        link_bg_square();

      if (Modernizr.touch) {
        scrollTarget = ".touchscroll";
      } else {
        // $(".touchscroll").hide();
      }

          var dom = $(this.getDOMNode());
          var onReorder = this.props.onReorder;
          dom.sortable({
          items: "li:not(.ui-state-disabled)",

          delay:150,
          distance: 5,
          opacity: 0.5 ,
          scroll: true,
          scrollSensitivity: 10 ,
          tolerance: "intersect" ,
          handle: scrollTarget,
          helper:'clone',

          stop: function (event, ui) {
            var order = dom.sortable("toArray", {attribute: "id"});
            var yy  = order.toString().replace('"',''); 
              doUpdMeeting1(yy);
              onReorder(order);
          },
          start: function(event, ui) {
          //$(ui.item).sortable('cancel');  
          //dom.sortable('cancel');           
        }
    }).disableSelection();
      },
      componentWillUpdate: function() {
      if (Modernizr.touch) {
        // touch device
        scrollTarget = ".touchscroll";
      } else {
        // $(".touchscroll").hide();
      }

        var dom = $(this.getDOMNode());
        var onReorder = this.props.onReorder;
        dom.sortable({
        items: "li:not(.ui-state-disabled)",

        delay:150,
        distance: 5,
        opacity: 0.5 ,
        scroll: true,
        scrollSensitivity: 10 ,
        tolerance: "intersect" ,
        handle: scrollTarget,
        helper:'clone',


        stop: function (event, ui) {
        	var order = dom.sortable("toArray", {attribute: "id"});
        	var yy  = order.toString().replace('"','');
            doUpdMeeting1(yy); 
            onReorder(order);
        },
        start: function(event, ui) {
           // dom.sortable('cancel');       
      }
    }).disableSelection();
  }
});

    var MeetingImg = React.createClass({
        render: function() {
  		  var src= "/content/dam/girlscouts-vtk/local/icon/meetings/"+ this.props.mid +".png";

          var imgReturn="";
         if( !imageExists( src ) ){ 
            imgReturn="hide"; 
         }
          return (
    	     	<img src={src} className={imgReturn}/>
           );        
        }
      });

    var ViewMeeting = React.createClass({
        render: function() {
          var date  = new Date(this.props.date).getTime();
  		    var src = "/content/girlscouts-vtk/en/vtk.details.html?elem="+date;
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

        function bgcolor(obj, comment){ 

         if(  moment(comment).get('year') < 1978 ){
            return "bg-square";
         }else if(  moment(comment) < moment( new Date()) ){
            return "bg-square passed";
         }else if(meetingPassed && 
            moment(comment) > moment( new Date())) {
          meetingPassed= false;
          return "bg-square current";
         }else if( obj[comment].cancelled =='true' ){
            return "bg-square canceled";
         }else{
            return "bg-square";
         }
        }

        function imageExists(image_url) {

          var http = new XMLHttpRequest();

          http.open('HEAD', image_url, false);
          http.send();

          return http.status != 404;
        }
      </script>  
    </div>
  </div>
</div><!--/panelWrapper-->





