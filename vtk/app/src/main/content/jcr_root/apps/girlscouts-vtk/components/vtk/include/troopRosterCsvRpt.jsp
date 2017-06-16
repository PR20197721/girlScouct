<%@ page import="
    com.day.text.csv.Csv,
 	org.girlscouts.vtk.ejb.*,
    java.util.*,
	org.girlscouts.vtk.models.*,
	org.girlscouts.vtk.dao.*,
	java.io.StringWriter"
%>

<%@include file="/libs/foundation/global.jsp" %>
<%@include file="session.jsp"%>
<%

	SimpleDateFormat FORMAT_MMM_dd_yyyy = new SimpleDateFormat("MMM dd yyyy");
    response.setHeader("Content-Encoding", "UTF-8");
    response.setContentType("text/csv; charset=UTF-8");
    response.setHeader("Content-Disposition","inline; filename=TroopRoster.csv");


	StringWriter writer = new StringWriter();
	Csv csvWriter = new Csv();
	csvWriter.writeInit(writer);

	List<Contact> contacts = (List<Contact>) session.getAttribute("vtk_cachable_contacts");
    java.util.Map<Contact, java.util.List<ContactExtras>> contactsExtras= contactUtil.getContactsExtras( user,  troop, contacts);
	java.util.List <MeetingE> meetingEvents= troop.getYearPlan().getMeetingEvents();

	// doc title
	csvWriter.writeRow( (troop.getSfTroopAge().substring( troop.getSfTroopAge().indexOf("-") +1) )+"  "+troop.getSfTroopName() +
        ","+(FORMAT_MMM_dd_yyyy.format( new java.util.Date() ) )  +
        ","+(contacts==null ? "" : contacts.size() ) + " Girls"
     );

	StringBuffer row= new StringBuffer();

	//doc header
	row.append("Girl Scout, Parent Guardian, Parent Phone, DOB, Age, Address, Secondary Info Name, Secondary Info Email");
	if(meetingEvents!=null)
		for( MeetingE meetingEvent: meetingEvents ){
			row.append(meetingEvent.getMeetingInfo().getName());
	    }
	csvWriter.writeRow( row.toString() );

	//Girl info

    if( contacts!=null)
            for (Contact gsContact : contacts) {
				row = new StringBuffer();
                if( ! "Girl".equals( gsContact.getRole() ) ) continue;
                 Contact caregiver = VtkUtil.getSubContact( gsContact, 1);
        
                //check permission again:must be TL
                if(!(VtkUtil.hasPermission(troop, Permission.PERMISSION_CAN_VIEW_MEMBER_DETAIL_TROOP_ID) ||
                         user.getApiConfig()==null || user.getApiConfig().getUser().getContactId().equals(caregiver.getContactId() ) ) ){ continue; }

                row.append( ( gsContact.getFirstName() +" "+ gsContact.getRole()) +","+
                    ( caregiver==null ? "" : (caregiver.getFirstName()==null ? "" : caregiver.getFirstName()) ) 
                    	+" "+ ((caregiver.getLastName() ==null ? "" :caregiver.getLastName()  ) )+","+
                    gsContact.getEmail() +","+
                    (gsContact.getPhone() ==null ? "" : gsContact.getPhone())
                           +","
                );


        		//dob
        		String dob="";
                if( gsContact.getDob() != null ){
                	try{
                    	dob= VtkUtil.formatDate(VtkUtil.FORMAT_MMddYYYY, VtkUtil.parseDate(VtkUtil.FORMAT_yyyyMMdd,gsContact.getDob()) ) ;
                    }catch(Exception e){e.printStackTrace();}         
                }
				row.append(dob +",");

        		//age
        		String age = ""+ gsContact.getAge() ;
				row.append(age +",");

        		//address
         		String address = ( gsContact.getAddress()==null ? "" : gsContact.getAddress() )+
                    	( gsContact.getCity()==null ? "" : gsContact.getCity() ) +
                        ( gsContact.getState()==null ? "" : (", "+gsContact.getState()) )+
                    	( gsContact.getZip()==null ? "" : gsContact.getZip() );
				row.append(address +",");

        		// secondary contact
        		if( gsContact.getContacts()!=null )
      			  for(Contact contactSub: gsContact.getContacts()){ 
                   if( !VtkUtil.hasPermission(troop, Permission.PERMISSION_CAN_VIEW_OWN_CHILD_DETAIL_TROOP_ID ) ){
                       row.append( contactSub.getFirstName() +",");
                       row.append( contactSub.getLastName() +",");
                 	   if( VtkUtil.hasPermission(troop, Permission.PERMISSION_SEND_EMAIL_ALL_TROOP_PARENTS_ID) ){ 
                         row.append( contactSub.getEmail() +",");
                         row.append( contactSub.getEmail() +",");
                	   } 
                       row.append( ( contactSub.getPhone()==null ? "" : contactSub.getPhone() ) +"," );
                  }
                }


		        // meetings/ girl
                if(meetingEvents!=null){
			     java.util.List<ContactExtras> infos = contactsExtras.get( gsContact );
                 for( MeetingE meetingEvent: meetingEvents ){


                    boolean isAttended= false, isAch= false;
					for(int y=0;y<infos.size();y++) {

                          //attendance
               			  if(infos.get(y).isAttended()) {
                              isAttended= true; //VtkUtil.formatDate(VtkUtil.FORMAT_Md,(java.util.Date)sched_bm_inverse.get( infos.get(y).getYearPlanComponent()))
                          }

                          //acvm 
                          if(infos.get(y).isAchievement() && infos.get(y).getYearPlanComponent().getType()== YearPlanComponentType.MEETING) {
                            isAch= true;
                          }
                    }
                    row.append(isAttended ? "ATD," : ","); //attended
                    row.append(isAch ? "ACV," : ","); //achvm
                 }
    		}	

            csvWriter.writeRow( row.toString() );


       }//edn contacts


	csvWriter.close();

	writer.close();
	String csvContents = writer.toString();
    out.println(csvContents);




%>