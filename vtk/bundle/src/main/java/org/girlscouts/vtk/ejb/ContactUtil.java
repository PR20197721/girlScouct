package org.girlscouts.vtk.ejb;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.dao.ContactDAO;
import org.girlscouts.vtk.models.Achievement;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Attendance;
import org.girlscouts.vtk.models.Contact;
import org.girlscouts.vtk.models.ContactExtras;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;

@Component
@Service(value = ContactUtil.class)
public class ContactUtil { // utils should probably be in a separate util folder

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
	
	public java.util.Map<Contact, java.util.List<ContactExtras>> getContactsExtras(User user, Troop troop, java.util.List <Contact> contacts){
		java.util.Map<Contact, java.util.List<ContactExtras>> contactsExtras = new java.util.TreeMap<Contact, java.util.List<ContactExtras>>();
		
		for( Contact contact: contacts){

			contactsExtras.put(contact, girlAttendAchievement( user,  troop,  contact));
		}
		return contactsExtras;
	}
	
   public java.util.List<ContactExtras> girlAttendAchievement(User user, Troop troop, Contact contact){
	
	   if( contact==null ) return null;
	   java.util.List<ContactExtras> extras = new java.util.ArrayList<ContactExtras>();
	   if( troop!=null && troop.getYearPlan()!=null && troop.getYearPlan().getMeetingEvents()!=null )
	     for(int i=0;i<troop.getYearPlan().getMeetingEvents().size();i++){   
		    MeetingE meeting = troop.getYearPlan().getMeetingEvents().get(i);
		    ContactExtras extra = new ContactExtras();
		    Attendance attendance = meeting.getAttendance();
		    Achievement achievement = meeting.getAchievement();
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
		    			meeting.setMeetingInfo(  yearPlanUtil.getMeeting(user, troop, meeting.getRefId()) );
		    		}catch(Exception e){e.printStackTrace();}
		    	}
		    	extra.setYearPlanComponent(meeting);
		    	extras.add(extra);
		    }
	   } 
	   
	   if( troop!=null && troop.getYearPlan()!=null && troop.getYearPlan().getActivities()!=null && troop.getYearPlan().getActivities().size()>0)
		   extras.addAll( getActivityAttendance(troop.getYearPlan().getActivities(), contact) );
	   return extras;
  }
   
   public  java.util.List<ContactExtras> getActivityAttendance( java.util.List<Activity> activities, Contact contact){
	   
	   java.util.List<ContactExtras> extras = new java.util.ArrayList<ContactExtras>();
	   for(Activity activity: activities){   
		    ContactExtras extra = new ContactExtras();
		    Attendance attendance = activity.getAttendance();
	    
		    String attendance_users = "";
		    if( attendance!=null && attendance.getUsers()!=null && !attendance.getUsers().equals("") )
		    	attendance_users = "," +attendance.getUsers() +","; 	
		    
		    if( attendance_users.contains( "," + contact.getId() + "," ) )	    	
		    	extra.setAttended(true);
		    
		    if( extra.isAttended() ){
		    	extra.setYearPlanComponent(activity);
		    	extras.add(extra);
		    }
		
	   }
	   return extras;
   }
}
