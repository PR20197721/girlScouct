package org.girlscouts.vtk.auth.permission;

import org.apache.commons.lang3.ArrayUtils;

public class PermissionConstants {

	public static final int PERMISSION_LOGIN_ID = 0;
	public static final int PERMISSION_VIEW_YEARPLAN_ID = 10;
	public static final int PERMISSION_ADD_YEARPLAN_ID = 11;
	public static final int PERMISSION_RM_YEARPLAN_ID = 12;
	public static final int PERMISSION_EDIT_YEARPLAN_ID = 13;
	public static final int PERMISSION_EDIT_ACTIVITY_ID = 120;
	public static final int PERMISSION_RM_ACTIVITY_ID = 130;
	public static final int PERMISSION_ADD_ACTIVITY_ID=150;
	public static final int PERMISSION_VIEW_MEETING_ID = 200;
	public static final int PERMISSION_EDIT_MEETING_ID = 220;
	public static final int PERMISSION_ADD_MEETING_ID = 240;
	public static final int PERMISSION_CREATE_MEETING_ID = 270; // cust meeting
    public static final int PERMISSION_REMOVE_MEETING_ID = 280; // cust meeting
	public static final int PERMISSION_VIEW_TROOP_ID = 300;
	public static final int PERMISSION_EDIT_TROOP_ID = 301;
	public static final int PERMISSION_CAN_VIEW_OWN_CHILD_DETAIL_TROOP_ID=302;
	public static final int PERMISSION_CAN_VIEW_MEMBER_DETAIL_TROOP_ID=303;
	public static final int PERMISSION_EDIT_TROOP_IMG_ID = 304;	
	public static final int PERMISSION_VIEW_FINANCE_ID = 400;
	public static final int PERMISSION_EDIT_FINANCE_ID = 401;
	public static final int PERMISSION_EDIT_FINANCE_FORM_ID = 402;
	
	public static final int PERMISSION_CREATE_FINANCE = 403;
	public static final int PERMISSION_UPDATE_FINANCE = 404;
	public static final int PERMISSION_DELETE_FINANCE = 405;
	
	public static final int PERMISSION_EDIT_YEARPLAN_MEETING_ID = 510;
    public static final int PERMISSION_ADD_YEARPLAN_MEETING_ID = 511;
    public static final int PERMISSION_RM_YEARPLAN_MEETING_ID = 512;
	public static final int PERMISSION_TERMINATE_ID = 1000; // max int for
	public static final int PERMISSION_VIEW_REPORT_ID=601;
	public static final int PERMISSION_VIEW_MILESTONE_ID=611;
	public static final int PERMISSION_EDIT_ATTENDANCE_ID=621;
	public static final int PERMISSION_SEND_EMAIL_MT_ID=631;	
	public static final int PERMISSION_SEND_EMAIL_ACT_ID=641;
	public static final int PERMISSION_SEND_EMAIL_ALL_TROOP_PARENTS_ID=642; //on troop page send all parents email;used by DP
	public static final int PERMISSION_VIEW_ACTIVITY_PLAN_ID=651; //in meeting meterials
	public static final int PERMISSION_EDIT_MILESTONE_ID=701;
	public static final int GROUP_ROOT = 0;
	public static final String GROUP_ROOT_DISPLAY = "ROOT";
	public static final int[] ROOT_PERMISSIONS;
	static {
		ROOT_PERMISSIONS = new int[PERMISSION_TERMINATE_ID];
		for (int i = 0; i < PERMISSION_TERMINATE_ID; i++) {
			ROOT_PERMISSIONS[i] = i;
		}
	}
	//council admin
	public static final int GROUP_ADMIN = 1;
	public static final String GROUP_ADMIN_DISPLAY = "Admin";
	public static final int[] GROUP_ADMIN_PERMISSIONS = new int[] {  PERMISSION_VIEW_REPORT_ID,  PERMISSION_EDIT_FINANCE_FORM_ID, PERMISSION_EDIT_MILESTONE_ID
		};

