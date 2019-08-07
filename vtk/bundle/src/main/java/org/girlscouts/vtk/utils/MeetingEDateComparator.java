package org.girlscouts.vtk.utils;

import org.girlscouts.vtk.models.MeetingE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

public class MeetingEDateComparator implements Comparator<MeetingE> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public int compare(MeetingE meeting1, MeetingE meeting2) {
        try {
            if(meeting1 != null && meeting2 != null && meeting1.getDate() != null && meeting2.getDate() != null){
                return meeting1.getDate().compareTo(meeting2.getDate());
            }else{
                log.error("Unable to compare meeting1="+meeting1+" and meeting2="+meeting2);
            }
        }catch(Exception e){
            log.debug("Error occurred while sorting meeting events", e);
        }
        return 0;
    }
}
