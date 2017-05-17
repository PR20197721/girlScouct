import * as React from 'react';

import { tree } from './tree';

interface MeetingProps {
      outdoor: boolean,
                id: string,
                cat: string,
                name: string,
                blurb: string,
                level: string,
                position: number
};

interface MeetingState {};

class Meeting extends React.Component<MeetingProps, MeetingState> {
    

    public render(): JSX.Element {
            console.log(this.props)
        return (<div className="meeting">
            <div className="square">
                <p>Meeting</p>
                <p className='postion'>{this.props.position}</p>
            </div>
            <div className="arrowGreen"></div>
            <div className="body">
                <ul>
                    <li>
                         {this.props.outdoor}<br /> {this.props.name}
                    </li>
                    <li>
                        <img src="" alt=""/>
                    </li>
                     <li>
                            {(this.props.outdoor)? <img src={tree} style={{'width':'60px','height':'60px'}} alt="" />:null}
                    </li>
                         
                </ul>
            </div>         
        </div>);
    }
}

export default Meeting;
