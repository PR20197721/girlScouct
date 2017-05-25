import * as React from "react";

import { modal } from "./data";

interface IGrayProps {};

interface IGrayState {
    height?: number | string,
    showGray?:boolean
};

class Gray extends React.Component<IGrayProps, IGrayState> {

    state: any;
    _modal: any;
    constructor() { 
        super();

        this.state = {
            height:'auto',
            showGray: false
        }
    }

    setDimension() { 

        this.setState({
            height: document.documentElement.clientHeight
        });
    }




    componentWillMount() { 
          window.addEventListener('resize', () => { 
            this.setDimension();
        })
         this._modal = modal.subscribe('gray', this.toggle.bind(this));    
        this.setDimension();
    }

    componentWillUnmount() {
        window.removeEventListener('resize');
        this._modal.remove();

    }


    hide() { 
        this.setState({ showGray: false });     
         document.body.style.overflowY = 'auto';
    }


    show() { 
        this.setDimension();

         document.body.style.overflowY = 'hidden';
         this.setState({ showGray: true });
    }
    

    toggle(action) { 

        if (action=='open'){ 
            this.show();
        }
        if (action=='close'){ 
            this.hide();
        }
    }



    public render(): JSX.Element {
        return (<div
            className={(!this.state.showGray) ? "vtk-yp-gray __hidden" : "vtk-yp-gray"}
            style={{
                height: this.state.height
            }} >
            </div>);
    }
}
export default Gray;

