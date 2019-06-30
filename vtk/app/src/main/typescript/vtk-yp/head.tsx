import * as React from 'react';
import * as data from './data'

import '../../scss/vtk-yp/head.scss';
import * as ReactDOM from "react-dom";
import VtkMainYp from "./vtk-yp-main";


export interface HeadProps {

}

declare var ________app________;

export default class Head extends React.Component<HeadProps, any> {

    render() {
        if (data.isIRM() || data.isSUM()){
            console.log("selecting tab "+data.getLevel());
            $("li.grade-level").each(function( index ) {
                if($(this).find('a').data('grade-level') == data.getLevel()){
                    $(this).addClass( "selected" );
                }else{
                    $(this).removeClass( "selected" );
                }
            });
            let _onClick: Function = (el) => {
                var selectedGradeLevel = $(el).data('grade-level');
                try{
                    $("#explore-close-preview").click();
                }catch(error){
                }
                data.setLevel(selectedGradeLevel);
                $('#vtk-yp-main').data('level', selectedGradeLevel);
                data.getYearPlan().then((response) => {
                    ReactDOM.render(<VtkMainYp data={response}/>,
                        document.getElementById("vtk-yp-main")
                    );
                })
            };
            return (
                <div>
                    <div className="columns small-24 small-centered">
                        <div className="columns large-offset-16 medium-offset-11 small-offset-9  large-2 medium-2 small-3 end" style={{textAlign: 'center'}}>
                            <span className="_triangle"></span></div>
                    </div>
                    <div className="columns small-20 small-centered">
                        <div style={{padding: '10px 0 0px 0', clear: 'both'}}>
                            <section className="grade-levels">
                                <p style={{marginBottom: '30px'}}>Select a level to get started.</p>
                                <ul>
                                    <li className="grade-level daisy">
                                        <a onClick={(e) => _onClick(e.currentTarget)} data-grade-level="daisy">Daisy</a>
                                    </li>
                                    <li className="grade-level brownie">
                                        <a onClick={(e) => _onClick(e.currentTarget)} data-grade-level="brownie">Brownie</a>
                                    </li>
                                    <li className="grade-level junior">
                                        <a onClick={(e) => _onClick(e.currentTarget)} data-grade-level="junior">Junior</a>
                                    </li>
                                    <li className="grade-level cadette">
                                        <a onClick={(e) => _onClick(e.currentTarget)} data-grade-level="cadette">Cadette</a>
                                    </li>
                                    <li className="grade-level senior">
                                        <a onClick={(e) => _onClick(e.currentTarget)} data-grade-level="senior">Senior</a>
                                    </li>
                                    <li className="grade-level ambassador">
                                        <a onClick={(e) => _onClick(e.currentTarget)} data-grade-level="ambassador">Ambassador</a>
                                    </li>
                                    <li className="grade-level multi-level">
                                        <a onClick={(e) => _onClick(e.currentTarget)} data-grade-level="multi-level">Multi-level</a>
                                    </li>
                                </ul>
                                <div style={{clear: 'both'}}></div>
                            </section>
                        </div>
                    </div>
                    <div className="__level-logo columns small-22 small-centered">
                        <div className="small-8 small-offset-16 medium-4 medium-offset-18 large-6 large-offset-18 end">
                            <div style={{padding: '10px 0 0px 0', clear: 'both'}}></div>
                            <img className={`bg-logo logo-${________app________}`} src={`/etc/designs/girlscouts-vtk/clientlibs/css/images/GS_${________app________}.png`} style={{
                                width: 'auto',
                                height: '43px',
                                float: 'right'
                            }}/>
                            {/* <div style={{ padding: '0px 0 20px 0', clear:'both' }}></div> */}
                        </div>
                    </div>
                </div>

            )
        }else{
            return (
                <div>
                    <div className="columns small-24 small-centered">
                        <div className="columns large-offset-16 medium-offset-11 small-offset-9  large-2 medium-2 small-3 end" style={{textAlign: 'center'}}>
                            <span className="_triangle"></span></div>
                    </div>
                    <div className="__level-logo columns small-22 small-centered">
                        <div className="small-8 small-offset-16 medium-4 medium-offset-18 large-6 large-offset-18 end">
                            <div style={{padding: '10px 0 0px 0', clear: 'both'}}></div>
                            <img className={`bg-logo logo-${________app________}`} src={`/etc/designs/girlscouts-vtk/clientlibs/css/images/GS_${________app________}.png`} style={{
                                width: 'auto',
                                height: '43px',
                                float: 'right'
                            }}/>
                            {/* <div style={{ padding: '0px 0 20px 0', clear:'both' }}></div> */}
                        </div>
                    </div>
                </div>

            )
        }
    }
}
