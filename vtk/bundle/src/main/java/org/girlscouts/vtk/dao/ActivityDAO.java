package org.girlscouts.vtk.dao;

import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;

public interface ActivityDAO {

    void createActivity(User user, Troop troop, Activity activity)
            throws IllegalStateException, IllegalAccessException;

    boolean isActivity(User user, Troop troop, String uuid)
            throws IllegalStateException, IllegalAccessException;

    Activity findActivity(User user, String path)
            throws IllegalStateException, IllegalAccessException;

    boolean isActivityByPath(User user, String path)
            throws IllegalStateException, IllegalAccessException;

    boolean updateActivity(User user, Troop troop, Activity activity)
            throws IllegalAccessException, IllegalStateException;

}
