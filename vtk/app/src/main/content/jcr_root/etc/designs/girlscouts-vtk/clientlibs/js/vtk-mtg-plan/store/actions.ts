import { ActionsTypes } from "./actionsType";
import COMUNICATIONS from "../comunications";
import { HELPER } from "../helper";
import store from './store';

declare const ___ENV___, printModal, VTKDataWorker, vtkTrackerPushAction;

export namespace Actions {
	//UTILITIES
	function makeUrl(env) {
		return env.db_host + "/" + env.modal + "/" + env.meeting; // to take out
	}
	function reorderActivities(array){
		let last = 0;

		const time = new Date(store.getState().helper.currentDate);
		const currenTime = time.getHours()*60 + time.getMinutes();

		return array.map((_item, _index)=>{
			_item['__counter__'] = HELPER.FORMAT.convertMinsToHrsMins(last+currenTime)
			_item['__index__'] = _index;
			last+=_item.duration;
			return _item;
		})
	}
	function processSurveyData(data, meetingId) {
		for (var key in data) {
			if (data[key].hasOwnProperty('meetingid')) {
				var idlist = data[key].meetingid.split(',');
				for (var i = idlist.length - 1; i >= 0; i--) {
					if (meetingId.indexOf(idlist[i]) > -1) {
						const survey = data[key];

						return {
							title: survey.bannerCopyBold,
							description: survey.bannerCopy,
							linkHref: survey.surveyLink,
							buttonTitle: survey.buttonCopy,
							meetingId
						}
					}
				}
			}
		}

		return {}
	}


	function processVideos(data) { 


		let orginizeData = {}
		
		for (var key in data) {
			if (data[key].hasOwnProperty('meetingid')) {
				const video = data[key];
				var idlist = video.meetingid.split(',');
				for (var i = idlist.length - 1; i >= 0; i--) {
					if (orginizeData.hasOwnProperty(idlist[i])) {
						orginizeData[idlist[i]].push({
							description: "",
							docType: 'movie',
							isOutdoorRelated: false,
							path: null,
							refId: video.url,
							title: video.name,
							type: "AID",
							uid: key,
						})
					} else { 
						orginizeData[idlist[i]] = [];
						orginizeData[idlist[i]].push({
							description: "",
							docType: 'movie',
							isOutdoorRelated: false,
							path: null,
							refId: video.url,
							title: video.name,
							type: "AID",
							uid: key,
						})
					}
					

					// if (meetingId.indexOf(idlist[i]) > -1) {
					// 	const video = data[key];

					// 	return {
					// 		description: "",
					// 		docType: 'movie',
					// 		isOutdoorRelated:false,
					// 		path: null,
					// 		refId:video.url,
					// 		title:video.name,
					// 		type:"AID",
					// 		uid:key,
					// 	}
					// }
				}
			}
		}

		return orginizeData
	}

	let __interval: HELPER.INTERVAL;

