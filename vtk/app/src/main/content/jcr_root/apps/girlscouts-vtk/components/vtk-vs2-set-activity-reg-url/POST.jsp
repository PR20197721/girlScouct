<%@page session="false" import="org.apache.sling.jcr.api.SlingRepository,
                                org.girlscouts.vtk.osgi.component.util.VTKActivitySetRegPathMigrationUtil,
                                org.slf4j.Logger,
                                org.slf4j.LoggerFactory,
                                javax.jcr.Node,
                                javax.jcr.Session,
                                javax.jcr.query.Query,
                                javax.jcr.query.QueryManager,
                                javax.jcr.query.QueryResult, javax.jcr.query.Row, java.util.concurrent.TimeUnit, org.apache.commons.lang.StringUtils" %>
<%
%>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%!
    public static final String THREAD_NAME = "vtk-update-vs2-thread";
    public static final String RUNNABLE_NAME = "runnable";
%>
<%
    VTKActivitySetRegPathMigrationUtil  regPathUtil = sling.getService(VTKActivitySetRegPathMigrationUtil.class);
    ServletContext ctxt = application.getContext("/apps/girlscouts-vtk/components/vtk-vs2-set-activity-reg-url");
    String cmd = (request.getParameter("cmd") != null) ? request.getParameter("cmd") : "";
    boolean threadExists = ctxt.getAttribute(THREAD_NAME) != null;
    boolean threadIsAlive = threadExists && ((Thread) (ctxt.getAttribute(THREAD_NAME))).isAlive();
    boolean dryRun = false;
    if (request.getParameter("dry_run") != null) {
        dryRun = true;
    }
    if ("stop".equals(cmd) && threadIsAlive) {
        ((SetTempSFActivityRegistrationURL) (ctxt.getAttribute(RUNNABLE_NAME))).requestStop();
        ((Thread) (ctxt.getAttribute(THREAD_NAME))).join();
        %>stopped<%
    } else {
        if (!threadIsAlive && "run".equals(cmd)) {
            SlingRepository repository = sling.getService(SlingRepository.class);
            SetTempSFActivityRegistrationURL wft = new SetTempSFActivityRegistrationURL(getServletContext(), dryRun, regPathUtil,  repository);
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
    public class SetTempSFActivityRegistrationURL implements Runnable {
        private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
        private ServletContext ctxt;
        private volatile boolean stop;
        private boolean dryRun = true;
        private SlingRepository repository;
        private String tempRegUrl;

        public SetTempSFActivityRegistrationURL(ServletContext ctxt, boolean dryRun, VTKActivitySetRegPathMigrationUtil  regPathUtil, SlingRepository repository) {
            this.repository = repository ;
            this.ctxt = ctxt;
            this.dryRun = dryRun;
            this.tempRegUrl = regPathUtil.tempRegUrl();
        }

        public void requestStop() {
            log.debug("Requesting stop");
            this.stop = true;
        }

        public void run() {
            long startTime = System.currentTimeMillis();
            try {
                log.info("Running SetTempSFActivityRegistrationURLThread " + ((this.dryRun) ? " (Dry Run)" : "") + " with tempRegUrl=" +tempRegUrl);
                if(!StringUtils.isBlank(tempRegUrl)) {
                    Session jcrSession = null;
                    try {
                        jcrSession = repository.loginAdministrative(null);
                        String sql = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([/vtk2020]) and CONTAINS(s.[refUid], 'sf-events-repository')";
                        QueryManager qm = jcrSession.getWorkspace().getQueryManager();
                        Query q = qm.createQuery(sql, Query.JCR_SQL2);
                        QueryResult result = q.execute();
                        for (javax.jcr.query.RowIterator it = result.getRows(); it.hasNext(); ) {
                            if(this.stop){
                                log.debug("Stopping SetTempSFActivityRegistrationURLThread");
                                return;
                            }
                            Row r = it.nextRow();
                            Node activity = r.getNode();
                            try {
                                if (activity.hasProperty("registerUrl")) {
                                    String oldRegUrl = activity.getProperty("registerUrl").getString();
                                    log.debug("{} before update registerUrl = {}",activity.getPath(),oldRegUrl);
                                    activity.setProperty("registerUrl",tempRegUrl);
                                    if(this.stop){
                                        log.debug("Stopping SetTempSFActivityRegistrationURLThread");
                                        return;
                                    }
                                    activity.getSession().save();
                                    log.debug("{} after update registerUrl = {}",activity.getPath(),tempRegUrl);
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
                }else{
                    log.debug("tempRegUrl is not set");
                }
            } catch (Exception ie) {
                log.info("SetTempSFActivityRegistrationURLThread: interrupted ", ie);
            } finally {
                disposeOfThread();
                long endTime = System.currentTimeMillis();
                long timeElapsed = endTime - startTime;
                String time = String.format("%02d min, %02d sec", TimeUnit.MILLISECONDS.toMinutes(timeElapsed), TimeUnit.MILLISECONDS.toSeconds(timeElapsed) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeElapsed)));
                log.debug("Total Execution time: {}",time);
            }
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