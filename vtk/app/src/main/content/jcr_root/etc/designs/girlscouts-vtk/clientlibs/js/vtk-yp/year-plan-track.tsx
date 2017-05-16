import * as React from 'react';
import * as data from './data'

import Meetings from './meetings';
import { getMeetings } from './data';

interface YplanTrackProps {
    track: string;
};

interface YplanTrackState {
    isOpen?: boolean;
    meetings?: any[];
};

class YplanTrack extends React.Component < YplanTrackProps,
YplanTrackState > {

    public state : YplanTrackState;
    constructor() {
        super();

        this.state = {
            isOpen: false,
            meetings:[]
        }
    }



    openPreview() {
        debugger;
            if (!this.state.meetings.length) {
                data
                    .getMeetings(this.props.track.split('###')[0])
                    .then((response) => {

                        console.info(response)
                        this.setState({
                            'meetings': [response],
                            'isOpen': !this.state.isOpen
                        });
                    })
            } else {
                this.setState({
                    'isOpen': !this.state.isOpen
                })
            }

        }
    

        selectPlan(name:string,url:string) { 
            console.log(name,url)
        }

    public render(): JSX.Element {
        
            
        return (
            <div className="__year-plan-track-row">

                <div className="__year-plan-track columns small-20 small-centered">

                    <div className="table">
                        <div className="cell c16">
                            {this.props.track.split('###')[1]}
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
                            <div className="btn button right">SELECT</div>
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
                            <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Explicabo
                                laboriosam omnis molestias, corporis odio quisquam beatae. Sequi dolorem nostrum
                                doloremque, cupiditate quod, maxime rem veniam corrupti accusantium, ea facilis
                                ipsa!</p>
                            <h4>DAISY PETALS/ BADGES</h4>
                        </div>
                        <div className="columns small-20 small-centered">

                            <Meetings meetings={this.state.meetings} />
                        

                            <br />
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
                                    <div onClick={() => {
                                        console.log('click')
                                        this.selectPlan(this.props.track.split('###')[1], this.props.track.split('###')[0])
                                    }} className="btn button right">SELECT</div>
                                    </div>
                                </div>
                            <br />
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
        );}
}

export default YplanTrack;
