package org.girlscouts.vtk.ejb;

import java.util.ArrayList;
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

import org.apache.commons.beanutils.BeanComparator;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.manager.impl.ObjectContentManagerImpl;
import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.AnnotationMapperImpl;
import org.apache.jackrabbit.ocm.query.Filter;
import org.apache.jackrabbit.ocm.query.QueryManager;
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.dao.TroopDAO;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Asset;
import org.girlscouts.vtk.models.Cal;
import org.girlscouts.vtk.models.Council;
import org.girlscouts.vtk.models.Finance;
import org.girlscouts.vtk.models.FinanceConfiguration;
import org.girlscouts.vtk.models.JcrNode;
import org.girlscouts.vtk.models.Location;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.Milestone;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.models.UserGlobConfig;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.modifiedcheck.ModifiedChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	@Activate
	void activate() {
	}

	public Troop getTroop(User user, String councilId, String troopId)
			throws IllegalAccessException {

		if (user != null
				&& !userUtil.hasPermission(user.getPermissions(),
						Permission.PERMISSION_VIEW_YEARPLAN_ID))
			throw new IllegalAccessException();

		Session mySession = null;
		Troop troop = null;
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

			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(mySession,
					mapper);

			ocm.refresh(true);
			troop = (Troop) ocm.getObject("/vtk/" + councilId + "/troops/"
					+ troopId);

			if (troop != null)
				troop.setRetrieveTime(new java.util.Date());

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

		if (user != null
				&& !userUtil.hasPermission(user.getPermissions(),
						Permission.PERMISSION_VIEW_YEARPLAN_ID))
			throw new IllegalAccessException();

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

		if (!userUtil.isCurrentTroopId(troop, user.getSid())) {
			troop.setErrCode("112");
			// return null;
			throw new java.lang.IllegalAccessException();
		}

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

	public boolean updateTroop(User user, Troop troop)
			throws java.lang.IllegalAccessException,
			java.lang.IllegalAccessException {

		Session mySession = null;
		boolean isUpdated = false;
		try {

			if (troop == null || troop.getYearPlan() == null) {
				return true;
			}
			troop.setErrCode("111");

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
			classes.add(Council.class);
			classes.add(org.girlscouts.vtk.models.Troop.class);

			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(mySession,
					mapper);

			Comparator<MeetingE> comp = new BeanComparator("id");
			Collections.sort(troop.getYearPlan().getMeetingEvents(), comp);

			// permission to update
			if (troop != null
					&& !userUtil.hasPermission(user.getPermissions(),
							Permission.PERMISSION_VIEW_YEARPLAN_ID))
				throw new IllegalAccessException();

			// lock
			if (troop != null && troop.getLastModified() != null) {

				if (!userUtil.isCurrentTroopId(troop, user.getSid())) {

					troop.setErrCode("112");
					throw new IllegalAccessException();
					// return false;
				}
			}

			if (mySession.itemExists(troop.getPath())) {
				ocm.update(troop);
			} else {
				String path = "";
				StringTokenizer t = new StringTokenizer(
						("/" + troop.getPath())
								.replace("/" + troop.getId(), ""),
						"/");
				int i = 0;
				while (t.hasMoreElements()) {
					String node = t.nextToken();
					path += "/" + node;
					if (!mySession.itemExists(path)) {
						if (i == 2) {
							ocm.insert(new Council(path));
						} else {
							ocm.insert(troop);
						}
					}
					i++;
				}
				ocm.insert(troop);
			}

			String old_errCode = troop.getErrCode();
			java.util.Calendar old_lastModified = troop.getLastModified();
			try {
				troop.setErrCode(null);
				troop.setLastModified(java.util.Calendar.getInstance());
				troop.setCurrentTroop(user.getSid());// 10/23/14

				// modif
				try {

					modifiedChecker.setModified(user.getSid(), troop
							.getYearPlan().getPath());
				} catch (Exception em) {
					em.printStackTrace();
				}

				ocm.update(troop);

				ocm.save();

				isUpdated = true;
				troop.setRefresh(true);

			} catch (Exception e) {

				log.error("!!!! ERROR !!!!!  TroopDAOImpl.updateTroop CAN NOT SAVE TROOP !!!! ERROR !!!!!");
				troop.setLastModified(old_lastModified);
				troop.setErrCode(old_errCode);
				troop.setRefresh(true);
			}
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

			Mapper mapper = new AnnotationMapperImpl(classes);
			ObjectContentManager ocm = new ObjectContentManagerImpl(mySession,
					mapper);
			ocm.remove(troop);
			ocm.save();
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

			troopGlobConfig = (UserGlobConfig) ocm
					.getObject("/vtk/global-settings");

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

	public Finance getFinanaces(Troop troop, int qtr) {

		
		System.err.println("*******Getting finances data");
		Session mySession = null;
		Finance result = null;
		try {
			mySession = sessionFactory.getSession();
			

			
			result = new Finance();
			result.setFinancialQuarter(qtr);
			
			String path = "/" + troop.getTroopPath() + "/finances/" + qtr;
			try{
				Node financeNode = mySession.getNode(path);
				Node incomeNode = financeNode.getNode("income");
				Node expensesNode = financeNode.getNode("expenses");
				PropertyIterator incomeFieldIterator = incomeNode.getProperties();
				PropertyIterator expensesFieldIterator = expensesNode.getProperties();
				Map<String, Double> incomeMap = new HashMap<String, Double>();
				while(incomeFieldIterator.hasNext()){
					
					Property temp = incomeFieldIterator.nextProperty();
					String fieldName = temp.getName();
					fieldName = this.restoreIllegalChars(fieldName);
					String value = temp.getValue().getString();
					
					if(value.isEmpty()){
						value = "0.00";
					}
					if(!fieldName.equals("jcr:primaryType")){
						incomeMap.put(fieldName, Double.parseDouble(value));
					}
				}
				
				Map<String, Double> expensesMap = new HashMap<String, Double>();
				while(expensesFieldIterator.hasNext()){
					Property temp = expensesFieldIterator.nextProperty();
					String fieldName = temp.getName();
					fieldName = this.replaceIllegalChars(fieldName);
					String value = temp.getValue().getString();
					if(value.isEmpty()){
						value = "0.00";
					}
					if(!fieldName.equals("jcr:primaryType")){
						expensesMap.put(fieldName, Double.parseDouble(value));
					}
				}
				result.setExpenses(expensesMap);
				result.setIncome(incomeMap);
				
			}catch(PathNotFoundException ex){
				
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

	public void setFinances(Troop troop, int qtr, java.util.Map<String, String[]> params) {

		
		Session mySession = null;
		try {
			String path = troop.getTroopPath() + "/finances/";
			mySession = sessionFactory.getSession();
			Node rootNode = mySession.getRootNode();
			if(!rootNode.hasNode(path)){
				this.establishBaseNode(path, mySession);
			}
			
			if(rootNode.hasNode(path + "/" + qtr)){
				//Remove quarter specific finance node if one exists
				Node tempNode = rootNode.getNode(path + "/" + qtr);
				tempNode.remove();
				
			}
			Node financeNode = rootNode.addNode(path + "/" + qtr, "nt:unstructured");
			Node expensesNode = financeNode.addNode("expenses");
			Node incomeNode = financeNode.addNode("income");
			this.populateFinanceChildren(incomeNode, expensesNode, params.get("expenses")[0], params.get("income")[0]);
			
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
	
	
	public FinanceConfiguration getFinanceConfiguration(Troop troop) { 

		Session mySession = null;
		FinanceConfiguration financeConfig = new FinanceConfiguration();
		try {
			mySession = sessionFactory.getSession();
			String configPath = "/" + troop.getTroopPath() + FinanceConfiguration.FINANCE_CONFIG;
			Node configNode = mySession.getNode(configPath);
			List<String> expensesList = new ArrayList<String>();
			List<String> incomeList = new ArrayList<String>();
			Value[] expensesValues = configNode.getProperty(Finance.EXPENSES).getValues();
			Value[] incomeValues = configNode.getProperty(Finance.INCOME).getValues();
			for(Value tempValue : expensesValues){
				expensesList.add(tempValue.getString());
			}
			
			for(Value tempValue : incomeValues){
				incomeList.add(tempValue.getString());
			}
			
			financeConfig.setExpenseFields(expensesList);
			financeConfig.setIncomeFields(incomeList);
			
			
			

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

	public void setFinanceConfiguration(Troop troop, String income, String expenses) {

		// TODO PERMISSIONS HERE
		Session mySession = null;
		try {
			mySession = sessionFactory.getSession();
			Node rootNode = mySession.getRootNode();
			String configPath = troop.getTroopPath() + FinanceConfiguration.FINANCE_CONFIG;
			if(!rootNode.hasNode(configPath)){
				this.establishBaseNode(configPath, mySession);
			}
			
			Node configNode = mySession.getNode("/" + configPath);
			String[] incomeFields = income.replaceAll("\\[|\\]", "").split(",");
			String[] expensesFields = expenses.replaceAll("\\[|\\]", "").split(",");
			configNode.setProperty(Finance.INCOME, incomeFields);
			configNode.setProperty(Finance.EXPENSES, expensesFields);
			
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
	
	//Populate the two nodes expenses and income with the properties and values enetered into the finance form
	private void populateFinanceChildren(Node incomeNode, Node expensesNode, String expensesParams, String incomeParams) throws RepositoryException{
		String[] expenses = expensesParams.replaceAll("\\[|\\]", "").replaceAll("/", "#").split(",");
		String[] income = incomeParams.replaceAll("\\[|\\]", "").replaceAll("/", "#").split(",");
	
	
		for(int i = 0; i < expenses.length; i = i + 2){
			
			String fieldName = expenses[i].trim();
			String fieldValue = expenses[i + 1].trim();
			System.err.println("Field Name: " + fieldName + " Field Value: " + fieldValue);
			expensesNode.setProperty(fieldName, fieldValue);
		}
		
		for(int i = 0; i < income.length; i = i + 2){
			
			String fieldName = income[i].trim();
			String fieldValue = income[i + 1].trim();
			System.err.println("Field Name: " + fieldName + " Field Value: " + fieldValue);
			incomeNode.setProperty(fieldName, fieldValue);
		}
	}
	
	private void establishBaseNode(String path, Session session) throws RepositoryException{
		Node rootNode = session.getRootNode();
		String[] pathElements = path.split("/");
		Node currentNode = rootNode.getNode("vtk");
		for(int i = 1; i < pathElements.length; i++){
			
			if(currentNode.hasNode(pathElements[i])){
				currentNode = currentNode.getNode(pathElements[i]);
			
			} else{
				System.err.println("#####Trying to add node: " + pathElements[i]);
				currentNode = currentNode.addNode(pathElements[i], "nt:unstructured");	
				
			}
		}
		
		
	}
	
	private String replaceIllegalChars(String value){
		String result = value;
		result = result.replaceAll("/","#");
		return result;
	}
	
	private String restoreIllegalChars(String value){
		String result = value;
		result = result.replaceAll("#", "/");
		return result;
	}
	
	
	
	

}// ednclass

