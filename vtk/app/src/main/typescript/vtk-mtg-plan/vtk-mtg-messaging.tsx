import * as React from 'react';
import { Actions } from './store/actions'
import './../../scss/vtk-mtg-plan/vtk-mtg-messaging.scss';

import { connect } from 'react-redux';

export interface VtkMtgPlanMsgProps {
    messages:Object[],
    dispatch: Function
}


class VtkMtgPlanMsg extends React.PureComponent<VtkMtgPlanMsgProps, any> {
  
//    close(e){
//     this.props.dispatch(Actions.REMOVE_MESSAGE());
//    }
  
   render() {

    let message = undefined;
    let newClass = 'alert-box ';
    
    if(!!this.props.messages.length){
        message = this.props.messages[0];
        newClass +=   message.type || ''; 
    }

    function close(e){
        this.props.dispatch(Actions.REMOVE_MESSAGE());
    }


    return (
      (message)?<div data-alert className={newClass}>
      <div dangerouslySetInnerHTML={{__html: message.message}} ></div>
      <a onClick={(e)=>{
          return close.bind(this)(e)
        }} className="close">&times;</a>
    </div>:null
    );
  }
}

function mapStateToProps(state) {
    return {
        messages:state.messages, 
        dispatch:state.dispatch
    };
}
  
export default connect(mapStateToProps)(VtkMtgPlanMsg);

