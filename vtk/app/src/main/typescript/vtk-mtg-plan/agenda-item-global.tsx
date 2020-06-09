import * as React from 'react';

export interface AgendaItemGlobalProps {
    multiactivities: any;
}

export default function AgendaItemGlobal(props: AgendaItemGlobalProps) {
    let globalAgendaItems = [];
    let hasGlobal = false;
    let globalSelected = false;
    //console.log("multiactivities " + props.multiactivities);
    if (props.multiactivities) {
        globalAgendaItems = props.multiactivities.filter((activity) => (activity.global))
    }
    //console.log("globalAgendaItems " +globalAgendaItems);
    if(globalAgendaItems.length){
        hasGlobal = true;
        globalAgendaItems.forEach( (agendaItem) => {
            if(agendaItem.isSelected){
                globalSelected = true;
            }
        });
    }

    return (
        <div>
            {(hasGlobal && !globalSelected && (props.multiactivities != null && props.multiactivities.length > 1)) ?
                <div className="__is-global"><img src='/etc/designs/girlscouts-vtk/clientlibs/css/images/globe_unselected.png'/></div> : null}
            {(hasGlobal && globalSelected || (props.multiactivities != null && props.multiactivities.length == 1 && hasGlobal)) ?
                <div className="__is-global"><img src='/etc/designs/girlscouts-vtk/clientlibs/css/images/globe_selected.png'/></div> : null}
        </div>
    );
}
