import './vtk-yp-main.scss';

import * as React from 'react';

import Category from './category';

interface VtkMainYpProps {};

interface VtkMainYpState {};

class VtkMainYp extends React.Component<VtkMainYpProps, VtkMainYpState> {
    public render(): JSX.Element {
        return (<div >
            <div className="columns small-20 small-centered">
                <h3 className="">Daisy Year Plan Libray for 2017-2018</h3>
                <div className="row">
                    <div className="small-24 medium-20 columns">
                        <p>Each Year plan  topic offers a list of up to 15 pre-selected meetings</p>
                    </div>
                    <div className="small-24 medium-4 columns">
                        pdf
                    </div>
                    
                </div>    
            </div>    
            <Category />
        </div>);
    }
}

export default VtkMainYp;

