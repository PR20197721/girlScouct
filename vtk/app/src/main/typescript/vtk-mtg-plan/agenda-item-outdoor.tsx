import * as React from 'react';

export interface AgendaItemOutdoorProps {
    multiactivities: any;
}

export default function AgendaItemOutdoor(props: AgendaItemOutdoorProps) {
    let outdoorAgendaItems = [];
    let hasOutdoor = false;
    let outdoorSelected = false;

    if (props.multiactivities) {
        //console.log(props.multiactivities)
        outdoorAgendaItems = props.multiactivities.filter((activity) => (activity.outdoor))
    }
    if(outdoorAgendaItems.length){
        hasOutdoor = true;
        outdoorAgendaItems.forEach( (agendaItem) => {
            if(agendaItem.isSelected){
                outdoorSelected = true;
            }
        });
    }

    return (
        <div>
            {(hasOutdoor && !outdoorSelected && (props.multiactivities != null && props.multiactivities.length > 1)) ?
                <div className="__is-outdoor"><img src='/etc/designs/girlscouts-vtk/clientlibs/css/images/indoor.png'/></div> : null}
            {(hasOutdoor && outdoorSelected || (props.multiactivities != null && props.multiactivities.length == 1 && hasOutdoor)) ?
                <div className="__is-outdoor"><img src='/etc/designs/girlscouts-vtk/clientlibs/css/images/outdoor.png'/></div> : null}
        </div>
    );
}
