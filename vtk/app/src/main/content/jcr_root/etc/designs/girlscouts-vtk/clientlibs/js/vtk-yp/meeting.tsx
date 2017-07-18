import './meeting.scss';

import * as React from 'react';

import { tree } from './tree';

interface MeetingProps {
      outdoor: boolean,
                id: string,
                cat: string,
                name: string,
                blurb: string,
                level: string,
                position: number,
                idx:number
                activities:any[] | null;
};

interface MeetingState {};

class Meeting extends React.Component<MeetingProps, MeetingState> {
    

    public render(): JSX.Element {

        return (<div className="meeting">
            <div className="square">
                <p>Meeting</p>
                <p className='postion'>{this.props.idx+1}</p>
            </div>
            <div className="arrowGreen"></div>
            <div className="body">

                <div className="small-24 column">
                    <div className={(this.props.activities && this.props.activities.length > 0) ? "_text small-24  medium-18 column" : "_text small-24  medium-21 column"} style={{ fontSize: '14px' }}>
                        <div className="truncate">{this.props.name.toUpperCase()}</div>
                        <div className="truncate">{this.props.cat}</div>
                        <div className="truncate">{this.props.blurb}</div>
                    </div>


                    {(this.props.activities && this.props.activities.length > 0) ?
                        <div className="small-24 medium-3 column" style={{ textAlign: "center" }}>
                            {(this.props.activities && this.props.activities.length > 0) ? <img src={tree} style={{ 'width': '60px', 'height': '60px' }} alt="" /> : null}
                        </div> : null}
                    
                    
                    <div className="small-24 medium-3 column" style={{ textAlign: 'center' }}>
                        <img src={"/content/dam/girlscouts-vtk/local/icon/meetings/" + this.props.id + ".png"} style={{ 'width': '60px', 'height': '60px' }} alt="" /> 
                    </div>
                         
                </div>




            </div>         
        </div>);
    }
}

export default Meeting;
