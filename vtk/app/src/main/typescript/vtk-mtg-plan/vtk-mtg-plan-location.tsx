import * as React from "react";
import Axios from 'axios';
import {connect} from "react-redux";
import Header from "../vtk-yp/header";
import {parseJSONVTK} from "../vtk-yp/data";


export interface VtkMtgPlanLocationProps {
    locationFind: any
}


export interface VtkMtgPlanLocationState {
    isEditOpen: boolean;
}



class VtkMtgPlanLocation extends React.Component <VtkMtgPlanLocationProps, VtkMtgPlanLocationState> {

    constructor() {
        super();
        this.state = {
            isEditOpen: false
        };
    }

    toggleEdit(){
        this.setState({
            isEditOpen: !this.state.isEditOpen
        })
    }

    saveData(){
        let data = '.......';
        Axios.post('path to your servlet', data).then((data) => {
            //do something based on response from servlet.
            this.toggleEdit();
            }
        );
    }

    render() {
        return (
            <div>
            {
                (this.props.locationFind && this.props.locationFind.length)
                ? <span> LOCATION:{' '}{this.props.locationFind[0].name}{' '}
                    <a href={`/content/girlscouts-vtk/controllers/vtk.map.html?address=${this.props.locationFind[0].address}`}
                        target="_blank">{this.props.locationFind[0].address}</a>
                                <br/><br/><i className="far fa-edit" onClick={ event => this.toggleEdit()}> </i>
                </span>
                : null
            }
                {
                    this.state.isEditOpen ?
                        <div>
                                    FORM to EDIT GOES here
                            <a onClick={this.saveData}>save</a>
                        </div>
                        : null}
            </div>
        );
    }
}
export default VtkMtgPlanLocation;