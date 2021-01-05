import * as React from 'react';
import {SortableHandle} from 'react-sortable-hoc';
import AgendaItemSelectedTime from './agenda-item-time';
import AgendaItemOutDoor from './agenda-item-outdoor';
import AgendaItemGlobal from './agenda-item-global';
import AgendaItemVirtual from './agenda-item-virtual';
import {HAS_PERMISSION_FOR} from './permission';
import {HELPER} from './helper';


export interface AgendaItemSingleProps {
    value: any,
    api: any,
    change_time: any
    schedule: any,
    helper: any,
    mid: string,
    openAgendaDetail: Function
}

const DragHandle = SortableHandle(() => <div className="__touch-move"></div>);

function AgendaItemSingle(props: AgendaItemSingleProps) {
    const {value, mid} = props;
    const permissionCheck = HAS_PERMISSION_FOR()('vtk_troop_haspermision_edit_yearplan_id');
    return (
        <li className="__agenda-item __single">
            {(permissionCheck || props.helper.currentDate < (Date.now()) && (new Date(props.helper.currentDate)).getFullYear() > 2000) ?
                <DragHandle/> : null}
            <div className="__main">
                <div style={{width: '29px', height: '22px'}}></div>
                <AgendaItemOutDoor multiactivities={props.value.multiactivities}/>
                <AgendaItemGlobal multiactivities={props.value.multiactivities}/>
                <AgendaItemVirtual multiactivities={props.value.multiactivities}/>
                <div className="__time_counter">{props.schedule ? props.value.__counter__ : props.value.__index__ + 1}</div>
                <div className="__description">
                    <div className="__title">
                        <div className="__text">
                            <a onClick={() => props.openAgendaDetail(props.value.multiactivities[0] != null ? props.value.multiactivities[0].activityDescription:"", value.name, value.subtitle, value.duration, props.value.multiactivities[0] != null ? props.value.multiactivities[0].outdoor:"", props.value.multiactivities[0] != null ? props.value.multiactivities[0].global:"",props.value.multiactivities[0] != null ? props.value.multiactivities[0].virtual:"", props.value.name)}>
                                {value.name}
                            </a>
                            {value.subtitle !== null ? <p>{value.subtitle}</p> : null}
                        </div>
                    </div>
                </div>
                <div className="__time">
                    {(permissionCheck) ?
                        <AgendaItemSelectedTime meeting={props.value} change_time={props.change_time} index={props.value.__index__}/> :
                        <span style={{paddingLeft: '18px'}}>{HELPER.FORMAT.convertMinsToHrsMins(props.value.duration)}</span>}
                </div>
                <div className="__remove"> {(permissionCheck) ? <i onClick={() => {
                    props.api(props.value)
                }} className="icon-button-circle-cross"/> : null}</div>
            </div>
        </li>
    );
}

export default AgendaItemSingle