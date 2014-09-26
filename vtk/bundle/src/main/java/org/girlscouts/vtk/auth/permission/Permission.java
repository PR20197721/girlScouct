package org.girlscouts.vtk.auth.permission;

import java.util.*;



public class Permission extends PermissionConstants {
	
    public static final Permission login          = new Permission(PERMISSION_LOGIN_ID, null);
    public static final Permission year_plan_view   = new Permission(PERMISSION_VIEW_YEARPLAN_ID, login);
   
    /*** MEETING ***/
    public static final Permission meeting_view    =  new Permission(PERMISSION_VIEW_MEETING_ID, year_plan_view);
    
    /*** ACTIVITY ***/
    public static final Permission activity_view   =  new Permission(PERMISSION_VIEW_ACTIVITY_ID, year_plan_view);
    public static final Permission activity_search =  new Permission(PERMISSION_SEARCH_ACTIVITY_ID, activity_view);
    public static final Permission activity_edit   =  new Permission(PERMISSION_EDIT_ACTIVITY_ID, activity_view);
    public static final Permission activity_create =  new Permission(PERMISSION_CREATE_ACTIVITY_ID, activity_edit);
    public static final Permission activity_rm     =  new Permission(PERMISSION_RM_ACTIVITY_ID, activity_edit);
    
   
	
	
	
    private static Map<Integer, Permission> GLOBAL_PERMISSION_MAP;
    
    private Integer thisId;
    private Permission thisParent;
    private Set<Permission> thisChildren;

    private Permission() {
    	throw new IllegalArgumentException("Permissions require a permission id and parent");
    }
    private Permission(final int i) {
    	this(i, null); 
    }
    private Permission(final int i, final Permission parent) {    
        thisId = Integer.valueOf(i);
        thisChildren = new HashSet<Permission>();
        thisParent = parent;
        if (GLOBAL_PERMISSION_MAP == null) {
        	GLOBAL_PERMISSION_MAP = new HashMap<Integer, Permission>();
        }
        GLOBAL_PERMISSION_MAP.put(thisId, this);
        if (parent != null) {
            parent.addChild(this);      
        }
    }

    public Integer getId() {
        return thisId;
    }

    public Permission getParent() {
        return thisParent;
    }

    private void addChild(final Permission child) {
        thisChildren.add(child);
    }

    public Permission[] getChildren() {
        return (Permission[])thisChildren.toArray(new Permission[0]);
    }

    public int getDepth() {
        if (thisParent == null) {
            return 0;
        } else {
            return thisParent.getDepth() + 1;
        }
    }

    public static Permission getPermission(final int i) {
        return GLOBAL_PERMISSION_MAP.get(new Integer(i));
    }

    public static Set<Integer> getPermissionTokens(int[] myPermissions) {
    	Set<Integer> myPermSet = new HashSet<Integer>();
    	for (int i: myPermissions) {
    		Permission thisPermission = GLOBAL_PERMISSION_MAP.get(Integer.valueOf(i));
    		if (thisPermission != null) {
	    		for (int currentDepth = thisPermission.getDepth(); currentDepth>0; currentDepth--) {
    				myPermSet.add(thisPermission.getId());
	    			thisPermission = thisPermission.getParent();
    				myPermSet.add(thisPermission.getId());
	    		}
    		}
    	}
    	return myPermSet;
    }
    public static void main(String[] args) {
    	
    	Set<Integer> myPermissionTokens = getPermissionTokens(GROUP_MEMBER_1G_PERMISSIONS ); 
    	System.out.println("PERMISSION_VIEW_ACTIVITY_ID " + myPermissionTokens.contains(PERMISSION_VIEW_ACTIVITY_ID));
    	System.out.println("PERMISSION_SEARCH_ACTIVITY_ID " + myPermissionTokens.contains(PERMISSION_SEARCH_ACTIVITY_ID));
    	System.out.println("PERMISSION_RM_ACTIVITY_ID " + myPermissionTokens.contains(PERMISSION_RM_ACTIVITY_ID));
    	System.out.println("PERMISSION_EDIT_ACTIVITY_ID " + myPermissionTokens.contains(PERMISSION_EDIT_ACTIVITY_ID));
    	System.out.println("PERMISSION_CREATE_ACTIVITY_ID " + myPermissionTokens.contains(PERMISSION_CREATE_ACTIVITY_ID));           
    	System.out.println("PERMISSION_VIEW_MEETING_ID " + myPermissionTokens.contains(PERMISSION_VIEW_MEETING_ID));
       	System.out.println("PERMISSION_LOGIN_ID  " + myPermissionTokens.contains(PERMISSION_LOGIN_ID ));
       	System.out.println("PERMISSION_VIEW_YEARPLAN_ID " + myPermissionTokens.contains(PERMISSION_VIEW_YEARPLAN_ID));    	
    	System.out.println("PERMISSION_TERMINATE_ID " + myPermissionTokens.contains(PERMISSION_TERMINATE_ID));

    }
}

