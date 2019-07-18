import * as React from 'react';

import { connect } from 'react-redux';

export interface VtkMtgPlanLoadingProps {
  loading:boolean
}

class VtkMtgPlanLoading extends React.Component<VtkMtgPlanLoadingProps, any> {
  render() {
    return (
     (this.props.loading)? <div className="vtk-mtg-plan-loading">
        <div className="wrap__loader vtk-mtg-loader" >
                    <div className="loader__circle loader__circle--1"></div>
                    <div className="loader__circle loader__circle--2"></div>
                    <div className="loader__circle loader__circle--3"></div>
                    <div className="loader__circle loader__circle--4"></div>
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


