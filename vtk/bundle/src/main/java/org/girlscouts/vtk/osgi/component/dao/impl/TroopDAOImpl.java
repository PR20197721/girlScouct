package org.girlscouts.vtk.osgi.component.dao.impl;

import com.day.cq.commons.jcr.JcrConstants;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.util.Text;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.exception.VtkException;
import org.girlscouts.vtk.models.*;
import org.girlscouts.vtk.modifiedcheck.ModifiedChecker;
import org.girlscouts.vtk.osgi.component.dao.CouncilDAO;
import org.girlscouts.vtk.osgi.component.dao.MeetingDAO;
import org.girlscouts.vtk.osgi.component.dao.TroopDAO;
import org.girlscouts.vtk.osgi.component.util.UserUtil;
import org.girlscouts.vtk.osgi.component.util.VtkUtil;
import org.girlscouts.vtk.osgi.service.*;
import org.girlscouts.vtk.utils.MeetingESortOrderComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.util.*;

@Component
@Service(value = TroopDAO.class)
public class TroopDAOImpl implements TroopDAO {
    private final Logger log = LoggerFactory.getLogger(getClass());
    @Reference
    private UserUtil userUtil;
    @Reference
    private ModifiedChecker modifiedChecker;
    @Reference
    private CouncilDAO councilDAO;
    @Reference
    private MeetingDAO meetingDAO;
    @Reference
    private GirlScoutsTroopOCMService girlScoutsTroopOCMService;
    @Reference
    private GirlScoutsYearPlanOCMService girlScoutsYearPlanOCMService;
    @Reference
    private GirlScoutsAssetOCMService girlScoutsAssetOCMService;
    @Reference
    private GirlScoutsLocationOCMService girlScoutsLocationOCMService;
    @Reference
    private GirlScoutsActivityOCMService girlScoutsActivityOCMService;
    @Reference
    private GirlScoutsCalOCMService girlScoutsCalOCMService;
    @Reference
    private GirlScoutsMeetingEOCMService girlScoutsMeetingEOCMService;
    @Reference
    private GirlScoutsNoteOCMService girlScoutsNoteOCMService;
    @Reference
    private GirlScoutsMeetingCancelledOCMService girlScoutsMeetingCancelledOCMService;
    @Reference
    private GirlScoutsJCRService girlScoutsRepoService;
    @Reference
    private ResourceResolverFactory resolverFactory;
    private Map<String, Object> resolverParams = new HashMap<String, Object>();

    @Activate
    void activate() {
        this.resolverParams.put(ResourceResolverFactory.SUBSERVICE, "vtkService");
    }

    public Troop getTroopByPath(User user, String troopPath) throws IllegalAccessException {
        Troop troop = null;
        // TODO Permission.PERMISSION_VIEW_YEARPLAN_ID
        try {
            troop = girlScoutsTroopOCMService.read(troopPath);
            if (troop != null && troop.getYearPlan().getMeetingEvents() != null) {
                Collections.sort(troop.getYearPlan().getMeetingEvents(), new MeetingESortOrderComparator());
            }
        } catch (Exception e) {
            log.error("Error Occurred: ", e);
        }
        return troop;
    }

    public YearPlan addYearPlan1(User user, Troop troop, String yearPlanPath) throws IllegalAccessException {
        // permission to update
        if (troop != null && !userUtil.hasPermission(troop, Permission.PERMISSION_ADD_YEARPLAN_ID)) {
            throw new IllegalAccessException();
        }
        YearPlan plan = null;
        try {
            plan = girlScoutsYearPlanOCMService.read(yearPlanPath);
            if (plan != null && plan.getCalFreq() == null) {
                plan.setCalFreq("biweekly");
            }
        } catch (Exception e) {
            log.error("Error Occurred: ", e);
        }
        return plan;
    }

