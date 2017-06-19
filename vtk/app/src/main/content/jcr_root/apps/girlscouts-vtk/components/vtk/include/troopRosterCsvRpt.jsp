import org.girlscouts.vtk.models.Meeting;

<%@ page import="org.girlscouts.vtk.ejb.*,
    java.util.*,
	org.girlscouts.vtk.models.*,
	org.girlscouts.vtk.dao.*"
%>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="session.jsp"%>
<%
	SimpleDateFormat FORMAT_MMM_dd_yyyy = new SimpleDateFormat("MMM dd yyyy");
    response.setHeader("Content-Encoding", "UTF-8");
    response.setContentType("application/vnd.ms-excel");
    response.setHeader("Content-Disposition","attachment; filename=TroopRoster.csv");
    
    StringBuilder csv= new StringBuilder();
	List<Contact> contacts = (List<Contact>) session.getAttribute("vtk_cachable_contacts");
    java.util.Map<Contact, java.util.List<ContactExtras>> contactsExtras= contactUtil.getContactsExtras( user,  troop, contacts);
	java.util.List <MeetingE> meetingEvents= troop.getYearPlan().getMeetingEvents();
    
	// doc title
	csv.append(
			FORMAT_MMM_dd_yyyy.format( new java.util.Date() ) +" "+
			(troop.getSfTroopAge().substring( troop.getSfTroopAge().indexOf("-") +1) )+"  "+troop.getSfTroopName()
     );

	//doc header
	csv.append("\nGirl Scout, Parent Guardian, Parent Email, Parent Phone, DOB, Age, Address, Secondary Info Name, Secondary Info Email,");
	if(meetingEvents!=null)
		for( MeetingE meetingEvent: meetingEvents ){
			if(meetingEvent.getMeetingInfo()==null ){
				Meeting meetingInfo = yearPlanUtil.getMeeting(user, troop, meetingEvent.getRefId());
				csv.append(meetingInfo.getName() +",");
			}else{
				csv.append(meetingEvent.getMeetingInfo().getName() +",");
			}
	    }

	//Girl info
    if( contacts!=null)
            for (Contact gsContact : contacts) {
            	
                if( ! "Girl".equals( gsContact.getRole() ) ) continue;
                 Contact caregiver = VtkUtil.getSubContact( gsContact, 1);
        
                //check permission again:must be TL
                if(!(VtkUtil.hasPermission(troop, Permission.PERMISSION_CAN_VIEW_MEMBER_DETAIL_TROOP_ID) ||
                         user.getApiConfig()==null || user.getApiConfig().getUser().getContactId().equals(caregiver.getContactId() ) ) ){ continue; }
                
                csv.append("\n");
                
                //age
                String age = ""+ gsContact.getAge() ;
                age = (age ==null || age.equals("null")) ? "" : age;
                
                //dob
        		String dob="";
                if( gsContact.getDob() != null ){
                	try{
                    	dob= VtkUtil.formatDate(VtkUtil.FORMAT_MMddYYYY, VtkUtil.parseDate(VtkUtil.FORMAT_yyyyMMdd,gsContact.getDob()) ) ;
                    }catch(Exception e){e.printStackTrace();}         
                }
                
                csv.append( ( gsContact.getFirstName() +" "+ gsContact.getRole()) +","+
                    ( caregiver==null ? "" : (caregiver.getFirstName()==null ? "" : caregiver.getFirstName()) ) 
                    	+" "+ ((caregiver.getLastName() ==null ? "" :caregiver.getLastName()  ) )+","+
                    	gsContact.getEmail()+","+
                    (gsContact.getPhone() ==null ? "" : gsContact.getPhone())+","+
                    dob +","+
					age +","
					
                );
        		//address
         		String address = ( gsContact.getAddress()==null ? "" : gsContact.getAddress() )+ " "+
                    	( gsContact.getCity()==null ? "" : gsContact.getCity() ) + " "+
                        ( gsContact.getState()==null ? "" : (", "+gsContact.getState()) )+ " "+
                    	( gsContact.getZip()==null ? "" : gsContact.getZip() );
         		csv.append(address.replace(",","") +",");

        		// secondary contact
        		if( gsContact.getContacts()!=null )
      			  for(Contact contactSub: gsContact.getContacts()){ 
                   if( !VtkUtil.hasPermission(troop, Permission.PERMISSION_CAN_VIEW_OWN_CHILD_DETAIL_TROOP_ID ) ){
                       csv.append( contactSub.getFirstName() +" ");
                       csv.append( contactSub.getLastName() +",");
                 	   if( VtkUtil.hasPermission(troop, Permission.PERMISSION_SEND_EMAIL_ALL_TROOP_PARENTS_ID) ){ 
                         csv.append( contactSub.getEmail() +",");
                	   } 
                  }
                }


		        // meetings/ girl
                if(meetingEvents!=null){
			     java.util.List<ContactExtras> infos = contactsExtras.get( gsContact );
                 for( MeetingE meetingEvent: meetingEvents ){


                    boolean isAttended= false, isAch= false;
					for(int y=0;y<infos.size();y++) {
						  if( !meetingEvent.getUid().equals(infos.get(y).getYearPlanComponent().getUid()) ) continue;

                          //attendance
               			  if(infos.get(y).isAttended()) {
                              isAttended= true; 
                          }

                          //acvm 
                          if(infos.get(y).isAchievement() && infos.get(y).getYearPlanComponent().getType()== YearPlanComponentType.MEETING) {
                            isAch= true;
                          }
                    }
                    csv.append(isAttended ? "X" : ""); //attended
                    csv.append(isAch ? "A" : ""); //achvm
                    csv.append(","); 
                 }
    		}	
       }//edn contacts
       out.println(csv.toString().trim());
%>