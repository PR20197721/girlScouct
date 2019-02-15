import * as React from 'react';
import './../../scss/vtk-mtg-plan/vtk-mtg-plan-loading.scss';

import { connect } from 'react-redux';

export interface VtkMtgPlanLoadingProps {
  loading:boolean
}

class VtkMtgPlanLoading extends React.Component<VtkMtgPlanLoadingProps, any> {
  render() {
    return (
     (this.props.loading)? <div className="vtk-mtg-plan-loading">
        <div className="spinner">
            <div className="dot1"></div>
            <div className="dot2"></div>
        </div>
      </div>:null
    );
  }
}


function mapStateToProps(state) {
  return {
      loading:state.loading
  };
}

export default connect(mapStateToProps)(VtkMtgPlanLoading);


