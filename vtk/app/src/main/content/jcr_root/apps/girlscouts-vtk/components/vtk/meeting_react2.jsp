<!-- PAGEID :: ./app/src/main/content/jcr_root/apps/girlscouts-vtk/components/vtk/meeting_react2.jsp -->
<%

String mid = planView.getYearPlanComponent().getUid();
MeetingE meeting = planView.getMeeting();
pageContext.setAttribute("MEETING_PATH", meeting.getPath());
pageContext.setAttribute("PLANVIEW_TIME", Long.valueOf(planView.getSearchDate().getTime()));
pageContext.setAttribute("DETAIL_TYPE", "meeting");

String readonlyModeStr = VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_YEARPLAN_ID) &&
    VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_MEETING_ID) ? "false" : "true";

Cookie cookie = new Cookie("VTKReadonlyMode", readonlyModeStr);
cookie.setPath("/");
response.addCookie(cookie);

String elemParam = request.getParameter("elem");
if (elemParam == null) {
    elemParam = "first";
}
String meetingDataUrl = "meeting." + elemParam + ".json";
%>
 <script src="/etc/designs/girlscouts-vtk/clientlibs/js/planView.js"></script>



<script>
    var thisMeetingPath = "";


          //===== POLYFILL OLD BROwSER ====
          if (!Array.prototype.findIndex) {
              Array.prototype.findIndex = function(predicate) {
                'use strict';
                if (this == null) {
                  throw new TypeError('Array.prototype.findIndex called on null or undefined');
                }
                if (typeof predicate !== 'function') {
                  throw new TypeError('predicate must be a function');
                }
                var list = Object(this);
                var length = list.length >>> 0;
                var thisArg = arguments[1];
                var value;

                for (var i = 0; i < length; i++) {
                  value = list[i];
                  if (predicate.call(thisArg, value, i, list)) {
                    return i;
                  }
                }
                return -1;
              };
          }

          function collectionComponent(){
            var list = [];


            function add(id,item,component) {
              list.push({
                id:id,
                item:item,
                component:component
              })

              };
          function get(id,property) {

              return list[list.findIndex(function(e){return e.item[property] === id })];
            };

            return {
              add:add,
              get:get
            }
          }
          var outDoorIconList = new collectionComponent();
</script>
<%
  String sectionClassDefinition = "";
%>
<%@include file="include/bodyTop.jsp"%>
<%@include file="include/modals/modal_help.jsp"%>



      <div id="vtk_banner2234" data-cached="<%=session.getAttribute("isHideVtkBanner")!=null ? "yes" : "no" %>"  class="column medium-20 small-24 small-centered" style="display:none;">
      </div>




    <script>

    $.ajax({
        url: '/content/vtkcontent/en/vtk-banner.simple.html',
        type: 'GET',
        dataType:'html',
        data: {
            a: Date.now()
        },
        success: function(result) {

          var htmlResults = $(result);
          var vtkBannerSections = htmlResults.find('.vtk-banner.section')
          
          
          vtkBannerSections.each(function(x,y){ 
            if($(y).find('.vtk-banner-disabled').length){
                $(this).remove();
            }
          })

          $("#vtk_banner2234").html(htmlResults);
              if($("#vtk_banner2234").data('cached') === 'no'){
                $("#vtk_banner2234").show();
              }


              $('.vtk-banner-button').click(function(){

                  $.ajax({
                    url:'/content/girlscouts-vtk/controllers/vtk.controller.html?act=hideVtkBanner',
                    dataType:'html',
                  }).done(function(){
                   $('.vtk-banner-image').slideUp();
                })


              });



            $('.vtk-banner-container').slick({
              slidesToScroll: 1,
              adaptiveHeight: true,
              autoplaySpeed: 10000,
              autoplay:true
            })

        }
    });


    
	function setHeightSS(p){
		
		var image = $('.banner-image');
		var scroll = $('.scroll-banner');
		var height = $(window).height();
		var imageHeight;
		var modalwidth = $(p).innerWidth();
		var realimgheight = document.getElementById('banner-image').height;
		var realimgwidth = document.getElementById('banner-image').width;


			imageHeight = image.height();


		scroll.css(
			{
				'maxHeight':$(window).height()-imageHeight-75+'px',
				'overflow-y':'auto'
			}
		);




	}

    </script>

