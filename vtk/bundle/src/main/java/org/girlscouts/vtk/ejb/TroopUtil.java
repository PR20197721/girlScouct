package org.girlscouts.vtk.ejb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import javax.jcr.Session;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.manager.impl.ObjectContentManagerImpl;
import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.AnnotationMapperImpl;
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.dao.ActivityDAO;
import org.girlscouts.vtk.dao.CouncilDAO;
import org.girlscouts.vtk.dao.MeetingDAO;
import org.girlscouts.vtk.dao.TroopDAO;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Asset;
import org.girlscouts.vtk.models.Cal;
import org.girlscouts.vtk.models.Council;
import org.girlscouts.vtk.models.JcrCollectionHoldString;
import org.girlscouts.vtk.models.Location;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.Milestone;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.UserGlobConfig;
import org.girlscouts.vtk.models.YearPlan;

@Component
@Service(value = TroopUtil.class)
public class TroopUtil {

	@Reference
	TroopDAO troopDAO;

	@Reference
	ActivityDAO activityDAO;

	@Reference
	MeetingDAO meetingDAO;

	@Reference
	CouncilDAO councilDAO;

	public Troop getTroop(String councilId, String troopId) {

		Troop troop = null;
		troop = troopDAO.getTroop(councilId, troopId);
		if (troop == null)
			return troop;
		if (troop != null && troop.getYearPlan() != null
				&& troop.getYearPlan().getMeetingEvents() != null) {

			Comparator<MeetingE> comp = new BeanComparator("id");
			Collections.sort(troop.getYearPlan().getMeetingEvents(), comp);
		}

		if (troop.getYearPlan() == null)
			return troop;
		java.util.List<Activity> activities = troop.getYearPlan()
				.getActivities();
		if (activities != null)
			for (int i = 0; i < activities.size(); i++) {
				if ((activities.get(i).getCancelled() == null || activities
						.get(i).getCancelled().equals("false"))
						&& !activities.get(i).getIsEditable()
						&& activities.get(i).getRefUid() != null) {

					Activity a = activityDAO.findActivity(activities.get(i)
							.getRefUid());
					if (a == null)
						activities.get(i).setCancelled("true");
					else
						activities.set(i, a);

				}
			}
		return troop;
	}

	public boolean isUpdated(Troop troop) {
		java.util.Date lastUpdate = meetingDAO.getLastModif(troop);
		if (lastUpdate != null && troop.getRetrieveTime().before(lastUpdate)) {
			troop.setRefresh(true);
			return true;
		}
		return false;
	}

	public void autoLogin(HttpSession session) {
		org.girlscouts.vtk.auth.models.ApiConfig config = new org.girlscouts.vtk.auth.models.ApiConfig();
		config.setId("test");
		config.setAccessToken("test");
		config.setInstanceUrl("test");
		config.setUserId("test_user");
		config.setUser(new org.girlscouts.vtk.auth.models.User());

		java.util.List<org.girlscouts.vtk.salesforce.Troop> troops = new java.util.ArrayList();
		org.girlscouts.vtk.salesforce.Troop troop = new org.girlscouts.vtk.salesforce.Troop();
		troop.setCouncilCode(1);
		troop.setGradeLevel("1-Brownie");
		troop.setTroopId("test_troop_id");
		troop.setCouncilId("123");
		troop.setTroopName("test");

		troops.add(troop);
		config.setTroops(troops);

		session.setAttribute(
				org.girlscouts.vtk.auth.models.ApiConfig.class.getName(),
				config);
	}

	public Troop createTroop(String councilId, String troopId) {

		Troop troop = null;
		Council council = councilDAO.getOrCreateCouncil(councilId);
		if (council == null)
			return null;

		java.util.List<Troop> troops = council.getTroops();
		if (troops == null)
			troops = new java.util.ArrayList<Troop>();
		troop = new Troop(troopId);
		troops.add(troop);
		council.setTroops(troops);

		councilDAO.updateCouncil(council);
		return troop;
	}

	public void logout(Troop troop) throws java.lang.IllegalAccessException {
		if (troop == null)
			return;
		Troop tmp_troop = troopDAO.getTroop_byPath(troop.getPath());
		if (tmp_troop == null)
			return;
		tmp_troop.setCurrentTroop(null);
		troopDAO.updateTroop(tmp_troop);
	}

	public void addAsset(Troop troop, String meetingUid, Asset asset)
			throws java.lang.IllegalAccessException,
			java.lang.IllegalStateException {

		// permission to update
		if (troop != null
				&& !meetingDAO.hasPermission(troop,
						Permission.PERMISSION_EDIT_YEARPLAN_ID))
			throw new IllegalAccessException();

		if (!meetingDAO.isCurrentTroopId(troop, troop.getCurrentTroop())) {
			troop.setErrCode("112");
			throw new IllegalStateException();
			// return;
		}
		java.util.List<MeetingE> meetings = troop.getYearPlan()
				.getMeetingEvents();
		for (int i = 0; i < meetings.size(); i++)
			if (meetings.get(i).getUid().equals(meetingUid))
				meetings.get(i).getAssets().add(asset);

		troopDAO.updateTroop(troop);
	}

