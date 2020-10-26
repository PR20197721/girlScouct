<%@page session="false" import="com.day.cq.wcm.api.Page,
                                org.apache.commons.lang.StringUtils,
                                org.apache.sling.api.resource.Resource,
                                org.apache.sling.jcr.api.SlingRepository,
                                org.girlscouts.vtk.osgi.component.util.VTKDataMigrationUtil,
                                org.slf4j.Logger,
                                org.slf4j.LoggerFactory,
                                javax.jcr.Node,
                                javax.jcr.Session, javax.jcr.query.*, java.util.HashMap, java.util.Iterator, java.util.Map, java.util.concurrent.TimeUnit" %>
<%
%>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%!
    public static final String THREAD_NAME = "vtk-update-vs2-thread";
    public static final String RUNNABLE_NAME = "runnable";
%>
<%
    VTKDataMigrationUtil  vtkDataMigrationUtil = sling.getService(VTKDataMigrationUtil.class);
    ServletContext ctxt = application.getContext("/apps/girlscouts-vtk/components/vtk-vs2-activity-reg-migration");
    String cmd = (request.getParameter("cmd") != null) ? request.getParameter("cmd") : "";
    boolean threadExists = ctxt.getAttribute(THREAD_NAME) != null;
    boolean threadIsAlive = threadExists && ((Thread) (ctxt.getAttribute(THREAD_NAME))).isAlive();
    boolean dryRun = false;
    if (request.getParameter("dry_run") != null) {
        dryRun = true;
    }
    if ("stop".equals(cmd) && threadIsAlive) {
        ((MigrateVtkActivityRegUrlThread) (ctxt.getAttribute(RUNNABLE_NAME))).requestStop();
        ((Thread) (ctxt.getAttribute(THREAD_NAME))).join();
        %>stopped<%
    } else {
        if (!threadIsAlive && "run".equals(cmd)) {
            SlingRepository repository = sling.getService(SlingRepository.class);
            MigrateVtkActivityRegUrlThread wft = new MigrateVtkActivityRegUrlThread(getServletContext(), dryRun, vtkDataMigrationUtil,  repository);
            Thread t = new Thread(wft);
            ctxt.setAttribute(THREAD_NAME, t);
            ctxt.setAttribute(RUNNABLE_NAME, wft);
            t.start();
            %>started<%
        } else {
            if (threadIsAlive) {
                %>running<%
            } else {
                if (!threadIsAlive) {
                    %>not running<%
                }
            }
        }
    }
