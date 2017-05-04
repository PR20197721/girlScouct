import * as React from "react"
import * as ReactDOM from "react-dom"

import VtkMainYp from './vtk-yp-main';

window.onload = function () { 

    ReactDOM.render(
       <VtkMainYp />,
        document.getElementById("vtk-yp-main")
    );
}

