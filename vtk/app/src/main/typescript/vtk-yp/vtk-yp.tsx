import * as React from "react"
import * as ReactDOM from "react-dom"
import * as data from './data'

import VtkMainYp from './vtk-yp-main';

window['startYPApp'] = function () {

    data.getYearPlan().then((response) => {
        ReactDOM.render(<VtkMainYp data={response}/>,
            document.getElementById("vtk-yp-main")
        );
    })
};