<%@include file="include/modals/modal_agenda.jsp"%>
<%@include file="include/modals/modal_meeting_reminder.jsp" %>
<%@include file="include/modals/modal_view_sent_emails.jsp"%>


  <div id="theMeeting">








    <script type="text/javascript">

    var printModal = new ModalVtk('print-modal');

    printModal.init();

    var cll = '<form class="print-modal" style="font-size:14px;padding:10px 20px 10px 20px;"><div style="" class="column small-24 medium-12"><input type="radio" name="whatToPrint" value="agenda"> Agenda <br /><input type="radio" name="whatToPrint" value="activity"> Activity Plan <br /></div><div  style="" class="column small-24 medium-12"><input type="radio" name="whatToPrint" value="meeting"> Meeting Overview <br /><input type="radio" name="whatToPrint" value="material"> Material List <br /></div><div class="vtk-js-modal_button_action vtk-js-modal_cancel_action  button tiny" style="margin-top:20px">Cancel</div><div class="vtk-js-modal_button_action vtk-js-modal_ok_action button tiny" style="margin-top:20px">Print</div></form>';



    function callPrintModal(){

        printModal.fillWith('What you would like to print?',cll, function(){

          var listPrintAdress = {
            agenda:'/content/girlscouts-vtk/controllers/vtk.pdfPrint.html?act=isAgenda&mid=<%=mid%>',
            activity:'/content/girlscouts-vtk/controllers/vtk.pdfPrint.html?act=isActivity&mid=<%=mid%>',
            meeting:'/content/girlscouts-vtk/controllers/vtk.pdfPrint.html?act=isOverview&mid=<%=mid%>',
            material:'/content/girlscouts-vtk/controllers/vtk.pdfPrint.html?act=isMaterials&mid=<%=mid%>'
          }

           function openWindow(open){
              if(open.length){
                window.open(listPrintAdress[open[0].value]);
                printModal.close();
              }
           }


           function printCallBack(){
            openWindow($('.print-modal').serializeArray());


           }

           function cancelCallBack(){
              printModal.close();
           }

            $('.vtk-js-modal_ok_action').on('click', printCallBack);
            $('.vtk-js-modal_cancel_action').on('click', cancelCallBack);
        })
    }







    var thisMeetingRefId;
    var thisMeetingImg="default";
    var thisMeetingDate="a";
    var isActivNew=0;
    var agendaSched= null;
    var scrollTarget = "";
    var thisMeetingUid;
    var mid= null;
    var helper=null;
    var nextMeetingDate=null;
    //var meetingLocation=null;
    var thisMeetingType=null;
    var sentEmails=0;
    var sentEmailsSubject=null;
    var locations =null;
    var thisMeetingNotes=null;
    var initialize = false;

    var MeetingList = React.createClass({displayName: "MeetingList",
      getInitialState: function() {
   	  
        return { show: false };
      },
      toggle: function() {
        this.setState({ show: !this.state.show });
      },
      render: function() {

       var scheduleDates =null;
       if( this.props.schedule!=null){
              scheduleDates= this.props.schedule.dates;
          }



       var that = this;
       var commentNodes = this.props.data.map(function (comment ,i ) {


        if( scheduleDates !=null ) {
          var scheduleDatesArray = scheduleDates.split(',');
          thisMeetingDate =  scheduleDatesArray[comment.id];
        } else {
          thisMeetingDate=new Date(helper.currentDate);
        }
          thisMeetingUid= comment.uid;
          thisMeetingRefId  = comment.refId;
          thisMeetingPath  = comment.path;

          thisMeetingImg   = "/content/dam/girlscouts-vtk/local/icon/meetings/"+ comment.meetingInfo.id +".png";
          mid=comment.uid;
          thisMeetingType= comment.type;
          sentEmails=comment.sentEmails;
          if( sentEmails!=null){
              sentEmailsSubject = sentEmails[0].subject;
              sentEmails= sentEmails.length;
          }


          thisMeetingDate = new Date( Number(thisMeetingDate) );

          if( isNaN(thisMeetingDate) ){
              thisMeetingDate = new Date(helper.currentDate);

          }

          thisMeetingNotes = comment.notes;
          console.log('set =>',thisMeetingNotes)


          if(!initialize){
            appVTK = initNotes;

            appVTK.getNotes('<%=meeting.getUid()%>','<%=user.getApiConfig().getUser().getSfUserId()%>').done(function(json){
                  appVTK.interateNotes(json);
            });
           
            initialize = true;
         }

     return (
            React.createElement(YearPlan, {item: comment, key: i},
                   React.createElement(MeetingPlan, {thisMeeting: comment, meetingModMONTH: moment.tz(thisMeetingDate,"America/New_York").format('MMMM'), meetingModDAY: moment.tz(thisMeetingDate,"America/New_York").format('DD'), meetingModHOUR: moment.tz(thisMeetingDate,"America/New_York").format('h:mm a'), uid: comment.uid, meetingTitle: comment.meetingInfo.name, meetingId: comment.id, meetingGlobalId: thisMeetingImg, location: comment.locationRef, cat: comment.meetingInfo.cat, blurb: comment.meetingInfo.meetingInfo["meeting short description"].str}),
                   React.createElement(MeetingAssets, {data: comment.assets}),
                   React.createElement(SortableList1, {data: comment.meetingInfo.activities, forceReload: that.props.forceReload})
            )
          );



      }); //end of loop

        return ( React.createElement("div", {className: "commentList"}, commentNodes)    );
      } //end of render
    });

    var ActivityNameAlex = React.createClass({displayName: "ActivityNameAlex",
      onClick: function() {
       loadModalPage('/content/girlscouts-vtk/controllers/vtk.meetingMisc.html?mid="+mid+"&isAgenda='+(this.props.item.activityNumber-1), true, 'Agenda')
        },
      render: function() {
        return (

            React.createElement("a", {href: "javascript:void(0)", onClick: this.onClick, className: this.props.selected ? "selected" : "", mid: mid, isAgenda: (this.props.item.activityNumber-1)},
               this.props.item.name
            )
        );
      }
    });


    var ActivityName = React.createClass({displayName: "ActivityName",
      render: function() {
        return (
            React.createElement("a", {"data-reveal-id": "modal_popup", "data-reveal-ajax": "true", href: "/content/girlscouts-vtk/controllers/vtk.include.modals.modal_agenda_edit.html?mid="+mid+"&isAgenda="+(this.props.item.activityNumber-1)}, (this.props.item.isOutdoor ? this.props.item.name_outdoor : this.props.item.name) )
        );
      }
    });



    var MeetingAssets = React.createClass({displayName: "MeetingAssets",
        getInitialState: function() {
           return { 
            data:[]
            };
         },
         componentWillMount:function(){
          
                  var _context = this;

                  var newData;

                  var Con = thisMeetingRefId.split('/').reverse()[0];

                  var url = location.origin+'/content/vtkcontent/en/resources/volunteer-aids/vtkvideos/_jcr_content/content/top/par.1.json'; 


                    function processData(json){


                     
                       newData = _context.props.data.slice(0);

                      function Add(e){
                        var newO ={
                          
                                  description:null,
                                  docType:'movie',
                                  isOutdoorRelated:null,
                                  path:null,
                                  refId:e.url,
                                  title:e.name
                       
                                  }
                        newData.push(newO)
                      } 


                      if(json){
                        for(var element in json ){
                        if(json[element].hasOwnProperty('meetingid')){
                            var idlist = json[element].meetingid.split(',')


                            for (var i = idlist.length - 1; i >= 0; i--) {
                              if(Con.indexOf(idlist[i]) > -1 ){
                              
                                Add(json[element]);

                                break;
                              }
                            };
                        }
                        }
                      }


                      return newData;
                    }

                  var call = $.ajax({
                    url:url
                  }).done(function(response){
                     _context.setState({'data':processData(response)});
                  }).error(function(err){
                    _context.setState({'data':processData()});
                    console.log(err);
                  })
                
      
         },

         render: function() {
              if(this.props.data==null){
                return React.createElement("section");
              }

              var commentNodes = this.state.data.map(function (comment ,i ) {
                var thisAssetExtension = "pdf";

                var urlPattern = /((([A-Za-z]{3,9}:(?:\/\/)?)(?:[-;:&=\+\$,\w]+@)?[A-Za-z0-9.-]+|(?:www.|[-;:&=\+\$,\w]+@)[A-Za-z0-9.-]+)((?:\/[\+~%\/.\w-_]*)?\??(?:[-\+=&;%@.\w_]*)#?(?:[\w]*))?)/;


                if(comment.docType=='movie' || urlPattern.test(comment.refId)){
                    thisAssetExtension = 'movie';
                }

                return (
                  React.createElement(
                      MeetingAsset,
                      {
                        item: comment,
                        key: i, 
                        refId: comment.refId,
                        title: comment.title,
                        description: comment.description,
                        extension: thisAssetExtension
                      }
                  )
                );

            });

           return (
             React.createElement("section", {className: "column large-20 medium-20 large-centered medium-centered"},
                 React.createElement("h6", null, "meeting aids"),
                   React.createElement("ul", {className: "large-block-grid-2 medium-block-grid-2 small-block-grid-1"},
                 commentNodes
                   ),
                   React.createElement(AddMeeting)
               )
           );
         }
       });


    var AddMeeting = React.createClass({displayName: "Add Meeting",
        render: function() {
        if( helper.permissions!=null && helper.permissions.indexOf('<%= Permission.PERMISSION_EDIT_MEETING_ID %>')!=-1 && thisMeetingType!='MEETINGCANCELED'){
          return (
             React.createElement("a", {className: "add-btn", "data-reveal-id": "modal_popup", "data-reveal-ajax": "true", href: "/content/girlscouts-vtk/controllers/vtk.include.modals.modal_meeting_aids.html?elem="+moment(thisMeetingDate).valueOf(), title: "Add meeting aids"}, React.createElement("i", {className: "icon-button-circle-plus"}), " Add Meeting Aids")
          );
        }else{ 
          return React.createElement("span");}
        }
      });


    var YearPlan = React.createClass({displayName: "YearPlan",
      render: function() {
        return (
          React.createElement("div", {className: "comment"},
            React.createElement("h2", {className: "commentAuthor"},
              this.props.author
            ),
            this.props.children
          )
        );
      }
    });

    var MeetingPlan = React.createClass({displayName: "MeetingPlan",

      render: function() {

        return (
                React.createElement("div",{className: "section-wrapper"},
                /*nav include*/
                React.createElement("div", {className: "column large-20 medium-20 large-centered medium-centered small-24"},

            React.createElement("div", {className: "meeting-navigation row collapse"},

            React.createElement("p", {className: "column"},

            React.createElement("span", null,

                    React.createElement(NavDirectionPrev)

      )

    ),

            React.createElement("div", {className: "column"},

            React.createElement(MeetingName,{meetingTitle: this.props.meetingTitle}),
            React.createElement(MeetingDate,{meetingModMONTH:this.props.meetingModMONTH,
                meetingModDAY:this.props.meetingModDAY,
                meetingModHOUR:this.props.meetingModHOUR})
    ),

          React.createElement("p", {className: "column"},

          React.createElement("span", null,



            React.createElement(NavDirectionNext)




      )

    )

  )

),
                /*end nav include*/


                /**main info */

        React.createElement("section", {className: "column large-20 medium-20 large-centered medium-centered", id: "main-info"},
            React.createElement("div", {className: "row"},
            React.createElement("div", {className: "column large-17 medium-17 small-17"},
            React.createElement("p", null, this.props.blurb),
            React.createElement("section", null,

            React.createElement(MeetingLocation, {location: this.props.location})
      ),
      React.createElement("section", null,
        // React.createElement("p", null, "Category:"),
        React.createElement("p", null, "Category: " + this.props.cat)
      )
    ),
    React.createElement("div", {className: "column large-7 medium-7 small-7 text-right"},
      React.createElement("img", {src: this.props.meetingGlobalId, alt: "badge"})
    )
  )
),





                /*end main info*/



                /*planning*/
                React.createElement("section", {className: "column large-20 medium-20 large-centered medium-centered"},
  React.createElement("h6", null, "planning materials"),
  React.createElement("ul", null,
   React.createElement("li", null,
    React.createElement("a", {"data-reveal-id": "modal_popup", "data-reveal-ajax": "true", href: "/content/girlscouts-vtk/controllers/vtk.include.modals.modal_agenda_edit.html?mid="+thisMeetingUid+"&isOverview=true"}, "Meeting Overview")
  ),
React.createElement(ActivityPlan),
   React.createElement("li", null,
    React.createElement("a", {"data-reveal-id": "modal_popup", "data-reveal-ajax": "true", href: "/content/girlscouts-vtk/controllers/vtk.include.modals.modal_agenda_edit.html?mid="+thisMeetingUid+"&isMaterials=true"}, "Materials List")
  )
  )
)
                /* end planning*/



                /*communication*/
         <% if( !user.getCurrentYear().equals( VtkUtil.getCurrentGSYear()+"") || VtkUtil.hasPermission(troop, Permission.PERMISSION_SEND_EMAIL_MT_ID) ){ %>

,React.createElement("section", {className: "column large-20 medium-20 large-centered medium-centered"},

  React.createElement("h6", null, "manage communications"),

  React.createElement("ul", {className: "large-block-grid-2 medium-block-grid-2 small-block-grid-2"},

        React.createElement(EmailMeetingReminder)
        <%if( !user.getApiConfig().isDemoUser()  ){%>
	        ,
	        React.createElement(AttendanceAchievement,{data:this.props.thisMeeting})
	    <%}%>




    )

)
<%} %>
/*end communication*/
      )
         )
      }
    });





    var EmailMeetingReminder = React.createClass({displayName: "Meeting Reminder Email",
        render: function() {
                    if( <%= user.getCurrentYear().equals( VtkUtil.getCurrentGSYear()+"")%> && helper.permissions.indexOf('<%= Permission.PERMISSION_SEND_EMAIL_MT_ID %>')!=-1 ) {
                        return (React.createElement(EmailMeetingReminderWithSched))
                    } else{
                       return (React.createElement(EmailMeetingReminderWithOutSched))
                    }
        }
    });


    var EmailMeetingReminderWithSched = React.createClass({displayName: "Meeting Reminder Email with sched",
        render: function() {

                        if( helper.currentDate!=null && (helper.currentDate> new Date("1/1/1977").getTime()) && thisMeetingType!='MEETINGCANCELED' ) {

                             return(
                                     React.createElement("li", null,
                                               React.createElement("a", {href: "#", "data-reveal-id": "modal-meeting-reminder", title: "Meeting Reminder Email"}, "Edit/Sent Meeting Reminder Email")
                                    ,  React.createElement(PrintSentEmails) )

                               )
                        } else if( thisMeetingType!='MEETINGCANCELED'){

                              return (React.createElement("li", null,
                                React.createElement("a", {href: "javascript:alert('You have not yet scheduled your meeting calendar.\\nPlease select a year plan and schedule your meetings by clicking on the MEETING DATES AND LOCATION link.')", title: "Meeting Reminder Email"}, "Edit/Sent Meeting Reminder Email")
                                 ,React.createElement(PrintSentEmails))
                                 );
                        } else {
                            return (React.createElement("li", null));
                        }



        }
    });

    var PrintSentEmails = React.createClass({displayName: "printEmailSent",
        render: function() {
              if (sentEmails>0) {
                return (
                        React.createElement("li", null,
                         React.createElement("span",null, " (", sentEmails , " sent -",
                          React.createElement("a", {href: "#", title: "view sent emails", className: "view", "data-reveal-id": "modal_view_sent_emails"}, " view"),
                          ")"
                          )
                        )
                 )
                }else{
                    return (React.createElement("li"))
                }
        }
    });

    var EmailMeetingReminderWithOutSched = React.createClass({displayName: "Meeting Reminder Email no shched",
        render: function() {

            if (sentEmails > 0) {

               return(
                       React.createElement("li", null,
                         React.createElement("a", {href: "#",className: "<%=( user.getCurrentYear().equals( VtkUtil.getCurrentGSYear()+"")) ? "" : "vtkDisableA"%>", title: "view sent emails","data-reveal-id": "modal_view_sent_emails"}, "Meeting Reminder email"),
                          React.createElement("span",null, " (", sentEmails, " sent -",
                            React.createElement("a", {href: "#", title: "view sent emails", className: "<%= (user.getCurrentYear().equals( VtkUtil.getCurrentGSYear()+"")) ? "view" : "vtkDisableA"%>", "data-reveal-id": "modal_view_sent_emails"}, " view"), ")"
                          )
                       )
                )
            } else if( thisMeetingType!='MEETINGCANCELED'){

                return (
                   React.createElement("li", null,
                         React.createElement("a", {href: "#",title: "view sent emails", className: "<%= (user.getCurrentYear().equals( VtkUtil.getCurrentGSYear()+"")) ? "" : "vtkDisableA"%>", "data-reveal-id": "modal_view_sent_emails"}, "Meeting Reminder email"),
                            React.createElement("span",null, "")
                   )
               )
            }else{

                return React.createElement("li", null);
            }
        }
    });


    var MeetingName = React.createClass({displayName: "Meeting Name h3",
        render: function() {
            if( thisMeetingType=='<%=YearPlanComponentType.MEETINGCANCELED%>'){
                 return  React.createElement("h3", null,  "MEETING (Canceled) : " ,this.props.id, this.props.meetingTitle)
            }else if(thisMeetingType=='<%=YearPlanComponentType.MEETING%>'){
                 return  React.createElement("h3", null,  "MEETING : " ,this.props.id, this.props.meetingTitle)

            }else{return React.createElement("h3")}
        }
    });

    var MeetingLocation = React.createClass({displayName: "Location",
        render: function() {

            var meetingLocation= null;
            var loc= this.props.location;

            if( locations!=null &&  loc!=null &&  loc!='' ){
                for(var i=0;i<locations.length;i++){
                    if( locations!=null && locations[i].path== loc)
                     meetingLocation = locations[i];
                }
            }

            if( meetingLocation!=null){
               return React.createElement("p", null,

                            React.createElement("span", null, "Location: "+meetingLocation.name+"  - "),
                            React.createElement("a", {href: "/content/girlscouts-vtk/controllers/vtk.map.html?address="+meetingLocation.address, target: "_blank"}, meetingLocation.address+" ")

                    )
            }else{return React.createElement("p")}
        }
    });

    var AttendanceAchievement = React.createClass({displayName: "Attendance and Achievement",
        render: function() {

            if( <%=!user.getCurrentYear().equals( VtkUtil.getCurrentGSYear()+"") %> ||
                this.props.data.type != '<%=YearPlanComponentType.MEETINGCANCELED%>'
                        && helper.permissions!=null && helper.permissions.indexOf('<%= Permission.PERMISSION_EDIT_ATTENDANCE_ID%>')!=-1){

                var isArch = (this.props.data.type == '<%=YearPlanComponentType.MEETING%>') ? this.props.data.meetingInfo.isAchievement : "false" ;
                var mName=this.props.data.meetingInfo.name;

                var txt="";

                if( helper.attendanceTotal ==null || helper.attendanceTotal==''){
                       txt+= "0 present, 0 achievements" ;
                }else{

                        if(helper.attendanceCurrent ==null || helper.attendanceCurrent ==0 ){
                          txt+="0 present, "  ;
                        }else{
                           txt+=helper.attendanceCurrent +" of "+ helper.attendanceTotal +" present, ";
                        }

                        if(helper.achievementCurrent ==null || helper.achievementCurrent==0){
                          txt+="0 achievements" ;
                        }else{
                          txt+= helper.achievementCurrent +" of " + helper.attendanceTotal + " achievement(s)";
                        }
                }
             return (
                   React.createElement("li", null,
                    React.createElement("a", {"data-reveal-id": "modal_popup", "data-reveal-ajax": "true", className: "<%=( user.getCurrentYear().equals( VtkUtil.getCurrentGSYear()+"")) ? "" : "vtkDisableA"%>",  href: "/content/girlscouts-vtk/controllers/vtk.include.modals.modal_attendance.html?mid="+this.props.data.uid+"&isAch="+isArch+"&mName="+mName}, "Record Attendance & Achievements"),
                      React.createElement("li", null, "(",txt,")")
                  )
                );
          } else{
                return  React.createElement("span") ;
          }
        }
    });

    var ActivityPlan = React.createClass({displayName: "Activity Plan",
         render: function() {

                if( helper.permissions!=null && helper.permissions.indexOf('<%= Permission.PERMISSION_VIEW_ACTIVITY_PLAN_ID%>')!=-1){
                  return (  React.createElement("li", null,
                     React.createElement("a", {"data-reveal-id": "modal_popup", "data-reveal-ajax": "true", href: "/content/girlscouts-vtk/controllers/vtk.include.modals.modal_agenda_edit.html?mid="+thisMeetingUid+"&isActivity=true"}, "Activity Plan")
                    )
                );

         }else{
             return React.createElement("li")
         }
         }
    });

    var MeetingDate = React.createClass({displayName: "Meeting Date",
        render: function() {
            if(thisMeetingDate!=null && thisMeetingDate.getTime() > new Date("1/1/1977").getTime() ){
                return (
                         React.createElement("p", {className: "date"},

                              React.createElement("span", {className: "month"}, this.props.meetingModMONTH),
                              React.createElement("span", {className: "day"}, this.props.meetingModDAY),
                              React.createElement("span", {className: "hour"}, this.props.meetingModHOUR)

                      )

                );
        }else{
            return React.createElement("p" )
        }
        }
      });



    var MeetingAsset = React.createClass({displayName: "MeetingAsset",
      render: function() {

           var _context = this;

          return (function(){

            if(_context.props.extension === "movie"){
              debugger;
              return React.createElement(
                "li",
                null,
                  React.createElement(
                    "a",
                    {
                      // href: _context.props.refId,
                      // href: "/content/girlscouts-vtk/controllers/vtk.include.modals.modal_youtube.html?resource="+_context.props.refId,
                      target: "_blank",
                      title: "Meeting Asset",
                      "data-reveal-id": "modal_popup_video",
                      "data-reveal-ajax": "/content/girlscouts-vtk/controllers/vtk.include.modals.modal_youtube.html?resource='"+_context.props.refId+"'",
                      className: "<%=( user.getCurrentYear().equals( VtkUtil.getCurrentGSYear()+"")) ? "" : "vtkDisableA"%> icon "+ _context.props.extension
                    },
                    _context.props.title,
                    (_context.props.item.isOutdoorRelated)? React.createElement(
                         "img",
                         {
                           src:'/etc/designs/girlscouts-vtk/clientlibs/css/images/outdoor.png',
                           style:{
                             width:'9%',
                             "marginLeft":"15px"
                           }
                         }
                       ): React.createElement(
                         "span",
                         null
                       )
                  ),
                  React.createElement(
                    "p",
                     {className: "info"},
                     _context.props.description
                  )
              )
            }else{
              return React.createElement(
                "li",
                null,
                  React.createElement(
                    "a",
                    {
                      href: _context.props.refId,
                      target: "_blank",
                      title: "View Meeting Aids",
                      className: "<%=( user.getCurrentYear().equals( VtkUtil.getCurrentGSYear()+"")) ? "" : "vtkDisableA"%> icon "+ _context.props.extension
                    },
                    _context.props.title,
                    (_context.props.item.isOutdoorRelated)? React.createElement(
                         "img",
                         {
                           src:'/etc/designs/girlscouts-vtk/clientlibs/css/images/outdoor.png',
                           style:{
                             width:'9%',
                             "marginLeft":"15px"
                           }
                         }
                       ): React.createElement(
                         "span",
                         null
                       )
                  ),
                  React.createElement(
                    "p",
                     {className: "info"},
                     _context.props.description
                  )
              )
            }
          }())



        // return (
        //         React.createElement(
        //           "li",
        //           null,
        //           React.createElement(
        //             "a",
        //               {
        //                 href: this.props.refId,
        //                 target: "_blank",
        //                 title: "View Meeting Aids",
        //                 className: "<%=( user.getCurrentYear().equals( VtkUtil.getCurrentGSYear()+"")) ? "" : "vtkDisableA"%> icon "+ this.props.extension
        //               }, 
        //               this.props.title,
        //               (this.props.item.isOutdoorRelated)? React.createElement(
        //                 "img",
        //                 {
        //                   src:'/etc/designs/girlscouts-vtk/clientlibs/css/images/outdoor.png',
        //                   style:{
        //                     width:'9%',
        //                     "margin-left":"15px"
        //                   }
        //                 }
        //               ): React.createElement(
        //                 "span",
        //                 null
        //               )
        //             ),
        //               React.createElement(
        //                 "p",
        //                 {
        //                   className: "info"
        //                 },
        //                 this.props.description
        //               )
        //         )
        // );
      }
    });

    var NavDirectionPrev = React.createClass({displayName: "Prev Date",
        render: function() {
            if(helper.prevDate!=0 && helper.prevDate!=null ){
            return (


                  React.createElement("a", {className: "direction prev", href: "/content/girlscouts-vtk/en/vtk.details.html?elem="+helper.prevDate})

             )
            }else{
                return React.createElement("span");
            }

        }
      });


    var NavDirectionNext = React.createClass({displayName: "NavDirectionNext",
        render: function() {
            if(nextMeetingDate!=0 && nextMeetingDate!=null ){
                return (
                       React.createElement("a", { className: "direction next", href: "/content/girlscouts-vtk/en/vtk.details.html?elem="+nextMeetingDate})

                      );
              }else{
                  return React.createElement("span", null, "");
              }
        }
      });



    var CommentBox = React.createClass({displayName: "CommentBox",
      loadCommentsFromServer: function( isFirst ) {


        this.dataWorker.getData();

      },
      forceReload: function() {

          this.dataWorker.getData(true);
      },
      getInitialState: function() {
        return {data: []};
      },
      componentDidMount: function() {
        this.dataWorker = new VTKDataWorker('<%= meetingDataUrl %>', this, function(data) {


            this.setState({
                data: data.yearPlan
            });
            // console.log(data.yearPlan.meetingEvents[0].notes);

            // thisMeetingNotes = data.yearPlan.meetingEvents[0].notes;

           // appVTK.data.set(data.yearPlan.meetingEvents[0].notes);

        }, 10000);
        this.dataWorker.start();
      },
      checkLocalUpdate: function(){
          if( (isActivNew == 1) || (isActivNew == 2) )
              { this.loadCommentsFromServer() ; }
      },
      render: function() {
          var x;
          var sched;


          if( <%=planView.getYearPlanComponent().getType()== YearPlanComponentType.MEETING%> && this.state.data.meetingEvents!=null){


              helper= this.state.data.helper;

              thisMeetingDate= helper.currentDate;
              nextMeetingDate= helper.nextDate;


              x =  this.state.data.meetingEvents;
              sched = this.state.data.schedule;

              locations= this.state.data.locations;

              return (
                   React.createElement(MeetingList, {data: x, schedule: sched, forceReload: this.forceReload})
              );
          }else if( <%=planView.getYearPlanComponent().getType()== YearPlanComponentType.MEETINGCANCELED%> &&  this.state.data.meetingCanceled!=null){

        	  helper= this.state.data.helper;

              thisMeetingDate= helper.currentDate;
              nextMeetingDate= helper.nextDate;

              x =  this.state.data.meetingEvents;
                  sched = this.state.data.schedule;
                  locations= this.state.data.locations;

                  return (
                       React.createElement(MeetingList, {data: x, schedule: sched, forceReload: this.forceReload})
                  );
          }else{

              return React.createElement("div", null, "loading...");
          }
      }
    });


     var SortableList1 = React.createClass({displayName: "SortableList1",
      getInitialState: function() {
          return {data: this.props.data};
        },

      componentWillReceiveProps: function(newProps) {
         this.setState({data: newProps.data});
      },
      

      componentDidMount: function(){
        //Make sure fundation wrap the dom
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
      },

      onReorder: function (order) {
          this.setState({data: null});
      },
        render: function () {
    
          return React.createElement("section", {className: "column large-20 medium-20 large-centered medium-centered"},
        React.createElement("h6", null, "meeting agenda"),
                React.createElement("p", {className: "vtkDisableP"}, "Select an agenda item to view details, edit duration or delete. Drag and drop to reorder."),
        React.createElement(SortableListItems1, {key: "{this.state.data}", data: this.state.data, onClick: this.alex, onReorder: this.onReorder, forceReload: this.props.forceReload}),
                React.createElement(AgendaTotal, {data: this.props.data}),
                React.createElement(AgendaItemAdd),
                React.createElement(Survey)
          );
        }
    });

    var SortableListItems1 = React.createClass({displayName: "SortableListItems1",
      render: function() {
        if( this.props.data!=null ){
          agendaSched=null;


            var _that = this;
          return (


               React.createElement("ul", null,

                  this.props.data.map((function(item, i) {

                  return React.createElement("li", {className: ( helper.permissions!=null && helper.permissions.indexOf('<%= Permission.PERMISSION_EDIT_MEETING_ID %>')!=-1 && thisMeetingType!='MEETINGCANCELED') ? "row ui-state-default" :"ui-state-disabled" , key: item.activityNumber, id: item.activityNumber},
                    React.createElement("div", {className: "wrapper clearfix"},



                React.createElement("img", {className: (moment(thisMeetingDate) < moment( new Date()) && (moment(thisMeetingDate).get('year') >2000)) ? "touchscroll hide" : "touchscroll <%=VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_YEARPLAN_ID) ? "" : " hide" %>", src: "/etc/designs/girlscouts-vtk/clientlibs/css/images/throbber.png"}),


                      React.createElement("div", {className: "large-3 medium-3 small-3 columns small-push-1 large-push-2"},
                    		  React.createElement(Outdoor,{item:item}),
                    		  
                    		  
                        React.createElement("span", null,   moment(thisMeetingDate).format('YYYY') <1978 ? item.activityNumber : moment.tz(getAgendaTime( item.duration ), "America/New_York").format("h:mm"), " ")


                      ),
                        React.createElement("div", {className: "large-17 columns medium-17 small-17 small-push-1 large-push-1"},
                        React.createElement(ActivityName, {item: item, key: item.uid, selected: item.uid, itemSelected: this.setSelectedItem, activityNumber: item.activityNumber - 1})
                        ),
                        React.createElement("div", {className: "large-3 medium-3 small-3 columns"},
                          React.createElement("span", null, ":", item.duration<10 ? ("0"+item.duration) : item.duration)
                        )
                      )
                    );
                            }))


              )


      );
        }else{
          return React.createElement("div", null, React.createElement("img", {src: "/etc/designs/girlscouts-vtk/images/loading.gif"}))
        }
      },
      componentDidMount: function() {     
    	  try{
           if( helper.permissions!=null && helper.permissions.indexOf('<%= Permission.PERMISSION_EDIT_MEETING_ID %>')!=-1){
                replaceMeetingHref(thisMeetingPath, moment(thisMeetingDate).valueOf());
           }
           resizeWindow();
       }catch(err){}

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
            repositionActivity1(thisMeetingRefId , yy, this.props.forceReload);
            onReorder(order);
          }.bind(this)
        });
      },
      componentWillUpdate: function() {
        var dom = $(ReactDOM.findDOMNode(this));
        if (Modernizr.touch) {
            scrollTarget = ".touchscroll";
          }

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
              repositionActivity1(thisMeetingRefId , yy, this.props.forceReload);
              onReorder(order);
            }.bind(this)
        });
      }
    });



      function repositionActivity1(meetingPath,newVals, callback){
    var x =$.ajax({
    url: '/content/girlscouts-vtk/controllers/vtk.controller.html?act=RearrangeActivity&mid='+meetingPath+'&isActivityCngAjax='+ newVals, // JQuery loads serverside.php
    data: '',
    dataType: 'html',
    success: function (data) {
        //location.reload();
        vtkTrackerPushAction('MoveAgendas');
        callback();
    },
    error: function (data) {
    }
    });
 }

    ReactDOM.render(
             <%
                String elem = request.getParameter("elem");
                if (elem != null) {
                elem = "&elem=" + elem;
                } else {
                elem = "";
                }
                %>
    React.createElement(CommentBox, {url: "/content/girlscouts-vtk/controllers/vtk.controller.html?reactjs=asdf"+ getElem(), pollInterval: 10000}),
      document.getElementById('theMeeting')
    );


   var AgendaTotal = React.createClass({displayName: "AgendaTotal",

          render: function() {
            return (
                React.createElement("p", {className: "row"},
                  React.createElement("strong", {className: "column small-2 small-push-21"}, getAgendaTotalTime(this.props.data))
                )

            );
          }
        });

   var AgendaItemAdd = React.createClass({displayName: "AgendaItemAdd",

       render: function() {

           if( helper.permissions!=null && helper.permissions.indexOf('<%= Permission.PERMISSION_EDIT_MEETING_ID %>')!=-1 && thisMeetingType!='MEETINGCANCELED'){
             return (

                     React.createElement("strong", null,
                             React.createElement("a", {"data-reveal-id": "modal_agenda", className: "add-btn"},
                               React.createElement("i", {className: "icon-button-circle-plus"}), " Add Agenda Item"))

             );
           }else{
                    return React.createElement("strong", null);
           }
       }
     });

   var Survey = React.createClass({
    displayName:"survey",

    click: function(){

      function addhttp(url) {
         if (!/^(f|ht)tps?:\/\//i.test(url)) {
            url = "http://" + url;
         }
         return url;
      }
            
       window.open(addhttp(this.state.url), '_blank');
    },

    getInitialState: function(){
      return {
         button:'',
          img:'/etc/designs/girlscouts-vtk/clientlibs/css/images/survey_icon.png',
          text:'',
          url:'',
          text_bold:'',
          show:false
      }
    },
    componentWillMount: function(){
        var Con = thisMeetingRefId.split('/').reverse()[0];

        var url = window.location.origin +'/content/vtkcontent/en/vtk-survey-links/_jcr_content/content/middle/par.1.json';

        var _context = this;
       

        function processData(data){
         for (var key in data) {
            if(data[key].hasOwnProperty('meetingid')){
                  var idlist = data[key].meetingid.split(',')

                  for (var i = idlist.length - 1; i >= 0; i--) {
                    if(Con.indexOf(idlist[i]) > -1){
                      setNewState(data[key]);
                      return false;
                    }
                  };
          
            }
          }


        }


          function setNewState(data){
 

            _context.setState({
              button:data.buttonCopy,
              img:'/etc/designs/girlscouts-vtk/clientlibs/css/images/survey_icon.png',
              text:data.bannerCopy,
              url:data.surveyLink,
              text_bold:data.bannerCopyBold,
              show:true
            })
           }



    
        $.ajax({
           url:url
        }).done(function(data){
          processData(data);
        }).error(function(err){
          console.log(err);
        });
           
    },
    render:function(){
      var _context = this;
      var className = "vtk-survey columns small-24" + (function(){ return (!_context.state.show) ? ' hide':''}());
      return (
        React.createElement(
          'div',
          {
            className:className,
            style:{
              cursor:'pointer',
            },
             onClick: this.click
          },
          React.createElement(
            "div",
            {
              'className':'text-center columns small-24 medium-push-1 medium-3 small-text-center medium-text-left',

            },
              React.createElement(
                "img",
                {

                  src:this.state.img,
                  alt:this.state.button,
                  title:this.state.text,
                  style:{
                    height:'34px',
                    padding:'0 10px'
                  }
                }
              )
            ),
          React.createElement(
            "div",
            {
              'className':'columns small-24 medium-push-1 medium-13 small-text-center medium-text-left',
              'style':{
                'padding':"5px 0px",
                'marginLeft':"-5px"
              }
            },
            React.createElement('b',null,this.state.text_bold+" "),
            this.state.text
            ),
          React.createElement(
            "div",
            {
              'className':'columns small-24 medium-4 medium-pull-1 small-text-center medium-text-left',
            },
            React.createElement(
              'div',
              null,
              React.createElement(
                "button",
                {
                  "className": "tiny",
                 style:{

                  },
                  'href':'http://google.com'
                 
                },
                this.state.button
                )
              )
            )
        )
      );
    }

   })

   var Outdoor = React.createClass({
                  displayName: "Outdoor",
                  _click:function(r){
                      _this = this;
                      $.ajax({
                        url:'/content/girlscouts-vtk/controllers/vtk.controller.html?cngOutdoor=true&mid='+mid+'&aid='+this.props.item.path+'&isOutdoor=true'
                      }).done(
                        function(e){
                          _this.setState({isOutdoor:true});
                        }
                      );

                  },
                  _clickfalse:function(r){
                      _this = this;
                      $.ajax({
                        url:'/content/girlscouts-vtk/controllers/vtk.controller.html?cngOutdoor=true&mid='+mid+'&aid='+this.props.item.path+'&isOutdoor=false'
                      }).done(
                        function(e){
                          _this.setState({isOutdoor:false});
                        }
                      );

                  },
                  getInitialState: function(){

                    return {
                              isOutdoor: this.props.item.isOutdoor,
                              isOutdoorAvailable: this.props.item.isOutdoorAvailable
                            };
                    },
                  render: function() {

                    outDoorIconList.add(this.props.item.path,this.props.item,this);

                    var _style = {

                        width: "30px",

                        cursor: 'pointer'
                    }

                   if( this.state.isOutdoor ){

                        return (

                          React.createElement(
                              'span',
                              { style: {
                                  "position": "absolute",
                                  "top": "-15px",
                                  "left": "-38px",
                                  "cursor": "pointer",
                                  "border": "none",
                                  "paddingTop": "8px"
                                },
                                  onClick: this._click,
                                  "data-tooltip":true,
                                  "aria-haspopup":true,
                                  className:"has-tip tip-top radius",
                                  title:"<b>Get Girls Outside!</b>"
                            }, React.createElement(
                            'img',{
                              src:'/etc/designs/girlscouts-vtk/clientlibs/css/images/outdoor.png',
                              style:_style,
                              onClick: this._clickfalse,
                              alt:'',
                              title:'',         
                           
                            }))


// API for toggle :'/content/girlscouts-vtk/controllers/vtk.controller.html?cngOutdoor=true&mid='+mid+'&aid='+this.props.item.uid+'&isOutdoor=false'
//                 '/content/girlscouts-vtk/controllers/vtk.controller.html?cngOutdoor=true&mid='+mid+'&aid='+this.props.item.uid+'&isOutdoor=true'
                        );
                    }else if(this.state.isOutdoorAvailable){

                        return (
                            React.createElement(
                              'span',
                              { style: {
                                  "position": "absolute",
                                  "top": "-15px",
                                  "left": "-38px",
                                  "cursor": "pointer",
                                  "border": "none",
                                  "paddingTop": "8px"
                                },
                                  onClick: this._click,
                                  "data-tooltip":true,
                                  "aria-haspopup":true,
                                  className:"has-tip tip-top radius",
                                  title:"<b>Get Girls Outside!</b>"
                            },
                              React.createElement('img',
                                {
                                  src:'/etc/designs/girlscouts-vtk/clientlibs/css/images/indoor.png',
                                  style:{
                                    width: "30px",
                                     },
                                     alt:'',
                                     title:'',                                 

                                }
                              ),
                              React.createElement(
                                  "p",
                                  {
                                    style:{
                                      'marginBottom':'0px',
                                      color:"green",
                                      "textAlign":"center",
                                      "fontSize":'11px',
                                    },
                                    onClick: this._click
                                  },
                                  "Select"
                                )
                              )
                            );
                    }else{
                        return (
                                React.createElement("span", "")
                            );
                    }
                  }
                });


  function getAgendaTotalTime(x){

  if( x==null ) return "";
    var total =0;
    x.map((function(item, i) {
          total += item.duration;
    }))


      var hours = Math.floor( total / 60);
      var minutes = total % 60;

      if( hours<=0 )
        return ":"+(minutes < 10 ? "0"+ minutes : minutes);
     else
      return hours+":"+ (minutes < 10 ? "0"+ minutes : minutes);

  }



  function getAgendaTime( duration ){
   if( agendaSched==null ){
      agendaSched= thisMeetingDate.getTime();
   }

  var curr= agendaSched;
    agendaSched  = addMinutes( agendaSched, duration);

    return curr;
  }

  function addMinutes(date, minutes) {
      return new Date(date + minutes*60000).getTime();
  }

  function getParameterByName(name) {
        name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
        var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
            results = regex.exec(location.search);
        return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
    }

  function getElem(){
      var elem = getParameterByName('elem');
      if( elem!=null && elem!='')
          return "&elem="+elem;

      return "";
  }

  loadNav('planView');
  vtkTrackerPushAction('ViewMeetingDetail');

      </script>
  </div>

  <% if( VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_MEETING_ID) ){ %>
        <%@include file="include/notes.jsp"%>
  <% } %>

<%@include file="include/bodyBottom.jsp" %>

<div id="modal_popup" class="reveal-modal" data-reveal></div>
<div id="modal_popup_video" class="reveal-modal" data-reveal></div>

<!-- Hack for make the tooltip nob -->
<style>
.tooltip .nub{
  left:10px;
}
</style>