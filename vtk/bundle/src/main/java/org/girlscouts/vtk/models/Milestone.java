package org.girlscouts.vtk.models;

/**
 * A milestone in a year plan instance.
 * e.g. Start of cookie sale
 * @author mike
 *
 */
public interface Milestone extends YearPlanComponent {
    String getName();
    void setName(String name);
}
