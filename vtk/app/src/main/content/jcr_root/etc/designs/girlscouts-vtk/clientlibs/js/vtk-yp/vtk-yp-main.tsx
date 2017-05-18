import './vtk-yp-main.scss';

import * as React from 'react';
import * as data from './data'

import { pdf, tree } from './tree'

import Category from './category';
import Header from './header';

interface VtkMainYpProps {
    data : any;
};

interface VtkMainYpState {
    pdf: string;
};

declare var doMeetingLib : any;

class VtkMainYp extends React.Component < VtkMainYpProps,
    VtkMainYpState> {
    

    constructor() { 
        super();
        this.state = {
            pdf:''
        }
    }


    componentDidMount() {
        data.getPDF().then((url) => { 
            this.setState({pdf:url})
        })
    }    

    public render() : JSX.Element {
        //debugger;
        const {header, bottom} = this.props.data;
        const { title, subtitle } = header;
        
     return (
            <div>
                <div>
                    <div className="columns small-20 small-centered" style={{padding:'0px'}}>
                        <h3 className="">{title}</h3>
                        <div className="row">
                            <div className="small-24 medium-18 columns">
                                <p>{subtitle}</p>
                            </div>
                            <div className="small-24 medium-6 columns" style={{textAlign:'right'}}>
                            {this.state.pdf ? <a target="_blank" href={this.state.pdf}> <img src={pdf} />  Year Plan Overview</a> : null}
                            </div>

                        </div>
                    </div>
                </div>

                {this
                    .props
                    .data
                    .Category
                    .map((cat, idx) => {
                        return <Category key={idx} {...cat}/>
                    })
                }

                <div className="columns small-24">
                    <Header subTitle={bottom.subtitle} title={bottom.title} />
                    
                    <div className="row">
                        <div className="columns small-20 small-centered">
                            <div className="columns small-12"><p>Customize - Mix and Match </p></div>
                            <div onClick={()=>{
                                doMeetingLib(true);    
                            }} className="columns small-12 vtk-yp-link" > View Meetings to Select</div>
                        </div>    
                    </div>
                </div>
            </div>)

    }
}

export default VtkMainYp;
