package org.girlscouts.vtk.ejb;

import java.io.File;
import java.io.FileInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;
import javax.mail.internet.InternetAddress;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.mail.HtmlEmail;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.manager.impl.ObjectContentManagerImpl;
import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.AnnotationMapperImpl;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.digester.DigesterMapperImpl;
import org.apache.jackrabbit.ocm.query.Filter;
import org.apache.jackrabbit.ocm.query.Query;
import org.apache.jackrabbit.ocm.query.QueryManager;
import org.apache.jackrabbit.commons.JcrUtils;
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.dao.CouncilDAO;
import org.girlscouts.vtk.dao.MeetingDAO;
import org.girlscouts.vtk.dao.TroopDAO;
import org.girlscouts.vtk.dao.YearPlanComponentType;
import org.girlscouts.vtk.helpers.ConfigManager;

import org.girlscouts.vtk.models.Achievement;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Asset;
import org.girlscouts.vtk.models.Attendance;
import org.girlscouts.vtk.models.Cal;
import org.girlscouts.vtk.models.Council;
import org.girlscouts.vtk.models.Finance;
import org.girlscouts.vtk.models.FinanceConfiguration;
import org.girlscouts.vtk.models.JcrNode;
import org.girlscouts.vtk.models.Location;
import org.girlscouts.vtk.models.Meeting;
import org.girlscouts.vtk.models.MeetingCanceled;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.MeetingE1;
import org.girlscouts.vtk.models.Milestone;
import org.girlscouts.vtk.models.SearchTag;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.models.UserGlobConfig;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.models.SentEmail;
import org.girlscouts.vtk.models.JcrCollectionHoldString;
import org.girlscouts.vtk.modifiedcheck.ModifiedChecker;
import org.girlscouts.vtk.utils.VtkException;
import org.girlscouts.vtk.utils.VtkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.traversal.NodeIterator;
import org.apache.jackrabbit.util.Text;

import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;

@Component
@Service(value = TroopDAO.class)
public class TroopDAOImpl implements TroopDAO {
	private final Logger log = LoggerFactory.getLogger("vtk");
	@Reference
	private SessionFactory sessionFactory;

	@Reference
	private UserUtil userUtil;

	private static UserGlobConfig troopGlobConfig;

	@Reference
	private ModifiedChecker modifiedChecker;

	@Reference
	private CouncilDAO councilDAO;

	@Reference
	private MeetingDAO meetingDAO;

	@Reference
	private MessageGatewayService messageGatewayService;

	@Reference
	ConfigManager configManager;
	
	@Activate
	void activate() {
	}

