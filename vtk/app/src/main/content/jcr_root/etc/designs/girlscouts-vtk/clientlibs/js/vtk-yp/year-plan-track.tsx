import * as React from 'react';
import * as data from './data'

import Meeting from './meeting';
import Meetings from './meetings';
import {getMeetings} from './data';

interface YplanTrackProps {
    track : string;
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

    selectPlan(name : string, url : string) {
        console.log(name, url)

            var is_new_yp = true;
            
            //show meeting lib or redirect to emty YP
            var is_show_meeting_lib = true;
            
            //year plan id from db
            var year_plan_id = 1;
            
            chgYearPlan(year_plan_id, url, 'THIS_IS_ERR_MGS_QA',  name, is_new_yp, is_show_meeting_lib);

    }

    public render() : JSX.Element {return(
            <div className="__year-plan-track-row">
                <div className="__year-plan-track columns small-20 small-centered">
                    <div className="table">
                        <div className="cell c16">
                            {this
                                .props
                                .track
                                .split('###')[1]}
                        </div>
                        <div
                            className={this.state.isOpen
                            ? "click-preview cell c3 __open"
                            : "click-preview cell c3 __close"}
                            onClick={() => {
                            this.openPreview();
                        }}>
                            Preview
                        </div>
                        <div className="cell c3">
                            <div
                                className={(true)
                                ? "btn button right"
                                : "btn button right inactive"}
                                onClick={() => {
                                this.selectPlan(this.props.track.split('###')[1], this.props.track.split('###')[0])
                            }}>{(true)
                                    ? 'SELECT'
                                    : 'SELECTED'}</div>
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
                                <div className="table">
                                    <div className="cell c16"></div>
                                    <div
                                        className={this.state.isOpen
                                        ? "click-preview cell c3 __open"
                                        : "click-preview cell c3 __close"}
                                        onClick={() => {
                                        this.openPreview();
                                    }}>
                                        Preview
                                    </div>
                                    <div className="cell c3">

                                        <div
                                            className={(true)
                                            ? "btn button right"
                                            : "btn button right inactive"}
                                            onClick={() => {
                                            this.selectPlan(this.props.track.split('###')[1], this.props.track.split('###')[0])
                                        }}>{(true)
                                                ? 'SELECT'
                                                : 'SELECTED'}</div>
                                    </div>
                                </div>
                                <br/>
                            </div>

                        </div>
                    </div>
           
                    <div className="">
                        <div
                            className="columns small-20 end small-centered"
                            style={{
                            borderBottom: '1px dotted black'
                        }}></div>
                    </div>
                </div>
           
        )}
}

export default YplanTrack;