<%@page session="false" import="javax.jcr.*,
        com.day.cq.wcm.api.Page,
        com.day.cq.wcm.api.PageManager,
        org.apache.sling.api.resource.Resource,
        com.day.cq.wcm.commons.WCMUtils,
        com.day.cq.wcm.api.NameConstants,
        com.day.cq.wcm.api.designer.Designer,
        com.day.cq.wcm.api.designer.Design,
        com.day.cq.wcm.api.designer.Style,
        org.apache.sling.api.resource.ValueMap,
        com.day.cq.wcm.api.components.ComponentContext,
        com.day.cq.wcm.api.components.EditContext,
        java.util.Date, java.text.SimpleDateFormat, 
        java.text.FieldPosition, java.text.ParsePosition,
        java.util.Iterator,
        java.util.Calendar,
        javax.jcr.Property,
        javax.jcr.Node,
        javax.jcr.NodeIterator,
        javax.jcr.PropertyType,
        javax.jcr.Session,
		javax.jcr.version.*,
        org.slf4j.Logger,
        org.slf4j.LoggerFactory,
        java.util.List,
        java.util.ArrayList,
         org.apache.sling.api.resource.ResourceResolver,
         org.apache.sling.jcr.api.SlingRepository,
com.day.cq.workflow.*,
com.day.cq.workflow.model.*,
com.day.cq.workflow.exec.*" %><%
%><%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects/>
<%!
public static final String THREAD_NAME = "thread";
public static final String RUNNABLE_NAME = "runnable";

%><%
ServletContext ctxt = application.getContext("/apps/tools/components/fixAssets");
String cmd = (request.getParameter("cmd") != null)?request.getParameter("cmd"):"";
boolean threadExists = ctxt.getAttribute(THREAD_NAME) != null;
boolean threadIsAlive = threadExists && ((Thread)(ctxt.getAttribute(THREAD_NAME))).isAlive();

long sleepMillis = 100;
long nodesPerSave = 20;
boolean dryRun = false;

String path = "/content/dam";
if(request.getParameter("nodes_per_save") != null) {
    try {
      long nodesPerSaveTmp = Long.parseLong(request.getParameter("nodes_per_save"));
      if(nodesPerSaveTmp > 0) nodesPerSave = nodesPerSaveTmp; 
    } catch(Exception e) {
    %><cq:include script="form.jsp"/><br/>Invalid entry for "Nodes per save".  The value must be a positive integer.<%
    }
}

if(request.getParameter("sleep_interval") != null) {
    try {
      long sleepMillisTmp = Long.parseLong(request.getParameter("sleep_interval"));
      if(sleepMillisTmp > 0) sleepMillis = sleepMillisTmp;
    } catch(Exception e) {
    %><cq:include script="form.jsp"/><br/>Invalid entry for "Sleep time between saves".  The value must be a positive integer.<%
    }
}
if(request.getParameter("path") != null) {
    path = request.getParameter("path");
}

if(request.getParameter("dry_run") != null) {
    dryRun = true;
}

if("stop".equals(cmd) && threadIsAlive) {
        ((FixAssetsThread)(ctxt.getAttribute(RUNNABLE_NAME))).requestStop();
        ((Thread)(ctxt.getAttribute(THREAD_NAME))).join();
        %>stopped<%
} else if(!threadIsAlive && "run".equals(cmd)) {
        SlingRepository repository = sling.getService(SlingRepository.class);
        FixAssetsThread wft = new FixAssetsThread(getServletContext(), nodesPerSave, sleepMillis, path, repository, sling.getService(WorkflowService.class), dryRun);
        Thread t = new Thread(wft);
        ctxt.setAttribute(THREAD_NAME,t);
        ctxt.setAttribute(RUNNABLE_NAME,wft);
        t.start();
        %>started<%
} else if(threadIsAlive) {
        %>running<%
} else if(!threadIsAlive) {
        %>not running<%
}


%><%!
public class FixAssetsThread implements Runnable {
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private ServletContext ctxt;
    private boolean clearRunning;
    private List listeners;
    //private ResourceResolver resourceResolver;
    private SlingRepository repository;
    private Session jcrSession ;
    private volatile boolean stop;
    private String currentNodePath = null;
    private boolean dryRun = true;

