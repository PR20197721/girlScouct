<%@page session="false" import="com.day.cq.wcm.api.NameConstants,
                                com.day.cq.wcm.api.Page,
                                com.day.cq.wcm.api.PageManager,
                                com.day.cq.wcm.api.components.ComponentContext,
                                com.day.cq.wcm.api.components.EditContext,
                                com.day.cq.wcm.api.designer.Design,
                                com.day.cq.wcm.api.designer.Designer,
                                com.day.cq.wcm.api.designer.Style,
                                com.day.cq.wcm.commons.WCMUtils,
                                org.apache.sling.api.resource.Resource,
                                org.apache.sling.api.resource.ResourceResolver,
                                org.apache.sling.api.resource.ResourceResolverFactory,
                                org.apache.sling.api.resource.ValueMap,
                                org.apache.sling.jcr.api.SlingRepository,
                                org.slf4j.Logger,
                                org.slf4j.LoggerFactory,
                                javax.jcr.*,
                                javax.jcr.query.*,
                                java.util.HashMap,
                                java.util.HashSet,
                                java.util.Map,
                                java.util.Set,
                                java.util.Iterator" %>
<%
%>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%!
    public static final String THREAD_NAME = "thread";
    public static final String RUNNABLE_NAME = "runnable";
    public static ResourceResolverFactory resolverFactory;
%><%
    resolverFactory = sling.getService(ResourceResolverFactory.class);
    ServletContext ctxt = application.getContext("/apps/girlscouts-vtk/components/vtk-data-migration");
    String cmd = (request.getParameter("cmd") != null) ? request.getParameter("cmd") : "";
    boolean threadExists = ctxt.getAttribute(THREAD_NAME) != null;
    boolean threadIsAlive = threadExists && ((Thread) (ctxt.getAttribute(THREAD_NAME))).isAlive();
    boolean dryRun = false;
    if (request.getParameter("dry_run") != null) {
        dryRun = true;
    }
    if ("stop".equals(cmd) && threadIsAlive) {
        ((MigrateVtkDataThread) (ctxt.getAttribute(RUNNABLE_NAME))).requestStop();
        ((Thread) (ctxt.getAttribute(THREAD_NAME))).join();
%>stopped<%
} else if (!threadIsAlive && "run".equals(cmd)) {
    SlingRepository repository = sling.getService(SlingRepository.class);
    MigrateVtkDataThread wft = new MigrateVtkDataThread(getServletContext(), repository, dryRun);
    Thread t = new Thread(wft);
    ctxt.setAttribute(THREAD_NAME, t);
    ctxt.setAttribute(RUNNABLE_NAME, wft);
    t.start();
%>started<%
} else if (threadIsAlive) {
%>running<%
} else if (!threadIsAlive) {
%>not running<%
    }
