package org.girlscouts.vtk.ejb;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.manager.impl.ObjectContentManagerImpl;
import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.AnnotationMapperImpl;
import org.apache.jackrabbit.ocm.query.Filter;
import org.apache.jackrabbit.ocm.query.Query;
import org.apache.jackrabbit.ocm.query.QueryManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.girlscouts.vtk.dao.CouncilDAO;
import org.girlscouts.vtk.models.*;
import org.girlscouts.vtk.osgi.component.CouncilMapper;
import org.girlscouts.vtk.utils.ModifyNodePermissions;
import org.girlscouts.vtk.utils.VtkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Service(value = CouncilDAO.class)
public class CouncilDAOImpl implements CouncilDAO {
    private final Logger log = LoggerFactory.getLogger("vtk");
    @Reference
    CouncilMapper councilMapper;
    @Reference
    CouncilRpt councilRpt;
    @Reference
    private SessionFactory sessionFactory;
    @Reference
    private UserUtil userUtil;
    @Reference
    private ModifyNodePermissions permiss;

    private Mapper mapper;

    @Activate
    void activate() {
        List<Class> classes = new ArrayList<Class>();
        classes.add(Council.class);
        mapper = new AnnotationMapperImpl(classes);
    }

    public Council findCouncil(User user, String path) {
        Council council = null;
        Session session = null;
        ResourceResolver rr = null;
        try {
            //TODO Permission.PERMISSION_LOGIN_ID
            rr = sessionFactory.getResourceResolver();
            session = rr.adaptTo(Session.class);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
            council = (Council) ocm.getObject(path);
        } catch (Exception e) {
            log.error("Error occured:", e);
        } finally {
            try {
                if (rr != null) {
                    sessionFactory.closeResourceResolver(rr);
                }
                if (session != null) {
                    session.logout();
                }
            } catch (Exception ex) {
                log.error("Error occured:", ex);
            }
        }

        return council;
    }

    public Council createCouncil(User user, Troop troop) {
        //TODO Permission.PERMISSION_LOGIN_ID
        Session session = null;
        ResourceResolver rr = null;
        Council council = null;
        try {
            List<Class> classes = new ArrayList<Class>();
            classes.add(Council.class);
            classes.add(YearPlan.class);
            classes.add(MeetingE.class);
            classes.add(Location.class);
            classes.add(Cal.class);
            classes.add(Activity.class);
            classes.add(Asset.class);
            classes.add(SentEmail.class);
            classes.add(JcrNode.class);
            classes.add(Milestone.class);
            classes.add(Troop.class);
            Mapper mapper = new AnnotationMapperImpl(classes);
            String vtkCouncilBase = troop.getCouncilPath();
            vtkCouncilBase = vtkCouncilBase.substring(0, vtkCouncilBase.lastIndexOf("/"));
            rr = sessionFactory.getResourceResolver();
            session = rr.adaptTo(Session.class);
            if (!session.itemExists(vtkCouncilBase)) {
                JcrUtils.getOrCreateByPath(vtkCouncilBase, "nt:unstructured", session);
                permiss.modifyNodePermissions(vtkCouncilBase, "vtk");
                permiss.modifyNodePermissions("/content/dam/girlscouts-vtk/troop-data" + vtkCouncilBase + "/", "vtk");
            }
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
            if (!ocm.objectExists(troop.getCouncilPath())) {
                council = new Council();
                council.setPath(troop.getCouncilPath());
                ocm.insert(council);
                ocm.save();
            } else {
                log.debug(">>>>>>>>>>> INFO : CouncilDAOImpl.createCouncil skipped because council already exists: " + troop.getCouncilPath());
                council = (Council) ocm.getObject(Council.class, troop.getCouncilPath());
            }
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        } finally {
            try {
                if (session != null) {
                    session.logout();
                }
                if (rr != null) {
                    sessionFactory.closeResourceResolver(rr);
                }
            } catch (Exception ex) {
                log.error("Error occurred: ", ex);
            }
        }
        return council;

    }

