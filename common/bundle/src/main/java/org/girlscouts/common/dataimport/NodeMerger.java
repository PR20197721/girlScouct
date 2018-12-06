package org.girlscouts.common.dataimport;

import org.apache.sling.api.resource.ResourceResolver;
import org.girlscouts.common.exception.GirlScoutsException;

public interface NodeMerger {
    String[] merge(String origPath, String destPath, ResourceResolver rr) throws GirlScoutsException;
}
