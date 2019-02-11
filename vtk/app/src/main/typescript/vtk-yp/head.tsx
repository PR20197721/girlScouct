
import * as React from 'react';
import './head.scss';

export interface HeadProps {
}



declare var  ________app________;

export default class Head extends React.Component<HeadProps, any> {
  render() {
    return (
    <div>
            <div className="columns small-24 small-centered">
                <div className="columns large-offset-16 medium-offset-11 small-offset-9  large-2 medium-2 small-3 end" style={{ textAlign: 'center' }} ><span className="_triangle"></span></div>
                </div>
                <div className="__level-logo columns small-22 small-centered">
                    <div className="small-8 small-offset-16 medium-4 medium-offset-18 large-6 large-offset-18 end">
                        <div style={{ padding: '10px 0 0px 0', clear:'both'}}></div>
                        <img className={`bg-logo logo-${________app________}`} src={`/etc/designs/girlscouts-vtk/clientlibs/css/images/GS_${________app________}.png`} style={{width:'auto',height:'43px', float:'right'}} />
                        {/* <div style={{ padding: '0px 0 20px 0', clear:'both' }}></div> */}
                    </div>
                </div>
        </div>

    )}
}
