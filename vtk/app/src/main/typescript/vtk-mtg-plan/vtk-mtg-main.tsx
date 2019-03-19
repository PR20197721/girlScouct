import * as React from 'react';
import VtkMtgPlanMainBar from './vtk-mtg-plan-main-bar';
import VtkMtgPlanMaterials from './vtk-mtg-plan-materials';
import VtkMtgPlanComunications from './vtk-mtg-plan-communications';
import VtkMtgPlanAids from './vtk-mtg-plan-aids';
import VtkMtgPlanAgenda from './vtk-mtg-plan-agenda';
import VtkMtgPlanMsg  from './vtk-mtg-messaging';
import VtkMtgPlanLoading from './vtk-mtg-plan-loading';
import VtkModal from './common/modal';
import VtkSurvey from './common/survey';
import * as moment from 'moment';


import './../../scss/vtk-mtg-plan/vtk-mtg-main.scss';
import { connect } from 'react-redux';
import { PERMISSION_CHECK } from './permission';

export interface VtkMtgPlanMainProps {
  meetingEvents:any;
  meeting: any;
  helper: any;
}

declare var thisMeetingDate, thisMeetingPath, thisMeetingNotes, replaceMeetingHref;

function VtkMtgPlanMain (props: VtkMtgPlanMainProps) {
    if(props.meetingEvents){ 
        // Set Globals hack use in other parts of the no react app in the MeetingReact2 file pending to take out when the app move complete // to react.
        if(props.meeting.schedule) {
          var scheduleDatesArray = props.meeting.schedule.dates.split(',');
          thisMeetingDate = scheduleDatesArray[props.meetingEvents.id];
        } else {
          thisMeetingDate = new Date(props.helper.currentDate);
        }

        thisMeetingPath = props.meetingEvents.path;
        thisMeetingNotes =props.meetingEvents.notes;

        //Replace Meeting need to take out
        if(PERMISSION_CHECK()('permission_edit_meeting_id')){
            replaceMeetingHref(thisMeetingPath, moment(thisMeetingDate).valueOf());
        }    
    }

   

    return ((props.meetingEvents)?
        <div>
            <div className="column large-20 medium-20 large-centered medium-centered">
                <VtkMtgPlanLoading />
                <VtkMtgPlanMsg  />
                <VtkMtgPlanMainBar />
                <VtkMtgPlanMaterials />
                <VtkMtgPlanComunications />
                <VtkMtgPlanAids />
                <VtkMtgPlanAgenda />
                <VtkSurvey />
            </div>
            <VtkModal />
        </div>
      :<div className="column large-20 medium-20 large-centered medium-centered" style={{height:'200px'}}>
          <VtkMtgPlanLoading />
      </div>
    );
}

function mapStateToProps(state) {
    return {
        meeting: state.meeting,
        meetingEvents: state.meetingEvents,
        helper: state.helper
    };
}
 
export default connect(mapStateToProps)(VtkMtgPlanMain);
  
  
