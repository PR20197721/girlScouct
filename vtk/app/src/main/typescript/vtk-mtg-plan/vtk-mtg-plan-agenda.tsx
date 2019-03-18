import * as React from 'react';
import VtkContent from './common/content';
import {SortableContainer, SortableElement, arrayMove, SortableHandle} from 'react-sortable-hoc';
import { connect } from 'react-redux';

import './../../scss/vtk-mtg-plan/vtk-mtg-plan-agenda.scss';
import { ActionsTypes } from './store/actionsType';
import { Actions } from './store/actions'

import AgendaItemSingle from './agenda-item-single-activity';
import AgendaItemMultiple from './agenda-item-multiple-activities';

import {HELPER}  from './helper';
import moment from 'moment';

import COMUNICATION from './comunications';

import { HAS_PERMISSION_FOR , DATE_MORE_THAN_1977, PERMISSION_CHECK, IS_MEETING_CANCEL} from './permission';


declare var popup,$;

export interface VtkMtgPlanAgendaProps {
  activities:any[],
  dispatch: Function,
  meetingEvents: any,
  meeting: any,
  sum,
  helper:any,
  user_variable:any
}

const DragHandle = SortableHandle(() => <div className="__touch-move"></div>);

const SortableItem =  SortableElement(({value,index}) => { 
  return (value.value.multiactivities && (value.value.multiactivities.length > 1))
    ?<AgendaItemMultiple key={`agenda-item-${value.value.uid}`} {...index} value={value.value} api={value.api} change_time={value.change_time}  schedule={value.meeting.schedule} helper={value.helper} mid={value.mid} openAgendaDetail={value.openAgendaDetail}/>
    :<AgendaItemSingle key={`agenda-item-${value.value.uid}`} {...index} value={value.value} api={value.remove} change_time={value.change_time} schedule={value.meeting.schedule} helper={value.helper} mid={value.mid} openAgendaDetail={value.openAgendaDetail}/>
});

const SortableList = SortableContainer(({items}) => {
  const isEditable = HAS_PERMISSION_FOR()('vtk_troop_haspermision_edit_yearplan_id');
  let {change_time,meeting,mid,helper, remove, api, openAgendaDetail} = items
  return (
    <div>
      <ul className={(items.disabledText)?"__agenda-items __past-year":"__agenda-items"}>
        {items.collection.map((value, index) => { 
          return (<SortableItem key={`item-${value.uid}-${index}`} index={index} value={{value,
            api,
            remove,
            index:value.__index__, 
            change_time,
            meeting,
            mid, 
            helper,
            openAgendaDetail
          }} disabled={!isEditable} />)
        })}
      </ul>
    </div>
  );
});

