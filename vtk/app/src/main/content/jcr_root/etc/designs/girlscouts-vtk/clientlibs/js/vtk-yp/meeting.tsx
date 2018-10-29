import './meeting.scss';

import * as React from 'react';

import { tree } from './tree';

interface MeetingProps {
   meetingInfo: MeetingInfo;
   idx:number;
   anyOutdoorActivityInMeetingAvailable:boolean;
   anyOutdoorActivityInMeeting:boolean;

};

interface MeetingInfo {
    outdoor: boolean,
    id: string,
    cat: string,
    name: string,
    blurb: string,
    level: string,
    position: number,
    req:string,
    reqTitle:string,

    activities:any[] | null;
}

declare var requirementsModal;
interface MeetingState {};

class Meeting extends React.Component<MeetingProps, MeetingState> {
    
    public render(): JSX.Element {
        let showClickRequiment='';
        let _onClick:Function = ()=>{};

        if(this.props.meetingInfo.req){
            showClickRequiment="_requirement_modal";
            _onClick = (e:Event) => {

                requirementsModal({reqTitle: this.props.meetingInfo.reqTitle,id:this.props.meetingInfo.id,name:this.props.meetingInfo.name,req:this.props.meetingInfo.req},this.props.anyOutdoorActivityInMeetingAvailable, this.props.anyOutdoorActivityInMeeting)
            }
        }

        return (<div className="meeting">  
                <div className="square-small show-for-small-only">
                    <p>Meeting</p>
                    <p className='postion'>{this.props.idx+1}</p>
                </div>
                <div className="arrowGreen-small show-for-small-only"></div>


                <div className="square show-for-medium-up">
                    <p>Meeting</p>
                    <p className='postion'>{this.props.idx+1}</p>
                </div>
                <div className="arrowGreen show-for-medium-up"></div>

           

            <div className="body">
                <div className="small-24 column">
                    <div className={(this.props.meetingInfo.activities && this.props.meetingInfo.activities.length > 0) ? "_text small-text-center medium-text-left small-24  medium-18 column" : "_text small-text-center medium-text-left small-24  medium-21 column"} style={{ fontSize: '14px' }}>
                        <div className="truncate">{this.props.meetingInfo.name.toUpperCase()}</div>
                        <div className="truncate">{this.props.meetingInfo.cat}</div>
                        <div className="truncate">{this.props.meetingInfo.blurb}</div>
                    </div>


                    {(this.props.meetingInfo.activities && this.props.meetingInfo.activities.length > 0) ?
                        <div className="small-24 medium-3 column small-text-center" style={{ textAlign: "center" }}>
                            {(this.props.anyOutdoorActivityInMeetingAvailable) ? <img src={tree} style={{ 'width': '60px', 'height': '60px' }} alt="" /> : null}
                        </div> : null}
                    
                    
                    <div className="small-24 medium-3 column small-text-center" style={{ textAlign: 'center' }}>
                        <img onClick={(e)=>_onClick(e)} className={showClickRequiment} src={"/content/dam/girlscouts-vtk/local/icon/meetings/" + this.props.meetingInfo.id + ".png"}  style={{ 'width': '60px', 'height': '60px' }} alt="" /> 
                    </div>
                         
                </div>
            </div>         
        </div>);
    }
}

export default Meeting;
