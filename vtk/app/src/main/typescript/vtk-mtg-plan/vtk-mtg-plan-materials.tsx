import * as React from 'react';
import VtkContent from './common/content';
import VtkLinkPopUp from './common/linkpopup';

import './../../scss/vtk-mtg-plan/vtk-mtg-plan-materials.scss';
import {connect} from 'react-redux';
import {PERMISSION_CHECK, HAS_PERMISSION_FOR} from './permission';

export interface VtkMtgPlanMaterialsProps {
    meetingEvents: any,
    user_variables: any,
    helper: any,
    role: string,
    participationCode: string
}

function VtkMtgPlanMaterials(props: VtkMtgPlanMaterialsProps) {

    let {meetingEvents, helper, user_variables, role, participationCode} = props;

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
    let defaultVirtualLinks = [
        {
            title: "How to Plan a Virtual Meeting",
            url: "https://www.girlscouts.org/en/girl-scouts-at-home/troop-leaders/virtual-troop-meeting-ideas.html",
            show: true
        },
        {
            title: "Digital Icebreakers and Games",
            url: "https://www.girlscouts.org/en/girl-scouts-at-home/troop-leaders/digital-icebreakers-games.html",
            show: true
        },
        {
            title: "Easy Badge and Journey Adaptations",
            url: "https://www.girlscouts.org/en/girl-scouts-at-home/troop-leaders/badge-journey-virtual-meetings.html",
            show: true
        },
        {
            title: "Virtual Bridging Resources",
            url: "https://www.girlscouts.org/en/about-girl-scouts/renew/bridge-now.html",
            show: true
        }
    ];
    let showDefaultVirtualLinks = true;
    let virtualLinkHtml = meetingEvents.meetingInfo.meetingInfo.virtualLink ? meetingEvents.meetingInfo.meetingInfo.virtualLink.str : null;
    let showVirtualLinks = (virtualLinkHtml || showDefaultVirtualLinks) && HAS_PERMISSION_FOR()('vtk_troop_haspermision_edit_yearplan_id');

    return (
        <div>
            <VtkContent idName="materials">
            <div className={showVirtualLinks ? "columns medium-12" : null}>
                <h6>PLANNING MATERIALS</h6>
                <ul>
                    {generator.map((links) => {
                        return (links.show)
                            ? <li key={links.title}><VtkLinkPopUp {...links}/></li>
                            : null;
                    })}
                </ul>
            </div>
            {showVirtualLinks && virtualLinkHtml &&
                <div className="columns medium-12">
                    <h6>VIRTUAL MEETING RESOURCES</h6>
                    <div dangerouslySetInnerHTML={{__html: virtualLinkHtml}}></div>
                </div>
            }
            {showVirtualLinks && !virtualLinkHtml && showDefaultVirtualLinks &&
                <div className="columns medium-12">
                    <h6>VIRTUAL MEETING RESOURCES</h6>
                    <ul>
                        {defaultVirtualLinks.map((link) => {
                            return (link.show)
                                ? <li key={link.title}><a href={link.url} target="_blank">{link.title}</a></li>
                                : null;
                        })}
                    </ul>
                </div>
            }
            </VtkContent>
        </div>
    );
}

function mapStateToProps(state) {
    return {
        meetingEvents: state.meetingEvents,
        user_variables: state.user_variable,
        helper: state.helper,
        role: state.role,
        participationCode: state.participationCode
    };
}

export default connect(mapStateToProps)(VtkMtgPlanMaterials);
