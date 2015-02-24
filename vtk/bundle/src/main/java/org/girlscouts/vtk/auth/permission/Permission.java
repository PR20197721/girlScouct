package org.girlscouts.vtk.auth.permission;

import java.util.*;

public class Permission extends PermissionConstants {

	public static final Permission login = new Permission(PERMISSION_LOGIN_ID, null);
	public static final Permission year_plan_view = new Permission( PERMISSION_VIEW_YEARPLAN_ID, login);
	public static final Permission year_plan_edit = new Permission( PERMISSION_EDIT_YEARPLAN_ID, year_plan_view);
        public static final Permission year_plan_add = new Permission( PERMISSION_ADD_YEARPLAN_ID, year_plan_edit);
	public static final Permission year_plan_rm = new Permission( PERMISSION_RM_YEARPLAN_ID, year_plan_edit);
	
	/***  YEAR_PLAN_MEETING ***/
	public static final Permission yearPlan_meeting_edit = new Permission( PERMISSION_EDIT_YEARPLAN_MEETING_ID, year_plan_view);
	public static final Permission yearPlan_meeting_add = new Permission( PERMISSION_ADD_YEARPLAN_MEETING_ID, yearPlan_meeting_edit);
	public static final Permission yearPlan_meeting_rm = new Permission( PERMISSION_RM_YEARPLAN_MEETING_ID, yearPlan_meeting_edit);
	
	/*** MEETING ***/
	public static final Permission meeting_view = new Permission(PERMISSION_VIEW_MEETING_ID, year_plan_view);
	public static final Permission meeting_edit = new Permission(PERMISSION_EDIT_MEETING_ID, meeting_view);
        public static final Permission meeting_add = new Permission(PERMISSION_ADD_MEETING_ID, meeting_edit);
        public static final Permission meeting_create = new Permission(PERMISSION_ADD_MEETING_ID, meeting_edit);
        public static final Permission meeting_remove = new Permission(PERMISSION_REMOVE_MEETING_ID, meeting_create);

	/*** ACTIVITY  ***/
        public static final Permission activity_edit= new Permission(PERMISSION_EDIT_ACTIVITY_ID, year_plan_view);
	public static final Permission activity_add= new Permission(PERMISSION_ADD_ACTIVITY_ID, year_plan_view);
	public static final Permission activity_rm = new Permission(PERMISSION_RM_ACTIVITY_ID, activity_edit);
	
	/*** TROOP ***/
	public static final Permission troop_view = new Permission( PERMISSION_VIEW_TROOP_ID, login);
	public static final Permission troop_edit = new Permission( PERMISSION_EDIT_TROOP_ID, troop_view);

	/*** FINANCE ***/
	public static final Permission finance_view = new Permission(PERMISSION_VIEW_FINANCE_ID, login);

	/*** REPORT ***/
	public static final Permission report_view = new Permission( PERMISSION_VIEW_REPORT_ID, login);

	/*** MILESTONES ***/
	public static final Permission milestons_view = new Permission(PERMISSION_VIEW_MILESTONE_ID, login);
	
	/*** ATTENDANCE ***/
	public static final Permission attendance_view = new Permission(PERMISSION_VIEW_ATTENDANCE_ID, meeting_view);
	
	/*** EMAIL ***/
	public static final Permission email_send = new Permission(PERMISSION_SEND_EMAIL_ID, meeting_view);
	
	private static Map<Integer, Permission> GLOBAL_PERMISSION_MAP;

	private Integer thisId;
	private Permission thisParent;
	private Set<Permission> thisChildren;

	private Permission() {
		throw new IllegalArgumentException(
				"Permissions require a permission id and parent");
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
		return (Permission[]) thisChildren.toArray(new Permission[0]);
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
		for (int i : myPermissions) {
			Permission thisPermission = GLOBAL_PERMISSION_MAP.get(Integer
					.valueOf(i));
			if (thisPermission != null) {
				for (int currentDepth = thisPermission.getDepth(); currentDepth > 0; currentDepth--) {
					myPermSet.add(thisPermission.getId());
					thisPermission = thisPermission.getParent();
					myPermSet.add(thisPermission.getId());
				}
			}
		}
		return myPermSet;
	}

	public static void main(String[] args) {

		Set<Integer> myPermissionTokens = getPermissionTokens(GROUP_MEMBER_1G_PERMISSIONS);

	}
}
