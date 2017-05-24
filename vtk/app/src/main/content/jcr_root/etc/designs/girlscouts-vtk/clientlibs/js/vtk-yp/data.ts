import * as axios from 'axios';

import Axios from 'axios';

export const ULR = 'URL';

declare var ________app________: string;
declare var ________app1________: string;
declare var ________isYearPlan________: boolean;
declare var ________currentYearPlanName________: string;
declare var ________troopName________: string;


export function getYearPlan() { 
    const level: string = `${________app________}`;
    return  Axios.get(
       window.location.origin + '/content/vtkcontent/en/year-plan-library/'+level+'/_jcr_content/content/middle/par.1.json')

        .then((data) => { 

            return parseJSONVTK(data.data)
        });
}
export function getPDF() {

           const level: string = `${________app________}`;
    return Axios.get(
        window.location.origin + '/content/dam/girlscouts-vtkcontent/PDF/'+level+'.1.json')
        .then((d: any) => {
     

            for (let a in d.data) {
                if (a.match(/.pdf/)) {
                    return window.location.origin + '/content/dam/girlscouts-vtkcontent/PDF/' + level +'/' + a;
                }
            }
        });
}
export function getMeetings(url: string) {

    return Axios.get(window.location.origin+ '/content/girlscouts-vtk/en/vtk.vtkyearplan.html?ypp=' + url).then((data) => { 
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
        meetings: json.meetingEvents
    };

    for (var s in json.meetings) { 

        if (s.match(/meeting/)) { 
            let index = parseInt(s.match(/[0-9]+/)[0]) - 1;
            meetings_.meetings[index] = json.meetings[s]
        }
    }
 
   return meetings_;
}
export var modal = (function () {
        
    var topics = {};
      var hOP = topics.hasOwnProperty;

  return {
    subscribe: function (topic, listener) {

      // Create the topic's object if not yet created
      if(!hOP.call(topics, topic)) topics[topic] = [];

      // Add the listener to queue
      var index = topics[topic].push(listener) -1;

      // Provide handle back for removal of topic
      return {
        remove: function() {
          delete topics[topic][index];
        }
      };
    },
    publish: function(topic, info) {
      // If the topic doesn't exist, or there's no listeners in queue, just leave
      if(!hOP.call(topics, topic)) return;

      // Cycle through topics queue, fire!
      topics[topic].forEach(function(item) {
      		item(info != undefined ? info : {});
      });
    },


    print: function () { 
        return topics;
    }
  };












    // let _modal = {};
    
    // function _reset() { }
    // function _open() { }
    // function _close() { }
    // function _register() { }
    // function _remove() { }


    // return {
    //     register: _register,
    //     reset: _reset,
    //     open: _open,
    //     close: _close,
    //     remove:_remove
    // }
})();
export var store = (function () {
    let _global:Object = {};
    
    function _add(name, data) { 
        _global[name] = data;
        return _global;
    }


    function _get(name) { 
        return (_global.hasOwnProperty('name')) ? _global[name] : {};
    }


    function _reset() { 
        _global = {};
        return _global;
    }
    

    return {
        add: _add,
        get: _get,
        reset: _reset,
    }



})();