%><%!
    public class MigrateVtkActivityRegUrlThread implements Runnable {
        private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
        private ServletContext ctxt;
        private volatile boolean stop;
        private boolean dryRun = true;
        private SlingRepository repository;
        private Map<String,String> activityIdMap = new HashMap<>();
        private String activityRegPattern = "";


        public MigrateVtkActivityRegUrlThread(ServletContext ctxt, boolean dryRun, VTKDataMigrationUtil vtkDataMigrationUtil, SlingRepository repository) {
            this.repository = repository ;
            this.ctxt = ctxt;
            this.dryRun = dryRun;
            this.activityIdMap = vtkDataMigrationUtil.getActivityIdMapping();
            this.activityRegPattern = vtkDataMigrationUtil.getActivityRegPattern();
        }

        public void requestStop() {
            log.debug("Requesting stop");
            this.stop = true;
        }

        public void run() {
            long startTime = System.currentTimeMillis();
            try {
                log.info("Running MigrateVtkActivityRegUrlThread " + ((this.dryRun) ? " (Dry Run)" : ""));
                //if(activityIdMap != null && activityIdMap.size() > 0) {
                    Session jcrSession = null;
                    try {
                        jcrSession = repository.loginAdministrative(null);
                        String sql = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([/vtk2020]) and CONTAINS(s.[refUid], 'sf-events-repository') and CONTAINS(s.[registerUrl], '"+this.activityRegPattern+"')";
                        QueryManager qm = jcrSession.getWorkspace().getQueryManager();
                        Query q = qm.createQuery(sql, Query.JCR_SQL2);
                        QueryResult result = q.execute();
                        for (javax.jcr.query.RowIterator it = result.getRows(); it.hasNext(); ) {
                            if(this.stop){
                                log.debug("Stopping MigrateVtkActivityRegUrlThread");
                                return;
                            }
                            Row r = it.nextRow();
                            Node activity = r.getNode();
                            try {
                                if(activity.hasProperty("eid")){
                                    String eid = activity.getProperty("eid").getString();
                                    if (!StringUtils.isBlank(eid) && activity.hasProperty("registerUrl")) {
                                        String refUid = activity.getProperty("refUid").getString();
                                        String searchPath = refUid.substring(0,refUid.lastIndexOf("/"));
                                        Node importedActivity = getEvent(searchPath,eid,jcrSession);
                                        Node data = importedActivity.getNode("jcr:content/data");
                                        if(data.hasProperty("registerUrl")){
                                            String newRegisterUrl = data.getProperty("registerUrl").getString();
                                            if(!StringUtils.isBlank(newRegisterUrl)) {
                                                activity.setProperty("registerUrl", newRegisterUrl);
                                                if(this.stop){
                                                    log.debug("Stopping MigrateVtkActivityRegUrlThread");
                                                    return;
                                                }
                                                if(!this.dryRun) {
                                                    activity.getSession().save();
                                                }
                                                log.debug("{} after update registerUrl = {}",activity.getPath(),newRegisterUrl);
                                            }
                                        }
                                    }
                                }else{
                                    log.debug("No eid found on activity {}", activity.getPath());
                                }
                            } catch (Exception e) {
                                log.error("Exception occurred updating reg url for {}", activity.getPath());
                            }
                        }
                    } catch (Exception e) {
                        log.error("Error Occurred: ", e);
                    } finally {
                        try {
                            if (jcrSession != null) {
                                ((Session) jcrSession).logout();
                            }
                        } catch (Exception e) {
                            log.error("Exception is thrown closing resource resolver: ", e);
                        }
                    }
                /*}else{
                    log.debug("tempRegUrl is not set");
                }*/
            } catch (Exception ie) {
                log.info("MigrateVtkActivityRegUrlThread: interrupted ", ie);
            } finally {
                disposeOfThread();
                long endTime = System.currentTimeMillis();
                long timeElapsed = endTime - startTime;
                String time = String.format("%02d min, %02d sec", TimeUnit.MILLISECONDS.toMinutes(timeElapsed), TimeUnit.MILLISECONDS.toSeconds(timeElapsed) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeElapsed)));
                log.debug("Total Execution time: {}",time);
            }
        }

        private Node getEvent(String activityYearFolder, String id, Session jcrSession) {
            try {
                String sql = "SELECT * FROM [cq:Page] AS s WHERE ISDESCENDANTNODE(s, '" + activityYearFolder + "') AND s.[jcr:content/data/eid]='" + id + "'";
                QueryManager qm = jcrSession.getWorkspace().getQueryManager();
                Query q = qm.createQuery(sql, Query.JCR_SQL2);
                log.debug("Executing JCR-SQL2 query: " + sql);
                QueryResult result = q.execute();
                for (RowIterator it = result.getRows(); it.hasNext(); ) {
                    Row r = it.nextRow();
                    Node activity = r.getNode();
                    return activity;
                }

            } catch (Exception e) {
                log.error("Error occured:" + e);
            }
            return null;
        }
        public void disposeOfThread() {
            synchronized (this.ctxt) {
                log.debug("Disposing Thread "+THREAD_NAME+" "+RUNNABLE_NAME);
                this.ctxt.removeAttribute(THREAD_NAME);
                this.ctxt.removeAttribute(RUNNABLE_NAME);
            }
        }
    }
%>