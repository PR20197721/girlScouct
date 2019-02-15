import * as React from 'react';
import VtkContent from './common/content';

import './../../scss/vtk-mtg-plan/vtk-mtg-plan-main-bar.scss'
import {connect} from 'react-redux';

// import * as moment from 'moment';
import * as moment from 'moment-timezone';

import {HELPER} from './helper';
import {DATE_MORE_THAN_1977} from './permission';

export interface VtkMtgPlanMainBarProps {
  helper : any,
  meetingInfo : any,
  meeting : any,
  meetingEvents : any
}


export interface VtkMtgPlanMainBarState {
  isOpen:boolean;
}

// export default class VtkMtgPlanMainBar extends
// React.Component<VtkMtgPlanMainBarProps, VtkMtgPlanMainBarState> {
class VtkMtgPlanMainBar extends React.Component < VtkMtgPlanMainBarProps,
any > {

  constructor(){
    super();
    this.state = {isOpen:false};
  }


  toggle(){
    this.setState({isOpen:!this.state.isOpen});
  }

  render() {
    let {meetingInfo, helper, meeting, meetingEvents} = this.props;
    let currentDate = moment(helper.currentDate).tz('America/New_York').format(HELPER.FORMAT.TIME_STD);
    let {locations} = meeting;
    let locationFind;
    if (locations) {

      if (locations.length) {
        locationFind = locations.filter((loc) => loc.path == meetingEvents.locationRef);
      }
    }

    return (
      <VtkContent idName="main-bar">
        <div id="main-bar">
          {(helper.prevDate)
            ? <div className="__previous __bar">
                <p>
                  <a
                    href={`/content/girlscouts-vtk/en/vtk.details.html?elem=${helper.prevDate}`}
                    data-time={helper.prevDate}></a>
                </p>
              </div>
            : null}
          <div className="__main __bar">
            <p>Meeting{' '}:{' '}{meetingInfo.name}</p>
            {(meeting.schedule && DATE_MORE_THAN_1977(helper.currentDate))
              ? <p className="__time">{currentDate}</p>
              : null}
          </div>
          {(helper.nextDate)
            ? <div className="__next __bar">
                <p>
                  <a
                    href={`/content/girlscouts-vtk/en/vtk.details.html?elem=${helper.nextDate}`}
                    data-time={helper.nextDate}></a>
                </p>
              </div>
            : null}
        </div>

        <div id="main-info">

          <p>
            {meetingInfo.meetingInfo['meeting short description'].str}
            <br/>
            <br/> CATEGORY: {' '}{meetingInfo.cat}<br/><br/> 

            {(locationFind && locationFind.length)
              ? <span>
                  LOCATION:{' '}{locationFind[0].name}{' '}
                  <a 
                    href={`/content/girlscouts-vtk/controllers/vtk.map.html?address=${locationFind[0].address}`}
                    target="_blank">{locationFind[0].address}</a>
                  <br/><br/></span>
              : null}
            
            {(meetingInfo.reqTitle)
              ? <div className="row">
                      <div className="columns small-24 medium-4" style={{fontSize:'14px'}}>
                        REQUIREMENTS:
                      </div>
                      <div className="columns small-24 medium-20">
                        <div className={(this.state.isOpen)?"__mid_show":"__mid_show __hidden"} 
                              style={{paddingLeft:'5px'}}
                            dangerouslySetInnerHTML={{
                            __html: meetingInfo.req + `<div class="__more_text"></div>`
                          }}></div>  {(this.state.isOpen)
                          ? <p  onClick={()=>this.toggle()} style={{textAlign:"center",color:'green',cursor:"pointer"}}> less <span className="arrow-up"></span> </p>
                          : <p onClick={()=>this.toggle()} style={{color:'green',padding:'5px 0 0 5px',cursor:"pointer",textAlign:'left'}}>see more</p>
                        }


                        
                      </div>
                  
                </div>
              : null
}
            
          </p>
          <br/>

          <div className="__img">
            <img
              src={HELPER
              .PATH
              .URL_MAKER("MTG_IMAGES", meetingInfo.id + '.png')}
              title={meetingInfo.name}
              alt="Badget-icon"/> {(meetingInfo.reqTitle)
              ? <span>{meetingInfo.reqTitle}</span>
              : null}
          </div>
        </div>

      </VtkContent>
    );
  }
}

function mapStateToProps(state) {
  return {helper: state.helper, meetingInfo: state.meetingEvents.meetingInfo, meeting: state.meeting, meetingEvents: state.meetingEvents};
}

export default connect(mapStateToProps)(VtkMtgPlanMainBar);
