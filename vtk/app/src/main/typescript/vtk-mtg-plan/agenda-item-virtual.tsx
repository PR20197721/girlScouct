import * as React from 'react';

export interface AgendaItemVirtualProps {
    multiactivities: any;
}

export default function AgendaItemVirtual(props: AgendaItemVirtualProps) {
    let hasVirtual = [];

    if (props.multiactivities) {
        hasVirtual = props.multiactivities.filter((activity) => activity.virtual)
    }

    return (
        <div>
            {(hasVirtual.length && !hasVirtual[0].isSelected && (props.multiactivities != null && props.multiactivities.length > 1)) ?
                <img src='/etc/designs/girlscouts-vtk/clientlibs/css/images/virtual_unselected.png'/> : null}
            {(hasVirtual.length && hasVirtual[0].isSelected || (props.multiactivities != null && props.multiactivities.length == 1) && hasVirtual.length) ?
                <img src='/etc/designs/girlscouts-vtk/clientlibs/css/images/virtual_selected.png'/> : null}
        </div>
    );
}
