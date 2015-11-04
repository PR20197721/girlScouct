<% 
boolean isLoadMeetingInfo=false;
YearPlan plan =troop.getYearPlan();

if (plan.getSchedule() != null || plan.getActivities() == null
            || plan.getActivities().size() <= 0) {

        // set meetingInfos if isLoadMeetingInfo
        if (isLoadMeetingInfo) {

            java.util.List<MeetingE> meetingEs = plan.getMeetingEvents();
            if( meetingEs!=null)
             for (int i = 0; i < meetingEs.size(); i++) {
                MeetingE meetingE = meetingEs.get(i);
                Meeting meetingInfo = yearPlanUtil.getMeeting(user,meetingE.getRefId());
                meetingE.setMeetingInfo(meetingInfo);
            }
            plan.setMeetingEvents(meetingEs);
            
            
            //load meetingCanceled
            if( plan.getMeetingCanceled()!=null )
             for (int i = 0; i < plan.getMeetingCanceled().size(); i++) {
                MeetingCanceled meetingCanceled = plan.getMeetingCanceled().get(i);
                Meeting meetingInfo = yearPlanUtil.getMeeting(user,
                        meetingCanceled.getRefId());
                meetingCanceled.setMeetingInfo(meetingInfo);
            }
           
        }

        meetingUtil.getYearPlanSched(plan);
    }
//if(true)return;
    // if no sched and activ -> activ on top
    java.util.Map orgSched = meetingUtil.getYearPlanSched(plan);
    java.util.Map container = new LinkedHashMap();
    java.util.Iterator itr = orgSched.keySet().iterator();
    while (itr.hasNext()) {
        java.util.Date date = (java.util.Date) itr.next();
        YearPlanComponent _comp = (YearPlanComponent) orgSched.get(date);

        switch (_comp.getType()) {
        case ACTIVITY:
            Activity activity = (Activity) _comp;
            container.put(date, activity);
            break;
    
        }
    }

    // now set meetings & etc
    itr = orgSched.keySet().iterator();
    boolean heal=false;
    while (itr.hasNext()) {
        java.util.Date date = (java.util.Date) itr.next();
        YearPlanComponent _comp = (YearPlanComponent) orgSched.get(date);

        switch (_comp.getType()) {
        case MEETINGCANCELED:
            MeetingCanceled meetingCanceled = (MeetingCanceled) _comp;
            container.put(date, meetingCanceled);
            break;
        case MEETING:

            MeetingE meetingE = (MeetingE) _comp;
            if (isLoadMeetingInfo) {
                Meeting meetingInfo = yearPlanUtil.getMeeting(user,
                        meetingE.getRefId());

                meetingE.setMeetingInfo(meetingInfo);
            }
            
            int maxLook=0;
            while (container.containsKey(date)) {
                date = new Date(date.getTime() + 5l);
                heal = true;
                maxLook++;
                if(maxLook>100) break;
            }
            
            container.put(date, meetingE);
            
            break;
        case MILESTONE:
            Milestone milestone = (Milestone) _comp;
            container.put(date, milestone);
            break;

        }
    }

    %>