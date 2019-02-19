import { reduce } from "./reducers";
import thunk from 'redux-thunk';
import { createStore, applyMiddleware } from 'redux';
import {createLogger} from 'redux-logger';

 const store = <any>createStore(reduce, undefined ,applyMiddleware(thunk, createLogger())) ;


 export default store;