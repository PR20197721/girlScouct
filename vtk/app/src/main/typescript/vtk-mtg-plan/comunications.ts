

import Axios from 'axios';

module COMUNICATION {

        
    interface IComunication {
        url: string,
        payload: any,

    }

    interface IAxiosConfig {
        method?: any,
        url: any,
        data: any,
        headers?: any
    }

    const BASE_URL = '/vtk-data'

    const _getTroopDataToken = () => {
    	// Ref: https://developer.mozilla.org/en-US/docs/Web/API/Document/cookie
    	// Get cookie: troopDataToken
    	var hash = document.cookie.replace(/(?:(?:^|.*;\s*)troopDataToken\s*\=\s*([^;]*).*$)|^.*$/, "$1");
    	return hash;
    }

    export function  FETCH(url){
         return Promise.all([CALL({
             url:`${BASE_URL}/${_getTroopDataToken()}/${url}?_=${Date.now()}`,
             method:'get',
             data:{}
         }),CALL({
            url:`/content/vtkcontent/en/vtk-survey-links/_jcr_content/content/middle/par.1.json`,
            method:'get',
            data:{}
         }), CALL({
             url: `/content/vtkcontent/en/resources/volunteer-aids/vtkvideos/_jcr_content/content/middle/par.1.json`,
             method: 'get',
             data: {}
         })])
    }

    export function INTERVAL_FETCH(url, resolve, _error ):any{
        let countTimes = 0; 
        let eTag = null;
        // let        
        return async function() {
            try {
                //add headers if there is a eTag 
                const _headers = (eTag)?{headers:{'If-None-Match':eTag}}:{};

                let _call = await CALL({...{
                    url:`${BASE_URL}/${_getTroopDataToken()}/${url}`,
                    method:'get',
                    data:{}
                },..._headers});
                countTimes++;

                //Check if there are a 'Etag header'
                eTag = _call.request.getResponseHeader("ETag");

                //pass information to the callback -> reducers
                if(_call.status==200){
                    resolve(_call,countTimes);
                }
                
            } catch (error) {
                _error(error);
            }
        }
    }



    let config:any = {
        timeout: 10000,
    };

    export let _axios:any = Axios.create(config);


    function CALL(config: IAxiosConfig): any{
        return _axios(config);
    }

    export function REQUEST(rqs:any){
        return _axios({...rqs,...config});
    }


    export function GET(get:IComunication):any {
        return CALL({
            url: get.url,
            method: "get",
            data: get.payload
        });
    }
    export function POST(post: IComunication): any {
        return CALL({
            url: post.url,
            method: "post",
            data: post.payload

        });
    }
    export function PUT(putItem: IComunication): any {
        return CALL({
           url: putItem.url,
            method: "put",
            data: putItem.payload
        })
    }
    export function DELETE(deleteItem: IComunication):any {
        return CALL({
            url: deleteItem.url,
            method: "delete",
            data: deleteItem.payload
        });
    }


    export function getOriginalActivity(conf:IComunication) { 
        return GET(conf);
    }

}

export default COMUNICATION;