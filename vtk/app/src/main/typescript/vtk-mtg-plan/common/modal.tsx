import * as React from 'react';
import $ from 'jquery';
import { connect } from 'react-redux';
import './modal.scss';
import { HELPER } from '../helper';
import { Actions } from '../store/actions';
import { ActionsTypes } from '../store/actionsType';
import { DetailedHTMLProps } from 'react';

export interface VtkModalProps {
    modal:{
        title:string,
        description:any
    }
    dispatch: any
}

class VtkModal extends React.PureComponent<VtkModalProps, any> {
		componentDidMount() {
			window.onresize = () => {
				this.forceUpdate();
			};
			window.onscroll = () =>{
				this.forceUpdate();
			}
		}

		componentWillUnmount() {
			window.onresize = () => {};
			window.onscroll = () => {};
		}
		replaceAll(str, rep, val) {
            if(str === null){
                return;
            }
            return str.replace(new RegExp(rep, 'g'), val);
        }
        checkAgenda(innerText){
            var assets = [];
            var assetNames = [];

            //Parse Meeting aid Titles and Hrefs into arrays
            $(".__list_of_assets").children().each(function(){
                assetNames.push($.trim($(this).find("a").text()));
                assets.push($(this).find("a").attr("href"));
            });
            var contentVal;
            try{
                contentVal = innerText;
            }catch(err){
                return innerText;
            }
            for(var i = 0; i < $(".__list_of_assets").children().length; i++) {
                try{
                    if(contentVal !== undefined){
                        contentVal = this.replaceAll(contentVal, assetNames[i], "<a href='"+assets[i]+"'target='_blank'>" + assetNames[i]+"</a>");
                    }
                } catch(err){
                    return innerText;
                }
            }
            return contentVal;
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
            
            const heigthLogic = () => {
                const w = Math.max(document.documentElement.clientWidth, window.innerWidth || 0);
                let h = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);   
              
                const ratio = w / h;
                
                if(h>600 && ratio > 1){
                   h=600;
                }
                return h;
			}
			
			const top = () => {
				return window.scrollY;
			}

			return HELPER.objectIsEmpty(this.props.modal) ? <div className="vtk_modal __background" style={{ top: top() + "px" }}>
					<div className="__modal" style={{height:heigthLogic()+'px'}}>
						<div className="__header">
							<h3>
								{this.props.modal.title}{" "}
							</h3>
							<i onClick={close} className="icon-button-circle-cross" />
						</div>
						<div className="__description" style={{height:(heigthLogic()-40)+'px'}}>
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
