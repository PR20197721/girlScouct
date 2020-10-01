import * as React from "react";
import Axios from 'axios';
import { connect } from "react-redux";
import Header from "../vtk-yp/header";
import { parseJSONVTK } from "../vtk-yp/data";
import './../../scss/vtk-mtg-plan/vtk-mtg-plan-location.scss';

export interface VtkMtgPlanLocationProps {
    locationFind: any,
    meetingPath: string,
    yearPlanPath: string
}

export interface VtkMtgPlanLocationState {
    isEditOpen: boolean;
    isAddOpen: boolean;
    locName: string;
    locAddress: string;
    displayLocationName: string;
    displayLocationAddress: string;
}

class VtkMtgPlanLocation extends React.Component<VtkMtgPlanLocationProps, VtkMtgPlanLocationState> {

    constructor(props: VtkMtgPlanLocationProps) {
        super(props);
        this.state = {
            isEditOpen: false,
            isAddOpen: false,
            locName: props && props.locationFind ? props.locationFind[0].name : '',
            locAddress: props && props.locationFind ? props.locationFind[0].address : '',
            displayLocationName: props && props.locationFind ? props.locationFind[0].name : null,
            displayLocationAddress: props && props.locationFind ? props.locationFind[0].address : null
        };
    }

    componentWillReceiveProps(props) {
        if (props.locationFind) {
            this.setState({
                displayLocationName: props.locationFind[0].name,
                displayLocationAddress: props.locationFind[0].address
            })
        }
        else {
            this.setState({
                displayLocationName: null,
                displayLocationAddress: null
            })
        }
    }

    handleNameChange = (event) => {
        this.setState({
            locName: event.target.value
        });
    }

    handleAddressChange = (event) => {
        this.setState({
            locAddress: event.target.value
        });
    }

    toggleAddEdit = (action) => {
        if (action === "edit") {
            this.setState({
                isEditOpen: !this.state.isEditOpen
            });
        }
        if (action === "add") {
            this.setState({
                isAddOpen: !this.state.isAddOpen
            });
        }
        if (action === "remove") {
            this.setState({
                isAddOpen: false,
                isEditOpen: false
            });
        }
    }

    saveData = (action) => {
        let data = {
            locName: this.state.locName,
            locAddress: this.state.locAddress,
            meetingPath: this.props.meetingPath,
            yearPlanPath: this.props.yearPlanPath
        };

        console.log("name::" + data.locName + "address::" + data.locAddress);
        if (action == "edit") {
            if (this.state.locName !== "" && this.state.locName !== null && this.state.locAddress !== "" && this.state.locAddress !== null) {
                Axios.post('/content/girlscouts-vtk/service/react/action/update-meeting-location.edit.html?locName=' + this.state.locName + '&locAddress=' + this.state.locAddress + '&meetingPath=' + this.props.meetingPath, data).then((data) => {
                    //do something based on response from servlet.
                    console.log("res::" + data);
                    this.toggleAddEdit("edit");
                    this.setState({ displayLocationName: this.state.locName, displayLocationAddress: this.state.locAddress });
                });
            }
        }

        if (action == "add") {
            if (this.state.locName !== "" && this.state.locName !== null && this.state.locAddress !== "" && this.state.locAddress !== null) {
                Axios.post('/content/girlscouts-vtk/service/react/action/update-meeting-location.add.html?yrPlanPath=' + this.props.yearPlanPath + '&locName=' + this.state.locName + '&locAddress=' + this.state.locAddress + '&meetingPath=' + this.props.meetingPath, data).then((response) => {
                    //do something based on response from servlet.
                    console.log("res::" + JSON.stringify(response));
                    console.log("res::" + response.data);
                    this.toggleAddEdit("add");
                    this.setState({ displayLocationName: this.state.locName, displayLocationAddress: this.state.locAddress });
                });
            }
        }

        if (action == "remove") {
            console.log("Inside remove savedata");
            let confirmRemove = confirm("Are you sure to remove the location?");
            if (confirmRemove) {
                Axios.post('/content/girlscouts-vtk/service/react/action/update-meeting-location.remove.html?meetingPath=' + this.props.meetingPath, data).then((response) => {
                    //do something based on response from servlet.
                    console.log("res::" + response);
                    console.log("res::" + response.data);
                    this.toggleAddEdit("remove");
                    this.setState({ displayLocationName: null, displayLocationAddress: null });
                });
            }
        }
    }

