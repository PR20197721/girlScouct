import * as React from "react";
import Axios from 'axios';
import {connect} from "react-redux";
import Header from "../vtk-yp/header";
import {parseJSONVTK} from "../vtk-yp/data";


export interface VtkMtgPlanLocationProps {
    locationFind: any,
	meetingPath: string

}


export interface VtkMtgPlanLocationState {
    isEditOpen: boolean;
	locName: string;
	locAddress: string;
	isAddOpen: boolean;
}


const closeLocDetails = {
    fontsize: '18px',
	paddingright: '10px',
	fontweight: 'normal',
	color: 'black'
}


class VtkMtgPlanLocation extends React.Component <VtkMtgPlanLocationProps, VtkMtgPlanLocationState> {

    constructor() {
        super();
        this.state = {
            isEditOpen: false,
			locName: '',
			locAddress: '',
			isAddOpen: false,
			
        };
    }
	
	handleChange = event => {
		console.log("INSIDE HANDLE CHANGE");
    	this.setState({ 
			locName: event.target.value,
			locAddress: event.target.value		
		 });
	console.log(this.state.locName + "address:::" + this.state.locAddress);
  	}

    toggleAddEdit(action){
		if (action == "edit"){
			console.log("INSIDE EDIT");
	        this.setState({
	            isEditOpen: !this.state.isEditOpen
	        });
		}
		if (action == "add") {
			this.setState({
	            isAddOpen: !this.state.isAddOpen
	        });
		}
    }

    saveData(action){
	console.log("INSIDE SAVE DATA");
        let data = {
			locName: this.state.locName,
			locAddress: this.state.locAddress,
			meetingPath: this.props.meetingPath
		};
		
		console.log("name::"+ data.locName + "address::" + data.locAddress);
		if (action == "edit") {
			console.log("Inside edit savedata");
	        Axios.post('/content/girlscouts-vtk/service/react/action/update-meeting-location.edit.html', data).then((data) => {
	            //do something based on response from servlet.
	            console.log("res::"+data);
				this.toggleAddEdit("edit");
	            }
	        );
		}
		
		if (action == "add") {
			console.log("Inside add savedata");
	        Axios.post('/content/girlscouts-vtk/service/react/action/update-meeting-location.add.html', data).then((data) => {
	            //do something based on response from servlet.
	            console.log("res::"+data);
				this.toggleAddEdit("add");
	            }
	        );
		}
    }

    render() {
        return (
            <div>
            {
                (this.props.locationFind && this.props.locationFind.length)
                ? <span> LOCATION:{' '}{this.props.locationFind[0].name}{' '}
                    <a href={`/content/girlscouts-vtk/controllers/vtk.map.html?address=${this.props.locationFind[0].address}`}
                        target="_blank">{this.props.locationFind[0].address}</a>&nbsp;&nbsp;&nbsp;
					<a href="javascript:void(0)" onClick={() => this.toggleAddEdit("edit")}><i className="fa fa-pencil fa-fw"></i>Edit</a>                          
                </span>
                : <a href="javascript:void(0)" onClick={() => this.toggleAddEdit("add")}><i className="icon-button-circle-plus"></i>Add a meeting location</a>
            }
                {
                    this.state.isEditOpen ?
                        <div>
						   <div className="content clearfix row" id="panel2">
						    <div id="meetingLocationEdit" className="columns small-24">
								<hr/>
								<div style={{display: 'inline-flex'}}>
							        <p><b>Add, delete or edit the location for this meeting.</b></p>
								    <a href="javascript:void(0)" onClick={() => this.toggleAddEdit("edit")}><span style={closeLocDetails}>X</span></a>
    							</div>
						        <div id="err" className="errorMsg error"></div>
						        <form id="editLocationForm">
						            <input type="hidden" id="loc_city" value=""/>
						            <input type="hidden" id="loc_state" value=""/>
						            <input type="hidden" id="loc_zip" value=""/>
						            <section className="row">
						                <div className="column small-10">
						                    <input type="text" placeholder="Location Name" name="locName" onChange={this.handleChange}/>
						                </div>
						                <div className="column small-10">
						                    <input type="text" placeholder="Location Address" name="locAddress" onChange={this.handleChange}/>
						                </div>
						                <button className="btn right" onClick={() => this.saveData("edit")}>Save</button>
						            </section>
						        </form>
						    </div>
						   </div>
                        </div>
                        : null}
				{
					this.state.isAddOpen ?
						<div className="content clearfix row">
						    <div id="meetingLocationAdd" className="columns small-24">
						     	<div style={{display: 'inline-flex'}}>
							       <p><b>Add the location for this meeting.</b></p>
								    <a href="javascript:void(0)" onClick={() => this.toggleAddEdit("add")}><span style={closeLocDetails}>X</span></a>
    							</div>
						        <div id="err" className="errorMsg error"></div>
						        <form id="addLocationForm">
						            <input type="hidden" id="loc_city" value=""/>
						            <input type="hidden" id="loc_state" value=""/>
						            <input type="hidden" id="loc_zip" value=""/>
						            <section className="row">
						                <div className="column small-10">
						                    <input type="text" placeholder="Location Name" name="locName" onChange={this.handleChange}/>
						                </div>
						                <div className="column small-10">
						                    <input type="text" placeholder="Location Address" name="locAddress" onChange={this.handleChange}/>
						                </div>
						                <button className="btn right" onClick={() => this.saveData("add")}>Add</button>
						            </section>
						        </form>
						   </div>
                        </div>
						: null
				}
            </div>
        );
    }
}
export default VtkMtgPlanLocation;