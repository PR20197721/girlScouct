package org.girlscouts.vtk.model;

import java.util.Date;

/**
 * A year plan component builds up a year plan instance.
 * It can be a {@link Meeting}, an {@link Activity} or a {@link Milestone}.
 * This interface might be used to merge lists of different components
 * and sort them by date.
 * 
 * @author mike
 *
 */
public interface YearPlanComponent {
    /**
     * @return the component type.
     * @see {@link YearPlanComponentType}
     */
    YearPlanComponentType getType();

    /**
     * @return the <b>start</b> date.
     *         <code>null</code> if not set.
     */
    Date getDate();
    void setDate(Date date);
}
