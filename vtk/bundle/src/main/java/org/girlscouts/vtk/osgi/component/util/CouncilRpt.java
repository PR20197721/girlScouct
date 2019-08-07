package org.girlscouts.vtk.osgi.component.util;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.mail.ByteArrayDataSource;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.settings.SlingSettingsService;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.models.Contact;
import org.girlscouts.vtk.models.CouncilRptBean;
import org.girlscouts.vtk.models.YearPlanRpt;
import org.girlscouts.vtk.osgi.service.GirlScoutsSalesForceService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataSource;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.*;
import java.util.*;

@Component(service = {CouncilRpt.class}, immediate = true, name = "org.girlscouts.vtk.osgi.component.util.CouncilRpt")
@Designate(ocd = CouncilRptConfiguration.class)
public class CouncilRpt {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private CouncilRptConfiguration config;
    @Reference
    private MessageGatewayService messageGatewayService;
    @Reference
    private SlingSettingsService slingSettings;
    @Reference
    private GirlScoutsSalesForceService gsSalesForceService;
    @Reference
    private ResourceResolverFactory resolverFactory;
    private Map<String, Object> resolverParams = new HashMap<String, Object>();

    @Activate
    void activate(CouncilRptConfiguration config) {
        log.info("Girl Scouts VTK Council report generator activated.");
        this.config = config;
        this.resolverParams.put(ResourceResolverFactory.SUBSERVICE, "vtkService");
    }

