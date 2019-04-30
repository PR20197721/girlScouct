package org.girlscouts.vtk.dao;

import org.girlscouts.vtk.models.Council;
import org.girlscouts.vtk.models.Milestone;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.utils.VtkException;

import java.util.List;

public interface CouncilDAO {

    Council findCouncil(User user, String path) throws IllegalAccessException, VtkException;

    Council createCouncil(User user, Troop troop) throws IllegalAccessException, VtkException;

    Council getOrCreateCouncil(User user, Troop troop) throws IllegalAccessException, VtkException;

    List<Milestone> getCouncilMilestones(User user, Troop troop) throws IllegalAccessException;

    void updateCouncilMilestones(User user, List<Milestone> milestones, Troop troop) throws IllegalAccessException;

    void GSMonthlyRpt();

    void GSMonthlyDetailedRpt(String year);

    void GSRptCouncilPublishFinance();


}