    public Council getOrCreateCouncil(User user, Troop troop) {
        //TODO Permission.PERMISSION_LOGIN_ID
        Council councilRepoData = findCouncil(user, troop.getCouncilPath());
        if (councilRepoData == null) {
            councilRepoData = createCouncil(user, troop);
        }
        return councilRepoData;
    }

    public List<Milestone> getCouncilMilestones(User user, Troop troop) {
        //TODO Permission.PERMISSION_VIEW_MILESTONE_ID
        CouncilInfo list = getCouncilInfo(user, troop);
        List<Milestone> milestones = list.getMilestones();
        sortMilestonesByDate(milestones);
        return milestones;
    }

    private CouncilInfo getCouncilInfo(User user, Troop troop) {
        Session session = null;
        ResourceResolver rr = null;
        CouncilInfo cinfo = null;
        try {
            rr = sessionFactory.getResourceResolver();
            session = rr.adaptTo(Session.class);
            List<Class> classes = new ArrayList<Class>();
            classes.add(Milestone.class);
            classes.add(CouncilInfo.class);
            Mapper mapper = new AnnotationMapperImpl(classes);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
            QueryManager queryManager = ocm.getQueryManager();
            Filter filter = queryManager.createFilter(CouncilInfo.class);
            Query query = queryManager.createQuery(filter);
            String path = troop.getCouncilPath() + "/councilInfo";
            if (session.itemExists(path)) {
                cinfo = (CouncilInfo) ocm.getObject(path);
                if (cinfo == null) {
                    log.error("OCM failed to retrieve " + troop.getCouncilPath() + "/councilInfo !!!");
                }
                if (cinfo.getMilestones() == null) {
                    List<Milestone> milestones = getAllMilestones(troop.getCouncilCode());
                    cinfo.setMilestones(milestones);
                    ocm.update(cinfo);
                    ocm.save();
                }
            } else {
                if (!session.itemExists(troop.getCouncilPath())) {
                    // create council, need user permission
                    log.error(troop.getCouncilPath() + "does NOT exist!!!");
                }
                cinfo = new CouncilInfo(path);
                List<Milestone> milestones = getAllMilestones(troop.getCouncilCode());
                cinfo.setMilestones(milestones);
                ocm.insert(cinfo);
                ocm.save();
            }
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        } finally {
            try {
                if (session != null) {
                    session.logout();
                }
                if (rr != null) {
                    sessionFactory.closeResourceResolver(rr);
                }
            } catch (Exception ex) {
                log.error("Error occurred: ", ex);
            }
        }
        return cinfo;
    }

