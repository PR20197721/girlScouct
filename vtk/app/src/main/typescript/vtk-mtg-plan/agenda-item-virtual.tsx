import * as React from 'react';

export interface AgendaItemVirtualProps {
    multiactivities: any;
}

export default function AgendaItemVirtual(props: AgendaItemVirtualProps) {
    let virtualAgendaItems = [];
    let hasVirtual = false;
    let virtualSelected = false;

    if (props.multiactivities) {
        console.log(props.multiactivities.toString())
        virtualAgendaItems = props.multiactivities.filter((activity) => (activity.virtual))
    }
    if(virtualAgendaItems.length){
        hasVirtual = true;
        virtualAgendaItems.forEach( (agendaItem) => {
            if(agendaItem.isSelected){
                virtualSelected = true;
            }
        });
    }
    console.log("hasVirtual = "+hasVirtual+", virtualSelected " + virtualSelected + ", props.multiactivities != null " + (props.multiactivities) + ", props.multiactivities.length = " + props.multiactivities.length);
    return (
        <div>
            {(hasVirtual && !virtualSelected && (props.multiactivities != null && props.multiactivities.length > 1)) ?
                <div className="__is-virtual"><img src='/etc/designs/girlscouts-vtk/clientlibs/css/images/virtual_unselected.png'/></div> : null}
            {(hasVirtual && virtualSelected || (props.multiactivities != null && props.multiactivities.length == 1 && hasVirtual)) ?
                <div className="__is-virtual"><img src='/etc/designs/girlscouts-vtk/clientlibs/css/images/virtual_selected.png'/></div> : null}
        </div>
    );
}
