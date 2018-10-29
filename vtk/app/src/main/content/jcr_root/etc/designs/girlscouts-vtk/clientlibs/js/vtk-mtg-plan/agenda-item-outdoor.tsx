import * as React from 'react';

export interface AgendaItemOutDoorProps {
  multiactivities: any;
}

export default function AgendaItemOutDoor (props: AgendaItemOutDoorProps) {
  let hasOutdoor = [];

  if(props.multiactivities){
    hasOutdoor = props.multiactivities.filter((activity)=>activity.outdoor)
  }
  
  return (
    <div  >
      {(hasOutdoor.length && !hasOutdoor[0].isSelected && props.multiactivities.length > 1)?<img src='/etc/designs/girlscouts-vtk/clientlibs/css/images/indoor.png' />:null}
      {(hasOutdoor.length && hasOutdoor[0].isSelected || props.multiactivities.length == 1 && hasOutdoor.length )?<img src='/etc/designs/girlscouts-vtk/clientlibs/css/images/outdoor.png' />:null}
    </div>
  );
}
