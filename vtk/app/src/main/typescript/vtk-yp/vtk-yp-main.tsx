import '../../scss/vtk-yp/vtk-yp-main.scss';

import * as React from 'react';
import * as data from './data'

import Category from './category';
import Gray from './vtk-gray';
import VtkPopUp from './vtk-popup';
import {selectPlan} from './year-plan-track';
import Head from './head';
import BYOHead from './byo-head';
import Meetings from './meetings';
import * as ReactDOM from "react-dom";


interface VtkMainYpProps {
    data: any;
};

interface VtkMainYpState {
    pdf: string;
    data: {
        name: string,
        url: string,
        is_show_meeting_lib: true
    };

    meeting: {
        desc: string,
        name: string,
        meetings: any[]
    }

    track: string;
    showMeetingSearch: boolean;
    showTracks: boolean;
    showPreview: boolean;
};

declare var $: any;
declare var ________app________: string;
declare var ________app1________: string;
declare var ________isYearPlan________: boolean;
declare var ________currentYearPlanName________: string;
declare var ________troopName________: string;
declare var chgYearPlan: Function


class VtkMainYp extends React.Component <VtkMainYpProps,
    VtkMainYpState> {

    meetingsPick: Object = {};

    constructor() {
        super();
        this.state = {
            pdf: '',
            data: {
                name: '',
                url: '',
                is_show_meeting_lib: true
            },
            meeting: {
                desc: '',
                name: '',
                meetings: []

            },
            track: '',
            showTracks: false,
            showMeetingSearch: false,
            showPreview: false
        }
    }

    store(state, func) {
        this.setState({
            data: state
        }, func)
    }

    reset() {
        this.setState({
            data: {
                name: '',
                url: '',
                is_show_meeting_lib: true
            }
        })
    }

    componentDidMount() {
        data
            .getPDF()
            .then((url) => {
                this.setState({pdf: url})
            })
    }

    clickHander() {
        selectPlan('Custom Year Plan', '', this.store.bind(this))
    }
    openMeetingSearch(){
        this.setState({
            showTracks: false,
            showPreview: false,
            showMeetingSearch: true
        });
    }
    openTracks() {
        this.setState({
            showTracks: !this.state.showTracks,
            showPreview: false,
            showMeetingSearch:false
        });
    }


    scrollto() { //NOP
        $('html, body').animate({scrollTop: document.getElementsByClassName('__meetings')[0].getBoundingClientRect().top + window.scrollY}, 'slow');
    }

    showPreview(props) {
        if (this.meetingsPick.hasOwnProperty(props.track)) {
            this.setState({
                meeting: this.meetingsPick[props.track],
                track: props.track
            });

        } else {
            data.getMeetings(props.track.split('###')[0]).then((meeting) => {

                this.meetingsPick[props.track] = meeting;
                this.setState({
                    meeting,
                    track: props.track
                });

            }).catch(() => {
                console.log('ERROR');
            })
        }


        this.setState({
            showPreview: !this.state.showPreview
        }, this.scrollto)


    }


    closePreview() {
        this.setState({
            showPreview: false
        }, this.scrollto)
    }

    public render(): JSX.Element {

        const {header, bottom, customizedYearPlanContent} = this.props.data;
        const {title, subtitle} = header;

        function renderChild(state) {
            return (________isYearPlan________ == false)
                ? <div className={state.data.name}>
                    <p>
                        <b>
                            You have selected the Year Plan below for {________app________
                            .charAt(0)
                            .toUpperCase() + ________app________.slice(1)}
                            {(________troopName________.match(/troop/i))
                                ? null
                                : 'Troop'}
                            {'  '}
                            {`${________troopName________}.`}
                            {'  '} Is this correct?
                        </b>
                    </p>
                    <table
                        style={{
                            width: '70%'
                        }}>
                        <tbody>
                        <tr>
                            <td>Troop Year Plan</td>
                            <td>{`${state.data.name}`}</td>
                        </tr>
                        </tbody>
                    </table>
                    <table
                        style={{
                            width: '70%',
                            margin: '0 auto'
                        }}>
                        <tbody>
                        <tr>
                            <td
                                style={{
                                    textAlign: 'center'
                                }}>
                                <div
                                    className="btn button btnCancel"
                                    style={{
                                        width: '100%', color: '#18aa51'
                                    }}
                                    onClick={() => {
                                        data
                                            .modal
                                            .publish('pop-select', 'close')
                                    }}>NO, CANCEL
                                </div>
                            </td>
                            <td
                                style={{
                                    textAlign: 'center'
                                }}>
                                <div
                                    className="btn button"
                                    style={{
                                        width: '100%'
                                    }}
                                    onClick={() => {
                                        this.openMeetingSearch()
                                        chgYearPlan('', state.data.url, '', state.data.name, ________isYearPlan________, ________currentYearPlanName________, state.data.is_show_meeting_lib);
                                        if (state.data.name === 'Custom Year Plan') {
                                            data
                                                .modal
                                                .publish('pop-select', 'close')
                                        }
                                    }}>YES, SELECT
                                </div>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
                : <div className={state.data.name}>
                    <p>
                        <b>
                            You want to replace your current Year Plan with the new Year Plan listed below
                            for {`${________app________
                            .charAt(0)
                            .toUpperCase() + ________app________.slice(1)}`}
                            {(________troopName________.match(/troop/i))
                                ? null
                                : "Troop"}
                            {' '}
                            {`${________troopName________}.`}
                            {' '}
                            Is this correct?
                        </b>
                    </p>
                    <table>
                        <tbody>
                        <tr>
                            <td>Current Year Plan:</td>
                            <td>{`${________currentYearPlanName________}`}<br/>
                                <b>IMPORTANT:</b>
                                {' '}
                                <span
                                    style={{
                                        color: '#FAA61A'
                                    }}>
                                        <b>Any customizations you made will be lost.</b>
                                    </span>
                            </td>
                        </tr>
                        <tr>
                            <td>New Year Plan</td>
                            <td>
                                {`${state.data.name}`}</td>
                        </tr>
                        </tbody>
                    </table>
                    <table
                        style={{
                            width: '70%',
                            margin: '0 auto'
                        }}>
                        <tbody>
                        <tr>
                            <td
                                style={{
                                    textAlign: 'center'
                                }}>
                                <div
                                    className="btn button"
                                    style={{
                                        width: '100%'
                                    }}
                                    onClick={() => {
                                        data
                                            .modal
                                            .publish('pop-select', 'close')
                                    }}>NO, CANCEL
                                </div>
                            </td>
                            <td
                                style={{
                                    textAlign: 'center'
                                }}>
                                <div
                                    className="btn button"
                                    style={{
                                        width: '100%'
                                    }}
                                    onClick={() => {
                                        chgYearPlan('', state.data.url, '', state.data.name, ________isYearPlan________, ________currentYearPlanName________, state.data.is_show_meeting_lib);
                                        if (state.data.name === 'Custom Year Plan') {
                                            data
                                                .modal
                                                .publish('pop-select', 'close')
                                        }
                                    }}>YES, SELECT
                                </div>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>

        }

        let renderActions = () => (
            <div className="columns small-20 medium-12" style={{textAlign: 'center'}}>
                <button id="explore-close-preview" onClick={() => this.closePreview()} className="btn button btn-line hide-for-print">CLOSE PREVIEW</button>
                <button id="explore-select-track" className={`btn button btn-default hide-for-print ${(________currentYearPlanName________ === this.state.meeting.name) ? ' selected inactive' : ''}`} onClick={() => {
                    if (!(________currentYearPlanName________ === this.state.meeting.name)) {
                        selectPlan(this.state.track.split('###')[1], this.state.track.split('###')[0], this.store.bind(this))
                    }
                }}>
                    {(________currentYearPlanName________ === this.state.meeting.name) ? 'SELECTED' : 'SELECT TRACK'}
                </button>
                <div style={{margin:"0 0 -3% 0", display:"inline-block"}}><a onClick={() => window.print()} title="print"><i className="temp5 icon-printer hide-for-print"></i></a></div>
            </div>)


        function gradeLevelSelector(){
            if (data.isIRM() || data.isSUM() || data.getDefaultLevel() === 'multi-level'){
                console.log("selecting tab "+data.getLevel());
                $("li.grade-level").each(function( index ) {
                    if($(this).find('a').data('grade-level') == data.getLevel()){
                        $(this).addClass( "selected" );
                    }else{
                        $(this).removeClass( "selected" );
                    }
                });
                let _onClick: Function = (el) => {
                    var selectedGradeLevel = $(el).data('grade-level');
                    try{
                        $("#explore-close-preview").click();
                    }catch(error){
                    }
                    data.setLevel(selectedGradeLevel);
                    $('#vtk-yp-main').data('level', selectedGradeLevel);
                    data.getYearPlan().then((response) => {
                        ReactDOM.render(<VtkMainYp data={response}/>,
                            document.getElementById("vtk-yp-main")
                        );
                    })
                };
                return  <div className="__padding">
                    <div className="columns small-22 medium-20 small-centered medium-centered" style={{padding: '0px'}}>
                        <section className="grade-levels">
                            <p>Select a level to get started.</p>
                            <ul>
                                <li className="grade-level daisy">
                                    <a onClick={(e) => _onClick(e.currentTarget)} data-grade-level="daisy">Daisy</a>
                                </li>
                                <li className="grade-level brownie">
                                    <a onClick={(e) => _onClick(e.currentTarget)} data-grade-level="brownie">Brownie</a>
                                </li>
                                <li className="grade-level junior">
                                    <a onClick={(e) => _onClick(e.currentTarget)} data-grade-level="junior">Junior</a>
                                </li>
                                <li className="grade-level cadette">
                                    <a onClick={(e) => _onClick(e.currentTarget)} data-grade-level="cadette">Cadette</a>
                                </li>
                                <li className="grade-level senior">
                                    <a onClick={(e) => _onClick(e.currentTarget)} data-grade-level="senior">Senior</a>
                                </li>
                                <li className="grade-level ambassador">
                                    <a onClick={(e) => _onClick(e.currentTarget)} data-grade-level="ambassador">Ambassador</a>
                                </li>
                                <li className="grade-level multi-level">
                                    <a onClick={(e) => _onClick(e.currentTarget)} data-grade-level="multi-level">Multi-level</a>
                                </li>
                            </ul>
                            <div style={{clear: 'both'}}></div>
                        </section>
                    </div>
                </div>
            }else{
                return null;
            }
        }
        return (
            <div>
                {gradeLevelSelector()}
                <div className="__padding">
                    <div
                        className="columns small-22 medium-20 small-centered medium-centered"
                        style={{
                            padding: '0px'
                        }}>
                        <h2 className="">{title}</h2>
                        <div className="row">
                            <div className="small-24  columns">
                                <p
                                    style={{
                                        'marginBottom': '30px'
                                    }}>{subtitle}</p>
                            </div>
                        </div>
                    </div>
                </div>
                {(________app1________ && ________currentYearPlanName________ || ________currentYearPlanName________ === 'Custom Year Plan')
                    ? <div className="__padding">
                        <div className="columns small-20 small-centered">
                            <div className="_intro ">
                                <p style={{fontSize: '25px', fontWeight: 'bold'}}><span><i style={{
                                    color: 'orange',
                                    float: 'initial',
                                    display: 'inline-block',
                                    fontSize: '50px',
                                    marginBottom: '10px'
                                }} className="icon-check"></i></span>The Troop's Year Plan is set<br/>
                                    <span style={{fontSize: '18px'}}>{________currentYearPlanName________}</span>
                                </p>
                                <div className="exploreLargeView">
                                    <div style={{display: 'inline-flex'}}>
                                        <p style={{
                                            marginLeft: '12px',
                                            width: '166px'
                                        }}>To start over and erase all meeting details, including attendance and achievements:</p>
                                        <p style={{
                                            marginLeft: '200px',
                                            width: '166px'
                                        }}>To add, delete, or change a meeting, go to your current Year Plan</p>
                                    </div>
                                    <div>
                                        <a href="javascript:exploreReset();" className="btn button btn-default resetExploreButton" style={{
                                            marginLeft: '24px',
                                            maxWidth: '300px',
                                            border: '1px solid #00a850',
                                            color: '#00a850',
                                            backgroundColor: 'white'
                                        }}>Reset my year plan</a>
                                        <a href="/content/girlscouts-vtk/en/vtk.html" className="btn button btn-default selectedExploreButton" style={{
                                            marginLeft: '177px',
                                            maxWidth: '300px'
                                        }}>View my year plan</a>
                                    </div>
                                </div>
                                <div className="exploreSmallView">
                                    <div>
                                        <p>To start over and erase all meeting details, including attendance and achievements:</p>
                                        <a href="javascript:exploreReset();" className="btn button btn-default resetExploreButton" style={{
                                            marginLeft: '24px',
                                            maxWidth: '300px',
                                            border: '1px solid #00a850',
                                            color: '#00a850',
                                            backgroundColor: 'white'
                                        }}>Reset my year plan</a>
                                    </div>
                                    <div>
                                        <p style={{marginTop: '25px'}}>To add, delete, or change a meeting, go to your current Year Plan</p>
                                        <a href="/content/girlscouts-vtk/en/vtk.html" className="btn button btn-default selectedExploreButton" style={{
                                            marginLeft: '23px',
                                            maxWidth: '300px',
                                            paddingLeft: '24px',
                                            paddingRight: '24px'
                                        }}>View my year plan</a>
                                    </div>
                                </div>
                                <br/> <br/>
                            </div>
                        </div>
                    </div>
                    : null}
                {(!(________app1________ && ________currentYearPlanName________ || ________currentYearPlanName________ === 'Custom Year Plan'))
                    ? <div className="__padding">
                        <div
                            className={`_main_boxes columns small-22 medium-20 small-centered medium-centered hide-for-print ${(this.state.showTracks)
                                ? '__OPEN'
                                : '__CLOSE'}`}>
                            <div
                                onClick={() => this.clickHander()}
                                className="columns  medium-24 large-12 _box_wrap">
                                <div className="_box __library">
                                    <div className="__img"></div>
                                    <h3>Build Your Own</h3>
                                    <p>Search or filter to select the badges and awards that fit the style of your
                                        troop.</p>
                                    <a
                                        className="btn button"
                                        style={{
                                            width: '100%'
                                        }}>start adding Petals, Badges or Journeys</a>
                                </div>
                            </div>
                            <div
                                onClick={() => this.openTracks()}
                                className="columns medium-24 large-12 _box_wrap">
                                <div className="_box __tracks">
                                    <div className="__img "></div>
                                    <h3>Pre-selected Tracks</h3>
                                    <p>Not sure what to pick? These tracks get your troop Year Plan started and let
                                        you add choices as well.</p>
                                    <a
                                        className="btn button"
                                        style={{
                                            width: '100%'
                                        }}>view popular tracks</a>
                                </div>
                            </div>
                            <br/><br/>
                        </div>
                    </div>
                    : null}
                <div
                    className={`_back_box_tracks ${(this.state.showMeetingSearch)
                        ? '__OPEN'
                        : '__CLOSE'}`}>
                    { <div className="__list__tracks columns small-24">
                            <BYOHead/>
                            <div id="meetingSearch"></div>
                      </div>
                    }
                </div>
                <div
                    className={`_back_box_tracks ${(this.state.showTracks)
                        ? '__OPEN'
                        : '__CLOSE'}`}>
                    {(!this.state.showPreview)
                        ? <div className="__categories_main">
                            <Head/> {(this.state.showTracks)
                            ? <div className="__categories-wrap">
                                {this
                                    .props
                                    .data
                                    .Category
                                    .map((cat, idx, arr) => {
                                        return <div key={'category-' + idx}>
                                            <Category
                                                {...cat}
                                                store={this
                                                    .store
                                                    .bind(this)}
                                                idx={idx}
                                                showPreview={this
                                                    .showPreview
                                                    .bind(this)}/>
                                        </div>
                                    })
                                }
                                <div className="row" style={{clear: 'both'}}>
                                    <div className="columns small-20 small-centered">

                                        <div className={this.state.pdf ? `columns small-24` : 'columns small-24'} style={!this.state.pdf ? {textAlign: 'center'} : {textAlign: 'center'}}>
                                            <button onClick={() => this.openTracks()} className='btn button btn-line'>CLOSE</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            : null}
                        </div>

                        : <div className="__list__tracks columns small-24">
                            <Head/>
                            <div className="columns small-22 small-centered">
                                <div className="__meetings">
                                    <div className="columns medium-22 small-22 small-centered medium-centered">
                                        <p>
                                            <b>Pre-selected Year Plan Track</b>
                                        </p>
                                        <p>{this.state.meeting.desc}</p>
                                        <div style={{display: 'inline-block', width: '100%'}}>
                                            <div className="columns small-24 medium-12">
                                                <p style={{fontSize: '18px'}}><b>{this.state.meeting.name}</b></p>
                                            </div>
                                            {renderActions()}
                                        </div>
                                        <br/>
                                    </div>
                                    <div className="columns medium-22 small-24 medium-centered">
                                        <Meetings meetings={this.state.meeting.meetings}/>
                                        <br/>
                                    </div>
                                </div>
                                <div className="columns small-22 small-centered">
                                    <div className="columns small-24 medium-11 medium-offset-1">
                                        <p style={{padding: '0 30px 0 0'}}>If you select this track, the meetings you see here will automatically fill your Year Plan calendar. You can add, change or delete meetings at any time.</p>
                                    </div>
                                    {renderActions()}
                                </div>

                            </div>
                        </div>}
                </div>
                <Gray/>
                <div className="pop-explore">
                    <VtkPopUp name="pop-select" title="SELECT YEAR PLAN">
                        {renderChild(this.state)}
                    </VtkPopUp>
                </div>
                <div className="small-20 columns small-centered"><p>Want to explore more before setting up a plan? Check out the <a href="https://www.girlscouts.org/en/our-program/badges/badge_explorer.html">Award and Badge Explorer</a> to mix and match badge and Journey choices. Include your Girl Scouts and let them give input as you plan your year.</p></div>
            </div>
        )
    }
}

export default VtkMainYp;