	export function INTERVAL_FETCH(url){

		let resolve = (response,count,dispatch)=>{

			let  activitiesState = store.getState().activities;
			
			let getThePreviewStateOfOpenClose = (actPath) => {
				const _activities = activitiesState.filter((activity)=>activity.path===actPath)
				return (_activities.length)?_activities[0].__show_sub_activities__:false;
			};

			let last = 0;
			const time = new Date(response.data.yearPlan.helper.currentDate);
			const currenTime = time.getHours()*60 + time.getMinutes();

			const data = { 
				meeting: response.data.yearPlan,
				meetingEvents: response.data.yearPlan.meetingEvents[0],
				helper: response.data.yearPlan.helper,
				activities: response.data.yearPlan.meetingEvents[0].meetingInfo.activities.map((activity,index)=>{
					activity['__index__']= index;
					activity['__show_sub_activities__'] = getThePreviewStateOfOpenClose(activity.path);
						activity['__counter__'] = HELPER.FORMAT.convertMinsToHrsMins(last + currenTime);
						last+=activity.duration;
					return activity;
				}),
	
			};


			// // //IDEA

			// let previousStateActivities:any = [...activitiesState];
			// let nextStateActivities:any = [...data.activities];		

			// let dd = previousStateActivities.map((x,index)=>{
			// 	x.uid=null;
			// 	x.path=null
			// 	x.multiactivities[0].uid = null;
			// 	x.multiactivities[0].path =null;
			// 	nextStateActivities[index].uid = null;
			// 	nextStateActivities[index].path = null
			// 	nextStateActivities[index].multiactivities[0].uid = null;
			// 	nextStateActivities[index].multiactivities[0].path = null;
			// 	return JSON.stringify(nextStateActivities[index])===JSON.stringify(x)}
			// )
		
			// let _message ={messages:[]};
			// if(!dd.every((d)=>d)){
			// 	_message = {messages:[{type:'warning',message:'<h3>Some Body is modyfing this troop sa this moment </h3>'}]};
				
			// }

			// let _data = {...data,..._message};



			store.dispatch({ type: ActionsTypes.FETCHING_INTERVAL_DONE, payload: data });


	      
			
			
		};
		let error = (r) => {

			if (r.response && r.response.status !== 304 ) { console.log(r) }
		};
		let call = COMUNICATIONS.INTERVAL_FETCH(url,resolve,error)
		
		__interval = new HELPER.INTERVAL(call,10000);
		__interval.on();	
	}

	export function FETCH(url) {
		INTERVAL_FETCH(url);
		return async(dispatch, getState) => {
			try{
				dispatch({ type: ActionsTypes.FETCHING_PAGE });
				const response = await COMUNICATIONS.FETCH(url);

				console.log(response);

				let last = 0;
				const time = new Date(response[0].data.yearPlan.helper.currentDate);
				const currenTime = time.getHours() * 60 + time.getMinutes();
				

				const user_variable = { ...document.getElementById('data-log').dataset };

				const meetingId = response[0].data.yearPlan.meetingEvents[0].meetingInfo.id;

				const survey = processSurveyData(response[1].data, meetingId);

				const videos = processVideos(response[2].data);

				const data = { 
					meeting: response[0].data.yearPlan,
					meetingEvents: response[0].data.yearPlan.meetingEvents[0],
					helper: response[0].data.yearPlan.helper,
					activities: response[0].data.yearPlan.meetingEvents[0].meetingInfo.activities.map((activity,index)=>{
						activity['__index__']= index;
						activity['__show_sub_activities__'] = true;
							activity['__counter__'] = HELPER.FORMAT.convertMinsToHrsMins(last + currenTime);
							last+=activity.duration;
						return activity;
					}),
					user_variable,
					survey,
					videos

				};

				dispatch({ type: ActionsTypes.FETCHING_DONE, payload: data });

			}catch(error){
                 (error) :void=>{ console.log(error) }
			}
		}
	}

