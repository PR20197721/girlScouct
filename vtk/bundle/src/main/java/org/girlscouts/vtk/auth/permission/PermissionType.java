package org.girlscouts.vtk.auth.permission;

public enum PermissionType {
	
	PERMISSION_LOGIN_ID(0) ,
    PERMISSION_VIEW_YEARPLAN_ID(10),
    PERMISSION_VIEW_ACTIVITY_ID(100) ,
    PERMISSION_SEARCH_ACTIVITY_ID(110) ,
    PERMISSION_EDIT_ACTIVITY_ID(120) ,
    PERMISSION_RM_ACTIVITY_ID(130) ,
    PERMISSION_CREATE_ACTIVITY_ID(140) ,
    PERMISSION_VIEW_MEETING_ID(150) ,    
    PERMISSION_TERMINATE_ID(160) ;

	private int value;
	PermissionType( int id ){ value=id; }
}
