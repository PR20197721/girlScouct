<%@page session="false" import="com.day.cq.wcm.api.NameConstants,
                                com.day.cq.wcm.api.Page,
                                com.day.cq.wcm.api.PageManager,
                                com.day.cq.wcm.api.components.ComponentContext,
                                com.day.cq.wcm.api.components.EditContext,
                                com.day.cq.wcm.api.designer.Design,
                                com.day.cq.wcm.api.designer.Designer,
                                com.day.cq.wcm.api.designer.Style,
                                com.day.cq.wcm.commons.WCMUtils,
                                com.day.cq.commons.jcr.JcrUtil,
                                org.apache.sling.api.resource.Resource,
                                org.apache.sling.api.resource.ResourceResolver,
                                org.apache.sling.api.resource.ResourceResolverFactory,
                                org.apache.sling.api.resource.ValueMap,
                                org.apache.sling.api.resource.ModifiableValueMap,
                                org.apache.sling.jcr.api.SlingRepository,
                                org.slf4j.Logger,
                                org.slf4j.LoggerFactory,
                                javax.jcr.*,
                                javax.jcr.query.*, java.text.SimpleDateFormat, java.util.*" %>
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
    ServletContext ctxt = application.getContext("/apps/girlscouts/components/script-event-datestring-timezone");
    String cmd = (request.getParameter("cmd") != null) ? request.getParameter("cmd") : "";
    boolean threadExists = ctxt.getAttribute(THREAD_NAME) != null;
    boolean threadIsAlive = threadExists && ((Thread) (ctxt.getAttribute(THREAD_NAME))).isAlive();
    boolean dryRun = false;
    if (request.getParameter("dry_run") != null) {
        dryRun = true;
    }
    boolean backup = false;
    if (request.getParameter("backup") != null) {
        backup = true;
    }
    if ("stop".equals(cmd) && threadIsAlive) {
        ((EventTimeUpdateThread) (ctxt.getAttribute(RUNNABLE_NAME))).requestStop();
        ((Thread) (ctxt.getAttribute(THREAD_NAME))).join();
%>stopped<%
} else if (!threadIsAlive && "run".equals(cmd)) {
    SlingRepository repository = sling.getService(SlingRepository.class);
    EventTimeUpdateThread wft = new EventTimeUpdateThread(getServletContext(), repository, dryRun, backup);
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
    public class EventTimeUpdateThread implements Runnable {
        private final Logger log = LoggerFactory.getLogger("org.girlscouts.scriptEventDateTimezone");
        private ServletContext ctxt;
        private volatile boolean stop = false;
        private boolean dryRun = true;
        private boolean backup = true;
        private long currentTimeMs = 0;
        private long sleepMillis = 100;
        private long nodesPerSave = 1000;
        private long nodesSaved = 0;
        private long nodeSaveCounter = 0;
        private SlingRepository repository;
        private Map<String, Object> resolverParams = new HashMap<String, Object>();

        public final String path = "/content";
        private List<String> skipList = new ArrayList<String>();
        private Session session;
        private ResourceResolver resourceResolver;

        public EventTimeUpdateThread(ServletContext ctxt, SlingRepository repository, boolean dryRun, boolean backup) {
            this.ctxt = ctxt;
            this.repository = repository ;
            this.backup = backup;
            this.dryRun = dryRun;
            this.resolverParams.put(ResourceResolverFactory.SUBSERVICE, "workflow-service");
            try {
                session = repository.loginAdministrative(null) ;
            } catch (Exception e) {
                log.error("fixAssets interrupted due to exception", e);
            }
            try{
                resourceResolver = resolverFactory.getAdministrativeResourceResolver(this.resolverParams);
            } catch(Exception e){
                log.error("Exception is " + e.getStackTrace());
            }
            skipList.add("/content/gsusa");
            skipList.add("/content/webtocase");
            skipList.add("/content/girlscouts-dxp");
            skipList.add("/content/girlscouts-dxp2");
            skipList.add("/content/girlscouts-future");
            skipList.add("/content/girlscouts-template");
            skipList.add("/content/vtk-maintenance");
            skipList.add("/content/rollout61-2");
            skipList.add("/content/rollout-61");
            skipList.add("/content/gsbulkeditor-test");
            skipList.add("/content/seo-test-council");
            skipList.add("/content/test3");
            skipList.add("/content/girlscouts-demo");
            skipList.add("/content/web2lead-demo");
            skipList.add("/content/seo-permission-test-2");
            skipList.add("/content/seo-test-042516");
            skipList.add("/content/regent");
            skipList.add("/content/cug");
            skipList.add("/content/mac");
            skipList.add("/content/geometrixx-media");
            skipList.add("/content/versionhistory");
            skipList.add("/content/sandbox");
            skipList.add("/content/girlscouts-mobile");
            skipList.add("/content/go-gold");
            skipList.add("/content/councils-own");
            skipList.add("/content/NewSignatureEvents");
            skipList.add("/content/NewSignatureEvents1");
            skipList.add("/content/NewSignatureEvents2");
            skipList.add("/content/our-camps");
            skipList.add("/content/investigate");
            skipList.add("/content/gsdsw");
            skipList.add("/content/cgspr");
        }

        public void requestStop() {
            log.debug("Requesting stop");
            this.stop = true;
        }

        public void run() {
            log.debug("Run");
            try{
                Resource startRes = resourceResolver.resolve(path);
                Iterator pages = startRes.listChildren();
                int counter = 0;
                while(pages.hasNext() && !this.stop){
                    Resource page = (Resource)pages.next();
                    if("cq:Page".equals(page.getResourceType())){
                        if(!skipList.contains(page.getPath())){
                            String timezone = null;
                            Resource councilProps = page.getChild("en/jcr:content");
                            if(councilProps != null){
                                Node cpNode = councilProps.adaptTo(Node.class);
                                if(cpNode.hasProperty("timezone")){
                                    timezone = cpNode.getProperty("timezone").getValue().toString();
                                    if (timezone.contains(":")){
                                        log.debug("Purging " + timezone.substring(timezone.indexOf(':')));
                                        timezone = timezone.substring(0, timezone.indexOf(':'));
                                        log.debug("Timezone is " + timezone);
                                    }
                                }else{
                                    log.error("no timezone property for "+cpNode.getPath());
                                }
                            }else{
                                log.error("no council props for "+page.getPath());
                            }
                            if(page.getChild("en/sf-events-repository") != null){
                                counter = processEvents(page,"en/sf-events-repository",timezone, counter);
                            }
                            if(page.getChild("en/events-repository") != null){
                                counter = processEvents(page,"en/events-repository",timezone, counter);
                            }
                        }
                    }
                }
            }catch(Exception e){
                log.error("Error is " + e.getStackTrace());
            }
        }


        public int processEvents(Resource resource, String path,String timezone, int counter){
            try{
                String EXPRESSION = "SELECT s.[jcr:path] "+ "FROM [cq:Page] AS s WHERE ISDESCENDANTNODE('"+resource.getPath()+"/"+path+"')";
                QueryResult result = search(EXPRESSION);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                String[] datePropNames = {"start","end","regOpen","regClose"};
                if (result != null) {
                    try {
                        RowIterator rowIter = result.getRows();
                        while (rowIter.hasNext()){
                            try{
                                Row row = rowIter.nextRow();
                                Node node = row.getNode();
                                Resource event = resourceResolver.resolve(node.getPath());
                                if(event.getChild("jcr:content/data") != null){
                                    Resource data = event.getChild("jcr:content/data");
                                    ValueMap valueMap = data.adaptTo(ModifiableValueMap.class);
                                    String startDate = valueMap.get("start",String.class);
                                    Node dataNode = data.adaptTo(Node.class);
                                    //rintln("Looking at " + data.getPath())
                                    for (int i=0; i<datePropNames.length; i++){
                                        log.debug("Looking at " + data.getPath()  + " property " + datePropNames[i]);
                                        if (dataNode.hasProperty(datePropNames[i])){
                                            Property dateProp = dataNode.getProperty(datePropNames[i]);
                                            if(dateProp.getType() == 1){
                                                counter++;
                                                log.debug(counter+" STRING DATE:"+dataNode.getPath()+" "+ ", value="+dateProp.getValue().toString());
                                                Date date = dateFormat.parse(dateProp.getString());
                                                Calendar calendar = Calendar.getInstance();
                                                calendar.setTime(date);
                                                if (!dryRun){
                                                    dateProp.remove();
                                                    log.debug("DataNode Property is" + Boolean.toString(dataNode.hasProperty(datePropNames[i])));
                                                    dataNode.setProperty(datePropNames[i],calendar);
                                                    dataNode.save();
                                                }
                                                log.debug(calendar.toString());
                                            }else{
                                                log.debug(dateProp.getString() + " is already in Date Format");
                                                if (dateProp.getType() != 5){
                                                    log.error("NEW TYPE:"+dataNode.getPath()+" "+dateProp.getType());
                                                }
                                            }
                                        }
                                    }
                                    if (!dataNode.hasProperty("timezone")){
                                        counter++;
                                        dataNode.setProperty("timezone",timezone);
                                        log.debug("New timezone " + timezone);
                                        if (!dryRun){
                                            dataNode.save();
                                        }
                                    }

                                }
                            }catch(Exception e){
                            	log.error("Exception is " + e.getStackTrace());
                            }
                        }
                    }catch(Exception e){
                    	log.error("Exception is " + e.getStackTrace());
                    }
                }
                return counter;
            } catch (Exception e){
                log.error("Exeption is" + e.getStackTrace());
                return 0;
            }
        }




        public QueryResult search(String EXPRESSION) {
            log.debug(EXPRESSION);
            QueryResult result = null;
            try {
                QueryManager queryManager = session.getWorkspace().getQueryManager();
                Query sql2Query = queryManager.createQuery(EXPRESSION, "JCR-SQL2");
                return sql2Query.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        return result;
        }
    }
%>