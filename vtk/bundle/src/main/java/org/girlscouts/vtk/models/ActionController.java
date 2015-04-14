package org.girlscouts.vtk.models;

public enum ActionController {
	ChangeMeetingPositions, CreateActivity, CreateSchedule, SelectYearPlan, AddLocation, RemoveLocation, CreateCustomAgenda, SetLocationAllMeetings, UpdateSched, RemoveCustomActivity, ChangeLocation, SwapMeetings, AddMeeting, RearrangeActivity, RemoveAgenda, EditAgendaDuration, RevertAgenda, ReLogin, AddAid, RemoveAsset, BindAssetToYPC, EditCustActivity, Search, CreateCustomActivity, isAltered, GetFinances, UpdateFinances, RmMeeting, UpdAttendance, UpdateFinanceAdmin
, CreateCustomYearPlan
	/*
	 * ActionController(int x){state=x;} private int state;
	 * 
	 * 
	 * public int getAction() { return this.state; }
	 * 
	 * public static ActionController getStatusFor(int desired) { for
	 * (ActionController status : values()) { if (desired == status.state) {
	 * return status; }
	 * 
	 * } return null; }
	 */

}
