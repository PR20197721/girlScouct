<%@ taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %>
<%@page session="false" import="org.apache.sling.api.resource.Resource,org.apache.sling.api.resource.*,
                                org.slf4j.Logger,
                                javax.jcr.query.*,
                                org.slf4j.LoggerFactory,
                                java.util.HashSet,
                                java.util.Iterator,
                                java.util.Set" %>
<%
%>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/><%

    Logger logger = LoggerFactory.getLogger("org.girlscouts.vtk.sfid");
    Set<String> paths = new HashSet<String>();
    paths.add("/vtk2019");
    paths.add("/vtk2018");
    paths.add("/vtk2017");
    paths.add("/vtk2016");
    paths.add("/vtk2015");
    paths.add("/vtk");
    String QUERY_LANGUAGE = "JCR-SQL2";
    Set<String> userSfIds = new HashSet<String>();
    Set<String> contactSfIds = new HashSet<String>();
    Set<String> campaignSfIds = new HashSet<String>();
    javax.jcr.Session s= (resourceResolver.adaptTo(Session.class));
    for (String path : paths) {
        logger.debug("processing " + path);
        Resource yearNode = resourceResolver.resolve(path);
        Iterator<Resource> councils = yearNode.listChildren();
        if (councils != null) {
            while (councils.hasNext()) {
                Resource council = councils.next();
                logger.debug("processing " + council.getPath());
                Node councilNode = council.adaptTo(Node.class);
                if(councilNode.hasNode("finances/template")){
                    try {
                        Resource councilFinanceTemplateNode = council.getChild("finances/template");
                        ValueMap vm = councilFinanceTemplateNode.getValueMap();
                        String lastUserUpdated = vm.get("lastUserUpdated", String.class);
                        if(lastUserUpdated != null && lastUserUpdated.trim().length() <= 18){
                            userSfIds.add(lastUserUpdated.trim());
                        }
                    } catch (Exception e) {
                        logger.debug("Error for:" + councilNode.getPath(), e);
                    }
                }
                if (councilNode.hasNode("troops")) {
                    try {
                        Resource troopsNode = council.getChild("troops");
                        Iterator<Resource> troops = troopsNode.listChildren();
                        if (troops != null) {
                            while (troops.hasNext()) {
                                Resource troop = troops.next();
                                String troopName = troop.getName().trim();
                                if(troopName != null &&  troopName.length() <= 18) {
                                    campaignSfIds.add(troopName);
                                }
                                if (troopName != null && troopName.startsWith("IRM_") && troopName.length() <= 22) {
                                    contactSfIds.add(troopName.replace("IRM_",""));

                                }
                                if(troopName != null && troopName.startsWith("SUM_") && troopName.length() <= 22){
                                    userSfIds.add(troopName.replace("SUM_",""));
                                }
                                ValueMap vm = troop.getValueMap();
                                String sfUserId = vm.get("sfUserId",String.class);
                                if(sfUserId != null){
                                    userSfIds.add(sfUserId.trim());
                                }
                                if(troop.getChild("finances") != null){
                                    try {
                                        Resource troopFinanceNode = troop.getChild("finances");
                                        Iterator<Resource> financeSections = troopFinanceNode.listChildren();
                                        if (financeSections != null) {
                                            while (financeSections.hasNext()) {
                                                Resource section = financeSections.next();
                                                ValueMap vm2 = section.getValueMap();
                                                String lastUserUpdated = vm2.get("lastUserUpdated", String.class);
                                                if(lastUserUpdated != null && lastUserUpdated.trim().length() <= 18){
                                                    userSfIds.add(lastUserUpdated.trim());
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        logger.debug("Error for:" + councilNode.getPath(), e);
                                    }
                                }
                                String troopPath = troop.getPath();
                                String sql = "SELECT * FROM [nt:base] AS s WHERE (ISDESCENDANTNODE([" + troopPath + "])) and (s.[ocm_classname] ='org.girlscouts.vtk.ocm.AttendanceNode' OR s.[ocm_classname] ='org.girlscouts.vtk.ocm.AchievementNode')";
                                try {
                                    javax.jcr.query.QueryManager qm = s.getWorkspace().getQueryManager();
                                    javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL);
                                    javax.jcr.query.QueryResult result = q.execute();
                                    if (result != null) {
                                        RowIterator rowIter = result.getRows();
                                        while (rowIter.hasNext()) {
                                            try {
                                                Row row = rowIter.nextRow();
                                                Node node = row.getNode();
                                                if (node.hasProperty("users")) {
                                                    String users = node.getProperty("users").getString();
                                                    if (users != null && users.trim().length() > 0) {
                                                        String[] usersArr = users.split(",");
                                                        if (usersArr != null && usersArr.length > 0) {
                                                            for (String user : usersArr) {
                                                                if (user != null && user.trim().length() > 0) {
                                                                    contactSfIds.add(user.trim());
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            } catch (Exception e2) {
                                                logger.debug("		Error for:" + troopPath, e2);
                                            }
                                        }
                                    }
                                } catch (Exception e1) {
                                    logger.debug("	Error for:" + troopPath, e1);
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.debug("Error for:" + councilNode.getPath(), e);
                    }

                }
                logger.debug("collected " + userSfIds.size() + " SF User ids");
                logger.debug("collected " + campaignSfIds.size() + " SF Campaign ids");
                logger.debug("collected " + contactSfIds.size() + " SF Contact ids");
            }
        }
    }
    StringBuffer sb = new StringBuffer();
    sb.append("USER ID's\n");
    for (String sfId : userSfIds) {
        sb.append(sfId + "\n");
    }
    sb.append("CAMPAIGN ID's\n");
    for (String sfId : campaignSfIds) {
        sb.append(sfId + "\n");
    }
    sb.append("CONTACT ID's\n");
    for (String sfId : contactSfIds) {
        sb.append(sfId + "\n");
    }
    response.setContentType("application/txt");
    response.setHeader("Content-Disposition", "attachment; filename=SFIdRpt.txt");
    out.println(sb.toString());
%>