import './vtk-popup.scss'

//import * as ACTION from '../../store/actions'
import * as React from "react"

//import VtkIcon from './vtk-icon'
//import {store} from '../../store/store'

interface IVtkPopUpProps {
    title : string,
  //  modals : any,
    name : string
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
    width?: number | string
};

class VtkPopUp extends React.Component < IVtkPopUpProps,IVtkPopUpState > {

    state : any;
    originalHeight : number;
    setAttributes : any;

    constructor() {
        super();

        this.state = {
            top: 0,
            left: 0,
            maxHeight: 500,
            width: 600
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
        //store.dispatch(ACTION.MODALS.REGISTER(this.props.name))

    }

    componentWillReceiveProps(props: any) {
        document.body.style.overflowY = 'auto';
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
        document.body.style.overflowY = 'auto';
    }

    hide(name : string) {
        //store.dispatch(ACTION.MODALS.CLOSE(name))
    }

    public render() : JSX.Element {

        let {title, children, name} = this.props;

        let events: {} = {};
        let visible: string = 'vtk-popup';

        events = {
            onClick: (e : any) => {
                this.hide(name)
            },

            style: {
                "color": "white",
                "position": "absolute",
                "top": "5px",
                "right": '5px'
            }
        }
/*
        if (!modals[name]) {
            visible = 'vtk-popup __hide';
        } else {
            visible = 'vtk-popup';
        }
*/
   visible = 'vtk-popup';
   
        return (
            <div
                id={name}
                className={visible}
                style={{

                left: this.state.left,
                top: this.state.top,
                width: this.state.width

            }}>
                <div className="__header">
                    {title}
                </div>
                <div
                    className="__content"
                   >
                    <div className="row">
                        hello word
                    </div>
                </div>
            </div>
        );
    }
}

export default VtkPopUp;
