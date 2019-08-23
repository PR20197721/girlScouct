package org.girlscouts.vtk.utils;

import org.girlscouts.vtk.models.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

public class ActivityNumberComparator implements Comparator<Activity> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public int compare(Activity activity1, Activity activity2) {
        try {
            if(activity1 != null && activity2 != null && (Integer)activity1.getActivityNumber() != null && (Integer)activity2.getActivityNumber() != null){
                return Integer.valueOf(activity1.getActivityNumber()).compareTo(activity2.getActivityNumber());
            }else{
                log.error("Unable to compare activity1="+activity1+" and activity2="+activity2);
            }
        }catch(Exception e){
            log.debug("Error occurred while sorting activities by activity number", e);
        }
        return 0;
    }
}
