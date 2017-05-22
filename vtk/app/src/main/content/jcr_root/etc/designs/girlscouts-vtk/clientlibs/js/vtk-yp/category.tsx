import * as React from 'react';

import Header from './header';
import YplanTrack from './year-plan-track';

interface CategoryProps {
    title: string;
    subtitle: string;
    categories: any[];
};

interface CategoryState {};

class Category extends React.Component<CategoryProps, CategoryState> {
    public render(): JSX.Element {


        return (<div className="column small-24">
            <Header title={this.props.title} subTitle={this.props.subtitle} />

            {this.props.categories.map((track,idx,array) => { 
                return <YplanTrack key={idx + track.track} {...track} last={array.length-1 == idx} />
            })}


            <br />

        </div>);
    }
}

export default Category;
