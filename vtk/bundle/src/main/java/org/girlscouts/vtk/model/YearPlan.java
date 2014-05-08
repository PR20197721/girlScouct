package org.girlscouts.vtk.model;

import java.util.List;

/**
 * Represents a national or council wise year plan template
 * No date information for meetings.
 * 
 * @author mike
 *
 */
public interface YearPlan {
    /**
     * @return returns year in 4 digits.
     */
    String getYear();
    
    /**
     * @return returns council ID. 0 for the national site.
     */
    String getCouncilId();
    
    /**
     * @return returns the level. e.g. Brownie
     */
    String getLevel();
    
    /**
     * @return returns the plan name. e.g. Journey = It's Your World
     */
    String getName();
    
    /**
     * @return returns a one-sentence description of the plan.
     */
    String getDescription();
    
    /**
     * @return returns a list of assets that belongs to this plan.
     */
    List<Asset> getResources();
    
    /**
     * @return returns a list of year plan components. 
     *         They should all be meetings here.
     */
    List<YearPlanComponent> getComponents();
}
