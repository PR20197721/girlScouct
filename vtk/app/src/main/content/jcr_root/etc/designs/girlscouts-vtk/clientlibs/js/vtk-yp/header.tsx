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
            <div className="columns small-20 small-centered" style={{padding:'0px'}}>
                <div  className="__header" >{this.props.title}</div>
                 <p><b>{this.props.subTitle}</b></p>
                </div>
            </div>    
           
        );
    }
}

export default Header;
