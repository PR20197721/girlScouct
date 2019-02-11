import './vtk-popup.scss'

import * as React from "react"

import { modal } from "./data"

//import VtkIcon from './vtk-icon'
//import {store} from '../../store/store'

interface IVtkPopUpProps {
    title : string,
  //  modals : any,
    name: string
};

interface IVtkPopUpState {
    modal?: {
        width: number,
        height: number
    },
    screen?: {
        width: number,
        height: number
    },
    top?: number,
    left?: number,
    maxHeight?: number | string,
    width?: number | string,
    visible?:boolean
};

class VtkPopUp extends React.Component < IVtkPopUpProps,IVtkPopUpState > {

    state : any;
    originalHeight : number;
    setAttributes: any;
    modal:any;


    constructor() {
        super();

        this.state = {
            top: 0,
            left: 0,
            maxHeight: 500,
            width: 600,
            visible:false
        }
    }

    setDimentions() {
        const element = document.getElementById(this.props.name);
        if (element) {
            this.setState({
                screen: {
                    height: document.documentElement.clientHeight,
                    width: document.documentElement.clientWidth
                },
                modal: {
                    height: (element) ? element.clientHeight : 0,
                    width: (element) ? element.clientWidth : 0
                },
                top: (document.documentElement.clientHeight < 200)
                    ? 0
                    : ((window.innerHeight / 2) - (element.offsetHeight / 2)),
                left: (document.documentElement.clientWidth < 600)
                    ? 0
                    : ((window.innerWidth / 2) - (element.offsetWidth / 2)),
       
                width: (document.documentElement.clientWidth <600)
                    ? window.innerWidth
                    : 600,
            })
        }

    }


    componentWillMount() {
        this.modal = modal.subscribe(this.props.name, this.openclose.bind(this))
        this.setDimentions();
    }


    componentDidMount() {
        window.addEventListener('resize', (e : Event) => {
            this.setDimentions();
        });

        window.addEventListener('load', (e : Event) => {
            this.setDimentions();
            const element = document.getElementById(this.props.name);
            if (element) {
                this.originalHeight = element.offsetHeight;
            }
        });
    }

    componentWillUnmount() {
        this.modal.remove();
        window.removeEventListener('resize',()=>{});
    }

    hide() {

        modal.publish('gray', 'close');
        this.setState({ 'visible': false });
  
    }


    open() {
                     modal.publish('gray', 'open');
        this.setState({ 'visible': true });

    }

    openclose(s) { 


        if (s == "close") { 

            this.hide();
        }
        if (s == "open") { 
 
            this.setDimentions();
            this.open();
        }

    }

    public render(): JSX.Element {

        let { title, children, name } = this.props;

        let events: {} = {};
        // let visible: string = 'vtk-popup';

        events = {
            onClick: (e: any) => {
                this.hide()
            },

            style: {
                "color": "white",
                "position": "absolute",
                "top": "5px",
                "right": '5px'
            }
        }



        const childrenWithProps = React.Children.map(this.props.children,
        (child:any) => React.cloneElement(child))
  

        return (
            
      
                <div
                    id={name}
                    className={this.state.visible ? 'vtk-popup' : 'vtk-popup ___hide'}
                    style={{

                        left: this.state.left,
                        top: this.state.top,
                        width: this.state.width

                    }}>
                    <div className="___header">
                        <div>{title}</div> <div onClick={() => modal.publish(this.props.name,'close')} style={{ position: 'absolute', top: '5px', right: '10px' }}><i className="icon-button-circle-cross"></i></div>
                    </div>
                    <div
                        className="___content"
                    >
                        <div className="row">
                        
                        {childrenWithProps}
                        </div>
                    </div>
                </div> )
        ;
    }
}

export default VtkPopUp;
