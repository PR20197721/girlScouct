import * as React from "react";
import * as ReactDOM from "react-dom";

import VtkMtgPlanMain from './vtk-mtg-main';
import { Provider } from 'react-redux';
import { Actions } from './store/actions';

import store from './store/store';


window['startMtgPlanApp'] = function (URL) {

    store.dispatch((dispatch) => {
        dispatch(Actions.FETCH(URL));
    });

    ReactDOM.render(
        <Provider store={store}>
            <VtkMtgPlanMain />
        </Provider>,
        document.getElementById('vtk-mtg-plan')
    );
}