    public void rmTroop(Troop troop) throws IllegalAccessException {
        try {
            // permission to update
            if (troop != null && !userUtil.hasPermission(troop, Permission.PERMISSION_RM_YEARPLAN_ID)) {
                throw new IllegalAccessException();
            }
            if (troop != null) {
                girlScoutsTroopOCMService.delete(troop);
            }
        } catch (Exception e) {
            log.error("Error Occurred: ", e);
        }
    }

    public Finance getFinances(Troop troop, int qtr, String currentYear) {
        Finance result = null;
        try {
            result = new Finance();
            result.setFinancialQuarter(qtr);
            String path = "/" + troop.getTroopPath() + "/finances/" + currentYear + "/" + qtr;
            ResourceResolver rr = null;
            try {
                rr = resolverFactory.getServiceResourceResolver(resolverParams);
                Session session = rr.adaptTo(Session.class);
                Node financeNode = session.getNode(path);
                Node incomeNode = financeNode.getNode("income");
                Node expensesNode = financeNode.getNode("expenses");
                PropertyIterator incomeFieldIterator = incomeNode.getProperties();
                PropertyIterator expensesFieldIterator = expensesNode.getProperties();
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
            } catch (Exception ex) {
                log.error("Error occurred:", ex);
            } finally {
                try {
                    if (rr != null) {
                        rr.close();
                    }
                } catch (Exception e) {
                    log.error("Exception is thrown closing resource resolver: ", e);
                }
            }
        } catch (Exception e) {
            log.error("Error Occurred: ", e);
        }
        return result;
    }

