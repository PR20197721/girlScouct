<%@ page import="org.apache.commons.lang3.StringEscapeUtils,
                 org.girlscouts.vtk.osgi.component.dao.YearPlanComponentType,
                 org.girlscouts.vtk.models.Contact,
                 org.girlscouts.vtk.models.ContactExtras,
                 org.girlscouts.vtk.models.Meeting"
%>
<%@ page import="org.girlscouts.vtk.models.MeetingE" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Map, java.text.SimpleDateFormat, org.girlscouts.vtk.auth.permission.Permission" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="session.jsp" %>
<%
    SimpleDateFormat FORMAT_MMM_dd_yyyy = new SimpleDateFormat("MMM dd yyyy");
    response.setHeader("Content-Encoding", "UTF-8");
    response.setContentType("text/csv; charset=UTF-8");
    response.setHeader("Content-Disposition","attachment; filename=TroopRoster.csv");

    StringBuilder csv= new StringBuilder();
    List<Contact> contacts = (List<Contact>) session.getAttribute("vtk_cachable_contacts");
    Map<Contact, List<ContactExtras>> contactsExtras= contactUtil.getContactsExtras( user,  selectedTroop, contacts);
    List <MeetingE> meetingEvents= null;
    if( selectedTroop!=null && selectedTroop.getYearPlan()!=null) meetingEvents = selectedTroop.getYearPlan().getMeetingEvents();
    csv.append("Legend:  X = attendance; A = achievement\n");
    // doc title
    csv.append(FORMAT_MMM_dd_yyyy.format( new Date() ) +" "+
            (selectedTroop.getSfTroopAge().substring( selectedTroop.getSfTroopAge().indexOf("-") +1) )+"  "+selectedTroop.getSfTroopName()
    );

    //doc header
    csv.append("\nGirl Scout, Parent Guardian, Parent Email, Parent Phone, DOB, Age, Address, Secondary Info Name, Secondary Info Email,");
    if(meetingEvents!=null)
        for( MeetingE meetingEvent: meetingEvents ){
            if(meetingEvent.getMeetingInfo()==null ){
                Meeting meetingInfo = yearPlanUtil.getMeeting(user, selectedTroop, meetingEvent.getRefId());
                csv.append( fmtValue(meetingInfo.getName()) +",");
            }else{
                csv.append( fmtValue( meetingEvent.getMeetingInfo().getName()) +",");
            }
        }

    //Girl info
    if( contacts!=null)
        for (Contact gsContact : contacts) {

            if( ! "Girl".equals( gsContact.getRole() ) ) continue;


            //check permission again:must be TL
            if(!(VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_CAN_VIEW_MEMBER_DETAIL_TROOP_ID) || apiConfig == null ||  VtkUtil.isUserCaregiverForContact(user, gsContact) ) ){ continue; }
            PrimaryGuardian caregiver = gsContact.getPrimaryGuardian();
            if(caregiver != null) {
                csv.append("\n");
                //age
                String age = "" + gsContact.getAge();
                age = (age == null || age.equals("null")) ? "" : fmtValue(age);
                //dob
                String dob = "";
                if (gsContact.getDob() != null) {
                    try {
                        dob = VtkUtil.formatDate(VtkUtil.FORMAT_MMddYYYY, VtkUtil.parseDate(VtkUtil.FORMAT_yyyyMMdd, gsContact.getDob()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                csv.append(fmtValue((gsContact.getFirstName() + " " + gsContact.getRole())) + ",");
                csv.append(fmtValue((caregiver == null ? "" : (caregiver.getFirstName() == null ? "" : caregiver.getFirstName())) + " " + ((caregiver.getLastName() == null ? "" : caregiver.getLastName()))) + ",");
                csv.append(fmtValue(gsContact.getEmail()) + "," + (gsContact.getPhone() == null ? "" : fmtValue(gsContact.getPhone())) + "," + fmtValue(dob) + "," + fmtValue(age) + ",");
                //address
                String address = (gsContact.getAddress() == null ? "" : gsContact.getAddress()) + " " + (gsContact.getCity() == null ? "" : gsContact.getCity()) + " " + (gsContact.getState() == null ? "" : (", " + gsContact.getState())) + " " + (gsContact.getZip() == null ? "" : gsContact.getZip());
                csv.append(fmtValue(address) + ",");
                // secondary contact
                if (gsContact.getContacts() != null) {
                    for (Contact contactSub : gsContact.getContacts()) {
                        if (!VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_CAN_VIEW_OWN_CHILD_DETAIL_TROOP_ID)) {
                            csv.append(fmtValue(contactSub.getFirstName() + " " + contactSub.getLastName()) + ",");
                            if (VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_SEND_EMAIL_ALL_TROOP_PARENTS_ID)) {
                                csv.append(fmtValue(contactSub.getEmail()) + ",");
                            } else {
                                csv.append(",");
                            }
                        } else {
                            csv.append(",");
                        }

                    }
                } else {
                    csv.append(",,");
                }
                // meetings/ girl
                if (meetingEvents != null) {
                    List<ContactExtras> infos = contactsExtras.get(gsContact);
                    for (MeetingE meetingEvent : meetingEvents) {
                        boolean isAttended = false, isAchievement = false;
                        for (ContactExtras info : infos) {
                            if (!meetingEvent.getUid().equals(info.getYearPlanComponent().getUid()))
                                continue;
                            //attendance
                            if (info.isAttended()) {
                                isAttended = true;
                            }
                            //acvm
                            if (info.isAchievement() && info.getYearPlanComponent().getType() == YearPlanComponentType.MEETING) {
                                isAchievement = true;
                            }
                        }
                        csv.append(isAttended ? "X" : ""); //attended
                        csv.append(isAchievement ? "A" : ""); //achvm
                        csv.append(",");
                    }
                }
            }
        }//edn contacts
    out.println(csv.toString().trim());
%>
<%!
    private String fmtValue(String value) {
        return value == null ? "" : StringEscapeUtils.escapeCsv(value);
    }
%>