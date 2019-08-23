package org.girlscouts.vtk.osgi.component.dao;

import org.girlscouts.vtk.exception.VtkException;
import org.girlscouts.vtk.models.*;

import java.util.Map;

public interface TroopDAO {
    Troop getTroopByPath(User user, String troopPath) throws IllegalAccessException;

    YearPlan addYearPlan1(User user, Troop troop, String yearPlanPath) throws IllegalAccessException;

    boolean updateTroop(User user, Troop troop) throws IllegalAccessException, VtkException;

    void rmTroop(Troop troop) throws IllegalAccessException;

    Finance getFinances(Troop troop, int qtr, String currentYear);

    void setFinances(Troop troop, int qtr, String currentYear, Map<String, String[]> params);

    FinanceConfiguration getFinanceConfiguration(Troop troop, String currentYear);

    void setFinanceConfiguration(Troop troop, String currentYear, String income, String expenses, String period, String recipient);

    boolean removeActivity(User user, Troop troop, Activity activity) throws IllegalAccessException;

    boolean removeMeeting(User user, Troop troop, MeetingE meeting) throws IllegalAccessException;

    boolean removeAsset(User user, Troop troop, Asset asset) throws IllegalAccessException;

    boolean removeMeetings(User user, Troop troop) throws IllegalAccessException;

    Map getArchivedYearPlans(User user, Troop troop);

    boolean isArchivedYearPlan(User user, Troop troop, String year);

}
