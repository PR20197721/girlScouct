import * as React from 'react';
import { connect } from 'react-redux';
import { Actions } from './store/actions'

export interface AgendaItemSelectedTimeProps {
    meeting:any,
    change_time:any,
    index: number,
}

export interface AgendaItemSelectedTimeState {
    value:any;
}


class AgendaItemSelected extends React.Component<AgendaItemSelectedTimeProps, AgendaItemSelectedTimeState> {

    constructor(props:AgendaItemSelectedTimeProps){
        super(props);
        this.state ={value:props.meeting.duration}
        this.change.bind(this);
    }
    
    change(e){
        this.props.change_time(this.props.meeting, e.target, this.props.index)
       this.setState({value:e.target.value})
    }

    componentWillReceiveProps(props){
        if(this.props.meeting.duration !== props.meeting.duration){
            this.setState({value:props.meeting.duration})
        }
        
    }


    render(){
        const options = [
            {value:5,text:'00:05'},
            {value:10,text:'00:10'},
            {value:15,text:'00:15'},
            {value:20,text:'00:20'},
            {value:25,text:'00:25'},
            {value:30,text:'00:30'},
            {value:35,text:'00:35'},
            {value:40,text:'00:40'},
            {value:45,text:'00:45'},
            {value:50,text:'00:50'},
            {value:55,text:'00:55'},
            {value:60,text:'00:60'}
        ]

       
        
        return <select value={this.state.value} onChange={(e)=>{this.change(e)}}>
            {options.map(({value,text})=>{

                return <option 
                key={value}
                value={value}
                //selected={value==this.props.meeting.duration}
                >
                    {text}
                </option>
            })}
        </select>
    }
};


export default AgendaItemSelected;


