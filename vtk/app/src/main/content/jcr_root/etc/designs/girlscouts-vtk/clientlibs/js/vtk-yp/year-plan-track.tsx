import * as React from 'react';


import {getMeetings, modal, urlPath} from './data';

import Category from './category';
import Meeting from './meeting';
import Meetings from './meetings';

import './year-plan-track.scss';

interface YplanTrackProps {
    track : string;
    isnew: string;
    last: boolean;
    store: any;
    first: boolean;
    showPreview:Function;
};

interface YplanTrackState {
    isOpen?: boolean;
    meetings?: {
        desc: string;
        meetings: any[];
        name: string;
    };
    imgSrc:string;
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
            },
            imgSrc:''
        }
    }

    openPreview() {
        let {track,last,first,isnew} = this.props;
        this.props.showPreview({track,last,first,isnew}) 
    }

    imgErrorHandler(e:React.SyntheticEvent<HTMLImageElement>){
        this.setState({imgSrc:`${urlPath}${'placeholder'}.png`})
    }

    componentWillMount(){
        const [_,content,path,template,yearPlan,level, track] = this.props.track.split('###')[0].split('/');
        this.setState({'imgSrc':`${urlPath}${level.toLowerCase()}_${track.toLowerCase()}.png`});
    }






    componentDidMount() {
      const el:any = this.refs.one;
        var wordArray = el.innerHTML.split(' ');
        while(el.scrollHeight > el.offsetHeight) {
            wordArray.pop();
            el.innerHTML = wordArray.join(' ') + '...';
        }
    }


    public render(): JSX.Element {
    
        return (<div className={`_year-plan-box ${!(________app1________!==this.props.track.split('###')[0])?' __selected':''}`}>
                    <div className="__top">
                       <p ref="one" style={(!(________app1________!==this.props.track.split('###')[0]))?{fontWeight:'bold', fontSize:'18px', lineHeight:'20px',height:'46px',overflow:'hidden',margin:'0px'}:{ height:'46px',overflow:'hidden',margin:'0px'}}>
                            {this
                            .props
                            .track
                            .split('###')[1]
                            } {" "}
                            <span style={{ color: '#FAA61A', fontWeight: 'bold' }}>
                                {(this.props.isnew == 'isnew') ? ' NEW' : null}
                            </span>
                        </p>

                       

                    </div>
                    
                    <img src={this.state.imgSrc} onError={(e)=>this.imgErrorHandler(e)} style={{}} />
                        

                    <div>
                        <a onClick={()=>this.openPreview()} className={`btn button ${(________app1________!==this.props.track.split('###')[0])?'':'selected'}`} style={{width:'100%'}}>{`${!(________app1________!==this.props.track.split('###')[0])?'SELECTED - ':' '}`}PREVIEW</a>
                    </div>
                </div>)
    }
}

export default YplanTrack;

export function selectPlan(name: string, url: string, store?: Function) {

       store({
            name: name,
            url:url,
            is_show_meeting_lib: 
            (url != '' || ________app________ == 'senior' || ________app________ == 'ambassador' || ________app________ == 'cadette')
                ? false : true
        }, function () { 
           modal.publish('pop-select', 'open') 
        });
}