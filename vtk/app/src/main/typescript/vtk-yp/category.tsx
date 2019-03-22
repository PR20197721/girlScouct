import * as React from 'react';

import Header from './header';
import YplanTrack from './year-plan-track';
import { modal } from './data';

interface CategoryProps {
    title: string;
    subtitle: string;
    categories: any[];
    store: Function;
    idx: number;
    showPreview: Function;
};

interface CategoryState {
   
};





class Category extends React.Component<CategoryProps, CategoryState> {
    
        
    public render(): JSX.Element {

        return (<div className="columns small-24">
            <div className="__categories">
                <Header title={this.props.title} subTitle={this.props.subtitle} />
                <div className="row">
                    <div className="columns small-20 small-centered">
                        {this.props.categories.map((track,idx,array) => { 
                            return <YplanTrack key={'YplanTrack' + idx + track.track} {...track} first={idx==0 && this.props.idx == 0} last={array.length - 1 == idx} store={this.props.store} showPreview={this.props.showPreview}/>
                        })}
                    </div>
                </div>
            </div>
                        
     
        </div>);
    }
}


export default Category;
