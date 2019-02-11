import * as React from 'react';

export interface VtkContentProps {
    idName?: String
}

export default class VtkContent extends React.PureComponent<VtkContentProps, any> {
  render() {

    let {idName} = this.props;

    return (
      <div id={(idName)?'vtk-mtg-plan-'+idName:''} className="row"> 
        <div
          className="columns small-24" 
          style={{'marginBottom':'20px'}}>
          {this.props.children}
        </div>
      </div>
    );
  }
}
