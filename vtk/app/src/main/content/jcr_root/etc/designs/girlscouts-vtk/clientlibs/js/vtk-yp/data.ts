import * as axios from 'axios';

import Axios from 'axios';

export const ULR = 'URL';

declare var ________app________: string;


export function getYearPlan() { 
    return  Axios.get(
        window.location.origin + '/content/vtkcontent/en/year-plan-library/daisy/_jcr_content/content/middle/par.1.json')
        .then((data) => { 
            return parseJSONVTK(data.data)
        });
}
    
export function getPDF() {

           const level: string = `${________app________}`;
    return Axios.get(
        window.location.origin + '/content/dam/girlscouts-vtkcontent/PDF/'+level+'.2.json')
        .then((d: any) => {
     

            for (let a in d.data) {
                if (a.match(/.pdf/)) {
                    return window.location.origin + '/content/dam/girlscouts-vtkcontent/PDF/' + level +'/' + a;
                }
            }
        });
}



export function getMeetings(url: string) {
    // Axios.defaults.headers.post['Content-Type'] = 'application/json';
    return Axios.get(window.location.origin + url + '.3.json').then((data) => { 
        console.log('AJAX',data.data)
        return parseMeetings(data.data);
    })
}




export function parseJSONVTK(json:any) {

    var parts: any[] = [];
    var currentCategory = 0;
    var counting = 0;
    var OtoR = {
        header: {},
        Category: [],
        bottom: {}
    }

    for (let part in json) {


        if (part === "jcr:primaryType" || part === "sling:resourceType") {
            continue;
        } else {
            counting++;
        }

        if (json[part].hasOwnProperty('level')) {

       
            if (json[part]['level'] === "Grade") {
                parts.push(json[part])
            } else if (json[part]['level'] === "Category") {
            
                json[part]['categories'] = [];
                parts.push(json[part])
                currentCategory = parts.length - 1;
            
            }
        } else {
        
            parts[currentCategory]['categories'].push(json[part])
        }
    }


    parts.forEach((elemen, idx) => {
        if (idx == 0 && elemen.level == "Grade") {
            OtoR['header'] = elemen;
        }

        if (idx > 0 && idx < parts.length - 1 && elemen.hasOwnProperty('categories')) {
            OtoR['Category'].push(elemen);
        }

        if (idx == parts.length - 1) {
            OtoR['bottom'] = elemen;
        }
    })


    return OtoR;

}    


export function parseMeetings(json: any) { 
    var meetings_ = {
        desc: json.desc,
        name: json.name,
        meetings: [],
        id:json.id
    };

    for (var s in json.meetings) { 
        debugger;
        if (s.match(/meeting/)) { 
            let index = parseInt(s.match(/[0-9]+/)[0]) - 1;
            meetings_.meetings[index] = json.meetings[s]
        }
    }
 
   return meetings_;
}

