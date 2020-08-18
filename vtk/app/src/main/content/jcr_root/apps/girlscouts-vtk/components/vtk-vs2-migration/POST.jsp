<%@page session="false" import="org.apache.commons.lang.StringUtils,
                                org.apache.sling.jcr.api.SlingRepository,
                                org.girlscouts.vtk.osgi.component.util.VTKDataMigrationUtil,
                                org.slf4j.Logger,
                                org.slf4j.LoggerFactory,
                                javax.jcr.Node,
                                javax.jcr.NodeIterator,
                                javax.jcr.RepositoryException,
                                javax.jcr.Session, java.util.*, java.util.concurrent.TimeUnit" %>
<%
%>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%!
    public static final String THREAD_NAME = "vtk-update-vs2-thread";
    public static final String RUNNABLE_NAME = "runnable";
    public static  VTKDataMigrationUtil vtkDataMigrationUtil;
%>
<%
    vtkDataMigrationUtil = sling.getService(VTKDataMigrationUtil.class);
    ServletContext ctxt = application.getContext("/apps/girlscouts-vtk/components/vtk-vs2-migration");
    String cmd = (request.getParameter("cmd") != null) ? request.getParameter("cmd") : "";
    boolean threadExists = ctxt.getAttribute(THREAD_NAME) != null;
    boolean threadIsAlive = threadExists && ((Thread) (ctxt.getAttribute(THREAD_NAME))).isAlive();
    boolean dryRun = false;
    if (request.getParameter("dry_run") != null) {
        dryRun = true;
    }
    if ("stop".equals(cmd) && threadIsAlive) {
        ((MigrateVtkTroopIdThread) (ctxt.getAttribute(RUNNABLE_NAME))).requestStop();
        ((Thread) (ctxt.getAttribute(THREAD_NAME))).join();
        %>stopped<%
    } else {
        if (!threadIsAlive && "run".equals(cmd)) {
            SlingRepository repository = sling.getService(SlingRepository.class);
            MigrateVtkTroopIdThread wft = new MigrateVtkTroopIdThread(getServletContext(), dryRun, repository);
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
    public class MigrateVtkTroopIdThread implements Runnable {
        private Set<String> paths = new LinkedHashSet<String>();
        private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
        private ServletContext ctxt;
        private volatile boolean stop;
        private boolean dryRun = true;
        private SlingRepository repository;
        private String[] councilArr = {"SUM","999","313","597","388","367","465","320","234","661","664","240","465","607","536","563","564","512","591","131","636","212","623","263","467","116","622","660","514","524","430","688","674","208","312","289","608","204","169","155","333","557","583","126","117","654","578","415","134","238","001","321","387","192","438","687","634","110","441","376","497","635","345","218","282","402","258","140","647","434","346","642","590","468","314","168","612","194","499","278","506","901","135","368","306","360","200","253","307","478","614","556","548","322","354","596","538","582","547","281","611","377","493","198","603","106","153","325","450","191","319","161","700","416","477","456"};
        private Map<String,String> troopIdMap = new HashMap<>();
        private Map<String,String> userIdMap = new HashMap<>();
        private Map<String,String> contactIdMap = new HashMap<>();
        private Set<String> unMappedTroopIds = new HashSet<String>();
        private Set<String> unMappedUserIds = new HashSet<String>();
        private Set<String> unMappedContactIds = new HashSet<String>();
        private Set<String> mappedTroopIds = new HashSet<String>();
        private Set<String> mappedUserIds = new HashSet<String>();
        private Set<String> mappedContactIds = new HashSet<String>();

        public MigrateVtkTroopIdThread(ServletContext ctxt, boolean dryRun, SlingRepository repository) {
            this.repository = repository ;
            this.ctxt = ctxt;
            this.paths.add("/vtk2019");
            this.paths.add("/vtk2018");
            this.paths.add("/vtk2017");
            this.paths.add("/vtk2016");
            this.paths.add("/vtk2015");
            this.paths.add("/vtk");
            this.dryRun = dryRun;
            this.troopIdMap = vtkDataMigrationUtil.getTroopIdMapping();
            this.userIdMap = vtkDataMigrationUtil.getUserIdMapping();
            this.contactIdMap = vtkDataMigrationUtil.getContactIdMapping();
        }

        public void requestStop() {
            log.debug("Requesting stop");
            this.stop = true;
        }

        public void run() {
            long startTime = System.currentTimeMillis();
            try {
                log.info("Running MigrateVtkTroopIdThread " + ((this.dryRun) ? " (Dry Run)" : "") + " with " + troopIdMap.size() + " troop id mappings, "+userIdMap.size()+" user id mappings "+contactIdMap.size()+" contact id mappings ");
                Session jcrSession = null;
                try {
                    jcrSession = repository.loginAdministrative(null) ;
                    for(String path:paths){
                        if(this.stop){
                            log.debug("Stopping MigrateVtkTroopIdThread");
                            return;
                        }
                        for(String councilStr:councilArr){
                            if(this.stop){
                                log.debug("Stopping MigrateVtkTroopIdThread");
                                return;
                            }
                            Node council = null;
                            try{
                                council = jcrSession.getNode(path+"/"+councilStr);
                            }catch(Exception e){

                            }
                            if(council != null ) {
                                if (council.hasProperty("ocm_classname") && "org.girlscouts.vtk.ocm.CouncilNode".equals(council.getProperty("ocm_classname").getString())) {
                                    Node troops = council.getNode("troops");
                                    NodeIterator it2 = troops.getNodes();
                                    while (it2.hasNext()) {
                                        long currentTime = System.currentTimeMillis();
                                        long timeElapsed = currentTime - startTime;
                                        String time = String.format("%02d min, %02d sec", TimeUnit.MILLISECONDS.toMinutes(timeElapsed), TimeUnit.MILLISECONDS.toSeconds(timeElapsed) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeElapsed)));
                                        Node troop = it2.nextNode();
                                        if(this.stop){
                                            log.debug("Stopping MigrateVtkTroopIdThread");
                                            return;
                                        }
                                        try {
                                            if (troop.hasProperty("ocm_classname") && "org.girlscouts.vtk.ocm.TroopNode".equals(troop.getProperty("ocm_classname").getString())) {
                                                String troopNodeName = troop.getName();
                                                if (!troopNodeName.startsWith("SHARED")) {
                                                    log.debug("Processing: {}, Execution time: {}", troop.getPath(), time);
                                                    log.debug("Mapped/Unmapped Troops: {}/{}, User Ids: {}/{}, Contact Ids: {}/{}",mappedTroopIds.size(), unMappedTroopIds.size(), mappedUserIds.size(), unMappedUserIds.size(), mappedContactIds.size(), unMappedContactIds.size());
                                                    if (!troopNodeName.startsWith("IRM") && !troopNodeName.startsWith("SUM")) {
                                                        //regular troop
                                                        String vs1TroopId = troopNodeName;
                                                        String vs2TroopId = troopIdMap.get(vs1TroopId);
                                                        if (!StringUtils.isBlank(vs2TroopId)) {
                                                            mappedTroopIds.add(vs1TroopId);
                                                            migrateTroop(troop, vs1TroopId, vs2TroopId);
                                                        } else {
                                                            unMappedTroopIds.add(vs1TroopId);
                                                            log.debug("Troop will not be migrated: No GSGlobalId mapping for troop {} ", vs1TroopId);
                                                        }
                                                    } else {
                                                        if (troopNodeName.startsWith("IRM_")) {
                                                            //irm troop
                                                            String vs1TroopId = troopNodeName;
                                                            String vs1ContactId = vs1TroopId.replace("IRM_", "");
                                                            String vs2GSGlobalId = contactIdMap.get(vs1ContactId);
                                                            if (!StringUtils.isBlank(vs2GSGlobalId)) {
                                                                mappedContactIds.add(vs1ContactId);
                                                                migrateTroop(troop, vs1TroopId, "IRM_" + vs2GSGlobalId);
                                                            } else {
                                                                unMappedContactIds.add(vs1ContactId);
                                                                log.debug("Troop will not be migrated: No GSGlobalId mapping for contact {} ", vs1ContactId);
                                                            }
                                                        } else {
                                                            //sum troop
                                                            if (troopNodeName.startsWith("SUM_")) {
                                                                String vs1TroopId = troopNodeName;
                                                                String vs1UserId = vs1TroopId.replace("SUM_", "");
                                                                String vs2TroopId = userIdMap.get(vs1UserId);
                                                                if (!StringUtils.isBlank(vs2TroopId)) {
                                                                    mappedUserIds.add(vs1UserId);
                                                                    migrateTroop(troop, vs1TroopId, "SUM_" + vs2TroopId);
                                                                } else {
                                                                    unMappedUserIds.add(vs1TroopId.replace("SUM_", ""));
                                                                    log.debug("Troop will not be migrated: No GSGlobalId mapping for user {} ", vs1TroopId);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }catch(Exception e){
                                            log.error("Error occurred migrating troop {} :",troop.getPath(),e);
                                        }
                                    }
                                }
                            }
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
                if(unMappedTroopIds.size()>0){
                    log.debug("Unmapped VS 1.0 Troop Ids: ");
                    for(String id:unMappedTroopIds){
                        log.debug(id);
                    }
                }
                if(unMappedUserIds.size()>0){
                    log.debug("Unmapped VS 1.0 User Ids: ");
                    for(String id:unMappedUserIds){
                        log.debug(id);
                    }
                }
                if(unMappedContactIds.size()>0){
                    log.debug("Unmapped VS 1.0 Contact Ids: ");
                    for(String id:unMappedContactIds){
                        log.debug(id);
                    }
                }
            } catch (Exception ie) {
                log.info("MigrateVtkTroopIdThread: interrupted ", ie);
            } finally {
                disposeOfThread();
                long endTime = System.currentTimeMillis();
                long timeElapsed = endTime - startTime;
                String time = String.format("%02d min, %02d sec", TimeUnit.MILLISECONDS.toMinutes(timeElapsed), TimeUnit.MILLISECONDS.toSeconds(timeElapsed) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeElapsed)));
                log.debug("Total Execution time: {}",time);
            }
        }

        private void migrateTroop(Node troop, String vs1TroopId, String vs2TroopId) throws RepositoryException {
            Session session = troop.getSession();
            String oldPath = troop.getPath();
            String newPath = oldPath.replace(vs1TroopId, vs2TroopId);
            log.debug("Moving "+oldPath+ " to "+newPath);
            session.move(oldPath, newPath);
            Node newNode = session.getNode(newPath);
            log.debug("Updating properties for "+newNode.getPath());
            newNode.setProperty("id",vs2TroopId);
            newNode.setProperty("sfTroopId",vs2TroopId);
            newNode.setProperty("vs1-id",vs1TroopId);
            if(newNode.hasNode("yearPlan/meetingEvents")) {
                Node meetingEvents = newNode.getNode("yearPlan/meetingEvents");
                if (meetingEvents != null && meetingEvents.hasNodes()) {
                    log.debug("Updating meetings in " + meetingEvents.getPath());
                    NodeIterator nodeItr = meetingEvents.getNodes();
                    while (nodeItr.hasNext()) {
                        if (this.stop) {
                            return;
                        }
                        Node meetingEvent = nodeItr.nextNode();
                        if (meetingEvent.hasProperty("refId")) {
                            String refId = meetingEvent.getProperty("refId").getString();
                            if (!StringUtils.isBlank(refId) && refId.contains(vs1TroopId)) {
                                log.debug("Updating refId for " + meetingEvent.getPath());
                                meetingEvent.setProperty("refId", refId.replace(vs1TroopId, vs2TroopId));
                            }
                        }
                        if (meetingEvent.hasNode("attendance")) {
                            log.debug("Updating attendance in " + meetingEvent.getPath());
                            Node attendance = meetingEvent.getNode("attendance");
                            updateUsers(attendance);
                        }
                        if (meetingEvent.hasNode("achievement")) {
                            log.debug("Updating achievement in " + meetingEvent.getPath());
                            Node achievement = meetingEvent.getNode("achievement");
                            updateUsers(achievement);
                        }

                    }
                }
            }
            if(newNode.hasNode("finances")) {
                Node finances = newNode.getNode("finances");
                if (finances != null && finances.hasNodes()) {
                    log.debug("Updating finances for " + finances.getPath());
                    NodeIterator nodeItr = finances.getNodes();
                    while (nodeItr.hasNext()) {
                        if (this.stop) {
                            return;
                        }
                        Node financeNode = nodeItr.nextNode();
                        if (financeNode.hasProperty("path")) {
                            String path = financeNode.getProperty("path").getString();
                            if (!StringUtils.isBlank(path) && path.contains(vs1TroopId)) {
                                log.debug("Updating path for " + financeNode.getPath());
                                financeNode.setProperty("path", path.replace(vs1TroopId, vs2TroopId));
                            }
                        }
                    }
                }
            }

            if(this.stop){
                return;
            }
            if(!this.dryRun) {
                session.save();
            }
        }

        private void updateUsers(Node node) {
            if(node != null){
                try {
                    String usersStr = node.getProperty("users").getString();
                    if(!StringUtils.isBlank(usersStr)) {
                        log.debug("VS1.0  users " + usersStr + " for " + node.getPath());
                        String[] usersArr = usersStr.split(",");
                        for (String user : usersArr) {
                            if (user != null) {
                                String vs2User = contactIdMap.get(user);
                                if (vs2User != null) {
                                    log.debug("Updating {} for {} to {} ",node.getName(),user, vs2User);
                                    usersStr = usersStr.replace(user, vs2User);
                                } else {
                                    unMappedContactIds.add(user);
                                    log.debug("User {} will not be migrated: No GSGlobalId mapping for contact {} ",node.getName(),user);
                                }
                            }
                        }
                        log.debug("VS2.0 users " + usersStr + " for " + node.getPath());
                        node.setProperty("users", usersStr);
                    }
                } catch (Exception e) {
                    log.error("Error occured while updating users for {} ", node, e);
                }
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