%><%!
    public class MigrateVtkDataThread implements Runnable {
        private Set<String> paths = new HashSet<String>();
        private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
        private ServletContext ctxt;
        private volatile boolean stop;
        private boolean dryRun = true;
        private long currentTimeMs = 0;
        private long sleepMillis = 100;
        private long nodesPerSave = 1000;
        private long nodesSaved = 0;
        private long nodeSaveCounter = 0;
        private SlingRepository repository;
        private Session jcrSession ;
        private Map<String, Object> resolverParams = new HashMap<String, Object>();

        public MigrateVtkDataThread(ServletContext ctxt, SlingRepository repository, boolean dryRun) {
            this.ctxt = ctxt;
            this.repository = repository ;
            this.paths.add("/vtk2018");
            this.paths.add("/vtk2017");
            this.paths.add("/vtk2016");
            this.paths.add("/vtk2015");
            this.paths.add("/vtk");
            this.paths.add("/content/girlscouts-vtk");
            this.dryRun = dryRun;
            this.resolverParams.put(ResourceResolverFactory.SUBSERVICE, "vtkService");
            try {
                jcrSession = repository.loginAdministrative(null) ;
            } catch (RepositoryException e) {
                log.error("fixAssets interrupted due to exception", e);
            }
        }

        public void requestStop() {
            log.debug("Requesting stop");
            this.stop = true;
        }

        public void run() {
            //ResourceResolver rr = null;
            try {
                //rr = resolverFactory.getServiceResourceResolver(this.resolverParams);
                //Session session = rr.adaptTo(Session.class);
                log.info("Running MigrateVtkDataThread " + ((this.dryRun) ? " (Dry Run)" : ""));
                try {
                    QueryResult result = null;
                    currentTimeMs = System.currentTimeMillis();
                    for (String path : this.paths) {
                        try {
                            Node yearNode = this.jcrSession.getNode(path);
                            NodeIterator councils = yearNode.getNodes();
                            while (councils.hasNext()) {
                            Node councilNode = councils.nextNode();
                                if (councilNode.hasProperty("ocm_classname") || "/content/girlscouts-vtk".equals(path)) {
                                    if (councilNode.hasProperty("ocm_classname")) {
                                        String councilOcmClassName = councilNode.getProperty("ocm_classname").getString();
                                        if (!"org.girlscouts.vtk.ocm.CouncilNode".equals(councilOcmClassName)) {
                                            councilNode.setProperty("ocm_classname_backup", councilOcmClassName);
                                            councilOcmClassName = "org.girlscouts.vtk.ocm.CouncilNode";
                                            councilNode.setProperty("ocm_classname", councilOcmClassName);
                                            log.debug("Setting " + councilNode.getPath() + " with ocm_classname=" + councilOcmClassName);
                                            this.nodeSaveCounter++;
                                        }
                                    }
                                    String EXPRESSION1 = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([" + councilNode.getPath() + "]) and s.[ocm_classname] LIKE 'org.girlscouts.vtk.models%'";
                                    String EXPRESSION2 = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([" + councilNode.getPath() + "]) and s.[ocm_classname] LIKE 'org.girlscouts.vtk.dao%'";
                                    String EXPRESSION3 = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([" + councilNode.getPath() + "]) and s.[ocm_classname] = 'org.girlscouts.vtk.ocm.MeetingENode'";
                                    String EXPRESSION4 = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([" + councilNode.getPath() + "]) and s.[ocm_classname] = 'org.girlscouts.vtk.ocm.YearPlanNode'";
                                    if (!this.stop) {
                                        result = search(EXPRESSION1);
                                        if (result != null) {
                                            try {
                                                RowIterator rowIter = result.getRows();
                                                log.debug("Processing " + rowIter.getSize() + " nodes");
                                                while (rowIter.hasNext()) {
                                                    if (this.stop) {break;}
                                                    try {
                                                        Row row = rowIter.nextRow();
                                                        Node node = row.getNode();
                                                        String ocmClassName = node.getProperty("ocm_classname").getString();
                                                        node.setProperty("ocm_classname_backup", ocmClassName);
                                                        ocmClassName = ocmClassName.replace("org.girlscouts.vtk.models", "org.girlscouts.vtk.ocm") + "Node";
                                                        node.setProperty("ocm_classname", ocmClassName);
                                                        log.debug("Setting " + node.getPath() + " with ocm_classname=" + ocmClassName);
                                                        this.nodeSaveCounter++;
                                                        if ((!this.dryRun) && (this.nodeSaveCounter % this.nodesPerSave) == 0) {
                                                            log.debug("Saving " + this.nodesPerSave + " nodes");
                                                            this.jcrSession.save();
                                                            this.jcrSession.refresh(false);
                                                            this.nodesSaved = this.nodeSaveCounter;
                                                            try {
                                                                log.debug("Sleeping for " + this.sleepMillis + " milliseconds");
                                                                Thread.sleep(this.sleepMillis);
                                                            } catch (InterruptedException ie) {
                                                                requestStop();
                                                            }
                                                        }
                                                    } catch (Exception e) {
                                                    }
                                                }
                                                if ((!this.dryRun)) {
                                                    log.debug("Saving before next step");
                                                    this.jcrSession.save();
                                                    this.jcrSession.refresh(false);
                                                    this.nodesSaved = this.nodeSaveCounter;
                                                    try {
                                                        log.debug("Sleeping for " + this.sleepMillis + " milliseconds");
                                                        Thread.sleep(this.sleepMillis);
                                                    } catch (InterruptedException ie) {
                                                        requestStop();
                                                    }
                                                }
                                            } catch (Exception e1) {
                                            }
                                        }
                                    }
                                    if (!this.stop) {
                                        result = search(EXPRESSION2);
                                        if (result != null) {
                                            try {
                                                RowIterator rowIter = result.getRows();
                                                log.debug("Processing " + rowIter.getSize() + " nodes");
                                                while (rowIter.hasNext()) {
                                                    if (this.stop) {break;}
                                                    try {
                                                        Row row = rowIter.nextRow();
                                                        Node node = row.getNode();
                                                        String ocmClassName = node.getProperty("ocm_classname").getString();
                                                        node.setProperty("ocm_classname_backup", ocmClassName);
                                                        ocmClassName = ocmClassName.replace("org.girlscouts.vtk.dao", "org.girlscouts.vtk.ocm") + "Node";
                                                        log.debug("Setting " + node.getPath() + " with ocm_classname=" + ocmClassName);
                                                        node.setProperty("ocm_classname", ocmClassName);
                                                        this.nodeSaveCounter++;
                                                        if ((!this.dryRun) && (this.nodeSaveCounter % this.nodesPerSave) == 0) {
                                                            log.debug("Saving " + this.nodesPerSave + " nodes");
                                                            this.jcrSession.save();
                                                            this.jcrSession.refresh(false);
                                                            this.nodesSaved = this.nodeSaveCounter;
                                                            try {
                                                                log.debug("Sleeping for " + this.sleepMillis + " milliseconds");
                                                                Thread.sleep(this.sleepMillis);
                                                            } catch (InterruptedException ie) {
                                                                requestStop();
                                                            }
                                                        }
                                                    } catch (Exception e) {
                                                    }
                                                }
                                                if ((!this.dryRun)) {
                                                    log.debug("Saving before next step");
                                                    this.jcrSession.save();
                                                    this.jcrSession.refresh(false);
                                                    this.nodesSaved = this.nodeSaveCounter;
                                                    try {
                                                        log.debug("Sleeping for " + this.sleepMillis + " milliseconds");
                                                        Thread.sleep(this.sleepMillis);
                                                    } catch (InterruptedException ie) {
                                                        requestStop();
                                                    }
                                                }
                                            } catch (Exception e1) {
                                            }
                                        }
                                    }
                                    if (!this.stop) {
                                        result = search(EXPRESSION3);
                                        if (result != null) {
                                            try {
                                                RowIterator rowIter = result.getRows();
                                                log.debug("Processing " + rowIter.getSize() + " nodes");
                                                while (rowIter.hasNext()) {
                                                    if (this.stop) {break;}
                                                    try {
                                                        Row row = rowIter.nextRow();
                                                        Node node = row.getNode();
                                                        if (!node.hasProperty("sortOrder")) {
                                                            String id = node.getProperty("id").getString();
                                                            Integer sortOrder = Integer.parseInt(id);
                                                            log.debug("Setting " + node.getPath() + " with sortOrder=" + sortOrder);
                                                            node.setProperty("sortOrder", sortOrder);
                                                            this.nodeSaveCounter++;
                                                            if ((!this.dryRun) && (this.nodeSaveCounter % this.nodesPerSave) == 0) {
                                                                log.debug("Saving " + this.nodesPerSave + " nodes");
                                                                this.jcrSession.save();
                                                                this.jcrSession.refresh(false);
                                                                this.nodesSaved = this.nodeSaveCounter;
                                                                try {
                                                                    log.debug("Sleeping for " + this.sleepMillis + " milliseconds");
                                                                    Thread.sleep(this.sleepMillis);
                                                                } catch (InterruptedException ie) {
                                                                    requestStop();
                                                                }
                                                            }
                                                        }
                                                    } catch (Exception e) {
                                                    }
                                                }
                                                if ((!this.dryRun)) {
                                                    log.debug("Saving before next step");
                                                    this.jcrSession.save();
                                                    this.jcrSession.refresh(false);
                                                    this.nodesSaved = this.nodeSaveCounter;
                                                    try {
                                                        log.debug("Sleeping for " + this.sleepMillis + " milliseconds");
                                                        Thread.sleep(this.sleepMillis);
                                                    } catch (InterruptedException ie) {
                                                        requestStop();
                                                    }
                                                }
                                            } catch (Exception e1) {
                                            }
                                        }
                                    }
                                    if (!this.stop) {
                                        result = search(EXPRESSION4);
                                        if (result != null) {
                                            try {
                                                RowIterator rowIter = result.getRows();
                                                log.debug("Processing " + rowIter.getSize() + " nodes");
                                                while (rowIter.hasNext()) {
                                                    if (this.stop) {break;}
                                                    try {
                                                        Row row = rowIter.nextRow();
                                                        Node node = row.getNode();
                                                        if (node.hasNode("schedule")) {
                                                            Node schedule = node.getNode("schedule");
                                                            if(!schedule.hasProperty("ocm_classname") || !"org.girlscouts.vtk.ocm.CalNode".equals(schedule.getProperty("ocm_classname").getString())){
                                                                log.debug("Setting " + schedule.getPath() + " with ocm_classname = org.girlscouts.vtk.ocm.CalNode");
                                                                schedule.setProperty("ocm_classname","org.girlscouts.vtk.ocm.CalNode");
                                                                this.nodeSaveCounter++;
                                                            }
                                                            if ((!this.dryRun) && (this.nodeSaveCounter % this.nodesPerSave) == 0) {
                                                                log.debug("Saving " + this.nodesPerSave + " nodes");
                                                                this.jcrSession.save();
                                                                this.jcrSession.refresh(false);
                                                                this.nodesSaved = this.nodeSaveCounter;
                                                                try {
                                                                    log.debug("Sleeping for " + this.sleepMillis + " milliseconds");
                                                                    Thread.sleep(this.sleepMillis);
                                                                } catch (InterruptedException ie) {
                                                                    requestStop();
                                                                }
                                                            }
                                                        }else{
                                                            log.debug("No schedule at "+node.getPath());
                                                        }
                                                    } catch (Exception e) {
                                                    }
                                                }
                                                if ((!this.dryRun)) {
                                                    log.debug("Saving before next step");
                                                    this.jcrSession.save();
                                                    this.jcrSession.refresh(false);
                                                    this.nodesSaved = this.nodeSaveCounter;
                                                    try {
                                                        log.debug("Sleeping for " + this.sleepMillis + " milliseconds");
                                                        Thread.sleep(this.sleepMillis);
                                                    } catch (InterruptedException ie) {
                                                        requestStop();
                                                    }
                                                }
                                            } catch (Exception e1) {
                                            }
                                        }
                                    }
                                }

                            }
                        }catch (Exception e) {
                            log.error("Could not process "+path+" successfully", e);
                        }
                    }
                    String EXPRESSION5 = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([/content/girlscouts-vtk]) and s.[ocm_classname] = 'org.girlscouts.vtk.ocm.MeetingNode'";
                    if (!this.stop) {
                        result = search(EXPRESSION5);
                        if (result != null) {
                            try {
                                RowIterator rowIter = result.getRows();
                                log.debug("Processing " + rowIter.getSize() + " nodes");
                                while (rowIter.hasNext()) {
                                    if (this.stop) {break;}
                                    try {
                                        Row row = rowIter.nextRow();
                                        Node node = row.getNode();
                                        //println(node.getPath());
                                        if(node.hasNode("meetingInfo/req")){
                                            Node req = node.getNode("meetingInfo/req");
                                            if(!req.hasProperty("ocm_classname") || !"org.girlscouts.vtk.ocm.JcrCollectionHoldStringNode".equals(req.getProperty("ocm_classname").getString())){
                                                req.setProperty("ocm_classname","org.girlscouts.vtk.ocm.JcrCollectionHoldStringNode");
                                                this.nodeSaveCounter++;
                                                if ((!this.dryRun) && (this.nodeSaveCounter % this.nodesPerSave) == 0) {
                                                    log.debug("Saving " + this.nodesPerSave + " nodes");
                                                    this.jcrSession.save();
                                                    this.jcrSession.refresh(false);
                                                    this.nodesSaved = this.nodeSaveCounter;
                                                    try {
                                                        log.debug("Sleeping for " + this.sleepMillis + " milliseconds");
                                                        Thread.sleep(this.sleepMillis);
                                                    } catch (InterruptedException ie) {
                                                        requestStop();
                                                    }
                                                }
                                            }
                                        }
                                        if(node.hasNode("meetingInfo/meeting short description") ){
                                            Node desc = node.getNode("meetingInfo/meeting short description");
                                            if(!desc.hasProperty("ocm_classname") || !"org.girlscouts.vtk.ocm.JcrCollectionHoldStringNode".equals(desc.getProperty("ocm_classname").getString())){
                                                desc.setProperty("ocm_classname","org.girlscouts.vtk.ocm.JcrCollectionHoldStringNode");
                                                this.nodeSaveCounter++;
                                                if ((!this.dryRun) && (this.nodeSaveCounter % this.nodesPerSave) == 0) {
                                                    log.debug("Saving " + this.nodesPerSave + " nodes");
                                                    this.jcrSession.save();
                                                    this.jcrSession.refresh(false);
                                                    this.nodesSaved = this.nodeSaveCounter;
                                                    try {
                                                        log.debug("Sleeping for " + this.sleepMillis + " milliseconds");
                                                        Thread.sleep(this.sleepMillis);
                                                    } catch (InterruptedException ie) {
                                                        requestStop();
                                                    }
                                                }
                                            }
                                        }
                                    }catch(Exception e){}
                                }
                                if ((!this.dryRun)) {
                                    log.debug("Saving before next step");
                                    this.jcrSession.save();
                                    this.jcrSession.refresh(false);
                                    this.nodesSaved = this.nodeSaveCounter;
                                    try {
                                        log.debug("Sleeping for " + this.sleepMillis + " milliseconds");
                                        Thread.sleep(this.sleepMillis);
                                    } catch (InterruptedException ie) {
                                        requestStop();
                                    }
                                }
                            } catch (Exception e1) {
                            }
                        }
                    }
                    if (!this.stop) {
                        log.debug("MigrateVtkDataThread completed, Updated " + this.nodesSaved + " nodes, total time: " + (System.currentTimeMillis() - currentTimeMs) + "ms");
                    } else {
                        log.debug("MigrateVtkDataThread stopped, Updated " + this.nodesSaved + " nodes, total time: " + (System.currentTimeMillis() - currentTimeMs) + "ms");
                    }
                } catch (Exception e) {
                    log.error("MigrateVtkDataThread interrupted due to exception", e);
                }
            } catch (Exception ie) {
                log.info("MigrateVtkDataThread: interrupted");
            } finally {
                if (jcrSession != null) {
                    jcrSession.logout();
                    jcrSession = null;
                }
                disposeOfThread();
            }
        }

        public void disposeOfThread() {
            synchronized (this.ctxt) {
                this.ctxt.removeAttribute(THREAD_NAME);
                this.ctxt.removeAttribute(RUNNABLE_NAME);
            }
        }

        public QueryResult search(String EXPRESSION) {
            String QUERY_LANGUAGE = "JCR-SQL2";
            log.debug(EXPRESSION);
            QueryResult result = null;
            try {
                QueryManager queryManager = this.jcrSession.getWorkspace().getQueryManager();
                Query sql2Query = queryManager.createQuery(EXPRESSION, QUERY_LANGUAGE);
                result = sql2Query.execute();
                log.debug("Query returned " + result.getRows().getSize() + " results");
                return result;
            } catch (Exception e) {
                log.error("JCR SQL2 query encountered error ", e);
            }
            return result;
        }
    }
%>