package org.girlscouts.vtk.modifiedcheck.impl;

import org.apache.commons.collections4.map.PassiveExpiringMap;

import org.girlscouts.vtk.modifiedcheck.ModifiedChecker;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = {ModifiedChecker.class }, immediate = true, name = "org.girlscouts.vtk.modifiedcheck.impl.ModifiedCheckImpl")
public class ModifiedCheckImpl implements ModifiedChecker {

    private static final Logger log = LoggerFactory.getLogger(ModifiedCheckImpl.class);

    private PassiveExpiringMap modifiedContainer;

    public ModifiedCheckImpl() {
        modifiedContainer = new PassiveExpiringMap(25000);
    }

    public boolean isModified(String sessionId, String yearplanId) {
        String modified_sId = (String) modifiedContainer.get(yearplanId);
        if (modified_sId != null && !modified_sId.equals(sessionId)) {
            log.debug("isModified : true");
            return true;
        }
        return false;
    }

    public void setModified(String sessionId, String path) {
        if (path == null || !path.endsWith("/yearPlan")) {
            return;
        }
        log.debug("inserting -" + path + "- into modifiedContainer...." + sessionId);
        modifiedContainer.put(path, sessionId);
        log.debug("Checking container size: " + modifiedContainer.size() + " : " + modifiedContainer.get(path));
    }
}
