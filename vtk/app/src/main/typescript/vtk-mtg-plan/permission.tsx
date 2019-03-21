import store from './store/store'

    export let PERMISSION_CHECK = function (){
        var _store = store.getState();

        const user_variable = _store.user_variable;
        const permissions  = _store.helper.permissions;

        return function (permissionType:string){
            if(permissionType in user_variable){
                return !!~permissions.indexOf(user_variable[permissionType])
            }else{
                throw new TypeError("permission type does not exist");
            }
        }
    }


    export let  HAS_PERMISSION_FOR= ()=>{
        var _store = store.getState();

        const user_variable = _store.user_variable;

        return function (permissionType:string){
            if(permissionType in user_variable){
                const has = _store.user_variable[permissionType]
                return (has == 'true')? true : false; //Because is a string
            }else{
                throw new TypeError("permission type does not exist");
            }
        }

    }


    export const DATE_MORE_THAN_1977 = (date:number)=> date > new Date("1/1/1977").getTime();
    
    const MEETING_TYPE = ()=> store.getState().meetingEvents.type;
  
    export const IS_MEETING_CANCEL = () => MEETING_TYPE() == 'MEETINGCANCELED';

    export const IS_MEETING = () => MEETING_TYPE() == 'MEETING';




