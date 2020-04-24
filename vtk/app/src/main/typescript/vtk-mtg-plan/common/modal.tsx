import * as React from 'react';
import * as $ from 'jquery';
import { connect } from 'react-redux';
import './modal.scss';
import { HELPER } from '../helper';
import { Actions } from '../store/actions';
import { ActionsTypes } from '../store/actionsType';
import { DetailedHTMLProps } from 'react';

export interface VtkModalProps {
    modal: {
        title: string,
        description: any
    }
    dispatch: any
}

class VtkModal extends React.PureComponent<VtkModalProps, any> {
    componentDidMount() {
        window.onresize = () => {
            this.forceUpdate();
        };
        window.onscroll = () => {
            this.forceUpdate();
        }
    }

    componentWillUnmount() {
        window.onresize = () => { };
        window.onscroll = () => { };
    }

    checkAgenda(innerText) {
        var newHtml = innerText || "";
        if (!newHtml.length) return "";

        $(".__list_of_assets li a").each(() => {
            var text = $(this).text() || "",
                href = $(this).attr("href") || "",
                link = `<a href='${href}' target='_blank'>${text}</a>`;
            if (text.length && href.length) {
                // Replace modal description text with asset links
                newHtml.split(text).join(link);
            }
        });

        return newHtml;
    }

    render() {
        const construct = () => {
            document.body.style.overflow = "hidden";
        };

        const destroy = () => {
            document.body.style.overflow = "";
            window.onbeforeunload = undefined;
        };

        if (HELPER.objectIsEmpty(this.props.modal)) {
            construct();
        } else {
            destroy();
        }

        const close = () => {
            this.props.dispatch({
                type: ActionsTypes.DESTROY_MODAL,
                payload: { modal: {} },
            });
        };

        const heightLogic = () => {
            const w = Math.max(document.documentElement.clientWidth, window.innerWidth || 0);
            let h = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);
            const ratio = w / h;

            if (h > 600 && ratio > 1) {
                h = 600;
            }
            return h;
        }

        const top = () => {
            return window.scrollY;
        }

        return HELPER.objectIsEmpty(this.props.modal) ? <div className="vtk_modal __background" style={{ top: top() + "px" }}>
            <div className="__modal" style={{ height: heightLogic() + 'px' }}>
                <div className="__header">
                    <h3>
                        {this.props.modal.title}{" "}
                    </h3>
                    <span className="modal-close-btn" onClick={close} >X</span>
                </div>
                <div className="__description" style={{ height: (heightLogic() - 40) + 'px' }}>
                    <div className="__scroll" dangerouslySetInnerHTML={{ __html: this.checkAgenda(this.props.modal.description) }} />
                </div>
            </div>
        </div> : null;
    }
}

let modal = state => {
    return { modal: state.modal };
};

export default connect(modal)(VtkModal);