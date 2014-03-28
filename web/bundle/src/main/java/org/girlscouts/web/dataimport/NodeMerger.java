package org.girlscouts.web.dataimport;

import org.apache.sling.api.resource.ResourceResolver;
import org.girlscouts.web.exception.GirlScoutsException;

public interface NodeMerger {
    String[] merge(String origPath, String destPath, ResourceResolver rr) throws GirlScoutsException;
}