    private long currentTimeMs = 0;
    private static final String ASSET_ROOT_PATH = "/content/dam";
    private long sleepMillis = 100;
    private long nodesPerSave = 20;
    private long nodeSaveCounter = 0;
    private boolean isStoppingMessageOutput = false;
	WorkflowSession wfSession;
    private String DAM_WORKFLOW_MODEL = "/etc/workflow/models/dam/update_asset/jcr:content/model";
	private String rootPath = ASSET_ROOT_PATH;

	private WorkflowService wfService;

    public FixAssetsThread(ServletContext ctxt, long nodesPerSave, long sleepMillis, String path, SlingRepository repository, WorkflowService wfService, boolean dryRun) {
        this.ctxt = ctxt; 
        this.clearRunning = clearRunning;
        this.repository = repository ;
        this.sleepMillis = sleepMillis;
        this.nodesPerSave = nodesPerSave;
        this.wfService = wfService;
        this.dryRun = dryRun;
        if(path != null) this.rootPath = path;
        try {
        	jcrSession = repository.loginAdministrative(null) ;
            wfSession = wfService.getWorkflowSession(jcrSession);
        } catch (RepositoryException e) {
            log.error("fixAssets interrupted due to exception", e);
        }
    }

    public void updateBuffer(String msg) {
        log.info(msg);
    }

    public void requestStop() {
        stop = true;   
    }

    public void run() {

        try {
            log.info("Running assets fix for path: " + this.rootPath + ((this.dryRun)?" (Dry Run)":""));
            try {
                currentTimeMs = System.currentTimeMillis();

                long count = traverse((Node) jcrSession.getItem(this.rootPath), 0);
				if((!this.dryRun) && (this.nodeSaveCounter % this.nodesPerSave) > 0 ) jcrSession.save();
                log.info("fixAssets completed, total nodes: " + count + ", total nodes updated: " + this.nodeSaveCounter + ", total time: " + (System.currentTimeMillis()-currentTimeMs) + "ms");
            } catch (RepositoryException e) {
                log.error("fixAssets interrupted due to exception", e);
            }
        } catch(Exception ie) {
            log.info("FixAssetsThread: interrupted");
        } finally {
          if (jcrSession != null) {
            jcrSession.logout();
            jcrSession = null;
            } 
            disposeOfThread();
        }
    }

