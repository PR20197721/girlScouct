package org.girlscouts.vtk.utils;

import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.MeetingE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

public class ActivityDateComparator implements Comparator<Activity> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public int compare(Activity activity1, Activity activity2) {
        try {
            if(activity1 != null && activity2 != null && activity1.getDate() != null && activity2.getDate() != null){
                return activity1.getDate().compareTo(activity2.getDate());
            }else{
                log.error("Unable to compare activity1="+activity1+" and activity2="+activity2);
            }
        }catch(Exception e){
            log.debug("Error occurred while sorting activities by date", e);
        }
        return 0;
    }
}
