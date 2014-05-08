package org.girlscouts.vtk.models;

import java.util.List;

/**
 * A national or council wise year plan template
 * No date information for meetings.
 * 
 * @author mike
 *
 */
public interface YearPlan {
    /**
     * @return year in 4 digits.
     */
    String getYear();
    
    /**
     * @return council ID. 0 for the national site.
     */
    String getCouncilId();
    
    /**
     * @return the level. e.g. Brownie
     */
    String getLevel();
    
    /**
     * @return the plan name. e.g. Journey = It's Your World
     */
    String getName();
    
    /**
     * @return a one-sentence description of the plan.
     */
    String getDescription();
    
    /**
     * @return a list of assets that belongs to this plan.
     */
    List<Asset> getResources();
    
    /**
     * @return a list of year plan components. 
     *         They should all be meetings here.
     */
    List<YearPlanComponent> getComponents();
}
