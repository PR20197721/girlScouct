import * as React from "react";
import { SortableHandle } from "react-sortable-hoc";
import { connect } from "react-redux";
import { Actions } from "./store/actions";
import AgendaItemSelectedTime from "./agenda-item-time";
import AgendaItemOutDoor from "./agenda-item-outdoor";
import AgendaItemGlobal from "./agenda-item-global";
import { HAS_PERMISSION_FOR } from "./permission";
import { HELPER } from "./helper";

export interface AgendaItemMultipleProps {
	value: any;
	api: any;
	index: number;
	dispatch:any;
	change_time:any;
	schedule: any;
	helper: any;
	mid:string;
	openAgendaDetail: Function;
}

const DragHandle = SortableHandle(() => <div className="__touch-move" />);

const Options = props => {
	return (
		<div className="" style={{ clear: "both",display: 'flex', flexWrap:'nowrap' }}>
			<div style={{}}>
				<div className={"radio-react"}>
					<input
						type={"radio"}
						name={`p-${props.parentUid}`}
						id={props.activity.uid}
						disabled={props.permission}
						value={props.activity.uid}
						// defaultChecked={props.isSelected}
						checked={props.isSelected}
						onClick={() => { if(!props.isSelected){ props.api(props.activity, props.parent) }} }
					/>
					<label
						htmlFor={props.activity.uid}
				
					>
						<div> </div>
					</label>
				</div>
			</div>

			<div style={{overflow:'hidden'}}>
				<div className={"__vtk_activity_name"} style={{}}>
					<a
						onClick={()=>props.openAgendaDetail(props.activity.activityDescription,props.activity.name,props.parent.duration,props.activity.outdoor, props.parent.name)}
					>
						{props.activity.name}
					</a>
				</div>
			</div>
		</div>
	);
};


export function AgendaItemMultiple (props: AgendaItemMultipleProps) {

		const selectedActivity = props.value.multiactivities.filter(
					activity => activity.isSelected
				);
		

		const permissionCheck = HAS_PERMISSION_FOR()(
			"vtk_troop_haspermision_edit_yearplan_id"
		);

		return (
			<li className="__agenda-item __multiple">
				 {(permissionCheck || props.helper.currentDate < (Date.now()) && (new Date(props.helper.currentDate)).getFullYear() > 2000 )?<DragHandle />:null}

				<div className="__main">
					<div
						onClick={() => props.dispatch(Actions.VIEW_SUB_ACTIVITIES(props.value.__index__))}
						className={ props.value.__show_sub_activities__ ? "__arrow __open __top" : "__arrow __close __top"}
					/>
					<div className="__is-global">
                        <AgendaItemGlobal multiactivities={props.value.multiactivities} />
                    </div>
                    <div className="__is-outdoor">
						<AgendaItemOutDoor multiactivities={props.value.multiactivities} />
					</div>
					<div className="__time_counter">{(props.schedule)? props.value.__counter__: props.value.__index__+1 } </div>
					<div className="__description">
						<div className="__title">		
								{selectedActivity.length ? (
									<div className="__text">
										{props.value.name}
										{(props.value.name)?<span>:&nbsp;&nbsp;</span>:null}
										<a onClick={()=>props.openAgendaDetail(selectedActivity[0].activityDescription, selectedActivity[0].name,props.value.duration,selectedActivity[0].outdoor,props.value.name)}>
											{selectedActivity[0].name}
										</a>
									</div>
								) : (
									<div className="__text">{props.value.name}{(props.value.name)?':  ':null} Select an activity</div>
								)}
						</div>

						<div
							className="__more-options"
							style={{ display: props.value.__show_sub_activities__ ? "block" : "none" }}
						>
							{props.value.multiactivities.map((activity, index) => {
								return (
									<Options
										key={activity.uid}
										{...{parent:props.value}}
										{...{isSelected: (selectedActivity.length)? activity.path ==  selectedActivity[0].path : activity.isSelected  }}
										{...{ activity: activity }}
										{...{ parentUid: props.value.uid }}
										{...{api:props.api}}
										mid={props.mid}
										permission={!permissionCheck}
										openAgendaDetail={props.openAgendaDetail}
									/>
								);
							})}
			
						</div>
					</div>
					<div className="__time">
						{permissionCheck ? (<AgendaItemSelectedTime meeting={props.value} change_time={props.change_time} index={props.value.__index__} />) : <span style={{paddingLeft:'18px'}}>{HELPER.FORMAT.convertMinsToHrsMins(props.value.duration)}</span>}
					</div>
					<div className="__remove" />
				    </div>
			</li>
		);
}

export default connect()(AgendaItemMultiple);

