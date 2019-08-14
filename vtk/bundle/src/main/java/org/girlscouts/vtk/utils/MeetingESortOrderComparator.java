package org.girlscouts.vtk.utils;

import org.girlscouts.vtk.models.MeetingE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

public class MeetingESortOrderComparator implements Comparator<MeetingE> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public int compare(MeetingE meeting1, MeetingE meeting2) {
        try {
            if(meeting1 != null && meeting2 != null && meeting1.getSortOrder() != null && meeting2.getSortOrder() != null){
                return meeting1.getSortOrder().compareTo(meeting2.getSortOrder());
            }else{
                log.error("Unable to compare meeting1="+meeting1+" and meeting2="+meeting2);
            }
        }catch(Exception e){
            log.debug("Error occurred while sorting meeting events", e);
        }
        return 0;
    }
}