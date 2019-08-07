package org.girlscouts.vtk.utils;

import org.girlscouts.vtk.models.Meeting;
import org.girlscouts.vtk.models.MeetingE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

public class MeetingPositionComparator implements Comparator<Meeting> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public int compare(Meeting meeting1, Meeting meeting2) {
        try {
            if(meeting1 != null && meeting2 != null && meeting1.getPosition() != null && meeting2.getPosition() != null){
                return meeting1.getPosition().compareTo(meeting2.getPosition());
            }else{
                log.error("Unable to compare meeting1="+meeting1+" and meeting2="+meeting2);
            }
        }catch(Exception e){
            log.debug("Error occurred while sorting meetings", e);
        }
        return 0;
    }
}
