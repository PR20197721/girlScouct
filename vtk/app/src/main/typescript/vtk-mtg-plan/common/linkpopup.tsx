import * as React from 'react';

export interface VtkLinkPopUpProps {
    title:string,
    url: string,
    onclick?: any;
}




export default class VtkLinkPopUp extends React.PureComponent<VtkLinkPopUpProps, any> {
  render() {

    let {title, url, onclick} = this.props;

    return (!onclick)?<a  style={{fontWeight:'bold'}}
        data-reveal-id="modal_popup"
        data-reveal-ajax="true" 
        href={url}> {title} </a>
        :<a  onClick={()=>onclick()} > {title} </a>
    ;
  }
}
