<%@page session="false" import="org.apache.sling.jcr.api.SlingRepository, org.slf4j.Logger, org.slf4j.LoggerFactory, javax.jcr.Node, javax.jcr.NodeIterator, javax.jcr.Session, javax.jcr.query.*, java.util.concurrent.TimeUnit, org.girlscouts.web.osgi.service.WebToLeadMigration" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%!
    public static final String THREAD_NAME = "web-update-web-to-lead-thread";
    public static final String RUNNABLE_NAME = "runnable";
%>
<%!
    public class MigrateWebToLeadForms implements Runnable {
        private final Logger log = LoggerFactory.getLogger("girlscouts.components.web-to-lead-vs2-migration");
        private ServletContext ctxt;
        private volatile boolean stop;
        private boolean dryRun = true;
        private SlingRepository repository;
        private WebToLeadMigration webToLeadMigration;


        public MigrateWebToLeadForms(ServletContext ctxt, boolean dryRun, SlingRepository repository, WebToLeadMigration webToLeadMigration) {
            this.repository = repository ;
            this.ctxt = ctxt;
            this.dryRun = dryRun;
            this.webToLeadMigration = webToLeadMigration;
        }

        public void requestStop() {
            log.debug("Requesting stop");
            this.stop = true;
        }

        public void run() {
            long startTime = System.currentTimeMillis();
            try {
                log.info("Running MigrateWebToLeadForms " + ((this.dryRun) ? " (Dry Run)" : ""));
                Session s = null;
                try {
                    s = repository.loginAdministrative(null) ;
                    QueryManager qm = s.getWorkspace().getQueryManager();
                    Node content = s.getNode("/content");
                    NodeIterator contentChildren = content.getNodes();
                    while(contentChildren.hasNext()){
                        if(this.stop){
                            log.debug("Stopping MigrateWebToLeadForms");
                            return;
                        }
                        Node site = contentChildren.nextNode();
                        if(site.hasProperty("jcr:primaryType") && site.getProperty("jcr:primaryType").getString().equals("cq:Page") ){
                            String sql = "SELECT * FROM [nt:unstructured] as s where ISDESCENDANTNODE(["+site.getPath()+"])AND s.[actionType]='girlscouts/components/form/actions/web-to-lead'";
                            log.debug("executing "+sql);
                            Query q = qm.createQuery(sql, "JCR-SQL2");
                            QueryResult result = q.execute();
                            RowIterator rowIter = result.getRows();
                            while (rowIter.hasNext()) {
                                if(this.stop){
                                    log.debug("Stopping MigrateWebToLeadForms");
                                    return;
                                }
                                try {
                                    Row row = rowIter.nextRow();
                                    Node node = row.getNode();
                                    webToLeadMigration.migrateWebToLeadForm(node.getPath(), dryRun);
                                }catch(Exception e){
                                    log.error("Error Occurred: ", e);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("Error Occurred: ", e);
                } finally {
                    log.debug("closing session");
                    try {
                        if (s != null) {
                            s.logout();
                        }
                    } catch (Exception e) {
                        log.error("Exception is thrown closing resource resolver: ", e);
                    }
                }
            } catch (Exception ie) {
                log.error("MigrateWebToLeadForms: interrupted ", ie);
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
<%
    ServletContext ctxt = application.getContext("/apps/girlscouts/components/web-to-lead-vs2-migration");
    String cmd = (request.getParameter("cmd") != null) ? request.getParameter("cmd") : "";
    boolean threadExists = ctxt.getAttribute(THREAD_NAME) != null;
    boolean threadIsAlive = threadExists && ((Thread) (ctxt.getAttribute(THREAD_NAME))).isAlive();
    boolean dryRun = false;
    if (request.getParameter("dry_run") != null) {
        dryRun = true;
    }
    if ("stop".equals(cmd) && threadIsAlive) {
        ((MigrateWebToLeadForms) (ctxt.getAttribute(RUNNABLE_NAME))).requestStop();
        ((Thread) (ctxt.getAttribute(THREAD_NAME))).join();
%>stopped<%
} else {
    if (!threadIsAlive && "run".equals(cmd)) {
        SlingRepository repository = sling.getService(SlingRepository.class);
        WebToLeadMigration webToLeadMigration = sling.getService(WebToLeadMigration.class);
        MigrateWebToLeadForms wft = new MigrateWebToLeadForms(getServletContext(), dryRun, repository, webToLeadMigration);
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
%>