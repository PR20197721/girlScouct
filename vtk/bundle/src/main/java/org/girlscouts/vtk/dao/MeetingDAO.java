package org.girlscouts.vtk.dao;

import org.girlscouts.vtk.models.*;
import org.girlscouts.vtk.utils.VtkException;

import java.util.Collection;
import java.util.List;
import java.util.Set;


public interface MeetingDAO {

    java.util.List<Meeting> getAllMeetings(User user, Troop troop, String gradeLevel)
            throws IllegalAccessException;

    java.util.List<MeetingE> getAllEventMeetings(User user, Troop troop,
                                                 String yearPlanId) throws IllegalAccessException;

    Meeting getMeeting(User user, Troop troop, String path)
            throws IllegalAccessException, VtkException;

    java.util.List<MeetingE> getAllEventMeetings_byPath(User user, Troop troop,
                                                        String yearPlanPath) throws IllegalAccessException;

    java.util.List<MeetingE> getAllUsersEventMeetings(User user,
                                                      Troop troop, String yearPlanId) throws IllegalStateException,
            IllegalAccessException;

    Meeting createCustomMeeting(User user, Troop troop,
                                MeetingE meetingEvent) throws IllegalAccessException;

    Meeting createCustomMeeting(User user, Troop troop,
                                MeetingE meetingEvent, Meeting meeting)
            throws IllegalAccessException;

    Meeting addActivity(User user, Troop troop, Meeting meeting,
                        Activity activity) throws IllegalAccessException;

    List<Meeting> search();

    List<org.girlscouts.vtk.models.Search> getData(User user,
                                                   Troop troop, String query) throws IllegalAccessException;

    SearchTag searchA(User user, Troop troop, String councilCode)
            throws IllegalAccessException;

    java.util.List<Activity> searchA1(User user, Troop troop,
                                      String lvl, String cat, String keywrd, java.util.Date startDate,
                                      java.util.Date endDate, String region)
            throws IllegalAccessException;

    List<Asset> getAllResources(User user, Troop troop, String path)
            throws IllegalAccessException;

    Asset getAsset(User user, Troop troop, String _path)
            throws IllegalAccessException;

    java.util.List<Asset> getGlobalResources(String resourceTags); // delim
    // ';'

    Meeting updateCustomMeeting(User user, Troop troop,
                                MeetingE meetingEvent, Meeting meeting)
            throws IllegalAccessException;

    Council getCouncil(User user, Troop troop, String councilId)
            throws IllegalAccessException;

    java.util.List<Milestone> getCouncilMilestones(String councilCode);

    void saveCouncilMilestones(java.util.List<Milestone> milestones);

    String removeLocation(User user, Troop troop, String locationName)
            throws IllegalAccessException;

    List<Asset> getAidTag_local(User user, Troop troop, String tags,
                                String meetingName, String meetingPath) throws IllegalAccessException;

    List<Asset> getAidTag(User user, Troop troop, String tags, String meetingName)
            throws IllegalAccessException;

    List<Asset> getResource_local(User user, Troop troop,
                                  String meetingName, String meetingPath) throws IllegalAccessException;

    List<Asset> getResource_global(User user, Troop troop, String tags,
                                   String meetingName) throws IllegalAccessException;

    Attendance getAttendance(User user, Troop troop, String mid);

    boolean setAttendance(User user, Troop troop, String mid,
                          Attendance attendance);

    Achievement getAchievement(User user, Troop troop, String mid);

    boolean setAchievement(User user, Troop troop, String mid,
                           Achievement a);

    boolean updateMeetingEvent(User user, Troop troop, MeetingE meeting)
            throws IllegalAccessException, IllegalStateException;

    MeetingE getMeetingE(User user, Troop troop, String path)
            throws IllegalAccessException, VtkException;

    int getAllResourcesCount(User user, Troop troop, String path)
            throws IllegalAccessException;

    int getAssetCount(User user, Troop troop, String _path)
            throws IllegalAccessException;

    int getCountLocalMeetingAidsByLevel(User user, Troop troop, String _path)
            throws IllegalAccessException;

    Collection<bean_resource> getResourceData(User user, Troop troop, String _path)
            throws IllegalAccessException;

    int getMeetingCount(User user, Troop troop, String path) throws IllegalAccessException;

    int getVtkAssetCount(User user, Troop troop, String path) throws IllegalAccessException;

    java.util.List<Meeting> getAllMeetings(User user, Troop troop) throws IllegalAccessException;

    java.util.List<Note> getNotes(User user, Troop troop, String path)
            throws IllegalAccessException, VtkException;

    boolean updateNote(User user, Troop troop, Note note) throws IllegalAccessException;

    boolean rmNote(User user, Troop troop, Note note) throws IllegalAccessException;

    boolean rmNote(User user, Troop troop, String noteId) throws IllegalAccessException;

    Note getNote(User user, Troop troop, String nid)
            throws IllegalAccessException;

    Set<String> getOutdoorMeetings(User user, Troop troop) throws IllegalAccessException;

    Set<String> getGlobalMeetings(User user, Troop troop) throws IllegalAccessException;

    List<Meeting> getMeetings(User user, Troop troop, String level) throws IllegalAccessException;

    boolean removeAttendance(User user, Troop troop,
                             Attendance attendance);

    boolean removeAchievement(User user, Troop troop,
                              Achievement achievement);


}