	public static final int GROUP_GUEST = 2;
	public static final String GROUP_GUEST_DISPLAY = "Guest";
	public static final int[] GROUP_GUEST_PERMISSIONS = new int[] { PERMISSION_LOGIN_ID, };

	public static final int GROUP_LEADER = 11;
	public static final String GROUP_LEADER_DISPLAY = "Leader";
	public static final int[] GROUP_LEADER_PERMISSIONS = new int[] {
			PERMISSION_ADD_YEARPLAN_ID, PERMISSION_RM_YEARPLAN_ID,
			PERMISSION_ADD_ACTIVITY_ID, PERMISSION_EDIT_ACTIVITY_ID,
			PERMISSION_RM_ACTIVITY_ID,
			PERMISSION_EDIT_MEETING_ID,
			PERMISSION_VIEW_MILESTONE_ID,
			PERMISSION_ADD_MEETING_ID, PERMISSION_CREATE_MEETING_ID,
			PERMISSION_REMOVE_MEETING_ID,PERMISSION_SEND_EMAIL_MT_ID,
			PERMISSION_SEND_EMAIL_ACT_ID,PERMISSION_VIEW_ACTIVITY_PLAN_ID,
			PERMISSION_EDIT_ATTENDANCE_ID,
			PERMISSION_REMOVE_MEETING_ID,PERMISSION_EDIT_TROOP_IMG_ID,
			PERMISSION_CAN_VIEW_MEMBER_DETAIL_TROOP_ID
			,PERMISSION_VIEW_FINANCE_ID, PERMISSION_EDIT_FINANCE_ID,
			PERMISSION_SEND_EMAIL_ALL_TROOP_PARENTS_ID
	};

	public static final int GROUP_MEMBER_2G = 12;
	public static final String GROUP_MEMBER_2G_DISPLAY = "Member with 2 girls";
	public static final int[] GROUP_MEMBER_2G_PERMISSIONS = new int[] { };

	public static final int GROUP_MEMBER_1G = 13;
	public static final String GROUP_MEMBER_1G_DISPLAY = "Member with 1 girl";
	public static final int[] GROUP_MEMBER_1G_PERMISSIONS = new int[] {
		PERMISSION_VIEW_YEARPLAN_ID, PERMISSION_VIEW_MEETING_ID, PERMISSION_VIEW_TROOP_ID, 
		PERMISSION_CAN_VIEW_OWN_CHILD_DETAIL_TROOP_ID,
		PERMISSION_VIEW_FINANCE_ID
	};

	public static final int GROUP_MEMBER_NO_TROOP = 14;
	public static final String GROUP_MEMBER_NO_TROOP_DISPLAY = "Member with no troop Associations";
	public static final int[] GROUP_MEMBER_NO_TROOP_PERMISSIONS = new int[] { };

	public static final int GROUP_MEMBER_TROOP = 15;
	public static final String GROUP_MEMBER_TROOP_DISPLAY = "Member with troop Associations";
	public static final int[] GROUP_MEMBER_TROOP_PERMISSIONS = new int[] { };
	//no longer using this group for council admins
	public static final int GROUP_MEMBER_COUNCIL = 16;
	public static final String GROUP_MEMBER_COUNCIL_DISPLAY = "COUNCIL";

	public static final int[] GROUP_MEMBER_COUNCIL_PERMISSIONS = ArrayUtils.addAll( GROUP_LEADER_PERMISSIONS, new int[] { PERMISSION_CAN_VIEW_MEMBER_DETAIL_TROOP_ID, PERMISSION_EDIT_FINANCE_ID, PERMISSION_VIEW_REPORT_ID, PERMISSION_EDIT_MILESTONE_ID, PERMISSION_EDIT_FINANCE_FORM_ID ,
			PERMISSION_DELETE_FINANCE });

}
