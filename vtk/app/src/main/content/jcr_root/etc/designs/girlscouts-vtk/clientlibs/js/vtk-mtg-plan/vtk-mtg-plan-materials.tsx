import * as React from 'react';
import VtkContent from './common/content';
import VtkLinkPopUp from './common/linkpopup';

import './vtk-mtg-plan-materials.scss';
import {connect} from 'react-redux';
import { PERMISSION_CHECK } from './permission';

export interface VtkMtgPlanMaterialsProps {
  meetingEvents : any,
  user_variables : any,
  helper : any
}

function VtkMtgPlanMaterials(props : VtkMtgPlanMaterialsProps) {

  let {meetingEvents, helper, user_variables} = props;

  let generator = [
    {
      title: 'Meeting Overview',
      url: "/content/girlscouts-vtk/controllers/vtk.include.modals.modal_agenda_edit.html?mi" +
          "d=" + meetingEvents.uid + "&isOverview=true",
      show: true
    }, {
      title: 'Activity Plan',
      url: "/content/girlscouts-vtk/controllers/vtk.include.modals.modal_agenda_edit.html?mi" +
          "d=" + meetingEvents.uid + "&isActivity=true",
      show: !!helper.permissions && PERMISSION_CHECK()('permission_view_activity_plan_id')
    }, {
      title: 'Materials List',
      url: "/content/girlscouts-vtk/controllers/vtk.include.modals.modal_agenda_edit.html?mi" +
          "d=" + meetingEvents.uid + "&isMaterials=true",
      show: true
    }
  ];

  return (
    <div>
      <VtkContent idName="materials">
        <h6>PLANNING MATERIALS</h6>
        <ul>
          {generator.map((links) => {
            return (links.show)
              ? <li key={links.title}><VtkLinkPopUp {...links}/></li>
              : null;
          })}
        </ul>
      </VtkContent>
    </div>
  );
}

function mapStateToProps(state) {
  return {
    meetingEvents: state.meetingEvents,
    user_variables: state.user_variable,
    helper: state.helper
  };
}

export default connect(mapStateToProps)(VtkMtgPlanMaterials);
