package org.girlscouts.vtk.ejb;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.dao.ContactDAO;
import org.girlscouts.vtk.models.Achievement;
import org.girlscouts.vtk.models.Attendance;
import org.girlscouts.vtk.models.Contact;
import org.girlscouts.vtk.models.ContactExtras;
import org.girlscouts.vtk.models.Meeting;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.models.YearPlanComponent;

@Component
@Service(value = ContactUtil.class)
public class ContactUtil {

	@Reference
	ContactDAO contactDAO;

	@Reference
	MeetingUtil meetingUtil;
	
	@Reference
	YearPlanUtil yearPlanUtil;
	
	public void saveContact(User user, Troop troop, Contact contact)
			throws IllegalStateException, IllegalAccessException {
		contactDAO.save(user, troop, contact);
	}

	public Contact getContact(User user, Troop troop, String contactId)
			throws IllegalStateException, IllegalAccessException {
		return contactDAO.retreive(user, troop, contactId);
	}
	
	
   public java.util.List<ContactExtras> girlAttendAchievement(User user, Troop troop, Contact contact){
	
	   if( contact==null ) return null;
	   java.util.List<ContactExtras> extras = new java.util.ArrayList<ContactExtras>();
	   
	   for(int i=0;i<troop.getYearPlan().getMeetingEvents().size();i++){
		   
		    MeetingE meeting = troop.getYearPlan().getMeetingEvents().get(i);
		    ContactExtras extra = new ContactExtras();
			Attendance attendance = meetingUtil.getAttendance(user, troop, meeting.getPath() + "/attendance");
		    Achievement achievement = meetingUtil.getAchievement(user, troop, meeting.getPath() + "/achievement"); 
		    
		    String attendance_users = "", achievement_users= "";
		    if( attendance!=null && attendance.getUsers()!=null && !attendance.getUsers().equals("") )
		    	attendance_users = "," +attendance.getUsers() +","; 	
		    if( achievement!=null && achievement.getUsers()!=null && !achievement.getUsers().equals("") )
		    	achievement_users = "," +achievement.getUsers() +","; 
		    
		    if( attendance_users.contains( "," + contact.getId() + "," ) )	    	
		    	extra.setAttended(true);
		    if( achievement_users.contains( "," +  contact.getId() + "," ) )	    	
		    	extra.setAchievement(true);
		    
		    
		    if( extra.isAttended() || extra.isAchievement() ){
		    	if( meeting.getMeetingInfo()==null ){
		    		try{ 
		    			meeting.setMeetingInfo(  yearPlanUtil.getMeeting(user,
							meeting.getRefId()) );
		    		}catch(Exception e){e.printStackTrace();}
		    	}
		    	extra.setYearPlanComponent(meeting);
		    	extras.add(extra);
		    }
		    
	   }//edn for i  
	   return extras;
  }
}
