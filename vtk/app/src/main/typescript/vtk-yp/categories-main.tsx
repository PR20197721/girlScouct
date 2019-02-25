import * as React from 'react';
import Category from './category';
import Head from './head';


export interface CategoriesMainProps {
    data:any;
    store:any
}

export default class CategoriesMain extends React.Component<CategoriesMainProps, any> {

    store(state, func) { 
        this.setState({ data: state }, func)
    }

    showPreview(){
        this.setState({showPreview:!this.state.showPreview});
    }
    
  render() {
    return (
        <div className="__categories_main">
        <Head />
        {(this.state.showTracks) ? <div className="__categories-wrap">
            {this
                .props
                .data
                .Category
                .map((cat, idx, arr) => {
                    return <div key={'category-' + idx}>
                        <Category  {...cat} store={this.store.bind(this)} idx={idx} showPreview={this.showPreview.bind(this)} />
                    </div>
                })
            }
        </div>: null}
    </div>
    );
  }
}
