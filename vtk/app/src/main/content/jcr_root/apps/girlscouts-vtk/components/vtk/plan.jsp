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
  String sectionClassDefinition = "meeting-detail";
%>
  <%@include file="include/bodyTop.jsp" %>
  <%@include file="include/modals/modal_help.jsp"%>
       
  <div id="vtk_banner2234" data-cached="<%=session.getAttribute("isHideVtkBanner")!=null ? "yes" : "no" %>"  class="column medium-20 small-24 small-centered" style="display:none;">
  </div>


<script>
$(function(){
  callExecuteBannerSlider()
})


</script>



   <%if( VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_YEARPLAN_ID) ){%>
       <%@include file="include/view_yp_dropdown.jsp"%>
   <%} %>



  <div id="yearPlanMeetings" class="<%= (user.getCurrentYear().equals( VtkUtil.getCurrentGSYear()+"") ) ? "vtk-currentYear-plan year_plan" : "vtk-pastYear-plan year_plan" %>">


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
                     this.isReordering = false;
                 }.bind(this),
             });
         } else if(  <%=user.getCurrentYear().equals( VtkUtil.getCurrentGSYear()+"" ) %> ){


             getDataIfModified("year-plan.json", this, function(data, textStatus, req){
                // Skip if is 304.
                // Skip if is reordering.
                if (req.status == 200 && !this.isReordering) {
                    this.setState({data:data});
                }

             });

         }
       },
        getInitialState: function() {
          return {data: []};
        },

        pollIntervalID: null,
        isReordering: false,
        componentDidMount: function() {
            loadNav('plan');


     

          // Need to skip dispatcher cache for the first time load.
          this.loadCommentsFromServer(true);
          this.pollIntervalID = setInterval( this.loadCommentsFromServer, this.props.pollInterval);
          setInterval( this.checkLocalUpdate, 100);
        },
        delayReload: function() {
            // Set a flag to indicating reordering in progress.
            this.isReordering = true;

            // Extend 10 seconds for the next poll.
            if (this.pollIntervalID != null) {
                clearTimeout(this.pollIntervalID);
            }

            this.pollIntervalID = setInterval( this.loadCommentsFromServer, this.props.pollInterval);
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
            }else if(this.state.data!=null && this.state.data.yearPlan !=null  && this.state.data.yearPlan =='NYP' &&  <%= !VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_YEARPLAN_ID) ? "true" :  "false" %>  ){
                return React.createElement("h3", {className:"notice column large-22 large-centered medium-20 medium-centered small-21 small-centered"}, "Hello! Your girl's Troop Leader has not yet set up the troop's Year Plan. Please contact the Troop Leader for more info on their use of the Volunteer Toolkit.");

            }else if(this.state.data!=null && this.state.data.yearPlan !=null  && this.state.data.yearPlan =='NYP' &&  <%= VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_YEARPLAN_ID) ? "true" : "false" %>  ){
                yesPlan.auto();
                return React.createElement("h3",null);
            }else{

                return React.createElement("h3",null);
            }

        }
      });
      var YearPlanComponents = React.createClass({displayName: "YearPlanComponents",
        onReorder: function (order) {
            // Reordering
            var parent = this.props.parentComponent;

            // Delay reload
            parent.delayReload();

            // Go ahead to reorder
            parent.setState({data: {}});
            parent.loadCommentsFromServer(true);
        },
        render: function() {
          return (
                React.createElement("div", {id: "yearPlanMeetings", className: "columns"},
                      React.createElement("div", {className: "row"},
                        React.createElement("div", {className: "column large-20 medium-20 large-centered medium-centered"},
                              React.createElement("h1", {className: "yearPlanTitle"}, this.props.yearPlanName),
                              React.createElement("p", {className: "hide-for-print <%= VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_YEARPLAN_ID) ? "" : "hide" %> "}, "Drag and drop to reorder meetings")
                          )
                      ),
                        React.createElement(MeetingComponent, {key: this.props.data.yearPlanName+(new Date()).getTime(), data: this.props.data, onReorder: this.onReorder})
                )
           );
        } //end of render
      });

      var outdoorIcon = React.createClass({
    	   displayName:"outdoorIcon",

    	   render:function(){


    		   var isOutdoorAvailable = this.props.isOutdoorAvailable,imgName;

               if (this.props.isOutdoor) {
                 imgName =  "outdoor.png";
              } else {
                  imgName = "indoor.png";
              }

               var options = {
                 className: 'outdoor-icon has-tip tip-top radius',
                 src:'/etc/designs/girlscouts-vtk/clientlibs/css/images/'+imgName,
                 style:{
                    width:'45px',
                    border:'none',
                    cursor:'pointer',
                    paddingTop:'3px'
                 },
                 "data-tooltip":true,
                 "aria-haspopup":true,
                 title:"<b>Get Girls Outside!</b>"
               }

    		   if (isOutdoorAvailable) {
    				   return (React.createElement("img", options));
    		   }
			   return null;
    	   }
    	})



      var DirectCalendar = React.createClass({
        displayName:'DirectCalendar',
        close: function(){
          this.props.close()
        },
        getInitialState: function(){
          return {
            html:'<div>...Loading</div>'
          }
        },
        componentDidMount(){
          var _this = this;

          $.ajax({
            method:'GET',
            url: '/content/girlscouts-vtk/controllers/vtk.sched.html?elem='+ this.props.time
          }).done(function(html){
            _this.setState({html:html}, function(){
              $( "#vtk-direct-calendar" ).datepicker({
                defaultDate: $( "#vtk-direct-calendar" ).data().date.trim()
              }).on('change', function(e){debugger;console.log(e,this)})
              })
          }).fail(function(){
            _this.setState({html:'<div><p>We Have A problem please if the problem continue</p></div>'})
          })

        },
        componentWillUnmount:function(){
          $( "#vtk-direct-calendar" ).datepicker('destroy');
        },
        render: function(){
          var _this = this;

          function calculateLeft(left){
            return left + 120;
          }
          function calculateTop(top){
            return top - 100;
          }

          return (React.createElement('div',{
            className:'vtk-direct-calendar',
            style:{
              top: calculateTop(this.props.element.clientY),
              left: calculateLeft(this.props.element.clientX)
            }
            },
            React.createElement('div',{
              style:{
                position:'absolute',
                top:'5px',
                right:'5px',
                zIndex:'1001'
              },
              onClick:function(){
                _this.close()
              }
              },'close'),
              React.createElement('div',{dangerouslySetInnerHTML: {__html: this.state.html}})
          ));
        }
      })



       var MeetingComponent = React.createClass({
        displayName: "MeetingComponent",
        getInitialState: function(){
          return {
            element:{},
            time:undefined,
          }
        },

        render: function() {  
          var _this = this;

            function closeModal(){
              _this.setState({
                element:{},
                time: undefined
              })
            }

           function openModal(options, time, comment){
            if(moment.tz(_this.props.data[comment],"America/New_York").get('year') > 1978){
              _this.setState({
                element:options,
                time:time
              })
            }
          }


        if( this.props.data!=null){
          var keys =  Object.keys( this.props.data );
          var obj = this.props.data;
          meetingPassed= true;
          return (React.createElement('div',{},
                        (this.state.time)?React.createElement(DirectCalendar,{element:this.state.element,time:this.state.time, close:closeModal}):null,
                        React.createElement("ul", {id: "sortable123"},
                        keys.map( function (comment ,i ) {
                          if( obj[comment].type == 'MEETINGCANCELED' ){

                                return (

                                    React.createElement("li", {className: 'row meeting ui-state-default ui-state-disabled'},
                                            React.createElement("div", {className: "column large-20 medium-20 large-centered medium-centered"},
                                            React.createElement("div", {}, React.createElement(DateBox, {comment: comment, obj: obj, openModal:openModal})),
                                            React.createElement("div", {className: "large-22 medium-22 small-24 columns"},
                                                /* React.createElement(outdoorIcon, {isOutdoorAvailable: obj[comment].anyOutdoorActivityInMeetingAvailable, isOutdoor: obj[comment].anyOutdoorActivityInMeeting}), */
                                                React.createElement("p", {className: "subtitle"}, React.createElement(ViewMeeting, {isOutdoor:  obj[comment].anyOutdoorActivityInMeeting, dateRaw:comment, date: moment(comment).toDate(), name: obj[comment].meetingInfo.name})),
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
                                    React.createElement("li", {className:  <%if( !VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_YEARPLAN_ID) ){%> true || <%} %> (moment(comment) < moment( new Date()) && (moment(comment).get('year') >2000)) ? 'row meeting ui-state-default ui-state-disabled' : 'row meeting ui-state-default', key: obj[comment].id, id: obj[comment].id+1},
                                      React.createElement("div", {className: "column large-20 medium-20 large-centered medium-centered"},
                                      React.createElement("img", {className: (moment(comment) < moment( new Date()) && (moment(comment).get('year') >2000)) ? "touchscroll hide" : "touchscroll <%= VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_YEARPLAN_ID) ? "" : " hide" %>", src: "/etc/designs/girlscouts-vtk/clientlibs/css/images/throbber.png"}),
                                      React.createElement("div", {}, React.createElement(DateBox, {comment: comment, obj: obj, openModal:openModal})),
                                      React.createElement("div", {className: "large-22 medium-22 small-24 columns"},
                                          React.createElement(outdoorIcon, {isOutdoorAvailable: obj[comment].anyOutdoorActivityInMeetingAvailable, isOutdoor: obj[comment].anyOutdoorActivityInMeeting}),
                                          React.createElement("p", {className: "subtitle"}, React.createElement(ViewMeeting, {isOutdoorAvailable:  obj[comment].anyOutdoorActivityInMeetingAvailable, isOutdoor:  obj[comment].anyOutdoorActivityInMeeting, dateRaw: comment, date: moment(comment).toDate(), name: obj[comment].meetingInfo.name})),
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
                                React.createElement("li", {draggable: false, className: "row meeting activity ui-state-default ui-state-disabled", key: obj[comment].uid},
                                  React.createElement("div", {className: "column large-20 medium-20 large-centered medium-centered"},
                                    React.createElement("div", {},
                                    React.createElement("div", {className: bgcolor(obj, comment, 0)},
                                        React.createElement("div", {className: "date"},
                                          React.createElement("p", {className: "month"},  moment.tz(comment,"America/New_York").get('year') < 1978 ? "" : moment.tz(comment,"America/New_York").format('MMM')),
                                          React.createElement("p", {className: "day"},  moment.tz(comment,"America/New_York").get('year') < 1978 ? "" : moment.tz(comment,"America/New_York").format('DD')),
                                          React.createElement("p", {className: "hour"},  moment.tz(comment,"America/New_York").get('year') < 1978 ? "" : moment.tz(comment,"America/New_York").format('hh:mm a'))
                                        )
                                      )
                                    ),
                                    React.createElement("div", {className: "large-22 medium-22 small-24 columns"},
                                      React.createElement("p", {className: "subtitle"},
                                        React.createElement(ViewMeeting, {dateRaw: comment, date: moment(comment), name: obj[comment].name})
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
                                React.createElement("span", null,  moment.tz(comment, "America/New_York").get('year') < 1978 ? "" : moment.tz(comment, "America/New_York").format('MM/DD/YY'), " ", obj[comment].blurb)
                              )
                            ) 

                                );
                          }
                        })
                      )
                    )
                  );
        }else{
          return React.createElement("div", null, React.createElement("img", {src: "/etc/designs/girlscouts-vtk/images/loading.gif"}))
        }
        },
      onReorder: function(order) {
        isActivNew=1;

      },
      componentDidMount: function() {
        resizeWindow();
        //--link_bg_square();
        //loadNav('plan');

       if (Modernizr.touch) {
         scrollTarget = ".touchscroll";
       } else {


       }

       $(document).foundation({
            tooltip: {
              selector : '.has-tip',
              additional_inheritable_classes : [],
              tooltip_class : '.tooltip',
              touch_close_text: 'tap to close',
              disable_for_touch: true,
              tip_template : function (selector, content) {
                return '<span data-selector="' + selector + '" class="'
                  + Foundation.libs.tooltip.settings.tooltip_class.substring(1)
                  + '">' + content + '<span class="nub"></span></span>';
              }
            }
          });




          var dom = $(ReactDOM.findDOMNode(this));
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


        var dom = $(ReactDOM.findDOMNode(this));
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
          var date  = new Date(this.props.dateRaw).getTime();
          if (date.toString() == 'NaN') {
              date = new Date(this.props.date).getTime();
          }
            var src = "<%=relayUrl %>/content/girlscouts-vtk/en/vtk.details.html?elem="+date;
          return (

              //React.createElement("a", {href: src}, this.props.name +":Outdoor available? "+this.props.isOutdoorAvailable+" Outdoor selected? : "+ this.props.isOutdoor)
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

      ReactDOM.render(
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
            var xx= moment.tz(comment,"America/New_York");
            console.log( obj[comment]);
     
            console.log(comment);
// var src="javascript:schedChanger('"+moment(comment).valueOf()+"')";÷

          var _this = this;
      return (        
    		  React.createElement("a",{
              onClick:function(event){
                   var e = event.currentTarget.offsetParent
                   event.preventDefault();
                    _this.props.openModal({
                    clientX:e.offsetLeft,
                    clientY:e.offsetTop
                  }, moment(comment).valueOf(), comment)
                        
              }
            },
			        React.createElement("div", {className: bgcolor(obj, comment, 1)},
			        React.createElement("div", {className:  (moment(comment).get('year') < 1978 || obj[comment].type == 'MEETINGCANCELED' ) ?  "hide" : "count"}, (obj[comment].id)+1),
			        React.createElement("div", {className: "date"},
			          React.createElement("p", {className: "month"},  moment.tz(comment,"America/New_York").get('year') < 1978 ? "meeting" : moment.tz(comment,"America/New_York").format('MMM')),
			          React.createElement("p", {className: "day"},  moment.tz(comment,"America/New_York").get('year') < 1978 ? (obj[comment].id)+1 : moment.tz(comment,"America/New_York").format('DD')),
			          React.createElement("p", {className: "hour"},  moment.tz(comment,"America/New_York").get('year') < 1978 ? "" : moment.tz(comment,"America/New_York").format('hh:mm a'))
			        )
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



  <!-- Hack for NUB in Tooltip -->
<style>
  .tooltip span.nub{
    left: 18px;
  } 
</style>

<%@include file="include/bodyBottom.jsp" %>
