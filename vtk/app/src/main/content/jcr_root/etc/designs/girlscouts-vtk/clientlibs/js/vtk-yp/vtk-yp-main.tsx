import './vtk-yp-main.scss';

import * as React from 'react';
import * as data from './data'

import { pdf, tree } from './tree'

import Category from './category';
import Gray from './vtk-gray';
import Header from './header';
import VtkPopUp from './vtk-popup';
import { selectPlan } from './year-plan-track';

interface VtkMainYpProps {
    data : any;
};

interface VtkMainYpState {
    pdf: string;
     data: {
        name: string,
        url: string,
        is_show_meeting_lib: boolean
    };
};

declare var doMeetingLib: any;
declare var ________app________: string;
declare var ________app1________: string;
declare var ________isYearPlan________: boolean;
declare var ________currentYearPlanName________: string;
declare var ________troopName________: string;
declare var    chgYearPlan: Function

class VtkMainYp extends React.Component < VtkMainYpProps,
    VtkMainYpState> {
    

    constructor() { 
        super();
        this.state = {
            pdf: '',
            data:{
                name: '',
                url: '',
                is_show_meeting_lib:true
            }
        }
    }


     store(state, func) { 
        this.setState({ data: state }, func)
    }

    reset() { 
        this.setState({
            data: {
                name: '',
                url: '',
                is_show_meeting_lib:true
            }
        })
    }    

    componentDidMount() {
        data.getPDF().then((url) => { 
            this.setState({pdf:url})
        })
    }    

    public render() : JSX.Element {

        const {header, bottom, customizedYearPlanContent} = this.props.data;
        const { title, subtitle } = header;



        function renderChild(state) { 
       
            return (________isYearPlan________ ==  false)
                ? <div className={state.data.name}>
                    <p>
                        <b>
                            You have selected the Year Plan below for {`${________app________}`}  {(________troopName________.match(/troop/i))?null:"Troop"}  {`${________troopName________}.`}  Is this correct?
                        </b>
                    </p>
                    
                    <table style={{ width: '70%' }}>
                        <tbody>
                            <tr>
                                <td>Troop Year Plan</td>
                                <td>{`${state.data.name}`}</td>
                            </tr>
                        </tbody>
                    </table>

                                        <table style={{width:'70%',margin:'0 auto'}}>
                        <tbody>
                        <tr>
                            <td style={{ textAlign: 'center' }}>
                                <div className="btn button" style={{width:'100%'}} onClick={() => { data.modal.publish('pop-select', 'close') }}>NO, CANCEL</div>
                            </td>
                            <td style={{ textAlign: 'center' }}>
                                    <div className="btn button" style={{width:'100%'}} onClick={() => {

                                        chgYearPlan('', state.data.url, '', state.data.name, ________isYearPlan________, ________currentYearPlanName________, state.data.is_show_meeting_lib)

                                        if (state.data.name === 'Custom Year Plan'){ 
                                            data.modal.publish('pop-select', 'close')
                                        }    
                                    
                                }}>YES, SELECT</div>
                            </td>
                                    
                            </tr>
                        </tbody>    
                    </table>
                    
                        
                </div>
                : <div className={state.data.name}>
                    <p>
                        <b>
                            You want to replace your current Year Plan with the new Year Plan listed below for {`${________app________}`}  {(________troopName________.match(/troop/i))?null:"Troop"}  {`${________troopName________}.`}  Is this correct?
                        </b>
                    </p>

                    <table >
                        <tbody>
                            <tr >
                                <td>Current Year Plan:</td>
                                <td>{`${________currentYearPlanName________}`}<br /> <b>IMPORTANT:</b> <span style={{color:'#FAA61A'}}><b>Any customizations you made will be lost.</b></span></td>
                            </tr>
                            <tr >
                                <td>New Year Plan</td>
                                <td> {`${state.data.name}`}</td>
                            </tr>
                        </tbody>
                    </table>

                    <table style={{width:'70%',margin:'0 auto'}}>
                        <tbody>
                        <tr>
                            <td style={{ textAlign: 'center' }}>
                                <div className="btn button" style={{width:'100%'}} onClick={() => { data.modal.publish('pop-select', 'close') }}>NO, CANCEL</div>
                            </td>
                            <td style={{ textAlign: 'center' }}>
                                    <div className="btn button" style={{width:'100%'}} onClick={() => {

                                        chgYearPlan('', state.data.url, '', state.data.name, ________isYearPlan________, ________currentYearPlanName________, state.data.is_show_meeting_lib)

                                     if (state.data.name === 'Custom Year Plan'){ 
                                            data.modal.publish('pop-select', 'close')
                                        }        
                                    
                                }}>YES, SELECT</div>
                            </td>
                                    
                            </tr>
                        </tbody>    
                    </table>

                </div>;


        }

        
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

                        return <div key={'category-'+idx}>
                            <Category  {...cat} store={this.store.bind(this)}/>

                           
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
                            <div className="columns small-17" style={{padding:'0px',marginLeft:'-5px'}}><p>{customizedYearPlanContent.title}</p></div>
                            <div onClick={()=>{
                                selectPlan('Custom Year Plan', '', this.store.bind(this)); 
                    

                         }} className="columns small-5 end vtk-yp-link" >{customizedYearPlanContent.linkText}</div>


                        </div>    
                    </div>
             </div>

                         <Gray /> 
             

            <VtkPopUp name="pop-select" title="SELECT YEAR PLAN">
                {renderChild(this.state)}
             </VtkPopUp>



             
         </div>
     
         
     
     )

    }
}

export default VtkMainYp;
