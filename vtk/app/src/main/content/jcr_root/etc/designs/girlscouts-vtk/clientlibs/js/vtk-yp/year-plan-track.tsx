import * as React from 'react';
import * as data from './data'

import Meeting from './meeting';
import Meetings from './meetings';
import {getMeetings} from './data';

interface YplanTrackProps {
    track : string;
    isnew :string;
};

interface YplanTrackState {
    isOpen?: boolean;
    meetings?: {
        desc: string;
        meetings: any[];
        name: string;
    };
};

declare var chgYearPlan : any;
declare var ________app________: string;
declare var ________app1________: string;
declare var ________isYearPlan________: boolean;
declare var ________currentYearPlanName________: string;

class YplanTrack extends React.Component < YplanTrackProps,
YplanTrackState > {

    public state : YplanTrackState;
    constructor() {
        super();

        this.state = {
            isOpen: false,
            meetings: {
                desc: '',
                meetings: [],
                name: ''
            }
        }
    }

    openPreview() {
        debugger;
        if (!this.state.meetings.meetings.length) {
            data
                .getMeetings(this.props.track.split('###')[0])
                .then((response) => {

                    console.info('response', response)
                    this.setState({
                        'meetings': {
                            name: response.name,
                            desc: response.desc,
                            meetings: response.meetings
                        },
                        'isOpen': !this.state.isOpen
                    });
                })

        } else {
            this.setState({
                'isOpen': !this.state.isOpen
            })

        }

    }

    public render() : JSX.Element {return(
            <div className="__year-plan-track-row">
              
                 <div className="columns small-20 small-centered" style={{padding:'0px'}}>
                    <div className="row">
                        <div className="__year-plan-track columns small-21">
                            <div className="table">
                                <div className="cell c16">
                                    {this
                                        .props
                                        .track
                                        .split('###')[1]} <span style={{marginLeft:'10px',color:'#FAA61A'}}>{(this.props.isnew=='isnew')?'NEW':null}</span> 

                                </div>
                                <div
                                    className={this.state.isOpen
                                    ? "click-preview cell c3 __open"
                                    : "click-preview cell c3 __close"}
                                    onClick={() => {
                                    this.openPreview();
                                }}>
                                {this.state.isOpen?'CLOSE PREVIEW':'PREVIEW'}
                                </div>
                                <div className="cell c3">
                                    <div
                                        className={(________app1________!==this.props.track.split('###')[0])
                                        ? "btn button right"
                                        : "btn button right inactive"}
                                        onClick={() => {
                                        selectPlan(this.props.track.split('###')[1], this.props.track.split('###')[0])
                                    }}>{(________app1________!==this.props.track.split('###')[0])
                                            ? 'SELECT'
                                            : 'SELECTED'}</div>
                                </div>
            
                            </div>
                        </div>
                        <div className="__year-plan-track columns small-3">
                        </div>
                    </div>
                </div>
             

                    <div className="row">
                        <div
                            className={this.state.isOpen
                            ? "__meetings"
                            : "__meetings hide"}>

                            <div className="big-arrow-white"></div>

                            <div className="columns small-20 small-centered">
                                <p>
                                    <b>Year Plan Overview</b>
                                </p>
                                <p>{this.state.meetings.desc}</p>
                                <h4>{this.state.meetings.name}</h4>
                            </div>
                            <div className="columns small-20 small-centered">

                                <Meetings meetings={this.state.meetings.meetings}/>

                                <br/>
                            </div>

                            <div className = "columns small-20 small-centered" style = {{padding:'0px'}}> 
                                <div className="row" style={{
                                    backgroundColor: 'transparent'
                                }}>
                                    <div className="columns small-21">
                                        <div className="table">
                                            <div className="cell c16"></div>
                                            <div
                                                className={this.state.isOpen
                                                ? "click-preview cell c3 __open"
                                                : "click-preview cell c3 __close"}
                                                onClick={() => {
                                                this.openPreview();
                                            }}>
                                                    {this.state.isOpen
                                                        ? 'CLOSE PREVIEW'
                                                        : 'PREVIEW'}
                                            </div>
                                            <div className="cell c3">
                                                <div
                                                    className={(________app1________!==this.props.track.split('###')[0])
                                                    ? "btn button right"
                                                    : "btn button right inactive"}
                                                    onClick={() => {
                                                    selectPlan(this.props.track.split('###')[1], this.props.track.split('###')[0])
                                                }}>{(________app1________!==this.props.track.split('###')[0])
                                                        ? 'SELECT'
                                                        : 'SELECTED'}
                                                </div>
                                            </div>
                                        </div>
                                        <br/>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
           

                    <div className="row">
                        <div className="columns small-20 small-centered " style={{
                                                   padding:'0px'
                            }}> 
                            <div
                                className="columns small-21 end"
                                style={{
                                borderBottom: '1px dotted black',
                                padding:'0px'
                            }}></div>
                        </div>
                    </div>
                </div>
           
        )}
}

export default YplanTrack;

export function selectPlan(name : string, url : string) {
        console.log(name, url)
        
            var confMsg ="Are You Sure? You will lose customizations that you have made";
        
   			//show meeting lib or redirect to emty YP
            var is_show_meeting_lib = true;
            if( ________app________ == 'senior' || 
           		 ________app________ == 'ambassador' ||
           		 	________app________ == 'cadette'){
            	is_show_meeting_lib= false;
            }
            
            chgYearPlan('', url, confMsg,  name, ________isYearPlan________, ________currentYearPlanName________, is_show_meeting_lib);
       }