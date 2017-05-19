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
            console.log(this.props)
        return (<div className="meeting">
            <div className="square">
                <p>Meeting</p>
                <p className='postion'>{this.props.idx+1}</p>
            </div>
            <div className="arrowGreen"></div>
            <div className="body">
                <ul>
                    <li style={{fontSize:'14px'}}>
                         <div>{this.props.name.toUpperCase()}</div>  <div>{this.props.cat}</div>  <div>{this.props.blurb}</div>
                    </li>
                    <li style={{textAlign:"center"}}>
                       {(this.props.activities && this.props.activities.length > 0)? <img src={tree} style={{'width':'60px','height':'60px'}} alt="" />:null}
                    
                    </li>
                     <li style={{textAlign:"right"}}>
                            <img src={"/content/dam/girlscouts-vtk/local/icon/meetings/"+this.props.id+".png"} style={{'width':'60px','height':'60px'}} alt=""/> </li>
                         
                </ul>
            </div>         
        </div>);
    }
}

export default Meeting;