	//MESSAGE TOP
	export function CREATE_MESSAGE() {}
	export function REMOVE_MESSAGE() {
		return { type: ActionsTypes.REMOVE_MESSAGE };
	}
	//Message window
	export function SHOW_MESSAGE() {
		printModal.alert("HELO", "DESCRIPTION");
		return { type: "" };
	}
	//AGENDA
	export function REMOVE_AGENDA(agendaPath,meetingPath, value) {
		return async (dispatch, getState) => {
			try {

				dispatch({ type: ActionsTypes.LOADING, payload:{loading:true}});
				let activityState = getState().activities;

				__interval.skip(3000);
				let response = await COMUNICATIONS.REQUEST({
					url: `/content/girlscouts-vtk/controllers/vtk.controller.html?act=RemoveAgenda&rmAgenda=${agendaPath}&mid=${meetingPath}`, // JQuery loads serverside.php
			        data: '',
			        dataType: 'json',
				});				
				
				activityState.splice(value.__index__,1)

				vtkTrackerPushAction('RemoveAgenda');

				let activities = reorderActivities(activityState);

				dispatch({ type: ActionsTypes.AGENDA_REMOVE, payload:{activities,loading:false} });
			} catch (error) {
				console.error(error);
			}
		};
	}
	export function CHANGE_SUB_ACTIVITIES({selected, parent, call}) {
		return async (dispatch, getState) => {
		
			try {
				dispatch({ type: ActionsTypes.LOADING, payload:{loading:true}});
				__interval.skip(3000);
				const response = await COMUNICATIONS.REQUEST(call);
				
				let activityState = [...getState().activities];

				let multiactivities = activityState[parent.__index__].multiactivities.map((activity)=>{
					activity.isSelected = (activity.path == selected.path);
					return activity;
				});

				activityState[parent.__index__].multiactivities = [...multiactivities];

				dispatch({
					type:ActionsTypes.AGENDA_CHANGE_SUBACTIVITY,
					payload:{
						activities:activityState,
						loading: false
					}
				})
			} catch (error) {
				console.error(error);
			}
		}
	}
	export function VIEW_SUB_ACTIVITIES(index){
		return (dispatch,getState) => {
			let activities = [...getState().activities];
			activities[index].__show_sub_activities__ = !activities[index].__show_sub_activities__;

			dispatch({type:ActionsTypes.TOGGLE_SUB_ACTIVITIES,payload:{activities}})
		}
	}
	export function CHANGE_ACTIVITIES_ORDER({oldIndex,newIndex}) {
		return async (dispatch, getState) => {
			try {
				dispatch({type:ActionsTypes.LOADING,payload:{loading:true}})

				let activitiesState = getState().activities;
	
				const meetingRefId = getState().meetingEvents.refId;
				const meetingPath = getState().meetingEvents.path;

				var b = activitiesState[oldIndex];
				
				// activitiesState[oldIndex] = activitiesState[newIndex];
				// activitiesState[newIndex] = b;

				activitiesState.splice(oldIndex, 1);
				activitiesState.splice(newIndex, 0, b);


				let newOrder = 	activitiesState.map((activity)=>activity.__index__+1).join(',');


				let activities = reorderActivities(activitiesState);

				dispatch({type:ActionsTypes.CHANGE_ORDER_OF_ACTIVITIES,payload:{activities}});


                __interval.skip(3000);
				let response = await COMUNICATIONS.REQUEST({
					url: '/content/girlscouts-vtk/controllers/vtk.controller.html?act=RearrangeActivity&mid='+meetingRefId+'&isActivityCngAjax='+ newOrder,
					data: '',
					dataType: 'html',
				})


				dispatch({type:ActionsTypes.LOADING,payload:{loading:false}})
				vtkTrackerPushAction('MoveAgendas');


			} catch (error) {
				console.error(error);
			}
		};
	}
	export function CHANGE_TIME(activity,meeting, target, index) {
		return async (dispatch, getState) => {
			try {
				dispatch({type:ActionsTypes.LOADING,payload:{loading:true}})

				const meetingPath = getState().meetingEvents.path;
                __interval.skip(3000);
				let response = await COMUNICATIONS.REQUEST({
					 url: '/content/girlscouts-vtk/controllers/vtk.controller.html',
					params:{
						act:'EditAgendaDuration',
						editAgendaDuration:target.value,
						aid:activity.path,
						mid:meetingPath
					},
					

					 type: 'POST',
					 cache:false,
				})

				vtkTrackerPushAction('ChangeAgenda');

				let activitiesState = [...getState().activities];
				activitiesState[index].duration = parseInt(target.value);
				let activities = reorderActivities(activitiesState);
				dispatch({type:ActionsTypes.AGENDA_CHANGE_TIME,payload:{activities, loading:false}})
			} catch (error) {
				console.error(error);
			}
		};
	}
}



