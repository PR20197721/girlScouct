package org.girlscouts.web.permissions;

import org.girlscouts.web.permissions.dto.CounselPermissionUpdateDTO;

@FunctionalInterface
public interface CounselPermissionUpdate {
	public void update(CounselPermissionUpdateDTO dto, CounselPermissionTool tool) throws CounselPermissionModificationException;
}
