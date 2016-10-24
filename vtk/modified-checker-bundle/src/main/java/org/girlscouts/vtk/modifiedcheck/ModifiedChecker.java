package org.girlscouts.vtk.modifiedcheck;

public interface ModifiedChecker {
	boolean isModified(String sessionId, String yearplanId);
	void setModified(String sessionId, String yearplanId);
}
