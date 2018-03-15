package org.girlscouts.web.permissions;

@SuppressWarnings("serial")
public class CounselPermissionModificationException extends Exception {

	public CounselPermissionModificationException(String string, Exception re) {
		super(string, re);
	}

	public CounselPermissionModificationException(String string) {
		super(string);
	}
	
}
