package org.girlscouts.vtk.dao;

import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;

public interface ActivityDAO {
    void addToYearPlan(User user, Troop troop, Activity activity) throws IllegalStateException, IllegalAccessException;

    boolean isActivityByPath(User user, String path) throws IllegalStateException, IllegalAccessException;

    Activity updateActivity(User user, Troop troop, Activity activity) throws IllegalAccessException, IllegalStateException;

}