    render() {
        return (
            <div>
                {(this.state.displayLocationName && this.state.displayLocationAddress) ?
                    <span>LOCATION:
                        { /https?:\/\//.test(this.state.displayLocationAddress) ? // Is virtual location
                            <span>
                                {` `}
                                <a style={{ marginRight: '10px' }} target="_blank" href={this.state.displayLocationAddress}>
                                    {this.state.displayLocationName}
                                </a>
                            </span>
                            : <span>
                                {` ${this.state.displayLocationName} `}
                                <a style={{ marginRight: '10px' }} target="_blank" href={`http://maps.google.com/maps?q=${this.state.displayLocationAddress}`}>
                                    {this.state.displayLocationAddress}
                                </a>
                            </span>
                        }
                        <a href="javascript:void(0)" onClick={() => this.toggleAddEdit("edit")} style={{ marginRight: '10px' }}><i className="icon-pencil"></i></a>
                        <a href="javascript:void(0)" onClick={() => this.saveData("remove")}><i className="icon-button-circle-cross"></i></a>
                    </span>
                    : <a href="javascript:void(0)" onClick={() => this.toggleAddEdit("add")}><i className="icon-button-circle-plus"></i>Add a meeting location</a>
                }
                { this.state.isEditOpen &&
                    <div>
                        <div className="content clearfix row" id="panel2">
                            <div id="meetingLocationEdit" className="columns small-24">
                                <hr />
                                <div className="editOpen">
                                    <p><b>Add, delete or edit the location for this meeting.</b></p>
                                    <a href="javascript:void(0)" onClick={() => this.toggleAddEdit("edit")}><span className="close"><b>X</b></span></a>
                                </div>
                                <div id="err" className="errorMsg error"></div>
                                <div id="editLocationForm">
                                    <input type="hidden" id="loc_city" value="" />
                                    <input type="hidden" id="loc_state" value="" />
                                    <input type="hidden" id="loc_zip" value="" />
                                    <section className="row">
                                        <div className="column small-10">
                                            <input type="text" placeholder="Location Name" name="locName" value={this.state.locName} onChange={this.handleNameChange} />
                                        </div>
                                        <div className="column small-10">
                                            <input type="text" placeholder="Location Address" name="locAddress" value={this.state.locAddress} onChange={this.handleAddressChange} />
                                        </div>
                                        <button className="btn right" onClick={() => this.saveData("edit")}>Save</button>
                                    </section>
                                </div>
                            </div>
                        </div>
                    </div>
                }
                { this.state.isAddOpen &&
                    <div className="content clearfix row">
                        <div id="meetingLocationAdd" className="columns small-24">
                            <hr />
                            <div className="addOpen">
                                <p><b>Add the location for this meeting.</b></p>
                                <a href="javascript:void(0)" onClick={() => this.toggleAddEdit("add")}><span className="close"><b>X</b></span></a>
                            </div>
                            <div id="err" className="errorMsg error"></div>
                            <div id="addLocationForm">
                                <input type="hidden" id="loc_city" value="" />
                                <input type="hidden" id="loc_state" value="" />
                                <input type="hidden" id="loc_zip" value="" />
                                <section className="row">
                                    <div className="column small-10">
                                        <input type="text" placeholder="Location Name" name="locName" onChange={this.handleNameChange} />
                                    </div>
                                    <div className="column small-10">
                                        <input type="text" placeholder="Location Address" name="locAddress" onChange={this.handleAddressChange} />
                                    </div>
                                    <button className="btn right" onClick={() => this.saveData("add")}>Add</button>
                                </section>
                            </div>
                        </div>
                    </div>
                }
            </div>
        );
    }
}
export default VtkMtgPlanLocation;