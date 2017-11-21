import * as React from 'react';
import * as data from './data'

import {getMeetings, modal} from './data';

import Category from './category';
import Meeting from './meeting';
import Meetings from './meetings';

interface YplanTrackProps {
    track : string;
    isnew: string;
    last: boolean;
    store: any;
    first: boolean;
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
declare var ________troopName________: string;

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
        if (!this.state.meetings.meetings.length) {
            data
                .getMeetings(this.props.track.split('###')[0])
                .then((response) => {

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


    public render(): JSX.Element {

        return (
            <div className="__year-plan-track-row">
              
                 <div className="columns medium-20 small-22 small-centered medium-centered" style={{padding:'0px'}}>
                    <div className="row">
                        <div className="__year-plan-track columns medium-21 small-24">
                            <div className="table">
                            <div className="cell c16" style={{paddingLeft:'5px'}}>
                                    {this
                                        .props
                                        .track
                                        .split('###')[1]} <span style={{ marginLeft: '12pt', color: '#FAA61A', fontWeight: 'bold' }}>{(this.props.isnew == 'isnew') ? 'NEW' : null}</span> <br />
                                    
                                        {(this.props.first && (________app________ === "brownie" || ________app________ === "daisy" || ________app________ === "junior")) ?
                                        <span>
                                            {'To learn about ' + ________app________.charAt(0).toUpperCase()+________app________.slice(1)  + ' badges use the new'} <a href='https://www.girlscouts.org/en/our-program/badges/badge_explorer.html' target="_blank">Badge Explorer.</a>
                                        </span>
                                        : null
                                    }
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
                                        selectPlan(this.props.track.split('###')[1], this.props.track.split('###')[0], this.props.store)
                                    }}>{(________app1________!==this.props.track.split('###')[0])
                                            ? 'SELECT'
                                            : 'SELECTED'}</div>
                                </div>
            
                            </div>
                        </div>
                        <div className="__year-plan-track columns medium-3">
                        </div>
                    </div>
                </div>
             

                    <div className="row">
                        <div
                            className={this.state.isOpen
                            ? "__meetings"
                            : "__meetings hide"}>

                        <div className="big-arrow-white show-for-medium-up"></div>
                        <div className="big-arrow-white-small show-for-small-only"></div>

                            <div className="columns medium-20 small-22 small-centered medium-centered">
                                <p>
                                    <b>Year Plan Overview</b>
                                </p>
                                <p>{this.state.meetings.desc}</p>
                                <h4>{this.state.meetings.name}</h4>
                                <br />
                    </div>
                            <div className="columns medium-20 small-24 medium-centered">

                                <Meetings meetings={this.state.meetings.meetings}/>

                                <br/>
                            </div>

                            <div className = "columns medium-20 small-22 small-centered medium-centered" style = {{padding:'0px'}}> 
                                <div className="row" style={{
                                    backgroundColor: 'transparent'
                                }}>
                                    <div className="columns medium-21 small-24">
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
                                                    selectPlan(this.props.track.split('###')[1], this.props.track.split('###')[0], this.props.store)
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
                    padding: '0px'
       
                }}> 
                            <div
                                className="columns small-21 end"
                                style={{
                                borderBottom: (this.props.last)?'none':'1px dashed black',
                                padding:'0px'
                            }}></div>
                        </div>
                    </div>

                </div>
                
                 
                
               
           
        )}
}

export default YplanTrack;



export function selectPlan(name: string, url: string, store?: Function) {
       store({
            name: name,
            url:url,
            is_show_meeting_lib: (url != '' || ________app________ == 'senior' || ________app________ == 'ambassador' || ________app________ == 'cadette')
                ? false : true
       }, function () { modal.publish('pop-select', 'open') });
}