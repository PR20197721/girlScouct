import * as React from 'react';

import Header from './header';
import YplanTrack from './year-plan-track';

interface CategoryProps {};

interface CategoryState {};

class Category extends React.Component<CategoryProps, CategoryState> {
    public render(): JSX.Element {
        return (<div className="column small-24">
            <Header title="Petals Leaf" subTitle="Girls Explore interesr on uil " />

            <YplanTrack/>

        </div>);
    }
}

export default Category;