	public void selectYearPlan(Troop troop, String yearPlanPath, String planName)
			throws java.lang.IllegalAccessException {

		// permission to update
		if (troop != null
				&& !meetingDAO.hasPermission(troop,
						Permission.PERMISSION_ADD_YEARPLAN_ID))
			throw new IllegalAccessException();

		if (!meetingDAO.isCurrentTroopId(troop, troop.getCurrentTroop())) {
			troop.setErrCode("112");
			return;
		}

		YearPlan oldPlan = troop.getYearPlan();
		YearPlan newYearPlan = troopDAO.addYearPlan(troop, yearPlanPath);
		try {

			newYearPlan.setName(planName);
			if (oldPlan == null || oldPlan.getMeetingEvents() == null
					|| oldPlan.getMeetingEvents().size() <= 0
					|| oldPlan.getSchedule() == null
					|| oldPlan.getSchedule().getDates().equals("")) {
				troop.setYearPlan(newYearPlan);
			} else if (oldPlan.getSchedule() != null) {
				String oldDates = oldPlan.getSchedule().getDates();
				int count = 0;
				java.util.StringTokenizer t = new java.util.StringTokenizer(
						oldDates, ",");

				// if number of dates less then new meetings
				if (t.countTokens() < newYearPlan.getMeetingEvents().size()) {
					int countDates = t.countTokens();
					long lastDate = 0, meetingTimeDiff = 99999;
					while (t.hasMoreElements()) {
						long diff = lastDate;
						lastDate = Long.parseLong(t.nextToken());
						if (diff != 0)
							meetingTimeDiff = lastDate - diff;
					}
					for (int z = countDates; z < newYearPlan.getMeetingEvents()
							.size(); z++)
						oldDates += (lastDate + meetingTimeDiff) + ",";
					oldPlan.getSchedule().setDates(oldDates);
					t = new java.util.StringTokenizer(oldDates, ",");
				}

				while (t.hasMoreElements()) {
					long date = Long.parseLong(t.nextToken());

					if (count >= newYearPlan.getMeetingEvents().size()) {
						// rm all other dates
						oldPlan.getSchedule().setDates(
								oldPlan.getSchedule()
										.getDates()
										.substring(
												0,
												oldPlan.getSchedule()
														.getDates()
														.indexOf("" + date)));

						// TODO re write
						java.util.List<MeetingE> toBeRm = new java.util.ArrayList();
						for (int i = count; i < oldPlan.getMeetingEvents()
								.size(); i++)
							toBeRm.add(oldPlan.getMeetingEvents().get(i));

						for (int i = 0; i < toBeRm.size(); i++)
							oldPlan.getMeetingEvents().remove(toBeRm.get(i));

						break;
					} else if (new java.util.Date().before(new java.util.Date(
							date))) {

						if (count >= oldPlan.getMeetingEvents().size())
							oldPlan.getMeetingEvents().add(
									newYearPlan.getMeetingEvents().get(count));
						else
							// replace meeting refs
							oldPlan.getMeetingEvents()
									.get(count)
									.setRefId(
											newYearPlan.getMeetingEvents()
													.get(count).getRefId());

						oldPlan.getMeetingEvents().get(count)
								.setCancelled("false");

					}
					count++;
				}

			} else {

				oldPlan.setMeetingEvents(newYearPlan.getMeetingEvents());
				troop.setYearPlan(oldPlan);
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				System.err
						.println("Error setting new Plan: dumping old plan and replacing with new Plan");
				troop.setYearPlan(newYearPlan);
			} catch (Exception ew) {
				ew.printStackTrace();
			}
		}

		// remove all PAST custom activitites
		if (troop.getYearPlan().getActivities() != null) {
			java.util.List<Activity> activityToRm = new java.util.ArrayList();
			for (int i = 0; i < troop.getYearPlan().getActivities().size(); i++)
				if (new java.util.Date().before(troop.getYearPlan()
						.getActivities().get(i).getDate()))
					activityToRm
							.add(troop.getYearPlan().getActivities().get(i));

			for (int i = 0; i < activityToRm.size(); i++)
				troop.getYearPlan().getActivities().remove(activityToRm.get(i));
		}// end if

		troop.getYearPlan().setAltered("false");
		troop.getYearPlan().setName(planName);
		troopDAO.updateTroop(troop);

	}

	public boolean updateTroop(Troop troop)
			throws java.lang.IllegalAccessException,
			java.lang.IllegalAccessException {

		return troopDAO.updateTroop(troop);
	}

	public void rmTroop(Troop troop) throws java.lang.IllegalAccessException,
			java.lang.IllegalAccessException {
		troopDAO.rmTroop(troop);
	}

	public UserGlobConfig getUserGlobConfig() {
		return troopDAO.getUserGlobConfig();
	}

	

}// end class