    public void setFinances(Troop troop, int qtr, String currentYear, java.util.Map<String, String[]> params) {
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Session session = rr.adaptTo(Session.class);
            String path = troop.getTroopPath() + "/finances/" + currentYear;
            Node financesNode = JcrUtils.getOrCreateByPath(path, JcrConstants.NT_UNSTRUCTURED, session);
            if (financesNode.hasNode(String.valueOf(qtr))) {
                session.removeItem(path + "/" + qtr);
            }
            Node expensesNode = JcrUtils.getOrCreateByPath(path + "/expenses" + qtr, JcrConstants.NT_UNSTRUCTURED, session);
            Node incomeNode = JcrUtils.getOrCreateByPath(path + "/income", JcrConstants.NT_UNSTRUCTURED, session);
            populateFinanceChildren(expensesNode, incomeNode, params.get("expenses")[0], params.get("income")[0]);
        } catch (Exception e) {
            log.error("Error Occurred: ", e);
        } finally {
            try {
                if (rr != null) {
                    rr.close();
                }
            } catch (Exception e) {
                log.error("Exception is thrown closing resource resolver: ", e);
            }
        }
    }

    public FinanceConfiguration getFinanceConfiguration(Troop troop, String currentYear) {
        FinanceConfiguration financeConfig = new FinanceConfiguration();
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Session session = rr.adaptTo(Session.class);
            String councilPath = "/" + troop.getCouncilPath();
            String configPath = councilPath + "/" + FinanceConfiguration.FINANCE_CONFIG + "/" + currentYear;
            Node configNode = JcrUtils.getOrCreateByPath(councilPath + "/" + FinanceConfiguration.FINANCE_CONFIG, JcrConstants.NT_UNSTRUCTURED, session);
            List<String> expensesList = new ArrayList<String>();
            List<String> incomeList = new ArrayList<String>();
            if (configNode.hasProperty(Finance.EXPENSES) && configNode.getProperty(Finance.EXPENSES) != null) {
                Value[] expensesValues = configNode.getProperty(Finance.EXPENSES).getValues();
                for (Value tempValue : expensesValues) {
                    expensesList.add(tempValue.getString());
                }
                financeConfig.setExpenseFields(expensesList);
                financeConfig.setPersisted(true);
            }
            if (configNode.hasProperty(Finance.INCOME) && configNode.getProperty(Finance.INCOME) != null) {
                Value[] incomeValues = configNode.getProperty(Finance.INCOME).getValues();
                for (Value tempValue : incomeValues) {
                    incomeList.add(tempValue.getString());
                }
                financeConfig.setIncomeFields(incomeList);
                financeConfig.setPersisted(true);
            }
            if (configNode.hasProperty(Finance.PERIOD) && configNode.getProperty(Finance.PERIOD) != null) {
                String period = configNode.getProperty(Finance.PERIOD).getString();
                financeConfig.setPeriod(period);
                financeConfig.setPersisted(true);
            }
            if (configNode.hasProperty(FinanceConfiguration.RECIPIENT) && configNode.getProperty(FinanceConfiguration.RECIPIENT) != null) {
                String recipient = configNode.getProperty(FinanceConfiguration.RECIPIENT).getString();
                financeConfig.setRecipient(recipient);
                financeConfig.setPersisted(true);
            }
        } catch (Exception e) {
            log.error("Error Occurred: ", e);
        } finally {
            try {
                if (rr != null) {
                    rr.close();
                }
            } catch (Exception e) {
                log.error("Exception is thrown closing resource resolver: ", e);
            }
        }
        return financeConfig;
    }

    public void setFinanceConfiguration(Troop troop, String currentYear, String income, String expenses, String period, String recipient) {
        // TODO PERMISSIONS HERE
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Session session = rr.adaptTo(Session.class);
            String configPath = troop.getCouncilPath() + "/" + FinanceConfiguration.FINANCE_CONFIG + "/" + currentYear;
            Node configNode = session.getNode(configPath);
            if (configNode == null) {
                configNode = JcrUtils.getOrCreateByPath(configPath, JcrConstants.NT_UNSTRUCTURED, session);
            }
            String[] incomeFields = income.replaceAll("\\[|\\]", "").split(",");
            String[] expensesFields = expenses.replaceAll("\\[|\\]", "").split(",");
            configNode.setProperty(Finance.INCOME, incomeFields);
            configNode.setProperty(Finance.EXPENSES, expensesFields);
            configNode.setProperty(Finance.PERIOD, period);
            configNode.setProperty(FinanceConfiguration.RECIPIENT, recipient);
        } catch (Exception e) {
            log.error("Error Occurred: ", e);
        } finally {
            try {
                if (rr != null) {
                    rr.close();
                }
            } catch (Exception e) {
                log.error("Exception is thrown closing resource resolver: ", e);
            }
        }
    }

    // Populate the two nodes expenses and income with the properties and values
    // enetered into the finance form
    private void populateFinanceChildren(Node incomeNode, Node expensesNode, String expensesParams, String incomeParams) throws RepositoryException {
        String[] expenses = expensesParams.replaceAll("\\[|\\]", "").split(",");
        String[] income = incomeParams.replaceAll("\\[|\\]", "").split(",");
        Map<String, String> incomeParamsMap = new HashMap<>();
        Map<String, String> expensesParamsMap = new HashMap<>();
        for (int i = 0; i < expenses.length; i = i + 2) {
            String fieldName = expenses[i].trim();
            fieldName = Text.escapeIllegalJcrChars(fieldName);
            String fieldValue = expenses[i + 1].trim();
            expensesParamsMap.put(fieldName, fieldValue);
            expensesNode.setProperty(fieldName, fieldValue);
        }
        for (int i = 0; i < income.length; i = i + 2) {
            String fieldName = income[i].trim();
            fieldName = Text.escapeIllegalJcrChars(fieldName);
            String fieldValue = income[i + 1].trim();
            incomeNode.setProperty(fieldName, fieldValue);
        }
    }

    public boolean updateTroop(User user, Troop troop) throws IllegalAccessException, VtkException {
        modifyTroop(user, troop);
        if (troop.getYearPlan().getPath() == null || !troop.getYearPlan().getPath().startsWith(troop.getPath())) {
            troop.getYearPlan().setPath(troop.getPath() + "/yearPlan");
        }
        modifyYearPlan(user, troop);
        modifySchedule(user, troop);
        List<Location> locations = troop.getYearPlan().getLocations();
        if (locations != null) {
            for (int i = 0; i < locations.size(); i++) {
                Location location = locations.get(i);
                if (location.getPath() == null) {
                    location.setPath(troop.getYearPlan().getPath() + "/locations/" + location.getUid());
                }
                modifyLocation(user, troop, locations.get(i));
            }
        }
        if (troop.getYearPlan().getActivities() != null) {
            for (int i = 0; i < troop.getYearPlan().getActivities().size(); i++) {
                modifyActivity(user, troop, troop.getYearPlan().getActivities().get(i));
            }
        }
        if (troop.getYearPlan().getMeetingEvents() != null) {
            for (int i = 0; i < troop.getYearPlan().getMeetingEvents().size(); i++) {
                MeetingE meeting = troop.getYearPlan().getMeetingEvents().get(i);
                if (meeting.getPath() == null || !meeting.getPath().startsWith(troop.getYearPlan().getPath())) {
                    meeting.setPath(troop.getYearPlan().getPath() + "/meetingEvents/" + meeting.getUid());
                }
                modifyMeeting(user, troop, meeting);
                modifyNotes(user, troop, meeting.getNotes());
                List<Asset> assets = meeting.getAssets();
                if (assets != null) {
                    for (int y = 0; y < assets.size(); y++) {
                        Asset asset = assets.get(y);
                        if (asset.getPath() == null) {
                            asset.setPath(meeting.getPath() + "/assets/" + asset.getUid());
                        }
                        modifyAsset(user, troop, asset);
                    }
                }
            }
        }
        // canceled meeting
        if (troop.getYearPlan().getMeetingCanceled() != null) {
            for (int i = 0; i < troop.getYearPlan().getMeetingCanceled().size(); i++) {
                MeetingCanceled meeting = troop.getYearPlan().getMeetingCanceled().get(i);
                if (meeting.getPath() == null || !meeting.getPath().startsWith(troop.getYearPlan().getPath())) {
                    meeting.setPath(troop.getYearPlan().getPath() + "/meetingCanceled/" + meeting.getUid());
                }
                modifyMeetingCanceled(user, troop, meeting);
                List<Asset> assets = meeting.getAssets();
                if (assets != null) {
                    for (int y = 0; y < assets.size(); y++) {
                        Asset asset = assets.get(y);
                        if (asset.getPath() == null) {
                            asset.setPath(meeting.getPath() + "/assets/" + asset.getUid());
                        }
                        modifyAsset(user, troop, asset);
                    }
                }
            }
        }
        // modif
        try {
            modifiedChecker.setModified(user.getSid(), troop.getYearPlan().getPath());
        } catch (Exception em) {
            log.error("Error occurred:", em);
        }
        return false;
    }

    public boolean modifyAsset(User user, Troop troop, Asset asset) {
        if (!asset.isDbUpdate()) {
            return true;
        }
        boolean isUpdated = false;
        try {
            // check council
            /*if (councilDAO.findCouncil(user, troop.getCouncilPath()) == null) {
                throw new VtkException("Found no council when creating troop# " + troop.getTroopPath());
            }*/
            // check troop
            if (getTroopByPath(user, troop.getPath()) == null) {
                throw new VtkException("Found no troop when creating asset# " + troop.getTroopPath());
            }
            // check meeting
            MeetingE meeting = meetingDAO.getMeetingE(user, troop, asset.getPath().substring(0, asset.getPath().lastIndexOf("/")).replace("/assets", ""));
            if (meeting == null) {
                throw new VtkException("Found no troop when creating asset# " + troop.getTroopPath());
            }
            if (asset.getPath() == null) {
                asset.setPath(meeting.getPath() + "/assets/" + asset.getUid());
            }
            if (girlScoutsAssetOCMService.read(asset.getPath()) == null) {
                girlScoutsAssetOCMService.create(asset);
            } else {
                girlScoutsAssetOCMService.update(asset);
            }
            isUpdated = true;
        } catch (Exception e) {
            log.error("Error Occurred: ", e);
        }
        return isUpdated;
    }

    public boolean modifyMeeting(User user, Troop troop, MeetingE meeting) {
        if (!meeting.isDbUpdate()) {
            return true;
        }
        boolean isUpdated = false;
        try {
            // check council
            /*if (councilDAO.findCouncil(user, troop.getCouncilPath()) == null) {
                throw new VtkException("Found no council when creating troop# " + troop.getTroopPath());
            }*/
            // check troop
            if (getTroopByPath(user, troop.getPath()) == null) {
                throw new VtkException("Found no troop when creating sched# " + troop.getTroopPath());
            }
            if (meeting.getPath() == null) {
                meeting.setPath(troop.getYearPlan().getPath() + "/meetingEvents/" + meeting.getUid());
            }
            if (girlScoutsMeetingEOCMService.read(meeting.getPath()) == null) {
                girlScoutsMeetingEOCMService.create(meeting);
            } else {
                girlScoutsMeetingEOCMService.update(meeting);
            }
            isUpdated = true;
        } catch (Exception e) {
            log.error("Error Occurred: ", e);
        }
        return isUpdated;
    }

    public boolean modifyActivity(User user, Troop troop, Activity activity) {
        if (!activity.isDbUpdate()) {
            return true;
        }
        boolean isUpdated = false;
        try {
            // check council
            /*if (councilDAO.findCouncil(user, troop.getCouncilPath()) == null) {
                throw new VtkException("Found no council when creating troop# " + troop.getTroopPath());
            }*/
            // check troop
            if (getTroopByPath(user, troop.getPath()) == null) {
                throw new VtkException("Found no troop when creating sched# " + troop.getTroopPath());
            }
            if (activity.getPath() == null) {
                activity.setPath(troop.getYearPlan().getPath() + "/activities/" + activity.getUid());
            }
            if (girlScoutsActivityOCMService.read(activity.getPath()) == null) {
                girlScoutsActivityOCMService.create(activity);
            } else {
                girlScoutsActivityOCMService.update(activity);
            }
            isUpdated = true;
        } catch (Exception e) {
            log.error("Error Occurred: ", e);
        }
        return isUpdated;
    }

    public boolean modifyLocation(User user, Troop troop, Location location) {
        if (!location.isDbUpdate()) {
            return true;
        }
        boolean isUpdated = false;
        try {
            // check council
            /*if (councilDAO.findCouncil(user, troop.getCouncilPath()) == null) {
                throw new VtkException("Found no council when creating troop# " + troop.getTroopPath());
            }*/
            // check troop
            if (getTroopByPath(user, troop.getPath()) == null) {
                throw new VtkException("Found no troop when creating sched# " + troop.getTroopPath());
            }
            if (location.getPath() == null) {
                location.setPath(troop.getYearPlan().getPath() + "/locations/" + location.getUid());
            }
            if (girlScoutsLocationOCMService.read(location.getPath()) == null) {
                girlScoutsLocationOCMService.create(location);
            } else {
                girlScoutsLocationOCMService.update(location);
            }
            isUpdated = true;
        } catch (Exception e) {
            log.error("Error Occurred: ", e);
        }
        return isUpdated;
    }

    public boolean modifySchedule(User user, Troop troop) {
        Cal schedule = troop.getYearPlan().getSchedule();
        if (schedule == null || !schedule.isDbUpdate()) {
            return true;
        }
        boolean isUpdated = false;
        try {
            if (schedule.getDates() == null) {// remove the schedule to reset
                // the yearplan
                if (schedule.getPath() == null) {
                    schedule.setPath(troop.getYearPlan().getPath() + "/schedule");
                }
                if (girlScoutsCalOCMService.read(schedule.getPath()) != null) {
                    girlScoutsCalOCMService.delete(schedule);
                }
                troop.getYearPlan().setSchedule(null);
            } else {
                /*if (councilDAO.findCouncil(user, troop.getCouncilPath()) == null) {
                    throw new VtkException("Found no council when creating troop# " + troop.getTroopPath());
                }*/
                // check troop
                if (getTroopByPath(user, troop.getPath()) == null) {
                    throw new VtkException("Found no troop when creating sched# " + troop.getTroopPath());
                }
                if (schedule.getPath() == null) {
                    schedule.setPath(troop.getYearPlan().getPath() + "/schedule");
                }
                if (girlScoutsCalOCMService.read(schedule.getPath()) != null) {
                    girlScoutsCalOCMService.update(schedule);
                } else {
                    girlScoutsCalOCMService.create(schedule);
                }
            }
            isUpdated = true;
        } catch (Exception e) {
            log.error("Error Occurred: ", e);
        }
        return isUpdated;
    }

    public boolean modifyYearPlan(User user, Troop troop) {
        YearPlan yearPlan = troop.getYearPlan();
        if (!yearPlan.isDbUpdate()) {
            return true;
        }
        boolean isUpdated = false;
        try {
            if (girlScoutsYearPlanOCMService.read(troop.getYearPlan().getPath()) == null) {
                girlScoutsYearPlanOCMService.create(yearPlan);
            } else {
                girlScoutsYearPlanOCMService.update(yearPlan);
            }
            isUpdated = true;
        } catch (Exception e) {
            log.error("Error Occurred: ", e);
        }
        return isUpdated;
    }

    public boolean modifyTroop(User user, Troop troop) throws VtkException {
        if (!troop.isDbUpdate()) {
            return true;
        }
        boolean isUpdated = false;
        try {
            if (troop == null || troop.getYearPlan() == null) {
                return true;
            }
            //Council council = councilDAO.findCouncil(user, troop.getCouncilPath());
            //if (council == null) {
            //throw new VtkException("Found no council when creating troop# " + troop.getTroopPath());
            //}
            if (troop != null && troop.getYearPlan() != null && troop.getYearPlan().getMeetingEvents() != null) {
                Collections.sort(troop.getYearPlan().getMeetingEvents(), new MeetingESortOrderComparator());
            }
            // permission to update
            if (troop != null && !userUtil.hasPermission(troop, Permission.PERMISSION_VIEW_YEARPLAN_ID)) {
                throw new IllegalAccessException();
            }
            troop.setCurrentTroop(user.getSid());// 10/23/14 Documenting the
            if (girlScoutsTroopOCMService.read(troop.getPath()) != null) {
                girlScoutsTroopOCMService.update(troop);
            } else {
                girlScoutsTroopOCMService.create(troop);
            }
            String old_errCode = troop.getErrCode();
            try {
                troop.setErrCode(null);
                try {
                    modifiedChecker.setModified(user.getSid(), troop.getYearPlan().getPath());
                } catch (Exception em) {
                    log.error("Error occurred: ", em);
                }
                isUpdated = true;
                troop.setRefresh(true);
            } catch (Exception e) {
                log.error("Error occurred while saving troop: ", e);
                troop.setErrCode(old_errCode);
                troop.setRefresh(true);
            }
        } catch (Exception e) {
            log.error("Error Occurred: ", e);
        }
        return isUpdated;
    }

    public boolean removeActivity(User user, Troop troop, Activity activity) {
        boolean isUpdated = false;
        try {
            isUpdated = girlScoutsActivityOCMService.delete(activity);
        } catch (Exception e) {
            log.error("Error Occurred: ", e);
        }
        return isUpdated;
    }

    public boolean removeMeeting(User user, Troop troop, MeetingE meeting) {
        boolean isUpdated = false;
        try {
            isUpdated = girlScoutsMeetingEOCMService.delete(meeting);
        } catch (Exception e) {
            log.error("Error Occurred: ", e);
        }
        return isUpdated;
    }

    public boolean removeAsset(User user, Troop troop, Asset asset) {
        boolean isUpdated = false;
        try {
            isUpdated = girlScoutsAssetOCMService.delete(asset);
        } catch (Exception e) {
            log.error("Error Occurred: ", e);
        }
        return isUpdated;
    }

    public boolean removeMeetings(User user, Troop troop) {
        boolean isUpdated = false;
        try {
            isUpdated = girlScoutsRepoService.removeNode(troop.getPath() + "/yearPlan/meetingEvents");
        } catch (Exception e) {
            log.error("Error Occurred: ", e);
        }
        return isUpdated;
    }

    public boolean modifyMeetingCanceled(User user, Troop troop, MeetingCanceled meeting) {
        if (!meeting.isDbUpdate()) {
            return true;
        }
        boolean isUpdated = false;
        try {
            // check council
            /*if (councilDAO.findCouncil(user, troop.getCouncilPath()) == null) {
                throw new VtkException("Found no council when creating troop# " + troop.getTroopPath());
            }*/
            // check troop
            if (getTroopByPath(user, troop.getPath()) == null) {
                throw new VtkException("Found no troop when creating sched# " + troop.getTroopPath());
            }
            if (meeting.getPath() == null) {
                meeting.setPath(troop.getYearPlan().getPath() + "/meetingCanceled/" + meeting.getUid());
            }
            if (girlScoutsMeetingCancelledOCMService.read(meeting.getPath()) != null) {
                girlScoutsMeetingCancelledOCMService.update(meeting);
            } else {
                girlScoutsMeetingCancelledOCMService.create(meeting);
            }
            isUpdated = true;
        } catch (Exception e) {
            log.error("Error Occurred: ", e);
        }
        return isUpdated;
    }

    public java.util.Map getArchivedYearPlans(User user, Troop troop) {
        java.util.Map container = new java.util.TreeMap();
        int currentGSYear = VtkUtil.getCurrentGSYear();
        for (int i = (currentGSYear - 1); i > (currentGSYear - 6); i--) {
            if (i == 2014) {
                if (isArchivedYearPlan(user, troop, "")) {
                    container.put(i, "/vtk/");
                }
            } else {
                if (isArchivedYearPlan(user, troop, i + "")) {
                    container.put(i, "/vtk" + currentGSYear + "/");
                }
            }
        }
        return container;
    }

    public boolean isArchivedYearPlan(User user, Troop troop, String year) {
        if (user == null || troop == null) {
            return false;
        }
        boolean isArchived = false;
        try {
            String currentYearPlanPath = troop.getTroopPath() + "/yearPlan";
            String archivedYearPlanPath = "/vtk" + year +"/"+ currentYearPlanPath.substring(8);
            if (girlScoutsYearPlanOCMService.read(archivedYearPlanPath) != null) {
                isArchived = true;
            }
        } catch (Exception e) {
            log.error("Error Occurred: ", e);
        }
        return isArchived;
    }

    public boolean modifyNote(User user, Troop troop, Note note) {
        if (note == null || !note.isDbUpdate()) {
            return true;
        }
        boolean isUpdated = false;
        try {
            if (girlScoutsNoteOCMService.read(note.getPath()) != null) {
                girlScoutsNoteOCMService.update(note);
            } else {
                girlScoutsNoteOCMService.create(note);
            }
            isUpdated = true;
        } catch (Exception e) {
            log.error("Error Occurred: ", e);
        }
        return isUpdated;
    }

    public void modifyNotes(User user, Troop troop, java.util.List<Note> notes) {
        if (notes != null) {
            for (int i = 0; i < notes.size(); i++) {
                modifyNote(user, troop, notes.get(i));
            }
        }
    }
}

