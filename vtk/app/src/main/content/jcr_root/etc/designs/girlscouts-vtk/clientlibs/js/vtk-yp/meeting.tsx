import * as React from 'react';

interface MeetingProps {
      outdoor: string,
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
                            <img src="" alt="" />
                    </li>
                         
                </ul>
            </div>         
        </div>);
    }
}

export default Meeting;
