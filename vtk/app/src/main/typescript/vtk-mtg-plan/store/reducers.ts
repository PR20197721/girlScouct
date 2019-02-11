import { ActionsTypes } from './actionsType';

declare const _USER_VARIBLES_;

let InitialState = {
    meeting:undefined,
    loading:false,
    meetingEvents:undefined,
    messages:[],
    user_variable:{},
    helper:undefined,
    activities:[],
    modal: {},
    survey: {},
    videos: {},
}

interface modal{
  title:string,
  description:'string'
}


export function reduce(state = InitialState, action = {type:'', payload:{}}) {
   
    switch (action.type) {
      //DEFAULT

      //LOADING
      case ActionsTypes.LOADING:
        return {...state,...action.payload}

      //FETCHING
      case ActionsTypes.FETCHING_PAGE:
        return {...state,...{loading:true}}
      case ActionsTypes.FETCHING_DONE:
        return {
            ...state, 
            ...{loading:false},
            ...action.payload,
            // ...{user_variable:{...document.getElementById('data-log').dataset}} //Get the data from the jsp //
          }
      case ActionsTypes.FETCHING_INTERVAL_DONE:
          return {
              ...state, 
              ...action.payload//Get the data from the jsp //
            }
  
      //ACTIVITIES
      case ActionsTypes.CHANGE_ORDER_OF_ACTIVITIES:
          return {...state,...action.payload};
      
      //MESSAGE
      case ActionsTypes.NEW_MESSAGE:
        return {...state,...{messages:action.payload}}
      case ActionsTypes.REMOVE_MESSAGE:
        return {...state,...{messages:[]}}
      //AGENDA
      case ActionsTypes.AGENDA_REMOVE:
          return {...state, ...action.payload}
      case ActionsTypes.AGENDA_CHANGE_SUBACTIVITY:
        return {...state, ...action.payload};
      case ActionsTypes.TOGGLE_SUB_ACTIVITIES:
        return {...state, ...action.payload}
      case ActionsTypes.AGENDA_CHANGE_TIME:
        return {...state, ...action.payload}
      //MODAL
      case ActionsTypes.DESTROY_MODAL:
        return {...state,...{modal:{}}}
      case ActionsTypes.CREATE_MODAL:
        return {...state,...action.payload}
      default:
        return state;
    }
 }