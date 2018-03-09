package org.girlscouts.web.permissions;

import javax.jcr.RepositoryException;

@SuppressWarnings("serial")
public class CounselPermissionModificationException extends Exception {

	public CounselPermissionModificationException(String string, RepositoryException re) {
		super(string, re);
	}
	
}