	public Troop getTroop(User user, String councilId, String troopId)
			throws IllegalAccessException, VtkException {
		// TODO Permission.PERMISSION_VIEW_YEARPLAN_ID)

		Session mySession = null;
		Troop troop = null;
		try {
			mySession = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Troop.class);
			classes.add(YearPlan.class);
			classes.add(MeetingE.class);
			classes.add(MeetingCanceled.class);
			classes.add(Activity.class);
			classes.add(Location.class);
			classes.add(Asset.class);
			classes.add(Cal.class);
			classes.add(Milestone.class);
			classes.add(SentEmail.class);
			classes.add(JcrCollectionHoldString.class);
			classes.add(Attendance.class);
			classes.add(Achievement.class);

			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(mySession,
					mapper);
			ocm.refresh(true);
			troop = (Troop) ocm.getObject(VtkUtil.getYearPlanBase(user, troop)
					+ councilId + "/troops/" + troopId);

			if (troop != null)
				troop.setRetrieveTime(new java.util.Date());

try{
	if( user.getApiConfig().isDemoUser() && user.getApiConfig().getTroops().get(0).getRole().equals("PA")){
		
		String DATES="";
		
		java.util.Calendar startDate = java.util.Calendar.getInstance();
		startDate.setTime( new java.util.Date("4/1/2016") );
		
		Cal cal = troop.getYearPlan().getSchedule();
		if( cal ==null ) cal= new org.girlscouts.vtk.models.Cal();
		java.util.List<MeetingE> meetings = troop.getYearPlan().getMeetingEvents();
	
		for(int i=0;i<meetings.size();i++){
			startDate.add( java.util.Calendar.DATE, 7);
			DATES += startDate.getTime().getTime() + ",";
		}
		
		cal.setDates(DATES);
		
		YearPlan yPlan= troop.getYearPlan();
		yPlan.setSchedule(cal);
		troop.setYearPlan(yPlan);
	}
}catch(Exception e){e.printStackTrace();}
			


		} catch (org.apache.jackrabbit.ocm.exception.IncorrectPersistentClassException ec) {
			ec.printStackTrace();
			throw new VtkException(
					"Could not complete intended action due to a server error. Code: "
							+ new java.util.Date().getTime());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (mySession != null)
					sessionFactory.closeSession(mySession);
			} catch (Exception es) {
				es.printStackTrace();
			}
		}

		return troop;
	}

	public Troop getTroop_byPath(User user, String troopPath)
			throws IllegalAccessException {
		Session mySession = null;
		Troop troop = null;
		// TODO Permission.PERMISSION_VIEW_YEARPLAN_ID

		try {
			mySession = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Troop.class);
			classes.add(YearPlan.class);

			classes.add(MeetingE.class);
			classes.add(Activity.class);
			classes.add(Location.class);
			classes.add(Asset.class);
			classes.add(Cal.class);
			classes.add(Milestone.class);
			classes.add(SentEmail.class);

			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(mySession,
					mapper);

			QueryManager queryManager = ocm.getQueryManager();
			Filter filter = queryManager.createFilter(Troop.class);

			ocm.refresh(true);
			troop = (Troop) ocm.getObject(troopPath);

			if (troop != null && troop.getYearPlan().getMeetingEvents() != null) {
				Comparator<MeetingE> comp = new BeanComparator("id");
				Collections.sort(troop.getYearPlan().getMeetingEvents(), comp);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				sessionFactory.closeSession(mySession);
			} catch (Exception es) {
				es.printStackTrace();
			}
		}

		return troop;
	}

	public YearPlan addYearPlan1(User user, Troop troop, String yearPlanPath)
			throws java.lang.IllegalAccessException,
			java.lang.IllegalAccessException {

		// permission to update
		if (troop != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_ADD_YEARPLAN_ID))
			throw new IllegalAccessException();

		Session mySession = null;
		String fmtYearPlanPath = yearPlanPath;
		YearPlan plan = null;
		try {

			mySession = sessionFactory.getSession();
			if (!yearPlanPath.endsWith("/"))
				yearPlanPath = yearPlanPath + "/";

			yearPlanPath += "meetings/";

			List<Class> classes = new ArrayList<Class>();
			classes.add(Troop.class);
			classes.add(YearPlan.class);
			classes.add(MeetingE.class);
			classes.add(Cal.class);
			classes.add(Milestone.class);

			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(mySession,
					mapper);
			QueryManager queryManager = ocm.getQueryManager();

			Filter filter = queryManager.createFilter(YearPlan.class);
			plan = (YearPlan) ocm.getObject(fmtYearPlanPath);
			if (plan != null && plan.getCalFreq() == null)
				plan.setCalFreq("biweekly");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				sessionFactory.closeSession(mySession);
			} catch (Exception es) {
				es.printStackTrace();
			}
		}

		return plan;

	}

	public void rmTroop(Troop troop) throws IllegalAccessException {
		Session mySession = null;
		try {

			// permission to update
			if (troop != null
					&& !userUtil.hasPermission(troop,
							Permission.PERMISSION_RM_YEARPLAN_ID))
				throw new IllegalAccessException();

			mySession = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Troop.class);
			classes.add(YearPlan.class);
			classes.add(MeetingE.class);
			classes.add(Location.class);
			classes.add(Cal.class);
			classes.add(Activity.class);
			classes.add(Asset.class);
			classes.add(Milestone.class);
			classes.add(SentEmail.class);
			classes.add(JcrCollectionHoldString.class);

			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(mySession,
					mapper);
			
			if (troop!=null && mySession.itemExists(troop.getPath())) {
				ocm.remove(troop);
				ocm.save();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				sessionFactory.closeSession(mySession);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public UserGlobConfig getUserGlobConfig() {

		loadUserGlobConfig();

		if (troopGlobConfig == null) {
			createUserGlobConfig();
			loadUserGlobConfig();
		}

		return troopGlobConfig;
	}

	public void loadUserGlobConfig() {

		troopGlobConfig = new UserGlobConfig();
		Session mySession = null;
		try {
			mySession = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(UserGlobConfig.class);

			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(mySession,
					mapper);

			QueryManager queryManager = ocm.getQueryManager();
			Filter filter = queryManager.createFilter(UserGlobConfig.class);

			troopGlobConfig = (UserGlobConfig) ocm.getObject("/"
					+ VtkUtil.getYearPlanBase(null, null) + "/global-settings");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				sessionFactory.closeSession(mySession);
			} catch (Exception es) {
				es.printStackTrace();
			}
		}

	}

	public void createUserGlobConfig() {

		Session mySession = null;
		try {
			mySession = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(UserGlobConfig.class);

			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(mySession,
					mapper);

			UserGlobConfig troopGlobConfig = new UserGlobConfig();
			ocm.insert(troopGlobConfig);
			ocm.save();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				sessionFactory.closeSession(mySession);
			} catch (Exception es) {
				es.printStackTrace();
			}
		}

	}

	public void updateUserGlobConfig() {
		Session mySession = null;
		try {
			mySession = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(UserGlobConfig.class);

			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(mySession,
					mapper);

			if (mySession.itemExists(troopGlobConfig.getPath())) {
				ocm.update(troopGlobConfig);
			} else {
				ocm.insert(troopGlobConfig);
			}
			ocm.save();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (sessionFactory != null)
					sessionFactory.closeSession(mySession);
			} catch (Exception es) {
				es.printStackTrace();
			}
		}

	}

	public Finance getFinances(Troop troop, int qtr, String currentYear) {
		Session mySession = null;
		Finance result = null;
		try {
			mySession = sessionFactory.getSession();
			result = new Finance();
			result.setFinancialQuarter(qtr);
			String path = "/" + troop.getTroopPath() + "/finances/"
					+ currentYear + "/" + qtr;
			try {
				Node financeNode = mySession.getNode(path);
				Node incomeNode = financeNode.getNode("income");
				Node expensesNode = financeNode.getNode("expenses");
				PropertyIterator incomeFieldIterator = incomeNode
						.getProperties();
				PropertyIterator expensesFieldIterator = expensesNode
						.getProperties();
				Map<String, Double> incomeMap = new HashMap<String, Double>();
				while (incomeFieldIterator.hasNext()) {

					Property temp = incomeFieldIterator.nextProperty();
					String fieldName = temp.getName();
					fieldName = Text.unescapeIllegalJcrChars(fieldName);
					String value = temp.getValue().getString();

					if (value.isEmpty()) {
						value = "0.00";
					}
					if (!fieldName.equals("jcr:primaryType")) {
						incomeMap.put(fieldName, Double.parseDouble(value));
					}
				}

				Map<String, Double> expensesMap = new HashMap<String, Double>();
				while (expensesFieldIterator.hasNext()) {
					Property temp = expensesFieldIterator.nextProperty();
					String fieldName = temp.getName();
					fieldName = Text.unescapeIllegalJcrChars(fieldName);
					String value = temp.getValue().getString();
					if (value.isEmpty()) {
						value = "0.00";
					}
					if (!fieldName.equals("jcr:primaryType")) {
						expensesMap.put(fieldName, Double.parseDouble(value));
					}
				}
				result.setExpenses(expensesMap);
				result.setIncome(incomeMap);

			} catch (PathNotFoundException ex) {

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (sessionFactory != null)
					sessionFactory.closeSession(mySession);

			} catch (Exception es) {
				es.printStackTrace();
			}
		}
		return result;
	}

	public void setFinances(Troop troop, int qtr, String currentYear,
			java.util.Map<String, String[]> params) {

		Session mySession = null;
		try {
			String path = troop.getTroopPath() + "/finances/" + currentYear;
			mySession = sessionFactory.getSession();
			Node rootNode = mySession.getRootNode();
			Node financesNode = null;
			if (!rootNode.hasNode(path)) {
				financesNode = this.establishBaseNode(path, mySession);
			}

			if (rootNode.hasNode(path + "/" + qtr)) {
				// Remove quarter specific finance node if one exists
				Node tempNode = rootNode.getNode(path + "/" + qtr);
				tempNode.remove();

			}
			Node financeNode = rootNode.addNode(path + "/" + qtr,
					"nt:unstructured");
			Node expensesNode = financeNode.addNode("expenses");
			Node incomeNode = financeNode.addNode("income");
			this.populateFinanceChildren(incomeNode, expensesNode,
					params.get("expenses")[0], params.get("income")[0]);

			mySession.save();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				sessionFactory.closeSession(mySession);
			} catch (Exception es) {
				es.printStackTrace();
			}
		}
	}

	public FinanceConfiguration getFinanceConfiguration(Troop troop,
			String currentYear) {

		Session mySession = null;
		FinanceConfiguration financeConfig = new FinanceConfiguration();
		try {
			mySession = sessionFactory.getSession();
			String councilPath = "/" + troop.getCouncilPath();
			String configPath = councilPath + "/"
					+ FinanceConfiguration.FINANCE_CONFIG + "/" + currentYear;
			Node configNode = null;
			try {
				configNode = mySession.getNode(configPath);
			} catch (PathNotFoundException pfe) {
				// Path does not exist. Get parent node.
				Node troopNode = mySession.getNode(councilPath);
				// Now create and bind it to config
				configNode = troopNode
						.addNode(FinanceConfiguration.FINANCE_CONFIG);
			}

			List<String> expensesList = new ArrayList<String>();
			List<String> incomeList = new ArrayList<String>();
			if (configNode.hasProperty(Finance.EXPENSES)) {
				Value[] expensesValues = configNode.getProperty(
						Finance.EXPENSES).getValues();
				for (Value tempValue : expensesValues) {
					expensesList.add(tempValue.getString());
				}
				financeConfig.setExpenseFields(expensesList);
				financeConfig.setPersisted(true);
			}
			if (configNode.hasProperty(Finance.INCOME)) {
				Value[] incomeValues = configNode.getProperty(Finance.INCOME)
						.getValues();
				for (Value tempValue : incomeValues) {
					incomeList.add(tempValue.getString());
				}
				financeConfig.setIncomeFields(incomeList);
				financeConfig.setPersisted(true);
			}
			if (configNode.hasProperty(Finance.PERIOD)) {
				String period = configNode.getProperty(Finance.PERIOD)
						.getString();
				financeConfig.setPeriod(period);
				financeConfig.setPersisted(true);
			}
			if (configNode.hasProperty(FinanceConfiguration.RECIPIENT)) {
				String recipient = configNode.getProperty(
						FinanceConfiguration.RECIPIENT).getString();
				financeConfig.setRecipient(recipient);
				financeConfig.setPersisted(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (sessionFactory != null)
					sessionFactory.closeSession(mySession);
			} catch (Exception es) {
				es.printStackTrace();
			}
		}
		return financeConfig;
	}

	public void setFinanceConfiguration(Troop troop, String currentYear,
			String income, String expenses, String period, String recipient) {

		// TODO PERMISSIONS HERE
		Session mySession = null;
		try {
			mySession = sessionFactory.getSession();
			Node rootNode = mySession.getRootNode();
			String configPath = troop.getCouncilPath() + "/"
					+ FinanceConfiguration.FINANCE_CONFIG + "/" + currentYear;
			Node financesNode = null;
			if (!rootNode.hasNode(configPath)) {
				financesNode = this.establishBaseNode(configPath, mySession);
			}

			Node configNode = mySession.getNode("/" + configPath);
			String[] incomeFields = income.replaceAll("\\[|\\]", "").split(",");
			String[] expensesFields = expenses.replaceAll("\\[|\\]", "").split(
					",");

			configNode.setProperty(Finance.INCOME, incomeFields);
			configNode.setProperty(Finance.EXPENSES, expensesFields);
			configNode.setProperty(Finance.PERIOD, period);
			configNode.setProperty(FinanceConfiguration.RECIPIENT, recipient);

			mySession.save();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				sessionFactory.closeSession(mySession);
			} catch (Exception es) {
				es.printStackTrace();
			}
		}
	}

	// Populate the two nodes expenses and income with the properties and values
	// enetered into the finance form
	private void populateFinanceChildren(Node incomeNode, Node expensesNode,
			String expensesParams, String incomeParams)
			throws RepositoryException {
		String[] expenses = expensesParams.replaceAll("\\[|\\]", "").split(",");
		String[] income = incomeParams.replaceAll("\\[|\\]", "").split(",");

		for (int i = 0; i < expenses.length; i = i + 2) {
			String fieldName = expenses[i].trim();
			fieldName = Text.escapeIllegalJcrChars(fieldName);
			String fieldValue = expenses[i + 1].trim();
			expensesNode.setProperty(fieldName, fieldValue);
		}

		for (int i = 0; i < income.length; i = i + 2) {

			String fieldName = income[i].trim();
			fieldName = Text.escapeIllegalJcrChars(fieldName);
			String fieldValue = income[i + 1].trim();
			incomeNode.setProperty(fieldName, fieldValue);
		}
	}

	private Node establishBaseNode(String path, Session session)
			throws RepositoryException {
		Node rootNode = session.getRootNode();
		String[] pathElements = path.split("/");

		String vtkBase = VtkUtil.getYearPlanBase(null, null);
		vtkBase = vtkBase.replaceAll("/", "");
		Node currentNode = rootNode.getNode(vtkBase);// "vtk");
		for (int i = 1; i < pathElements.length; i++) {
			if (!pathElements[i].equals("")) {
				if (currentNode.hasNode(pathElements[i])) {
					currentNode = currentNode.getNode(pathElements[i]);

				} else {
					currentNode = currentNode.addNode(pathElements[i],
							"nt:unstructured");

				}
			}
		}
		return currentNode;
	}

	public boolean updateTroop(User user, Troop troop)
			throws java.lang.IllegalAccessException,
			java.lang.IllegalAccessException, VtkException {

		modifyTroop(user, troop);
		
		if (troop.getYearPlan().getPath() == null
				|| !troop.getYearPlan().getPath().startsWith(troop.getPath()))
			troop.getYearPlan().setPath(troop.getPath() + "/yearPlan");
		
		modifyYearPlan(user, troop);
		
		modifySchedule(user, troop);
		

		java.util.List<Location> locations = troop.getYearPlan().getLocations();
		if (locations != null)
			for (int i = 0; i < locations.size(); i++) {
				Location location = locations.get(i);
				if (location.getPath() == null)
					location.setPath(troop.getYearPlan().getPath()
							+ "/locations/" + location.getUid());
				modifyLocation(user, troop, locations.get(i));
			}
		
		if (troop.getYearPlan().getActivities() != null)
			for (int i = 0; i < troop.getYearPlan().getActivities().size(); i++)
				modifyActivity(user, troop, troop.getYearPlan().getActivities()
						.get(i));
		
		if (troop.getYearPlan().getMeetingEvents() != null)
			for (int i = 0; i < troop.getYearPlan().getMeetingEvents().size(); i++) {
				MeetingE meeting = troop.getYearPlan().getMeetingEvents()
						.get(i);

				if (meeting.getPath() == null
						|| !meeting.getPath().startsWith(
								troop.getYearPlan().getPath()))
					meeting.setPath(troop.getYearPlan().getPath()
							+ "/meetingEvents/" + meeting.getUid());
				
				modifyMeeting(user, troop, meeting);
				
				java.util.List<Asset> assets = meeting.getAssets();
				if (assets != null)
					for (int y = 0; y < assets.size(); y++) {
						Asset asset = assets.get(y);
						if (asset.getPath() == null)
							asset.setPath(meeting.getPath() + "/assets/"
									+ asset.getUid());
						
						modifyAsset(user, troop, asset);
						
					}
			}
		
		// canceled meeting
		if (troop.getYearPlan().getMeetingCanceled() != null)
			for (int i = 0; i < troop.getYearPlan().getMeetingCanceled().size(); i++) {
				MeetingCanceled meeting = troop.getYearPlan()
						.getMeetingCanceled().get(i);

				if (meeting.getPath() == null
						|| !meeting.getPath().startsWith(
								troop.getYearPlan().getPath()))
					meeting.setPath(troop.getYearPlan().getPath()
							+ "/meetingCanceled/" + meeting.getUid());
				modifyMeetingCanceled(user, troop, meeting);
				java.util.List<Asset> assets = meeting.getAssets();
				if (assets != null)
					for (int y = 0; y < assets.size(); y++) {
						Asset asset = assets.get(y);
						if (asset.getPath() == null)
							asset.setPath(meeting.getPath() + "/assets/"
									+ asset.getUid());
						modifyAsset(user, troop, asset);
					}
			}
		

		// modif
		try {
			modifiedChecker.setModified(user.getSid(), troop.getYearPlan()
					.getPath());
		} catch (Exception em) {
			em.printStackTrace();
		}
		
		return false;
	}

	public boolean modifyAsset(User user, Troop troop, Asset asset)
			throws java.lang.IllegalAccessException,
			java.lang.IllegalAccessException {

		if (!asset.isDbUpdate())
			return true;

		Session mySession = null;
		boolean isUpdated = false;
		try {
			mySession = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();

			classes.add(Asset.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(mySession,
					mapper);

			if (!ocm.objectExists(asset.getPath())) {

				// check council
				if (councilDAO.findCouncil(user, troop.getSfCouncil()) == null) {
					throw new VtkException(
							"Found no council when creating troop# "
									+ troop.getTroopPath());

				}

				// check troop
				if (getTroop(user, troop.getSfCouncil(), troop.getSfTroopId()) == null) {
					throw new VtkException(
							"Found no troop when creating asset# "
									+ troop.getTroopPath());
				}

				// check meeting
				if (meetingDAO.getMeetingE(user, troop, asset.getPath()
						.substring(0, asset.getPath().lastIndexOf("/"))
						.replace("/assets", "")) == null) {
					throw new VtkException(
							"Found no troop when creating asset# "
									+ troop.getTroopPath());
				}

				if (!mySession.itemExists(asset.getPath().substring(0,
						asset.getPath().lastIndexOf("/")))) {
					JcrUtils.getOrCreateByPath(
							asset.getPath().substring(0,
									asset.getPath().lastIndexOf("/")),
							"nt:unstructured", mySession);
				}

			}
			if (!ocm.objectExists(asset.getPath())) {
				ocm.insert(asset);
			} else {
				ocm.update(asset);
			}
			ocm.save();
			isUpdated = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (mySession != null) {
					sessionFactory.closeSession(mySession);
				}
			} catch (Exception es) {
				es.printStackTrace();
			}
		}

		return isUpdated;
	}

	public boolean modifyMeeting(User user, Troop troop, MeetingE meeting)
			throws java.lang.IllegalAccessException {

		if (!meeting.isDbUpdate())
			return true;

		Session mySession = null;
		boolean isUpdated = false;
		try {
			mySession = sessionFactory.getSession();
			
			
			List<Class> classes = new ArrayList<Class>();
			classes.add(MeetingE.class );
			classes.add(Asset.class);
			classes.add(SentEmail.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(mySession,	mapper);
			
			if (meeting.getPath() == null
					|| !ocm.objectExists(troop.getPath()
							+ "/yearPlan/meetingEvents")) {
				// check council
			
				if (councilDAO.findCouncil(user, troop.getSfCouncil()) == null) {
					throw new VtkException(
							"Found no council when creating troop# "
									+ troop.getTroopPath());
				}
				
				// check troop
				if (getTroop(user, troop.getSfCouncil(), troop.getSfTroopId()) == null) {
					throw new VtkException(
							"Found no troop when creating sched# "
									+ troop.getTroopPath());
				}
				
				if (mySession.itemExists(troop.getPath() + "/yearPlan")) {
					JcrUtils.getOrCreateByPath(troop.getPath()
							+ "/yearPlan/meetingEvents", "nt:unstructured",
							mySession);

				}
				
				meeting.setPath(troop.getYearPlan().getPath()
						+ "/meetingEvents/" + meeting.getUid());
			}
			
			
			
			
			
			
			
			
			if (!ocm.objectExists(meeting.getPath())) {
				
				ocm.insert(meeting);
				
			} else {
				
				ocm.update(meeting);
				
			}
			
			ocm.save();
			
			isUpdated = true;
		} catch (org.apache.jackrabbit.ocm.exception.ObjectContentManagerException iise) {
			// org.apache.jackrabbit.ocm.exception.ObjectContentManagerException:
			// Cannot persist current session changes.; nested exception is
			// javax.jcr.InvalidItemStateException: Unable to update a stale
			// item: item.save()
			// javax.jcr.InvalidItemStateException
			// skip here because we are getting concurrent modification of the
			// same node (most likely)
			// when vtk is calling ajax to this method twice.
			// need to fix the ajax call.
			log.error(">>>> Skipping stale update for " + meeting.getPath());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (mySession != null)
					sessionFactory.closeSession(mySession);
			} catch (Exception es) {
				es.printStackTrace();
			}
		}
		
		return isUpdated;
	}

	public boolean modifyActivity(User user, Troop troop, Activity activity)
			throws java.lang.IllegalAccessException,
			java.lang.IllegalAccessException {

		if (!activity.isDbUpdate())
			return true;

		Session mySession = null;
		boolean isUpdated = false;
		try {
			mySession = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Activity.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(mySession,
					mapper);
			if (activity.getPath() == null) {

				// check council
				if (councilDAO.findCouncil(user, troop.getSfCouncil()) == null) {
					throw new VtkException(
							"Found no council when creating troop# "
									+ troop.getTroopPath());

				}

				// check troop
				if (getTroop(user, troop.getSfCouncil(), troop.getSfTroopId()) == null) {
					throw new VtkException(
							"Found no troop when creating sched# "
									+ troop.getTroopPath());
				}

				if (!mySession.itemExists(troop.getYearPlan().getPath()
						+ "/activities"))
					JcrUtils.getOrCreateByPath(troop.getYearPlan().getPath()
							+ "/activities", "nt:unstructured", mySession);

				activity.setPath(troop.getYearPlan().getPath() + "/activities/"
						+ activity.getUid());
			}
			if (!ocm.objectExists(activity.getPath()))
				ocm.insert(activity);
			else
				ocm.update(activity);
			ocm.save();
			isUpdated = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (mySession != null)
					sessionFactory.closeSession(mySession);
			} catch (Exception es) {
				es.printStackTrace();
			}
		}

		return isUpdated;
	}

	public boolean modifyLocation(User user, Troop troop, Location location)
			throws java.lang.IllegalAccessException,
			java.lang.IllegalAccessException {

		if (!location.isDbUpdate())
			return true;

		Session mySession = null;
		boolean isUpdated = false;
		try {
			mySession = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Location.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(mySession,
					mapper);

			if (!ocm.objectExists(location.getPath())) {

				// check council
				if (councilDAO.findCouncil(user, troop.getSfCouncil()) == null) {
					throw new VtkException(
							"Found no council when creating troop# "
									+ troop.getTroopPath());

				}

				// check troop
				if (getTroop(user, troop.getSfCouncil(), troop.getSfTroopId()) == null) {
					throw new VtkException(
							"Found no troop when creating sched# "
									+ troop.getTroopPath());
				}

				if (!mySession.itemExists(location.getPath().substring(0,
						location.getPath().lastIndexOf("/"))))
					JcrUtils.getOrCreateByPath(
							location.getPath().substring(0,
									location.getPath().lastIndexOf("/")),
							"nt:unstructured", mySession);

			}

			if (!ocm.objectExists(location.getPath()))
				ocm.insert(location);
			else
				ocm.update(location);
			ocm.save();
			isUpdated = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (mySession != null)
					sessionFactory.closeSession(mySession);
			} catch (Exception es) {
				es.printStackTrace();
			}
		}

		return isUpdated;
	}

	public boolean modifySchedule(User user, Troop troop)
			throws java.lang.IllegalAccessException,
			java.lang.IllegalAccessException {

		Cal schedule = troop.getYearPlan().getSchedule();

		if (schedule == null || !schedule.isDbUpdate())
			return true;

		Session mySession = null;
		boolean isUpdated = false;
		try {
			mySession = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Cal.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(mySession,
					mapper);
			if (schedule.getDates() == null) {// remove the schedule to reset
												// the yearplan
				if (schedule.getPath() == null) {
					schedule.setPath(troop.getYearPlan().getPath()
							+ "/schedule");
				}
				if (ocm.objectExists(schedule.getPath()))
					ocm.remove(schedule);
				troop.getYearPlan().setSchedule(null);
			} else {
				if (schedule.getPath() == null) {
					if (councilDAO.findCouncil(user, troop.getSfCouncil()) == null) {
						throw new VtkException(
								"Found no council when creating troop# "
										+ troop.getTroopPath());
					}

					// check troop
					if (getTroop(user, troop.getSfCouncil(),
							troop.getSfTroopId()) == null) {
						throw new VtkException(
								"Found no troop when creating sched# "
										+ troop.getTroopPath());
					}

					if (!mySession.itemExists(troop.getYearPlan().getPath()
							+ "/schedule"))
						JcrUtils.getOrCreateByPath(troop.getYearPlan()
								.getPath() + "/schedule", "nt:unstructured",
								mySession);
					schedule.setPath(troop.getYearPlan().getPath()
							+ "/schedule");
				}

				if (ocm.objectExists(schedule.getPath()))
					ocm.update(schedule);
				else
					ocm.insert(schedule);
			}
			ocm.save();
			isUpdated = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (mySession != null)
					sessionFactory.closeSession(mySession);
			} catch (Exception es) {
				es.printStackTrace();
			}
		}

		return isUpdated;
	}

	public boolean modifyYearPlan(User user, Troop troop)
			throws java.lang.IllegalAccessException,
			java.lang.IllegalAccessException {

		YearPlan yearPlan = troop.getYearPlan();
		if (!yearPlan.isDbUpdate())
			return true;

		Session mySession = null;
		boolean isUpdated = false;
		try {
			mySession = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(YearPlan.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(mySession,
					mapper);

			ocm.update(yearPlan);
			ocm.save();
			isUpdated = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (mySession != null)
					sessionFactory.closeSession(mySession);
			} catch (Exception es) {
				es.printStackTrace();
			}
		}
		return isUpdated;
	}

	public boolean modifyTroop(User user, Troop troop)
			throws java.lang.IllegalAccessException, VtkException {
		if (!troop.isDbUpdate()) {
			return true;
		}
		Session mySession = null;
		boolean isUpdated = false;
		try {
			if (troop == null || troop.getYearPlan() == null) {
				return true;
			}

			Council council = councilDAO
					.findCouncil(user, troop.getSfCouncil()); // 0729815
			if (council == null) {
				throw new VtkException("Found no council when creating troop# "
						+ troop.getTroopPath());
			}

			mySession = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Troop.class);
			classes.add(YearPlan.class);
			classes.add(MeetingE.class);
			classes.add(Location.class);
			classes.add(Cal.class);
			classes.add(Activity.class);
			classes.add(Asset.class);
			classes.add(JcrNode.class);
			classes.add(Milestone.class);
			classes.add(SentEmail.class);
			classes.add(Council.class);
			classes.add(org.girlscouts.vtk.models.Troop.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(mySession,
					mapper);
			Comparator<MeetingE> comp = new BeanComparator("id");
			if (troop != null && troop.getYearPlan() != null
					&& troop.getYearPlan().getMeetingEvents() != null)
				Collections.sort(troop.getYearPlan().getMeetingEvents(), comp);

			// permission to update
			if (troop != null
					&& !userUtil.hasPermission(troop,
							Permission.PERMISSION_VIEW_YEARPLAN_ID))
				throw new IllegalAccessException();

			if (!mySession.itemExists("/" + troop.getCouncilPath() + "/troops")) {
				JcrUtils.getOrCreateByPath("/" + troop.getCouncilPath()
						+ "/troops", "nt:unstructured", mySession);
			}

			//troop.setLastModified(java.util.Calendar.getInstance());
			troop.setCurrentTroop(user.getSid());// 10/23/14 Documenting the
													// last user who modified
													// this troop data

			if (!ocm.objectExists(troop.getPath())) {
				ocm.insert(troop);
			} else {
				ocm.update(troop);
			}
			ocm.save();

			String old_errCode = troop.getErrCode();
			//java.util.Calendar old_lastModified = troop.getLastModified();
			try {
				troop.setErrCode(null);

				try {
					modifiedChecker.setModified(user.getSid(), troop
							.getYearPlan().getPath());
				} catch (Exception em) {
					em.printStackTrace();
				}

				isUpdated = true;
				troop.setRefresh(true);
			} catch (Exception e) {

				log.error("!!!! ERROR !!!!!  TroopDAOImpl.updateTroop CAN NOT SAVE TROOP !!!! ERROR !!!!!");
				//troop.setLastModified(old_lastModified);
				troop.setErrCode(old_errCode);
				troop.setRefresh(true);
			}

		} catch (VtkException ex) {
			ex.printStackTrace();
			throw new VtkException(
					"Could not complete intended action due to a server error. Code: "
							+ new java.util.Date().getTime());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (mySession != null)
					sessionFactory.closeSession(mySession);
			} catch (Exception es) {
				es.printStackTrace();
			}
		}
		return isUpdated;
	}

	public boolean removeActivity(User user, Troop troop, Activity activity)
			throws java.lang.IllegalAccessException,
			java.lang.IllegalAccessException {

		Session mySession = null;
		boolean isUpdated = false;
		try {
			mySession = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Activity.class);
			classes.add(SentEmail.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(mySession,
					mapper);
			ocm.remove(activity);
			ocm.save();
			isUpdated = true;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (mySession != null)
					sessionFactory.closeSession(mySession);
			} catch (Exception es) {
				es.printStackTrace();
			}
		}

		return isUpdated;
	}

	public boolean removeMeeting(User user, Troop troop, MeetingE meeting)
			throws java.lang.IllegalAccessException,
			java.lang.IllegalAccessException {

		Session mySession = null;
		boolean isUpdated = false;
		try {
			mySession = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(MeetingE.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(mySession,
					mapper);
			ocm.remove(meeting);
			ocm.save();
			isUpdated = true;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (mySession != null)
					sessionFactory.closeSession(mySession);
			} catch (Exception es) {
				es.printStackTrace();
			}
		}

		return isUpdated;
	}

	public boolean removeAsset(User user, Troop troop, Asset asset)
			throws java.lang.IllegalAccessException,
			java.lang.IllegalAccessException {

		Session mySession = null;
		boolean isUpdated = false;
		try {
			mySession = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(Asset.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(mySession,
					mapper);
			ocm.remove(asset);
			ocm.save();
			isUpdated = true;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (mySession != null)
					sessionFactory.closeSession(mySession);
			} catch (Exception es) {
				es.printStackTrace();
			}
		}

		return isUpdated;
	}

	public boolean removeMeetings(User user, Troop troop)
			throws java.lang.IllegalAccessException,
			java.lang.IllegalAccessException {

		Session mySession = null;
		boolean isUpdated = false;
		try {
			mySession = sessionFactory.getSession();
			if (mySession.itemExists(troop.getPath()
					+ "/yearPlan/meetingEvents")) {
				mySession.removeItem(troop.getPath()
						+ "/yearPlan/meetingEvents");
				mySession.save();
			}
			isUpdated = true;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (mySession != null)
					sessionFactory.closeSession(mySession);
			} catch (Exception es) {
				es.printStackTrace();
			}
		}

		return isUpdated;
	}

	public boolean modifyMeetingCanceled(User user, Troop troop,
			MeetingCanceled meeting) throws java.lang.IllegalAccessException,
			java.lang.IllegalAccessException {

		if (!meeting.isDbUpdate())
			return true;

		Session mySession = null;
		boolean isUpdated = false;
		try {
			mySession = sessionFactory.getSession();
			List<Class> classes = new ArrayList<Class>();
			classes.add(MeetingCanceled.class);
			classes.add(Asset.class);
			classes.add(SentEmail.class);
			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(mySession,
					mapper);
			if (meeting.getPath() == null
					|| !ocm.objectExists(troop.getPath()
							+ "/yearPlan/meetingCanceled")) {

				if (!mySession.itemExists(troop.getPath()
						+ "/yearPlan/meetingCanceled"))
					JcrUtils.getOrCreateByPath(troop.getPath()
							+ "/yearPlan/meetingCanceled", "nt:unstructured",
							mySession);
				meeting.setPath(troop.getYearPlan().getPath()
						+ "/meetingCanceled/" + meeting.getUid());
			}
			if (!ocm.objectExists(meeting.getPath())) {
				ocm.insert(meeting);
			} else {
				ocm.update(meeting);
			}
			ocm.save();
			isUpdated = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (mySession != null)
					sessionFactory.closeSession(mySession);
			} catch (Exception es) {
				es.printStackTrace();
			}
		}

		return isUpdated;
	}
	
	
	
	public void removeDemoTroops() {

		Session session = null;
		try {
			session = sessionFactory.getSession();
			
			String sql = "select * from nt:unstructured where jcr:path like '"+VtkUtil.getYearPlanBase(null, null)+"%' and sfTroopId like 'SHARED_%' and ocm_classname='org.girlscouts.vtk.models.Troop'";
			javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
			javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL);
			QueryResult result = q.execute();
			
			
			//??? result.getNodes().remove();
			
			java.util.List<Node> nodes2Rm = new java.util.ArrayList();
			javax.jcr.NodeIterator itr =  result.getNodes();
		    while (itr.hasNext()) {
		      Node node = itr.nextNode();
		      nodes2Rm.add(node);		    
		     }
		    
		    for(int i=0;i<nodes2Rm.size();i++)
		    	nodes2Rm.get(i).remove();
			session.save();
			
			emailCronRpt(null);
			
		} catch (Exception e) {
			e.printStackTrace();
			emailCronRpt(e.toString());
		} finally {
			try {
				if (session != null)
					sessionFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public void emailCronRpt(String msg){
		try {
			MessageGateway<HtmlEmail> messageGateway = messageGatewayService
					.getGateway(HtmlEmail.class);

			String DEMO_CRON_EMAIL = (String) configManager.getConfig("DEMO_CRON_EMAIL");
			
			HtmlEmail email = new HtmlEmail();
			java.util.List<InternetAddress> toAddresses = new java.util.ArrayList();
			toAddresses.add( new InternetAddress(DEMO_CRON_EMAIL) );
			email.setTo(toAddresses);
			if (msg != null) {
				email.setSubject("GirlScouts VTK Demo Site Cron ERROR");
				email.setTextMsg("Error removing test nodes from database on "+  new java.util.Date() + ":" + msg);
			} else {
				email.setSubject("GirlScouts VTK Demo Site Cron");
				email.setTextMsg("Successfully removed all test nodes from database on "+  new java.util.Date() + ".");
			}
			messageGateway.send(email);
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}// ednclass