    public List<String> getActivityRpt(String sfCouncil) {
        List<String> activities = new ArrayList<String>();
        String sql1 = "select jcr:path " + " from nt:base " + " where isdescendantnode( '/vtk" + VtkUtil.getCurrentGSYear() + "/" + sfCouncil + "/troops/') and ocm_classname='org.girlscouts.vtk.ocm.ActivityNode'";
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Session session = rr.adaptTo(Session.class);
            QueryManager qm = session.getWorkspace().getQueryManager();
            Query q = qm.createQuery(sql1, Query.SQL);
            QueryResult result = q.execute();
            for (RowIterator it = result.getRows(); it.hasNext(); ) {
                Row r = it.nextRow();
                String path = r.getValue("jcr:path").getString();
                if (path.indexOf("/yearPlan") != -1) {
                    String yp = path.substring(0, path.indexOf("/yearPlan") + 9);
                    if (!activities.contains(yp)) {
                        activities.add(yp);
                    }
                }
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
        return activities;
    }

    public List<CouncilRptBean> getRpt(String sfCouncil, ApiConfig config) {
        List<CouncilRptBean> container = new ArrayList<CouncilRptBean>();
        String sql = "SELECT s.* from [nt:unstructured] as s where ISDESCENDANTNODE([" + VtkUtil.getYearPlanBase(null, null) + sfCouncil + "/troops/]) and s.[ocm_classname] = 'org.girlscouts.vtk.ocm.YearPlanNode' and s.[jcr:path] not like '%/yearPlan-%'";
        List<String> activities = getActivityRpt(sfCouncil);
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Session session = rr.adaptTo(Session.class);
            QueryManager qm = session.getWorkspace().getQueryManager();
            Query q = qm.createQuery(sql, Query.JCR_SQL2);
            log.debug("Executing query: "+sql);
            QueryResult result = q.execute();
            if (result != null && result.getNodes().hasNext()) {
                for (NodeIterator it = result.getNodes(); it.hasNext(); ) {
                    try {
                        Node node = it.nextNode();
                        Node troop = node.getParent();
                        if(!troop.hasProperty("isIRM") || (troop.hasProperty("isIRM") && !troop.getProperty("isIRM").getBoolean())) {
                            String yearPlanName = "", libPath = "", ageGroup = "";
                            boolean isAltered = false;
                            String path = node.getPath();
                            try {
                                isAltered = node.getProperty("altered").getBoolean();
                            } catch (Exception e) {
                                log.error("Error occured:", e);
                            }
                            try {
                                yearPlanName = node.getProperty("name").getString();
                            } catch (Exception e) {
                                log.error("Error occured:", e);
                            }
                            try {
                                libPath = node.getProperty("refId").getString();
                            } catch (Exception e) {
                                log.error("Error occured:", e);
                            }
                            String troopName = "";
                            List<Contact> leaders = new ArrayList<Contact>();
                            try {
                                troopName = troop.getProperty("sfTroopName").getString();
                                String troopId = troop.getProperty("sfTroopId").getString();
                                if (config != null && troopId != null) {
                                    try {
                                        leaders.addAll(gsSalesForceService.getTroopLeaderInfoByTroopId(config, troopId));
                                    } catch (Exception e) {
                                        log.error("Unable to retrieve troop user from salesforce : " + node.getPath(), e);
                                    }
                                }
                            } catch (Exception e) {
                                log.error("Unable to resolve troop name for troop : " + node.getPath(), e);
                            }
                            if (troop != null && (libPath == null || libPath.equals("") || (yearPlanName != null && yearPlanName.trim().toLowerCase().equals("custom year plan")))) {
                                try {
                                    libPath = troop.getProperty("sfTroopAge").getString().toLowerCase().substring(2);
                                } catch (Exception e) {
                                    log.error("Error occured:", e);
                                }
                            }
                            if (libPath.contains("brownie")) {
                                ageGroup = "brownie";
                            } else if (libPath.contains("daisy")) {
                                ageGroup = "daisy";
                            } else if (libPath.contains("junior")) {
                                ageGroup = "junior";
                            } else if (libPath.contains("senior")) {
                                ageGroup = "senior";
                            } else if (libPath.contains("cadette")) {
                                ageGroup = "cadette";
                            } else if (libPath.contains("ambassador")) {
                                ageGroup = "ambassador";
                            } else if (libPath.contains("multi-level")) {
                                ageGroup = "multi-level";
                            }
                            CouncilRptBean crb = new CouncilRptBean();
                            crb.setYearPlanName(yearPlanName);
                            crb.setAltered(isAltered);
                            crb.setLibPath(libPath);
                            crb.setAgeGroup(ageGroup);
                            crb.setYearPlanPath(path);
                            crb.setTroopName(troopName);
                            crb.setTroopLeaders(leaders);
                            try {
                                crb.setTroopId(path.split("/")[4]);
                            } catch (Exception e) {
                                log.error("Error occured:", e);
                            }
                            if (activities.contains(path)) {
                                crb.setActivity(true);
                            }
                            container.add(crb);
                        }
                    } catch (Exception e) {
                        log.error("Error occured:", e);
                    }
                }
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
        return container;
    }

    public java.util.List<CouncilRptBean> getCollection_byAgeGroup(java.util.List<CouncilRptBean> results, final String ageGroup) {
        java.util.List<CouncilRptBean> container = (java.util.List<CouncilRptBean>) CollectionUtils.select(results, new Predicate<CouncilRptBean>() {
            public boolean evaluate(CouncilRptBean o) {
                return o.getAgeGroup().equals(ageGroup);
            }
        });
        return container;
    }

    public int countAltered(java.util.List<CouncilRptBean> results) {
        int countAltered = CollectionUtils.countMatches(results, new Predicate() {
            public boolean evaluate(Object o) {
                return ((CouncilRptBean) o).isAltered() == true;
            }
        });
        return countAltered;
    }

    public java.util.List<CouncilRptBean> getCollection_byYearPlanName(java.util.List<CouncilRptBean> results, final String ageGroup) {
        Collection<CouncilRptBean> container = CollectionUtils.select(results, new Predicate<CouncilRptBean>() {
            public boolean evaluate(CouncilRptBean o) {
                return o.getYearPlanName().equals(ageGroup);
            }
        });
        return (java.util.List<CouncilRptBean>) container;
    }

    public int countActivity(java.util.List<CouncilRptBean> results) {
        int countActivity = CollectionUtils.countMatches(results, new Predicate() {
            public boolean evaluate(Object o) {
                return ((CouncilRptBean) o).isActivity() == true;
            }
        });
        return countActivity;
    }

    public Map<String, String> getTroopNames(String councilId, String yearPlanPath) {
        Map<String, String> container = new TreeMap<String, String>();
        String sql = "select parent.sfTroopId, parent.sfTroopName from [nt:base] as parent INNER JOIN [nt:base] as child ON ISCHILDNODE(child, parent) " + " where (isdescendantnode (parent, [" + VtkUtil.getYearPlanBase(null, null) + councilId + "/troops/]))  and " + " parent.ocm_classname='org.girlscouts.vtk.ocm.TroopNode' and child.refId like '" + yearPlanPath + "%'";
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Session session = rr.adaptTo(Session.class);
            QueryManager qm = session.getWorkspace().getQueryManager();
            Query q = qm.createQuery(sql, Query.SQL);
            QueryResult result = q.execute();
            for (RowIterator it = result.getRows(); it.hasNext(); ) {
                Row r = it.nextRow();
                String troopId = r.getValue("parent.sfTroopId").getString();
                String troopName = r.getValue("parent.sfTroopName").getString();
                container.put(troopId, troopName);
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
        return container;
    }

    public Map<String, String> getDistinctPlanNamesPath(java.util.List<CouncilRptBean> results) {
        Map<String, String> container = new TreeMap<String, String>();
        for (CouncilRptBean bean : results) {
            container.put(bean.getLibPath(), bean.getYearPlanName());
        }
        return container;

    }

    public java.util.List<CouncilRptBean> getCollection_byYearPlanPath(java.util.List<CouncilRptBean> results, final String yearPlanPath) {
        Collection<CouncilRptBean> container = CollectionUtils.select(results, new Predicate<CouncilRptBean>() {
            public boolean evaluate(CouncilRptBean o) {
                return o.getLibPath().equals(yearPlanPath);
            }
        });
        return (java.util.List<CouncilRptBean>) container;
    }

    public Map<String, String> getDistinctPlanByName(java.util.List<CouncilRptBean> results) {
        Map<String, String> container = new TreeMap<String, String>();
        for (CouncilRptBean bean : results) {
            container.put(bean.getYearPlanName(), bean.getLibPath());
        }
        return container;
    }

    public String saveRpt(StringBuffer sb) {
        String rptId = "RPT" + new java.util.Date().getTime();
        int currYear = VtkUtil.getCurrentGSYear();
        javax.jcr.Node node = null;
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            Node rootNode = session.getNode("/vtk" + currYear);
            Node rptNode = null;
            if (!rootNode.hasNode("rpt")) {
                rptNode = rootNode.addNode("rpt", "nt:unstructured");
            } else {
                rptNode = session.getNode("/vtk" + currYear + "/rpt/");
            }
            Node thisRpt = rptNode.addNode(rptId, "nt:unstructured");
            thisRpt.setProperty("rpt", sb.toString());
            thisRpt.setProperty("id", rptId);
            session.save();
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
        return rptId;
    }

    public void emailRpt(String msg, String subject) {
        try {
            log.info("VTK Monthly Report Email Attempt Begin.");
            MessageGateway<MultiPartEmail> messageGateway = messageGatewayService.getGateway(MultiPartEmail.class);
            // create the mail
            MultiPartEmail email = new MultiPartEmail();
            for (String address : config.toEmailAddresses()) {
                email.addTo(address);
            }
            email.setFrom(config.fromEmailAddress());
            email.setSubject(subject + " (ENV:" + slingSettings.getRunModes() + ")");
            email.setMsg("Please find attached GS Report attached as of " + new java.util.Date());
            DataSource source = new ByteArrayDataSource(msg, "application/text");
            // add the attachment
            email.attach(source, "rpt.csv", "rpt");
            messageGateway.send(email);
            log.info("VTK Monthly Report Email Success!");
        } catch (Throwable e) {
            log.error("VTK Monthly Report Email Error");
            log.error("VTK Monthly Report Email Error: ", e);
        } finally {
            log.info("VTK Monthly Report Email Attempt End.");
        }
    }
}
