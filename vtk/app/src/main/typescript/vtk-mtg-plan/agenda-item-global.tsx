import * as React from 'react';

export interface AgendaItemGlobalProps {
    multiactivities: any;
}

export default function AgendaItemGlobal(props: AgendaItemGlobalProps) {
    let hasGlobal = [];

    if (props.multiactivities) {
        hasGlobal = props.multiactivities.filter((activity) => activity.global)
    }

    return (
        <div>
            {(hasGlobal.length && !hasGlobal[0].isSelected && (props.multiactivities != null && props.multiactivities.length > 1)) ?
                <img src='/etc/designs/girlscouts-vtk/clientlibs/css/images/globe_unselected.png'/> : null}
            {(hasGlobal.length && hasGlobal[0].isSelected || (props.multiactivities != null && props.multiactivities.length == 1) && hasGlobal.length) ?
                <img src='/etc/designs/girlscouts-vtk/clientlibs/css/images/globe_selected.png'/> : null}
        </div>
    );
}