export function VtkMtgPlanAgenda (props: VtkMtgPlanAgendaProps) {
    const deleteAgendaModal = (agendaTitle, agendaPath, thisMeetingPath, value)=>{
        var cll = "<p><b>Are you sure you want to remove the activity " + agendaTitle + " from your meeting plan?</b></p>";

   
        function okCallBack() {

          props.dispatch(Actions.REMOVE_AGENDA(agendaPath, thisMeetingPath, value))
          popup.close();
        }

        function cancelCallBack() {
          $(".vtk-js-modal_ok_action").off("click");
          $(".vtk-js-modal_cancel_action").off("click");
          popup.close();

        }

        popup.confirm("DELETE ACTIVITY", cll,okCallBack,cancelCallBack);
    }
    const change_time = (activity, target, index) => {
      props.dispatch(Actions.CHANGE_TIME(activity, props.meeting, target, index))
    }
    const onChangeSubActivity = (selected, parent) => {
			props.dispatch(Actions.CHANGE_SUB_ACTIVITIES({
					selected: selected,
					parent: parent,
					call: {
						url: "/content/girlscouts-vtk/controllers/vtk.controller.html",
						cache: false,
						method: "post",
						dataType: "json",
						params: {
							act: "selectSubActivity",
							aPath: parent.path,
							subAPath: selected.path,
							mPath: props.meetingEvents.path,
							a: Date.now(),
						},
					},
				}));
		};
    const remove = (value) =>{
       deleteAgendaModal(value.name,value.path,props.meetingEvents.path, value);
    }
    const  onSortEnd = ({oldIndex, newIndex}) => {
      props.dispatch(Actions.CHANGE_ACTIVITIES_ORDER({oldIndex,newIndex}));
 
    }; 
    const  openAgendaDetail = (materials, meetingName, recommendedTime, outdoor, activityPath) => {


      if(props.user_variable.vtk_current_year==props.user_variable.user_current_year){

        var modal = async () => {
          let _recommendedTime = recommendedTime;
          let response;
          let _level:string=  props.meetingEvents.meetingInfo.level.toLowerCase();
          let _id:string = props.meetingEvents.meetingInfo.id;

          let _url = `/content/girlscouts-vtk/meetings/myyearplan${props.user_variable.vtk_current_year}/${_level}/${_id}/activities.-1.json`

          try{
            response = await COMUNICATION.getOriginalActivity({url:_url,payload:''});
            let object = response.data;
            let act;
            let arrayOfMultiactivities: any[] =[];
            
            for (let activity in object){
              arrayOfMultiactivities.push(object[activity]);
            }
          
            let _find: any[] = arrayOfMultiactivities.filter((m)=>m.name === activityPath)

            if (_find.length) {
              _recommendedTime = _find[0].duration;
            }else{
              try {
                  for (var mainActivityItem in arrayOfMultiactivities) {
                      var found = false;
                      if(arrayOfMultiactivities[mainActivityItem].hasOwnProperty('multiactivities')){
                          for (var activityKey in arrayOfMultiactivities[mainActivityItem].multiactivities) {
                              try {
                                  if (activityKey.indexOf('agenda') == 0) {
                                      if (arrayOfMultiactivities[mainActivityItem].multiactivities[activityKey].name == meetingName) {
                                          _recommendedTime = arrayOfMultiactivities[mainActivityItem].duration;
                                          found = true;
                                          break;
                                      }
                                  }
                              } catch (err) {
                                  console.log(err);
                              }
                          }
                      }
                      if(found){
                          break;
                      }
                  }
              }catch(er){console.log(er);}
            }

          } catch(err){
            console.log(err)
          }

          let template = 
          `<p><b>${_recommendedTime} min</b> Recommended time</p>
          <h3 style="text-transform:uppercase">${meetingName} ${(outdoor)?'- Outside':''}</h3>
          <br />
          <div>${materials}</div>`;


          props.dispatch({
          type:ActionsTypes.CREATE_MODAL,
          payload:{
            modal:{
              title:"ACTIVITY",
              description: template
            }
          }
          })

        }
        
        
        
        modal();
      }
  }


    const isEditable = HAS_PERMISSION_FOR()('vtk_troop_haspermision_edit_yearplan_id');

    return (<div>
        <VtkContent idName="agenda">
          <h6>Agenda</h6>

          <SortableList items={{
            collection:[...props.activities],
            api:onChangeSubActivity, 
            remove,
            change_time, 
            meeting: props.meeting, 
            mid:props.meetingEvents.uid,
            helper:props.helper,
            openAgendaDetail:openAgendaDetail,
            disabledText: props.user_variable.vtk_current_year!==props.user_variable.user_current_year
            }} onSortEnd={onSortEnd}  SortableContainer={false} useDragHandle={true}  />
          
          <p style={{fontSize:'14px',textAlign:'right',paddingRight: '116px', fontWeight: 'bold'}}>
              {HELPER.FORMAT.convertMinsToHrsMins(props.sum.duration)}
          </p>
          
          {(PERMISSION_CHECK()('permission_edit_meeting_id')&& !IS_MEETING_CANCEL())?<a className="add-btn" data-reveal-id ="modal_agenda">
            <i className="icon-button-circle-plus"></i>Add Agenda Item
          </a>:null}

        </VtkContent>
    </div>);
}

const mapStatetoProps = (state) => {
  return {
    meetingEvents: state.meetingEvents,
    meeting: state.meeting,
    activities : state.activities,
    dispatch : state.dispatch,
    sum : state.activities.reduce((c,n)=>{
      return {duration: c.duration+n.duration}
    }),
    helper : state.helper,
    user_variable: state.user_variable
  }
}


export default connect(mapStatetoProps)(VtkMtgPlanAgenda);



