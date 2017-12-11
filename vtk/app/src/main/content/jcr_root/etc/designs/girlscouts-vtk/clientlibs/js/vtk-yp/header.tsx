import * as React from 'react';

interface HeaderProps {
    title: string,
    subTitle:string
};

interface HeaderState {};

class Header extends React.Component<HeaderProps, HeaderState> {
    public render(): JSX.Element {
        return (
            <div className="row">
            <div className="columns medium-20 small-22 small-centered medium-centered" style={{padding:'0px'}}>
                <div  className="__header" ><b>{this.props.title}</b></div>
                <p style={{paddingLeft:'5px'}}><b>{this.props.subTitle}</b></p>
                </div>
            </div>    
           
        );
    }
}

export default Header;
