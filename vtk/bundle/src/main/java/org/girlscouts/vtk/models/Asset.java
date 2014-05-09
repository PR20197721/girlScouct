package org.girlscouts.vtk.models;

/**
 * Asset reference in the DAM.
 * It can be either shared asset or troop leader customize asset.
 * 
 * @author mike
 *
 */
public interface Asset {
    /**
     * Whether this is custom asset
     * 
     * @return <code>true</code> if this asset is created by troop leader and
     *         should be deleted from DAM if this reference is deleted.
     *         <code>false</code> if this asset is shared
     */
    boolean isCustom();

    /**
     * @return the path in the JCR
     */
    String getPath();
    
    /**
     * Remove the actual content in DAM if it is custom asset
     * Callback when this asset is removed from a meeting instance.
     */
    void discard();
}