    public long traverse(Node n, int level) throws RepositoryException {
        long count = 1;
        if (stop) {
            if(!isStoppingMessageOutput) {
                isStoppingMessageOutput = true;
            }
            return count;
        }
        String path = n.getPath();
        currentNodePath = path;
        try {
			Thread.sleep(sleepMillis);
        } catch (InterruptedException ie) {
            requestStop();
			return level;
        }
        String nodeTypeName = n.getPrimaryNodeType().getName();
        String assetPath = n.getPath();
		boolean changesPending = false;
        if (nodeTypeName.equals("dam:Asset")) {
            /*
            if(n.hasNode("jcr:content/metadata")) {
				Node metadataNode = n.getNode("jcr:content/metadata");
                if(metadataNode.hasProperty("dam:Comments")) {
                    Property damComments = metadataNode.getProperty("dam:Comments");
                    if(damComments.getString().startsWith("XML:")) {
                        log.debug("Removing dam:Comments property from " + metadataNode.getPath());
						damComments.remove();
                        changesPending = true;
                    }
                  }

            }
            */
			if(n.hasNode("jcr:content/renditions/original/jcr:content")) {
    			Property binProp = n.getNode("jcr:content/renditions/original/jcr:content").getProperty("jcr:data");
    			long size = binProp.getBinary().getSize();
                //log.debug("Asset size " + size + " bytes, " + n.getPath());
				Node version = getVersionWithSmallerFiles(n.getSession(), n, size);
                if(version != null) {
                    Node versionOrigContent = version.getNode("jcr:content/renditions/original/jcr:content");
					Property versionBinProp = versionOrigContent.getProperty("jcr:data");
                    String mime = n.getNode("jcr:content/renditions/original/jcr:content").getProperty("jcr:mimeType").getString();
                    String versionMime = versionOrigContent.getProperty("jcr:mimeType").getString();
                    log.debug("Updating " + n.getPath() + " with new binary" + ((this.dryRun)?" (Dry Run)":""));
                    if(!this.dryRun) binProp.setValue(versionBinProp.getBinary());
                    changesPending = true;
                }
            }
            if(changesPending) {
                this.nodeSaveCounter++;
                if((this.nodeSaveCounter % this.nodesPerSave) == 0) {
                    log.info("Saving changes for " + this.nodesPerSave + " assets." + ((this.dryRun)?" (Dry Run)":""));
                    if(!this.dryRun) n.getSession().save();
                }
            }

            /*if(!n.hasNode("jcr:content/renditions/cq5dam.thumbnail.140.100.png") || !n.hasNode("jcr:content/renditions/cq5dam.thumbnail.319.319.png") || !n.hasNode("jcr:content/renditions/cq5dam.thumbnail.48.48.png") ) {
             runWorkflowOnNode(n, DAM_WORKFLOW_MODEL);
          } else if(n.hasNode("jcr:content/renditions/original/jcr:content") && !n.hasNode("jcr:content/metadata")) {
            try {
            	Node contentNode = n.getNode("jcr:content");
            	contentNode.addNode("metadata");
            	contentNode.getSession().save();
            } catch (Exception e) {
                log.error("Failed to create metadata node for asset: " + n.getPath());
            }
			runWorkflowOnNode(n, DAM_WORKFLOW_MODEL);
          } else if(!n.hasNode("jcr:content/renditions/original/jcr:content")) {
			log.error("Asset missing original rendition!! " + n.getPath());
            }*/

		  return count;
        } else if(nodeTypeName.equals("sling:Folder") || nodeTypeName.equals("sling:OrderedFolder")) {
            log.debug("Traversing folder: " + n.getPath());
            NodeIterator nIter = n.getNodes();
            while (nIter.hasNext()) {
            	count += traverse(nIter.nextNode(), level + 1);
            }
        }
        return count;

    }

    private Node getVersionWithSmallerFiles(Session session, Node n, long size) throws RepositoryException { 
        if(n.isNodeType("mix:versionable")) {
            VersionManager mgr = session.getWorkspace().getVersionManager();
            VersionHistory vh = mgr.getVersionHistory(n.getPath());
            VersionIterator vi = vh.getAllVersions();
            while(vi.hasNext()) {
                Version v = vi.nextVersion();
                Node frNode = v.getFrozenNode();
                if(frNode.hasNode("jcr:content/renditions/original/jcr:content")) {
                    Property binProp = frNode.getNode("jcr:content/renditions/original/jcr:content").getProperty("jcr:data");
                    long verSize = binProp.getBinary().getSize();
                    if(size > verSize && (size - verSize) > 100000) {
                        log.info(n.getPath() + ": Version " + v.getName() + " is smaller by " + (size - verSize) + " bytes");
                        return frNode;
                    }
                }
            }
        }
        return null;
    }

    public void runWorkflowOnNode(Node n, String modelPath) {
			 String nodePath = null;
             try {
				nodePath = n.getPath();
				log.info("Starting workflow for " + nodePath);
			 // Get the workflow data
         	 // The first param in the newWorkflowData method is the payloadType.  Just a fancy name to let it know what type of workflow it is working with.
         		WorkflowData wfData = wfSession.newWorkflowData("JCR_PATH", n.getPath());
                WorkflowModel wfModel = wfSession.getModel(modelPath);

          	 // Run the Workflow.
            	wfSession.startWorkflow(wfModel, wfData);
            } catch (Exception e) {
				log.error("Failed to run workflow for " + nodePath, e);
            }
    }

    public void disposeOfThread() {
        synchronized(this.ctxt) {
            this.ctxt.removeAttribute(THREAD_NAME);
            this.ctxt.removeAttribute(RUNNABLE_NAME);
        }
    }
}
%>