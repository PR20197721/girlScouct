import './vtk-yp-main.scss';

import * as React from 'react';
import * as data from './data'

import { pdf, tree } from './tree'

import Category from './category';
import Header from './header';
import {selectPlan} from './year-plan-track';

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

        const {header, bottom, bottomContent} = this.props.data;
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
                    .map((cat, idx, arr) => {
                        return <div>
                            <Category key={idx} {...cat} />
                           
                        </div>
                    })
                }
               
                 
                <div className="columns small-24">
                    {(this
                    .props
                    .data
                    .Category.length)?<div style={{ marginBottom: '50px'}}></div>:null}
                    <Header subTitle={bottom.subtitle} title={bottom.title} />
                    
                    <div className="row">
                        <div className="columns small-20 small-centered">
                            <div className="columns small-10" style={{padding:'0px',marginLeft:'-5px'}}><p>{bottomContent.title}</p></div>
                            <div onClick={()=>{

                                selectPlan('Custom Year Plan','');    
                             }} className="columns small-10 end vtk-yp-link" > {bottomContent.linkText}</div>

                        </div>    
                    </div>
                </div>
            </div>)

    }
}

export default VtkMainYp;
