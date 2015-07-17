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

<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.touch-punch.min.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/planView.js"></script>

<%
//final org.girlscouts.vtk.utils.ModifyNodePermissions modPerm = sling.getService(org.girlscouts.vtk.utils.ModifyNodePermissions.class);
//modPerm.modifyNodePermissions("/vtk2018", "vtk");
%>



<div id="vtkTabNav"></div>


 <div id="panelWrapper" class="row meeting-detail content">
  <div id="vtkNav"></div>
  <%@include file="include/modals/modal_help.jsp"%>

   <%if( hasPermission(troop, Permission.PERMISSION_EDIT_YEARPLAN_ID) ){%>
       <%@include file="include/view_yp_dropdown.jsp"%>
   <%} %>

  <div id="yearPlanMeetings">
    <div id="thePlan">




      <script type="text/javascript">





 var isActivNew;
      var isFirst=1;
      var meetingPassed=true;
      var scrollTarget = "";

      var CommentBox = React.createClass({displayName: "CommentBox",
       loadCommentsFromServer: function( isFirst ) {
    	 if (isFirst) {
             $.ajax({
                 url: this.props.url + '&isFirst=1',
                 dataType: 'json',
                 cache: false,
                 success: function(data) {
                	 console.info('data coming back');
                     this.setState({data:data});
                 }.bind(this),
             });
    	 } else {
	    	 getDataIfModified("year-plan.json", this, function(data, textStatus, req){
	    		// Skip if is 304.
	    		if (req.status == 200) {
		            this.setState({data:data});
	    		}

	    	 });
    	 }
       },
        getInitialState: function() {
          return {data: []};
        },
        componentDidMount: function() {

        	loadNav('plan');

          this.loadCommentsFromServer();
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
                      React.createElement(YearPlanComponents, {yearPlanName: yearPlanName, data: x, parentComponent: this})
                );
            }else if(this.state.data!=null && this.state.data.yearPlan !=null  && this.state.data.yearPlan =='NYP' &&  <%= !hasPermission(troop, Permission.PERMISSION_EDIT_YEARPLAN_ID) ? "true" :  "false" %>  ){
            	return React.createElement("h3", {className:"notice column large-22 large-centered medium-20 medium-centered small-21 small-centered"}, "The Year Plan has not yet been set up by the troop leader.");    
           
            }else if(this.state.data!=null && this.state.data.yearPlan !=null  && this.state.data.yearPlan =='NYP' &&  <%= hasPermission(troop, Permission.PERMISSION_EDIT_YEARPLAN_ID) ? "true" : "false" %>  ){
            	yesPlan();
                return React.createElement("h3",null);
            }else{
            	
            	return React.createElement("h3",null);
            }
            
        }
      });



       var YearPlanComponents = React.createClass({displayName: "YearPlanComponents",
        onReorder: function (order) {
        	var parent = this.props.parentComponent;
        	parent.setState({data: {}});
        	parent.loadCommentsFromServer(true);
        },
        render: function() {
          return (
                React.createElement("div", {id: "yearPlanMeetings", className: "columns"},
                      React.createElement("div", {className: "row"},
                        React.createElement("div", {className: "column large-20 medium-20 large-centered medium-centered"},
                              React.createElement("h1", {className: "yearPlanTitle"}, this.props.yearPlanName),
                              React.createElement("p", {className: "hide-for-print <%= hasPermission(troop, Permission.PERMISSION_EDIT_YEARPLAN_ID) ? "" : "hide" %> "}, "Drag and drop to reorder meetings")
                          )
                      ),
                        React.createElement(MeetingComponent, {key: this.props.data, data: this.props.data, onReorder: this.onReorder})
                )
           );
        } //end of render
      });


       var MeetingComponent = React.createClass({displayName: "MeetingComponent",
        render: function() {
            if( this.props.data!=null){
                var keys =  Object.keys( this.props.data );
                var obj = this.props.data;
                meetingPassed= true;
                return (React.createElement("ul", {id: "sortable123"},
                             keys.map( function (comment ,i ) {

                              if( obj[comment].type == 'MEETINGCANCELED' ){
                                     return (


             React.createElement("li", {className: 'row meeting ui-state-default ui-state-disabled'},
                     React.createElement("div", {className: "column large-20 medium-20 large-centered medium-centered"},
                     React.createElement("div", {}, React.createElement(DateBox, {comment: comment, obj: obj})),
                     React.createElement("div", {className: "large-22 medium-22 small-24 columns"},
                         React.createElement("p", {className: "subtitle"}, React.createElement(ViewMeeting, {date: moment(comment).toDate(), name: obj[comment].meetingInfo.name})),
                         React.createElement("p", {className: "category"}, obj[comment].meetingInfo.cat),
                         React.createElement("p", {className: "blurb"}, obj[comment].meetingInfo.blurb)

                     ),
                     React.createElement("div", {className: "large-2 medium-2 columns hide-for-small"},
                         React.createElement(MeetingImg, {mid: obj[comment].meetingInfo.id})
                     )
                     )
                 )



                                     );


                             }else if( obj[comment].type == 'MEETING' ){
                                        return (


        		React.createElement("li", {className:  <%if( !hasPermission(troop, Permission.PERMISSION_EDIT_YEARPLAN_ID) ){%> true || <%} %> (moment(comment) < moment( new Date()) && (moment(comment).get('year') >2000)) ? 'row meeting ui-state-default ui-state-disabled' : 'row meeting ui-state-default', key: obj[comment].id, id: obj[comment].id+1},
        			    React.createElement("div", {className: "column large-20 medium-20 large-centered medium-centered"},
    			        React.createElement("img", {className: (moment(comment) < moment( new Date()) && (moment(comment).get('year') >2000)) ? "touchscroll hide" : "touchscroll <%=hasPermission(troop, Permission.PERMISSION_EDIT_YEARPLAN_ID) ? "" : " hide" %>", src: "/etc/designs/girlscouts-vtk/clientlibs/css/images/throbber.png"}),
    			        React.createElement("div", {}, React.createElement(DateBox, {comment: comment, obj: obj})),
    			        React.createElement("div", {className: "large-22 medium-22 small-24 columns"},
    			            React.createElement("p", {className: "subtitle"}, React.createElement(ViewMeeting, {date: moment(comment).toDate(), name: obj[comment].meetingInfo.name})),
    			            React.createElement("p", {className: "category"}, obj[comment].meetingInfo.cat),
    			            React.createElement("p", {className: "blurb"}, obj[comment].meetingInfo.blurb)
    			        ),
    			        React.createElement("div", {className: "large-2 medium-2 columns hide-for-small"},
    			            React.createElement(MeetingImg, {mid: obj[comment].meetingInfo.id})
    			        )
        			    )
        			)



                                        );
                                  }else if( obj[comment].type == 'ACTIVITY' ){
                                        return (
React.createElement("li", {draggable: false, className: "row meeting activity ui-state-default ui-state-disabled", key: obj[comment].id},
  React.createElement("div", {className: "column large-20 medium-20 large-centered medium-centered"},
    React.createElement("div", {},
    React.createElement("div", {className: bgcolor(obj, comment, 0)},
        React.createElement("div", {className: "date"},
          React.createElement("p", {className: "month"},  moment(comment).get('year') < 1978 ? "" : moment(comment).format('MMM')),
          React.createElement("p", {className: "day"},  moment(comment).get('year') < 1978 ? "" : moment(comment).format('DD')),
          React.createElement("p", {className: "hour"},  moment(comment).get('year') < 1978 ? "" : moment(comment).format('hh:mm a'))
        )
      )
    ),
    React.createElement("div", {className: "large-22 medium-22 small-24 columns"},
      React.createElement("p", {className: "subtitle"},
        React.createElement(ViewMeeting, {date: moment(comment), name: obj[comment].name})
      ),
        React.createElement("p", {className: "category"},  obj[comment].content.replace('&nbsp;','').replace(/(<([^>]+)>)/ig,"") ),
        React.createElement("p", {className: "blurb"}, obj[comment].locationName.replace('&nbsp;','').replace(/(<([^>]+)>)/ig,""))
    ),
    React.createElement("div", {className: "large-2 medium-2 columns hide-for-small"})
  )
)

                                        );
                                  }else if( obj[comment].type == 'MILESTONE' && obj[comment].show){
                                        return (
                                        React.createElement("li", {className: "row milestone"},
  React.createElement("div", {className: "column large-20 medium-20 large-centered medium-centered"},
    React.createElement("span", null,  moment(comment).get('year') < 1978 ? "" : moment(comment).format('MM/DD/YY'), " ", obj[comment].blurb)
  )
)

                                        );
                                  }


                               })

                        )
                );
        }else{
          return React.createElement("div", null, React.createElement("img", {src: "/etc/designs/girlscouts-vtk/images/loading.gif"}))
             }
        },
      onReorder: function(order) {
        isActivNew=1;
        alert(1);
      },
      componentDidMount: function() {
        resizeWindow();
        link_bg_square();
        //loadNav('plan');

       if (Modernizr.touch) {
         scrollTarget = ".touchscroll";
       } else {


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
              doUpdMeeting1(yy, onReorder, order);
          },
          start: function(event, ui) {

        }
    }).disableSelection();
          
      },
      componentWillUpdate: function() {


       if (Modernizr.touch) {
         scrollTarget = ".touchscroll";
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
            doUpdMeeting1(yy, onReorder, order);
        },
        start: function(event, ui) {
      }
    }).disableSelection();
  }

});


      var MeetingImg = React.createClass({displayName: "MeetingImg",
        render: function() {
          var src= "/content/dam/girlscouts-vtk/local/icon/meetings/"+ this.props.mid +".png";
          var imgReturn="";
         if( !imageExists( src ) ){
            imgReturn="hide";
         }
          return (
                React.createElement("img", {src: src, className: imgReturn})
           );
        }
      });

    var ViewMeeting = React.createClass({displayName: "ViewMeeting",
        render: function() {
          var date  = new Date(this.props.date).getTime();
            var src = "/content/girlscouts-vtk/en/vtk.details.html?elem="+date;
          return (
              React.createElement("a", {href: src}, this.props.name)
          );
        }
      });

    function doUpdMeeting1(newVals, callback, callbackArgs){
        var x =$.ajax({
            url: '/content/girlscouts-vtk/controllers/vtk.controller.html?act=ChangeMeetingPositions&isMeetingCngAjax='+ newVals, // JQuery loads serverside.php
            data: '',
            dataType: 'html',
        }).done(function( html ) {
        	vtkTrackerPushAction('MoveMeetings');
        	console.info('Before calling callback');
        	if (callback) {
        		console.info('Calling callback');
        		callback(callbackArgs);
        	}
        });
    }

      React.render(
        React.createElement(CommentBox, {url: "/content/girlscouts-vtk/controllers/vtk.controller.html?yearPlanSched=X", pollInterval: 10000}),
          document.getElementById('thePlan')
        );

        function bgcolor(obj, comment, objType){

	if( obj[comment].cancelled =='true' ){

            return "bg-square canceled";

        }else if(  meetingPassed &&
             ((moment(comment) > moment( new Date() )) || (moment(comment).get('year') < 1978) )
             ) {
                    if( objType=='1'){  meetingPassed= false;}

                            return "bg-square current";


         }else if(   moment(comment).get('year') < 1978 ){
            return "bg-square";


        }else if(  moment(comment) < moment( new Date()) ){
            return "bg-square passed";

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
 var DateBox = React.createClass({displayName: "DateBox",
        render: function() {


            var obj = this.props.obj;
            var comment= this.props.comment;

      return (
        React.createElement("div", {className: bgcolor(obj, comment, 1)},
        React.createElement("div", {className:  (moment(comment).get('year') < 1978 || obj[comment].type == 'MEETINGCANCELED' ) ?  "hide" : "count"}, (obj[comment].id)+1),
        React.createElement("div", {className: "date"},
          React.createElement("p", {className: "month"},  moment(comment).get('year') < 1978 ? "meeting" : moment(comment).format('MMM')),
          React.createElement("p", {className: "day"},  moment(comment).get('year') < 1978 ? (obj[comment].id)+1 : moment(comment).format('DD')),
          React.createElement("p", {className: "hour"},  moment(comment).get('year') < 1978 ? "" : moment(comment).format('hh:mm a'))
        )
      )
           );
        }
      });

/*
        //when dialog resizes we need to place is in the middle of the page.
       $(window).resize(function() {
         dWidth = $('.vtk-body .ui-dialog.modalWrap').width();
         if($(window).width() > 920) {
           $('.vtk-body .ui-dialog.modalWrap').css({'max-width':$(window).width()/2, 'width':'100%', 'left': ($(window).width()-dWidth)/2});
         } else {
           $('.vtk-body .ui-dialog.modalWrap').css({'max-width':$(window).width()/2, 'width':'100%', 'left': '0 !important'});
         }
       });

*/


      </script>

    </div>
  </div>
</div><!--/panelWrapper-->




