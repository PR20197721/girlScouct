import * as React from 'react';

import Header from './header';
import YplanTrack from './year-plan-track';
import { modal } from './data';

interface CategoryProps {
    title: string;
    subtitle: string;
    categories: any[];
    store: Function
};

interface CategoryState {
   
};





class Category extends React.Component<CategoryProps, CategoryState> {
    
        
    public render(): JSX.Element {


        return (<div className="column small-24">
            <Header title={this.props.title} subTitle={this.props.subtitle} />

            {this.props.categories.map((track,idx,array) => { 
                return <YplanTrack key={'YplanTrack'+idx + track.track} {...track} last={array.length - 1 == idx} store={this.props.store}/>
            })}

     


        </div>);
    }
}

export default Category;
