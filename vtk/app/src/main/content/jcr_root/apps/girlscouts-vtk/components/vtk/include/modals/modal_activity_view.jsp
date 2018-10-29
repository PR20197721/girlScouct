		<%
    			List<Activity> activities = meetingInfo.getActivities();
				Collections.sort(activities, new Comparator<Activity>() {
					public int compare(Activity activity1, Activity activity2) {
						return activity1.getActivityNumber() - activity2.getActivityNumber();
					}
				});

				StringBuilder builder = new StringBuilder();
				for (Activity __activity : activities) {

					String description = __activity.getActivityDescription();


					java.util.List<Activity> subActivities = __activity.getMultiactivities();
					Activity selectedActivity = VtkUtil.findSelectedActivity( subActivities );
                    if( selectedActivity ==null && (subActivities!=null &&  subActivities.size()>1)){
                      
                            builder.append("<p style=\"color:red; font-size:18px; font-weight:bolder;\"><b>Activity "+ __activity.getActivityNumber()+" : Select Your Activity </b></p>");
                		
                    }else{
                    	selectedActivity = selectedActivity==null ? subActivities.get(0) : selectedActivity;
						builder.append("<p style=\"font-size:18px; font-weight:bold;\"><b>Activity " + __activity.getActivityNumber() ); 
                		if(subActivities.size()!=1 ){
                			builder.append(" - Choice "+ selectedActivity.getActivityNumber() );
                		}
                		builder.append(": ");
                		builder.append( subActivities.size()==1 ? __activity.getName() : selectedActivity.getName()) ;
                		builder.append("</b></p>");
                		builder.append("<p  style=\"font-size:18px; font-weight:bold;\">"+selectedActivity.getActivityDescription()+"</p>");

                    }
				}
		%>
		<div class="editable-textarea column small-20 small-centered" id="editMeetingActivity"><%=builder.toString()%></div>