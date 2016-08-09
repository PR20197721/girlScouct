package org.girlscouts.vtk.models;

public class ContactExtras {

		private boolean attended, achievement;
		private YearPlanComponent yearPlanComponent;
		
		
		public boolean isAttended() {
			return attended;
		}
		public void setAttended(boolean attended) {
			this.attended = attended;
		}
		public boolean isAchievement() {
			return achievement;
		}
		public void setAchievement(boolean achievement) {
			this.achievement = achievement;
		}
		public YearPlanComponent getYearPlanComponent() {
			return yearPlanComponent;
		}
		public void setYearPlanComponent(YearPlanComponent yearPlanComponent) {
			this.yearPlanComponent = yearPlanComponent;
		}
	
}
