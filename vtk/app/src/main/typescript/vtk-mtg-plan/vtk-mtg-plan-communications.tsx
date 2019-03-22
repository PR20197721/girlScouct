import * as React from 'react';
import VtkContent from './common/content';
import VtkLinkPopUp from './common/linkpopup';
import {connect} from 'react-redux';
import { PERMISSION_CHECK, IS_MEETING_CANCEL, DATE_MORE_THAN_1977, IS_MEETING, HAS_PERMISSION_FOR} from './permission';

export interface VtkMtgPlanCommunicationsProps {
  state:Object,
  meetingInfo:Object,
  user_variable:any,
  meetingEvents:Object,
  helper:Object,
}

declare var printModal;

let Link = (props) => {
	let {modal,title, className} = props;
	return <a href="#" style={{fontWeight:'bold'}} className={className} data-reveal-id={modal} title={title}>{title}</a>
}


let EmailMeetingReminderWithSched = (props) => {
  let {helper,meetingEvents} = props;

  const alert = () => {
    const text = `<p>You have not yet scheduled your meeting calendar.
    Please select a year plan and schedule your meetings by clicking on the MEETING DATES AND LOCATION link.</p>`;
		printModal.alert("Note", text);
	};
  
  if (helper.currentDate && DATE_MORE_THAN_1977(helper.currentDate) && !IS_MEETING_CANCEL()) {
    return <p>
      <Link modal={'modal-meeting-reminder'} title={"Edit/Sent Meeting Reminder Email"} />
      {(meetingEvents.sentEmails && meetingEvents.sentEmails.length>0)
        ?<span className="a-tag-modal"><br />{`( ${meetingEvents.sentEmails.length} sent - `}<Link modal={'modal_view_sent_emails'} title={`view`} />{` )`}</span>
        :null
      }
    </p>
	} else if (!IS_MEETING_CANCEL()) {
		return <a href="#" style={{fontWeight:'bold'}} onClick={()=>alert()}>Edit/Sent Meeting Reminder Email</a>
	} else {
		return null
	}

  
}

let EmailMeetingReminderWithOutSched = (props) => {
  let { meetingEvents, user_variable } = props;

  if(meetingEvents.sentEmails && meetingEvents.sentEmails.length>0){
    return <p>
      <Link 
      modal={'modal-meeting-reminder'} 
      className={(user_variable.user_current_year !== user_variable.vtk_current_year?'disabled':'')} 
      title={"Edit/Sent Meeting Reminder Email"} />
      <span><br />{`( ${meetingEvents.sentEmails.length} sent - `} <Link 
      className={(user_variable.user_current_year !== user_variable.vtk_current_year?'disabled':'')} 
      modal={'modal_view_sent_emails'} 
      title={` views )`} />
      </span>
    </p>

  }else if(!IS_MEETING_CANCEL()){
    return <p>
        <Link 
        modal={'modal-meeting-reminder'} 
        className={(user_variable.user_current_year !== user_variable.vtk_current_year?'disabled':'')} 
        title={"Edit/Sent Meeting Reminder Email"} />
      </p>
  }else{
    return null
  }
}

let Editsendemail = (props) => {
  return (props.user_variable.user_current_year == props.user_variable.vtk_current_year && PERMISSION_CHECK()('permission_send_email_mt_id'))?<EmailMeetingReminderWithSched  {...props}/>:<EmailMeetingReminderWithOutSched {...props}/>
}

let RecordAchivements = (props) => {
  let {user_variable, helper, meetingEvents} = props;
  
    let isArch = (IS_MEETING())? meetingEvents.meetingInfo.isAchievement:false;
    var mName = meetingEvents.meetingInfo.name;

    let txt="";

    if( !helper.attendanceTotal ){
      txt+= `0 present, 0 achievements`;
    }else{

      if(!helper.attendanceCurrent){
        txt+=`0 present, `;
      }else{
          txt+=`${helper.attendanceCurrent} of ${helper.attendanceTotal} present, `;
      }

      if(!helper.achievementCurrent){
        txt+=`0 achievements` ;
      }else{
        txt+= `${helper.achievementCurrent} of ${helper.attendanceTotal} achievement(s)`;
      }
    }

  
    return <p>
          {(user_variable.vtk_current_year===user_variable.user_current_year)
          ?<a data-reveal-id="modal_popup" style={{fontWeight:'bold'}} 
          href={`/content/girlscouts-vtk/controllers/vtk.include.modals.modal_attendance.html?eType=${meetingEvents.type}&mid=${meetingEvents.uid}&isAch=${isArch}&mName=${meetingEvents.meetingInfo.name}`} 
          data-reveal-ajax="true">Record Attendance & Achievements</a>
          :<a className="disabled">Record Attendance & Achievements</a>}<br />{`(${txt})`}
          
    </p>

 
}

let Selector = (props) => {
  return (props.links.id == "editsendemail" )
  ? <Editsendemail {...props} /> 
  : <RecordAchivements {...props} />;
}


function VtkMtgPlanCommunications (props:VtkMtgPlanCommunicationsProps){
    let {user_variable, meetingEvents, helper}:any = props;

    let placeholder = [ 
      {title:'Edit/Sent Meeting Reminder Email',url:'url',show:true, id:'editsendemail'},
      {title:'Record Attendance & Achievements',url:'url',show:true, id:'recordAchivements'},
    ];

    let permission = HAS_PERMISSION_FOR()(
			"vtk_troop_haspermision_edit_yearplan_id"
		);

    return (
      <div>
        <VtkContent idName="comunications">
        {(permission || user_variable.user_current_year !== user_variable.vtk_current_year)?<div><h6>Manage Communications</h6>
          <ul className="large-block-grid-2 medium-block-grid-2 small-block-grid-1">
            {placeholder.map((links)=>
            <li key={links.title}>
              <Selector links={links} user_variable={user_variable} helper={helper} meetingEvents={meetingEvents}/>
            </li>)
            }
          </ul></div>:null}
        </VtkContent>
      </div>
    );
  }



  let mapStatetoProps = (state) => {
    return {
      state:state,
      meetingInfo: state.meetingEvents.meetingInfo,
      user_variable:state.user_variable,
      meetingEvents:state.meetingEvents,
      helper:state.helper
    };
  }


export default connect(mapStatetoProps)(VtkMtgPlanCommunications);