    public void updateCouncilMilestones(User user, List<Milestone> milestones, Troop troop) throws IllegalAccessException {
        //TODO: permissions here Permission.PERMISSION_EDIT_MILESTONE_ID
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = sessionFactory.getResourceResolver();
            session = rr.adaptTo(Session.class);
            List<Class> classes = new ArrayList<Class>();
            classes.add(Milestone.class);
            classes.add(CouncilInfo.class);
            Mapper mapper = new AnnotationMapperImpl(classes);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
            CouncilInfo list = getCouncilInfo(user, troop);
            java.util.List<Milestone> oldMilestones = list.getMilestones();
            sortMilestonesByDate(oldMilestones);
            int i = 0;
            for (; i < oldMilestones.size(); i++) {
                if (milestones.size() > i && oldMilestones.get(i).getBlurb().equals(milestones.get(i).getBlurb())) {
                    oldMilestones.get(i).setDate(milestones.get(i).getDate());
                    oldMilestones.get(i).setShow(milestones.get(i).getShow());
                    continue;
                } else {
                    oldMilestones.remove(i);
                    i--;
                }
            }
            for (int k = i; k < milestones.size(); k++) {
                oldMilestones.add(milestones.get(k));
            }
            list.setMilestones(oldMilestones);
            ocm.update(list);
            ocm.save();
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        } finally {
            try {
                if (session != null) {
                    session.logout();
                }
                if (rr != null) {
                    sessionFactory.closeResourceResolver(rr);
                }
            } catch (Exception ex) {
                log.error("Error occurred: ", ex);
            }
        }
    }


    private List<Milestone> getAllMilestones(String councilCode) {
        String councilPath = councilMapper.getCouncilBranch(councilCode);
        List<Milestone> milestones = new ArrayList<Milestone>();
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = sessionFactory.getResourceResolver();
            session = rr.adaptTo(Session.class);
            List<Class> classes = new ArrayList<Class>();
            classes.add(Milestone.class);
            Mapper mapper = new AnnotationMapperImpl(classes);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
            if (session.itemExists(councilPath + "/en/milestones")) {
                QueryManager queryManager = ocm.getQueryManager();
                Filter filter = queryManager.createFilter(Milestone.class);
                filter.setScope(councilPath + "/en/milestones//");
                Query query = queryManager.createQuery(filter);
                milestones = (List<Milestone>) ocm.getObjects(query);
                String newGSYearDateString = "";
                try {
                    newGSYearDateString = VtkUtil.getNewGSYearDateString();
                    DateFormat format = new SimpleDateFormat("MMdd");
                    // TODO: use joda time
                    Date newGSYearDate = format.parse(newGSYearDateString);
                    newGSYearDate.setYear(new Date().getYear());
                    Date startDate, endDate;
                    if (new Date().compareTo(newGSYearDate) >= 0) {
                        // New troop year has started for this calendar year.
                        // Current school year is this year to next year.
                        startDate = newGSYearDate;
                        endDate = new Date(newGSYearDate.getTime());
                        endDate.setYear(new Date().getYear() + 1);
                    } else {
                        // New troop year has NOT started for this calendar year.
                        // Current school year is last year to this year.
                        endDate = newGSYearDate;
                        startDate = new Date(newGSYearDate.getTime());
                        startDate.setYear(new Date().getYear() - 1);
                    }
                    List<Milestone> filteredMilestones = new ArrayList<Milestone>();
                    for (Milestone milestone : milestones) {
                        if (milestone.getDate().before(endDate) && milestone.getDate().compareTo(startDate) >= 0) {
                            filteredMilestones.add(milestone);
                        }
                    }
                    milestones = filteredMilestones;
                } catch (ParseException pe) {
                    log.warn("ParseException MMdd for new year " + newGSYearDateString + "\n" + "No milestones have been filtered.", pe);
                }
                sortMilestonesByDate(milestones);
            } else {
                log.error(councilPath + "/en/milestones does NOT exist!");
                return milestones;
            }
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        } finally {
            try {
                if (session != null) {
                    session.logout();
                }
                if (rr != null) {
                    sessionFactory.closeResourceResolver(rr);
                }
            } catch (Exception ex) {
                log.error("Error occurred: ", ex);
            }
        }
        return milestones;
    }

    private void sortMilestonesByDate(List<Milestone> milestones) {
        Comparator<Milestone> comp = new Comparator<Milestone>() {
            public int compare(Milestone m1, Milestone m2) {
                if (m1.getDate() != null) {
                    if (m2.getDate() != null) {
                        return m1.getDate().compareTo(m2.getDate());
                    } else {
                        return -1;
                    }
                } else if (m2.getDate() != null) {
                    return 1;
                } else {
                    return m1.getBlurb().compareTo(m2.getBlurb());
                }
            }

        };
        Collections.sort(milestones, comp);
    }

    public void GSMonthlyRpt() {
        Session session = null;
        ResourceResolver rr = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat format1 = new SimpleDateFormat("MM-dd-yyyy");
        Set<String> allowedReportUsers = new HashSet<String>();
        allowedReportUsers.add("005g0000002apMT");
        allowedReportUsers.add("005G0000006oEkZ");
        allowedReportUsers.add("005G0000006oBVG");
        allowedReportUsers.add("005g0000002G004");
        boolean isHtml = false;
        java.util.Map<String, String> cTrans = new java.util.TreeMap();
        cTrans.put("597", "Girl Scouts of Northeast Texas");
        cTrans.put("477", "Girl Scouts of Minnesota and Wisconsin River Valleys, Inc.");
        cTrans.put("465", "Girl Scouts of Southeastern Michigan");
        cTrans.put("367", "Girl Scouts - North Carolina Coastal Pines, Inc.");
        cTrans.put("320", "Girl Scouts of West Central Florida, Inc.");
        cTrans.put("388", "Girl Scout Council of the Southern Appalachians, Inc.");
        cTrans.put("313", "Girl Scouts of Gateway Council, Inc.");
        cTrans.put("664", "Oregon and SW Washington");
        cTrans.put("234", "North East Ohio");
        cTrans.put("661", "Sierra Nevada");
        cTrans.put("664", "Oregon & SW Wash");
        cTrans.put("240", "Western Ohio");
        cTrans.put("607", "Arizona Cactus Pine");
        cTrans.put("536", "Kansas Heartland");
        cTrans.put("563", "Western Oklahoma");
        cTrans.put("564", "Eastern Oklahoma");
        cTrans.put("591", "San Jacinto");
        cTrans.put("636", "Northern CA");
        cTrans.put("512", "Colorado");
        cTrans.put("313", "Gateway");
        cTrans.put("212", "Kentucky Wilderness Road");
        cTrans.put("623", "San Diego");
        cTrans.put("131", "Central & Southern NJ");
        cTrans.put("263", "Western PA");
        cTrans.put("467", "Wisconsin Badgerland");
        cTrans.put("116", "Central & Western Mass");
        cTrans.put("622", "Orange County");
        cTrans.put("660", "Southern Nevada");
        cTrans.put("514", "Eastern IA & Western IL");
        cTrans.put("524", "Greater Iowa");
        cTrans.put("430", "Greater Chicago and NW  Indiana");
        cTrans.put("578", "Central Texas");
        cTrans.put("208", "Kentuckiana");
        cTrans.put("700", "USA Girl Scouts Overseas");
        cTrans.put("204", "Nation's Capital");
        cTrans.put("674", "Utah");
        cTrans.put("258", "Heart of Pennsylvania");
        cTrans.put("333", "Greater Atlanta");
        cTrans.put("135", "Heart of New Jersey");
        cTrans.put("289", "Black Diamond");
        cTrans.put("155", "Heart of the Hudson");
        cTrans.put("325", "Historic Georgia");
        cTrans.put("608", "Southern Arizona");
        cTrans.put("312", "Citrus");
        cTrans.put("169", "NYPENN Pathways");
        cTrans.put("596", "Greater South Texas");
        cTrans.put("583", "Texas Oklahoma Plains");
        cTrans.put("688", "Western Washington");
        cTrans.put("590", "Southwest Texas");
        cTrans.put("634", "Heart of Central California");
        cTrans.put("376", "Eastern South Carolina");
        cTrans.put("346", "Louisiana East");
        cTrans.put("117", "Eastern Massachusetts");
        cTrans.put("654", "Montana and Wyoming");
        cTrans.put("134", "Jersey Shore");
        cTrans.put("415", "Northern Illinois");
        cTrans.put("557", "New Mexico Trails");
        cTrans.put("110", "Maine");
        cTrans.put("126", "Green and White Mountains");
        cTrans.put("687", "Eastern Washington and Northern Idaho");
        cTrans.put("441", "Southwest Indiana");
        cTrans.put("238", "Ohio's Heartland");
        StringBuffer sb = new StringBuffer();
        try {
            rr = sessionFactory.getResourceResolver();
            session = rr.adaptTo(Session.class);
            sb.append("Council Report generated on " + format1.format(new java.util.Date()) + " \n ");
            String limitRptToCouncil = null;
            limitRptToCouncil = limitRptToCouncil == null ? "" : limitRptToCouncil.trim();
            HashSet<String> ageGroups = new HashSet<String>();
            String sql = "select  sfTroopName,sfTroopAge,jcr:path, sfTroopId,sfCouncil,excerpt(.) from nt:base where isdescendantnode( '" + VtkUtil.getYearPlanBase(null, null) + "" + (limitRptToCouncil.equals("") ? "" : (limitRptToCouncil + "/")) + "') and ocm_classname= 'org.girlscouts.vtk.models.Troop'";
            javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
            javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL);
            java.util.Map container = new java.util.TreeMap();
            javax.jcr.query.QueryResult result = q.execute();
            for (javax.jcr.query.RowIterator it = result.getRows(); it.hasNext(); ) {
                javax.jcr.query.Row r = it.nextRow();
                String path = r.getValue("jcr:path").getString();
                String sfCouncil = null, sfTroopAge = null;
                try {
                    sfCouncil = r.getValue("sfCouncil").getString();
                } catch (Exception e) {
                }
                try {
                    sfTroopAge = r.getValue("sfTroopAge").getString();
                } catch (Exception e) {
                }
                Integer counter = (Integer) container.get(sfCouncil + "|" + sfTroopAge);
                if (counter == null) {
                    container.put(sfCouncil + "|" + sfTroopAge, new Integer(0));
                } else {
                    container.put(sfCouncil + "|" + sfTroopAge, new Integer(counter.intValue() + 1));
                }
            }
            java.util.Iterator itr = container.keySet().iterator();
            while (itr.hasNext()) {
                String key = (String) itr.next();
                String councilId = key.substring(0, key.indexOf("|"));
                String ageGroup = key.substring(key.indexOf("|") + 1);
                Integer count = (Integer) container.get(key);
                sb.append((isHtml ? "<br/>" : "\n") + "\"" + cTrans.get(councilId) + "\"," + councilId + "," + ageGroup + "," + count);
            }
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        } finally {
            try {
                if (session != null) {
                    session.logout();
                }
                if (rr != null) {
                    sessionFactory.closeResourceResolver(rr);
                }
            } catch (Exception ex) {
                log.error("Error occurred: ", ex);
            }
        }
        //save to db
        String rptId = councilRpt.saveRpt(sb);
        //email rpt
        councilRpt.emailRpt(sb.toString(), "GS Monthly Report"); //"/vtk"+VtkUtil.getCurrentGSYear()+"/rpt/"+ rptId);
    }

    public void GSMonthlyDetailedRpt(String year) {
        Session session = null;
        ResourceResolver rr = null;
        Set<String> allowedReportUsers = new HashSet<String>();
        allowedReportUsers.add("005g0000002apMT");
        allowedReportUsers.add("005G0000006oEkZ");
        allowedReportUsers.add("005G0000006oBVG");
        allowedReportUsers.add("005g0000002G004");
        allowedReportUsers.add("005G0000006oEjsIAE");
        StringBuffer sb = new StringBuffer();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat format1 = new SimpleDateFormat("MM-dd-yyyy");
        sb.append("Council Report generated on " + format1.format(new java.util.Date()) + " \n");
        java.util.Map<String, String> cTrans = new java.util.TreeMap();
        cTrans.put("597", "Girl Scouts of Northeast Texas");
        cTrans.put("477", "Girl Scouts of Minnesota and Wisconsin River Valleys Inc.");
        cTrans.put("465", "Girl Scouts of Southeastern Michigan");
        cTrans.put("367", "Girl Scouts - North Carolina Coastal Pines Inc.");
        cTrans.put("320", "Girl Scouts of West Central Florida Inc.");
        cTrans.put("388", "Girl Scout Council of the Southern Appalachians Inc.");
        cTrans.put("313", "Girl Scouts of Gateway Council Inc.");
        cTrans.put("664", "Oregon and SW Washington");
        cTrans.put("234", "North East Ohio");
        cTrans.put("661", "Sierra Nevada");
        cTrans.put("664", "Oregon & SW Wash");
        cTrans.put("240", "Western Ohio");
        cTrans.put("607", "Arizona Cactus Pine");
        cTrans.put("536", "Kansas Heartland");
        cTrans.put("563", "Western Oklahoma");
        cTrans.put("564", "Eastern Oklahoma");
        cTrans.put("591", "San Jacinto");
        cTrans.put("636", "Northern CA");
        cTrans.put("512", "Colorado");
        cTrans.put("313", "Gateway");
        cTrans.put("212", "Kentucky Wilderness Road");
        cTrans.put("623", "San Diego");
        cTrans.put("131", "Central & Southern NJ");
        cTrans.put("263", "Western PA");
        cTrans.put("467", "Wisconsin Badgerland");
        cTrans.put("116", "Central & Western Mass");
        cTrans.put("622", "Orange County");
        cTrans.put("660", "Southern Nevada");
        cTrans.put("514", "Eastern IA & Western IL");
        cTrans.put("524", "Greater Iowa");
        cTrans.put("430", "Greater Chicago and NW  Indiana");
        cTrans.put("578", "Central Texas");
        cTrans.put("208", "Kentuckiana");
        cTrans.put("700", "USA Girl Scouts Overseas");
        cTrans.put("204", "Nation's Capital");
        cTrans.put("674", "Utah");
        cTrans.put("258", "Heart of Pennsylvania");
        cTrans.put("333", "Greater Atlanta");
        cTrans.put("135", "Heart of New Jersey");
        cTrans.put("289", "Black Diamond");
        cTrans.put("155", "Heart of the Hudson");
        cTrans.put("325", "Historic Georgia");
        cTrans.put("608", "Southern Arizona");
        cTrans.put("312", "Citrus");
        cTrans.put("169", "NYPENN Pathways");
        cTrans.put("596", "Greater South Texas");
        cTrans.put("583", "Texas Oklahoma Plains");
        cTrans.put("688", "Western Washington");
        cTrans.put("590", "Southwest Texas");
        cTrans.put("634", "Heart of Central California");
        cTrans.put("376", "Eastern South Carolina");
        cTrans.put("346", "Louisiana East");
        cTrans.put("117", "Eastern Massachusetts");
        cTrans.put("654", "Montana and Wyoming");
        cTrans.put("134", "Jersey Shore");
        cTrans.put("415", "Northern Illinois");
        cTrans.put("557", "New Mexico Trails");
        cTrans.put("110", "Maine");
        cTrans.put("126", "Green and White Mountains");
        cTrans.put("687", "Eastern Washington and Northern Idaho");
        cTrans.put("441", "Southwest Indiana");
        cTrans.put("238", "Ohio's Heartland");
        String limitRptToCouncil = null;//request.getParameter("limitRptToCouncil");
        limitRptToCouncil = limitRptToCouncil == null ? "" : limitRptToCouncil.trim();
        List<String> councils = VtkUtil.getCouncils();
        String gsYear = null;
        if (year == null || "".equals(year)) {
            gsYear = VtkUtil.getYearPlanBase(null, null);
        } else {
            gsYear = "/vtk" + year + "/";
        }
        try {
            rr = sessionFactory.getResourceResolver();
            session = rr.adaptTo(Session.class);
            for (String council : councils) {
                String sql = "select  sfTroopName,sfTroopAge,jcr:path, sfTroopId,sfCouncil,excerpt(.) from nt:unstructured where isdescendantnode( '" + gsYear + "" + council + "/') and ocm_classname= 'org.girlscouts.vtk.models.Troop' and id not like 'SHARED_%'";
                javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
                javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL);
                javax.jcr.query.QueryResult result = q.execute();
                for (javax.jcr.query.RowIterator it = result.getRows(); it.hasNext(); ) {
                    javax.jcr.query.Row r = it.nextRow();
                    javax.jcr.Node n = r.getNode();
                    javax.jcr.Node n1 = null;
                    try {
                        n1 = n.getNode("yearPlan");
                    } catch (javax.jcr.PathNotFoundException pnfe) {
                        log.error("Year Plan path not found: ", pnfe);
                    }
                    if (n1 == null) {
                        log.debug("Year Plan doesnt exists. skipping record " + n.getPath());
                        continue;
                    }

                    String yearPlanName = "", sfCouncil = null, sfTroopAge = null, sfTroopName = null, sfTroopId = null;
                    try {
                        yearPlanName = n1.getProperty("name").getValue().getString();
                    } catch (Exception e) {
                    }
                    try {
                        sfTroopId = r.getValue("sfTroopId").getString();
                    } catch (Exception e) {
                    }
                    try {
                        sfTroopName = r.getValue("sfTroopName").getString();
                    } catch (Exception e) {
                    }
                    try {
                        sfCouncil = r.getValue("sfCouncil").getString();
                    } catch (Exception e) {
                    }
                    try {
                        sfTroopAge = r.getValue("sfTroopAge").getString();

                    } catch (Exception e) {
                    }
                    sb.append("\n \"" + cTrans.get(sfCouncil) + "\"," + sfCouncil + "," + sfTroopAge + ",\"" + yearPlanName + "\"," + sfTroopId + "," + sfTroopName);
                }//edn for
            }//edn for
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        } finally {
            try {
                if (session != null) {
                    session.logout();
                }
                if (rr != null) {
                    sessionFactory.closeResourceResolver(rr);
                }
            } catch (Exception ex) {
                log.error("Error occurred: ", ex);
            }
        }
        //email rpt
        councilRpt.emailRpt(sb.toString(), "GS Detailed Report");
    }

    public void GSRptCouncilPublishFinance() {
        Session session = null;
        ResourceResolver rr = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat format1 = new SimpleDateFormat("MM-dd-yyyy");
        Set<String> allowedReportUsers = new HashSet<String>();
        allowedReportUsers.add("005g0000002apMT");
        allowedReportUsers.add("005G0000006oEkZ");
        allowedReportUsers.add("005G0000006oBVG");
        allowedReportUsers.add("005g0000002G004");
        boolean isHtml = false;
        java.util.Map<String, String> cTrans = new java.util.TreeMap();
        cTrans.put("597", "Girl Scouts of Northeast Texas");
        cTrans.put("477", "Girl Scouts of Minnesota and Wisconsin River Valleys, Inc.");
        cTrans.put("465", "Girl Scouts of Southeastern Michigan");
        cTrans.put("367", "Girl Scouts - North Carolina Coastal Pines, Inc.");
        cTrans.put("320", "Girl Scouts of West Central Florida, Inc.");
        cTrans.put("388", "Girl Scout Council of the Southern Appalachians, Inc.");
        cTrans.put("313", "Girl Scouts of Gateway Council, Inc.");
        cTrans.put("664", "Oregon and SW Washington");
        cTrans.put("234", "North East Ohio");
        cTrans.put("661", "Sierra Nevada");
        cTrans.put("664", "Oregon & SW Wash");
        cTrans.put("240", "Western Ohio");
        cTrans.put("607", "Arizona Cactus Pine");
        cTrans.put("536", "Kansas Heartland");
        cTrans.put("563", "Western Oklahoma");
        cTrans.put("564", "Eastern Oklahoma");
        cTrans.put("591", "San Jacinto");
        cTrans.put("636", "Northern CA");
        cTrans.put("512", "Colorado");
        cTrans.put("313", "Gateway");
        cTrans.put("212", "Kentucky Wilderness Road");
        cTrans.put("623", "San Diego");
        cTrans.put("131", "Central & Southern NJ");
        cTrans.put("263", "Western PA");
        cTrans.put("467", "Wisconsin Badgerland");
        cTrans.put("116", "Central & Western Mass");
        cTrans.put("622", "Orange County");
        cTrans.put("660", "Southern Nevada");
        cTrans.put("514", "Eastern IA & Western IL");
        cTrans.put("524", "Greater Iowa");
        cTrans.put("430", "Greater Chicago and NW  Indiana");
        cTrans.put("578", "Central Texas");
        cTrans.put("208", "Kentuckiana");
        cTrans.put("700", "USA Girl Scouts Overseas");
        cTrans.put("204", "Nation's Capital");
        cTrans.put("674", "Utah");
        cTrans.put("258", "Heart of Pennsylvania");
        cTrans.put("333", "Greater Atlanta");
        cTrans.put("135", "Heart of New Jersey");
        cTrans.put("289", "Black Diamond");
        cTrans.put("155", "Heart of the Hudson");
        cTrans.put("325", "Historic Georgia");
        cTrans.put("608", "Southern Arizona");
        cTrans.put("312", "Citrus");
        cTrans.put("169", "NYPENN Pathways");
        cTrans.put("596", "Greater South Texas");
        cTrans.put("583", "Texas Oklahoma Plains");
        cTrans.put("688", "Western Washington");
        cTrans.put("590", "Southwest Texas");
        cTrans.put("634", "Heart of Central California");
        cTrans.put("376", "Eastern South Carolina");
        cTrans.put("346", "Louisiana East");
        cTrans.put("117", "Eastern Massachusetts");
        cTrans.put("654", "Montana and Wyoming");
        cTrans.put("134", "Jersey Shore");
        cTrans.put("415", "Northern Illinois");
        cTrans.put("557", "New Mexico Trails");
        cTrans.put("110", "Maine");
        cTrans.put("126", "Green and White Mountains");
        cTrans.put("687", "Eastern Washington and Northern Idaho");
        cTrans.put("441", "Southwest Indiana");
        cTrans.put("238", "Ohio's Heartland");
        StringBuffer sb = new StringBuffer();
        try {
            java.util.List<String> councils = getCouncils(VtkUtil.getYearPlanBase(null, null));
            rr = sessionFactory.getResourceResolver();
            session = rr.adaptTo(Session.class);
            for (int i = 0; i < councils.size(); i++) {
                String councilId = councils.get(i);
                java.util.Date submitTime = null;
                try {
                    String timePath = VtkUtil.getYearPlanBase(null, null) + "" + councilId + "/finances/template";
                    String timePathFinalized = VtkUtil.getYearPlanBase(null, null) + "" + councilId + "/finances/finalized";
                    if (!session.itemExists(timePathFinalized)) continue;
                    Node infoNode = session.getNode(timePath);
                    if (infoNode != null) {
                        try {
                            submitTime = new java.util.Date(infoNode.getProperty("submitTime").getLong());
                        } catch (Exception e) {
                        }
                    }
                } catch (Exception e) {
                    log.error("Error occurred: ", e);
                }
                sb.append((isHtml ? "<br/>" : "\n") + "\"" + cTrans.get(councilId) + "\"," + councilId + "," + (submitTime == null ? "N/A" : submitTime));
            }
            councilRpt.emailRpt(sb.toString(), "Report - Current # of Council's Who have Published Finance Form 2.0");
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        } finally {
            try {
                if (session != null) {
                    session.logout();
                }
                if (rr != null) {
                    sessionFactory.closeResourceResolver(rr);
                }
            } catch (Exception ex) {
                log.error("Error occurred: ", ex);
            }
        }
    }

    public List<String> getCouncils(String currYearPath) {
        List<String> councils = new ArrayList();
        Session session = null;
        ResourceResolver resourceResolver = null;
        try {
            resourceResolver = sessionFactory.getResourceResolver();
            session = resourceResolver.adaptTo(Session.class);
            Resource myResource = resourceResolver.getResource(currYearPath);
            if (myResource == null) {
                return null;
            }
            Iterable<Resource> rcouncils = myResource.getChildren();
            for (Resource rcouncil : rcouncils) {
                String councilPath = rcouncil.getPath();
                String council = councilPath.substring(councilPath.lastIndexOf("/") + 1);
                councils.add(council);
            }
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        } finally {
            try {
                if (session != null) {
                    session.logout();
                }
                if (resourceResolver != null) {
                    sessionFactory.closeResourceResolver(resourceResolver);
                }
            } catch (Exception ex) {
                log.error("Error occurred: ", ex);
            }
        }
        return councils;
    }
}
