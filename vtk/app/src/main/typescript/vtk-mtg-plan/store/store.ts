import {reduce} from "./reducers";
import thunk from 'redux-thunk';
import {applyMiddleware, createStore} from 'redux';
import {createLogger} from 'redux-logger';

const store = <any>createStore(reduce, void 0, applyMiddleware(thunk, createLogger()));


export default store;